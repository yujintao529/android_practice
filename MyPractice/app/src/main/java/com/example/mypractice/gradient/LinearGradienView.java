package com.example.mypractice.gradient;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.view.View;

/**
 * Created by jintao on 2015/7/14.
 */
public class LinearGradienView extends View{
    private LinearGradient linearGradient;

    private Paint testPaint;
    public LinearGradienView(Context context) {
        super(context);
        linearGradient=new LinearGradient(0,0,0,50, Color.RED,Color.BLUE, Shader.TileMode.REPEAT);
        testPaint=new Paint();
        testPaint.setColor(Color.BLACK);
        testPaint.setStyle(Paint.Style.STROKE);
        testPaint.setStrokeWidth(5);
        testPaint.setShader(linearGradient);
        testPaint.setTextSize(80);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawText("123456",10,100,testPaint);
        canvas.translate(0,20);
        canvas.drawText("123456",100,120,testPaint);
    }
}
