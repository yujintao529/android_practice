package com.demon.yu.camera

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.example.mypractice.Logger

class CameraFrameLayout(context: Context, attr: AttributeSet? = null) : FrameLayout(context, attr) {
    private var ratio = -1f
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (ratio != -1f) {
            val width = MeasureSpec.getSize(widthMeasureSpec)
            val height = (ratio * width).toInt()
            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY))
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }


    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        Logger.debug("CameraFrameLayout", "onLayout($width,$height)")
    }

    fun setRatio(ratio: Float) {
        this.ratio = ratio
        requestLayout()
    }
}