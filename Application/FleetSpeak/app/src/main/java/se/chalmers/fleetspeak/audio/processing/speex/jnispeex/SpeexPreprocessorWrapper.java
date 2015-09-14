package se.chalmers.fleetspeak.audio.processing.speex.jnispeex;


/**
 *  This is the Speex preprocessor. The preprocess can do noise suppression, residual echo suppression (after using the echo canceller), automatic gain control (AGC) and voice activity detection (VAD).
 *  http://www.speex.org/docs/api/speex-api-reference/group__SpeexPreprocessState.html
 * Wrapped by Fridgeridge on 2015-09-08.
 */
public class SpeexPreprocessorWrapper {


    /**
     * Used like the ioctl function to control the preprocessor parameters
     * @param st Preprocessor state
     * @param request ioctl-type request (one of the SPEEX_PREPROCESS_* macros)
     * @param ptr Data exchanged to-from function
     * @return 0 if no error, -1 if request in unknown
     */
    public native static int ctl(long st, int request, long ptr);

    /**
     * Update preprocessor state, but do not compute the output
     * @param st Preprocessor state
     * @param x Audio sample vector (in and out). Must be same size as specified in
     */
    public native static void estimateUpdate(long st, byte[] x);

    /**
     * Preprocess a frame
     * @param st Preprocessor state
     * @param x Audio sample vector (in and out). Must be same size as specified in init
     * @return Bool value for voice activity (1 for speech, 0 for noise/silence), ONLY if VAD turned on.
     */
    public native static int run(long st, byte[] x);

    /**
     * Destroys a preprocessor state
     * @param st Preprocessor state to destroy
     */
    public native static void destroy(long st);

    /**
     * Creates a new preprocessing state. You MUST create one state per channel processed.
     * @param frameSize Number of samples to process at one time (should correspond to 10-20 ms). Must be the same value as that used for the echo canceller for residual echo cancellation to work.
     * @param sampleRate Sampling rate used for the input.
     * @return Newly created preprocessor state
     */
    public native static long init(int frameSize, int sampleRate);



}
