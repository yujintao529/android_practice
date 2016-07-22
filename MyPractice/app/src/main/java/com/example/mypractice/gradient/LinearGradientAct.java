package com.example.mypractice.gradient;

import android.app.Activity;
import android.os.Bundle;

public class LinearGradientAct extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(new LinearGradienView(this));
	}
}
