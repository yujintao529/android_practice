package com.demon.yu.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.hardware.camera2.params.MeteringRectangle
import android.media.ImageReader
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.util.Size
import android.view.Surface
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.demon.yu.utils.BitmapUtils
import com.demon.yu.utils.ToastUtils
import com.example.mypractice.Logger
import kotlin.math.abs

/**
 * 逻辑中有个假设，setSurfaceTexture调用前于camera open
 */
open class CameraSession(private val context: Context, private val cameraConfig: CameraConfig) : CameraDeviceManager.CameraDeviceCb, Handler.Callback, LifecycleObserver {


    companion object {
        const val ERROR_CODE = -1//custom
        const val TAG = "CameraSession"//custom

        //msg what
        const val REQUEST_CAMERA_COD = 1
        const val MSG_CONFIG_CAMERA = 2
        const val MSG_START_PREVIEW = 3
        const val MSG_SWITCH_CAMERA = 4
        const val MSG_TAKE_PICTURE = 5
        const val MSG_RE_PREVIEW = 6

        //camera state
        const val STATE_CAMERA_IDLE = 0
        const val STATE_CAMERA_PREVIEW = 0
        const val STATE_CAMERA_RECORD = 0
    }

    private val requestManager by lazy { RequestManager() }
    private val cameraDeviceManager: CameraDeviceManager
    private var cameraCaptureSession: CameraCaptureSession? = null


    //surfaceTexture
    private var surface: Surface? = null
    private var surfaceTexture: SurfaceTexture? = null


    //cameraDevice
    private var cameraDevice: CameraDevice? = null

    //take picture
    private var imageReader: ImageReader? = null


    //cb
    var cameraSessionCb: CameraSessionCb? = null


    //摄像头相关配置
    private var cameraCharacteristicsEntry: CameraCharacteristicsEntry? = null
    private var currentCameraFacing: Int = cameraConfig.cameraFacing


    private val cameraThread = HandlerThread("camera2-session")
    private val cameraHandler: Handler

    init {
        cameraThread.start()
        cameraHandler = Handler(cameraThread.looper, this)
        cameraDeviceManager = CameraDeviceManager(context.getSystemService(CameraManager::class.java), cameraHandler)
    }


    fun initPreviewConfig() {
        val pair = CameraUtils.takeCameraInterval(cameraDeviceManager.cameraManager, currentCameraFacing)
                ?: throw java.lang.IllegalStateException("initConfig error, no camera found")
        val cameraCharacteristicsEntry = CameraCharacteristicsEntry(pair.first, pair.second)
        cameraHandler.obtainMessage(MSG_CONFIG_CAMERA, cameraCharacteristicsEntry).sendToTarget()
    }


    @CameraThread
    private fun setCharacteristicsEntry(cameraCharacteristicsEntry: CameraCharacteristicsEntry): Boolean {
        requestManager.setCharacteristics(cameraCharacteristicsEntry.cameraCharacteristics)
        val size = calculatePreviewSize(cameraCharacteristicsEntry.cameraCharacteristics, cameraConfig.aspectRatio)
        this.cameraCharacteristicsEntry = cameraCharacteristicsEntry
        if (size != cameraConfig.previewSize) {
            cameraConfig.aspectRatio = size.width * 1f / size.height
            cameraConfig.previewSize = size
            cameraConfig.pictureSize = getOptimalSize(cameraCharacteristicsEntry.cameraCharacteristics, ImageReader::class.java, cameraConfig.aspectRatio)
            imageReader?.close()
            imageReader = ImageReader.newInstance(size.width, size.height, ImageFormat.JPEG, 2)
            cameraSessionCb?.onCameraPreviewSizeChanged(size, cameraCharacteristicsEntry.cameraCharacteristics)
            return true
        }
        return false
    }


    private fun calculatePreviewSize(cameraCharacteristics: CameraCharacteristics, aspectRatio: Float): Size {
        return getOptimalSize(cameraCharacteristics, SurfaceTexture::class.java, aspectRatio)
    }


