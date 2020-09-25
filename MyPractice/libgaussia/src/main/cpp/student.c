//
// Created by 郁金涛 on 2020/9/23.
//

#include "student.h"
#include <jni.h>

Student* create(int32_t age)
{
    Student* stu=malloc(sizeof(Student));
    stu->age=age;
    return stu;
}

void setAge(Student* student,jint age)
{
    student->age=age;
}
void free(Student* stu)
{
    free(stu);
}
