package se.chalmers.fleetspeak.network.udp;
import java.nio.ByteBuffer;

/**
 * Reads and interprets a byte array as a RTP-packet of version 2.
 * OBS! Currently does not support a CSRC count of more than one.
 * 
 * @author Patrik Haar
 */
public class RTPPacket {

    private int version = 2;
    private boolean padding = false;
    private boolean extensions = false;
    private int cc = 0;
    private boolean marker = false;
    private byte payloadType;
    private short seqNumber;
    private int timestamp;
    private long ssrc;
    private byte[] payload;

    
    /**
     * Creates a RTPPacket meant to be read.
     * The given ByteBuffer will be assumed to contain a RTP version 2 header
     * and read as such.
     * @param readerBuffer The ByteBuffer wrapping the data.
     */
    public RTPPacket(ByteBuffer readerBuffer) {
        int len = readerBuffer.limit();
        int b = readerBuffer.get() & 0xff;
        
        version = (b>>>6)&3;
        padding = (b & 0x20) == 0x20;
        extensions = (b & 0x10) == 0x10;
        cc = b & 0x0F;
        
        b = readerBuffer.get() & 0xff;
        marker = (b & 0x80) == 0x80;
        payloadType = (byte) (b & 0x7F);

        seqNumber = (short) (((readerBuffer.get() & 0xff) << 8) | (readerBuffer.get() & 0xff));

        timestamp = readerBuffer.getInt();
        ssrc = readerBuffer.getInt();
        
        payload = new byte[len - 12];
        readerBuffer.get(payload, 0, payload.length);
    }
    
    /**
     * Creates a RTPPacket meant to be read.
     * The given byte-array will be assumed to contain a RTP version 2 header
     * and read as such.
     * @param data A data packet with RTP version 2 format.
     */
    public RTPPacket(byte[] data) {
        int b = data[0] & 0xff;
        version = (b>>>6)&3;
        padding = (b & 0x20) == 0x20;
        extensions = (b & 0x10) == 0x10;
        cc = b & 0x0F;

        b = data[1] & 0xff;

        marker = (b & 0x80) == 0x80;
        payloadType = (byte) (b & 0x7F);
        seqNumber = (short) (((data[2] & 0xff) << 8) | (data[3] & 0xff));

        timestamp = data[7] & 0xFF |
                (data[6] & 0xFF) << 8 |
                (data[5] & 0xFF) << 16 |
                (data[4] & 0xFF) << 24;
        
        ssrc = data[11] & 0xFF |
                (data[10] & 0xFF) << 8 |
                (data[9] & 0xFF) << 16 |
                (data[8] & 0xFF) << 24;
        
        payload = new byte[data.length-12];
        System.arraycopy(data, 12, payload, 0, data.length-12);;
    }

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
    public RTPPacket(byte payloadType, short seqNumber, int timestamp, long ssrc,
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
    public RTPPacket(short seqNumber, int timestamp, byte[] payload) {
    	this((byte)0, seqNumber, timestamp, 0, payload);
    }
    
    public int getVersion() {
    	return version;
    }
    
    public boolean isPadded() {
    	return padding;
    }
    
    public boolean isExtended() {
    	return extensions;
    }
    
    public int getCSRCCount() {
    	return cc;
    }
    
    public boolean getMarker() {
        return marker;
    }
    
    public int getPayloadType() {
        return payloadType;
    }

    public int getSeqNumber() {
        return this.seqNumber;
    }

    public byte[] getPayload() {
        return payload;
    }

    public int getTimestamp() {
        return timestamp;
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
    
    /**
     * A detailed conversion which includes all the information of
     * the RTPPacket into the resulting byte-array.
     * @return The RTPPacket converted to a byte-array.
     */
    public byte[] toByteArrayDetailed() {
    	byte[] b = new byte[payload.length + 12];
    	b[0] = (byte) ((version << 6) | (padding?0x20:0) | (extensions?0x10:0) | cc);
        b[1] = (byte) (payloadType | (marker?0x80:0));
        b[2] = (byte) ((seqNumber & 0xFF00) >> 8);
        b[3] = (byte) (seqNumber & 0x00FF);

        b[4] = (byte) ((timestamp & 0xFF000000) >> 24);
        b[5] = (byte) ((timestamp & 0x00FF0000) >> 16);
        b[6] = (byte) ((timestamp & 0x0000FF00) >> 8);
        b[7] = (byte) ((timestamp & 0x000000FF));
        
        b[8] = (byte) ((ssrc & 0xFF000000) >> 24);
        b[9] = (byte) ((ssrc & 0x00FF0000) >> 16);
        b[10] = (byte) ((ssrc & 0x0000FF00) >> 8);
        b[11] = (byte) ((ssrc & 0x000000FF));
        
        System.arraycopy(payload, 0, b, 12, payload.length);
        return b;
    }

    @Override
    public String toString() {
        return "RTP Packet[seq=" + this.seqNumber + ", timestamp=" + timestamp +
                ", payload_size=" + payload.length + ", payload=" + payloadType + "]";
    }
}