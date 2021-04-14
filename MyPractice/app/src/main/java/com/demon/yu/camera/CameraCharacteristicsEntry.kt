package com.demon.yu.camera

import android.hardware.camera2.CameraCharacteristics

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