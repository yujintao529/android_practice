package com.demon.yu.utils

import android.widget.Toast
import com.example.mypractice.common.Common

object ToastUtils {
    fun toast(content: String) {
        uiThread {

        }
        Toast.makeText(Common.application, content, Toast.LENGTH_SHORT).show()
    }

}