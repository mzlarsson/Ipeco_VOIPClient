package se.chalmers.fleetspeak.network.udp;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The purpose of the JitterBuffer is to make audio-streams more consistent,
 * it does this by adding a delay to wait for or realign packets that arrived
 * out of order.
 * Packets arriving too late are dropped.
 * If the buffer is not completely full it enters a "building mode" where it
 * will attempt to fill the buffer without disrupting conversations.
 * It does this by inserting silent packages next to other "silent" packages,
 * meaning time-gaps between two packages with the correct sequence number but
 * with a high timestamp difference. This allows the client to conserve
 * network bandwidth by not sending anything while no/too low audio is recorded.
 *
 * @author Patrik Haar
 */
public class JitterBuffer{

	private static final int SOUND_ARRAY_SIZE = 320;
	private static final int TIME_BETWEEN_SAMPLES = 10;
	
	private JitterBufferQueue buffer;
	private short lastReadSeqNbr = -1;
	private long lastReadtimestamp = -1;
	private boolean ready, buildMode;
	private Logger logger;

	private long bufferTime;
	
	/**
	 * Constructs a JitterBuffer which delays the media in favor of consistency
	 * where packets have more time to arrive and can be sorted in the right order.
	 * Higher buffer time will improve consistency on poor networks but will add a
	 * delay to the processing of the media equal to the buffer time. 
	 * @param bufferTime The delay the JitterBuffer has to work with in milliseconds.
	 */
	public JitterBuffer(long bufferTime) {
		logger = Logger.getLogger("Debug");
		buffer = new JitterBufferQueue();
		this.bufferTime = bufferTime;
	}
	
	/**
	 * Adds the packet to the correct place in the queue or drops it if it arrived too late.
	 * @param packet The RTPPacket to be added to the queue.
	 */
	public void write(RTPPacket packet) {
		if((short)(packet.seqNumber - lastReadSeqNbr) > 0) {
			buffer.offer(packet);
			if(isFullyBuffered()) {
				if (!ready) {
					ready = true;					
				}
				buildMode = false;
				if (buffer.getBufferedTime()>(bufferTime*2)) {
					logger.log(Level.FINEST, "(" + Thread.currentThread().getName() + ")Buffer is too long, reducing delay");
					while(buffer.getBufferedTime()>(bufferTime)) {
						buffer.poll();
					}
				}
			}
		} else {
			logger.log(Level.WARNING,"(" + Thread.currentThread().getName() + ")Dropped a packet arriving to late, "
					+ "sequence number: " + packet.seqNumber);
		}
	}

	/**
	 * Polls the next RTPPacket available or null if the buffer is not full
	 * or packets are missing.
	 * @return The first RTPPacket of the queue if available, null if not.
	 */
	public RTPPacket read() {
		RTPPacket p = null;
		if(ready) {
			if(!buildMode) {
				if (!isFullyBuffered()) {
					buildMode = true;
				}
				p = buffer.poll();
			} else {
				RTPPacket tmp = buffer.peek();
				if (tmp != null) {
					if (tmp.seqNumber == ((short)(lastReadSeqNbr + 1))) {
						if (tmp.timestamp - 4*TIME_BETWEEN_SAMPLES <= lastReadtimestamp) { // 2*timestamp to compensate for variations.
							p = buffer.poll();
						} else {
							logger.log(Level.FINEST, "(" + Thread.currentThread().getName() + ")[Buildmode] Returned null to reader due to too big of a time difference between package "
									+ lastReadSeqNbr + "-" + (lastReadSeqNbr+1) + " (" + (tmp.timestamp-lastReadtimestamp) + ")");
							lastReadtimestamp += TIME_BETWEEN_SAMPLES;
						}
					} else {
						logger.log(Level.FINEST, "(" + Thread.currentThread().getName() + ")[Buildmode] Returned null to reader due to unmatching sequence numbers, expected "
								+ (lastReadSeqNbr+1) + " but next was " + (tmp.seqNumber));
						lastReadSeqNbr += 1;
						lastReadtimestamp += TIME_BETWEEN_SAMPLES;
					}
				} else {
					ready = false;
				}
			}
			if (p!=null) {
				lastReadSeqNbr = p.seqNumber;
				lastReadtimestamp = p.timestamp;				
			}
		} else {
			logger.log(Level.FINEST, "(" + Thread.currentThread().getName() + ")[Not Ready] Returned null due to the jitterbuffer not being ready, last packet was: " + lastReadSeqNbr);
		}
		return p;
	}

	private boolean isFullyBuffered() {
		return buffer.getBufferedTime() >= bufferTime;
	}
	
	public int getSoundArraySize() {
		return SOUND_ARRAY_SIZE;
	}
}
