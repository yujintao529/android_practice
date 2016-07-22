package com.example.mypractice.resource.drawable;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.mypractice.R;

/**
 * Created by jintao on 2015/8/28.
 */
public class LampCordAct extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lampcord);

        ImageView imageView= (ImageView) findViewById(R.id.image);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        imageView.setImageDrawable(new LampCord(this));
//        imageView.setBackgroundDrawable();
    }
}
