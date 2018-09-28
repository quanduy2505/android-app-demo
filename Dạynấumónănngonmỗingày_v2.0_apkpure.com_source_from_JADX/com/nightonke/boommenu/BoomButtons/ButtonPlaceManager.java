package com.nightonke.boommenu.BoomButtons;

import android.graphics.Point;
import android.support.v4.app.NotificationCompat.MessagingStyle;
import android.support.v7.widget.helper.ItemTouchHelper;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.common.ConnectionResult;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.tapadoo.android.C0784R;
import java.util.ArrayList;
import java.util.Iterator;
import me.wangyuwei.loadingview.C0801R;
import org.apache.http.util.LangUtils;
import rx.internal.operators.OnSubscribeConcatMap;

public class ButtonPlaceManager {
    private static ButtonPlaceManager ourInstance;

    /* renamed from: com.nightonke.boommenu.BoomButtons.ButtonPlaceManager.1 */
    static /* synthetic */ class C07461 {
        static final /* synthetic */ int[] f15xaf1aa661;
        static final /* synthetic */ int[] $SwitchMap$com$nightonke$boommenu$BoomButtons$ButtonPlaceEnum;

        static {
            f15xaf1aa661 = new int[ButtonPlaceAlignmentEnum.values().length];
            try {
                f15xaf1aa661[ButtonPlaceAlignmentEnum.Center.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f15xaf1aa661[ButtonPlaceAlignmentEnum.Top.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f15xaf1aa661[ButtonPlaceAlignmentEnum.Bottom.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f15xaf1aa661[ButtonPlaceAlignmentEnum.Left.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                f15xaf1aa661[ButtonPlaceAlignmentEnum.Right.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                f15xaf1aa661[ButtonPlaceAlignmentEnum.TL.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                f15xaf1aa661[ButtonPlaceAlignmentEnum.TR.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                f15xaf1aa661[ButtonPlaceAlignmentEnum.BL.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                f15xaf1aa661[ButtonPlaceAlignmentEnum.BR.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            try {
                f15xaf1aa661[ButtonPlaceAlignmentEnum.Unknown.ordinal()] = 10;
            } catch (NoSuchFieldError e10) {
            }
            $SwitchMap$com$nightonke$boommenu$BoomButtons$ButtonPlaceEnum = new int[ButtonPlaceEnum.values().length];
            try {
                $SwitchMap$com$nightonke$boommenu$BoomButtons$ButtonPlaceEnum[ButtonPlaceEnum.Horizontal.ordinal()] = 1;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$BoomButtons$ButtonPlaceEnum[ButtonPlaceEnum.Vertical.ordinal()] = 2;
            } catch (NoSuchFieldError e12) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$BoomButtons$ButtonPlaceEnum[ButtonPlaceEnum.SC_1.ordinal()] = 3;
            } catch (NoSuchFieldError e13) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$BoomButtons$ButtonPlaceEnum[ButtonPlaceEnum.SC_2_1.ordinal()] = 4;
            } catch (NoSuchFieldError e14) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$BoomButtons$ButtonPlaceEnum[ButtonPlaceEnum.SC_2_2.ordinal()] = 5;
            } catch (NoSuchFieldError e15) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$BoomButtons$ButtonPlaceEnum[ButtonPlaceEnum.SC_3_1.ordinal()] = 6;
            } catch (NoSuchFieldError e16) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$BoomButtons$ButtonPlaceEnum[ButtonPlaceEnum.SC_3_2.ordinal()] = 7;
            } catch (NoSuchFieldError e17) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$BoomButtons$ButtonPlaceEnum[ButtonPlaceEnum.SC_3_3.ordinal()] = 8;
            } catch (NoSuchFieldError e18) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$BoomButtons$ButtonPlaceEnum[ButtonPlaceEnum.SC_3_4.ordinal()] = 9;
            } catch (NoSuchFieldError e19) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$BoomButtons$ButtonPlaceEnum[ButtonPlaceEnum.SC_4_1.ordinal()] = 10;
            } catch (NoSuchFieldError e20) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$BoomButtons$ButtonPlaceEnum[ButtonPlaceEnum.SC_4_2.ordinal()] = 11;
            } catch (NoSuchFieldError e21) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$BoomButtons$ButtonPlaceEnum[ButtonPlaceEnum.SC_5_1.ordinal()] = 12;
            } catch (NoSuchFieldError e22) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$BoomButtons$ButtonPlaceEnum[ButtonPlaceEnum.SC_5_2.ordinal()] = 13;
            } catch (NoSuchFieldError e23) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$BoomButtons$ButtonPlaceEnum[ButtonPlaceEnum.SC_5_3.ordinal()] = 14;
            } catch (NoSuchFieldError e24) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$BoomButtons$ButtonPlaceEnum[ButtonPlaceEnum.SC_5_4.ordinal()] = 15;
            } catch (NoSuchFieldError e25) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$BoomButtons$ButtonPlaceEnum[ButtonPlaceEnum.SC_6_1.ordinal()] = 16;
            } catch (NoSuchFieldError e26) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$BoomButtons$ButtonPlaceEnum[ButtonPlaceEnum.SC_6_2.ordinal()] = 17;
            } catch (NoSuchFieldError e27) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$BoomButtons$ButtonPlaceEnum[ButtonPlaceEnum.SC_6_3.ordinal()] = 18;
            } catch (NoSuchFieldError e28) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$BoomButtons$ButtonPlaceEnum[ButtonPlaceEnum.SC_6_4.ordinal()] = 19;
            } catch (NoSuchFieldError e29) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$BoomButtons$ButtonPlaceEnum[ButtonPlaceEnum.SC_6_5.ordinal()] = 20;
            } catch (NoSuchFieldError e30) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$BoomButtons$ButtonPlaceEnum[ButtonPlaceEnum.SC_6_6.ordinal()] = 21;
            } catch (NoSuchFieldError e31) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$BoomButtons$ButtonPlaceEnum[ButtonPlaceEnum.SC_7_1.ordinal()] = 22;
            } catch (NoSuchFieldError e32) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$BoomButtons$ButtonPlaceEnum[ButtonPlaceEnum.SC_7_2.ordinal()] = 23;
            } catch (NoSuchFieldError e33) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$BoomButtons$ButtonPlaceEnum[ButtonPlaceEnum.SC_7_3.ordinal()] = 24;
            } catch (NoSuchFieldError e34) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$BoomButtons$ButtonPlaceEnum[ButtonPlaceEnum.SC_7_4.ordinal()] = 25;
            } catch (NoSuchFieldError e35) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$BoomButtons$ButtonPlaceEnum[ButtonPlaceEnum.SC_7_5.ordinal()] = 26;
            } catch (NoSuchFieldError e36) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$BoomButtons$ButtonPlaceEnum[ButtonPlaceEnum.SC_7_6.ordinal()] = 27;
            } catch (NoSuchFieldError e37) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$BoomButtons$ButtonPlaceEnum[ButtonPlaceEnum.SC_8_1.ordinal()] = 28;
            } catch (NoSuchFieldError e38) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$BoomButtons$ButtonPlaceEnum[ButtonPlaceEnum.SC_8_2.ordinal()] = 29;
            } catch (NoSuchFieldError e39) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$BoomButtons$ButtonPlaceEnum[ButtonPlaceEnum.SC_8_3.ordinal()] = 30;
            } catch (NoSuchFieldError e40) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$BoomButtons$ButtonPlaceEnum[ButtonPlaceEnum.SC_8_4.ordinal()] = 31;
            } catch (NoSuchFieldError e41) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$BoomButtons$ButtonPlaceEnum[ButtonPlaceEnum.SC_8_5.ordinal()] = 32;
            } catch (NoSuchFieldError e42) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$BoomButtons$ButtonPlaceEnum[ButtonPlaceEnum.SC_8_6.ordinal()] = 33;
            } catch (NoSuchFieldError e43) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$BoomButtons$ButtonPlaceEnum[ButtonPlaceEnum.SC_8_7.ordinal()] = 34;
            } catch (NoSuchFieldError e44) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$BoomButtons$ButtonPlaceEnum[ButtonPlaceEnum.SC_9_1.ordinal()] = 35;
            } catch (NoSuchFieldError e45) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$BoomButtons$ButtonPlaceEnum[ButtonPlaceEnum.SC_9_2.ordinal()] = 36;
            } catch (NoSuchFieldError e46) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$BoomButtons$ButtonPlaceEnum[ButtonPlaceEnum.SC_9_3.ordinal()] = 37;
            } catch (NoSuchFieldError e47) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$BoomButtons$ButtonPlaceEnum[ButtonPlaceEnum.HAM_1.ordinal()] = 38;
            } catch (NoSuchFieldError e48) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$BoomButtons$ButtonPlaceEnum[ButtonPlaceEnum.HAM_2.ordinal()] = 39;
            } catch (NoSuchFieldError e49) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$BoomButtons$ButtonPlaceEnum[ButtonPlaceEnum.HAM_3.ordinal()] = 40;
            } catch (NoSuchFieldError e50) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$BoomButtons$ButtonPlaceEnum[ButtonPlaceEnum.HAM_4.ordinal()] = 41;
            } catch (NoSuchFieldError e51) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$BoomButtons$ButtonPlaceEnum[ButtonPlaceEnum.HAM_5.ordinal()] = 42;
            } catch (NoSuchFieldError e52) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$BoomButtons$ButtonPlaceEnum[ButtonPlaceEnum.HAM_6.ordinal()] = 43;
            } catch (NoSuchFieldError e53) {
            }
        }
    }

    public static ArrayList<Point> getCircleButtonPositions(ButtonPlaceEnum placeEnum, ButtonPlaceAlignmentEnum alignmentEnum, Point parentSize, float radius, int buttonNumber, float buttonHorizontalMargin, float buttonVerticalMargin, float buttonInclinedMargin, float buttonTopMargin, float buttonBottomMargin, float buttonLeftMargin, float buttonRightMargin) {
        ArrayList<Point> positions = new ArrayList(buttonNumber);
        float r = radius;
        float hm = buttonHorizontalMargin;
        float vm = buttonVerticalMargin;
        float im = buttonInclinedMargin;
        int h = buttonNumber / 2;
        int i;
        float b;
        float c;
        float a;
        float e;
        switch (C07461.$SwitchMap$com$nightonke$boommenu$BoomButtons$ButtonPlaceEnum[placeEnum.ordinal()]) {
            case OnSubscribeConcatMap.BOUNDARY /*1*/:
                if (buttonNumber % 2 != 0) {
                    for (i = h - 1; i >= 0; i--) {
                        positions.add(point(((-2.0f * r) - hm) - (((float) i) * ((2.0f * r) + hm)), 0.0f));
                    }
                    positions.add(point(0, 0));
                    for (i = 0; i < h; i++) {
                        positions.add(point(((2.0f * r) + hm) + (((float) i) * ((2.0f * r) + hm)), 0.0f));
                    }
                    break;
                }
                for (i = h - 1; i >= 0; i--) {
                    positions.add(point(((-r) - (hm / 2.0f)) - (((float) i) * ((2.0f * r) + hm)), 0.0f));
                }
                for (i = 0; i < h; i++) {
                    positions.add(point(((hm / 2.0f) + r) + (((float) i) * ((2.0f * r) + hm)), 0.0f));
                }
                break;
            case OnSubscribeConcatMap.END /*2*/:
                if (buttonNumber % 2 != 0) {
                    for (i = h - 1; i >= 0; i--) {
                        positions.add(point(0.0f, ((-2.0f * r) - vm) - (((float) i) * ((2.0f * r) + vm))));
                    }
                    positions.add(point(0, 0));
                    for (i = 0; i < h; i++) {
                        positions.add(point(0.0f, ((2.0f * r) + vm) + (((float) i) * ((2.0f * r) + vm))));
                    }
                    break;
                }
                for (i = h - 1; i >= 0; i--) {
                    positions.add(point(0.0f, ((-r) - (vm / 2.0f)) - (((float) i) * ((2.0f * r) + vm))));
                }
                for (i = 0; i < h; i++) {
                    positions.add(point(0.0f, ((vm / 2.0f) + r) + (((float) i) * ((2.0f * r) + vm))));
                }
                break;
            case ConnectionResult.SERVICE_DISABLED /*3*/:
                positions.add(point(0, 0));
                break;
            case ConnectionResult.SIGN_IN_REQUIRED /*4*/:
                positions.add(point(((-hm) / 2.0f) - r, 0.0f));
                positions.add(point((hm / 2.0f) + r, 0.0f));
                break;
            case ConnectionResult.INVALID_ACCOUNT /*5*/:
                positions.add(point(0.0f, ((-vm) / 2.0f) - r));
                positions.add(point(0.0f, (vm / 2.0f) + r));
                break;
            case ConnectionResult.RESOLUTION_REQUIRED /*6*/:
                positions.add(point((-hm) - (2.0f * r), 0.0f));
                positions.add(point(0, 0));
                positions.add(point((2.0f * r) + hm, 0.0f));
                break;
            case ConnectionResult.NETWORK_ERROR /*7*/:
                positions.add(point(0.0f, (-vm) - (2.0f * r)));
                positions.add(point(0, 0));
                positions.add(point(0.0f, (2.0f * r) + vm));
                break;
            case ConnectionResult.INTERNAL_ERROR /*8*/:
                b = (hm / 2.0f) + r;
                c = (float) (((double) b) / (Math.sqrt(3.0d) / 2.0d));
                a = c / 2.0f;
                positions.add(point(-b, -a));
                positions.add(point(b, -a));
                positions.add(point(0.0f, c));
                break;
            case ConnectionResult.SERVICE_INVALID /*9*/:
                b = (hm / 2.0f) + r;
                c = (float) (((double) b) / (Math.sqrt(3.0d) / 2.0d));
                a = c / 2.0f;
                positions.add(point(0.0f, -c));
                positions.add(point(-b, a));
                positions.add(point(b, a));
                break;
            case ConnectionResult.DEVELOPER_ERROR /*10*/:
                positions.add(point(((-hm) / 2.0f) - r, ((-vm) / 2.0f) - r));
                positions.add(point((hm / 2.0f) + r, ((-vm) / 2.0f) - r));
                positions.add(point(((-hm) / 2.0f) - r, (vm / 2.0f) + r));
                positions.add(point((hm / 2.0f) + r, (vm / 2.0f) + r));
                break;
            case ConnectionResult.LICENSE_CHECK_FAILED /*11*/:
                a = (float) (((double) ((2.0f * r) + im)) / Math.sqrt(2.0d));
                positions.add(point(0.0f, -a));
                positions.add(point(a, 0.0f));
                positions.add(point(0.0f, a));
                positions.add(point(-a, 0.0f));
                break;
            case C0801R.styleable.Toolbar_titleTextAppearance /*12*/:
                b = (hm / 2.0f) + r;
                c = (float) (((double) b) / (Math.sqrt(3.0d) / 2.0d));
                a = c / 2.0f;
                positions.add(point(-2.0f * b, -c));
                positions.add(point(0.0f, -c));
                positions.add(point(2.0f * b, -c));
                positions.add(point(((-hm) / 2.0f) - r, a));
                positions.add(point((hm / 2.0f) + r, a));
                break;
            case ConnectionResult.CANCELED /*13*/:
                b = (hm / 2.0f) + r;
                c = (float) (((double) b) / (Math.sqrt(3.0d) / 2.0d));
                a = c / 2.0f;
                positions.add(point((hm / 2.0f) + r, -a));
                positions.add(point(((-hm) / 2.0f) - r, -a));
                positions.add(point(2.0f * b, c));
                positions.add(point(0.0f, c));
                positions.add(point(-2.0f * b, c));
                break;
            case ConnectionResult.TIMEOUT /*14*/:
                positions.add(point(0, 0));
                positions.add(point(0.0f, (-vm) - (2.0f * r)));
                positions.add(point((2.0f * r) + hm, 0.0f));
                positions.add(point(0.0f, (2.0f * r) + vm));
                positions.add(point((-hm) - (2.0f * r), 0.0f));
                break;
            case ConnectionResult.INTERRUPTED /*15*/:
                a = (float) (((double) ((2.0f * r) + im)) / Math.sqrt(2.0d));
                positions.add(point(0, 0));
                positions.add(point(a, -a));
                positions.add(point(a, a));
                positions.add(point(-a, a));
                positions.add(point(-a, -a));
                break;
            case ConnectionResult.API_UNAVAILABLE /*16*/:
                positions.add(point((-hm) - (2.0f * r), ((-vm) / 2.0f) - r));
                positions.add(point(0.0f, ((-vm) / 2.0f) - r));
                positions.add(point((2.0f * r) + hm, ((-vm) / 2.0f) - r));
                positions.add(point((-hm) - (2.0f * r), (vm / 2.0f) + r));
                positions.add(point(0.0f, (vm / 2.0f) + r));
                positions.add(point((2.0f * r) + hm, (vm / 2.0f) + r));
                break;
            case ConnectionResult.SIGN_IN_FAILED /*17*/:
                positions.add(point(((-hm) / 2.0f) - r, (-vm) - (2.0f * r)));
                positions.add(point(((-hm) / 2.0f) - r, 0.0f));
                positions.add(point(((-hm) / 2.0f) - r, (2.0f * r) + vm));
                positions.add(point((hm / 2.0f) + r, (-vm) - (2.0f * r)));
                positions.add(point((hm / 2.0f) + r, 0.0f));
                positions.add(point((hm / 2.0f) + r, (2.0f * r) + vm));
                break;
            case ConnectionResult.SERVICE_UPDATING /*18*/:
                b = (hm / 2.0f) + r;
                c = (float) (((double) b) / (Math.sqrt(3.0d) / 2.0d));
                a = c / 2.0f;
                positions.add(point(-b, (-a) - c));
                positions.add(point(b, (-a) - c));
                positions.add(point(2.0f * b, 0.0f));
                positions.add(point(b, a + c));
                positions.add(point(-b, a + c));
                positions.add(point(-2.0f * b, 0.0f));
                break;
            case ConnectionResult.SERVICE_MISSING_PERMISSION /*19*/:
                b = (hm / 2.0f) + r;
                c = (float) (((double) b) / (Math.sqrt(3.0d) / 2.0d));
                a = c / 2.0f;
                positions.add(point(0.0f, -2.0f * b));
                positions.add(point(a + c, -b));
                positions.add(point(a + c, b));
                positions.add(point(0.0f, 2.0f * b));
                positions.add(point((-a) - c, b));
                positions.add(point((-a) - c, -b));
                break;
            case ConnectionResult.RESTRICTED_PROFILE /*20*/:
                b = (hm / 2.0f) + r;
                c = (float) (((double) b) / (Math.sqrt(3.0d) / 2.0d));
                a = c / 2.0f;
                e = c - a;
                positions.add(point(-2.0f * b, ((-a) - c) + e));
                positions.add(point(0.0f, ((-a) - c) + e));
                positions.add(point(2.0f * b, ((-a) - c) + e));
                positions.add(point(((-hm) / 2.0f) - r, e));
                positions.add(point((hm / 2.0f) + r, e));
                positions.add(point(0.0f, (a + c) + e));
                break;
            case C0801R.styleable.AppCompatTheme_actionBarWidgetTheme /*21*/:
                b = (hm / 2.0f) + r;
                c = (float) (((double) b) / (Math.sqrt(3.0d) / 2.0d));
                a = c / 2.0f;
                e = c - a;
                positions.add(point(0.0f, ((-a) - c) - e));
                positions.add(point(((-hm) / 2.0f) - r, -e));
                positions.add(point((hm / 2.0f) + r, -e));
                positions.add(point(-2.0f * b, (a + c) - e));
                positions.add(point(0.0f, (a + c) - e));
                positions.add(point(2.0f * b, (a + c) - e));
                break;
            case C0801R.styleable.Toolbar_collapseIcon /*22*/:
                positions.add(point((-hm) - (2.0f * r), (-vm) - (2.0f * r)));
                positions.add(point(0.0f, (-vm) - (2.0f * r)));
                positions.add(point((2.0f * r) + hm, (-vm) - (2.0f * r)));
                positions.add(point((-hm) - (2.0f * r), 0.0f));
                positions.add(point(0, 0));
                positions.add(point((2.0f * r) + hm, 0.0f));
                positions.add(point(0.0f, (2.0f * r) + vm));
                break;
            case C0801R.styleable.Toolbar_collapseContentDescription /*23*/:
                positions.add(point(0.0f, (-vm) - (2.0f * r)));
                positions.add(point((-hm) - (2.0f * r), 0.0f));
                positions.add(point(0, 0));
                positions.add(point((2.0f * r) + hm, 0.0f));
                positions.add(point((-hm) - (2.0f * r), (2.0f * r) + vm));
                positions.add(point(0.0f, (2.0f * r) + vm));
                positions.add(point((2.0f * r) + hm, (2.0f * r) + vm));
                break;
            case C0801R.styleable.Toolbar_navigationIcon /*24*/:
                b = (hm / 2.0f) + r;
                c = (float) (((double) b) / (Math.sqrt(3.0d) / 2.0d));
                a = c / 2.0f;
                positions.add(point(0, 0));
                positions.add(point(-b, (-a) - c));
                positions.add(point(b, (-a) - c));
                positions.add(point(2.0f * b, 0.0f));
                positions.add(point(b, a + c));
                positions.add(point(-b, a + c));
                positions.add(point(-2.0f * b, 0.0f));
                break;
            case MessagingStyle.MAXIMUM_RETAINED_MESSAGES /*25*/:
                b = (hm / 2.0f) + r;
                c = (float) (((double) b) / (Math.sqrt(3.0d) / 2.0d));
                a = c / 2.0f;
                positions.add(point(0, 0));
                positions.add(point(0.0f, -2.0f * b));
                positions.add(point(a + c, -b));
                positions.add(point(a + c, b));
                positions.add(point(0.0f, 2.0f * b));
                positions.add(point((-a) - c, b));
                positions.add(point((-a) - c, -b));
                break;
            case C0801R.styleable.Toolbar_logoDescription /*26*/:
                b = (hm / 2.0f) + r;
                c = (float) (((double) b) / (Math.sqrt(3.0d) / 2.0d));
                a = c / 2.0f;
                positions.add(point(-3.0f * b, -a));
                positions.add(point(-b, -a));
                positions.add(point(b, -a));
                positions.add(point(3.0f * b, -a));
                positions.add(point(-2.0f * b, c));
                positions.add(point(0.0f, c));
                positions.add(point(2.0f * b, c));
                break;
            case C0801R.styleable.Toolbar_titleTextColor /*27*/:
                b = (hm / 2.0f) + r;
                c = (float) (((double) b) / (Math.sqrt(3.0d) / 2.0d));
                a = c / 2.0f;
                positions.add(point(-2.0f * b, -c));
                positions.add(point(0.0f, -c));
                positions.add(point(2.0f * b, -c));
                positions.add(point(-3.0f * b, a));
                positions.add(point(-b, a));
                positions.add(point(b, a));
                positions.add(point(3.0f * b, a));
                break;
            case C0801R.styleable.Toolbar_subtitleTextColor /*28*/:
                b = (hm / 2.0f) + r;
                c = (float) (((double) b) / (Math.sqrt(3.0d) / 2.0d));
                a = c / 2.0f;
                positions.add(point(-2.0f * b, (-a) - c));
                positions.add(point(0.0f, (-a) - c));
                positions.add(point(2.0f * b, (-a) - c));
                positions.add(point(((-hm) / 2.0f) - r, 0.0f));
                positions.add(point((hm / 2.0f) + r, 0.0f));
                positions.add(point(-2.0f * b, a + c));
                positions.add(point(0.0f, a + c));
                positions.add(point(2.0f * b, a + c));
                break;
            case C0801R.styleable.AppCompatTheme_actionModeBackground /*29*/:
                b = (hm / 2.0f) + r;
                c = (float) (((double) b) / (Math.sqrt(3.0d) / 2.0d));
                a = c / 2.0f;
                positions.add(point((-a) - c, -2.0f * b));
                positions.add(point((-a) - c, 0.0f));
                positions.add(point((-a) - c, 2.0f * b));
                positions.add(point(0.0f, ((-vm) / 2.0f) - r));
                positions.add(point(0.0f, (vm / 2.0f) + r));
                positions.add(point(a + c, -2.0f * b));
                positions.add(point(a + c, 0.0f));
                positions.add(point(a + c, 2.0f * b));
                break;
            case C0801R.styleable.AppCompatTheme_actionModeSplitBackground /*30*/:
                positions.add(point((-hm) - (2.0f * r), (-vm) - (2.0f * r)));
                positions.add(point(0.0f, (-vm) - (2.0f * r)));
                positions.add(point((2.0f * r) + hm, (-vm) - (2.0f * r)));
                positions.add(point((-hm) - (2.0f * r), 0.0f));
                positions.add(point((2.0f * r) + hm, 0.0f));
                positions.add(point((-hm) - (2.0f * r), (2.0f * r) + vm));
                positions.add(point(0.0f, (2.0f * r) + vm));
                positions.add(point((2.0f * r) + hm, (2.0f * r) + vm));
                break;
            case C0801R.styleable.AppCompatTheme_actionModeCloseDrawable /*31*/:
                b = (hm / 2.0f) + r;
                c = (float) (((double) b) / (Math.sqrt(3.0d) / 2.0d));
                a = c / 2.0f;
                positions.add(point(0.0f, (-2.0f * a) - (2.0f * c)));
                positions.add(point(((-hm) / 2.0f) - r, (-a) - c));
                positions.add(point((hm / 2.0f) + r, (-a) - c));
                positions.add(point(-2.0f * b, 0.0f));
                positions.add(point(2.0f * b, 0.0f));
                positions.add(point(((-hm) / 2.0f) - r, a + c));
                positions.add(point((hm / 2.0f) + r, a + c));
                positions.add(point(0.0f, (2.0f * a) + (2.0f * c)));
                break;
            case ItemTouchHelper.END /*32*/:
                a = (float) (((double) ((2.0f * r) + im)) / Math.sqrt(2.0d));
                positions.add(point(0.0f, -2.0f * a));
                positions.add(point(a, -a));
                positions.add(point(2.0f * a, 0.0f));
                positions.add(point(a, a));
                positions.add(point(0.0f, 2.0f * a));
                positions.add(point(-a, a));
                positions.add(point(-2.0f * a, 0.0f));
                positions.add(point(-a, -a));
                break;
            case C0801R.styleable.AppCompatTheme_actionModeCopyDrawable /*33*/:
                positions.add(point((((-hm) * 3.0f) / 2.0f) - (3.0f * r), ((-vm) / 2.0f) - r));
                positions.add(point(((-hm) / 2.0f) - r, ((-vm) / 2.0f) - r));
                positions.add(point((hm / 2.0f) + r, ((-vm) / 2.0f) - r));
                positions.add(point(((3.0f * hm) / 2.0f) + (3.0f * r), ((-vm) / 2.0f) - r));
                positions.add(point((((-hm) * 3.0f) / 2.0f) - (3.0f * r), (vm / 2.0f) + r));
                positions.add(point(((-hm) / 2.0f) - r, (vm / 2.0f) + r));
                positions.add(point((hm / 2.0f) + r, (vm / 2.0f) + r));
                positions.add(point(((3.0f * hm) / 2.0f) + (3.0f * r), (vm / 2.0f) + r));
                break;
            case C0801R.styleable.AppCompatTheme_actionModePasteDrawable /*34*/:
                positions.add(point(((-hm) / 2.0f) - r, (((-vm) * 3.0f) / 2.0f) - (3.0f * r)));
                positions.add(point((hm / 2.0f) + r, (((-vm) * 3.0f) / 2.0f) - (3.0f * r)));
                positions.add(point(((-hm) / 2.0f) - r, ((-vm) / 2.0f) - r));
                positions.add(point((hm / 2.0f) + r, ((-vm) / 2.0f) - r));
                positions.add(point(((-hm) / 2.0f) - r, (vm / 2.0f) + r));
                positions.add(point((hm / 2.0f) + r, (vm / 2.0f) + r));
                positions.add(point(((-hm) / 2.0f) - r, ((3.0f * vm) / 2.0f) + (3.0f * r)));
                positions.add(point((hm / 2.0f) + r, ((3.0f * vm) / 2.0f) + (3.0f * r)));
                break;
            case C0801R.styleable.AppCompatTheme_actionModeSelectAllDrawable /*35*/:
                positions.add(point((-hm) - (2.0f * r), (-vm) - (2.0f * r)));
                positions.add(point(0.0f, (-vm) - (2.0f * r)));
                positions.add(point((2.0f * r) + hm, (-vm) - (2.0f * r)));
                positions.add(point((-hm) - (2.0f * r), 0.0f));
                positions.add(point(0, 0));
                positions.add(point((2.0f * r) + hm, 0.0f));
                positions.add(point((-hm) - (2.0f * r), (2.0f * r) + vm));
                positions.add(point(0.0f, (2.0f * r) + vm));
                positions.add(point((2.0f * r) + hm, (2.0f * r) + vm));
                break;
            case C0801R.styleable.AppCompatTheme_actionModeShareDrawable /*36*/:
                b = (hm / 2.0f) + r;
                c = (float) (((double) b) / (Math.sqrt(3.0d) / 2.0d));
                a = c / 2.0f;
                positions.add(point(0.0f, (-2.0f * a) - (2.0f * c)));
                positions.add(point(((-hm) / 2.0f) - r, (-a) - c));
                positions.add(point((hm / 2.0f) + r, (-a) - c));
                positions.add(point(-2.0f * b, 0.0f));
                positions.add(point(0, 0));
                positions.add(point(2.0f * b, 0.0f));
                positions.add(point(((-hm) / 2.0f) - r, a + c));
                positions.add(point((hm / 2.0f) + r, a + c));
                positions.add(point(0.0f, (2.0f * a) + (2.0f * c)));
                break;
            case LangUtils.HASH_OFFSET /*37*/:
                a = (float) (((double) ((2.0f * r) + im)) / Math.sqrt(2.0d));
                positions.add(point(0.0f, -2.0f * a));
                positions.add(point(a, -a));
                positions.add(point(2.0f * a, 0.0f));
                positions.add(point(a, a));
                positions.add(point(0, 0));
                positions.add(point(0.0f, 2.0f * a));
                positions.add(point(-a, a));
                positions.add(point(-2.0f * a, 0.0f));
                positions.add(point(-a, -a));
                break;
        }
        calculatePositionsInParent(positions, parentSize);
        calculateOffset(positions, alignmentEnum, parentSize, radius * 2.0f, radius * 2.0f, buttonTopMargin, buttonBottomMargin, buttonLeftMargin, buttonRightMargin);
        return positions;
    }

    public static ArrayList<Point> getCircleButtonPositions(ButtonPlaceEnum placeEnum, ButtonPlaceAlignmentEnum alignmentEnum, Point parentSize, float buttonWidth, float buttonHeight, int buttonNumber, float buttonHorizontalMargin, float buttonVerticalMargin, float buttonInclinedMargin, float buttonTopMargin, float buttonBottomMargin, float buttonLeftMargin, float buttonRightMargin) {
        ArrayList<Point> positions = new ArrayList(buttonNumber);
        float w = buttonWidth;
        float h = buttonHeight;
        float hm = buttonHorizontalMargin;
        float vm = buttonVerticalMargin;
        float im = buttonInclinedMargin;
        int half = buttonNumber / 2;
        int i;
        switch (C07461.$SwitchMap$com$nightonke$boommenu$BoomButtons$ButtonPlaceEnum[placeEnum.ordinal()]) {
            case OnSubscribeConcatMap.BOUNDARY /*1*/:
                if (buttonNumber % 2 != 0) {
                    for (i = half - 1; i >= 0; i--) {
                        positions.add(point(((-w) - hm) - (((float) i) * (w + hm)), 0.0f));
                    }
                    positions.add(point(0, 0));
                    for (i = 0; i < half; i++) {
                        positions.add(point((w + hm) + (((float) i) * (w + hm)), 0.0f));
                    }
                    break;
                }
                for (i = half - 1; i >= 0; i--) {
                    positions.add(point((((-w) / 2.0f) - (hm / 2.0f)) - (((float) i) * (w + hm)), 0.0f));
                }
                for (i = 0; i < half; i++) {
                    positions.add(point(((w / 2.0f) + (hm / 2.0f)) + (((float) i) * (w + hm)), 0.0f));
                }
                break;
            case OnSubscribeConcatMap.END /*2*/:
                if (buttonNumber % 2 != 0) {
                    for (i = half - 1; i >= 0; i--) {
                        positions.add(point(0.0f, ((-h) - vm) - (((float) i) * (h + vm))));
                    }
                    positions.add(point(0, 0));
                    for (i = 0; i < half; i++) {
                        positions.add(point(0.0f, (h + vm) + (((float) i) * (h + vm))));
                    }
                    break;
                }
                for (i = half - 1; i >= 0; i--) {
                    positions.add(point(0.0f, (((-h) / 2.0f) - (vm / 2.0f)) - (((float) i) * (h + vm))));
                }
                for (i = 0; i < half; i++) {
                    positions.add(point(0.0f, ((h / 2.0f) + (vm / 2.0f)) + (((float) i) * (h + vm))));
                }
                break;
            case ConnectionResult.SERVICE_DISABLED /*3*/:
                positions.add(point(0, 0));
                break;
            case ConnectionResult.SIGN_IN_REQUIRED /*4*/:
                positions.add(point(((-hm) / 2.0f) - (w / 2.0f), 0.0f));
                positions.add(point((hm / 2.0f) + (w / 2.0f), 0.0f));
                break;
            case ConnectionResult.INVALID_ACCOUNT /*5*/:
                positions.add(point(0.0f, ((-vm) / 2.0f) - (h / 2.0f)));
                positions.add(point(0.0f, (vm / 2.0f) + (h / 2.0f)));
                break;
            case ConnectionResult.RESOLUTION_REQUIRED /*6*/:
                positions.add(point((-hm) - w, 0.0f));
                positions.add(point(0, 0));
                positions.add(point(hm + w, 0.0f));
                break;
            case ConnectionResult.NETWORK_ERROR /*7*/:
                positions.add(point(0.0f, (-vm) - h));
                positions.add(point(0, 0));
                positions.add(point(0.0f, vm + h));
                break;
            case ConnectionResult.INTERNAL_ERROR /*8*/:
                positions.add(point(((-hm) / 2.0f) - (w / 2.0f), ((-vm) / 2.0f) - (h / 2.0f)));
                positions.add(point((hm / 2.0f) + (w / 2.0f), ((-vm) / 2.0f) - (h / 2.0f)));
                positions.add(point(0.0f, (vm / 2.0f) + (h / 2.0f)));
                break;
            case ConnectionResult.SERVICE_INVALID /*9*/:
                positions.add(point(0.0f, ((-vm) / 2.0f) - (h / 2.0f)));
                positions.add(point(((-hm) / 2.0f) - (w / 2.0f), (vm / 2.0f) + (h / 2.0f)));
                positions.add(point((hm / 2.0f) + (w / 2.0f), (vm / 2.0f) + (h / 2.0f)));
                break;
            case ConnectionResult.DEVELOPER_ERROR /*10*/:
                positions.add(point(((-hm) / 2.0f) - (w / 2.0f), ((-vm) / 2.0f) - (h / 2.0f)));
                positions.add(point((hm / 2.0f) + (w / 2.0f), ((-vm) / 2.0f) - (h / 2.0f)));
                positions.add(point(((-hm) / 2.0f) - (w / 2.0f), (vm / 2.0f) + (h / 2.0f)));
                positions.add(point((hm / 2.0f) + (w / 2.0f), (vm / 2.0f) + (h / 2.0f)));
                break;
            case ConnectionResult.LICENSE_CHECK_FAILED /*11*/:
                positions.add(point(0.0f, ((-vm) / 2.0f) - (h / 2.0f)));
                positions.add(point(hm + w, 0.0f));
                positions.add(point(0.0f, (vm / 2.0f) + (h / 2.0f)));
                positions.add(point((-hm) - w, 0.0f));
                break;
            case C0801R.styleable.Toolbar_titleTextAppearance /*12*/:
                positions.add(point((-hm) - w, ((-vm) / 2.0f) - (h / 2.0f)));
                positions.add(point(0.0f, ((-vm) / 2.0f) - (h / 2.0f)));
                positions.add(point(hm + w, ((-vm) / 2.0f) - (h / 2.0f)));
                positions.add(point(((-hm) / 2.0f) - (w / 2.0f), (vm / 2.0f) + (h / 2.0f)));
                positions.add(point((hm / 2.0f) + (w / 2.0f), (vm / 2.0f) + (h / 2.0f)));
                break;
            case ConnectionResult.CANCELED /*13*/:
                positions.add(point(((-hm) / 2.0f) - (w / 2.0f), ((-vm) / 2.0f) - (h / 2.0f)));
                positions.add(point((hm / 2.0f) + (w / 2.0f), ((-vm) / 2.0f) - (h / 2.0f)));
                positions.add(point((-hm) - w, (vm / 2.0f) + (h / 2.0f)));
                positions.add(point(0.0f, (vm / 2.0f) + (h / 2.0f)));
                positions.add(point(hm + w, (vm / 2.0f) + (h / 2.0f)));
                break;
            case ConnectionResult.TIMEOUT /*14*/:
                positions.add(point(0.0f, (-vm) - h));
                positions.add(point((-hm) - w, 0.0f));
                positions.add(point(0, 0));
                positions.add(point(hm + w, 0.0f));
                positions.add(point(0.0f, vm + h));
                break;
            case ConnectionResult.INTERRUPTED /*15*/:
                positions.add(point((-hm) - w, ((-vm) / 2.0f) - (h / 2.0f)));
                positions.add(point(hm + w, ((-vm) / 2.0f) - (h / 2.0f)));
                positions.add(point(0, 0));
                positions.add(point((-hm) - w, (vm / 2.0f) + (h / 2.0f)));
                positions.add(point(hm + w, (vm / 2.0f) + (h / 2.0f)));
                break;
            case ConnectionResult.API_UNAVAILABLE /*16*/:
                positions.add(point((-hm) - w, ((-vm) / 2.0f) - (h / 2.0f)));
                positions.add(point(0.0f, ((-vm) / 2.0f) - (h / 2.0f)));
                positions.add(point(hm + w, ((-vm) / 2.0f) - (h / 2.0f)));
                positions.add(point((-hm) - w, (vm / 2.0f) + (h / 2.0f)));
                positions.add(point(0.0f, (vm / 2.0f) + (h / 2.0f)));
                positions.add(point(hm + w, (vm / 2.0f) + (h / 2.0f)));
                break;
            case ConnectionResult.SIGN_IN_FAILED /*17*/:
                positions.add(point(((-hm) / 2.0f) - (w / 2.0f), (-vm) - h));
                positions.add(point(((-hm) / 2.0f) - (w / 2.0f), 0.0f));
                positions.add(point(((-hm) / 2.0f) - (w / 2.0f), vm + h));
                positions.add(point((hm / 2.0f) + (w / 2.0f), (-vm) - h));
                positions.add(point((hm / 2.0f) + (w / 2.0f), 0.0f));
                positions.add(point((hm / 2.0f) + (w / 2.0f), vm + h));
                break;
            case ConnectionResult.SERVICE_UPDATING /*18*/:
                positions.add(point(((-hm) / 2.0f) - (w / 2.0f), (-vm) - h));
                positions.add(point((hm / 2.0f) + (w / 2.0f), (-vm) - h));
                positions.add(point((-hm) - w, 0.0f));
                positions.add(point(hm + w, 0.0f));
                positions.add(point(((-hm) / 2.0f) - (w / 2.0f), vm + h));
                positions.add(point((hm / 2.0f) + (w / 2.0f), vm + h));
                break;
            case ConnectionResult.SERVICE_MISSING_PERMISSION /*19*/:
                positions.add(point(0.0f, (-vm) - h));
                positions.add(point(hm + w, ((-vm) / 2.0f) - (h / 2.0f)));
                positions.add(point(hm + w, (vm / 2.0f) + (h / 2.0f)));
                positions.add(point(0.0f, vm + h));
                positions.add(point((-hm) - w, ((-vm) / 2.0f) - (h / 2.0f)));
                positions.add(point((-hm) - w, (vm / 2.0f) + (h / 2.0f)));
                break;
            case ConnectionResult.RESTRICTED_PROFILE /*20*/:
                positions.add(point((-hm) - w, (-vm) - h));
                positions.add(point(0.0f, (-vm) - h));
                positions.add(point(hm + w, (-vm) - h));
                positions.add(point(((-hm) / 2.0f) - (w / 2.0f), 0.0f));
                positions.add(point((hm / 2.0f) + (w / 2.0f), 0.0f));
                positions.add(point(0.0f, vm + h));
                break;
            case C0801R.styleable.AppCompatTheme_actionBarWidgetTheme /*21*/:
                positions.add(point(0.0f, (-vm) - h));
                positions.add(point(((-hm) / 2.0f) - (w / 2.0f), 0.0f));
                positions.add(point((hm / 2.0f) + (w / 2.0f), 0.0f));
                positions.add(point((-hm) - w, vm + h));
                positions.add(point(0.0f, vm + h));
                positions.add(point(hm + w, vm + h));
                break;
            case C0801R.styleable.Toolbar_collapseIcon /*22*/:
                positions.add(point((-hm) - w, (-vm) - h));
                positions.add(point(0.0f, (-vm) - h));
                positions.add(point(hm + w, (-vm) - h));
                positions.add(point((-hm) - w, 0.0f));
                positions.add(point(0, 0));
                positions.add(point(hm + w, 0.0f));
                positions.add(point(0.0f, vm + h));
                break;
            case C0801R.styleable.Toolbar_collapseContentDescription /*23*/:
                positions.add(point(0.0f, (-vm) - h));
                positions.add(point((-hm) - w, 0.0f));
                positions.add(point(0, 0));
                positions.add(point(hm + w, 0.0f));
                positions.add(point((-hm) - w, vm + h));
                positions.add(point(0.0f, vm + h));
                positions.add(point(hm + w, vm + h));
                break;
            case C0801R.styleable.Toolbar_navigationIcon /*24*/:
                positions.add(point(((-hm) / 2.0f) - (w / 2.0f), (-vm) - h));
                positions.add(point((hm / 2.0f) + (w / 2.0f), (-vm) - h));
                positions.add(point((-hm) - w, 0.0f));
                positions.add(point(0, 0));
                positions.add(point(hm + w, 0.0f));
                positions.add(point(((-hm) / 2.0f) - (w / 2.0f), vm + h));
                positions.add(point((hm / 2.0f) + (w / 2.0f), vm + h));
                break;
            case MessagingStyle.MAXIMUM_RETAINED_MESSAGES /*25*/:
                positions.add(point(0.0f, (-vm) - h));
                positions.add(point(hm + w, ((-vm) / 2.0f) - (h / 2.0f)));
                positions.add(point(hm + w, (vm / 2.0f) + (h / 2.0f)));
                positions.add(point(0, 0));
                positions.add(point(0.0f, vm + h));
                positions.add(point((-hm) - w, ((-vm) / 2.0f) - (h / 2.0f)));
                positions.add(point((-hm) - w, (vm / 2.0f) + (h / 2.0f)));
                break;
            case C0801R.styleable.Toolbar_logoDescription /*26*/:
                positions.add(point((((-hm) * 3.0f) / 2.0f) - ((3.0f * w) / 2.0f), ((-vm) / 2.0f) - (h / 2.0f)));
                positions.add(point(((-hm) / 2.0f) - (w / 2.0f), ((-vm) / 2.0f) - (h / 2.0f)));
                positions.add(point((hm / 2.0f) + (w / 2.0f), ((-vm) / 2.0f) - (h / 2.0f)));
                positions.add(point(((3.0f * hm) / 2.0f) + ((3.0f * w) / 2.0f), ((-vm) / 2.0f) - (h / 2.0f)));
                positions.add(point((-hm) - w, (vm / 2.0f) + (h / 2.0f)));
                positions.add(point(0.0f, (vm / 2.0f) + (h / 2.0f)));
                positions.add(point(hm + w, (vm / 2.0f) + (h / 2.0f)));
                break;
            case C0801R.styleable.Toolbar_titleTextColor /*27*/:
                positions.add(point((-hm) - w, ((-vm) / 2.0f) - (h / 2.0f)));
                positions.add(point(0.0f, ((-vm) / 2.0f) - (h / 2.0f)));
                positions.add(point(hm + w, ((-vm) / 2.0f) - (h / 2.0f)));
                positions.add(point((((-hm) * 3.0f) / 2.0f) - ((3.0f * w) / 2.0f), (vm / 2.0f) + (h / 2.0f)));
                positions.add(point(((-hm) / 2.0f) - (w / 2.0f), (vm / 2.0f) + (h / 2.0f)));
                positions.add(point((hm / 2.0f) + (w / 2.0f), (vm / 2.0f) + (h / 2.0f)));
                positions.add(point(((3.0f * hm) / 2.0f) + ((3.0f * w) / 2.0f), (vm / 2.0f) + (h / 2.0f)));
                break;
            case C0801R.styleable.Toolbar_subtitleTextColor /*28*/:
                positions.add(point((-hm) - w, (-vm) - h));
                positions.add(point(0.0f, (-vm) - h));
                positions.add(point(hm + w, (-vm) - h));
                positions.add(point(((-hm) / 2.0f) - (w / 2.0f), 0.0f));
                positions.add(point((hm / 2.0f) + (w / 2.0f), 0.0f));
                positions.add(point((-hm) - w, vm + h));
                positions.add(point(0.0f, vm + h));
                positions.add(point(hm + w, vm + h));
                break;
            case C0801R.styleable.AppCompatTheme_actionModeBackground /*29*/:
                positions.add(point((-hm) - w, (-vm) - h));
                positions.add(point((-hm) - w, 0.0f));
                positions.add(point((-hm) - w, vm + h));
                positions.add(point(0.0f, ((-vm) / 2.0f) - (h / 2.0f)));
                positions.add(point(0.0f, (vm / 2.0f) + (h / 2.0f)));
                positions.add(point(hm + w, (-vm) - h));
                positions.add(point(hm + w, 0.0f));
                positions.add(point(hm + w, vm + h));
                break;
            case C0801R.styleable.AppCompatTheme_actionModeSplitBackground /*30*/:
                positions.add(point((-hm) - w, (-vm) - h));
                positions.add(point(0.0f, (-vm) - h));
                positions.add(point(hm + w, (-vm) - h));
                positions.add(point((-hm) - w, 0.0f));
                positions.add(point(hm + w, 0.0f));
                positions.add(point((-hm) - w, vm + h));
                positions.add(point(0.0f, vm + h));
                positions.add(point(hm + w, vm + h));
                break;
            case C0801R.styleable.AppCompatTheme_actionModeCloseDrawable /*31*/:
                positions.add(point(0.0f, ((-vm) * 2.0f) - (2.0f * h)));
                positions.add(point((hm / 2.0f) + (w / 2.0f), (-vm) - h));
                positions.add(point(hm + w, 0.0f));
                positions.add(point((hm / 2.0f) + (w / 2.0f), vm + h));
                positions.add(point(0.0f, (2.0f * vm) + (2.0f * h)));
                positions.add(point(((-hm) / 2.0f) - (w / 2.0f), vm + h));
                positions.add(point((-hm) - w, 0.0f));
                positions.add(point(((-hm) / 2.0f) - (w / 2.0f), (-vm) - h));
                break;
            case ItemTouchHelper.END /*32*/:
                positions.add(point(0.0f, (-vm) - h));
                positions.add(point(hm + w, ((-vm) / 2.0f) - (h / 2.0f)));
                positions.add(point((2.0f * hm) + (2.0f * w), 0.0f));
                positions.add(point(hm + w, (vm / 2.0f) + (h / 2.0f)));
                positions.add(point(0.0f, vm + h));
                positions.add(point((-hm) - w, (vm / 2.0f) + (h / 2.0f)));
                positions.add(point(((-hm) * 2.0f) - (2.0f * w), 0.0f));
                positions.add(point((-hm) - w, ((-vm) / 2.0f) - (h / 2.0f)));
                break;
            case C0801R.styleable.AppCompatTheme_actionModeCopyDrawable /*33*/:
                positions.add(point((((-hm) * 3.0f) / 2.0f) - ((3.0f * w) / 2.0f), ((-vm) / 2.0f) - (h / 2.0f)));
                positions.add(point(((-hm) / 2.0f) - (w / 2.0f), ((-vm) / 2.0f) - (h / 2.0f)));
                positions.add(point((hm / 2.0f) + (w / 2.0f), ((-vm) / 2.0f) - (h / 2.0f)));
                positions.add(point(((3.0f * hm) / 2.0f) + ((3.0f * w) / 2.0f), ((-vm) / 2.0f) - (h / 2.0f)));
                positions.add(point((((-hm) * 3.0f) / 2.0f) - ((3.0f * w) / 2.0f), (vm / 2.0f) + (h / 2.0f)));
                positions.add(point(((-hm) / 2.0f) - (w / 2.0f), (vm / 2.0f) + (h / 2.0f)));
                positions.add(point((hm / 2.0f) + (w / 2.0f), (vm / 2.0f) + (h / 2.0f)));
                positions.add(point(((3.0f * hm) / 2.0f) + ((3.0f * w) / 2.0f), (vm / 2.0f) + (h / 2.0f)));
                break;
            case C0801R.styleable.AppCompatTheme_actionModePasteDrawable /*34*/:
                positions.add(point(((-hm) / 2.0f) - (w / 2.0f), (((-vm) * 3.0f) / 2.0f) - ((3.0f * h) / 2.0f)));
                positions.add(point((hm / 2.0f) + (w / 2.0f), (((-vm) * 3.0f) / 2.0f) - ((3.0f * h) / 2.0f)));
                positions.add(point(((-hm) / 2.0f) - (w / 2.0f), ((-vm) / 2.0f) - (h / 2.0f)));
                positions.add(point((hm / 2.0f) + (w / 2.0f), ((-vm) / 2.0f) - (h / 2.0f)));
                positions.add(point(((-hm) / 2.0f) - (w / 2.0f), (vm / 2.0f) + (h / 2.0f)));
                positions.add(point((hm / 2.0f) + (w / 2.0f), (vm / 2.0f) + (h / 2.0f)));
                positions.add(point(((-hm) / 2.0f) - (w / 2.0f), ((3.0f * vm) / 2.0f) + ((3.0f * h) / 2.0f)));
                positions.add(point((hm / 2.0f) + (w / 2.0f), ((3.0f * vm) / 2.0f) + ((3.0f * h) / 2.0f)));
                break;
            case C0801R.styleable.AppCompatTheme_actionModeSelectAllDrawable /*35*/:
                positions.add(point((-hm) - w, (-vm) - h));
                positions.add(point(0.0f, (-vm) - h));
                positions.add(point(hm + w, (-vm) - h));
                positions.add(point((-hm) - w, 0.0f));
                positions.add(point(0, 0));
                positions.add(point(hm + w, 0.0f));
                positions.add(point((-hm) - w, vm + h));
                positions.add(point(0.0f, vm + h));
                positions.add(point(hm + w, vm + h));
                break;
            case C0801R.styleable.AppCompatTheme_actionModeShareDrawable /*36*/:
                positions.add(point(0.0f, ((-vm) * 2.0f) - (2.0f * h)));
                positions.add(point((hm / 2.0f) + (w / 2.0f), (-vm) - h));
                positions.add(point(hm + w, 0.0f));
                positions.add(point((hm / 2.0f) + (w / 2.0f), vm + h));
                positions.add(point(0.0f, (2.0f * vm) + (2.0f * h)));
                positions.add(point(((-hm) / 2.0f) - (w / 2.0f), vm + h));
                positions.add(point((-hm) - w, 0.0f));
                positions.add(point(((-hm) / 2.0f) - (w / 2.0f), (-vm) - h));
                positions.add(point(0, 0));
                break;
            case LangUtils.HASH_OFFSET /*37*/:
                positions.add(point(0.0f, (-vm) - h));
                positions.add(point(hm + w, ((-vm) / 2.0f) - (h / 2.0f)));
                positions.add(point((2.0f * hm) + (2.0f * w), 0.0f));
                positions.add(point(hm + w, (vm / 2.0f) + (h / 2.0f)));
                positions.add(point(0.0f, vm + h));
                positions.add(point((-hm) - w, (vm / 2.0f) + (h / 2.0f)));
                positions.add(point(((-hm) * 2.0f) - (2.0f * w), 0.0f));
                positions.add(point((-hm) - w, ((-vm) / 2.0f) - (h / 2.0f)));
                positions.add(point(0, 0));
                break;
        }
        switch (C07461.$SwitchMap$com$nightonke$boommenu$BoomButtons$ButtonPlaceEnum[placeEnum.ordinal()]) {
            case ConnectionResult.INTERNAL_ERROR /*8*/:
                adjustOffset(positions, 0.0f, calculateYOffsetToCenter(hm, vm, w, h));
                break;
            case ConnectionResult.SERVICE_INVALID /*9*/:
                adjustOffset(positions, 0.0f, -calculateYOffsetToCenter(hm, vm, w, h));
                break;
            case ConnectionResult.LICENSE_CHECK_FAILED /*11*/:
            case C0801R.styleable.Toolbar_titleTextAppearance /*12*/:
            case ConnectionResult.CANCELED /*13*/:
            case ConnectionResult.TIMEOUT /*14*/:
            case ConnectionResult.INTERRUPTED /*15*/:
            case ConnectionResult.API_UNAVAILABLE /*16*/:
            case ConnectionResult.SIGN_IN_FAILED /*17*/:
            case ConnectionResult.SERVICE_UPDATING /*18*/:
            case ConnectionResult.SERVICE_MISSING_PERMISSION /*19*/:
            case ConnectionResult.RESTRICTED_PROFILE /*20*/:
            case C0801R.styleable.AppCompatTheme_actionBarWidgetTheme /*21*/:
            case C0801R.styleable.Toolbar_collapseIcon /*22*/:
            case C0801R.styleable.Toolbar_collapseContentDescription /*23*/:
            case C0801R.styleable.Toolbar_navigationIcon /*24*/:
            case MessagingStyle.MAXIMUM_RETAINED_MESSAGES /*25*/:
            case C0801R.styleable.Toolbar_logoDescription /*26*/:
            case C0801R.styleable.Toolbar_titleTextColor /*27*/:
            case C0801R.styleable.Toolbar_subtitleTextColor /*28*/:
            case C0801R.styleable.AppCompatTheme_actionModeBackground /*29*/:
            case C0801R.styleable.AppCompatTheme_actionModeSplitBackground /*30*/:
            case C0801R.styleable.AppCompatTheme_actionModeCloseDrawable /*31*/:
            case ItemTouchHelper.END /*32*/:
            case C0801R.styleable.AppCompatTheme_actionModeCopyDrawable /*33*/:
            case C0801R.styleable.AppCompatTheme_actionModePasteDrawable /*34*/:
            case C0801R.styleable.AppCompatTheme_actionModeSelectAllDrawable /*35*/:
            case C0801R.styleable.AppCompatTheme_actionModeShareDrawable /*36*/:
            case LangUtils.HASH_OFFSET /*37*/:
                adjustOffset(positions, 0.0f, (h - w) / 2.0f);
                break;
        }
        calculatePositionsInParent(positions, parentSize);
        calculateOffset(positions, alignmentEnum, parentSize, w, h, buttonTopMargin, buttonBottomMargin, buttonLeftMargin, buttonRightMargin);
        return positions;
    }

    public static ArrayList<Point> getHamButtonPositions(ButtonPlaceEnum placeEnum, ButtonPlaceAlignmentEnum alignmentEnum, Point parentSize, float buttonWidth, float buttonHeight, int buttonNumber, float buttonHorizontalMargin, float buttonVerticalMargin, float buttonTopMargin, float buttonBottomMargin, float buttonLeftMargin, float buttonRightMargin, Float bottomHamButtonTopMargin) {
        ArrayList<Point> positions = new ArrayList(buttonNumber);
        float w = buttonWidth;
        float h = buttonHeight;
        float hm = buttonHorizontalMargin;
        float vm = buttonVerticalMargin;
        int half = buttonNumber / 2;
        int i;
        switch (C07461.$SwitchMap$com$nightonke$boommenu$BoomButtons$ButtonPlaceEnum[placeEnum.ordinal()]) {
            case OnSubscribeConcatMap.BOUNDARY /*1*/:
                if (buttonNumber % 2 != 0) {
                    for (i = half - 1; i >= 0; i--) {
                        positions.add(point(((-w) - hm) - (((float) i) * (w + hm)), 0.0f));
                    }
                    positions.add(point(0, 0));
                    for (i = 0; i < half; i++) {
                        positions.add(point((w + hm) + (((float) i) * (w + hm)), 0.0f));
                    }
                    break;
                }
                for (i = half - 1; i >= 0; i--) {
                    positions.add(point((((-w) / 2.0f) - (hm / 2.0f)) - (((float) i) * (w + hm)), 0.0f));
                }
                for (i = 0; i < half; i++) {
                    positions.add(point(((w / 2.0f) + (hm / 2.0f)) + (((float) i) * (w + hm)), 0.0f));
                }
                break;
            case OnSubscribeConcatMap.END /*2*/:
            case C0801R.styleable.AppCompatTheme_actionModeWebSearchDrawable /*38*/:
            case C0801R.styleable.AppCompatTheme_actionModePopupWindowStyle /*39*/:
            case C0801R.styleable.AppCompatTheme_textAppearanceLargePopupMenu /*40*/:
            case C0801R.styleable.AppCompatTheme_textAppearanceSmallPopupMenu /*41*/:
            case C0784R.styleable.AppCompatTheme_textAppearancePopupMenuHeader /*42*/:
            case C0801R.styleable.AppCompatTheme_dialogTheme /*43*/:
                if (buttonNumber % 2 == 0) {
                    for (i = half - 1; i >= 0; i--) {
                        positions.add(point(0.0f, (((-h) / 2.0f) - (vm / 2.0f)) - (((float) i) * (h + vm))));
                    }
                    for (i = 0; i < half; i++) {
                        positions.add(point(0.0f, ((h / 2.0f) + (vm / 2.0f)) + (((float) i) * (h + vm))));
                    }
                } else {
                    for (i = half - 1; i >= 0; i--) {
                        positions.add(point(0.0f, ((-h) - vm) - (((float) i) * (h + vm))));
                    }
                    positions.add(point(0, 0));
                    for (i = 0; i < half; i++) {
                        positions.add(point(0.0f, (h + vm) + (((float) i) * (h + vm))));
                    }
                }
                if (buttonNumber >= 2 && bottomHamButtonTopMargin != null) {
                    ((Point) positions.get(positions.size() - 1)).offset(0, (int) (bottomHamButtonTopMargin.floatValue() - vm));
                    break;
                }
        }
        calculatePositionsInParent(positions, parentSize);
        calculateOffset(positions, alignmentEnum, parentSize, w, h, buttonTopMargin, buttonBottomMargin, buttonLeftMargin, buttonRightMargin);
        return positions;
    }

    private static void calculatePositionsInParent(ArrayList<Point> positions, Point parentSize) {
        for (int i = 0; i < positions.size(); i++) {
            Point point = (Point) positions.get(i);
            positions.set(i, new Point((int) (((double) point.x) + (((double) parentSize.x) / 2.0d)), (int) (((double) point.y) + (((double) parentSize.y) / 2.0d))));
        }
    }

    private static float calculateYOffsetToCenter(float horizontalMargin, float verticalMargin, float width, float height) {
        return (((horizontalMargin / 2.0f) + (width / 2.0f)) * ((horizontalMargin / 2.0f) + (width / 2.0f))) / (verticalMargin + height);
    }

    private static void adjustOffset(ArrayList<Point> position, float x, float y) {
        for (int i = 0; i < position.size(); i++) {
            position.set(i, point(((float) ((Point) position.get(i)).x) + x, ((float) ((Point) position.get(i)).y) + y));
        }
    }

    private static void calculateOffset(ArrayList<Point> positions, ButtonPlaceAlignmentEnum alignmentEnum, Point parentSize, float width, float height, float buttonTopMargin, float buttonBottomMargin, float buttonLeftMargin, float buttonRightMargin) {
        int minHeight = UrlImageViewHelper.CACHE_DURATION_INFINITE;
        int maxHeight = Target.SIZE_ORIGINAL;
        int minWidth = UrlImageViewHelper.CACHE_DURATION_INFINITE;
        int maxWidth = Target.SIZE_ORIGINAL;
        Point offset = new Point(0, 0);
        Iterator it = positions.iterator();
        while (it.hasNext()) {
            Point position = (Point) it.next();
            maxHeight = Math.max(maxHeight, position.y);
            minHeight = Math.min(minHeight, position.y);
            maxWidth = Math.max(maxWidth, position.x);
            minWidth = Math.min(minWidth, position.x);
        }
        switch (C07461.f15xaf1aa661[alignmentEnum.ordinal()]) {
            case OnSubscribeConcatMap.END /*2*/:
                offset.y = (int) (((height / 2.0f) + buttonTopMargin) - ((float) minHeight));
                break;
            case ConnectionResult.SERVICE_DISABLED /*3*/:
                offset.y = (int) (((((float) parentSize.y) - (height / 2.0f)) - ((float) maxHeight)) - buttonBottomMargin);
                break;
            case ConnectionResult.SIGN_IN_REQUIRED /*4*/:
                offset.x = (int) (((width / 2.0f) + buttonLeftMargin) - ((float) minWidth));
                break;
            case ConnectionResult.INVALID_ACCOUNT /*5*/:
                offset.x = (int) (((((float) parentSize.x) - (width / 2.0f)) - ((float) maxWidth)) - buttonRightMargin);
                break;
            case ConnectionResult.RESOLUTION_REQUIRED /*6*/:
                offset.y = (int) (((height / 2.0f) + buttonTopMargin) - ((float) minHeight));
                offset.x = (int) (((width / 2.0f) + buttonLeftMargin) - ((float) minWidth));
                break;
            case ConnectionResult.NETWORK_ERROR /*7*/:
                offset.y = (int) (((height / 2.0f) + buttonTopMargin) - ((float) minHeight));
                offset.x = (int) (((((float) parentSize.x) - (width / 2.0f)) - ((float) maxWidth)) - buttonRightMargin);
                break;
            case ConnectionResult.INTERNAL_ERROR /*8*/:
                offset.y = (int) (((((float) parentSize.y) - (height / 2.0f)) - ((float) maxHeight)) - buttonBottomMargin);
                offset.x = (int) (((width / 2.0f) + buttonLeftMargin) - ((float) minWidth));
                break;
            case ConnectionResult.SERVICE_INVALID /*9*/:
                offset.y = (int) (((((float) parentSize.y) - (height / 2.0f)) - ((float) maxHeight)) - buttonBottomMargin);
                offset.x = (int) (((((float) parentSize.x) - (width / 2.0f)) - ((float) maxWidth)) - buttonRightMargin);
                break;
            case ConnectionResult.DEVELOPER_ERROR /*10*/:
                throw new RuntimeException("Unknown button-place-alignment-enum!");
        }
        for (int i = 0; i < positions.size(); i++) {
            position = (Point) positions.get(i);
            positions.set(i, new Point(position.x + offset.x, position.y + offset.y));
        }
    }

    private static Point point(float x, float y) {
        return new Point((int) x, (int) y);
    }

    private static Point point(double x, double y) {
        return new Point((int) x, (int) y);
    }

    private static Point point(int x, int y) {
        return new Point(x, y);
    }

    static {
        ourInstance = new ButtonPlaceManager();
    }

    public static ButtonPlaceManager getInstance() {
        return ourInstance;
    }

    private ButtonPlaceManager() {
    }
}
