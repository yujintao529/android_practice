package com.demon.yu.camera

import android.content.Context
import android.graphics.Bitmap
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.demon.yu.utils.BitmapUtils

object CameraUtils {

    fun getJpegOrientation(cameraCharacteristics: CameraCharacteristics, deviceOrientation: Int): Int {
        var myDeviceOrientation = deviceOrientation
        if (myDeviceOrientation == android.view.OrientationEventListener.ORIENTATION_UNKNOWN) {
            return 0
        }
        val sensorOrientation = cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)!!

        // Round device orientation to a multiple of 90
        myDeviceOrientation = (myDeviceOrientation + 45) / 90 * 90

        // Reverse device orientation for front-facing cameras
        val facingFront = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT
        if (facingFront) {
            myDeviceOrientation = -myDeviceOrientation
        }

        // Calculate desired JPEG orientation relative to camera orientation to make
        // the image upright relative to the device orientation
        return (sensorOrientation + myDeviceOrientation + 360) % 360
    }


    /**
     * 根据传入的characteristics及jpeg图片，生成正确的jpeg图片
     */
    fun modifyJpegOrientation(context: Context, characteristics: CameraCharacteristics, inputJpegBitmap: Bitmap): Bitmap {
        val display = (context.getSystemService(AppCompatActivity.WINDOW_SERVICE) as WindowManager).defaultDisplay
        val needRotate = CameraUtils.getJpegOrientation(cameraCharacteristics = characteristics, deviceOrientation = display.rotation)
        if (needRotate == 0) {
            return inputJpegBitmap
        }
        val result = BitmapUtils.rotate(inputJpegBitmap, needRotate)
        return result
    }


    fun takeCameraInterval(cameraManager: CameraManager, lensFacing: Int): Pair<String, CameraCharacteristics>? {
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


}


/**
 * 判断相机的 Hardware Level 是否大于等于指定的 Level。
 */
fun CameraCharacteristics.isHardwareLevelSupported(requiredLevel: Int): Boolean {
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