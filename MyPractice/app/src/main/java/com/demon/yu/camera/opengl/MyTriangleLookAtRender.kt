package com.demon.yu.camera.opengl

import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * 可以进行调节的三角形
 */
class MyBaseTriangleLookAtRender : GLSurfaceView.Renderer {
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
        // Create a rotation transformation for the triangle
//            val time = SystemClock.uptimeMillis() % 4000L
//            val angle = 0.090f * time.toInt()
//            Matrix.setRotateM(viewMatrix, 0, angle, 0f, 0f, -1.0f)
        // Combine the projection and camera view matrices
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        triangle?.draw(vPMatrix)
    }

}