package com.demon.yu.avatar.interact

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.IntRange
import com.example.mypractice.R

class LightInteractNumberView(context: Context?, attrs: AttributeSet? = null) :
    LinearLayout(context, attrs) {


    init {
        orientation = HORIZONTAL

    }

    private var currentNumber = 0
    private val numberDrawable = arrayOf(
        R.drawable.n0,
        R.drawable.n1,
        R.drawable.n2,
        R.drawable.n3,
        R.drawable.n4,
        R.drawable.n5,
        R.drawable.n6,
        R.drawable.n7,
        R.drawable.n8,
        R.drawable.n9,
    )


    private val dismissRunnable = Runnable {
        animate().cancel()
        animate().alpha(0f).setDuration(80L).start()
    }


    private fun isReachMax(): Boolean {
        return currentNumber >= 10
    }

    fun setNumber(number: Int) {
        if (alpha < 1f) {
            animate().cancel()
            animate().alpha(1f).setDuration(80L).start()
        }
        addOrShowPrefixViewIfNeed()
        updateNumber(number)
    }

    fun continueNumber(number: Int) {
        updateNumber(number)
    }

    private var valueAnimator: ValueAnimator? = null
    private fun updateNumber(number: Int) {
        currentNumber = number
        if (isReachMax()) {
            showMaxView()
        } else {
            refreshNumbersView()
        }
        post {
            pivotX = (width / 2).toFloat()
            pivotY = (height / 2).toFloat()
            if (valueAnimator?.isRunning == true) {
                valueAnimator?.cancel()
            }
            valueAnimator = ValueAnimator.ofFloat(1f, 0.8f, 1f)
            valueAnimator?.duration = 120L
            valueAnimator?.addUpdateListener {
                scaleX = it.animatedValue as Float
                scaleY = it.animatedValue as Float
            }
            valueAnimator?.start()
        }
    }

    private fun showMaxView() {
        addOrShowPrefixViewIfNeed()
        if (childCount >= 2) {
            for (i in 1 until childCount) {
                getChildAt(i).visibility = View.VISIBLE
                if (i == 1) {
                    (getChildAt(1) as ImageView).let {
                        it.visibility = View.VISIBLE
                        it.setImageResource(R.drawable.max)
                    }
                }
            }
        } else if (childCount == 1) {
            val imageView = createImageView()
            imageView.setImageResource(R.drawable.max)
            addView(imageView, 1)
        }


    }

    private fun refreshNumbersView() {
        var destNumber = currentNumber
        assert(childCount >= 1)
        var leaveChild = childCount - 1
        do {
            val dest = destNumber % 10
            if (leaveChild <= 0) {
                addNumberView(dest, 1)
            } else {
                updateNumberView(dest, leaveChild)
            }
            leaveChild -= 1
            destNumber /= 10
        } while (destNumber > 0)
    }

    private fun addOrShowPrefixViewIfNeed() {
        if (childCount == 0) {
            val prefix = createImageView()
            prefix.setImageResource(R.drawable.prefix)
            addView(prefix)
        } else {
            (getChildAt(0) as ImageView).let {
                it.visibility = View.VISIBLE
            }
        }
    }

    fun end() {
        alpha = 0f
        currentNumber = 0
        for (i in 0 until childCount) {
            getChildAt(i).visibility = View.GONE
        }
    }

    private fun addNumberView(@IntRange(from = 0, to = 9) number: Int, position: Int) {
        val imageView = createImageView()
        imageView.setImageResource(numberDrawable[number])
        addView(imageView, position)
    }

    private fun updateNumberView(@IntRange(from = 0, to = 9) number: Int, childPosition: Int) {
        val imageView: ImageView = getChildAt(childPosition) as ImageView
        imageView.setImageResource(numberDrawable[number])
        imageView.visibility = View.VISIBLE
    }


    override fun generateDefaultLayoutParams(): LayoutParams {
        return LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    }


    private fun createImageView(): ImageView {
        return ImageView(context)
    }

}