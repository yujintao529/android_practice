package com.demon.yu.view

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.demon.yu.view.recyclerview.*
import com.example.mypractice.R

class MyCustomize2RecyclerViewAct : AppCompatActivity() {

    private var a2RecyclerView: MyCustomize2RecyclerView? = null
    private var layoutManger: MyCustomizeLayoutManger? = null
    private val adapter = MyStaticAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_customize_2_recycler_act)
        a2RecyclerView = findViewById(R.id.recyclerView)
        adapter.update(createObj())
        layoutManger = MyCustomizeLayoutManger(this)
        a2RecyclerView?.layoutManager = layoutManger
        a2RecyclerView?.adapter = adapter
        a2RecyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                recyclerView as MyCustomize2RecyclerView
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

            private var lastState = -1
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (lastState == -1) {
                    lastState = newState
                    return
                }
                if (lastState != newState && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    recyclerView as MyCustomize2RecyclerView
                    var destChild: View? = null
                    var maxCloseDistance: Float = Float.MAX_VALUE
                    for (i in 0..adapter.itemCount) {
                        val child = layoutManger?.findViewByPosition(i)
                        if (child != null) {
                            val distance =
                                recyclerView.getDistance(child.getCenterX(), child.getCenterY())
                            if (distance < maxCloseDistance) {
                                maxCloseDistance = distance
                                destChild = child
                            }
                            if (i == 0) {
//                            Log.d("yujintao", "left = ${child.left},top = ${child.top}")
                                /**
                                 * 2022-04-14 13:53:44.265 4595-4595/com.example.mypractice D/yujintao: input (528,1092),scale = 1.0
                                 * 2022-04-14 13:53:44.148 4595-4595/com.example.mypractice D/yujintao: input (435,1092),scale = 1.0
                                 */
                                Log.d(
                                    "yujintao",
                                    "input (${child.getCenterX()},${child.getCenterY()}),scale = $distance"
                                )
                            }
                        }
                    }
                    if (destChild != null) {
                        recyclerView.scrollToCenter(destChild.getCenterX(), destChild.getCenterY())
                    }
                }
                lastState = newState
            }
        })
    }

    private fun createObj(): List<MyStaticObj> {
        return (0..100).map { MyStaticObj() }.toList()
    }
}