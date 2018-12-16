package com.demon.yu.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;


/**
 * canvas.clipRect(clipRect); 裁剪区域，其他的地方不会参与绘制。减少overDraw
 */
public class OverDrawView extends View {


    public OverDrawView(Context context) {
        super(context);
    }

    public OverDrawView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    private Rect clipRect = new Rect();

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        clipRect.set(30, 30, getMeasuredWidth() - 30, getMeasuredHeight() - 30);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.clipRect(clipRect);
        super.draw(canvas);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.clipRect(clipRect);
//        super.onDraw(canvas);
//        canvas.drawColor(Color.WHITE);
//        canvas.drawColor(Color.WHITE);

    }
}
