package com.example.mypractice.v4;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mypractice.Logger;
import com.example.mypractice.R;
import com.example.mypractice.common.Common;

import java.util.ArrayList;

import static android.support.v4.view.ViewPager.SCROLL_STATE_IDLE;
import static android.support.v4.view.ViewPager.SCROLL_STATE_SETTLING;

/**
 * Created by yujintao on 2017/3/15.
 * 简化版tablayout，为了支持我们特定的需求
 */

public class MfwTabLayout extends HorizontalScrollView {
    private static final String TAG = MfwTabLayout.class.getSimpleName();

    public static final int INDICATOR_DURATION = 100;

    //模式
    public static final int MODE_MATCH = 0;
    public static final int MODE_WRAP = 1;
    //default value
    public static final int TAB_DEFAULT_COLOR = 0xff111111;
    public static final int TAB_DEFAULT_SELECT_COLOR = 0xffff8400;
    public static final int TAB_DEFAULT_TAB_MINWIDTH = 40;
    public static final int INDICATOR_DEFAULT_HEIGHT = 4;
    public static final int INDICATOR_DEFAULT_COLOR = 0xffff8400;

    public static final int MIN_TAB_MARGIN = 10;

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
    private ColorStateList mTabTextColorStateList;
    private int mMode;//tab填充模式
    private int mMinTabWidth;//tab最小宽度,只有在model为WARP时起作用
    private int mTabStartMargin;//tab开始margin，距左边距的位置
    private int mTabEndMargin;//tab结束的margin,距右边距的位置
    private int mTabPaddingStart;//tab padding left
    private int mTabPaddingEnd;//tab padding right
    private int mTabPaddingTop;//tab padding top
    private int mTabPaddingBottom;//tab padding bottom
    private int mTabMargin = 10;//只有在tab模式为WARP时起作用
    private int mIndicatorHeight;//跟随线的高度
    private int mIndicatorColor;//跟随线的颜色
    private Drawable mIndicatorDrawable;//跟随线的drawable,如果存在上面两个属性失效
    private int mTabTitleAndIconPadding;
    private float mTextSize;


    public MfwTabLayout(Context context) {
        this(context, null);
    }

