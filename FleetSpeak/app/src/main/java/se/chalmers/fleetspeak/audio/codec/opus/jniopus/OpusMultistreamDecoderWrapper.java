package se.chalmers.fleetspeak.audio.codec.opus.jniopus;

/**
 * Created by Fridgeridge on 2015-07-19.
 */
public class OpusMultistreamDecoderWrapper {

    public native static int getSize(int streams, int coupledStreams);

    public native static int init(long instance, int frames, int channels, int streams, int coupledStreams, byte[] mapping);

    public native static long create(int sampleRate, int channels);

    public native static int decodeNative(long instance);

    public native static int decode(long instance, byte[] inData );

    public native static void destroy(long instance);

}
