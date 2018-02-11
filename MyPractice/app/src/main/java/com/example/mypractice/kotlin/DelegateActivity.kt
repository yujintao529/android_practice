package com.example.mypractice.kotlin

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.mypractice.R
import kotlinx.android.synthetic.main.activity_kotlin_delegate.*

/**
 * Created by yujintao on 2018/2/11.
 */
class DelegateActivity : AppCompatActivity() {

    private var textContent: String? by bindToTextView(R.id.textView)
    private var status:Boolean  by bindToVisiable(R.id.trueView, R.id.falseView)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kotlin_delegate)
        textViewSetBtn.setOnClickListener {
            textContent = "代理设置"
        }
        change.setOnClickListener {
            status = !status
        }
    }





}