    public MfwTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setHorizontalScrollBarEnabled(false);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.mfw_tab_layout, 0, R.style.MfwTabDefaultStyle);
        mMode = typedArray.getInt(R.styleable.mfw_tab_layout_mfwtab_mode, MODE_MATCH);
        int colorTabDefault = typedArray.getColor(R.styleable.mfw_tab_layout_mfwtab_tab_text_color, TAB_DEFAULT_COLOR);
        int colorTabSelectedDefault = typedArray.getColor(R.styleable.mfw_tab_layout_mfwtab_tab_text_select_color, TAB_DEFAULT_SELECT_COLOR);
        mTabTextColorStateList = createColorStateList(colorTabDefault, colorTabSelectedDefault);
        mTextSize = typedArray.getDimensionPixelSize(R.styleable.mfw_tab_layout_mfwtab_tab_text_size, 15);
        mMinTabWidth = typedArray.getDimensionPixelSize(R.styleable.mfw_tab_layout_mfwtab_tab_minwidth, TAB_DEFAULT_TAB_MINWIDTH);
        mIndicatorHeight = typedArray.getDimensionPixelSize(R.styleable.mfw_tab_layout_mfwtab_indicator_height, INDICATOR_DEFAULT_HEIGHT);
        mTabPaddingStart = typedArray.getDimensionPixelSize(R.styleable.mfw_tab_layout_mfwtab_tab_padding_start, 0);
        mTabPaddingEnd = typedArray.getDimensionPixelSize(R.styleable.mfw_tab_layout_mfwtab_tab_padding_end, 0);
        mTabPaddingTop = typedArray.getDimensionPixelSize(R.styleable.mfw_tab_layout_mfwtab_tab_padding_top, 0);
        mTabPaddingBottom = typedArray.getDimensionPixelSize(R.styleable.mfw_tab_layout_mfwtab_tab_padding_bottom, 0);
        mTabStartMargin = typedArray.getDimensionPixelSize(R.styleable.mfw_tab_layout_mfwtab_tab_start_margin, 0);
        mTabEndMargin = typedArray.getDimensionPixelSize(R.styleable.mfw_tab_layout_mfwtab_tab_end_margin, 0);
        mIndicatorColor = typedArray.getColor(R.styleable.mfw_tab_layout_mfwtab_indicator_color, INDICATOR_DEFAULT_COLOR);
        mTabMargin = typedArray.getDimensionPixelSize(R.styleable.mfw_tab_layout_mfwtab_tab_margin, mTabMargin);
        mIndicatorDrawable = typedArray.getDrawable(R.styleable.mfw_tab_layout_mfwtab_indicator_drawable);
        mTabTitleAndIconPadding = typedArray.getDimensionPixelSize(R.styleable.mfw_tab_layout_mfwtab_title_icon_padding, 0);
        typedArray.recycle();
        mTabs = new ArrayList<>();
        mTabSelectedListeners = new ArrayList<>();
        mInnerView = new InnerView(context);
        mInnerView.setPadding(mTabStartMargin, 0, mTabEndMargin, 0);
        addView(mInnerView, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));

    }

    /**
     * 直接关联viewpager
     *
     * @param viewPager
     */
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
            mInnerDataObeserver = new InnerDataObserver();
        }
        mPagerAdapter.registerDataSetObserver(mInnerDataObeserver);
        viewPager.addOnPageChangeListener(new InnerScrollListener());
        addTabSelectListener(new OnTabSelectedListener() {
            @Override
            public void onTabSelected(Tab tab) {
                mViewPager.setCurrentItem(tab.position);
            }

            @Override
            public void onTabUnselected(Tab tab) {

            }
        });
        performViewPagerInject();

    }

    /**
     * 获取当前选中的tab
     *
     * @return
     */
    public Tab getSelectedTab() {
        return mCurrentTab;
    }

    /**
     * @param titles
     * @return
     */
    public ArrayList<Tab> setupStringArray(String[] titles) {
        clear();
        mInnerView.removeAllViews();
        for (int i = 0, size = titles.length; i < size; i++) {
            final Tab tab = newTab();
            tab.setTitle(titles[i]);
            addTabInterval(tab, i, i == 0);
        }
        return mTabs;
    }

    /**
     * @param index
     */
    public void setTabSelected(int index) {
        performTabSelect(index);
        selectTab(mTabs.get(index), true);
    }


    /**
     * 解析viewpager，添加tab
     */
    private void performViewPagerInject() {
        if (mPagerAdapter == null) {
            return;
        }
        mTabs.clear();
        mInnerView.removeAllViews();
        for (int index = 0, size = mPagerAdapter.getCount(); index < size; index++) {
            final Tab tab = newTab();
            tab.setTitle(mPagerAdapter.getPageTitle(index));
            addTabInterval(tab, index, mViewPager.getCurrentItem() == index);
        }
    }


    /**
     * 设置tab mode
     *
     * @param mode
     */
    public void setTabMode(int mode) {
        if (mMode != mode) {
            mMode = mode;
            performViewPagerInject();
            requestLayout();
        }
    }

    /**
     * 获取当前tabmode
     *
     * @return
     */
    public int getTabMode() {
        return mMode;
    }


    /**
     * 添加手动添加Tab
     *
     * @param tab
     */
    public void addTab(Tab tab) {
        addTab(tab, mTabs.isEmpty());
    }

    public void addTab(Tab tab, boolean isSelected) {
        addTabInterval(tab, mTabs.size(), isSelected);
    }


    /**
     * 删除某个tab
     *
     * @param index
     */
    public void removeIndex(int index) {
        removeTab(mTabs.get(index));
    }

    /**
     * 选中某个tab
     *
     * @param position
     */
    public void selectTabPosition(int position) {
        if (mTabs.size() > position) {
            selectTab(mTabs.get(position), true);
        }
    }


    void scrollToPosition(int position, float positionOffset, boolean updateIndicator) {
        int scrollX = calculateScrollXForTab(position, positionOffset);
        Logger.debug(TAG,"scrollToPosition  "+position+" positionOffset "+positionOffset+" scrollX "+scrollX);
        if(mScrollAnimator!=null&&mScrollAnimator.isRunning()){
            mScrollAnimator.cancel();
        }
        scrollTo(scrollX, 0);
        if (updateIndicator) {
            mInnerView.setIndicatorPosition(position, positionOffset);
        }
    }

    /**
     * 计算跟随线位置
     *
     * @param position
     * @param positionOffset
     * @return
     */
    private int calculateScrollXForTab(int position, float positionOffset) {
        if (mMode == MODE_WRAP) {
            final View selectedChild = mInnerView.getChildAt(position);
            final View nextChild = position + 1 < mInnerView.getChildCount()
                    ? mInnerView.getChildAt(position + 1)
                    : null;
            final int selectedWidth = selectedChild != null ? selectedChild.getWidth() : 0;
            final int nextWidth = nextChild != null ? nextChild.getWidth() : 0;
            return selectedChild.getLeft()
                    + (int) (((selectedWidth + nextWidth) * 0.5f + mTabMargin) * positionOffset)
                    + (selectedChild.getWidth() / 2)
                    - (getWidth() / 2);
        }
        return 0;
    }

    /**
     * 添加onTabSelectedListener
     *
     * @param onTabSelectedListener
     */
    public void addTabSelectListener(OnTabSelectedListener onTabSelectedListener) {
        if (!mTabSelectedListeners.contains(onTabSelectedListener)) {
            mTabSelectedListeners.add(onTabSelectedListener);
        }
    }

    /**
     * 删除onTabSelectedListener
     *
     * @param onTabSelectedListener
     */
    public void removeTabSelectListener(OnTabSelectedListener onTabSelectedListener) {
        if (mTabSelectedListeners.contains(onTabSelectedListener)) {
            mTabSelectedListeners.remove(onTabSelectedListener);
        }
    }


    /**
     * 清楚数据状态
     */
    private void clear() {
        mInnerView.removeAllViews();
        mTabs.clear();
        mCurrentTab = null;
    }


    /**
     * @param tab
     */
    public void removeTab(Tab tab) {
        Logger.debug(TAG, "remove tab " + tab);
        mTabs.remove(tab);
        mInnerView.removeView(tab);
        resetTabPosition();
        if (tab == mCurrentTab && mTabs.size() > 0) {
            mCurrentTab = null;
            selectTab(mTabs.get(0), true);
        }
        requestLayout();
    }


    /**
     * 选中tab，更新indicator
     *
     * @param tab
     * @param updateIndicator
     */
    private void selectTab(Tab tab, boolean updateIndicator) {
        Logger.debug(TAG, " select tab " + tab+ " mcurrentTab "+mCurrentTab);
        if (mCurrentTab == tab) {
            scrollToPosition(tab.position, 0, false);
        } else {
            if (updateIndicator) {
                if (mCurrentTab == null) {
                    mInnerView.setIndicatorPosition(tab.position, 0);
                } else {
                    mInnerView.indicatorToPosition(tab.position);
                }
                animateToTab(tab.position);
            }
        }
        if (mCurrentTab != null) {
            triggerTabUnselect(mCurrentTab);
        }
        mCurrentTab = tab;
        triggerTabSelect(mCurrentTab);
        performTabSelect(tab.position);
    }



    //滑动的animator
    private ValueAnimator mScrollAnimator;


    /**
     * 滚动到position的位置
     * @param position
     */
    public void animateToTab(int position) {
        if (getWindowToken() == null || !ViewCompat.isLaidOut(this)) {
            scrollToPosition(position, 0f, true);
            return;
        }
        final int startScrollX = getScrollX();
        final int targetScrollX = calculateScrollXForTab(position, 0);
        Logger.debug(TAG,"animateToTab startScrollX "+startScrollX+" targetScrollX "+targetScrollX);
        if (startScrollX != targetScrollX) {
            if (mScrollAnimator == null) {
                mScrollAnimator = new ValueAnimator();
                mScrollAnimator.setInterpolator(new FastOutLinearInInterpolator());
                mScrollAnimator.setDuration(INDICATOR_DURATION);
                mScrollAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        scrollTo((Integer) animation.getAnimatedValue(), 0);
                        Logger.debug(TAG,"onAnimationUpdate "+animation.getAnimatedValue());
                    }
                });
            }
            mScrollAnimator.setIntValues(startScrollX, targetScrollX);
            mScrollAnimator.start();
        }
        mInnerView.indicatorToPosition(position);
    }

    /**
     * 触发tabselect的监听器
     *
     * @param tab
     */
    void triggerTabUnselect(Tab tab) {
        for (OnTabSelectedListener onTabSelectedListener : mTabSelectedListeners) {
            onTabSelectedListener.onTabUnselected(tab);
        }
    }


    /**
     * 触发tabselect的监听器
     *
     * @param tab
     */
    private void triggerTabSelect(Tab tab) {
        for (OnTabSelectedListener onTabSelectedListener : mTabSelectedListeners) {
            onTabSelectedListener.onTabSelected(tab);
        }
    }

    /**
     * 内部添加tab的方法
     * @param tab
     * @param index
     * @param isSelected
     */
    private void addTabInterval(Tab tab, int index, boolean isSelected) {
        tab.tabLayout = this;
        mInnerView.addView(tab, index, createLayoutParam());
        mTabs.add(index, tab);
        resetTabPosition();
        if (isSelected) {
            performTabSelect(index);
            selectTab(tab, true);
        }
        tab.updateTab();
        requestLayout();
    }

    /**
     * 充值tab的position
     */
    private void resetTabPosition() {
        for (int i = 0, size = mTabs.size(); i < size; i++) {
            mTabs.get(i).position = i;
        }
    }


    /**
     * 完成tab的select效果
     *
     * @param position
     */
    private void performTabSelect(int position) {
        int count = mInnerView.getChildCount();
        if (position < count && !mInnerView.getChildAt(position).isSelected()) {
            for (int index = 0; index < count; index++) {
                View view = mInnerView.getChildAt(index);
                view.setSelected(position == index);
            }
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if (widthMode == MeasureSpec.UNSPECIFIED) {
            return;
        }
        if (getChildCount() == 0) {
            return;
        }
        Logger.debug(TAG, "measure width " + MeasureSpec.getSize(widthMeasureSpec) + " state " + getMeasureMode(widthMeasureSpec));
        Logger.debug(TAG, "tablayout 1 size " + getMeasuredWidth() + " state " + getMeasureMode(widthMeasureSpec));
        final ViewGroup viewGroup = (ViewGroup) getChildAt(0);
        final int paddingSum = viewGroup.getPaddingLeft() + viewGroup.getPaddingRight();//这个值也就是mTabStartMargin和mTabEndMargin
        final int childCount = viewGroup.getChildCount();
        if (mMode == MODE_MATCH) {
            //match模式下，无论state是atMost还是exactly，直接用使用最大值
            final int mostWidth = MeasureSpec.getSize(widthMeasureSpec);
            //MATCH 模式下，如果添加的tab过多导致tab无法显示完整怎么处理？
            //目前的策略是增加滑动，同时使用mTabMargin,作为间距....所以尽量别出现这个情况好吗？？直接使用wrap
            final int contentWidth = mostWidth - paddingSum;
            int sumWidth = 0;
            for (int i = 0; i < childCount; i++) {
                final View innerView = viewGroup.getChildAt(i);
                sumWidth += innerView.getMeasuredWidth();
            }
            if (sumWidth + MIN_TAB_MARGIN * childCount > contentWidth) {//如果所有子view的大小加上最小间距大小已经超过了match大小的话，直接采用WARP模式
                Logger.debug(TAG, "tab width is small ,so change mode to wrap");
                setTabMode(MODE_WRAP);
                for (int i = 0; i < childCount; i++) {
                    View inner = viewGroup.getChildAt(i);
                    if (i != childCount - 1) {
                        MarginLayoutParams marginLayoutParams = (MarginLayoutParams) inner.getLayoutParams();
                        marginLayoutParams.setMargins(0, 0, mTabMargin, 0);
                    }
                }
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                return;
            }
            int spaceWidth = contentWidth - sumWidth;
            int margin = Math.round(spaceWidth * 1f / childCount / 2);
            if (margin > 0) {
                for (int i = 0; i < childCount; i++) {
                    View inner = viewGroup.getChildAt(i);
                    updateInnerMargin((MarginLayoutParams) inner.getLayoutParams(), margin);
                }
            }
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else if (mMode == MODE_WRAP) {
            for (int i = 0; i < childCount; i++) {
                View inner = viewGroup.getChildAt(i);
                if (i != childCount - 1) {
                    MarginLayoutParams marginLayoutParams = (MarginLayoutParams) inner.getLayoutParams();
                    marginLayoutParams.setMargins(0, 0, mTabMargin, 0);
                }
            }
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    private static void updateInnerMargin(MarginLayoutParams layoutParams, int margin) {
        layoutParams.setMargins(margin, 0, margin, 0);
    }


    private static final String getMeasureMode(int measureSpec) {
        String mode = "";
        switch (MeasureSpec.getMode(measureSpec)) {
            case MeasureSpec.AT_MOST:
                mode = "at_most";
                break;
            case MeasureSpec.EXACTLY:
                mode = "exactly";
                break;
            case MeasureSpec.UNSPECIFIED:
                mode = "unspecified";
                break;
        }
        return mode;
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

    /**
     * 目前返回相同的layoutParam，后面方便扩展
     *
     * @return
     */
    private LinearLayout.LayoutParams createLayoutParam() {
        LinearLayout.LayoutParams layoutParams;
        if (mMode == MODE_MATCH) {
            layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, 0);
        } else {
            layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, 0);
        }
        return layoutParams;
    }


    public Tab newTab() {
        Tab tab = new Tab(getContext());
        return tab;
    }

    //tab parent
    private class InnerView extends LinearLayout {

        private int mLastPosition = -1;
        private float mPositionOffset = 0;//0-1
        private int indicatorLeft;
        private int indicatorRight;
        private Paint mIndicatorPaint;
        Path path;
        private RectF leftRound;
        private RectF rightRound;

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
            path = new Path();
            path.setFillType(Path.FillType.WINDING);
            leftRound = new RectF();
            rightRound = new RectF();
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }


        @Override
        public void draw(Canvas canvas) {
            super.draw(canvas);
            if (indicatorLeft >= 0 && indicatorRight > indicatorLeft) {
                if (mIndicatorDrawable != null) {
                    mIndicatorDrawable.setBounds(indicatorLeft, getHeight() - mIndicatorDrawable.getIntrinsicHeight(), indicatorRight, getHeight());
                    mIndicatorDrawable.draw(canvas);
                } else {
                    final int height = getHeight();
                    //下面的画线很坑，没办法...
                    path.reset();
                    path.moveTo(indicatorLeft, height);
                    leftRound.set(indicatorLeft, height - mIndicatorHeight, indicatorLeft + mIndicatorHeight * 2, height + mIndicatorHeight);
                    path.arcTo(leftRound, 180, 90);
                    path.lineTo(indicatorRight - mIndicatorHeight, height - mIndicatorHeight);
                    rightRound.set(indicatorRight - mIndicatorHeight * 2, height - mIndicatorHeight, indicatorRight, height + mIndicatorHeight);
                    path.arcTo(rightRound, 270, 90);
                    path.lineTo(indicatorLeft, height);
                    canvas.drawPath(path, mIndicatorPaint);
                }
            }
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            super.onLayout(changed, l, t, r, b);
            updateIndicator();
            Logger.d(TAG, "");
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
                valueAnimator.setDuration(INDICATOR_DURATION);
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


    /**
     * tab用来展现tablayout title的view，默认支持图标和文字，可以自定义view
     */
    public static class Tab extends LinearLayout {

        private TextView textView;
        private int position;
        MfwTabLayout tabLayout;
        private Drawable iconDrawable;
        private ImageView icon;
        private View customView;//自定义view

        private Tab(Context context) {
            super(context);
            setOrientation(LinearLayout.HORIZONTAL);
            setGravity(Gravity.CENTER);
            textView = new TextView(context);
            textView.setSingleLine(true);
            textView.setGravity(Gravity.CENTER);
            addView(textView);
            icon = new ImageView(context);
            icon.setScaleType(ImageView.ScaleType.CENTER_CROP);
            addView(icon);
            setClickable(true);
        }

        private void updateTab() {
            if (customView != null) {
                textView.setVisibility(View.GONE);
                icon.setVisibility(View.GONE);
                addView(customView);
            } else {
                textView.setTextColor(tabLayout.mTabTextColorStateList);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabLayout.mTextSize);
                textView.setMinWidth(tabLayout.mMinTabWidth);
                if (iconDrawable != null) {
                    icon.setVisibility(View.VISIBLE);
                    icon.setImageDrawable(iconDrawable);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    lp.leftMargin = tabLayout.mTabTitleAndIconPadding;
                } else {
                    icon.setVisibility(View.GONE);
                }
            }


            setPadding(tabLayout.mTabPaddingStart, tabLayout.mTabPaddingTop, tabLayout.mTabPaddingEnd, tabLayout.mTabPaddingBottom);
            requestLayout();
        }


        /**
         * 设置展现title
         *
         * @param title
         */
        public Tab setTitle(CharSequence title) {
            textView.setText(title);
            return this;
        }

        /**
         * 设置titleIcon,必须在添加tab前设定
         *
         * @param drawableID icon的drawable
         */
        public Tab setTitleIcon(@DrawableRes int drawableID) {
            iconDrawable = ContextCompat.getDrawable(getContext(), drawableID);
            return this;
        }

        /**
         * 必须在添加tab前设定
         *
         * @param view
         */
        public Tab setCustomView(View view) {
            customView = view;
            return this;
        }

        public int getPosition() {
            return position;
        }

        @Override
        public final boolean performClick() {
            super.performClick();
            tabLayout.selectTab(this, true);
            return true;
        }

        @Override
        protected LayoutParams generateDefaultLayoutParams() {
            return new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        @Override
        public final void setSelected(boolean selected) {
            super.setSelected(selected);
            textView.setSelected(selected);
            icon.setSelected(selected);
            if (customView != null) {
                customView.setSelected(selected);
            }
        }

        @Override
        public String toString() {
            return "Tab{" +
                    "position=" + position +
                    '}';
        }
    }

    /**
     * 关联viewpager滚动使用的
     */
    private class InnerScrollListener implements ViewPager.OnPageChangeListener {
        private int scrollState;
        private int previousScrollState;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


            boolean updateIndicator = !(scrollState == SCROLL_STATE_SETTLING
                    && previousScrollState == SCROLL_STATE_IDLE);
            scrollToPosition(position, positionOffset, updateIndicator);
            Logger.debug(TAG,"onPageScrolled "+position+" "+positionOffset);
        }

        @Override
        public void onPageSelected(int position) {
            final boolean updateIndicator = scrollState == SCROLL_STATE_IDLE
                    || (scrollState == SCROLL_STATE_SETTLING
                    && previousScrollState == SCROLL_STATE_IDLE);
            Logger.debug(TAG,"onPageSelected "+position);
            if(mCurrentTab!=null&&mCurrentTab.position==position){
                return;
            }
            selectTab(mTabs.get(position), updateIndicator);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            previousScrollState = scrollState;
            scrollState = state;
        }
    }

    /**
     * 内部使用的关联viewpagger的情况
     */
    private class InnerDataObserver extends DataSetObserver {
        public InnerDataObserver() {
            super();
        }

        @Override
        public void onChanged() {
            super.onChanged();
            performViewPagerInject();

        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
            performViewPagerInject();
        }
    }


    /**
     * tab的监听器
     */
    public interface OnTabSelectedListener {

        @Deprecated
        void onTabSelected(Tab tab);


        @Deprecated
        void onTabUnselected(Tab tab);
    }


}
