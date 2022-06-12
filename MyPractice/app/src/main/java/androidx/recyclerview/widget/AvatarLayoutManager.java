package androidx.recyclerview.widget;


import android.util.Pair;
import android.view.View;

/**
 * Created by yujintao.529 on 2022/6/11
 */
abstract public class AvatarLayoutManager extends RecyclerView.LayoutManager {

    protected AvatarRecyclerView avatarRecyclerView;

    @Override
    public void setRecyclerView(RecyclerView recyclerView) {
        if (recyclerView instanceof AvatarRecyclerView) {
            super.setRecyclerView(recyclerView);
            avatarRecyclerView = (AvatarRecyclerView) recyclerView;
            return;
        }
        throw new IllegalArgumentException("AvatarLayoutManager only support AvatarRecyclerView");
    }

    protected void offsetChildrenHorAndVer(int dx, int dy) {
        if (avatarRecyclerView != null) {
            avatarRecyclerView.offsetChildrenHorAndVer(dx, dy);
        }
    }

    protected void offsetChildHorAndVer(View child, int dx, int dy) {
        if (avatarRecyclerView != null) {
            avatarRecyclerView.offsetChildrenHorAndVer(child, dx, dy);
        }
    }

    public Pair<Integer, Integer> scrollHorAndVerBy(int dx, int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        return new Pair<>(0, 0);
    }


    protected void bindRealViewHolderIfNeed(View child, int position) {
        avatarRecyclerView.bindRealViewHolderIfNeed(child, position);
    }

    @Override
    public final boolean canScrollHorizontally() {
        return true;
    }

    @Override
    public boolean canScrollVertically() {
        return true;
    }
}
