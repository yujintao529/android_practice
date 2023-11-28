package com.demon.yu.memory

import android.app.ActivityManager
import android.app.ActivityManager.MemoryInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.os.Build
import android.os.Bundle
import android.os.Debug
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.ColorUtils
import com.bytedance.Test
import com.demon.yu.lib.gaussian.StudentNative
import com.demon.yu.utils.MemoryUnit
import com.example.mypractice.Logger
import com.example.mypractice.R

@RequiresApi(Build.VERSION_CODES.M)
class MemoryActivity : AppCompatActivity() {

    private var textInfo: TextView? = null

    //    private val arr = Array<Int>(1024 * 1024 * 50) { 0 } //200m？
    private val javaMemory = mutableListOf<JavaMemoryObj>()
    private val bitmapMemory = mutableListOf<BitmapMemoryObj>()
    private val memoryMap = mutableMapOf<String, Float>()
    private val nativeMemoryMap = mutableListOf<Long>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memory)
        textInfo = findViewById(R.id.memoryInfo)
        refreshMemoryInfo()

    }


    private fun refreshMemoryInfo() {
        memoryMap["Runtime.maxMemory"] =
            MemoryUnit.B.toMB(Runtime.getRuntime().maxMemory().toFloat())
        memoryMap["Runtime.freeMemory"] =
            MemoryUnit.B.toMB(Runtime.getRuntime().freeMemory().toFloat())
        memoryMap["Runtime.totalMemory"] =
            MemoryUnit.B.toMB(Runtime.getRuntime().totalMemory().toFloat())
        val am = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        memoryMap["am.largeMemoryClass"] = am.largeMemoryClass.toFloat()
        memoryMap["am.memoryClass"] = am.memoryClass.toFloat()
        val memoryInfo = MemoryInfo()
        am.getMemoryInfo(memoryInfo)
        memoryMap["am.memory.availMem"] = MemoryUnit.B.toMB(memoryInfo.availMem.toFloat())
        memoryMap["am.memory.totalMem"] = MemoryUnit.B.toMB(memoryInfo.totalMem.toFloat())
        memoryMap["am.memory.threshold"] = MemoryUnit.B.toMB(memoryInfo.threshold.toFloat())

        //频率上有限制。dumpsys meminfo com.example.mypractice total pss
        val debugMemoryInfo = am.getProcessMemoryInfo(intArrayOf(android.os.Process.myPid()))[0]
//        val debugMemoryInfo = Debug.MemoryInfo()
//        Debug.getMemoryInfo(debugMemoryInfo) //和上面的一样

        memoryMap["debug.memory.totalPss"] =
            MemoryUnit.KB.toMB(debugMemoryInfo.totalPss.toFloat())
        memoryMap["debug.memory.totalPrivateDirty"] =
            MemoryUnit.KB.toMB(debugMemoryInfo.totalPrivateDirty.toFloat())
        memoryMap["debug.memory.totalSharedDirty"] =
            MemoryUnit.KB.toMB(debugMemoryInfo.totalSharedDirty.toFloat())
        memoryMap["debug.memory.totalPrivateClean"] =
            MemoryUnit.KB.toMB(debugMemoryInfo.totalPrivateClean.toFloat())

        memoryMap["debug.memory.totalSharedClean"] =
            MemoryUnit.KB.toMB(debugMemoryInfo.totalSharedClean.toFloat())

        //dumpsys meminfo com.example.mypractice 中的dalvik pss
        memoryMap["debug.memory.dalvikPss"] =
            MemoryUnit.KB.toMB(debugMemoryInfo.dalvikPss.toFloat())
        memoryMap["debug.memory.dalvikPrivateDirty"] =
            MemoryUnit.KB.toMB(debugMemoryInfo.dalvikPrivateDirty.toFloat())
        memoryMap["debug.memory.dalvikSharedDirty"] =
            MemoryUnit.KB.toMB(debugMemoryInfo.dalvikSharedDirty.toFloat())

        //dumpsys meminfo com.example.mypractice 中的native pss
        memoryMap["debug.memory.nativePss"] =
            MemoryUnit.KB.toMB(debugMemoryInfo.nativePss.toFloat())
        memoryMap["debug.memory.nativePrivateDirty"] =
            MemoryUnit.KB.toMB(debugMemoryInfo.nativePrivateDirty.toFloat())
        memoryMap["debug.memory.nativeSharedDirty"] =
            MemoryUnit.KB.toMB(debugMemoryInfo.nativeSharedDirty.toFloat())


        val memoryState = debugMemoryInfo.memoryStats
        /**
         *  stats.put("summary.java-heap", Integer.toString(getSummaryJavaHeap()));
        stats.put("summary.native-heap", Integer.toString(getSummaryNativeHeap()));
        stats.put("summary.code", Integer.toString(getSummaryCode()));
        stats.put("summary.stack", Integer.toString(getSummaryStack()));
        stats.put("summary.graphics", Integer.toString(getSummaryGraphics()));
        stats.put("summary.private-other", Integer.toString(getSummaryPrivateOther()));
        stats.put("summary.system", Integer.toString(getSummarySystem()));
        stats.put("summary.total-pss", Integer.toString(getSummaryTotalPss()));
        stats.put("summary.total-swap", Integer.toString(getSummaryTotalSwap()));
         */
        memoryState.forEach {
            memoryMap["debug.${it.key}"] = MemoryUnit.KB.toMB(it.value.toFloatOrNull() ?: 0F)
        }


        memoryMap["debug.pss"] =
            MemoryUnit.KB.toMB(
                Debug.getPss().toFloat()
            ) //Retrieves the PSS memory used by the process as given by the smaps.


        memoryMap["debug.native.heap"] =
                //对应 dumpsys meminfo com.example.mypractice native help。但是没有Dalvik
            MemoryUnit.B.toMB(Debug.getNativeHeapSize().toFloat()) //
        memoryMap["debug.native.heap_allocate"] =
            MemoryUnit.B.toMB(Debug.getNativeHeapAllocatedSize().toFloat()) //
        memoryMap["debug.native.heap_free"] =
            MemoryUnit.B.toMB(Debug.getNativeHeapFreeSize().toFloat())


        refreshUi()
    }


    private fun refreshUi() {
        val builder = StringBuilder()
        memoryMap.forEach { entry ->
            if (entry.value > 1024f) {
                append(
                    builder,
                    entry.key,
                    MemoryUnit.MB.toGB(entry.value).toString().subString(5) + "GB"
                )
            } else if (entry.value < 1) {
                append(
                    builder,
                    entry.key,
                    MemoryUnit.MB.toKB(entry.value).toString().subString(5) + "KB"
                )
            } else {
                append(builder, entry.key, entry.value.toString().subString(5) + "MB")
            }

        }
        textInfo?.text = builder.toString()
    }


    private fun append(builder: StringBuilder, key: String, value: String) {
        builder.append(warpKey(key)).append(" : ").append(value).append("\n")
    }

    private fun warpKey(key: String, length: Int = 25): String {
        if (key.length < length) {
            return key.padEnd(length)
        }
        return key
    }


    private fun String.subString(length: Int): String {
        if (this.length > length) {
            return this.substring(0, length)
        }
        return this
    }

    fun onClick(v: View) {
        refreshMemoryInfo()
    }


    fun onAddJavaMemory(v: View) {
        javaMemory.add(JavaMemoryObj(size = 10))
        refreshMemoryInfo()
    }

    fun onAddNativeMemory(v: View) {
        val result = StudentNative.mallocMemory(10)
        Test.crash()
        refreshMemoryInfo()
//        val clazz = Class.forName("com.demon.yu.lib.gaussian.StudentNative", true, classLoader)
//        val method = clazz.getDeclaredMethod("mallocMemory", Int::class.java)
//        val result = method.invoke(null, 10)
    }

    fun onAddBitmapMemory(v: View) {
        bitmapMemory.add(BitmapMemoryObj(10))
        refreshMemoryInfo()
    }


    class JavaMemoryObj(private val size: Int = 1) {
        private val arr = Array<Int>(size * 256 * 1024) { 0 } //size MB
    }

    class BitmapMemoryObj(private val size: Int = 1) {
        private val bitmap = Bitmap.createBitmap(1, size * 256 * 1024, Bitmap.Config.ARGB_8888) //

        init {
            val canvas = Canvas(bitmap)
            canvas.drawColor(com.demon.yu.view.recyclerview.ColorUtils.getRandomColor())
            Logger.debug(
                "MemoryActivity",
                "add bitmap ${MemoryUnit.B.toMB(bitmap.allocationByteCount.toFloat())}MB"
            )
        }
    }

}