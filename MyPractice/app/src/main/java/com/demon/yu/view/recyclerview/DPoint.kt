package com.demon.yu.view.recyclerview

import android.graphics.Point
import android.graphics.Rect
import android.graphics.RectF
import android.view.View

data class DPoint(var x: Double, var y: Double)

fun Point.toDPoint(): DPoint {
    return DPoint(x.toDouble(), y.toDouble())
}

fun Point.copy(): Point {
    return Point(this)
}

fun DPoint.toPoint(): Point {
    return Point(x.toInt(), y.toInt())
}

fun Rect.toRectF(): RectF {
    return RectF(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
}

fun View.getCenterX(): Int {
    if (width == 0) {
        return left
    }
    return left + width / 2
}

fun View.getCenterPoint(): Point {
    return Point(getCenterX(), getCenterY())
}

fun View.getCenterY(): Int {
    if (height == 0) {
        return top
    }
    return top + height / 2
}