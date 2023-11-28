//
// Created by yujintao on 2023/3/23.
//

#ifndef MYPRACTICE_MEMORY_H
#define MYPRACTICE_MEMORY_H

#endif //MYPRACTICE_MEMORY_H

#include <string.h>
#include <jni.h>
#include <stdlib.h>
struct memory{
    int32_t arr[256 * 1024] ;
};
typedef struct memory memory;