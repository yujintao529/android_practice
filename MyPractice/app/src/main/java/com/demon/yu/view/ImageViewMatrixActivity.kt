package com.demon.yu.view

import android.media.Image
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.mypractice.R

class ImageViewMatrixActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
    }

    private val scaleTypeArr = arrayOf(
            ImageView.ScaleType.CENTER,
            ImageView.ScaleType.MATRIX, ImageView.ScaleType.CENTER_CROP,
            ImageView.ScaleType.CENTER_INSIDE, ImageView.ScaleType.FIT_CENTER,
            ImageView.ScaleType.FIT_END, ImageView.ScaleType.FIT_START, ImageView.ScaleType.FIT_XY
    )

    private var typeIndex = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_imageview_matrix)
        onScaleTypeChanged()

    }

    fun onScaleTypeChanged() {
        val imageView2 = findViewById<ImageView>(R.id.imageView2)
        imageView2.scaleType = scaleTypeArr[typeIndex++ % scaleTypeArr.size]
        val text = findViewById<TextView>(R.id.cateogory)
        text.text = "${imageView2.scaleType.toString()}"
    }

    fun changeScaleType(view: View) {
        onScaleTypeChanged()
    }
}