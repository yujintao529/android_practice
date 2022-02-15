package com.demon.yu.widget;


import android.appwidget.AppWidgetManager;
import android.content.Context;

import com.example.mypractice.R;

public class ExampleAppWidgetProvider extends AbsExampleAppWidgetProvider {


    @Override
    protected String getTag() {
        return "ExampleAppWidgetProvider";
    }

    @Override
    public int layout() {
        return R.layout.appwidget_example_layout;
    }


    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

}
