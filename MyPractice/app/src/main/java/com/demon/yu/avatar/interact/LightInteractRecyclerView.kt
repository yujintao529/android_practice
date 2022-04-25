package com.demon.yu.avatar.interact

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.demon.yu.extenstion.dp2Px
import com.example.mypractice.R
import com.facebook.drawee.view.SimpleDraweeView

class LightInteractRecyclerView(context: Context, attrs: AttributeSet? = null) :
    RecyclerView(context, attrs) {
    private val snapHelper: PagerSnapHelper = PagerSnapHelper()

    private val lightInteractAdapter = LightInteractAdapter()

    init {
        snapHelper.attachToRecyclerView(this)
    }

    private class LightInteractViewHolder(view: View) : ViewHolder(view) {
        private val simpleDraweeView: SimpleDraweeView = SimpleDraweeView(view.context)

        fun onBind(lightInteractModel: LightInteractModel, position: Int) {
            simpleDraweeView.setImageURI(lightInteractModel.icon)
        }
    }


    fun initView() {
        addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: State
            ) {
                super.getItemOffsets(outRect, view, parent, state)
                outRect.left = 8.dp2Px()
                outRect.right = 8.dp2Px()
            }
        })
    }

    fun update(list: List<LightInteractModel>, scrollToCenter: Boolean) {
        lightInteractAdapter.update(list)
    }

    private class LightInteractAdapter() : RecyclerView.Adapter<LightInteractViewHolder>() {
        val listData = mutableListOf<LightInteractModel>()
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LightInteractViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_light_interact_view_holder_item, null)
            return LightInteractViewHolder(view)
        }

        override fun onBindViewHolder(holder: LightInteractViewHolder, position: Int) {
            val lightInteractModel = listData[position]
            holder.onBind(lightInteractModel, position)
        }

        override fun getItemCount(): Int {
            return listData.size
        }

        fun update(list: List<LightInteractModel>) {
            listData.clear()
            listData.addAll(list)
            notifyDataSetChanged()
        }
    }
}
