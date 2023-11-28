
#include "com_demon_yu_lib_gaussian_StudentNative.h"
#include "jni.h"
#include "student.h"
#include "memory.h"
/*
 * Class:     com_demon_yu_lib_gaussian_StudentNative
 * Method:    setStudentAge
 * Signature: (JI)V
 */
JNIEXPORT void JNICALL Java_com_demon_yu_lib_gaussian_StudentNative_setStudentAge
        (JNIEnv *env, jobject jobject, jlong jlong, jint jint) {

    Student *student = (Student *) jlong;
    setStuAge(student, jint);
}

/*
 * Class:     com_demon_yu_lib_gaussian_StudentNative
 * Method:    releaseStudent
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_demon_yu_lib_gaussian_StudentNative_releaseStudent
        (JNIEnv *env, jobject jobject, jlong jlong) {
    freeStu((Student *) jlong);
}

/*
 * Class:     com_demon_yu_lib_gaussian_StudentNative
 * Method:    createStudent
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_com_demon_yu_lib_gaussian_StudentNative_createStudent
        (JNIEnv *env, jobject jobject) {
    Student *stu = createStu(0);
    return (long) stu;
}

JNIEXPORT jlong JNICALL Java_com_demon_yu_lib_gaussian_StudentNative_mallocMemory
        (JNIEnv *env, jclass jclazz, jint size) {
    memory *memoryArr = malloc(size * sizeof(memory));
    return (jlong) memoryArr;
}

JNIEXPORT void JNICALL Java_com_bytedance_Test_crash
        (JNIEnv *env, jclass class) {
    char * str= "yujintao";
    str[0] = 2;
}