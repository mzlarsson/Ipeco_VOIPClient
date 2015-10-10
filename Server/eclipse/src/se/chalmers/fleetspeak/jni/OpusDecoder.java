package se.chalmers.fleetspeak.jni;

public class OpusDecoder{
	static{
        System.loadLibrary("NativeAudio");
    }

    /**
     *  Gets the size of an OpusDecoder structure.
     * @param channels Number of channels. This must be 1 or 2.
     * @return The size in bytes.
     */
    public native static int getSize(int channels);

    /**
     * Creates an Opus decoder instance
     * @param sampleRate The decoding target sample rate
     * @param channels The number of channels of the decoding target.
     * @return A long pointing to the Opus decoder instance
     */
    public native static long create(int sampleRate, int channels);

    /**
     * Gets the number of frames in an Opus packet.
     * @param opusInData Opus packet
     * @param offset Offset of packet
     * @param length Length of packet
     * @return The number of frames
     */
    public native static int getPacketFrames(byte[] opusInData, int offset, int length);

    /**
     *
     * @param opusInstance Opus decoder instance
     * @param opusInData The encoded Opus data
     * @param opusInDataOffset Offset to the encoded data
     * @param inputLength Number of bytes in payload
     * @param pcmOutData Decoded output pcm data
     * @param pcmOutDataOffset Offset to the decoded data
     * @param frameSize Number of samples per channel
     * @param fec  Flag (0 or 1) to request that any in-band forward error correction data be decoded
     * @return
     */
    public native static int decode(
                                    long opusInstance,
                                    byte[] opusInData, int opusInDataOffset, int inputLength,
                                    byte[] pcmOutData, int pcmOutDataOffset, int frameSize,
                                    int fec);
    /**
     * Destroys the decoder and frees memory
     * @param opusInstance Address to the Opus decoder instance
     */
    public native static void destroy(long opusInstance);
}
