package android.support.design.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.annotation.VisibleForTesting;
import android.support.design.C0023R;
import android.support.design.widget.CoordinatorLayout.DefaultBehavior;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;
import android.support.v4.view.AbsSavedState;
import android.support.v4.view.OnApplyWindowInsetsListener;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.WindowInsetsCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

@DefaultBehavior(Behavior.class)
public class AppBarLayout extends LinearLayout {
    private static final int INVALID_SCROLL_RANGE = -1;
    static final int PENDING_ACTION_ANIMATE_ENABLED = 4;
    static final int PENDING_ACTION_COLLAPSED = 2;
    static final int PENDING_ACTION_EXPANDED = 1;
    static final int PENDING_ACTION_NONE = 0;
    private boolean mCollapsed;
    private boolean mCollapsible;
    private int mDownPreScrollRange;
    private int mDownScrollRange;
    private boolean mHaveChildWithInterpolator;
    private WindowInsetsCompat mLastInsets;
    private List<OnOffsetChangedListener> mListeners;
    private int mPendingAction;
    private final int[] mTmpStatesArray;
    private int mTotalScrollRange;

    public static class LayoutParams extends android.widget.LinearLayout.LayoutParams {
        static final int COLLAPSIBLE_FLAGS = 10;
        static final int FLAG_QUICK_RETURN = 5;
        static final int FLAG_SNAP = 17;
        public static final int SCROLL_FLAG_ENTER_ALWAYS = 4;
        public static final int SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED = 8;
        public static final int SCROLL_FLAG_EXIT_UNTIL_COLLAPSED = 2;
        public static final int SCROLL_FLAG_SCROLL = 1;
        public static final int SCROLL_FLAG_SNAP = 16;
        int mScrollFlags;
        Interpolator mScrollInterpolator;

        @RestrictTo({Scope.LIBRARY_GROUP})
        @Retention(RetentionPolicy.SOURCE)
        public @interface ScrollFlags {
        }

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            this.mScrollFlags = SCROLL_FLAG_SCROLL;
            TypedArray a = c.obtainStyledAttributes(attrs, C0023R.styleable.AppBarLayout_Layout);
            this.mScrollFlags = a.getInt(C0023R.styleable.AppBarLayout_Layout_layout_scrollFlags, 0);
            if (a.hasValue(C0023R.styleable.AppBarLayout_Layout_layout_scrollInterpolator)) {
                this.mScrollInterpolator = AnimationUtils.loadInterpolator(c, a.getResourceId(C0023R.styleable.AppBarLayout_Layout_layout_scrollInterpolator, 0));
            }
            a.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
            this.mScrollFlags = SCROLL_FLAG_SCROLL;
        }

        public LayoutParams(int width, int height, float weight) {
            super(width, height, weight);
            this.mScrollFlags = SCROLL_FLAG_SCROLL;
        }

        public LayoutParams(android.view.ViewGroup.LayoutParams p) {
            super(p);
            this.mScrollFlags = SCROLL_FLAG_SCROLL;
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
            this.mScrollFlags = SCROLL_FLAG_SCROLL;
        }

        @TargetApi(19)
        @RequiresApi(19)
        public LayoutParams(android.widget.LinearLayout.LayoutParams source) {
            super(source);
            this.mScrollFlags = SCROLL_FLAG_SCROLL;
        }

        @TargetApi(19)
        @RequiresApi(19)
        public LayoutParams(LayoutParams source) {
            super(source);
            this.mScrollFlags = SCROLL_FLAG_SCROLL;
            this.mScrollFlags = source.mScrollFlags;
            this.mScrollInterpolator = source.mScrollInterpolator;
        }

        public void setScrollFlags(int flags) {
            this.mScrollFlags = flags;
        }

        public int getScrollFlags() {
            return this.mScrollFlags;
        }

        public void setScrollInterpolator(Interpolator interpolator) {
            this.mScrollInterpolator = interpolator;
        }

        public Interpolator getScrollInterpolator() {
            return this.mScrollInterpolator;
        }

