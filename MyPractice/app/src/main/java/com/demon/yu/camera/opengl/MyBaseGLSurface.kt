package com.demon.yu.camera.opengl

import android.content.Context
import android.opengl.GLES31
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyBaseGLSurface(context: Context) : GLSurfaceView(context) {

    private val myRenderer: MyBaseRender

    init {
        myRenderer = MyBaseRender()
        setRenderer(myRenderer)
    }

    private inner class MyBaseRender : Renderer {
        private var triangle: Triangle? = null
        override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
            triangle = Triangle()
//            GLES31.glClearColor(0.5f, 0.0f, 0.0f, 0.0f)
        }

        override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
            GLES31.glViewport(0, 0, width, height)
        }

        override fun onDrawFrame(gl: GL10?) {
            GLES31.glClear(GLES31.GL_COLOR_BUFFER_BIT)
            triangle?.draw()
        }

    }
}