package com.demon.yu.camera.opengl

import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 *
 * 1. gl.frustumM(floatArr,offset,left,right,top,bottom,near,far) 透视投影
 * 先是left，right和bottom,top，这4个参数会影响图像左右和上下缩放比，所以往往会设置的值分别-(float) width / height和
 * (float) width / height，top和bottom和top会影响上下缩放比，如果left和right已经设置好缩放，则bottom只需要设置为-1，
 * top设置为1，这样就能保持图像不变形。也可以将left，right 与bottom，top交换比例，即bottom和top设置为 -height/width
 * 和 height/width, left和right设置为-1和1
 * near和far需要结合拍摄相机即观察者眼睛的位置。
 *
 * 2. gl.setLookAtM（floatArr,offset,eyeX,eyeY,eyeZ,centerX,centerY,centerZ,upX,upY,upZ）
 * floatArr，offset，用于存储计算的数组
 * eyeX,eyeY,eyeZ,camera的坐标位置。
 * centerX,centerY,centerZ camera观察的方向可以理解为摄像机镜头的指向，通过camera位置与观察目标点可以确定一个向量，此向量即代表了摄像机观察的方向
 * upX,upY,upZ 不太好理解，你就把自己的头当作摄像头，眼睛看向观察点，自己头顶的指向就是up的方向
 *
 */
class MyBaseTriangleLookAtRender : GLSurfaceView.Renderer {
    private var triangle: Triangle? = null
    private val vPMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)

    @Volatile
    var lookAtUpX: Float = 0f

    @Volatile
    var lookAtUpY: Float = 1f

    @Volatile
    var lookAtUpZ: Float = 0f

    @Volatile
    var lookAtEyeX: Float = 0f

    @Volatile
    var lookAtEyeY: Float = 0f

    @Volatile
    var lookAtEyeZ: Float = 0f

    @Volatile
    var lootAtCenterX: Float = 0f

    @Volatile
    var lootAtCenterY: Float = 0f

    @Volatile
    var lootAtCenterZ: Float = 0f

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
//        Matrix.orthoM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 10f)

    }

    override fun onDrawFrame(gl: GL10?) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)//根据指定的指执行清楚操作
        // Set the camera position (View matrix)
        Matrix.setLookAtM(viewMatrix, 0, lookAtEyeX, lookAtEyeY, lookAtEyeZ, lootAtCenterX, lootAtCenterY, lootAtCenterZ, lookAtUpX, lookAtUpY, lookAtUpZ)
        // Create a rotation transformation for the triangle
//            val time = SystemClock.uptimeMillis() % 4000L
//            val angle = 0.090f * time.toInt()
//            Matrix.setRotateM(viewMatrix, 0, angle, 0f, 0f, -1.0f)
        // Combine the projection and camera view matrices
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        triangle?.draw(vPMatrix)
    }

}