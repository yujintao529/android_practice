package com.example.mypractice.v7;

import android.content.Context;
import android.util.AttributeSet;

import com.example.mypractice.Logger;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by jintao on 2015/9/1.
 */
public class RecyclerViewPager extends RecyclerView {

    public RecyclerViewPager(Context context) {
        super(context);
        init(context);
    }

    public RecyclerViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RecyclerViewPager(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context){
        setOnScrollListener(new DefaultScrollListener());
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
    }

    public class DefaultScrollListener extends OnScrollListener{
        public DefaultScrollListener() {
            super();
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            switch (newState){
                case RecyclerView.SCROLL_STATE_DRAGGING:
                    Logger.d("recycler view onScrollStateChanged SCROLL_STATE_DRAGGING");
                    break;
                case RecyclerView.SCROLL_STATE_IDLE:
                    Logger.d("recycler view onScrollStateChanged SCROLL_STATE_IDLE");
//                    smoothScrollToPosition(new Random().nextInt(3));
                    break;
                case RecyclerView.SCROLL_STATE_SETTLING:
                    Logger.d("recycler view onScrollStateChanged SCROLL_STATE_SETTLING");
                    break;
            }

        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            Logger.d("recycler view onScrolled "+dx+" "+dy);
//            View v=null;
//            ViewPropertyAnimator.animate(v).scaleX(2);
//            v.startAnimation();
            //ViewPropertyAnimator.animate(view);
        }
    }
}
