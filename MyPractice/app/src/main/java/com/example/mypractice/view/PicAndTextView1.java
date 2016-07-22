package com.example.mypractice.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by 郁金涛 on 2016/7/22 14:47
 * 邮箱：jintao@17guagua.com
 *
 * @description
 */
public class PicAndTextView1 extends RelativeLayout{
    private CharSequence text="民共和国中华人民共和国中华人民共和国中华人民共和国中华人民共和国中华民共和国中华人民共和国中华人民共和国中华人民共和国中华人民共和国中华人民共和国中华人民共和国中华人民共和国";
    private StaticLayout staticLayout;
    private TextPaint mPaint;
    private Rect rect;
    private Paint paint;
    public PicAndTextView1(Context context) {
        super(context);
    }

    public PicAndTextView1(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint=new TextPaint();
        mPaint.setColor(Color.RED);
        rect=new Rect();
        paint=new Paint();
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width=getMeasuredWidth();
        int height=getMeasuredHeight();
        rect.set(0,0,width,height);
        staticLayout=new StaticLayout(text,mPaint,width, Layout.Alignment.ALIGN_NORMAL,1.0f,0.0f,false);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        staticLayout.draw(canvas);
        canvas.drawRect(rect,paint);
        super.dispatchDraw(canvas);
    }
}
