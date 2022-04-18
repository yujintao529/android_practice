package com.demon.yu.view.recyclerview

import android.content.Context
import android.graphics.Point
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.demon.yu.extenstion.dp2Px
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin


class MyCustomizeCircleLayoutManger(val context: Context) : RecyclerView.LayoutManager() {


    private var measureWidth: Int = 0
    private var measureHeight: Int = 0


    private var radius: Int = 0

    private var maxScaleSize = 1.5f


    init {
        radius = 110.dp2Px()
    }

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

    //关闭，虽然好像没啥用
    override fun isMeasurementCacheEnabled(): Boolean {
        return false
    }

    override fun canScrollHorizontally(): Boolean {
        return true
    }


    override fun canScrollVertically(): Boolean {
        return true
    }


    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
//        super.onLayoutChildren(recycler, state)

        if (itemCount == 0) {
            detachAndScrapAttachedViews(recycler);
            return;
        }
        detachAndScrapAttachedViews(recycler)
        fill(recycler, state)
    }


    private fun fill(recycler: RecyclerView.Recycler, state: RecyclerView.State) {

        val viewCache = SparseArray<View>(childCount)
//...
//...
        if (childCount != 0) {
            //...
            //Cache all views by their existing position, before updating counts
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                viewCache.put(i, child)
            }

            //Temporarily detach all views.
            // Views we still need will be added back at the proper index.
            for (i in 0 until viewCache.size()) {
                detachView(viewCache.get(i))
            }
        }

        for (i in 0 until getVisibleCount(state)) {
            var view = viewCache.get(i)
            if (view == null) {
                view = recycler.getViewForPosition(i)
                addView(view)
                measureChildWithMargins(view, 0, 0)
                layoutChildInternal(view, i)
            } else {

            }

        }


        //删除无用detachView
        for (i in 0 until viewCache.size()) {
            val removingView = viewCache.get(i)
            recycler.recycleView(removingView)
        }

    }


    private fun layoutChildInternal(view: View, position: Int) {
//        if (position > 7) {
//            return
//        }
        val point = calculateChildCoordinate(position)
        val childWidth = view.measuredWidth
        val childHeight = view.measuredHeight

        layoutDecorated(
            view,
            point.x - childWidth / 2,
            point.y - childHeight / 2,
            point.x + childWidth / 2,
            point.y + childHeight / 2
        )
    }


    private val coordinateCache: SparseArray<Point> = SparseArray(35)


    private fun calculateChildCoordinate(position: Int): Point {
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
                coordinateCache.put(position, cache)
            }
        }
        val zero = coordinateCache.get(0)
        val dest =
            calculateCircleCoordinate(position + 1, radius, zero.x, zero.y).toPoint()
        coordinateCache.put(position, dest)
        return dest
    }


    /**
     *
     * 1，6，12，18
     * [1,7]  1
     * [8-19] 2
     * [20-37] 3
     */

    /**
     * index [0-6]
     *
     *
     */
    private fun calculateCoordinate(
        index: Int,
        radius: Int,
        centerX: Float,
        centerY: Float
    ): Pair<Int, Int> {

        val x = centerX + radius * cos(2 * Math.PI * index / 6)
        val y = centerY + radius * sin(2 * Math.PI * index / 6)
        return x.toInt() to y.toInt()
    }

    /**
     *    CGPoint tmpPoint = CGPointMake(
     *    centerPoint.x - (level - 1) * cellSize * cos(itemDu * itemGroup),
     *    centerPoint.y - (level - 1) * cellSize * sin(itemDu * itemGroup));
     */
    private fun calculateCircleCoordinate(
        index: Int,
        radius: Int,
        centerX: Int,
        centerY: Int
    ): DPoint {
        val hexagonalPoint = calculateHexagonalPoint(index)
        val level = hexagonalPoint.level
        val destRadius = radius * (level - 1)
        val avgAngle = 2 * PI / (calFormulaCount(level) - calFormulaCount(level - 1))
        if (level == 1) {
            return DPoint(centerX.toDouble(), centerY.toDouble())
        } else if (level % 2 == 0) {
            val destAngle = avgAngle / 2 + (hexagonalPoint.levelNumber - 1) * avgAngle
            return DPoint(
                centerX + sin(destAngle) * destRadius, centerY - cos(destAngle) * destRadius
            )
        } else {
            val destAngle = (hexagonalPoint.levelNumber - 1) * avgAngle
            return DPoint(
                centerX + sin(destAngle) * destRadius, centerY - cos(destAngle) * destRadius
            )
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
        if (index == 1) {
            result.level = 1
            result.levelNumber = 1
        } else {
            result.level = calculateLevel(index)
            result.levelNumber = index - calFormulaCount(result.level - 1)
        }
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

    }

    override fun scrollHorizontallyBy(
        dx: Int,
        recycler: RecyclerView.Recycler?,
        state: RecyclerView.State?
    ): Int {
        offsetChildrenHorizontal(-dx)
        return dx
    }

    override fun scrollVerticallyBy(
        dy: Int,
        recycler: RecyclerView.Recycler?,
        state: RecyclerView.State?
    ): Int {
        offsetChildrenVertical(-dy)
        return dy
    }

    override fun onItemsUpdated(recyclerView: RecyclerView, positionStart: Int, itemCount: Int) {
        super.onItemsUpdated(recyclerView, positionStart, itemCount)
    }


    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }
}