    /**
     * 切换摄像头,不考虑切换摄像头导致的分辨率不支持的问题
     */
    fun switchCamera() {
        Logger.d(TAG, "switchCamera currentCameraFacing = $currentCameraFacing")
        currentCameraFacing = if (currentCameraFacing == CameraCharacteristics.LENS_FACING_FRONT) {
            CameraCharacteristics.LENS_FACING_BACK
        } else {
            CameraCharacteristics.LENS_FACING_FRONT
        }
        val pair = CameraUtils.takeCameraInterval(cameraDeviceManager.cameraManager, currentCameraFacing)
                ?: throw java.lang.IllegalStateException("initConfig error, no camera found")
        val cameraCharacteristicsEntry = CameraCharacteristicsEntry(pair.first, pair.second)
        cameraHandler.obtainMessage(MSG_RE_PREVIEW, cameraCharacteristicsEntry).sendToTarget()
    }


    @CameraThread
    private fun rePreviewInterval(cameraCharacteristicsEntry: CameraCharacteristicsEntry) {
        if (setCharacteristicsEntry(cameraCharacteristicsEntry).not()) {
            cameraCaptureSession?.stopRepeating()
            cameraDevice?.close()
            surfaceTexture?.let {
                startPreview(it)
            }
        }

    }


    @CameraThread
    private fun setCameraCaptureSession(cameraCaptureSession: CameraCaptureSession, preview: Boolean) {
        this.cameraCaptureSession = cameraCaptureSession
        if (preview) {
            cameraDevice?.let {
                val builder = requestManager.createPreviewRequestBuilder(it)
                surface?.let { surface ->
                    builder.addTarget(surface)
                }
                val request = requestManager.getPreviewRequest(builder)
                cameraCaptureSession.setRepeatingRequest(request, PreviewCaptureListener(), cameraHandler)
            }
        }
    }

    /**
     * 拍照
     */
    fun takePicture(takePictureCb: TakePictureCb) {
        cameraHandler.obtainMessage(MSG_TAKE_PICTURE, takePictureCb).sendToTarget()
    }

