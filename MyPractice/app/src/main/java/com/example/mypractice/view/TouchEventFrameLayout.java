package com.example.mypractice.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.example.mypractice.Logger;

/**
 * Created by jintao on 2015/9/8.
 */
public class TouchEventFrameLayout extends FrameLayout {
    public static final String TAG="TouchEventImageView";

    public TouchEventFrameLayout(Context context) {
        super(context);
    }

    public TouchEventFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchEventFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                Logger.debug(TAG,"onTouchEvent ACTION_DOWN");
                break;
            case MotionEvent.ACTION_MOVE:
                Logger.debug(TAG,"onTouchEvent ACTION_MOVE");
                break;
            case MotionEvent.ACTION_UP:
                Logger.debug(TAG,"onTouchEvent ACTION_MOVE");
                break;
            case MotionEvent.ACTION_CANCEL:
                Logger.debug(TAG,"onTouchEvent ACTION_CANCEL");
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                Logger.debug(TAG,"onInterceptTouchEvent ACTION_DOWN");
                break;
            case MotionEvent.ACTION_MOVE:
                Logger.debug(TAG,"onInterceptTouchEvent ACTION_MOVE");
                break;
            case MotionEvent.ACTION_UP:
                Logger.debug(TAG,"onInterceptTouchEvent ACTION_MOVE");
                break;
            case MotionEvent.ACTION_CANCEL:
                Logger.debug(TAG,"onInterceptTouchEvent ACTION_CANCEL");
                break;
        }
        return super.onInterceptTouchEvent(event);
    }
}
