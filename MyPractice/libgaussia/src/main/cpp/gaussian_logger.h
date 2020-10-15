//
// Created by 郁金涛 on 2020-02-23.
//

#include <jni.h>

#ifndef MYPRACTICE_GAUSSIAN_LOGGER_H
#define MYPRACTICE_GAUSSIAN_LOGGER_H


#define LOG_I(...) ((void)__android_log_print(ANDROID_LOG_INFO, "gaussian:", __VA_ARGS__))
#define LOG_D(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, "gaussian:", __VA_ARGS__))
#define LOG_E(...) ((void)__android_log_print(ANDROID_LOG_ERROR, "gaussian:", __VA_ARGS__))


extern void __x_log(const char* tag, const char* fmt);

extern void __x_debug_log(const char* tag, const char* fmt);
extern void __x_info_log(const char* tag, const char* fmt);
extern void __x_error_log(const char* tag, const char* fmt);

#endif //MYPRACTICE_GAUSSIAN_LOGGER_H