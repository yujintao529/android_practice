package com.demon.yu.view;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mypractice.R;

public class NextScrollViewAct extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nextscrollview);
        MyTitleTopNestScrollView myTitleTopNestScrollView = findViewById(R.id.MyTitleTopNestScrollView);
    }
}
