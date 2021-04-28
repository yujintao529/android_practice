package com.demon.yu.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatSeekBar
import com.demon.yu.utils.LayoutInflaterManager
import com.example.mypractice.R

class CommonSeekView(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs), SeekBar.OnSeekBarChangeListener {
    private var labelValueTV: TextView
    private var seekBarView: AppCompatSeekBar

    private val maxProgress: Int = 1000

    private var maxValue: Number = 100
    private var minValue: Number = 0
    private var currentValue: Number = 0
    private var label: String = ""

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

    fun <T : Number> initSeekRange(current: T, min: T, max: T) {
        maxValue = max
        currentValue = current
        minValue = min
        refreshSeekBarLabelAndValue()
    }

    private fun onProgressChanged() {

    }

    private fun refreshSeekBarLabelAndValue() {
        labelValueTV.text = "$label : $currentValue"
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
    }


}