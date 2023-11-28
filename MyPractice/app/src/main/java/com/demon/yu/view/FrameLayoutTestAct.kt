package com.demon.yu.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.demon.yu.view.recyclerview.ColorUtils
import com.example.mypractice.R
import kotlinx.android.synthetic.main.activity_tab_layout.add

/**
 * @description
 * @author yujinta.529
 * @create 2023-11-20
 */
class FrameLayoutTestAct : AppCompatActivity() {
    private val listData = mutableListOf<ItemData>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_framelayout_test_view)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.itemAnimator = null
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        listData.add(ItemData(height = 100))
        listData.add(ItemData(height = 800))
        listData.add(ItemData(height = 200))
        recyclerView.adapter = object : RecyclerView.Adapter<ViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                return object : ViewHolder(
                    LayoutInflater.from(this@FrameLayoutTestAct)
                        .inflate(R.layout.activity_framelayout_test_item_view, parent, false)
                ){

                }
            }

            override fun getItemCount(): Int {
                return listData.size
            }

            override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                val itemData = listData[position]
                holder.itemView.setBackgroundColor(itemData.color)
                holder.itemView.layoutParams.height = itemData.height
                holder.itemView.tag = position
            }
        }
        findViewById<View>(R.id.add).setOnClickListener {
            val oldList = ArrayList(listData)
            listData.add(ItemData())
            DiffUtil.calculateDiff(DiffCallback(listData, oldList))
                .dispatchUpdatesTo(recyclerView.adapter!!)
        }
        val btn = findViewById<View>(R.id.update)
        btn.setOnClickListener {
            val oldList = ArrayList(listData)
            listData.removeLast()
            listData.removeLast()
            listData.add(ItemData(height = 800))
            listData.add(ItemData(height = 200))
            DiffUtil.calculateDiff(DiffCallback(listData, oldList))
                .dispatchUpdatesTo(recyclerView.adapter!!)
        }
    }


    class ItemData(
        val color: Int = ColorUtils.getRandomColor(),
        val height: Int = ColorUtils.getRandomHeight()
    )

    private class DiffCallback(private val newList: List<*>, private val oldList: List<*>) :
        DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return newList[newItemPosition] == oldList[oldItemPosition]
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return return newList[newItemPosition] == oldList[oldItemPosition]
        }

    }
}