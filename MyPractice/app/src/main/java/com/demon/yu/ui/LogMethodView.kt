package com.demon.yu.ui

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.ViewGroup
import com.example.mypractice.Logger

private const val TAG = "LogMethodView"

class LogMethodView(context: Context?, attrs: AttributeSet? = null) : ViewGroup(context, attrs) {

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        Logger.d(TAG, "onDraw canvas")
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        Logger.d(TAG, "onLayout changed=$changed")
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        Logger.d(TAG, "onMeasure")
    }
}