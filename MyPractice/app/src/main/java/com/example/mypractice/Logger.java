package com.example.mypractice;

import android.util.Log;

/**
 * Created by jintao on 2015/8/31.
 */
public class Logger {
    public static final String TAG = "MyPractice";

    public static void d(String message) {
        Log.d(TAG, message);
    }

    public static void d(String formate, Object... args) {
        d(String.format(formate, args));
    }

    public static void debug(String tag, String message) {
        Log.d(tag, message);
    }

    public static void debug(String tag, String formate, Object... args) {
        debug(tag, String.format(formate, args));
    }

    public static void error(String tag, String message, Throwable throwable) {
        Log.e(tag, message, throwable);
    }
}
