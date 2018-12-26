package com.example.mypractice.kotlin

object SingleObject {
    fun sayHello() {
        "hello"
    }
}

class CompaintObject {

    var name: String? = "yujintao"

    companion object CompanionObject {
        fun create(): CompaintObject = CompaintObject()
    }

    object Object {
        fun create(): CompaintObject = create()
    }

    inner class InnerObject {
        fun sayName() {
            name
        }
    }
}

