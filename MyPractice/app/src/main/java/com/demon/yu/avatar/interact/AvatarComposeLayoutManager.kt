package com.demon.yu.avatar.interact

import android.content.Context
import android.graphics.Point
import android.graphics.Rect
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import androidx.core.util.forEach
import androidx.recyclerview.widget.RecyclerView
import com.demon.yu.extenstion.dp2Px
import com.demon.yu.view.recyclerview.DPoint
import com.demon.yu.view.recyclerview.FakeLayoutCoorExchangeUtils
import com.demon.yu.view.recyclerview.HexagonalPoint
import com.demon.yu.view.recyclerview.toPoint
import kotlin.math.*


class AvatarComposeLayoutManager(val context: Context) : RecyclerView.LayoutManager() {


    private var measureWidth: Int = 0
    private var measureHeight: Int = 0


    var radius: Int = 120.dp2Px()
    private val coordinateCache: SparseArray<Point> = SparseArray(200)

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
        clearCoordinateCacheIfNeed(state.itemCount)
        fill(recycler, state)
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

    fun fill(recycler: RecyclerView.Recycler, state: RecyclerView.State) {

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

        val visibleChildCount = getVisibleCount(state)
        for (i in 0 until visibleChildCount) {
            var view = viewCache.get(i)
            if (view == null) {
                view = recycler.getViewForPosition(i)
                addView(view)
                measureChildWithMargins(view, 0, 0)
                layoutChildInternal(view, i, visibleChildCount)

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
                coordinateCache.put(position, cache)
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

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }
}