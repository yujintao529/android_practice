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
 * 2. 可以观看3d三角形的界面。
 *
 * 透视投影变换：https://blog.csdn.net/popy007/article/details/1797121
 *

 * opengl坐标系：
 *
 * 应用投影和相机矩阵的流程及含义
 * * 几个关键函数
 * * 1. gl.frustumM  gl.setLookAtM
 *
 *
 *
 *
 */
class MyBaseGLSurface(context: Context) : GLSurfaceView(context) {

    val myRenderer: MyBaseTriangleLookAtRender

    init {
        myRenderer = MyBaseTriangleLookAtRender()
        setEGLContextClientVersion(3)
        setRenderer(myRenderer)
    }


    fun setLookAtX(x: Float) {
        myRenderer.lookAtX = x
    }

    fun setLookAtY(y: Float) {
        myRenderer.lookAtY = y
    }

    fun setLookAtZ(z: Float) {
        myRenderer.lookAtZ = z
    }


    /**
     * 一个正面的三角形
     */
    private inner class MyBaseTriangleRender : Renderer {
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
            /**
             * x，y 以像素为单位，指定了视口的左下角位置。
             * width，height 表示这个视口矩形的宽度和高度，根据窗口的实时变化重绘窗口。
             * 在默认情况下，视口被设置为占据打开窗口的整个像素矩形，窗口大小和设置视口大小相同，所以为了选择一个更小的绘图区域，就可以用glViewport函数来实现这一变换，在窗口中定义一个像素矩形，最终将图像映射到这个矩形中。例如可以对窗口区域进行划分，在同一个窗口中显示分割屏幕的效果，以显示多个视图。
             */
            GLES30.glViewport(0, 0, width, height)
            // create a projection matrix from device screen geometry
            Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 10f) //透视函数


        }

        override fun onDrawFrame(gl: GL10?) {
            GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)//根据指定的指执行清楚操作
            // Set the camera position (View matrix)
            Matrix.setLookAtM(viewMatrix, 0, 0.0f, 0f, 7f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)
            // Combine the projection and camera view matrices
            Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
            triangle?.draw(vPMatrix)
        }

    }

    inner class MyBaseTriangleLookAtRender : Renderer {
        private var triangle: Triangle? = null
        private val vPMatrix = FloatArray(16)
        private val projectionMatrix = FloatArray(16)
        private val viewMatrix = FloatArray(16)

        @Volatile
        var lookAtX: Float = 0f

        @Volatile
        var lookAtY: Float = 1f

        @Volatile
        var lookAtZ: Float = 0f

        @Volatile
        var lookAtEyeX: Float = 0f

        @Volatile
        var lookAtEyeY: Float = 0f

        @Volatile
        var lookAtEyeZ: Float = 0f


        override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
            GLES30.glClearColor(1f, 1f, 1f, 0.0f) //指定用来清楚缓冲区的值
            triangle = Triangle()

        }

        override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
            val ratio: Float = width.toFloat() / height.toFloat()
            /**
             * x，y 以像素为单位，指定了视口的左下角位置。
             * width，height 表示这个视口矩形的宽度和高度，根据窗口的实时变化重绘窗口。
             * 在默认情况下，视口被设置为占据打开窗口的整个像素矩形，窗口大小和设置视口大小相同，所以为了选择一个更小的绘图区域，就可以用glViewport函数来实现这一变换，在窗口中定义一个像素矩形，最终将图像映射到这个矩形中。例如可以对窗口区域进行划分，在同一个窗口中显示分割屏幕的效果，以显示多个视图。
             */
            GLES30.glViewport(0, 0, width, height)
            // create a projection matrix from device screen geometry
            Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 10f) //透视函数


        }

        override fun onDrawFrame(gl: GL10?) {
            GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)//根据指定的指执行清楚操作
            // Set the camera position (View matrix)
            Matrix.setLookAtM(viewMatrix, 0, lookAtEyeX, lookAtEyeY, lookAtEyeZ, 0f, 0f, 0f, lookAtX, lookAtY, lookAtZ)
            // Combine the projection and camera view matrices
            Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
            triangle?.draw(vPMatrix)
        }

    }

    /**
     *  根据版本判定选择egl的版本
     */
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