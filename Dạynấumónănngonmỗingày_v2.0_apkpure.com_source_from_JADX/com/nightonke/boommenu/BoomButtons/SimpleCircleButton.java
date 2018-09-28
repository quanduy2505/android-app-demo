package com.nightonke.boommenu.BoomButtons;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import com.nightonke.boommenu.C0763R;
import java.util.ArrayList;

public class SimpleCircleButton extends BoomButton {

    public static class Builder extends BoomButtonBuilder {
        public Builder index(int index) {
            this.index = index;
            return this;
        }

        public Builder innerListener(InnerOnBoomButtonClickListener listener) {
            this.listener = listener;
            return this;
        }

        public Builder listener(OnBMClickListener onBMClickListener) {
            this.onBMClickListener = onBMClickListener;
            return this;
        }

        public Builder rotateImage(boolean rotateImage) {
            this.rotateImage = rotateImage;
            return this;
        }

        public Builder shadowEffect(boolean shadowEffect) {
            this.shadowEffect = shadowEffect;
            return this;
        }

        public Builder shadowOffsetX(int shadowOffsetX) {
            this.shadowOffsetX = shadowOffsetX;
            return this;
        }

        public Builder shadowOffsetY(int shadowOffsetY) {
            this.shadowOffsetY = shadowOffsetY;
            return this;
        }

        public Builder shadowRadius(int shadowRadius) {
            this.shadowRadius = shadowRadius;
            return this;
        }

        public Builder shadowColor(int shadowColor) {
            this.shadowColor = shadowColor;
            return this;
        }

        public Builder normalImageRes(int normalImageRes) {
            this.normalImageRes = normalImageRes;
            return this;
        }

        public Builder highlightedImageRes(int highlightedImageRes) {
            this.highlightedImageRes = highlightedImageRes;
            return this;
        }

        public Builder unableImageRes(int unableImageRes) {
            this.unableImageRes = unableImageRes;
            return this;
        }

        public Builder normalImageDrawable(Drawable normalImageDrawable) {
            this.normalImageDrawable = normalImageDrawable;
            return this;
        }

        public Builder highlightedImageDrawable(Drawable highlightedImageDrawable) {
            this.highlightedImageDrawable = highlightedImageDrawable;
            return this;
        }

        public Builder unableImageDrawable(Drawable unableImageDrawable) {
            this.unableImageDrawable = unableImageDrawable;
            return this;
        }

        public Builder imageRect(Rect imageRect) {
            this.imageRect = imageRect;
            return this;
        }

        public Builder imagePadding(Rect imagePadding) {
            this.imagePadding = imagePadding;
            return this;
        }

        public Builder rippleEffect(boolean rippleEffect) {
            this.rippleEffect = rippleEffect;
            return this;
        }

        public Builder normalColor(int normalColor) {
            this.normalColor = normalColor;
            return this;
        }

        public Builder normalColorRes(int normalColorRes) {
            this.normalColorRes = Integer.valueOf(normalColorRes);
            return this;
        }

        public Builder highlightedColor(int highlightedColor) {
            this.highlightedColor = highlightedColor;
            return this;
        }

        public Builder highlightedColorRes(int highlightedColorRes) {
            this.highlightedColorRes = Integer.valueOf(highlightedColorRes);
            return this;
        }

        public Builder unableColor(int unableColor) {
            this.unableColor = unableColor;
            return this;
        }

        public Builder unableColorRes(int unableColorRes) {
            this.unableColorRes = Integer.valueOf(unableColorRes);
            return this;
        }

        public Builder pieceColor(int pieceColor) {
            this.pieceColor = Integer.valueOf(pieceColor);
            return this;
        }

        public Builder pieceColorRes(int pieceColorRes) {
            this.pieceColorRes = Integer.valueOf(pieceColorRes);
            return this;
        }

        public Builder unable(boolean unable) {
            this.unable = unable;
            return this;
        }

        public Builder buttonRadius(int buttonRadius) {
            this.buttonRadius = buttonRadius;
            return this;
        }

        public int getButtonRadius() {
            return this.buttonRadius;
        }

        public SimpleCircleButton build(Context context) {
            return new SimpleCircleButton(context, null);
        }
    }

    private SimpleCircleButton(Builder builder, Context context) {
        super(context);
        this.context = context;
        init(builder);
    }

    private void init(Builder builder) {
        LayoutInflater.from(this.context).inflate(C0763R.layout.bmb_simple_circle_button, this, true);
        initAttrs(builder);
        initShadow(this.buttonRadius + this.shadowRadius);
        initCircleButton();
        initImage();
        this.centerPoint = new PointF((float) ((this.buttonRadius + this.shadowRadius) + this.shadowOffsetX), (float) ((this.buttonRadius + this.shadowRadius) + this.shadowOffsetY));
    }

    private void initAttrs(Builder builder) {
        super.initAttrs(builder);
    }

    public ArrayList<View> goneViews() {
        ArrayList<View> goneViews = new ArrayList();
        goneViews.add(this.image);
        return goneViews;
    }

    public ArrayList<View> rotateViews() {
        ArrayList<View> rotateViews = new ArrayList();
        if (this.rotateImage) {
            rotateViews.add(this.image);
        }
        return rotateViews;
    }

    public int trueWidth() {
        return ((this.buttonRadius * 2) + (this.shadowRadius * 2)) + (this.shadowOffsetX * 2);
    }

    public int trueHeight() {
        return ((this.buttonRadius * 2) + (this.shadowRadius * 2)) + (this.shadowOffsetY * 2);
    }

    public int contentWidth() {
        return this.buttonRadius * 2;
    }

    public int contentHeight() {
        return this.buttonRadius * 2;
    }

    public void toPress() {
        if (this.lastStateIsNormal && this.ableToHighlight) {
            toPressImage();
            this.lastStateIsNormal = false;
        }
    }

    public void toNormal() {
        if (!this.lastStateIsNormal) {
            toNormalImage();
            this.lastStateIsNormal = true;
        }
    }

    public void setRotateAnchorPoints() {
        this.image.setPivotX((float) (this.buttonRadius - this.imageRect.left));
        this.image.setPivotY((float) (this.buttonRadius - this.imageRect.top));
    }

    public void setSelfScaleAnchorPoints() {
    }
}
