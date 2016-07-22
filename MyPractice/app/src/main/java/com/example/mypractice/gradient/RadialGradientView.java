package com.example.mypractice.gradient;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.view.View;

public class RadialGradientView extends View{

	Paint mPaint;
	LinearGradient mGradient;
	Matrix mMatrix;
	Paint mPaint2;
	float[] mPoints;
	public RadialGradientView(Context context, AttributeSet attrs) {
		super(context, attrs);
		//这是线性渐变的渲染。渲染的部分为 终点和起点的射线（为什么是射线？我也不知道..效果就是从终点触发过起点，一直到边界）。
		//所有垂直这条射线的直线（都是一直到边界位置）所形成的图型。那么渐变的颜色呢？每一条垂直这条直线的颜色相同，同时最开始的颜色
		//颜色位置为射线与边界的交叉点。这个点到终点的颜色渐变。渐变的过程其实就是垂直射线的所有直线形成的。
		//这个渐变所用的相对起点就是这个view的起点和边界。
		//注意：如果起点(0,20,0,50)，那么绘出的区域为50-0y轴，x轴则是边界，同时，0-20的颜色为开始绘制的颜色，也就是黑色了。
		mGradient=new LinearGradient(0,0, 0,1, 0xffffffff,0x00FFFFFF, TileMode.CLAMP); 
		
		mPaint=new Paint();
		mPaint2=new Paint();
		mPaint2.setStyle(Style.STROKE);
		mPaint2.setColor(Color.BLACK);
//		mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));//这行太坑了
		mPaint.setShader(mGradient);
		mMatrix=new Matrix();
		//

		//通过我很多实验，矩阵的变形并不在画出图形进行变形，而是直接将绘图数据进行矩阵变形然后在进行绘制。
		mMatrix.setScale(1,50f);//缩放行为。但是不会超出所划区域，也就是如果是drawRect，不会超出这个区域。其实我感觉就是改变了渐变的终点而已。
		mMatrix.postRotate(90);
		//所以如果postTranslate把(20,50)的效果就是把终点平移，其实起点也平移了，但是由于渲染的机制，还是会到边界的交汇点。
		//绘图数据变形后，就开始按照新的图形来进行渲染了

		mMatrix.postTranslate(50,0);//
		mGradient.setLocalMatrix(mMatrix);
		mPoints=new float[]{200f,200f,300f,300f};
	}
	@Override
	protected void onDraw(Canvas canvas) {
		drawRectPaint(canvas);
		
//		drawPaint(canvas);
		canvas.drawPosText("点",mPoints, mPaint2);
	}
	private void drawPaint(Canvas canvas){
		canvas.drawPaint(mPaint);
	}
	
	private void drawRectPaint(Canvas canvas){
		//这个区域范围内进行渐变。但是渐变线所采用的相对起点仍然是此视图的起点和范围。不受所画图的影响。
		canvas.drawRect(0, 0, 50,50, mPaint);
	}
	
}
