package com.example.mypractice;

import android.app.Application;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.example.mypractice.common.Common;
import com.facebook.stetho.Stetho;

/**
 * Created by jintao on 2015/10/10.
 */
public class YUApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        final Resources resources = getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        Common.screenHeight = displayMetrics.heightPixels;
        Common.screenWidth = displayMetrics.widthPixels;
        Common.application = this;
        Stetho.initializeWithDefaults(this);
        Glide.init(this, new GlideBuilder());
    }
}
