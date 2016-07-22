package com.example.mypractice.matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.example.mypractice.R;


import android.R.color;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

/**
 * <p>Copyright: Copyright (c) 2013</p>
 * 
 * <p>Company: 呱呱视频社区<a href="www.guagua.cn">www.guagua.cn</a></p>
 *
 * @description 用handler实现刷新，  矩阵
 * 				另外的版本采用ObjectAnimatorf
 *
 * @author jintao
 * @modify  
 * @version 1.0.0 
*/

public class Flowers2 extends View {

	private static final int isRefresh = 0;
	private static final int stopRefresh = 1;
	private static final int startRefresh = 2;
	private static final int ALWAYS_MODE = 1;
	private static final int ONCE_MODE = 2;
	List<BaseFlower> list;
	int width=500;
	int heigth=500;
	Paint paint;
	Random random = new Random(System.currentTimeMillis());
	int sum = 30;
	Bitmap bitmap;
	RefreshHandler refreshHandler;
	long lastOnDrawTime;
	public int playMode = 1;

	public Flowers2(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public Flowers2(Context context, int width, int height) {
		super(context);
		init();
	}

	void init() {
		list = new ArrayList<BaseFlower>();
		paint = new Paint();
		refreshHandler = new RefreshHandler();
		//写死一个
		bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.room_anim_flower);
		playMode=ALWAYS_MODE;
//		playMode=ONCE_MODE;
	}
	public void setWidthAndHeight(int width,int height){
		this.width=width;
		this.heigth=height;
		initFlowsers();
	}
	
	/**
	 * @author: jintao
	 * @return: 
	 * @date: 2014-12-8 下午8:51:40
	 * 测试数据
	*/
		
	public void initFlowsers() {
		width=getWidth();
		heigth=getHeight();
		for(int i=0;i<10;i++){
			BlossomFlower blossomFlower=new BlossomFlower(width/4+random.nextInt(width/2),heigth/4+random.nextInt(heigth/2));
			blossomFlower.duction=2000+random.nextInt(50)*100;
			list.add(blossomFlower);
		}
		for(int i=0;i<20;i++){
			DropFlower dropFlower=new DropFlower(100+random.nextInt(400),-200+random.nextInt(100));
			dropFlower.alpha=155+random.nextInt(100);
			dropFlower.sSpeed=10+i*1;
			dropFlower.hSpeed=-10+random.nextInt(20);
			list.add(dropFlower);
		}
	}

	public void reset() {
		list.clear();
		initFlowsers();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawColor(color.transparent);
		int count = canvas.save();
		BaseFlower temp;
		int timeDifference=0;
		long ondrawTime=System.currentTimeMillis();
		if(lastOnDrawTime!=0){
			timeDifference=(int) (ondrawTime-lastOnDrawTime);
//			BaseAndroidLogTemp.debug(timeDifference+"");
		}
		for (int i = 0, size = list.size(); i < size; i++) {
			temp = list.get(i);
			temp.action(canvas, paint, bitmap, timeDifference);
		}
		lastOnDrawTime=ondrawTime;
		canvas.restoreToCount(count);

	}

	private boolean isNeedRefresh() {
		boolean isNeddRefresh = false;

		switch (playMode) {
			case ALWAYS_MODE:
				isNeddRefresh = true;
				break;
			case ONCE_MODE:
				for (int i = 0, size = list.size(); i < size; i++) {
					BaseFlower flower = list.get(i);
					if (flower.isDisplay == true) {
						isNeddRefresh = true;
						break;
					}
				}
			default:
				break;
		}
		return isNeddRefresh;
	}

	public void startPlay() {
		refreshHandler.start();
	}

	public void pausePlay() {
		refreshHandler.stop();
	}

	public void stopPlay() {
		refreshHandler.stop();
		reset();
	}

	private class RefreshHandler extends Handler {
		public int delay =50;//毫秒
		public boolean status;

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case isRefresh:
					if (!status)
						return;
					invalidate();
					if (isNeedRefresh()) {
						sendEmptyMessageDelayed(isRefresh, delay);
					}
					break;
				default:
					break;
			}
		}

		private void start() {
			status = true;
			sendEmptyMessageDelayed(isRefresh,0);
		}

		private void stop() {
			status = false;
		}
	}
}
