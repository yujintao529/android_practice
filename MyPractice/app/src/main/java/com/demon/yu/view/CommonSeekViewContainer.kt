package com.demon.yu.view

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import com.demon.yu.utils.dp2px

/**
 * 只能配合CommonSeekView使用
 */
class CommonSeekViewContainer(context: Context, attrs: AttributeSet? = null) : ScrollView(context, attrs) {

    private val child: LinearLayout = LinearLayout(context)

    init {
        child.orientation = LinearLayout.VERTICAL
        addView(child, LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        child.orientation = LinearLayout.VERTICAL
    }

    fun createSeekView(): CommonSeekView {
        val commonSeekView = CommonSeekView(context)
        val lp = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        lp.setMargins(0, 10.dp2px(), 0, 0)
        child.addView(commonSeekView, lp)
        return commonSeekView
    }
}