package com.example.mypractice.v4;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mypractice.R;

import java.util.ArrayList;

import static android.support.v4.view.ViewPager.SCROLL_STATE_IDLE;
import static android.support.v4.view.ViewPager.SCROLL_STATE_SETTLING;

/**
 * Created by yujintao on 2017/3/15.
 * 简化版tablayout，为了支持我们特定的需求
 */

public class MfwTabLayout extends HorizontalScrollView {
    private static final String TAG = MfwTabLayout.class.getSimpleName();

    //模式
    public static final int MODE_MATCH = 0;
    public static final int MODE_WRAP = 1;
    //default value
    public static final int TAB_DEFAULT_COLOR = 0xff111111;
    public static final int TAB_DEFAULT_SELECT_COLOR = 0xffff8400;
    public static final int TAB_DEFAULT_TAB_MINWIDTH = 40;
    public static final int INDICATOR_DEFAULT_HEIGHT = 4;
    public static final int INDICATOR_DEFAULT_COLOR = 0xffff8400;

    //绑定viewpager需要的数据
    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    private DataSetObserver mInnerDataObeserver;

    //val
    private Tab mCurrentTab;


    //data
    private ArrayList<Tab> mTabs;
    private ArrayList<OnTabSelectedListener> mTabSelectedListeners;
    //view
    private InnerView mInnerView;

    //view attr
    private ColorStateList mTabColorStateList;
    private int mMode;//tab填充模式
    private int mMinTabWidth;//tab最小宽度,只有在model为WARP时起作用
    private int mTabStartMargin;//tab开始margin，距左边距的位置
    private int mTabEndMargin;//tab结束的margin,距右边距的位置,利用填充一个空白view实现
    private int mTabPaddingStart;
    private int mTabPaddingEnd;
    private int mTabPaddingTop;
    private int mTabPaddingBottom;

    private int mIndicatorHeight;
    private int mIndicatorColor;
    @StyleRes
    int mTabApparence;

    public MfwTabLayout(Context context) {
        this(context, null);
    }

