package com.demon.yu.utils

object MathUtils {
    fun clamp(dest: Float, min: Float, max: Float): Float {
        if (dest > max)
            return max
        if (dest < min)
            return min
        return dest
    }
}