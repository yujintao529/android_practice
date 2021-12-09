package com.demon.yu.kotlin

import android.app.Application
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mypractice.Logger
import com.example.mypractice.R

class Coroutines2Act : AppCompatActivity() {


    private val coroutinesViewModel: CoroutinesViewModel by viewModels { ViewModelProvider.AndroidViewModelFactory.getInstance(application) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coroutines2)
        coroutinesViewModel.init()
    }


    fun onClick(view: View) {
        CoroutinesEventBus.postEvent(Event("testCoroutines2Act"))
        CoroutinesEventBus.registerWithSticker(Event::class.java, coroutinesViewModel.viewModelScope) { t -> Logger.debug("com.demon.yu.kotlin.Coroutines2Act", "onEventWith sticker ($t) ${Thread.currentThread().name}") }
    }

    class CoroutinesViewModel(application: Application) : AndroidViewModel(application) {

        fun init() {
            CoroutinesEventBus.register(Event::class.java, viewModelScope) { t -> Logger.debug("com.demon.yu.kotlin.Coroutines2Act", "onEvent($t) ${Thread.currentThread().name}") }
        }

        override fun onCleared() {
            super.onCleared()
        }
    }


    data class Event(val msg: String)
}