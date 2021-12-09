package com.demon.yu.kotlin

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

class CoroutinesEventBusDispatcher(private val scope: CoroutineScope, private val context: CoroutineContext? = null) {


    fun <T> dispatch(sharedFlow: SharedFlow<T>, eventSubscriber: EventSubscriber<T>): CoroutinesEventBus.JobEventSubscription = runBlocking {
        val job = scope.launch(context ?: EmptyCoroutineContext) {
            try {
                log("CoroutinesEventBusDispatcher", "dispatch launch ")
                sharedFlow.collect {
                    log("CoroutinesEventBusDispatcher", "dispatch collect $it")
                    eventSubscriber.onEvent(it)
                }
            } catch (th: Throwable) {
                //ignore
                log("CoroutinesEventBusDispatcher", "dispatch error ${th.message}")
                CoroutinesEventBus.reportError(th)
            }
        }
        CoroutinesEventBus.JobEventSubscription(job)
    }
}