        boolean isCollapsible() {
            return (this.mScrollFlags & SCROLL_FLAG_SCROLL) == SCROLL_FLAG_SCROLL && (this.mScrollFlags & COLLAPSIBLE_FLAGS) != 0;
        }
    }

    public interface OnOffsetChangedListener {
        void onOffsetChanged(AppBarLayout appBarLayout, int i);
    }

    /* renamed from: android.support.design.widget.AppBarLayout.1 */
    class C08311 implements OnApplyWindowInsetsListener {
        C08311() {
        }

        public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
            return AppBarLayout.this.onWindowInsetChanged(insets);
        }
    }

    public static class Behavior extends HeaderBehavior<AppBarLayout> {
        private static final int INVALID_POSITION = -1;
        private static final int MAX_OFFSET_ANIMATION_DURATION = 600;
        private WeakReference<View> mLastNestedScrollingChildRef;
        private ValueAnimatorCompat mOffsetAnimator;
        private int mOffsetDelta;
        private int mOffsetToChildIndexOnLayout;
        private boolean mOffsetToChildIndexOnLayoutIsMinHeight;
        private float mOffsetToChildIndexOnLayoutPerc;
        private DragCallback mOnDragCallback;
        private boolean mSkipNestedPreScroll;
        private boolean mWasNestedFlung;

        public static abstract class DragCallback {
            public abstract boolean canDrag(@NonNull AppBarLayout appBarLayout);
        }

        /* renamed from: android.support.design.widget.AppBarLayout.Behavior.1 */
        class C08321 implements AnimatorUpdateListener {
            final /* synthetic */ AppBarLayout val$child;
            final /* synthetic */ CoordinatorLayout val$coordinatorLayout;

            C08321(CoordinatorLayout coordinatorLayout, AppBarLayout appBarLayout) {
                this.val$coordinatorLayout = coordinatorLayout;
                this.val$child = appBarLayout;
            }

            public void onAnimationUpdate(ValueAnimatorCompat animator) {
                Behavior.this.setHeaderTopBottomOffset(this.val$coordinatorLayout, this.val$child, animator.getAnimatedIntValue());
            }
        }

        protected static class SavedState extends AbsSavedState {
            public static final Creator<SavedState> CREATOR;
            boolean firstVisibleChildAtMinimumHeight;
            int firstVisibleChildIndex;
            float firstVisibleChildPercentageShown;

            /* renamed from: android.support.design.widget.AppBarLayout.Behavior.SavedState.1 */
            static class C08331 implements ParcelableCompatCreatorCallbacks<SavedState> {
                C08331() {
                }

                public SavedState createFromParcel(Parcel source, ClassLoader loader) {
                    return new SavedState(source, loader);
                }

                public SavedState[] newArray(int size) {
                    return new SavedState[size];
                }
            }

            public SavedState(Parcel source, ClassLoader loader) {
                super(source, loader);
                this.firstVisibleChildIndex = source.readInt();
                this.firstVisibleChildPercentageShown = source.readFloat();
                this.firstVisibleChildAtMinimumHeight = source.readByte() != null;
            }

            public SavedState(Parcelable superState) {
                super(superState);
            }

            public void writeToParcel(Parcel dest, int flags) {
                super.writeToParcel(dest, flags);
                dest.writeInt(this.firstVisibleChildIndex);
                dest.writeFloat(this.firstVisibleChildPercentageShown);
                dest.writeByte((byte) (this.firstVisibleChildAtMinimumHeight ? AppBarLayout.PENDING_ACTION_EXPANDED : 0));
            }

            static {
                CREATOR = ParcelableCompat.newCreator(new C08331());
            }
        }

        public /* bridge */ /* synthetic */ int getLeftAndRightOffset() {
            return super.getLeftAndRightOffset();
        }

        public /* bridge */ /* synthetic */ int getTopAndBottomOffset() {
            return super.getTopAndBottomOffset();
        }

        public /* bridge */ /* synthetic */ boolean setLeftAndRightOffset(int i) {
            return super.setLeftAndRightOffset(i);
        }

        public /* bridge */ /* synthetic */ boolean setTopAndBottomOffset(int i) {
            return super.setTopAndBottomOffset(i);
        }

        public Behavior() {
            this.mOffsetToChildIndexOnLayout = INVALID_POSITION;
        }

        public Behavior(Context context, AttributeSet attrs) {
            super(context, attrs);
            this.mOffsetToChildIndexOnLayout = INVALID_POSITION;
        }

        public boolean onStartNestedScroll(CoordinatorLayout parent, AppBarLayout child, View directTargetChild, View target, int nestedScrollAxes) {
            boolean started = (nestedScrollAxes & AppBarLayout.PENDING_ACTION_COLLAPSED) != 0 && child.hasScrollableChildren() && parent.getHeight() - directTargetChild.getHeight() <= child.getHeight();
            if (started && this.mOffsetAnimator != null) {
                this.mOffsetAnimator.cancel();
            }
            this.mLastNestedScrollingChildRef = null;
            return started;
        }

        public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, int dx, int dy, int[] consumed) {
            if (dy != 0 && !this.mSkipNestedPreScroll) {
                int min;
                int max;
                if (dy < 0) {
                    min = -child.getTotalScrollRange();
                    max = min + child.getDownNestedPreScrollRange();
                } else {
                    min = -child.getUpNestedPreScrollRange();
                    max = 0;
                }
                consumed[AppBarLayout.PENDING_ACTION_EXPANDED] = scroll(coordinatorLayout, child, dy, min, max);
            }
        }

        public void onNestedScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
            if (dyUnconsumed < 0) {
                scroll(coordinatorLayout, child, dyUnconsumed, -child.getDownNestedScrollRange(), 0);
                this.mSkipNestedPreScroll = true;
                return;
            }
            this.mSkipNestedPreScroll = false;
        }

        public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, AppBarLayout abl, View target) {
            if (!this.mWasNestedFlung) {
                snapToChildIfNeeded(coordinatorLayout, abl);
            }
            this.mSkipNestedPreScroll = false;
            this.mWasNestedFlung = false;
            this.mLastNestedScrollingChildRef = new WeakReference(target);
        }

        public boolean onNestedFling(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, float velocityX, float velocityY, boolean consumed) {
            boolean flung = false;
            if (!consumed) {
                flung = fling(coordinatorLayout, child, -child.getTotalScrollRange(), 0, -velocityY);
            } else if (velocityY < 0.0f) {
                targetScroll = (-child.getTotalScrollRange()) + child.getDownNestedPreScrollRange();
                if (getTopBottomOffsetForScrollingSibling() < targetScroll) {
                    animateOffsetTo(coordinatorLayout, child, targetScroll, velocityY);
                    flung = true;
                }
            } else {
                targetScroll = -child.getUpNestedPreScrollRange();
                if (getTopBottomOffsetForScrollingSibling() > targetScroll) {
                    animateOffsetTo(coordinatorLayout, child, targetScroll, velocityY);
                    flung = true;
                }
            }
            this.mWasNestedFlung = flung;
            return flung;
        }

        public void setDragCallback(@Nullable DragCallback callback) {
            this.mOnDragCallback = callback;
        }

        private void animateOffsetTo(CoordinatorLayout coordinatorLayout, AppBarLayout child, int offset, float velocity) {
            int duration;
            int distance = Math.abs(getTopBottomOffsetForScrollingSibling() - offset);
            velocity = Math.abs(velocity);
            if (velocity > 0.0f) {
                duration = Math.round(1000.0f * (((float) distance) / velocity)) * 3;
            } else {
                duration = (int) ((1.0f + (((float) distance) / ((float) child.getHeight()))) * 150.0f);
            }
            animateOffsetWithDuration(coordinatorLayout, child, offset, duration);
        }

        private void animateOffsetWithDuration(CoordinatorLayout coordinatorLayout, AppBarLayout child, int offset, int duration) {
            int currentOffset = getTopBottomOffsetForScrollingSibling();
            if (currentOffset != offset) {
                if (this.mOffsetAnimator == null) {
                    this.mOffsetAnimator = ViewUtils.createAnimator();
                    this.mOffsetAnimator.setInterpolator(AnimationUtils.DECELERATE_INTERPOLATOR);
                    this.mOffsetAnimator.addUpdateListener(new C08321(coordinatorLayout, child));
                } else {
                    this.mOffsetAnimator.cancel();
                }
                this.mOffsetAnimator.setDuration((long) Math.min(duration, MAX_OFFSET_ANIMATION_DURATION));
                this.mOffsetAnimator.setIntValues(currentOffset, offset);
                this.mOffsetAnimator.start();
            } else if (this.mOffsetAnimator != null && this.mOffsetAnimator.isRunning()) {
                this.mOffsetAnimator.cancel();
            }
        }

        private int getChildIndexOnOffset(AppBarLayout abl, int offset) {
            int count = abl.getChildCount();
            for (int i = 0; i < count; i += AppBarLayout.PENDING_ACTION_EXPANDED) {
                View child = abl.getChildAt(i);
                if (child.getTop() <= (-offset) && child.getBottom() >= (-offset)) {
                    return i;
                }
            }
            return INVALID_POSITION;
        }

        private void snapToChildIfNeeded(CoordinatorLayout coordinatorLayout, AppBarLayout abl) {
            int offset = getTopBottomOffsetForScrollingSibling();
            int offsetChildIndex = getChildIndexOnOffset(abl, offset);
            if (offsetChildIndex >= 0) {
                View offsetChild = abl.getChildAt(offsetChildIndex);
                int flags = ((LayoutParams) offsetChild.getLayoutParams()).getScrollFlags();
                if ((flags & 17) == 17) {
                    int newOffset;
                    int snapTop = -offsetChild.getTop();
                    int snapBottom = -offsetChild.getBottom();
                    if (offsetChildIndex == abl.getChildCount() + INVALID_POSITION) {
                        snapBottom += abl.getTopInset();
                    }
                    if (checkFlag(flags, AppBarLayout.PENDING_ACTION_COLLAPSED)) {
                        snapBottom += ViewCompat.getMinimumHeight(offsetChild);
                    } else if (checkFlag(flags, 5)) {
                        int seam = snapBottom + ViewCompat.getMinimumHeight(offsetChild);
                        if (offset < seam) {
                            snapTop = seam;
                        } else {
                            snapBottom = seam;
                        }
                    }
                    if (offset < (snapBottom + snapTop) / AppBarLayout.PENDING_ACTION_COLLAPSED) {
                        newOffset = snapBottom;
                    } else {
                        newOffset = snapTop;
                    }
                    animateOffsetTo(coordinatorLayout, abl, MathUtils.constrain(newOffset, -abl.getTotalScrollRange(), 0), 0.0f);
                }
            }
        }

        private static boolean checkFlag(int flags, int check) {
            return (flags & check) == check;
        }

        public boolean onMeasureChild(CoordinatorLayout parent, AppBarLayout child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
            if (((android.support.design.widget.CoordinatorLayout.LayoutParams) child.getLayoutParams()).height != -2) {
                return super.onMeasureChild(parent, child, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec, heightUsed);
            }
            parent.onMeasureChild(child, parentWidthMeasureSpec, widthUsed, MeasureSpec.makeMeasureSpec(0, 0), heightUsed);
            return true;
        }

        public boolean onLayoutChild(CoordinatorLayout parent, AppBarLayout abl, int layoutDirection) {
            boolean handled = super.onLayoutChild(parent, abl, layoutDirection);
            int pendingAction = abl.getPendingAction();
            int offset;
            if (pendingAction != 0) {
                boolean animate = (pendingAction & AppBarLayout.PENDING_ACTION_ANIMATE_ENABLED) != 0;
                if ((pendingAction & AppBarLayout.PENDING_ACTION_COLLAPSED) != 0) {
                    offset = -abl.getUpNestedPreScrollRange();
                    if (animate) {
                        animateOffsetTo(parent, abl, offset, 0.0f);
                    } else {
                        setHeaderTopBottomOffset(parent, abl, offset);
                    }
                } else if ((pendingAction & AppBarLayout.PENDING_ACTION_EXPANDED) != 0) {
                    if (animate) {
                        animateOffsetTo(parent, abl, 0, 0.0f);
                    } else {
                        setHeaderTopBottomOffset(parent, abl, 0);
                    }
                }
            } else if (this.mOffsetToChildIndexOnLayout >= 0) {
                View child = abl.getChildAt(this.mOffsetToChildIndexOnLayout);
                offset = -child.getBottom();
                if (this.mOffsetToChildIndexOnLayoutIsMinHeight) {
                    offset += ViewCompat.getMinimumHeight(child);
                } else {
                    offset += Math.round(((float) child.getHeight()) * this.mOffsetToChildIndexOnLayoutPerc);
                }
                setTopAndBottomOffset(offset);
            }
            abl.resetPendingAction();
            this.mOffsetToChildIndexOnLayout = INVALID_POSITION;
            setTopAndBottomOffset(MathUtils.constrain(getTopAndBottomOffset(), -abl.getTotalScrollRange(), 0));
            abl.dispatchOffsetUpdates(getTopAndBottomOffset());
            return handled;
        }

        boolean canDragView(AppBarLayout view) {
            if (this.mOnDragCallback != null) {
                return this.mOnDragCallback.canDrag(view);
            }
            if (this.mLastNestedScrollingChildRef == null) {
                return true;
            }
            View scrollingView = (View) this.mLastNestedScrollingChildRef.get();
            if (scrollingView == null || !scrollingView.isShown() || ViewCompat.canScrollVertically(scrollingView, INVALID_POSITION)) {
                return false;
            }
            return true;
        }

        void onFlingFinished(CoordinatorLayout parent, AppBarLayout layout) {
            snapToChildIfNeeded(parent, layout);
        }

        int getMaxDragOffset(AppBarLayout view) {
            return -view.getDownNestedScrollRange();
        }

        int getScrollRangeForDragFling(AppBarLayout view) {
            return view.getTotalScrollRange();
        }

        int setHeaderTopBottomOffset(CoordinatorLayout coordinatorLayout, AppBarLayout appBarLayout, int newOffset, int minOffset, int maxOffset) {
            int curOffset = getTopBottomOffsetForScrollingSibling();
            int consumed = 0;
            if (minOffset == 0 || curOffset < minOffset || curOffset > maxOffset) {
                this.mOffsetDelta = 0;
            } else {
                newOffset = MathUtils.constrain(newOffset, minOffset, maxOffset);
                if (curOffset != newOffset) {
                    int interpolatedOffset = appBarLayout.hasChildWithInterpolator() ? interpolateOffset(appBarLayout, newOffset) : newOffset;
                    boolean offsetChanged = setTopAndBottomOffset(interpolatedOffset);
                    consumed = curOffset - newOffset;
                    this.mOffsetDelta = newOffset - interpolatedOffset;
                    if (!offsetChanged && appBarLayout.hasChildWithInterpolator()) {
                        coordinatorLayout.dispatchDependentViewsChanged(appBarLayout);
                    }
                    appBarLayout.dispatchOffsetUpdates(getTopAndBottomOffset());
                    updateAppBarLayoutDrawableState(coordinatorLayout, appBarLayout, newOffset, newOffset < curOffset ? INVALID_POSITION : AppBarLayout.PENDING_ACTION_EXPANDED);
                }
            }
            return consumed;
        }

        @VisibleForTesting
        boolean isOffsetAnimatorRunning() {
            return this.mOffsetAnimator != null && this.mOffsetAnimator.isRunning();
        }

        private int interpolateOffset(AppBarLayout layout, int offset) {
            int absOffset = Math.abs(offset);
            int i = 0;
            int z = layout.getChildCount();
            while (i < z) {
                View child = layout.getChildAt(i);
                LayoutParams childLp = (LayoutParams) child.getLayoutParams();
                Interpolator interpolator = childLp.getScrollInterpolator();
                if (absOffset < child.getTop() || absOffset > child.getBottom()) {
                    i += AppBarLayout.PENDING_ACTION_EXPANDED;
                } else if (interpolator == null) {
                    return offset;
                } else {
                    int childScrollableHeight = 0;
                    int flags = childLp.getScrollFlags();
                    if ((flags & AppBarLayout.PENDING_ACTION_EXPANDED) != 0) {
                        childScrollableHeight = 0 + ((child.getHeight() + childLp.topMargin) + childLp.bottomMargin);
                        if ((flags & AppBarLayout.PENDING_ACTION_COLLAPSED) != 0) {
                            childScrollableHeight -= ViewCompat.getMinimumHeight(child);
                        }
                    }
                    if (ViewCompat.getFitsSystemWindows(child)) {
                        childScrollableHeight -= layout.getTopInset();
                    }
                    if (childScrollableHeight <= 0) {
                        return offset;
                    }
                    return Integer.signum(offset) * (child.getTop() + Math.round(((float) childScrollableHeight) * interpolator.getInterpolation(((float) (absOffset - child.getTop())) / ((float) childScrollableHeight))));
                }
            }
            return offset;
        }

        private void updateAppBarLayoutDrawableState(CoordinatorLayout parent, AppBarLayout layout, int offset, int direction) {
            View child = getAppBarChildOnOffset(layout, offset);
            if (child != null) {
                int flags = ((LayoutParams) child.getLayoutParams()).getScrollFlags();
                boolean collapsed = false;
                if ((flags & AppBarLayout.PENDING_ACTION_EXPANDED) != 0) {
                    int minHeight = ViewCompat.getMinimumHeight(child);
                    if (direction > 0 && (flags & 12) != 0) {
                        collapsed = (-offset) >= (child.getBottom() - minHeight) - layout.getTopInset();
                    } else if ((flags & AppBarLayout.PENDING_ACTION_COLLAPSED) != 0) {
                        collapsed = (-offset) >= (child.getBottom() - minHeight) - layout.getTopInset();
                    }
                }
                if (layout.setCollapsedState(collapsed) && VERSION.SDK_INT >= 11 && shouldJumpElevationState(parent, layout)) {
                    layout.jumpDrawablesToCurrentState();
                }
            }
        }

        private boolean shouldJumpElevationState(CoordinatorLayout parent, AppBarLayout layout) {
            List<View> dependencies = parent.getDependents(layout);
            int i = 0;
            int size = dependencies.size();
            while (i < size) {
                android.support.design.widget.CoordinatorLayout.Behavior behavior = ((android.support.design.widget.CoordinatorLayout.LayoutParams) ((View) dependencies.get(i)).getLayoutParams()).getBehavior();
                if (!(behavior instanceof ScrollingViewBehavior)) {
                    i += AppBarLayout.PENDING_ACTION_EXPANDED;
                } else if (((ScrollingViewBehavior) behavior).getOverlayTop() != 0) {
                    return true;
                } else {
                    return false;
                }
            }
            return false;
        }

        private static View getAppBarChildOnOffset(AppBarLayout layout, int offset) {
            int absOffset = Math.abs(offset);
            int z = layout.getChildCount();
            for (int i = 0; i < z; i += AppBarLayout.PENDING_ACTION_EXPANDED) {
                View child = layout.getChildAt(i);
                if (absOffset >= child.getTop() && absOffset <= child.getBottom()) {
                    return child;
                }
            }
            return null;
        }

        int getTopBottomOffsetForScrollingSibling() {
            return getTopAndBottomOffset() + this.mOffsetDelta;
        }

        public Parcelable onSaveInstanceState(CoordinatorLayout parent, AppBarLayout abl) {
            Parcelable superState = super.onSaveInstanceState(parent, abl);
            int offset = getTopAndBottomOffset();
            int i = 0;
            int count = abl.getChildCount();
            while (i < count) {
                View child = abl.getChildAt(i);
                int visBottom = child.getBottom() + offset;
                if (child.getTop() + offset > 0 || visBottom < 0) {
                    i += AppBarLayout.PENDING_ACTION_EXPANDED;
                } else {
                    SavedState ss = new SavedState(superState);
                    ss.firstVisibleChildIndex = i;
                    ss.firstVisibleChildAtMinimumHeight = visBottom == ViewCompat.getMinimumHeight(child) + abl.getTopInset();
                    ss.firstVisibleChildPercentageShown = ((float) visBottom) / ((float) child.getHeight());
                    return ss;
                }
            }
            return superState;
        }

        public void onRestoreInstanceState(CoordinatorLayout parent, AppBarLayout appBarLayout, Parcelable state) {
            if (state instanceof SavedState) {
                SavedState ss = (SavedState) state;
                super.onRestoreInstanceState(parent, appBarLayout, ss.getSuperState());
                this.mOffsetToChildIndexOnLayout = ss.firstVisibleChildIndex;
                this.mOffsetToChildIndexOnLayoutPerc = ss.firstVisibleChildPercentageShown;
                this.mOffsetToChildIndexOnLayoutIsMinHeight = ss.firstVisibleChildAtMinimumHeight;
                return;
            }
            super.onRestoreInstanceState(parent, appBarLayout, state);
            this.mOffsetToChildIndexOnLayout = INVALID_POSITION;
        }
    }

    public static class ScrollingViewBehavior extends HeaderScrollingViewBehavior {
        public /* bridge */ /* synthetic */ int getLeftAndRightOffset() {
            return super.getLeftAndRightOffset();
        }

        public /* bridge */ /* synthetic */ int getTopAndBottomOffset() {
            return super.getTopAndBottomOffset();
        }

        public /* bridge */ /* synthetic */ boolean onLayoutChild(CoordinatorLayout coordinatorLayout, View view, int i) {
            return super.onLayoutChild(coordinatorLayout, view, i);
        }

        public /* bridge */ /* synthetic */ boolean onMeasureChild(CoordinatorLayout coordinatorLayout, View view, int i, int i2, int i3, int i4) {
            return super.onMeasureChild(coordinatorLayout, view, i, i2, i3, i4);
        }

        public /* bridge */ /* synthetic */ boolean setLeftAndRightOffset(int i) {
            return super.setLeftAndRightOffset(i);
        }

        public /* bridge */ /* synthetic */ boolean setTopAndBottomOffset(int i) {
            return super.setTopAndBottomOffset(i);
        }

        public ScrollingViewBehavior(Context context, AttributeSet attrs) {
            super(context, attrs);
            TypedArray a = context.obtainStyledAttributes(attrs, C0023R.styleable.ScrollingViewBehavior_Layout);
            setOverlayTop(a.getDimensionPixelSize(C0023R.styleable.ScrollingViewBehavior_Layout_behavior_overlapTop, 0));
            a.recycle();
        }

        public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
            return dependency instanceof AppBarLayout;
        }

        public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
            offsetChildAsNeeded(parent, child, dependency);
            return false;
        }

        public boolean onRequestChildRectangleOnScreen(CoordinatorLayout parent, View child, Rect rectangle, boolean immediate) {
            AppBarLayout header = findFirstDependency(parent.getDependencies(child));
            if (header != null) {
                rectangle.offset(child.getLeft(), child.getTop());
                Rect parentRect = this.mTempRect1;
                parentRect.set(0, 0, parent.getWidth(), parent.getHeight());
                if (!parentRect.contains(rectangle)) {
                    boolean z;
                    if (immediate) {
                        z = false;
                    } else {
                        z = true;
                    }
                    header.setExpanded(false, z);
                    return true;
                }
            }
            return false;
        }

        private void offsetChildAsNeeded(CoordinatorLayout parent, View child, View dependency) {
            android.support.design.widget.CoordinatorLayout.Behavior behavior = ((android.support.design.widget.CoordinatorLayout.LayoutParams) dependency.getLayoutParams()).getBehavior();
            if (behavior instanceof Behavior) {
                ViewCompat.offsetTopAndBottom(child, (((dependency.getBottom() - child.getTop()) + ((Behavior) behavior).mOffsetDelta) + getVerticalLayoutGap()) - getOverlapPixelsForOffset(dependency));
            }
        }

        float getOverlapRatioForOffset(View header) {
            if (!(header instanceof AppBarLayout)) {
                return 0.0f;
            }
            AppBarLayout abl = (AppBarLayout) header;
            int totalScrollRange = abl.getTotalScrollRange();
            int preScrollDown = abl.getDownNestedPreScrollRange();
            int offset = getAppBarLayoutOffset(abl);
            if (preScrollDown != 0 && totalScrollRange + offset <= preScrollDown) {
                return 0.0f;
            }
            int availScrollRange = totalScrollRange - preScrollDown;
            if (availScrollRange != 0) {
                return 1.0f + (((float) offset) / ((float) availScrollRange));
            }
            return 0.0f;
        }

        private static int getAppBarLayoutOffset(AppBarLayout abl) {
            android.support.design.widget.CoordinatorLayout.Behavior behavior = ((android.support.design.widget.CoordinatorLayout.LayoutParams) abl.getLayoutParams()).getBehavior();
            if (behavior instanceof Behavior) {
                return ((Behavior) behavior).getTopBottomOffsetForScrollingSibling();
            }
            return 0;
        }

        AppBarLayout findFirstDependency(List<View> views) {
            int z = views.size();
            for (int i = 0; i < z; i += AppBarLayout.PENDING_ACTION_EXPANDED) {
                View view = (View) views.get(i);
                if (view instanceof AppBarLayout) {
                    return (AppBarLayout) view;
                }
            }
            return null;
        }

        int getScrollRange(View v) {
            if (v instanceof AppBarLayout) {
                return ((AppBarLayout) v).getTotalScrollRange();
            }
            return super.getScrollRange(v);
        }
    }

    public AppBarLayout(Context context) {
        this(context, null);
    }

    public AppBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mTotalScrollRange = INVALID_SCROLL_RANGE;
        this.mDownPreScrollRange = INVALID_SCROLL_RANGE;
        this.mDownScrollRange = INVALID_SCROLL_RANGE;
        this.mPendingAction = 0;
        this.mTmpStatesArray = new int[PENDING_ACTION_COLLAPSED];
        setOrientation(PENDING_ACTION_EXPANDED);
        ThemeUtils.checkAppCompatTheme(context);
        if (VERSION.SDK_INT >= 21) {
            ViewUtilsLollipop.setBoundsViewOutlineProvider(this);
            ViewUtilsLollipop.setStateListAnimatorFromAttrs(this, attrs, 0, C0023R.style.Widget_Design_AppBarLayout);
        }
        TypedArray a = context.obtainStyledAttributes(attrs, C0023R.styleable.AppBarLayout, 0, C0023R.style.Widget_Design_AppBarLayout);
        ViewCompat.setBackground(this, a.getDrawable(C0023R.styleable.AppBarLayout_android_background));
        if (a.hasValue(C0023R.styleable.AppBarLayout_expanded)) {
            setExpanded(a.getBoolean(C0023R.styleable.AppBarLayout_expanded, false));
        }
        if (VERSION.SDK_INT >= 21 && a.hasValue(C0023R.styleable.AppBarLayout_elevation)) {
            ViewUtilsLollipop.setDefaultAppBarLayoutStateListAnimator(this, (float) a.getDimensionPixelSize(C0023R.styleable.AppBarLayout_elevation, 0));
        }
        a.recycle();
        ViewCompat.setOnApplyWindowInsetsListener(this, new C08311());
    }

    public void addOnOffsetChangedListener(OnOffsetChangedListener listener) {
        if (this.mListeners == null) {
            this.mListeners = new ArrayList();
        }
        if (listener != null && !this.mListeners.contains(listener)) {
            this.mListeners.add(listener);
        }
    }

    public void removeOnOffsetChangedListener(OnOffsetChangedListener listener) {
        if (this.mListeners != null && listener != null) {
            this.mListeners.remove(listener);
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        invalidateScrollRanges();
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        invalidateScrollRanges();
        this.mHaveChildWithInterpolator = false;
        int z = getChildCount();
        for (int i = 0; i < z; i += PENDING_ACTION_EXPANDED) {
            if (((LayoutParams) getChildAt(i).getLayoutParams()).getScrollInterpolator() != null) {
                this.mHaveChildWithInterpolator = true;
                break;
            }
        }
        updateCollapsible();
    }

    private void updateCollapsible() {
        boolean haveCollapsibleChild = false;
        int z = getChildCount();
        for (int i = 0; i < z; i += PENDING_ACTION_EXPANDED) {
            if (((LayoutParams) getChildAt(i).getLayoutParams()).isCollapsible()) {
                haveCollapsibleChild = true;
                break;
            }
        }
        setCollapsibleState(haveCollapsibleChild);
    }

    private void invalidateScrollRanges() {
        this.mTotalScrollRange = INVALID_SCROLL_RANGE;
        this.mDownPreScrollRange = INVALID_SCROLL_RANGE;
        this.mDownScrollRange = INVALID_SCROLL_RANGE;
    }

    public void setOrientation(int orientation) {
        if (orientation != PENDING_ACTION_EXPANDED) {
            throw new IllegalArgumentException("AppBarLayout is always vertical and does not support horizontal orientation");
        }
        super.setOrientation(orientation);
    }

    public void setExpanded(boolean expanded) {
        setExpanded(expanded, ViewCompat.isLaidOut(this));
    }

    public void setExpanded(boolean expanded, boolean animate) {
        this.mPendingAction = (animate ? PENDING_ACTION_ANIMATE_ENABLED : 0) | (expanded ? PENDING_ACTION_EXPANDED : PENDING_ACTION_COLLAPSED);
        requestLayout();
    }

    protected boolean checkLayoutParams(android.view.ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams((int) INVALID_SCROLL_RANGE, -2);
    }

    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    protected LayoutParams generateLayoutParams(android.view.ViewGroup.LayoutParams p) {
        if (VERSION.SDK_INT >= 19 && (p instanceof android.widget.LinearLayout.LayoutParams)) {
            return new LayoutParams((android.widget.LinearLayout.LayoutParams) p);
        }
        if (p instanceof MarginLayoutParams) {
            return new LayoutParams((MarginLayoutParams) p);
        }
        return new LayoutParams(p);
    }

    boolean hasChildWithInterpolator() {
        return this.mHaveChildWithInterpolator;
    }

    public final int getTotalScrollRange() {
        if (this.mTotalScrollRange != INVALID_SCROLL_RANGE) {
            return this.mTotalScrollRange;
        }
        int range = 0;
        int z = getChildCount();
        for (int i = 0; i < z; i += PENDING_ACTION_EXPANDED) {
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            int childHeight = child.getMeasuredHeight();
            int flags = lp.mScrollFlags;
            if ((flags & PENDING_ACTION_EXPANDED) == 0) {
                break;
            }
            range += (lp.topMargin + childHeight) + lp.bottomMargin;
            if ((flags & PENDING_ACTION_COLLAPSED) != 0) {
                range -= ViewCompat.getMinimumHeight(child);
                break;
            }
        }
        int max = Math.max(0, range - getTopInset());
        this.mTotalScrollRange = max;
        return max;
    }

    boolean hasScrollableChildren() {
        return getTotalScrollRange() != 0;
    }

    int getUpNestedPreScrollRange() {
        return getTotalScrollRange();
    }

    int getDownNestedPreScrollRange() {
        if (this.mDownPreScrollRange != INVALID_SCROLL_RANGE) {
            return this.mDownPreScrollRange;
        }
        int range = 0;
        for (int i = getChildCount() + INVALID_SCROLL_RANGE; i >= 0; i += INVALID_SCROLL_RANGE) {
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            int childHeight = child.getMeasuredHeight();
            int flags = lp.mScrollFlags;
            if ((flags & 5) == 5) {
                range += lp.topMargin + lp.bottomMargin;
                if ((flags & 8) != 0) {
                    range += ViewCompat.getMinimumHeight(child);
                } else if ((flags & PENDING_ACTION_COLLAPSED) != 0) {
                    range += childHeight - ViewCompat.getMinimumHeight(child);
                } else {
                    range += childHeight;
                }
            } else if (range > 0) {
                break;
            }
        }
        int max = Math.max(0, range);
        this.mDownPreScrollRange = max;
        return max;
    }

    int getDownNestedScrollRange() {
        if (this.mDownScrollRange != INVALID_SCROLL_RANGE) {
            return this.mDownScrollRange;
        }
        int range = 0;
        int z = getChildCount();
        for (int i = 0; i < z; i += PENDING_ACTION_EXPANDED) {
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            int childHeight = child.getMeasuredHeight() + (lp.topMargin + lp.bottomMargin);
            int flags = lp.mScrollFlags;
            if ((flags & PENDING_ACTION_EXPANDED) == 0) {
                break;
            }
            range += childHeight;
            if ((flags & PENDING_ACTION_COLLAPSED) != 0) {
                range -= ViewCompat.getMinimumHeight(child) + getTopInset();
                break;
            }
        }
        int max = Math.max(0, range);
        this.mDownScrollRange = max;
        return max;
    }

    void dispatchOffsetUpdates(int offset) {
        if (this.mListeners != null) {
            int z = this.mListeners.size();
            for (int i = 0; i < z; i += PENDING_ACTION_EXPANDED) {
                OnOffsetChangedListener listener = (OnOffsetChangedListener) this.mListeners.get(i);
                if (listener != null) {
                    listener.onOffsetChanged(this, offset);
                }
            }
        }
    }

    final int getMinimumHeightForVisibleOverlappingContent() {
        int topInset = getTopInset();
        int minHeight = ViewCompat.getMinimumHeight(this);
        if (minHeight != 0) {
            return (minHeight * PENDING_ACTION_COLLAPSED) + topInset;
        }
        int childCount = getChildCount();
        int lastChildMinHeight = childCount >= PENDING_ACTION_EXPANDED ? ViewCompat.getMinimumHeight(getChildAt(childCount + INVALID_SCROLL_RANGE)) : 0;
        if (lastChildMinHeight != 0) {
            return (lastChildMinHeight * PENDING_ACTION_COLLAPSED) + topInset;
        }
        return getHeight() / 3;
    }

    protected int[] onCreateDrawableState(int extraSpace) {
        int[] extraStates = this.mTmpStatesArray;
        int[] states = super.onCreateDrawableState(extraStates.length + extraSpace);
        extraStates[0] = this.mCollapsible ? C0023R.attr.state_collapsible : -C0023R.attr.state_collapsible;
        int i = (this.mCollapsible && this.mCollapsed) ? C0023R.attr.state_collapsed : -C0023R.attr.state_collapsed;
        extraStates[PENDING_ACTION_EXPANDED] = i;
        return mergeDrawableStates(states, extraStates);
    }

    private boolean setCollapsibleState(boolean collapsible) {
        if (this.mCollapsible == collapsible) {
            return false;
        }
        this.mCollapsible = collapsible;
        refreshDrawableState();
        return true;
    }

    boolean setCollapsedState(boolean collapsed) {
        if (this.mCollapsed == collapsed) {
            return false;
        }
        this.mCollapsed = collapsed;
        refreshDrawableState();
        return true;
    }

    @Deprecated
    public void setTargetElevation(float elevation) {
        if (VERSION.SDK_INT >= 21) {
            ViewUtilsLollipop.setDefaultAppBarLayoutStateListAnimator(this, elevation);
        }
    }

    @Deprecated
    public float getTargetElevation() {
        return 0.0f;
    }

    int getPendingAction() {
        return this.mPendingAction;
    }

    void resetPendingAction() {
        this.mPendingAction = 0;
    }

    @VisibleForTesting
    final int getTopInset() {
        return this.mLastInsets != null ? this.mLastInsets.getSystemWindowInsetTop() : 0;
    }

    WindowInsetsCompat onWindowInsetChanged(WindowInsetsCompat insets) {
        WindowInsetsCompat newInsets = null;
        if (ViewCompat.getFitsSystemWindows(this)) {
            newInsets = insets;
        }
        if (!ViewUtils.objectEquals(this.mLastInsets, newInsets)) {
            this.mLastInsets = newInsets;
            invalidateScrollRanges();
        }
        return insets;
    }
}
