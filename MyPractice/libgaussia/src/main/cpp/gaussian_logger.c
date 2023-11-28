//
// Created by 郁金涛 on 2020-03-22.
//

#include <android/log.h>
#include "gaussian_logger.h"

void __x_debug_log(const char *tag, const char *fmt) {
    LOG_D("[%s] %s", tag, fmt);
}

void __x_info_log(const char *tag, const char *fmt) {
    LOG_I("[%s] %s", tag, fmt);
}

void __x_error_log(const char *tag, const char *fmt) {
    LOG_E("[%s] %s", tag, fmt);
}
