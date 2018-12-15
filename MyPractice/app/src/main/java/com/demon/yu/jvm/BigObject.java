package com.demon.yu.jvm;

import android.graphics.Bitmap;

public class BigObject {

//    long[] longs = new long[100];

    private Bitmap bitmap;

    public BigObject() {
        bitmap=Bitmap.createBitmap(200,200, Bitmap.Config.ARGB_8888);
    }
}
