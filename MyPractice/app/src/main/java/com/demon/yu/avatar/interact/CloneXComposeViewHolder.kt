package com.demon.yu.avatar.interact

import android.view.View
import androidx.recyclerview.widget.AvatarRecyclerView


abstract class CloneXComposeViewHolder<T>(view: View) : AvatarRecyclerView.AvatarViewHolder(view) {


    abstract fun bindData(data: T, position: Int, payload: List<Any?>?)

    open fun onScrolled() {

    }

    open fun onIdle() {

    }

    open fun release() {}
    open fun attachedToWindow() {}
    open fun detachedFromWindow() {}

    open fun onVisible(position: Int) {}
    open fun onHide(position: Int) {}
}


