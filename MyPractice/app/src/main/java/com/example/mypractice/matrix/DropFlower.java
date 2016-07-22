package com.example.mypractice.matrix;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

public class DropFlower extends BaseFlower{

	int alpha=255;
	public int nowDuction=0;
	
	public int hSpeed=10;
	public int sSpeed=10;
	public float left;
	public float top;
	Matrix matrix;
	public DropFlower(float locationX, float locationY) {
		super(locationX, locationY);
		left=locationX;
		top=locationY;
		matrix=new Matrix();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void action(Canvas canvas, Paint paint, Bitmap bitmap, int timeDifference) {
		//最好检查时间差。。
		left+=hSpeed;
		top+=sSpeed;
		if(left>canvas.getWidth()||left<0){
			hSpeed=-hSpeed;
		}
		if(top>canvas.getHeight()){
			top=locationY;
			isDisplay=false;
		}
		matrix.setTranslate(left,top);
		paint.setAlpha(alpha);
		canvas.drawBitmap(bitmap, matrix, paint);
		paint.setAlpha(255);
	}

}
