package com.demon.yu.view.recyclerview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.view.View

class MyRegionView(context: Context, attr: AttributeSet? = null) : View(context, attr) {

    var color = Color.parseColor("#55ffffff")


    override fun onDraw(canvas: Canvas) {
        canvas.drawColor(color)
    }
}