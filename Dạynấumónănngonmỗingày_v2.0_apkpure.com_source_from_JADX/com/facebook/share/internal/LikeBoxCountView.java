package com.facebook.share.internal;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;
import com.facebook.C0378R;
import com.google.android.gms.common.ConnectionResult;
import rx.internal.operators.OnSubscribeConcatMap;

public class LikeBoxCountView extends FrameLayout {
    private int additionalTextPadding;
    private Paint borderPaint;
    private float borderRadius;
    private float caretHeight;
    private LikeBoxCountViewCaretPosition caretPosition;
    private float caretWidth;
    private TextView likeCountLabel;
    private int textPadding;

    /* renamed from: com.facebook.share.internal.LikeBoxCountView.1 */
    static /* synthetic */ class C04561 {
        static final /* synthetic */ int[] f5x5851190f;

        static {
            f5x5851190f = new int[LikeBoxCountViewCaretPosition.values().length];
            try {
                f5x5851190f[LikeBoxCountViewCaretPosition.LEFT.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f5x5851190f[LikeBoxCountViewCaretPosition.TOP.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f5x5851190f[LikeBoxCountViewCaretPosition.RIGHT.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f5x5851190f[LikeBoxCountViewCaretPosition.BOTTOM.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    public enum LikeBoxCountViewCaretPosition {
        LEFT,
        TOP,
        RIGHT,
        BOTTOM
    }

    public LikeBoxCountView(Context context) {
        super(context);
        this.caretPosition = LikeBoxCountViewCaretPosition.LEFT;
        initialize(context);
    }

    public void setText(String text) {
        this.likeCountLabel.setText(text);
    }

    public void setCaretPosition(LikeBoxCountViewCaretPosition caretPosition) {
        this.caretPosition = caretPosition;
        switch (C04561.f5x5851190f[caretPosition.ordinal()]) {
            case OnSubscribeConcatMap.BOUNDARY /*1*/:
                setAdditionalTextPadding(this.additionalTextPadding, 0, 0, 0);
            case OnSubscribeConcatMap.END /*2*/:
                setAdditionalTextPadding(0, this.additionalTextPadding, 0, 0);
            case ConnectionResult.SERVICE_DISABLED /*3*/:
                setAdditionalTextPadding(0, 0, this.additionalTextPadding, 0);
            case ConnectionResult.SIGN_IN_REQUIRED /*4*/:
                setAdditionalTextPadding(0, 0, 0, this.additionalTextPadding);
            default:
        }
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int top = getPaddingTop();
        int left = getPaddingLeft();
        int right = getWidth() - getPaddingRight();
        int bottom = getHeight() - getPaddingBottom();
        switch (C04561.f5x5851190f[this.caretPosition.ordinal()]) {
            case OnSubscribeConcatMap.BOUNDARY /*1*/:
                left = (int) (((float) left) + this.caretHeight);
                break;
            case OnSubscribeConcatMap.END /*2*/:
                top = (int) (((float) top) + this.caretHeight);
                break;
            case ConnectionResult.SERVICE_DISABLED /*3*/:
                right = (int) (((float) right) - this.caretHeight);
                break;
            case ConnectionResult.SIGN_IN_REQUIRED /*4*/:
                bottom = (int) (((float) bottom) - this.caretHeight);
                break;
        }
        drawBorder(canvas, (float) left, (float) top, (float) right, (float) bottom);
    }

    private void initialize(Context context) {
        setWillNotDraw(false);
        this.caretHeight = getResources().getDimension(C0378R.dimen.com_facebook_likeboxcountview_caret_height);
        this.caretWidth = getResources().getDimension(C0378R.dimen.com_facebook_likeboxcountview_caret_width);
        this.borderRadius = getResources().getDimension(C0378R.dimen.com_facebook_likeboxcountview_border_radius);
        this.borderPaint = new Paint();
        this.borderPaint.setColor(getResources().getColor(C0378R.color.com_facebook_likeboxcountview_border_color));
        this.borderPaint.setStrokeWidth(getResources().getDimension(C0378R.dimen.com_facebook_likeboxcountview_border_width));
        this.borderPaint.setStyle(Style.STROKE);
        initializeLikeCountLabel(context);
        addView(this.likeCountLabel);
        setCaretPosition(this.caretPosition);
    }

    private void initializeLikeCountLabel(Context context) {
        this.likeCountLabel = new TextView(context);
        this.likeCountLabel.setLayoutParams(new LayoutParams(-1, -1));
        this.likeCountLabel.setGravity(17);
        this.likeCountLabel.setTextSize(0, getResources().getDimension(C0378R.dimen.com_facebook_likeboxcountview_text_size));
        this.likeCountLabel.setTextColor(getResources().getColor(C0378R.color.com_facebook_likeboxcountview_text_color));
        this.textPadding = getResources().getDimensionPixelSize(C0378R.dimen.com_facebook_likeboxcountview_text_padding);
        this.additionalTextPadding = getResources().getDimensionPixelSize(C0378R.dimen.com_facebook_likeboxcountview_caret_height);
    }

    private void setAdditionalTextPadding(int left, int top, int right, int bottom) {
        this.likeCountLabel.setPadding(this.textPadding + left, this.textPadding + top, this.textPadding + right, this.textPadding + bottom);
    }

    private void drawBorder(Canvas canvas, float left, float top, float right, float bottom) {
        Path borderPath = new Path();
        float ovalSize = 2.0f * this.borderRadius;
        borderPath.addArc(new RectF(left, top, left + ovalSize, top + ovalSize), -180.0f, 90.0f);
        if (this.caretPosition == LikeBoxCountViewCaretPosition.TOP) {
            borderPath.lineTo((((right - left) - this.caretWidth) / 2.0f) + left, top);
            borderPath.lineTo(((right - left) / 2.0f) + left, top - this.caretHeight);
            borderPath.lineTo((((right - left) + this.caretWidth) / 2.0f) + left, top);
        }
        borderPath.lineTo(right - this.borderRadius, top);
        borderPath.addArc(new RectF(right - ovalSize, top, right, top + ovalSize), -90.0f, 90.0f);
        if (this.caretPosition == LikeBoxCountViewCaretPosition.RIGHT) {
            borderPath.lineTo(right, (((bottom - top) - this.caretWidth) / 2.0f) + top);
            borderPath.lineTo(this.caretHeight + right, ((bottom - top) / 2.0f) + top);
            borderPath.lineTo(right, (((bottom - top) + this.caretWidth) / 2.0f) + top);
        }
        borderPath.lineTo(right, bottom - this.borderRadius);
        borderPath.addArc(new RectF(right - ovalSize, bottom - ovalSize, right, bottom), 0.0f, 90.0f);
        if (this.caretPosition == LikeBoxCountViewCaretPosition.BOTTOM) {
            borderPath.lineTo((((right - left) + this.caretWidth) / 2.0f) + left, bottom);
            borderPath.lineTo(((right - left) / 2.0f) + left, this.caretHeight + bottom);
            borderPath.lineTo((((right - left) - this.caretWidth) / 2.0f) + left, bottom);
        }
        borderPath.lineTo(this.borderRadius + left, bottom);
        borderPath.addArc(new RectF(left, bottom - ovalSize, left + ovalSize, bottom), 90.0f, 90.0f);
        if (this.caretPosition == LikeBoxCountViewCaretPosition.LEFT) {
            borderPath.lineTo(left, (((bottom - top) + this.caretWidth) / 2.0f) + top);
            borderPath.lineTo(left - this.caretHeight, ((bottom - top) / 2.0f) + top);
            borderPath.lineTo(left, (((bottom - top) - this.caretWidth) / 2.0f) + top);
        }
        borderPath.lineTo(left, this.borderRadius + top);
        canvas.drawPath(borderPath, this.borderPaint);
    }
}
