package com.demon.yu.camera

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.os.*
import android.util.Size
import android.view.Surface
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
        const val MSG_OPEN_CAMERA = 1
        const val MSG_CLOSE_CAMERA = 2
        const val MSG_START_PREVIEW = 3
        const val MSG_SWITCH_CAMERA = 4
        private const val RATIO_16_9: Float = 16f / 9f
        private const val RATIO_4_3: Float = 4f / 3f
        var aspectRatio: Float = RATIO_4_3

        //camera state
        const val STATE_CAMERA_IDLE = 0
        const val STATE_CAMERA_PREVIEW = 0
        const val STATE_CAMERA_RECORD = 0

    }

    private val cameraManager: CameraManager by lazy { getSystemService(CameraManager::class.java) }

    private var cameraThread = HandlerThread("camera2-thread")
    private lateinit var cameraHandler: Handler
    private val textureViewListener = TextureViewListener()
    private var cameraDevice: CameraDevice? = null
    private var targetRatio = aspectRatio
    private var cameraCaptureSession: CameraCaptureSession? = null


    private var focusManager: FocusManager? = null
    private var previewSize: Size? = null

    private var cameraSession: CameraSession? = null

    private var state: Int = 0


    //相机配置
    private var currentCameraCharacteristicsEntry: CameraCharacteristicsEntry? = null

    private val cameraCharacteristicsMap = mutableMapOf<String, CameraCharacteristicsEntry>() //camera_id-> CameraCharacteristicsEntry
    private val cameraMap = mutableMapOf<Int, CameraCharacteristicsEntry>() //len_face -> CameraCharacteristicsEntry


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera2)
        initCameraConfig()
        cameraThread.start()
        cameraHandler = Handler(cameraThread.looper, CameraHandlerCallback())
        textureView.surfaceTextureListener = textureViewListener
        requestCameraPermission()
        initView()
        initManager()
    }

    private fun initManager() {
        focusManager = FocusManager(manualFocusView, Looper.getMainLooper())
        val cameraDeviceManager = CameraDeviceManager(cameraManager, cameraHandler)
        cameraSession = CameraSession(context = this, cameraConfig = CameraConfig(), cameraDeviceManager = cameraDeviceManager, cameraHandler = cameraHandler)
        cameraSession?.cameraSessionCb = CameraSessionCbImpl()
    }

    private fun initCameraConfig() {
        val front = getFrontCamera()
        if (front != null) {
            val entry = buildCameraCharaEntry(front.first, front.second)
            cameraCharacteristicsMap[front.first] = entry
            cameraMap[entry.lensFacing] = entry
        }
        val back = getBackCamera()
        if (back != null) {
            val entry = buildCameraCharaEntry(back.first, back.second)
            cameraCharacteristicsMap[back.first] = entry
            cameraMap[entry.lensFacing] = entry
        }
    }


    private fun getRandomTakePictureFilePath(): String {
        val parent = FilesManager.getInstance().getSDCardRootFiles("picture")
        val filePath = "${parent.absolutePath}/picture_%d.jpeg".format(System.currentTimeMillis())
        FileUtils.createNewFile(File(filePath))
        return filePath
    }

    private fun initView() {
        takePicture.setOnClickListener {
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
        val cameraEntry = cameraMap.filter { it.value.isFrontCamera() }.values.firstOrNull()
        if (cameraEntry != null) {
            cameraHandler.obtainMessage(MSG_OPEN_CAMERA, cameraEntry).sendToTarget()
        } else {
            val cameraEntry = cameraMap.filter { it.value.isFrontCamera().not() }.values.firstOrNull()
            if (cameraEntry != null) {
                cameraHandler.obtainMessage(MSG_OPEN_CAMERA, cameraEntry).sendToTarget()
            }
        }
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


    private fun startPreviewInterval(surface: Surface, cameraCaptureSession: CameraCaptureSession) {
        Logger.debug(TAG, "startPreviewInterval")
        val builder = cameraCaptureSession.device.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
        builder.addTarget(surface)
        cameraCaptureSession.setRepeatingRequest(builder.build(), null, cameraHandler)
    }


    private inner class CameraHandlerCallback : Handler.Callback {
        @SuppressLint("MissingPermission")
        override fun handleMessage(msg: Message): Boolean {
            Logger.debug(TAG, "handleMessage $msg")
            when (msg.what) {
                MSG_SWITCH_CAMERA -> {
                    val destCameraCharacteristicsEntry = cameraMap.filter { it.value != currentCameraCharacteristicsEntry }.values.firstOrNull()
                    cameraHandler.obtainMessage(MSG_OPEN_CAMERA, destCameraCharacteristicsEntry).sendToTarget()
                }
                MSG_OPEN_CAMERA -> {
                    val cameraCharacteristicsEntry = msg.obj as CameraCharacteristicsEntry
                    if (cameraCharacteristicsEntry == currentCameraCharacteristicsEntry) {
                        Logger.debug(TAG, "openCamera cancel,cameraCharacteristicsEntry same ")
                        return true
                    }
                    cameraSession?.setCharacteristicsEntry(cameraCharacteristicsEntry)
                    currentCameraCharacteristicsEntry = cameraCharacteristicsEntry
                    Logger.debug(TAG, "openCamera previewSize=$previewSize,targetRatio=$targetRatio,camera=$currentCameraCharacteristicsEntry")

                }
                MSG_START_PREVIEW -> {
                    cameraHandler.removeMessages(MSG_START_PREVIEW)


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
                if (currentCameraCharacteristicsEntry != null) {
                    cameraSession?.startPreview(surfaceTexture)
                }

            }
            Logger.debug(TAG, "onTexture width=$width,height=$height,previewSize=$previewSize")

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


    private fun buildCameraCharaEntry(cameraID: String, cameraCharacteristics: CameraCharacteristics): CameraCharacteristicsEntry {
        return CameraCharacteristicsEntry(cameraID, cameraCharacteristics)
    }


    private inner class CameraSessionCbImpl : CameraSession.CameraSessionCb {
        override fun onCameraPreviewSizeChanged(previewSize: Size, cameraCharacteristics: CameraCharacteristics) {
            focusManager?.onPreviewChanged(previewSize.width, previewSize.height, cameraCharacteristics)
            resizeTextureView(previewSize.height * 1f / previewSize.width)
        }
    }

}

