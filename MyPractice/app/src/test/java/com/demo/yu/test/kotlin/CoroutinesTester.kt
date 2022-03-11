package com.demo.yu.test.kotlin

import com.demon.yu.kotlin.CoroutinesEventBus
import com.demon.yu.kotlin.EventSubscriber
import com.demon.yu.kotlin.eventCoroutineScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.junit.Test
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.system.measureTimeMillis


/**
 *
 * [runBlocking]: 非suspend函数，可用于普通函数,开启一个BlockingCoroutine新的协程作用域，同时会阻塞当前线程的执行。
 *
 * [coroutineScope]:suspend函数，和runBlocking差不多，启动一个协程，但是不会阻塞调用此函数的线程。
 * [supervisorScope]
 * [launch]: 在当前协程scope里创建一个新的scope，只能在协程作用域里调用，因为本身是Coroutine的扩展方法，函数本身的参数可以见其实现，又明确的说明
 *
 * [withContext]: suspend函数。
 *
 * [GlobalScope]: 全局的协程scope，使用[EmptyCoroutineContext]
 *
 * [async]:CoroutineScope扩展函数，只能在协程作用域里调用。
 *
 * [CoroutineContext]: 一堆元素集合的上下文，比较重要的元素有[job] [CoroutineDispatcher]
 *
 * [CoroutineDispatcher]: 决定相关的协程在哪个线程或哪些线程上执行，协程调度器可以将协程限制在一个特定的线程执行，
 *                        或将它分派到一个线程池，亦或是让它不受限地运行。有四个常用的：
 *                        [Dispatchers.Unconfined] ：特殊的一个派发器，似乎是main，但是官方文档也没说明
 *                        [Dispatchers.Main] : 主线程，android对应的就是uithread,[Dispatchers.Main.immediate]这个和main差不多，
 *                                             以android为例：main总是通过post触发，而immediate检测到是mainThread的话，则直接调用
 *                        [Dispatchers.io] : io线程，用于频繁io的线程
 *                        [Dispatchers.default] : 默认调度器,GlobalScope所使用的
 *
 * [sequence]: 异步流，是同步的，会阻塞主线程
 *
 * [buildSequence]:废弃，使用sequence代替
 *
 * [flow]: FlowCollector，也是异步流，是非同步的，不会阻塞主线程，和sequence的区别可以看 [CoroutinesTester.flowTest1] [CoroutinesTester.sequenceTest1]
 *         支持Backpressure，有几种模式：buffer，conflate，collectLatest等等,用法区别见 [CoroutinesTester.flowTest2] [CoroutinesTester.flowTest3] 等
 *         flow还有一些生命周期的操作符，start，onCompletion，还有一些集合类型的操作符map，first等，已经线程切换操作符flowOn等，flow取消则是通过coroutineJob处理
 *         还有一些操作形式 .asFlow，将集合数据转化成flow,异常处理则是通过retry，catch或者onCompletion来完成，另外：flow不允许在其block改变线程上下文，需要通过flowOn去改变
 *
 *         异常处理：catch 只是中间操作符不能捕获下游的异常，对于下游的异常，可以多次使用 catch 操作符来解决。
 *         除了传统的 try...catch 之外，还可以借助 onEach 操作符。把业务逻辑放到 onEach 操作符内，在 onEach 之后是 catch 操作符，最后是 collect()
 *                ：retryWhen
 *
 *
 *         线程切换：flowOn,只会影响上游操作。
 *
 *         并行操作：buffer及flatMapMerge 遗留
 *
 *         其他操作符：onEach 遗留
 *
 *         合并操作符：zip，combine，flattenMerge,flatMapMerge,flatMapConcat,flatMapLatest
 *                   zip: 合并flow，新的 flow 的 item 个数 = 较小的 flow 的 item 个数。如果个数是动态那么就等待双发个相同数字的flow数据出现才会执行那一次的collect
 *                   combine :合并flow，第一个flow会一直和第二个flow最后一次数据合并发出，具体规则可以看 [CoroutinesTester.flowWithCombineTest]
 *
 *                   flattenMerge: flattenMerge 不会组合多个 flow ，而是将它们作为单个流执行，并行执行的
 *
 *                   flattenConcat : flattenMerge 不会组合多个 flow ，而是将它们作为单个流执行 串行执行的
 *
 *                   flatMapMerge: 就是map和flattenMerge的组合
 *
 *                   flatMapConcat:就是map和flattenConcat的组合
 *
 *                   flatMapLatest: 当发射了新值之后，上个 flow 如果没有处理完就会被取消。
 *
 * [launchIn]: 实现很简单，作用就是在单独的协程中启动流的收集。
 *
 *
 *
 * TODO 可以实现一个eventBus
 * [SharedFlow],[MutableSharedFlow]:是热流，事件被广播给未知数量的订阅者。在没有订阅者的情况下，任何发布的事件都会立即删除。它是一种用于必须立即处理或根本不处理的事件的设计模式
 * https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-shared-flow/index.html?query=interface%20SharedFlow%3Cout%20T%3E%20:%20Flow%3CT%3E
 *
 * [StateFlow],[MutableStateFlow]:是热流
 * https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-mutable-state-flow/index.html
 *
 * [RestrictsSuspension]: 注解： Classes and interfaces marked with this annotation are restricted when used as receivers for extension
 *                        `suspend` functions. These `suspend` extensions can only invoke other member or extension `suspend` functions on this particular
 *                        receiver and are restricted from calling arbitrary suspension functions.
 *
 *
 *
 * [newSingleThreadContext]: 用于单独线程Dispatcher
 *
 *
 * 关于cancel的英文说明：https://medium.com/androiddevelopers/cancellation-in-coroutines-aa6b90163629
 *
 *
 * Q:这是什么语法？  context[CoroutineExceptionHandler]  = context[CoroutineExceptionHandler.Key]
 *  通过反编译看，最终都是通过get(CoroutineExceptionHandler.Key)来获取的
 *
 *
 */

