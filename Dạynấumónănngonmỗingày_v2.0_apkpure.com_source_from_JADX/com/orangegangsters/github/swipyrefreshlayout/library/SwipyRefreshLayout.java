package com.orangegangsters.github.swipyrefreshlayout.library;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build.VERSION;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.AbsListView;
import com.google.android.gms.common.ConnectionResult;
import rx.internal.operators.OnSubscribeConcatMap;

public class SwipyRefreshLayout extends ViewGroup {
    private static final int ALPHA_ANIMATION_DURATION = 300;
    private static final int ANIMATE_TO_START_DURATION = 200;
    private static final int ANIMATE_TO_TRIGGER_DURATION = 200;
    private static final int CIRCLE_BG_LIGHT = -328966;
    private static final int CIRCLE_DIAMETER = 40;
    private static final int CIRCLE_DIAMETER_LARGE = 56;
    private static final float DECELERATE_INTERPOLATION_FACTOR = 2.0f;
    public static final int DEFAULT = 1;
    private static final int DEFAULT_CIRCLE_TARGET = 64;
    private static final float DRAG_RATE = 0.5f;
    private static final int INVALID_POINTER = -1;
    public static final int LARGE = 0;
    private static final int[] LAYOUT_ATTRS;
    private static final String LOG_TAG;
    private static final int MAX_ALPHA = 255;
    private static final float MAX_PROGRESS_ANGLE = 0.8f;
    private static final float MAX_SWIPE_DISTANCE_FACTOR = 0.6f;
    private static final int REFRESH_TRIGGER_DISTANCE = 120;
    private static final int SCALE_DOWN_DURATION = 150;
    private static final int STARTING_PROGRESS_ALPHA = 76;
    public static final String TAG = "SwipyRefreshLayout";
    private int mActivePointerId;
    private Animation mAlphaMaxAnimation;
    private Animation mAlphaStartAnimation;
    private final Animation mAnimateToCorrectPosition;
    private final Animation mAnimateToStartPosition;
    private boolean mBothDirection;
    private int mCircleHeight;
    private CircleImageView mCircleView;
    private int mCircleViewIndex;
    private int mCircleWidth;
    private int mCurrentTargetOffsetTop;
    private final DecelerateInterpolator mDecelerateInterpolator;
    private SwipyRefreshLayoutDirection mDirection;
    protected int mFrom;
    private float mInitialDownY;
    private float mInitialMotionY;
    private boolean mIsBeingDragged;
    private OnRefreshListener mListener;
    private int mMediumAnimationDuration;
    private boolean mNotify;
    private boolean mOriginalOffsetCalculated;
    protected int mOriginalOffsetTop;
    private MaterialProgressDrawable mProgress;
    private AnimationListener mRefreshListener;
    private boolean mRefreshing;
    private boolean mReturningToStart;
    private boolean mScale;
    private Animation mScaleAnimation;
    private Animation mScaleDownAnimation;
    private Animation mScaleDownToStartAnimation;
    private float mSpinnerFinalOffset;
    private float mStartingScale;
    private View mTarget;
    private float mTotalDragDistance;
    private int mTouchSlop;
    private boolean mUsingCustomStart;

    /* renamed from: com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout.1 */
    class C07701 implements AnimationListener {
        C07701() {
        }

        public void onAnimationStart(Animation animation) {
        }

        public void onAnimationRepeat(Animation animation) {
        }

        public void onAnimationEnd(Animation animation) {
            if (SwipyRefreshLayout.this.mRefreshing) {
                SwipyRefreshLayout.this.mProgress.setAlpha(SwipyRefreshLayout.MAX_ALPHA);
                SwipyRefreshLayout.this.mProgress.start();
                if (SwipyRefreshLayout.this.mNotify && SwipyRefreshLayout.this.mListener != null) {
                    SwipyRefreshLayout.this.mListener.onRefresh(SwipyRefreshLayout.this.mDirection);
                }
            } else {
                SwipyRefreshLayout.this.mProgress.stop();
                SwipyRefreshLayout.this.mCircleView.setVisibility(8);
                SwipyRefreshLayout.this.setColorViewAlpha(SwipyRefreshLayout.MAX_ALPHA);
                if (SwipyRefreshLayout.this.mScale) {
                    SwipyRefreshLayout.this.setAnimationProgress(0.0f);
                } else {
                    SwipyRefreshLayout.this.setTargetOffsetTopAndBottom(SwipyRefreshLayout.this.mOriginalOffsetTop - SwipyRefreshLayout.this.mCurrentTargetOffsetTop, true);
                }
            }
            SwipyRefreshLayout.this.mCurrentTargetOffsetTop = SwipyRefreshLayout.this.mCircleView.getTop();
        }
    }

    /* renamed from: com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout.2 */
    class C07712 extends Animation {
        C07712() {
        }

        public void applyTransformation(float interpolatedTime, Transformation t) {
            SwipyRefreshLayout.this.setAnimationProgress(interpolatedTime);
        }
    }

    /* renamed from: com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout.3 */
    class C07723 extends Animation {
        C07723() {
        }

        public void applyTransformation(float interpolatedTime, Transformation t) {
            SwipyRefreshLayout.this.setAnimationProgress(1.0f - interpolatedTime);
        }
    }

    /* renamed from: com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout.4 */
    class C07734 extends Animation {
        final /* synthetic */ int val$endingAlpha;
        final /* synthetic */ int val$startingAlpha;

        C07734(int i, int i2) {
            this.val$startingAlpha = i;
            this.val$endingAlpha = i2;
        }

        public void applyTransformation(float interpolatedTime, Transformation t) {
            SwipyRefreshLayout.this.mProgress.setAlpha((int) (((float) this.val$startingAlpha) + (((float) (this.val$endingAlpha - this.val$startingAlpha)) * interpolatedTime)));
        }
    }

    /* renamed from: com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout.5 */
    class C07745 implements AnimationListener {
        C07745() {
        }

        public void onAnimationStart(Animation animation) {
        }

        public void onAnimationEnd(Animation animation) {
            if (!SwipyRefreshLayout.this.mScale) {
                SwipyRefreshLayout.this.startScaleDownAnimation(null);
            }
        }

        public void onAnimationRepeat(Animation animation) {
        }
    }

    /* renamed from: com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout.6 */
    class C07756 extends Animation {
        C07756() {
        }

