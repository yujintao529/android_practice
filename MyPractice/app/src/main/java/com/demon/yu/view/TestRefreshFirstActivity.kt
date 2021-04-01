package com.demon.yu.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.mypractice.R

/**
 * 实验：第二个activity更改第一个activity的View，什么时候真正的更新
 * question：
 * 1. activity更新的控制机制。
 * 2. viewRootImpl的刷新机制是如何控制的。
 *
 *  整个resume是通过AMS和WMS协同工作
 *
 * 引申出来的内容：
 * 1. 启动页面时，activity的onStart和onResume是在handleLaunchActivity的通知里调用的
 * 2. 页面返回可见时，activity的onStart，onRestart,onResume是在handleResumeActivity的通知里调用的
 */
class TestRefreshFirstActivity : AppCompatActivity() {
    companion object {
        var testRefreshFirstActivity: TestRefreshFirstActivity? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actvitiy_test_refresh_first)
        testRefreshFirstActivity = this
    }


    private var index = 0
    private val dimens = arrayOf(300, 400)
    fun changeSize() {
        val showColor = findViewById<View>(R.id.showColor)
        showColor.layoutParams.width = dimens[index % 2]
        showColor.layoutParams.height = dimens[index % 2]
        index++
        showColor.requestLayout()
    }

    fun onClickToSecond(view: View) {
        val intent = Intent(this, TestRefreshSecondActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        testRefreshFirstActivity = null
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onRestart() {
        super.onRestart()
    }

}