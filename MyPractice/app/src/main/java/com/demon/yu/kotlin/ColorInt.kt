package com.demon.yu.kotlin

import android.graphics.Color

/**
 * @description
 * @author yujinta.529
 * @create 2022-11-16
 *
 * 1. 在kotlin中,0xFFFFFFFF，没有像java一样变成负数，而是像无符号一样，
 *    变成正整数，超过了int.MAX_VALUE。
 *    在java中,0xFFFFFFFFF,会变成-1。
 * 2.
 */

val colorBlack: Int = 0xFFFFFFFF.toInt()
val colorBlack2: Int = -0x1
val colorjava: Int = Color.WHITE

fun main(args: Array<String>) {
    println("colorBlack=$colorBlack colorBlack2=$colorBlack2 colorjava=$colorjava")
    println("0xFFFFFFFF ${0xFFFFFFFF}")
}
