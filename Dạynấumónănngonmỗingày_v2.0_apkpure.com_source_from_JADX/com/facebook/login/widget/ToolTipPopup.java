package com.facebook.login.widget;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnScrollChangedListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.bumptech.glide.request.target.Target;
import com.facebook.C0378R;
import java.lang.ref.WeakReference;

public class ToolTipPopup {
    public static final long DEFAULT_POPUP_DISPLAY_TIME = 6000;
    private final WeakReference<View> mAnchorViewRef;
    private final Context mContext;
    private long mNuxDisplayTime;
    private PopupContentView mPopupContent;
    private PopupWindow mPopupWindow;
    private final OnScrollChangedListener mScrollListener;
    private Style mStyle;
    private final String mText;

    /* renamed from: com.facebook.login.widget.ToolTipPopup.1 */
    class C04471 implements OnScrollChangedListener {
        C04471() {
        }

        public void onScrollChanged() {
            if (ToolTipPopup.this.mAnchorViewRef.get() != null && ToolTipPopup.this.mPopupWindow != null && ToolTipPopup.this.mPopupWindow.isShowing()) {
                if (ToolTipPopup.this.mPopupWindow.isAboveAnchor()) {
                    ToolTipPopup.this.mPopupContent.showBottomArrow();
                } else {
                    ToolTipPopup.this.mPopupContent.showTopArrow();
                }
            }
        }
    }

    /* renamed from: com.facebook.login.widget.ToolTipPopup.2 */
    class C04482 implements Runnable {
        C04482() {
        }

        public void run() {
            ToolTipPopup.this.dismiss();
        }
    }

    /* renamed from: com.facebook.login.widget.ToolTipPopup.3 */
    class C04493 implements OnClickListener {
        C04493() {
        }

        public void onClick(View v) {
            ToolTipPopup.this.dismiss();
        }
    }

    private class PopupContentView extends FrameLayout {
        private View bodyFrame;
        private ImageView bottomArrow;
        private ImageView topArrow;
        private ImageView xOut;

        public PopupContentView(Context context) {
            super(context);
            init();
        }

        private void init() {
            LayoutInflater.from(getContext()).inflate(C0378R.layout.com_facebook_tooltip_bubble, this);
            this.topArrow = (ImageView) findViewById(C0378R.id.com_facebook_tooltip_bubble_view_top_pointer);
            this.bottomArrow = (ImageView) findViewById(C0378R.id.com_facebook_tooltip_bubble_view_bottom_pointer);
            this.bodyFrame = findViewById(C0378R.id.com_facebook_body_frame);
            this.xOut = (ImageView) findViewById(C0378R.id.com_facebook_button_xout);
        }

        public void showTopArrow() {
            this.topArrow.setVisibility(0);
            this.bottomArrow.setVisibility(4);
        }

        public void showBottomArrow() {
            this.topArrow.setVisibility(4);
            this.bottomArrow.setVisibility(0);
        }
    }

    public enum Style {
        BLUE,
        BLACK
    }

    public ToolTipPopup(String text, View anchor) {
        this.mStyle = Style.BLUE;
        this.mNuxDisplayTime = DEFAULT_POPUP_DISPLAY_TIME;
        this.mScrollListener = new C04471();
        this.mText = text;
        this.mAnchorViewRef = new WeakReference(anchor);
        this.mContext = anchor.getContext();
    }

    public void setStyle(Style mStyle) {
        this.mStyle = mStyle;
    }

    public void show() {
        if (this.mAnchorViewRef.get() != null) {
            this.mPopupContent = new PopupContentView(this.mContext);
            ((TextView) this.mPopupContent.findViewById(C0378R.id.com_facebook_tooltip_bubble_view_text_body)).setText(this.mText);
            if (this.mStyle == Style.BLUE) {
                this.mPopupContent.bodyFrame.setBackgroundResource(C0378R.drawable.com_facebook_tooltip_blue_background);
                this.mPopupContent.bottomArrow.setImageResource(C0378R.drawable.com_facebook_tooltip_blue_bottomnub);
                this.mPopupContent.topArrow.setImageResource(C0378R.drawable.com_facebook_tooltip_blue_topnub);
                this.mPopupContent.xOut.setImageResource(C0378R.drawable.com_facebook_tooltip_blue_xout);
            } else {
                this.mPopupContent.bodyFrame.setBackgroundResource(C0378R.drawable.com_facebook_tooltip_black_background);
                this.mPopupContent.bottomArrow.setImageResource(C0378R.drawable.com_facebook_tooltip_black_bottomnub);
                this.mPopupContent.topArrow.setImageResource(C0378R.drawable.com_facebook_tooltip_black_topnub);
                this.mPopupContent.xOut.setImageResource(C0378R.drawable.com_facebook_tooltip_black_xout);
            }
            View decorView = ((Activity) this.mContext).getWindow().getDecorView();
            int decorWidth = decorView.getWidth();
            int decorHeight = decorView.getHeight();
            registerObserver();
            this.mPopupContent.measure(MeasureSpec.makeMeasureSpec(decorWidth, Target.SIZE_ORIGINAL), MeasureSpec.makeMeasureSpec(decorHeight, Target.SIZE_ORIGINAL));
            this.mPopupWindow = new PopupWindow(this.mPopupContent, this.mPopupContent.getMeasuredWidth(), this.mPopupContent.getMeasuredHeight());
            this.mPopupWindow.showAsDropDown((View) this.mAnchorViewRef.get());
            updateArrows();
            if (this.mNuxDisplayTime > 0) {
                this.mPopupContent.postDelayed(new C04482(), this.mNuxDisplayTime);
            }
            this.mPopupWindow.setTouchable(true);
            this.mPopupContent.setOnClickListener(new C04493());
        }
    }

    public void setNuxDisplayTime(long displayTime) {
        this.mNuxDisplayTime = displayTime;
    }

    private void updateArrows() {
        if (this.mPopupWindow != null && this.mPopupWindow.isShowing()) {
            if (this.mPopupWindow.isAboveAnchor()) {
                this.mPopupContent.showBottomArrow();
            } else {
                this.mPopupContent.showTopArrow();
            }
        }
    }

    public void dismiss() {
        unregisterObserver();
        if (this.mPopupWindow != null) {
            this.mPopupWindow.dismiss();
        }
    }

    private void registerObserver() {
        unregisterObserver();
        if (this.mAnchorViewRef.get() != null) {
            ((View) this.mAnchorViewRef.get()).getViewTreeObserver().addOnScrollChangedListener(this.mScrollListener);
        }
    }

    private void unregisterObserver() {
        if (this.mAnchorViewRef.get() != null) {
            ((View) this.mAnchorViewRef.get()).getViewTreeObserver().removeOnScrollChangedListener(this.mScrollListener);
        }
    }
}
