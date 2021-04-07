package com.demon.yu.utils

import android.content.Context
import android.util.TypedValue
import com.example.mypractice.common.Common

object UIUtils {


    //    inline fun <reified T : Number> dp2px(input: Float): T {
//        val result = TypedValue.applyDimension(
//                TypedValue.COMPLEX_UNIT_DIP,
//                input, Common.application.resources.displayMetrics)
//
//        return when (T::class) {
//            Int::class -> result.toInt() as T
//            Float::class -> result as T
//            else -> throw IllegalStateException("Type not supported")
//        }
//    }
//
    fun dp2px(dipValue: Float): Float {
        val scale = Common.application.resources.displayMetrics.density
        return dipValue * scale + 0.5f
    }

    fun px2dp(pxValue: Float): Float {
        return px2dp(Common.application, pxValue).toFloat()
    }


    fun sp2px(context: Context, sp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.resources.displayMetrics)
    }

    fun dp2px(context: Context, dipValue: Float): Float {
        val scale = context.resources.displayMetrics.density
        return dipValue * scale + 0.5f
    }

    private fun px2dp(context: Context, pxValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }
}