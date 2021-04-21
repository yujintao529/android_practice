package com.demon.yu.camera

import android.hardware.camera2.CameraCharacteristics
import android.util.Size

class CameraConfig {
    companion object {
        private const val RATIO_16_9: Float = 16f / 9f
        private const val RATIO_4_3: Float = 4f / 3f
    }

    var previewSize: Size = Size(0, 0)
    var pictureSize: Size = Size(0, 0)
    var aspectRatio: Float = RATIO_16_9
    var cameraFacing: Int = CameraCharacteristics.LENS_FACING_FRONT


}