package com.demon.yu.avatar.interact

import android.graphics.Point
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.demon.yu.extenstion.dp2Px
import com.example.mypractice.R
import com.example.mypractice.common.Common

class CloneXAvatarComposeAct : AppCompatActivity() {
    private var lightInteractView: LightInteractView? = null
    private var circleBg: View? = null
    private var rootView: FrameLayout? = null
    private val lightInteractComponent: LightInteractComponent by lazy {
        LightInteractComponent.getLightInteractComponent(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clonexavator_compose_layout)
        lightInteractView = findViewById(R.id.lightInteractView)
        rootView = findViewById(R.id.rootView)
        circleBg?.visibility = View.GONE
        lightInteractView?.updateList(createInteractModels(), true)
        lightInteractView?.setInteractPoint(
            Point(
                Common.screenWidth / 2,
                Common.screenHeight / 2 - 100.dp2Px()
            )
        )
    }


    private fun createInteractModels(): List<LightInteractModel> {
        val drawableArr =
            arrayListOf(R.drawable.heart, R.drawable.zhuoyizhuo, R.drawable.baba, R.drawable.paozhu)
        return (0..8).map { LightInteractModel(resourceID = drawableArr[it % 4]) }
    }

}