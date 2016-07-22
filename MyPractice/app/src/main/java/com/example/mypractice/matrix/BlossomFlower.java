package com.example.mypractice.matrix;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;


/**
 * <p>Copyright: Copyright (c) 2013</p>
 * 
 * <p>Company: 呱呱视频社区<a href="www.guagua.cn">www.guagua.cn</a></p>
 *
 * @description 坐标版本
 *
 *
 * @author jintao
 * @modify 
 * @version 1.0.0 
*/
	
public class BlossomFlower extends BaseFlower{
	int alpha=255;
	public int duction=3000;
	public int nowDuction=0;
	Matrix matrix;
	float[] fs;
	public BlossomFlower(float locationX,float locationY){
		super(locationX,locationY);
		fs=new float[9];
		matrix=new Matrix();
	}
	
	/* (non-Javadoc)
	 * @see com.qiqi.matrix.IAction#action(android.graphics.Canvas, android.graphics.Paint, android.graphics.Bitmap, int)
	 */
	//此方法运行在ondraw中，不能进行对象创建，同时所有的参数使用后需要复原。
	@Override
	public void action(Canvas canvas, Paint paint, Bitmap bitmap,int timeDifference) {
		nowDuction+=timeDifference;
		matrix.reset();
//		BaseAndroidLogTemp.debug(matrix.toString());
		
		if(nowDuction>duction){
			nowDuction=0;
//			matrix.postTranslate(locationX,locationY);
//			canvas.drawBitmap(bitmap, matrix, paint);
		}else{
			float rate=(float)(nowDuction%duction*0.001+0.05);
			
//			BaseAndroidLogTemp.debug("rate is "+rate);
			matrix.postScale(rate,rate,1.0f,1.0f);
			matrix.postTranslate(locationX,locationY);
			int absAlpha=(int) (255-rate*255);
			int alpha=absAlpha<0?0:absAlpha;
			paint.setAlpha(alpha);
			canvas.drawBitmap(bitmap, matrix, paint);
			paint.setAlpha(255);
		}
//		BaseAndroidLogTemp.debug(matrix.toString());
		
	}
	
}
