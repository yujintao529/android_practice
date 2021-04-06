package com.demon.yu.utils

import android.text.TextUtils

fun CharSequence?.isEmpty(): Boolean {
    return this == null || TextUtils.isEmpty(this)
}

fun CharSequence?.isNotEmpty(): Boolean {
    return this.isEmpty().not()
}