fun CoroutineScope.log(message: String) {
    println("${Thread.currentThread().name} : $message")
}

fun threadName() = Thread.currentThread().name

class CoroutinesTester {

    val scope = MainScope()

    private fun log(message: String) = println("${Thread.currentThread().name} : $message")

    @Test
    fun testRunBlock() {
        invokeCanncle()
    }

    fun invokeCanncle() = runBlocking {
        val job = launch {
            log("testSuspendCoroutine start")
            val resultSus = testSuspendCoroutine()
            log("testSuspendCoroutine end $resultSus")
            doWord("test")

        }

        val handle = job.invokeOnCompletion {
            log("invokeOnCompletion call " + it?.message)
        }
    }

    suspend fun testSuspendCoroutine() = suspendCoroutine<Int> {
        Thread {
            log("testSuspendCoroutine inner start")
            Thread.sleep(2000L)
//            it.resumeWith(Result.success(4))
            it.resumeWithException(RuntimeException("error"))
            log("testSuspendCoroutine inner end")
        }.start()

    }


    fun main() = runBlocking {
        val result = launch {
            doWord("launch")
            doWord2()
        }
        doWord2()
        doWord("main")
        log("Hello $result")
    }

    // this is your first suspending function
    suspend fun doWord(name: String): String {
        delay(1000L)
        log("World! $name")
        return ""
    }

    suspend fun doWord2() = coroutineScope {
        delay(2000L)
        log("World2! $this")
    }

    @Test
    fun mainInner() = runBlocking {
        runBlockingTest()
    }


    //runBlocking or coroutineScope or launch

    fun runBlockingTest() = runBlocking {
        log("runBlockingTest start")
        coroutineScopeTest()
        val job = launch {
            delay(1000L)
            log("runBlockingTest inner launch")
        }
        log("runBlockingTest end")
    }

    suspend fun coroutineScopeTest() = coroutineScope {
        log("coroutineScopeTest start")
        val job = launch {
            delay(1000L)
            log("coroutineScopeTest inner launch")
        }
        log("coroutineScopeTest end")
    }


    suspend fun withContextTest() = withContext(Dispatchers.Main) {
        log("withContextTest start")
        withContext(Dispatchers.IO) {
            delay(1000L)
            log("withContextTest io thread")
        }
        log("withContextTest end")
    }


    suspend fun asyncTest() = coroutineScope {
        val job1 = async {
            delay(5000L)
            5
        }
        val job2 = async {
            delay(8000L)
            6
        }
        val repeatJob = launch {
            repeat(100) {
                log("repeat $it")
                delay(1000L)
            }
        }
        log("job1 + job2 = ${job1.await() + job2.await()}")
        repeatJob.cancel()
    }


