package se.chalmers.fleetspeak.Network.UDP;

/**
 * Created by Nieo on 28/08/15.
 */
public class RTPPacket {

    public static final int HEADER_SIZE = 12;

    protected int version = 2;
    protected boolean padding = false;
    protected boolean extensions = false;
    protected int cc = 0;
    protected boolean marker = false;
    protected byte payloadType;
    protected short seqNumber;
    protected long timestamp;
    protected long ssrc;
    private byte[] payload;

    /**
     * Creates a RTPPacket meant to be sent.
     * Defaults the values not given to false if boolean
     * and 0 if not, except for RTP-version which is set to 2.
     * @param payloadType The type of the payload.
     * @param seqNumber The sequence number of the packet.
     * @param timestamp The timestamp of the packet.
     * @param ssrc A long of which the first 32 bits is
     * interpreted as the SSRC and the last 32 bits as the CSRC.
     * @param payload The data to be sent.
     */
    public RTPPacket(byte payloadType, short seqNumber, long timestamp, long ssrc,
                     byte[] payload) {
        this.payloadType = payloadType;
        this.payload = payload;
        this.seqNumber = seqNumber;
        this.timestamp = timestamp;
        this.ssrc = ssrc;
    }

    /**
     * Creates a RTPPacket meant to be sent.
     * Defaults the values not given to false if boolean
     * and 0 if not, except for RTP-version which is set to 2.
     * @param seqNumber The sequence number of the packet.
     * @param timestamp The timestamp of the packet.
     * @param payload The data to be sent.
     */
    public RTPPacket(short seqNumber, long timestamp, byte[] payload) {
        this((byte)0, seqNumber, timestamp, 0, payload);
    }
    /**
     * This is a more effective method than the detailed one which only
     * tags the sequence number and timestamp dynamically and sets
     * all the others to their defaults.
     * @return The simplified byte-array conversion of the RTPPacket.
     */
    public byte[] toByteArraySimple() {
        byte[] b = new byte[payload.length + 12];
        b[0] = (byte) 0x80;

        b[2] = (byte) ((seqNumber & 0xFF00) >> 8);
        b[3] = (byte) (seqNumber & 0x00FF);

        b[4] = (byte) ((timestamp & 0xFF000000) >> 24);
        b[5] = (byte) ((timestamp & 0x00FF0000) >> 16);
        b[6] = (byte) ((timestamp & 0x0000FF00) >> 8);
        b[7] = (byte) ((timestamp & 0x000000FF));

        System.arraycopy(payload, 0, b, 12, payload.length);
        return b;
    }
}
