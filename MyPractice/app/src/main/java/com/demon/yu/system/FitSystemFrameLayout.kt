package com.demon.yu.system

import android.content.Context
import android.util.AttributeSet
import android.view.WindowInsets
import android.widget.FrameLayout
import androidx.core.view.ViewCompat

/**
 * @description
 * @author yujinta.529
 * @create 2022-11-14
 */
class FitSystemFrameLayout(context: Context, attributeSet: AttributeSet? = null) :
    FrameLayout(context, attributeSet) {
    init {
//        ViewCompat.setOnApplyWindowInsetsListener(this,FitWindowSystemListener())
    }

    override fun dispatchApplyWindowInsets(insets: WindowInsets?): WindowInsets {
        return super.dispatchApplyWindowInsets(insets)
    }

    override fun onApplyWindowInsets(insets: WindowInsets?): WindowInsets {
        return super.onApplyWindowInsets(insets)
    }

}