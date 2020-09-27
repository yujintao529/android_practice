//
// Created by 郁金涛 on 2020/9/23.
//

#ifndef MYPRACTICE_STUDENT_H
#define MYPRACTICE_STUDENT_H

#include <string.h>
#include <jni.h>
#include <stdlib.h>

struct Student{
    int32_t age;
};
typedef struct Student Student;

Student* createStu(int32_t);
void setStuAge(Student*,jint);
void freeStu(Student*);

#endif //MYPRACTICE_STUDENT_H
