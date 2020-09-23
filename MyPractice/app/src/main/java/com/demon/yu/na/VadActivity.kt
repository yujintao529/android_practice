package com.demon.yu.na

import android.app.Activity
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.demon.yu.avd.WebVadHelper
import com.example.mypractice.R


class VadActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vad)
        WebVadHelper.testVad()
    }

}