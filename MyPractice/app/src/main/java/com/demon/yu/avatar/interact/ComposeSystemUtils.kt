package com.demon.yu.avatar.interact

import android.app.Service
import android.content.Context
import android.os.Vibrator

object ComposeSystemUtils {

    fun vibrator(context: Context, isContinue: Boolean) {
        val vib = context.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
        vib.vibrate(longArrayOf(100, 100), if (isContinue) 0 else -1)
    }

    fun cancelVibrator(context: Context) {
        val vib = context.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
        vib.cancel()
    }
}