package com.demon.yu.view.recyclerview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.demon.yu.extenstion.dp2Px
import java.lang.Math.pow
import kotlin.math.sqrt

class MyCustomizeRecyclerView(context: Context, attr: AttributeSet? = null) :
    RecyclerView(context, attr) {


    private val centerRegionWidth = 160.dp2Px()
    private val centerRegionHeight = 160.dp2Px()
    var centerRegionXRadius = 80.dp2Px()
    var centerRegionYRadius = 80.dp2Px()
    var cornerRadius = 40.dp2Px()
    val centerRegionRect = Rect()
    private var centerRegionRectF = RectF()
    val centerRegionColor = Color.parseColor("#6642403F")

    private var maxScaleCircleRadius = 30.dp2Px()
    private val maxScaleCircleRegionColor = Color.BLUE

    var interRect = Rect() //centerRegion  - cornerRadius 的矩形区域

    var centerX: Int = 0
    var centerY: Int = 0


    private val edgeRegionWidth = 260.dp2Px()
    private val edgeRegionHeight = 260.dp2Px()

    private val fringeWidth = (edgeRegionWidth - centerRegionWidth) / 2f

    private val edgeRegionColor = Color.RED
    val edgeRegionRect = Rect()
    var edgeRegionRectF = RectF()
    private var secondScaleSize = 1.6f
    private var maxScaleSize = 2.4f
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG.or(Paint.DITHER_FLAG))
    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        super.onMeasure(widthSpec, heightSpec)
        centerRegionRect.set(
            (measuredWidth - centerRegionWidth) / 2,
            (measuredHeight - centerRegionHeight) / 2,
            (measuredWidth - centerRegionWidth) / 2 + centerRegionWidth,
            (measuredHeight - centerRegionHeight) / 2 + centerRegionHeight
        )
        centerRegionRectF = centerRegionRect.toRectF()
        edgeRegionRect.set(
            (measuredWidth - edgeRegionWidth) / 2,
            (measuredHeight - edgeRegionHeight) / 2,
            (measuredWidth - edgeRegionWidth) / 2 + edgeRegionWidth,
            (measuredHeight - edgeRegionHeight) / 2 + edgeRegionHeight
        )
        edgeRegionRectF = edgeRegionRect.toRectF()
        centerX = measuredWidth / 2
        centerY = measuredHeight / 2
        interRect.set(
            centerRegionRect.left + cornerRadius,
            centerRegionRect.top + cornerRadius,
            centerRegionRect.right - cornerRadius,
            centerRegionRect.bottom - cornerRadius
        )

        Log.d("yujintao", "interRect = ${interRect},centerRegionRect=${centerRegionRect}")

    }


    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
        //debug
        paint.style = Paint.Style.FILL
        paint.color = centerRegionColor
        canvas?.drawRoundRect(
            centerRegionRectF,
            cornerRadius.toFloat(),
            cornerRadius.toFloat(),
            paint
        )
        paint.color = edgeRegionColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f.dp2Px().toFloat()
        canvas?.drawRoundRect(
            edgeRegionRectF,
            (cornerRadius + fringeWidth).toFloat(), (cornerRadius + fringeWidth).toFloat(), paint
        )
        paint.color = maxScaleCircleRegionColor
        canvas?.drawCircle(
            centerX.toFloat(),
            centerY.toFloat(),
            maxScaleCircleRadius.toFloat(),
            paint
        )
    }

    /**
     * 判断是否在去掉corner radius的矩形区域，
     * 这个区域必然是最大scale的效果
     */
    fun isInCenterRoundRegion(x: Int, y: Int): Boolean {
        return interRect.contains(x, y)
    }

    /**
     * 是否处于edgeRegion外面的区域，是正常的效果
     */
    fun isOutEdgeRoundRegion(x: Int, y: Int): Boolean {
        return edgeRegionRect.contains(x, y).not()
    }


    /**
     *
     */
    fun getScaleSize(x: Int, y: Int): Float {
        when {
            isInCenterRoundRegion(x, y) -> {
                return 1 * secondScaleSize
            }
            isOutEdgeRoundRegion(x, y) -> {
                return 1f
            }
            //先找线性计算的地方，好计算
            y >= interRect.top && y <= interRect.bottom -> {
                if (x <= interRect.left && x >= centerRegionRect.left) {
                    return 1 * secondScaleSize
                } else if (x < centerRegionRect.left && x > edgeRegionRect.left) {
                    return 1 + ((x - edgeRegionRect.left) / fringeWidth) * (secondScaleSize - 1)
                } else if (x >= interRect.right && x <= centerRegionRect.right) {
                    return 1 * secondScaleSize
                } else if (x > centerRegionRect.right && x < edgeRegionRect.right) {
                    return 1 + ((edgeRegionRect.right - x) / fringeWidth) * (secondScaleSize - 1)
                } else if (x >= interRect.left && x <= interRect.right) {
                    return 1 * secondScaleSize
                } else {
                    return 1f
                }
            }
            x >= interRect.left && x <= interRect.right -> {
                if (y <= interRect.top && y >= centerRegionRect.top) {
                    return 1 * secondScaleSize
                } else if (y <= centerRegionRect.top && y >= edgeRegionRect.top) {
                    return 1 + ((y - edgeRegionRect.top) / fringeWidth) * (secondScaleSize - 1)
                } else if (y >= interRect.bottom && y <= centerRegionRect.bottom) {
                    return 1 * secondScaleSize
                } else if (y >= centerRegionRect.bottom && y <= edgeRegionRect.bottom) {
                    return 1 + ((edgeRegionRect.bottom - y) / fringeWidth) * (secondScaleSize - 1)
                } else if (y >= interRect.top && y <= interRect.bottom) {
                    return 1 * secondScaleSize
                } else {
                    return 1f
                }
            }
            x <= interRect.left && y <= interRect.top -> {
                val distance = sqrt(
                    pow(
                        (interRect.left - x).toDouble(),
                        2.0
                    ) + pow((interRect.top - y).toDouble(), 2.0)
                ).toInt()
                if (distance <= cornerRadius) {
                    return secondScaleSize
                } else if (distance > cornerRadius && distance <= cornerRadius + fringeWidth) {
                    return 1 + ((cornerRadius + fringeWidth - distance) / fringeWidth) * (secondScaleSize - 1)
                } else {
                    return 1f
                }
            }
            x <= interRect.left && y >= interRect.bottom -> {
                val distance = sqrt(
                    pow(
                        (interRect.left - x).toDouble(),
                        2.0
                    ) + pow((y - interRect.bottom).toDouble(), 2.0)
                ).toInt()
                if (distance <= cornerRadius) {
                    return secondScaleSize
                } else if (distance > cornerRadius && distance <= cornerRadius + fringeWidth) {
                    return 1 + ((cornerRadius + fringeWidth - distance) / fringeWidth) * (secondScaleSize - 1)
                } else {
                    return 1f
                }
            }
            x >= interRect.right && y <= interRect.top -> {
                val distance = sqrt(
                    pow(
                        (x - interRect.right).toDouble(),
                        2.0
                    ) + pow((interRect.top - y).toDouble(), 2.0)
                ).toInt()
                if (distance <= cornerRadius) {
                    return secondScaleSize
                } else if (distance > cornerRadius && distance <= cornerRadius + fringeWidth) {
                    return 1 + ((cornerRadius + fringeWidth - distance) / fringeWidth) * (secondScaleSize - 1)
                } else {
                    return 1f
                }
            }

            x >= interRect.right && y >= interRect.bottom -> {
                val distance = sqrt(
                    pow(
                        (x - interRect.right).toDouble(),
                        2.0
                    ) + pow((y - interRect.bottom).toDouble(), 2.0)
                ).toInt()
                if (distance <= cornerRadius) {
                    return secondScaleSize
                } else if (distance > cornerRadius && distance <= cornerRadius + fringeWidth) {
                    return 1 + ((cornerRadius + fringeWidth - distance) / fringeWidth) * (secondScaleSize - 1)
                } else {
                    return 1f
                }
            }

            else -> {
                return 1f
            }
        }
    }


}