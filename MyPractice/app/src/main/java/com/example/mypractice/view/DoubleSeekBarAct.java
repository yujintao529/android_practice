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
        MfwDoubleSeekBar doubleSeekBar = (MfwDoubleSeekBar) findViewById(R.id.seek_bar);
        doubleSeekBar.setOnDotTextAdapter(new MfwDoubleSeekBar
                .OnDotTextAdapter() {
            @Override
            public String content(int position, int value, int count) {
                return position + "->" + value;
            }
        });
        doubleSeekBar.setOnScrollChangeListener(new MfwDoubleSeekBar.OnScrollChangeListener() {
            @Override
            public void onChanging(int startValue, int endValue) {
                if (MfwCommon.DEBUG) {
                    MfwLog.d(DoubleSeekBarAct.class.getSimpleName(), "onChanging  " + startValue + " " + endValue);
                }
            }

            @Override
            public void onChanged(int startValue, int endValue) {
                if (MfwCommon.DEBUG) {
                    MfwLog.d(DoubleSeekBarAct.class.getSimpleName(), "onChanged  " + startValue + " " + endValue);
                }
            }
        });
        doubleSeekBar = (MfwDoubleSeekBar) findViewById(R.id.seek_bar_2);
        doubleSeekBar.setSeekBarMode(MfwDoubleSeekBar.MODE_SINGLE);
        doubleSeekBar.setCurrentValue(0, 20);

    }


}
