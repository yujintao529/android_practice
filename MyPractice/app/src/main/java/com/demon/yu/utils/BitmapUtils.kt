package com.demon.yu.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.view.Surface

object BitmapUtils {
    fun loadFileToBitmap(context: Context, filePath: String): Bitmap {
    }

    fun rotate(src: Bitmap, rotate: Int): Bitmap {
        if (rotate % 360 == Surface.ROTATION_0) {
            return src
        }
        val destWidth = 0
        val destHeight = 0
        val matrix = Matrix()
        when (rotate) {
            Surface.ROTATION_90 -> {

            }
        }
    }
}