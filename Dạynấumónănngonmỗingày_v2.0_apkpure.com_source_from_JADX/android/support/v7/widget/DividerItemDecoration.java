package android.support.v7.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.support.v7.widget.RecyclerView.State;
import android.view.View;

public class DividerItemDecoration extends ItemDecoration {
    private static final int[] ATTRS;
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    private final Rect mBounds;
    private Drawable mDivider;
    private int mOrientation;

    static {
        int[] iArr = new int[VERTICAL];
        iArr[HORIZONTAL] = 16843284;
        ATTRS = iArr;
    }

    public DividerItemDecoration(Context context, int orientation) {
        this.mBounds = new Rect();
        TypedArray a = context.obtainStyledAttributes(ATTRS);
        this.mDivider = a.getDrawable(HORIZONTAL);
        a.recycle();
        setOrientation(orientation);
    }

    public void setOrientation(int orientation) {
        if (orientation == 0 || orientation == VERTICAL) {
            this.mOrientation = orientation;
            return;
        }
        throw new IllegalArgumentException("Invalid orientation. It should be either HORIZONTAL or VERTICAL");
    }

    public void setDrawable(@NonNull Drawable drawable) {
        if (drawable == null) {
            throw new IllegalArgumentException("Drawable cannot be null.");
        }
        this.mDivider = drawable;
    }

    public void onDraw(Canvas c, RecyclerView parent, State state) {
        if (parent.getLayoutManager() != null) {
            if (this.mOrientation == VERTICAL) {
                drawVertical(c, parent);
            } else {
                drawHorizontal(c, parent);
            }
        }
    }

    @SuppressLint({"NewApi"})
    private void drawVertical(Canvas canvas, RecyclerView parent) {
        int left;
        int right;
        canvas.save();
        if (parent.getClipToPadding()) {
            left = parent.getPaddingLeft();
            right = parent.getWidth() - parent.getPaddingRight();
            canvas.clipRect(left, parent.getPaddingTop(), right, parent.getHeight() - parent.getPaddingBottom());
        } else {
            left = HORIZONTAL;
            right = parent.getWidth();
        }
        int childCount = parent.getChildCount();
        for (int i = HORIZONTAL; i < childCount; i += VERTICAL) {
            View child = parent.getChildAt(i);
            parent.getDecoratedBoundsWithMargins(child, this.mBounds);
            int bottom = this.mBounds.bottom + Math.round(ViewCompat.getTranslationY(child));
            this.mDivider.setBounds(left, bottom - this.mDivider.getIntrinsicHeight(), right, bottom);
            this.mDivider.draw(canvas);
        }
        canvas.restore();
    }

    @SuppressLint({"NewApi"})
    private void drawHorizontal(Canvas canvas, RecyclerView parent) {
        int top;
        int bottom;
        canvas.save();
        if (parent.getClipToPadding()) {
            top = parent.getPaddingTop();
            bottom = parent.getHeight() - parent.getPaddingBottom();
            canvas.clipRect(parent.getPaddingLeft(), top, parent.getWidth() - parent.getPaddingRight(), bottom);
        } else {
            top = HORIZONTAL;
            bottom = parent.getHeight();
        }
        int childCount = parent.getChildCount();
        for (int i = HORIZONTAL; i < childCount; i += VERTICAL) {
            View child = parent.getChildAt(i);
            parent.getLayoutManager().getDecoratedBoundsWithMargins(child, this.mBounds);
            int right = this.mBounds.right + Math.round(ViewCompat.getTranslationX(child));
            this.mDivider.setBounds(right - this.mDivider.getIntrinsicWidth(), top, right, bottom);
            this.mDivider.draw(canvas);
        }
        canvas.restore();
    }

    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
        if (this.mOrientation == VERTICAL) {
            outRect.set(HORIZONTAL, HORIZONTAL, HORIZONTAL, this.mDivider.getIntrinsicHeight());
        } else {
            outRect.set(HORIZONTAL, HORIZONTAL, this.mDivider.getIntrinsicWidth(), HORIZONTAL);
        }
    }
}
