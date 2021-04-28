package com.demon.yu.camera.opengl

import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.demon.yu.view.CommonSeekView
import com.demon.yu.view.CommonSeekViewContainer
import com.example.mypractice.R
import com.example.mypractice.common.Common

class OpenGLTriLookAtActivity : AppCompatActivity() {


    private var rootView: FrameLayout? = null
    private var commonSeekViewContainer: CommonSeekViewContainer? = null
    private var myBaseGLSurface: MyBaseGLSurface? = null
    private val myBaseTriangleRender by lazy { MyBaseTriangleRender() }
    private val MyBaseTriangleLookAtRender by lazy { MyBaseTriangleLookAtRender() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_opengl_es_base)
        rootView = findViewById(R.id.rootView)
        commonSeekViewContainer = findViewById(R.id.commonSeekViewContainer)
        myBaseGLSurface = MyBaseGLSurface(this).also {
            rootView?.addView(it, 0, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Common.screenHeight / 2))
            it.setRenderer(MyBaseTriangleLookAtRender)
        }
        initLookAtParams()
    }

    private fun initLookAtParams() {
        var seekView = commonSeekViewContainer?.createSeekView()
        seekView?.setLabel("setLookAt-upX")
        seekView?.initValueRange(0f, -1f, 1f)
        seekView?.valueChangedListener = object : CommonSeekView.ValueChangedListener {
            override fun onValueChanged(value: Float) {
                MyBaseTriangleLookAtRender.lookAtX = value
            }
        }
        seekView = commonSeekViewContainer?.createSeekView()
        seekView?.setLabel("setLookAt-upY")
        seekView?.initValueRange(1f, -1f, 1f)
        seekView?.valueChangedListener = object : CommonSeekView.ValueChangedListener {
            override fun onValueChanged(value: Float) {
                MyBaseTriangleLookAtRender.lookAtY = value
            }
        }
        seekView = commonSeekViewContainer?.createSeekView()
        seekView?.setLabel("setLookAt-upZ")
        seekView?.initValueRange(0f, -1f, 1f)
        seekView?.valueChangedListener = object : CommonSeekView.ValueChangedListener {
            override fun onValueChanged(value: Float) {
                MyBaseTriangleLookAtRender.lookAtZ = value
            }
        }
        seekView = commonSeekViewContainer?.createSeekView()
        seekView?.setLabel("setLookAt-eyeX")

        seekView?.valueChangedListener = object : CommonSeekView.ValueChangedListener {
            override fun onValueChanged(value: Float) {
                MyBaseTriangleLookAtRender.lookAtEyeX = value
            }
        }
        seekView?.initValueRange(0f, -5f, 5f)
        seekView = commonSeekViewContainer?.createSeekView()
        seekView?.setLabel("setLookAt-eyeY")

        seekView?.valueChangedListener = object : CommonSeekView.ValueChangedListener {
            override fun onValueChanged(value: Float) {
                MyBaseTriangleLookAtRender.lookAtEyeY = value
            }
        }
        seekView?.initValueRange(0f, -5f, 5f)
        seekView = commonSeekViewContainer?.createSeekView()
        seekView?.setLabel("setLookAt-eyeZ")
        seekView?.valueChangedListener = object : CommonSeekView.ValueChangedListener {
            override fun onValueChanged(value: Float) {
                MyBaseTriangleLookAtRender.lookAtEyeZ = value
            }
        }
        seekView?.initValueRange(6f, 3.1f, 9.9f)

    }


}