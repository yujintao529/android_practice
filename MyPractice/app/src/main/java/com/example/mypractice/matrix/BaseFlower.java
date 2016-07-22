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
 * @description  矩阵版本
 *
 *
 * @author jintao
 * @modify 
 * @version 1.0.0 
*/
	
public abstract class BaseFlower implements IAction{
	int selfHeigth=50;
	int selfWidth=50;
	//容器的高度和宽度，变量名字用错了...
	float locationX;
	float locationY;
	boolean isDisplay;
	public BaseFlower(float locationX,float locationY){
		this.locationX=locationX;
		this.locationY=locationY;
		isDisplay=true;
	}
	public void setSelfWidthAndHeight(int width,int height){
		selfHeigth=height;
		selfWidth=width;
	}
}
