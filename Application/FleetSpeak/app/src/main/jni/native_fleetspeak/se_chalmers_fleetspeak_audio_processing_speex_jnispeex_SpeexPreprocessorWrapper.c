
/*
 * Class:     se_chalmers_fleetspeak_audio_processing_speex_jnispeex_SpeexPreprocessorWrapper
 * Method:    ctl
 * Signature: (JIJ)I
 */
JNIEXPORT jint JNICALL Java_se_chalmers_fleetspeak_audio_processing_speex_jnispeex_SpeexPreprocessorWrapper_ctl
        (JNIEnv *, jclass, jlong, jint, jlong);

/*
 * Class:     se_chalmers_fleetspeak_audio_processing_speex_jnispeex_SpeexPreprocessorWrapper
 * Method:    estimateUpdate
 * Signature: (J[B)V
 */
               JNIEXPORT void JNICALL Java_se_chalmers_fleetspeak_audio_processing_speex_jnispeex_SpeexPreprocessorWrapper_estimateUpdate
               (JNIEnv *, jclass, jlong, jbyteArray);

/*
 * Class:     se_chalmers_fleetspeak_audio_processing_speex_jnispeex_SpeexPreprocessorWrapper
 * Method:    run
 * Signature: (J[B)I
 */
JNIEXPORT jint JNICALL Java_se_chalmers_fleetspeak_audio_processing_speex_jnispeex_SpeexPreprocessorWrapper_run
        (JNIEnv *, jclass, jlong, jbyteArray);

/*
 * Class:     se_chalmers_fleetspeak_audio_processing_speex_jnispeex_SpeexPreprocessorWrapper
 * Method:    destroy
 * Signature: (J)V
 */
               JNIEXPORT void JNICALL Java_se_chalmers_fleetspeak_audio_processing_speex_jnispeex_SpeexPreprocessorWrapper_destroy
               (JNIEnv *, jclass, jlong);

/*
 * Class:     se_chalmers_fleetspeak_audio_processing_speex_jnispeex_SpeexPreprocessorWrapper
 * Method:    init
 * Signature: (II)J
 */
JNIEXPORT jlong JNICALL Java_se_chalmers_fleetspeak_audio_processing_speex_jnispeex_SpeexPreprocessorWrapper_init
        (JNIEnv *, jclass, jint, jint);