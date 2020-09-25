//
// Created by 郁金涛 on 2020/9/23.
//

#ifndef MYPRACTICE_STUDENT_H
#define MYPRACTICE_STUDENT_H

#include <string.h>
#include <jni.h>

struct Student{
    int32_t age;

};
typedef struct Student Student;

Student* create(int32_t);
void setAge(Student*,jint);
void free(Student*);

#endif //MYPRACTICE_STUDENT_H
