package com.demon.yu.utils

fun uiThread(block: () -> Unit) {
    if (ThreadPoolUtils.isMainThread()) {
        block.invoke()
    } else {
        ThreadPoolUtils.getMainHandler().post(block)
    }
}

fun ioThread(block: () -> Unit) {
    ThreadPoolUtils.threadPool.execute(block)
}

fun <T : Number> T.dp2px(): T {
    return UIUtils.dp2px(this.toFloat()) as T
}

fun <T : Number> T.px2dp(): T {
    return UIUtils.px2dp(this.toFloat()) as T
}

