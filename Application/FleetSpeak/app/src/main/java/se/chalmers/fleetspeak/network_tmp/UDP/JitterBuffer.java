package se.chalmers.fleetspeak.network_tmp.UDP;

/**
 * Created by Volt on 29/08/15.
 */
public class JitterBuffer{

    private static final int SOUND_ARRAY_SIZE = 320;
    private static final int TIME_BETWEEN_SAMPLES = 20;

    private JitterBufferQueue buffer;
    private short lastReadSeqNbr = -1;
    private long lastReadtimestamp = -1;
    private boolean ready, buildMode;

    private long bufferTime;

    /**
     * Constructs a JitterBuffer which delays the media in favor of consistency
     * where packets have more time to arrive and can be sorted in the right order.
     * Higher buffer time will improve consistency on poor networks but will add a
     * delay to the processing of the media equal to the buffer time.
     * @param bufferTime The delay the JitterBuffer has to work with in milliseconds.
     */
    public JitterBuffer(long bufferTime) {
        buffer = new JitterBufferQueue();
        this.bufferTime = bufferTime;
    }

    /**
     * Adds the packet to the correct place in the queue or drops it if it arrived too late.
     * @param packet The RTPPacket to be added to the queue.
     */
    public void write(RTPPacket packet) {
        if(packet.seqNumber > lastReadSeqNbr) {
            buffer.offer(packet);
            if(isFullyBuffered()) {
                if (!ready) {
                    ready = true;
                }
                buildMode = false;
                if (buffer.getBufferedTime()>(bufferTime*2)) { //If the buffer is TOO long we poll until we are "up to date" again.
                    while(buffer.getBufferedTime()>(bufferTime)) {
                        buffer.poll();
                    }
                }
            }
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
                lastReadSeqNbr = p.seqNumber;
                lastReadtimestamp = p.timestamp;
            }
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
