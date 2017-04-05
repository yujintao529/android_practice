package com.example.mypractice.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.mypractice.R;

/**
 * Created by yujintao on 2017/4/5.
 */

public class DoubleSeekBar extends View {


    //doubleSeekBar模式，两种
    public static final int MODE_SINGLE=0;
    public static final int MODE_MUL=1;



    //val
    private int height=5;//线的高度
    private int dotHeight=10;//点的高度
    private int dotHalfHeight=5;//点的一半的高度

    //value
    private int min=0;
    private int max=100;
    private int currentStart=0;
    private int currentEnd=100;

    private Paint paint;


    private Drawable barThumb;

    public DoubleSeekBar(Context context) {
        this(context, null);
    }
    public DoubleSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        if (isInEditMode()) {
            return;
        }
        initAttrs(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {



        return super.onTouchEvent(event);
    }



    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //height默认仅支持wrap_content

        int width =MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(MeasureSpec.makeMeasureSpec(width,MeasureSpec.EXACTLY)
                ,MeasureSpec.makeMeasureSpec(barThumb.getIntrinsicHeight()+getPaddingTop()+getPaddingBottom(),MeasureSpec.EXACTLY));

    }

    private void initAttrs(Context context, AttributeSet attrs) {
        context.obtainStyledAttributes(attrs, R.styleable.double_seek_bar);
        barThumb=context.getResources().getDrawable(R.drawable.hotel_filter_seek_bar_thumb);
        int drawableHeight=barThumb.getIntrinsicHeight();
        int drawableWidth=barThumb.getIntrinsicWidth();

    }

    public DoubleSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (isInEditMode()) {
            return;
        }
        initAttrs(context, attrs);
    }
}
