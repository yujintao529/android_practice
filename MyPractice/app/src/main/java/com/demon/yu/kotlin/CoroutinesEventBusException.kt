package com.demon.yu.kotlin

class CoroutinesEventBusException : RuntimeException {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
}