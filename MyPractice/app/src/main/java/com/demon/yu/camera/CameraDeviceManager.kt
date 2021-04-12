package com.demon.yu.camera

import android.annotation.SuppressLint
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.os.Handler

/**
 * 负责控制camera打开关闭
 */
class CameraDeviceManager(private val cameraManager: CameraManager, private val cameraHandler: Handler) : CameraDevice.StateCallback() {

    private var currentCameraDevice: CameraDevice? = null
    private var currentCameraCharacteristicsEntry: Camera2Act.CameraCharacteristicsEntry? = null

    var cameraDeviceCb: CameraDeviceCb? = null

    @SuppressLint("MissingPermission")
    fun openCamera(cameraCharacteristicsEntry: Camera2Act.CameraCharacteristicsEntry) {
        currentCameraCharacteristicsEntry = cameraCharacteristicsEntry
        currentCameraDevice?.close()
        cameraManager.openCamera(cameraCharacteristicsEntry.cameraID, this, cameraHandler)
    }


    fun getCharacteristics(cameraId: String): CameraCharacteristics? {
        try {
            return cameraManager.getCameraCharacteristics(cameraId)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
        return null
    }

    fun getCameraIdList(): Array<String>? {
        try {
            return cameraManager.cameraIdList
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
        return null
    }


    override fun onOpened(camera: CameraDevice) {
        currentCameraDevice = camera
        currentCameraCharacteristicsEntry?.let {
            cameraDeviceCb?.onCameraOpened(camera, it)
        }

    }

    override fun onDisconnected(camera: CameraDevice) {
        cameraDeviceCb?.onCameraClosed(camera)
    }

    override fun onError(camera: CameraDevice, error: Int) {
    }


    interface CameraDeviceCb {
        fun onCameraOpened(camera: CameraDevice, entry: Camera2Act.CameraCharacteristicsEntry)
        fun onCameraClosed(camera: CameraDevice)
    }
}