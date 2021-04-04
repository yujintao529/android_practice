package com.demon.yu.camera

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.util.Size
import android.view.Surface
import android.view.TextureView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.demon.yu.utils.PermissionsUtil
import com.demon.yu.utils.ThreadPoolUtils
import com.demon.yu.utils.ToastUtils
import com.example.mypractice.Logger
import com.example.mypractice.R
import kotlinx.android.synthetic.main.activity_camera2.*
import kotlin.math.abs


/**
 * activity能力：
 * 1. 预览视频
 * 2. 拍照
 *
 * notes：
 * 1. 相机 Sensor 的宽是长边，而高是短边。所以需要注意相机获取出来的分辨率
 */
class Camera2Act : AppCompatActivity() {

    companion object {
        const val TAG = "Camera2Act"
        const val REQUEST_CAMERA_COD = 1
        const val MSG_OPEN_CAMERA = 1
        const val MSG_CLOSE_CAMERA = 2
        const val MSG_START_PREVIEW = 3
        var aspectRatio: Float = 4f / 3f //width / height

        //camera state
        const val STATE_CAMERA_IDLE = 0
    }

    private val cameraManager: CameraManager by lazy { getSystemService(CameraManager::class.java) }

    private var cameraThread = HandlerThread("camera2-thread")
    private lateinit var cameraHandler: Handler
    private val cameraStateCallbackInner = CameraStateCallbackInner()
    private val textureViewListener = TextureViewListener()
    private var cameraDevice: CameraDevice? = null
    private var targetRatio = aspectRatio
    private var cameraCaptureSession: CameraCaptureSession? = null
    private var surface: Surface? = null
    private var previewSize: Size? = null


