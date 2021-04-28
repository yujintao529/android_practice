package com.demon.yu.utils

object AssertUtils {
    fun assertLT(smallValue: Float, bigValue: Float) {
        if (smallValue > bigValue) {
            throw IllegalArgumentException("$smallValue is big $bigValue")
        }
    }
}