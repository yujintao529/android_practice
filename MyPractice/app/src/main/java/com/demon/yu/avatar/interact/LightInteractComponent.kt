package com.demon.yu.avatar.interact

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider

class LightInteractComponent(application: Application) : AndroidViewModel(application) {
    companion object {
        fun getLightInteractComponent(act: AppCompatActivity): LightInteractComponent {
            val viewModelProvider = ViewModelProvider(
                act,
                ViewModelProvider.AndroidViewModelFactory.getInstance(act.application)
            )
            return viewModelProvider.get(LightInteractComponent::class.java)
        }
    }

}