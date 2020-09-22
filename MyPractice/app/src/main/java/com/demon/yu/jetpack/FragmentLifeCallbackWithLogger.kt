package com.demon.yu.jetpack

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.mypractice.Logger

class FragmentLifeCallbackWithLogger : FragmentManager.FragmentLifecycleCallbacks() {
    override fun onFragmentViewCreated(fm: FragmentManager, f: Fragment, v: View, savedInstanceState: Bundle?) {
        super.onFragmentViewCreated(fm, f, v, savedInstanceState)
        Logger.d("onFragmentViewCreated,$f")
    }

    override fun onFragmentStopped(fm: FragmentManager, f: Fragment) {
        super.onFragmentStopped(fm, f)
        Logger.d("onFragmentStopped,$f")
    }

    override fun onFragmentCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
        super.onFragmentCreated(fm, f, savedInstanceState)
        Logger.d("onFragmentCreated,$f")
    }

    override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
        super.onFragmentResumed(fm, f)
        Logger.d("onFragmentResumed,$f")
    }

    override fun onFragmentAttached(fm: FragmentManager, f: Fragment, context: Context) {
        super.onFragmentAttached(fm, f, context)
        Logger.d("onFragmentAttached,$f")
    }

    override fun onFragmentPreAttached(fm: FragmentManager, f: Fragment, context: Context) {
        super.onFragmentPreAttached(fm, f, context)
        Logger.d("onFragmentPreAttached,$f")
    }

    override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
        super.onFragmentDestroyed(fm, f)
        Logger.d("onFragmentDestroyed,$f")
    }

    override fun onFragmentSaveInstanceState(fm: FragmentManager, f: Fragment, outState: Bundle) {
        super.onFragmentSaveInstanceState(fm, f, outState)
        Logger.d("onFragmentSaveInstanceState,$f")
    }

    override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
        super.onFragmentStarted(fm, f)
        Logger.d("onFragmentStarted,$f")
    }

    override fun onFragmentViewDestroyed(fm: FragmentManager, f: Fragment) {
        super.onFragmentViewDestroyed(fm, f)
        Logger.d("onFragmentViewDestroyed,$f")
    }

    override fun onFragmentPreCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
        super.onFragmentPreCreated(fm, f, savedInstanceState)
        Logger.d("onFragmentPreCreated,$f")
    }

    override fun onFragmentActivityCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
        super.onFragmentActivityCreated(fm, f, savedInstanceState)
        Logger.d("onFragmentActivityCreated,$f")
    }

    override fun onFragmentPaused(fm: FragmentManager, f: Fragment) {
        super.onFragmentPaused(fm, f)
        Logger.d("onFragmentPaused,$f")
    }

    override fun onFragmentDetached(fm: FragmentManager, f: Fragment) {
        super.onFragmentDetached(fm, f)
    }
}