package se.chalmers.fleetspeak.network.udp;

import java.util.logging.Level;
import java.util.logging.Logger;

import se.chalmers.fleetspeak.sound.BufferedAudioStream;

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
public class JitterBuffer implements BufferedAudioStream{

	private static final int SOUND_ARRAY_SIZE = 320;
	private static final int TIME_BETWEEN_SAMPLES = 40;
	
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
		if(packet.seqNumber > lastReadSeqNbr) {
			//FIXME Temporary printout.
//			byte[] payload = packet.getPayload();
//			System.out.print(packet.seqNumber + "\t" + packet.timestamp + "\t");
//			for(int i=0; i<payload.length; i++) {
//				System.out.print(payload[i] + " ");
//			}
//			System.out.println();
			buffer.offer(packet);
			if(isFullyBuffered()) {
				if (!ready) {
					ready = true;					
				}
				buildMode = false;
			}
		} else {
			logger.log(Level.WARNING,"Dropped a packet arriving to late, "
					+ "sequence number: " + packet.seqNumber);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public byte[] read() {
		byte[] audio = null;
		if(ready) {
			RTPPacket p = null;
			if(!buildMode) {
				if (!isFullyBuffered()) {
					buildMode = true;
				}
				p = buffer.poll();
			} else {
				RTPPacket tmp = buffer.peek();
				if (tmp != null) {
					if (tmp.seqNumber == ((short)(lastReadSeqNbr + 1))) {
						if (tmp.timestamp - 2*TIME_BETWEEN_SAMPLES <= lastReadtimestamp) { // 2*timestamp to compensate for variations.
							p = buffer.poll();
						} else {
							lastReadtimestamp += TIME_BETWEEN_SAMPLES;
						}
					} else {
						lastReadSeqNbr += 1;
						lastReadtimestamp += TIME_BETWEEN_SAMPLES;
					}
				} else {
					ready = false;
				}
			}
			if (p!=null) {
				audio = p.toByteArraySimple();
				lastReadSeqNbr = p.seqNumber;
				lastReadtimestamp = p.timestamp;				
			}
		}
		return audio;
	}

	private boolean isFullyBuffered() {
		return buffer.getBufferedTime() > bufferTime;
	}
	
	public int getSoundArraySize() {
		return SOUND_ARRAY_SIZE;
	}
}
