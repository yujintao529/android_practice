package com.example.mypractice.graphic;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.example.mypractice.R;

public class DrawingBoradAct extends Activity {

	DrawingBoard myPicture;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		getWindow().addFlags()
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_drawingboard);
		myPicture=(DrawingBoard) findViewById(R.id.picture);
	}
	public void onClick(View view){
		switch (view.getId()) {
			case R.id.eraser:
				myPicture.setEraserEffective(!myPicture.isEffective());
				break;
			default:
				break;
		}
	}
	
}
