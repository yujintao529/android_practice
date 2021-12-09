package com.demon.yu.extenstion

import android.app.Activity
import android.content.Intent

fun Activity.buildIntent(activity: Class<*>): Intent {
    val intent = Intent(this, activity)
    return intent
}

fun Activity.start(activity: Class<*>): Intent {
    return buildIntent(activity).also {
        startActivity(it)
    }
}

