package com.example.mypractice.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
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
    private static final int STATUE_RIGHT_SCROLL = 4;

    //val
    private int lineHeight;//线的高度
    private int lineOutColor;
    private int lineInColor;
    private int dotInColor;
    private int dotOutColor;
    private int dotRadius;
    private Xfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER);

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

    private int mode;//模式


    private boolean autoNest;//牛逼的属性，自动NumPart位置调整。

    private LeftAnimatorUpdateListener leftAnimatorUpdateListener = new LeftAnimatorUpdateListener();
    private RightAnimatorUpdateListener rightAnimatorUpdateListener = new RightAnimatorUpdateListener();


    private ArrayList<Point> points = new ArrayList<>();
    private ArrayList<String> content = new ArrayList<>();
    private ArrayList<Point> contentPoint = new ArrayList<>();


    private OnDotTextAdapter onDotTextAdapter;


    private SeekbarStrategy seekbarStrategy;

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
     * @param context
     * @param attrs
     */

    private void initAttrs(Context context, AttributeSet attrs) {
        seekbarStrategy = new LinearStrategry();//default
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.mfw_double_seek_bar);
        int max = typedArray.getInt(R.styleable.mfw_double_seek_bar_mfwdsb_max, 100);
        int min = typedArray.getInt(R.styleable.mfw_double_seek_bar_mfwdsb_min, 0);
        int currentStart = typedArray.getInt(R.styleable.mfw_double_seek_bar_mfwdsb_start, min);
        int currentEnd = typedArray.getInt(R.styleable.mfw_double_seek_bar_mfwdsb_end, max);
        int numPart = typedArray.getInt(R.styleable.mfw_double_seek_bar_mfwdsb_numpart, 1);
        setInitValue(min, max);
        setPartNum(numPart);
        seekbarStrategy.setCurrentValue(currentStart, currentEnd);
        autoNest = typedArray.getBoolean(R.styleable.mfw_double_seek_bar_mfwdsb_autonest, false);
        lineInColor = typedArray.getColor(R.styleable.mfw_double_seek_bar_mfwdsb_incolor, 0xffff9d00);
        lineOutColor = typedArray.getColor(R.styleable.mfw_double_seek_bar_mfwdsb_outcolor, 0xffececec);
        dotInColor = typedArray.getColor(R.styleable.mfw_double_seek_bar_mfwdsb_dotincolor, 0xffff9d00);
        dotOutColor = typedArray.getColor(R.styleable.mfw_double_seek_bar_mfwdsb_dotoutcolor, 0xffececec);
        lineHeight = typedArray.getDimensionPixelSize(R.styleable.mfw_double_seek_bar_mfwdsb_line_height, 5);
        int textSize = typedArray.getDimensionPixelSize(R.styleable.mfw_double_seek_bar_mfwdsb_text_size, 30);
        int textColor = typedArray.getColor(R.styleable.mfw_double_seek_bar_mfwdsb_text_color, 0xff696969);
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        textPaint.setTextSize(textSize);
        textPaint.setColor(textColor);
        dotRadius = (int) (lineHeight * 1.2);
        mode = typedArray.getInt(R.styleable.mfw_double_seek_bar_mfwdsb_mode, MODE_MUL);
        typedArray.recycle();
        barThumb = (BitmapDrawable) context.getResources().getDrawable(R.drawable.hotel_filter_seek_bar_thumb);
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mCurrentStartP = new Point();
        mCurrentEndP = new Point();
        mMinP = new Point();
        mMaxP = new Point();
        barThumb.setCallback(this);
        barThumb.setBounds(0, 0, barThumb.getIntrinsicWidth(), barThumb.getIntrinsicHeight());
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean handle = false;
        final float x = event.getX();
        float currentStart = seekbarStrategy.getCurrentStart();
        float currentEnd = seekbarStrategy.getCurrentEnd();
        float per = calculateCurrentValuePer(x);
        if (MfwCommon.DEBUG) {
            MfwLog.d(TAG, "onTouchEvent per = " + per);
        }
        //先不考虑多点触控
        switch (MotionEventCompat.getActionMasked(event)) {
            case MotionEvent.ACTION_DOWN:
                //消除animator
                if (mode == MODE_MUL && (x <= mCurrentStartP.x || ((x < mCurrentEndP.x) && Math.abs(x - mCurrentStartP.x) < Math.abs(x - mCurrentEndP.x)))) {
                    status = STATUE_LEFT_DRAG;
                    if (leftAnimator != null) {
                        leftAnimator.cancel();
                    }

                    currentStart = seekbarStrategy.caculateValue(per);
                } else {
                    if (rightAnimator != null) {
                        rightAnimator.cancel();
                    }
                    status = STATUE_RIGHT_DRAG;
                    currentEnd = seekbarStrategy.caculateValue(per);
                }
                setCurrentValueInterval(currentStart, currentEnd);
                handle = true;
                getParent().requestDisallowInterceptTouchEvent(true);
                if (onScrollChangeListener != null) {
                    onScrollChangeListener.onChanged(seekbarStrategy.getCurrentStart(), seekbarStrategy.getCurrentEnd());
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (status == STATUE_LEFT_DRAG) {
                    currentStart = seekbarStrategy.caculateValue(per);
                } else if (status == STATUE_RIGHT_DRAG) {
                    currentEnd = seekbarStrategy.caculateValue(per);
                }
                setCurrentValueInterval(currentStart, currentEnd);
                if (MfwCommon.DEBUG) {
                    MfwLog.d(TAG, "onTouchEvent move = " + currentStart + " " + currentEnd);
                }
                if (onScrollChangeListener != null) {
                    onScrollChangeListener.onChanging(seekbarStrategy.getCurrentStart(), seekbarStrategy.getCurrentEnd());
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
                if (autoNest) {
                    if (status == STATUE_LEFT_DRAG) {
                        if (leftAnimator == null) {
                            leftAnimator = new ValueAnimator();
                            leftAnimator.addUpdateListener(leftAnimatorUpdateListener);
                            leftAnimator.addListener(new AnimatorListener());
                        }
                        currentStart = seekbarStrategy.caculateValue(per);
                        for (int i = 0, size = seekbarStrategy.getPartNum(); i < size; i++) {
                            if (currentStart >= seekbarStrategy.getPartValue(i) && currentStart <= seekbarStrategy.getPartValue(i + 1)) {
                                int middleValue = (seekbarStrategy.getPartValue(i) + seekbarStrategy.getPartValue(i + 1)) / 2;
                                status = STATUE_LEFT_SCROLL;
                                if (currentStart > middleValue && currentEnd >= seekbarStrategy.getPartValue(i + 1)) {
                                    animateToPartInterval(leftAnimator, currentStart, seekbarStrategy.getPartValue(i + 1));
                                } else {
                                    animateToPartInterval(leftAnimator, currentStart, seekbarStrategy.getPartValue(i));
                                }
                                break;
                            }
                        }
                    } else if (status == STATUE_RIGHT_DRAG) {
                        currentEnd = seekbarStrategy.caculateValue(per);
                        if (rightAnimator == null) {
                            rightAnimator = new ValueAnimator();
                            rightAnimator.addUpdateListener(rightAnimatorUpdateListener);
                            rightAnimator.addListener(new AnimatorListener());

                        }
                        for (int i = 0, size = seekbarStrategy.getPartNum(); i < size; i++) {
                            if (currentEnd >= seekbarStrategy.getPartValue(i) && currentEnd <= seekbarStrategy.getPartValue(i + 1)) {
                                int middleValue = (seekbarStrategy.getPartValue(i) + seekbarStrategy.getPartValue(i + 1)) / 2;
                                status = STATUE_RIGHT_SCROLL;
                                if (currentEnd < middleValue && currentStart <= seekbarStrategy.getPartValue(i)) {
                                    animateToPartInterval(rightAnimator, currentEnd, seekbarStrategy.getPartValue(i));
                                } else {
                                    animateToPartInterval(rightAnimator, currentEnd, seekbarStrategy.getPartValue(i + 1));
                                }
                                break;
                            }
                        }
                    }
                } else {
                    if (status == STATUE_LEFT_DRAG) {
                        currentStart = seekbarStrategy.caculateValue(per);
                    } else if (status == STATUE_RIGHT_DRAG) {
                        currentEnd = seekbarStrategy.caculateValue(per);
                    }
                    setCurrentValueInterval(currentStart, currentEnd);
                    if (onScrollChangeListener != null) {
                        onScrollChangeListener.onChanged(seekbarStrategy.getCurrentStart(), seekbarStrategy.getCurrentEnd());
                    }
                    status = STATUE_DILE;
                }

                break;
        }

        return handle || super.onTouchEvent(event);
    }


    private float calculateCurrentValuePer(float pointX) {
        return Math.max(0, Math.min((pointX - mMinP.x) / (mMaxP.x - mMinP.x), 1));
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
    private void setCurrentValueInterval(float start, float end) {
        if (status == STATUE_RIGHT_DRAG) {
            end = Math.max(end, start);
        } else if (status == STATUE_LEFT_DRAG) {
            start = Math.min(end, start);
        }
        if (MfwCommon.DEBUG) {
            MfwLog.d(TAG, "setCurrentValueInterval start = " + start + " end = " + end);
        }
        seekbarStrategy.setCurrentValue(start, end);
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
        seekbarStrategy.setInitValue(min, max);
        seekbarStrategy.setPartNum(seekbarStrategy.getPartNum());
        requestLayout();
    }

    /**
     * 设置分块,必须大于1
     *
     * @param num
     */
    public void setPartNum(int num) {
        seekbarStrategy.setPartNum(num);
        points.clear();
        for (int i = 0, size = seekbarStrategy.getPartNum(); i <= size; i++) {
            points.add(new Point());
        }
        requestLayout();
    }


    /**
     * 因为文案是通过接口传递进来的，为了保证回掉函数不在onMeasure里执行，所以把文字的计算单独拿出来
     *
     * @param onDotTextAdapter
     */
    public void setOnDotTextAdapter(OnDotTextAdapter onDotTextAdapter) {
        this.onDotTextAdapter = onDotTextAdapter;
        content.clear();
        contentPoint.clear();
        if (this.onDotTextAdapter != null) {
            for (int i = 0, size = seekbarStrategy.getPartNum(); i <= size; i++) {
                String text = onDotTextAdapter.content(i, seekbarStrategy.getPartValue(i), size);
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
    private void animateToPartInterval(ValueAnimator valueAnimator, float startValue, float endValue) {
        if (valueAnimator.isRunning()) {
            valueAnimator.cancel();
        }
        valueAnimator.setFloatValues(startValue, endValue);
        valueAnimator.setDuration(200);
        valueAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isInEditMode()) {
            return;
        }
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

    //便于使用
    float[] arrTemp = new float[2];

    /**
     * 计算当前刻度，设置rect
     */
    private final void caculateLineIn() {
        seekbarStrategy.getPercentIn(arrTemp);
        lineInRect.set((int) ((lineRect.right - lineRect.left) * arrTemp[0]) + lineRect.left, lineRect.top, (int) ((lineRect.right - lineRect.left) * arrTemp[1]) + lineRect.left, lineRect.bottom);
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
                if (i == 0) {
                    textPoint.set((int) ((point.x) - dotRadius * 1.2), (int) (getMeasuredHeight() - getPaddingBottom() + textPaint.getTextSize() / 2));
                } else if (i == size - 1) {
                    textPoint.set((int) (getMeasuredWidth() - getPaddingRight() - w), (int) (getMeasuredHeight() - getPaddingBottom() + textPaint.getTextSize() / 2));
                } else {
                    textPoint.set((int) (point.x - w / 2 + dotRadius * 1.2), (int) (getMeasuredHeight() - getPaddingBottom() + textPaint.getTextSize() / 2));
                }

            }

        }
        if (reMeasure) {
            //最后加上1dp的值，为了text下面有点空白
            setMeasuredDimension(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY)
                    , MeasureSpec.makeMeasureSpec((int) (getMeasuredHeight() + textPaint.getTextSize() / 2 + dip2px(getContext(), 1)), MeasureSpec.EXACTLY));
        }

    }

    public static int dip2px(Context context, int dp) {
        return Math.round(context.getResources().getDisplayMetrics().density * dp);
    }

    public int getMin() {
        return seekbarStrategy.getMin();
    }

    public int getMax() {
        return seekbarStrategy.getMax();
    }

    public int getCurrentStart() {
        return seekbarStrategy.getCurrentStart();
    }

    public int getCurrentEnd() {
        return seekbarStrategy.getCurrentEnd();
    }

    public void setOnScrollChangeListener(OnScrollChangeListener onScrollChangeListener) {
        this.onScrollChangeListener = onScrollChangeListener;
    }

    public void setCurrentValue(int currentStart, int currentEnd) {
        setCurrentValueInterval(currentStart, currentEnd);
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
            float value = (float) animation.getAnimatedValue();
            setCurrentValueInterval(seekbarStrategy.getCurrentStart(), value);
            if (MfwCommon.DEBUG) {
                MfwLog.d(TAG, "onAnimationUpdate  = " + value);
            }
            if (onScrollChangeListener != null) {
                onScrollChangeListener.onChanging(seekbarStrategy.getCurrentStart(), seekbarStrategy.getCurrentEnd());
            }
        }
    }

    private class AnimatorListener implements ValueAnimator.AnimatorListener {

        @Override
        public void onAnimationStart(Animator animation) {
            if (MfwCommon.DEBUG) {
                MfwLog.d(TAG, "onAnimationStart  = ");
            }
            if (onScrollChangeListener != null) {
                onScrollChangeListener.onChanged(seekbarStrategy.getCurrentStart(), seekbarStrategy.getCurrentEnd());
            }
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if (onScrollChangeListener != null) {
                onScrollChangeListener.onChanged(seekbarStrategy.getCurrentStart(), seekbarStrategy.getCurrentEnd());
            }
            status = STATUE_DILE;
            if (MfwCommon.DEBUG) {
                MfwLog.d(TAG, "onAnimationEnd  = ");
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            if (onScrollChangeListener != null) {
                onScrollChangeListener.onChanged(seekbarStrategy.getCurrentStart(), seekbarStrategy.getCurrentEnd());
            }
            status = STATUE_DILE;
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

            float value = (float) animation.getAnimatedValue();
            if (MfwCommon.DEBUG) {
                MfwLog.d(TAG, "onAnimationUpdate  = " + value);
            }
            setCurrentValueInterval(value, seekbarStrategy.getCurrentEnd());
            if (onScrollChangeListener != null) {
                onScrollChangeListener.onChanging(seekbarStrategy.getCurrentStart(), seekbarStrategy.getCurrentEnd());
            }
        }
    }

    /**
     * 计算seekbar策略
     * 为了保证平滑滑动，所以内部使用float类型进行计算,但是提供外面的全是int类型
     */
    public static abstract class SeekbarStrategy {

        int partNum;
        float mMax;
        float mMin;
        float mCurrentStart;
        float mCurrentEnd;

        public SeekbarStrategy() {
            partNum = 1;
        }


        public void setInitValue(int min, int max) {
            this.mMin = min;
            this.mMax = max;
            this.mMin = Math.max(min, 0);
            this.mMax = Math.max(min, max);
            mCurrentStart = Math.max(mCurrentStart, min);
            mCurrentEnd = Math.min(mCurrentEnd, max);
            setPartNum(partNum);
        }

        public int getMax() {
            return (int) mMax;
        }

        public int getMin() {
            return (int) mMin;
        }

        private void setCurrentValue(float start, float end) {
            if (start > end) {
                throw new IllegalArgumentException("start must not bigger than end");
            }
            start = Math.max(start, mMin);
            end = Math.min(end, mMax);
            mCurrentStart = start;
            mCurrentEnd = end;
        }

        public int getCurrentStart() {
            return (int) mCurrentStart;
        }

        public int getCurrentEnd() {
            return (int) mCurrentEnd;
        }

        public abstract float caculateValue(float per);

        public abstract void getPercentIn(float[] arr);

        public abstract int getPartValue(int part);

        public void setPartNum(int num) {
            this.partNum = num;
            this.partNum = Math.max(1, num);
        }

        public int getPartNum() {
            return partNum;
        }
    }

    public void setSeekbarStrategy(SeekbarStrategy seekbarStrategy) {
        if (this.seekbarStrategy != null) {
            seekbarStrategy.setInitValue(this.seekbarStrategy.getMin(), this.seekbarStrategy.getMax());
            seekbarStrategy.setPartNum(this.seekbarStrategy.getPartNum());
        }
        this.seekbarStrategy = seekbarStrategy;
        setPartNum(seekbarStrategy.partNum);
        requestLayout();
    }

    /**
     * 集合策略，填入几个值做为刻度
     */
    public static class ArrStrategry extends SeekbarStrategy {

        ArrayList<Integer> integers;


        /**
         * @param integers size必须大于等于2
         */
        public ArrStrategry(ArrayList<Integer> integers) {
            super();
            this.integers = integers;
            setPartNum(integers.size() - 1);
            setInitValue(integers.get(0), integers.get(integers.size() - 1));
        }

        @Override
        public void setPartNum(int num) {
            //忽略输入值
            super.setPartNum(integers.size() - 1);

        }

        @Override
        public void setInitValue(int min, int max) {
            super.setInitValue(min, max);
        }

        /**
         * @param per 百分比，必须>=0，<=1
         * @return
         */
        @Override
        public float caculateValue(float per) {
            if (per >= 1) {
                return integers.get(partNum);
            } else if (per <= 0) {
                return integers.get(0);
            }
            float perAvg = 1f / partNum;
            int part = (int) (per / perAvg);
            float avg = per - part * perAvg;
            if (MfwCommon.DEBUG) {
                MfwLog.d(TAG, "caculateValue  = " + per);
            }
            return integers.get(part) + (integers.get(part + 1) - integers.get(part)) / perAvg * avg;
        }

        @Override
        public void getPercentIn(float[] arr) {
            float startPer = 0f;
            float endPer = 1f;
            final int size = integers.size() - 1;
            final float perAvg = 1f / size;
            for (int i = 0; i < size; i++) {
                if (mCurrentStart >= integers.get(i) && mCurrentStart <= integers.get(i + 1)) {
                    float off = mCurrentStart - integers.get(i);
                    float diff = integers.get(i + 1) - integers.get(i);
                    float per = off / diff;
                    startPer = perAvg * i + per * perAvg;
                }
                if (mCurrentEnd >= integers.get(i) && mCurrentEnd <= integers.get(i + 1)) {
                    float off = mCurrentEnd - integers.get(i);
                    float diff = integers.get(i + 1) - integers.get(i);
                    float per = off / diff;

                    endPer = i * perAvg + per * perAvg;
                    if (MfwCommon.DEBUG) {
                        MfwLog.d(TAG, "getPercentIn " + mCurrentEnd + " " + endPer);
                    }
                }
            }

            arr[0] = startPer;
            arr[1] = endPer;
        }

        @Override
        public int getPartValue(int part) {
            part = Math.min(part, partNum);
            part = Math.max(0, part);
            return integers.get(part);
        }
    }


    public static class LinearStrategry extends SeekbarStrategy {

        private ArrayList<Integer> partNums;

        public LinearStrategry() {
            super();
            partNums = new ArrayList<>();
        }

        @Override
        public void setPartNum(int num) {
            super.setPartNum(num);
            partNums.clear();
            for (int i = 0; i <= partNum; i++) {
                float avgPart = i * 1f / partNum;
                partNums.add((int) ((mMax - mMin) * avgPart + mMin));
            }
        }

        @Override
        public float caculateValue(float per) {
            return (mMax - mMin) * per + mMin;
        }

        @Override
        public void getPercentIn(float[] arr) {
            float startPer = (mCurrentStart - mMin) / (mMax - mMin);
            float endPer = (mCurrentEnd - mMin) / (mMax - mMin);
            arr[0] = startPer;
            arr[1] = endPer;
        }

        @Override
        public int getPartValue(int part) {
            part = Math.min(part, partNum);
            part = Math.max(0, part);
            return partNums.get(part);
        }

    }

}
