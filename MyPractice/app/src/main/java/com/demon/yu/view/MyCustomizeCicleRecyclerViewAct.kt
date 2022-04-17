package com.demon.yu.view

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.demon.yu.view.recyclerview.*
import com.example.mypractice.R

class MyCustomizeCicleRecyclerViewAct : AppCompatActivity() {

    private var circleRecyclerView: MyCustomizeCircleRecyclerView? = null
    private var layoutManger: MyCustomizeLayoutManger? = null
    private val adapter = MyStaticAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_customize_circle_recycler_act)
        circleRecyclerView = findViewById(R.id.recyclerView)
        adapter.update(createObj())
        layoutManger = MyCustomizeLayoutManger(this)
        circleRecyclerView?.layoutManager = layoutManger
        circleRecyclerView?.adapter = adapter
        circleRecyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                recyclerView as MyCustomizeCircleRecyclerView
                for (i in 0..adapter.itemCount) {

                    val child = layoutManger?.findViewByPosition(i)
                    if (child != null) {
                        child.pivotX = (child.width / 2).toFloat()
                        child.pivotY = (child.height / 2).toFloat()
                        val scale =
                            recyclerView.getScaleSize(child.getCenterX(), child.getCenterY())
                        child.scaleX = scale
                        child.scaleY = scale
                        if (i == 0) {
//                            Log.d("yujintao", "left = ${child.left},top = ${child.top}")
                            /**
                             * 2022-04-14 13:53:44.265 4595-4595/com.example.mypractice D/yujintao: input (528,1092),scale = 1.0
                             * 2022-04-14 13:53:44.148 4595-4595/com.example.mypractice D/yujintao: input (435,1092),scale = 1.0
                             */
                            Log.d(
                                "yujintao",
                                "input (${child.getCenterX()},${child.getCenterY()}),scale = $scale"
                            )
                        }
                    }
                }
            }
        })
    }

    private fun createObj(): List<MyStaticObj> {
        return (0..100).map { MyStaticObj() }.toList()
    }
}