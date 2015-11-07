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

	private static final int DEFAULT_TIME_BETWEEN_SAMPLES = 20;
	private static final double TIME_IGNORE_MULTIPIER = 1.5;
	
	private JitterBufferQueue buffer;
	private short lastReadSeqNbr = -1;
	private long lastReadtimestamp = -1;
	private boolean ready, buildMode;
	private PacketCounter pc;
	private Logger logger;

	private int bufferTime;
	private int frameSizeMs;
	
	/**
	 * Constructs a JitterBuffer which delays the media in favor of consistency
	 * where packets have more time to arrive and can be sorted in the right order.
	 * Higher buffer time will improve consistency on poor networks but will add a
	 * delay to the processing of the media equal to the buffer time. 
	 * @param bufferTime The delay the JitterBuffer has to work with in milliseconds.
	 */
	public JitterBuffer(int bufferTime) {
		this(bufferTime, DEFAULT_TIME_BETWEEN_SAMPLES);
	}
	
	public JitterBuffer(int bufferTime, int frameSizeMs) {
		logger = Logger.getLogger("Debug");
		buffer = new JitterBufferQueue();
		pc = new PacketCounter(logger);
		this.bufferTime = bufferTime;
		this.frameSizeMs = frameSizeMs;
	}
	
	/**
	 * Adds the packet to the correct place in the queue or drops it if it arrived too late.
	 * @param packet The RTPPacket to be added to the queue.
	 */
	public void write(RTPPacket packet) {
		if(((short)(packet.seqNumber - lastReadSeqNbr) > 0) || (packet.seqNumber>0 && lastReadSeqNbr<0)) {
			buffer.offer(packet);
			if(isFullyBuffered()) {
				if (!ready) {
					ready = true;					
				}
				buildMode = false;
				if (buffer.getBufferedTime()>(bufferTime*2)) { // Buffer is too long, reducing delay
					while(buffer.getBufferedTime()>(bufferTime)) {
						pc.neverReadPacket();
						buffer.poll();
					}
				}
			}
		} // else we don't do anything (drop the packet)
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
				pc.normalPacket();
				p = buffer.poll();
			} else {
				RTPPacket tmp = buffer.peek();
				if (tmp != null) {
					if (tmp.seqNumber == ((short)(lastReadSeqNbr + 1))) {
						if (tmp.timestamp - TIME_IGNORE_MULTIPIER*frameSizeMs <= lastReadtimestamp) { // x*timestamp to compensate for variations.
							pc.normalPacket();
							p = buffer.poll();
						} else {
							pc.silentPacket();
							lastReadtimestamp += frameSizeMs;
						}
					} else {
						pc.lostPacket();
						lastReadSeqNbr += 1;
						lastReadtimestamp += frameSizeMs;
					}
				} else {
					pc.notReadyPacket();
					ready = false;
				}
			}
			if (p!=null) {
				lastReadSeqNbr = p.seqNumber;
				lastReadtimestamp = p.timestamp;				
			}
		} else {
			pc.notReadyPacket();
		}
		return p;
	}

	/**
	 * Sets the delay of the jitterbuffer has to correct potential errors in the buffer.
	 * @param bufferTime Time in milliseconds.
	 */
	public void setBufferTime(int bufferTime) {
		this.bufferTime = bufferTime;
	}
	
	/**
	 * Sets the time between the packets to enable silent building of the buffer.
	 * @param frameSize The time between packets in milliseconds.
	 */
	public void setFrameSizeMs(int frameSize) {
		this.frameSizeMs = frameSize;
	}
	
	/**
	 * Flush the jitter buffer to empty it of all current packets.
	 */
	public void flush() {
		while(ready) {
			read();
		}
		lastReadSeqNbr = Short.MIN_VALUE;
	}
	
	private boolean isFullyBuffered() {
		return buffer.getBufferedTime() >= bufferTime;
	}
	
	private static class PacketCounter {
		
		private enum PacketStatus {
			NORMAL, SILENT, LOST, NOT_READY, NEVER_READ;
		}
		
		private PacketStatus[] statusArr = new PacketStatus[100];
		private int psPointer = -1;
		private int[] counters = {100, 0, 0, 0, 0};
		private Logger logger;

		public PacketCounter(Logger logger) {
			for (int i=0; i< statusArr.length; i++) {
				statusArr[i] = PacketStatus.NORMAL;
			}
			this.logger = logger;
		}
		
		private PacketStatus next() {
			psPointer = ++psPointer%statusArr.length;
			return statusArr[psPointer];
		}
		
		private synchronized void update(PacketStatus ps) {
			PacketStatus next = next();
			if (!next.equals(ps)) {
				decrease(next);
				increase(ps);
				statusArr[psPointer] = ps;
			}
			if (psPointer == 0 && getSilentPercentage() != 0) {
				logger.log(Level.FINEST, "(" + Thread.currentThread().getName() + ") Silent packages: "
						+ getSilentPercentage() + "% " + countersToString());
			}
		}
		
		public void normalPacket() {
			update(PacketStatus.NORMAL);
		}
		
		public void silentPacket() {
			update(PacketStatus.SILENT);
		}
		
		public void lostPacket() {
			update(PacketStatus.LOST);
		}
		
		public void notReadyPacket() {
			update(PacketStatus.NOT_READY);
		}
		
		public void neverReadPacket() {
			update(PacketStatus.NEVER_READ);
		}
		
		private void increase(PacketStatus ps) {
			counters[ps.ordinal()]++;
		}
		
		private void decrease(PacketStatus ps) {
			counters[ps.ordinal()]--;
		}
		
		private int getSilentPercentage() {
			return counters[PacketStatus.SILENT.ordinal()]+counters[PacketStatus.LOST.ordinal()]+counters[PacketStatus.NOT_READY.ordinal()]+counters[PacketStatus.NEVER_READ.ordinal()];
		}
		
		private int getErrorPercentage() {
			return counters[PacketStatus.LOST.ordinal()]+counters[PacketStatus.NOT_READY.ordinal()]+counters[PacketStatus.NEVER_READ.ordinal()];
		}
		
		private String countersToString() {
			return "[Normal:"+counters[PacketStatus.NORMAL.ordinal()]+" Silent:"+counters[PacketStatus.SILENT.ordinal()]
					+" Lost:"+counters[PacketStatus.LOST.ordinal()]+" NotReady:"+counters[PacketStatus.NOT_READY.ordinal()]
							+" NeverRead:"+counters[PacketStatus.NEVER_READ.ordinal()];
		}
	}
}
