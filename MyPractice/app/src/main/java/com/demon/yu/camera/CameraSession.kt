package com.demon.yu.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.hardware.camera2.params.MeteringRectangle
import android.media.ImageReader
import android.os.Handler
import android.util.Size
import android.view.Surface
import com.demon.yu.utils.BitmapUtils
import com.demon.yu.utils.ToastUtils
import com.example.mypractice.Logger
import kotlin.math.abs

/**
 * 逻辑中有个假设，setSurfaceTexture调用前于camera open
 */
open class CameraSession(private val context: Context, private val cameraDeviceManager: CameraDeviceManager, private val cameraConfig: CameraConfig, private val cameraHandler: Handler) : CameraDeviceManager.CameraDeviceCb {


    companion object {
        const val ERROR_CODE = -1//custom
    }

    private val requestManager by lazy { RequestManager() }

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


    init {
        cameraDeviceManager.cameraDeviceCb = this
    }

    fun setCharacteristicsEntry(cameraCharacteristicsEntry: CameraCharacteristicsEntry) {
        requestManager.setCharacteristics(cameraCharacteristicsEntry.cameraCharacteristics)
        val size = getOptimalSize(cameraCharacteristicsEntry.cameraCharacteristics, SurfaceTexture::class.java, cameraConfig.aspectRatio)
        cameraConfig.aspectRatio = size.width * 1f / size.height
        cameraConfig.previewSize = size
        cameraConfig.pictureSize = getOptimalSize(cameraCharacteristicsEntry.cameraCharacteristics, ImageReader::class.java, cameraConfig.aspectRatio)
        imageReader = ImageReader.newInstance(size.width, size.height, ImageFormat.JPEG, 2)
        this.cameraCharacteristicsEntry = cameraCharacteristicsEntry
        cameraSessionCb?.onCameraPreviewSizeChanged(size, cameraCharacteristicsEntry.cameraCharacteristics)

    }


    private fun setCameraCaptureSession(cameraCaptureSession: CameraCaptureSession) {
        this.cameraCaptureSession = cameraCaptureSession
    }


    /**
     * 拍照
     */
    fun takePicture(takePictureCb: TakePictureCb) {
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


    private fun buildCameraPreviewSession(cameraDevice: CameraDevice, surfaceTexture: SurfaceTexture) {
        this.cameraDevice = cameraDevice
        this.surfaceTexture = surfaceTexture
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
            setCameraCaptureSession(session)
        }

        override fun onConfigureFailed(session: CameraCaptureSession) {
            ToastUtils.toast("CameraCaptureSession onConfigureFailed")
        }

    }


    /**
     * 1. 根据配置打开摄像头
     * 2. 创建session
     * 3. 开始预览
     */
    fun startPreview(surfaceTexture: SurfaceTexture) {
        this.surfaceTexture = surfaceTexture
        if (cameraCharacteristicsEntry == null) {
            throw IllegalStateException(" cameraCharacteristicsEntry is null, please call setCameraCharacteristicsEntry before")
        }
        surfaceTexture.setDefaultBufferSize(cameraConfig.previewSize.width, cameraConfig.previewSize.height)
        cameraCharacteristicsEntry?.let {
            cameraDeviceManager.openCamera(it)
        }
    }


    override fun onCameraOpened(cameraDevice: CameraDevice, entry: CameraCharacteristicsEntry) {
        surfaceTexture?.let {
            buildCameraPreviewSession(cameraDevice, it)
        }
    }

    override fun onCameraClosed(camera: CameraDevice) {

    }

    override fun onCameraError(camera: CameraDevice, error: Int) {
        ToastUtils.toast("onCameraError error=$error ")
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