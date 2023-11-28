package com.demon.yu.utils

/**
 * @description
 * @author yujinta.529
 * @create 2023-03-22
 */
enum class MemoryUnit {
    B {
        override fun toB(value: Float) = value
        override fun toKB(value: Float) = value / (C1 / C0)
        override fun toMB(value: Float) = value / (C2 / C0)
        override fun toGB(value: Float) = value / (C3 / C0)
    },
    KB {
        override fun toB(value: Float) = d(value, C1 / C0, MAX)
        override fun toKB(value: Float) = value
        override fun toMB(value: Float) = value / (C2 / C1)
        override fun toGB(value: Float) = value / (C3 / C1)
    },
    MB {
        override fun toB(value: Float) = d(value, C2 / C0, MAX)
        override fun toKB(value: Float) = d(value, C2 / C1, MAX)
        override fun toMB(value: Float) = value
        override fun toGB(value: Float) = value / (C3 / C2)
    },
    GB {
        override fun toB(value: Float) = d(value, C3 / C0, MAX)
        override fun toKB(value: Float) = d(value, C3 / C1, MAX)
        override fun toMB(value: Float) = d(value, C3 / C2, MAX)
        override fun toGB(value: Float) = value
    };

    open fun toB(value: Float): Float {
        throw AbstractMethodError()
    }

    open fun toKB(value: Float): Float {
        throw AbstractMethodError()
    }

    open fun toMB(value: Float): Float {
        throw AbstractMethodError()
    }

    open fun toGB(value: Float): Float {
        throw AbstractMethodError()
    }

    internal val C0 = 1L
    internal val C1 = C0 * 1024L
    internal val C2 = C1 * 1024L
    internal val C3 = C2 * 1024L

    val MAX = Float.MAX_VALUE

    /**
     * Scale d by m, checking for overflow.
     * This has a short name to make above code more readable.
     */
    open fun d(d: Float, m: Long, over: Float): Float {
        if (d > +over) return Float.MAX_VALUE
        return if (d < -over) Float.MIN_VALUE else d * m
    }
}