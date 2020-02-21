package com.demon.yu.glide;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.demon.yu.lib.gausiia.Guassia;
import com.example.mypractice.R;

public class GlideTestAct extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glide);
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        Glide.with(this).load("").into(imageView);
    }
}
