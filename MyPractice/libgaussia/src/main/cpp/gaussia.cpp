//
// Created by 郁金涛 on 2020-02-19.
//

#include "com_demon_yu_lib_gausiia_Guassia.h"
#include "string"
#include "stdio.h"
#include <android/log.h>

#define TAG "gaussia"

extern "C" JNIEXPORT void JNICALL Java_com_demon_yu_lib_gausiia_Guassia_guassia
        (JNIEnv *, jclass) {
    std::string hello = "sdf";
    __android_log_print(ANDROID_LOG_DEBUG, TAG, "test");
}