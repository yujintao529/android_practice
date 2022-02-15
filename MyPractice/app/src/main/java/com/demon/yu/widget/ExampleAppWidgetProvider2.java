package com.demon.yu.widget;

import com.example.mypractice.R;

public class ExampleAppWidgetProvider2 extends AbsExampleAppWidgetProvider {

    @Override
    protected String getTag() {
        return "ExampleAppWidgetProvider2";
    }


    @Override
    public int layout() {
        return R.layout.appwidget_example_layout_2;
    }
}
