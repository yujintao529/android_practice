package com.demon.yu.view

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.mypractice.R

class TestRefreshSecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actvitiy_test_refresh_second)
    }


    fun onClickChangeFirstActivity(view: View) {
        TestRefreshFirstActivity.testRefreshFirstActivity?.changeSize()
    }


}