    private var state: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera2)
        cameraThread.start()
        cameraHandler = Handler(cameraThread.looper, CameraHandlerCallback())
        textureView.surfaceTextureListener = textureViewListener
        resizeTextureView(targetRatio)
        requestCameraPermission()

    }

    override fun onDestroy() {
        super.onDestroy()
        cameraHandler.removeCallbacksAndMessages(null)
        cameraThread.quit()
        cameraDevice?.close()
        cameraDevice = null
    }


    private fun onCameraGranted() {
        val pair = getFrontCamera() ?: return
        cameraHandler.obtainMessage(MSG_OPEN_CAMERA, pair).sendToTarget()
    }

    private fun getFrontCamera(): Pair<String, CameraCharacteristics>? {
        // 遍历所有可用的摄像头 ID，只取出其中的前置和后置摄像头信息。
        val cameraIdList = cameraManager.cameraIdList
        cameraIdList.forEach { cameraId ->
            val cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId)
            if (cameraCharacteristics.isHardwareLevelSupported(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL)) {
                if (cameraCharacteristics[CameraCharacteristics.LENS_FACING] == CameraCharacteristics.LENS_FACING_FRONT) {
                    return cameraId to cameraCharacteristics
                }
            }
        }
        return null
    }

    /**
     * 判断相机的 Hardware Level 是否大于等于指定的 Level。
     */
    private fun CameraCharacteristics.isHardwareLevelSupported(requiredLevel: Int): Boolean {
        val sortedLevels = intArrayOf(
                CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY,
                CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED,
                CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL
        )
        val deviceLevel = this[CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL]
        if (requiredLevel == deviceLevel) {
            return true
        }
        for (sortedLevel in sortedLevels) {
            if (requiredLevel == sortedLevel) {
                return true
            } else if (deviceLevel == sortedLevel) {
                return false
            }
        }
        return false
    }

    private fun requestCameraPermission() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            onCameraGranted()
        } else {
            // BEGIN_INCLUDE(camera_permission_request)
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.CAMERA)) {
                // Provide an additional rationale to the user if the permission was not granted
                // and the user would benefit from additional context for the use of the permission.
                // For example if the user has previously denied the permission.
                ToastUtils.toast("大哥给我权限,重新进")
            } else {

                // Camera permission has not been granted yet. Request it directly.
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA),
                        REQUEST_CAMERA_COD)
            }
        }

    }


    private fun getOptimalSize(cameraCharacteristics: CameraCharacteristics, clazz: Class<*>, aspectRatio: Float): Size? {
        val streamConfigurationMap = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
        val supportedSizes = streamConfigurationMap?.getOutputSizes(clazz)
        if (supportedSizes != null) {
            for (size in supportedSizes) {
                if (abs(size.width.toFloat() / size.height) - aspectRatio < 0.01f) {
                    return size
                }
            }
        }
        return null
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_COD) {
            if (PermissionsUtil.verifyPermissions(grantResults)) {
                onCameraGranted()
            }
        }

    }

    private fun resizeTextureView(targetRatio: Float) {
        textureView.setRatio(targetRatio)
//        val targetHeight = Common.screenWidth * targetRatio
//        if (targetHeight > Common.screenHeight) {
//            val marginLayoutParams = textureView.layoutParams as ViewGroup.MarginLayoutParams
//            marginLayoutParams.bottomMargin = ((targetHeight - Common.screenHeight) * -1).toInt()
//            textureView.layoutParams.width = Common.screenWidth
//            textureView.layoutParams.height = targetHeight.toInt()
//            textureView.requestLayout()
//        } else {
//            textureView.layoutParams.width = Common.screenWidth
//            textureView.layoutParams.height = targetHeight.toInt()
//            textureView.requestLayout()
//        }
    }


    private fun startPreviewInterval(surface: Surface, cameraCaptureSession: CameraCaptureSession) {
        Logger.debug(TAG, "startPreviewInterval")
        val builder = cameraCaptureSession.device.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
        builder.addTarget(surface)
        cameraCaptureSession.setRepeatingRequest(builder.build(), null, cameraHandler)
    }

    private fun stopPreviewInterval(cameraCaptureSession: CameraCaptureSession) {
        cameraCaptureSession.stopRepeating()
    }

    private inner class CameraHandlerCallback : Handler.Callback {
        @SuppressLint("MissingPermission")
        override fun handleMessage(msg: Message): Boolean {
            Logger.debug(TAG, "handleMessage $msg")
            when (msg.what) {
                MSG_OPEN_CAMERA -> {
                    val pair = msg.obj as Pair<String, CameraCharacteristics>
                    cameraManager.openCamera(pair.first, cameraStateCallbackInner, ThreadPoolUtils.getMainHandler())
                    val size = getOptimalSize(pair.second, SurfaceTexture::class.java, targetRatio)
                    if (size != null) {
                        previewSize = size
                    } else {
                        val map = pair.second.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                        val supportedSizes = map?.getOutputSizes(TextureView::class.java)
                        if (supportedSizes != null && supportedSizes.isNotEmpty()) {
                            val targetSize = supportedSizes[0]
                            targetRatio = targetSize.width * 1f / targetSize.height
                            previewSize = size
                        }
                        ToastUtils.toast("不能预览，没有合适的分辨率")
                    }

                }
                MSG_CLOSE_CAMERA -> {


                }

                MSG_START_PREVIEW -> {
                    //ignore
                    if (surface == null) {
                        Logger.debug(TAG, "start preview unable,surface not ready ")
                        return true
                    }
                    if (cameraDevice == null) {
                        Logger.debug(TAG, "start preview unable,cameraDevice not ready ")
                        return true
                    }
                    if (cameraCaptureSession != null) {
                        //处理上一个cameraCaptureSession
                        cameraCaptureSession?.abortCaptures()
                        cameraCaptureSession?.stopRepeating()
                    }
                    cameraHandler.removeMessages(MSG_START_PREVIEW)
                    val outputs = listOf(surface)
                    cameraDevice?.createCaptureSession(outputs, object : CameraCaptureSession.StateCallback() {
                        override fun onConfigured(session: CameraCaptureSession) {
                            Logger.debug(TAG, "MSG_START_PREVIEW onConfigured,session=$session")
                            if (cameraCaptureSession == null) {
                                cameraCaptureSession = session
                                surface?.let {
                                    startPreviewInterval(it, session)
                                }
                            }
                        }

                        override fun onConfigureFailed(session: CameraCaptureSession) {
                            Logger.debug(TAG, "MSG_START_PREVIEW onConfigureFailed,session=$session")
                        }

                        override fun onReady(session: CameraCaptureSession) {
                            super.onReady(session)
                            Logger.debug(TAG, "MSG_START_PREVIEW onReady,session=$session")
                        }

                        override fun onActive(session: CameraCaptureSession) {
                            super.onActive(session)
                            Logger.debug(TAG, "MSG_START_PREVIEW onActive,session=$session")
                        }

                        override fun onCaptureQueueEmpty(session: CameraCaptureSession) {
                            super.onCaptureQueueEmpty(session)
                            Logger.debug(TAG, "MSG_START_PREVIEW onCaptureQueueEmpty,session=$session")
                        }

                        override fun onClosed(session: CameraCaptureSession) {
                            super.onClosed(session)
                            Logger.debug(TAG, "MSG_START_PREVIEW onClosed,session=$session")
                        }

                        override fun onSurfacePrepared(session: CameraCaptureSession, surface: Surface) {
                            super.onSurfacePrepared(session, surface)
                            Logger.debug(TAG, "MSG_START_PREVIEW onSurfacePrepared,session=$session")
                        }
                    }, cameraHandler)
                }
            }
            return true
        }


    }


    private inner class TextureViewListener : TextureView.SurfaceTextureListener {


        private fun onTexture(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
            surface?.release()
            surface = Surface(surfaceTexture)
            cameraHandler.obtainMessage(MSG_START_PREVIEW).sendToTarget()
        }

        override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
            Logger.debug(TAG, "onSurfaceTextureAvailable,width= $width,height = $height")
            onTexture(surface, width, height)
        }

        override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
            Logger.debug(TAG, "onSurfaceTextureSizeChanged,width= $width,height = $height")
            onTexture(surface, width, height)
        }

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
            Logger.debug(TAG, "onSurfaceTextureDestroyed")
            return true
        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
            Logger.debug(TAG, "onSurfaceTextureUpdated")
        }

    }

    private inner class CameraStateCallbackInner : CameraDevice.StateCallback() {

        override fun onOpened(camera: CameraDevice) {
            cameraDevice = camera
            cameraHandler.obtainMessage(MSG_START_PREVIEW).sendToTarget()
        }

        override fun onDisconnected(camera: CameraDevice) {
            cameraDevice = null

        }

        override fun onError(camera: CameraDevice, error: Int) {
        }

    }
}

