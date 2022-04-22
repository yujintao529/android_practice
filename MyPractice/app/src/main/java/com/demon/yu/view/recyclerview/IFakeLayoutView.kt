package com.demon.yu.view.recyclerview

import android.graphics.Point

interface IFakeLayoutView {
    fun getFakeHeight(): Int
    fun getFakeWidth(): Int
    fun getFakeTop(): Int
    fun getFakeLeft(): Int
    fun getCenterPoint(): Point
    fun getFakePivotX(): Int
    fun getFakePivotY(): Int
}