package com.demon.yu.avatar.interact

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.demon.yu.view.recyclerview.*

class CloneXAvatarComposeLayout(context: Context, attrs: AttributeSet? = null) :
    FrameLayout(context, attrs) {

    private val avatarComposeRecyclerView = AvatarComposeRecyclerView(context)
    private val circleImageView = ImageView(context)

    private var avatarComposeLayoutManager: AvatarComposeLayoutManager
    private val adapter = MyStaticAdapter()

    init {
        val lp = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        lp.gravity = Gravity.CENTER
        circleImageView.visibility = View.GONE
        addView(circleImageView, lp)
        addView(avatarComposeRecyclerView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        avatarComposeLayoutManager = AvatarComposeLayoutManager(context)
        avatarComposeRecyclerView.layoutManager = avatarComposeLayoutManager
        avatarComposeRecyclerView.adapter = adapter
        avatarComposeRecyclerView.addOnScrollListener(ComposeOnScrollListener())
    }

    fun updateData(list: List<MyStaticObj>) {
        adapter.update(list)
    }


    private inner class ComposeOnScrollListener : RecyclerView.OnScrollListener() {
        private var lastState = -1
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (lastState == -1) {
                lastState = newState
                return
            }
            if (lastState != newState && newState == RecyclerView.SCROLL_STATE_IDLE) {
                recyclerView as AvatarComposeRecyclerView
                var destChild: View? = null
                var maxCloseDistance: Float = Float.MAX_VALUE
                for (i in 0..adapter.itemCount) {
                    val child = avatarComposeLayoutManager.findViewByPosition(i)
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

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            recyclerView as AvatarComposeRecyclerView
            for (i in 0..adapter.itemCount) {
                val child = avatarComposeLayoutManager.findViewByPosition(i)
                if (child != null) {

                    FakeLayoutCoorExchangeUtils.setCenterPivot(child)
                    val point = FakeLayoutCoorExchangeUtils.getCenterPoint(child)
                    val scale =
                        recyclerView.getScaleSize(point.x, point.y)
                    child.scaleX = scale
                    child.scaleY = scale
                    recyclerView.translateXY(
                        child,
                        point.x,
                        point.y,
                        i == 0
                    )
                }
            }
        }
    }
}