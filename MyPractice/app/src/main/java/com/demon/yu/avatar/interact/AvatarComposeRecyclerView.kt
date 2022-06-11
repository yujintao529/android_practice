package com.demon.yu.avatar.interact

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.annotation.Px
import androidx.core.graphics.toRectF
import androidx.recyclerview.widget.AvatarRecyclerView
import com.demon.yu.extenstion.dp2Px
import com.demon.yu.view.fresco.IViewDrawListener
import com.demon.yu.view.recyclerview.copy
import java.lang.StrictMath.pow
import kotlin.math.*

class AvatarComposeRecyclerView(context: Context, attr: AttributeSet? = null) :
    AvatarRecyclerView(context, attr) {


    private val tag = "MyCustomize2RecyclerView"

    private var maxScaleSize = 124f / 60f

    private var secondScaleSize = 85f / 60f

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG.or(Paint.DITHER_FLAG))


    private var centerX: Int = 0
    private var centerY: Int = 0


    private var radius = 120.dp2Px()
    private var dismiss2NormalScaleDistance = 60.dp2Px().toFloat()
    private var radiusDouble = radius * 2

    private var scaleDistance = radiusDouble

    private val centerRegionWidth = radius * 2
    private val centerRegionHeight = (sin(PI / 3) * radius * 2).toInt()
    private var centerRegionRect = Rect()
    private var cornerRadius = 50.dp2Px()


    private var centerPoint = Point()
    var onLayoutListener: OnLayoutListener? = null
    var onDrawListener: OnDrawListener? = null
    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        super.onMeasure(widthSpec, heightSpec)
        centerX = measuredWidth / 2
        centerY = measuredHeight / 2
        centerRegionRect.set(
            (measuredWidth - centerRegionWidth) / 2,
            (measuredHeight - centerRegionHeight) / 2,
            (measuredWidth - centerRegionWidth) / 2 + centerRegionWidth,
            (measuredHeight - centerRegionHeight) / 2 + centerRegionHeight
        )
        scaleDistance =
            (2 * cos(PI / 6) * radius).toInt() + (radiusDouble - (2 * cos(PI / 6) * radius).toInt()) / 6
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


    override fun onScrolled(@Px dx: Int, @Px dy: Int) {
        // Do nothing
    }

    override fun dispatchDraw(canvas: Canvas) {
        onDrawListener?.onDraw(canvas)
        super.dispatchDraw(canvas)
//        if (BuildConfig.DEBUG.not() || isInEditMode.not()) {
//            return
//        }
        paint.color = Color.RED
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f.dp2Px().toFloat()
        canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), radiusDouble.toFloat(), paint)
        paint.color = Color.BLUE
        canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), radius.toFloat(), paint)
        canvas.drawRoundRect(
            centerRegionRect.toRectF(),
            cornerRadius.toFloat(),
            cornerRadius.toFloat(),
            paint
        )

    }


    fun getDistance(x: Int, y: Int): Float {
        return calculateDistance(x, y)
    }

    fun scrollToCenter(x: Int, y: Int, duration: Int) {
        smoothScrollBy(x - centerX, y - centerY, null, duration)
    }

    fun scaleXY(view: View, x: Int, y: Int, distance: Float) {
        val scale = when {
            distance >= 0 && distance <= radius -> {
                val scale =
                    (radius - distance) / (radius) * (maxScaleSize - secondScaleSize)
                scale + secondScaleSize
            }
            distance > radius && distance <= scaleDistance -> { //0...secondScaleSize,不要问我怎么算的
                val ratio =
                    max(
                        0f,
                        StrictMath.min(
                            ((StrictMath.abs((y - centerY))) / (distance)),
                            1f
                        )
                    )
                val scale = (scaleDistance - distance) / (scaleDistance - radius)
                val scaleRatio =
                    secondScaleSize + pow(ratio.toDouble(), 6.0) * 2.5f
                StrictMath.min(secondScaleSize, 1 + scale * (scaleRatio.toFloat() - 1))
            }
            distance > scaleDistance -> {

                val ratio = StrictMath.pow(
                    (1 - (StrictMath.abs((y - centerY))) / (distance)).toDouble(),
                    2.0

                ).toFloat() //1..0
                val t = StrictMath.abs((y - centerY)) / (distance)
                val scale =
                    (distance - scaleDistance + dismiss2NormalScaleDistance) / dismiss2NormalScaleDistance - 1 //0...max

                max(0f, 1 - scale * scale * scale * scale * scale * scale) // 0..1

            }
//            distance >= scaleDistance && distance <= radiusDouble -> {
//               1f
//            }
//
//            distance <= radiusDouble + dismiss2NormalScaleDistance && distance >= radiusDouble -> {
//
//                (dismiss2NormalScaleDistance + radiusDouble - distance) / dismiss2NormalScaleDistance
//            }
//
            else -> {
                0f
            }
        }
        view.scaleX = scale
        view.scaleY = scale
    }


    fun translateXY(view: View, x: Int, y: Int, distance: Float) {
        val translateRange = radius
        if (distance <= translateRange) {
            view.translationX = 0f
            view.translationY = 0f
            return
        }
        when {
            else -> {
                val magnitude = (distance - translateRange) * 0.35f/// translateRange * 124f
                val cos = StrictMath.abs((centerY - y) / distance)
                val ratio = 1f - 0.5 * (pow(cos.toDouble(), 8.0))
                val magnitudeX = centerX - (centerX - x) * (distance - magnitude) / distance
                view.translationX = ((magnitudeX - x) * ratio).toFloat()
                val magnitudeY = centerY - (centerY - y) * (distance - magnitude) / distance
                view.translationY = ((magnitudeY - y) * ratio).toFloat()
            }
        }
    }

    fun calculateDistance(x: Int, y: Int): Float {
        return sqrt(
            StrictMath.abs(x - centerX).toDouble().pow(2.0) + StrictMath.abs(y - centerY).toDouble()
                .pow(2.0)
        ).toFloat()
    }

    override fun drawChild(canvas: Canvas?, child: View, drawingTime: Long): Boolean {
        val viewHolder = getChildViewHolder(child)
        if (child.scaleX == 0f) {
            if (child is IViewDrawListener) {
                child.notifyDrawStatus(false)
            }
            return false
        }
        if (child is IViewDrawListener) {
            child.notifyDrawStatus(true)
        }
        return super.drawChild(canvas, child, drawingTime)
    }


    override fun scrollBy(x: Int, y: Int) {
        super.scrollBy(x, y)
    }

    interface OnLayoutListener {
        fun onCenter(point: Point)
    }

    interface OnDrawListener {
        fun onDraw(canvas: Canvas)
    }
}