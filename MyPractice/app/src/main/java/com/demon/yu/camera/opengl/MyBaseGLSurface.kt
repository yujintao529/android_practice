package com.demon.yu.camera.opengl

import android.content.Context
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.example.mypractice.Logger
import com.facebook.stetho.common.LogUtil
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.egl.EGLContext
import javax.microedition.khronos.egl.EGLDisplay
import javax.microedition.khronos.opengles.GL10


/**
 * note:
 * 1. 选择opengles的版本，可以通过setEGLContextClientVersion选择，也可以自定义MyContextFactory选择。
 * 2. 一定进行opengles的版本选择，否则会出现很多莫名其妙的问题。我这里直接设置setEGLContextClientVersion3
 * 3. opengl坐标系，是将屏幕（无论什么尺寸）都归一化为[-1,1][1,1][-1,-1],[1,-1]的坐标系里。所以为了将图像能够正常现实到屏幕中需要进行投影和相机视图的转变
 * task:
 * 1. 横竖屏都是等边三角形
 *
 *
 * opengl坐标系：
 *
 * 应用投影和相机矩阵的流程及含义
 * 1.
 */
class MyBaseGLSurface(context: Context) : GLSurfaceView(context) {

    private val myRenderer: MyBaseRender

    init {
        myRenderer = MyBaseRender()
        setEGLContextClientVersion(3)
        setRenderer(myRenderer)
    }


    private inner class MyBaseRender : Renderer {
        private var triangle: Triangle? = null
        private val vPMatrix = FloatArray(16)
        private val projectionMatrix = FloatArray(16)
        private val viewMatrix = FloatArray(16)

        override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
            GLES30.glClearColor(1f, 1f, 1f, 0.0f) //指定用来清楚缓冲区的值
            triangle = Triangle()

        }

        override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
            val ratio: Float = width.toFloat() / height.toFloat()
            GLES30.glViewport(0, 0, width, height)
            // create a projection matrix from device screen geometry
            Matrix.perspectiveM()
            Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f) //透视函数


        }

        override fun onDrawFrame(gl: GL10?) {
            GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)//根据指定的指执行清楚操作
            // Set the camera position (View matrix)
            Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 3f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)
            // Combine the projection and camera view matrices
            Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
            triangle?.draw(vPMatrix)
        }

    }

    private class MyContextFactory(listener: EGLBaseListener<Int>) : EGLContextFactory {
        private val listener: EGLBaseListener<Int> = listener

        override fun createContext(egl: EGL10, display: EGLDisplay, eglConfig: EGLConfig?): EGLContext? {
            var context: EGLContext? = null
            var version: Int = 1
            try {
                context = egl.eglCreateContext(display, eglConfig, EGL10.EGL_NO_CONTEXT, intArrayOf(EGL_CONTEXT_CLIENT_VERSION, 3, EGL10.EGL_NONE))
            } catch (ex: Exception) {
                Logger.error("ContextFactory", "3 eglContext error", ex)
            }
            if (context == null || context === EGL10.EGL_NO_CONTEXT) {
                LogUtil.d("un support OpenGL ES 3.0 ")
                try {
                    context = egl.eglCreateContext(display, eglConfig, EGL10.EGL_NO_CONTEXT, intArrayOf(EGL_CONTEXT_CLIENT_VERSION, 2, EGL10.EGL_NONE))
                } catch (ex: Exception) {
                    Logger.error("ContextFactory", "2 eglContext error", ex)
                }
            } else {
                version = 3
            }
            if (context == null || context === EGL10.EGL_NO_CONTEXT) {
                LogUtil.d("un support OpenGL ES 2.0 ")
            } else {
                version = 2
            }
            if (listener != null) {
                listener.onFinish(version)
            }
            return context
        }

        override fun destroyContext(egl: EGL10, display: EGLDisplay?, context: EGLContext?) {
            if (!egl.eglDestroyContext(display, context)) {
                LogUtil.d("destroyContext false")
            }
        }

        companion object {
            private const val EGL_CONTEXT_CLIENT_VERSION = 0x3098
        }


    }

    private interface EGLBaseListener<T> {
        fun onFinish(version: T)
    }
}