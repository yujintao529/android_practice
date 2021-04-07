package com.demon.yu.camera;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.view.animation.PathInterpolatorCompat;

import com.demon.yu.utils.UIUtils;
import com.example.mypractice.R;


/**
 * Created by niuwei on 2017/11/10.
 */

public class ManualFocusView extends View {

    private static final int CIRCLE_NORMAL_RADIUS_DP = 32;
    private static final int CIRCLE_LARGE_RADIUS_DP = 41;
    private static final int CIRCLE_SMALL_DOT_RADIUS_DP = 4;

    private static final int STATUS_SHOW = 0;
    private static final int STATUS_HIDE = 1;

    private float mDownX;
    private float mDownY;
    private float mCircleRadius;
    private float mSmallCircleRadius;
    private float mCircleAlpha;
    private float mCircleBorderThick;
    private ValueAnimator mGradientAnimator;
    private ValueAnimator mConstantAnimator;
    private Paint mRedPaint;
    private Paint mRedPaintNonSolid;
    private IFocusTouchListener mListener;

    public ManualFocusView(Context context) {
        this(context, null);
    }

    public ManualFocusView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ManualFocusView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mRedPaint = new Paint();
        mRedPaint.setAntiAlias(true);
        mRedPaint.setColor(getResources().getColor(R.color.c_white));
        mRedPaint.setStyle(Paint.Style.STROKE);
        mRedPaint.setStrokeWidth(UIUtils.INSTANCE.dp2px(getContext(), 1f));

        mRedPaintNonSolid = new Paint();
        mRedPaintNonSolid.setAntiAlias(true);
        mRedPaintNonSolid.setColor(getResources().getColor(R.color.c_white));
        mRedPaintNonSolid.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawCircle(canvas);
    }

    private void startAnimation(final int status) {

        if (mGradientAnimator == null) {
            mGradientAnimator = ValueAnimator.ofFloat(0.f, 1.f);
            mGradientAnimator.setDuration(400);
            mGradientAnimator.setInterpolator(PathInterpolatorCompat.create(0.14f, 1, 0.34f, 1));
            mGradientAnimator.addUpdateListener(animation -> {
                float animatedValue = (Float) animation.getAnimatedValue();
                mSmallCircleRadius = animatedValue * CIRCLE_SMALL_DOT_RADIUS_DP;
                mCircleRadius = CIRCLE_LARGE_RADIUS_DP - (CIRCLE_LARGE_RADIUS_DP - CIRCLE_NORMAL_RADIUS_DP) * animatedValue;
                mCircleAlpha = 255 * animatedValue;
                mCircleBorderThick = 5 - 4 * animatedValue;
                invalidate();
            });
        }

        if (mConstantAnimator == null) {
            mConstantAnimator = ValueAnimator.ofFloat(CIRCLE_NORMAL_RADIUS_DP, CIRCLE_NORMAL_RADIUS_DP);
            mConstantAnimator.setDuration(500);
            mConstantAnimator.setStartDelay(400);
            mConstantAnimator.addUpdateListener(animation -> {
                mCircleRadius = (Float) animation.getAnimatedValue();
                invalidate();
            });
            mConstantAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    startAnimation(STATUS_HIDE);
                }
            });
        }
        stopAnimation();
        if (status == STATUS_SHOW) {
            mGradientAnimator.setDuration(400);
            mGradientAnimator.setFloatValues(0.f, 1.f);
            mGradientAnimator.start();
            mConstantAnimator.start();
        } else if (status == STATUS_HIDE) {
            mGradientAnimator.setDuration(320);
            mGradientAnimator.setFloatValues(1.f, 0.f);
            mGradientAnimator.start();
        }
    }

    private void stopAnimation() {
        if (mGradientAnimator != null) {
            mGradientAnimator.cancel();
        }
        if (mConstantAnimator != null) {
            mConstantAnimator.cancel();
        }
        mCircleRadius = 0;
        mCircleAlpha = 0;
        mSmallCircleRadius = 0;
        mCircleBorderThick = 0;
        invalidate();
    }

    private void drawCircle(Canvas canvas) {
        mRedPaint.setAlpha((int) mCircleAlpha);
        mRedPaint.setStrokeWidth(UIUtils.INSTANCE.dp2px(mCircleBorderThick));
        canvas.drawCircle(mDownX, mDownY, UIUtils.INSTANCE.dp2px(mCircleRadius), mRedPaint);
        canvas.drawCircle(mDownX, mDownY, UIUtils.INSTANCE.dp2px(mSmallCircleRadius), mRedPaintNonSolid);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (mListener == null) {
            return false;
        }
        if (mListener.canPerformTouchEvent(action)) {
            mDownX = event.getX();
            mDownY = event.getY();
            startAnimation(STATUS_SHOW);
            mListener.performTouchEvent(action, mDownX, mDownY);
        } else {
            mListener.interceptTouchEvent(action, mDownX, mDownY);
        }
//                break;
//        }
        return super.onTouchEvent(event);
    }

    public void setListener(IFocusTouchListener listener) {
        mListener = listener;
    }

    /**
     * NOTE：传入的motionEvent需要按照forceView本身的坐标进行转换。可参考ManualFocusCoordinateHelper
     *
     * @param event
     */
    public void singleTap(MotionEvent event) {
        mDownX = event.getX();
        mDownY = event.getY();
        startAnimation(STATUS_SHOW);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mListener = null;
    }

    public interface IFocusTouchListener {
        /**
         * 是否显示手动对焦动画
         *
         * @return true | false
         */
        boolean canPerformTouchEvent(int action);

        /**
         * 将点击事件外传，当美颜View显示的时候则隐藏
         * 当canPerformTouchEvent返回false的时候调用
         */
        void interceptTouchEvent(int action, float x, float y);

        /**
         * canPerformTouchEvent为true调用，显示对焦动画
         */
        void performTouchEvent(int action, float x, float y);
    }
}
