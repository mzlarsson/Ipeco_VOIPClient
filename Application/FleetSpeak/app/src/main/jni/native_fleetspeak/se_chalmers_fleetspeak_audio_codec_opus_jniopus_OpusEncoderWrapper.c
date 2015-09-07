#include <jni.h>
#include <stdint.h>
#include <opus.h>
#include <se_chalmers_fleetspeak_audio_codec_opus_jniopus_OpusEncoderWrapper.h>


/*
 * Class:     se_chalmers_fleetspeak_audio_codec_opus_jniopus_OpusEncoderWrapper
 * Method:    getSize
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_se_chalmers_fleetspeak_audio_codec_opus_jniopus_OpusEncoderWrapper_getSize
  (JNIEnv *env, jclass jc, jint channels)
  {
    return opus_encoder_get_size(channels);
  }

/*
 * Class:     se_chalmers_fleetspeak_audio_codec_opus_jniopus_OpusEncoderWrapper
 * Method:    create
 * Signature: (II)J
 */
JNIEXPORT jlong JNICALL Java_se_chalmers_fleetspeak_audio_codec_opus_jniopus_OpusEncoderWrapper_create
  (JNIEnv *env, jclass jc, jint sampleRate, jint channels)
  {
    int error;
    OpusEncoder *opusEncoder = opus_encoder_create(sampleRate, channels, OPUS_APPLICATION_VOIP, &error);

    if(OPUS_OK != error)
      opusEncoder = 0;
    return (jlong) (intptr_t) (opusEncoder);
  }

/*
 * Class:     se_chalmers_fleetspeak_audio_codec_opus_jniopus_OpusEncoderWrapper
 * Method:    encode
 * Signature: (J[BII[BII)I
 */
JNIEXPORT jint JNICALL Java_se_chalmers_fleetspeak_audio_codec_opus_jniopus_OpusEncoderWrapper_encode
  (JNIEnv *env, jclass jc, jlong encoder, jbyteArray pcmInData, jint pcmInDataOffset, jint pcmSampleRate, jbyteArray opusOutData, jint opusOutDataOffset, jint outputLength)
{
  jbyte *audioInData;
  jbyte *audioOutData;

  audioInData = (*env)->GetPrimitiveArrayCritical(env, pcmInData, 0);
  audioOutData = (*env)->GetPrimitiveArrayCritical(env, opusOutData, 0);

  int encodedBytes = opus_encode((OpusEncoder *)(intptr_t)(encoder),
                                   (opus_int16 *)(audioInData),
                                   pcmSampleRate,
                                   (unsigned char *) (audioOutData),
                                   outputLength);
    (*env)->ReleasePrimitiveArrayCritical(env, opusOutData, audioOutData, 0);
    (*env)->ReleasePrimitiveArrayCritical(env, pcmInData, audioInData, JNI_ABORT);

  return encodedBytes;
}

/*
 * Class:     se_chalmers_fleetspeak_audio_codec_opus_jniopus_OpusEncoderWrapper
 * Method:    destroy
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_se_chalmers_fleetspeak_audio_codec_opus_jniopus_OpusEncoderWrapper_destroy
  (JNIEnv *env, jclass jc, jlong opusInstance)
{
opus_encoder_destroy((OpusEncoder *) (intptr_t)(opusInstance));
}

/*
 * Class:     se_chalmers_fleetspeak_audio_codec_opus_jniopus_OpusEncoderWrapper
 * Method:    ctl
 * Signature: (JI)I
 */
JNIEXPORT jint JNICALL Java_se_chalmers_fleetspeak_audio_codec_opus_jniopus_OpusEncoderWrapper_ctl
        (JNIEnv *env, jclass jc, jlong opusInstance, jint request)
{
  int error = opus_encoder_ctl((OpusEncoder *) (intptr_t)(opusInstance), OPUS_SET_BITRATE(request));
  return error;
}
