package com.demon.yu.avatar.interact

import android.content.Context
import android.graphics.*
import android.util.Pair
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import androidx.core.util.forEach
import androidx.recyclerview.widget.AvatarLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.demon.yu.extenstion.dp2Px
import com.demon.yu.view.recyclerview.DPoint
import com.demon.yu.view.recyclerview.FakeLayoutCoorExchangeUtils
import com.demon.yu.view.recyclerview.HexagonalPoint
import com.demon.yu.view.recyclerview.toPoint
import com.example.mypractice.Logger
import com.example.mypractice.common.Common
import kotlin.math.*


class AvatarComposeLayoutManager(val context: Context) : AvatarLayoutManager(),
    AvatarComposeRecyclerView.OnDrawListener {

    companion object {
        const val TAG = "AvatarComposeLayoutManager"
    }

    private var measureWidth: Int = 0
    private var measureHeight: Int = 0


    var radius: Int = 120.dp2Px()
    private val coordinateCache: SparseArray<Point> = SparseArray(200)

    private val viewRegion = Rect()
    private var lastChildCount = 0
    private var avatarComposeRecyclerView: AvatarComposeRecyclerView? = null

    private var fakeScrollX: Int = 0
    private var fakeScrollY: Int = 0

    private var currentPosition: Int = -1

    var onCenterChangedListener: OnCenterChangedListener? = null

    //仅支持matchParent及exactly width/height
    override fun onMeasure(
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State,
        widthSpec: Int,
        heightSpec: Int
    ) {
//        super.onMeasure(recycler, state, widthSpec, heightSpec)
        measureWidth = View.MeasureSpec.getSize(widthSpec)
        measureHeight = View.MeasureSpec.getSize(heightSpec)
        setMeasuredDimension(measureWidth, measureHeight)
    }

    override fun setRecyclerView(recyclerView: RecyclerView?) {
        super.setRecyclerView(recyclerView)
        avatarComposeRecyclerView = recyclerView as? AvatarComposeRecyclerView
    }

    //关闭，虽然好像没啥用
    override fun isMeasurementCacheEnabled(): Boolean {
        return false
    }


    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        if (itemCount == 0) {
            removeAndRecycleAllViews(recycler);
            return
        }
        detachAndScrapAttachedViews(recycler)
        clearCoordinateCacheIfNeed(state.itemCount)
        fill(recycler, state)
        recycleScrapViews(recycler)
    }

    private fun recycleScrapViews(recycler: RecyclerView.Recycler) {

    }


    private fun clearCoordinateCacheIfNeed(currentCount: Int) {
        if ((lastChildCount >= 7 && currentCount < 7) || (lastChildCount < 7 && currentCount >= 7)
            || (lastChildCount < 7 && currentCount != lastChildCount)
        ) {
            coordinateCache.clear()
        }
        lastChildCount = currentCount
    }


    private var maxCircleRadius = 0
    private fun fill(recycler: RecyclerView.Recycler, state: RecyclerView.State) {

//        val viewCache = SparseArray<View>(childCount)
//        if (childCount != 0) {
//            //...
//            //Cache all views by their existing position, before updating counts
//            for (i in 0 until childCount) {
//                val child = getChildAt(i)
//                viewCache.put(i, child)
//            }
//
//            //Temporarily detach all views.
//            // Views we still need will be added back at the proper index.
//            for (i in 0 until viewCache.size()) {
//                detachView(viewCache.get(i))
//            }
//        }
        val visibleChildCount = getVisibleCount(state)
        var destPosition: Int = 0
        var destChildDistance = 0f
        var minCloseDistance: Float = Float.MAX_VALUE
        for (position in visibleChildCount - 1 downTo 0) {
            val view = recycler.getViewForPosition(position)
            addView(view)
            measureChildWithMargins(view, 0, 0)
            layoutChildInternal(view, position, visibleChildCount)
            FakeLayoutCoorExchangeUtils.setCenterPivot(view)
            destChildDistance = shapeChange(view, position)
            bindRealViewHolderIfNeed(view, position)
            if (destChildDistance < minCloseDistance) {
                minCloseDistance = destChildDistance
                destPosition = position
            }
        }
        setCenterPosition(destPosition)
        if (visibleChildCount == 1) {
            maxCircleRadius = radius
            val left = (measureWidth - maxCircleRadius * 2) / 2
            val top = (measureHeight - maxCircleRadius * 2) / 2
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
            maxCircleRadius = max(viewRegion.width(), viewRegion.height()) / 2 + radiusDiff
        }

//        Logger.debug("AvatarComposeLayoutManager", "viewRegion=$viewRegion")

//        //删除无用detachView
//        for (i in 0 until viewCache.size()) {
//            val removingView = viewCache.get(i)
//            recycler.recycleView(removingView)
//        }
    }

    private fun getCenterPoint(): Point {
        return avatarComposeRecyclerView?.getCenterPoint() ?: Point(
            Common.screenWidth / 2,
            Common.screenHeight / 2
        )
    }


    private fun shapeChange(child: View, position: Int): Float {
        val avatarComposeRecyclerView = avatarComposeRecyclerView ?: return 0f
        val childPoint = FakeLayoutCoorExchangeUtils.getCenterPoint(child)
        val destChildDistance =
            avatarComposeRecyclerView.getDistance(childPoint.x, childPoint.y)
        avatarComposeRecyclerView.translateXY(
            child,
            childPoint.x,
            childPoint.y,
            destChildDistance
        )
        avatarComposeRecyclerView.scaleXY(child, childPoint.x, childPoint.y, destChildDistance)
        return destChildDistance
    }


    private fun layoutChildInternal(view: View, position: Int, childCount: Int) {
        val point = calculateChildCoordinate(position, childCount)
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


    private fun calculateChildCoordinate(position: Int, childCount: Int): Point {
        var cache = coordinateCache.get(position)
        if (cache != null) {
            return cache
        }
        if (position == 0) {
            cache = Point(measureWidth / 2, measureHeight / 2)
            coordinateCache.put(position, cache)
            return cache
        } else {
            cache = coordinateCache.get(0)
            if (cache == null) {
                cache = Point(measureWidth / 2, measureHeight / 2)
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
        coordinateCache.forEach { key, value ->
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


    private fun calFormulaCount(level: Int): Int {
        return 3 * level.toDouble().pow(2.0).toInt() - 3 * level + 1
    }


    private fun getVisibleCount(state: RecyclerView.State): Int {
        return state.itemCount
    }

    override fun onLayoutCompleted(state: RecyclerView.State) {
        super.onLayoutCompleted(state)
        Logger.debug("AvatarComposeLayoutManager", "onLayoutCompleted ${state.itemCount}")
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
        fakeScrollX += scrollXY.first
        fakeScrollY += scrollXY.second
        offsetChild(-dx, -dy, state)
//        offsetChild(-dx, -dy, state)
        return Pair(dx, dy)
    }

//    fun damping(x: Float, max: Float): Float {
//        var y = Math.abs(x);
//
//        y = (0.82231 * max / (1 + 4338.47 / Math.pow(y, 1.14791))).toFloat()
//
//        return Math.round(if (x < 0f) -y else y).toFloat()
//    }

    private var realScrollXTotal: Float = 0f
    private var realScrollYTotal: Float = 0f
    private fun checkIfScroll(dx: Int, dy: Int): Pair<Int, Int> {
        val distance = calDistance(fakeScrollX + dx, fakeScrollY + dy)
        val maxDistance = maxCircleRadius.toFloat() + 200f
        realScrollXTotal += dx
        realScrollYTotal += dy
        realScrollXTotal = min(maxDistance, realScrollXTotal)
        realScrollYTotal = min(maxDistance, realScrollXTotal)
        if (distance <= maxDistance) {
            return Pair.create(dx, dy)
        } else {
            return Pair.create(0, 0)
//            val dis = min(maxDistance, distance.toFloat())
//            var damping = 1 - (dis - maxCircleRadius) / 200f // 1..0
//            //0.3-0.0
//            damping *= 0.4f
////            val ratio = (dis + (dis - maxCircleRadius) * damping) / maxDistance
//            val resDx = Math.round(dx * damping)
//            val resDy = Math.round(dy * damping)
//            return Pair.create(resDx, resDy)
        }
    }


    private fun calDistance(x: Int, y: Int): Int {
        return sqrt(abs(x).toDouble().pow(2) + abs(y).toDouble().pow(2)).toInt()
    }


    private fun offsetChild(dx: Int, dy: Int, state: RecyclerView.State) {
        val childCount = getVisibleCount(state)
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


    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.CYAN
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }


    override fun onDraw(canvas: Canvas) {
        val restore = canvas.save()
        paint.strokeWidth = 5f
        canvas.translate(-fakeScrollX.toFloat(), -fakeScrollY.toFloat())
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

    interface OnCenterChangedListener {
        fun onCenter(lastPosition: Int, currentPosition: Int)
    }
}