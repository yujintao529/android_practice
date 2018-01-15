package com.example.mypractice.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;

import com.example.mypractice.Logger;
import com.example.mypractice.R;

public class TouchEventAct extends Activity {
    public static final String TAG = "TouchEventAct";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touchevent);
        TouchEventFrameLayout parent = (TouchEventFrameLayout) findViewById(R.id.parent);
        TouchEventFrameLayout child = (TouchEventFrameLayout) findViewById(R.id.child);
//        child.setiEvent(new TouchEventFrameLayout.IEvent() {
//            float lastY;
//            float initY;
//
//            @Override
//            public boolean onInterceptTouchEvent(MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        lastY = initY = event.getY();
//                        Logger.debug(TAG, "parent onInterceptTouchEvent ACTION_DOWN");
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//                        float diffY = event.getY() - initY;
//                        Logger.debug(TAG, "parent onInterceptTouchEvent ACTION_MOVE");
//                        if (diffY > 20) {
//                            return true;
//                        }
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        Logger.debug(TAG, "parent onInterceptTouchEvent ACTION_UP");
//                        break;
//                    case MotionEvent.ACTION_CANCEL:
//                        Logger.debug(TAG, "parent onInterceptTouchEvent ACTION_CANCEL");
//                        break;
//                }
//                return false;
//            }
//
//            @Override
//            public boolean onTouchEvent(MotionEvent motionEvent) {
//                switch (motionEvent.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        Logger.debug(TAG, "child onTouchEvent ACTION_DOWN");
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//                        Logger.debug(TAG, "child onTouchEvent ACTION_MOVE");
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        Logger.debug(TAG, "child onTouchEvent ACTION_UP");
//                        break;
//                    case MotionEvent.ACTION_CANCEL:
//                        Logger.debug(TAG, "child onTouchEvent ACTION_CANCEL");
//                        break;
//                }
//                return false;
//            }
//        });
//        parent.setiEvent(new TouchEventFrameLayout.IEvent() {
//            @Override
//            public boolean onInterceptTouchEvent(MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        Logger.debug(TAG, "parent onInterceptTouchEvent ACTION_DOWN");
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//                        Logger.debug(TAG, "parent onInterceptTouchEvent ACTION_MOVE");
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        Logger.debug(TAG, "parent onInterceptTouchEvent ACTION_UP");
//                        break;
//                    case MotionEvent.ACTION_CANCEL:
//                        Logger.debug(TAG, "parent onInterceptTouchEvent ACTION_CANCEL");
//                        break;
//                }
//                return false;
//            }
//
//            @Override
//            public boolean onTouchEvent(MotionEvent motionEvent) {
//                switch (motionEvent.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        Logger.debug(TAG, "parent onTouchEvent ACTION_DOWN");
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//                        Logger.debug(TAG, "parent onTouchEvent ACTION_MOVE");
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        Logger.debug(TAG, "parent onTouchEvent ACTION_UP");
//                        break;
//                    case MotionEvent.ACTION_CANCEL:
//                        Logger.debug(TAG, "parent onTouchEvent ACTION_CANCEL");
//                        break;
//                }
//                return false;
//            }
//        });

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        return super.dispatchTouchEvent(ev);
    }
}
