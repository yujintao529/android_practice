package com.demon.yu.system

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.util.TypedValue
import android.view.*
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.view.*
import androidx.viewpager.widget.PagerAdapter
import com.demon.yu.view.recyclerview.ColorUtils
import com.example.mypractice.Logger
import com.example.mypractice.R
import kotlinx.android.synthetic.main.activity_fitsystem_window.*

/**
 * @description
 *
 *  q1. viewpager不设置fitsSystemWindows，让其adpater的子view设置没有效果？理论上应该会生效的。
 *     a1: 因为viewPager是在adapter中添加view的，dispatchApplyWindowInsets是在viewLayout前发生的。
 *         所以需要在添加子view后，调用requestApplyInsets，重新触发一次传递才可以生效
 *
 *  q2. 在viewPager中，如何让每个page的背景铺满（一个imageView做为背景），然后titleBar正常下移呢
 *     a2： 在添加view的时候触发requestApplyInsets，同时让titlBar设置fitsSystemWindows即可
 *
 *  info1：windowInsets.consumeSystemWindowInsets 会生成一个完全消费系统状态栏等insets的结构，如果要消费部分，需要自己builder
 *        一个windowInsets。
 *  info2：viewGroup.dispatchApplyWindowInsets-viewGroup.onApplyWindowInsets
 *  ｜fitsSystemWindows=true --->  fitSystemWindowsInt --->自己消费
 *  ｜fitsSystemWindows=false ---> fitSystemWindows--->子view.dispatchApplyWindowInsets
 *
 *
 *  fitsSystemWindows ：只是增加了默认处理行为，也就是增加了padding，如果不设置也会收到onApplyWindowInsets回掉，
 *                      但是需要根据自己的业务自己处理行为了
 *
 *
 *  sBrokenInsetsDispatch ：在targetSdkVersion < Build.VERSION_CODES.Q前，windowInsets被消费后，会停止后面子view继续消费，
 *                          新版本上，会继续消费回掉。
 *
 *
 *
 * @author yujinta.529
 * @create 2022-11-14
 */
class FitSystemWindowAct : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fitsystem_window)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.decorView.systemUiVisibility =
                (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN.or(View.SYSTEM_UI_FLAG_LAYOUT_STABLE))
            window.statusBarColor = Color.TRANSPARENT
        }

        val pagerAdapter = initAdapter()
        fitSystemViewPager.adapter = pagerAdapter

        val result = ViewCompat.getFitsSystemWindows(fitSystemViewPager)
        Logger.debug("FitSystemWindowAct", "getFitsSystemWindows $result")
    }

    @SuppressLint("WrongConstant")
    private fun initAdapter(): PagerAdapter {
        val list = mutableListOf<View>()
        for (i in 0..5) {
            val view = FitSystemFrameLayout(this)
            val titleBar = TextView(this)
            titleBar.text = "我是titleBar"
            titleBar.setPadding(20, 10, 20, 10)
            titleBar.setBackgroundColor(Color.WHITE)
            titleBar.setTextColor(Color.BLACK)
            titleBar.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20f)
            val lp = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            ViewCompat.setOnApplyWindowInsetsListener(titleBar) { v, insets ->
                lp.topMargin = insets.systemWindowInsetTop
                Logger.debug("FitSystemWindowAct", "$v ${insets.systemWindowInsets}")
                insets//不能使用insets.consumeSystemWindowInsets()消费内容，因为sBrokenInsetsDispatch这个会导致editText无法收到回掉了
            }
            lp.gravity = Gravity.CENTER_HORIZONTAL or Gravity.TOP
            val bg = View(this)//背景view，可能是个网络图啥的
            bg.setBackgroundColor(ColorUtils.getRandomColor())
            view.addView(
                bg,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            view.addView(titleBar, lp)
            val editText = AppCompatEditText(this)//输入框
            editText.hint = "请点击"
            val editLp = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            editText.setHintTextColor(Color.GRAY)
            editText.setPadding(20, 10, 20, 10)
            editText.setBackgroundColor(Color.WHITE)
            editText.setTextColor(Color.BLACK)
            editText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20f)

            editLp.setMargins(40, 20, 100, 0)
            editLp.gravity = Gravity.BOTTOM
            view.addView(editText, editLp)

            val button = AppCompatButton(this)
            button.text = "点击输入"
            button.setPadding(40, 30, 40, 30)
            button.setTextColor(Color.BLACK)
            val btnLp = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            btnLp.gravity = Gravity.CENTER
            //21一下会使用applyWindowInsets替换，30会使用动画
            ViewCompat.setWindowInsetsAnimationCallback(editText,object:
                WindowInsetsAnimationCompat.Callback(DISPATCH_MODE_STOP) {
                override fun onProgress(
                    insets: WindowInsetsCompat,
                    runningAnimations: MutableList<WindowInsetsAnimationCompat>
                ): WindowInsetsCompat {
                    editText.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                        bottomMargin = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
                    }
                    return insets
                }
            })
            button.setOnClickListener {
                WindowInsetsControllerUtils.showInputMethod(window, editText)
            }
            view.addView(button, btnLp)


            list.add(view)
        }
        return FitViewAdapter(list)
    }


    class FitViewAdapter(val listView: List<View>) : PagerAdapter() {
        override fun getCount(): Int {
            return listView.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view == `object`
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val view = listView[position]
            container.addView(
                view,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            container.requestApplyInsets()////必须每次添加必须要触发，因为requestApplyInsets是在布局前或者有系统布局改变时触发
            return view
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }
    }

}