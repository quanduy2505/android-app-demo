package com.tapadoo.alerter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import com.tapadoo.android.C0784R;
import java.lang.ref.WeakReference;

public final class Alerter {
    private static WeakReference<Activity> activityWeakReference;
    private Alert alert;

    /* renamed from: com.tapadoo.alerter.Alerter.1 */
    static class C07821 implements Runnable {
        final /* synthetic */ View val$alertView;

        C07821(View view) {
            this.val$alertView = view;
        }

        public void run() {
            ((ViewGroup) this.val$alertView.getParent()).removeView(this.val$alertView);
        }
    }

    /* renamed from: com.tapadoo.alerter.Alerter.2 */
    class C07832 implements Runnable {
        C07832() {
        }

        public void run() {
            ViewGroup decorView = Alerter.this.getActivityDecorView();
            if (decorView != null && Alerter.this.getAlert().getParent() == null) {
                decorView.addView(Alerter.this.getAlert());
            }
        }
    }

    private Alerter() {
    }

    public static Alerter create(@NonNull Activity activity) {
        if (activity == null) {
            throw new IllegalArgumentException("Activity cannot be null!");
        }
        Alerter alerter = new Alerter();
        clearCurrent(activity);
        alerter.setActivity(activity);
        alerter.setAlert(new Alert(activity));
        return alerter;
    }

    private static void clearCurrent(@NonNull Activity activity) {
        if (activity != null) {
            try {
                View alertView = activity.getWindow().getDecorView().findViewById(C0784R.id.flAlertBackground);
                if (alertView == null || alertView.getWindowToken() == null) {
                    Log.d(Alerter.class.getClass().getSimpleName(), activity.getString(C0784R.string.msg_no_alert_showing));
                    return;
                }
                ViewCompat.animate(alertView).alpha(0.0f).withEndAction(new C07821(alertView)).start();
                Log.d(Alerter.class.getClass().getSimpleName(), activity.getString(C0784R.string.msg_alert_cleared));
            } catch (Exception ex) {
                Log.e(Alerter.class.getClass().getSimpleName(), Log.getStackTraceString(ex));
            }
        }
    }

    public Alert show() {
        if (getActivityWeakReference() != null) {
            ((Activity) getActivityWeakReference().get()).runOnUiThread(new C07832());
        }
        return getAlert();
    }

    public Alerter setTitle(@StringRes int titleId) {
        if (getAlert() != null) {
            getAlert().setTitle(titleId);
        }
        return this;
    }

    public Alerter setTitle(String title) {
        if (getAlert() != null) {
            getAlert().setTitle(title);
        }
        return this;
    }

    public Alerter setText(@StringRes int textId) {
        if (getAlert() != null) {
            getAlert().setText(textId);
        }
        return this;
    }

    public Alerter setText(String text) {
        if (getAlert() != null) {
            getAlert().setText(text);
        }
        return this;
    }

    public Alerter setBackgroundColor(@ColorRes int colorResId) {
        if (!(getAlert() == null || getActivityWeakReference() == null)) {
            getAlert().setAlertBackgroundColor(ContextCompat.getColor((Context) getActivityWeakReference().get(), colorResId));
        }
        return this;
    }

    public Alerter setIcon(@DrawableRes int iconId) {
        if (getAlert() != null) {
            getAlert().setIcon(iconId);
        }
        return this;
    }

    public Alerter setOnClickListener(@NonNull OnClickListener onClickListener) {
        if (getAlert() != null) {
            getAlert().setOnClickListener(onClickListener);
        }
        return this;
    }

    public Alerter setDuration(@NonNull long milliseconds) {
        if (getAlert() != null) {
            getAlert().setDuration(milliseconds);
        }
        return this;
    }

    public Alerter enableIconPulse(boolean pulse) {
        if (getAlert() != null) {
            getAlert().pulseIcon(pulse);
        }
        return this;
    }

    public Alerter enableInfiniteDuration(boolean infiniteDuration) {
        if (getAlert() != null) {
            getAlert().setEnableInfiniteDuration(infiniteDuration);
        }
        return this;
    }

    public Alerter setOnShowListener(@NonNull OnShowAlertListener listener) {
        if (getAlert() != null) {
            getAlert().setOnShowListener(listener);
        }
        return this;
    }

    public Alerter setOnHideListener(@NonNull OnHideAlertListener listener) {
        if (getAlert() != null) {
            getAlert().setOnHideListener(listener);
        }
        return this;
    }

    private Alert getAlert() {
        return this.alert;
    }

    private void setAlert(Alert alert) {
        this.alert = alert;
    }

    @Nullable
    private WeakReference<Activity> getActivityWeakReference() {
        return activityWeakReference;
    }

    @Nullable
    private ViewGroup getActivityDecorView() {
        if (getActivityWeakReference() == null || getActivityWeakReference().get() == null) {
            return null;
        }
        return (ViewGroup) ((Activity) getActivityWeakReference().get()).getWindow().getDecorView();
    }

    private void setActivity(@NonNull Activity activity) {
        activityWeakReference = new WeakReference(activity);
    }
}
