package com.nightonke.boommenu.BoomButtons;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils.TruncateAt;
import android.view.LayoutInflater;
import android.view.View;
import com.nightonke.boommenu.C0763R;
import java.util.ArrayList;

public class TextOutsideCircleButton extends BoomButton {

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

        public Builder rotateText(boolean rotateText) {
            this.rotateText = rotateText;
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

        public Builder normalTextRes(int normalTextRes) {
            this.normalTextRes = normalTextRes;
            return this;
        }

        public Builder highlightedTextRes(int highlightedTextRes) {
            this.highlightedTextRes = highlightedTextRes;
            return this;
        }

        public Builder unableTextRes(int unableTextRes) {
            this.unableTextRes = unableTextRes;
            return this;
        }

        public Builder normalText(String normalText) {
            this.normalText = normalText;
            return this;
        }

        public Builder highlightedText(String highlightedText) {
            this.highlightedText = highlightedText;
            return this;
        }

        public Builder unableText(String unableText) {
            this.unableText = unableText;
            return this;
        }

        public Builder normalTextColor(int normalTextColor) {
            this.normalTextColor = normalTextColor;
            return this;
        }

        public Builder highlightedTextColor(int highlightedTextColor) {
            this.highlightedTextColor = highlightedTextColor;
            return this;
        }

        public Builder unableTextColor(int unableTextColor) {
            this.unableTextColor = unableTextColor;
            return this;
        }

        public Builder textTopMargin(int textTopMargin) {
            if (textTopMargin < 0) {
                textTopMargin = 0;
            }
            this.textTopMargin = textTopMargin;
            return this;
        }

        public Builder textWidth(int textWidth) {
            this.textWidth = textWidth;
            return this;
        }

        public Builder textHeight(int textHeight) {
            this.textHeight = textHeight;
            return this;
        }

        public Builder textPadding(Rect textPadding) {
            this.textPadding = textPadding;
            return this;
        }

        public Builder typeface(Typeface typeface) {
            this.typeface = typeface;
            return this;
        }

        public Builder maxLines(int maxLines) {
            this.maxLines = maxLines;
            return this;
        }

        public Builder textGravity(int gravity) {
            this.textGravity = gravity;
            return this;
        }

        public Builder ellipsize(TruncateAt ellipsize) {
            this.ellipsize = ellipsize;
            return this;
        }

        public Builder textSize(int textSize) {
            this.textSize = textSize;
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

        public int getButtonContentWidth() {
            int width = this.buttonRadius * 2;
            if (this.textRect != null) {
                return Math.max(width, this.textWidth);
            }
            return width;
        }

        public int getButtonContentHeight() {
            int height = this.buttonRadius * 2;
            if (this.textRect != null) {
                return Math.max(height, (this.textRect.bottom - this.shadowOffsetY) - this.shadowRadius);
            }
            return height;
        }

        public int getButtonRadius() {
            return this.buttonRadius;
        }

        public TextOutsideCircleButton build(Context context) {
            return new TextOutsideCircleButton(context, null);
        }
    }

    private TextOutsideCircleButton(Builder builder, Context context) {
        super(context);
        this.context = context;
        init(builder);
    }

    private void init(Builder builder) {
        LayoutInflater.from(this.context).inflate(C0763R.layout.bmb_text_outside_circle_button, this, true);
        initAttrs(builder);
        initTextOutsideCircleButtonLayout();
        initShadow(this.buttonRadius + this.shadowRadius);
        initCircleButton();
        initText(this.layout);
        initImage();
        this.centerPoint = new PointF((float) this.trueRadius, (float) this.trueRadius);
    }

    private void initAttrs(Builder builder) {
        super.initAttrs(builder);
    }

    public ArrayList<View> goneViews() {
        ArrayList<View> goneViews = new ArrayList();
        goneViews.add(this.image);
        goneViews.add(this.text);
        return goneViews;
    }

    public ArrayList<View> rotateViews() {
        ArrayList<View> rotateViews = new ArrayList();
        if (this.rotateImage) {
            rotateViews.add(this.image);
        }
        if (this.rotateText) {
            rotateViews.add(this.text);
        }
        return rotateViews;
    }

    public int trueWidth() {
        return this.trueRadius * 2;
    }

    public int trueHeight() {
        return this.trueRadius * 2;
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
            toPressText();
            this.lastStateIsNormal = false;
        }
    }

    public void toNormal() {
        if (!this.lastStateIsNormal) {
            toNormalImage();
            toNormalText();
            this.lastStateIsNormal = true;
        }
    }

    public void setRotateAnchorPoints() {
        this.image.setPivotX((float) (this.buttonRadius - this.imageRect.left));
        this.image.setPivotY((float) (this.buttonRadius - this.imageRect.top));
        this.text.setPivotX((float) (this.trueRadius - this.textRect.left));
        this.text.setPivotY((float) (this.trueRadius - this.textRect.top));
    }

    public void setSelfScaleAnchorPoints() {
    }
}
