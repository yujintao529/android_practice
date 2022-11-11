package com.demon.yu.kotlin

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@ExperimentalContracts
fun String?.isNotNullOrEmpty(): Boolean {
    contract {
        returns(true) implies (this@isNotNullOrEmpty != null)
    }
    return this != null && this.isNotBlank()
}

@ExperimentalContracts
fun String?.isNullOrEmpty(): Boolean {
    contract {
        returns(false) implies (this@isNullOrEmpty != null)// implies (this@isNullOrEmpty != null)
    }
    return this == null || this.isNotBlank()
}

@ExperimentalContracts
fun <T> List<T>?.getFirst(): T? {
    contract {
        returnsNotNull() implies (this@getFirst != null)
    }
    return this?.firstOrNull()
}

@ExperimentalContracts
fun testString() {
    var str1: String? = "str1"
    var str2: String? = "str2"
    if (str1.isNotNullOrEmpty() && str2.isNotNullOrEmpty()) {
        join(str1, str2)
    }

    //not无法约定出来，但是！可以约定出来
    if (!str1.isNullOrEmpty()) {
        join(str1, str1)
    }
    if (str1.isNullOrEmpty().not()) {
        join(str1!!, str1!!)
    }
    //returnsNotNull
    val listStudents: MutableList<Int>? = mutableListOf()
    if (listStudents.getFirst() != null) {
        listStudents.add(0) //如果没有returnsNotNull() implies (this@getFirst != null) 约束，此处是需要加上问号的？
    }

    getAppVersion()
}

fun join(str1: String, str2: String): String {
    return str1 + str2
}

class ContractsObject

@ExperimentalContracts
fun testAccept() {
    val obj: ContractsObject? = null
    if (checkIsNotNull(obj)) {
        accept(obj)
    }
}
@ExperimentalContracts
fun getAppVersion() {
    val appVersion: Int
    safeRun {
        appVersion = 50
    }
}

@ExperimentalContracts
fun safeRun(runFunction: () -> Unit) {
    contract {
        //使用EXACTLY_ONCE
        callsInPlace(runFunction, kotlin.contracts.InvocationKind.EXACTLY_ONCE)
    }
    try {
        runFunction()
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}

@ExperimentalContracts
fun checkIsNotNull(obj: ContractsObject?): Boolean {
    contract {
        returns(true) implies (obj != null)
    }
    return obj != null
}

fun accept(contract: ContractsObject) {

}

@ExperimentalContracts
fun main(args: Array<String>) {
    testString()
}