package com.example.mypractice.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.OverScroller;

import com.example.mypractice.Logger;

/**
 * Created by jintao on 2015/9/7.
 */
public class ScrollViewGroup extends ViewGroup {

    public static final String TAG = "ScrollViewGroup";
    public static final int INVALIDATE_POINTER=-1;
    private OverScroller mScroller;
    private Context mConext;
    private int mTouchSlop;
    private VelocityTracker mVelocityTracker;

    private int mMinVelocityTrackerValue;
    private float mInitX;
    private float mInitY;
    private float mLastX;
    private float mLastY;
    private boolean mIsDraged = false;
    private int mActivePointer;
    public ScrollViewGroup(Context context) {
        super(context);
        mConext = context;
        init();
    }

    public ScrollViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        mConext = context;
        init();
    }

    public ScrollViewGroup(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mConext = context;
        init();

    }

    private void init() {
        mScroller = new OverScroller(mConext);
        mTouchSlop = ViewConfiguration.get(mConext).getScaledTouchSlop();
        setOverScrollMode(OVER_SCROLL_ALWAYS);
        final ViewConfiguration viewConfiguration=ViewConfiguration.get(mConext);
        mMinVelocityTrackerValue=viewConfiguration.getScaledMinimumFlingVelocity();
        mActivePointer=INVALIDATE_POINTER;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {


        if (ev.getAction() != MotionEvent.ACTION_DOWN && mIsDraged == true) {
            return true;
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Logger.d("onInterceptTouchEvent down " + ev.getX());
                ev.getActionIndex();
                mInitX = mLastX = ev.getX();
                mInitY = mLastY = ev.getY();
                mIsDraged = false;
                break;
            case MotionEvent.ACTION_MOVE:
                Logger.d("onInterceptTouchEvent move " + ev.getX());
                final float x = ev.getX();
                final float y = ev.getY();
                final float diffX = mLastX - x;
                final float absX = Math.abs(diffX);


                if (absX > mTouchSlop) {
                    mIsDraged = true;
                    mLastX = diffX > 0 ? mLastX + mTouchSlop : mLastX - mTouchSlop;
                    mLastY = y;
                }

                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (mIsDraged) {
                    gurarenteeVelocityTracker();
                    mIsDraged = false;
                }
                break;
        }


        return mIsDraged;
    }


    private void gurarenteeVelocityTracker() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }

    }

    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.clear();
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
    }

    /**
     * 此方法会触发onOverScrolled方法。同时会计算好相应的值
     *
     * @param deltaX scrollx的差值，也就是上一个事件位置，减去当前事件位置的值。
     * @param deltaY
     * @param scrollX 当前scrollx的值。
     * @param scrollY
     * @param scrollRangeX scroll x的范围。
     * @param scrollRangeY
     * @param maxOverScrollX scroll x范围外可以继续滑动的范围。
     * @param maxOverScrollY
     * @param isTouchEvent 是否是点击事件触发的还是scroller滑动触发的
     * @return
     */
    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        final int overScrollMode = getOverScrollMode();
        final boolean canScrollHorizontal =
                computeHorizontalScrollRange() > computeHorizontalScrollExtent();
        final boolean canScrollVertical =
                computeVerticalScrollRange() > computeVerticalScrollExtent();
        final boolean overScrollHorizontal = overScrollMode == OVER_SCROLL_ALWAYS ||
                (overScrollMode == OVER_SCROLL_IF_CONTENT_SCROLLS && canScrollHorizontal);
        final boolean overScrollVertical = overScrollMode == OVER_SCROLL_ALWAYS ||
                (overScrollMode == OVER_SCROLL_IF_CONTENT_SCROLLS && canScrollVertical);



        if(scrollX <0 || scrollX > scrollRangeX){

            

        }

        int newScrollX = scrollX + deltaX;
        if (!overScrollHorizontal) {
            maxOverScrollX = 0;
        }

        int newScrollY = scrollY + deltaY;
        if (!overScrollVertical) {
            maxOverScrollY = 0;
        }



        // Clamp values if at the limits and record
        final int left = -maxOverScrollX;
        final int right = maxOverScrollX + scrollRangeX;
        final int top = -maxOverScrollY;
        final int bottom = maxOverScrollY + scrollRangeY;

        boolean clampedX = false;
        if (newScrollX > right) {
            newScrollX = right;
            clampedX = true;
        } else if (newScrollX < left) {
            newScrollX = left;
            clampedX = true;
        }

        boolean clampedY = false;
        if (newScrollY > bottom) {
            newScrollY = bottom;
            clampedY = true;
        } else if (newScrollY < top) {
            newScrollY = top;
            clampedY = true;
        }
        Logger.d("newscrolly %d bottom %d top %d clampedY %b",newScrollY,bottom,top,clampedY);
        onOverScrolled(newScrollX, newScrollY, clampedX, clampedY);

        return clampedX || clampedY;
       // return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
    }


    /**
     * 此方法是overScrollBy时进行调用。
     * @param scrollX x轴滚动位置
     * @param scrollY y。。。。
     * @param clampedX x轴是否滚动到maxover的位置
     * @param clampedY
     */
    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        Logger.d("onOverScrolled scrolly " + scrollY + " clampedY "+clampedY);
        if(!mScroller.isFinished()){
            scrollTo(scrollX, 0);
        }else{
            scrollTo(scrollX, 0);
        }

    }


    @Override
    protected int computeHorizontalScrollRange() {
        final int widthWithoutPadding = getWidth() - getPaddingLeft() - getPaddingRight();
        if (getChildCount() == 0) {
            return 0;
        }
//        View view = getChildAt(0);
        return super.computeHorizontalScrollRange();
    }


    protected int getScrollXRange() {
        final int widthWithoutPadding = getWidth() - getPaddingLeft() - getPaddingRight();
        return widthWithoutPadding * Math.max(0, getChildCount() - 1);
    }


    /**
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gurarenteeVelocityTracker();
        mVelocityTracker.addMovement(event);
        switch (event.getAction()&MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_MOVE:
//                Logger.d("onTouchEvent ACTION_MOVE " + event.getX() +" scrollX "+getScrollX());

                int actionIndex=event.findPointerIndex(mActivePointer);

                final float x = event.getX(actionIndex);
                final float y = event.getY();
                final int diffX = (int) (mLastX - x);
                final int diffY = (int) (mLastY - y);
                final float absX = Math.abs(diffX);
                final int scrollX = getScrollX();
                final int scrollY = getScrollY();

                if(overScrollBy(diffX, 0, scrollX, 0, getScrollXRange(), 0, getMaxXScrollOver(), getMaxYScrollOver(), true)){
                    mVelocityTracker.clear();
                }
                onScrollChanged(getScrollX(), getScrollY(), scrollX, scrollY);
                mLastX = x;
                mLastY = y;
                mIsDraged = true;
                break;
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                actionIndex=event.getActionIndex();
                mActivePointer=event.getPointerId(actionIndex);
                Logger.d("onTouchEvent ACTION_DOWN " + event.getX());
                mInitX = mLastX = event.getX();
                mInitY = mLastY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mIsDraged) {
                    mIsDraged = false;
                    mVelocityTracker.computeCurrentVelocity(1000);
                    int xVelocity = (int) mVelocityTracker.getXVelocity();
                    Logger.d("MotionEvent.ACTION_UP xVelocity %d mscroll %d", xVelocity, getScrollX());
                    if(Math.abs(xVelocity)>mMinVelocityTrackerValue) {
                        Logger.d("mScroller fling ...startx "+getScrollX());
                        mScroller.fling(getScrollX(), 0, -xVelocity, 0, 0, getScrollXRange()+1, 0, 0, getMaxXScrollOver(), 0);
                        recycleVelocityTracker();
                    }else{
                        if(mScroller.springBack(getScrollX(),0,0, getScrollXRange(),0,0)){
                            Logger.d("MotionEvent.ACTION_UP springBack " + true);
                        }
                    }
                    invalidate();
                }
                break;

            /**
             * 主要处理多点触控那个问题。
             * ACTION_POINTER_DOWN，ACTION_POINTER_UP，当有第二个或者以上的手指按上屏幕时，会产生。
             * 上面两个事件，为了区分第二个或者第三个手指，增加MotionEvent.ACTION_MASK变量用来&event.getAction()
             * 多点触控的第二个或者以上getAcion&MASK的结果都是上面两个.
             * getActionIndex代表的是当前动作的第几个手指，也就是无论是第几个按下去的，只要是第一个抬起来的，那就是第几个
             * 但是为了区分，不同的手指，所以提供了getPointerId，传入actionIndex，得到一个pointerID
             *
             *
             *
             */
            case MotionEvent.ACTION_POINTER_DOWN:
                actionIndex=event.getActionIndex();
                mActivePointer=event.getPointerId(actionIndex);
                mLastX=event.getX();
                Logger.d("ACTION_POINTER_DOWN pinter index  %d  action %d pointerID %d" ,event.getActionIndex(),event.getAction(),event.getPointerId(event.getActionIndex()));
                break;
            case MotionEvent.ACTION_POINTER_UP:
                onOtherPointerUp(event);
                Logger.d("ACTION_POINTER_DOWN pinter index  %d  action %d pointerID %d" ,event.getActionIndex(),event.getAction(),event.getPointerId(event.getActionIndex()));
                break;
        }
        return true;
    }

    private void onOtherPointerUp(MotionEvent event) {
        final int actionIndex=event.getActionIndex();
        final int pointer=event.getPointerId(actionIndex);
        if(mActivePointer==pointer){
            mActivePointer=pointer==0?1:0;
            mLastX=event.getX(event.findPointerIndex(mActivePointer));
            if (mVelocityTracker != null) {
                mVelocityTracker.clear();
            }
        }
    }


    protected int handleMaxOverScrollX(){
        return 0;
    }


    protected int getMaxXScrollOver() {
        return 100;
    }

    protected int getMaxYScrollOver() {
        return 0;
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            final int currX = mScroller.getCurrX();
            final int currY = mScroller.getCurrY();
            final int scrollX = getScrollX();
            final int scrollY = getScrollY();
            if (currX != scrollX) {
                Logger.d("computeScroll currx %d scrollx %d",currX,scrollX);
                overScrollBy(currX-scrollX  , 0, scrollX, 0, getScrollXRange(), 0, getMaxXScrollOver(), getMaxYScrollOver(),false);
                onScrollChanged(getScrollX(), getScrollY(), scrollX, scrollY);
            }
            //此处需要调用刷新界面，currx和scrollX如果当前值如果相同的话，下次就会不同了，然后就可以往回拉了
            postInvalidate();
        }
    }



    public void smoothScrollTo(int x,int y){
        if(x!=getScrollX()||y!=getScrollY()) {
            if(!mScroller.isFinished()){
                mScroller.abortAnimation();;
            }
            mScroller.startScroll(getScrollX(), getScrollY(), x, y);
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int childWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        final int childHeight = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        View child;
        for (int i = 0, size = getChildCount(); i < size; i++) {
            child = getChildAt(i);
//            measureChildWithMargins();

            child.measure(MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY));
        }

//        setMeasuredDimension(widthMeasureSpec,heightMeasureSpec);

    }



    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            final int childCount = getChildCount();
            View child;
            final int width = getMeasuredWidth();
            for (int i = 0; i < childCount; i++) {
                child = getChildAt(i);
                child.layout(l + width * i, t, r + width * i, b);
            }
        }
    }
}
