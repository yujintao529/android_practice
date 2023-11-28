package com.demon.yu.cpu

import android.system.Os
import android.system.OsConstants

/**
 * @description
 * @author yujinta.529
 * @create 2023-09-08
 */
object CpuManager {

    fun getClkTck() = Os.sysconf(OsConstants. _SC_CLK_TCK)
}