package com.demon.yu.avatar.interact

import android.view.ViewGroup
import androidx.recyclerview.widget.AvatarRecyclerView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.demon.yu.extenstion.dp2Px
import com.demon.yu.view.fresco.ClipSimpleDraweeView
import com.demon.yu.view.fresco.FrescoAvatarUtils
import com.demon.yu.view.recyclerview.MyCircleView
import com.example.mypractice.Logger
import com.facebook.drawee.view.SimpleDraweeView

class CloneXComposeAdapter(val onComposeAdapterListener: OnComposeAdapterListener) :
    AvatarRecyclerView.AvatarAdapter<CloneXComposeViewHolder<*>>() {


    init {
        //不要修改这个，这个adapter不需要实现itemChanged这些，因为是稳定的stableids
//        setHasStableIds(true)
        registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                super.onItemRangeChanged(positionStart, itemCount)
                Logger.debug(
                    "CloneXComposeAdapter",
                    "onItemRangeChanged positionStart =$positionStart,itemCount=$itemCount"
                )
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
                super.onItemRangeChanged(positionStart, itemCount, payload)
                Logger.debug(
                    "CloneXComposeAdapter",
                    "onItemRangeChanged positionStart =$positionStart,itemCount=$itemCount"
                )
            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                Logger.debug(
                    "CloneXComposeAdapter",
                    "onItemRangeInserted positionStart =$positionStart,itemCount=$itemCount"
                )
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                super.onItemRangeRemoved(positionStart, itemCount)
                Logger.debug(
                    "CloneXComposeAdapter",
                    "onItemRangeRemoved positionStart =$positionStart,itemCount=$itemCount"
                )
            }

            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount)
                Logger.debug(
                    "CloneXComposeAdapter",
                    "onItemRangeMoved fromPosition =$fromPosition,toPosition=$toPosition,itemCount=$itemCount"
                )
            }
        })
    }

    private val listData = mutableListOf<CloneXStaticObj>()

    fun update(data: List<CloneXStaticObj>) {
        listData.clear()
        listData.addAll(data)
        notifyDataSetChanged()
    }

    fun notifyItemChanged(data: List<CloneXStaticObj>) {
        val oldList = ArrayList<CloneXStaticObj>(listData)
        listData.clear()
        listData.addAll(data)
        DiffUtil.calculateDiff(DiffCallback(oldList, listData)).dispatchUpdatesTo(this)
    }

    fun notifyItemChanged(position: Int, obj: CloneXStaticObj) {
        if (listData.size > position) {
            listData[position] = obj
            notifyItemChanged(position)
        }
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
            return CloneXAnimViewHolder(view)
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
        Logger.debug("CloneXComposeAdapter", "onRealBind $position")
        when {
            holder is CloneXAnimViewHolder -> {
                holder.bindData(myStaticObj, position, null)
            }
            holder.itemView is MyCircleView -> {
                (holder.itemView as? MyCircleView)?.color = myStaticObj.color
                (holder.itemView as? MyCircleView)?.number = position
            }
            holder.itemView is SimpleDraweeView -> {
                FrescoAvatarUtils.bindAvatar(
                    holder.itemView,
                    "asset:///avatar1.webp",
                    60.dp2Px(),
                    60.dp2Px()
                )
                FrescoAvatarUtils.asCircle(holder.itemView)
            }
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

    class DiffCallback(val origin: List<CloneXStaticObj>, val newList: List<CloneXStaticObj>) :
        DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            return origin.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
            return super.getChangePayload(oldItemPosition, newItemPosition)
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return areContentsTheSame(oldItemPosition, newItemPosition)
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldObj = origin[oldItemPosition]
            val newObj = newList[newItemPosition]

            if (oldObj.viewType == 1 && newObj.viewType == 1) {
                return oldObj.assetAvatarUri?.toString() == newObj.assetAvatarUri?.toString()
            }
            if (oldObj.viewType == 2 && newObj.viewType == 2) {
                return oldObj.color == newObj.color
            }
            return false
        }

    }
}