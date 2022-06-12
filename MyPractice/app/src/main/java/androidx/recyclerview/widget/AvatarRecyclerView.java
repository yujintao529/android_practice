package androidx.recyclerview.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.os.TraceCompat;

import java.util.List;


/**
 * Created by yujintao.529 on 2022/6/11
 * NOTION：注意RecyclerView版本的变化对scrollStep的影响！！！理论上比较成熟。
 * 或者copy整套代码
 */
public class AvatarRecyclerView extends RecyclerView {
    private AvatarLayoutManager avatarLayoutManager;
    private AvatarAdapter avatarAdapter;

    public AvatarRecyclerView(@NonNull Context context) {
        super(context);
    }

    public AvatarRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AvatarRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setLayoutManager(@Nullable LayoutManager layout) {
        if (layout instanceof AvatarLayoutManager) {
            super.setLayoutManager(layout);
            avatarLayoutManager = (AvatarLayoutManager) layout;
            return;
        }
        throw new IllegalArgumentException("AvatarRecyclerView only support AvatarLayoutManager");
    }

    @Override
    public void setAdapter(@Nullable Adapter adapter) {
        if (adapter instanceof AvatarAdapter) {
            super.setAdapter(adapter);
            avatarAdapter = (AvatarAdapter) adapter;
            return;
        }
        throw new IllegalArgumentException("AvatarRecyclerView only support AvatarAdapter");
    }

    String exceptionLabel() {
        return super.exceptionLabel();
    }

    @Override
    void scrollStep(int dx, int dy, @Nullable int[] consumed) {
        Log.d("AvatarRecyclerView", "scrollStep dx=" + dx + " dy=" + dy);
        startInterceptRequestLayout();
        onEnterLayoutOrScroll();
        TraceCompat.beginSection(TRACE_SCROLL_TAG);
        fillRemainingScrollValues(mState);

        int consumedX = 0;
        int consumedY = 0;
        //同时需要口占layout的scrollHorizontallyBy方法
        Pair<Integer, Integer> integerPair = avatarLayoutManager.scrollHorAndVerBy(dx, dy, mRecycler, mState);
        consumedX = integerPair.first;
        consumedY = integerPair.second;

        TraceCompat.endSection();
        repositionShadowingViews();

        onExitLayoutOrScroll();
        stopInterceptRequestLayout(false);

        if (consumed != null) {
            consumed[0] = consumedX;
            consumed[1] = consumedY;
        }
    }

    void offsetChildrenHorAndVer(int dx, int dy) {
        final int childCount = mChildHelper.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = mChildHelper.getChildAt(i);
            view.offsetLeftAndRight(dx);
            view.offsetTopAndBottom(dy);
        }
    }

    @Override
    public boolean drawChild(Canvas canvas, View child, long drawingTime) {
        if (isVisible(child)) {
            return super.drawChild(canvas, child, drawingTime);
        } else {
            return false;
        }
    }

    void offsetChildrenHorAndVer(View child, int dx, int dy) {
        child.offsetLeftAndRight(dx);
        child.offsetTopAndBottom(dy);
    }

    protected void bindRealViewHolderIfNeed(View child, int position) {
        ViewHolder viewHolder = getChildViewHolder(child);
        if (viewHolder instanceof AvatarViewHolder) {
            AvatarViewHolder avatarViewHolder = (AvatarViewHolder) viewHolder;
            if (isVisible(child)) {
                if (!avatarViewHolder.realOnBindState) {
                    avatarAdapter.onRealBind(avatarViewHolder, position);
                    avatarViewHolder.realOnBindState = true;
                }
                avatarAdapter.onVisible(avatarViewHolder, position);
            } else {
                avatarAdapter.onHide(avatarViewHolder, position);
            }

        } else {
            throw new IllegalStateException("AvatarRecyclerView only support AvatarViewHolder");
        }

    }

    protected boolean isVisible(View child) {
        return child.getScaleY() > 0 && child.getScaleX() > 0;
    }

    public abstract static class AvatarViewHolder extends RecyclerView.ViewHolder {

        protected boolean realOnBindState;

        public AvatarViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        void resetInternal() {
            super.resetInternal();
            realOnBindState = false;
        }
    }

    public abstract static class AvatarAdapter<VH extends AvatarViewHolder> extends RecyclerView.Adapter<VH> {

        protected abstract void onRealBind(VH holder, int position);

        @Override
        public final void onBindViewHolder(@NonNull VH holder, int position, @NonNull List<Object> payloads) {
            super.onBindViewHolder(holder, position, payloads);
        }

        @Override
        public final void onBindViewHolder(@NonNull VH holder, int position) {
        }

        protected void onVisible(VH holder, int position) {

        }

        protected void onHide(VH holder, int position) {

        }

    }
}
