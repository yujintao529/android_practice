package com.example.mypractice.v7;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.mypractice.R;

/**
 * Created by yujintao on 2017/2/16.
 */
public class MaterilActivity extends AppCompatActivity {
    public static final String TAG = MaterilActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ripple);

    }
}
