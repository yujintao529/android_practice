package com.demon.yu.utils

import android.text.TextUtils

/**
 * CharSequence?.isEmpty():Boolean =run {
 *
 *
 * }
 */

fun CharSequence?.isEmpty(): Boolean {
    return this == null || TextUtils.isEmpty(this)
}

fun CharSequence?.isNotEmpty(): Boolean {
    return this.isEmpty().not()
}