    suspend fun newThreadTest() {
        newSingleThreadContext("Ctx1").use { ctx1 ->
            newSingleThreadContext("Ctx2").use { ctx2 ->
                runBlocking(ctx1) {
                    log("Started in ctx1")
                    withContext(ctx2) {
                        log("Working in ctx2")
                    }
                    log("Back to ctx1")
                }
            }
        }
    }

    suspend fun parentScopeTest() = supervisorScope {
        // 启动一个协程来处理某种传入请求（request）

        val request = launch(Dispatchers.Default + CoroutineName("outer")) {
            repeat(3) { i -> // 启动少量的子作业
                launch(CoroutineName("inner")) {
                    delay((i + 1) * 200L) // 延迟 200 毫秒、400 毫秒、600 毫秒的时间
                    log("Coroutine $i is done")
                }
            }
            println("request: I'm done and I don't explicitly join my children that are still active")
        }
        //request.join() // 等待请求的完成，包括其所有子协程完成后才会继续向下执行，如果没有这句话，子协程同样会执行完成，但是会在下面输出后才执行万
        log("Now processing of the request is complete")
    }

    @Test
    fun sequenceTest() = runBlocking {
        sequenceInit().forEach {
            log("sequenceTest forEach $it")
        }
    }

    fun sequenceInit() = sequence {
        GlobalScope.launch {
            for (i in 1..3) {
                delay(1000L)
                log("sequenceInit  GlobalScope.launch $i")
            }
        }
        for (i in 1..3) {
            Thread.sleep(1000) //不可以使用delay，以为不是coroutineScope
            log("sequenceInit start yield")
            yield(i) // 产生下一个值
            log("sequenceInit end yield")
        }
    }


    suspend fun simple(): List<Int> {
        log("simple delay 1000")
        delay(1000) // 假装我们在这里做了一些异步的事情
        return listOf(1, 2, 3)
    }

    fun simpleTest() = runBlocking<Unit> {
        simple().forEach { value -> log(value.toString()) }
    }


    fun flowInit() = flow<Int> {
        (1..3).forEach {
            delay(1000L)
            log("flowInit emit head")
            emit(it)
            log("flowInit emit end")
        }
    }

    @Test
    fun flowTest() = runBlocking {
        // 启动并发的协程以验证主线程并未阻塞
        launch {
            for (k in 1..3) {
                log("I'm not blocked $k")
                delay(1000)
            }
        }
        flowInit().onCompletion {
            log("flowTest onCompletion $it") //it throwable?
        }.onStart {
            log("flowTest onStart ")
        }.buffer().collect { value ->
            log("flowTest collect $value")
        }
    }

    @Test
    fun flowTest1() = runBlocking {
        launch {
            for (k in 1..5) {
                log("I'm not blocked $k")
                delay(100)
            }
        }
        flow {
            for (i in 1..5) {
                emit(i)
                delay(100)
            }
        }.collect {
            log("flowTest1 $it")
        }
    }

    @Test
    fun flowTest2() = runBlocking {
        flow {
            List(20) {
                emit(it)
                delay(50)
            }
        }.collectLatest {
            log("flowTest1 before delay $it")
            delay(200)
            log("flowTest1 after delay $it")
        }
    }

    @Test
    fun flowTest3() = runBlocking {
        flow {
            List(20) {
                emit(it)
                delay(50)
            }
        }.conflate().collect {
            log("flowTest1 before delay $it")
            delay(200)
            log("flowTest1 after delay $it")
        }
    }

    @Test
    fun flowTestWithFlowOn() = runBlocking {
        flow {
            List(20) {
                emit(it)
                delay(100L)
                log("flowTestWithFlowOn")
            }
        }.flowOn(Dispatchers.IO).collect {
            log("flowTestWithFlowOn  $it")
        }
    }

    @Test
    fun flowTestWithException() = runBlocking {
        flow {
            List(20) {
                emit(it)
                delay(100L)
                log("flowTestWithFlowOn")
                if (it == 3) {
                    throw IllegalStateException("error")
                }
            }
        }.catch { throwable ->
            log("flowTestWithException catch throwable $throwable")
        }.onCompletion { throwable -> //Throwable?
            log("flowTestWithException onCompletion $throwable") //如果前面没有catch的话，throwable则有值，但是无论如何都会调用此处
        }.flowOn(Dispatchers.IO).collect {
            log("flowTestWithFlowOn  $it")
        }
    }

