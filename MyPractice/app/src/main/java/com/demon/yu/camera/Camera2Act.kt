package com.demon.yu.camera

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.os.*
import android.util.Size
import android.view.TextureView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.demo.yu.context.files.FilesManager
import com.demon.yu.utils.*
import com.demon.yu.utils.FileUtils
import com.example.mypractice.Logger
import com.example.mypractice.R
import kotlinx.android.synthetic.main.activity_camera2.*
import java.io.File


/**
 * activity能力：
 * 1. 预览视频
 * 2. 拍照
 *
 * notes：
 * 1. 相机 Sensor 的宽是长边，而高是短边。所以需要注意相机获取出来的分辨率
 * 2. CameraDevice创建captureSession需要注意，CameraDevice创建create CameraSession会销毁上一个captureSession
 * 3. 每一个captureRequest必须要有个Target
 *
 *
 *
 * question:
 * 1. bitmap canvas matrix旋转的效果。
 */
class Camera2Act : AppCompatActivity() {

    companion object {
        const val TAG = "Camera2Act"
        const val REQUEST_CAMERA_COD = 1
        const val MSG_CONFIG_CAMERA = 2
        const val MSG_START_PREVIEW = 3
        const val MSG_SWITCH_CAMERA = 4
        const val MSG_TAKE_PICTURE = 5
    }

    private val cameraManager: CameraManager by lazy { getSystemService(CameraManager::class.java) }

    private var cameraThread = HandlerThread("camera2-thread")
    private lateinit var cameraHandler: Handler
    private val textureViewListener = TextureViewListener()
    private var cameraDevice: CameraDevice? = null

    private var focusManager: FocusManager? = null

    private var cameraSession: CameraSession? = null

    //相机配置
    private var cameraPreviewSize: Size? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera2)
        cameraThread.start()
        cameraHandler = Handler(cameraThread.looper, CameraHandlerCallback())
        initView()
        initManager()
        requestCameraPermission()
    }

    private fun initManager() {
        focusManager = FocusManager(manualFocusView, Looper.getMainLooper())
        cameraSession = CameraSession(context = this, cameraConfig = CameraConfig()).apply {
            lifecycle.addObserver(this)
        }
        cameraSession?.cameraSessionCb = CameraSessionCbImpl()
    }
