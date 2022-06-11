package androidx.recyclerview.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.os.TraceCompat;

/**
 * Created by yujintao.529 on 2022/6/11
 * NOTION：注意RecyclerView版本的变化对scrollStep的影响！！！理论上比较成熟。
 * 或者copy整套代码
 */
public class AvatarRecyclerView extends RecyclerView {
    public AvatarRecyclerView(@NonNull Context context) {
        super(context);
    }

    public AvatarRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AvatarRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
        if (dx != 0) {
            consumedX = mLayout.scrollHorizontallyBy(dx, mRecycler, mState);
        }
        if (dy != 0) {
            consumedY = mLayout.scrollVerticallyBy(dy, mRecycler, mState);
        }

        TraceCompat.endSection();
        repositionShadowingViews();

        onExitLayoutOrScroll();
        stopInterceptRequestLayout(false);

        if (consumed != null) {
            consumed[0] = consumedX;
            consumed[1] = consumedY;
        }
    }
}
