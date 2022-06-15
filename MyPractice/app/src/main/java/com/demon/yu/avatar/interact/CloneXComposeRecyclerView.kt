package com.demon.yu.avatar.interact

import android.content.Context
import android.graphics.Canvas
import android.graphics.Point
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.AvatarRecyclerView
import com.demon.yu.view.recyclerview.FakeLayoutCoorExchangeUtils
import com.demon.yu.view.recyclerview.copy
import com.example.mypractice.Logger

class CloneXComposeRecyclerView(context: Context, attr: AttributeSet? = null) :
    AvatarRecyclerView(context, attr) {
    companion object {
        private const val MAX_SCROLL_TO_CENTER_DURATION = 2000
    }


    private var centerX: Int = 0
    private var centerY: Int = 0
    private var centerPoint = Point()
    var onLayoutListener: OnLayoutListener? = null
    var onDrawListener: OnDrawListener? = null


    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        super.onMeasure(widthSpec, heightSpec)
        centerX = measuredWidth / 2
        centerY = measuredHeight / 2
        Logger.debug(
            CloneXComposeUiConfig.TAG,
            "measuredWidth =$measuredWidth,measuredHeight=$measuredHeight"
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        val centerX = (r - l) / 2
        val centerY = (b - t) / 2
        if (centerPoint.x != centerX || centerPoint.y != centerY) {
            centerPoint.set(centerX, centerY)
            onLayoutListener?.onCenter(centerPoint.copy())
        }
    }


    override fun dispatchDraw(canvas: Canvas) {
        onDrawListener?.onDraw(canvas)
        super.dispatchDraw(canvas)
    }


    fun scrollToCenter(x: Int, y: Int, duration: Int) {
        smoothScrollBy(x - centerX, y - centerY, null, duration)
    }


    /**
     * @return int 滑动预估时间
     */
    fun scrollViewToCenter(child: View): Int {
        val destCenterPoint = FakeLayoutCoorExchangeUtils.getCenterPoint(child)
        val scrollDuration =
            computeScrollDuration(destCenterPoint.x, destCenterPoint.y, 0, 0)
        scrollToCenter(destCenterPoint.x, destCenterPoint.y, scrollDuration)
        return scrollDuration
    }

    fun scrollCenterToPosition(position: Int): Int {
        if (adapter?.itemCount ?: 0 < position) {
            return -1
        }
        val child = layoutManager?.findViewByPosition(position)
        if (child != null) {
            return scrollViewToCenter(child)
        }
        return -1
    }

    private fun computeScrollDuration(dx: Int, dy: Int, vx: Int, vy: Int): Int {
        val absDx = Math.abs(dx)
        val absDy = Math.abs(dy)
        val horizontal = absDx > absDy
        val velocity = Math.sqrt((vx * vx + vy * vy).toDouble()).toInt()
        val delta = Math.sqrt((dx * dx + dy * dy).toDouble()).toInt()
        val containerSize = if (horizontal) width else height
        val halfContainerSize = containerSize / 2
        val distanceRatio = Math.min(1f, 1f * delta / containerSize)
        val distance =
            (halfContainerSize + halfContainerSize).toFloat() * distanceInfluenceForSnapDuration(
                distanceRatio
            )
        val duration: Int = if (velocity > 0) {
            4 * Math.round(1000 * Math.abs(distance / velocity))
        } else {
            val absDelta = (if (horizontal) absDx else absDy).toFloat()
            ((absDelta / containerSize + 1) * 300).toInt()
        }
        return Math.min(duration, MAX_SCROLL_TO_CENTER_DURATION)
    }

    private fun distanceInfluenceForSnapDuration(f: Float): Float {
        var f = f
        f -= 0.5f // center the values about 0.
        f *= 0.3f * Math.PI.toFloat() / 2.0f
        return Math.sin(f.toDouble()).toFloat()
    }

    interface OnDrawListener {
        fun onDraw(canvas: Canvas)
    }


    interface OnLayoutListener {
        fun onCenter(point: Point)
    }
}