package com.demon.yu.avatar.interact

import android.content.Context
import android.graphics.Point
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.demon.yu.view.recyclerview.FakeLayoutCoorExchangeUtils
import com.demon.yu.view.recyclerview.MyStaticAdapter
import com.demon.yu.view.recyclerview.MyStaticObj
import com.example.mypractice.Logger
import com.example.mypractice.R

class CloneXAvatarComposeLayout(context: Context, attrs: AttributeSet? = null) :
    FrameLayout(context, attrs) {
    companion object {
        private const val MAX_SCROLL_TO_CENTER_DURATION = 2000
        private const val TAG = "CloneXAvatar"
    }

    val avatarComposeRecyclerView = AvatarComposeRecyclerView(context)
    private val circleImageView = ImageView(context)

    var avatarComposeLayoutManager: AvatarComposeLayoutManager
    private val adapter = MyStaticAdapter()


    private var currentCenterChild: View? = null
    private var currentCenterPosition: Int = -1
    var onCenterChangeListener: OnComposeLayoutListener? = null

    private val scrollToCenterCallbackRunnable = Runnable {
        circleImageView.animate().alpha(1f).setDuration(80L).start()
        Log.d(TAG, "scrollToCenterCallbackRunnable circleImageView alpha=1")
        if (currentCenterChild == null) {
            if (childCount > 0) {
                onCenterChangeListener?.onCenter(getChildAt(0))
            }
        } else {
            onCenterChangeListener?.onCenter(currentCenterChild!!)
        }
    }


    init {
        val lp = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        lp.gravity = Gravity.CENTER
        circleImageView.alpha = 1f
        circleImageView.setImageResource(R.drawable.avatar_compose_circle_shaddow)
        addView(circleImageView, lp)
        addView(avatarComposeRecyclerView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        avatarComposeLayoutManager = AvatarComposeLayoutManager(context)
        avatarComposeRecyclerView.layoutManager = avatarComposeLayoutManager
        avatarComposeRecyclerView.adapter = adapter
        val listener = ComposeOnScrollListener()
        avatarComposeRecyclerView.addOnScrollListener(listener)
        avatarComposeRecyclerView.onDrawListener = avatarComposeLayoutManager
        avatarComposeLayoutManager.onCenterChangedListener = listener
    }

    fun updateData(list: List<MyStaticObj>) {
        adapter.update(list)
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
//        Log.d(TAG, "onScrollChanged l=$l,t=$t")
    }


    private inner class ComposeOnScrollListener : RecyclerView.OnScrollListener(),
        AvatarComposeLayoutManager.OnCenterChangedListener {
        private var lastState = -1 //初始化状态
        private var currentPosition = -1

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            Log.d(TAG, "onScrollStateChanged newState=$newState")
            if (newState == RecyclerView.SCROLL_STATE_DRAGGING ||
                (lastState != RecyclerView.SCROLL_STATE_DRAGGING && newState == RecyclerView.SCROLL_STATE_SETTLING)
            ) {
                removeCallbacks(scrollToCenterCallbackRunnable)
                circleImageView.alpha = 0f
                onCenterChangeListener?.onScrolled()
            }
            if (lastState != newState && newState == RecyclerView.SCROLL_STATE_IDLE) {
                recyclerView as AvatarComposeRecyclerView
//                if (currentPosition == -1) {
//                    return
//                }
//                val childView =
//                    avatarComposeLayoutManager.findViewByPosition(currentPosition) ?: return
//                val centerPoint = FakeLayoutCoorExchangeUtils.getCenterPoint(childView)
//                val destChildDistance =
//                    recyclerView.getDistance(centerPoint.x, centerPoint.y)
//                if (destChildDistance != 0f) {
//                    val scrollDuration =
//                        computeScrollDuration(centerPoint.x, centerPoint.y, 0, 0)
//                    recyclerView.scrollToCenter(
//                        centerPoint.x,
//                        centerPoint.y,
//                        scrollDuration
//                    )
//                    removeCallbacks(scrollToCenterCallbackRunnable)
//                    postDelayed(scrollToCenterCallbackRunnable, scrollDuration.toLong() / 2)
//                }

                var destChild: View? = null
                var destPosition: Int = 0
                var destChildDistance: Float = 0f
                var destCenterPoint: Point? = null
                var maxCloseDistance: Float = Float.MAX_VALUE

                for (i in 0 until adapter.itemCount) {
                    val child = avatarComposeLayoutManager.findViewByPosition(i)
                    if (child != null) {
                        val centerPoint = FakeLayoutCoorExchangeUtils.getCenterPoint(child)
                        destChildDistance =
                            recyclerView.getDistance(centerPoint.x, centerPoint.y)
                        if (destChildDistance < maxCloseDistance) {
                            maxCloseDistance = destChildDistance
                            destChild = child
                            destPosition = i
                            destCenterPoint = centerPoint
                        }
                    }
                }
                if (destChild != null && destCenterPoint != null && destChildDistance != 0f) {
                    val scrollDuration =
                        computeScrollDuration(destCenterPoint.x, destCenterPoint.y, 0, 0)
                    recyclerView.scrollToCenter(
                        destCenterPoint.x,
                        destCenterPoint.y,
                        scrollDuration
                    )
                    Log.d("CloneXAvatar", "scrollDuration =$scrollDuration")
                    removeCallbacks(scrollToCenterCallbackRunnable)
                    postDelayed(scrollToCenterCallbackRunnable, scrollDuration.toLong() / 2)
                }
            }
            lastState = newState
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            recyclerView as AvatarComposeRecyclerView
//            var destChild: View? = null
//            var destPosition: Int = 0
//            var destChildDistance: Float = 0f
//            var maxCloseDistance: Float = Float.MAX_VALUE
//            val init = System.currentTimeMillis()
//            Debug.startMethodTracing()
//            for (i in 0..adapter.itemCount) {
//                val child = avatarComposeLayoutManager.findViewByPosition(i)
//                if (child != null) {
//                    FakeLayoutCoorExchangeUtils.setCenterPivot(child)
//                    val point = FakeLayoutCoorExchangeUtils.getCenterPoint(child)
//                    destChildDistance =
//                        avatarComposeRecyclerView.calculateDistance(point.x, point.y)
////                    recyclerView.translateXY(
////                        child,
////                        point.x,
////                        point.y,
////                        destChildDistance
////                    )
////                    recyclerView.scaleXY(child, point.x, point.y, destChildDistance)
//                    if (destChildDistance < maxCloseDistance) {
//                        maxCloseDistance = destChildDistance
//                        destChild = child
//                        destPosition = i
//                    }
//                }
//            }
//            if (destChild != null) {
//                if (currentCenterChild != destChild) {
//                    currentCenterChild = destChild
//                    currentCenterPosition = destPosition
//                    if (lastState != -1) {
//                        ComposeSystemUtils.vibrator(context, false)
//                    }
//                }
//            }
//            Debug.startMethodTracing()
//            Logger.debug(TAG, "execute time ${System.currentTimeMillis() - init}")
        }

        override fun onCenter(lastPosition: Int, currentPosition: Int) {
            if (lastState != -1) {
                ComposeSystemUtils.vibrator(context, false)
            }
            this.currentPosition = currentPosition
            Logger.debug(TAG, "onCenter currentPosition = $currentPosition")
        }
    }


    private fun computeScrollDuration(dx: Int, dy: Int, vx: Int, vy: Int): Int {
        val absDx = Math.abs(dx)
        val absDy = Math.abs(dy)
        val horizontal = absDx > absDy
        val velocity = Math.sqrt((vx * vx + vy * vy).toDouble()).toInt()
        val delta = Math.sqrt((dx * dx + dy * dy).toDouble()).toInt()
        val containerSize = if (horizontal) width else height
        val halfContainerSize = containerSize / 2
        val distanceRatio = Math.min(1f, 1f * delta / containerSize)
        val distance =
            (halfContainerSize + halfContainerSize).toFloat() * distanceInfluenceForSnapDuration(
                distanceRatio
            )
        val duration: Int = if (velocity > 0) {
            4 * Math.round(1000 * Math.abs(distance / velocity))
        } else {
            val absDelta = (if (horizontal) absDx else absDy).toFloat()
            ((absDelta / containerSize + 1) * 300).toInt()
        }
        return Math.min(duration, MAX_SCROLL_TO_CENTER_DURATION)
    }

    private fun distanceInfluenceForSnapDuration(f: Float): Float {
        var f = f
        f -= 0.5f // center the values about 0.
        f *= 0.3f * Math.PI.toFloat() / 2.0f
        return Math.sin(f.toDouble()).toFloat()
    }

    interface OnComposeLayoutListener {
        fun onCenter(view: View)
        fun onScrolled()
    }

}