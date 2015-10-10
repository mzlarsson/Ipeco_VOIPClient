
#include <jni.h>
/* Header for class se_chalmers_fleetspeak_jni_OpusDecoder */

#ifndef _Included_se_chalmers_fleetspeak_jni_OpusDecoder
#define _Included_se_chalmers_fleetspeak_jni_OpusDecoder
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     se_chalmers_fleetspeak_jni_OpusDecoder
 * Method:    getSize
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_se_chalmers_fleetspeak_jni_OpusDecoder_getSize
  (JNIEnv *, jclass, jint);

/*
 * Class:     se_chalmers_fleetspeak_jni_OpusDecoder
 * Method:    create
 * Signature: (II)J
 */
JNIEXPORT jlong JNICALL Java_se_chalmers_fleetspeak_jni_OpusDecoder_create
  (JNIEnv *, jclass, jint, jint);

/*
 * Class:     se_chalmers_fleetspeak_jni_OpusDecoder
 * Method:    getPacketFrames
 * Signature: ([BII)I
 */
JNIEXPORT jint JNICALL Java_se_chalmers_fleetspeak_jni_OpusDecoder_getPacketFrames
  (JNIEnv *, jclass, jbyteArray, jint, jint);

/*
 * Class:     se_chalmers_fleetspeak_jni_OpusDecoder
 * Method:    decode
 * Signature: (J[BII[BIII)I
 */
JNIEXPORT jint JNICALL Java_se_chalmers_fleetspeak_jni_OpusDecoder_decode
  (JNIEnv *, jclass, jlong, jbyteArray, jint, jint, jbyteArray, jint, jint, jint);

/*
 * Class:     se_chalmers_fleetspeak_jni_OpusDecoder
 * Method:    destroy
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_se_chalmers_fleetspeak_jni_OpusDecoder_destroy
  (JNIEnv *, jclass, jlong);

#ifdef __cplusplus
}
#endif
#endif
