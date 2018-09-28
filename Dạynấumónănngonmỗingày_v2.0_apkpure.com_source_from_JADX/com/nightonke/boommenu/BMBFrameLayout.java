package com.nightonke.boommenu;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

class BMBFrameLayout extends FrameLayout {
    private boolean requestLayoutNotFinish;

    public BMBFrameLayout(Context context) {
        super(context);
        this.requestLayoutNotFinish = false;
    }

    public BMBFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.requestLayoutNotFinish = false;
    }

    public BMBFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.requestLayoutNotFinish = false;
    }

    public void requestLayout() {
        if (!this.requestLayoutNotFinish) {
            this.requestLayoutNotFinish = true;
            super.requestLayout();
        }
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        this.requestLayoutNotFinish = false;
    }
}
