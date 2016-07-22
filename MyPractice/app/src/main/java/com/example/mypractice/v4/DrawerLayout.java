package com.example.mypractice.v4;

import android.content.Context;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.example.mypractice.Logger;

/**
 * Created by jintao on 2015/9/16.
 */
public class DrawerLayout extends ViewGroup{

    private static final String TAG=DrawerLayout.class.getSimpleName();

    private ViewDragHelper mViewDragHelper;

    private View  mLeft;
    private View mContent;

    public DrawerLayout(Context context) {
        super(context);
        init();
    }

    public DrawerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
    private void init(){
        mViewDragHelper=ViewDragHelper.create(this,1.0f,new MyCallBack());
        mViewDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);

    }
    public void openLeft(){
        mViewDragHelper.settleCapturedViewAt(mLeft.getWidth(),0);
    }
    public void closeLeft(){
        mViewDragHelper.settleCapturedViewAt(0,0);
    }
    @Override
    public void computeScroll() {
        Logger.d("computeScroll");
        if(mViewDragHelper.continueSettling(true)){
//            Logger.d("computeScroll");
            invalidate();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mViewDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        final int childCount = getChildCount();
        for(int i=0;i<childCount;i++) {
            measureChild(getChildAt(i),widthMeasureSpec,heightMeasureSpec);
        }

    }

    /**
     *
     *
     *
     * @param changed
     * @param l
     * @param t
     * @param r
     * @param b
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        final int childCount=getChildCount();
        for(int i=0;i<childCount;i++){
            if(i==1){
                mLeft=getChildAt(i);
                final int height=mLeft.getMeasuredHeight();
                final int width= mLeft.getMeasuredWidth();
                mLeft.layout(l-width+1,t,l+1,b);
//                mLeft.layout(l,t,l+width,b);
            }else if(i==0){
                mContent=getChildAt(i);
                mContent.layout(l,t,r,b);
            }
        }
    }

    private void ensureLayout(){

    }

    private class MyCallBack extends ViewDragHelper.Callback{

        /**
         * Called when the user's input indicates that they want to capture the given child view
         * with the pointer indicated by pointerId. The callback should return true if the user
         * is permitted to drag the given view with the indicated pointer.
         * <p/>
         * <p>ViewDragHelper may call this method multiple times for the same view even if
         * the view is already captured; this indicates that a new pointer is trying to take
         * control of the view.</p>
         * <p/>
         * <p>If this method returns true, a call to {@link #onViewCaptured(View, int)}
         * will follow if the capture is successful.</p>
         *
         * @param child     Child the user is attempting to capture
         * @param pointerId ID of the pointer attempting the capture
         * @return true if capture should be allowed, false otherwise
         */
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return mLeft==child;
        }

        public MyCallBack() {
            super();
        }

        /**
         * Called when the drag state changes. See the <code>STATE_*</code> constants
         * for more information.
         *
         * @param state The new drag state
         * @see # STATE_IDLE
         * @see # STATE_DRAGGING
         * @see # STATE_SETTLING
         */
        @Override
        public void onViewDragStateChanged(int state) {
            super.onViewDragStateChanged(state);
        }

        /**
         * Called when the captured view's position changes as the result of a drag or settle.
         *
         * @param changedView View whose position changed
         * @param left        New X coordinate of the left edge of the view
         * @param top         New Y coordinate of the top edge of the view
         * @param dx          Change in X position from the last call
         * @param dy          Change in Y position from the last call
         */
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            Logger.d("left %d dx %d",left,dx);
            if(mLeft==changedView){
//                invalidate();
            }
        }

        /**
         *
         *
         * Called when a child view is captured for dragging or settling. The ID of the pointer
         * currently dragging the captured view is supplied. If activePointerId is
         * identified as {@link # INVALID_POINTER} the capture is programmatic instead of
         * pointer-initiated.
         *
         * @param capturedChild   Child view that was captured
         * @param activePointerId Pointer id tracking the child capture
         */
        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            super.onViewCaptured(capturedChild, activePointerId);
        }

        /**
         * Called when the child view is no longer being actively dragged.
         * The fling velocity is also supplied, if relevant. The velocity values may
         * be clamped to system minimums or maximums.
         * <p/>
         * <p>Calling code may decide to fling or otherwise release the view to let it
         * settle into place. It should do so using {@link # settleCapturedViewAt(int, int)}
         * or {@link # flingCapturedView(int, int, int, int)}. If the Callback invokes
         * one of these methods, the ViewDragHelper will enter {@link # STATE_SETTLING}
         * and the view capture will not fully end until it comes to a complete stop.
         * If neither of these methods is invoked before <code>onViewReleased</code> returns,
         * the view will stop in place and the ViewDragHelper will return to
         * {@link # STATE_IDLE}.</p>
         *
         * @param releasedChild The captured child view now being released
         * @param xvel          X velocity of the pointer as it left the screen in pixels per second.
         * @param yvel          Y velocity of the pointer as it left the screen in pixels per second.
         */
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {

            super.onViewReleased(releasedChild, xvel, yvel);

            Logger.d("onViewReleased " + xvel);

            final float absVel=Math.abs(xvel);

            if(xvel<0&&absVel>mViewDragHelper.getMinVelocity()){
                mViewDragHelper.settleCapturedViewAt(-mLeft.getWidth(),0);
            }else if(xvel>0&&absVel>mViewDragHelper.getMinVelocity()){
                mViewDragHelper.settleCapturedViewAt(0,0);
            }else{
//                final int middlePosition=(mLeft.getRight()-mLeft.getLeft())/2;
                if(mLeft.getRight()>mLeft.getWidth  ()/2){
                    mViewDragHelper.settleCapturedViewAt(0,0);
                }else{
                    mViewDragHelper.settleCapturedViewAt(-mLeft.getWidth(),0);
                }
            }


//            mViewDragHelper.flingCapturedView(-getWidth(), 0, 0, 0);
            invalidate();
//            mViewDragHelper.
        }

        /**
         * Called when one of the subscribed edges in the parent view has been touched
         * by the user while no child view is currently captured.
         *
         * @param edgeFlags A combination of edge flags describing the edge(s) currently touched
         * @param pointerId ID of the pointer touching the described edge(s)
         * @see # EDGE_LEFT
         * @see # EDGE_TOP
         * @see # EDGE_RIGHT
         * @see # EDGE_BOTTOM
         */
        @Override
        public void onEdgeTouched(int edgeFlags, int pointerId) {
            Logger.d("onEdgeTouched " + edgeFlags);
            super.onEdgeTouched(edgeFlags, pointerId);

        }

        /**
         * Called when the given edge may become locked. This can happen if an edge drag
         * was preliminarily rejected before beginning, but after {@link #onEdgeTouched(int, int)}
         * was called. This method should return true to lock this edge or false to leave it
         * unlocked. The default behavior is to leave edges unlocked.
         *
         * @param edgeFlags A combination of edge flags describing the edge(s) locked
         * @return true to lock the edge, false to leave it unlocked
         */
        @Override
        public boolean onEdgeLock(int edgeFlags) {
            Logger.d("onEdgeLock " + edgeFlags);
            return true;
        }

        /**
         * Called when the user has started a deliberate drag away from one
         * of the subscribed edges in the parent view while no child view is currently captured.
         *
         * @param edgeFlags A combination of edge flags describing the edge(s) dragged
         * @param pointerId ID of the pointer touching the described edge(s)
         * @see # EDGE_LEFT
         * @see # EDGE_TOP
         * @see # EDGE_RIGHT
         * @see # EDGE_BOTTOM
         */
        @Override
        public void onEdgeDragStarted(int edgeFlags, int pointerId) {
            super.onEdgeDragStarted(edgeFlags, pointerId);
            mViewDragHelper.captureChildView(mLeft, pointerId);
            Logger.d("onEdgeDragStarted " + edgeFlags);
        }

        /**
         * Called to determine the Z-order of child views.
         *
         * @param index the ordered position to query for
         * @return index of the view that should be ordered at position <code>index</code>
         */
        @Override
        public int getOrderedChildIndex(int index) {
            return super.getOrderedChildIndex(index);
        }

        /**
         * Return the magnitude of a draggable child view's horizontal range of motion in pixels.
         * This method should return 0 for views that cannot move horizontally.
         *
         * @param child Child view to check
         * @return range of horizontal motion in pixels
         */
        @Override
        public int getViewHorizontalDragRange(View child) {
            Logger.d("getViewHorizontalDragRange " +mLeft.getWidth());
            return mLeft.getWidth();
        }

        /**
         * Return the magnitude of a draggable child view's vertical range of motion in pixels.
         * This method should return 0 for views that cannot move vertically.
         *
         * @param child Child view to check
         * @return range of vertical motion in pixels
         */
        @Override
        public int getViewVerticalDragRange(View child) {
            Logger.d("getViewVerticalDragRange "+ child);
//            return mLeft.getHeight()/4;
            return 0;
        }


        /**
         * Restrict the motion of the dragged child view along the horizontal axis.
         * The default implementation does not allow horizontal motion; the extending
         * class must override this method and provide the desired clamping.
         *
         * @param child Child view being dragged
         * @param left  Attempted motion along the X axis
         * @param dx    Proposed change in position for left
         * @return The new clamped position for left
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            Logger.d("clampViewPositionHorizontal " + child +" left "+left +" dx "+dx);

            return Math.min(left,0);
        }

        /**
         * Restrict the motion of the dragged child view along the vertical axis.
         * The default implementation does not allow vertical motion; the extending
         * class must override this method and provide the desired clamping.
         *
         * @param child Child view being dragged
         * @param top   Attempted motion along the Y axis
         * @param dy    Proposed change in position for top
         * @return The new clamped position for top
         */
        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            return top;
        }
    }
}