    @CameraThread
    private fun takePictureInterval(takePictureCb: TakePictureCb) {
        if (cameraCaptureSession == null) {
            Logger.debug(Camera2Act.TAG, "cameraCaptureSession not ready,please take picture later")
            takePictureCb.onImageError(ERROR_CODE)
            return
        }
        val cameraCharacteristics = cameraCharacteristicsEntry?.cameraCharacteristics ?: return
        val composeListener = TakePictureComposeListener(takePictureCb, cameraCharacteristics = cameraCharacteristics)
        cameraCaptureSession?.device?.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)?.also {
            imageReader?.surface?.let { surface ->
                it.addTarget(surface)
            }
            surface?.let { surface ->
                it.addTarget(surface)
            }

            imageReader?.setOnImageAvailableListener(composeListener, cameraHandler)
            cameraCaptureSession?.capture(it.build(), composeListener, cameraHandler)
        }
    }


    private fun getPreviewRequestBuilder(): CaptureRequest.Builder {
        val surface = surface ?: throw IllegalAccessException("surface not ready")
        val cameraDevice = cameraDevice ?: throw IllegalAccessException("cameraDevice not ready")
        val builder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
        builder.addTarget(surface)
        return builder
    }

    fun applyTouchFocusRequest(afRectangle: MeteringRectangle, aeRectangle: MeteringRectangle) {
        val request = requestManager.getTouch2FocusRequest(getPreviewRequestBuilder(), afRectangle, aeRectangle)
        applyRequestInterval(request)
    }


    override fun handleMessage(msg: Message): Boolean {
        when (msg.what) {
            MSG_CONFIG_CAMERA -> {
                val cameraCharacteristicsEntry = msg.obj as CameraCharacteristicsEntry
                setCharacteristicsEntry(cameraCharacteristicsEntry)
            }
            MSG_START_PREVIEW -> {
                val surfaceTexture = msg.obj as SurfaceTexture
                startPreviewInterval(surfaceTexture)
            }
            MSG_RE_PREVIEW -> {
                val cameraCharacteristicsEntry = msg.obj as CameraCharacteristicsEntry
                setCharacteristicsEntry(cameraCharacteristicsEntry)
            }
            MSG_TAKE_PICTURE -> {
                val cb = msg.obj as TakePictureCb
                takePictureInterval(cb)
            }
        }
        return true
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        cameraCaptureSession?.stopRepeating()
        cameraDevice?.close()
        cameraHandler.removeCallbacksAndMessages(null)
        cameraThread.quit()

    }


    private fun buildCameraPreviewSession(cameraDevice: CameraDevice, surfaceTexture: SurfaceTexture) {
        this.cameraDevice = cameraDevice
        this.surface = Surface(surfaceTexture)
        val list = mutableListOf<Surface>()
        this.surface?.let {
            list.add(it)
        }
        imageReader?.surface?.let {
            list.add(it)
        }
        cameraDevice.createCaptureSession(list, CaptureSessionStateCb(), cameraHandler)

    }

    private fun applyRequestInterval(captureRequest: CaptureRequest, callback: CameraCaptureSession.CaptureCallback? = null) {
        cameraCaptureSession?.capture(captureRequest, callback, cameraHandler)
    }


    private fun getOptimalSize(cameraCharacteristics: CameraCharacteristics, clazz: Class<*>, aspectRatio: Float): Size {
        val streamConfigurationMap = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
        val supportedSizes = streamConfigurationMap?.getOutputSizes(clazz)
        if (supportedSizes != null) {
            for (size in supportedSizes) {
                if (abs(size.width.toFloat() / size.height - aspectRatio) < 0.01f) {
                    return size
                }
            }
        }
        if (supportedSizes != null && supportedSizes.isNotEmpty()) {
            return supportedSizes[0]
        }
        throw IllegalStateException("sorry,get no size for class ${clazz.canonicalName},ratio $aspectRatio")
    }


    private inner class CaptureSessionStateCb : CameraCaptureSession.StateCallback() {
        override fun onConfigured(session: CameraCaptureSession) {
            Logger.debug(TAG, "onConfigured session=$session")
            setCameraCaptureSession(session, true)
        }

        override fun onConfigureFailed(session: CameraCaptureSession) {
            Logger.debug(TAG, "onConfigureFailed session")
            ToastUtils.toast("CameraCaptureSession onConfigureFailed.session=$session")
        }

        override fun onClosed(session: CameraCaptureSession) {
            Logger.debug(TAG, "onClosed session=$session")

        }
    }


    @CameraThread
    private fun startPreviewInterval(surfaceTexture: SurfaceTexture) {
        this.surfaceTexture = surfaceTexture
        if (cameraCharacteristicsEntry == null) {
            throw IllegalStateException(" cameraCharacteristicsEntry is null, please call setCameraCharacteristicsEntry before")
        }
        surfaceTexture.setDefaultBufferSize(cameraConfig.previewSize.width, cameraConfig.previewSize.height)
        cameraCharacteristicsEntry?.let {
            Logger.debug(TAG, "startPreview previewSize=${cameraConfig.previewSize}")
            cameraDeviceManager.openCamera(it)
        }
    }


    fun startPreview(surfaceTexture: SurfaceTexture) {
        cameraHandler.obtainMessage(MSG_START_PREVIEW, surfaceTexture).sendToTarget()
    }


    override fun onCameraOpened(cameraDevice: CameraDevice, entry: CameraCharacteristicsEntry) {
        Logger.debug(TAG, "onCameraOpened cameraDevice=$cameraDevice")
        surfaceTexture?.let {
            buildCameraPreviewSession(cameraDevice, it)
        }
    }

    override fun onCameraClosed(camera: CameraDevice) {
        Logger.debug(TAG, "onCameraClosed cameraDevice=$cameraDevice")
    }

    override fun onCameraError(camera: CameraDevice, error: Int) {
        ToastUtils.toast("onCameraError error=$error ")
        Logger.debug(TAG, "onCameraError cameraDevice=$cameraDevice,error=$error")
    }

    private inner class PreviewCaptureListener : CameraCaptureSession.CaptureCallback() {
        override fun onCaptureStarted(session: CameraCaptureSession, request: CaptureRequest, timestamp: Long, frameNumber: Long) {
            super.onCaptureStarted(session, request, timestamp, frameNumber)
//            Logger.debug(TAG, "onCaptureStarted session=$session")
        }

        override fun onCaptureProgressed(session: CameraCaptureSession, request: CaptureRequest, partialResult: CaptureResult) {
            super.onCaptureProgressed(session, request, partialResult)
            Logger.debug(TAG, "onCaptureProgressed session=$session")
        }

        override fun onCaptureCompleted(session: CameraCaptureSession, request: CaptureRequest, result: TotalCaptureResult) {
            super.onCaptureCompleted(session, request, result)
//            Logger.debug(TAG, "onCaptureCompleted session=$session")
        }

        override fun onCaptureFailed(session: CameraCaptureSession, request: CaptureRequest, failure: CaptureFailure) {
            super.onCaptureFailed(session, request, failure)
            Logger.debug(TAG, "onCaptureFailed session=$session")
        }

        override fun onCaptureSequenceCompleted(session: CameraCaptureSession, sequenceId: Int, frameNumber: Long) {
            super.onCaptureSequenceCompleted(session, sequenceId, frameNumber)
            Logger.debug(TAG, "onCaptureSequenceCompleted session=$session")
        }

        override fun onCaptureSequenceAborted(session: CameraCaptureSession, sequenceId: Int) {
            super.onCaptureSequenceAborted(session, sequenceId)
            Logger.debug(TAG, "onCaptureSequenceAborted session=$session")
        }

        override fun onCaptureBufferLost(session: CameraCaptureSession, request: CaptureRequest, target: Surface, frameNumber: Long) {
            super.onCaptureBufferLost(session, request, target, frameNumber)
            Logger.debug(TAG, "onCaptureBufferLost session=$session")
        }
    }


    private inner class TakePictureComposeListener(private val takePictureCb: TakePictureCb, private val cameraCharacteristics: CameraCharacteristics) : ImageReader.OnImageAvailableListener, CameraCaptureSession.CaptureCallback() {

        override fun onImageAvailable(reader: ImageReader) {
            val image = reader.acquireNextImage()
            image.use { //autoClose 类会自动关闭,无论是否发生异常
                val jpegByteBuffer = it.planes[0].buffer// Jpeg image data only occupy the planes[0].
                val jpegByteArray = ByteArray(jpegByteBuffer.remaining())
                jpegByteBuffer.get(jpegByteArray)
                val bitmap = BitmapUtils.bytes2Bitmap(jpegByteArray)
                if (bitmap == null) {
                    takePictureCb.onImageError(ERROR_CODE)
                    return
                }
                val result = CameraUtils.modifyJpegOrientation(this@CameraSession.context, cameraCharacteristics, bitmap)
                takePictureCb.onImageAvailable(result)
            }
        }


        override fun onCaptureCompleted(session: CameraCaptureSession, request: CaptureRequest, result: TotalCaptureResult) {
            super.onCaptureCompleted(session, request, result)
        }
    }

    interface TakePictureCb {
        fun onImageAvailable(bitmap: Bitmap)
        fun onImageError(code: Int)
    }

    interface CameraSessionCb {
        fun onCameraPreviewSizeChanged(previewSize: Size, cameraCharacteristics: CameraCharacteristics)
    }


}