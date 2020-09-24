package com.demon.yu.na

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import com.demon.yu.lib.gaussian.GaussianHelper
import com.example.mypractice.R
import kotlinx.android.synthetic.main.activity_gaussian.*


class GaussianActivity : Activity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        getWindow().setBackgroundDrawable(null);
        setContentView(R.layout.activity_gaussian)
        originBitmap.setImageResource(R.drawable.lol_jinkesi)
        val bitmap=BitmapFactory.decodeResource(resources,R.drawable.lol_jinkesi)
        blur.setOnClickListener {
            GaussianHelper.blur(bitmap)
            destBitmap.setImageBitmap(bitmap)
        }
        testJni.setOnClickListener {
            GaussianHelper.createStudent()
        }
    }

    fun drawableToBitmap(drawable: Drawable): Bitmap? {
        var bitmap: Bitmap? = null
        if (drawable is BitmapDrawable) {
            val bitmapDrawable = drawable
            if (bitmapDrawable.bitmap != null) {
                return bitmapDrawable.bitmap
            }
        }
        bitmap = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
            Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888) // Single color bitmap will be created of 1x1 pixel
        } else {
            Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        }
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight())
        drawable.draw(canvas)
        return bitmap
    }
}