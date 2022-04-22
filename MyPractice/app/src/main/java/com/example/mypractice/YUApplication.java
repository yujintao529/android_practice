package com.example.mypractice;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import androidx.annotation.NonNull;

import com.demon.yu.utils.ProcessUtils;
import com.demon.yu.utils.ThreadPoolUtils;
import com.example.mypractice.common.Common;
import com.facebook.drawee.backends.pipeline.Fresco;
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
        Fresco.initialize(this);
        Logger.debug("YUApplication", "onCreate process  " + ProcessUtils.getProcessInfo(this));
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Logger.debug("YUApplication", "onConfigurationChanged " + newConfig);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        ThreadPoolUtils.init();

        Logger.debug("YUApplication", "attachBaseContext " + base);
    }
}
