package com.bytedance;

/**
 * @author yujinta.529
 * @description
 * @create 2023-03-29
 */
public class Test {
    static {
        System.loadLibrary("gaussia-lib");
    }
    public static native void crash();
}
