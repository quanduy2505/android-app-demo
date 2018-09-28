package devlight.io.library.behavior;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.os.Build.VERSION;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.CoordinatorLayout.Behavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar.SnackbarLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorUpdateListener;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.view.View;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.animation.Interpolator;
import devlight.io.library.ntb.NavigationTabBar;

public class NavigationTabBarBehavior extends VerticalScrollingBehavior<NavigationTabBar> {
    private static final int ANIMATION_DURATION = 300;
    private static final Interpolator INTERPOLATOR;
    private boolean mBehaviorTranslationEnabled;
    private boolean mFabBottomMarginInitialized;
    private float mFabDefaultBottomMargin;
    private float mFabTargetOffset;
    private FloatingActionButton mFloatingActionButton;
    private boolean mHidden;
    private int mSnackBarHeight;
    private SnackbarLayout mSnackBarLayout;
    private float mTargetOffset;
    private ViewPropertyAnimatorCompat mTranslationAnimator;
    private ObjectAnimator mTranslationObjectAnimator;

    /* renamed from: devlight.io.library.behavior.NavigationTabBarBehavior.2 */
    class C07862 implements AnimatorUpdateListener {
        final /* synthetic */ NavigationTabBar val$child;

        C07862(NavigationTabBar navigationTabBar) {
            this.val$child = navigationTabBar;
        }

        public void onAnimationUpdate(ValueAnimator animation) {
            if (NavigationTabBarBehavior.this.mSnackBarLayout != null && (NavigationTabBarBehavior.this.mSnackBarLayout.getLayoutParams() instanceof MarginLayoutParams)) {
                NavigationTabBarBehavior.this.mTargetOffset = this.val$child.getBarHeight() - this.val$child.getTranslationY();
                MarginLayoutParams p = (MarginLayoutParams) NavigationTabBarBehavior.this.mSnackBarLayout.getLayoutParams();
                p.setMargins(p.leftMargin, p.topMargin, p.rightMargin, (int) NavigationTabBarBehavior.this.mTargetOffset);
                NavigationTabBarBehavior.this.mSnackBarLayout.requestLayout();
            }
            if (NavigationTabBarBehavior.this.mFloatingActionButton != null && (NavigationTabBarBehavior.this.mFloatingActionButton.getLayoutParams() instanceof MarginLayoutParams)) {
                NavigationTabBarBehavior.this.mFabTargetOffset = NavigationTabBarBehavior.this.mFabDefaultBottomMargin - this.val$child.getTranslationY();
                p = (MarginLayoutParams) NavigationTabBarBehavior.this.mFloatingActionButton.getLayoutParams();
                p.setMargins(p.leftMargin, p.topMargin, p.rightMargin, (int) NavigationTabBarBehavior.this.mFabTargetOffset);
                NavigationTabBarBehavior.this.mFloatingActionButton.requestLayout();
            }
        }
    }

    /* renamed from: devlight.io.library.behavior.NavigationTabBarBehavior.3 */
    class C07873 implements OnLayoutChangeListener {
        final /* synthetic */ NavigationTabBar val$child;