//
//    private fun initCameraConfig() {
//        val front = getFrontCamera()
//        if (front != null) {
//            val entry = buildCameraCharaEntry(front.first, front.second)
//            cameraCharacteristicsMap[front.first] = entry
//            cameraMap[entry.lensFacing] = entry
//        }
//        val back = getBackCamera()
//        if (back != null) {
//            val entry = buildCameraCharaEntry(back.first, back.second)
//            cameraCharacteristicsMap[back.first] = entry
//            cameraMap[entry.lensFacing] = entry
//        }
//    }


    private fun getRandomTakePictureFilePath(): String {
        val parent = FilesManager.getInstance().getSDCardRootFiles("picture")
        val filePath = "${parent.absolutePath}/picture_%d.jpeg".format(System.currentTimeMillis())
        FileUtils.createNewFile(File(filePath))
        return filePath
    }

    private fun initView() {
        takePicture.setOnClickListener {
            cameraHandler.obtainMessage(MSG_TAKE_PICTURE).sendToTarget()
        }
        switchCamera.setOnClickListener {
            switchCamera()
        }
        manualFocusView.setListener(object : ManualFocusView.IFocusTouchListener {
            override fun canPerformTouchEvent(action: Int): Boolean {
                return true
            }

            override fun interceptTouchEvent(action: Int, x: Float, y: Float) {
            }

            override fun performTouchEvent(action: Int, x: Float, y: Float) {
                focusManager?.startFocus(x, y)
                val meteringRectangleAF = focusManager?.getFocusArea(x, y, true)
                val meteringRectangleAE = focusManager?.getFocusArea(x, y, false)
                if (meteringRectangleAE != null && meteringRectangleAF != null) {
                    cameraSession?.applyTouchFocusRequest(meteringRectangleAF, meteringRectangleAE)
                }
            }

        })
    }

    private fun switchCamera() {
        cameraHandler.obtainMessage(MSG_SWITCH_CAMERA).sendToTarget()
    }


    override fun onDestroy() {
        super.onDestroy()
        cameraHandler.removeCallbacksAndMessages(null)
        cameraThread.quit()
        cameraDevice?.close()
        cameraDevice = null
    }


    private fun onCameraGranted() {
        cameraHandler.obtainMessage(MSG_CONFIG_CAMERA).sendToTarget()
    }

    private fun getFrontCamera(): Pair<String, CameraCharacteristics>? {
        // 遍历所有可用的摄像头 ID，只取出其中的前置和后置摄像头信息。
        return CameraUtils.takeCameraInterval(cameraManager, CameraCharacteristics.LENS_FACING_FRONT)
    }

    private fun getBackCamera(): Pair<String, CameraCharacteristics>? {
        // 遍历所有可用的摄像头 ID，只取出其中的前置和后置摄像头信息。
        return CameraUtils.takeCameraInterval(cameraManager, CameraCharacteristics.LENS_FACING_BACK)
    }


    private fun requestCameraPermission() {
        val hasCameraPermission = PermissionsUtil.hasPermission(Manifest.permission.CAMERA)
        val hasStoragePermission = PermissionsUtil.hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val needsPermission = mutableListOf<String>()
        if (hasCameraPermission.not()) {
            needsPermission.add(Manifest.permission.CAMERA)
        }
        if (hasStoragePermission.not()) {
            needsPermission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (needsPermission.isEmpty()) {
            onCameraGranted()
        } else {
            // BEGIN_INCLUDE(camera_permission_request)
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.CAMERA) || ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Provide an additional rationale to the user if the permission was not granted
                // and the user would benefit from additional context for the use of the permission.
                // For example if the user has previously denied the permission.
                ToastUtils.toast("大哥给我权限,重新进")
            } else {

                // Camera permission has not been granted yet. Request it directly.
                ActivityCompat.requestPermissions(this, needsPermission.toTypedArray(),
                        REQUEST_CAMERA_COD)
            }
        }

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
        cameraContainer.setRatio(targetRatio)
    }


    private inner class CameraHandlerCallback : Handler.Callback {
        @SuppressLint("MissingPermission")
        override fun handleMessage(msg: Message): Boolean {
            Logger.debug(TAG, "handleMessage $msg")
            when (msg.what) {
                MSG_SWITCH_CAMERA -> {
                    cameraSession?.switchCamera()
                }
                MSG_CONFIG_CAMERA -> {
                    cameraSession?.initPreviewConfig()
                    Logger.debug(TAG, "config camera initPreviewConfig")
                }
                MSG_START_PREVIEW -> {
                    if (cameraPreviewSize != null) {
                        cameraHandler.removeMessages(MSG_START_PREVIEW)
                        val surfaceTexture = msg.obj as SurfaceTexture
                        cameraSession?.startPreview(surfaceTexture)
                    }
                }
                MSG_TAKE_PICTURE -> {
                    cameraSession?.takePicture(object : CameraSession.TakePictureCb {
                        override fun onImageAvailable(bitmap: Bitmap) {
                            val destFilePath = getRandomTakePictureFilePath()
                            BitmapUtils.saveBitmapToFile(bitmap, destFilePath)
                            uiThread {
                                Camera2PreviewActivity.startPreviewActivity(this@Camera2Act, destFilePath)
                            }
                        }

                        override fun onImageError(code: Int) {
                            Logger.debug(TAG, "takePicture error $code")
                        }
                    })
                }
            }
            return true
        }


    }

    private inner class TextureViewListener : TextureView.SurfaceTextureListener {

        private var width: Int = 0
        private var height: Int = 0
        var currentSurfaceTexture: SurfaceTexture? = null
        private fun onTexture(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
            if (this.width != width || this.height != height) {
                this.width = width
                this.height = height
                this.currentSurfaceTexture = surfaceTexture
                cameraHandler.obtainMessage(MSG_START_PREVIEW, surfaceTexture).sendToTarget()

            }
            Logger.debug(TAG, "onTexture width=$width,height=$height")

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
//            Logger.debug(TAG, "onSurfaceTextureUpdated")
        }
    }


    private inner class CameraSessionCbImpl : CameraSession.CameraSessionCb {
        override fun onCameraPreviewSizeChanged(previewSize: Size, cameraCharacteristics: CameraCharacteristics) {
            focusManager?.onPreviewChanged(previewSize.width, previewSize.height, cameraCharacteristics)
            uiThread {
                resizeTextureView(previewSize.width * 1f / previewSize.height)
                cameraPreviewSize = previewSize
                textureView.surfaceTextureListener = textureViewListener

            }
        }
    }
}

