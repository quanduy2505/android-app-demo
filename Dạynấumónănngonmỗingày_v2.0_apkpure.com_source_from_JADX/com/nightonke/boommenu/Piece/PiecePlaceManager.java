package com.nightonke.boommenu.Piece;

import android.content.Context;
import android.graphics.Point;
import android.support.v4.app.NotificationCompat.MessagingStyle;
import android.support.v7.widget.helper.ItemTouchHelper;
import com.google.android.gms.common.ConnectionResult;
import com.tapadoo.android.C0784R;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import me.wangyuwei.loadingview.C0801R;
import org.apache.http.util.LangUtils;
import rx.internal.operators.OnSubscribeConcatMap;
import rx.internal.schedulers.NewThreadWorker;

public class PiecePlaceManager {
    private static PiecePlaceManager ourInstance;

    /* renamed from: com.nightonke.boommenu.Piece.PiecePlaceManager.1 */
    static class C07611 implements Comparator<Point> {
        C07611() {
        }

        public int compare(Point lhs, Point rhs) {
            return Integer.valueOf(lhs.y).compareTo(Integer.valueOf(rhs.y));
        }
    }

    /* renamed from: com.nightonke.boommenu.Piece.PiecePlaceManager.2 */
    static /* synthetic */ class C07622 {
        static final /* synthetic */ int[] $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum;

        static {
            $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum = new int[PiecePlaceEnum.values().length];
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_1.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_2_1.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_2_2.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_3_1.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_3_2.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_3_3.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_3_4.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_4_1.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_4_2.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_5_1.ordinal()] = 10;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_5_2.ordinal()] = 11;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_5_3.ordinal()] = 12;
            } catch (NoSuchFieldError e12) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_5_4.ordinal()] = 13;
            } catch (NoSuchFieldError e13) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_6_1.ordinal()] = 14;
            } catch (NoSuchFieldError e14) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_6_2.ordinal()] = 15;
            } catch (NoSuchFieldError e15) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_6_3.ordinal()] = 16;
            } catch (NoSuchFieldError e16) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_6_4.ordinal()] = 17;
            } catch (NoSuchFieldError e17) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_6_5.ordinal()] = 18;
            } catch (NoSuchFieldError e18) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_6_6.ordinal()] = 19;
            } catch (NoSuchFieldError e19) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_7_1.ordinal()] = 20;
            } catch (NoSuchFieldError e20) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_7_2.ordinal()] = 21;
            } catch (NoSuchFieldError e21) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_7_3.ordinal()] = 22;
            } catch (NoSuchFieldError e22) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_7_4.ordinal()] = 23;
            } catch (NoSuchFieldError e23) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_7_5.ordinal()] = 24;
            } catch (NoSuchFieldError e24) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_7_6.ordinal()] = 25;
            } catch (NoSuchFieldError e25) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_8_1.ordinal()] = 26;
            } catch (NoSuchFieldError e26) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_8_2.ordinal()] = 27;
            } catch (NoSuchFieldError e27) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_8_3.ordinal()] = 28;
            } catch (NoSuchFieldError e28) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_8_4.ordinal()] = 29;
            } catch (NoSuchFieldError e29) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_8_5.ordinal()] = 30;
            } catch (NoSuchFieldError e30) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_8_6.ordinal()] = 31;
            } catch (NoSuchFieldError e31) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_8_7.ordinal()] = 32;
            } catch (NoSuchFieldError e32) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_9_1.ordinal()] = 33;
            } catch (NoSuchFieldError e33) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_9_2.ordinal()] = 34;
            } catch (NoSuchFieldError e34) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_9_3.ordinal()] = 35;
            } catch (NoSuchFieldError e35) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.Share.ordinal()] = 36;
            } catch (NoSuchFieldError e36) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.HAM_1.ordinal()] = 37;
            } catch (NoSuchFieldError e37) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.HAM_2.ordinal()] = 38;
            } catch (NoSuchFieldError e38) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.HAM_3.ordinal()] = 39;
            } catch (NoSuchFieldError e39) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.HAM_4.ordinal()] = 40;
            } catch (NoSuchFieldError e40) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.HAM_5.ordinal()] = 41;
            } catch (NoSuchFieldError e41) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.HAM_6.ordinal()] = 42;
            } catch (NoSuchFieldError e42) {
            }
        }
    }

    public static ArrayList<Point> getDotPositions(PiecePlaceEnum piecePlaceEnum, Point parentSize, int dotRadius, int dotHorizontalMargin, int dotVerticalMargin, int dotInclinedMargin) {
        ArrayList<Point> positions = new ArrayList();
        float r = (float) dotRadius;
        float hm = (float) dotHorizontalMargin;
        float vm = (float) dotVerticalMargin;
        float im = (float) dotInclinedMargin;
        float b;
        float c;
        float a;
        float e;
        switch (C07622.$SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[piecePlaceEnum.ordinal()]) {
            case OnSubscribeConcatMap.BOUNDARY /*1*/:
                positions.add(point(0, 0));
                break;
            case OnSubscribeConcatMap.END /*2*/:
                positions.add(point(((-hm) / 2.0f) - r, 0.0f));
                positions.add(point((hm / 2.0f) + r, 0.0f));
                break;
            case ConnectionResult.SERVICE_DISABLED /*3*/:
                positions.add(point(0.0f, ((-vm) / 2.0f) - r));
                positions.add(point(0.0f, (vm / 2.0f) + r));
                break;
            case ConnectionResult.SIGN_IN_REQUIRED /*4*/:
                positions.add(point((-hm) - (2.0f * r), 0.0f));
                positions.add(point(0, 0));
                positions.add(point((2.0f * r) + hm, 0.0f));
                break;
            case ConnectionResult.INVALID_ACCOUNT /*5*/:
                positions.add(point(0.0f, (-hm) - (2.0f * r)));
                positions.add(point(0, 0));
                positions.add(point(0.0f, (2.0f * r) + hm));
                break;
            case ConnectionResult.RESOLUTION_REQUIRED /*6*/:
                b = (hm / 2.0f) + r;
                c = (float) (((double) b) / (Math.sqrt(3.0d) / 2.0d));
                a = c / 2.0f;
                positions.add(point(-b, -a));
                positions.add(point(b, -a));
                positions.add(point(0.0f, c));
                break;
            case ConnectionResult.NETWORK_ERROR /*7*/:
                b = (hm / 2.0f) + r;
                c = (float) (((double) b) / (Math.sqrt(3.0d) / 2.0d));
                a = c / 2.0f;
                positions.add(point(0.0f, -c));
                positions.add(point(-b, a));
                positions.add(point(b, a));
                break;
            case ConnectionResult.INTERNAL_ERROR /*8*/:
                positions.add(point(((-hm) / 2.0f) - r, ((-vm) / 2.0f) - r));
                positions.add(point((hm / 2.0f) + r, ((-vm) / 2.0f) - r));
                positions.add(point(((-hm) / 2.0f) - r, (vm / 2.0f) + r));
                positions.add(point((hm / 2.0f) + r, (vm / 2.0f) + r));
                break;
            case ConnectionResult.SERVICE_INVALID /*9*/:
                a = (float) (((double) ((2.0f * r) + im)) / Math.sqrt(2.0d));
                positions.add(point(0.0f, -a));
                positions.add(point(a, 0.0f));
                positions.add(point(0.0f, a));
                positions.add(point(-a, 0.0f));
                break;
            case ConnectionResult.DEVELOPER_ERROR /*10*/:
                b = (hm / 2.0f) + r;
                c = (float) (((double) b) / (Math.sqrt(3.0d) / 2.0d));
                a = c / 2.0f;
                positions.add(point(-2.0f * b, -a));
                positions.add(point(0.0f, -a));
                positions.add(point(2.0f * b, -a));
                positions.add(point(((-hm) / 2.0f) - r, c));
                positions.add(point((hm / 2.0f) + r, c));
                break;
            case ConnectionResult.LICENSE_CHECK_FAILED /*11*/:
                b = (hm / 2.0f) + r;
                c = (float) (((double) b) / (Math.sqrt(3.0d) / 2.0d));
                a = c / 2.0f;
                positions.add(point(((-hm) / 2.0f) - r, -c));
                positions.add(point((hm / 2.0f) + r, -c));
                positions.add(point(-2.0f * b, a));
                positions.add(point(0.0f, a));
                positions.add(point(2.0f * b, a));
                break;
            case C0801R.styleable.Toolbar_titleTextAppearance /*12*/:
                positions.add(point(0, 0));
                positions.add(point(0.0f, (-vm) - (2.0f * r)));
                positions.add(point((2.0f * r) + hm, 0.0f));
                positions.add(point(0.0f, (2.0f * r) + vm));
                positions.add(point((-hm) - (2.0f * r), 0.0f));
                break;
            case ConnectionResult.CANCELED /*13*/:
                a = (float) (((double) ((2.0f * r) + im)) / Math.sqrt(2.0d));
                positions.add(point(0, 0));
                positions.add(point(a, -a));
                positions.add(point(a, a));
                positions.add(point(-a, a));
                positions.add(point(-a, -a));
                break;
            case ConnectionResult.TIMEOUT /*14*/:
                positions.add(point((-hm) - (2.0f * r), ((-vm) / 2.0f) - r));
                positions.add(point(0.0f, ((-vm) / 2.0f) - r));
                positions.add(point((2.0f * r) + hm, ((-vm) / 2.0f) - r));
                positions.add(point((-hm) - (2.0f * r), (vm / 2.0f) + r));
                positions.add(point(0.0f, (vm / 2.0f) + r));
                positions.add(point((2.0f * r) + hm, (vm / 2.0f) + r));
                break;
            case ConnectionResult.INTERRUPTED /*15*/:
                positions.add(point(((-hm) / 2.0f) - r, (-vm) - (2.0f * r)));
                positions.add(point(((-hm) / 2.0f) - r, 0.0f));
                positions.add(point(((-hm) / 2.0f) - r, (2.0f * r) + vm));
                positions.add(point((hm / 2.0f) + r, (-vm) - (2.0f * r)));
                positions.add(point((hm / 2.0f) + r, 0.0f));
                positions.add(point((hm / 2.0f) + r, (2.0f * r) + vm));
                break;
            case ConnectionResult.API_UNAVAILABLE /*16*/:
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
            case ConnectionResult.SIGN_IN_FAILED /*17*/:
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
            case ConnectionResult.SERVICE_UPDATING /*18*/:
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
            case ConnectionResult.SERVICE_MISSING_PERMISSION /*19*/:
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
            case ConnectionResult.RESTRICTED_PROFILE /*20*/:
                positions.add(point((-hm) - (2.0f * r), (-vm) - (2.0f * r)));
                positions.add(point(0.0f, (-vm) - (2.0f * r)));
                positions.add(point((2.0f * r) + hm, (-vm) - (2.0f * r)));
                positions.add(point((-hm) - (2.0f * r), 0.0f));
                positions.add(point(0, 0));
                positions.add(point((2.0f * r) + hm, 0.0f));
                positions.add(point(0.0f, (2.0f * r) + vm));
                break;
            case C0801R.styleable.AppCompatTheme_actionBarWidgetTheme /*21*/:
                positions.add(point(0.0f, (-vm) - (2.0f * r)));
                positions.add(point((-hm) - (2.0f * r), 0.0f));
                positions.add(point(0, 0));
                positions.add(point((2.0f * r) + hm, 0.0f));
                positions.add(point((-hm) - (2.0f * r), (2.0f * r) + vm));
                positions.add(point(0.0f, (2.0f * r) + vm));
                positions.add(point((2.0f * r) + hm, (2.0f * r) + vm));
                break;
            case C0801R.styleable.Toolbar_collapseIcon /*22*/:
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
            case C0801R.styleable.Toolbar_collapseContentDescription /*23*/:
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
            case C0801R.styleable.Toolbar_navigationIcon /*24*/:
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
            case MessagingStyle.MAXIMUM_RETAINED_MESSAGES /*25*/:
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
            case C0801R.styleable.Toolbar_logoDescription /*26*/:
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
            case C0801R.styleable.Toolbar_titleTextColor /*27*/:
                b = (vm / 2.0f) + r;
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
            case C0801R.styleable.Toolbar_subtitleTextColor /*28*/:
                positions.add(point((-hm) - (2.0f * r), (-vm) - (2.0f * r)));
                positions.add(point(0.0f, (-vm) - (2.0f * r)));
                positions.add(point((2.0f * r) + hm, (-vm) - (2.0f * r)));
                positions.add(point((-hm) - (2.0f * r), 0.0f));
                positions.add(point((2.0f * r) + hm, 0.0f));
                positions.add(point((-hm) - (2.0f * r), (2.0f * r) + vm));
                positions.add(point(0.0f, (2.0f * r) + vm));
                positions.add(point((2.0f * r) + hm, (2.0f * r) + vm));
                break;
            case C0801R.styleable.AppCompatTheme_actionModeBackground /*29*/:
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
            case C0801R.styleable.AppCompatTheme_actionModeSplitBackground /*30*/:
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
            case C0801R.styleable.AppCompatTheme_actionModeCloseDrawable /*31*/:
                positions.add(point((((-hm) * 3.0f) / 2.0f) - (3.0f * r), ((-vm) / 2.0f) - r));
                positions.add(point(((-hm) / 2.0f) - r, ((-vm) / 2.0f) - r));
                positions.add(point((hm / 2.0f) + r, ((-vm) / 2.0f) - r));
                positions.add(point(((3.0f * hm) / 2.0f) + (3.0f * r), ((-vm) / 2.0f) - r));
                positions.add(point((((-hm) * 3.0f) / 2.0f) - (3.0f * r), (vm / 2.0f) + r));
                positions.add(point(((-hm) / 2.0f) - r, (vm / 2.0f) + r));
                positions.add(point((hm / 2.0f) + r, (vm / 2.0f) + r));
                positions.add(point(((3.0f * hm) / 2.0f) + (3.0f * r), (vm / 2.0f) + r));
                break;
            case ItemTouchHelper.END /*32*/:
                positions.add(point(((-hm) / 2.0f) - r, (((-vm) * 3.0f) / 2.0f) - (3.0f * r)));
                positions.add(point((hm / 2.0f) + r, (((-vm) * 3.0f) / 2.0f) - (3.0f * r)));
                positions.add(point(((-hm) / 2.0f) - r, ((-vm) / 2.0f) - r));
                positions.add(point((hm / 2.0f) + r, ((-vm) / 2.0f) - r));
                positions.add(point(((-hm) / 2.0f) - r, (vm / 2.0f) + r));
                positions.add(point((hm / 2.0f) + r, (vm / 2.0f) + r));
                positions.add(point(((-hm) / 2.0f) - r, ((3.0f * vm) / 2.0f) + (3.0f * r)));
                positions.add(point((hm / 2.0f) + r, ((3.0f * vm) / 2.0f) + (3.0f * r)));
                break;
            case C0801R.styleable.AppCompatTheme_actionModeCopyDrawable /*33*/:
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
            case C0801R.styleable.AppCompatTheme_actionModePasteDrawable /*34*/:
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
            case C0801R.styleable.AppCompatTheme_actionModeSelectAllDrawable /*35*/:
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
            default:
                throw new RuntimeException("Unknown piece-place-enum!");
        }
        Iterator it = positions.iterator();
        while (it.hasNext()) {
            ((Point) it.next()).offset((parentSize.x / 2) - dotRadius, (parentSize.y / 2) - dotRadius);
        }
        return positions;
    }

    public static ArrayList<Point> getHamPositions(PiecePlaceEnum piecePlaceEnum, Point parentSize, int hamWidth, int hamHeight, int hamVerticalMargin) {
        ArrayList<Point> positions = new ArrayList();
        float w = (float) hamWidth;
        float h = (float) hamHeight;
        float vm = (float) hamVerticalMargin;
        int half = piecePlaceEnum.pieceNumber() / 2;
        int i;
        if (piecePlaceEnum.pieceNumber() % 2 == 0) {
            for (i = half - 1; i >= 0; i--) {
                positions.add(point(0.0f, (((-h) / 2.0f) - (vm / 2.0f)) - (((float) i) * (h + vm))));
            }
            for (i = 0; ((float) i) < h; i++) {
                positions.add(point(0.0f, ((h / 2.0f) + (vm / 2.0f)) + (((float) i) * (h + vm))));
            }
        } else {
            for (i = half - 1; i >= 0; i--) {
                positions.add(point(0.0f, ((-h) - vm) - (((float) i) * (h + vm))));
            }
            positions.add(point(0, 0));
            for (i = 0; ((float) i) < h; i++) {
                positions.add(point(0.0f, (h + vm) + (((float) i) * (h + vm))));
            }
        }
        Iterator it = positions.iterator();
        while (it.hasNext()) {
            ((Point) it.next()).offset((int) (((float) (parentSize.x / 2)) - (w / 2.0f)), (int) (((float) (parentSize.y / 2)) - (h / 2.0f)));
        }
        return positions;
    }

    public static ArrayList<Point> getShareDotPositions(Point parentSize, int dotRadius, int dotNumber, int shareLineLength) {
        ArrayList<Point> positions = new ArrayList();
        float h = (float) ((((double) shareLineLength) * Math.sqrt(3.0d)) / 3.0d);
        for (int i = 0; i < dotNumber; i++) {
            switch (i % 3) {
                case NewThreadWorker.PURGE_FREQUENCY /*0*/:
                    positions.add(point(h / 2.0f, (float) ((-shareLineLength) / 2)));
                    break;
                case OnSubscribeConcatMap.BOUNDARY /*1*/:
                    positions.add(point(-h, 0.0f));
                    break;
                case OnSubscribeConcatMap.END /*2*/:
                    positions.add(point(h / 2.0f, (float) (shareLineLength / 2)));
                    break;
                default:
                    break;
            }
        }
        Collections.sort(positions, new C07611());
        Iterator it = positions.iterator();
        while (it.hasNext()) {
            ((Point) it.next()).offset((parentSize.x / 2) - dotRadius, (parentSize.y / 2) - dotRadius);
        }
        return positions;
    }

    public static BoomPiece createPiece(Context context, PiecePlaceEnum piecePlaceEnum, int color) {
        switch (C07622.$SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[piecePlaceEnum.ordinal()]) {
            case OnSubscribeConcatMap.BOUNDARY /*1*/:
            case OnSubscribeConcatMap.END /*2*/:
            case ConnectionResult.SERVICE_DISABLED /*3*/:
            case ConnectionResult.SIGN_IN_REQUIRED /*4*/:
            case ConnectionResult.INVALID_ACCOUNT /*5*/:
            case ConnectionResult.RESOLUTION_REQUIRED /*6*/:
            case ConnectionResult.NETWORK_ERROR /*7*/:
            case ConnectionResult.INTERNAL_ERROR /*8*/:
            case ConnectionResult.SERVICE_INVALID /*9*/:
            case ConnectionResult.DEVELOPER_ERROR /*10*/:
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
                return createDot(context, color);
            case LangUtils.HASH_OFFSET /*37*/:
            case C0801R.styleable.AppCompatTheme_actionModeWebSearchDrawable /*38*/:
            case C0801R.styleable.AppCompatTheme_actionModePopupWindowStyle /*39*/:
            case C0801R.styleable.AppCompatTheme_textAppearanceLargePopupMenu /*40*/:
            case C0801R.styleable.AppCompatTheme_textAppearanceSmallPopupMenu /*41*/:
            case C0784R.styleable.AppCompatTheme_textAppearancePopupMenuHeader /*42*/:
                return createHam(context, color);
            default:
                throw new RuntimeException("Unknown button-enum!");
        }
    }

    private static Dot createDot(Context context, int color) {
        Dot dot = new Dot(context);
        dot.init(color);
        return dot;
    }

    private static Ham createHam(Context context, int color) {
        Ham ham = new Ham(context);
        ham.init(color);
        return ham;
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
        ourInstance = new PiecePlaceManager();
    }

    public static PiecePlaceManager getInstance() {
        return ourInstance;
    }

    private PiecePlaceManager() {
    }
}
