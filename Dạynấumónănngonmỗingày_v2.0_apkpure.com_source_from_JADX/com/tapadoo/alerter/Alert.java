package com.tapadoo.alerter;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.tapadoo.android.C0784R;

public class Alert extends FrameLayout implements OnClickListener, AnimationListener {
    private static final int CLEAN_UP_DELAY_MILLIS = 100;
    private static final long DISPLAY_TIME_IN_SECONDS = 3000;
    private long duration;
    private boolean enableIconPulse;
    private boolean enableInfiniteDuration;
    private FrameLayout flBackground;
    private ImageView ivIcon;
    private boolean marginSet;
    private OnHideAlertListener onHideListener;
    private OnShowAlertListener onShowListener;
    private Animation slideInAnimation;
    private Animation slideOutAnimation;
    private TextView tvText;
    private TextView tvTitle;

    /* renamed from: com.tapadoo.alerter.Alert.1 */
    class C07791 implements Runnable {
        C07791() {
        }

        public void run() {
            Alert.this.hide();
        }
    }

    /* renamed from: com.tapadoo.alerter.Alert.2 */
    class C07802 implements AnimationListener {
        C07802() {
        }

        public void onAnimationStart(Animation animation) {
            Alert.this.flBackground.setOnClickListener(null);
            Alert.this.flBackground.setClickable(false);
        }

        public void onAnimationEnd(Animation animation) {
            Alert.this.removeFromParent();
        }

        public void onAnimationRepeat(Animation animation) {
        }
    }

    /* renamed from: com.tapadoo.alerter.Alert.3 */
    class C07813 implements Runnable {
        C07813() {
        }

        public void run() {
            try {
                if (Alert.this.getParent() == null) {
                    Log.e(getClass().getSimpleName(), "getParent() returning Null");
                    return;
                }
                try {
                    ((ViewGroup) Alert.this.getParent()).removeView(Alert.this);
                    if (Alert.this.onHideListener != null) {
                        Alert.this.onHideListener.onHide();
                    }
                } catch (Exception e) {
                    Log.e(getClass().getSimpleName(), "Cannot remove from parent layout");
                }
            } catch (Exception ex) {
                Log.e(getClass().getSimpleName(), Log.getStackTraceString(ex));
            }
        }
    }

    public Alert(@NonNull Context context) {
        super(context, null, C0784R.attr.alertStyle);
        this.duration = DISPLAY_TIME_IN_SECONDS;
        this.enableIconPulse = true;
        initView();
    }

    public Alert(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, C0784R.attr.alertStyle);
        this.duration = DISPLAY_TIME_IN_SECONDS;
        this.enableIconPulse = true;
        initView();
    }

    public Alert(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.duration = DISPLAY_TIME_IN_SECONDS;
        this.enableIconPulse = true;
        initView();
    }

    private void initView() {
        inflate(getContext(), C0784R.layout.alerter_alert_view, this);
        setHapticFeedbackEnabled(true);
        this.flBackground = (FrameLayout) findViewById(C0784R.id.flAlertBackground);
        this.ivIcon = (ImageView) findViewById(C0784R.id.ivIcon);
        this.tvTitle = (TextView) findViewById(C0784R.id.tvTitle);
        this.tvText = (TextView) findViewById(C0784R.id.tvText);
        this.flBackground.setOnClickListener(this);
        this.slideInAnimation = AnimationUtils.loadAnimation(getContext(), C0784R.anim.alerter_slide_in_from_top);
        this.slideOutAnimation = AnimationUtils.loadAnimation(getContext(), C0784R.anim.alerter_slide_out_to_top);
        this.slideInAnimation.setAnimationListener(this);
        setAnimation(this.slideInAnimation);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!this.marginSet) {
            this.marginSet = true;
            ((MarginLayoutParams) getLayoutParams()).topMargin = getContext().getResources().getDimensionPixelSize(C0784R.dimen.alerter_alert_negative_margin_top);
            requestLayout();
        }
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.slideInAnimation.setAnimationListener(null);
    }

    public boolean onTouchEvent(MotionEvent event) {
        performClick();
        return super.onTouchEvent(event);
    }

    public void onClick(View v) {
        hide();
    }

    public void setOnClickListener(OnClickListener listener) {
        this.flBackground.setOnClickListener(listener);
    }

    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).setVisibility(visibility);
        }
    }

    public void onAnimationStart(Animation animation) {
        if (!isInEditMode()) {
            performHapticFeedback(1);
            setVisibility(0);
        }
    }

    public void onAnimationEnd(Animation animation) {
        if (this.enableIconPulse) {
            try {
                this.ivIcon.startAnimation(AnimationUtils.loadAnimation(getContext(), C0784R.anim.alerter_pulse));
            } catch (Exception ex) {
                Log.e(getClass().getSimpleName(), Log.getStackTraceString(ex));
            }
        }
        if (this.onShowListener != null) {
            this.onShowListener.onShow();
        }
        if (!this.enableInfiniteDuration) {
            postDelayed(new C07791(), this.duration);
        }
    }

    public void onAnimationRepeat(Animation animation) {
    }

    public void hide() {
        try {
            this.slideOutAnimation.setAnimationListener(new C07802());
            startAnimation(this.slideOutAnimation);
        } catch (Exception ex) {
            Log.e(getClass().getSimpleName(), Log.getStackTraceString(ex));
        }
    }

    private void removeFromParent() {
        postDelayed(new C07813(), 100);
    }

    public void setAlertBackgroundColor(@ColorInt int color) {
        this.flBackground.setBackgroundColor(color);
    }

    public void setTitle(@StringRes int titleId) {
        setTitle(getContext().getString(titleId));
    }

    public void setText(@StringRes int textId) {
        setText(getContext().getString(textId));
    }

    public FrameLayout getAlertBackground() {
        return this.flBackground;
    }

    public TextView getTitle() {
        return this.tvTitle;
    }

    public void setTitle(@NonNull String title) {
        if (!TextUtils.isEmpty(title)) {
            this.tvTitle.setVisibility(0);
            this.tvTitle.setText(title);
        }
    }

    public TextView getText() {
        return this.tvText;
    }

    public void setText(String text) {
        if (!TextUtils.isEmpty(text)) {
            this.tvText.setVisibility(0);
            this.tvText.setText(text);
        }
    }

    public ImageView getIcon() {
        return this.ivIcon;
    }

    public void setIcon(@DrawableRes int iconId) {
        this.ivIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), iconId));
    }

    public long getDuration() {
        return this.duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void pulseIcon(boolean shouldPulse) {
        this.enableIconPulse = shouldPulse;
    }

    public void setEnableInfiniteDuration(boolean enableInfiniteDuration) {
        this.enableInfiniteDuration = enableInfiniteDuration;
    }

    public void setOnShowListener(@NonNull OnShowAlertListener listener) {
        this.onShowListener = listener;
    }

    public void setOnHideListener(@NonNull OnHideAlertListener listener) {
        this.onHideListener = listener;
    }
}
