package com.example.mypractice.kotlin

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup

class MyView : ViewGroup {

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs) {
        System.out.println("constructor ")
    }

    init {
        System.out.println("init ")
    }


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}