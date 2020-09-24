package com.demon.yu.avd;

public class WebVadHelper {
    private long webVadNative;
    public static final int MODEL_0 = 0;
    public static final int MODEL_1 = 1;
    public static final int MODEL_2 = 2;
    public static final int MODEL_3 = 3;
    public static final int ACTIVE_VOICE = 1;
    public static final int NO_ACTIVE_VOICE = 0;
    public static final int ERROR = -1;


    static {
        System.loadLibrary("webrtcavd");
    }

    public WebVadHelper() {
        webVadNative = nativeCreateWebVad();
        nativeInitWebVad(webVadNative);
    }

    public void setMode(int mode) {
        nativeSetModel(webVadNative, mode);
    }

    public int process(int hz, float[] audio_frame,int length) {
        return nativeProcess(webVadNative, hz, audio_frame,length);
    }

    public void release() {
        nativeRelease(webVadNative);
    }


    private native long nativeCreateWebVad();

    private native int nativeInitWebVad(long p);

    private native int nativeSetModel(long p, int mode);

    private native int nativeProcess(long p, int hz, float[] audio_frame, long frame_size);

    private native int nativeRelease(long p);

    private static native int nativeValidRateAndFrameLength(int rate, long frame_length);


}
