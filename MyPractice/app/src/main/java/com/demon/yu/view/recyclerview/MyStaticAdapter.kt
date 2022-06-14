package com.demon.yu.view.recyclerview

import android.view.ViewGroup
import androidx.recyclerview.widget.AvatarRecyclerView
import androidx.recyclerview.widget.RecyclerView
import com.demon.yu.extenstion.dp2Px
import com.demon.yu.view.fresco.ClipSimpleDraweeView
import com.demon.yu.view.fresco.FrescoAvatarUtils
import com.facebook.drawee.view.SimpleDraweeView

class MyStaticAdapter() : AvatarRecyclerView.AvatarAdapter<MyStaticViewHolder>() {

    private val listData = mutableListOf<MyStaticObj>()

    init {
        setHasStableIds(true)
    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

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
        } else if (viewType == 1) {
            val view = ClipSimpleDraweeView(parent.context)
            view.layoutParams = RecyclerView.LayoutParams(
                view.getDestWidth(60.dp2Px()),
                view.getDestHeight(60.dp2Px())
            )
            return MyStaticViewHolder(view)
        } else {
            val simpleDraweeView = SimpleDraweeView(parent.context)
            simpleDraweeView.layoutParams = RecyclerView.LayoutParams(60.dp2Px(), 60.dp2Px())
            return MyStaticViewHolder(simpleDraweeView)
        }

    }

    override fun getItemViewType(position: Int): Int {
        return listData[position].viewType
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onRealBind(holder: MyStaticViewHolder, position: Int) {
        val myStaticObj = listData[position]
        (holder.itemView as? MyCircleView)?.color = myStaticObj.color
        (holder.itemView as? MyCircleView)?.number = position
        if (holder.itemView is ClipSimpleDraweeView) {
            holder.itemView.initAvator()
        } else if (holder.itemView is SimpleDraweeView) {
            FrescoAvatarUtils.bindAvatar(
                holder.itemView,
                "asset:///avator.webp",
                60.dp2Px(),
                60.dp2Px()
            )
            FrescoAvatarUtils.asCircle(holder.itemView)
        }
    }

    override fun onVisible(holder: MyStaticViewHolder, position: Int) {
        super.onVisible(holder, position)
        if (holder.itemView is ClipSimpleDraweeView) {
            holder.itemView.startAnimation()
        }

    }

    override fun onHide(holder: MyStaticViewHolder, position: Int) {
        super.onHide(holder, position)
        if (holder.itemView is ClipSimpleDraweeView) {
            holder.itemView.stopAnimation()
        }
    }


}