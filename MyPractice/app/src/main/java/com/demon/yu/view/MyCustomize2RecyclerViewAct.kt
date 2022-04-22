package com.demon.yu.view

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
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
        findViewById<View>(R.id.update).setOnClickListener {
            val number =
                findViewById<EditText>(R.id.inputNumber).editableText.toString().toIntOrNull()
            if (number == null) {
                Toast.makeText(this@MyCustomize2RecyclerViewAct, "输入错误", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val data = createObj(number)
            adapter.update(data)

        }
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

                        FakeLayoutCoorExchangeUtils.setCenterPivot(child)
                        val scale =
                            recyclerView.getScaleSize(child.getCenterX(), child.getCenterY())
                        child.scaleX = scale
                        child.scaleY = scale
                        recyclerView.translateXY(
                            child,
                            child.getCenterX(),
                            child.getCenterY(),
                            i == 0
                        )
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
                            Log.d(
                                "yujintao",
                                "input (${child.getCenterX()},${child.getCenterY()}),translatex = ${child.translationX},translateY= ${child.translationY}"
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

    private fun createObj(number: Int = 100): List<MyStaticObj> {
        return (0 until number).map { MyStaticObj() }.toList()
    }

}