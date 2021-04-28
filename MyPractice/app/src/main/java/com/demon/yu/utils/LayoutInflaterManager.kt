package com.demon.yu.utils

import android.content.Context
import android.view.LayoutInflater

object LayoutInflaterManager {
    fun getInflater(context: Context): LayoutInflater {
        return LayoutInflater.from(context)
    }
}