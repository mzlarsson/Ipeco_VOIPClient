#include <jni.h>
#include <stdint.h>
#include <opus.h>
#include <speex/speex_echo.h>
#include <speex/speex_preprocess.h>

/*
 * Method:    processAll
 * Signature: (JJJI[BI[BI[BI)I
 */
JNIEXPORT jint JNICALL Java_se_chalmers_fleetspeak_audio_processing_NativeAudioProcessor_processAll
        (JNIEnv *env, jclass jc, jlong opusEncoder, jlong speexEchoState, jlong speexProcessorState,
         jint sampleRate, jbyteArray src, jint srcOffset, jbyteArray play, jint playOffset,
         jbyteArray output, jint outputLength)
{

}

/*
 * Class:     se_chalmers_fleetspeak_audio_processing_NativeAudioProcessor
 * Method:    setup
 * Signature: (JJJ)I
 */
JNIEXPORT jint JNICALL Java_se_chalmers_fleetspeak_audio_processing_NativeAudioProcessor_setup
        (JNIEnv *env, jclass jc, jlong opusEncoder, jlong speexEchoState, jlong speexProcessorState)
{

}

/*
 * Class:     se_chalmers_fleetspeak_audio_processing_NativeAudioProcessor
 * Method:    processorCTL
 * Signature: (JIJ)I
 */
JNIEXPORT jint JNICALL Java_se_chalmers_fleetspeak_audio_processing_NativeAudioProcessor_processorCTL
        (JNIEnv *env, jclass jc, jlong speexProcessorState, jint request, jlong ptr)
{

}
/*
 * Class:     se_chalmers_fleetspeak_audio_processing_NativeAudioProcessor
 * Method:    encoderCTL
 * Signature: (JI)I
 */
JNIEXPORT jint JNICALL Java_se_chalmers_fleetspeak_audio_processing_NativeAudioProcessor_encoderCTL
        (JNIEnv *env, jclass jc, jlong opusEncoder, jint request)
{

}
/*
 * Class:     se_chalmers_fleetspeak_audio_processing_NativeAudioProcessor
 * Method:    echoCTL
 * Signature: (JIJ)I
 */
JNIEXPORT jint JNICALL Java_se_chalmers_fleetspeak_audio_processing_NativeAudioProcessor_echoCTL
        (JNIEnv *env, jclass jc, jlong speexEchoState, jint request, jlong ptr)
{

}

/*
 * Class:     se_chalmers_fleetspeak_audio_processing_NativeAudioProcessor
 * Method:    encodeToOpus
 * Signature: (J[BII[BII)I
 */
JNIEXPORT jint JNICALL Java_se_chalmers_fleetspeak_audio_processing_NativeAudioProcessor_encodeToOpus
        (JNIEnv *env, jclass jc, jlong opusEncoder, jbyteArray pcmInData, jint pcmInDataOffset,
         jint pcmSampleRate, jbyteArray opusOutData, jint opusOutDataOffset, jint outputLength)
{
    jbyte *audioInData;
    jbyte *audioOutData;

    audioInData = (*env)->GetPrimitiveArrayCritical(env, pcmInData, 0);
    audioOutData = (*env)->GetPrimitiveArrayCritical(env, opusOutData, 0);

    int encodedBytes = opus_encode((OpusEncoder * )(intptr_t)(opusEncoder),
                                   (opus_int16 * )(audioInData),
                                   pcmSampleRate,
                                   (unsigned char *) (audioOutData),
                                   outputLength);
    (*env)->ReleasePrimitiveArrayCritical(env, opusOutData, audioOutData, 0);
    (*env)->ReleasePrimitiveArrayCritical(env, pcmInData, audioInData, JNI_ABORT);

    return encodedBytes;
}

/*
 * Class:     se_chalmers_fleetspeak_audio_processing_NativeAudioProcessor
 * Method:    cancellation
 * Signature: (J[B[B[B)V
 */
JNIEXPORT void JNICALL Java_se_chalmers_fleetspeak_audio_processing_NativeAudioProcessor_cancellation
        (JNIEnv *env, jclass jc, jlong speexEchoState, jbyteArray rec, jbyteArray play, jbyteArray out)
{

}

/*
 * Class:     se_chalmers_fleetspeak_audio_processing_NativeAudioProcessor
 * Method:    run
 * Signature: (J[B)I
 */
JNIEXPORT jint JNICALL Java_se_chalmers_fleetspeak_audio_processing_NativeAudioProcessor_run
        (JNIEnv *env, jclass jc, jlong speexProcessorState, jbyteArray x)
{

}

/*
 * Class:     se_chalmers_fleetspeak_audio_processing_NativeAudioProcessor
 * Method:    destroy
 * Signature: (JJJ)V
 */
JNIEXPORT void JNICALL Java_se_chalmers_fleetspeak_audio_processing_NativeAudioProcessor_destroy
(JNIEnv *env, jclass jc, jlong opusEncoder, jlong speexEchoState, jlong speexProcessorState)
{
opus_encoder_destroy((OpusEncoder *) (intptr_t)(opusEncoder));
speex_echo_state_destroy((SpeexEchoState *) (intptr_t)(speexEchoState));
speex_preprocess_state_destroy((SpeexPreprocessState *) (intptr_t) (speexProcessorState));
}

/*
 * Class:     se_chalmers_fleetspeak_audio_processing_NativeAudioProcessor
 * Method:    createOpusEncoder
 * Signature: (II)J
 */
JNIEXPORT jlong JNICALL Java_se_chalmers_fleetspeak_audio_processing_NativeAudioProcessor_createOpusEncoder
        (JNIEnv *env, jclass jc, jint channels, jint sampleRate)
{
    int error;
    OpusEncoder *opusEncoder = opus_encoder_create(sampleRate, channels, OPUS_APPLICATION_VOIP, &error);

    if(OPUS_OK != error)
        opusEncoder = 0;
    return (jlong) (intptr_t) (opusEncoder);
}

/*
 * Class:     se_chalmers_fleetspeak_audio_processing_NativeAudioProcessor
 * Method:    createSpeexProcessor
 * Signature: (II)J
 */
JNIEXPORT jlong JNICALL Java_se_chalmers_fleetspeak_audio_processing_NativeAudioProcessor_createSpeexProcessor
        (JNIEnv *env, jclass jc, jint frameSize, jint sampleRate)
{
    SpeexPreprocessState *sps = speex_preprocess_state_init(frameSize, sampleRate);
    return  (jlong) (intptr_t) (sps);
}

/*
 * Class:     se_chalmers_fleetspeak_audio_processing_NativeAudioProcessor
 * Method:    createSpeexEchoState
 * Signature: (II)J
 */
JNIEXPORT jlong JNICALL Java_se_chalmers_fleetspeak_audio_processing_NativeAudioProcessor_createSpeexEchoState
        (JNIEnv *env, jclass jc, jint frameSize, jint filterLength)
{
    SpeexEchoState *ses = speex_echo_state_init(frameSize, filterLength);
    return  (jlong) (intptr_t) (ses);
}