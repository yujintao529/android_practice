package com.demon.yu.system

import android.content.Context
import android.util.AttributeSet
import android.view.WindowInsets
import androidx.viewpager.widget.ViewPager

/**
 * @description
 * @author yujinta.529
 * @create 2022-11-14
 */
class FitSystemViewPager(context: Context, attributeSet: AttributeSet? = null) :
    ViewPager(context, attributeSet) {

    override fun onApplyWindowInsets(insets: WindowInsets): WindowInsets {
        //可以自己消费一部分在给子view,测试下一
//        val consumeSystemInsets = insets.consumeSystemWindowInsets()
        return super.onApplyWindowInsets(insets)
    }

    override fun dispatchApplyWindowInsets(insets: WindowInsets?): WindowInsets {
        return super.dispatchApplyWindowInsets(insets)
    }
}