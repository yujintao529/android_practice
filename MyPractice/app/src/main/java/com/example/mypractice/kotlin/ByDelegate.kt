package com.example.mypractice.kotlin

import android.app.Activity
import android.view.View
import android.widget.TextView
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Created by yujintao on 2018/2/11.
 */


fun Activity.bindToTextView(textView: TextView): ReadWriteProperty<Any?, String?> {
    return TextViewBinding(lazy { textView })
}

fun Activity.bindToTextView(textViewID: Int): ReadWriteProperty<Any?, String?> {
    return TextViewBinding(lazy { findViewById(textViewID) as TextView })
}


fun Activity.bindToVisiable(tViewID: Int, fViewID: Int): ReadWriteProperty<Any?, Boolean> {
    return StatusBinding(lazy { findViewById(tViewID) }, lazy { findViewById(fViewID) })
}

class StatusBinding(trueView: Lazy<View>, falseView: Lazy<View>) : ReadWriteProperty<Any?, Boolean> {

    private val tView by trueView
    private val fView by falseView

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) {
        if (value) {
            tView.visibility = View.VISIBLE
            fView.visibility = View.GONE
        } else {
            fView.visibility = View.VISIBLE
            tView.visibility = View.GONE
        }
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): Boolean {
        return tView.visibility == View.VISIBLE
    }
}

fun Activity.visibility(id: Int): VisiableBinding {
    return VisiableBinding(lazy { findViewById(id) })
}

class VisiableBinding(viewLazy: Lazy<View>) : ReadWriteProperty<View, Int> {

    private val view by viewLazy

    override fun getValue(thisRef: View, property: KProperty<*>): Int {
        return view.visibility
    }

    override fun setValue(thisRef: View, property: KProperty<*>, value: Int) {
        view.visibility = value
    }

}


class TextViewBinding(viewLazy: Lazy<TextView>) : ReadWriteProperty<Any?, String?> {

    val view by viewLazy

    override fun getValue(thisRef: Any?, property: KProperty<*>): String? {
        return view.text?.toString()
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: String?) {
        view.text = value
    }

}

/**
 * reified 的用法。只能在内敛函数中使用
 */
inline fun <reified T> membersOf() = T::class.members

fun main(args: Array<String>) {
    membersOf<View>().forEach {
        println(it.name)
    }
}