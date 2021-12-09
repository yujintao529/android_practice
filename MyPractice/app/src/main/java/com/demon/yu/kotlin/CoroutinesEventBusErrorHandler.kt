package com.demon.yu.kotlin

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlin.coroutines.CoroutineContext

class CoroutinesEventBusErrorHandler(override val key: CoroutineContext.Key<*>) : CoroutineExceptionHandler {


    override fun handleException(context: CoroutineContext, exception: Throwable) {
    }
}