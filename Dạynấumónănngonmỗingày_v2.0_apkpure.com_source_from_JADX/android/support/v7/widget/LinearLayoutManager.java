package android.support.v7.widget;

import android.content.Context;
import android.graphics.PointF;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.v4.media.TransportMediator;
import android.support.v4.view.accessibility.AccessibilityEventCompat;
import android.support.v4.view.accessibility.AccessibilityRecordCompat;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.RecyclerView.LayoutManager.LayoutPrefetchRegistry;
import android.support.v7.widget.RecyclerView.LayoutManager.Properties;
import android.support.v7.widget.RecyclerView.LayoutParams;
import android.support.v7.widget.RecyclerView.Recycler;
import android.support.v7.widget.RecyclerView.SmoothScroller.ScrollVectorProvider;
import android.support.v7.widget.RecyclerView.State;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.helper.ItemTouchHelper.ViewDropHandler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import com.google.android.gms.common.ConnectionResult;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import java.util.List;
import me.wangyuwei.loadingview.C0801R;
import rx.internal.operators.OnSubscribeConcatMap;

public class LinearLayoutManager extends LayoutManager implements ViewDropHandler, ScrollVectorProvider {
    static final boolean DEBUG = false;
    public static final int HORIZONTAL = 0;
    public static final int INVALID_OFFSET = Integer.MIN_VALUE;
    private static final float MAX_SCROLL_FACTOR = 0.33333334f;
    private static final String TAG = "LinearLayoutManager";
    public static final int VERTICAL = 1;
    final AnchorInfo mAnchorInfo;
    private int mInitialItemPrefetchCount;
    private boolean mLastStackFromEnd;
    private final LayoutChunkResult mLayoutChunkResult;
    private LayoutState mLayoutState;
    int mOrientation;
    OrientationHelper mOrientationHelper;
    SavedState mPendingSavedState;
    int mPendingScrollPosition;
    int mPendingScrollPositionOffset;
    private boolean mRecycleChildrenOnDetach;
    private boolean mReverseLayout;
    boolean mShouldReverseLayout;
    private boolean mSmoothScrollbarEnabled;
    private boolean mStackFromEnd;

    class AnchorInfo {
        int mCoordinate;
        boolean mLayoutFromEnd;
        int mPosition;
        boolean mValid;

        AnchorInfo() {
            reset();
        }

        void reset() {
            this.mPosition = -1;
            this.mCoordinate = LinearLayoutManager.INVALID_OFFSET;
            this.mLayoutFromEnd = LinearLayoutManager.DEBUG;
            this.mValid = LinearLayoutManager.DEBUG;
        }

        void assignCoordinateFromPadding() {
            int endAfterPadding;
            if (this.mLayoutFromEnd) {
                endAfterPadding = LinearLayoutManager.this.mOrientationHelper.getEndAfterPadding();
            } else {
                endAfterPadding = LinearLayoutManager.this.mOrientationHelper.getStartAfterPadding();
            }
            this.mCoordinate = endAfterPadding;
        }

        public String toString() {
            return "AnchorInfo{mPosition=" + this.mPosition + ", mCoordinate=" + this.mCoordinate + ", mLayoutFromEnd=" + this.mLayoutFromEnd + ", mValid=" + this.mValid + '}';
        }

        boolean isViewValidAsAnchor(View child, State state) {
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            return (lp.isItemRemoved() || lp.getViewLayoutPosition() < 0 || lp.getViewLayoutPosition() >= state.getItemCount()) ? LinearLayoutManager.DEBUG : true;
        }

        public void assignFromViewAndKeepVisibleRect(View child) {
            int spaceChange = LinearLayoutManager.this.mOrientationHelper.getTotalSpaceChange();
            if (spaceChange >= 0) {
                assignFromView(child);
                return;
            }
            this.mPosition = LinearLayoutManager.this.getPosition(child);
            int previousEndMargin;
            int startMargin;
            if (this.mLayoutFromEnd) {
                previousEndMargin = (LinearLayoutManager.this.mOrientationHelper.getEndAfterPadding() - spaceChange) - LinearLayoutManager.this.mOrientationHelper.getDecoratedEnd(child);
                this.mCoordinate = LinearLayoutManager.this.mOrientationHelper.getEndAfterPadding() - previousEndMargin;
                if (previousEndMargin > 0) {
                    int childSize = LinearLayoutManager.this.mOrientationHelper.getDecoratedMeasurement(child);
                    int estimatedChildStart = this.mCoordinate - childSize;
                    int layoutStart = LinearLayoutManager.this.mOrientationHelper.getStartAfterPadding();
                    startMargin = estimatedChildStart - (layoutStart + Math.min(LinearLayoutManager.this.mOrientationHelper.getDecoratedStart(child) - layoutStart, LinearLayoutManager.HORIZONTAL));
                    if (startMargin < 0) {
                        this.mCoordinate += Math.min(previousEndMargin, -startMargin);
                        return;
                    }
                    return;
                }
                return;
            }
            int childStart = LinearLayoutManager.this.mOrientationHelper.getDecoratedStart(child);
            startMargin = childStart - LinearLayoutManager.this.mOrientationHelper.getStartAfterPadding();
            this.mCoordinate = childStart;
            if (startMargin > 0) {
                int estimatedEnd = childStart + LinearLayoutManager.this.mOrientationHelper.getDecoratedMeasurement(child);
                previousEndMargin = (LinearLayoutManager.this.mOrientationHelper.getEndAfterPadding() - spaceChange) - LinearLayoutManager.this.mOrientationHelper.getDecoratedEnd(child);
                int endMargin = (LinearLayoutManager.this.mOrientationHelper.getEndAfterPadding() - Math.min(LinearLayoutManager.HORIZONTAL, previousEndMargin)) - estimatedEnd;
                if (endMargin < 0) {
                    this.mCoordinate -= Math.min(startMargin, -endMargin);
                }
            }
        }

        public void assignFromView(View child) {
            if (this.mLayoutFromEnd) {
                this.mCoordinate = LinearLayoutManager.this.mOrientationHelper.getDecoratedEnd(child) + LinearLayoutManager.this.mOrientationHelper.getTotalSpaceChange();
            } else {
                this.mCoordinate = LinearLayoutManager.this.mOrientationHelper.getDecoratedStart(child);
            }
            this.mPosition = LinearLayoutManager.this.getPosition(child);
        }
    }

    protected static class LayoutChunkResult {
        public int mConsumed;
        public boolean mFinished;
        public boolean mFocusable;
        public boolean mIgnoreConsumed;

        protected LayoutChunkResult() {
        }

        void resetInternal() {
            this.mConsumed = LinearLayoutManager.HORIZONTAL;
            this.mFinished = LinearLayoutManager.DEBUG;
            this.mIgnoreConsumed = LinearLayoutManager.DEBUG;
            this.mFocusable = LinearLayoutManager.DEBUG;
        }
    }

    static class LayoutState {
        static final int INVALID_LAYOUT = Integer.MIN_VALUE;
        static final int ITEM_DIRECTION_HEAD = -1;
        static final int ITEM_DIRECTION_TAIL = 1;
        static final int LAYOUT_END = 1;
        static final int LAYOUT_START = -1;
        static final int SCROLLING_OFFSET_NaN = Integer.MIN_VALUE;
        static final String TAG = "LLM#LayoutState";
        int mAvailable;
        int mCurrentPosition;
        int mExtra;
        boolean mInfinite;
        boolean mIsPreLayout;
        int mItemDirection;
        int mLastScrollDelta;
        int mLayoutDirection;
        int mOffset;
        boolean mRecycle;
        List<ViewHolder> mScrapList;
        int mScrollingOffset;

        LayoutState() {
            this.mRecycle = true;
            this.mExtra = LinearLayoutManager.HORIZONTAL;
            this.mIsPreLayout = LinearLayoutManager.DEBUG;
            this.mScrapList = null;
        }

        boolean hasMore(State state) {
            return (this.mCurrentPosition < 0 || this.mCurrentPosition >= state.getItemCount()) ? LinearLayoutManager.DEBUG : true;
        }

        View next(Recycler recycler) {
            if (this.mScrapList != null) {
                return nextViewFromScrapList();
            }
            View view = recycler.getViewForPosition(this.mCurrentPosition);
            this.mCurrentPosition += this.mItemDirection;
            return view;
        }

        private View nextViewFromScrapList() {
            int size = this.mScrapList.size();
            for (int i = LinearLayoutManager.HORIZONTAL; i < size; i += LAYOUT_END) {
                View view = ((ViewHolder) this.mScrapList.get(i)).itemView;
                LayoutParams lp = (LayoutParams) view.getLayoutParams();
                if (!lp.isItemRemoved() && this.mCurrentPosition == lp.getViewLayoutPosition()) {
                    assignPositionFromScrapList(view);
                    return view;
                }
            }
            return null;
        }

        public void assignPositionFromScrapList() {
            assignPositionFromScrapList(null);
        }

        public void assignPositionFromScrapList(View ignore) {
            View closest = nextViewInLimitedList(ignore);
            if (closest == null) {
                this.mCurrentPosition = LAYOUT_START;
            } else {
                this.mCurrentPosition = ((LayoutParams) closest.getLayoutParams()).getViewLayoutPosition();
            }
        }

        public View nextViewInLimitedList(View ignore) {
            int size = this.mScrapList.size();
            View closest = null;
            int closestDistance = UrlImageViewHelper.CACHE_DURATION_INFINITE;
            for (int i = LinearLayoutManager.HORIZONTAL; i < size; i += LAYOUT_END) {
                View view = ((ViewHolder) this.mScrapList.get(i)).itemView;
                LayoutParams lp = (LayoutParams) view.getLayoutParams();
                if (!(view == ignore || lp.isItemRemoved())) {
                    int distance = (lp.getViewLayoutPosition() - this.mCurrentPosition) * this.mItemDirection;
                    if (distance >= 0 && distance < closestDistance) {
                        closest = view;
                        closestDistance = distance;
                        if (distance == 0) {
                            break;
                        }
                    }
                }
            }
            return closest;
        }

