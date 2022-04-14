package com.demon.yu.extenstion

import android.content.res.Resources

fun Float.dp2Px(): Int {
    val scale: Float = Resources.getSystem().displayMetrics.density
    return (this * scale + 0.5f).toInt()
}

fun Int.dp2Px(): Int {
    val scale: Float = Resources.getSystem().displayMetrics.density
    return (this * scale + 0.5f).toInt()
}