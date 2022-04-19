package com.demon.yu.view.recyclerview

import android.app.Service
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Vibrator
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.recyclerview.widget.RecyclerView
import com.demon.yu.extenstion.dp2Px
import java.lang.StrictMath.abs
import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.sin
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
    private var dismiss2NormalScaleDistance = 60.dp2Px().toFloat()
    private var radiusDouble = radius * 2

    private var NormalScaleDistance = 30.dp2Px().toFloat()


    private val centerRegionWidth = radius * 2
    private val centerRegionHeight = (sin(PI / 3) * radius * 2).toInt()
    private var centerRegionRect = Rect()
    private var cornerRadius = 50.dp2Px()

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

    }


    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
        paint.color = Color.RED
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f.dp2Px().toFloat()
        canvas?.drawCircle(centerX.toFloat(), centerY.toFloat(), radiusDouble.toFloat(), paint)
        paint.color = Color.BLUE
        canvas?.drawCircle(centerX.toFloat(), centerY.toFloat(), radius.toFloat(), paint)
        canvas?.drawRoundRect(
            centerRegionRect.toRectF(),
            cornerRadius.toFloat(),
            cornerRadius.toFloat(),
            paint
        )
    }

    private val decelerateInterpolator = DecelerateInterpolator(1.2f)
    private val accelerateInterpolator = AccelerateInterpolator()


    fun getDistance(x: Int, y: Int): Float {
        return calculateDistance(x, y)
    }

    fun scrollToCenter(x: Int, y: Int) {
        val vib = context.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
        vib.vibrate(longArrayOf(100, 200), -1)
        smoothScrollBy(x - centerX, y - centerY)
    }
    /**
     * 不分段实现版本
     */
//    fun getScaleSize(x: Int, y: Int): Float {
//        val distance = calculateDistance(x, y)
//        when {
//            distance <= innerCircleRadius -> {
//                return maxScaleSize
//            }
//            distance > innerCircleRadius && distance <= radius -> {
//                val scale =
//                    (radius - distance) / (radius - innerCircleRadius) * (maxScaleSize - secondScaleSize)
//                return secondScaleSize + scale
//            }
//            distance >= radius && distance < radiusDouble -> {
//                val scale =
//                    (radiusDouble - distance) / (radiusDouble - radius) * (secondScaleSize - 1)
//                return 1 + scale
//            }
//            distance >= radiusDouble  && distance <= dismiss2NormalScaleDistance + radiusDouble  -> {
//                return (dismiss2NormalScaleDistance + radiusDouble - distance) / dismiss2NormalScaleDistance
//            }
//            distance >= radiusDouble+dismiss2NormalScaleDistance -> {
//                return 0f
//            }
//            else -> {
//
//                return 1f
//            }
//        }
//    }

    /**
     * 分段缩放的实现版本
     */
//    fun getScaleSize(x: Int, y: Int): Float {
//        val distance = calculateDistance(x, y)
//        when {
//            distance <= innerCircleRadius -> {
//                return maxScaleSize
//            }
//            distance > innerCircleRadius && distance <= radius -> {
//                val scale =
//                    (radius - distance) / (radius - innerCircleRadius) * (maxScaleSize - secondScaleSize)
//                return scale + secondScaleSize
//            }
//            distance >= radius && distance < radius + secondScaleDistance -> {
//                return secondScaleSize
//            }
//            distance >= radius + secondScaleDistance && distance < radiusDouble -> {
//                val scale =
//                    (radiusDouble - distance) / (radiusDouble - radius - secondScaleDistance)
//                return 1 + decelerateInterpolator.getInterpolation(scale) * (secondScaleSize - 1)
////                return 1 + scale
//            }
//            distance >= radiusDouble && distance < radiusDouble + NormalScaleDistance -> {
//                return 1f
//            }
//
//            distance <= radiusDouble + NormalScaleDistance + dismiss2NormalScaleDistance && distance >= radiusDouble + NormalScaleDistance -> {
//                return (dismiss2NormalScaleDistance + radiusDouble + NormalScaleDistance - distance) / dismiss2NormalScaleDistance
//            }
//
//            distance >= dismiss2NormalScaleDistance + radiusDouble + NormalScaleDistance -> {
//                return 0f
//            }
//            else -> {
//                return 0f
//            }
//        }
//    }

