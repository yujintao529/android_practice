package com.demo.yu.test.kotlin

import android.graphics.Matrix
import android.graphics.RectF
import org.junit.Test

/**
 * @description
 * @author yujinta.529
 * @create 2022-09-28
 */
class BaseTester {
    @Test
    fun testMatrix() {
        val matrix = Matrix()
        matrix.setTranslate(100f, 100f);
        val src = RectF(0f, 0f, 100f, 100f);
        val dest = RectF()
        matrix.mapRect(src, dest)//dest(100f,100f,200f,200f)
    }
}