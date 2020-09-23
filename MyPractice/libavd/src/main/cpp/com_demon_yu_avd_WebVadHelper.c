//
// Created by yujintao on 2020/9/23.
//

#include "com_demon_yu_avd_WebVadHelper.h"
#include "include/webrtc_vad.h"
#include <jni.h>

JNIEXPORT void JNICALL Java_com_demon_yu_avd_WebVadHelper_testVad(JNIEnv *env , jclass jclass )
{
    VadInst* vad=WebRtcVad_Create();

    WebRtcVad_Free(vad);
}