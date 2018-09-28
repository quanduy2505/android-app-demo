package agency.tango.materialintroscreen.parallax;

import agency.tango.materialintroscreen.C0005R;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.FloatRange;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.LinearLayout;

public class ParallaxLinearLayout extends LinearLayout implements Parallaxable {

    public static class LayoutParams extends android.widget.LinearLayout.LayoutParams {
        float parallaxFactor;

        LayoutParams(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            this.parallaxFactor = 0.0f;
            TypedArray typedArray = context.obtainStyledAttributes(attributeSet, C0005R.styleable.ParallaxLayout_Layout);
            this.parallaxFactor = typedArray.getFloat(C0005R.styleable.ParallaxLayout_Layout_layout_parallaxFactor, this.parallaxFactor);
            typedArray.recycle();
        }

        LayoutParams(int width, int height) {
            super(width, height);
            this.parallaxFactor = 0.0f;
        }

        LayoutParams(int width, int height, int gravity) {
            super(width, height, (float) gravity);
            this.parallaxFactor = 0.0f;
        }

        LayoutParams(android.view.ViewGroup.LayoutParams source) {
            super(source);
            this.parallaxFactor = 0.0f;
        }

        LayoutParams(MarginLayoutParams source) {
            super(source);
            this.parallaxFactor = 0.0f;
        }
    }

    public ParallaxLinearLayout(Context context) {
        super(context);
    }

    public ParallaxLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ParallaxLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected boolean checkLayoutParams(android.view.ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-1, -1);
    }

    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    protected LayoutParams generateLayoutParams(android.view.ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    public void setOffset(@FloatRange(from = -1.0d, to = 1.0d) float offset) {
        for (int i = getChildCount() - 1; i >= 0; i--) {
            View child = getChildAt(i);
            LayoutParams p = (LayoutParams) child.getLayoutParams();
            if (p.parallaxFactor != 0.0f) {
                child.setTranslationX((((float) getWidth()) * (-offset)) * p.parallaxFactor);
            }
        }
    }
}
