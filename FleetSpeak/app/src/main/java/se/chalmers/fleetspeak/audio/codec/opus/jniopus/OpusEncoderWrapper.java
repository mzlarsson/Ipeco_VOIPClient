package se.chalmers.fleetspeak.audio.codec.opus.jniopus;

/**
 * Created by Fridgeridge on 2015-07-19.
 */
public class OpusEncoderWrapper {

    static{
        System.loadLibrary("Opus");
    }

    /**
     *
     * @param channels Number of channels. This must be 1 or 2.
     * @return
     */
    public native static int getSize(int channels);

    /**
     * Creates an Opus encoder instance
     * @param sampleRate The encoding target sample rate
     * @param channels The number of channels of the encoding target.
     * @return A long pointing to the Opus encoder instance
     */
    public native static long create(int sampleRate, int channels);

    /**
     *
     * @param encoder Encoder state
     * @param pcmInData Input signal
     * @param pcmInDataOffset Starting point of data
     * @param pcmSampleRate Number of samples per channel in the input signal
     * @param opusOutData Output payload
     * @param opusOutDataOffset Starting point of data
     * @param outputLength Size of the allocated memory for the output payload
     * @return The length of the encoded packet (in bytes) on success or a negative error code (see Error codes) on failure.
     */
    public native static int encode(
            long encoder,
            byte[] pcmInData, int pcmInDataOffset, int pcmSampleRate,
            byte[] opusOutData, int opusOutDataOffset, int outputLength);

    /**
     * Destroys the encoder and frees memory
     * @param opusInstance Address to the Opus encoder instance
     */
    public native static void destroy(long opusInstance);

}
