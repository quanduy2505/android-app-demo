package com.nightonke.boommenu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class BMBShadow extends FrameLayout {
    private int shadowColor;
    private int shadowCornerRadius;
    private boolean shadowEffect;
    private int shadowOffsetX;
    private int shadowOffsetY;
    private int shadowRadius;

    public BMBShadow(Context context) {
        super(context);
        this.shadowEffect = true;
    }

    public BMBShadow(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.shadowEffect = true;
    }

    public BMBShadow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.shadowEffect = true;
    }

    private void initPadding() {
        int xPadding = this.shadowRadius + Math.abs(this.shadowOffsetX);
        int yPadding = this.shadowRadius + Math.abs(this.shadowOffsetY);
        setPadding(xPadding, yPadding, xPadding, yPadding);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0) {
            createShadow();
        }
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        createShadow();
    }

    private void createShadow() {
        if (this.shadowEffect) {
            BitmapDrawable shadowDrawable = new BitmapDrawable(getResources(), createShadowBitmap());
            if (VERSION.SDK_INT <= 16) {
                setBackgroundDrawable(shadowDrawable);
                return;
            } else {
                setBackground(shadowDrawable);
                return;
            }
        }
        clearShadow();
    }

    private Bitmap createShadowBitmap() {
        Bitmap shadowBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Config.ALPHA_8);
        Canvas canvas = new Canvas(shadowBitmap);
        RectF shadowRect = new RectF((float) (this.shadowRadius + Math.abs(this.shadowOffsetX)), (float) (this.shadowRadius + Math.abs(this.shadowOffsetY)), (float) ((getWidth() - this.shadowRadius) - Math.abs(this.shadowOffsetX)), (float) ((getHeight() - this.shadowRadius) - Math.abs(this.shadowOffsetY)));
        Paint shadowPaint = new Paint();
        shadowPaint.setAntiAlias(true);
        shadowPaint.setColor(0);
        shadowPaint.setStyle(Style.FILL);
        if (!isInEditMode()) {
            shadowPaint.setShadowLayer((float) this.shadowRadius, (float) this.shadowOffsetX, (float) this.shadowOffsetY, this.shadowColor);
        }
        canvas.drawRoundRect(shadowRect, (float) this.shadowCornerRadius, (float) this.shadowCornerRadius, shadowPaint);
        return shadowBitmap;
    }

    public void setShadowOffsetX(int shadowOffsetX) {
        this.shadowOffsetX = shadowOffsetX;
        initPadding();
    }

    public void setShadowOffsetY(int shadowOffsetY) {
        this.shadowOffsetY = shadowOffsetY;
        initPadding();
    }

    public void setShadowRadius(int shadowRadius) {
        this.shadowRadius = shadowRadius;
        initPadding();
    }

    public void setShadowCornerRadius(int shadowCornerRadius) {
        this.shadowCornerRadius = shadowCornerRadius;
        initPadding();
    }

    public void setShadowColor(int shadowColor) {
        this.shadowColor = shadowColor;
    }

    public void setShadowEffect(boolean shadowEffect) {
        this.shadowEffect = shadowEffect;
    }

    public void clearShadow() {
        Util.setDrawable(this, null);
    }
}
