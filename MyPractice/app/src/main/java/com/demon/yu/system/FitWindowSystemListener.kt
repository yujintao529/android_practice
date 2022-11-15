package com.demon.yu.system

import android.view.View
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.WindowInsetsCompat
import com.example.mypractice.Logger

/**
 * @description
 * @author yujinta.529
 * @create 2022-11-14
 */
class FitWindowSystemListener : OnApplyWindowInsetsListener {
    override fun onApplyWindowInsets(v: View, insets: WindowInsetsCompat): WindowInsetsCompat {
        Logger.debug("FitWindowSystemListener","onApplyWindowInsets view=${v},insets=${insets.toString()}")
        return insets
    }
}