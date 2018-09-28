package agency.tango.materialintroscreen.widgets;

import agency.tango.materialintroscreen.adapter.SlidesAdapter;
import android.content.Context;
import android.support.v4.view.CustomViewPager;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import rx.internal.operators.OnSubscribeConcatMap;
import rx.internal.schedulers.NewThreadWorker;

public class SwipeableViewPager extends CustomViewPager {
    private boolean alphaExitTransitionEnabled;
    private int currentIt;
    private float startPos;
    private boolean swipingAllowed;

    public SwipeableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.startPos = 0.0f;
        this.alphaExitTransitionEnabled = false;
        this.swipingAllowed = true;
    }

    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (MotionEventCompat.getActionMasked(event)) {
            case NewThreadWorker.PURGE_FREQUENCY /*0*/:
                return super.onInterceptTouchEvent(event);
            case OnSubscribeConcatMap.BOUNDARY /*1*/:
                if (this.swipingAllowed) {
                    return super.onInterceptTouchEvent(event);
                }
                return false;
            case OnSubscribeConcatMap.END /*2*/:
                if (this.swipingAllowed) {
                    return super.onInterceptTouchEvent(event);
                }
                return false;
            default:
                return super.onInterceptTouchEvent(event);
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (MotionEventCompat.getActionMasked(event)) {
            case NewThreadWorker.PURGE_FREQUENCY /*0*/:
                this.startPos = event.getX();
                this.currentIt = getCurrentItem();
                resolveSwipingRightAllowed();
                return super.onTouchEvent(event);
            case OnSubscribeConcatMap.BOUNDARY /*1*/:
                if (this.swipingAllowed || this.startPos - event.getX() <= 16.0f) {
                    this.startPos = 0.0f;
                    return super.onTouchEvent(event);
                }
                smoothScrollTo(getWidth() * this.currentIt, 0);
                return true;
            case OnSubscribeConcatMap.END /*2*/:
                if (this.swipingAllowed || this.startPos - event.getX() <= 16.0f) {
                    return super.onTouchEvent(event);
                }
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

    public SlidesAdapter getAdapter() {
        return (SlidesAdapter) super.getAdapter();
    }

    public boolean executeKeyEvent(KeyEvent event) {
        return false;
    }

    public void moveToNextPage() {
        setCurrentItem(getCurrentItem() + 1, true);
    }

    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        super.onPageScrolled(position, positionOffset, positionOffsetPixels);
    }

    public int getPreviousItem() {
        return getCurrentItem() - 1;
    }

    public void setSwipingRightAllowed(boolean allowed) {
        this.swipingAllowed = allowed;
    }

    public void alphaExitTransitionEnabled(boolean alphaExitTransitionEnabled) {
        this.alphaExitTransitionEnabled = alphaExitTransitionEnabled;
    }

    public boolean alphaExitTransitionEnabled() {
        return this.alphaExitTransitionEnabled && this.swipingAllowed;
    }

    private void resolveSwipingRightAllowed() {
        if (getAdapter().shouldLockSlide(getCurrentItem())) {
            setSwipingRightAllowed(false);
        } else {
            setSwipingRightAllowed(true);
        }
    }
}
