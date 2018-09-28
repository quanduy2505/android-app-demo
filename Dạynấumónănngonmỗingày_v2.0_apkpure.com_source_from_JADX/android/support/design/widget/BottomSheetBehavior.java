package android.support.design.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.annotation.VisibleForTesting;
import android.support.design.C0023R;
import android.support.design.widget.CoordinatorLayout.Behavior;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;
import android.support.v4.view.AbsSavedState;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.PointerIconCompat;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.support.v4.widget.ViewDragHelper.Callback;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import rx.internal.schedulers.NewThreadWorker;

public class BottomSheetBehavior<V extends View> extends Behavior<V> {
    private static final float HIDE_FRICTION = 0.1f;
    private static final float HIDE_THRESHOLD = 0.5f;
    public static final int PEEK_HEIGHT_AUTO = -1;
    public static final int STATE_COLLAPSED = 4;
    public static final int STATE_DRAGGING = 1;
    public static final int STATE_EXPANDED = 3;
    public static final int STATE_HIDDEN = 5;
    public static final int STATE_SETTLING = 2;
    int mActivePointerId;
    private BottomSheetCallback mCallback;
    private final Callback mDragCallback;
    boolean mHideable;
    private boolean mIgnoreEvents;
    private int mInitialY;
    private int mLastNestedScrollDy;
    int mMaxOffset;
    private float mMaximumVelocity;
    int mMinOffset;
    private boolean mNestedScrolled;
    WeakReference<View> mNestedScrollingChildRef;
    int mParentHeight;
    private int mPeekHeight;
    private boolean mPeekHeightAuto;
    private int mPeekHeightMin;
    private boolean mSkipCollapsed;
    int mState;
    boolean mTouchingScrollingChild;
    private VelocityTracker mVelocityTracker;
    ViewDragHelper mViewDragHelper;
    WeakReference<V> mViewRef;

    /* renamed from: android.support.design.widget.BottomSheetBehavior.1 */
    class C00301 implements Runnable {
        final /* synthetic */ View val$child;
        final /* synthetic */ int val$state;

        C00301(View view, int i) {
            this.val$child = view;
            this.val$state = i;
        }

        public void run() {
            BottomSheetBehavior.this.startSettlingAnimation(this.val$child, this.val$state);
        }
    }

    public static abstract class BottomSheetCallback {
        public abstract void onSlide(@NonNull View view, float f);

        public abstract void onStateChanged(@NonNull View view, int i);
    }

    private class SettleRunnable implements Runnable {
        private final int mTargetState;
        private final View mView;

        SettleRunnable(View view, int targetState) {
            this.mView = view;
            this.mTargetState = targetState;
        }

        public void run() {
            if (BottomSheetBehavior.this.mViewDragHelper == null || !BottomSheetBehavior.this.mViewDragHelper.continueSettling(true)) {
                BottomSheetBehavior.this.setStateInternal(this.mTargetState);
            } else {
                ViewCompat.postOnAnimation(this.mView, this);
            }
        }
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    @Retention(RetentionPolicy.SOURCE)
    public @interface State {
    }

    /* renamed from: android.support.design.widget.BottomSheetBehavior.2 */
    class C08402 extends Callback {
        C08402() {
        }

        public boolean tryCaptureView(View child, int pointerId) {
            boolean z = true;
            if (BottomSheetBehavior.this.mState == BottomSheetBehavior.STATE_DRAGGING || BottomSheetBehavior.this.mTouchingScrollingChild) {
                return false;
            }
            if (BottomSheetBehavior.this.mState == BottomSheetBehavior.STATE_EXPANDED && BottomSheetBehavior.this.mActivePointerId == pointerId) {
                View scroll = (View) BottomSheetBehavior.this.mNestedScrollingChildRef.get();
                if (scroll != null && ViewCompat.canScrollVertically(scroll, BottomSheetBehavior.PEEK_HEIGHT_AUTO)) {
                    return false;
                }
            }
            if (BottomSheetBehavior.this.mViewRef == null || BottomSheetBehavior.this.mViewRef.get() != child) {
                z = false;
            }
            return z;
        }

        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            BottomSheetBehavior.this.dispatchOnSlide(top);
        }