        public void applyTransformation(float interpolatedTime, Transformation t) {
            int endTarget;
            if (!SwipyRefreshLayout.this.mUsingCustomStart) {
                switch (C07789.f16xa188b3d0[SwipyRefreshLayout.this.mDirection.ordinal()]) {
                    case SwipyRefreshLayout.DEFAULT /*1*/:
                        endTarget = SwipyRefreshLayout.this.getMeasuredHeight() - ((int) SwipyRefreshLayout.this.mSpinnerFinalOffset);
                        break;
                    default:
                        endTarget = (int) (SwipyRefreshLayout.this.mSpinnerFinalOffset - ((float) Math.abs(SwipyRefreshLayout.this.mOriginalOffsetTop)));
                        break;
                }
            }
            endTarget = (int) SwipyRefreshLayout.this.mSpinnerFinalOffset;
            SwipyRefreshLayout.this.setTargetOffsetTopAndBottom((SwipyRefreshLayout.this.mFrom + ((int) (((float) (endTarget - SwipyRefreshLayout.this.mFrom)) * interpolatedTime))) - SwipyRefreshLayout.this.mCircleView.getTop(), false);
        }
    }

    /* renamed from: com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout.7 */
    class C07767 extends Animation {
        C07767() {
        }

        public void applyTransformation(float interpolatedTime, Transformation t) {
            SwipyRefreshLayout.this.moveToStart(interpolatedTime);
        }
    }

    /* renamed from: com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout.8 */
    class C07778 extends Animation {
        C07778() {
        }

        public void applyTransformation(float interpolatedTime, Transformation t) {
            SwipyRefreshLayout.this.setAnimationProgress(SwipyRefreshLayout.this.mStartingScale + ((-SwipyRefreshLayout.this.mStartingScale) * interpolatedTime));
            SwipyRefreshLayout.this.moveToStart(interpolatedTime);
        }
    }