        C07873(NavigationTabBar navigationTabBar) {
            this.val$child = navigationTabBar;
        }

        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            if (NavigationTabBarBehavior.this.mFloatingActionButton != null && (NavigationTabBarBehavior.this.mFloatingActionButton.getLayoutParams() instanceof MarginLayoutParams)) {
                NavigationTabBarBehavior.this.mFabTargetOffset = NavigationTabBarBehavior.this.mFabDefaultBottomMargin - this.val$child.getTranslationY();
                MarginLayoutParams p = (MarginLayoutParams) NavigationTabBarBehavior.this.mFloatingActionButton.getLayoutParams();
                p.setMargins(p.leftMargin, p.topMargin, p.rightMargin, (int) NavigationTabBarBehavior.this.mFabTargetOffset);
                NavigationTabBarBehavior.this.mFloatingActionButton.requestLayout();
            }
        }
    }

    /* renamed from: devlight.io.library.behavior.NavigationTabBarBehavior.1 */
    class C12331 implements ViewPropertyAnimatorUpdateListener {
        final /* synthetic */ NavigationTabBar val$child;

        C12331(NavigationTabBar navigationTabBar) {
            this.val$child = navigationTabBar;
        }

        public void onAnimationUpdate(View view) {
            if (NavigationTabBarBehavior.this.mSnackBarLayout != null && (NavigationTabBarBehavior.this.mSnackBarLayout.getLayoutParams() instanceof MarginLayoutParams)) {
                NavigationTabBarBehavior.this.mTargetOffset = this.val$child.getBarHeight() - view.getTranslationY();
                MarginLayoutParams p = (MarginLayoutParams) NavigationTabBarBehavior.this.mSnackBarLayout.getLayoutParams();
                p.setMargins(p.leftMargin, p.topMargin, p.rightMargin, (int) NavigationTabBarBehavior.this.mTargetOffset);
                NavigationTabBarBehavior.this.mSnackBarLayout.requestLayout();
            }
            if (NavigationTabBarBehavior.this.mFloatingActionButton != null && (NavigationTabBarBehavior.this.mFloatingActionButton.getLayoutParams() instanceof MarginLayoutParams)) {
                p = (MarginLayoutParams) NavigationTabBarBehavior.this.mFloatingActionButton.getLayoutParams();
                NavigationTabBarBehavior.this.mFabTargetOffset = NavigationTabBarBehavior.this.mFabDefaultBottomMargin - view.getTranslationY();
                p.setMargins(p.leftMargin, p.topMargin, p.rightMargin, (int) NavigationTabBarBehavior.this.mFabTargetOffset);
                NavigationTabBarBehavior.this.mFloatingActionButton.requestLayout();
            }
        }
    }

    public /* bridge */ /* synthetic */ int getOverScrollDirection() {
        return super.getOverScrollDirection();
    }

    public /* bridge */ /* synthetic */ int getScrollDirection() {
        return super.getScrollDirection();
    }

    static {
        INTERPOLATOR = new LinearOutSlowInInterpolator();
    }

    public NavigationTabBarBehavior(boolean behaviorTranslationEnabled) {
        this.mSnackBarHeight = -1;
        this.mTargetOffset = 0.0f;
        this.mFabTargetOffset = 0.0f;
        this.mFabDefaultBottomMargin = 0.0f;
        this.mBehaviorTranslationEnabled = true;
        this.mBehaviorTranslationEnabled = behaviorTranslationEnabled;
    }

    public boolean onLayoutChild(CoordinatorLayout parent, NavigationTabBar child, int layoutDirection) {
        return super.onLayoutChild(parent, child, layoutDirection);
    }

    public boolean onDependentViewChanged(CoordinatorLayout parent, NavigationTabBar child, View dependency) {
        return super.onDependentViewChanged(parent, child, dependency);
    }

    public void onDependentViewRemoved(CoordinatorLayout parent, NavigationTabBar child, View dependency) {
        super.onDependentViewRemoved(parent, child, dependency);
    }

    public boolean layoutDependsOn(CoordinatorLayout parent, NavigationTabBar child, View dependency) {
        updateSnackBar(child, dependency);
        updateFloatingActionButton(dependency);
        return super.layoutDependsOn(parent, child, dependency);
    }

    public void onNestedVerticalOverScroll() {
    }

    public void onDirectionNestedPreScroll() {
    }

    protected boolean onNestedDirectionFling() {
        return false;
    }

    public void onNestedScroll(CoordinatorLayout coordinatorLayout, NavigationTabBar child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        if (dyConsumed < 0) {
            handleDirection(child, -1);
        } else if (dyConsumed > 0) {
            handleDirection(child, 1);
        }
    }

    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, NavigationTabBar child, View directTargetChild, View target, int nestedScrollAxes) {
        return nestedScrollAxes == 2 || super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
    }

    private void handleDirection(NavigationTabBar child, int scrollDirection) {
        if (!this.mBehaviorTranslationEnabled) {
            return;
        }
        if (scrollDirection == -1 && this.mHidden) {
            this.mHidden = false;
            animateOffset(child, 0, false, true);
        } else if (scrollDirection == 1 && !this.mHidden) {
            this.mHidden = true;
            animateOffset(child, child.getHeight(), false, true);
        }
    }

    private void animateOffset(NavigationTabBar child, int offset, boolean forceAnimation, boolean withAnimation) {
        if (!this.mBehaviorTranslationEnabled && !forceAnimation) {
            return;
        }
        if (VERSION.SDK_INT < 19) {
            ensureOrCancelObjectAnimation(child, offset, withAnimation);
            this.mTranslationObjectAnimator.start();
            return;
        }
        ensureOrCancelAnimator(child, withAnimation);
        this.mTranslationAnimator.translationY((float) offset).start();
    }

    private void ensureOrCancelAnimator(NavigationTabBar child, boolean withAnimation) {
        long j = 300;
        if (this.mTranslationAnimator == null) {
            this.mTranslationAnimator = ViewCompat.animate(child);
            ViewPropertyAnimatorCompat viewPropertyAnimatorCompat = this.mTranslationAnimator;
            if (!withAnimation) {
                j = 0;
            }
            viewPropertyAnimatorCompat.setDuration(j);
            this.mTranslationAnimator.setUpdateListener(new C12331(child));
            this.mTranslationAnimator.setInterpolator(INTERPOLATOR);
            return;
        }
        viewPropertyAnimatorCompat = this.mTranslationAnimator;
        if (!withAnimation) {
            j = 0;
        }
        viewPropertyAnimatorCompat.setDuration(j);
        this.mTranslationAnimator.cancel();
    }

    private static ObjectAnimator objectAnimatorOfTranslationY(View target, int offset) {
        if (VERSION.SDK_INT >= 14) {
            return ObjectAnimator.ofFloat(target, View.TRANSLATION_Y, new float[]{(float) offset});
        }
        ObjectAnimator res = new ObjectAnimator();
        res.setTarget(target);
        res.setPropertyName("translationY");
        res.setFloatValues(new float[]{(float) offset});
        return res;
    }

    private void ensureOrCancelObjectAnimation(NavigationTabBar child, int offset, boolean withAnimation) {
        if (this.mTranslationObjectAnimator != null) {
            this.mTranslationObjectAnimator.cancel();
        }
        this.mTranslationObjectAnimator = objectAnimatorOfTranslationY(child, offset);
        this.mTranslationObjectAnimator.setDuration(withAnimation ? 300 : 0);
        this.mTranslationObjectAnimator.setInterpolator(INTERPOLATOR);
        this.mTranslationObjectAnimator.addUpdateListener(new C07862(child));
    }

    public static NavigationTabBarBehavior from(NavigationTabBar view) {
        LayoutParams params = view.getLayoutParams();
        if (params instanceof CoordinatorLayout.LayoutParams) {
            Behavior behavior = ((CoordinatorLayout.LayoutParams) params).getBehavior();
            if (behavior instanceof NavigationTabBarBehavior) {
                return (NavigationTabBarBehavior) behavior;
            }
            throw new IllegalArgumentException("The view is not associated with NavigationTabBarBehavior");
        }
        throw new IllegalArgumentException("The view is not a child of CoordinatorLayout");
    }

    public void setBehaviorTranslationEnabled(boolean behaviorTranslationEnabled) {
        this.mBehaviorTranslationEnabled = behaviorTranslationEnabled;
    }

    public void hideView(NavigationTabBar view, int offset, boolean withAnimation) {
        if (!this.mHidden) {
            this.mHidden = true;
            animateOffset(view, offset, true, withAnimation);
        }
    }

    public void resetOffset(NavigationTabBar view, boolean withAnimation) {
        if (this.mHidden) {
            this.mHidden = false;
            animateOffset(view, 0, true, withAnimation);
        }
    }

    private void updateSnackBar(NavigationTabBar child, View dependency) {
        if (dependency != null && (dependency instanceof SnackbarLayout)) {
            this.mSnackBarLayout = (SnackbarLayout) dependency;
            if (VERSION.SDK_INT >= 19) {
                this.mSnackBarLayout.addOnLayoutChangeListener(new C07873(child));
            }
            if (this.mSnackBarHeight == -1) {
                this.mSnackBarHeight = dependency.getHeight();
            }
            int targetMargin = (int) (child.getBarHeight() - child.getTranslationY());
            child.bringToFront();
            if (VERSION.SDK_INT >= 21) {
                dependency.setStateListAnimator(null);
                dependency.setElevation(0.0f);
            }
            if (dependency.getLayoutParams() instanceof MarginLayoutParams) {
                MarginLayoutParams p = (MarginLayoutParams) dependency.getLayoutParams();
                p.setMargins(p.leftMargin, p.topMargin, p.rightMargin, targetMargin);
                dependency.requestLayout();
            }
        }
    }

    private void updateFloatingActionButton(View dependency) {
        if (dependency != null && (dependency instanceof FloatingActionButton)) {
            this.mFloatingActionButton = (FloatingActionButton) dependency;
            if (!this.mFabBottomMarginInitialized && (dependency.getLayoutParams() instanceof MarginLayoutParams)) {
                this.mFabBottomMarginInitialized = true;
                this.mFabDefaultBottomMargin = (float) ((MarginLayoutParams) dependency.getLayoutParams()).bottomMargin;
            }
        }
    }
}
