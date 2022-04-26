package com.demon.yu.avatar.interact

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Point
import android.os.SystemClock
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.demon.yu.extenstion.dp2Px
import com.example.mypractice.R
import com.example.mypractice.common.Common
import com.facebook.drawee.view.SimpleDraweeView

class LightInteractView(context: Context, attrs: AttributeSet? = null) :
    FrameLayout(context, attrs), View.OnTouchListener,
    InteractHorizontalScrollView.OnScrollListener {

    companion object {
        private const val STATE_IDLE = 0
        private const val STATE_INTERACT = 1
        private const val LIGHT_INTERACT_ANMI_DURATION = 655L
        private const val LIGHT_INTERACT_REPEAT_DELAY = 100L
        private const val LIGHT_INTERACT_BACK_TO_IDLE = 1500L
        private const val LIGHT_INTERACT_ICON_ANIM = 100L
    }

    private var state: Int = STATE_IDLE

    private var touchSlop = ViewConfiguration.get(context).scaledTouchSlop

    private val parentContainer: LinearLayout
    private val horizontalScrollView: InteractHorizontalScrollView

    private val leftBtn: TextView
    private val rightBtn: TextView
    private val lightInteractIsMyself = false
    private val featureContainer: View
    private val lightInteractNumberView: LightInteractNumberView = LightInteractNumberView(context)
    private val circleBgView: View
    private var centerPosition = 0
    private var root: View
    private val destInteractPoint = Point()
    private var horizontalScrollViewIsScrolling = false

    private var currentLightNumber = 0
    private var reCount: Boolean = true


    private var btnIsMySelf: Boolean = false
    var onBtnClickListener: OnBtnClickListener? = null

    private val backToIdleRunnable = Runnable {
        if (horizontalScrollViewIsScrolling.not()) {
            scrollToCenterPosition(needPost = false)
            setState(STATE_IDLE)
        }
    }

    private val recountRunnable = Runnable {
        currentLightNumber = 0
        reCount = true
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.light_interact_view_layout, this)
        root = findViewById(R.id.rootView)
        parentContainer = findViewById(R.id.lightViewContainer)
        horizontalScrollView = findViewById(R.id.horizontalScrollView)
        leftBtn = findViewById(R.id.leftBtn)
        rightBtn = findViewById(R.id.rightBtn)
        circleBgView = findViewById(R.id.circleBg)
        destInteractPoint.set(Common.screenWidth / 2, Common.screenHeight / 2)
        featureContainer = findViewById(R.id.featureContainer)
        horizontalScrollView.enableScroll = false
        horizontalScrollView.onScrollListener = this
        val lp = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        lp.gravity = Gravity.TOP or Gravity.LEFT
        addView(lightInteractNumberView, lp)
        changeLightInteractMode(btnIsMySelf)
    }


    fun setInteractPoint(point: Point) {
        destInteractPoint.set(point.x, point.y)
        lightInteractNumberView.translationX = point.x.toFloat() + 30.dp2Px()
        lightInteractNumberView.translationY = point.y.toFloat() - 20.dp2Px()
    }


    fun updateList(list: List<LightInteractModel>, scrollToCenter: Boolean) {
        parentContainer.removeAllViews()
        list.forEach {
            addLightInteractView(it)
        }
        if (scrollToCenter) {
            scrollToCenterPosition(needPost = true)
        }
    }


    fun changeLightInteractMode(isMyself: Boolean) {
        btnIsMySelf = isMyself
        if (btnIsMySelf) {
            leftBtn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.change_clothes, 0, 0)
            leftBtn.text = "换装扮"
            rightBtn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.change_motion, 0, 0)
            rightBtn.text = "换表情"
        } else {
            leftBtn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.send_msg, 0, 0)
            leftBtn.text = "发消息"
            rightBtn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.teke_to, 0, 0)
            rightBtn.text = "合拍"
        }

    }


    private fun refreshInteractState() {
        if (state == STATE_IDLE) {
            featureContainer.animate().let {
                it.alpha(1f).setDuration(LIGHT_INTERACT_ICON_ANIM).withStartAction {
                    hideSideView()
                }.start()
            }
            horizontalScrollView.enableScroll = false
            lightInteractNumberView.end()
        } else {
            featureContainer.animate().let {
                it.alpha(0f).setDuration(LIGHT_INTERACT_ICON_ANIM).withEndAction {
                    showSideView()
                }.start()
            }
            removeCallbacks(backToIdleRunnable)
//            horizontalScrollView.enableScroll(true)

        }
    }


    private fun scrollToCenterPosition(needPost: Boolean) {
        fun scrollAndHide() {
            val childCount = parentContainer.childCount
            val screenWidth = Common.screenWidth
            centerPosition = childCount / 2
            val destView = parentContainer.getChildAt(centerPosition)
            val centerLeft = screenWidth / 2 - destView.width / 2
            horizontalScrollView.scrollTo(destView.left - centerLeft, 0)
            hideSideView()
        }
        if (needPost) post { scrollAndHide() } else scrollAndHide()
    }

    private fun hideSideView() {
        (0 until parentContainer.childCount).forEach {
            if (centerPosition != it) {
                parentContainer.getChildAt(it).visibility = View.INVISIBLE
            } else {
                parentContainer.getChildAt(it).visibility = View.VISIBLE
            }
        }
    }

    private fun showSideView() {
        (0 until parentContainer.childCount).forEach {
            parentContainer.getChildAt(it).visibility = View.VISIBLE
        }
    }


    private fun addLightInteractView(listInteractModel: LightInteractModel) {
        val simpleDraweeView = LayoutInflater.from(context)
            .inflate(
                R.layout.item_light_interact_view_holder_item,
                parentContainer,
                false
            ) as SimpleDraweeView
        simpleDraweeView.setActualImageResource(listInteractModel.resourceID)
        val lp = LinearLayout.LayoutParams(58.dp2Px(), 58.dp2Px())
        lp.marginStart = 8.dp2Px()
        lp.marginEnd = 8.dp2Px()
        parentContainer.addView(simpleDraweeView, lp)
        simpleDraweeView.setTag(R.id.interact_view_key, listInteractModel)
        simpleDraweeView.setOnTouchListener(this)
    }


    private fun delayBackToIdle(delay: Long = LIGHT_INTERACT_BACK_TO_IDLE) {
        postDelayed(backToIdleRunnable, delay)
    }


    private fun onLightInteract(count: Int, reStart: Boolean) {
        postDelayed({
            if (reStart) {
                lightInteractNumberView.setNumber(count)
            } else {
                lightInteractNumberView.continueNumber(count)
            }
        }, LIGHT_INTERACT_ANMI_DURATION - 100)
        ComposeSystemUtils.vibrator(context)

    }

    private var isClicking: Boolean = false
    private fun startCartoonPlay(needRepeat: Boolean, forceRepeat: Boolean = false) {
        if (needRepeat) {
            if (hasStartRepeatSendCartoon.not() || forceRepeat) {
                sendInteractCartoon()
                hasStartRepeatSendCartoon = true
                currentLightInteractView?.animate()?.let {
                    it.cancel()
                    it.scaleX(0.6f).scaleY(0.6f).setDuration(100L).start()
                }
                removeCallbacks(sendCartoonRunnable)
                postDelayed(sendCartoonRunnable, LIGHT_INTERACT_REPEAT_DELAY)
            }
        } else {
            sendInteractCartoon()
            if (isClicking) {
                return
            }
            currentLightInteractView?.animate()?.let {
                it.scaleX(0.6f).scaleY(0.6f).setDuration(LIGHT_INTERACT_ICON_ANIM / 2)
                    .withStartAction {
                        isClicking = true
                    }.withEndAction {
                        it.cancel()
                        it.scaleX(1f).scaleY(1f).setDuration(LIGHT_INTERACT_ICON_ANIM / 2)
                            .withEndAction {
                                isClicking = false
                            }.start()

                    }.start()
            }
        }
    }

    private fun stopCartoonPlay() {
        removeCallbacks(sendCartoonRunnable)
    }


    private fun setState(state: Int) {
        this.state = state
        refreshInteractState()
    }


    private var sendCartoonRunnable: Runnable = object : Runnable {
        override fun run() {
            sendInteractCartoon()
            postDelayed(this, LIGHT_INTERACT_REPEAT_DELAY)
        }
    }

    private var lastEventX: Float = 0f
    private var lastRawX: Float = 0f
    private var initRawX: Float = 0f
    private var lastEventY: Float = 0f
    private var lastRawY: Float = 0f
    private var initRawY: Float = 0f
    private var currentLightInteractView: View? = null
    private var currentLightInteractModel: LightInteractModel? = null
    private var currentLightInteractViewLocalArrCache = IntArray(2)
    private var downTimestamp = 0L
    private var longTouch = false
    private var hasStartRepeatSendCartoon = false

    private var longTouchRunnable = Runnable {
        longTouch = true
        startCartoonPlay(true)
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        val simpleDraweeView = v as SimpleDraweeView
        v.pivotY = (v.width / 2).toFloat()
        v.pivotY = (v.width / 2).toFloat()
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                longTouch = false
                log("ACTION_DOWN ${horizontalScrollView.enableScroll}")
                downTimestamp = SystemClock.uptimeMillis()
                startInteractFromView(v, event)
                setState(STATE_INTERACT)
                postDelayed(longTouchRunnable, 200L)//80ms啥都没动，触发longTouch
            }
            MotionEvent.ACTION_MOVE -> {
                if ((SystemClock.uptimeMillis() - downTimestamp > 100L ||
                            event.rawX - initRawX >= touchSlop || event.rawY - initRawY >= touchSlop
                            )
                ) { //长按
                    removeCallbacks(longTouchRunnable)
                    v.parent.requestDisallowInterceptTouchEvent(true)
                    longTouch = true
                }
                readEventOrClear(event, false)
                if (longTouch) {
                    v.parent.requestDisallowInterceptTouchEvent(true)
                    startCartoonPlay(true)
                    val view = detachInteractView(event)
                    if (view != currentLightInteractView && view != null) {
                        changeLightInteractView(view)
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                log("onTouch ACTION_UP, longTouch=${longTouch}")
                if (longTouch.not()) {
                    performClick()
                    startCartoonPlay(false)
                }
                v.parent.requestDisallowInterceptTouchEvent(false)
                endInteract(longTouch)
                stopCartoonPlay()
                delayBackToIdle()
                removeCallbacks(longTouchRunnable)
                horizontalScrollView.enableScroll = true
                downTimestamp = 0L
                longTouch = false
                hasStartRepeatSendCartoon = false
            }
            MotionEvent.ACTION_CANCEL -> {
                log("onTouch MotionEvent.ACTION_CANCEL")
                endInteract(true)
                stopCartoonPlay()
                delayBackToIdle()
                removeCallbacks(longTouchRunnable)
                longTouch = false
                v.parent.requestDisallowInterceptTouchEvent(false)
                downTimestamp = 0L
                hasStartRepeatSendCartoon = false
                horizontalScrollView.enableScroll = true
            }
        }
        return true
    }

    private fun log(msg: String, throwable: Throwable? = null) {
        Log.d("LightInteractView", msg, throwable)
    }

    private fun detachInteractView(event: MotionEvent): View? {
        if (event.y <= 0 || top >= 74.dp2Px()) {
            return currentLightInteractView
        }
        val currentLightInteractView = currentLightInteractView ?: return null
        if (event.rawX < currentLightInteractView.left - horizontalScrollView.scrollX) {//往左划动
            val index = parentContainer.indexOfChild(currentLightInteractView)
            if (index == 0) {
                return currentLightInteractView
            }
            for (i in index - 1 downTo 0) {
                val destView = parentContainer.getChildAt(i)
                if (destView.left <= event.rawX + horizontalScrollView.scrollX &&
                    destView.right >= event.rawX + horizontalScrollView.scrollX
                ) {
                    return destView
                }
            }
        } else {
            val index = parentContainer.indexOfChild(currentLightInteractView)
            for (i in index until parentContainer.childCount) {
                val destView = parentContainer.getChildAt(i)
                if (destView.left <= event.rawX + horizontalScrollView.scrollX &&
                    destView.right >= event.rawX + horizontalScrollView.scrollX
                ) {
                    return destView
                }
            }
        }
        return currentLightInteractView
    }

    private fun changeLightInteractView(destView: View) {
        val lastView = currentLightInteractView
        val lastModel = currentLightInteractModel
        currentLightInteractView = destView
        currentLightInteractModel = destView.getTag(R.id.interact_view_key) as LightInteractModel
        currentLightInteractViewLocalArrCache.fill(0)
        lastView?.animate()?.let {
            it.cancel()
            it.scaleX(1f).scaleY(1f).setDuration(50L).start()
        }
        currentLightInteractView?.animate()?.let {
            it.cancel()
            it.scaleX(0.6f).scaleY(0.6f).setDuration(50L).start()
        }
        //切换view时立马触发一次，同时重制定时任务
        startCartoonPlay(needRepeat = true, forceRepeat = true)
    }

    private fun sendInteractCartoon() {
        val currentLightInteractView = currentLightInteractView ?: return
        val currentLightInteractModel = currentLightInteractModel ?: return
        if (currentLightInteractViewLocalArrCache[0] == 0) {
            currentLightInteractViewLocalArrCache[0] =
                currentLightInteractView.left - horizontalScrollView.scrollX
            currentLightInteractViewLocalArrCache[1] =
                currentLightInteractView.top + root.top
        }
        val simpleDraweeView = SimpleDraweeView(context)
        simpleDraweeView.setActualImageResource(currentLightInteractModel.resourceID)
        val lp = FrameLayout.LayoutParams(
            currentLightInteractView.width,
            currentLightInteractView.height
        )
        lp.gravity = Gravity.TOP or Gravity.LEFT
        lp.leftMargin = currentLightInteractViewLocalArrCache[0]
        lp.topMargin = currentLightInteractViewLocalArrCache[1]
        addView(simpleDraweeView, lp)
        simpleDraweeView.pivotX = (currentLightInteractView.width / 2).toFloat()
        simpleDraweeView.pivotY = (currentLightInteractView.height / 2).toFloat()
        val valueAnimatorSet = AnimatorSet()
        val scaleAnimator = ValueAnimator.ofFloat(
            36f / 58f,
            66f / 58f,
            80f / 58f,
            136f / 58f,
            80f / 58f,
            66f / 58f,
            36f / 58f
        )
        scaleAnimator.duration = LIGHT_INTERACT_ANMI_DURATION
        scaleAnimator.addUpdateListener {
            simpleDraweeView.scaleX = it.animatedValue as Float
            simpleDraweeView.scaleY = it.animatedValue as Float
        }
        val translateX =
            destInteractPoint.x - (currentLightInteractViewLocalArrCache[0] + currentLightInteractView.width / 2)
        val translateY =
            destInteractPoint.y - (currentLightInteractViewLocalArrCache[1] + currentLightInteractView.height / 2)

        val translateAnimator = ValueAnimator.ofFloat(0f, 1f)

        val startPoint = Point(
            currentLightInteractViewLocalArrCache[0] + currentLightInteractView.width / 2,
            currentLightInteractViewLocalArrCache[1] + currentLightInteractView.height / 2
        )
        val endPoint = Point(
            destInteractPoint.x,
            destInteractPoint.y
        )
        translateAnimator.duration = LIGHT_INTERACT_ANMI_DURATION
        val cubicBezierInterpolator = CubicBezierInterpolator(CubicBezierInterpolator.LINEAR)
        val bezierEvaluator =
            BezierEvaluator(
                Point(
                    startPoint.x + (endPoint.x - startPoint.x) / 4,
                    startPoint.y + (endPoint.y - startPoint.y) / 4 * 3
                )
            )
        translateAnimator.addUpdateListener {
            val value = it.animatedFraction
//            val currentY = cubicBezierInterpolator.getBezierCoordinateY(value)
//            val currentX = cubicBezierInterpolator.getXForTime(value)
//            decelerate.getInterpolation(value)
//            simpleDraweeView.translationX = currentX * translateX
//            simpleDraweeView.translationY = currentY * translateY

            val destPoint = bezierEvaluator.evaluate(value, startPoint, endPoint)
            simpleDraweeView.translationX = (destPoint.x - startPoint.x).toFloat()
            simpleDraweeView.translationY = (destPoint.y - startPoint.y).toFloat()

//            simpleDraweeView.translationX = translateX * value
//            simpleDraweeView.translationY = translateY * value

            if (value >= 0.5) {
                simpleDraweeView.alpha = 1.5f - value
            }
        }


        valueAnimatorSet.addListener(InteractCartoonListener(simpleDraweeView))
        valueAnimatorSet.playTogether(translateAnimator, scaleAnimator)
//        valueAnimatorSet.playTogether(translateAnimator)
        valueAnimatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {

            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationRepeat(animation: Animator?) {
            }
        })
