package com.example.mypractice.dialog;

import com.example.mypractice.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

public class PopuwindowAct extends Activity {
	PopupWindow popupWindow ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_popuwindow);
		
//		popupWindow= new PopupWindow(new MyPopupWindow(this),getWindowManager().getDefaultDisplay().getWidth(),getWindowManager().getDefaultDisplay().getHeight(),true);
//		popupWindow= new PopupWindow(new MyPopupWindow(this),ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT,true);
		popupWindow= new PopupWindow(new MyPopupWindow(this),ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT,true);
//		popupWindow= new PopupWindow(new MyPopupWindow(this),300,300,true);
//		popupWindow.setBackgroundDrawable(background)
		popupWindow.setBackgroundDrawable(new ColorDrawable(Color.GRAY));
//		popupWindow.setOutsideTouchable(true);
	}

	public void onClick(View view) {
//		getWindow().setBackgroundDrawable(new ColorDrawable(Color.GREEN));
//		popupWindow.showAsDropDown(view);
		popupWindow.showAtLocation(view, Gravity.CENTER,0	, 0);//鍦ㄤ腑闂存樉绀恒�
	}

	public class MyPopupWindow extends RelativeLayout {

		public MyPopupWindow(Context context) {
			super(context);
			
			init();
			setBackgroundColor(Color.GRAY);
		}
		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			
		}
		public void init() {
			LinearLayout layout = new LinearLayout(PopuwindowAct.this);
//			layout.setBackground(new ColorDrawable(Color.BLUE));
			layout.setBackgroundColor(Color.BLUE);
			layout.setOrientation(LinearLayout.VERTICAL);
//			RelativeLayout.LayoutParams l = (android.widget.RelativeLayout.LayoutParams) layout.getLayoutParams();
//			RelativeLayout.LayoutParams l=new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, MATCH_PARENT);
			
//			l.gravity = Gravity.CENTER_HORIZONTAL;
//			layout.setLayoutParams(l);
			LinearLayout.LayoutParams l=new LinearLayout.LayoutParams(-2, -2);
			l.gravity=Gravity.CENTER_HORIZONTAL;
			
			Button button = new Button(PopuwindowAct.this);
			button.setText("asdf");
			layout.addView(button, l);
			
			Button button2 = new Button(PopuwindowAct.this);
			button2.setText("asdfdfasd");
			LinearLayout.LayoutParams l2=new LinearLayout.LayoutParams(-1,-2);
//			l2.gravity=
			layout.addView(button2, l2);
			addView(layout, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			
		}
	}
}
