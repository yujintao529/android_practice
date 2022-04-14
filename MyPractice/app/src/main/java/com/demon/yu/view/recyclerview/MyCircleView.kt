package com.demon.yu.view.recyclerview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.TextView

class MyCircleView(context: Context, attr: AttributeSet? = null) : TextView(context, attr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)

    var number = -1
        set(value) {
            field = value
            text = number.toString()
        }

    var color = Color.CYAN
        set(value) {
            field = value
            invalidate()
        }

    init {
        setTextColor(Color.WHITE)
        setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20F)
        gravity = Gravity.CENTER
    }

    override fun onDraw(canvas: Canvas) {
        paint.color = color
        canvas.drawCircle(measuredWidth / 2f, measuredHeight / 2f, measuredWidth / 2f, paint)
        super.onDraw(canvas)
    }


}