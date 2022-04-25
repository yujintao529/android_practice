package com.demon.yu.view.recyclerview

import android.graphics.Point
import android.view.View

object FakeLayoutCoorExchangeUtils {


    fun shiftingLayout(
        view: View,
        positionCenterPoint: Point,
        block: (left: Int, top: Int) -> Unit
    ) {
        if (view is IFakeLayoutView) {
            val left = positionCenterPoint.x - view.measuredWidth / 2
            val top = positionCenterPoint.y - view.measuredHeight / 2
            block(left - view.getFakeLeft(), top - view.getFakeTop())
        } else {
            block.invoke(
                positionCenterPoint.x - view.measuredWidth / 2,
                positionCenterPoint.y - view.measuredHeight / 2
            )
        }
    }

    fun setCenterPivot(view: View) {
        if (view is IFakeLayoutView) {
            view.pivotX = (view.getFakeLeft() + view.getFakeWidth() / 2).toFloat()
            view.pivotY = (view.getFakeTop() + view.getFakeHeight() / 2).toFloat()
        } else {
            view.pivotX = (view.width / 2).toFloat()
            view.pivotY = (view.height / 2).toFloat()
        }
    }


    fun getFakeWidth(view: IFakeLayoutView): Int {
        return view.getFakeWidth()
    }

    fun getFakeHeight(view: IFakeLayoutView): Int {
        return view.getFakeHeight()
    }


    fun getCenterPoint(
        view: View,
    ): Point {
        if (view is IFakeLayoutView) {
            return view.getCenterPoint()
        } else {
            return view.getCenterPoint()
        }
    }
}