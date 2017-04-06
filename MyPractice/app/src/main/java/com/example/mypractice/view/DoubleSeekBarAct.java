package com.example.mypractice.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.mypractice.R;

/**
 * Created by yujintao on 2017/4/5.
 */

public class DoubleSeekBarAct extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_double_seek_bar);
        DoubleSeekBar doubleSeekBar= (DoubleSeekBar) findViewById(R.id.seek_bar);
        doubleSeekBar.setOnDotTextAdapter(new DoubleSeekBar.OnDotTextAdapter() {
            @Override
            public String content(int position, int value, int count) {
                return position+"->"+value;
            }
        });
        doubleSeekBar= (DoubleSeekBar) findViewById(R.id.seek_bar_2);
        doubleSeekBar.setSeekBarMode(DoubleSeekBar.MODE_SINGLE);
        doubleSeekBar.setCurrentValue(0,20);

    }


}
