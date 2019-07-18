package com.demon.yu.window

import android.app.Application
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.example.mypractice.R
import com.example.mypractice.common.Common

class OverlayWindow(private val windowManager: WindowManager) {


    private val windowLayoutParams = WindowManager.LayoutParams()


    private val rootView: View


    private var isAdd = false

    init {
        val textView = TextView(Common.application)
        textView.text = "悬浮窗"
        textView.setTextColor(Color.WHITE)
        textView.setPadding(20, 20, 20, 20)
        textView.setBackgroundResource(R.drawable.overlay_window_bg)
        rootView = textView
        windowLayoutParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        windowLayoutParams.x = 200
        windowLayoutParams.y = 200
        windowLayoutParams.gravity = Gravity.LEFT.or(Gravity.TOP)
        windowLayoutParams.alpha = 1f
        windowLayoutParams.width=WindowManager.LayoutParams.WRAP_CONTENT
        windowLayoutParams.height=WindowManager.LayoutParams.WRAP_CONTENT

    }


    fun show() {
        if (!isAdd) {
            isAdd = true
            windowManager.addView(rootView, windowLayoutParams)
        }

    }

    fun hide() {
        if (!isAdd) {
            isAdd = false
            windowManager.removeView(rootView)
        }
    }


    fun updateType(type: Int) {
        windowLayoutParams.type = type
        show()
    }

}