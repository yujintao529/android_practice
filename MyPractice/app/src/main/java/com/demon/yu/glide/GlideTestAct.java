package com.demon.yu.glide;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.mypractice.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class GlideTestAct extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glide);
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        Glide.with(this).load("").into(imageView);
    }
}
