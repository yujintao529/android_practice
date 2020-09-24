package com.example.mypractice.kotlin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log

class LazyActivity : AppCompatActivity() {

    private lateinit var name: String


    private val city: String by lazy {
        "北京"
    }
    private val province by ProvinceLazy {
        "北京"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        name = "god"
    }
}

class ProvinceLazy(private val _initializer: () -> String) : Lazy<String> {

    private var _value: String? = null

    private val objLock = Object()

    override val value: String
        get() {
            if (_value == null) {
                synchronized(objLock) {
                    if (_value == null) {
                        _value = _initializer!!()
                    }
                }
            }

            return _value as String
        }

    override fun isInitialized() = _value != null

}

data class User(val name: String)

class lateinitActivity : AppCompatActivity() {

    private lateinit var user: User
    private var lastUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lastUser = User(intent.getStringExtra("name"))
        user = User(intent.getStringExtra("name"))

        Log.d("lateinitActivity", "user ${user.name}")
        //        Log.d("lateinitActivity", "lastuser ${lastUser.name}")//由于kotlin可空检查机制，不允许直接使用
        Log.d("lateinitActivity", "lastuser ${lastUser?.name}")
        Log.d("lateinitActivity", "lastuser ${lastUser!!.name}")
    }
}


class UserLazy : Lazy<User> {

    private val _user1 = User("god")
    private val _user2 = User("demon")
    private var _index = 0

    private val _users = arrayOf(_user1, _user2)

    override val value: User
        get() {
            return _users[_index++ % 2]
        }

    override fun isInitialized() = true

}

val user by UserLazy()

fun main(args: Array<String>) {
    print("$user")
    print("$user")
}


