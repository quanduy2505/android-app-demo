package com.nightonke.boommenu.BoomButtons;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build.VERSION;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.common.ConnectionResult;
import com.nightonke.boommenu.BMBShadow;
import com.nightonke.boommenu.BoomButtons.TextOutsideCircleButton.Builder;
import com.nightonke.boommenu.C0763R;
import com.nightonke.boommenu.Util;
import java.util.ArrayList;
import rx.internal.operators.OnSubscribeConcatMap;
import rx.internal.schedulers.NewThreadWorker;

public abstract class BoomButton extends FrameLayout {
    protected boolean ableToHighlight;
    protected FrameLayout button;
    protected int buttonCornerRadius;
    protected int buttonHeight;
    protected int buttonRadius;
    protected int buttonWidth;
    public PointF centerPoint;
    protected boolean containsSubText;
    protected Context context;
    protected TruncateAt ellipsize;
    protected int highlightedColor;
    protected Integer highlightedColorRes;
    protected Drawable highlightedImageDrawable;
    protected int highlightedImageRes;
    protected String highlightedText;
    protected int highlightedTextColor;
    protected int highlightedTextRes;
    protected ImageView image;
    protected Rect imagePadding;
    protected Rect imageRect;
    protected int index;
    protected boolean lastStateIsNormal;
    protected ViewGroup layout;
    protected InnerOnBoomButtonClickListener listener;
    protected int maxLines;
    protected StateListDrawable nonRippleBitmapDrawable;
    protected GradientDrawable nonRippleGradientDrawable;
    protected int normalColor;
    protected Integer normalColorRes;
    protected Drawable normalImageDrawable;
    protected int normalImageRes;
    protected String normalText;
    protected int normalTextColor;
    protected int normalTextRes;
    protected OnBMClickListener onBMClickListener;
    protected Integer pieceColor;
    protected Integer pieceColorRes;
    protected RippleDrawable rippleDrawable;
    protected boolean rippleEffect;
    protected boolean rippleEffectWorks;
    protected boolean rotateImage;
    protected boolean rotateText;
    protected BMBShadow shadow;
    protected int shadowColor;
    protected int shadowCornerRadius;
    protected boolean shadowEffect;
    protected int shadowOffsetX;
    protected int shadowOffsetY;
    protected int shadowRadius;
    protected TruncateAt subEllipsize;
    protected String subHighlightedText;
    protected int subHighlightedTextColor;
    protected int subHighlightedTextRes;
    protected int subMaxLines;
    protected String subNormalText;
    protected int subNormalTextColor;
    protected int subNormalTextRes;
    protected TextView subText;
    protected int subTextGravity;
    protected Rect subTextPadding;
    protected Rect subTextRect;
    protected int subTextSize;
    protected Typeface subTypeface;
    protected String subUnableText;
    protected int subUnableTextColor;
    protected int subUnableTextRes;
    protected TextView text;
    protected int textGravity;
    protected int textHeight;
    protected Rect textPadding;
    protected Rect textRect;
    protected int textSize;
    protected int textTopMargin;
    protected int textWidth;
    protected int trueRadius;
    protected Typeface typeface;
    protected boolean unable;
    protected int unableColor;
    protected Integer unableColorRes;
    protected Drawable unableImageDrawable;
    protected int unableImageRes;
    protected String unableText;
    protected int unableTextColor;
    protected int unableTextRes;

    /* renamed from: com.nightonke.boommenu.BoomButtons.BoomButton.1 */
    class C07391 implements Runnable {
        C07391() {
        }

        public void run() {
            BoomButton.this.text.setSelected(true);
        }
    }

    /* renamed from: com.nightonke.boommenu.BoomButtons.BoomButton.2 */
    class C07402 implements Runnable {
        C07402() {
        }

        public void run() {
            BoomButton.this.subText.setSelected(true);
        }
    }

    /* renamed from: com.nightonke.boommenu.BoomButtons.BoomButton.3 */
    class C07413 implements OnClickListener {
        C07413() {
        }

