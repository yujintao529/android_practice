package com.demon.yu.avd;

public class WebVadHelper {
    private long webVadNative;
    public static final int MODEL_0 = 0;
    public static final int MODEL_1 = 1;
    public static final int MODEL_2 = 2;
    public static final int MODEL_3 = 3;


    static {
        System.loadLibrary("webrtcavd");
    }

    public WebVadHelper() {
        webVadNative = nativeCreateWebVad();
    }

    public void setMode(int mode) {
        nativeSetModel(webVadNative, mode);
    }

    public void process(int hz, float[] audio_frame) {
        nativeProcess(webVadNative, hz, audio_frame, audio_frame.length);
    }

    public void release() {
        nativeRelease(webVadNative);
    }


    public native long nativeCreateWebVad();

    public native int nativeInitWebVad(long p);

    public native int nativeSetModel(long p, int mode);

    public native int nativeProcess(long p, int hz, float[] audio_frame, long frame_size);

    public native int nativeRelease(long p);

    public static native int nativeValidRateAndFrameLength(int rate, long frame_length);


}
