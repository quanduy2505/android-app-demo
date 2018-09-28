package devlight.io.library.behavior;

import android.annotation.TargetApi;
import android.os.Parcelable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.CoordinatorLayout.Behavior;
import android.support.v4.view.WindowInsetsCompat;
import android.view.View;

abstract class VerticalScrollingBehavior<V extends View> extends Behavior<V> {
    private int mOverScrollDirection;
    private int mScrollDirection;
    private int mTotalDy;
    private int mTotalDyUnconsumed;

    protected abstract void onDirectionNestedPreScroll();

    protected abstract boolean onNestedDirectionFling();

    protected abstract void onNestedVerticalOverScroll();

    VerticalScrollingBehavior() {
        this.mTotalDyUnconsumed = 0;
        this.mTotalDy = 0;
        this.mOverScrollDirection = 0;
        this.mScrollDirection = 0;
    }

    public int getOverScrollDirection() {
        return this.mOverScrollDirection;
    }

    public int getScrollDirection() {
        return this.mScrollDirection;
    }

    @TargetApi(21)
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, V v, View directTargetChild, View target, int nestedScrollAxes) {
        return (nestedScrollAxes & 2) != 0;
    }

    public void onNestedScrollAccepted(CoordinatorLayout coordinatorLayout, V child, View directTargetChild, View target, int nestedScrollAxes) {
        super.onNestedScrollAccepted(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
    }

    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, V child, View target) {
        super.onStopNestedScroll(coordinatorLayout, child, target);
    }

    public void onNestedScroll(CoordinatorLayout coordinatorLayout, V child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        if (dyUnconsumed > 0 && this.mTotalDyUnconsumed < 0) {
            this.mTotalDyUnconsumed = 0;
            this.mOverScrollDirection = 1;
        } else if (dyUnconsumed < 0 && this.mTotalDyUnconsumed > 0) {
            this.mTotalDyUnconsumed = 0;
            this.mOverScrollDirection = -1;
        }
        this.mTotalDyUnconsumed += dyUnconsumed;
        onNestedVerticalOverScroll();
    }

    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, V child, View target, int dx, int dy, int[] consumed) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);
        if (dy > 0 && this.mTotalDy < 0) {
            this.mTotalDy = 0;
            this.mScrollDirection = 1;
        } else if (dy < 0 && this.mTotalDy > 0) {
            this.mTotalDy = 0;
            this.mScrollDirection = -1;
        }
        this.mTotalDy += dy;
        onDirectionNestedPreScroll();
    }

    public boolean onNestedFling(CoordinatorLayout coordinatorLayout, V child, View target, float velocityX, float velocityY, boolean consumed) {
        super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY, consumed);
        this.mScrollDirection = velocityY > 0.0f ? 1 : -1;
        return onNestedDirectionFling();
    }

    public boolean onNestedPreFling(CoordinatorLayout coordinatorLayout, V child, View target, float velocityX, float velocityY) {
        return super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY);
    }

    public WindowInsetsCompat onApplyWindowInsets(CoordinatorLayout coordinatorLayout, V child, WindowInsetsCompat insets) {
        return super.onApplyWindowInsets(coordinatorLayout, child, insets);
    }

    public Parcelable onSaveInstanceState(CoordinatorLayout parent, V child) {
        return super.onSaveInstanceState(parent, child);
    }
}
