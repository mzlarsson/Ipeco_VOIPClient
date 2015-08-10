package se.chalmers.fleetspeak.audio.codec.opus.jniopus;

/**
 * Created by Fridgeridge on 2015-07-19.
 */
public class OpusMultistreamDecoderWrapper {

    /**
     * Gets the size of an OpusMSDecoder structure.
     * @param streams The total number of streams coded in the input. This must be no more than 255.
     * @param coupledStreams Number streams to decode as coupled (2 channel) streams.
     * This must be no larger than the total number of streams. Additionally, The total number of coded channels (streams + coupled_streams) must be no more than 255.
     * @return The size in bytes on success, or a negative error code (see Error codes) on error.
     */
    public native static int getSize(int streams, int coupledStreams);

    /**
     * Allocates and initializes a multistream decoder state.
     * @param sampleRate Sampling rate to decode at (in Hz). This must be one of 8000, 12000, 16000, 24000, or 48000.
     * @param channels Number of channels to output. This must be at most 255. It may be different from the number of coded channels (streams + coupled_streams).
     * @param streams The total number of streams coded in the input. This must be no more than 255.
     * @param coupledStreams Number of streams to decode as coupled (2 channel) streams. This must be no larger than the total number of streams.
     *                       Additionally, The total number of coded channels (streams + coupled_streams) must be no more than 255.
     * @param mapping Mapping from coded channels to output channels.
     * @return A long pointing to the Opus multistream decoder instance
     */
    public native static long create(int sampleRate, int channels, int streams, int coupledStreams, byte[] mapping);

    /**
     *
     * @param instance Multistream decoder state.
     * @param opusInData Input payload.
     * @param opusOffset Offset to the encoded data.
     * @param opusLength Number of bytes in payload.
     * @param pcmOutData Output signal, with interleaved samples. This must contain room for frame_size*channels samples.
     * @param frameSize The number of samples per channel of available space in pcm. For the PLC and FEC cases, frame_size must be a multiple of 2.5 ms.
     * @param fec Flag (0 or 1) to request that any in-band forward error correction data be decoded. If no such data is available, the frame is decoded as if it were lost.
     * @return Number of samples decoded on success or a negative error code on failure.
     */
    public native static int decode(long instance, byte[] opusInData, int opusOffset, int opusLength, byte[] pcmOutData, int frameSize, int fec);

    /**
     * Frees an OpusMSDecoder allocated by opus_multistream_decoder_create().
     * @param instance Multistream decoder state to be freed.
     */
    public native static void destroy(long instance);

}
