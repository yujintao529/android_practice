package com.demon.yu.kotlin

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class CoroutinesEventBusDispatcher {


    @Throws(CancellationException::class)
    fun <T> dispatch(sharedFlow: SharedFlow<T>, eventSubscriber: EventSubscriber<T>, eventBusDispatcher: CoroutineDispatcher) = runBlocking {
        withContext(eventBusDispatcher) {
            sharedFlow.collect {
                eventSubscriber.onEvent(it)
            }
        }
    }
}