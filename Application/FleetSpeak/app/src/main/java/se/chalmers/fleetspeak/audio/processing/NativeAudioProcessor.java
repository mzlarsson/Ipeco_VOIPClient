package se.chalmers.fleetspeak.audio.processing;

/**
 * A class for applying audio effects and encoding natively through NDK, using Opus and Speex
 * Created by Fridgeridge on 2015-09-10.
 */
public class NativeAudioProcessor {

    static{
        System.loadLibrary("NativeAudio");
    }

    /*
    Applies AEC, noise reduction and OPUS encoding to a PCM sample.
     */
    public native static int processAll(int frameSize, byte[] src, int srcOffset, byte[] play, int playOffset, byte[] output, int outputLength);

    /*
    Sets up the different processors with relevant settings and CTL-functions;
     */
    public native static int setup(long opusEncoder, long speexEchoState, long speexProcessorState);

    public native static int processorCTL(long speexProcessorState, int request, long ptr);

    public native static int encoderCTL(long opusEncoder, int request);

    public native static int echoCTL(long speexEchoState, int request, long ptr);

    public native static int encodeToOpus(
            long opusEncoder,
            byte[] pcmInData, int pcmInDataOffset, int pcmSampleRate,
            byte[] opusOutData, int opusOutDataOffset, int outputLength);

    public native static void cancellation(long speexEchoState, byte[] rec, byte[] play, byte[] out);

    public native static int run(long speexProcessorState, byte[] x);

    public native static void destroy(long opusEncoder, long speexEchoState, long speexProcessorState);

    public native static long createOpusEncoder(int channels, int sampleRate);

    public native static long createSpeexProcessor(int frameSize, int sampleRate);

    public native static long createSpeexEchoState(int frameSize, int filterLength);



}
