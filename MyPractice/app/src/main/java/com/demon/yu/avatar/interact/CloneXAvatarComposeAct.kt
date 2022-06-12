package com.demon.yu.avatar.interact

import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.demon.yu.view.recyclerview.MyStaticObj
import com.example.mypractice.R

class CloneXAvatarComposeAct : AppCompatActivity() {
    private var lightInteractView: LightInteractView? = null
    private var circleBg: View? = null
    private var rootView: FrameLayout? = null
    private val lightInteractComponent: LightInteractComponent by lazy {
        LightInteractComponent.getLightInteractComponent(this)
    }

    private var cloneXAvatarComposeLayout: CloneXAvatarComposeLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clonexavator_compose_layout)
        lightInteractView = findViewById(R.id.lightInteractView)
        rootView = findViewById(R.id.rootView)
        circleBg?.visibility = View.GONE
        lightInteractView?.updateList(createInteractModels(), true)
        cloneXAvatarComposeLayout = findViewById(R.id.cloneXAvatarComposeLayout)
        cloneXAvatarComposeLayout?.updateData(createAvatarObj(8))
        cloneXAvatarComposeLayout?.avatarComposeRecyclerView?.onLayoutListener =
            object : AvatarComposeRecyclerView.OnLayoutListener {
                override fun onCenter(point: Point) {
                    lightInteractView?.setInteractPoint(point)
                }
            }
        cloneXAvatarComposeLayout?.onCenterChangeListener =
            object : CloneXAvatarComposeLayout.OnCenterChangeListener {
                override fun onCenter(view: View) {
                    Log.d("CloneXAvatar", "OnCenterChangeListener onCenter")
                }

                override fun onScrolled() {
                    lightInteractView?.backToIdleLock()
                    Log.d("CloneXAvatar", "OnCenterChangeListener onScrolled")
                }
            }
        findViewById<View>(R.id.submit).setOnClickListener {
            val input = findViewById<EditText>(R.id.inputNumber)
            val result = input.editableText.toString().toIntOrNull()
            if (result != null && result > 0) {
                cloneXAvatarComposeLayout?.updateData(createAvatarObj(result))
            }
        }
    }


    private fun createInteractModels(): List<LightInteractModel> {
        val drawableArr =
            arrayListOf(R.drawable.heart, R.drawable.zhuoyizhuo, R.drawable.baba, R.drawable.paozhu)
        return (0..10).map { LightInteractModel(resourceID = drawableArr[it % 4]) }
    }


    private fun createAvatarObj(number: Int): List<MyStaticObj> {
        return (0..number).map { MyStaticObj(viewType = if (it < 50) 1 else 2) }.toList()
    }

}