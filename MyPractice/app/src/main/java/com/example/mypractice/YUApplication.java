package com.example.mypractice;

import android.content.res.Resources;
import android.support.multidex.MultiDexApplication;
import android.util.DisplayMetrics;

import com.example.mypractice.common.Common;
import com.squareup.leakcanary.LeakCanary;

/**
 * Created by jintao on 2015/10/10.
 */
public class YUApplication extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        final Resources resources = getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        Common.screenHeight = displayMetrics.heightPixels;
        Common.screenWidth = displayMetrics.widthPixels;
    }
}
