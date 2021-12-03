package com.demon.yu.kotlin

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import java.lang.ref.WeakReference

/**
 * 协程版的eventBus
 * 1. MutableStateFlow :
 *  1）如果连续setValue（消费value慢于生产value），那么mutableStateFlow只会用最后的值进行collect回调。
 *  2）天生支持粘性事件？
 *
 * 2.  MutableSharedFlow<T>(0,0,BufferOverflow.SUSPEND)
 *  1) 第一个参数为0，那么就不会触发粘性事件行为
 *  2) 第一个参数为1，粘性事件
 *  3）sharedFlow会emit 所有的value给collector
 *
 * 综上来看，MutableSharedFlow最适合用来做触发器哈哈
 *
 * q:
 * 1. emit后，什么时间点触发collect及如何处理cancel。如果先emit value然后cancel，如何达到先触发collect再触发cancel呢
 *   a: 最终是通过[CancellableContinuation]的resume/resumeWith(Result)来触发collect。
 *   b: 关于emit及cancel时序的问题，目前没有解决思路（除非自己控制emit及cancel的时序，但是不太好）
 *
 * 2. [SharedFlow.collect]如何实现持续观察的？延伸来说，koltin如何实现唤醒的？
 *   a: collect 有两个路径：1.没有emit的value时，会进行等待，2. 有value时，则进行collect的回调处理
 *   no_value: collect -> awaitValue->suspendCancellableCoroutine->suspendCoroutineUninterceptedOrReturn->CancellableContinuationImpl.getResult
 *   has_value:collect -> [SharedFlowImpl.tryTakeValue] [返回no_value则会进入，awaitValue]->[FlowCollector.emit]
 *
 *   [CancellableContinuationImpl.getResult]会有两种返回，1.[IntrinsicsKt.COROUTINE_SUSPENDED][CoroutineSingletons.COROUTINE_SUSPENDED] 代表会暂停suspend
 *                                                      2.似乎是emit的value？？？存疑需要调试
 *
 * 3. [suspendCancellableCoroutine],[CancellableContinuation.suspendCoroutineUninterceptedOrReturn] kotlin coroutines提供的一个封装,用来处理暂停及恢复的流程及cancel时的一些处理
 *    封装的使用相对固定，可以参考suspendCancellableCoroutine的实现
 *    如果是自定义的一些suspend行为，需要支持取消的，可以使用suspendCancellableCoroutine
 *
 * 4. scope context job为什么创造了这么多概念？https://medium.com/@elizarov/coroutine-context-and-scope-c8b255d59055
 *
 * 5. collect如何感知cancel并抛出异常？
 *    a: collect里有两个when(true)潜逃的循环，如果没有新数据会await，有的话会触发collect的拉姆达函数。每次都会去判断ensureActive()
 */


private val _defaultValue = Any()
val eventCoroutineScope = CoroutineScope(newSingleThreadContext("eventBus-thread"))
private const val tag = "CoroutinesEventBus"


object CoroutinesEventBus {

    @ObsoleteCoroutinesApi
    val stateFlow = MutableStateFlow(Any())


    private val mutex = Mutex()
    private val mutableMap = mutableMapOf<String, ObserverObject<*>>()
    private val mutableTypeMap = mutableMapOf<EventSubscriber<*>, Class<*>>()
    val coroutinesDispatcher = CoroutinesEventBusDispatcher()
    var onError: ((throwable: Throwable) -> Unit)? = null

    fun <T> register(type: Class<T>, coroutineDispatcher: CoroutineDispatcher = Dispatchers.Main, eventSubscriber: EventSubscriber<T>): EventSubscription {
        val observerObject: ObserverObject<T>
        if (mutableMap.containsKey(type.simpleName)) {
            observerObject = mutableMap[type.simpleName] as ObserverObject<T>
        } else {
            observerObject = ObserverObject(type)
            mutableMap[type.simpleName] = observerObject
        }
        mutableTypeMap[eventSubscriber] = type
        return observerObject.addEventObserver(eventSubscriber)
    }

    fun <T> unregister(eventSubscriber: EventSubscriber<T>) {
        val type = mutableTypeMap[eventSubscriber]
        if (type != null) {
            val observerObject = mutableMap[type.simpleName]
            observerObject?.removeEventObserver(eventSubscriber)
        }
    }

    private fun reportError(error: Throwable) {
        eventCoroutineScope.async {
            onError?.invoke(error)
        }
    }


    fun postEvent(any: Any) {
        val observerObject = mutableMap[any.javaClass.simpleName]
        observerObject?.subscribe(any)
    }

    private class ObserverObject<T>(val type: Class<T>) {

        private val mutableList = mutableListOf<EventSubscriber<*>>()

        //        val stateFlow = MutableStateFlow(_defaultValue)
        val sharedFlow = MutableSharedFlow<T>(0, 0, BufferOverflow.SUSPEND)
        val jobCollections = mutableMapOf<EventSubscriber<*>, WeakReferenceJob>()
        fun addEventObserver(eventSubscriber: EventSubscriber<T>, dispatcher: CoroutineDispatcher): EventSubscription {
            if (mutableList.contains(eventSubscriber)) {
                throw CoroutinesEventBusException("eventSubscriber($eventSubscriber) has registered,please register once ")
            }
            mutableList.add(eventSubscriber)

            val job = eventCoroutineScope.launch {
                try {
                    sharedFlow.collect {
                        System.out.println("collect $it")
                        eventSubscriber.onEvent(it)
                    }
                } catch (th: Throwable) {
                    //ignore
                    reportError(th)
                }
            }
            try {
                coroutinesDispatcher.dispatch(sharedFlow, eventSubscriber, dispatcher)
            } catch (th: Throwable) {
                reportError(th)
            }

            val subscription = JobEventSubscription(job)
            jobCollections[eventSubscriber] = WeakReferenceJob(subscription)
            return subscription
        }

        fun removeEventObserver(eventSubscriber: EventSubscriber<*>) {
            mutableList.remove(eventSubscriber)
            jobCollections.remove(eventSubscriber)?.cancel()
        }

        fun subscribe(any: Any) {
            Dispatchers.Default
            eventCoroutineScope.launch {
                println("subscribe $any")
                sharedFlow.emit(any as T)
            }

        }
    }

    private class JobEventSubscription(private val job: Job) : EventSubscription {
        override fun cancel() {
            job.cancel()
        }
    }


    private class WeakReferenceJob(eventSubscription: EventSubscription) : WeakReference<EventSubscription>(eventSubscription) {

        fun cancel() {
            get()?.also {
                eventCoroutineScope.launch {
                    println("cancel $it")
                    it.cancel()
                }
            }
        }
    }


}


