package com.example.mypractice.kotlin

fun main(args: Array<String>) {
    println("chengfa " + (chufa(1, 3)))
    println(wrapInline(1, 3) { x, y ->
        println("nothing")
        return@wrapInline
    })
    val result = returnInline(2) {
        return@returnInline it * 3 //局部返回
//        return //直接推出main函数
    }
    println("result $result")
}

//内敛函数
inline fun chufa(first: Int, second: Int) = first / second

//crossinline返回。只能局部返回，不能非局部返回
inline fun wrapInline(first: Int, second: Int, crossinline bloc: (x: Int, y: Int) -> Unit) = {
    bloc(1, 3)// 可以调用
    var run = Runnable {
        bloc(1, 3) //可以调用
    }
}

//非局部返回。内敛函数的闭包参数，可以局部返回也可以非局部返回
inline fun returnInline(first: Int, bloc: (x: Int) -> Int): Int {
    return bloc(first)
}