//    /**
//     * 分段缩放的实现版本，不要innerCircleRadius，不要NormalScaleDistance
//     */
//    fun getScaleSize(x: Int, y: Int): Float {
//        val distance = calculateDistance(x, y)
//        when {
////            distance <= innerCircleRadius -> {
////                return maxScaleSize
////            }
//            distance >= 0 && distance <= radius -> {
//                val scale =
//                    (radius - distance) / (radius) * (maxScaleSize - secondScaleSize)
//                return scale + secondScaleSize
//            }
//            distance >= radius && distance < radius + secondScaleDistance -> {
//                return secondScaleSize
//            }
//            distance >= radius + secondScaleDistance && distance < radiusDouble -> {
//                val scale =
//                    (radiusDouble - distance) / (radiusDouble - radius - secondScaleDistance)
//                return 1 + decelerateInterpolator.getInterpolation(scale) * (secondScaleSize - 1)
//            }
//
//            distance <= radiusDouble + dismiss2NormalScaleDistance && distance >= radiusDouble -> {
//                return (dismiss2NormalScaleDistance + radiusDouble - distance) / dismiss2NormalScaleDistance
//            }
//
//            else -> {
//                return 0f
//            }
//        }
//    }
    /**
     * 分段缩放的实现版本，不要innerCircleRadius，NormalScaleDistance，secondScaleSize
     */
    fun getScaleSize(x: Int, y: Int): Float {
        val distance = calculateDistance(x, y)
        when {
//            distance <= innerCircleRadius -> {
//                return maxScaleSize
//            }
            distance >= 0 && distance <= radius -> {
                val scale =
                    (radius - distance) / (radius) * (maxScaleSize - secondScaleSize)
                return scale + secondScaleSize
            }
            distance >= radius && distance < radiusDouble -> {
                val scale =
                    (radiusDouble - distance) / (radius)
                return 1 + decelerateInterpolator.getInterpolation(scale) * (secondScaleSize - 1)
            }

            distance <= radiusDouble + dismiss2NormalScaleDistance && distance >= radiusDouble -> {
                return (dismiss2NormalScaleDistance + radiusDouble - distance) / dismiss2NormalScaleDistance
            }

            else -> {
                return 0f
            }
        }
    }

    fun translateXY(view: View, x: Int, y: Int, log: Boolean) {
        val distance = calculateDistance(x, y)
        if (distance <= radius) {
            view.translationX = 0f
            view.translationY = 0f
            return
        }
        when {
//            x >= centerRegionRect.left + cornerRadius && x <= centerRegionRect.right - cornerRadius -> {
//
//            }
//            y >= centerRegionRect.top + cornerRadius && y <= centerRegionRect.bottom - cornerRadius -> {
//
//            }
            else -> {

                val magnitude = (distance - radius) / radius * 124f
                val ratio = magnitude / distance
                val sin =
                    abs((centerX - x) / distance) * 1.2f + 0.2f
//                val sin =accelerateInterpolator.getInterpolation((centerY - y) / distance * 0.5f)
                view.translationX = (centerX - x) * ratio
//                view.translationY = (centerY - y) * ratio * (0.5f + sin)
                view.translationY = (centerY - y) * ratio * sin
                if (log) {
                    Log.d(
                        "yujintao2",
                        "ratio =${ratio},sin=${sin},translationY=${view.translationY}"
                    )
                }
            }
        }
    }

    private fun calculateDistance(x: Int, y: Int): Float {
        return sqrt(
            abs(x - centerX).toDouble().pow(2.0) + abs(y - centerY).toDouble().pow(2.0)
        ).toFloat()
    }


}