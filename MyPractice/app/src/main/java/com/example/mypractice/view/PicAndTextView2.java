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
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by 郁金涛 on 2016/7/22 14:47
 * 邮箱：jintao@17guagua.com
 *
 * 1.本版采用StaticLayout进行文字绘制，如果需要设置单独点击事件的话，那么就需要拦截触摸事件和分发了。
 * @description
 */
public class PicAndTextView2 extends ViewGroup{


    private CharSequence text="共和国中华民共和国中华人民共民共和国中华人民共和国中华人民共和国中华人民共和国";
    private CharSequence text1="美利坚合众国美利坚合众国美利坚合众国";
    private StaticLayout staticLayout;
    private StaticLayout staticLayout1;
    private TextPaint mPaint;
    private Rect rect;
    private Paint paint;
    private ArrayList<StaticLayoutEntry> staticLayoutEntries=new ArrayList<>();//所有的staticlayout
    private SparseArray<Rect> mEachLineRect=new SparseArray<>();//每行的rect
    private ArrayList<Object> mChildList=new ArrayList<>();
    private int mIndex;//添加序号
    private int mWidth;
    public PicAndTextView2(Context context) {
        super(context);
    }

    public PicAndTextView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint=new TextPaint();
        mPaint.setColor(Color.RED);
        rect=new Rect();
        paint=new Paint();
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.STROKE);
    }

    public void clean(){
        mChildList.clear();
        mIndex=0;
    }

    public void addNewChild(View view){
        addView(view);
        mChildList.add(view);
        mIndex++;
    }



    //
    public void addText(CharSequence text){
        mChildList.add(text);
        mIndex++;
    }




    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        staticLayoutEntries.clear();
        mEachLineRect.clear();
        mWidth=getMeasuredWidth();
        final int size=mChildList.size();

        for(int i=0;i<size;i++){
            Object object=mChildList.get(i);

        }


    }



    private StaticLayout generateStaticLayout(CharSequence mCharSequence,int width){
        return new StaticLayout(mCharSequence,mPaint,width, Layout.Alignment.ALIGN_NORMAL,1.0f,0.0f,false);
    }



    @Override
    protected void dispatchDraw(Canvas canvas) {




        super.dispatchDraw(canvas);
    }
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(changed){
            int childCount=getChildCount();
            for(int i=0;i<childCount;i++){
                View view=getChildAt(i);

            }
        }
    }





    private class StaticLayoutEntry{
        public StaticLayout staticLayout;
        public int left;
        public int top;
//        public int Rect;
    }
















    @Deprecated
    private class ChildLinked{
        public View view;
        public CharSequence mCharSequence;
        public int type;
        @Deprecated
        public ChildLinked next;

        public ChildLinked(View view, CharSequence mCharSequence, int type, ChildLinked next) {
            this.view = view;
            this.mCharSequence = mCharSequence;
            this.type = type;
            this.next = next;
        }

    }
}
