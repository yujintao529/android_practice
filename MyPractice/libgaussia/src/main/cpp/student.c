//
// Created by 郁金涛 on 2020/9/23.
//

#include "student.h"
#include <jni.h>
#include <stdlib.h>

Student* createStu(int32_t age)
{
    Student* stu=malloc(sizeof(Student));
    stu->age=age;
    return stu;
}

void setStuAge(Student* student,jint age)
{
    student->age=age;
}
void freeStu(Student* stu)
{
    free(stu);
}
