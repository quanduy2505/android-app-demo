package com.nightonke.boommenu.BoomButtons;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils.TruncateAt;
import com.nightonke.boommenu.Util;

public abstract class BoomButtonBuilder {
    int buttonCornerRadius;
    int buttonHeight;
    int buttonRadius;
    int buttonWidth;
    boolean containsSubText;
    TruncateAt ellipsize;
    int highlightedColor;
    Integer highlightedColorRes;
    Drawable highlightedImageDrawable;
    int highlightedImageRes;
    String highlightedText;
    int highlightedTextColor;
    int highlightedTextRes;
    Rect imagePadding;
    Rect imageRect;
    int index;
    InnerOnBoomButtonClickListener listener;
    int maxLines;
    int normalColor;
    Integer normalColorRes;
    Drawable normalImageDrawable;
    int normalImageRes;
    String normalText;
    int normalTextColor;
    int normalTextRes;
    OnBMClickListener onBMClickListener;
    Integer pieceColor;
    Integer pieceColorRes;
    boolean rippleEffect;
    boolean rotateImage;
    boolean rotateText;
    int shadowColor;
    int shadowCornerRadius;
    boolean shadowEffect;
    int shadowOffsetX;
    int shadowOffsetY;
    int shadowRadius;
    TruncateAt subEllipsize;
    String subHighlightedText;
    int subHighlightedTextColor;
    int subHighlightedTextRes;
    int subMaxLines;
    String subNormalText;
    int subNormalTextColor;
    int subNormalTextRes;
    int subTextGravity;
    Rect subTextPadding;
    Rect subTextRect;
    int subTextSize;
    Typeface subTypeface;
    String subUnableText;
    int subUnableTextColor;
    int subUnableTextRes;
    int textGravity;
    int textHeight;
    Rect textPadding;
    Rect textRect;
    int textSize;
    int textTopMargin;
    int textWidth;
    Typeface typeface;
    boolean unable;
    int unableColor;
    Integer unableColorRes;
    Drawable unableImageDrawable;
    int unableImageRes;
    String unableText;
    int unableTextColor;
    int unableTextRes;

    public BoomButtonBuilder() {
        this.index = -1;
        this.rotateImage = true;
        this.rotateText = true;
        this.containsSubText = true;
        this.pieceColor = null;
        this.pieceColorRes = null;
        this.shadowEffect = true;
        this.shadowOffsetX = Util.dp2px(0.0f);
        this.shadowOffsetY = Util.dp2px(3.0f);
        this.shadowRadius = Util.dp2px(8.0f);
        this.shadowColor = Color.parseColor("#88757575");
        this.shadowCornerRadius = Util.dp2px(5.0f);
        this.normalImageRes = -1;
        this.highlightedImageRes = -1;
        this.unableImageRes = -1;
        this.normalImageDrawable = null;
        this.highlightedImageDrawable = null;
        this.unableImageDrawable = null;
        this.imageRect = new Rect(Util.dp2px(10.0f), Util.dp2px(10.0f), Util.dp2px(70.0f), Util.dp2px(70.0f));
        this.imagePadding = new Rect(0, 0, 0, 0);
        this.normalTextRes = -1;
        this.highlightedTextRes = -1;
        this.unableTextRes = -1;
        this.normalTextColor = -1;
        this.highlightedTextColor = -1;
        this.unableTextColor = -1;
        this.textRect = new Rect(Util.dp2px(15.0f), Util.dp2px(52.0f), Util.dp2px(65.0f), Util.dp2px(72.0f));
        this.textPadding = new Rect(0, 0, 0, 0);
        this.typeface = null;
        this.maxLines = 1;
        this.textGravity = 17;
        this.ellipsize = TruncateAt.MARQUEE;
        this.textSize = 10;
        this.subNormalTextRes = -1;
        this.subHighlightedTextRes = -1;
        this.subUnableTextRes = -1;
        this.subNormalTextColor = -1;
        this.subHighlightedTextColor = -1;
        this.subUnableTextColor = -1;
        this.subTextRect = new Rect(Util.dp2px(70.0f), Util.dp2px(35.0f), Util.dp2px(280.0f), Util.dp2px(55.0f));
        this.subTextPadding = new Rect(0, 0, 0, 0);
        this.subTypeface = null;
        this.subMaxLines = 1;
        this.subTextGravity = 8388627;
        this.subEllipsize = TruncateAt.MARQUEE;
        this.subTextSize = 10;
        this.textTopMargin = Util.dp2px(5.0f);
        this.textWidth = Util.dp2px(80.0f);
        this.textHeight = Util.dp2px(20.0f);
        this.rippleEffect = true;
        this.normalColor = Util.getColor();
        this.normalColorRes = null;
        this.highlightedColor = Util.getColor();
        this.highlightedColorRes = null;
        this.unableColor = Util.getColor();
        this.unableColorRes = null;
        this.unable = false;
        this.buttonRadius = Util.dp2px(40.0f);
        this.buttonWidth = Util.dp2px(300.0f);
        this.buttonHeight = Util.dp2px(60.0f);
        this.buttonCornerRadius = Util.dp2px(5.0f);
    }

    public int pieceColor(Context context) {
        if (this.pieceColor == null && this.pieceColorRes == null) {
            if (this.unable) {
                return Util.getColor(context, this.unableColorRes, this.unableColor);
            }
            return Util.getColor(context, this.normalColorRes, this.normalColor);
        } else if (this.pieceColor == null) {
            return Util.getColor(context, this.pieceColorRes.intValue());
        } else {
            return Util.getColor(context, this.pieceColorRes, this.pieceColor.intValue());
        }
    }

    public void setUnable(boolean unable) {
        this.unable = unable;
    }
}
