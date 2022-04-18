package com.demon.yu.view.recyclerview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.recyclerview.widget.RecyclerView
import com.demon.yu.extenstion.dp2Px
import java.lang.StrictMath.abs
import kotlin.math.pow
import kotlin.math.sqrt

class MyCustomize2RecyclerView(context: Context, attr: AttributeSet? = null) :
    RecyclerView(context, attr) {


    private var maxScaleSize = 124f / 60f

    private var secondScaleSize = 85f / 60f

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG.or(Paint.DITHER_FLAG))


    var centerX: Int = 0
    var centerY: Int = 0


    //    private var edgeCircleRadius = 240.dp2Px()
    private var innerCircleRadius = 20.dp2Px()


    private var secondScaleDistance = 80.dp2Px()

    private var radius = 120.dp2Px()
    private var radiusDouble = radius * 2
    private var dismiss2NormalScaleDistance = 30.dp2Px().toFloat()
    private var NormalScaleDistance = 30.dp2Px().toFloat()


    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        super.onMeasure(widthSpec, heightSpec)
        centerX = measuredWidth / 2
        centerY = measuredHeight / 2
    }


    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
        paint.color = Color.RED
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f.dp2Px().toFloat()
        canvas?.drawCircle(centerX.toFloat(), centerY.toFloat(), radiusDouble.toFloat(), paint)
        paint.color = Color.BLUE
        canvas?.drawCircle(centerX.toFloat(), centerY.toFloat(), innerCircleRadius.toFloat(), paint)
        canvas?.drawCircle(centerX.toFloat(), centerY.toFloat(), 240.dp2Px().toFloat(), paint)
    }

    private val decelerateInterpolator = DecelerateInterpolator()
    private val accelerateInterpolator = AccelerateInterpolator()


    fun getDistance(x: Int, y: Int): Float {
        return calculateDistance(x, y)
    }

    fun scrollToCenter(x: Int, y: Int) {

        smoothScrollBy(x - centerX, y - centerY)
    }

    fun getScaleSize(x: Int, y: Int): Float {
        val distance = calculateDistance(x, y)
        when {
            distance <= innerCircleRadius -> {
                return maxScaleSize
            }
            distance > innerCircleRadius && distance <= radius -> {
                val scale =
                    (radius - distance) / (radius - innerCircleRadius) * (maxScaleSize - secondScaleSize)
                return scale + secondScaleSize
            }
            distance >= radius && distance < radius + secondScaleDistance -> {
                return secondScaleSize
            }
            distance >= radius + secondScaleDistance && distance < radiusDouble -> {
                val scale =
                    (radiusDouble - distance) / (radiusDouble - radius - secondScaleDistance)
                return 1 + decelerateInterpolator.getInterpolation(scale) * (secondScaleSize - 1)
//                return 1 + scale
            }
            distance >= radiusDouble && distance < radiusDouble + NormalScaleDistance -> {
                return 1f
            }

            distance <= radiusDouble + NormalScaleDistance + dismiss2NormalScaleDistance && distance >= radiusDouble + NormalScaleDistance -> {
                return (dismiss2NormalScaleDistance + radiusDouble + NormalScaleDistance - distance) / dismiss2NormalScaleDistance
            }

            distance >= dismiss2NormalScaleDistance + radiusDouble + NormalScaleDistance -> {
                return 0f
            }
            else -> {
                return 0f
            }
        }
    }

    private fun calculateDistance(x: Int, y: Int): Float {
        return sqrt(
            abs(x - centerX).toDouble().pow(2.0) + abs(y - centerY).toDouble().pow(2.0)
        ).toFloat()
    }


}