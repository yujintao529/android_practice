package com.demon.yu.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.view.Surface
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream

object BitmapUtils {
    fun loadFileToBitmap(context: Context, filePath: String): Bitmap {
        return BitmapFactory.decodeFile(filePath)
    }

    /**
     * 正确旋转bitmap的方式
     */
    fun rotate(src: Bitmap, rotate: Int): Bitmap {
        if (rotate % 360 == Surface.ROTATION_0) {
            return src
        }
        var destWidth = src.width
        var destHeight = src.height
        val matrix = Matrix()
        matrix.setRotate(rotate.toFloat())
        return Bitmap.createBitmap(src, 0, 0, destWidth, destHeight, matrix, true)
    }


    fun rotate2(src: Bitmap, rotate: Int): Bitmap {
        if (rotate % 360 == Surface.ROTATION_0) {
            return src
        }
        var destWidth = src.width
        var destHeight = src.height
        val matrix = Matrix()
        matrix.setRotate(90f, destWidth / 2f, destHeight / 2f)
        val newBitmap = Bitmap.createBitmap(destHeight, destWidth, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(newBitmap)
        canvas.drawBitmap(src, matrix, null)
        return newBitmap
    }

    fun saveBitmapToFile(bitmap: Bitmap, filePath: String, compressFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
                         quality: Int = 100): Boolean {
        val file = File(filePath)
        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }

        }
        var bufferedOutputStream: BufferedOutputStream? = null
        try {
            bufferedOutputStream = BufferedOutputStream(FileOutputStream(file))
            bitmap.compress(compressFormat, quality, bufferedOutputStream)
            bufferedOutputStream.flush()
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        } finally {
            try {
                bufferedOutputStream?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        return true
    }


    fun bytes2Bitmap(bytes: ByteArray): Bitmap? {
        return if (bytes.isNotEmpty()) {
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } else {
            null
        }
    }
}