        public void onClick(View v) {
            if (BoomButton.this.listener != null) {
                BoomButton.this.listener.onButtonClick(BoomButton.this.index, BoomButton.this);
            }
            if (BoomButton.this.onBMClickListener != null) {
                BoomButton.this.onBMClickListener.onBoomButtonClick(BoomButton.this.index);
            }
        }
    }

    /* renamed from: com.nightonke.boommenu.BoomButtons.BoomButton.4 */
    class C07424 implements OnTouchListener {
        C07424() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case NewThreadWorker.PURGE_FREQUENCY /*0*/:
                    if (Util.pointInView(new PointF(event.getX(), event.getY()), BoomButton.this.button)) {
                        BoomButton.this.toPress();
                        BoomButton.this.ableToHighlight = true;
                        break;
                    }
                    break;
                case OnSubscribeConcatMap.BOUNDARY /*1*/:
                case ConnectionResult.SERVICE_DISABLED /*3*/:
                    BoomButton.this.ableToHighlight = false;
                    BoomButton.this.toNormal();
                    break;
                case OnSubscribeConcatMap.END /*2*/:
                    if (!Util.pointInView(new PointF(event.getX(), event.getY()), BoomButton.this.button)) {
                        BoomButton.this.ableToHighlight = false;
                        BoomButton.this.toNormal();
                        break;
                    }
                    BoomButton.this.toPress();
                    break;
            }
            return false;
        }
    }

    /* renamed from: com.nightonke.boommenu.BoomButtons.BoomButton.5 */
    class C07435 implements OnClickListener {
        C07435() {
        }

        public void onClick(View v) {
            if (BoomButton.this.listener != null) {
                BoomButton.this.listener.onButtonClick(BoomButton.this.index, BoomButton.this);
            }
            if (BoomButton.this.onBMClickListener != null) {
                BoomButton.this.onBMClickListener.onBoomButtonClick(BoomButton.this.index);
            }
        }
    }

    /* renamed from: com.nightonke.boommenu.BoomButtons.BoomButton.6 */
    class C07446 implements OnTouchListener {
        C07446() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case NewThreadWorker.PURGE_FREQUENCY /*0*/:
                    if (Util.pointInView(new PointF(event.getX(), event.getY()), BoomButton.this.button)) {
                        BoomButton.this.toPress();
                        BoomButton.this.ableToHighlight = true;
                        break;
                    }
                    break;
                case OnSubscribeConcatMap.BOUNDARY /*1*/:
                case ConnectionResult.SERVICE_DISABLED /*3*/:
                    BoomButton.this.ableToHighlight = false;
                    BoomButton.this.toNormal();
                    break;
                case OnSubscribeConcatMap.END /*2*/:
                    if (!Util.pointInView(new PointF(event.getX(), event.getY()), BoomButton.this.button)) {
                        BoomButton.this.ableToHighlight = false;
                        BoomButton.this.toNormal();
                        break;
                    }
                    BoomButton.this.toPress();
                    break;
            }
            return false;
        }
    }

    public abstract int contentHeight();

    public abstract int contentWidth();

    public abstract ArrayList<View> goneViews();

    public abstract ArrayList<View> rotateViews();

    public abstract void setRotateAnchorPoints();

    public abstract void setSelfScaleAnchorPoints();

    protected abstract void toNormal();

    protected abstract void toPress();

    public abstract int trueHeight();

    public abstract int trueWidth();

    protected BoomButton(Context context) {
        super(context);
        this.index = -1;
        this.lastStateIsNormal = true;
        this.ableToHighlight = true;
        this.pieceColor = null;
        this.pieceColorRes = null;
        this.shadowEffect = true;
        this.shadowOffsetX = 0;
        this.shadowOffsetY = 0;
        this.shadowRadius = 0;
        this.shadowCornerRadius = 0;
        this.normalImageRes = -1;
        this.highlightedImageRes = -1;
        this.unableImageRes = -1;
        this.imageRect = null;
        this.imagePadding = null;
        this.normalTextRes = -1;
        this.highlightedTextRes = -1;
        this.unableTextRes = -1;
        this.textRect = null;
        this.textPadding = null;
        this.subNormalTextRes = -1;
        this.subHighlightedTextRes = -1;
        this.subUnableTextRes = -1;
        this.subTextRect = null;
        this.subTextPadding = null;
        this.rippleEffect = true;
        this.normalColorRes = null;
        this.highlightedColorRes = null;
        this.unableColorRes = null;
        this.unable = false;
        this.rippleEffectWorks = true;
    }

    protected void initAttrs(BoomButtonBuilder builder) {
        this.index = builder.index;
        this.listener = builder.listener;
        this.onBMClickListener = builder.onBMClickListener;
        this.rotateImage = builder.rotateImage;
        this.rotateText = builder.rotateText;
        this.containsSubText = builder.containsSubText;
        this.pieceColor = builder.pieceColor;
        this.pieceColorRes = builder.pieceColorRes;
        this.shadowEffect = builder.shadowEffect;
        if (this.shadowEffect) {
            this.shadowOffsetX = builder.shadowOffsetX;
            this.shadowOffsetY = builder.shadowOffsetY;
            this.shadowRadius = builder.shadowRadius;
            this.shadowCornerRadius = builder.shadowCornerRadius;
            this.shadowColor = builder.shadowColor;
        }
        this.normalImageRes = builder.normalImageRes;
        this.highlightedImageRes = builder.highlightedImageRes;
        this.unableImageRes = builder.unableImageRes;
        this.normalImageDrawable = builder.normalImageDrawable;
        this.highlightedImageDrawable = builder.highlightedImageDrawable;
        this.unableImageDrawable = builder.unableImageDrawable;
        this.imageRect = builder.imageRect;
        this.imagePadding = builder.imagePadding;
        this.normalTextRes = builder.normalTextRes;
        this.highlightedTextRes = builder.highlightedTextRes;
        this.unableTextRes = builder.unableTextRes;
        this.normalText = builder.normalText;
        this.highlightedText = builder.highlightedText;
        this.unableText = builder.unableText;
        this.normalTextColor = builder.normalTextColor;
        this.highlightedTextColor = builder.highlightedTextColor;
        this.unableTextColor = builder.unableTextColor;
        this.textRect = builder.textRect;
        this.textPadding = builder.textPadding;
        this.typeface = builder.typeface;
        this.maxLines = builder.maxLines;
        this.textGravity = builder.textGravity;
        this.ellipsize = builder.ellipsize;
        this.textSize = builder.textSize;
        this.subNormalTextRes = builder.subNormalTextRes;
        this.subHighlightedTextRes = builder.subHighlightedTextRes;
        this.subUnableTextRes = builder.subUnableTextRes;
        this.subNormalText = builder.subNormalText;
        this.subHighlightedText = builder.subHighlightedText;
        this.subUnableText = builder.subUnableText;
        this.subNormalTextColor = builder.subNormalTextColor;
        this.subHighlightedTextColor = builder.subHighlightedTextColor;
        this.subUnableTextColor = builder.subUnableTextColor;
        this.subTextRect = builder.subTextRect;
        this.subTextPadding = builder.subTextPadding;
        this.subTypeface = builder.subTypeface;
        this.subMaxLines = builder.subMaxLines;
        this.subTextGravity = builder.subTextGravity;
        this.subEllipsize = builder.subEllipsize;
        this.subTextSize = builder.subTextSize;
        this.rippleEffect = builder.rippleEffect;
        this.normalColor = builder.normalColor;
        this.normalColorRes = builder.normalColorRes;
        this.highlightedColor = builder.highlightedColor;
        this.highlightedColorRes = builder.highlightedColorRes;
        this.unableColor = builder.unableColor;
        this.unableColorRes = builder.unableColorRes;
        this.unable = builder.unable;
        this.buttonRadius = builder.buttonRadius;
        this.buttonWidth = builder.buttonWidth;
        this.buttonHeight = builder.buttonHeight;
        this.buttonCornerRadius = builder.buttonCornerRadius;
        boolean z = this.rippleEffect && VERSION.SDK_INT >= 21;
        this.rippleEffectWorks = z;
        this.textTopMargin = builder.textTopMargin;
        this.textWidth = builder.textWidth;
        this.textHeight = builder.textHeight;
        if (builder instanceof Builder) {
            int buttonAndShadowWidth = ((this.buttonRadius * 2) + (this.shadowOffsetX * 2)) + (this.shadowRadius * 2);
            if (this.textWidth > buttonAndShadowWidth) {
                this.textRect = new Rect(0, ((this.shadowOffsetY + this.shadowRadius) + (this.buttonRadius * 2)) + this.textTopMargin, this.textWidth, (((this.shadowOffsetY + this.shadowRadius) + (this.buttonRadius * 2)) + this.textTopMargin) + this.textHeight);
            } else {
                this.textRect = new Rect((buttonAndShadowWidth - this.textWidth) / 2, ((this.shadowOffsetY + this.shadowRadius) + (this.buttonRadius * 2)) + this.textTopMargin, ((buttonAndShadowWidth - this.textWidth) / 2) + this.textWidth, (((this.shadowOffsetY + this.shadowRadius) + (this.buttonRadius * 2)) + this.textTopMargin) + this.textHeight);
            }
            this.trueRadius = (int) (Util.distance(new Point((this.shadowOffsetX + this.shadowRadius) + this.buttonRadius, (this.shadowOffsetY + this.shadowRadius) + this.buttonRadius), new Point(this.textRect.right, this.textRect.bottom)) + 1.0f);
            if (this.textWidth > buttonAndShadowWidth) {
                this.textRect.offset(this.trueRadius - (this.textWidth / 2), this.trueRadius - ((this.shadowOffsetY + this.shadowRadius) + this.buttonRadius));
            } else {
                this.textRect.offset(this.trueRadius - ((this.shadowOffsetX + this.shadowRadius) + this.buttonRadius), this.trueRadius - ((this.shadowOffsetY + this.shadowRadius) + this.buttonRadius));
            }
        }
    }

    protected void initTextOutsideCircleButtonLayout() {
        this.layout = (ViewGroup) findViewById(C0763R.id.layout);
        this.layout.setLayoutParams(new LayoutParams(this.trueRadius * 2, this.trueRadius * 2));
    }

    protected void initShadow(int shadowCornerRadius) {
        if (this.shadowEffect) {
            this.shadow = (BMBShadow) findViewById(C0763R.id.shadow);
            this.shadow.setShadowOffsetX(this.shadowOffsetX);
            this.shadow.setShadowOffsetY(this.shadowOffsetY);
            this.shadow.setShadowColor(this.shadowColor);
            this.shadow.setShadowRadius(this.shadowRadius);
            this.shadow.setShadowCornerRadius(shadowCornerRadius);
        }
    }

    protected void initImage() {
        this.image = new ImageView(this.context);
        LayoutParams params = new LayoutParams(this.imageRect.right - this.imageRect.left, this.imageRect.bottom - this.imageRect.top);
        params.leftMargin = this.imageRect.left;
        if (VERSION.SDK_INT >= 17) {
            params.setMarginStart(this.imageRect.left);
        }
        params.topMargin = this.imageRect.top;
        if (this.imagePadding != null) {
            this.image.setPadding(this.imagePadding.left, this.imagePadding.top, this.imagePadding.right, this.imagePadding.bottom);
        }
        this.button.addView(this.image, params);
        this.lastStateIsNormal = false;
        toNormal();
    }

    protected void initText(ViewGroup parent) {
        this.text = new TextView(this.context);
        LayoutParams params = new LayoutParams(this.textRect.right - this.textRect.left, this.textRect.bottom - this.textRect.top);
        params.leftMargin = this.textRect.left;
        if (VERSION.SDK_INT >= 17) {
            params.setMarginStart(this.textRect.left);
        }
        params.topMargin = this.textRect.top;
        if (this.textPadding != null) {
            this.text.setPadding(this.textPadding.left, this.textPadding.top, this.textPadding.right, this.textPadding.bottom);
        }
        if (this.typeface != null) {
            this.text.setTypeface(this.typeface);
        }
        this.text.setMaxLines(this.maxLines);
        this.text.setTextSize(2, (float) this.textSize);
        this.text.setGravity(this.textGravity);
        this.text.setEllipsize(this.ellipsize);
        if (this.ellipsize == TruncateAt.MARQUEE) {
            this.text.setSingleLine(true);
            this.text.setMarqueeRepeatLimit(-1);
            this.text.setHorizontallyScrolling(true);
            this.text.setFocusable(true);
            this.text.setFocusableInTouchMode(true);
            this.text.setFreezesText(true);
            post(new C07391());
        }
        parent.addView(this.text, params);
    }

    protected void initSubText(ViewGroup parent) {
        if (this.containsSubText) {
            this.subText = new TextView(this.context);
            LayoutParams params = new LayoutParams(this.subTextRect.right - this.subTextRect.left, this.subTextRect.bottom - this.subTextRect.top);
            params.leftMargin = this.subTextRect.left;
            if (VERSION.SDK_INT >= 17) {
                params.setMarginStart(this.subTextRect.left);
            }
            params.topMargin = this.subTextRect.top;
            if (this.subTextPadding != null) {
                this.subText.setPadding(this.subTextPadding.left, this.subTextPadding.top, this.subTextPadding.right, this.subTextPadding.bottom);
            }
            if (this.subTypeface != null) {
                this.subText.setTypeface(this.subTypeface);
            }
            this.subText.setMaxLines(this.maxLines);
            this.subText.setTextSize(2, (float) this.subTextSize);
            this.subText.setGravity(this.subTextGravity);
            this.subText.setEllipsize(this.subEllipsize);
            if (this.subEllipsize == TruncateAt.MARQUEE) {
                this.subText.setSingleLine(true);
                this.subText.setMarqueeRepeatLimit(-1);
                this.subText.setHorizontallyScrolling(true);
                this.subText.setFocusable(true);
                this.subText.setFocusableInTouchMode(true);
                this.subText.setFreezesText(true);
                post(new C07402());
            }
            parent.addView(this.subText, params);
        }
    }

    @SuppressLint({"NewApi"})
    protected void initCircleButtonDrawable() {
        if (this.rippleEffectWorks) {
            RippleDrawable rippleDrawable = new RippleDrawable(ColorStateList.valueOf(highlightedColor()), Util.getOvalDrawable(this.button, this.unable ? unableColor() : normalColor()), null);
            Util.setDrawable(this.button, rippleDrawable);
            this.rippleDrawable = rippleDrawable;
            return;
        }
        this.nonRippleBitmapDrawable = Util.getOvalStateListBitmapDrawable(this.button, this.buttonRadius, normalColor(), highlightedColor(), unableColor());
        if (isNeededColorAnimation()) {
            this.nonRippleGradientDrawable = Util.getOvalDrawable(this.button, this.unable ? unableColor() : normalColor());
        }
        Util.setDrawable(this.button, this.nonRippleBitmapDrawable);
    }

    @SuppressLint({"NewApi"})
    protected void initCircleButton() {
        this.button = (FrameLayout) findViewById(C0763R.id.button);
        LayoutParams params = (LayoutParams) this.button.getLayoutParams();
        params.width = this.buttonRadius * 2;
        params.height = this.buttonRadius * 2;
        this.button.setLayoutParams(params);
        this.button.setEnabled(!this.unable);
        this.button.setOnClickListener(new C07413());
        initCircleButtonDrawable();
        this.button.setOnTouchListener(new C07424());
    }

    @SuppressLint({"NewApi"})
    protected void initHamButtonDrawable() {
        if (this.rippleEffectWorks) {
            RippleDrawable rippleDrawable = new RippleDrawable(ColorStateList.valueOf(highlightedColor()), Util.getRectangleDrawable(this.button, this.buttonCornerRadius, this.unable ? unableColor() : normalColor()), null);
            Util.setDrawable(this.button, rippleDrawable);
            this.rippleDrawable = rippleDrawable;
            return;
        }
        this.nonRippleBitmapDrawable = Util.getRectangleStateListBitmapDrawable(this.button, this.buttonWidth, this.buttonHeight, this.buttonCornerRadius, normalColor(), highlightedColor(), unableColor());
        if (isNeededColorAnimation()) {
            this.nonRippleGradientDrawable = Util.getRectangleDrawable(this.button, this.buttonCornerRadius, this.unable ? unableColor() : normalColor());
        }
        Util.setDrawable(this.button, this.nonRippleBitmapDrawable);
    }

    @SuppressLint({"NewApi"})
    protected void initHamButton() {
        this.button = (FrameLayout) findViewById(C0763R.id.button);
        LayoutParams params = (LayoutParams) this.button.getLayoutParams();
        params.width = this.buttonWidth;
        params.height = this.buttonHeight;
        this.button.setLayoutParams(params);
        this.button.setEnabled(!this.unable);
        this.button.setOnClickListener(new C07435());
        initHamButtonDrawable();
        this.button.setOnTouchListener(new C07446());
    }

    public LayoutParams place(int left, int top, int width, int height) {
        LayoutParams layoutParams = new LayoutParams(width, height);
        layoutParams.leftMargin = left;
        layoutParams.topMargin = top;
        setLayoutParams(layoutParams);
        return layoutParams;
    }

    protected void toPressImage() {
        if (this.unable && this.unableImageRes != -1) {
            this.image.setImageResource(this.unableImageRes);
        } else if (this.unable && this.unableImageDrawable != null) {
            this.image.setImageDrawable(this.unableImageDrawable);
        } else if (this.highlightedImageRes != -1) {
            this.image.setImageResource(this.highlightedImageRes);
        } else if (this.highlightedImageDrawable != null) {
            this.image.setImageDrawable(this.highlightedImageDrawable);
        }
    }

    protected void toNormalImage() {
        if (this.unable && this.unableImageRes != -1) {
            this.image.setImageResource(this.unableImageRes);
        } else if (this.unable && this.unableImageDrawable != null) {
            this.image.setImageDrawable(this.unableImageDrawable);
        } else if (this.normalImageRes != -1) {
            this.image.setImageResource(this.normalImageRes);
        } else if (this.normalImageDrawable != null) {
            this.image.setImageDrawable(this.normalImageDrawable);
        }
    }

    protected void toPressText() {
        if (this.unable && this.unableTextRes != -1) {
            setText(this.unableTextRes);
        } else if (this.unable && this.unableText != null) {
            setText(this.unableText);
        } else if (this.highlightedTextRes != -1) {
            setText(this.highlightedTextRes);
        } else if (this.highlightedText != null) {
            setText(this.highlightedText);
        }
        if (this.unable) {
            this.text.setTextColor(this.unableTextColor);
        } else {
            this.text.setTextColor(this.highlightedTextColor);
        }
    }

    protected void toNormalText() {
        if (this.unable && this.unableTextRes != -1) {
            setText(this.unableTextRes);
        } else if (this.unable && this.unableText != null) {
            setText(this.unableText);
        } else if (this.normalTextRes != -1) {
            setText(this.normalTextRes);
        } else if (this.normalText != null) {
            setText(this.normalText);
        }
        if (this.unable) {
            this.text.setTextColor(this.unableTextColor);
        } else {
            this.text.setTextColor(this.normalTextColor);
        }
    }

    protected void toPressSubText() {
        if (this.unable && this.subUnableTextRes != -1) {
            setSubText(this.subUnableTextRes);
        } else if (this.unable && this.subUnableText != null) {
            setSubText(this.subUnableText);
        } else if (this.subHighlightedTextRes != -1) {
            setSubText(this.subHighlightedTextRes);
        } else if (this.subHighlightedText != null) {
            setSubText(this.subHighlightedText);
        }
        if (this.subText == null) {
            return;
        }
        if (this.unable) {
            this.subText.setTextColor(this.subUnableTextColor);
        } else {
            this.subText.setTextColor(this.subHighlightedTextColor);
        }
    }

    protected void toNormalSubText() {
        if (this.unable && this.subUnableTextRes != -1) {
            setSubText(this.subUnableTextRes);
        } else if (this.unable && this.subUnableText != null) {
            setSubText(this.subUnableText);
        } else if (this.subNormalTextRes != -1) {
            setSubText(this.subNormalTextRes);
        } else if (this.subNormalText != null) {
            setSubText(this.subNormalText);
        }
        if (this.subText == null) {
            return;
        }
        if (this.unable) {
            this.subText.setTextColor(this.subUnableTextColor);
        } else {
            this.subText.setTextColor(this.subNormalTextColor);
        }
    }

    private void setText(int stringRes) {
        setText((String) getContext().getResources().getText(stringRes));
    }

    private void setText(String string) {
        if (string != null && !string.equals(this.text.getText())) {
            this.text.setText(string);
        }
    }

    private void setSubText(int stringRes) {
        setSubText((String) getContext().getResources().getText(stringRes));
    }

    private void setSubText(String string) {
        if (string != null && this.subText != null && !string.equals(this.subText.getText())) {
            this.subText.setText(string);
        }
    }

    public int pieceColor() {
        if (this.pieceColor == null && this.pieceColorRes == null) {
            if (this.unable) {
                return unableColor();
            }
            return normalColor();
        } else if (this.pieceColor == null) {
            return Util.getColor(this.context, this.pieceColorRes.intValue());
        } else {
            return Util.getColor(this.context, this.pieceColorRes, this.pieceColor.intValue());
        }
    }

    public int buttonColor() {
        if (this.unable) {
            return unableColor();
        }
        return normalColor();
    }

    public boolean isNeededColorAnimation() {
        if (this.pieceColor == null) {
            return false;
        }
        if (this.unable) {
            if (this.pieceColor.compareTo(Integer.valueOf(unableColor())) == 0) {
                return false;
            }
            return true;
        } else if (this.pieceColor.compareTo(Integer.valueOf(normalColor())) == 0) {
            return false;
        } else {
            return true;
        }
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        this.unable = !enabled;
    }

    public void cleanListener() {
        this.listener = null;
    }

    public void willShow() {
        if (!this.rippleEffectWorks && isNeededColorAnimation()) {
            Util.setDrawable(this.button, this.nonRippleGradientDrawable);
        }
    }

    public void didShow() {
        if (!this.rippleEffectWorks && isNeededColorAnimation()) {
            Util.setDrawable(this.button, this.nonRippleBitmapDrawable);
        }
    }

    public void willHide() {
        if (!this.rippleEffectWorks && isNeededColorAnimation()) {
            Util.setDrawable(this.button, this.nonRippleGradientDrawable);
        }
    }

    public void didHide() {
    }

    public boolean prepareColorTransformAnimation() {
        if (this.rippleEffectWorks) {
            if (this.rippleDrawable == null) {
                throw new RuntimeException("Background drawable is null!");
            }
        } else if (this.nonRippleGradientDrawable == null) {
            throw new RuntimeException("Background drawable is null!");
        }
        return this.rippleEffectWorks;
    }

    protected void setNonRippleButtonColor(int color) {
        this.nonRippleGradientDrawable.setColor(color);
    }

    protected void setRippleButtonColor(int color) {
        ((GradientDrawable) this.rippleDrawable.getDrawable(0)).setColor(color);
    }

    public void setClickable(boolean clickable) {
        super.setClickable(clickable);
        this.button.setClickable(clickable);
    }

    protected int normalColor() {
        return Util.getColor(this.context, this.normalColorRes, this.normalColor);
    }

    protected int highlightedColor() {
        return Util.getColor(this.context, this.highlightedColorRes, this.highlightedColor);
    }

    protected int unableColor() {
        return Util.getColor(this.context, this.unableColorRes, this.unableColor);
    }
}
