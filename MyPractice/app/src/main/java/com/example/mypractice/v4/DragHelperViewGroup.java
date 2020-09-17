package com.example.mypractice.v4;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.core.view.MotionEventCompat;
import androidx.customview.widget.ViewDragHelper;

/**
 * Created by jintao on 2015/9/16.
 */
public class DragHelperViewGroup extends FrameLayout {


    private ViewDragHelper mViewDragHelper;


    public DragHelperViewGroup(Context context) {
        super(context);
        init();
    }

    public DragHelperViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DragHelperViewGroup(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getActionMasked();
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mViewDragHelper.cancel();
            return false;
        }
        return mViewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mViewDragHelper.processTouchEvent(event);
        return true;
    }

    private void init(){
        mViewDragHelper=ViewDragHelper.create(this,new MyCallback());
    }
    private class MyCallback extends ViewDragHelper.Callback{


        public MyCallback(){

        }
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return true;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return left;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            return top;
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
        }

        /**
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
            return super.onEdgeLock(edgeFlags);
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
            return super.getViewHorizontalDragRange(child);
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
            return super.getViewVerticalDragRange(child);
        }
    }
}
