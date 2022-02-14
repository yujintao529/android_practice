package com.demon.yu.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.*
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.RemoteViews
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.demon.yu.utils.ProcessUtils
import com.example.mypractice.Logger
import com.example.mypractice.R
import kotlinx.android.synthetic.main.activity_widget_target.*

private val ACTION_CREATE_APPWIDGET = "CreateAppWidget"

class AppWidgetTargetAct : AppCompatActivity() {

    private val createAppWidgetReceiver = AppWidgetBroadcastReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_widget_target)

        val intentFilter = IntentFilter()
        intentFilter.addAction(ACTION_CREATE_APPWIDGET)
        registerReceiver(createAppWidgetReceiver, intentFilter)
        Logger.debug("AppWidgetTargetAct", "currentProcess + " + ProcessUtils.getProcessInfo(this))
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(createAppWidgetReceiver)
    }

    fun onRefresh(view: View) {
        val ids = AppWidgetManager.getInstance(this).getAppWidgetIds(ComponentName(this, ExampleAppWidgetProvider::class.java))
        val ids2 = AppWidgetManager.getInstance(this).getAppWidgetIds(ComponentName(this, ExampleAppWidgetProvider2::class.java))
        val size = ids?.size ?: 0
        val size2 = ids2?.size ?: 0
        info.text = "${size + size2}个widget"
    }


    fun onModifyWidget(view: View) {
        val appWidgetManager = AppWidgetManager.getInstance(this)
        val ids = appWidgetManager.getAppWidgetIds(ComponentName(this, ExampleAppWidgetProvider::class.java))
        val views = RemoteViews(packageName, R.layout.appwidget_example_layout)
        views.setImageViewResource(R.id.imageView, R.drawable.lol_mangseng)
        appWidgetManager.updateAppWidget(ids, views)
    }


    fun updateWidget1(view: View) {
        val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
        val appWidgetManager = AppWidgetManager.getInstance(this)
        val ids = appWidgetManager.getAppWidgetIds(ComponentName(this, ExampleAppWidgetProvider::class.java))
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        sendBroadcast(intent)
    }

    fun updateWidget2(view: View) {
        val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
        val appWidgetManager = AppWidgetManager.getInstance(this)
        val ids = appWidgetManager.getAppWidgetIds(ComponentName(this, ExampleAppWidgetProvider2::class.java))
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        sendBroadcast(intent)
    }

    fun onAddWidget(view: View) {
        val appWidgetManager = AppWidgetManager.getInstance(this)
        val ids = appWidgetManager.getAppWidgetIds(ComponentName(this, ExampleAppWidgetProvider::class.java))
        if (ids.isNotEmpty()) {
            Toast.makeText(this, "已经添加了", Toast.LENGTH_LONG).show()
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (appWidgetManager.isRequestPinAppWidgetSupported) {
                val intent = Intent().apply {
                    intent.action = ACTION_CREATE_APPWIDGET
                }
                val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                appWidgetManager.requestPinAppWidget(ComponentName(this, ExampleAppWidgetProvider::class.java), null, pendingIntent)
                return
            }
        }
        Toast.makeText(this, "无法安装", Toast.LENGTH_LONG).show()
    }

    class AppWidgetBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            Logger.d("AppWidgetBroadcastReceiver", " call fun onReceive action: [${intent.action}], flags: [${intent.flags}]")
            Toast.makeText(context, "Create Success ID : [${
                intent.getIntExtra(
                        AppWidgetManager.EXTRA_APPWIDGET_ID,
                        AppWidgetManager.INVALID_APPWIDGET_ID
                )
            }]", Toast.LENGTH_LONG).show()
        }

    }
}