//        valueAnimatorSet.interpolator = DecelerateInterpolator()
        valueAnimatorSet.start()

        //临时放到这里
        removeCallbacks(recountRunnable)
        postDelayed(recountRunnable, 1000L)
        currentLightNumber++
        onLightInteract(currentLightNumber, reCount)
        reCount = false

    }

    private fun startInteractFromView(v: View, motionEvent: MotionEvent) {
        readEventOrClear(motionEvent, true)
        currentLightInteractView = v
        currentLightInteractModel = v.getTag(R.id.interact_view_key) as LightInteractModel
        currentLightInteractView?.let {
            it.pivotX = (it.width / 2).toFloat()
            it.pivotY = (it.height / 2).toFloat()
        }
    }


    private fun endInteract(needScaleAnim: Boolean = true) {
        if (needScaleAnim) {
            currentLightInteractView?.let {
                if (it.scaleX != 1f) {
                    it.animate().cancel()
                    it.animate().scaleX(1f).scaleY(1f).setDuration(LIGHT_INTERACT_ICON_ANIM).start()
                }
            }
        }
        currentLightInteractViewLocalArrCache.fill(0)
        currentLightInteractView = null
        currentLightInteractModel = null
        readEventOrClear()
    }

    private fun isInScrollingContainer(): Boolean {
        var p = parent
        while (p != null && p is ViewGroup) {
            if (p.shouldDelayChildPressedState()) {
                return true
            }
            p = p.getParent()
        }
        return false
    }

    private fun readEventOrClear(event: MotionEvent? = null, isInit: Boolean = false) {
        lastEventX = event?.x ?: 0f
        lastEventY = event?.y ?: 0f
        lastRawX = event?.rawX ?: 0f
        lastRawY = event?.rawY ?: 0f
        if (isInit) {
            initRawY = event?.rawY ?: 0f
            initRawX = event?.rawX ?: 0f
        }
    }

    private class InteractCartoonListener(private val view: SimpleDraweeView) :
        Animator.AnimatorListener {
        override fun onAnimationStart(animation: Animator?) {
        }

        override fun onAnimationEnd(animation: Animator?) {
            (view.parent as ViewGroup).removeView(view)
        }

        override fun onAnimationRepeat(animation: Animator?) {
        }

        override fun onAnimationCancel(animation: Animator?) {
        }

    }

    override fun onScroll(isScrolling: Boolean) {
        horizontalScrollViewIsScrolling = isScrolling
        if (isScrolling.not()) {
            removeCallbacks(backToIdleRunnable)
            delayBackToIdle()
        }
    }

    interface OnBtnClickListener {
        fun onLeftBtnClick(view: View, isMySelf: Boolean)
        fun onRightBtnClick(view: View, isMySelf: Boolean)
    }
}