    public MfwTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setHorizontalScrollBarEnabled(false);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.mfw_tab_layout);
        mMode = typedArray.getInt(R.styleable.mfw_tab_layout_mfwtab_mode, MODE_MATCH);
        int colorTabDfault = typedArray.getColor(R.styleable.mfw_tab_layout_mfwtab_tab_color, TAB_DEFAULT_COLOR);
        int colorTabSelectedDfault = typedArray.getColor(R.styleable.mfw_tab_layout_mfwtab_tab_color, TAB_DEFAULT_SELECT_COLOR);
        mTabColorStateList = createColorStateList(colorTabDfault, colorTabSelectedDfault);
        mMinTabWidth = typedArray.getDimensionPixelSize(R.styleable.mfw_tab_layout_mfwtab_tab_minwidth, TAB_DEFAULT_TAB_MINWIDTH);
        mIndicatorHeight = typedArray.getDimensionPixelSize(R.styleable.mfw_tab_layout_mfwtab_indicator_height, INDICATOR_DEFAULT_HEIGHT);
        mTabPaddingStart = typedArray.getDimensionPixelSize(R.styleable.mfw_tab_layout_mfwtab_tab_padding_start, 0);
        mTabPaddingEnd = typedArray.getDimensionPixelSize(R.styleable.mfw_tab_layout_mfwtab_tab_padding_end, 0);
        mTabPaddingTop = typedArray.getDimensionPixelSize(R.styleable.mfw_tab_layout_mfwtab_tab_padding_top, 0);
        mTabPaddingBottom = typedArray.getDimensionPixelSize(R.styleable.mfw_tab_layout_mfwtab_tab_padding_bottom, 0);
        mTabStartMargin = typedArray.getDimensionPixelSize(R.styleable.mfw_tab_layout_mfwtab_tab_start_margin, 0);
        mTabEndMargin = typedArray.getDimensionPixelSize(R.styleable.mfw_tab_layout_mfwtab_tab_end_margin, 0);
        mIndicatorColor = typedArray.getColor(R.styleable.mfw_tab_layout_mfwtab_indicator_color, INDICATOR_DEFAULT_COLOR);
        typedArray.recycle();
        mTabs = new ArrayList<>();
        mTabSelectedListeners = new ArrayList<>();
        setFillViewport(true);
        mInnerView = new InnerView(context);
        mInnerView.setPadding(mTabStartMargin,0,mTabEndMargin,0);
        addView(mInnerView, new HorizontalScrollView.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));

    }


    public void setupViewPager(final ViewPager viewPager) {
        if (viewPager == null) {
            throw new IllegalArgumentException("viewpager must not be null");
        }
        if (viewPager.getAdapter() == null) {
            throw new IllegalArgumentException("viewpager must has set adapter");
        }
        if (mPagerAdapter != null) {
            mPagerAdapter.unregisterDataSetObserver(mInnerDataObeserver);
        }
        mViewPager = viewPager;
        mPagerAdapter = mViewPager.getAdapter();
        if (mInnerDataObeserver == null) {
            mInnerDataObeserver = new InnerDataObeserver();
        }
        mPagerAdapter.registerDataSetObserver(mInnerDataObeserver);
        viewPager.addOnPageChangeListener(new InnerScrollLisner());
        addTabSelectListener(new OnTabSelectedListener() {
            @Override
            public void onTabSelected(Tab tab) {
                mViewPager.setCurrentItem(tab.position);
            }

            @Override
            public void onTabUnselected(Tab tab) {

            }
        });
        perfomViewPagerInject();

    }


    private void perfomViewPagerInject() {
        if (mPagerAdapter == null) {
            return;
        }
        mInnerView.removeAllViews();
        for (int index = 0, size = mPagerAdapter.getCount(); index < size; index++) {
            final Tab tab = newTab();
            tab.setTitle(mPagerAdapter.getPageTitle(index));
            addTabInterval(tab, index, mViewPager.getCurrentItem() == index);
        }
    }


    void addTab(Tab tab) {
        addTabInterval(tab, mTabs.size(), mTabs.isEmpty());
    }


    void selectTabPosition(int position) {
        if (mTabs.size() > position) {
            selectTab(mTabs.get(position), true);
        }
    }




    void scrollToPosition(int position, float positionOffset, boolean updateIndicator) {
        int scrollX = calculateScrollXForTab(position, positionOffset);
        scrollTo(scrollX, 0);
        if (updateIndicator) {
            mInnerView.setIndicatorPosition(position, positionOffset);
        }
    }

    private int calculateScrollXForTab(int position, float positionOffset) {
        if (mMode == MODE_WRAP) {
            final View selectedChild = mInnerView.getChildAt(position);
            final View nextChild = position + 1 < mInnerView.getChildCount()
                    ? mInnerView.getChildAt(position + 1)
                    : null;
            final int selectedWidth = selectedChild != null ? selectedChild.getWidth() : 0;
            final int nextWidth = nextChild != null ? nextChild.getWidth() : 0;

            return selectedChild.getLeft()
                    + ((int) ((selectedWidth + nextWidth) * positionOffset * 0.5f))
                    + (selectedChild.getWidth() / 2)
                    - (getWidth() / 2);
        }
        return 0;
    }

    public void addTabSelectListener(OnTabSelectedListener onTabSelectedListener) {
        if (!mTabSelectedListeners.contains(onTabSelectedListener)) {
            mTabSelectedListeners.add(onTabSelectedListener);
        }
    }



    void selectTab(Tab tab, boolean updateIndicator) {
        if (mCurrentTab == tab) {
            scrollToPosition(tab.position,0,false);
        } else {
            if (updateIndicator) {
                if (mCurrentTab == null) {
                    mInnerView.setIndicatorPosition(tab.position, 0);
                } else {
                    mInnerView.indicatorToPosition(tab.position);
                }
            }
        }
        trigerTabUnselect(mCurrentTab);
        mCurrentTab = tab;
        trigerTabSelect(mCurrentTab);
        performTabSelect(tab.position);
    }

    void trigerTabUnselect(Tab tab) {
        for (OnTabSelectedListener onTabSelectedListener : mTabSelectedListeners) {
            onTabSelectedListener.onTabUnselected(tab);
        }
    }
    void trigerTabSelect(Tab tab) {
        for (OnTabSelectedListener onTabSelectedListener : mTabSelectedListeners) {
            onTabSelectedListener.onTabSelected(tab);
        }
    }


    private void addTabInterval(Tab tab, int index, boolean isSelected) {
        mInnerView.addView(tab, index, createLayoutParam());
        mInnerView.setWeightSum(mTabs.size());
        mTabs.add(index, tab);
        resetTabPosition();
        if (isSelected) {
            performTabSelect(index);
            selectTab(tab, true);
        }
        tab.updateTab();
    }

    private void resetTabPosition() {
        for (int i = 0, size = mTabs.size(); i < size; i++) {
            mTabs.get(i).position = i;
        }
    }

    private void performTabSelect(int position) {
        int count = mInnerView.getChildCount();
        if (position < count && !mInnerView.getChildAt(position).isSelected()) {
            Log.d(TAG,"select position "+position);
            for (int index = 0; index < count; index++) {
                View view = mInnerView.getChildAt(index);
                view.setSelected(position == index);
            }
        }
    }


    private static ColorStateList createColorStateList(int defaultColor, int selectedColor) {
        final int[][] states = new int[2][];
        final int[] colors = new int[2];
        int i = 0;

        states[i] = SELECTED_STATE_SET;
        colors[i] = selectedColor;
        i++;

        states[i] = EMPTY_STATE_SET;
        colors[i] = defaultColor;
        i++;

        return new ColorStateList(states, colors);
    }

    private LinearLayout.LayoutParams createLayoutParam() {
        LinearLayout.LayoutParams layoutParams;
        if (mMode == MODE_MATCH) {
            layoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
        } else {
            layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, 0);
        }
        return layoutParams;
    }


    public Tab newTab() {
        Tab tab = new Tab(getContext());
        tab.tabLayout = this;
        return tab;
    }

    //tab parent
    private class InnerView extends LinearLayout {


        private int mLastPosition = -1;
        private float mPositionOffset = 0;//0-1

        private int indicatorLeft;
        private int indicatorRight;

        private Paint mIndicatorPaint;




        public InnerView(Context context) {
            this(context, null);
        }

        public InnerView(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
            setOrientation(LinearLayout.HORIZONTAL);
            setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            mIndicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
            mIndicatorPaint.setColor(mIndicatorColor);
            mIndicatorPaint.setStyle(Paint.Style.FILL);
            setWillNotDraw(false);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

        @Override
        public void draw(Canvas canvas) {
            super.draw(canvas);
            if (indicatorLeft >= 0 && indicatorRight > indicatorLeft) {
                canvas.drawRect(indicatorLeft, getHeight() - mIndicatorHeight, indicatorRight, getHeight(), mIndicatorPaint);
            }
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            updateIndicator();
        }

        private void updateIndicator() {
            final View currentView = getChildAt(mLastPosition);
            final View next = getChildAt(mLastPosition + 1);
            if (currentView == null) return;
            if (next != null) {
                int left = (int) (currentView.getLeft() + (next.getLeft() - currentView.getLeft()) * mPositionOffset);
                int right = (int) (currentView.getRight() + (next.getRight() - currentView.getRight()) * mPositionOffset);
                setIndicator(left, right);
            } else {
                setIndicator(currentView.getLeft(), currentView.getRight());
                mPositionOffset = 0;
            }
        }

        private void setIndicatorPosition(int position, float positionOffset) {
            mLastPosition = position;
            mPositionOffset = positionOffset;
            updateIndicator();
        }

        private void setIndicator(int left, int right) {
            if (indicatorLeft != left || indicatorRight != right) {
                indicatorLeft = left;
                indicatorRight = right;
                ViewCompat.postInvalidateOnAnimation(this);
            }
        }

        private void indicatorToPosition(int position) {
            if (position == mLastPosition) return;
            final View current = getChildAt(mLastPosition);
            final View next = getChildAt(position);
            if (current != null) {
                ValueAnimator valueAnimator = new ValueAnimator();
                valueAnimator.setFloatValues(0, 1);
                valueAnimator.setDuration(100);
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float fraction = animation.getAnimatedFraction();
                        int left = (int) ((next.getLeft() - current.getLeft()) * fraction + current.getLeft());
                        int right = (int) ((next.getRight() - current.getRight()) * fraction + current.getRight());
                        setIndicator(left, right);
                    }
                });
                mLastPosition = position;
                valueAnimator.start();
            } else {
                setIndicator(next.getLeft(), next.getRight());
                mLastPosition = position;
            }


        }

    }


    //tabView，title
    public static class Tab extends LinearLayout {

        private TextView textView;
        private int position;
        MfwTabLayout tabLayout;

        public Tab(Context context) {
            super(context);
            setOrientation(LinearLayout.VERTICAL);
            setGravity(Gravity.CENTER);
            textView = new TextView(context);
            textView.setSingleLine(true);
            textView.setGravity(Gravity.CENTER);
            addView(textView);
            setClickable(true);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        }

        private void updateTab() {
            textView.setTextColor(tabLayout.mTabColorStateList);
            textView.setMinWidth(tabLayout.mMinTabWidth);
            setPadding(tabLayout.mTabPaddingStart, tabLayout.mTabPaddingTop, tabLayout.mTabPaddingEnd, tabLayout.mTabPaddingBottom);
        }

        public void setTitle(CharSequence title) {
            textView.setText(title);
        }

        @Override
        public boolean performClick() {
            super.performClick();
            tabLayout.selectTab(this, true);
            return true;
        }

        @Override
        protected LayoutParams generateDefaultLayoutParams() {
            LinearLayout.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            return layoutParams;
        }

        @Override
        public void setSelected(boolean selected) {
            super.setSelected(selected);
            if (selected) {
                textView.setSelected(true);
            } else {
                textView.setSelected(false);
            }

        }

    }


    private class InnerScrollLisner implements ViewPager.OnPageChangeListener {
        private int scrollState;
        private int previousScrollState;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


            boolean updateIndicator = !(scrollState == SCROLL_STATE_SETTLING
                    && previousScrollState == SCROLL_STATE_IDLE);
            scrollToPosition(position, positionOffset, updateIndicator);
            Log.d(TAG, "onpageScrolled " + position + " " + positionOffset);
        }

        @Override
        public void onPageSelected(int position) {
            Log.d(TAG, "onPageSelected " + position);
            final boolean updateIndicator = scrollState == SCROLL_STATE_IDLE
                    || (scrollState == SCROLL_STATE_SETTLING
                    && previousScrollState == SCROLL_STATE_IDLE);
            selectTab(mTabs.get(position), updateIndicator);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            previousScrollState = scrollState;
            scrollState = state;
            Log.d(TAG, "onPageScrollStateChanged " + state);
        }
    }

    /**
     *
     */
    private class InnerDataObeserver extends DataSetObserver {
        public InnerDataObeserver() {
            super();
        }

        @Override
        public void onChanged() {
            super.onChanged();
            perfomViewPagerInject();

        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
            perfomViewPagerInject();
        }
    }


    public interface OnTabSelectedListener {

        void onTabSelected(Tab tab);

        void onTabUnselected(Tab tab);
    }


    private static class SpecialLinearLayout extends LinearLayout{

        public SpecialLinearLayout(Context context) {
            super(context);
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            super.onLayout(changed, l, t, r, b);
        }
    }
}
