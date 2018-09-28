package com.nightonke.boommenu.Piece;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout.LayoutParams;

public abstract class BoomPiece extends View {
    private boolean requestLayoutNotFinish;

    public abstract void init(int i);

    public abstract void setColor(int i);

    public BoomPiece(Context context) {
        super(context);
        this.requestLayoutNotFinish = false;
    }

    public void place(int left, int top, int width, int height) {
        LayoutParams layoutParams = (LayoutParams) getLayoutParams();
        if (layoutParams != null) {
            layoutParams.leftMargin = left;
            layoutParams.topMargin = top;
            layoutParams.width = width;
            layoutParams.height = height;
            setLayoutParams(layoutParams);
        }
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
