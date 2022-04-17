package com.demon.yu.view.recyclerview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.animation.DecelerateInterpolator
import androidx.recyclerview.widget.RecyclerView
import com.demon.yu.extenstion.dp2Px
import java.lang.StrictMath.abs
import kotlin.math.pow
import kotlin.math.sqrt

class MyCustomizeCircleRecyclerView(context: Context, attr: AttributeSet? = null) :
    RecyclerView(context, attr) {


    private var maxScaleSize = 2f


    private val paint = Paint(Paint.ANTI_ALIAS_FLAG.or(Paint.DITHER_FLAG))


    var centerX: Int = 0
    var centerY: Int = 0


    private var outEdgeCircleRadius = 180.dp2Px()
    private var edgeCircleRadius = 160.dp2Px()
    private var innerCircleRadius = 20.dp2Px()

    private var radiusDouble = 240.dp2Px()

    private var dismiss2NormalScaleDistance = 30.dp2Px().toFloat()


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
        canvas?.drawCircle(centerX.toFloat(), centerY.toFloat(), edgeCircleRadius.toFloat(), paint)
        paint.color = Color.BLUE
        canvas?.drawCircle(centerX.toFloat(), centerY.toFloat(), innerCircleRadius.toFloat(), paint)
        canvas?.drawCircle(centerX.toFloat(), centerY.toFloat(), 240.dp2Px().toFloat(), paint)
    }

    private val decelerateInterpolator = DecelerateInterpolator()

    fun getScaleSize(x: Int, y: Int): Float {
        val distance = calculateDistance(x, y)
        when {
            distance <= innerCircleRadius -> {
                return maxScaleSize
            }
            distance <= edgeCircleRadius && distance > innerCircleRadius -> {
                val scale =
                    (edgeCircleRadius - distance) / (edgeCircleRadius - innerCircleRadius) * (maxScaleSize - 1)
                return 1 + decelerateInterpolator.getInterpolation(scale)
            }
            distance > dismiss2NormalScaleDistance + radiusDouble -> {
                return 0f
            }
            distance <= dismiss2NormalScaleDistance + radiusDouble && distance >= radiusDouble -> {
                return (dismiss2NormalScaleDistance + radiusDouble - distance) / dismiss2NormalScaleDistance
            }
            else -> {

                return 1f
            }
        }
    }

    private fun calculateDistance(x: Int, y: Int): Float {
        return sqrt(
            abs(x - centerX).toDouble().pow(2.0) + abs(y - centerY).toDouble().pow(2.0)
        ).toFloat()
    }


}