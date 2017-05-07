package com.example.mypractice.canvas;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by yujintao on 2017/5/7.
 */

public class ClipPathView extends FrameLayout {
    public static final String TAG = ClipPathView.class.getSimpleName();

    private Paint paint;

    public ClipPathView(@NonNull Context context) {
        super(context);
        init();
    }

    public ClipPathView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private Path leftTop;
    private Path leftBottom;
    private Path rightTop;
    private Path rightBottom;

    private RectF leftTopRF;

    private PaintFlagsDrawFilter paintFlagsDrawFilter;
    private void init() {
        paint=new Paint(Paint.ANTI_ALIAS_FLAG|Paint.DITHER_FLAG);
        leftTop=new Path();
        leftTopRF=new RectF();
        paintFlagsDrawFilter=new PaintFlagsDrawFilter(0, Paint.FILTER_BITMAP_FLAG|Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {


        /**
         * 1.首先clipPath是没有办法消除锯齿的，所以为了消除锯齿，只能接口path来使用，通过setXfermode来实现
         * 2.不能开启硬件加速，裁剪是不支持硬件加速的
         */


        /** 实现圆角start*/
        /**
         * 1. path.arcTo,addArc等等是21才可以使用的方法。而且clippath是对canvas进行的话，所以对已经画好的是不起作用的。
         *
         * Op参数
         *   A:表示第一个裁剪的形状;
         *    B:表示第二次裁剪的形状;
         *    Region.Op.DIFFERENCE ：是A形状中不同于B的部分显示出来
         *    Region.Op.REPLACE：是只显示B的形状
         *    Region.Op.REVERSE_DIFFERENCE ：是B形状中不同于A的部分显示出来，这是没有设置时候默认的
         *    Region.Op.INTERSECT：是A和B交集的形状
         *    Region.Op.UNION：是A和B的全集
         *    Region.Op.XOR：是全集形状减去交集形状之后的部分
         */
        canvas.save();
        final int width = getMeasuredWidth();
        final int height=getMeasuredHeight();
        canvas.setDrawFilter(paintFlagsDrawFilter);
        leftTopRF.set(0,0,width,height);
        leftTop.moveTo(0,0);
        leftTop.lineTo(width/2,0);
        leftTop.addRoundRect(leftTopRF,50,50, Path.Direction.CCW);
        canvas.clipPath(leftTop, Region.Op.INTERSECT);
        super.dispatchDraw(canvas);
        canvas.restore();
        /** end*/
    }
}
