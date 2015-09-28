package se.chalmers.fleetspeak.audio.processing.speex.jnispeex;

/**
 * A class for wrapping the speex echo canceller using JNI
 * http://www.speex.org/docs/api/speex-api-reference/group__SpeexEchoState.html#g306c6fd37bb64e4de6a50808e65cbedf
 * Wrapped by Fridgeridge on 2015-09-08.
 */
public class SpeexEchoStateWrapper {


    //Macros used by the ctl function
    public static final int GET_FRAME_SIZE = 3;
    public static final int SET_SAMPLING_RATE = 24;
    public static final int GET_SAMPLING_RATE = 25;

    /**
     * Performs echo cancellation a frame, based on the audio sent to the speaker
     * (no delay is added to playback ni this form)
     * @param st Echo canceller state
     * @param rec Signal from the microphone (near end + far end echo)
     * @param play Signal played to the speaker (received from far end)
     * @param out Returns near-end signal with echo removed
     */
    public native static void cancellation(long st, byte[] rec, byte[] play, byte[] out);

    /**
     * Perform echo cancellation using internal playback buffer,
     * which is delayed by two frames to account for the delay introduced by most soundcards
     * (but it could be off!)
     * @param st Echo canceller state
     * @param rec Signal from the microphone (near end + far end echo)
     * @param out Returns near-end signal with echo removed
     */
    public native static void capture(long st, byte[] rec, byte[] out);

    /**
     * Used like the ioctl function to control the echo canceller parameters
     * @param st Echo canceller state
     * @param request ioctl-type request (one of the SPEEX_ECHO_* macros)
     * @param ptr Data exchanged to-from function
     * @return 0 if no error, -1 if request in unknown
     */
    public native static int ctl(long st, int request, long ptr);

    /**
     * Let the echo canceller know that a frame was just queued to the soundcard
     * @param st Echo canceller state
     * @param play Signal played to the speaker (received from far end)
     */
    public native static void playback(long st, byte[] play);

    /**
     * Destroys an echo canceller state
     * @param st Echo canceller state
     */
    public native static void destroy(long st);

    /**
     * Creates a new echo canceller state
     * @param frameSize Number of samples to process at one time (should correspond to 10-20 ms)
     * @param filterLength Number of samples of echo to cancel (should generally correspond to 100-500 ms)
     * @return Newly-created echo canceller state
     */
    public native static long init(int frameSize, int filterLength);




}
