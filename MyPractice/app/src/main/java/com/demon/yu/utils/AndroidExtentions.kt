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