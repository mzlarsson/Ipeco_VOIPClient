#include <jni.h>
#include <stdint.h>
#include <opus.h>
#include <se_chalmers_fleetspeak_audio_codec_opus_jniopus_OpusDecoderWrapper.h>

/*
 * Class:     se_chalmers_fleetspeak_audio_codec_opus_jniopus_OpusDecoderWrapper
 * Method:    getSize
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_se_chalmers_fleetspeak_audio_codec_opus_jniopus_OpusDecoderWrapper_getSize
  (JNIEnv *env, jclass jc, jint channels)
{
  return opus_decoder_get_size(channels);
}

/*
 * Class:     se_chalmers_fleetspeak_audio_codec_opus_jniopus_OpusDecoderWrapper
 * Method:    create
 * Signature: (II)J
 */
JNIEXPORT jlong JNICALL Java_se_chalmers_fleetspeak_audio_codec_opus_jniopus_OpusDecoderWrapper_create
  (JNIEnv *env, jclass jc, jint sampleRate, jint channels)
{
  int error;
  OpusDecoder *opusDecoder = opus_decoder_create(sampleRate, channels, &error);
  if(error < 0){
    opusDecoder = 0;
  }
  return (jlong) (intptr_t) (opusDecoder);
}

/*
 * Class:     se_chalmers_fleetspeak_audio_codec_opus_jniopus_OpusDecoderWrapper
 * Method:    decode
 * Signature: (J[BII[BIII)I
 */
JNIEXPORT jint JNICALL Java_se_chalmers_fleetspeak_audio_codec_opus_jniopus_OpusDecoderWrapper_decode
  (JNIEnv *env, jclass jc, jlong opusInstance, jbyteArray opusInData, jint opusInDataOffset, jint inputLength, jbyteArray pcmOutData, jint pcmOutDataOffset, jint frameSize, jint fec)
{

  jbyte *encodedData = (*env)->GetPrimitiveArrayCritical(env, opusInData, 0);

  jbyte *decodedData = (*env)->GetPrimitiveArrayCritical(env, pcmOutData, 0);

  int status = opus_decode(
          (OpusDecoder *) (intptr_t)(opusInstance),
          (unsigned char *) ((encodedData ? (encodedData+ opusInDataOffset) : NULL)),
          inputLength,
          (unsigned char *) (decodedData+pcmOutDataOffset),
          frameSize,
          fec);
  (*env)->ReleasePrimitiveArrayCritical(env, pcmOutData, decodedData, 0);

  (*env)->ReleasePrimitiveArrayCritical(env, opusInData, encodedData,0);


  return status;
}

/*
 * Class:     se_chalmers_fleetspeak_audio_codec_opus_jniopus_OpusDecoderWrapper
 * Method:    getPacketFrames
 * Signature: ([BII)I
 */
JNIEXPORT jint JNICALL Java_se_chalmers_fleetspeak_audio_codec_opus_jniopus_OpusDecoderWrapper_getPacketFrames
        (JNIEnv *env, jclass jc, jbyteArray opusInData, jint offset, jint length){
    int status;
    if(opusInData){
        jbyte *encodedData = (*env)->GetPrimitiveArrayCritical(env, opusInData, NULL);
        if(encodedData){
            status = opus_packet_get_nb_frames( (unsigned char *) (encodedData + offset), (opus_int32) length);
            (*env)->ReleasePrimitiveArrayCritical(env, opusInData, encodedData, JNI_ABORT);
        }

    }
    return status;
}

/*
 * Class:     se_chalmers_fleetspeak_audio_codec_opus_jniopus_OpusDecoderWrapper
 * Method:    destroy
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_se_chalmers_fleetspeak_audio_codec_opus_jniopus_OpusDecoderWrapper_destroy
  (JNIEnv *env, jclass jc, jlong opusInstance)
{
    opus_decoder_destroy((OpusDecoder *) (intptr_t)(opusInstance));
}
