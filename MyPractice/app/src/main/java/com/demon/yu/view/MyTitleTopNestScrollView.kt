package com.demon.yu.view

import android.content.Context
import android.support.v4.view.NestedScrollingParent2
import android.support.v4.view.NestedScrollingParentHelper
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.example.mypractice.Logger


class MyTitleTopNestScrollView : LinearLayout, NestedScrollingParent2 {


    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr)

    private val nestedScrollHelper: NestedScrollingParentHelper = NestedScrollingParentHelper(this)

    init {
        orientation = LinearLayout.VERTICAL
    }


    var child: View? = null

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        child = if (childCount >= 3) {
            getChildAt(0)
        } else {
            null
        }
    }

    override fun onStopNestedScroll(target: View, type: Int) {
        nestedScrollHelper.onStopNestedScroll(target, type)
    }

    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        return this.child != null && (axes and ViewCompat.SCROLL_AXIS_VERTICAL != 0)
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {
        nestedScrollHelper.onNestedScrollAccepted(child, target, axes, type)
    }


    override fun onNestedScroll(target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, type: Int) {
        Logger.d("onNestedScroll $dxConsumed,$dyConsumed,$dxUnconsumed,$dyUnconsumed")
        if (child == null) {
            return
        }
        val childHeight = child!!.height


        if (dyUnconsumed < 0) {
            var leftScrollY = Math.min(childHeight, scrollY)
            var last = if (leftScrollY >= Math.abs(dyUnconsumed)) dyUnconsumed else leftScrollY * -1
            scrollBy(0, last)
        }
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        Logger.d("onNestedPreScroll $dx,$dy")
        if (child == null) {
            return
        }
        val childHeight = child!!.height


        if (dy > 0) {
            var leftScrollY = Math.min(childHeight, childHeight - scrollY)
            var last = if (leftScrollY >= dy) dy else leftScrollY
            scrollBy(0, last)
            consumed[1] = last
        }

    }

    override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
        return false
    }

}