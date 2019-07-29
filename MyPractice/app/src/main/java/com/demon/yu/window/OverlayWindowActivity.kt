package com.demon.yu.window

import android.app.AppOpsManager
import android.content.ComponentName
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.WindowManager
import android.widget.ArrayAdapter
import com.example.mypractice.Logger
import com.example.mypractice.R
import kotlinx.android.synthetic.main.activity_overlay_window.*

class OverlayWindowActivity : AppCompatActivity() {


    private val overlayWindow: OverlayWindow? by lazy {
        val windowManager = OverlayWindow.getInstance(baseContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager)
        windowManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_overlay_window)

        if (!checkOverlaysEnable(this)) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("请先开启悬浮窗权限")
            builder.setNegativeButton("取消", DialogInterface.OnClickListener { dialog, int ->
                finish()
            })
            builder.setPositiveButton("开启", DialogInterface.OnClickListener { dialog, int ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                    startActivity(intent)
                    finish()
                } else {
                    finish()
                }
            })
            builder.show()
            return
        }


        val list = initWindowTypeValues()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, list)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        show.setOnClickListener {
            var type = spinner.selectedItem
            if (type != null) {
                type = type.toString().substring(type.toString().lastIndexOf("_") + 1, type.toString().length)
            }
            Logger.d("selected $type")

            overlayWindow?.updateType(Integer.parseInt(type.toString()))
        }
        dismiss.setOnClickListener {
            overlayWindow?.hide()
        }
    }


    private fun initWindowTypeValues(): ArrayList<String> {
        val list = ArrayList<String>()
        list.add("FIRST_APPLICATION_WINDOW_1")
        list.add("TYPE_BASE_APPLICATION_1")
        list.add("TYPE_APPLICATION_2")
        list.add("TYPE_APPLICATION_STARTING_3")
        list.add("TYPE_DRAWN_APPLICATION_4")
        list.add("LAST_APPLICATION_WINDOW_99")
        list.add("FIRST_SUB_WINDOW_1000")
        list.add("TYPE_APPLICATION_PANEL_1000")
        list.add("TYPE_APPLICATION_MEDIA_1001")
        list.add("TYPE_APPLICATION_SUB_PANEL_1002")
        list.add("TYPE_APPLICATION_ATTACHED_DIALOG_1003")
        list.add("TYPE_APPLICATION_MEDIA_OVERLAY_1004")
        list.add("TYPE_APPLICATION_ABOVE_SUB_PANEL_1005")
        list.add("LAST_SUB_WINDOW_1999")
        list.add("FIRST_SYSTEM_WINDOW_2000")
        list.add("TYPE_STATUS_BAR_2000")
        list.add("TYPE_SEARCH_BAR_2001")
        list.add("TYPE_PHONE_2002")
        list.add("TYPE_SYSTEM_ALERT_2003")
        list.add("TYPE_KEYGUARD_2004")
        list.add("TYPE_TOAST_2005")
        list.add("TYPE_SYSTEM_OVERLAY_2006")
        list.add("TYPE_PRIORITY_PHONE_2007")
        list.add("TYPE_SYSTEM_DIALOG_2008")
        list.add("TYPE_KEYGUARD_DIALOG_2009")
        list.add("TYPE_SYSTEM_ERROR_2010")
        list.add("TYPE_INPUT_METHOD_2011")
        list.add("TYPE_INPUT_METHOD_DIALOG_2012")
        list.add("TYPE_WALLPAPER_2013")
        list.add("TYPE_STATUS_BAR_PANEL_2014")
        list.add("TYPE_SECURE_SYSTEM_OVERLAY_2015")
        list.add("TYPE_DRAG_2016")
        list.add("TYPE_STATUS_BAR_SUB_PANEL_2017")
        list.add("TYPE_POINTER_2018")
        list.add("TYPE_NAVIGATION_BAR_2019")
        list.add("TYPE_VOLUME_OVERLAY_2020")
        list.add("TYPE_BOOT_PROGRESS_2021")
        list.add("TYPE_INPUT_CONSUMER_2022")
        list.add("TYPE_DREAM_2023")
        list.add("TYPE_NAVIGATION_BAR_PANEL_2024")
        list.add("TYPE_DISPLAY_OVERLAY_2026")
        list.add("TYPE_MAGNIFICATION_OVERLAY_2027")
        list.add("TYPE_PRIVATE_PRESENTATION_2030")
        list.add("TYPE_VOICE_INTERACTION_2031")
        list.add("TYPE_ACCESSIBILITY_OVERLAY_2032")
        list.add("TYPE_VOICE_INTERACTION_STARTING_2033")
        list.add("TYPE_APPLICATION_OVERLAY_2038")
        return list
    }

    fun checkOverlaysEnable(context: Context): Boolean {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
//                                if (!status) {
//                    val appOpsMgr = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
//                    val mode = appOpsMgr.checkOpNoThrow("android:system_alert_window", android.os.Process.myUid(), context.packageName)
//                    status = status or (mode == AppOpsManager.MODE_ALLOWED || mode == AppOpsManager.MODE_IGNORED)
//                }
                return Settings.canDrawOverlays(context)
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> return Settings.canDrawOverlays(context)
            Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT -> return true
            else -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    try {
//                        //oppo和viov等手机查看不出来，所以直接默认没有权限
//                        if (MayaPermissionManager.needExtraAdaption()) {
//                            return false
//                        }
                        val manager = context.getSystemService(Context.APP_OPS_SERVICE) as? AppOpsManager
                        val clazz = AppOpsManager::class.java
                        val method = clazz.getDeclaredMethod("checkOp", Int::class.java, Int::class.java, String::class.java)
                        val result = AppOpsManager.MODE_ALLOWED == method.invoke(manager, 24, Binder.getCallingUid(), context.packageName)
                        Log.d("MayaPermissionManager", " overlay permission $result")
                        return result
//                        return false
                    } catch (e: java.lang.Exception) {
                    }
                }
                return false
            }
        }
    }
}