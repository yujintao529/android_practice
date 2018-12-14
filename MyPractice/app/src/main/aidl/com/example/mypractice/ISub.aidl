// ISub.aidl
package com.example.mypractice;

// Declare any non-default types here with import statements

interface ISub {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);

    int sub(int a,int b);
}