    @Test
    fun sequenceTest1() = runBlocking {
        launch {
            for (k in 1..5) {
                log("I'm  blocked $k")
                delay(100)
            }
        }
        sequence {
            for (i in 1..5) {
                yield(i)
                Thread.sleep(100)
            }
        }.forEach {
            log("flowTest1 $it")
        }
    }


    @Test
    fun flowTestWithBuffer() = runBlocking {


        var time = measureTimeMillis {
            (1..100).asFlow().buffer().onEach {
                log("flowTestWithOnEach $it")
            }.collect {
                log("flowTestWithBuffer $it")
            }
        }
        log("flowTestWithBuffer  cast $time")


        time = measureTimeMillis {
            (1..100).asFlow().collect {
                log("flowTestNoBuffer $it")
            }
        }
        log("flowTestNoBuffer cast $time")

//        time = measureTimeMillis {
//            (1..200).asFlow().flatMapMerge {
//                flow {
//                    emit(it)
//                }
//            }.collect {
//                delay(50)
//                log("flowTestWithBuffer buffer $it")
//            }
//        }
        log("flowTestWithBuffer flatmapMerge cast $time")
    }


    @Test
    fun flowWithMapTest() = runBlocking {
        (1..4).asFlow().map {
            flow {
                emit("$it first")
            }
        }.flattenMerge(16).collect {

        }
    }

    @Test
    fun flowWithZipTest() = runBlocking {

//        val flow1 = (1..5).asFlow().onEach { delay(80L) }
//        val flow2 = flowOf("one", "two", "three", "four").onEach { delay(100L) }
//        flow1.zip(flow2) { i, name ->
//            "$i + $name"
//        }.collect {
//            log("flowWithZipTest $it")
//        }
        val flow1 = flow {
            (1..100).forEach {
                delay(1000L)
                emit(it)
            }
        }

        val flow2 = flow {
            (1..100).forEach {
                delay(2000L)
                emit(it)
            }
        }
        flow1.zip(flow2) { i, j ->
            i + j
        }.collect {
            log("flowWithZipTest $it")
        }
    }

    @Test
    fun flowWithCombineTest() = runBlocking {

//        val flow1 = (1..5).asFlow().onEach { delay(80L) }
//        val flow2 = flowOf("one", "two", "three", "four").onEach { delay(100L) }
//        flow1.combine(flow2) { i, name ->
//            "$i + $name"
//        }.collect {
//            log("flowWithZipTest $it")
//        }
        val flow1 = flow {
            (1..100).forEach {
                delay(1000L)
                emit(it)
            }
        }

        val flow2 = flow {
            (1..100).forEach {
                delay(2000L)
                emit(it)
            }
        }
        flow1.combine(flow2) { i, j ->
            "$i $j"
        }.collect {
            log("flowWithCombineTest $it")
        }
    }

    @Test
    fun flowWithFlattenMergeTest() = runBlocking {
        val flow1 = flow {
            (1..5).forEach {
                delay(1000L)
                emit(it)
            }
        }

        val flow2 = flow {
            (6..10).forEach {
                delay(2000L)
                emit(it)
            }
        }
        flowOf(flow1, flow2).flattenMerge().collect {
            log("flowWithFlattenMergeTest $it")
        }
    }

    @Test
    fun flowWithFlattenConcatTest() = runBlocking {
//        val flow1 = flow {
//            (1..5).forEach {
//                delay(1000L)
//                emit(it)
//            }
//        }
//
//        val flow2 = flow {
//            (6..10).forEach {
//                delay(1000L)
//                emit(it)
//            }
//        }
        val flow1 = flow {
            (1..100).forEach {
                delay(1000L)
                emit(it)
            }
        }

        val flow2 = flow {
            (1..100).forEach {
                delay(2000L)
                emit(it)
            }
        }
        flowOf(flow1, flow2).flattenConcat().collect {
            log("flowWithFlattenConcatTest $it")
        }
    }

    @Test
    fun flowWithFlatMapLatestTest() = runBlocking {
        val flow1 = flow {
            (1..5).forEach {
                delay(1000L)
                log("flow1 start emit $it")
                emit(it)
            }
        }

        flow1.flatMapLatest {
            flow {
                delay(1500L)//会影响结果
                log("flow2 start emit $it")
                emit("second + $it")
            }
        }.collect {
            log("flowWithFlatMapLatestTest $it")
        }
    }


