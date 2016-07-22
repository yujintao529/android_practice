package com.example.mypractice.matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.example.mypractice.R;

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
 * @description 用handler实现刷新，  测试用的。
 * 				另外的版本采用ObjectAnimatorf
 *
 * @author jintao
 * @modify  
 * @version 1.0.0 
*/

public class Flowers extends View {

	private static final int isRefresh = 0;
	private static final int stopRefresh = 1;
	private static final int startRefresh = 2;
	private static final int ALWAYS_MODE = 1;
	private static final int ONCE_MODE = 2;
	List<Flower> list;
	int width=500;
	int heigth=500;
	Paint paint;
	Random random = new Random(System.currentTimeMillis());
	int sum = 30;
	Bitmap bitmap;
	RefreshHandler refreshHandler;
	public int playMode = 1;

	public Flowers(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public Flowers(Context context, int width, int height) {
		super(context);
		init();
		setWidthAndHeight(width, height);
	}

	void init() {
		list = new ArrayList<Flower>();
		paint = new Paint();
		refreshHandler = new RefreshHandler();
		//写死一个
		bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.room_anim_flower);
		initFlowsers();
		playMode=ALWAYS_MODE;
//		playMode=ONCE_MODE;
	}
	public void setWidthAndHeight(int width,int height){
		this.width=width;
		this.heigth=height;
	}
	public void initFlowsers() {
		for (int i = 0; i < sum; i++) {
			Flower flower = new Flower();
			flower.top =-200+random.nextInt(100);
			flower.left = 100 + random.nextInt(width - 100);
			flower.Hspeed = -10+i;
			flower.Sspeed = 5 + i *2;
			flower.alpha=random.nextInt(255);
			list.add(flower);
		}
	}

	public void reset() {
		list.clear();
		initFlowsers();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int count = canvas.save();
		Flower temp;
		for (int i = 0, size = list.size(); i < size; i++) {
			temp = list.get(i);
			paint.setAlpha(temp.alpha);
			if(playMode==ALWAYS_MODE||temp.isDisplay)
				canvas.drawBitmap(bitmap, temp.left, temp.top, paint);
			temp.drop(width,heigth);
		}
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
					Flower flower = list.get(i);
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
		public int delay = 50;//毫秒
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
