package com.example.mypractice.matrix;


import com.example.mypractice.R;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class FlowerAct extends Activity {

	private Flowers flowers;
	private Flowers2 flowers2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Display display=getWindowManager().getDefaultDisplay();
		WindowManager manager=(WindowManager) getSystemService(Context.WINDOW_SERVICE);
//		manager.addView(new Flowers2(this, 300, 300),new WindowManager.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		setContentView(R.layout.activity_flower);
//		flowers=(Flowers) findViewById(R.id.container);
//		flowers.setWidthAndHeight(display.getWidth()-50,display.getHeight()-50);
		flowers2=(Flowers2) findViewById(R.id.container);
//		flowers2.setWidthAndHeight(display.getWidth()-50,display.getHeight()-50);
//		setContentView(new MatrixImage(this));
//		setContentView(R.layout.activity_main);
//		final ImageView imageView=new ImageView(this);
//		imageView.setBackgroundResource(R.drawable.ic_launcher);
//		setContentView(imageView);
////		imageView.animate().rotationY(30);
//		AnimatorUpdateListener animatorUpdateListener=new AnimatorUpdateListener() {
//			@Override
//			public void onAnimationUpdate(ValueAnimator animation) {
//				imageView.invalidate();
//				logger.d("text");
//			}
//		};
//		ObjectAnimator animator=ObjectAnimator.ofFloat(imageView, "rotationX", 45);
//		animator.setRepeatCount(Animation.INFINITE);
//		animator.addUpdateListener(animatorUpdateListener);
//		animator.start();
//		AnimatorListener animatorListener=new AnimatorListener() {
//			@Override
//			public void onAnimationStart(Animator animation) {
//			}
//			@Override
//			public void onAnimationRepeat(Animator animation) {
//			}
//			@Override
//			public void onAnimationEnd(Animator animation) {
//			}
//			@Override
//			public void onAnimationCancel(Animator animation) {
//			}
//		};
//		
	}
	public void onClick(View view){
		switch (view.getId()) {
			case R.id.start:
				flowers2.initFlowsers();
				flowers2.startPlay();
				break;
			case R.id.pause:
				flowers2.pausePlay();
			case R.id.stop:
				flowers2.stopPlay();
			default:
				break;
		}
	}
}