    /* renamed from: com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout.9 */
    static /* synthetic */ class C07789 {
        static final /* synthetic */ int[] f16xa188b3d0;

        static {
            f16xa188b3d0 = new int[SwipyRefreshLayoutDirection.values().length];
            try {
                f16xa188b3d0[SwipyRefreshLayoutDirection.BOTTOM.ordinal()] = SwipyRefreshLayout.DEFAULT;
            } catch (NoSuchFieldError e) {
            }
            try {
                f16xa188b3d0[SwipyRefreshLayoutDirection.TOP.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    public interface OnRefreshListener {
        void onRefresh(SwipyRefreshLayoutDirection swipyRefreshLayoutDirection);
    }

    static {
        LOG_TAG = SwipyRefreshLayout.class.getSimpleName();
        int[] iArr = new int[DEFAULT];
        iArr[LARGE] = 16842766;
        LAYOUT_ATTRS = iArr;
    }

    private void setColorViewAlpha(int targetAlpha) {
        this.mCircleView.getBackground().setAlpha(targetAlpha);
        this.mProgress.setAlpha(targetAlpha);
    }

    public void setSize(int size) {
        if (size == 0 || size == DEFAULT) {
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            int i;
            if (size == 0) {
                i = (int) (56.0f * metrics.density);
                this.mCircleWidth = i;
                this.mCircleHeight = i;
            } else {
                i = (int) (40.0f * metrics.density);
                this.mCircleWidth = i;
                this.mCircleHeight = i;
            }
            this.mCircleView.setImageDrawable(null);
            this.mProgress.updateSizes(size);
            this.mCircleView.setImageDrawable(this.mProgress);
        }
    }

    public SwipyRefreshLayout(Context context) {
        this(context, null);
    }

    public SwipyRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mRefreshing = false;
        this.mTotalDragDistance = -1.0f;
        this.mOriginalOffsetCalculated = false;
        this.mActivePointerId = INVALID_POINTER;
        this.mCircleViewIndex = INVALID_POINTER;
        this.mRefreshListener = new C07701();
        this.mAnimateToCorrectPosition = new C07756();
        this.mAnimateToStartPosition = new C07767();
        this.mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        this.mMediumAnimationDuration = getResources().getInteger(17694721);
        setWillNotDraw(false);
        this.mDecelerateInterpolator = new DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR);
        TypedArray a = context.obtainStyledAttributes(attrs, LAYOUT_ATTRS);
        setEnabled(a.getBoolean(LARGE, true));
        a.recycle();
        TypedArray a2 = context.obtainStyledAttributes(attrs, C0769R.styleable.SwipyRefreshLayout);
        SwipyRefreshLayoutDirection direction = SwipyRefreshLayoutDirection.getFromInt(a2.getInt(C0769R.styleable.SwipyRefreshLayout_srl_direction, LARGE));
        if (direction != SwipyRefreshLayoutDirection.BOTH) {
            this.mDirection = direction;
            this.mBothDirection = false;
        } else {
            this.mDirection = SwipyRefreshLayoutDirection.TOP;
            this.mBothDirection = true;
        }
        a2.recycle();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        this.mCircleWidth = (int) (metrics.density * 40.0f);
        this.mCircleHeight = (int) (metrics.density * 40.0f);
        createProgressView();
        ViewCompat.setChildrenDrawingOrderEnabled(this, true);
        this.mSpinnerFinalOffset = 64.0f * metrics.density;
    }

    protected int getChildDrawingOrder(int childCount, int i) {
        if (this.mCircleViewIndex < 0) {
            return i;
        }
        if (i == childCount + INVALID_POINTER) {
            return this.mCircleViewIndex;
        }
        if (i >= this.mCircleViewIndex) {
            return i + DEFAULT;
        }
        return i;
    }

    private void createProgressView() {
        this.mCircleView = new CircleImageView(getContext(), CIRCLE_BG_LIGHT, 20.0f);
        this.mProgress = new MaterialProgressDrawable(getContext(), this);
        this.mProgress.setBackgroundColor(CIRCLE_BG_LIGHT);
        this.mCircleView.setImageDrawable(this.mProgress);
        this.mCircleView.setVisibility(8);
        addView(this.mCircleView);
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        this.mListener = listener;
    }

    private boolean isAlphaUsedForScale() {
        return VERSION.SDK_INT < 11;
    }

    public void setRefreshing(boolean refreshing) {
        if (!refreshing || this.mRefreshing == refreshing) {
            setRefreshing(refreshing, false);
            return;
        }
        int endTarget;
        this.mRefreshing = refreshing;
        if (!this.mUsingCustomStart) {
            switch (C07789.f16xa188b3d0[this.mDirection.ordinal()]) {
                case DEFAULT /*1*/:
                    endTarget = getMeasuredHeight() - ((int) this.mSpinnerFinalOffset);
                    break;
                default:
                    endTarget = (int) (this.mSpinnerFinalOffset - ((float) Math.abs(this.mOriginalOffsetTop)));
                    break;
            }
        }
        endTarget = (int) this.mSpinnerFinalOffset;
        setTargetOffsetTopAndBottom(endTarget - this.mCurrentTargetOffsetTop, true);
        this.mNotify = false;
        startScaleUpAnimation(this.mRefreshListener);
    }

    private void startScaleUpAnimation(AnimationListener listener) {
        this.mCircleView.setVisibility(LARGE);
        if (VERSION.SDK_INT >= 11) {
            this.mProgress.setAlpha(MAX_ALPHA);
        }
        this.mScaleAnimation = new C07712();
        this.mScaleAnimation.setDuration((long) this.mMediumAnimationDuration);
        if (listener != null) {
            this.mCircleView.setAnimationListener(listener);
        }
        this.mCircleView.clearAnimation();
        this.mCircleView.startAnimation(this.mScaleAnimation);
    }

    private void setAnimationProgress(float progress) {
        if (isAlphaUsedForScale()) {
            setColorViewAlpha((int) (255.0f * progress));
            return;
        }
        ViewCompat.setScaleX(this.mCircleView, progress);
        ViewCompat.setScaleY(this.mCircleView, progress);
    }

    private void setRefreshing(boolean refreshing, boolean notify) {
        if (this.mRefreshing != refreshing) {
            this.mNotify = notify;
            ensureTarget();
            this.mRefreshing = refreshing;
            if (this.mRefreshing) {
                animateOffsetToCorrectPosition(this.mCurrentTargetOffsetTop, this.mRefreshListener);
            } else {
                startScaleDownAnimation(this.mRefreshListener);
            }
        }
    }

    private void startScaleDownAnimation(AnimationListener listener) {
        this.mScaleDownAnimation = new C07723();
        this.mScaleDownAnimation.setDuration(150);
        this.mCircleView.setAnimationListener(listener);
        this.mCircleView.clearAnimation();
        this.mCircleView.startAnimation(this.mScaleDownAnimation);
    }

    private void startProgressAlphaStartAnimation() {
        this.mAlphaStartAnimation = startAlphaAnimation(this.mProgress.getAlpha(), STARTING_PROGRESS_ALPHA);
    }

    private void startProgressAlphaMaxAnimation() {
        this.mAlphaMaxAnimation = startAlphaAnimation(this.mProgress.getAlpha(), MAX_ALPHA);
    }

    private Animation startAlphaAnimation(int startingAlpha, int endingAlpha) {
        if (this.mScale && isAlphaUsedForScale()) {
            return null;
        }
        Animation alpha = new C07734(startingAlpha, endingAlpha);
        alpha.setDuration(300);
        this.mCircleView.setAnimationListener(null);
        this.mCircleView.clearAnimation();
        this.mCircleView.startAnimation(alpha);
        return alpha;
    }

    public void setProgressBackgroundColor(int colorRes) {
        this.mCircleView.setBackgroundColor(colorRes);
        this.mProgress.setBackgroundColor(getResources().getColor(colorRes));
    }

    @Deprecated
    public void setColorScheme(int... colors) {
        setColorSchemeResources(colors);
    }

    public void setColorSchemeResources(int... colorResIds) {
        Resources res = getResources();
        int[] colorRes = new int[colorResIds.length];
        for (int i = LARGE; i < colorResIds.length; i += DEFAULT) {
            colorRes[i] = res.getColor(colorResIds[i]);
        }
        setColorSchemeColors(colorRes);
    }

    public void setColorSchemeColors(int... colors) {
        ensureTarget();
        this.mProgress.setColorSchemeColors(colors);
    }

    public boolean isRefreshing() {
        return this.mRefreshing;
    }

    private void ensureTarget() {
        if (this.mTarget == null) {
            for (int i = LARGE; i < getChildCount(); i += DEFAULT) {
                View child = getChildAt(i);
                if (!child.equals(this.mCircleView)) {
                    this.mTarget = child;
                    break;
                }
            }
        }
        if (this.mTotalDragDistance == -1.0f && getParent() != null && ((View) getParent()).getHeight() > 0) {
            this.mTotalDragDistance = (float) ((int) Math.min(((float) ((View) getParent()).getHeight()) * MAX_SWIPE_DISTANCE_FACTOR, 120.0f * getResources().getDisplayMetrics().density));
        }
    }

    public void setDistanceToTriggerSync(int distance) {
        this.mTotalDragDistance = (float) distance;
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        if (getChildCount() != 0) {
            if (this.mTarget == null) {
                ensureTarget();
            }
            if (this.mTarget != null) {
                View child = this.mTarget;
                int childLeft = getPaddingLeft();
                int childTop = getPaddingTop();
                child.layout(childLeft, childTop, childLeft + ((width - getPaddingLeft()) - getPaddingRight()), childTop + ((height - getPaddingTop()) - getPaddingBottom()));
                int circleWidth = this.mCircleView.getMeasuredWidth();
                this.mCircleView.layout((width / 2) - (circleWidth / 2), this.mCurrentTargetOffsetTop, (width / 2) + (circleWidth / 2), this.mCurrentTargetOffsetTop + this.mCircleView.getMeasuredHeight());
            }
        }
    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (this.mTarget == null) {
            ensureTarget();
        }
        if (this.mTarget != null) {
            this.mTarget.measure(MeasureSpec.makeMeasureSpec((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight(), 1073741824), MeasureSpec.makeMeasureSpec((getMeasuredHeight() - getPaddingTop()) - getPaddingBottom(), 1073741824));
            this.mCircleView.measure(MeasureSpec.makeMeasureSpec(this.mCircleWidth, 1073741824), MeasureSpec.makeMeasureSpec(this.mCircleHeight, 1073741824));
            if (!(this.mUsingCustomStart || this.mOriginalOffsetCalculated)) {
                this.mOriginalOffsetCalculated = true;
                int measuredHeight;
                switch (C07789.f16xa188b3d0[this.mDirection.ordinal()]) {
                    case DEFAULT /*1*/:
                        measuredHeight = getMeasuredHeight();
                        this.mOriginalOffsetTop = measuredHeight;
                        this.mCurrentTargetOffsetTop = measuredHeight;
                        break;
                    default:
                        measuredHeight = -this.mCircleView.getMeasuredHeight();
                        this.mOriginalOffsetTop = measuredHeight;
                        this.mCurrentTargetOffsetTop = measuredHeight;
                        break;
                }
            }
            this.mCircleViewIndex = INVALID_POINTER;
            for (int index = LARGE; index < getChildCount(); index += DEFAULT) {
                if (getChildAt(index) == this.mCircleView) {
                    this.mCircleViewIndex = index;
                    return;
                }
            }
        }
    }

    public boolean canChildScrollUp() {
        if (VERSION.SDK_INT >= 14) {
            return ViewCompat.canScrollVertically(this.mTarget, INVALID_POINTER);
        }
        if (this.mTarget instanceof AbsListView) {
            AbsListView absListView = this.mTarget;
            if (absListView.getChildCount() <= 0 || (absListView.getFirstVisiblePosition() <= 0 && absListView.getChildAt(LARGE).getTop() >= absListView.getPaddingTop())) {
                return false;
            }
            return true;
        } else if (this.mTarget.getScrollY() <= 0) {
            return false;
        } else {
            return true;
        }
    }

    public boolean canChildScrollDown() {
        if (VERSION.SDK_INT >= 14) {
            return ViewCompat.canScrollVertically(this.mTarget, DEFAULT);
        }
        if (!(this.mTarget instanceof AbsListView)) {
            return true;
        }
        AbsListView absListView = this.mTarget;
        try {
            if (absListView.getCount() <= 0 || absListView.getLastVisiblePosition() + DEFAULT != absListView.getCount() || absListView.getChildAt(absListView.getLastVisiblePosition() - absListView.getFirstVisiblePosition()).getBottom() == absListView.getPaddingBottom()) {
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        ensureTarget();
        int action = MotionEventCompat.getActionMasked(ev);
        if (this.mReturningToStart && action == 0) {
            this.mReturningToStart = false;
        }
        switch (C07789.f16xa188b3d0[this.mDirection.ordinal()]) {
            case DEFAULT /*1*/:
                if (!isEnabled() || this.mReturningToStart) {
                    return false;
                }
                if ((!this.mBothDirection && canChildScrollDown()) || this.mRefreshing) {
                    return false;
                }
                break;
            default:
                if (!isEnabled() || this.mReturningToStart) {
                    return false;
                }
                if ((!this.mBothDirection && canChildScrollUp()) || this.mRefreshing) {
                    return false;
                }
                break;
        }
        switch (action) {
            case LARGE /*0*/:
                setTargetOffsetTopAndBottom(this.mOriginalOffsetTop - this.mCircleView.getTop(), true);
                this.mActivePointerId = MotionEventCompat.getPointerId(ev, LARGE);
                this.mIsBeingDragged = false;
                float initialDownY = getMotionEventY(ev, this.mActivePointerId);
                if (initialDownY != -1.0f) {
                    this.mInitialDownY = initialDownY;
                    break;
                }
                return false;
            case DEFAULT /*1*/:
            case ConnectionResult.SERVICE_DISABLED /*3*/:
                this.mIsBeingDragged = false;
                this.mActivePointerId = INVALID_POINTER;
                break;
            case OnSubscribeConcatMap.END /*2*/:
                break;
            case ConnectionResult.RESOLUTION_REQUIRED /*6*/:
                onSecondaryPointerUp(ev);
                break;
        }
        if (this.mActivePointerId == INVALID_POINTER) {
            return false;
        }
        float y = getMotionEventY(ev, this.mActivePointerId);
        if (y == -1.0f) {
            return false;
        }
        float yDiff;
        if (this.mBothDirection) {
            if (y > this.mInitialDownY) {
                setRawDirection(SwipyRefreshLayoutDirection.TOP);
            } else if (y < this.mInitialDownY) {
                setRawDirection(SwipyRefreshLayoutDirection.BOTTOM);
            }
            if ((this.mDirection == SwipyRefreshLayoutDirection.BOTTOM && canChildScrollDown()) || (this.mDirection == SwipyRefreshLayoutDirection.TOP && canChildScrollUp())) {
                this.mInitialDownY = y;
                return false;
            }
        }
        switch (C07789.f16xa188b3d0[this.mDirection.ordinal()]) {
            case DEFAULT /*1*/:
                yDiff = this.mInitialDownY - y;
                break;
            default:
                yDiff = y - this.mInitialDownY;
                break;
        }
        if (yDiff > ((float) this.mTouchSlop) && !this.mIsBeingDragged) {
            switch (C07789.f16xa188b3d0[this.mDirection.ordinal()]) {
                case DEFAULT /*1*/:
                    this.mInitialMotionY = this.mInitialDownY - ((float) this.mTouchSlop);
                    break;
                default:
                    this.mInitialMotionY = this.mInitialDownY + ((float) this.mTouchSlop);
                    break;
            }
            this.mIsBeingDragged = true;
            this.mProgress.setAlpha(STARTING_PROGRESS_ALPHA);
        }
        return this.mIsBeingDragged;
    }

    private float getMotionEventY(MotionEvent ev, int activePointerId) {
        int index = MotionEventCompat.findPointerIndex(ev, activePointerId);
        if (index < 0) {
            return -1.0f;
        }
        return MotionEventCompat.getY(ev, index);
    }

    public void requestDisallowInterceptTouchEvent(boolean b) {
    }

    private boolean isAnimationRunning(Animation animation) {
        return (animation == null || !animation.hasStarted() || animation.hasEnded()) ? false : true;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r29) {
        /*
        r28 = this;
        r4 = android.support.v4.view.MotionEventCompat.getActionMasked(r29);	 Catch:{ Exception -> 0x0084 }
        r0 = r28;
        r0 = r0.mReturningToStart;	 Catch:{ Exception -> 0x0084 }
        r22 = r0;
        if (r22 == 0) goto L_0x0016;
    L_0x000c:
        if (r4 != 0) goto L_0x0016;
    L_0x000e:
        r22 = 0;
        r0 = r22;
        r1 = r28;
        r1.mReturningToStart = r0;	 Catch:{ Exception -> 0x0084 }
    L_0x0016:
        r22 = com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout.C07789.f16xa188b3d0;	 Catch:{ Exception -> 0x0084 }
        r0 = r28;
        r0 = r0.mDirection;	 Catch:{ Exception -> 0x0084 }
        r23 = r0;
        r23 = r23.ordinal();	 Catch:{ Exception -> 0x0084 }
        r22 = r22[r23];	 Catch:{ Exception -> 0x0084 }
        switch(r22) {
            case 1: goto L_0x0046;
            default: goto L_0x0027;
        };	 Catch:{ Exception -> 0x0084 }
    L_0x0027:
        r22 = r28.isEnabled();	 Catch:{ Exception -> 0x0084 }
        if (r22 == 0) goto L_0x0043;
    L_0x002d:
        r0 = r28;
        r0 = r0.mReturningToStart;	 Catch:{ Exception -> 0x0084 }
        r22 = r0;
        if (r22 != 0) goto L_0x0043;
    L_0x0035:
        r22 = r28.canChildScrollUp();	 Catch:{ Exception -> 0x0084 }
        if (r22 != 0) goto L_0x0043;
    L_0x003b:
        r0 = r28;
        r0 = r0.mRefreshing;	 Catch:{ Exception -> 0x0084 }
        r22 = r0;
        if (r22 == 0) goto L_0x0065;
    L_0x0043:
        r22 = 0;
    L_0x0045:
        return r22;
    L_0x0046:
        r22 = r28.isEnabled();	 Catch:{ Exception -> 0x0084 }
        if (r22 == 0) goto L_0x0062;
    L_0x004c:
        r0 = r28;
        r0 = r0.mReturningToStart;	 Catch:{ Exception -> 0x0084 }
        r22 = r0;
        if (r22 != 0) goto L_0x0062;
    L_0x0054:
        r22 = r28.canChildScrollDown();	 Catch:{ Exception -> 0x0084 }
        if (r22 != 0) goto L_0x0062;
    L_0x005a:
        r0 = r28;
        r0 = r0.mRefreshing;	 Catch:{ Exception -> 0x0084 }
        r22 = r0;
        if (r22 == 0) goto L_0x0065;
    L_0x0062:
        r22 = 0;
        goto L_0x0045;
    L_0x0065:
        switch(r4) {
            case 0: goto L_0x006b;
            case 1: goto L_0x02e4;
            case 2: goto L_0x00a2;
            case 3: goto L_0x02e4;
            case 4: goto L_0x0068;
            case 5: goto L_0x02cd;
            case 6: goto L_0x02df;
            default: goto L_0x0068;
        };	 Catch:{ Exception -> 0x0084 }
    L_0x0068:
        r22 = 1;
        goto L_0x0045;
    L_0x006b:
        r22 = 0;
        r0 = r29;
        r1 = r22;
        r22 = android.support.v4.view.MotionEventCompat.getPointerId(r0, r1);	 Catch:{ Exception -> 0x0084 }
        r0 = r22;
        r1 = r28;
        r1.mActivePointerId = r0;	 Catch:{ Exception -> 0x0084 }
        r22 = 0;
        r0 = r22;
        r1 = r28;
        r1.mIsBeingDragged = r0;	 Catch:{ Exception -> 0x0084 }
        goto L_0x0068;
    L_0x0084:
        r7 = move-exception;
        r22 = "SwipyRefreshLayout";
        r23 = new java.lang.StringBuilder;
        r23.<init>();
        r24 = "An exception occured during SwipyRefreshLayout onTouchEvent ";
        r23 = r23.append(r24);
        r24 = r7.toString();
        r23 = r23.append(r24);
        r23 = r23.toString();
        android.util.Log.e(r22, r23);
        goto L_0x0068;
    L_0x00a2:
        r0 = r28;
        r0 = r0.mActivePointerId;	 Catch:{ Exception -> 0x0084 }
        r22 = r0;
        r0 = r29;
        r1 = r22;
        r14 = android.support.v4.view.MotionEventCompat.findPointerIndex(r0, r1);	 Catch:{ Exception -> 0x0084 }
        if (r14 >= 0) goto L_0x00b5;
    L_0x00b2:
        r22 = 0;
        goto L_0x0045;
    L_0x00b5:
        r0 = r29;
        r21 = android.support.v4.view.MotionEventCompat.getY(r0, r14);	 Catch:{ Exception -> 0x0084 }
        r22 = com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout.C07789.f16xa188b3d0;	 Catch:{ Exception -> 0x0084 }
        r0 = r28;
        r0 = r0.mDirection;	 Catch:{ Exception -> 0x0084 }
        r23 = r0;
        r23 = r23.ordinal();	 Catch:{ Exception -> 0x0084 }
        r22 = r22[r23];	 Catch:{ Exception -> 0x0084 }
        switch(r22) {
            case 1: goto L_0x00fd;
            default: goto L_0x00cc;
        };	 Catch:{ Exception -> 0x0084 }
    L_0x00cc:
        r0 = r28;
        r0 = r0.mInitialMotionY;	 Catch:{ Exception -> 0x0084 }
        r22 = r0;
        r22 = r21 - r22;
        r23 = 1056964608; // 0x3f000000 float:0.5 double:5.222099017E-315;
        r13 = r22 * r23;
    L_0x00d8:
        r0 = r28;
        r0 = r0.mIsBeingDragged;	 Catch:{ Exception -> 0x0084 }
        r22 = r0;
        if (r22 == 0) goto L_0x0068;
    L_0x00e0:
        r0 = r28;
        r0 = r0.mProgress;	 Catch:{ Exception -> 0x0084 }
        r22 = r0;
        r23 = 1;
        r22.showArrow(r23);	 Catch:{ Exception -> 0x0084 }
        r0 = r28;
        r0 = r0.mTotalDragDistance;	 Catch:{ Exception -> 0x0084 }
        r22 = r0;
        r12 = r13 / r22;
        r22 = 0;
        r22 = (r12 > r22 ? 1 : (r12 == r22 ? 0 : -1));
        if (r22 >= 0) goto L_0x010a;
    L_0x00f9:
        r22 = 0;
        goto L_0x0045;
    L_0x00fd:
        r0 = r28;
        r0 = r0.mInitialMotionY;	 Catch:{ Exception -> 0x0084 }
        r22 = r0;
        r22 = r22 - r21;
        r23 = 1056964608; // 0x3f000000 float:0.5 double:5.222099017E-315;
        r13 = r22 * r23;
        goto L_0x00d8;
    L_0x010a:
        r22 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r23 = java.lang.Math.abs(r12);	 Catch:{ Exception -> 0x0084 }
        r6 = java.lang.Math.min(r22, r23);	 Catch:{ Exception -> 0x0084 }
        r0 = (double) r6;	 Catch:{ Exception -> 0x0084 }
        r22 = r0;
        r24 = 4600877379321698714; // 0x3fd999999999999a float:-1.5881868E-23 double:0.4;
        r22 = r22 - r24;
        r24 = 0;
        r22 = java.lang.Math.max(r22, r24);	 Catch:{ Exception -> 0x0084 }
        r0 = r22;
        r0 = (float) r0;	 Catch:{ Exception -> 0x0084 }
        r22 = r0;
        r23 = 1084227584; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r22 = r22 * r23;
        r23 = 1077936128; // 0x40400000 float:3.0 double:5.325712093E-315;
        r5 = r22 / r23;
        r22 = java.lang.Math.abs(r13);	 Catch:{ Exception -> 0x0084 }
        r0 = r28;
        r0 = r0.mTotalDragDistance;	 Catch:{ Exception -> 0x0084 }
        r23 = r0;
        r9 = r22 - r23;
        r0 = r28;
        r0 = r0.mUsingCustomStart;	 Catch:{ Exception -> 0x0084 }
        r22 = r0;
        if (r22 == 0) goto L_0x028c;
    L_0x0145:
        r0 = r28;
        r0 = r0.mSpinnerFinalOffset;	 Catch:{ Exception -> 0x0084 }
        r22 = r0;
        r0 = r28;
        r0 = r0.mOriginalOffsetTop;	 Catch:{ Exception -> 0x0084 }
        r23 = r0;
        r0 = r23;
        r0 = (float) r0;	 Catch:{ Exception -> 0x0084 }
        r23 = r0;
        r16 = r22 - r23;
    L_0x0158:
        r22 = 0;
        r23 = 1073741824; // 0x40000000 float:2.0 double:5.304989477E-315;
        r23 = r23 * r16;
        r0 = r23;
        r23 = java.lang.Math.min(r9, r0);	 Catch:{ Exception -> 0x0084 }
        r23 = r23 / r16;
        r20 = java.lang.Math.max(r22, r23);	 Catch:{ Exception -> 0x0084 }
        r22 = 1082130432; // 0x40800000 float:4.0 double:5.34643471E-315;
        r22 = r20 / r22;
        r0 = r22;
        r0 = (double) r0;	 Catch:{ Exception -> 0x0084 }
        r22 = r0;
        r24 = 1082130432; // 0x40800000 float:4.0 double:5.34643471E-315;
        r24 = r20 / r24;
        r0 = r24;
        r0 = (double) r0;	 Catch:{ Exception -> 0x0084 }
        r24 = r0;
        r26 = 4611686018427387904; // 0x4000000000000000 float:0.0 double:2.0;
        r24 = java.lang.Math.pow(r24, r26);	 Catch:{ Exception -> 0x0084 }
        r22 = r22 - r24;
        r0 = r22;
        r0 = (float) r0;	 Catch:{ Exception -> 0x0084 }
        r22 = r0;
        r23 = 1073741824; // 0x40000000 float:2.0 double:5.304989477E-315;
        r19 = r22 * r23;
        r22 = r16 * r19;
        r23 = 1073741824; // 0x40000000 float:2.0 double:5.304989477E-315;
        r8 = r22 * r23;
        r0 = r28;
        r0 = r0.mDirection;	 Catch:{ Exception -> 0x0084 }
        r22 = r0;
        r23 = com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection.TOP;	 Catch:{ Exception -> 0x0084 }
        r0 = r22;
        r1 = r23;
        if (r0 != r1) goto L_0x0294;
    L_0x01a1:
        r0 = r28;
        r0 = r0.mOriginalOffsetTop;	 Catch:{ Exception -> 0x0084 }
        r22 = r0;
        r23 = r16 * r6;
        r23 = r23 + r8;
        r0 = r23;
        r0 = (int) r0;	 Catch:{ Exception -> 0x0084 }
        r23 = r0;
        r18 = r22 + r23;
    L_0x01b2:
        r0 = r28;
        r0 = r0.mCircleView;	 Catch:{ Exception -> 0x0084 }
        r22 = r0;
        r22 = r22.getVisibility();	 Catch:{ Exception -> 0x0084 }
        if (r22 == 0) goto L_0x01c9;
    L_0x01be:
        r0 = r28;
        r0 = r0.mCircleView;	 Catch:{ Exception -> 0x0084 }
        r22 = r0;
        r23 = 0;
        r22.setVisibility(r23);	 Catch:{ Exception -> 0x0084 }
    L_0x01c9:
        r0 = r28;
        r0 = r0.mScale;	 Catch:{ Exception -> 0x0084 }
        r22 = r0;
        if (r22 != 0) goto L_0x01e7;
    L_0x01d1:
        r0 = r28;
        r0 = r0.mCircleView;	 Catch:{ Exception -> 0x0084 }
        r22 = r0;
        r23 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        android.support.v4.view.ViewCompat.setScaleX(r22, r23);	 Catch:{ Exception -> 0x0084 }
        r0 = r28;
        r0 = r0.mCircleView;	 Catch:{ Exception -> 0x0084 }
        r22 = r0;
        r23 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        android.support.v4.view.ViewCompat.setScaleY(r22, r23);	 Catch:{ Exception -> 0x0084 }
    L_0x01e7:
        r0 = r28;
        r0 = r0.mTotalDragDistance;	 Catch:{ Exception -> 0x0084 }
        r22 = r0;
        r22 = (r13 > r22 ? 1 : (r13 == r22 ? 0 : -1));
        if (r22 >= 0) goto L_0x02a7;
    L_0x01f1:
        r0 = r28;
        r0 = r0.mScale;	 Catch:{ Exception -> 0x0084 }
        r22 = r0;
        if (r22 == 0) goto L_0x0208;
    L_0x01f9:
        r0 = r28;
        r0 = r0.mTotalDragDistance;	 Catch:{ Exception -> 0x0084 }
        r22 = r0;
        r22 = r13 / r22;
        r0 = r28;
        r1 = r22;
        r0.setAnimationProgress(r1);	 Catch:{ Exception -> 0x0084 }
    L_0x0208:
        r0 = r28;
        r0 = r0.mProgress;	 Catch:{ Exception -> 0x0084 }
        r22 = r0;
        r22 = r22.getAlpha();	 Catch:{ Exception -> 0x0084 }
        r23 = 76;
        r0 = r22;
        r1 = r23;
        if (r0 <= r1) goto L_0x022d;
    L_0x021a:
        r0 = r28;
        r0 = r0.mAlphaStartAnimation;	 Catch:{ Exception -> 0x0084 }
        r22 = r0;
        r0 = r28;
        r1 = r22;
        r22 = r0.isAnimationRunning(r1);	 Catch:{ Exception -> 0x0084 }
        if (r22 != 0) goto L_0x022d;
    L_0x022a:
        r28.startProgressAlphaStartAnimation();	 Catch:{ Exception -> 0x0084 }
    L_0x022d:
        r22 = 1061997773; // 0x3f4ccccd float:0.8 double:5.246966156E-315;
        r17 = r5 * r22;
        r0 = r28;
        r0 = r0.mProgress;	 Catch:{ Exception -> 0x0084 }
        r22 = r0;
        r23 = 0;
        r24 = 1061997773; // 0x3f4ccccd float:0.8 double:5.246966156E-315;
        r0 = r24;
        r1 = r17;
        r24 = java.lang.Math.min(r0, r1);	 Catch:{ Exception -> 0x0084 }
        r22.setStartEndTrim(r23, r24);	 Catch:{ Exception -> 0x0084 }
        r0 = r28;
        r0 = r0.mProgress;	 Catch:{ Exception -> 0x0084 }
        r22 = r0;
        r23 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r0 = r23;
        r23 = java.lang.Math.min(r0, r5);	 Catch:{ Exception -> 0x0084 }
        r22.setArrowScale(r23);	 Catch:{ Exception -> 0x0084 }
    L_0x0259:
        r22 = -1098907648; // 0xffffffffbe800000 float:-0.25 double:NaN;
        r23 = 1053609165; // 0x3ecccccd float:0.4 double:5.205520926E-315;
        r23 = r23 * r5;
        r22 = r22 + r23;
        r23 = 1073741824; // 0x40000000 float:2.0 double:5.304989477E-315;
        r23 = r23 * r19;
        r22 = r22 + r23;
        r23 = 1056964608; // 0x3f000000 float:0.5 double:5.222099017E-315;
        r15 = r22 * r23;
        r0 = r28;
        r0 = r0.mProgress;	 Catch:{ Exception -> 0x0084 }
        r22 = r0;
        r0 = r22;
        r0.setProgressRotation(r15);	 Catch:{ Exception -> 0x0084 }
        r0 = r28;
        r0 = r0.mCurrentTargetOffsetTop;	 Catch:{ Exception -> 0x0084 }
        r22 = r0;
        r22 = r18 - r22;
        r23 = 1;
        r0 = r28;
        r1 = r22;
        r2 = r23;
        r0.setTargetOffsetTopAndBottom(r1, r2);	 Catch:{ Exception -> 0x0084 }
        goto L_0x0068;
    L_0x028c:
        r0 = r28;
        r0 = r0.mSpinnerFinalOffset;	 Catch:{ Exception -> 0x0084 }
        r16 = r0;
        goto L_0x0158;
    L_0x0294:
        r0 = r28;
        r0 = r0.mOriginalOffsetTop;	 Catch:{ Exception -> 0x0084 }
        r22 = r0;
        r23 = r16 * r6;
        r23 = r23 + r8;
        r0 = r23;
        r0 = (int) r0;	 Catch:{ Exception -> 0x0084 }
        r23 = r0;
        r18 = r22 - r23;
        goto L_0x01b2;
    L_0x02a7:
        r0 = r28;
        r0 = r0.mProgress;	 Catch:{ Exception -> 0x0084 }
        r22 = r0;
        r22 = r22.getAlpha();	 Catch:{ Exception -> 0x0084 }
        r23 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        r0 = r22;
        r1 = r23;
        if (r0 >= r1) goto L_0x0259;
    L_0x02b9:
        r0 = r28;
        r0 = r0.mAlphaMaxAnimation;	 Catch:{ Exception -> 0x0084 }
        r22 = r0;
        r0 = r28;
        r1 = r22;
        r22 = r0.isAnimationRunning(r1);	 Catch:{ Exception -> 0x0084 }
        if (r22 != 0) goto L_0x0259;
    L_0x02c9:
        r28.startProgressAlphaMaxAnimation();	 Catch:{ Exception -> 0x0084 }
        goto L_0x0259;
    L_0x02cd:
        r10 = android.support.v4.view.MotionEventCompat.getActionIndex(r29);	 Catch:{ Exception -> 0x0084 }
        r0 = r29;
        r22 = android.support.v4.view.MotionEventCompat.getPointerId(r0, r10);	 Catch:{ Exception -> 0x0084 }
        r0 = r22;
        r1 = r28;
        r1.mActivePointerId = r0;	 Catch:{ Exception -> 0x0084 }
        goto L_0x0068;
    L_0x02df:
        r28.onSecondaryPointerUp(r29);	 Catch:{ Exception -> 0x0084 }
        goto L_0x0068;
    L_0x02e4:
        r0 = r28;
        r0 = r0.mActivePointerId;	 Catch:{ Exception -> 0x0084 }
        r22 = r0;
        r23 = -1;
        r0 = r22;
        r1 = r23;
        if (r0 != r1) goto L_0x02fc;
    L_0x02f2:
        r22 = 1;
        r0 = r22;
        if (r4 != r0) goto L_0x02f8;
    L_0x02f8:
        r22 = 0;
        goto L_0x0045;
    L_0x02fc:
        r0 = r28;
        r0 = r0.mActivePointerId;	 Catch:{ Exception -> 0x0084 }
        r22 = r0;
        r0 = r29;
        r1 = r22;
        r14 = android.support.v4.view.MotionEventCompat.findPointerIndex(r0, r1);	 Catch:{ Exception -> 0x0084 }
        r0 = r29;
        r21 = android.support.v4.view.MotionEventCompat.getY(r0, r14);	 Catch:{ Exception -> 0x0084 }
        r22 = com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout.C07789.f16xa188b3d0;	 Catch:{ Exception -> 0x0084 }
        r0 = r28;
        r0 = r0.mDirection;	 Catch:{ Exception -> 0x0084 }
        r23 = r0;
        r23 = r23.ordinal();	 Catch:{ Exception -> 0x0084 }
        r22 = r22[r23];	 Catch:{ Exception -> 0x0084 }
        switch(r22) {
            case 1: goto L_0x0358;
            default: goto L_0x0321;
        };	 Catch:{ Exception -> 0x0084 }
    L_0x0321:
        r0 = r28;
        r0 = r0.mInitialMotionY;	 Catch:{ Exception -> 0x0084 }
        r22 = r0;
        r22 = r21 - r22;
        r23 = 1056964608; // 0x3f000000 float:0.5 double:5.222099017E-315;
        r13 = r22 * r23;
    L_0x032d:
        r22 = 0;
        r0 = r22;
        r1 = r28;
        r1.mIsBeingDragged = r0;	 Catch:{ Exception -> 0x0084 }
        r0 = r28;
        r0 = r0.mTotalDragDistance;	 Catch:{ Exception -> 0x0084 }
        r22 = r0;
        r22 = (r13 > r22 ? 1 : (r13 == r22 ? 0 : -1));
        if (r22 <= 0) goto L_0x0365;
    L_0x033f:
        r22 = 1;
        r23 = 1;
        r0 = r28;
        r1 = r22;
        r2 = r23;
        r0.setRefreshing(r1, r2);	 Catch:{ Exception -> 0x0084 }
    L_0x034c:
        r22 = -1;
        r0 = r22;
        r1 = r28;
        r1.mActivePointerId = r0;	 Catch:{ Exception -> 0x0084 }
        r22 = 0;
        goto L_0x0045;
    L_0x0358:
        r0 = r28;
        r0 = r0.mInitialMotionY;	 Catch:{ Exception -> 0x0084 }
        r22 = r0;
        r22 = r22 - r21;
        r23 = 1056964608; // 0x3f000000 float:0.5 double:5.222099017E-315;
        r13 = r22 * r23;
        goto L_0x032d;
    L_0x0365:
        r22 = 0;
        r0 = r22;
        r1 = r28;
        r1.mRefreshing = r0;	 Catch:{ Exception -> 0x0084 }
        r0 = r28;
        r0 = r0.mProgress;	 Catch:{ Exception -> 0x0084 }
        r22 = r0;
        r23 = 0;
        r24 = 0;
        r22.setStartEndTrim(r23, r24);	 Catch:{ Exception -> 0x0084 }
        r11 = 0;
        r0 = r28;
        r0 = r0.mScale;	 Catch:{ Exception -> 0x0084 }
        r22 = r0;
        if (r22 != 0) goto L_0x038a;
    L_0x0383:
        r11 = new com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout$5;	 Catch:{ Exception -> 0x0084 }
        r0 = r28;
        r11.<init>();	 Catch:{ Exception -> 0x0084 }
    L_0x038a:
        r0 = r28;
        r0 = r0.mCurrentTargetOffsetTop;	 Catch:{ Exception -> 0x0084 }
        r22 = r0;
        r0 = r28;
        r1 = r22;
        r0.animateOffsetToStartPosition(r1, r11);	 Catch:{ Exception -> 0x0084 }
        r0 = r28;
        r0 = r0.mProgress;	 Catch:{ Exception -> 0x0084 }
        r22 = r0;
        r23 = 0;
        r22.showArrow(r23);	 Catch:{ Exception -> 0x0084 }
        goto L_0x034c;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout.onTouchEvent(android.view.MotionEvent):boolean");
    }

    private void animateOffsetToCorrectPosition(int from, AnimationListener listener) {
        this.mFrom = from;
        this.mAnimateToCorrectPosition.reset();
        this.mAnimateToCorrectPosition.setDuration(200);
        this.mAnimateToCorrectPosition.setInterpolator(this.mDecelerateInterpolator);
        if (listener != null) {
            this.mCircleView.setAnimationListener(listener);
        }
        this.mCircleView.clearAnimation();
        this.mCircleView.startAnimation(this.mAnimateToCorrectPosition);
    }

    private void animateOffsetToStartPosition(int from, AnimationListener listener) {
        if (this.mScale) {
            startScaleDownReturnToStartAnimation(from, listener);
            return;
        }
        this.mFrom = from;
        this.mAnimateToStartPosition.reset();
        this.mAnimateToStartPosition.setDuration(200);
        this.mAnimateToStartPosition.setInterpolator(this.mDecelerateInterpolator);
        if (listener != null) {
            this.mCircleView.setAnimationListener(listener);
        }
        this.mCircleView.clearAnimation();
        this.mCircleView.startAnimation(this.mAnimateToStartPosition);
    }

    private void moveToStart(float interpolatedTime) {
        setTargetOffsetTopAndBottom((this.mFrom + ((int) (((float) (this.mOriginalOffsetTop - this.mFrom)) * interpolatedTime))) - this.mCircleView.getTop(), false);
    }

    private void startScaleDownReturnToStartAnimation(int from, AnimationListener listener) {
        this.mFrom = from;
        if (isAlphaUsedForScale()) {
            this.mStartingScale = (float) this.mProgress.getAlpha();
        } else {
            this.mStartingScale = ViewCompat.getScaleX(this.mCircleView);
        }
        this.mScaleDownToStartAnimation = new C07778();
        this.mScaleDownToStartAnimation.setDuration(150);
        if (listener != null) {
            this.mCircleView.setAnimationListener(listener);
        }
        this.mCircleView.clearAnimation();
        this.mCircleView.startAnimation(this.mScaleDownToStartAnimation);
    }

    private void setTargetOffsetTopAndBottom(int offset, boolean requiresUpdate) {
        this.mCircleView.bringToFront();
        this.mCircleView.offsetTopAndBottom(offset);
        this.mCurrentTargetOffsetTop = this.mCircleView.getTop();
        if (requiresUpdate && VERSION.SDK_INT < 11) {
            invalidate();
        }
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        int pointerIndex = MotionEventCompat.getActionIndex(ev);
        if (MotionEventCompat.getPointerId(ev, pointerIndex) == this.mActivePointerId) {
            this.mActivePointerId = MotionEventCompat.getPointerId(ev, pointerIndex == 0 ? DEFAULT : LARGE);
        }
    }

    public SwipyRefreshLayoutDirection getDirection() {
        return this.mBothDirection ? SwipyRefreshLayoutDirection.BOTH : this.mDirection;
    }

    public void setDirection(SwipyRefreshLayoutDirection direction) {
        if (direction == SwipyRefreshLayoutDirection.BOTH) {
            this.mBothDirection = true;
        } else {
            this.mBothDirection = false;
            this.mDirection = direction;
        }
        int measuredHeight;
        switch (C07789.f16xa188b3d0[this.mDirection.ordinal()]) {
            case DEFAULT /*1*/:
                measuredHeight = getMeasuredHeight();
                this.mOriginalOffsetTop = measuredHeight;
                this.mCurrentTargetOffsetTop = measuredHeight;
            default:
                measuredHeight = -this.mCircleView.getMeasuredHeight();
                this.mOriginalOffsetTop = measuredHeight;
                this.mCurrentTargetOffsetTop = measuredHeight;
        }
    }

    private void setRawDirection(SwipyRefreshLayoutDirection direction) {
        if (this.mDirection != direction) {
            this.mDirection = direction;
            int measuredHeight;
            switch (C07789.f16xa188b3d0[this.mDirection.ordinal()]) {
                case DEFAULT /*1*/:
                    measuredHeight = getMeasuredHeight();
                    this.mOriginalOffsetTop = measuredHeight;
                    this.mCurrentTargetOffsetTop = measuredHeight;
                default:
                    measuredHeight = -this.mCircleView.getMeasuredHeight();
                    this.mOriginalOffsetTop = measuredHeight;
                    this.mCurrentTargetOffsetTop = measuredHeight;
            }
        }
    }
}
