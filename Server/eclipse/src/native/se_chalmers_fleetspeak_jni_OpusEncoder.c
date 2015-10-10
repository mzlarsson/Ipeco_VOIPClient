#include <jni.h>
#include <stdint.h>
#include <opus.h>
#include <se_chalmers_fleetspeak_jni_OpusEncoder.h>

/*
 * Class:     se_chalmers_fleetspeak_jni_OpusEncoder
 * Method:    getSize
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_se_chalmers_fleetspeak_jni_OpusEncoder_getSize
  (JNIEnv *env, jclass jc, jint channels)
  {
    return opus_encoder_get_size(channels);
  }

/*
 * Class:     se_chalmers_fleetspeak_jni_OpusEncoder
 * Method:    create
 * Signature: (II)J
 */
JNIEXPORT jlong JNICALL Java_se_chalmers_fleetspeak_jni_OpusEncoder_create
  (JNIEnv *env, jclass jc, jint sampleRate, jint channels)
  {
    int error;
    OpusEncoder *opusEncoder = opus_encoder_create(sampleRate, channels, OPUS_APPLICATION_VOIP, &error);

    if(OPUS_OK != error)
      opusEncoder = 0;
    return (jlong) (intptr_t) (opusEncoder);
  }

/*
 * Class:     se_chalmers_fleetspeak_jni_OpusEncoder
 * Method:    encode
 * Signature: (J[BII[BII)I
 */
JNIEXPORT jint JNICALL Java_se_chalmers_fleetspeak_jni_OpusEncoder_encode
  (JNIEnv *env, jclass jc, jlong encoder, jbyteArray pcmInData, jint pcmInDataOffset, jint frameSize, jbyteArray opusOutData, jint opusOutDataOffset, jint outputLength)
  {
  jbyte *audioInData;
  jbyte *audioOutData;

  audioInData = (*env)->GetPrimitiveArrayCritical(env, pcmInData, 0);
  audioOutData = (*env)->GetPrimitiveArrayCritical(env, opusOutData, 0);

  int encodedBytes = opus_encode((OpusEncoder *)(intptr_t)(encoder),
                                   (opus_int16 *)(audioInData),
                                   frameSize,
                                   (unsigned char *) (audioOutData),
                                   outputLength);
    (*env)->ReleasePrimitiveArrayCritical(env, opusOutData, audioOutData, 0);
    (*env)->ReleasePrimitiveArrayCritical(env, pcmInData, audioInData, JNI_ABORT);

  return encodedBytes;
}

/*
 * Class:     se_chalmers_fleetspeak_jni_OpusEncoder
 * Method:    destroy
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_se_chalmers_fleetspeak_jni_OpusEncoder_destroy
  (JNIEnv *env, jclass jc, jlong opusInstance)
  {
opus_encoder_destroy((OpusEncoder *) (intptr_t)(opusInstance));
}

/*
 * Class:     se_chalmers_fleetspeak_jni_OpusEncoder
 * Method:    ctl
 * Signature: (JI)I
 */
JNIEXPORT jint JNICALL Java_se_chalmers_fleetspeak_jni_OpusEncoder_ctl
  (JNIEnv *env, jclass jc, jlong opusInstance, jint request)
  {
  int error = opus_encoder_ctl((OpusEncoder *) (intptr_t)(opusInstance), OPUS_SET_VBR_CONSTRAINT(request));
  return error;
}
