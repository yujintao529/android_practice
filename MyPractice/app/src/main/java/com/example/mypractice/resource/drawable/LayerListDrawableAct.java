package com.example.mypractice.resource.drawable;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mypractice.R;

/**
 * Created by yujintao on 2017/5/7.
 */

public class LayerListDrawableAct extends AppCompatActivity{
    public static final String TAG = LayerListDrawableAct.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layerlist);
    }

}
