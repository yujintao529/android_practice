package com.demon.yu.utils

fun uiThread(block: () -> Unit) {
    ThreadPoolUtils.getMainHandler().post(block)
}

fun ioThread(block: () -> Unit) {
    ThreadPoolUtils.threadPool.execute(block)
}