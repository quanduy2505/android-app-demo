package android.support.design.widget;

import android.graphics.PorterDuff.Mode;
import android.os.Build.VERSION;
import com.google.android.gms.common.ConnectionResult;

class ViewUtils {
    static final Creator DEFAULT_ANIMATOR_CREATOR;

    /* renamed from: android.support.design.widget.ViewUtils.1 */
    static class C08611 implements Creator {
        C08611() {
        }

        public ValueAnimatorCompat createAnimator() {
            return new ValueAnimatorCompat(VERSION.SDK_INT >= 12 ? new ValueAnimatorCompatImplHoneycombMr1() : new ValueAnimatorCompatImplGingerbread());
        }
    }

    ViewUtils() {
    }

    static {
        DEFAULT_ANIMATOR_CREATOR = new C08611();
    }

    static ValueAnimatorCompat createAnimator() {
        return DEFAULT_ANIMATOR_CREATOR.createAnimator();
    }

    static boolean objectEquals(Object a, Object b) {
        return a == b || (a != null && a.equals(b));
    }

    static Mode parseTintMode(int value, Mode defaultMode) {
        switch (value) {
            case ConnectionResult.SERVICE_DISABLED /*3*/:
                return Mode.SRC_OVER;
            case ConnectionResult.INVALID_ACCOUNT /*5*/:
                return Mode.SRC_IN;
            case ConnectionResult.SERVICE_INVALID /*9*/:
                return Mode.SRC_ATOP;
            case ConnectionResult.TIMEOUT /*14*/:
                return Mode.MULTIPLY;
            case ConnectionResult.INTERRUPTED /*15*/:
                return Mode.SCREEN;
            default:
                return defaultMode;
        }
    }
}
