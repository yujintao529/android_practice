package com.example.mypractice.source;

import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.example.mypractice.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 说明：
 *     1. 通过日志可以看出，传递给子view'的event包含的getPointerIdBits和父view的touchtarget里的pointerIdBits是一致的
 *        这说明了，viewGroup事件的拆分机制
 *     2. motionEvent不仅包含产生的事件信息，也包含当前view处理过的事件信息。
 */
public class LogFrameLayout extends FrameLayout {
    private String name;

    private MotionEvent lastMotionEvent;

    public LogFrameLayout(@NonNull Context context) {
        super(context);
    }

    public LogFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        boolean result = super.dispatchTouchEvent(ev);

        final int actionMasked = ev.getActionMasked();
        if (actionMasked != MotionEvent.ACTION_MOVE || !lastIsMoveEvent()) {
            LogEvent logEvent = new LogEvent(this, ev);
            Logger.debug("LogFrameLayout", logEvent.log());
        }
        lastMotionEvent = copyFrom(ev);
        return result;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    public boolean lastIsMoveEvent() {
        return lastMotionEvent != null && lastMotionEvent.getActionMasked() == MotionEvent.ACTION_MOVE;
    }

    public MotionEvent copyFrom(MotionEvent motionEvent) {
        return MotionEvent.obtainNoHistory(motionEvent);
    }

    @Override
    public String toString() {
        return "LogFrameLayout{" +
                "name='" + name + '\'' +
                '}';
    }

    public String touchTargetsDesc() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(toString()).append(":[");
        try {
            Field filed = getClass().getSuperclass().getSuperclass().getDeclaredField("mFirstTouchTarget");
            filed.setAccessible(true);
            Object value = filed.get(this);//TouchTarget

            while (value != null) {
                value = touchTargetDesc(value, stringBuilder);
            }
        } catch (Exception e) {
            //ignore
            Log.e("touchTargetsDesc","fail ",e);
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    public Object touchTargetDesc(Object touchTarget, StringBuilder stringBuilder) {
        try {
            stringBuilder.append("(");
            Field field = touchTarget.getClass().getDeclaredField("child");
            field.setAccessible(true);
            View view = (View) field.get(touchTarget);
            stringBuilder.append("").append(view.toString()).append(",");
            Field intIDS = touchTarget.getClass().getDeclaredField("pointerIdBits");
            intIDS.setAccessible(true);
            stringBuilder.append(Integer.toBinaryString((Integer) intIDS.get(touchTarget)));
            stringBuilder.append(")");
            Field touchTargetField = touchTarget.getClass().getDeclaredField("next");
            touchTarget = touchTargetField.get(touchTarget);
        } catch (Exception e) {
            Log.e("touchTargetDesc","fail ",e);
        }
        return touchTarget;
    }


    public static class LogEvent {
        public LogFrameLayout logFrameLayout;
        public MotionEvent motionEvent;
        private MotionEvent.PointerProperties outProperties = new MotionEvent.PointerProperties();

        public LogEvent(LogFrameLayout logFrameLayout, MotionEvent motionEvent) {
            this.logFrameLayout = logFrameLayout;
            this.motionEvent = motionEvent;
        }

        public String log() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(logFrameLayout.name).append("->");
            int pointerIndex = motionEvent.getActionIndex();
            motionEvent.getPointerProperties(pointerIndex, outProperties);
            final int actionMasked = motionEvent.getActionMasked();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                stringBuilder.append(MotionEvent.actionToString(actionMasked));
            } else {
                stringBuilder.append(actionMasked);
            }
            stringBuilder.append(" pointerIndex:" + pointerIndex);
            stringBuilder.append(" getPointerCount:").append(motionEvent.getPointerCount()).append(" (");
            stringBuilder.append(getIDS(motionEvent));
            stringBuilder.append(")");
            stringBuilder.append("touchTargets.");
            stringBuilder.append(logFrameLayout.touchTargetsDesc());
            return stringBuilder.toString();
        }


        public String getIDS(MotionEvent motionEvent) {
            try {
                Method method = MotionEvent.class.getDeclaredMethod("getPointerIdBits");
                method.setAccessible(true);
                return Integer.toBinaryString((int) method.invoke(motionEvent));
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            return "";
        }

    }
}