        void log() {
            Log.d(TAG, "avail:" + this.mAvailable + ", ind:" + this.mCurrentPosition + ", dir:" + this.mItemDirection + ", offset:" + this.mOffset + ", layoutDir:" + this.mLayoutDirection);
        }
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    public static class SavedState implements Parcelable {
        public static final Creator<SavedState> CREATOR;
        boolean mAnchorLayoutFromEnd;
        int mAnchorOffset;
        int mAnchorPosition;

        /* renamed from: android.support.v7.widget.LinearLayoutManager.SavedState.1 */
        static class C02131 implements Creator<SavedState> {
            C02131() {
            }

            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        }

        SavedState(Parcel in) {
            boolean z = true;
            this.mAnchorPosition = in.readInt();
            this.mAnchorOffset = in.readInt();
            if (in.readInt() != LinearLayoutManager.VERTICAL) {
                z = LinearLayoutManager.DEBUG;
            }
            this.mAnchorLayoutFromEnd = z;
        }

        public SavedState(SavedState other) {
            this.mAnchorPosition = other.mAnchorPosition;
            this.mAnchorOffset = other.mAnchorOffset;
            this.mAnchorLayoutFromEnd = other.mAnchorLayoutFromEnd;
        }

        boolean hasValidAnchor() {
            return this.mAnchorPosition >= 0 ? true : LinearLayoutManager.DEBUG;
        }

        void invalidateAnchor() {
            this.mAnchorPosition = -1;
        }

        public int describeContents() {
            return LinearLayoutManager.HORIZONTAL;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.mAnchorPosition);
            dest.writeInt(this.mAnchorOffset);
            dest.writeInt(this.mAnchorLayoutFromEnd ? LinearLayoutManager.VERTICAL : LinearLayoutManager.HORIZONTAL);
        }

        static {
            CREATOR = new C02131();
        }
    }

    public LinearLayoutManager(Context context) {
        this(context, VERTICAL, DEBUG);
    }

