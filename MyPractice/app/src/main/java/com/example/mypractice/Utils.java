package com.example.mypractice;

import android.util.SparseArray;
import android.view.View;
import android.widget.Scroller;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by jintao on 2015/8/31.
 */
public class Utils {
    public static SparseArray<String> sparseArray=new SparseArray<String>();
    static {
        sparseArray.put( android.R.attr.state_active,"state_active");
        sparseArray.put( android.R.attr.state_checkable,"state_checkable");
        sparseArray.put( android.R.attr.state_checked,"state_checked");
        sparseArray.put( android.R.attr.state_enabled,"state_enabled");
        sparseArray.put( android.R.attr.state_focused,"state_focused");
        sparseArray.put( android.R.attr.state_long_pressable,"state_long_pressable");
        sparseArray.put( android.R.attr.state_window_focused,"state_window_focused");
        sparseArray.put( android.R.attr.state_selected,"state_selected");
        sparseArray.put( android.R.attr.state_hovered,"state_hovered");
        sparseArray.put( android.R.attr.state_pressed,"state_pressed");
    }
    public static String[]  toReadDrawableState(int[] states){
        List<String> temp=new ArrayList<String>();
        for(int i=0;i<states.length;i++){
            for(int j=0;j<sparseArray.size();j++){
                if(sparseArray.keyAt(j)==states[i]){
                    temp.add(sparseArray.valueAt(j));
                }
            }
        }
        Object[] d=temp.toArray();
        String[] s=new String[d.length];
        for(int i=0;i<d.length;i++){
            s[i]= (String) d[i];
        }
        return s;
    }
    public static String toReadScroller(Scroller scroller){
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append("scroller[")
                .append("currX-"+scroller.getCurrX()).append(" currY-"+scroller.getCurrY())
                .append(" finalX-"+scroller.getFinalX()).append(" finalY-"+scroller.getFinalY())
                .append(" startX-"+scroller.getStartX()).append(" startY-"+scroller.getStartY())
                .append("]");
        return stringBuilder.toString();
    }
    public static int getCenterXChildPosition(RecyclerView recyclerView) {
        int childCount = recyclerView.getChildCount();
        if (childCount > 0) {
            for (int i = 0; i < childCount; i++) {
                View child = recyclerView.getChildAt(i);
                if (isChildInCenterX(recyclerView, child)) {
                    return recyclerView.getChildPosition(child);
                }
            }
        }
        return childCount;
    }

    public static boolean isChildInCenterX(RecyclerView recyclerView, View view) {
        int childCount = recyclerView.getChildCount();
        int[] lvLocationOnScreen = new int[2];
        int[] vLocationOnScreen = new int[2];
        recyclerView.getLocationOnScreen(lvLocationOnScreen);
        int middleX = lvLocationOnScreen[0] + recyclerView.getWidth() / 2;
        if (childCount > 0) {
            view.getLocationOnScreen(vLocationOnScreen);
            if (vLocationOnScreen[0] <= middleX && vLocationOnScreen[0] + view.getWidth() >= middleX) {
                return true;
            }
        }
        return false;
    }
}