        public void onViewDragStateChanged(int state) {
            if (state == BottomSheetBehavior.STATE_DRAGGING) {
                BottomSheetBehavior.this.setStateInternal(BottomSheetBehavior.STATE_DRAGGING);
            }
        }

        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            int top;
            int targetState;
            if (yvel < 0.0f) {
                top = BottomSheetBehavior.this.mMinOffset;
                targetState = BottomSheetBehavior.STATE_EXPANDED;
            } else if (BottomSheetBehavior.this.mHideable && BottomSheetBehavior.this.shouldHide(releasedChild, yvel)) {
                top = BottomSheetBehavior.this.mParentHeight;
                targetState = BottomSheetBehavior.STATE_HIDDEN;
            } else if (yvel == 0.0f) {
                int currentTop = releasedChild.getTop();
                if (Math.abs(currentTop - BottomSheetBehavior.this.mMinOffset) < Math.abs(currentTop - BottomSheetBehavior.this.mMaxOffset)) {
                    top = BottomSheetBehavior.this.mMinOffset;
                    targetState = BottomSheetBehavior.STATE_EXPANDED;
                } else {
                    top = BottomSheetBehavior.this.mMaxOffset;
                    targetState = BottomSheetBehavior.STATE_COLLAPSED;
                }
            } else {
                top = BottomSheetBehavior.this.mMaxOffset;
                targetState = BottomSheetBehavior.STATE_COLLAPSED;
            }
            if (BottomSheetBehavior.this.mViewDragHelper.settleCapturedViewAt(releasedChild.getLeft(), top)) {
                BottomSheetBehavior.this.setStateInternal(BottomSheetBehavior.STATE_SETTLING);
                ViewCompat.postOnAnimation(releasedChild, new SettleRunnable(releasedChild, targetState));
                return;
            }
            BottomSheetBehavior.this.setStateInternal(targetState);
        }

