package com.example.mypractice.graphic;

import android.R.color;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DrawingBoard extends View implements IEraser,IPen{
	private static final int ERASER=0x01;
	private static final int DEFAULT=0x00;
	Path path;
	Paint paint;
	float mX;
	float mY;
	Bitmap bitmap;
	Canvas canvas;
	Paint bitmapPaint;
	int mode=DEFAULT;//
	PorterDuffXfermode porterDuffXfermode;
	public DrawingBoard(Context context,AttributeSet attributeSet){
		super(context,attributeSet);
		init();
	}public DrawingBoard(Context context) {
		super(context);
		init();
	}
	public void init(){
		path=new Path();
		paint=new Paint(Paint.DITHER_FLAG);
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setColor(0xFFFF0000);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
	    paint.setStrokeWidth(10);
	    bitmapPaint=new Paint();
	    porterDuffXfermode=new PorterDuffXfermode(Mode.CLEAR);
	    
	}
	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		if(hasWindowFocus&&bitmap==null){
			bitmap=Bitmap.createBitmap(getWidth(), getHeight(),Config.ARGB_8888);
		    canvas=new Canvas(bitmap);
		}
		super.onWindowFocusChanged(hasWindowFocus);
	}
	public void setColor(int color){
		if(paint!=null){
			paint.setColor(color);
		}
	}
	public void setPorterDuff(){
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				path.reset();
				path.moveTo(event.getX(), event.getY());
				mX=event.getX();
				mY=event.getY();
				invalidate();
				break;
			case MotionEvent.ACTION_MOVE:
				float xT=Math.abs(mX-event.getX());
				float yT=Math.abs(mY-event.getY());
				if(xT>3||yT>3){
					path.quadTo(mX+(event.getX()-mX)/2,(event.getY()-mY)/2+mY, event.getX(),event.getY());
					mX=event.getX();
					mY=event.getY();
				}
				invalidate();
				break;
			case MotionEvent.ACTION_UP:
				path.lineTo(event.getX(), event.getY());
				canvas.drawPath(path, paint);
				path.reset();
				invalidate();
				break;
			default:
				break;
		}
		
		return true;
	}
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(color.white);
		canvas.drawBitmap(bitmap,0, 0, bitmapPaint);
		canvas.drawPath(path, paint);
	}
	@Override
	public boolean setPenColor() {
		return false;
	}
	@Override
	public boolean setPenWidth() {
		return false;
	}
	@Override
	public boolean setEraserHeight() {
		return false;
	}
	@Override
	public boolean isEffective() {
		return (mode & ERASER)==ERASER;
	}
	
	@Override
	public boolean setEraserEffective(boolean flag) {
		if(paint==null)return false;
		if(flag){
			paint.setXfermode(porterDuffXfermode);
			paint.setColor(Color.WHITE);
//			paint.setAlpha(0);
			mode |= ERASER;
		}else{
			paint.setXfermode(null);
			paint.setColor(Color.RED);
//			paint.setAlpha(0);
			mode &=~ERASER;
		}
		return false;
	}
	@Override
	public boolean setEraserWidth() {
		return false;
	}
	
}
