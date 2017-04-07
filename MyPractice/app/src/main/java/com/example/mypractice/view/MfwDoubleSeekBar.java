package com.example.mypractice.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Xfermode;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


import com.example.mypractice.R;
import com.example.mypractice.view.MfwCommon;
import com.example.mypractice.view.MfwLog;

import java.util.ArrayList;

/**
 * Created by yujintao on 2017/4/5.
 */

public class MfwDoubleSeekBar extends View {
    private static final String TAG = MfwDoubleSeekBar.class.getSimpleName();

    //doubleSeekBar模式，两种
    public static final int MODE_SINGLE = 0;
    public static final int MODE_MUL = 1;
    //拖拽状态
    private static final int STATUE_DILE = 0;
    private static final int STATUE_LEFT_DRAG = 1;
    private static final int STATUE_RIGHT_DRAG = 2;
    private static final int STATUE_LEFT_SCROLL = 3;
    private static final int sSTATUE_RIGHT_SCROLL = 3;

    //val
    private int lineHeight;//线的高度
    private int lineOutColor;
    private int lineInColor;
    private int dotInColor;
    private int dotOutColor;
    private int numPart;
    private int dotRadius;
    private Xfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER);
    //value
    private int min = 0;//最小值
    private int max = 1000;//最大值
    private int currentStart = 20;//当前左边的值
    private int currentEnd = 300;//当前右边的值
    private Rect lineRect = new Rect();//横线的rect
    private Rect lineInRect = new Rect();//选择区域的rect
    Point mCurrentStartP;//左边的值的point
    Point mCurrentEndP;//右边值的point
    Point mMinP;//右边值的point
    Point mMaxP;//右边值的point
    private Paint linePaint;
    private Paint textPaint;
    private int status = STATUE_DILE;//当前拖拽的状态

    private ValueAnimator leftAnimator;//左边动态滑动的animator
    private ValueAnimator rightAnimator;//右边动态滑动的animator

    private BitmapDrawable barThumb;//仅支持图片BitmapDrawable;

    private OnScrollChangeListener onScrollChangeListener;

    private int mode = MODE_MUL;


    private boolean autoNest = true;//牛逼的属性，自动NumPart位置调整。

    private LeftAnimatorUpdateListener leftAnimatorUpdateListener = new LeftAnimatorUpdateListener();
    private RightAnimatorUpdateListener rightAnimatorUpdateListener = new RightAnimatorUpdateListener();


    private ArrayList<Point> points = new ArrayList<>();
    private ArrayList<String> content = new ArrayList<>();
    private ArrayList<Point> contentPoint = new ArrayList<>();


    private OnDotTextAdapter onDotTextAdapter;

    public MfwDoubleSeekBar(Context context) {
        this(context, null);
    }

    public MfwDoubleSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        if (isInEditMode()) {
            return;
        }
        initAttrs(context, attrs);
    }


    /**
     * <attr name="mfwdsb_line_height" format="dimension|reference"/>
     *
     * @param context
     * @param attrs
     */

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.mfw_double_seek_bar);
        max = typedArray.getInt(R.styleable.mfw_double_seek_bar_mfwdsb_max, max);
        min = typedArray.getInt(R.styleable.mfw_double_seek_bar_mfwdsb_min, min);
        currentStart = typedArray.getInt(R.styleable.mfw_double_seek_bar_mfwdsb_start, min);
        currentEnd = typedArray.getInt(R.styleable.mfw_double_seek_bar_mfwdsb_end, max);
        numPart = typedArray.getInt(R.styleable.mfw_double_seek_bar_mfwdsb_numpart, 1);
        autoNest = typedArray.getBoolean(R.styleable.mfw_double_seek_bar_mfwdsb_autonest, false);
        lineInColor = typedArray.getColor(R.styleable.mfw_double_seek_bar_mfwdsb_incolor, 0xffff9d00);
        lineOutColor = typedArray.getColor(R.styleable.mfw_double_seek_bar_mfwdsb_outcolor, 0xffececec);
        dotInColor = typedArray.getColor(R.styleable.mfw_double_seek_bar_mfwdsb_dotincolor, 0xffff9d00);
        dotOutColor = typedArray.getColor(R.styleable.mfw_double_seek_bar_mfwdsb_dotoutcolor, 0xffececec);
        lineHeight = typedArray.getDimensionPixelSize(R.styleable.mfw_double_seek_bar_mfwdsb_line_height, 5);
        int textSize = typedArray.getDimensionPixelSize(R.styleable.mfw_double_seek_bar_mfwdsb_text_size, 30);
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        textPaint.setTextSize(textSize);
        dotRadius = (int) (lineHeight * 1.2);
        typedArray.recycle();
        barThumb = (BitmapDrawable) context.getResources().getDrawable(R.drawable.hotel_filter_seek_bar_thumb);
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mCurrentStartP = new Point();
        mCurrentEndP = new Point();
        mMinP = new Point();
        mMaxP = new Point();
        barThumb.setCallback(this);
        barThumb.setBounds(0, 0, barThumb.getIntrinsicWidth(), barThumb.getIntrinsicHeight());
        for (int i = 0; i <= numPart; i++) {
            points.add(new Point());
        }

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean handle = false;
        final float x = event.getX();
        //先不考虑多点触控
        switch (MotionEventCompat.getActionMasked(event)) {
            case MotionEvent.ACTION_DOWN:
                //消除animator
                if (mode == MODE_MUL && (x <= mCurrentStartP.x || ((x < mCurrentEndP.x) && Math.abs(x - mCurrentStartP.x) < Math.abs(x - mCurrentEndP.x)))) {
                    status = STATUE_LEFT_DRAG;
                    currentStart = calculateCurrentValue(x);
                    if (leftAnimator != null) {
                        leftAnimator.cancel();
                    }
                } else {
                    if (rightAnimator != null) {
                        rightAnimator.cancel();
                    }
                    status = STATUE_RIGHT_DRAG;
                    currentEnd = calculateCurrentValue(x);
                }
                setCurrentValue(currentStart, currentEnd);
                if (onScrollChangeListener != null) {
                    onScrollChangeListener.onChanged(currentStart, currentEnd);
                }
                handle = true;
                break;
            case MotionEvent.ACTION_MOVE:
                if (status == STATUE_LEFT_DRAG) {
                    int value = calculateCurrentValue(x);
                    currentStart = Math.min(value, currentEnd);
                } else if (status == STATUE_RIGHT_DRAG) {
                    int value = calculateCurrentValue(x);
                    currentEnd = Math.max(value, currentStart);
                }
                setCurrentValue(currentStart, currentEnd);
                if (onScrollChangeListener != null) {
                    onScrollChangeListener.onChanging(currentStart, currentEnd);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (autoNest) {
                    if (status == STATUE_LEFT_DRAG) {
                        int value = calculateCurrentValue(x);
                        if (leftAnimator == null) {
                            leftAnimator = new ValueAnimator();
                            leftAnimator.addUpdateListener(leftAnimatorUpdateListener);
                            leftAnimator.addListener(new AnimatorListener());
                        }
                        currentStart = Math.min(value, currentEnd);
                        for (int i = 0, size = integers.size() - 1; i < size; i++) {
                            if (currentStart >= integers.get(i) && currentStart <= integers.get(i + 1)) {
                                int middleValue = (integers.get(i) + integers.get(i + 1)) / 2;
                                if (currentStart > middleValue && currentEnd > integers.get(i + 1)) {
                                    animateToPartInterval(leftAnimator, currentStart, integers.get(i + 1));
                                } else {
                                    animateToPartInterval(leftAnimator, currentStart, integers.get(i));
                                }
                                break;
                            }
                        }
                    } else if (status == STATUE_RIGHT_DRAG) {
                        int value = calculateCurrentValue(x);
                        currentEnd = Math.max(value, currentStart);
                        if (rightAnimator == null) {
                            rightAnimator = new ValueAnimator();
                            rightAnimator.addUpdateListener(rightAnimatorUpdateListener);
                            rightAnimator.addListener(new AnimatorListener());

                        }
                        for (int i = 0, size = integers.size() - 1; i < size; i++) {
                            if (currentEnd >= integers.get(i) && currentEnd <= integers.get(i + 1)) {
                                int middleValue = (integers.get(i) + integers.get(i + 1)) / 2;
                                if (currentEnd < middleValue && currentStart < integers.get(i)) {
                                    animateToPartInterval(rightAnimator, currentEnd, integers.get(i));
                                } else {
                                    animateToPartInterval(rightAnimator, currentEnd, integers.get(i + 1));
                                }
                                break;
                            }
                        }
                    }
                }
                status = STATUE_DILE;
                break;
        }

        return handle || super.onTouchEvent(event);
    }


    private int calculateCurrentValue(float pointX) {
        float per = (pointX - mMinP.x) / (mMaxP.x - mMinP.x);
        float correctPer = Math.max(Math.min(1, per), 0);
        return (int) ((max - min) * correctPer + min);
    }


    private void onDrawDot(Canvas canvas) {
        Point point;
        for (int i = 0, size = points.size(); i < size; i++) {
            int radius = (i == 0 || i == size - 1) ? (int) (dotRadius * 1.2) : dotRadius;
            point = points.get(i);
            if (point.x < mCurrentStartP.x || point.x > mCurrentEndP.x) {
                linePaint.setColor(dotOutColor);
                canvas.drawCircle(point.x, point.y, radius, linePaint);
            } else {
                linePaint.setColor(dotInColor);
                canvas.drawCircle(point.x, point.y, radius, linePaint);
            }

        }
    }


    /**
     * 设置当前左值和右值
     *
     * @param start
     * @param end
     */
    public void setCurrentValue(int start, int end) {
        currentStart = start;
        currentEnd = end;
        currentStart = Math.min(currentStart, currentEnd);
        currentStart = Math.max(min, currentStart);
        currentEnd = Math.min(currentEnd, max);
        caculateLineIn();
        postInvalidate();
    }

    /**
     * 设置最大值和最小值
     *
     * @param min
     * @param max
     */
    public void setInitValue(int min, int max) {
        this.min = min;
        this.max = max;
        this.min = Math.max(min, 0);
        this.max = Math.max(min, max);
        requestLayout();
    }

    /**
     * 设置分块,必须大于1
     *
     * @param num
     */
    public void setPartNum(int num) {
        this.numPart = Math.max(num, 1);
        points.clear();
        for (int i = 0; i <= numPart; i++) {
            points.add(new Point());
        }
        requestLayout();
    }

    private ArrayList<Integer> integers = new ArrayList<>();

    /**
     * 因为文案是通过接口传递进来的，为了保证回掉函数不在onMeasure里执行，所以把文字的计算单独拿出来
     *
     * @param onDotTextAdapter
     */
    public void setOnDotTextAdapter(OnDotTextAdapter onDotTextAdapter) {
        this.onDotTextAdapter = onDotTextAdapter;
        content.clear();
        contentPoint.clear();
        integers.clear();
        if (this.onDotTextAdapter != null) {
            for (int i = 0, size = points.size(); i < size; i++) {
                float per = i * 1f / (size - 1);
                int value = (int) ((max - min) * per + min);
                integers.add(value);
                String text = onDotTextAdapter.content(i, value, size);
                content.add(text);
                contentPoint.add(new Point());
            }
            requestLayout();
        }

    }


    /**
     * 设置seekbar模式
     *
     * @param mode
     */
    public void setSeekBarMode(int mode) {
        this.mode = mode;
        requestLayout();
    }

    /**
     * 执行自动的归整
     *
     * @param valueAnimator
     * @param startValue
     * @param endValue
     */
    private void animateToPartInterval(ValueAnimator valueAnimator, int startValue, int endValue) {
        if (valueAnimator.isRunning()) {
            valueAnimator.cancel();
        }
        valueAnimator.setIntValues(startValue, endValue);
        valueAnimator.setDuration(200);
        valueAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isInEditMode()) {
            return;
        }
        canvas.drawColor(Color.WHITE);
        onDrawLine(canvas);
        onDrawDot(canvas);
        onDrawBar(canvas);
        onDrawText(canvas);

    }

    private void onDrawText(Canvas canvas) {
        if (content.size() == 0 || content.size() != contentPoint.size()) {
            return;
        }
        for (int i = 0, size = content.size(); i < size; i++) {
            canvas.drawText(content.get(i), contentPoint.get(i).x, contentPoint.get(i).y, textPaint);
            canvas.drawPoint(contentPoint.get(i).x, contentPoint.get(i).y, textPaint);
        }
    }


    private void onDrawLine(Canvas canvas) {
        canvas.save();
        linePaint.setXfermode(null);
        linePaint.setColor(lineOutColor);
        linePaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(lineRect, linePaint);
        linePaint.setColor(lineInColor);
        linePaint.setXfermode(xfermode);
        canvas.drawRect(lineInRect, linePaint);
        canvas.restore();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

    }

    /**
     * 绘制seekbar
     *
     * @param canvas
     */
    private void onDrawBar(Canvas canvas) {
        int halfHeight = barThumb.getIntrinsicHeight() / 2;
        int halfWidth = barThumb.getIntrinsicWidth() / 2;
        if (mode == MODE_MUL) {
            canvas.save();
            canvas.translate(mCurrentStartP.x - halfWidth, mCurrentStartP.y - halfHeight);
//            canvas.drawRect(0, 0, barThumb.getIntrinsicWidth(), barThumb.getIntrinsicHeight(), textPaint);
            barThumb.draw(canvas);
            canvas.restore();
        }
        canvas.save();
        canvas.translate(mCurrentEndP.x - halfWidth, mCurrentStartP.y - halfHeight);
        barThumb.draw(canvas);
        canvas.restore();

    }

    /**
     * 计算当前刻度，设置rect
     */
    private final void caculateLineIn() {
        float starPercent = (currentStart - min) * 1f / (max - min);
        float endPercent = (currentEnd - min) * 1f / (max - min);
        lineInRect.set((int) ((lineRect.right - lineRect.left) * starPercent) + lineRect.left, lineRect.top, (int) ((lineRect.right - lineRect.left) * endPercent) + lineRect.left, lineRect.bottom);
        mCurrentStartP.set(lineInRect.left, lineInRect.centerY());
        mCurrentEndP.set(lineInRect.right, lineInRect.centerY());
    }


    /**
     * 1.中间的线的起始位置为bar的中线点。
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //height默认仅支持wrap_content
        if (isInEditMode()) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = Math.max(barThumb.getIntrinsicHeight(), lineHeight);
        setMeasuredDimension(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY)
                , MeasureSpec.makeMeasureSpec(height + getPaddingTop() + getPaddingBottom(), MeasureSpec.EXACTLY));
        lineRect.set(barThumb.getIntrinsicWidth() / 2 + getPaddingLeft(),
                (height - lineHeight) / 2 + getPaddingTop(), width - barThumb.getIntrinsicWidth() / 2 - getPaddingRight(), (height - lineHeight) / 2 + getPaddingTop() + lineHeight);
        mMinP.set(lineRect.left, lineRect.centerY());
        mMaxP.set(lineRect.right, lineRect.centerY());
        caculateLineIn();
        Point point;
        boolean reMeasure = false;
        for (int i = 0, size = points.size(); i < size; i++) {
            point = points.get(i);
            float per = i * 1f / (size - 1);
            point.set((int) (lineRect.left + lineRect.width() * per), lineRect.centerY());
            if (content.size() == size) {
                reMeasure = true;
                float w = textPaint.measureText(content.get(i));
                Point textPoint = contentPoint.get(i);
                //这个地方不是通用的，是为了满足drawable空白才特定设置的
                textPoint.set((int) (point.x - w / 2), (int) (getMeasuredHeight() - getPaddingBottom() + textPaint.getTextSize() / 4));
            }

        }
        if (reMeasure) {
            setMeasuredDimension(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY)
                    , MeasureSpec.makeMeasureSpec((int) (getMeasuredHeight() + textPaint.getTextSize() / 4), MeasureSpec.EXACTLY));
        }

    }


    public void setOnScrollChangeListener(OnScrollChangeListener onScrollChangeListener) {
        this.onScrollChangeListener = onScrollChangeListener;
    }

    public interface OnDotTextAdapter {
        String content(int position, int value, int count);
    }

    public interface OnScrollChangeListener {
        void onChanging(int startValue, int endValue);

        void onChanged(int startValue, int endValue);
    }

    private class RightAnimatorUpdateListener implements ValueAnimator.AnimatorUpdateListener {

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            int value = (int) animation.getAnimatedValue();
            setCurrentValue(currentStart, value);
        }
    }

    private class AnimatorListener implements ValueAnimator.AnimatorListener {

        @Override
        public void onAnimationStart(Animator animation) {
            if (MfwCommon.DEBUG) {
                MfwLog.d(TAG, "onAnimationStart  = ");
            }
            if (onScrollChangeListener != null) {
                onScrollChangeListener.onChanged(currentStart, currentEnd);
            }
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if (onScrollChangeListener != null) {
                onScrollChangeListener.onChanged(currentStart, currentEnd);
            }
            if (MfwCommon.DEBUG) {
                MfwLog.d(TAG, "onAnimationEnd  = ");
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            if (onScrollChangeListener != null) {
                onScrollChangeListener.onChanged(currentStart, currentEnd);
            }
            if (MfwCommon.DEBUG) {
                MfwLog.d(TAG, "onAnimationCancel  = ");
            }
        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }


    private class LeftAnimatorUpdateListener implements ValueAnimator.AnimatorUpdateListener {

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {

            int value = (int) animation.getAnimatedValue();
            if (MfwCommon.DEBUG) {
                MfwLog.d(TAG, "onAnimationUpdate  = " + value);
            }
            setCurrentValue(value, currentEnd);
            if (onScrollChangeListener != null) {
                onScrollChangeListener.onChanging(currentStart, currentEnd);
            }
        }
    }

}
