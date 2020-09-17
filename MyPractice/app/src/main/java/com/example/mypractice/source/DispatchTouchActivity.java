package com.example.mypractice.source;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.example.mypractice.R;

import javax.net.ssl.HttpsURLConnection;

public class DispatchTouchActivity extends Activity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_source_dispatch_touch);
        ((LogFrameLayout) findViewById(R.id.parent)).setName("parent");
//        ((LogFrameLayout) findViewById(R.id.parent)).setMotionEventSplittingEnabled(false);
        findViewById(R.id.one).setOnClickListener(this);
        ((LogFrameLayout) findViewById(R.id.one)).setName("one");
        findViewById(R.id.two).setOnClickListener(this);
        ((LogFrameLayout) findViewById(R.id.two)).setName("two");
        findViewById(R.id.one).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        findViewById(R.id.two).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }


    @Override
    public void onClick(View v) {
    }


}
