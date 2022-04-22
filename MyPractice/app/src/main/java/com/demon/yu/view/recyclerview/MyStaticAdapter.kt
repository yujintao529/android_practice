package com.demon.yu.view.recyclerview

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.demon.yu.extenstion.dp2Px
import com.demon.yu.view.fresco.ClipSimpleDraweeView

class MyStaticAdapter : RecyclerView.Adapter<MyStaticViewHolder>() {

    private val listData = mutableListOf<MyStaticObj>()

    fun update(data: List<MyStaticObj>) {
        listData.clear()
        listData.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyStaticViewHolder {
        if (viewType == 2) {
            val view = MyCircleView(parent.context)
            view.layoutParams = RecyclerView.LayoutParams(60.dp2Px(), 60.dp2Px())
            return MyStaticViewHolder(view)
        } else {
            val view = ClipSimpleDraweeView(parent.context)
            view.layoutParams = RecyclerView.LayoutParams(60.dp2Px(), 60.dp2Px())
            return MyStaticViewHolder(view)
        }

    }

    override fun onBindViewHolder(holder: MyStaticViewHolder, position: Int) {
        val myStaticObj = listData[position]
        (holder.itemView as? MyCircleView)?.color = myStaticObj.color
        (holder.itemView as? MyCircleView)?.number = position
        if (holder.itemView is ClipSimpleDraweeView) {
            holder.itemView.initAvator()
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            return 1
        } else {
            return 2
        }

    }

    override fun getItemCount(): Int {
        return listData.size
    }
}