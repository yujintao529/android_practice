package com.demon.yu.kotlin

import kotlin.coroutines.CoroutineContext

class EventBusCoroutinesDispatcher : CoroutineContext.Element {

    companion object Key : CoroutineContext.Key<EventBusCoroutinesDispatcher>

    override val key: CoroutineContext.Key<*>
        get() = Key


}