package com.demon.yu.widget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.RemoteViews;

import com.demon.yu.utils.ProcessUtils;
import com.demon.yu.utils.TimeUtils;
import com.example.mypractice.Logger;
import com.example.mypractice.R;
import com.google.gson.Gson;

import java.util.Arrays;

public abstract class AbsExampleAppWidgetProvider extends AppWidgetProvider {
    private static final String TAG = "AbsExampleAppWidgetProvider";


    public AbsExampleAppWidgetProvider() {
        super();
    }

    private static int index = 1;

    protected int widgetID = -1;

    protected String getTag() {
        return TAG;
    }


    public abstract int layout();

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Logger.debug(getTag(), "onReceive " + intent.getAction() + " " + intent.getType() + " processInfo " + ProcessUtils.getProcessInfo(context));


    }

    private RemoteViews updateTextInfo(Context context, String info) {
        RemoteViews views = new RemoteViews(context.getPackageName(), layout());
        views.setTextViewText(R.id.date, "更新时间:" + TimeUtils.getCurrentTimeWithStyle());
        Gson gson = new Gson();
        BookModel bookModel = gson.fromJson(info, BookModel.class);
        views.setTextViewText(R.id.info, "更新内容:" + bookModel.toString());
        return views;
    }


    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        Logger.debug(getTag(), "onUpdate " + Arrays.toString(appWidgetIds));

//        HttpManager.INSTANCE.request(new Request("https://jsonplaceholder.typicode.com/posts/" + index), new HttpManager.CallBack() {
//            @Override
//            public void onResult(@NotNull String result) {
//                super.onResult(result);
//                Logger.debug(getTag(), "onResult " + result);
//                RemoteViews remoteViews = updateTextInfo(context, result);
//                appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
//            }
//        });
//        index++;
        final int N = appWidgetIds.length;
//
//        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i = 0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];
            AppWidgetProviderInfo appWidgetProviderInfo = appWidgetManager.getAppWidgetInfo(appWidgetId);
            Logger.d("appWidgetProviderInfo minWidth = " + appWidgetProviderInfo.minWidth
                    + " minHeight=" + appWidgetProviderInfo.minHeight);
//            // Create an Intent to launch ExampleActivity
//            Intent intent = new Intent(context, AppWidgetTargetAct.class);
//            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
//
//            // Get the layout for the App Widget and attach an on-click listener
//            // to the button
//            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget_example_layout);
//            views.setOnClickPendingIntent(R.id.imageView, pendingIntent);
//
//            // Tell the AppWidgetManager to perform an update on the current app widget
//            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        Logger.debug(getTag(), "onAppWidgetOptionsChanged " + appWidgetId);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        Logger.debug(getTag(), "onDeleted " + Arrays.toString(appWidgetIds));
    }

    /**
     * 首次安装后
     *
     * @param context
     */
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] ids = appWidgetManager.getAppWidgetIds(new ComponentName(context, getClass()));
        if (ids.length > 0) {
            widgetID = ids[0];
        }
        Logger.debug(getTag(), "onEnabled " + Arrays.toString(ids));
    }


    private void enableAlarmManager(Context context) {
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] ids = appWidgetManager.getAppWidgetIds(new ComponentName(context, AbsExampleAppWidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        int requestCode = 0;
        PendingIntent pendIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long triggerAtTime = SystemClock.elapsedRealtime() + 10 * 1000;
        int interval = 10 * 1000;
        alarmMgr.setRepeating(AlarmManager.RTC, triggerAtTime, interval, pendIntent);
    }


    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        widgetID = -1;
        Logger.debug(getTag(), "onDisabled ");
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int requestCode = 0;
        PendingIntent pendIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmMgr.cancel(pendIntent);
    }

    @Override
    public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
        super.onRestored(context, oldWidgetIds, newWidgetIds);
        Logger.debug(getTag(), "onRestored " + oldWidgetIds + " " + newWidgetIds);
    }
}
