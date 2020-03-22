//
// Created by 郁金涛 on 2020-03-22.
//

#include <android/log.h>
#include "gaussian_logger.h"

#define LOG_I(...) ((void)__android_log_print(ANDROID_LOG_INFO, "gaussian:", __VA_ARGS__))
#define LOG_D(...) ((void)__android_log_print(AN, "gaussian:", __VA_ARGS__))
#define LOG_E(...) ((void)__android_log_print(ANDROID_LOG_INFO, "gaussian:", __VA_ARGS__))


void __x_debug_log(const char *tag, const char *fmt) {
    LOG_I("[%s] %s",tag,fmt);
}

void __x_info_log(const char *tag, const char *fmt) {
    LOG_I("[%s] %s",tag,fmt);
}

void __x_error_log(const char *tag, const char *fmt) {
    LOG_I("[%s] %s",tag,fmt);
}
