package com.demon.yu.system

import android.os.CancellationSignal
import android.view.View
import android.view.Window
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsAnimationControlListenerCompat
import androidx.core.view.WindowInsetsCompat

/**
 * @description
 * @author yujinta.529
 * @create 2022-12-13
 */
object WindowInsetsControllerUtils {
    fun getController(window: Window, view: View) {

    }

    fun showInputMethod(window: Window, view: View) {
        val controller = WindowCompat.getInsetsController(window, view)
        controller.show(WindowInsetsCompat.Type.ime())
    }

    fun listenImeInsetsChange(
        window: Window,
        view: View,
        signal: CancellationSignal,
        listenerCompat: WindowInsetsAnimationControlListenerCompat
    ) {
        val controller = WindowCompat.getInsetsController(window, view)
        controller.controlWindowInsetsAnimation(
            WindowInsetsCompat.Type.ime(), 2000L, null, signal, listenerCompat
        )

    }

    fun removeImeInsetsChange(
        window: Window,
        view: View,
        listenerCompat: WindowInsetsAnimationControlListenerCompat
    ) {
        val controller = WindowCompat.getInsetsController(window, view)
    }
}