    public LinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        this.mReverseLayout = DEBUG;
        this.mShouldReverseLayout = DEBUG;
        this.mStackFromEnd = DEBUG;
        this.mSmoothScrollbarEnabled = true;
        this.mPendingScrollPosition = -1;
        this.mPendingScrollPositionOffset = INVALID_OFFSET;
        this.mPendingSavedState = null;
        this.mAnchorInfo = new AnchorInfo();
        this.mLayoutChunkResult = new LayoutChunkResult();
        this.mInitialItemPrefetchCount = 2;
        setOrientation(orientation);
        setReverseLayout(reverseLayout);
        setAutoMeasureEnabled(true);
    }

    public LinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this.mReverseLayout = DEBUG;
        this.mShouldReverseLayout = DEBUG;
        this.mStackFromEnd = DEBUG;
        this.mSmoothScrollbarEnabled = true;
        this.mPendingScrollPosition = -1;
        this.mPendingScrollPositionOffset = INVALID_OFFSET;
        this.mPendingSavedState = null;
        this.mAnchorInfo = new AnchorInfo();
        this.mLayoutChunkResult = new LayoutChunkResult();
        this.mInitialItemPrefetchCount = 2;
        Properties properties = LayoutManager.getProperties(context, attrs, defStyleAttr, defStyleRes);
        setOrientation(properties.orientation);
        setReverseLayout(properties.reverseLayout);
        setStackFromEnd(properties.stackFromEnd);
        setAutoMeasureEnabled(true);
    }

    public LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-2, -2);
    }

    public boolean getRecycleChildrenOnDetach() {
        return this.mRecycleChildrenOnDetach;
    }

    public void setRecycleChildrenOnDetach(boolean recycleChildrenOnDetach) {
        this.mRecycleChildrenOnDetach = recycleChildrenOnDetach;
    }

    public void onDetachedFromWindow(RecyclerView view, Recycler recycler) {
        super.onDetachedFromWindow(view, recycler);
        if (this.mRecycleChildrenOnDetach) {
            removeAndRecycleAllViews(recycler);
            recycler.clear();
        }
    }

    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        if (getChildCount() > 0) {
            AccessibilityRecordCompat record = AccessibilityEventCompat.asRecord(event);
            record.setFromIndex(findFirstVisibleItemPosition());
            record.setToIndex(findLastVisibleItemPosition());
        }
    }

    public Parcelable onSaveInstanceState() {
        if (this.mPendingSavedState != null) {
            return new SavedState(this.mPendingSavedState);
        }
        Parcelable state = new SavedState();
        if (getChildCount() > 0) {
            ensureLayoutState();
            boolean didLayoutFromEnd = this.mLastStackFromEnd ^ this.mShouldReverseLayout;
            state.mAnchorLayoutFromEnd = didLayoutFromEnd;
            View refChild;
            if (didLayoutFromEnd) {
                refChild = getChildClosestToEnd();
                state.mAnchorOffset = this.mOrientationHelper.getEndAfterPadding() - this.mOrientationHelper.getDecoratedEnd(refChild);
                state.mAnchorPosition = getPosition(refChild);
                return state;
            }
            refChild = getChildClosestToStart();
            state.mAnchorPosition = getPosition(refChild);
            state.mAnchorOffset = this.mOrientationHelper.getDecoratedStart(refChild) - this.mOrientationHelper.getStartAfterPadding();
            return state;
        }
        state.invalidateAnchor();
        return state;
    }

    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof SavedState) {
            this.mPendingSavedState = (SavedState) state;
            requestLayout();
        }
    }

    public boolean canScrollHorizontally() {
        return this.mOrientation == 0 ? true : DEBUG;
    }

    public boolean canScrollVertically() {
        return this.mOrientation == VERTICAL ? true : DEBUG;
    }

    public void setStackFromEnd(boolean stackFromEnd) {
        assertNotInLayoutOrScroll(null);
        if (this.mStackFromEnd != stackFromEnd) {
            this.mStackFromEnd = stackFromEnd;
            requestLayout();
        }
    }

    public boolean getStackFromEnd() {
        return this.mStackFromEnd;
    }

    public int getOrientation() {
        return this.mOrientation;
    }

    public void setOrientation(int orientation) {
        if (orientation == 0 || orientation == VERTICAL) {
            assertNotInLayoutOrScroll(null);
            if (orientation != this.mOrientation) {
                this.mOrientation = orientation;
                this.mOrientationHelper = null;
                requestLayout();
                return;
            }
            return;
        }
        throw new IllegalArgumentException("invalid orientation:" + orientation);
    }

    private void resolveShouldLayoutReverse() {
        boolean z = true;
        if (this.mOrientation == VERTICAL || !isLayoutRTL()) {
            this.mShouldReverseLayout = this.mReverseLayout;
            return;
        }
        if (this.mReverseLayout) {
            z = DEBUG;
        }
        this.mShouldReverseLayout = z;
    }

    public boolean getReverseLayout() {
        return this.mReverseLayout;
    }

    public void setReverseLayout(boolean reverseLayout) {
        assertNotInLayoutOrScroll(null);
        if (reverseLayout != this.mReverseLayout) {
            this.mReverseLayout = reverseLayout;
            requestLayout();
        }
    }

    public View findViewByPosition(int position) {
        int childCount = getChildCount();
        if (childCount == 0) {
            return null;
        }
        int viewPosition = position - getPosition(getChildAt(HORIZONTAL));
        if (viewPosition >= 0 && viewPosition < childCount) {
            View child = getChildAt(viewPosition);
            if (getPosition(child) == position) {
                return child;
            }
        }
        return super.findViewByPosition(position);
    }

    protected int getExtraLayoutSpace(State state) {
        if (state.hasTargetScrollPosition()) {
            return this.mOrientationHelper.getTotalSpace();
        }
        return HORIZONTAL;
    }

    public void smoothScrollToPosition(RecyclerView recyclerView, State state, int position) {
        LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(recyclerView.getContext());
        linearSmoothScroller.setTargetPosition(position);
        startSmoothScroll(linearSmoothScroller);
    }

    public PointF computeScrollVectorForPosition(int targetPosition) {
        boolean z = DEBUG;
        if (getChildCount() == 0) {
            return null;
        }
        int direction;
        if (targetPosition < getPosition(getChildAt(HORIZONTAL))) {
            z = VERTICAL;
        }
        if (z != this.mShouldReverseLayout) {
            direction = -1;
        } else {
            direction = VERTICAL;
        }
        if (this.mOrientation == 0) {
            return new PointF((float) direction, 0.0f);
        }
        return new PointF(0.0f, (float) direction);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onLayoutChildren(android.support.v7.widget.RecyclerView.Recycler r21, android.support.v7.widget.RecyclerView.State r22) {
        /*
        r20 = this;
        r0 = r20;
        r0 = r0.mPendingSavedState;
        r17 = r0;
        if (r17 != 0) goto L_0x0016;
    L_0x0008:
        r0 = r20;
        r0 = r0.mPendingScrollPosition;
        r17 = r0;
        r18 = -1;
        r0 = r17;
        r1 = r18;
        if (r0 == r1) goto L_0x0020;
    L_0x0016:
        r17 = r22.getItemCount();
        if (r17 != 0) goto L_0x0020;
    L_0x001c:
        r20.removeAndRecycleAllViews(r21);
    L_0x001f:
        return;
    L_0x0020:
        r0 = r20;
        r0 = r0.mPendingSavedState;
        r17 = r0;
        if (r17 == 0) goto L_0x0046;
    L_0x0028:
        r0 = r20;
        r0 = r0.mPendingSavedState;
        r17 = r0;
        r17 = r17.hasValidAnchor();
        if (r17 == 0) goto L_0x0046;
    L_0x0034:
        r0 = r20;
        r0 = r0.mPendingSavedState;
        r17 = r0;
        r0 = r17;
        r0 = r0.mAnchorPosition;
        r17 = r0;
        r0 = r17;
        r1 = r20;
        r1.mPendingScrollPosition = r0;
    L_0x0046:
        r20.ensureLayoutState();
        r0 = r20;
        r0 = r0.mLayoutState;
        r17 = r0;
        r18 = 0;
        r0 = r18;
        r1 = r17;
        r1.mRecycle = r0;
        r20.resolveShouldLayoutReverse();
        r0 = r20;
        r0 = r0.mAnchorInfo;
        r17 = r0;
        r0 = r17;
        r0 = r0.mValid;
        r17 = r0;
        if (r17 == 0) goto L_0x007e;
    L_0x0068:
        r0 = r20;
        r0 = r0.mPendingScrollPosition;
        r17 = r0;
        r18 = -1;
        r0 = r17;
        r1 = r18;
        if (r0 != r1) goto L_0x007e;
    L_0x0076:
        r0 = r20;
        r0 = r0.mPendingSavedState;
        r17 = r0;
        if (r17 == 0) goto L_0x00c0;
    L_0x007e:
        r0 = r20;
        r0 = r0.mAnchorInfo;
        r17 = r0;
        r17.reset();
        r0 = r20;
        r0 = r0.mAnchorInfo;
        r17 = r0;
        r0 = r20;
        r0 = r0.mShouldReverseLayout;
        r18 = r0;
        r0 = r20;
        r0 = r0.mStackFromEnd;
        r19 = r0;
        r18 = r18 ^ r19;
        r0 = r18;
        r1 = r17;
        r1.mLayoutFromEnd = r0;
        r0 = r20;
        r0 = r0.mAnchorInfo;
        r17 = r0;
        r0 = r20;
        r1 = r21;
        r2 = r22;
        r3 = r17;
        r0.updateAnchorInfoForLayout(r1, r2, r3);
        r0 = r20;
        r0 = r0.mAnchorInfo;
        r17 = r0;
        r18 = 1;
        r0 = r18;
        r1 = r17;
        r1.mValid = r0;
    L_0x00c0:
        r0 = r20;
        r1 = r22;
        r8 = r0.getExtraLayoutSpace(r1);
        r0 = r20;
        r0 = r0.mLayoutState;
        r17 = r0;
        r0 = r17;
        r0 = r0.mLastScrollDelta;
        r17 = r0;
        if (r17 < 0) goto L_0x02fb;
    L_0x00d6:
        r9 = r8;
        r10 = 0;
    L_0x00d8:
        r0 = r20;
        r0 = r0.mOrientationHelper;
        r17 = r0;
        r17 = r17.getStartAfterPadding();
        r10 = r10 + r17;
        r0 = r20;
        r0 = r0.mOrientationHelper;
        r17 = r0;
        r17 = r17.getEndPadding();
        r9 = r9 + r17;
        r17 = r22.isPreLayout();
        if (r17 == 0) goto L_0x014e;
    L_0x00f6:
        r0 = r20;
        r0 = r0.mPendingScrollPosition;
        r17 = r0;
        r18 = -1;
        r0 = r17;
        r1 = r18;
        if (r0 == r1) goto L_0x014e;
    L_0x0104:
        r0 = r20;
        r0 = r0.mPendingScrollPositionOffset;
        r17 = r0;
        r18 = -2147483648; // 0xffffffff80000000 float:-0.0 double:NaN;
        r0 = r17;
        r1 = r18;
        if (r0 == r1) goto L_0x014e;
    L_0x0112:
        r0 = r20;
        r0 = r0.mPendingScrollPosition;
        r17 = r0;
        r0 = r20;
        r1 = r17;
        r7 = r0.findViewByPosition(r1);
        if (r7 == 0) goto L_0x014e;
    L_0x0122:
        r0 = r20;
        r0 = r0.mShouldReverseLayout;
        r17 = r0;
        if (r17 == 0) goto L_0x02ff;
    L_0x012a:
        r0 = r20;
        r0 = r0.mOrientationHelper;
        r17 = r0;
        r17 = r17.getEndAfterPadding();
        r0 = r20;
        r0 = r0.mOrientationHelper;
        r18 = r0;
        r0 = r18;
        r18 = r0.getDecoratedEnd(r7);
        r5 = r17 - r18;
        r0 = r20;
        r0 = r0.mPendingScrollPositionOffset;
        r17 = r0;
        r16 = r5 - r17;
    L_0x014a:
        if (r16 <= 0) goto L_0x0321;
    L_0x014c:
        r10 = r10 + r16;
    L_0x014e:
        r0 = r20;
        r0 = r0.mAnchorInfo;
        r17 = r0;
        r0 = r17;
        r0 = r0.mLayoutFromEnd;
        r17 = r0;
        if (r17 == 0) goto L_0x0328;
    L_0x015c:
        r0 = r20;
        r0 = r0.mShouldReverseLayout;
        r17 = r0;
        if (r17 == 0) goto L_0x0325;
    L_0x0164:
        r12 = 1;
    L_0x0165:
        r0 = r20;
        r0 = r0.mAnchorInfo;
        r17 = r0;
        r0 = r20;
        r1 = r21;
        r2 = r22;
        r3 = r17;
        r0.onAnchorReady(r1, r2, r3, r12);
        r20.detachAndScrapAttachedViews(r21);
        r0 = r20;
        r0 = r0.mLayoutState;
        r17 = r0;
        r18 = r20.resolveIsInfinite();
        r0 = r18;
        r1 = r17;
        r1.mInfinite = r0;
        r0 = r20;
        r0 = r0.mLayoutState;
        r17 = r0;
        r18 = r22.isPreLayout();
        r0 = r18;
        r1 = r17;
        r1.mIsPreLayout = r0;
        r0 = r20;
        r0 = r0.mAnchorInfo;
        r17 = r0;
        r0 = r17;
        r0 = r0.mLayoutFromEnd;
        r17 = r0;
        if (r17 == 0) goto L_0x0335;
    L_0x01a7:
        r0 = r20;
        r0 = r0.mAnchorInfo;
        r17 = r0;
        r0 = r20;
        r1 = r17;
        r0.updateLayoutStateToFillStart(r1);
        r0 = r20;
        r0 = r0.mLayoutState;
        r17 = r0;
        r0 = r17;
        r0.mExtra = r10;
        r0 = r20;
        r0 = r0.mLayoutState;
        r17 = r0;
        r18 = 0;
        r0 = r20;
        r1 = r21;
        r2 = r17;
        r3 = r22;
        r4 = r18;
        r0.fill(r1, r2, r3, r4);
        r0 = r20;
        r0 = r0.mLayoutState;
        r17 = r0;
        r0 = r17;
        r15 = r0.mOffset;
        r0 = r20;
        r0 = r0.mLayoutState;
        r17 = r0;
        r0 = r17;
        r11 = r0.mCurrentPosition;
        r0 = r20;
        r0 = r0.mLayoutState;
        r17 = r0;
        r0 = r17;
        r0 = r0.mAvailable;
        r17 = r0;
        if (r17 <= 0) goto L_0x0203;
    L_0x01f5:
        r0 = r20;
        r0 = r0.mLayoutState;
        r17 = r0;
        r0 = r17;
        r0 = r0.mAvailable;
        r17 = r0;
        r9 = r9 + r17;
    L_0x0203:
        r0 = r20;
        r0 = r0.mAnchorInfo;
        r17 = r0;
        r0 = r20;
        r1 = r17;
        r0.updateLayoutStateToFillEnd(r1);
        r0 = r20;
        r0 = r0.mLayoutState;
        r17 = r0;
        r0 = r17;
        r0.mExtra = r9;
        r0 = r20;
        r0 = r0.mLayoutState;
        r17 = r0;
        r0 = r17;
        r0 = r0.mCurrentPosition;
        r18 = r0;
        r0 = r20;
        r0 = r0.mLayoutState;
        r19 = r0;
        r0 = r19;
        r0 = r0.mItemDirection;
        r19 = r0;
        r18 = r18 + r19;
        r0 = r18;
        r1 = r17;
        r1.mCurrentPosition = r0;
        r0 = r20;
        r0 = r0.mLayoutState;
        r17 = r0;
        r18 = 0;
        r0 = r20;
        r1 = r21;
        r2 = r17;
        r3 = r22;
        r4 = r18;
        r0.fill(r1, r2, r3, r4);
        r0 = r20;
        r0 = r0.mLayoutState;
        r17 = r0;
        r0 = r17;
        r6 = r0.mOffset;
        r0 = r20;
        r0 = r0.mLayoutState;
        r17 = r0;
        r0 = r17;
        r0 = r0.mAvailable;
        r17 = r0;
        if (r17 <= 0) goto L_0x029f;
    L_0x0267:
        r0 = r20;
        r0 = r0.mLayoutState;
        r17 = r0;
        r0 = r17;
        r10 = r0.mAvailable;
        r0 = r20;
        r0.updateLayoutStateToFillStart(r11, r15);
        r0 = r20;
        r0 = r0.mLayoutState;
        r17 = r0;
        r0 = r17;
        r0.mExtra = r10;
        r0 = r20;
        r0 = r0.mLayoutState;
        r17 = r0;
        r18 = 0;
        r0 = r20;
        r1 = r21;
        r2 = r17;
        r3 = r22;
        r4 = r18;
        r0.fill(r1, r2, r3, r4);
        r0 = r20;
        r0 = r0.mLayoutState;
        r17 = r0;
        r0 = r17;
        r15 = r0.mOffset;
    L_0x029f:
        r17 = r20.getChildCount();
        if (r17 <= 0) goto L_0x02d5;
    L_0x02a5:
        r0 = r20;
        r0 = r0.mShouldReverseLayout;
        r17 = r0;
        r0 = r20;
        r0 = r0.mStackFromEnd;
        r18 = r0;
        r17 = r17 ^ r18;
        if (r17 == 0) goto L_0x042f;
    L_0x02b5:
        r17 = 1;
        r0 = r20;
        r1 = r21;
        r2 = r22;
        r3 = r17;
        r13 = r0.fixLayoutEndGap(r6, r1, r2, r3);
        r15 = r15 + r13;
        r6 = r6 + r13;
        r17 = 0;
        r0 = r20;
        r1 = r21;
        r2 = r22;
        r3 = r17;
        r13 = r0.fixLayoutStartGap(r15, r1, r2, r3);
        r15 = r15 + r13;
        r6 = r6 + r13;
    L_0x02d5:
        r0 = r20;
        r1 = r21;
        r2 = r22;
        r0.layoutForPredictiveAnimations(r1, r2, r15, r6);
        r17 = r22.isPreLayout();
        if (r17 != 0) goto L_0x0451;
    L_0x02e4:
        r0 = r20;
        r0 = r0.mOrientationHelper;
        r17 = r0;
        r17.onLayoutComplete();
    L_0x02ed:
        r0 = r20;
        r0 = r0.mStackFromEnd;
        r17 = r0;
        r0 = r17;
        r1 = r20;
        r1.mLastStackFromEnd = r0;
        goto L_0x001f;
    L_0x02fb:
        r10 = r8;
        r9 = 0;
        goto L_0x00d8;
    L_0x02ff:
        r0 = r20;
        r0 = r0.mOrientationHelper;
        r17 = r0;
        r0 = r17;
        r17 = r0.getDecoratedStart(r7);
        r0 = r20;
        r0 = r0.mOrientationHelper;
        r18 = r0;
        r18 = r18.getStartAfterPadding();
        r5 = r17 - r18;
        r0 = r20;
        r0 = r0.mPendingScrollPositionOffset;
        r17 = r0;
        r16 = r17 - r5;
        goto L_0x014a;
    L_0x0321:
        r9 = r9 - r16;
        goto L_0x014e;
    L_0x0325:
        r12 = -1;
        goto L_0x0165;
    L_0x0328:
        r0 = r20;
        r0 = r0.mShouldReverseLayout;
        r17 = r0;
        if (r17 == 0) goto L_0x0333;
    L_0x0330:
        r12 = -1;
    L_0x0331:
        goto L_0x0165;
    L_0x0333:
        r12 = 1;
        goto L_0x0331;
    L_0x0335:
        r0 = r20;
        r0 = r0.mAnchorInfo;
        r17 = r0;
        r0 = r20;
        r1 = r17;
        r0.updateLayoutStateToFillEnd(r1);
        r0 = r20;
        r0 = r0.mLayoutState;
        r17 = r0;
        r0 = r17;
        r0.mExtra = r9;
        r0 = r20;
        r0 = r0.mLayoutState;
        r17 = r0;
        r18 = 0;
        r0 = r20;
        r1 = r21;
        r2 = r17;
        r3 = r22;
        r4 = r18;
        r0.fill(r1, r2, r3, r4);
        r0 = r20;
        r0 = r0.mLayoutState;
        r17 = r0;
        r0 = r17;
        r6 = r0.mOffset;
        r0 = r20;
        r0 = r0.mLayoutState;
        r17 = r0;
        r0 = r17;
        r14 = r0.mCurrentPosition;
        r0 = r20;
        r0 = r0.mLayoutState;
        r17 = r0;
        r0 = r17;
        r0 = r0.mAvailable;
        r17 = r0;
        if (r17 <= 0) goto L_0x0391;
    L_0x0383:
        r0 = r20;
        r0 = r0.mLayoutState;
        r17 = r0;
        r0 = r17;
        r0 = r0.mAvailable;
        r17 = r0;
        r10 = r10 + r17;
    L_0x0391:
        r0 = r20;
        r0 = r0.mAnchorInfo;
        r17 = r0;
        r0 = r20;
        r1 = r17;
        r0.updateLayoutStateToFillStart(r1);
        r0 = r20;
        r0 = r0.mLayoutState;
        r17 = r0;
        r0 = r17;
        r0.mExtra = r10;
        r0 = r20;
        r0 = r0.mLayoutState;
        r17 = r0;
        r0 = r17;
        r0 = r0.mCurrentPosition;
        r18 = r0;
        r0 = r20;
        r0 = r0.mLayoutState;
        r19 = r0;
        r0 = r19;
        r0 = r0.mItemDirection;
        r19 = r0;
        r18 = r18 + r19;
        r0 = r18;
        r1 = r17;
        r1.mCurrentPosition = r0;
        r0 = r20;
        r0 = r0.mLayoutState;
        r17 = r0;
        r18 = 0;
        r0 = r20;
        r1 = r21;
        r2 = r17;
        r3 = r22;
        r4 = r18;
        r0.fill(r1, r2, r3, r4);
        r0 = r20;
        r0 = r0.mLayoutState;
        r17 = r0;
        r0 = r17;
        r15 = r0.mOffset;
        r0 = r20;
        r0 = r0.mLayoutState;
        r17 = r0;
        r0 = r17;
        r0 = r0.mAvailable;
        r17 = r0;
        if (r17 <= 0) goto L_0x029f;
    L_0x03f5:
        r0 = r20;
        r0 = r0.mLayoutState;
        r17 = r0;
        r0 = r17;
        r9 = r0.mAvailable;
        r0 = r20;
        r0.updateLayoutStateToFillEnd(r14, r6);
        r0 = r20;
        r0 = r0.mLayoutState;
        r17 = r0;
        r0 = r17;
        r0.mExtra = r9;
        r0 = r20;
        r0 = r0.mLayoutState;
        r17 = r0;
        r18 = 0;
        r0 = r20;
        r1 = r21;
        r2 = r17;
        r3 = r22;
        r4 = r18;
        r0.fill(r1, r2, r3, r4);
        r0 = r20;
        r0 = r0.mLayoutState;
        r17 = r0;
        r0 = r17;
        r6 = r0.mOffset;
        goto L_0x029f;
    L_0x042f:
        r17 = 1;
        r0 = r20;
        r1 = r21;
        r2 = r22;
        r3 = r17;
        r13 = r0.fixLayoutStartGap(r15, r1, r2, r3);
        r15 = r15 + r13;
        r6 = r6 + r13;
        r17 = 0;
        r0 = r20;
        r1 = r21;
        r2 = r22;
        r3 = r17;
        r13 = r0.fixLayoutEndGap(r6, r1, r2, r3);
        r15 = r15 + r13;
        r6 = r6 + r13;
        goto L_0x02d5;
    L_0x0451:
        r0 = r20;
        r0 = r0.mAnchorInfo;
        r17 = r0;
        r17.reset();
        goto L_0x02ed;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v7.widget.LinearLayoutManager.onLayoutChildren(android.support.v7.widget.RecyclerView$Recycler, android.support.v7.widget.RecyclerView$State):void");
    }

    public void onLayoutCompleted(State state) {
        super.onLayoutCompleted(state);
        this.mPendingSavedState = null;
        this.mPendingScrollPosition = -1;
        this.mPendingScrollPositionOffset = INVALID_OFFSET;
        this.mAnchorInfo.reset();
    }

    void onAnchorReady(Recycler recycler, State state, AnchorInfo anchorInfo, int firstLayoutItemDirection) {
    }

    private void layoutForPredictiveAnimations(Recycler recycler, State state, int startOffset, int endOffset) {
        if (state.willRunPredictiveAnimations() && getChildCount() != 0 && !state.isPreLayout() && supportsPredictiveItemAnimations()) {
            int scrapExtraStart = HORIZONTAL;
            int scrapExtraEnd = HORIZONTAL;
            List<ViewHolder> scrapList = recycler.getScrapList();
            int scrapSize = scrapList.size();
            int firstChildPos = getPosition(getChildAt(HORIZONTAL));
            for (int i = HORIZONTAL; i < scrapSize; i += VERTICAL) {
                ViewHolder scrap = (ViewHolder) scrapList.get(i);
                if (!scrap.isRemoved()) {
                    if (((scrap.getLayoutPosition() < firstChildPos ? true : DEBUG) != this.mShouldReverseLayout ? -1 : VERTICAL) == -1) {
                        scrapExtraStart += this.mOrientationHelper.getDecoratedMeasurement(scrap.itemView);
                    } else {
                        scrapExtraEnd += this.mOrientationHelper.getDecoratedMeasurement(scrap.itemView);
                    }
                }
            }
            this.mLayoutState.mScrapList = scrapList;
            if (scrapExtraStart > 0) {
                updateLayoutStateToFillStart(getPosition(getChildClosestToStart()), startOffset);
                this.mLayoutState.mExtra = scrapExtraStart;
                this.mLayoutState.mAvailable = HORIZONTAL;
                this.mLayoutState.assignPositionFromScrapList();
                fill(recycler, this.mLayoutState, state, DEBUG);
            }
            if (scrapExtraEnd > 0) {
                updateLayoutStateToFillEnd(getPosition(getChildClosestToEnd()), endOffset);
                this.mLayoutState.mExtra = scrapExtraEnd;
                this.mLayoutState.mAvailable = HORIZONTAL;
                this.mLayoutState.assignPositionFromScrapList();
                fill(recycler, this.mLayoutState, state, DEBUG);
            }
            this.mLayoutState.mScrapList = null;
        }
    }

    private void updateAnchorInfoForLayout(Recycler recycler, State state, AnchorInfo anchorInfo) {
        if (!updateAnchorFromPendingData(state, anchorInfo) && !updateAnchorFromChildren(recycler, state, anchorInfo)) {
            anchorInfo.assignCoordinateFromPadding();
            anchorInfo.mPosition = this.mStackFromEnd ? state.getItemCount() - 1 : HORIZONTAL;
        }
    }

    private boolean updateAnchorFromChildren(Recycler recycler, State state, AnchorInfo anchorInfo) {
        if (getChildCount() == 0) {
            return DEBUG;
        }
        View focused = getFocusedChild();
        if (focused != null && anchorInfo.isViewValidAsAnchor(focused, state)) {
            anchorInfo.assignFromViewAndKeepVisibleRect(focused);
            return true;
        } else if (this.mLastStackFromEnd != this.mStackFromEnd) {
            return DEBUG;
        } else {
            View referenceChild;
            if (anchorInfo.mLayoutFromEnd) {
                referenceChild = findReferenceChildClosestToEnd(recycler, state);
            } else {
                referenceChild = findReferenceChildClosestToStart(recycler, state);
            }
            if (referenceChild == null) {
                return DEBUG;
            }
            anchorInfo.assignFromView(referenceChild);
            if (!state.isPreLayout() && supportsPredictiveItemAnimations()) {
                boolean notVisible;
                if (this.mOrientationHelper.getDecoratedStart(referenceChild) >= this.mOrientationHelper.getEndAfterPadding() || this.mOrientationHelper.getDecoratedEnd(referenceChild) < this.mOrientationHelper.getStartAfterPadding()) {
                    notVisible = true;
                } else {
                    notVisible = DEBUG;
                }
                if (notVisible) {
                    int endAfterPadding;
                    if (anchorInfo.mLayoutFromEnd) {
                        endAfterPadding = this.mOrientationHelper.getEndAfterPadding();
                    } else {
                        endAfterPadding = this.mOrientationHelper.getStartAfterPadding();
                    }
                    anchorInfo.mCoordinate = endAfterPadding;
                }
            }
            return true;
        }
    }

    private boolean updateAnchorFromPendingData(State state, AnchorInfo anchorInfo) {
        boolean z = DEBUG;
        if (state.isPreLayout() || this.mPendingScrollPosition == -1) {
            return DEBUG;
        }
        if (this.mPendingScrollPosition < 0 || this.mPendingScrollPosition >= state.getItemCount()) {
            this.mPendingScrollPosition = -1;
            this.mPendingScrollPositionOffset = INVALID_OFFSET;
            return DEBUG;
        }
        anchorInfo.mPosition = this.mPendingScrollPosition;
        if (this.mPendingSavedState != null && this.mPendingSavedState.hasValidAnchor()) {
            anchorInfo.mLayoutFromEnd = this.mPendingSavedState.mAnchorLayoutFromEnd;
            if (anchorInfo.mLayoutFromEnd) {
                anchorInfo.mCoordinate = this.mOrientationHelper.getEndAfterPadding() - this.mPendingSavedState.mAnchorOffset;
                return true;
            }
            anchorInfo.mCoordinate = this.mOrientationHelper.getStartAfterPadding() + this.mPendingSavedState.mAnchorOffset;
            return true;
        } else if (this.mPendingScrollPositionOffset == INVALID_OFFSET) {
            View child = findViewByPosition(this.mPendingScrollPosition);
            if (child == null) {
                if (getChildCount() > 0) {
                    boolean z2;
                    if (this.mPendingScrollPosition < getPosition(getChildAt(HORIZONTAL))) {
                        z2 = true;
                    } else {
                        z2 = DEBUG;
                    }
                    if (z2 == this.mShouldReverseLayout) {
                        z = true;
                    }
                    anchorInfo.mLayoutFromEnd = z;
                }
                anchorInfo.assignCoordinateFromPadding();
                return true;
            } else if (this.mOrientationHelper.getDecoratedMeasurement(child) > this.mOrientationHelper.getTotalSpace()) {
                anchorInfo.assignCoordinateFromPadding();
                return true;
            } else if (this.mOrientationHelper.getDecoratedStart(child) - this.mOrientationHelper.getStartAfterPadding() < 0) {
                anchorInfo.mCoordinate = this.mOrientationHelper.getStartAfterPadding();
                anchorInfo.mLayoutFromEnd = DEBUG;
                return true;
            } else if (this.mOrientationHelper.getEndAfterPadding() - this.mOrientationHelper.getDecoratedEnd(child) < 0) {
                anchorInfo.mCoordinate = this.mOrientationHelper.getEndAfterPadding();
                anchorInfo.mLayoutFromEnd = true;
                return true;
            } else {
                int decoratedEnd;
                if (anchorInfo.mLayoutFromEnd) {
                    decoratedEnd = this.mOrientationHelper.getDecoratedEnd(child) + this.mOrientationHelper.getTotalSpaceChange();
                } else {
                    decoratedEnd = this.mOrientationHelper.getDecoratedStart(child);
                }
                anchorInfo.mCoordinate = decoratedEnd;
                return true;
            }
        } else {
            anchorInfo.mLayoutFromEnd = this.mShouldReverseLayout;
            if (this.mShouldReverseLayout) {
                anchorInfo.mCoordinate = this.mOrientationHelper.getEndAfterPadding() - this.mPendingScrollPositionOffset;
                return true;
            }
            anchorInfo.mCoordinate = this.mOrientationHelper.getStartAfterPadding() + this.mPendingScrollPositionOffset;
            return true;
        }
    }

    private int fixLayoutEndGap(int endOffset, Recycler recycler, State state, boolean canOffsetChildren) {
        int gap = this.mOrientationHelper.getEndAfterPadding() - endOffset;
        if (gap <= 0) {
            return HORIZONTAL;
        }
        int fixOffset = -scrollBy(-gap, recycler, state);
        endOffset += fixOffset;
        if (canOffsetChildren) {
            gap = this.mOrientationHelper.getEndAfterPadding() - endOffset;
            if (gap > 0) {
                this.mOrientationHelper.offsetChildren(gap);
                return gap + fixOffset;
            }
        }
        return fixOffset;
    }

    private int fixLayoutStartGap(int startOffset, Recycler recycler, State state, boolean canOffsetChildren) {
        int gap = startOffset - this.mOrientationHelper.getStartAfterPadding();
        if (gap <= 0) {
            return HORIZONTAL;
        }
        int fixOffset = -scrollBy(gap, recycler, state);
        startOffset += fixOffset;
        if (canOffsetChildren) {
            gap = startOffset - this.mOrientationHelper.getStartAfterPadding();
            if (gap > 0) {
                this.mOrientationHelper.offsetChildren(-gap);
                return fixOffset - gap;
            }
        }
        return fixOffset;
    }

    private void updateLayoutStateToFillEnd(AnchorInfo anchorInfo) {
        updateLayoutStateToFillEnd(anchorInfo.mPosition, anchorInfo.mCoordinate);
    }

    private void updateLayoutStateToFillEnd(int itemPosition, int offset) {
        this.mLayoutState.mAvailable = this.mOrientationHelper.getEndAfterPadding() - offset;
        this.mLayoutState.mItemDirection = this.mShouldReverseLayout ? -1 : VERTICAL;
        this.mLayoutState.mCurrentPosition = itemPosition;
        this.mLayoutState.mLayoutDirection = VERTICAL;
        this.mLayoutState.mOffset = offset;
        this.mLayoutState.mScrollingOffset = INVALID_OFFSET;
    }

    private void updateLayoutStateToFillStart(AnchorInfo anchorInfo) {
        updateLayoutStateToFillStart(anchorInfo.mPosition, anchorInfo.mCoordinate);
    }

    private void updateLayoutStateToFillStart(int itemPosition, int offset) {
        this.mLayoutState.mAvailable = offset - this.mOrientationHelper.getStartAfterPadding();
        this.mLayoutState.mCurrentPosition = itemPosition;
        this.mLayoutState.mItemDirection = this.mShouldReverseLayout ? VERTICAL : -1;
        this.mLayoutState.mLayoutDirection = -1;
        this.mLayoutState.mOffset = offset;
        this.mLayoutState.mScrollingOffset = INVALID_OFFSET;
    }

    protected boolean isLayoutRTL() {
        return getLayoutDirection() == VERTICAL ? true : DEBUG;
    }

    void ensureLayoutState() {
        if (this.mLayoutState == null) {
            this.mLayoutState = createLayoutState();
        }
        if (this.mOrientationHelper == null) {
            this.mOrientationHelper = OrientationHelper.createOrientationHelper(this, this.mOrientation);
        }
    }

    LayoutState createLayoutState() {
        return new LayoutState();
    }

    public void scrollToPosition(int position) {
        this.mPendingScrollPosition = position;
        this.mPendingScrollPositionOffset = INVALID_OFFSET;
        if (this.mPendingSavedState != null) {
            this.mPendingSavedState.invalidateAnchor();
        }
        requestLayout();
    }

    public void scrollToPositionWithOffset(int position, int offset) {
        this.mPendingScrollPosition = position;
        this.mPendingScrollPositionOffset = offset;
        if (this.mPendingSavedState != null) {
            this.mPendingSavedState.invalidateAnchor();
        }
        requestLayout();
    }

    public int scrollHorizontallyBy(int dx, Recycler recycler, State state) {
        if (this.mOrientation == VERTICAL) {
            return HORIZONTAL;
        }
        return scrollBy(dx, recycler, state);
    }

    public int scrollVerticallyBy(int dy, Recycler recycler, State state) {
        if (this.mOrientation == 0) {
            return HORIZONTAL;
        }
        return scrollBy(dy, recycler, state);
    }

    public int computeHorizontalScrollOffset(State state) {
        return computeScrollOffset(state);
    }

    public int computeVerticalScrollOffset(State state) {
        return computeScrollOffset(state);
    }

    public int computeHorizontalScrollExtent(State state) {
        return computeScrollExtent(state);
    }

    public int computeVerticalScrollExtent(State state) {
        return computeScrollExtent(state);
    }

    public int computeHorizontalScrollRange(State state) {
        return computeScrollRange(state);
    }

    public int computeVerticalScrollRange(State state) {
        return computeScrollRange(state);
    }

    private int computeScrollOffset(State state) {
        boolean z = DEBUG;
        if (getChildCount() == 0) {
            return HORIZONTAL;
        }
        ensureLayoutState();
        OrientationHelper orientationHelper = this.mOrientationHelper;
        View findFirstVisibleChildClosestToStart = findFirstVisibleChildClosestToStart(!this.mSmoothScrollbarEnabled ? true : DEBUG, true);
        if (!this.mSmoothScrollbarEnabled) {
            z = true;
        }
        return ScrollbarHelper.computeScrollOffset(state, orientationHelper, findFirstVisibleChildClosestToStart, findFirstVisibleChildClosestToEnd(z, true), this, this.mSmoothScrollbarEnabled, this.mShouldReverseLayout);
    }

    private int computeScrollExtent(State state) {
        boolean z = DEBUG;
        if (getChildCount() == 0) {
            return HORIZONTAL;
        }
        ensureLayoutState();
        OrientationHelper orientationHelper = this.mOrientationHelper;
        View findFirstVisibleChildClosestToStart = findFirstVisibleChildClosestToStart(!this.mSmoothScrollbarEnabled ? true : DEBUG, true);
        if (!this.mSmoothScrollbarEnabled) {
            z = true;
        }
        return ScrollbarHelper.computeScrollExtent(state, orientationHelper, findFirstVisibleChildClosestToStart, findFirstVisibleChildClosestToEnd(z, true), this, this.mSmoothScrollbarEnabled);
    }

    private int computeScrollRange(State state) {
        boolean z = DEBUG;
        if (getChildCount() == 0) {
            return HORIZONTAL;
        }
        ensureLayoutState();
        OrientationHelper orientationHelper = this.mOrientationHelper;
        View findFirstVisibleChildClosestToStart = findFirstVisibleChildClosestToStart(!this.mSmoothScrollbarEnabled ? true : DEBUG, true);
        if (!this.mSmoothScrollbarEnabled) {
            z = true;
        }
        return ScrollbarHelper.computeScrollRange(state, orientationHelper, findFirstVisibleChildClosestToStart, findFirstVisibleChildClosestToEnd(z, true), this, this.mSmoothScrollbarEnabled);
    }

    public void setSmoothScrollbarEnabled(boolean enabled) {
        this.mSmoothScrollbarEnabled = enabled;
    }

    public boolean isSmoothScrollbarEnabled() {
        return this.mSmoothScrollbarEnabled;
    }

    private void updateLayoutState(int layoutDirection, int requiredSpace, boolean canUseExistingSpace, State state) {
        int scrollingOffset;
        int i = -1;
        int i2 = VERTICAL;
        this.mLayoutState.mInfinite = resolveIsInfinite();
        this.mLayoutState.mExtra = getExtraLayoutSpace(state);
        this.mLayoutState.mLayoutDirection = layoutDirection;
        LayoutState layoutState;
        View child;
        if (layoutDirection == VERTICAL) {
            layoutState = this.mLayoutState;
            layoutState.mExtra += this.mOrientationHelper.getEndPadding();
            child = getChildClosestToEnd();
            layoutState = this.mLayoutState;
            if (!this.mShouldReverseLayout) {
                i = VERTICAL;
            }
            layoutState.mItemDirection = i;
            this.mLayoutState.mCurrentPosition = getPosition(child) + this.mLayoutState.mItemDirection;
            this.mLayoutState.mOffset = this.mOrientationHelper.getDecoratedEnd(child);
            scrollingOffset = this.mOrientationHelper.getDecoratedEnd(child) - this.mOrientationHelper.getEndAfterPadding();
        } else {
            child = getChildClosestToStart();
            layoutState = this.mLayoutState;
            layoutState.mExtra += this.mOrientationHelper.getStartAfterPadding();
            layoutState = this.mLayoutState;
            if (!this.mShouldReverseLayout) {
                i2 = -1;
            }
            layoutState.mItemDirection = i2;
            this.mLayoutState.mCurrentPosition = getPosition(child) + this.mLayoutState.mItemDirection;
            this.mLayoutState.mOffset = this.mOrientationHelper.getDecoratedStart(child);
            scrollingOffset = (-this.mOrientationHelper.getDecoratedStart(child)) + this.mOrientationHelper.getStartAfterPadding();
        }
        this.mLayoutState.mAvailable = requiredSpace;
        if (canUseExistingSpace) {
            LayoutState layoutState2 = this.mLayoutState;
            layoutState2.mAvailable -= scrollingOffset;
        }
        this.mLayoutState.mScrollingOffset = scrollingOffset;
    }

    boolean resolveIsInfinite() {
        return (this.mOrientationHelper.getMode() == 0 && this.mOrientationHelper.getEnd() == 0) ? true : DEBUG;
    }

    void collectPrefetchPositionsForLayoutState(State state, LayoutState layoutState, LayoutPrefetchRegistry layoutPrefetchRegistry) {
        int pos = layoutState.mCurrentPosition;
        if (pos >= 0 && pos < state.getItemCount()) {
            layoutPrefetchRegistry.addPosition(pos, layoutState.mScrollingOffset);
        }
    }

    public void collectInitialPrefetchPositions(int adapterItemCount, LayoutPrefetchRegistry layoutPrefetchRegistry) {
        boolean fromEnd;
        int anchorPos;
        int direction = -1;
        if (this.mPendingSavedState == null || !this.mPendingSavedState.hasValidAnchor()) {
            resolveShouldLayoutReverse();
            fromEnd = this.mShouldReverseLayout;
            if (this.mPendingScrollPosition == -1) {
                anchorPos = fromEnd ? adapterItemCount - 1 : HORIZONTAL;
            } else {
                anchorPos = this.mPendingScrollPosition;
            }
        } else {
            fromEnd = this.mPendingSavedState.mAnchorLayoutFromEnd;
            anchorPos = this.mPendingSavedState.mAnchorPosition;
        }
        if (!fromEnd) {
            direction = VERTICAL;
        }
        int targetPos = anchorPos;
        for (int i = HORIZONTAL; i < this.mInitialItemPrefetchCount && targetPos >= 0 && targetPos < adapterItemCount; i += VERTICAL) {
            layoutPrefetchRegistry.addPosition(targetPos, HORIZONTAL);
            targetPos += direction;
        }
    }

    public void setInitialPrefetchItemCount(int itemCount) {
        this.mInitialItemPrefetchCount = itemCount;
    }

    public int getInitialItemPrefetchCount() {
        return this.mInitialItemPrefetchCount;
    }

    public void collectAdjacentPrefetchPositions(int dx, int dy, State state, LayoutPrefetchRegistry layoutPrefetchRegistry) {
        int delta;
        if (this.mOrientation == 0) {
            delta = dx;
        } else {
            delta = dy;
        }
        if (getChildCount() != 0 && delta != 0) {
            updateLayoutState(delta > 0 ? VERTICAL : -1, Math.abs(delta), true, state);
            collectPrefetchPositionsForLayoutState(state, this.mLayoutState, layoutPrefetchRegistry);
        }
    }

    int scrollBy(int dy, Recycler recycler, State state) {
        int i = HORIZONTAL;
        if (!(getChildCount() == 0 || dy == 0)) {
            this.mLayoutState.mRecycle = true;
            ensureLayoutState();
            int layoutDirection = dy > 0 ? VERTICAL : -1;
            int absDy = Math.abs(dy);
            updateLayoutState(layoutDirection, absDy, true, state);
            int consumed = this.mLayoutState.mScrollingOffset + fill(recycler, this.mLayoutState, state, DEBUG);
            if (consumed >= 0) {
                if (absDy > consumed) {
                    i = layoutDirection * consumed;
                } else {
                    i = dy;
                }
                this.mOrientationHelper.offsetChildren(-i);
                this.mLayoutState.mLastScrollDelta = i;
            }
        }
        return i;
    }

    public void assertNotInLayoutOrScroll(String message) {
        if (this.mPendingSavedState == null) {
            super.assertNotInLayoutOrScroll(message);
        }
    }

    private void recycleChildren(Recycler recycler, int startIndex, int endIndex) {
        if (startIndex != endIndex) {
            int i;
            if (endIndex > startIndex) {
                for (i = endIndex - 1; i >= startIndex; i--) {
                    removeAndRecycleViewAt(i, recycler);
                }
                return;
            }
            for (i = startIndex; i > endIndex; i--) {
                removeAndRecycleViewAt(i, recycler);
            }
        }
    }

    private void recycleViewsFromStart(Recycler recycler, int dt) {
        if (dt >= 0) {
            int limit = dt;
            int childCount = getChildCount();
            int i;
            View child;
            if (this.mShouldReverseLayout) {
                for (i = childCount - 1; i >= 0; i--) {
                    child = getChildAt(i);
                    if (this.mOrientationHelper.getDecoratedEnd(child) > limit || this.mOrientationHelper.getTransformedEndWithDecoration(child) > limit) {
                        recycleChildren(recycler, childCount - 1, i);
                        return;
                    }
                }
                return;
            }
            for (i = HORIZONTAL; i < childCount; i += VERTICAL) {
                child = getChildAt(i);
                if (this.mOrientationHelper.getDecoratedEnd(child) > limit || this.mOrientationHelper.getTransformedEndWithDecoration(child) > limit) {
                    recycleChildren(recycler, HORIZONTAL, i);
                    return;
                }
            }
        }
    }

    private void recycleViewsFromEnd(Recycler recycler, int dt) {
        int childCount = getChildCount();
        if (dt >= 0) {
            int limit = this.mOrientationHelper.getEnd() - dt;
            int i;
            View child;
            if (this.mShouldReverseLayout) {
                for (i = HORIZONTAL; i < childCount; i += VERTICAL) {
                    child = getChildAt(i);
                    if (this.mOrientationHelper.getDecoratedStart(child) < limit || this.mOrientationHelper.getTransformedStartWithDecoration(child) < limit) {
                        recycleChildren(recycler, HORIZONTAL, i);
                        return;
                    }
                }
                return;
            }
            for (i = childCount - 1; i >= 0; i--) {
                child = getChildAt(i);
                if (this.mOrientationHelper.getDecoratedStart(child) < limit || this.mOrientationHelper.getTransformedStartWithDecoration(child) < limit) {
                    recycleChildren(recycler, childCount - 1, i);
                    return;
                }
            }
        }
    }

    private void recycleByLayoutState(Recycler recycler, LayoutState layoutState) {
        if (layoutState.mRecycle && !layoutState.mInfinite) {
            if (layoutState.mLayoutDirection == -1) {
                recycleViewsFromEnd(recycler, layoutState.mScrollingOffset);
            } else {
                recycleViewsFromStart(recycler, layoutState.mScrollingOffset);
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    int fill(android.support.v7.widget.RecyclerView.Recycler r8, android.support.v7.widget.LinearLayoutManager.LayoutState r9, android.support.v7.widget.RecyclerView.State r10, boolean r11) {
        /*
        r7 = this;
        r6 = -2147483648; // 0xffffffff80000000 float:-0.0 double:NaN;
        r2 = r9.mAvailable;
        r3 = r9.mScrollingOffset;
        if (r3 == r6) goto L_0x0016;
    L_0x0008:
        r3 = r9.mAvailable;
        if (r3 >= 0) goto L_0x0013;
    L_0x000c:
        r3 = r9.mScrollingOffset;
        r4 = r9.mAvailable;
        r3 = r3 + r4;
        r9.mScrollingOffset = r3;
    L_0x0013:
        r7.recycleByLayoutState(r8, r9);
    L_0x0016:
        r3 = r9.mAvailable;
        r4 = r9.mExtra;
        r1 = r3 + r4;
        r0 = r7.mLayoutChunkResult;
    L_0x001e:
        r3 = r9.mInfinite;
        if (r3 != 0) goto L_0x0024;
    L_0x0022:
        if (r1 <= 0) goto L_0x0034;
    L_0x0024:
        r3 = r9.hasMore(r10);
        if (r3 == 0) goto L_0x0034;
    L_0x002a:
        r0.resetInternal();
        r7.layoutChunk(r8, r10, r9, r0);
        r3 = r0.mFinished;
        if (r3 == 0) goto L_0x0039;
    L_0x0034:
        r3 = r9.mAvailable;
        r3 = r2 - r3;
        return r3;
    L_0x0039:
        r3 = r9.mOffset;
        r4 = r0.mConsumed;
        r5 = r9.mLayoutDirection;
        r4 = r4 * r5;
        r3 = r3 + r4;
        r9.mOffset = r3;
        r3 = r0.mIgnoreConsumed;
        if (r3 == 0) goto L_0x0053;
    L_0x0047:
        r3 = r7.mLayoutState;
        r3 = r3.mScrapList;
        if (r3 != 0) goto L_0x0053;
    L_0x004d:
        r3 = r10.isPreLayout();
        if (r3 != 0) goto L_0x005d;
    L_0x0053:
        r3 = r9.mAvailable;
        r4 = r0.mConsumed;
        r3 = r3 - r4;
        r9.mAvailable = r3;
        r3 = r0.mConsumed;
        r1 = r1 - r3;
    L_0x005d:
        r3 = r9.mScrollingOffset;
        if (r3 == r6) goto L_0x0076;
    L_0x0061:
        r3 = r9.mScrollingOffset;
        r4 = r0.mConsumed;
        r3 = r3 + r4;
        r9.mScrollingOffset = r3;
        r3 = r9.mAvailable;
        if (r3 >= 0) goto L_0x0073;
    L_0x006c:
        r3 = r9.mScrollingOffset;
        r4 = r9.mAvailable;
        r3 = r3 + r4;
        r9.mScrollingOffset = r3;
    L_0x0073:
        r7.recycleByLayoutState(r8, r9);
    L_0x0076:
        if (r11 == 0) goto L_0x001e;
    L_0x0078:
        r3 = r0.mFocusable;
        if (r3 == 0) goto L_0x001e;
    L_0x007c:
        goto L_0x0034;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v7.widget.LinearLayoutManager.fill(android.support.v7.widget.RecyclerView$Recycler, android.support.v7.widget.LinearLayoutManager$LayoutState, android.support.v7.widget.RecyclerView$State, boolean):int");
    }

    void layoutChunk(Recycler recycler, State state, LayoutState layoutState, LayoutChunkResult result) {
        View view = layoutState.next(recycler);
        if (view == null) {
            result.mFinished = true;
            return;
        }
        int right;
        int left;
        int bottom;
        int top;
        LayoutParams params = (LayoutParams) view.getLayoutParams();
        boolean z;
        boolean z2;
        if (layoutState.mScrapList == null) {
            z = this.mShouldReverseLayout;
            if (layoutState.mLayoutDirection == -1) {
                z2 = true;
            } else {
                z2 = HORIZONTAL;
            }
            if (z == z2) {
                addView(view);
            } else {
                addView(view, HORIZONTAL);
            }
        } else {
            z = this.mShouldReverseLayout;
            if (layoutState.mLayoutDirection == -1) {
                z2 = true;
            } else {
                z2 = HORIZONTAL;
            }
            if (z == z2) {
                addDisappearingView(view);
            } else {
                addDisappearingView(view, HORIZONTAL);
            }
        }
        measureChildWithMargins(view, HORIZONTAL, HORIZONTAL);
        result.mConsumed = this.mOrientationHelper.getDecoratedMeasurement(view);
        if (this.mOrientation == VERTICAL) {
            if (isLayoutRTL()) {
                right = getWidth() - getPaddingRight();
                left = right - this.mOrientationHelper.getDecoratedMeasurementInOther(view);
            } else {
                left = getPaddingLeft();
                right = left + this.mOrientationHelper.getDecoratedMeasurementInOther(view);
            }
            if (layoutState.mLayoutDirection == -1) {
                bottom = layoutState.mOffset;
                top = layoutState.mOffset - result.mConsumed;
            } else {
                top = layoutState.mOffset;
                bottom = layoutState.mOffset + result.mConsumed;
            }
        } else {
            top = getPaddingTop();
            bottom = top + this.mOrientationHelper.getDecoratedMeasurementInOther(view);
            if (layoutState.mLayoutDirection == -1) {
                right = layoutState.mOffset;
                left = layoutState.mOffset - result.mConsumed;
            } else {
                left = layoutState.mOffset;
                right = layoutState.mOffset + result.mConsumed;
            }
        }
        layoutDecoratedWithMargins(view, left, top, right, bottom);
        if (params.isItemRemoved() || params.isItemChanged()) {
            result.mIgnoreConsumed = true;
        }
        result.mFocusable = view.isFocusable();
    }

    boolean shouldMeasureTwice() {
        return (getHeightMode() == 1073741824 || getWidthMode() == 1073741824 || !hasFlexibleChildInBothOrientations()) ? DEBUG : true;
    }

    int convertFocusDirectionToLayoutDirection(int focusDirection) {
        int i = INVALID_OFFSET;
        int i2 = VERTICAL;
        switch (focusDirection) {
            case VERTICAL /*1*/:
                if (this.mOrientation == VERTICAL || !isLayoutRTL()) {
                    return -1;
                }
                return VERTICAL;
            case OnSubscribeConcatMap.END /*2*/:
                if (this.mOrientation == VERTICAL) {
                    return VERTICAL;
                }
                if (isLayoutRTL()) {
                    return -1;
                }
                return VERTICAL;
            case ConnectionResult.SIGN_IN_FAILED /*17*/:
                if (this.mOrientation != 0) {
                    return INVALID_OFFSET;
                }
                return -1;
            case C0801R.styleable.AppCompatTheme_actionModeCopyDrawable /*33*/:
                if (this.mOrientation != VERTICAL) {
                    return INVALID_OFFSET;
                }
                return -1;
            case C0801R.styleable.AppCompatTheme_textAppearanceSearchResultTitle /*66*/:
                if (this.mOrientation != 0) {
                    i2 = INVALID_OFFSET;
                }
                return i2;
            case TransportMediator.KEYCODE_MEDIA_RECORD /*130*/:
                if (this.mOrientation == VERTICAL) {
                    i = VERTICAL;
                }
                return i;
            default:
                return INVALID_OFFSET;
        }
    }

    private View getChildClosestToStart() {
        return getChildAt(this.mShouldReverseLayout ? getChildCount() - 1 : HORIZONTAL);
    }

    private View getChildClosestToEnd() {
        return getChildAt(this.mShouldReverseLayout ? HORIZONTAL : getChildCount() - 1);
    }

    private View findFirstVisibleChildClosestToStart(boolean completelyVisible, boolean acceptPartiallyVisible) {
        if (this.mShouldReverseLayout) {
            return findOneVisibleChild(getChildCount() - 1, -1, completelyVisible, acceptPartiallyVisible);
        }
        return findOneVisibleChild(HORIZONTAL, getChildCount(), completelyVisible, acceptPartiallyVisible);
    }

    private View findFirstVisibleChildClosestToEnd(boolean completelyVisible, boolean acceptPartiallyVisible) {
        if (this.mShouldReverseLayout) {
            return findOneVisibleChild(HORIZONTAL, getChildCount(), completelyVisible, acceptPartiallyVisible);
        }
        return findOneVisibleChild(getChildCount() - 1, -1, completelyVisible, acceptPartiallyVisible);
    }

    private View findReferenceChildClosestToEnd(Recycler recycler, State state) {
        if (this.mShouldReverseLayout) {
            return findFirstReferenceChild(recycler, state);
        }
        return findLastReferenceChild(recycler, state);
    }

    private View findReferenceChildClosestToStart(Recycler recycler, State state) {
        if (this.mShouldReverseLayout) {
            return findLastReferenceChild(recycler, state);
        }
        return findFirstReferenceChild(recycler, state);
    }

    private View findFirstReferenceChild(Recycler recycler, State state) {
        return findReferenceChild(recycler, state, HORIZONTAL, getChildCount(), state.getItemCount());
    }

    private View findLastReferenceChild(Recycler recycler, State state) {
        return findReferenceChild(recycler, state, getChildCount() - 1, -1, state.getItemCount());
    }

    View findReferenceChild(Recycler recycler, State state, int start, int end, int itemCount) {
        ensureLayoutState();
        View invalidMatch = null;
        View outOfBoundsMatch = null;
        int boundsStart = this.mOrientationHelper.getStartAfterPadding();
        int boundsEnd = this.mOrientationHelper.getEndAfterPadding();
        int diff = end > start ? VERTICAL : -1;
        for (int i = start; i != end; i += diff) {
            View childAt = getChildAt(i);
            int position = getPosition(childAt);
            if (position >= 0 && position < itemCount) {
                if (((LayoutParams) childAt.getLayoutParams()).isItemRemoved()) {
                    if (invalidMatch == null) {
                        invalidMatch = childAt;
                    }
                } else if (this.mOrientationHelper.getDecoratedStart(childAt) < boundsEnd && this.mOrientationHelper.getDecoratedEnd(childAt) >= boundsStart) {
                    return childAt;
                } else {
                    if (outOfBoundsMatch == null) {
                        outOfBoundsMatch = childAt;
                    }
                }
            }
        }
        if (outOfBoundsMatch == null) {
            outOfBoundsMatch = invalidMatch;
        }
        return outOfBoundsMatch;
    }

    public int findFirstVisibleItemPosition() {
        View child = findOneVisibleChild(HORIZONTAL, getChildCount(), DEBUG, true);
        return child == null ? -1 : getPosition(child);
    }

    public int findFirstCompletelyVisibleItemPosition() {
        View child = findOneVisibleChild(HORIZONTAL, getChildCount(), true, DEBUG);
        return child == null ? -1 : getPosition(child);
    }

    public int findLastVisibleItemPosition() {
        View child = findOneVisibleChild(getChildCount() - 1, -1, DEBUG, true);
        if (child == null) {
            return -1;
        }
        return getPosition(child);
    }

    public int findLastCompletelyVisibleItemPosition() {
        View child = findOneVisibleChild(getChildCount() - 1, -1, true, DEBUG);
        if (child == null) {
            return -1;
        }
        return getPosition(child);
    }

    View findOneVisibleChild(int fromIndex, int toIndex, boolean completelyVisible, boolean acceptPartiallyVisible) {
        ensureLayoutState();
        int start = this.mOrientationHelper.getStartAfterPadding();
        int end = this.mOrientationHelper.getEndAfterPadding();
        int next = toIndex > fromIndex ? VERTICAL : -1;
        View partiallyVisible = null;
        for (int i = fromIndex; i != toIndex; i += next) {
            View child = getChildAt(i);
            int childStart = this.mOrientationHelper.getDecoratedStart(child);
            int childEnd = this.mOrientationHelper.getDecoratedEnd(child);
            if (childStart < end && childEnd > start) {
                if (!completelyVisible) {
                    return child;
                }
                if (childStart >= start && childEnd <= end) {
                    return child;
                }
                if (acceptPartiallyVisible && partiallyVisible == null) {
                    partiallyVisible = child;
                }
            }
        }
        return partiallyVisible;
    }

    public View onFocusSearchFailed(View focused, int focusDirection, Recycler recycler, State state) {
        resolveShouldLayoutReverse();
        if (getChildCount() == 0) {
            return null;
        }
        int layoutDir = convertFocusDirectionToLayoutDirection(focusDirection);
        if (layoutDir == INVALID_OFFSET) {
            return null;
        }
        View referenceChild;
        ensureLayoutState();
        if (layoutDir == -1) {
            referenceChild = findReferenceChildClosestToStart(recycler, state);
        } else {
            referenceChild = findReferenceChildClosestToEnd(recycler, state);
        }
        if (referenceChild == null) {
            return null;
        }
        View nextFocus;
        ensureLayoutState();
        updateLayoutState(layoutDir, (int) (MAX_SCROLL_FACTOR * ((float) this.mOrientationHelper.getTotalSpace())), DEBUG, state);
        this.mLayoutState.mScrollingOffset = INVALID_OFFSET;
        this.mLayoutState.mRecycle = DEBUG;
        fill(recycler, this.mLayoutState, state, true);
        if (layoutDir == -1) {
            nextFocus = getChildClosestToStart();
        } else {
            nextFocus = getChildClosestToEnd();
        }
        if (nextFocus == referenceChild || !nextFocus.isFocusable()) {
            return null;
        }
        return nextFocus;
    }

    private void logChildren() {
        Log.d(TAG, "internal representation of views on the screen");
        for (int i = HORIZONTAL; i < getChildCount(); i += VERTICAL) {
            View child = getChildAt(i);
            Log.d(TAG, "item " + getPosition(child) + ", coord:" + this.mOrientationHelper.getDecoratedStart(child));
        }
        Log.d(TAG, "==============");
    }

    void validateChildOrder() {
        boolean z = true;
        Log.d(TAG, "validating child count " + getChildCount());
        if (getChildCount() >= VERTICAL) {
            int lastPos = getPosition(getChildAt(HORIZONTAL));
            int lastScreenLoc = this.mOrientationHelper.getDecoratedStart(getChildAt(HORIZONTAL));
            int i;
            View child;
            int pos;
            int screenLoc;
            StringBuilder append;
            if (this.mShouldReverseLayout) {
                i = VERTICAL;
                while (i < getChildCount()) {
                    child = getChildAt(i);
                    pos = getPosition(child);
                    screenLoc = this.mOrientationHelper.getDecoratedStart(child);
                    if (pos < lastPos) {
                        logChildren();
                        append = new StringBuilder().append("detected invalid position. loc invalid? ");
                        if (screenLoc >= lastScreenLoc) {
                            z = DEBUG;
                        }
                        throw new RuntimeException(append.append(z).toString());
                    } else if (screenLoc > lastScreenLoc) {
                        logChildren();
                        throw new RuntimeException("detected invalid location");
                    } else {
                        i += VERTICAL;
                    }
                }
                return;
            }
            i = VERTICAL;
            while (i < getChildCount()) {
                child = getChildAt(i);
                pos = getPosition(child);
                screenLoc = this.mOrientationHelper.getDecoratedStart(child);
                if (pos < lastPos) {
                    logChildren();
                    append = new StringBuilder().append("detected invalid position. loc invalid? ");
                    if (screenLoc >= lastScreenLoc) {
                        z = DEBUG;
                    }
                    throw new RuntimeException(append.append(z).toString());
                } else if (screenLoc < lastScreenLoc) {
                    logChildren();
                    throw new RuntimeException("detected invalid location");
                } else {
                    i += VERTICAL;
                }
            }
        }
    }

    public boolean supportsPredictiveItemAnimations() {
        return (this.mPendingSavedState == null && this.mLastStackFromEnd == this.mStackFromEnd) ? true : DEBUG;
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    public void prepareForDrop(View view, View target, int x, int y) {
        int dropDirection;
        assertNotInLayoutOrScroll("Cannot drop a view during a scroll or layout calculation");
        ensureLayoutState();
        resolveShouldLayoutReverse();
        int myPos = getPosition(view);
        int targetPos = getPosition(target);
        if (myPos < targetPos) {
            dropDirection = VERTICAL;
        } else {
            dropDirection = -1;
        }
        if (this.mShouldReverseLayout) {
            if (dropDirection == VERTICAL) {
                scrollToPositionWithOffset(targetPos, this.mOrientationHelper.getEndAfterPadding() - (this.mOrientationHelper.getDecoratedStart(target) + this.mOrientationHelper.getDecoratedMeasurement(view)));
            } else {
                scrollToPositionWithOffset(targetPos, this.mOrientationHelper.getEndAfterPadding() - this.mOrientationHelper.getDecoratedEnd(target));
            }
        } else if (dropDirection == -1) {
            scrollToPositionWithOffset(targetPos, this.mOrientationHelper.getDecoratedStart(target));
        } else {
            scrollToPositionWithOffset(targetPos, this.mOrientationHelper.getDecoratedEnd(target) - this.mOrientationHelper.getDecoratedMeasurement(view));
        }
    }
}
