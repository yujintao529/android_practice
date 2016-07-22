package com.example.mypractice.resource.drawable;

import android.R;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;

import com.example.mypractice.Logger;
import com.example.mypractice.Utils;

import java.util.Arrays;

/**
 * **********************************************************************************
 * Module Name: LampCord</br>
 * File Name: <b>LampCord.java</b></br>
 * Description: TODO</br>
 * Author: 郁金涛</br>
 * 版权 2008-2015，金华长风信息技术有限公司</br>
 * 所有版权保护
 * 这是金华长风信息技术有限公司未公开的私有源代码, 本文件及相关内容未经金华长风信息技术有限公司
 * 事先书面同意，不允许向任何第三方透露，泄密部分或全部; 也不允许任何形式的私自备份。
 * *************************************************************************************
 */
public class LampCord extends Drawable implements Animatable {

    private static final int ACTION_DURATION = 500;//
    private static final long ACTION_DIFF = 1000 / 60;
    private static final int CLOSE_VALUE_DP=50;


    private boolean isRunning = false;
    private Context mContext;
    private long mStartTime;
    private float mAnimProgress;
    private Rect mBounds;
    private RectF mBoundsF;
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG);
    private boolean mAction;
    private Bitmap bitmap;
    private Matrix mMatrix;
    private Matrix mSaveMatrix;
    private TimeInterpolator mClosedInterpolator;
    private TimeInterpolator mOpenInterpolator;

    private int mBitmapheight;
    private int mChangeValue;

    public LampCord(Context context) {
        this.mContext = context;
        mClosedInterpolator=new AnticipateInterpolator(2);
        mOpenInterpolator=new OvershootInterpolator(2);
        bitmap= BitmapFactory.decodeResource(context.getResources(), com.example.mypractice.R.drawable.gg_activity_webview_lampcord);
        mMatrix=new Matrix();
        mSaveMatrix=new Matrix();
        mAction=false;
        mPaint.setColor(Color.RED);
        mChangeValue=0;

    }
    private Runnable mUpdate = new Runnable() {

        @Override
        public void run() {

            final long time = SystemClock.uptimeMillis();
            mAnimProgress = Math.min((time - mStartTime)*1f / ACTION_DURATION, 1);
            onPrepared(mAction,mAnimProgress);
            if (mAnimProgress == 1) {
                stop();
                return;
            }
            invalidateSelf();
            scheduleSelf(this, time + ACTION_DIFF);
        }
    };

    public void onPrepared(boolean action,float progress){
        float value =0.0f;
        if(action){
            value = mOpenInterpolator.getInterpolation(progress)*CLOSE_VALUE_DP;
        }else{
            value = mClosedInterpolator.getInterpolation(progress)*-CLOSE_VALUE_DP;
        }
        mMatrix.setTranslate(0, value);
        mMatrix.setConcat(mSaveMatrix,mMatrix);
    }

    @Override
    public int getIntrinsicHeight() {
        return bitmap.getHeight()+200;
    }

    @Override
    public int getIntrinsicWidth() {
        return bitmap.getWidth();
    }

    @Override
    protected boolean onStateChange(int[] state) {
        Logger.d(Arrays.toString(Utils.toReadDrawableState(state)));
        if (!DrawableUtil.hasState(state, R.attr.state_pressed)||isRunning()) {
            return false;
        }
        start();
        return true;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        resetBounds();
    }

    private void resetBounds() {
        mBounds = getBounds();
        mBoundsF=new RectF(mBounds);
    }

    @Override
    public boolean isStateful() {
        return true;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawBitmap(bitmap,mMatrix,mPaint);
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter cf) {

    }

    @Override
    public int getOpacity() {
        return 0;
    }

    @Override
    public void scheduleSelf(Runnable what, long when) {
        super.scheduleSelf(what, when);
    }

    @Override
    public void start() {
        isRunning = true;
        mStartTime = SystemClock.uptimeMillis();
        scheduleSelf(mUpdate, mStartTime + ACTION_DIFF);
        invalidateSelf();

    }

    @Override
    public void stop() {
        isRunning = false;
        mStartTime = 0;
        unscheduleSelf(mUpdate);
        invalidateSelf();
        mAction=!mAction;
        mSaveMatrix.set(mMatrix);
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }


}
