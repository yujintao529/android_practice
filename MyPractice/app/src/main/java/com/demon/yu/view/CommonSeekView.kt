package com.demon.yu.view

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatSeekBar
import com.demon.yu.utils.LayoutInflaterManager
import com.demon.yu.utils.MathUtils
import com.example.mypractice.R

class CommonSeekView(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs), SeekBar.OnSeekBarChangeListener {
    private var labelValueTV: TextView
    private var seekBarView: AppCompatSeekBar

    private val maxProgress: Int = 1000

    private var maxValue: Float = 100f
    private var minValue: Float = 0f
    private var currentValue: Float = 0f
    private var label: String = "无"

    var valueChangedListener: ValueChangedListener? = null

    init {
        LayoutInflaterManager.getInflater(context).inflate(R.layout.seek_bar_item_with_label_layout, this)
        labelValueTV = findViewById(R.id.labelValue)
        seekBarView = findViewById(R.id.seekBar)
        seekBarView.max = maxProgress
        seekBarView.setOnSeekBarChangeListener(this)
        refreshSeekBarLabelAndValue()
    }

    fun setLabel(label: String) {
        this.label = label
        refreshSeekBarLabelAndValue()
    }

    fun initValueRange(value: Float, minValue: Float, maxValue: Float) {
        val value = MathUtils.clamp(value, minValue, maxValue)
        this.maxValue = maxValue
        this.minValue = minValue
        setCurrentValue(value)
    }

    private fun setCurrentValue(value: Float) {
        val value = MathUtils.clamp(value, minValue, maxValue)
        val ratio = (value - minValue) / (maxValue - minValue)
        seekBarView.progress = (ratio * maxProgress).toInt()
    }


    private fun onProgressChanged() {
        val ratio = seekBarView.progress * 1f / maxProgress
        currentValue = (maxValue - minValue) * ratio + minValue
        valueChangedListener?.onValueChanged(currentValue)
        refreshSeekBarLabelAndValue()
    }


    private fun refreshSeekBarLabelAndValue() {
        labelValueTV.text = "$label : $currentValue"
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        onProgressChanged()
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
    }

    @FunctionalInterface
    interface ValueChangedListener {
        fun onValueChanged(value: Float)
    }

}