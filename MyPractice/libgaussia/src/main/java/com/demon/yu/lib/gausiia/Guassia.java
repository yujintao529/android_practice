package com.demon.yu.lib.gausiia;

/**
 * created by yujintao 2020-02-19
 */
public class Guassia {
    static {
        System.loadLibrary("gaussia-lib");
    }

    public static native void guassia();

    public static void test() {
        guassia();
    }
}
