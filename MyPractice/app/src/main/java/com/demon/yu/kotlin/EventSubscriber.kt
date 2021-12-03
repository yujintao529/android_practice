package com.demon.yu.kotlin

@FunctionalInterface
interface EventSubscriber<T> {
    fun onEvent(t: T)
}