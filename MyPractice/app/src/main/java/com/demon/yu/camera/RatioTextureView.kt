package com.demon.yu.camera

import android.content.Context
import android.util.AttributeSet
import android.view.TextureView

class RatioTextureView(context: Context, attr: AttributeSet? = null) : TextureView(context, attr) {
    private var ratio = -1f
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (ratio != -1f) {
            val width = MeasureSpec.getSize(widthMeasureSpec)
            setMeasuredDimension(width, (ratio * width).toInt())
        }
    }

    fun setRatio(ratio: Float) {
        this.ratio = ratio
        requestLayout()
//        val targetHeight = Common.screenWidth * ratio
//        if (targetHeight > Common.screenHeight) {
//            val marginLayoutParams = textureView.layoutParams as ViewGroup.MarginLayoutParams
//            marginLayoutParams.bottomMargin = ((targetHeight - Common.screenHeight) * -1).toInt()
//            textureView.layoutParams.width = Common.screenWidth
//            textureView.layoutParams.height = targetHeight.toInt()
//            textureView.requestLayout()
//        } else {
//            textureView.layoutParams.width = Common.screenWidth
//            textureView.layoutParams.height = targetHeight.toInt()
//            textureView.requestLayout()
//        }
//        post {
//            val width = measuredWidth
//            val height = measuredHeight
//            val destHeight = measuredWidth * ratio
//            if (destHeight > height) {
//                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
//                layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
//                (layoutParams as ViewGroup.MarginLayoutParams?)?.bottomMargin = (height - destHeight).toInt()
//            } else {
//                layoutParams.width = width
//                layoutParams.height = destHeight.toInt()
//                requestLayout()
//            }
//        }
    }
}