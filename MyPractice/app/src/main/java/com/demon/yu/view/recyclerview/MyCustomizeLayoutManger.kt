package com.demon.yu.view.recyclerview

import android.content.Context
import android.graphics.Point
import android.graphics.Rect
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import androidx.core.util.forEach
import androidx.recyclerview.widget.RecyclerView
import com.demon.yu.extenstion.dp2Px
import kotlin.math.*


class MyCustomizeLayoutManger(val context: Context) : RecyclerView.LayoutManager() {


    private var measureWidth: Int = 0
    private var measureHeight: Int = 0


    var radius: Int = 120.dp2Px()


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

    private val viewRegion = Rect()

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
        viewRegion.set(find4Coordinate()) //找到四周的范围


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
            calculateCoordinate2(position + 1, radius, zero.x, zero.y).toPoint()
        coordinateCache.put(position, dest)

        return dest
    }

    //先不复用的view的场景下也就是所有的view都添加的情况下，找到最上，最左，最右，最下的坐标，用来辅助是否可以滑动的情况
    private fun find4Coordinate(): Rect {
        val rect = Rect()
        coordinateCache.forEach { key, value ->
            if (value.x < rect.left) {
                rect.left = value.x
            }
            if (value.x > rect.right) {
                rect.right = value.x
            }
            if (value.y < rect.top) {
                rect.top = value.y
            }
            if (value.y > rect.bottom) {
                rect.bottom = value.y
            }
        }
        return rect
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
    private fun calculateCoordinate2(
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
            if (itemGroupNum == 1) {

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