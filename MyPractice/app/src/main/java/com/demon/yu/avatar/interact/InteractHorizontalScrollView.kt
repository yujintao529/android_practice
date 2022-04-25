package com.demon.yu.avatar.interact

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.HorizontalScrollView

class InteractHorizontalScrollView(context: Context, attrs: AttributeSet? = null) :
    HorizontalScrollView(context, attrs), View.OnTouchListener {


    init {
        setOnTouchListener(this)
    }

    var enableScroll: Boolean = true
    private var isScrolling = false
        set(value) {
            field = value
            onScrollListener?.onScroll(value)
        }

    var onScrollListener: OnScrollListener? = null
    private val scrollRunnableOnAnimation = Runnable {
        isScrolling = false
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return enableScroll && super.onInterceptTouchEvent(ev)
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (enableScroll) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    isScrolling = true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    removeCallbacks(scrollRunnableOnAnimation)
                    postOnAnimationDelayed(scrollRunnableOnAnimation, 50L)
                }
            }
            return super.onTouchEvent(event)
        } else {
            return true
        }
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        removeCallbacks(scrollRunnableOnAnimation)
        postOnAnimationDelayed(scrollRunnableOnAnimation, 50L)
    }


    interface OnScrollListener {
        fun onScroll(isScrolling: Boolean)
    }

}