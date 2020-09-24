package com.demon.yu.lib.gaussian;

import android.graphics.Bitmap;

/**
 * created by yujintao 2020-02-21
 */
public class GaussianHelper {
    static {
        System.loadLibrary("gaussia-lib");
    }

    public static native void blur(Bitmap bitmap);
    public static native long createStudent();
}
