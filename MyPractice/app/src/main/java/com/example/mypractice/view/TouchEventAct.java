package com.example.mypractice.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ViewGroup;

import com.example.mypractice.Logger;
import com.example.mypractice.R;


/**
 * 核心思想
 * intercept是拦截，也就是拦截本层的事件传递
 * touch是消费
 * 一旦事件消费，那么后续事件是不会继续传到本层的intercept，但是父层是可以接收到的，除非调用requestDisallowInterceptTouchEvent().这样会直接传递到本层的touch
 * 拦截的作用仅是本层的拦截，防止子view得到事件，想当于把自己变成u型结构的最后一层。并不代表他会消费事件
 * 分析各种组合的情况 。 down--move一段--up 过程
 * p=parent,c=child onInterceptTouchEvent=intercept onTouchEvent=touch
 * 1. 所有的donw事件都返回false的话，那么不会接收到move和up等时间。如果touch都返回false的话也不会在继续接收和intercept没有关系
 * 2. p intercept和touch 都返回false，c intercept 返回false，touch返回true的话，那么p的intercept仍然会接收到move和up事件。
 * 3. p intercept和touch 都返回true后，那么无论c返回什么，后续事件只会发送到p 的touch里。如果p的touch返回的false，那么后续事件就不会继续接收了，p的intercept也接收不到了
 *
 */
public class TouchEventAct extends Activity {
    public static final String TAG = "TouchEventAct";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touchevent);
        TouchEventFrameLayout parent = (TouchEventFrameLayout) findViewById(R.id.parent);
        final TouchEventFrameLayout child = (TouchEventFrameLayout) findViewById(R.id.child);
        child.setiEvent(new TouchEventFrameLayout.IEvent() {
            float lastY;
            float initY;

            @Override
            public boolean onInterceptTouchEvent(MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastY = initY = event.getY();
                        Logger.debug(TAG, "child onInterceptTouchEvent ACTION_DOWN");
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float diffY = event.getY() - initY;
//                        Logger.debug(TAG, "child onInterceptTouchEvent ACTION_MOVE " + diffY);
//                        if (diffY > 20) {
//                            return true;
//                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        Logger.debug(TAG, "child onInterceptTouchEvent ACTION_UP");
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        Logger.debug(TAG, "child onInterceptTouchEvent ACTION_CANCEL");
                        break;
                }
                return false;
            }

            @Override
            public boolean onTouchEvent(MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Logger.debug(TAG, "child onTouchEvent ACTION_DOWN");
                        break;
                    case MotionEvent.ACTION_MOVE:
                        Logger.debug(TAG, "child onTouchEvent ACTION_MOVE");
                        break;
                    case MotionEvent.ACTION_UP:
                        Logger.debug(TAG, "child onTouchEvent ACTION_UP");
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        Logger.debug(TAG, "child onTouchEvent ACTION_CANCEL");
                        break;
                }
                return true;
            }
        });
        parent.setiEvent(new TouchEventFrameLayout.IEvent() {
            float lastY;
            float initY;

            @Override
            public boolean onInterceptTouchEvent(MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastY = initY = event.getY();
                        Logger.debug(TAG, "parent onInterceptTouchEvent ACTION_DOWN");
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float diffY = event.getY() - initY;
                        Logger.debug(TAG, "parent onInterceptTouchEvent ACTION_MOVE " + diffY);
//                        if (diffY > 20) {
//                            return true;
//                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        Logger.debug(TAG, "parent onInterceptTouchEvent ACTION_UP");
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        Logger.debug(TAG, "parent onInterceptTouchEvent ACTION_CANCEL");
                        break;
                }
                return false;
            }

            @Override
            public boolean onTouchEvent(MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Logger.debug(TAG, "parent onTouchEvent ACTION_DOWN");
                        break;
                    case MotionEvent.ACTION_MOVE:
                        Logger.debug(TAG, "parent onTouchEvent ACTION_MOVE");
                        break;
                    case MotionEvent.ACTION_UP:
                        Logger.debug(TAG, "parent onTouchEvent ACTION_UP");
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        Logger.debug(TAG, "parent onTouchEvent ACTION_CANCEL");
                        break;
                }
                return false;
            }
        });

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        return super.dispatchTouchEvent(ev);
    }
}
