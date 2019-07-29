package com.demon.yu.window

import android.annotation.SuppressLint
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Bundle
import android.os.Handler
import android.support.v4.view.ViewConfigurationCompat
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.TextView
import android.widget.Toast
import com.example.mypractice.Logger
import com.example.mypractice.R
import com.example.mypractice.common.Common
import kotlin.math.abs

class OverlayWindow(private val windowManager: WindowManager) {


    companion object {

        @SuppressLint("CI_StaticFieldLeak")
        var overlayWindow: OverlayWindow? = null

        fun getInstance(windowManager: WindowManager): OverlayWindow? {
            if (overlayWindow == null) {
                overlayWindow = OverlayWindow(windowManager)
            }
            return overlayWindow
        }
    }

    private val windowLayoutParams = WindowManager.LayoutParams()


    private val rootView: View
    val viewConfiguration = ViewConfiguration.get(Common.application)

    private var isAdd = -1

    init {
        val textView = TextView(Common.application)
        textView.text = "悬浮窗"
        textView.setTextColor(Color.WHITE)
        textView.setPadding(20, 20    , 20, 20)
        textView.setBackgroundResource(R.drawable.overlay_window_bg)
        rootView = textView
        windowLayoutParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON.or(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        windowLayoutParams.x = 200
        windowLayoutParams.y = 200
        windowLayoutParams.gravity = Gravity.LEFT.or(Gravity.TOP)
        windowLayoutParams.alpha = 1f
        windowLayoutParams.format = PixelFormat.RGBA_8888
        windowLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
        windowLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        textView.setOnClickListener {
            val intent = Intent(Common.application, OverlayWindowActivity::class.java)
            intent.setPackage(Common.application.packageName)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val handler = Handler()
            val pendingIntent = PendingIntent.getActivity(Common.application, 1, intent, PendingIntent.FLAG_ONE_SHOT)
            pendingIntent.send(2, { pendingIntent, intent, resultCode, resultData, resultExtras ->
                Toast.makeText(Common.application, "跳转成功!", Toast.LENGTH_SHORT).show()
            }, handler)
        }
        textView.setOnKeyListener { v, keyCode, event ->
            Toast.makeText(Common.application, "按下 $keyCode $event!", Toast.LENGTH_SHORT).show()
            false
        }

        textView.addOnAttachStateChangeListener(object:View.OnAttachStateChangeListener{
            override fun onViewDetachedFromWindow(v: View?) {
                Logger.d("onViewDetachedFromWindow $v")
            }

            override fun onViewAttachedToWindow(v: View?) {
                Logger.d("onViewAttachedToWindow $v")
            }

        })

        textView.setOnTouchListener(object : View.OnTouchListener {
            var initX: Float = 0f
            var initY: Float = 0f
            var dragging = false
            var lastX: Float = 0f
            var lastY: Float = 0f
            override fun onTouch(v: View, event: MotionEvent): Boolean {


                val eventAction = event.actionMasked

                when (eventAction) {
                    MotionEvent.ACTION_DOWN -> {
                        initX = event.rawX
                        initY = event.rawY
                        lastX = initX
                        lastY = initY

                        dragging = false
                    }
                    MotionEvent.ACTION_MOVE -> {
                        var diffX = event.rawX - initX
                        var diffY = event.rawY - initY

                        if (dragging || abs(diffX) > viewConfiguration.scaledTouchSlop || abs(diffY) > viewConfiguration.scaledTouchSlop) {

                            windowLayoutParams.x += (event.rawX - lastX).toInt()
                            windowLayoutParams.y += (event.rawY - lastY).toInt()
                            Logger.d("update layoutParams $windowLayoutParams")
                            windowManager.updateViewLayout(v, windowLayoutParams)
                            dragging = true
                        }
                        lastX = event.rawX
                        lastY = event.rawY
                    }
                    MotionEvent.ACTION_UP,
                    MotionEvent.ACTION_CANCEL -> {
                    }

                }
                return dragging
            }

        })

    }


    fun show() {
        if (isAdd != 1) {
            isAdd = 1
            try {
                windowManager.addView(rootView, windowLayoutParams)
            } catch (ex: Exception) {
                Toast.makeText(Common.application, "${ex.message}", Toast.LENGTH_SHORT).show()
                isAdd = 0
            }
        }

    }

    fun hide() {
        if (isAdd == 1) {
            isAdd = 0
            windowManager.removeView(rootView)
        }
    }


    fun updateType(type: Int) {
        windowLayoutParams.type = type
        hide()
        show()
    }

}