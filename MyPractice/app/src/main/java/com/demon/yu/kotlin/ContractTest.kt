package com.demon.yu.kotlin

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@ExperimentalContracts
fun String?.isNotNullOrEmpty(): Boolean {
    contract {
        returns(true) implies (this@isNotNullOrEmpty is String)
    }
    return this != null && this.isNotBlank()
}

@ExperimentalContracts
fun testString() {
    var str1: String? = "str1"
    var str2: String? = "str2"
    if (str1.isNotNullOrEmpty() && str2.isNotNullOrEmpty()) {
        join(str1, str2)
    }
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
fun checkIsNotNull(obj: ContractsObject?): Boolean {
    contract {
        returns(true) implies (obj != null)
    }
    return obj != null
}

fun accept(contract: ContractsObject) {

}

@ExperimentalContracts
fun main(args:Array<String>){
    testString()
}