    @Test
    fun flowWithLaunchInTest() = runBlocking {
        (1..5).asFlow().onEach {
            log("flowWithLaunchInTest $it")
        }.collect()
        log("flowWithLaunchInTest done ")


        (1..5).asFlow().onEach {
            log("flowWithLaunchInTest launchIn onEach $it")
        }.launchIn(this)

        log("flowWithLaunchInTest launchIn onEach done")
    }


    @Test
    fun flowTestWithSharedFlow() = runBlocking {
        flow {
            List(20) {
                emit(it)
                delay(100L)
                log("flowTestWithSharedFlow")
                if (it == 3) {
                    throw IllegalStateException("error")
                }
            }
        }.catch { throwable ->
            log("flowTestWithSharedFlow catch throwable $throwable")
        }.onCompletion { throwable -> //Throwable?
            log("flowTestWithSharedFlow onCompletion $throwable") //如果前面没有catch的话，throwable则有值，但是无论如何都会调用此处
        }.flowOn(Dispatchers.IO).shareIn(this, SharingStarted.WhileSubscribed()).collect {
            log("flowTestWithSharedFlow collect $it")
        }
    }

    @Test
    fun flowTestWithStateFlow() = runBlocking {
        val coroutineScope = CoroutineScope(Dispatchers.IO)
        val stateFlow = MutableStateFlow(false)
        coroutineScope.launch {
            stateFlow.value = true
            stateFlow.collect {
                log("flowTestWithStateFlow collect $it")
            }
        }
        val job = coroutineScope.launch {
            delay(2000L)
            stateFlow.value = false
        }
        job.join()
        coroutineScope.cancel()
    }

    @Test
    fun onFlowEventTest1() = runBlocking {
//        CoroutinesEventBus.register(FlowEvent::class.java, object : EventSubscriber<FlowEvent> {
//            override fun onEvent(t: FlowEvent) {
//                log("onFlowEventTest1  $t")
//            }
//        })
//        CoroutinesEventBus.postEvent(FlowEvent("test1"))
//        CoroutinesEventBus.postEvent(FlowEvent("test2"))
//        delay(100)
//
//        CoroutinesEventBus.register(FlowEvent::class.java, object : EventSubscriber<FlowEvent> {
//            override fun onEvent(t: FlowEvent) {
//                log("onFlowEventTest1 sticker $t")
//            }
//        })
        val eventSubscriber = object : EventSubscriber<FlowEvent> {
            override fun onEvent(t: FlowEvent) {
                log("onFlowEventTest1 eventSubscriber $t")
            }
        }
        val eventSubscriber2 = object : EventSubscriber<FlowEvent> {
            override fun onEvent(t: FlowEvent) {
                log("onFlowEventTest1 eventSubscriber2 $t")
            }
        }
//        CoroutinesEventBus.register(FlowEvent::class.java, coroutineDispatcher = Dispatchers.IO, eventSubscriber = eventSubscriber)
//        CoroutinesEventBus.register(FlowEvent::class.java, coroutineDispatcher = Dispatchers.Default, eventSubscriber = eventSubscriber2)
        CoroutinesEventBus.postEvent(FlowEvent("test3"))
//        delay(1000L)
//        CoroutinesEventBus.unregister(eventSubscriber)
//        CoroutinesEventBus.postEvent(FlowEvent("test4"))


        delay(20000000L)
    }


    @Test
    fun testSuspend() = runBlocking {
        log("testSuspend start ${threadName()}")
        val flowEvent = testSuspendCancellableCoroutine()
        log("testSuspend end $flowEvent")

    }

    private suspend fun testSuspendCancellableCoroutine(): FlowEvent = suspendCancellableCoroutine {

        println("testSuspendCancellableCoroutine thread ${threadName()} start")

        eventCoroutineScope.async {
            repeat(2) {
                println("....")
                delay(1000L)
            }

            println("testSuspendCancellableCoroutine GlobalScope async ${threadName()}")
        }


        it.invokeOnCancellation {
            println("suspendCancellableCoroutine invokeOnCancellation")
        }
        println("testSuspendCancellableCoroutine  thread ${threadName()} end")

    }

    data class FlowEvent(val info: String)


}