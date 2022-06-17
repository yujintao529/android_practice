package com.demon.yu.avatar.interact

import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.demon.yu.view.FrescoWebpViewAct
import com.demon.yu.view.recyclerview.FakeLayoutCoorExchangeUtils
import com.example.mypractice.Logger
import com.example.mypractice.R

class CloneXAvatarComposeLayout(context: Context, attrs: AttributeSet? = null) :
    FrameLayout(context, attrs), CloneXComposeAdapter.OnComposeAdapterListener {
    companion object {
        private const val TAG = "CloneXAvatarComposeLayout"
    }

    val avatarComposeRecyclerView = CloneXComposeRecyclerView(context)
    private val circleImageView = ImageView(context)

    var cloneXComposeLayoutManager: CloneXComposeLayoutManager
    private val adapter = CloneXComposeAdapter(this)

    //
    private var currentCenterView: View? = null
    private var currentCenterModel: Any? = null
    private var currentCenterPosition = -1

    private var valueAnimator: ValueAnimator? = null
    var onCenterChangeListener: OnAvatarComposeListener? = null

    private val maxScaleSize = 124f / 60f

    private val scrollToCenterCallbackRunnable = Runnable {
        Logger.debug(TAG, "onScrollStateChanged circleImageView alpha= 1")
        circleImageView.animate().alpha(1f).setDuration(80L).start()
    }

    init {
        val lp = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        lp.gravity = Gravity.CENTER
        circleImageView.alpha = 1f
        circleImageView.setImageResource(R.drawable.avatar_compose_circle_shaddow)
        addView(circleImageView, lp)
        addView(avatarComposeRecyclerView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        cloneXComposeLayoutManager = CloneXComposeLayoutManager(context)
        avatarComposeRecyclerView.layoutManager = cloneXComposeLayoutManager
        avatarComposeRecyclerView.adapter = adapter
        val composeListener = ComposeOnScrollListener()
        avatarComposeRecyclerView.addOnScrollListener(composeListener)
        cloneXComposeLayoutManager.onCenterChangedListener = composeListener
        post {
            circleImageView.pivotX = (circleImageView.width / 2).toFloat()
            circleImageView.pivotY = (circleImageView.height / 2).toFloat()
        }
    }

    private var lastCount = 0
    fun updateData(list: List<CloneXStaticObj>) {
        if (lastCount >= 7) {
            avatarComposeRecyclerView.itemAnimator = null
        }
        lastCount = list.size
        adapter.update(list)
        scrollToCenterCallbackRunnable.run()
    }

    private inner class ComposeOnScrollListener : RecyclerView.OnScrollListener(),
        CloneXComposeLayoutManager.OnCenterChangedListener {
        private var lastState = -1 //初始化状态

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (lastState == RecyclerView.SCROLL_STATE_IDLE && newState == RecyclerView.SCROLL_STATE_DRAGGING) {
//                CloneXVirtualAppearanceUtils.mobFriendsAvatarAggregateSlide()
            }
            if (newState == RecyclerView.SCROLL_STATE_DRAGGING ||
                (lastState != RecyclerView.SCROLL_STATE_DRAGGING && newState == RecyclerView.SCROLL_STATE_SETTLING)
            ) {
                Logger.debug(TAG, "onScrollStateChanged circleImageView alpha= 0")
                removeCallbacks(scrollToCenterCallbackRunnable)
                circleImageView.animate().cancel()
                circleImageView.alpha = 0f
                onCenterChangeListener?.onScrolled()
            }
            if (lastState != newState && newState == RecyclerView.SCROLL_STATE_IDLE) {
                recyclerView as CloneXComposeRecyclerView
                var destChild: View? = null
                var destChildDistance: Float = 0f
                var minCloseDistance: Float = Float.MAX_VALUE

                for (i in 0 until adapter.itemCount) {
                    val child = cloneXComposeLayoutManager.findViewByPosition(i)
                    if (child != null) {
                        val centerPoint = FakeLayoutCoorExchangeUtils.getCenterPoint(child)
                        destChildDistance =
                            cloneXComposeLayoutManager.calculateDistance(
                                centerPoint.x,
                                centerPoint.y
                            )
                        if (destChildDistance < minCloseDistance) {
                            minCloseDistance = destChildDistance
                            destChild = child
                        }
                    }
                }
                if (destChild != null && minCloseDistance != 0f) {
                    val scrollDuration = recyclerView.scrollViewToCenter(destChild)
                    Log.d("CloneXAvatar", "scrollDuration =$scrollDuration")
                    removeCallbacks(scrollToCenterCallbackRunnable)
                    postDelayed(scrollToCenterCallbackRunnable, scrollDuration.toLong() / 2)
                } else {
                    scrollToCenterCallbackRunnable.run()
                }
            }
            lastState = newState
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
//            recyclerView as AvatarComposeRecyclerView
//            var destChild: View? = null
//            var destPosition: Int = 0
//            var destChildDistance: Float = 0f
//            var maxCloseDistance: Float = Float.MAX_VALUE
//            for (i in 0..adapter.itemCount) {
//                val child = avatarComposeLayoutManager.findViewByPosition(i)
//                if (child != null) {
//                    FakeLayoutCoorExchangeUtils.setCenterPivot(child)
//                    val point = FakeLayoutCoorExchangeUtils.getCenterPoint(child)
//                    destChildDistance = avatarComposeRecyclerView.calculateDistance(point.x, point.y)
//                    recyclerView.translateXY(
//                        child,
//                        point.x,
//                        point.y,
//                        destChildDistance
//                    )
//                    recyclerView.scaleXY(child, point.x, point.y, destChildDistance)
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
//                    currentCenterModel = adapter.getObjectByPosition(destPosition)
//                    if (lastState != -1) {
//                        ComposeSystemUtils.vibrator(context, false)
//                    }
//                    currentCenterModel?.let {
//                        onCenterChangeListener?.onCenter(destChild, currentCenterPosition, it)
//                    }
//                }
//            }
        }

        override fun onCenter(lastPosition: Int, currentPosition: Int) {
            if (lastState != -1) {
                ComposeSystemUtils.vibrator(context, false)
            }
            currentCenterPosition = currentPosition
            currentCenterView = getCurrentCenterViewLock()
            currentCenterModel = getCurrentCenterModelLock()
            Logger.debug(TAG, "onCenter currentPosition = $currentPosition")
            notifyCenterChangedListener()
        }
    }


    private fun notifyCenterChangedListener() {
        onCenterChangeListener?.onCenter(currentCenterPosition, currentCenterModel)
    }

    private fun getCurrentCenterViewLock(): View? {
        return cloneXComposeLayoutManager.findViewByPosition(currentCenterPosition)
    }

    private fun getCurrentCenterModelLock(): Any? {
        if (adapter.itemCount > currentCenterPosition) {
            return adapter.getObjectByPosition(currentCenterPosition)
        }
        return null
    }


    fun onLightInteract(
        composeUserModel: ComposeUserModel,
        lightInteractModel: LightInteractModel
    ) {
        val currentCenterChild = getCurrentCenterViewLock() ?: return
        if (valueAnimator?.isRunning == true) {
            valueAnimator?.cancel()
        }
        valueAnimator = ValueAnimator.ofFloat(1f, 0.8f, 1f)
        valueAnimator?.duration = 120L
        valueAnimator?.addUpdateListener {
            currentCenterChild.scaleX = maxScaleSize * it.animatedValue as Float
            currentCenterChild.scaleY = maxScaleSize * it.animatedValue as Float
            circleImageView.scaleX = it.animatedValue as Float
            circleImageView.scaleY = it.animatedValue as Float
        }
        valueAnimator?.startDelay = 655 - 100
        valueAnimator?.start()
    }

    interface OnAvatarComposeListener {
        fun onCenter(position: Int, any: Any?)
        fun onScrolled()
    }


    override fun onClick(model: ComposeUserModel, position: Int) {
        if (currentCenterPosition != position) {
            avatarComposeRecyclerView.scrollCenterToPosition(position)
            return
        }
        val intent = Intent(context, FrescoWebpViewAct::class.java)
        context.startActivity(intent)
    }
}