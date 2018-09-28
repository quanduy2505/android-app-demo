package agency.tango.materialintroscreen.widgets;

import agency.tango.materialintroscreen.C0005R;
import agency.tango.materialintroscreen.listeners.IFinishListener;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class OverScrollViewPager extends RelativeLayout {
    private IFinishListener finishListener;
    private boolean mIsBeingDragged;
    private float mMotionBeginX;
    private int mTouchSlop;
    private float positionOffset;
    private SwipeableViewPager swipeableViewPager;

    final class SmoothScrollRunnable implements Runnable {
        private int currentPosition;
        private final long duration;
        private final Interpolator interpolator;
        private final int scrollFromPosition;
        private final int scrollToPosition;
        private long startTime;

        SmoothScrollRunnable(int fromPosition, int toPosition, long duration, Interpolator scrollAnimationInterpolator) {
            this.startTime = -1;
            this.currentPosition = -1;
            this.scrollFromPosition = fromPosition;
            this.scrollToPosition = toPosition;
            this.interpolator = scrollAnimationInterpolator;
            this.duration = duration;
        }

        public void run() {
            if (this.startTime == -1) {
                this.startTime = System.currentTimeMillis();
            } else {
                this.currentPosition = this.scrollFromPosition - Math.round(((float) (this.scrollFromPosition - this.scrollToPosition)) * this.interpolator.getInterpolation(((float) Math.max(Math.min(((System.currentTimeMillis() - this.startTime) * 1000) / this.duration, 1000), 0)) / 1000.0f));
                OverScrollViewPager.this.moveOverScrollView((float) this.currentPosition);
            }
            if (this.scrollToPosition != this.currentPosition) {
                ViewCompat.postOnAnimation(OverScrollViewPager.this, this);
            }
        }
    }

    public OverScrollViewPager(Context context) {
        this(context, null);
    }

    public OverScrollViewPager(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OverScrollViewPager(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.swipeableViewPager = null;
        this.mIsBeingDragged = false;
        this.mMotionBeginX = 0.0f;
        this.positionOffset = 0.0f;
        this.swipeableViewPager = createOverScrollView();
        addView(this.swipeableViewPager, new LayoutParams(-1, -1));
        this.mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    public boolean onInterceptTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == 0) {
            this.mMotionBeginX = event.getX();
            this.mIsBeingDragged = false;
        } else if (action == 2 && !this.mIsBeingDragged) {
            float scrollDirectionDiff = event.getX() - this.mMotionBeginX;
            if (Math.abs(scrollDirectionDiff) > ((float) this.mTouchSlop) && canOverScrollAtEnd() && scrollDirectionDiff < 0.0f) {
                this.mIsBeingDragged = true;
            }
        }
        return this.mIsBeingDragged;
    }

    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float moveOffset = event.getX() - this.mMotionBeginX;
        if (action == 2) {
            moveOverScrollView(moveOffset);
        } else if (action == 1) {
            if (this.positionOffset > 0.5f) {
                finishOverScrollViewWithAnimation(moveOffset);
            } else {
                resetOverScrollViewWithAnimation(moveOffset);
            }
            this.mIsBeingDragged = false;
        }
        return true;
    }

    public SwipeableViewPager getOverScrollView() {
        return this.swipeableViewPager;
    }

    public void registerFinishListener(IFinishListener listener) {
        this.finishListener = listener;
    }

    private void moveOverScrollView(float currentX) {
        if (canScroll(currentX)) {
            scrollTo((int) (-currentX), 0);
            this.positionOffset = calculateOffset();
            this.swipeableViewPager.onPageScrolled(this.swipeableViewPager.getAdapter().getLastItemPosition(), this.positionOffset, 0);
            if (shouldFinish()) {
                this.finishListener.doOnFinish();
            }
        }
    }

    private float calculateOffset() {
        return ((((float) getScrollX()) * 100.0f) / ((float) getWidth())) / 100.0f;
    }

    private boolean shouldFinish() {
        return this.positionOffset == 1.0f;
    }

    private boolean canScroll(float currentX) {
        return currentX <= 0.0f;
    }

    private void resetOverScrollViewWithAnimation(float currentX) {
        post(new SmoothScrollRunnable((int) currentX, 0, 300, new AccelerateInterpolator()));
    }

    private void finishOverScrollViewWithAnimation(float currentX) {
        post(new SmoothScrollRunnable((int) currentX, -getWidth(), 300, new AccelerateInterpolator()));
    }

    private boolean canOverScrollAtEnd() {
        SwipeableViewPager viewPager = getOverScrollView();
        PagerAdapter adapter = viewPager.getAdapter();
        if (adapter == null || adapter.getCount() <= 0 || !viewPager.alphaExitTransitionEnabled() || viewPager.getCurrentItem() != adapter.getCount() - 1) {
            return false;
        }
        return true;
    }

    private SwipeableViewPager createOverScrollView() {
        SwipeableViewPager swipeableViewPager = new SwipeableViewPager(getContext(), null);
        swipeableViewPager.setId(C0005R.id.swipeable_view_pager);
        return swipeableViewPager;
    }
}
