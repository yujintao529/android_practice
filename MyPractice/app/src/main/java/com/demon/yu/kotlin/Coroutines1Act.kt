package com.demon.yu.kotlin

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.demon.yu.extenstion.start
import com.example.mypractice.R

class Coroutines1Act : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coroutines1)
    }


    fun onClick(view: View) {
        start(Coroutines2Act::class.java)
    }
}