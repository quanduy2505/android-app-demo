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
import com.nightonke.boommenu.Util;
import java.util.ArrayList;

public class HamButton extends BoomButton {

    public static class Builder extends BoomButtonBuilder {
        public Builder() {
            this.imageRect = new Rect(0, 0, Util.dp2px(60.0f), Util.dp2px(60.0f));
            this.textRect = new Rect(Util.dp2px(70.0f), Util.dp2px(10.0f), Util.dp2px(280.0f), Util.dp2px(40.0f));
            this.textGravity = 8388627;
            this.textSize = 15;
        }

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

        public Builder containsSubText(boolean containsSubText) {
            this.containsSubText = containsSubText;
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

        public Builder shadowCornerRadius(int shadowCornerRadius) {
            this.shadowCornerRadius = shadowCornerRadius;
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

        public Builder textRect(Rect textRect) {
            this.textRect = textRect;
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

        public Builder subNormalTextRes(int subNormalTextRes) {
            this.subNormalTextRes = subNormalTextRes;
            return this;
        }

        public Builder subHighlightedTextRes(int subHighlightedTextRes) {
            this.subHighlightedTextRes = subHighlightedTextRes;
            return this;
        }

        public Builder subUnableTextRes(int subUnableTextRes) {
            this.subUnableTextRes = subUnableTextRes;
            return this;
        }

        public Builder subNormalText(String subNormalText) {
            this.subNormalText = subNormalText;
            return this;
        }

        public Builder subHighlightedText(String subHighlightedText) {
            this.subHighlightedText = subHighlightedText;
            return this;
        }

        public Builder subUnableText(String subUnableText) {
            this.subUnableText = subUnableText;
            return this;
        }

        public Builder subNormalTextColor(int subNormalTextColor) {
            this.subNormalTextColor = subNormalTextColor;
            return this;
        }

        public Builder subHighlightedTextColor(int subHighlightedTextColor) {
            this.subHighlightedTextColor = subHighlightedTextColor;
            return this;
        }

        public Builder subUnableTextColor(int subUnableTextColor) {
            this.subUnableTextColor = subUnableTextColor;
            return this;
        }

        public Builder subTextRect(Rect subTextRect) {
            this.subTextRect = subTextRect;
            return this;
        }

        public Builder subTextPadding(Rect subTextPadding) {
            this.subTextPadding = subTextPadding;
            return this;
        }

        public Builder subTypeface(Typeface subTypeface) {
            this.subTypeface = subTypeface;
            return this;
        }

        public Builder subMaxLines(int subMaxLines) {
            this.subMaxLines = subMaxLines;
            return this;
        }

        public Builder subTextGravity(int subTextGravity) {
            this.subTextGravity = subTextGravity;
            return this;
        }

        public Builder subEllipsize(TruncateAt subEllipsize) {
            this.subEllipsize = subEllipsize;
            return this;
        }

        public Builder subTextSize(int subTextSize) {
            this.subTextSize = subTextSize;
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

        public Builder buttonWidth(int buttonWidth) {
            this.buttonWidth = buttonWidth;
            return this;
        }

        public Builder buttonHeight(int buttonHeight) {
            this.buttonHeight = buttonHeight;
            return this;
        }

        public Builder buttonCornerRadius(int buttonCornerRadius) {
            this.buttonCornerRadius = buttonCornerRadius;
            return this;
        }

        public int getButtonWidth() {
            return this.buttonWidth;
        }

        public int getButtonHeight() {
            return this.buttonHeight;
        }

        public HamButton build(Context context) {
            return new HamButton(context, null);
        }
    }

    private HamButton(Builder builder, Context context) {
        super(context);
        this.context = context;
        init(builder);
    }

    private void init(Builder builder) {
        LayoutInflater.from(this.context).inflate(C0763R.layout.bmb_ham_button, this, true);
        initAttrs(builder);
        initShadow(builder.shadowCornerRadius);
        initHamButton();
        initText(this.button);
        initSubText(this.button);
        initImage();
        this.centerPoint = new PointF(((((float) this.buttonWidth) / 2.0f) + ((float) this.shadowRadius)) + ((float) this.shadowOffsetX), ((((float) this.buttonHeight) / 2.0f) + ((float) this.shadowRadius)) + ((float) this.shadowOffsetY));
    }

    private void initAttrs(Builder builder) {
        super.initAttrs(builder);
    }

    public ArrayList<View> goneViews() {
        ArrayList<View> goneViews = new ArrayList();
        goneViews.add(this.image);
        goneViews.add(this.text);
        if (this.subText != null) {
            goneViews.add(this.subText);
        }
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
        return (this.buttonWidth + (this.shadowRadius * 2)) + (this.shadowOffsetX * 2);
    }

    public int trueHeight() {
        return (this.buttonHeight + (this.shadowRadius * 2)) + (this.shadowOffsetY * 2);
    }

    public int contentWidth() {
        return this.buttonWidth;
    }

    public int contentHeight() {
        return this.buttonHeight;
    }

    public void toPress() {
        if (this.lastStateIsNormal && this.ableToHighlight) {
            toPressImage();
            toPressText();
            toPressSubText();
            this.lastStateIsNormal = false;
        }
    }

    public void toNormal() {
        if (!this.lastStateIsNormal) {
            toNormalImage();
            toNormalText();
            toNormalSubText();
            this.lastStateIsNormal = true;
        }
    }

    public void setRotateAnchorPoints() {
    }

    public void setSelfScaleAnchorPoints() {
    }
}
