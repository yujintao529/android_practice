package com.demon.yu.kotlin

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlin.coroutines.CoroutineContext

@FunctionalInterface
fun interface EventSubscriber<T> {
    fun onEvent(t: T)
}

fun test(context: CoroutineContext) {
    val dest = context[CoroutineExceptionHandler]
    val dest2 = context[CoroutineExceptionHandler.Key]
    context[EventBusCoroutinesDispatcher]
}