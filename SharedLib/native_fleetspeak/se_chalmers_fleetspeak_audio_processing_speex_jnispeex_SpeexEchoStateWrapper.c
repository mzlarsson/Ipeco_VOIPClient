


/*
 * Class:     se_chalmers_fleetspeak_audio_processing_speex_jnispeex_SpeexEchoStateWrapper
 * Method:    cancellation
 * Signature: (J[B[B[B)V
 */
JNIEXPORT void JNICALL Java_se_chalmers_fleetspeak_audio_processing_speex_jnispeex_SpeexEchoStateWrapper_cancellation
(JNIEnv *, jclass, jlong, jbyteArray, jbyteArray, jbyteArray);

/*
 * Class:     se_chalmers_fleetspeak_audio_processing_speex_jnispeex_SpeexEchoStateWrapper
 * Method:    capture
 * Signature: (J[B[B)V
 */
JNIEXPORT void JNICALL Java_se_chalmers_fleetspeak_audio_processing_speex_jnispeex_SpeexEchoStateWrapper_capture
(JNIEnv *, jclass, jlong, jbyteArray, jbyteArray);

/*
 * Class:     se_chalmers_fleetspeak_audio_processing_speex_jnispeex_SpeexEchoStateWrapper
 * Method:    ctl
 * Signature: (JIJ)I
 */
JNIEXPORT jint JNICALL Java_se_chalmers_fleetspeak_audio_processing_speex_jnispeex_SpeexEchoStateWrapper_ctl
        (JNIEnv *, jclass, jlong, jint, jlong);

/*
 * Class:     se_chalmers_fleetspeak_audio_processing_speex_jnispeex_SpeexEchoStateWrapper
 * Method:    playback
 * Signature: (J[B)V
 */
               JNIEXPORT void JNICALL Java_se_chalmers_fleetspeak_audio_processing_speex_jnispeex_SpeexEchoStateWrapper_playback
               (JNIEnv *, jclass, jlong, jbyteArray);

/*
 * Class:     se_chalmers_fleetspeak_audio_processing_speex_jnispeex_SpeexEchoStateWrapper
 * Method:    destroy
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_se_chalmers_fleetspeak_audio_processing_speex_jnispeex_SpeexEchoStateWrapper_destroy
(JNIEnv *, jclass, jlong);

/*
 * Class:     se_chalmers_fleetspeak_audio_processing_speex_jnispeex_SpeexEchoStateWrapper
 * Method:    init
 * Signature: (II)J
 */
JNIEXPORT jlong JNICALL Java_se_chalmers_fleetspeak_audio_processing_speex_jnispeex_SpeexEchoStateWrapper_init
        (JNIEnv *, jclass, jint, jint);