        public int clampViewPositionVertical(View child, int top, int dy) {
            return MathUtils.constrain(top, BottomSheetBehavior.this.mMinOffset, BottomSheetBehavior.this.mHideable ? BottomSheetBehavior.this.mParentHeight : BottomSheetBehavior.this.mMaxOffset);
        }

        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return child.getLeft();
        }

        public int getViewVerticalDragRange(View child) {
            if (BottomSheetBehavior.this.mHideable) {
                return BottomSheetBehavior.this.mParentHeight - BottomSheetBehavior.this.mMinOffset;
            }
            return BottomSheetBehavior.this.mMaxOffset - BottomSheetBehavior.this.mMinOffset;
        }
    }

    protected static class SavedState extends AbsSavedState {
        public static final Creator<SavedState> CREATOR;
        final int state;

        /* renamed from: android.support.design.widget.BottomSheetBehavior.SavedState.1 */
        static class C08411 implements ParcelableCompatCreatorCallbacks<SavedState> {
            C08411() {
            }

            public SavedState createFromParcel(Parcel in, ClassLoader loader) {
                return new SavedState(in, loader);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        }

        public SavedState(Parcel source) {
            this(source, null);
        }

        public SavedState(Parcel source, ClassLoader loader) {
            super(source, loader);
            this.state = source.readInt();
        }

        public SavedState(Parcelable superState, int state) {
            super(superState);
            this.state = state;
        }

        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(this.state);
        }

        static {
            CREATOR = ParcelableCompat.newCreator(new C08411());
        }
    }

    public BottomSheetBehavior() {
        this.mState = STATE_COLLAPSED;
        this.mDragCallback = new C08402();
    }

    public BottomSheetBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mState = STATE_COLLAPSED;
        this.mDragCallback = new C08402();
        TypedArray a = context.obtainStyledAttributes(attrs, C0023R.styleable.BottomSheetBehavior_Layout);
        TypedValue value = a.peekValue(C0023R.styleable.BottomSheetBehavior_Layout_behavior_peekHeight);
        if (value == null || value.data != PEEK_HEIGHT_AUTO) {
            setPeekHeight(a.getDimensionPixelSize(C0023R.styleable.BottomSheetBehavior_Layout_behavior_peekHeight, PEEK_HEIGHT_AUTO));
        } else {
            setPeekHeight(value.data);
        }
        setHideable(a.getBoolean(C0023R.styleable.BottomSheetBehavior_Layout_behavior_hideable, false));
        setSkipCollapsed(a.getBoolean(C0023R.styleable.BottomSheetBehavior_Layout_behavior_skipCollapsed, false));
        a.recycle();
        this.mMaximumVelocity = (float) ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
    }

    public Parcelable onSaveInstanceState(CoordinatorLayout parent, V child) {
        return new SavedState(super.onSaveInstanceState(parent, child), this.mState);
    }

    public void onRestoreInstanceState(CoordinatorLayout parent, V child, Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(parent, child, ss.getSuperState());
        if (ss.state == STATE_DRAGGING || ss.state == STATE_SETTLING) {
            this.mState = STATE_COLLAPSED;
        } else {
            this.mState = ss.state;
        }
    }

    public boolean onLayoutChild(CoordinatorLayout parent, V child, int layoutDirection) {
        int peekHeight;
        if (ViewCompat.getFitsSystemWindows(parent) && !ViewCompat.getFitsSystemWindows(child)) {
            ViewCompat.setFitsSystemWindows(child, true);
        }
        int savedTop = child.getTop();
        parent.onLayoutChild(child, layoutDirection);
        this.mParentHeight = parent.getHeight();
        if (this.mPeekHeightAuto) {
            if (this.mPeekHeightMin == 0) {
                this.mPeekHeightMin = parent.getResources().getDimensionPixelSize(C0023R.dimen.design_bottom_sheet_peek_height_min);
            }
            peekHeight = Math.max(this.mPeekHeightMin, this.mParentHeight - ((parent.getWidth() * 9) / 16));
        } else {
            peekHeight = this.mPeekHeight;
        }
        this.mMinOffset = Math.max(0, this.mParentHeight - child.getHeight());
        this.mMaxOffset = Math.max(this.mParentHeight - peekHeight, this.mMinOffset);
        if (this.mState == STATE_EXPANDED) {
            ViewCompat.offsetTopAndBottom(child, this.mMinOffset);
        } else if (this.mHideable && this.mState == STATE_HIDDEN) {
            ViewCompat.offsetTopAndBottom(child, this.mParentHeight);
        } else if (this.mState == STATE_COLLAPSED) {
            ViewCompat.offsetTopAndBottom(child, this.mMaxOffset);
        } else if (this.mState == STATE_DRAGGING || this.mState == STATE_SETTLING) {
            ViewCompat.offsetTopAndBottom(child, savedTop - child.getTop());
        }
        if (this.mViewDragHelper == null) {
            this.mViewDragHelper = ViewDragHelper.create(parent, this.mDragCallback);
        }
        this.mViewRef = new WeakReference(child);
        this.mNestedScrollingChildRef = new WeakReference(findScrollingChild(child));
        return true;
    }

    public boolean onInterceptTouchEvent(CoordinatorLayout parent, V child, MotionEvent event) {
        boolean z = true;
        if (child.isShown()) {
            View scroll;
            int action = MotionEventCompat.getActionMasked(event);
            if (action == 0) {
                reset();
            }
            if (this.mVelocityTracker == null) {
                this.mVelocityTracker = VelocityTracker.obtain();
            }
            this.mVelocityTracker.addMovement(event);
            switch (action) {
                case NewThreadWorker.PURGE_FREQUENCY /*0*/:
                    int initialX = (int) event.getX();
                    this.mInitialY = (int) event.getY();
                    scroll = (View) this.mNestedScrollingChildRef.get();
                    if (scroll != null && parent.isPointInChildBounds(scroll, initialX, this.mInitialY)) {
                        this.mActivePointerId = event.getPointerId(event.getActionIndex());
                        this.mTouchingScrollingChild = true;
                    }
                    boolean z2 = this.mActivePointerId == PEEK_HEIGHT_AUTO && !parent.isPointInChildBounds(child, initialX, this.mInitialY);
                    this.mIgnoreEvents = z2;
                    break;
                case STATE_DRAGGING /*1*/:
                case STATE_EXPANDED /*3*/:
                    this.mTouchingScrollingChild = false;
                    this.mActivePointerId = PEEK_HEIGHT_AUTO;
                    if (this.mIgnoreEvents) {
                        this.mIgnoreEvents = false;
                        return false;
                    }
                    break;
            }
            if (!this.mIgnoreEvents && this.mViewDragHelper.shouldInterceptTouchEvent(event)) {
                return true;
            }
            scroll = (View) this.mNestedScrollingChildRef.get();
            if (action != STATE_SETTLING || scroll == null || this.mIgnoreEvents || this.mState == STATE_DRAGGING || parent.isPointInChildBounds(scroll, (int) event.getX(), (int) event.getY()) || Math.abs(((float) this.mInitialY) - event.getY()) <= ((float) this.mViewDragHelper.getTouchSlop())) {
                z = false;
            }
            return z;
        }
        this.mIgnoreEvents = true;
        return false;
    }

    public boolean onTouchEvent(CoordinatorLayout parent, V child, MotionEvent event) {
        if (!child.isShown()) {
            return false;
        }
        int action = MotionEventCompat.getActionMasked(event);
        if (this.mState == STATE_DRAGGING && action == 0) {
            return true;
        }
        this.mViewDragHelper.processTouchEvent(event);
        if (action == 0) {
            reset();
        }
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(event);
        if (action == STATE_SETTLING && !this.mIgnoreEvents && Math.abs(((float) this.mInitialY) - event.getY()) > ((float) this.mViewDragHelper.getTouchSlop())) {
            this.mViewDragHelper.captureChildView(child, event.getPointerId(event.getActionIndex()));
        }
        if (this.mIgnoreEvents) {
            return false;
        }
        return true;
    }

    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, V v, View directTargetChild, View target, int nestedScrollAxes) {
        this.mLastNestedScrollDy = 0;
        this.mNestedScrolled = false;
        if ((nestedScrollAxes & STATE_SETTLING) != 0) {
            return true;
        }
        return false;
    }

    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, V child, View target, int dx, int dy, int[] consumed) {
        if (target == ((View) this.mNestedScrollingChildRef.get())) {
            int currentTop = child.getTop();
            int newTop = currentTop - dy;
            if (dy > 0) {
                if (newTop < this.mMinOffset) {
                    consumed[STATE_DRAGGING] = currentTop - this.mMinOffset;
                    ViewCompat.offsetTopAndBottom(child, -consumed[STATE_DRAGGING]);
                    setStateInternal(STATE_EXPANDED);
                } else {
                    consumed[STATE_DRAGGING] = dy;
                    ViewCompat.offsetTopAndBottom(child, -dy);
                    setStateInternal(STATE_DRAGGING);
                }
            } else if (dy < 0 && !ViewCompat.canScrollVertically(target, PEEK_HEIGHT_AUTO)) {
                if (newTop <= this.mMaxOffset || this.mHideable) {
                    consumed[STATE_DRAGGING] = dy;
                    ViewCompat.offsetTopAndBottom(child, -dy);
                    setStateInternal(STATE_DRAGGING);
                } else {
                    consumed[STATE_DRAGGING] = currentTop - this.mMaxOffset;
                    ViewCompat.offsetTopAndBottom(child, -consumed[STATE_DRAGGING]);
                    setStateInternal(STATE_COLLAPSED);
                }
            }
            dispatchOnSlide(child.getTop());
            this.mLastNestedScrollDy = dy;
            this.mNestedScrolled = true;
        }
    }

    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, V child, View target) {
        if (child.getTop() == this.mMinOffset) {
            setStateInternal(STATE_EXPANDED);
        } else if (target == this.mNestedScrollingChildRef.get() && this.mNestedScrolled) {
            int top;
            int targetState;
            if (this.mLastNestedScrollDy > 0) {
                top = this.mMinOffset;
                targetState = STATE_EXPANDED;
            } else if (this.mHideable && shouldHide(child, getYVelocity())) {
                top = this.mParentHeight;
                targetState = STATE_HIDDEN;
            } else if (this.mLastNestedScrollDy == 0) {
                int currentTop = child.getTop();
                if (Math.abs(currentTop - this.mMinOffset) < Math.abs(currentTop - this.mMaxOffset)) {
                    top = this.mMinOffset;
                    targetState = STATE_EXPANDED;
                } else {
                    top = this.mMaxOffset;
                    targetState = STATE_COLLAPSED;
                }
            } else {
                top = this.mMaxOffset;
                targetState = STATE_COLLAPSED;
            }
            if (this.mViewDragHelper.smoothSlideViewTo(child, child.getLeft(), top)) {
                setStateInternal(STATE_SETTLING);
                ViewCompat.postOnAnimation(child, new SettleRunnable(child, targetState));
            } else {
                setStateInternal(targetState);
            }
            this.mNestedScrolled = false;
        }
    }

    public boolean onNestedPreFling(CoordinatorLayout coordinatorLayout, V child, View target, float velocityX, float velocityY) {
        return target == this.mNestedScrollingChildRef.get() && (this.mState != STATE_EXPANDED || super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY));
    }

    public final void setPeekHeight(int peekHeight) {
        boolean layout = false;
        if (peekHeight == PEEK_HEIGHT_AUTO) {
            if (!this.mPeekHeightAuto) {
                this.mPeekHeightAuto = true;
                layout = true;
            }
        } else if (this.mPeekHeightAuto || this.mPeekHeight != peekHeight) {
            this.mPeekHeightAuto = false;
            this.mPeekHeight = Math.max(0, peekHeight);
            this.mMaxOffset = this.mParentHeight - peekHeight;
            layout = true;
        }
        if (layout && this.mState == STATE_COLLAPSED && this.mViewRef != null) {
            View view = (View) this.mViewRef.get();
            if (view != null) {
                view.requestLayout();
            }
        }
    }

    public final int getPeekHeight() {
        return this.mPeekHeightAuto ? PEEK_HEIGHT_AUTO : this.mPeekHeight;
    }

    public void setHideable(boolean hideable) {
        this.mHideable = hideable;
    }

    public boolean isHideable() {
        return this.mHideable;
    }

    public void setSkipCollapsed(boolean skipCollapsed) {
        this.mSkipCollapsed = skipCollapsed;
    }

    public boolean getSkipCollapsed() {
        return this.mSkipCollapsed;
    }

    public void setBottomSheetCallback(BottomSheetCallback callback) {
        this.mCallback = callback;
    }

    public final void setState(int state) {
        if (state != this.mState) {
            if (this.mViewRef != null) {
                View child = (View) this.mViewRef.get();
                if (child != null) {
                    ViewParent parent = child.getParent();
                    if (parent != null && parent.isLayoutRequested() && ViewCompat.isAttachedToWindow(child)) {
                        child.post(new C00301(child, state));
                    } else {
                        startSettlingAnimation(child, state);
                    }
                }
            } else if (state == STATE_COLLAPSED || state == STATE_EXPANDED || (this.mHideable && state == STATE_HIDDEN)) {
                this.mState = state;
            }
        }
    }

    public final int getState() {
        return this.mState;
    }

    void setStateInternal(int state) {
        if (this.mState != state) {
            this.mState = state;
            View bottomSheet = (View) this.mViewRef.get();
            if (bottomSheet != null && this.mCallback != null) {
                this.mCallback.onStateChanged(bottomSheet, state);
            }
        }
    }

    private void reset() {
        this.mActivePointerId = PEEK_HEIGHT_AUTO;
        if (this.mVelocityTracker != null) {
            this.mVelocityTracker.recycle();
            this.mVelocityTracker = null;
        }
    }

    boolean shouldHide(View child, float yvel) {
        if (this.mSkipCollapsed) {
            return true;
        }
        if (child.getTop() < this.mMaxOffset) {
            return false;
        }
        if (Math.abs((((float) child.getTop()) + (HIDE_FRICTION * yvel)) - ((float) this.mMaxOffset)) / ((float) this.mPeekHeight) <= HIDE_THRESHOLD) {
            return false;
        }
        return true;
    }

    private View findScrollingChild(View view) {
        if (view instanceof NestedScrollingChild) {
            return view;
        }
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            int count = group.getChildCount();
            for (int i = 0; i < count; i += STATE_DRAGGING) {
                View scrollingChild = findScrollingChild(group.getChildAt(i));
                if (scrollingChild != null) {
                    return scrollingChild;
                }
            }
        }
        return null;
    }

    private float getYVelocity() {
        this.mVelocityTracker.computeCurrentVelocity(PointerIconCompat.TYPE_DEFAULT, this.mMaximumVelocity);
        return VelocityTrackerCompat.getYVelocity(this.mVelocityTracker, this.mActivePointerId);
    }

    void startSettlingAnimation(View child, int state) {
        int top;
        if (state == STATE_COLLAPSED) {
            top = this.mMaxOffset;
        } else if (state == STATE_EXPANDED) {
            top = this.mMinOffset;
        } else if (this.mHideable && state == STATE_HIDDEN) {
            top = this.mParentHeight;
        } else {
            throw new IllegalArgumentException("Illegal state argument: " + state);
        }
        setStateInternal(STATE_SETTLING);
        if (this.mViewDragHelper.smoothSlideViewTo(child, child.getLeft(), top)) {
            ViewCompat.postOnAnimation(child, new SettleRunnable(child, state));
        }
    }

    void dispatchOnSlide(int top) {
        View bottomSheet = (View) this.mViewRef.get();
        if (bottomSheet != null && this.mCallback != null) {
            if (top > this.mMaxOffset) {
                this.mCallback.onSlide(bottomSheet, ((float) (this.mMaxOffset - top)) / ((float) (this.mParentHeight - this.mMaxOffset)));
            } else {
                this.mCallback.onSlide(bottomSheet, ((float) (this.mMaxOffset - top)) / ((float) (this.mMaxOffset - this.mMinOffset)));
            }
        }
    }

    @VisibleForTesting
    int getPeekHeightMin() {
        return this.mPeekHeightMin;
    }

    public static <V extends View> BottomSheetBehavior<V> from(V view) {
        LayoutParams params = view.getLayoutParams();
        if (params instanceof CoordinatorLayout.LayoutParams) {
            Behavior behavior = ((CoordinatorLayout.LayoutParams) params).getBehavior();
            if (behavior instanceof BottomSheetBehavior) {
                return (BottomSheetBehavior) behavior;
            }
            throw new IllegalArgumentException("The view is not associated with BottomSheetBehavior");
        }
        throw new IllegalArgumentException("The view is not a child of CoordinatorLayout");
    }
}
