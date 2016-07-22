package com.example.mypractice.v4;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.example.mypractice.R;

/**
 * Created by jintao on 2015/9/16.
 */
public class  DrawerlayoutAct extends FragmentActivity {
    /**
     * Perform initialization of all fragments and loaders.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
    }
}
