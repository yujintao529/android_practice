package com.example.mypractice.matrix;

import android.graphics.Matrix;


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
	
public class Flower {
	int selfHeigth=50;
	int selfWidth=50;
	float left;
	float top;
	//每一百毫秒速度
	int Sspeed;
	int alpha=0;
	int Hspeed;
	int degree;
	int degreeSpeed;
	//现在不用
	boolean isDisplay=true;
	Matrix matrix;
	public Flower(){
		super();
		
	}
	public Flower(float left, float top) {
		super();
		this.left = left;
		this.top = top;
	}
	public void drop(int width,int height){
		top+=Sspeed;
		left+=Hspeed;
		if(top>height){
			top=-selfHeigth;
			isDisplay=false;
		}
		if(left>width||left<0){
			Hspeed=-Hspeed;
		}
	}
	
}
