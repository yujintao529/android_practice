package com.demon.yu.camera

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.*
import android.util.Size
import android.view.Surface
import android.view.TextureView
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.demo.yu.context.files.FilesManager
import com.demon.yu.utils.*
import com.demon.yu.utils.FileUtils
import com.example.mypractice.Logger
import com.example.mypractice.R
import kotlinx.android.synthetic.main.activity_camera2.*
import kotlinx.coroutines.android.asCoroutineDispatcher
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
    private val cameraStateCallbackInner = CameraStateCallbackInner()
    private val textureViewListener = TextureViewListener()
    private var cameraDevice: CameraDevice? = null
    private var targetRatio = aspectRatio
    private var cameraCaptureSession: CameraCaptureSession? = null


    private var focusManager: FocusManager? = null
    private var previewSize: Size? = null

    private var cameraSession: CameraSession? = null
    private var cameraDeviceManager: CameraDeviceManager? = null

    //拍照
    private var pictureSize: Size? = null
    private var imageReader: ImageReader? = null

    private var state: Int = 0

    //surfaceTexture
    private var surface: Surface? = null

    //相机配置
    private var currentCameraCharacteristicsEntry: CameraCharacteristicsEntry? = null

    private val cameraCharacteristicsMap = mutableMapOf<String, CameraCharacteristicsEntry>() //camera_id-> CameraCharacteristicsEntry
    private val cameraMap = mutableMapOf<Int, CameraCharacteristicsEntry>() //len_face -> CameraCharacteristicsEntry

    private var takePackageComposeListener: TakeImageComposeListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera2)
        initCameraConfig()
        cameraThread.start()
        cameraHandler = Handler(cameraThread.looper, CameraHandlerCallback())
        cameraHandler.asCoroutineDispatcher()
        textureView.surfaceTextureListener = textureViewListener
        resizeTextureView(targetRatio)
        requestCameraPermission()
        initView()
        initManager()
    }

    private fun initManager() {
        focusManager = FocusManager(manualFocusView, Looper.getMainLooper())
        cameraSession = CameraSessionImpl(context = this, cameraConfig = CameraConfig(), cameraHandler = cameraHandler)
        cameraSession?.cameraSessionCb = CameraSessionCbImpl()
        cameraDeviceManager = CameraDeviceManager(cameraManager, cameraHandler)
        cameraDeviceManager?.cameraDeviceCb = cameraSession

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
            takePictureCustom()
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

    private fun takePictureCustom() {
        if (cameraCaptureSession == null) {
            Logger.debug(TAG, "cameraCaptureSession not ready,please take picture later")
            return
        }
        cameraCaptureSession?.device?.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)?.also {
            imageReader?.surface?.let { surface ->
                it.addTarget(surface)
            }
            surface?.let { surface ->
                it.addTarget(surface)
            }
            imageReader?.setOnImageAvailableListener(takePackageComposeListener, cameraHandler)
            cameraCaptureSession?.capture(it.build(), takePackageComposeListener, cameraHandler)
        }

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
        return takeCameraInterval(CameraCharacteristics.LENS_FACING_FRONT)
    }

    private fun getBackCamera(): Pair<String, CameraCharacteristics>? {
        // 遍历所有可用的摄像头 ID，只取出其中的前置和后置摄像头信息。
        return takeCameraInterval(CameraCharacteristics.LENS_FACING_BACK)
    }

    private fun takeCameraInterval(lensFacing: Int): Pair<String, CameraCharacteristics>? {
        val cameraIdList = cameraManager.cameraIdList
        cameraIdList.forEach { cameraId ->
            val cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId)
            if (cameraCharacteristics.isHardwareLevelSupported(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL)) {
                if (cameraCharacteristics[CameraCharacteristics.LENS_FACING] == lensFacing) {
                    return cameraId to cameraCharacteristics
                }
            }
            if (cameraCharacteristics.isHardwareLevelSupported(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED)) {
                if (cameraCharacteristics[CameraCharacteristics.LENS_FACING] == lensFacing) {
                    return cameraId to cameraCharacteristics
                }
            }
            if (cameraCharacteristics.isHardwareLevelSupported(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY)) {
                if (cameraCharacteristics[CameraCharacteristics.LENS_FACING] == lensFacing) {
                    return cameraId to cameraCharacteristics
                }
            }
        }
        return null
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
                    cameraDeviceManager?.openCamera(cameraCharacteristicsEntry)
                    cameraSession?.setCharacteristics(cameraCharacteristicsEntry.cameraCharacteristics)

                    takePackageComposeListener = TakeImageComposeListener(cameraCharacteristicsEntry.cameraCharacteristics)

                    currentCameraCharacteristicsEntry = cameraCharacteristicsEntry

                    Logger.debug(TAG, "openCamera previewSize=$previewSize,targetRatio=$targetRatio,camera=$currentCameraCharacteristicsEntry")

                }
                MSG_START_PREVIEW -> {
                    //ignore
//                    if (surface == null) {
//                        Logger.debug(TAG, "start preview unable,surface not ready ")
//                        return true
//                    }
//                    if (cameraDevice == null) {
//                        Logger.debug(TAG, "start preview unable,cameraDevice not ready ")
//                        return true
//                    }
//                    if (cameraCaptureSession != null) {
//                        //处理上一个cameraCaptureSession
//                        cameraCaptureSession?.close()
//                        cameraCaptureSession = null
//                    }


                    cameraHandler.removeMessages(MSG_START_PREVIEW)

                    cameraSession?.buildCameraPreviewSession()

                    val outputs = listOf(surface, imageReader?.surface)
                    cameraDevice?.createCaptureSession(outputs, object : CameraCaptureSession.StateCallback() {
                        override fun onConfigured(session: CameraCaptureSession) {
                            Logger.debug(TAG, "MSG_START_PREVIEW onConfigured,session=$session")
                            if (cameraCaptureSession == null) {
                                cameraCaptureSession = session
                                cameraSession?.setCameraCaptureSession(session)
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

        private var width: Int = 0
        private var height: Int = 0
        private fun onTexture(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
            if (this.width != width || this.height != height) {
                this.width = width
                this.height = height
                cameraSession?.setSurfaceTexture(surfaceTexture, width, height)
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


    private inner class CameraStateCallbackInner : CameraDevice.StateCallback() {

        override fun onOpened(camera: CameraDevice) {
            cameraDevice = camera
            cameraHandler.obtainMessage(MSG_START_PREVIEW).sendToTarget()
        }

        override fun onDisconnected(camera: CameraDevice) {
            cameraDevice = null
            Logger.debug(TAG, "CameraStateCallbackInner onDisconnected")
        }

        override fun onError(camera: CameraDevice, error: Int) {
            Logger.error(TAG, "CameraStateCallbackInner onError $error", null);
        }
    }


    /**
     * 根据传入的characteristics及jpeg图片，生成正确的jpeg图片
     */
    private fun modifyJpegOrientation(characteristics: CameraCharacteristics, inputJpegFilePath: String): String {
        val display = (getSystemService(WINDOW_SERVICE) as WindowManager).defaultDisplay
        val needRotate = CameraUtils.getJpegOrientation(cameraCharacteristics = characteristics, deviceOrientation = display.rotation)
        if (needRotate == 0) {
            return inputJpegFilePath
        }
        val originBitmap = BitmapUtils.loadFileToBitmap(this, inputJpegFilePath)
        val newBitmap = BitmapUtils.rotate(originBitmap, needRotate)
        val newPath = getRandomTakePictureFilePath()
        BitmapUtils.saveBitmapToFile(newBitmap, newPath)
        return newPath
    }


    private inner class TakeImageComposeListener(private val cameraCharacteristics: CameraCharacteristics) : ImageReader.OnImageAvailableListener, CameraCaptureSession.CaptureCallback() {

        override fun onImageAvailable(reader: ImageReader) {
            val image = reader.acquireNextImage()
            image.use { //autoClose 类会自动关闭,无论是否发生异常
                val jpegByteBuffer = it.planes[0].buffer// Jpeg image data only occupy the planes[0].
                val jpegByteArray = ByteArray(jpegByteBuffer.remaining())
                jpegByteBuffer.get(jpegByteArray)
                val filePath = getRandomTakePictureFilePath()
                FileUtils.writeFile(filePath, jpegByteArray)
                ToastUtils.toast("图片保存$filePath")
                Logger.debug(TAG, "TakeImageComposeListener onImageAvailable path = $filePath")
                val newFilePath = modifyJpegOrientation(cameraCharacteristics, filePath)
                uiThread {
                    Camera2PreviewActivity.startPreviewActivity(this@Camera2Act, newFilePath, filePath)
                }
            }
        }


        override fun onCaptureCompleted(session: CameraCaptureSession, request: CaptureRequest, result: TotalCaptureResult) {
            super.onCaptureCompleted(session, request, result)
        }
    }

    private fun buildCameraCharaEntry(cameraID: String, cameraCharacteristics: CameraCharacteristics): CameraCharacteristicsEntry {
        return CameraCharacteristicsEntry(cameraID, cameraCharacteristics)
    }


    class CameraCharacteristicsEntry(val cameraID: String, val cameraCharacteristics: CameraCharacteristics) {


        val lensFacing: Int
            get() = cameraCharacteristics[CameraCharacteristics.LENS_FACING] ?: -1

        fun isFrontCamera() = lensFacing == CameraCharacteristics.LENS_FACING_FRONT


        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as CameraCharacteristicsEntry

            if (cameraID != other.cameraID) return false

            return true
        }

        override fun hashCode(): Int {
            return cameraID.hashCode()
        }

        override fun toString(): String {
            return "CameraCharacteristicsEntry(cameraID='$cameraID', cameraCharacteristics=$cameraCharacteristics, lensFacing=$lensFacing)"
        }

    }

    private inner class CameraSessionCbImpl : CameraSession.CameraSessionCb {
        override fun onCameraPreviewSizeChanged(previewSize: Size, cameraCharacteristics: CameraCharacteristics) {
            focusManager?.onPreviewChanged(previewSize.width, previewSize.height, cameraCharacteristics)
        }
    }

}

