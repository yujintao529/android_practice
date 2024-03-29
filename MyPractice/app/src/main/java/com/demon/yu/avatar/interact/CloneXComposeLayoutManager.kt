package com.demon.yu.avatar.interact

import android.content.Context
import android.graphics.*
import android.util.Pair
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import androidx.core.os.TraceCompat
import androidx.recyclerview.widget.AvatarLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.demon.yu.extenstion.dp2Px
import com.demon.yu.view.recyclerview.*
import com.example.mypractice.BuildConfig
import com.example.mypractice.Logger
import com.example.mypractice.common.Common
import kotlin.math.*


class CloneXComposeLayoutManager(val context: Context) : AvatarLayoutManager(),
    CloneXComposeRecyclerView.OnDrawListener {
    companion object {
        private const val TAG = "CloneXComposeLayoutManager"
    }

    private var measuredWidth: Int = 0
    private var measuredHeight: Int = 0
    private var centerX: Int = 0
    private var centerY: Int = 0


    private var radius: Int = CloneXComposeUiConfig.RADIUS
    private var maxScaleSize = CloneXComposeUiConfig.MAX_SCALE
    private var secondScaleSize = CloneXComposeUiConfig.SECOND_MAX_SCALE
    private var dismiss2NormalScaleDistance = CloneXComposeUiConfig.DISMISS_2_NORMAL_SCALE_DISTANCE
    private var radiusDouble = radius * 2
    private var scaleDistance = radiusDouble

    private val coordinateCache: SparseArray<Point> = SparseArray(200)
    private var cloneXComposeRecyclerView: CloneXComposeRecyclerView? = null


    private var maxCircleRadius = 0
    private var currentPosition: Int = -1
    var onCenterChangedListener: OnCenterChangedListener? = null


    private val layoutState = LayoutState()

    //仅支持matchParent及exactly width/height
    override fun onMeasure(
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State,
        widthSpec: Int,
        heightSpec: Int
    ) {
//        super.onMeasure(recycler, state, widthSpec, heightSpec)
        measuredWidth = View.MeasureSpec.getSize(widthSpec)
        measuredHeight = View.MeasureSpec.getSize(heightSpec)
        setMeasuredDimension(measuredWidth, measuredHeight)
        centerX = measuredWidth / 2
        centerY = measuredHeight / 2
        scaleDistance =
            (2 * cos(PI / 6) * radius).toInt() + (radiusDouble - (2 * cos(PI / 6) * radius).toInt()) / 6
        Logger.debug("AvatarComposeLayoutManager", "onMeasure centerX=$centerX,centerY=$centerY")
        offsetShowRegion()
    }

    override fun setRecyclerView(recyclerView: RecyclerView?) {
        super.setRecyclerView(recyclerView)
        cloneXComposeRecyclerView = recyclerView as? CloneXComposeRecyclerView
        cloneXComposeRecyclerView?.onDrawListener = this
    }


    //关闭
    override fun isMeasurementCacheEnabled(): Boolean {
        return false
    }


    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        if (itemCount == 0) {
            removeAndRecycleAllViews(recycler);
            return
        }
        offsetShowRegion()
        detachAndScrapAttachedViews(recycler)
        clearCoordinateCacheIfNeed(state.itemCount)
        fill(recycler, state, true)
        recycleScrapViews(recycler)
    }

    /**
     * 回收所有没有使用的scrapViews
     */
    private fun recycleScrapViews(recycler: RecyclerView.Recycler) {
        Logger.debug(
            CloneXComposeUiConfig.TAG,
            "recycleScrapViews scrapSize = " + recycler.scrapList.size
        )
        val list = recycler.scrapList.toList()
        for (element in list) {
            removeAndRecycleView(element.itemView, recycler)
        }
    }

    private val viewRegion = Rect()


    private var lastChildCount = 0

    private fun clearCoordinateCacheIfNeed(currentCount: Int) {
        if ((lastChildCount >= 7 && currentCount < 7) || (lastChildCount < 7 && currentCount >= 7)
            || (lastChildCount < 7 && currentCount != lastChildCount)
        ) {
            coordinateCache.clear()
        }
        lastChildCount = currentCount
    }

    private fun fill(
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State,
        checkFillStatus: Boolean
    ) {

        fillInternal(recycler, state)
        val itemCount = getItemCount(state)
        if (itemCount > 0 && childCount == 0 && checkFillStatus) {
            layoutState.reset()
            fillInternal(recycler, state)
            calculateViewRegion(state)
        } else {
            calculateViewRegion(state)
        }
        if (checkFillStatus) {
            if ((layoutState.scrollY != 0 || layoutState.scrollX != 0) && layoutState.minCloseDistance != 0f) {
                scrollToPosition(currentPosition)
            }
        }
    }

    private fun fillInternal(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        val visibleChildCount = getItemCount(state)
        var destPosition: Int = -1
        var destChildDistance = 0f
        var minCloseDistance: Float = Float.MAX_VALUE
        TraceCompat.beginSection("CCLM fill")

//        for (position in visibleChildCount - 1 downTo 0) {
        for (position in 0 until visibleChildCount) {
            val point = calculateChildCoordinate(position, visibleChildCount)
            if (layoutState.showRegion.contains(point.x, point.y).not()) {
                continue
            }
            val view = recycler.getViewForPosition(position) //会处理
            if (view.parent == null) {
                addView(view)
                measureChildWithMargins(view, 0, 0)
                layoutChildInternal(view, position, point)
                FakeLayoutCoorExchangeUtils.setCenterPivot(view)
            }
            offsetChildHorAndVer(view, -layoutState.scrollX, -layoutState.scrollY)
            destChildDistance = shapeChange(view, position)
            TraceCompat.beginSection("CCLM fill bindRealViewHolderIfNeed")
            bindRealViewHolderIfNeed(view, position)
            TraceCompat.endSection()
            if (destChildDistance < minCloseDistance) {
                minCloseDistance = destChildDistance
                destPosition = position
            }
        }
        if(destPosition!=-1){
            layoutState.minCloseDistance = minCloseDistance
            setCenterPosition(destPosition)
        }
    }


    private fun calculateViewRegion(state: RecyclerView.State) {
        if (getItemCount(state) == 1) {
            maxCircleRadius = radius
            val left = (measuredWidth - maxCircleRadius * 2) / 2
            val top = (measuredHeight - maxCircleRadius * 2) / 2
            viewRegion.set(
                left,
                top,
                left + maxCircleRadius * 2,
                top + maxCircleRadius * 2
            ) //找到四周的范围
        } else {
            viewRegion.set(find4Coordinate()) //找到四周的范围
            val point = getCenterPoint()
            val centerDiffX = abs(viewRegion.exactCenterX() - point.x)
            val centerDiffY = abs(viewRegion.exactCenterY() - point.y)
            val radiusDiff = calDistance(centerDiffX.toInt(), centerDiffY.toInt())
            maxCircleRadius =
                (max(viewRegion.width(), viewRegion.height()) / 2 + radiusDiff).toInt()
        }
        Logger.debug(
            CloneXComposeUiConfig.TAG,
            "viewRegion =  $viewRegion, maxCircleRadius= $maxCircleRadius"
        )
        TraceCompat.endSection()
    }


    override fun scrollToPosition(position: Int) {
        val centerChild = findViewByPosition(position) ?: return
        val childPoint = FakeLayoutCoorExchangeUtils.getCenterPoint(centerChild)
        val diffX = centerX - childPoint.x
        val diffY = centerY - childPoint.y
        layoutState.scrollX -= diffX
        layoutState.scrollY -= diffY
        postOnAnimation {
            requestLayout()
        }
    }

    override fun smoothScrollToPosition(
        recyclerView: RecyclerView?,
        state: RecyclerView.State?,
        position: Int
    ) {
        cloneXComposeRecyclerView?.scrollCenterToPosition(position)
    }

    fun calculateDistance(x: Int, y: Int): Float {
        return calDistance(x - centerX, y - centerY)
    }


    private fun calDistance(x: Int, y: Int): Float {
        return sqrt(abs(x).toDouble().pow(2) + abs(y).toDouble().pow(2)).toFloat()
    }


    private fun getCenterPoint(): Point {
        return cloneXComposeRecyclerView?.getCenterPoint() ?: Point(
            Common.screenWidth / 2,
            Common.screenHeight / 2
        )
    }


    private fun layoutChildInternal(view: View, position: Int, point: Point) {
        val childWidth = view.measuredWidth
        val childHeight = view.measuredHeight
        FakeLayoutCoorExchangeUtils.shiftingLayout(view, point) { left, top ->
            layoutDecorated(
                view,
                left,
                top,
                left + childWidth,
                top + childHeight
            )
        }
    }


    private fun shapeChange(child: View, position: Int): Float {
        val childPoint = FakeLayoutCoorExchangeUtils.getCenterPoint(child)
        val destChildDistance = calculateDistance(childPoint.x, childPoint.y)
        translateXY(
            child,
            childPoint.x,
            childPoint.y,
            destChildDistance
        )
        scaleXY(child, childPoint.x, childPoint.y, destChildDistance)
        return destChildDistance
    }


    private fun setCenterPosition(destPosition: Int) {
        val lastPosition = currentPosition
        if (lastPosition != destPosition) {
            currentPosition = destPosition
            notifyCenterChanged(lastPosition, currentPosition)
        }
    }

    private fun notifyCenterChanged(lastPosition: Int, currentPosition: Int) {
        onCenterChangedListener?.onCenter(lastPosition, currentPosition)
    }


    private fun calculateChildCoordinate(position: Int, childCount: Int): Point {
        var cache = coordinateCache.get(position)
//        if (cache != null) {
//            return cache
//        }
        if (position == 0) {
            cache = Point(measuredWidth / 2, measuredHeight / 2)
            coordinateCache.put(position, cache)
            return cache
        } else {
            cache = coordinateCache.get(0)
            if (cache == null) {
                cache = Point(measuredWidth / 2, measuredHeight / 2)
                coordinateCache.put(0, cache)
            }
        }
        val zero = coordinateCache.get(0)
        val dest: Point = if (childCount >= 7) {
            calculateCenteredHexagonalCoordinate(position, radius, zero.x, zero.y).toPoint()
        } else {
            calculateCoordinate(position, radius, childCount, zero.x, zero.y).toPoint()
        }
        coordinateCache.put(position, dest)

        return dest
    }


    //先不复用的view的场景下也就是所有的view都添加的情况下，找到最上，最左，最右，最下的坐标，用来辅助是否可以滑动的情况
    private fun find4Coordinate(): Rect {
        val rect = Rect()
        val count = coordinateCache.size()
        for (position in 0 until count) {
            val value = coordinateCache.valueAt(position)
            if (value.x < rect.left || rect.left == 0) {
                rect.left = value.x
            }
            if (value.x > rect.right || rect.right == 0) {
                rect.right = value.x
            }
            if (value.y < rect.top || rect.top == 0) {
                rect.top = value.y
            }
            if (value.y > rect.bottom || rect.bottom == 0) {
                rect.bottom = value.y
            }
        }
        return rect
    }


    private fun calculateCoordinate(
        index: Int,
        radius: Int,
        childCount: Int,
        centerX: Int,
        centerY: Int
    ): DPoint {
        if (childCount == 1 || childCount >= 7 || index == 0) {
            throw IllegalAccessException("not suppose to be here childCount=$childCount,index=$index")
        }

        if (childCount == 2) {
            return DPoint(centerX.toDouble(), (centerY - radius).toDouble())
        }
        val avgAngle = Math.PI * 2 / (childCount - 1)
        when (childCount) {
            3 -> {
                return if (index == 1) {
                    DPoint((centerX + radius).toDouble(), centerY.toDouble())
                } else {
                    DPoint((centerX - radius).toDouble(), centerY.toDouble())
                }
            }
            4, 6 -> {
                val x = sin(avgAngle * (index - 1)) * radius
                val y = cos(avgAngle * (index - 1)) * radius
                return DPoint(centerX + x, centerY - y)
            }
            5 -> {
                val x = sin(Math.PI / 4 + avgAngle * (index - 1)) * radius
                val y = cos(Math.PI / 4 + avgAngle * (index - 1)) * radius
                return DPoint(centerX + x, centerY - y)
            }
        }
        throw IllegalAccessException("not suppose to be here | end")
    }

    /**
     * 6变形的布局结构，非正规，按照设计有特殊规则。
     */
    private fun calculateCenteredHexagonalCoordinate(
        index: Int,
        radius: Int,
        centerX: Int,
        centerY: Int
    ): DPoint {
        val du = PI / 3.0
        val result = DPoint(0.0, 0.0)
        val hexagonalPoint = calculateHexagonalPoint(index)
        val level = hexagonalPoint.level

        if (level == 1) {
            return DPoint(centerX.toDouble(), centerY.toDouble())
        } else if (level == 2) {
            return DPoint(
                centerX - radius * cos(du * hexagonalPoint.levelNumber),
                centerY - radius * sin(du * hexagonalPoint.levelNumber)
            )
        } else if (level == 3) {
            var itemGroup = ceil((hexagonalPoint.levelNumber.toDouble() / (level - 1))).toInt()
            var itemGroupNum = hexagonalPoint.levelNumber - (itemGroup - 1) * (level - 1)
            when (itemGroup) {
                1 -> {
                    if (itemGroupNum == 1) {
                        itemGroup = 1
                        itemGroupNum = 2
                    } else {
                        itemGroup = 4
                        itemGroupNum = 2
                    }
                }
                2 -> {
                    if (itemGroupNum == 1) {
                        itemGroup = 2
                        itemGroupNum = 2
                    } else {
                        itemGroup = 6
                        itemGroupNum = 2
                    }
                }
                3 -> {
                    if (itemGroupNum == 1) {
                        itemGroup = 3
                        itemGroupNum = 2
                    } else {
                        itemGroup = 5
                        itemGroupNum = 2
                    }
                }
                4 -> {
                    if (itemGroupNum == 1) {
                        itemGroup = 2
                        itemGroupNum = 1
                    } else {
                        itemGroup = 1
                        itemGroupNum = 1
                    }
                }
                5 -> {
                    if (itemGroupNum == 1) {
                        itemGroup = 4
                        itemGroupNum = 1
                    } else {
                        itemGroup = 5
                        itemGroupNum = 1
                    }
                }
                6 -> {
                    if (itemGroupNum == 1) {
                        itemGroup = 3
                        itemGroupNum = 1
                    } else {
                        itemGroup = 6
                        itemGroupNum = 1
                    }
                }
            }
            val tempPointX = centerX - (level - 1) * radius * cos((du * itemGroup))
            val tempPointY = centerY - (level - 1) * radius * sin((du * itemGroup))
            if (itemGroup == 1) {
                result.x = tempPointX + radius * (itemGroupNum - 1)
                result.y = tempPointY
            } else if (itemGroup == 2) {
                result.x = tempPointX + radius * cos(du) * (itemGroupNum - 1)
                result.y = tempPointY + radius * sin(du) * (itemGroupNum - 1)
            } else if (itemGroup == 3) {
                result.x = tempPointX - radius * cos(du) * (itemGroupNum - 1)
                result.y = tempPointY + radius * sin(du) * (itemGroupNum - 1)
            } else if (itemGroup == 4) {
                result.x = tempPointX - radius * (itemGroupNum - 1)
                result.y = tempPointY
            } else if (itemGroup == 5) {
                result.x = tempPointX - radius * cos(du) * (itemGroupNum - 1)
                result.y = tempPointY - radius * sin(du) * (itemGroupNum - 1)
            } else if (itemGroup == 6) {
                result.x = tempPointX + radius * cos(du) * (itemGroupNum - 1)
                result.y = tempPointY - radius * sin(du) * (itemGroupNum - 1)
            }
            return result
        } else {
            val itemGroup = ceil((hexagonalPoint.levelNumber.toDouble() / (level - 1))).toInt()
            val itemGroupNum = hexagonalPoint.levelNumber - (itemGroup - 1) * (level - 1)
            val tempPointX = centerX - (level - 1) * radius * cos((du * itemGroup))
            val tempPointY = centerY - (level - 1) * radius * sin((du * itemGroup))
            if (itemGroup == 1) {
                result.x = tempPointX + radius * (itemGroupNum - 1)
                result.y = tempPointY
            } else if (itemGroup == 2) {
                result.x = tempPointX + radius * cos(du) * (itemGroupNum - 1)
                result.y = tempPointY + radius * sin(du) * (itemGroupNum - 1)
            } else if (itemGroup == 3) {
                result.x = tempPointX - radius * cos(du) * (itemGroupNum - 1)
                result.y = tempPointY + radius * sin(du) * (itemGroupNum - 1)
            } else if (itemGroup == 4) {
                result.x = tempPointX - radius * (itemGroupNum - 1)
                result.y = tempPointY
            } else if (itemGroup == 5) {
                result.x = tempPointX - radius * cos(du) * (itemGroupNum - 1)
                result.y = tempPointY - radius * sin(du) * (itemGroupNum - 1)
            } else if (itemGroup == 6) {
                result.x = tempPointX + radius * cos(du) * (itemGroupNum - 1)
                result.y = tempPointY - radius * sin(du) * (itemGroupNum - 1)
            }
            return result
        }

    }


    /**
     * 3n(2) -3n + 1
     */
    private fun calculateLevel(number: Int): Int {
        for (level in 1..100) {
            if (number > calFormulaCount(level) && number <= calFormulaCount(level + 1)) {
                return level + 1
            }
        }
        return -1
    }

    private fun calculateHexagonalPoint(index: Int): HexagonalPoint {
        val result = HexagonalPoint(index = index)
        assert(index != 0)
        result.level = calculateLevel(index + 1)
        result.levelNumber = index + 1 - calFormulaCount(result.level - 1)
        return result
    }


    private fun scaleXY(view: View, x: Int, y: Int, distance: Float) {
        val scale = when {
            distance >= 0 && distance <= radius -> {
                val scale =
                    (radius - distance) / (radius) * (maxScaleSize - secondScaleSize)
                scale + secondScaleSize
            }
            distance > radius && distance <= scaleDistance -> { //0...secondScaleSize,不要问我怎么算的
                val ratio =
                    max(
                        0f,
                        StrictMath.min(
                            ((StrictMath.abs((y - centerY))) / (distance)),
                            1f
                        )
                    )
                val scale = (scaleDistance - distance) / (scaleDistance - radius)
                val scaleRatio =
                    secondScaleSize + StrictMath.pow(ratio.toDouble(), 6.0) * 2.5f
                StrictMath.min(secondScaleSize, 1 + scale * (scaleRatio.toFloat() - 1))
            }
            distance > scaleDistance -> {
                val scale =
                    (distance - scaleDistance + dismiss2NormalScaleDistance) / dismiss2NormalScaleDistance - 1 //0...max
                max(0f, 1 - StrictMath.pow(scale.toDouble(), 6.0).toFloat()) // 0..1

            }
            else -> {
                0f
            }
        }
        view.scaleX = scale
        view.scaleY = scale
    }


    private fun translateXY(view: View, x: Int, y: Int, distance: Float) {
        val translateRange = radius
        val centerPoint = getCenterPoint()
        val centerX = centerPoint.x
        val centerY = centerPoint.y
        if (distance <= translateRange) {
            view.translationX = 0f
            view.translationY = 0f
            return
        }
        when {
            else -> {
                val magnitude = (distance - translateRange) * 0.35f/// translateRange * 124f
                val cos = StrictMath.abs((centerY - y) / distance)
                val ratio = 1f - 0.5 * (StrictMath.pow(cos.toDouble(), 8.0))
                val magnitudeX = centerX - (centerX - x) * (distance - magnitude) / distance
                view.translationX = ((magnitudeX - x) * ratio).toFloat()
                val magnitudeY = centerY - (centerY - y) * (distance - magnitude) / distance
                view.translationY = ((magnitudeY - y) * ratio).toFloat()
            }
        }
    }


    private fun calFormulaCount(level: Int): Int {
        return 3 * level.toDouble().pow(2.0).toInt() - 3 * level + 1
    }


    private fun getItemCount(state: RecyclerView.State): Int {
        return state.itemCount
    }

    override fun onLayoutCompleted(state: RecyclerView.State) {
        super.onLayoutCompleted(state)
    }

    override fun scrollHorAndVerBy(
        dx: Int,
        dy: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ): Pair<Int, Int> {
        if (dx == 0 && dy == 0) {
            return Pair(0, 0)
        }
        val scrollXY = checkIfScroll(dx, dy)
        if (scrollXY.first == 0 && scrollXY.second == 0) {
            return Pair(0, 0)
        }
        layoutState.scrollX += scrollXY.first
        layoutState.scrollY += scrollXY.second
        offsetShowRegion()
        detachAndScrapAttachedViews(recycler)
        fill(recycler, state, false)
        recycleScrapViews(recycler)
//        offsetChild(-dx, -dy, state)
        return Pair(dx, dy)
    }

    private fun offsetShowRegion() {
        val len = 50.dp2Px()
        layoutState.showRegion.set(
            -len,
            -len,
            measuredWidth + len,
            measuredHeight + len
        )
        layoutState.showRegion.offset(layoutState.scrollX, layoutState.scrollY)
    }

    private fun offsetChild(dx: Int, dy: Int, state: RecyclerView.State) {
        val childCount = getItemCount(state)
        var destPosition: Int = 0
        var destChildDistance = 0f
        var minCloseDistance: Float = Float.MAX_VALUE
        for (position in 0 until childCount) {
            val child = findViewByPosition(position)
            if (child != null) {
                offsetChildHorAndVer(child, dx, dy)
                destChildDistance = shapeChange(child, position)
                bindRealViewHolderIfNeed(child, position)
            }
            if (destChildDistance < minCloseDistance) {
                minCloseDistance = destChildDistance
                destPosition = position
            }
        }
        setCenterPosition(destPosition)
    }


    private fun checkIfScroll(dx: Int, dy: Int): Pair<Int, Int> {
        val distance = calDistance(layoutState.scrollX + dx, layoutState.scrollY + dy)
        val maxDistance = maxCircleRadius.toFloat() + 100.dp2Px()
        if (distance <= maxDistance) {
            return Pair.create(dx, dy)
        } else {
            return Pair.create(0, 0)
        }
    }


    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    interface OnCenterChangedListener {
        fun onCenter(lastPosition: Int, currentPosition: Int)
    }


    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.CYAN
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }


    override fun onDraw(canvas: Canvas) {
        if (BuildConfig.DEBUG.not()) {
            return
        }
        val restore = canvas.save()
        paint.strokeWidth = 5f
        canvas.translate(-layoutState.scrollX.toFloat(), -layoutState.scrollY.toFloat())
        canvas.drawRect(viewRegion, paint)
        canvas.drawCircle(
            getCenterPoint().x.toFloat(),
            getCenterPoint().y.toFloat(),
            maxCircleRadius.toFloat(),
            paint
        )
        paint.strokeWidth = 10f
        canvas.drawPoint(viewRegion.exactCenterX(), viewRegion.exactCenterY(), paint)
        canvas.restoreToCount(restore)
    }

    /**
     * 滚动处理逻辑梳理：
     * 1. 收集滚动信息
     * 2. 滚动不处理detach/remove，只处理添加
     */

    private inner class LayoutState {
        var scrollX: Int = 0
        var scrollY: Int = 0
        var showRegion = Rect()
        var minCloseDistance: Float = 0f
        fun reset() {
            scrollX = 0
            scrollY = 0
            minCloseDistance = 0f
            showRegion.set(0, 0, measuredWidth, measuredHeight)
        }

    }

}

