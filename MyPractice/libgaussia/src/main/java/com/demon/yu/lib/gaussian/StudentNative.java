package com.demon.yu.lib.gaussian;


public class StudentNative {
    static {
        System.loadLibrary("gaussia-lib");
    }

    private long studentNative = -1;

    public StudentNative() {
        studentNative = createStudent();
    }

    public void setAge(int age) {
        if (studentNative != -1) {
            setStudentAge(studentNative, age);
        }
    }

    public void release() {
        releaseStudent(studentNative);
    }

    public native void setStudentAge(long studentNative, int age);

    public native void releaseStudent(long studentNative);

    public native long createStudent();


    public native static long mallocMemory(int size);

}
