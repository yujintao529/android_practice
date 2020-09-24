//
// Created by yujintao on 2020/9/23.
//

#include "com_demon_yu_avd_WebVadHelper.h"
#include "include/webrtc_vad.h"
#include <jni.h>
#include <android/log.h>

/*
 * Class:     com_demon_yu_avd_WebVadHelper
 * Method:    nativeCreateWebVad
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_com_demon_yu_avd_WebVadHelper_nativeCreateWebVad
        (JNIEnv * env, jobject jobject)
{

    struct WebRtcVadInst* inst=WebRtcVad_Create();
    LOGD("webvadHelper nativeCreateWebVad %p",inst);
    return (jlong) inst;
}

/*
 * Class:     com_demon_yu_avd_WebVadHelper
 * Method:    nativeInitWebVad
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_com_demon_yu_avd_WebVadHelper_nativeInitWebVad
        (JNIEnv * env , jobject jobject, jlong p)
{
    struct WebRtcVadInst* inst = (struct WebRtcVadInst *) p;
    jint result=WebRtcVad_Init(inst);
    LOGD("webvadHelper nativeInitWebVad %d %p",result,inst);
    return result;
}

/*
 * Class:     com_demon_yu_avd_WebVadHelper
 * Method:    nativeSetModel
 * Signature: (JI)I
 */
JNIEXPORT jint JNICALL Java_com_demon_yu_avd_WebVadHelper_nativeSetModel
        (JNIEnv * env , jobject jobject, jlong p , jint mode)
{
    struct WebRtcVadInst* inst = (struct WebRtcVadInst *) p;
    jint result=WebRtcVad_set_mode(inst, mode);
    LOGD("webvadHelper nativeSetModel %d %p",result,inst);
    return result;
}

/*
 * Class:     com_demon_yu_avd_WebVadHelper
 * Method:    nativeProcess
 * Signature: (JI[FJ)I
 */
JNIEXPORT jint JNICALL Java_com_demon_yu_avd_WebVadHelper_nativeProcess
        (JNIEnv * env, jobject jobject , jlong p , jint hz , jfloatArray data, jlong length)
{
    struct WebRtcVadInst* inst = (struct WebRtcVadInst *) p;
    LOGD("webvadHelper nativeProcess %p %d %p %llu",inst,hz,data,length);
    int result=WebRtcVad_Process(inst,hz,data,length);
    return result;
}



/*
 * Class:     com_demon_yu_avd_WebVadHelper
 * Method:    nativeRelease
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_com_demon_yu_avd_WebVadHelper_nativeRelease
        (JNIEnv * env , jobject jobject, jlong p)
{
    struct WebRtcVadInst* inst = (struct WebRtcVadInst *) p;
    WebRtcVad_Free(inst);
    return 0;
}



/*
 * Class:     com_demon_yu_avd_WebVadHelper
 * Method:    nativeValidRateAndFrameLength
 * Signature: (IJ)I
 */
JNIEXPORT jint JNICALL Java_com_demon_yu_avd_WebVadHelper_nativeValidRateAndFrameLength
        (JNIEnv * env, jclass jclass, jint hz, jlong length)
{

}
