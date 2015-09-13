package se.chalmers.fleetspeak.Network.UDP;

import java.util.Arrays;

import se.chalmers.fleetspeak.audio.sound.AudioType;

/**
 * Reads and interprets a byte array as a RTP-packet of version 2.
 * OBS! Currently does not support a CSRC count of more than one.
 *
 * @author Patrik Haar
 */
public class RTPPacket {

    public static final int HEADER_SIZE = 12;

    protected int version = 2;
    protected boolean padding = false;
    protected boolean extensions = false;
    protected int cc = 0;
    protected boolean marker = false;
    protected AudioType payloadType = AudioType.NONE;
    protected short seqNumber;
    protected long timestamp;
    protected long ssrc = 0;
    private byte[] payload;

    /**
     * Creates a RTPPacket meant to interpret <b>incoming</b> packets.
     * The given byte-array will be assumed to contain a RTP version 2
     * header and will be read as such, the data will be trimmed down
     * if it is padded so that only the actual data is in the payload.
     * @param data A data packet with RTP version 2 format.
     */
    public RTPPacket(byte[] data) {
        if (data==null || data.length<12) {
            throw new IllegalArgumentException("Input array cannot be " + (data==null?"null.":"shorter than 12 in length."));
        }
        int b = data[0] & 0xff;
        version = (b>>>6)&3;
        padding = (b & 0x20) == 0x20;
        extensions = (b & 0x10) == 0x10;
        cc = b & 0x0F;

        b = data[1] & 0xff;

        marker = (b & 0x80) == 0x80;
        payloadType = AudioType.getAudioType(b & 0x7F);
        seqNumber = (short) (((data[2] & 0xff) << 8) | (data[3] & 0xff));

        timestamp = data[7] & 0xFF |
                (data[6] & 0xFF) << 8 |
                (data[5] & 0xFF) << 16 |
                (data[4] & 0xFF) << 24;

        ssrc = data[11] & 0xFF |
                (data[10] & 0xFF) << 8 |
                (data[9] & 0xFF) << 16 |
                (data[8] & 0xFF) << 24;

        if (data.length < (payloadType.getMaxLength()+HEADER_SIZE)) {
            throw new IllegalArgumentException("Data array was too short ("+ data.length
                    + " bytes), AudioType " + payloadType + " requires atleast "
                    + (payloadType.getMaxLength()+HEADER_SIZE) + " bytes");
        }
        byte[] payloadArray = new byte[data.length-HEADER_SIZE];
        System.arraycopy(data, HEADER_SIZE, payloadArray, 0, payloadArray.length);
        payload = getActualPayload(payloadArray);
        padding = false;
    }

    /**
     * Creates a RTPPacket meant to be <b>sent</b>, automatically
     * pads the data to match its payload type.
     * Defaults the values not given to false if boolean
     * and 0 if not, except for RTP-version which is set to 2.
     * @param payloadType The type of the payload.
     * @param seqNumber The sequence number of the packet.
     * @param timestamp The timestamp of the packet.
     * @param data The data to be sent.
     */
    public RTPPacket(AudioType payloadType, short seqNumber, long timestamp, byte[] data) {
        if (data==null || payloadType == null) {
            throw new NullPointerException("data byte-array or payload type cannot be null");
        }
        this.payloadType = payloadType;
        this.seqNumber = seqNumber;
        this.timestamp = timestamp;
        this.payload = getPaddedPayload(data);
        if (payload.length> data.length) {
            this.padding = true;
        }
    }

    public byte[] getPayload() {
        return !padding?payload:getActualPayload(payload);
    }

    /**
     * Calculates the padding on the packet and returns an array with the actual data
     * if the payload type of the packet is known, otherwise it return everything
     * except the header.
     * @param data The complete incoming packet minus the header.
     * @return An array with the actual data, or everything if unknown payloadtype.
     */
    private byte[] getActualPayload(byte[] data) {
        if(!payloadType.equals(AudioType.NONE) && (data.length>=payloadType.getMaxLength())) {
            byte emptyBytes = 0;
            if(padding) {
                emptyBytes = data[payloadType.getMaxLength() - 1];
            }
            byte[] barr = new byte[payloadType.getMaxLength()-emptyBytes];
            System.arraycopy(data, 0, barr, 0, barr.length);
            return barr;
        } else {
            return data;
        }
    }

    /**
     * Pads the data to the appropriate size for its payload type and tags how
     * many bytes were added at the end of the packet.
     * @param data The data to get padded to size.
     * @return The resulting byte array.
     */
    private byte[] getPaddedPayload(byte[] data) {
        if (!payloadType.equals(AudioType.NONE)) {
            byte[] barr = new byte[payloadType.getMaxLength()];
            if (data.length<barr.length) {
                System.arraycopy(data, 0, barr, 0, data.length);
                barr[barr.length-1] = (byte) (barr.length-data.length);
            } else {
                System.arraycopy(data, 0, barr, 0, barr.length);
            }
            return barr;
        } else {
            return data;
        }
    }

    /**
     * A detailed conversion which includes all the information of
     * the RTPPacket into the resulting byte-array.
     * @return The RTPPacket converted to a byte-array.
     */
    public byte[] toByteArrayDetailed() {
        byte[] b = new byte[payload.length + 12];
        b[0] = (byte) ((version << 6) | (padding?0x20:0) | (extensions?0x10:0) | cc);
        b[1] = (byte) (payloadType.getPayloadType() | (marker?0x80:0));
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
                ", payload_size=" + (payload==null?0:payload.length) + ", payload=" + payloadType + "]";
    }
}