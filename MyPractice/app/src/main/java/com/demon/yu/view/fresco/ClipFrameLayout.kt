package com.demon.yu.view.fresco

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.demon.yu.extenstion.dp2Px

class ClipFrameLayout(context: Context, attr: AttributeSet? = null) : FrameLayout(context, attr) {


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val lp = child.layoutParams

            val childWidthMeasureSpec = getChildMeasureSpec(
                widthMeasureSpec,
                paddingLeft + paddingRight, lp.width + 20.dp2Px()
            )
            val childHeightMeasureSpec = getChildMeasureSpec(
                heightMeasureSpec,
                paddingTop + paddingBottom, lp.height + 20.dp2Px()
            )

            child.measure(childWidthMeasureSpec, childHeightMeasureSpec)
        }

    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            child.layout(0, 0, child.width, child.height)
        }
    }

}