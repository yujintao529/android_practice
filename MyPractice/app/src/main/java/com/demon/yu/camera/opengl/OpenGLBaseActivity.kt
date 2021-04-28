package com.demon.yu.camera.opengl

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class OpenGLBaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(MyBaseGLSurface(this))
    }

}