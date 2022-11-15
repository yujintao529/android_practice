package com.demon.yu.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatButton

/**
 * @description
 * @author yujinta.529
 * @create 2022-11-14
 */
class HorToolsView(context: Context, attributeSet: AttributeSet? = null) :
    HorizontalScrollView(context, attributeSet) {

    private val parentView = LinearLayout(context)

    init {
        parentView.setBackgroundColor(Color.WHITE)
        addView(parentView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    }

    fun addToolsBtn(title: String, tag: String, onClickListener: OnClickListener) {
        val btn = createBtn()
        parentView.addView(btn)
        btn.text = title
        btn.tag = tag
        btn.setOnClickListener(onClickListener)
    }

    private fun createBtn(): AppCompatButton {
        val appCompatButton = AppCompatButton(context)
        appCompatButton.setTextColor(Color.BLACK)
        appCompatButton.setPadding(10, 10, 10, 10)
        val lp = LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        lp.setMargins(60, 20, 60, 20)
        appCompatButton.layoutParams = lp
        return appCompatButton
    }
}