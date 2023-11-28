package com.demon.yu.view.recyclerview

import android.graphics.Color
import java.util.*
import kotlin.random.asKotlinRandom

object ColorUtils {

    fun getRandomColor(): Int {
        val rnd = Random()
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
    }
    fun getRandomHeight():Int{
        return Random().asKotlinRandom().nextInt(300, 500)
    }
}