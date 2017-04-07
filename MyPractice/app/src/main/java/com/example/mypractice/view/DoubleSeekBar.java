package com.example.mypractice.view;

import android.animation.ValueAnimator;
import android.content.Context;
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
import android.util.Log;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;

import com.example.mypractice.R;

import java.util.ArrayList;

/**
 * Created by yujintao on 2017/4/5.
 */

public class DoubleSeekBar extends View {
    private static final String TAG = DoubleSeekBar.class.getSimpleName();

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
    private int lineHeight = 10;//线的高度
    private int lineOutColor = Color.RED;
    private int lineInColor = Color.BLUE;
    private int dotInColor = Color.RED;
    private int dotOutColor = Color.BLUE;
    private int numPart = 4;
    private int dotRadius = 10;
    private Xfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER);
    //value
    private int min = 0;//最小值
    private int max = 400;//最大值
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

    public DoubleSeekBar(Context context) {
        this(context, null);
    }

    public DoubleSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        if (isInEditMode()) {
            return;
        }
        initAttrs(context, attrs);
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
                if (onScrollChangeListener != null) {
                    onScrollChangeListener.onChanged(currentStart, currentEnd);
                }
                caculateLineIn();
                invalidate();
                handle = true;
                break;
            case MotionEvent.ACTION_MOVE:
                if (status == STATUE_LEFT_DRAG) {
                    int value = calculateCurrentValue(x);
                    currentStart = Math.min(value, currentEnd);
//                    Log.d(TAG,"motion move currentStart "+currentStart);
                } else if (status == STATUE_RIGHT_DRAG) {
                    int value = calculateCurrentValue(x);
                    currentEnd = Math.max(value, currentStart);
//                    Log.d(TAG,"motion move currentEnd "+currentEnd);
                }
                if (onScrollChangeListener != null) {
                    onScrollChangeListener.onChanging(currentStart, currentEnd);
                }
                caculateLineIn();
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:

                if (onScrollChangeListener != null) {
                    onScrollChangeListener.onChanged(currentStart, currentEnd);
                }
                if (autoNest) {
                    if (status == STATUE_LEFT_DRAG) {
                        int value = calculateCurrentValue(x);
                        if (leftAnimator == null) {
                            leftAnimator = new ValueAnimator();
                            leftAnimator.addUpdateListener(leftAnimatorUpdateListener);
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
//                    Log.d(TAG,"motion move currentEnd "+currentEnd);

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
            int radius = (i == 0 || i == size - 1) ? dotRadius + 5 : dotRadius;
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
        Log.d(TAG, "on measure " + MeasureSpec.getMode(widthMeasureSpec) + " " + MeasureSpec.getMode(heightMeasureSpec));
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


    private void initAttrs(Context context, AttributeSet attrs) {
        context.obtainStyledAttributes(attrs, R.styleable.double_seek_bar);
        barThumb = (BitmapDrawable) context.getResources().getDrawable(R.drawable.hotel_filter_seek_bar_thumb);
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mCurrentStartP = new Point();
        mCurrentEndP = new Point();
        mMinP = new Point();
        mMaxP = new Point();
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        textPaint.setTextSize(30);
        barThumb.setCallback(this);
        barThumb.setBounds(0, 0, barThumb.getIntrinsicWidth(), barThumb.getIntrinsicHeight());
        for (int i = 0; i <= numPart; i++) {
            points.add(new Point());
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

    private class LeftAnimatorUpdateListener implements ValueAnimator.AnimatorUpdateListener {

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            int value = (int) animation.getAnimatedValue();
            setCurrentValue(value, currentEnd);
        }
    }

}
