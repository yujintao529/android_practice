package com.demon.yu.avatar.interact

import android.view.ViewGroup
import androidx.recyclerview.widget.AvatarRecyclerView
import androidx.recyclerview.widget.RecyclerView
import com.demon.yu.extenstion.dp2Px
import com.demon.yu.view.fresco.ClipSimpleDraweeView
import com.demon.yu.view.fresco.FrescoAvatarUtils
import com.demon.yu.view.recyclerview.MyCircleView
import com.facebook.drawee.view.SimpleDraweeView

class CloneXComposeAdapter(val onComposeAdapterListener: OnComposeAdapterListener) :
    AvatarRecyclerView.AvatarAdapter<CloneXComposeViewHolder<*>>() {


    init {
        //不要修改这个，这个adapter不需要实现itemChanged这些，因为是稳定的stableids
        setHasStableIds(true)
    }

    private val listData = mutableListOf<CloneXStaticObj>()

    fun update(data: List<CloneXStaticObj>) {
        listData.clear()
        listData.addAll(data)
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun getObjectByPosition(position: Int): Any? {
        if (listData.size <= position) {
            return null
        }
        return listData[position]
    }

    override fun onViewRecycled(holder: CloneXComposeViewHolder<*>) {
        super.onViewRecycled(holder)
        holder.release()
    }

    override fun onViewAttachedToWindow(holder: CloneXComposeViewHolder<*>) {
        super.onViewAttachedToWindow(holder)
        holder.attachedToWindow()
    }

    override fun onViewDetachedFromWindow(holder: CloneXComposeViewHolder<*>) {
        super.onViewDetachedFromWindow(holder)
        holder.detachedFromWindow()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CloneXComposeViewHolder<*> {
        if (viewType == 2) {
            val view = MyCircleView(parent.context)
            view.layoutParams = RecyclerView.LayoutParams(60.dp2Px(), 60.dp2Px())
            return CloneXStaticViewHolder(view)
        } else if (viewType == 1) {
            val view = ClipSimpleDraweeView(parent.context)
            view.layoutParams = RecyclerView.LayoutParams(
                view.getDestWidth(60.dp2Px()),
                view.getDestHeight(60.dp2Px())
            )
            return CloneXStaticViewHolder(view)
        } else {
            val simpleDraweeView = SimpleDraweeView(parent.context)
            simpleDraweeView.layoutParams = RecyclerView.LayoutParams(60.dp2Px(), 60.dp2Px())
            return CloneXStaticViewHolder(simpleDraweeView)
        }
    }


    override fun getItemViewType(position: Int): Int {
        return listData[position].viewType
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    interface OnComposeAdapterListener {
        fun onClick(model: ComposeUserModel, position: Int)
    }

    override fun onRealBind(holder: CloneXComposeViewHolder<*>, position: Int) {
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
        holder.itemView.setOnClickListener {
            onComposeAdapterListener.onClick(ComposeUserModel(), position)
        }
    }


    override fun onVisible(holder: CloneXComposeViewHolder<*>, position: Int) {
        super.onVisible(holder, position)
        holder.onVisible(position)

    }

    override fun onHide(holder: CloneXComposeViewHolder<*>, position: Int) {
        super.onHide(holder, position)
        holder.onHide(position)
    }
}