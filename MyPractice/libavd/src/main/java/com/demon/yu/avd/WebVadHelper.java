package com.demon.yu.avd;

public class WebVadHelper {
    static {
        System.loadLibrary("webrtcavd");
    }
    public static native void testVad();
}
