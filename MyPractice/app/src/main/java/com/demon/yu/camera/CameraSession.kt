package com.demon.yu.camera

import android.content.Context
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.params.MeteringRectangle
import android.media.ImageReader
import android.os.Handler
import android.util.Size
import android.view.Surface
import kotlin.math.abs

/**
 * 逻辑中有个假设，setSurfaceTexture调用前于camera open
 */
abstract class CameraSession(private val context: Context, private val cameraConfig: CameraConfig, private val cameraHandler: Handler) : CameraDeviceManager.CameraDeviceCb {


    private val requestManager by lazy { RequestManager() }

    private var cameraCaptureSession: CameraCaptureSession? = null

    var cameraSessionCb: CameraSessionCb? = null

    //surfaceTexture
    private var surface: Surface? = null
    private var surfaceTexture: SurfaceTexture? = null
//    private var surfaceSize: Size = Size(0, 0)


    //cameraDevice
    private var cameraDevice: CameraDevice? = null

    //take picture
    private var imageReader: ImageReader? = null


    fun setCharacteristics(characteristics: CameraCharacteristics) {
        requestManager.setCharacteristics(characteristics)
        val size = getOptimalSize(characteristics, SurfaceTexture::class.java, cameraConfig.aspectRatio)
        cameraConfig.aspectRatio = size.width * 1f / size.height
        cameraConfig.previewSize = size
        cameraConfig.pictureSize = getOptimalSize(characteristics, ImageReader::class.java, cameraConfig.aspectRatio)
        surfaceTexture?.setDefaultBufferSize(size.width, size.height)
        cameraSessionCb?.onCameraPreviewSizeChanged(size, characteristics)
        imageReader = ImageReader.newInstance(size.width, size.height, ImageFormat.JPEG, 2)
    }


    fun setCameraCaptureSession(cameraCaptureSession: CameraCaptureSession) {
        this.cameraCaptureSession = cameraCaptureSession
    }

//    fun setSurfaceTexture(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
//        this.surfaceTexture = surfaceTexture
//        this.surface = Surface(surfaceTexture)
//        surfaceSize = Size(width, height)
//    }


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


    fun buildCameraPreviewSession(surfaceTexture: SurfaceTexture) {
        this.surfaceTexture = surfaceTexture
        this.surface = Surface(surfaceTexture)
        val list = mutableListOf<Surface>()
        this.surface?.let {
            list.add(it)
        }
        imageReader?.surface?.let {
            list.add(it)
        }
        cameraDevice?.createCaptureSession(list, CaptureSessionStateCb(), cameraHandler)

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


    override fun onCameraOpened(camera: CameraDevice, entry: Camera2Act.CameraCharacteristicsEntry) {
        cameraDevice = camera
        buildCameraPreviewSession()
    }

    override fun onCameraClosed(camera: CameraDevice) {
        cameraDevice = null
    }

    private inner class CaptureSessionStateCb : CameraCaptureSession.StateCallback() {
        override fun onConfigured(session: CameraCaptureSession) {
            TODO("Not yet implemented")
        }

        override fun onConfigureFailed(session: CameraCaptureSession) {
            TODO("Not yet implemented")
        }

    }


    interface CameraSessionCb {
        fun onCameraPreviewSizeChanged(previewSize: Size, cameraCharacteristics: CameraCharacteristics)
    }
}