package com.example.mypractice.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.Scroller;

import com.example.mypractice.Logger;
import com.example.mypractice.Utils;

/**
 * Created by jintao on 2015/9/2.
 */
public class ScrollerLinearLayout extends LinearLayout {
    Scroller mScroller;

    public ScrollerLinearLayout(Context context) {
        super(context);
        init(context);
    }

    public ScrollerLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ScrollerLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }
    private void init(Context context){
        mScroller=new Scroller(context);
    }

    public void smoothScrollTo(int fx, int fy) {
        int dx = fx - mScroller.getFinalX();
        int dy = fy - mScroller.getFinalY();
        smoothScrollBy(dx, dy);
    }

    public void smoothScrollBy(int dx, int dy) {

        mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx, dy);
        postInvalidate();
    }

    @Override
    public void computeScroll() {
        Logger.d("scroller computeScroll "+ Utils.toReadScroller(mScroller));
        if(mScroller.computeScrollOffset()){
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            requestLayout();

        }
    }
}
