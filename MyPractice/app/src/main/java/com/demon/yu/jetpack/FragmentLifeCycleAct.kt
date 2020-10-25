package com.demon.yu.jetpack

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Lifecycle
import com.example.mypractice.R
import kotlinx.android.synthetic.main.activity_fragment_lifecycle.*

fun newFragment(color: Int): FragmentLifeCycleAct.FragmentColor {
    val bundle = Bundle()
    bundle.putInt("color", color)
    val fragmentColor = FragmentLifeCycleAct.FragmentColor()
    fragmentColor.arguments = bundle
    return fragmentColor
}

class FragmentLifeCycleAct : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_lifecycle)
        supportFragmentManager.registerFragmentLifecycleCallbacks(FragmentLifeCallbackWithLogger(), false)
        supportFragmentManager.beginTransaction().apply {
            val fragment = newFragment(Color.BLUE)
            add(R.id.fragmentContainer, fragment)
            setMaxLifecycle(fragment, Lifecycle.State.STARTED)
            commitAllowingStateLoss()
        }
        initViewPager()
    }

    private fun initViewPager() {
        val fragments= mutableListOf<Fragment>()
        fragments.add( newFragment(Color.RED))
        fragments.add( newFragment(Color.GRAY))
        fragments.add( newFragment(Color.YELLOW))
        viewPager.adapter=object: FragmentPagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT){
            override fun getItem(position: Int): Fragment {
                return fragments[position]
            }

            override fun getCount(): Int {
                return fragments.size
            }
        }
    }

    class FragmentColor : Fragment() {

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val view = View(activity)
            val color = arguments?.getInt("color", Color.BLACK) ?: Color.BLACK
            view.setBackgroundColor(color)
            view.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            return view
        }

        override fun toString(): String {
            return super.toString()
        }
    }
}