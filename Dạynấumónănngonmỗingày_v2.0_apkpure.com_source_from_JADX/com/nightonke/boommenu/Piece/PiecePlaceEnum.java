package com.nightonke.boommenu.Piece;

import android.support.v4.app.NotificationCompat.MessagingStyle;
import android.support.v7.widget.helper.ItemTouchHelper;
import com.google.android.gms.common.ConnectionResult;
import com.tapadoo.android.C0784R;
import me.wangyuwei.loadingview.C0801R;
import org.apache.http.util.LangUtils;
import rx.internal.operators.OnSubscribeConcatMap;

public enum PiecePlaceEnum {
    DOT_1(0),
    DOT_2_1(1),
    DOT_2_2(2),
    DOT_3_1(3),
    DOT_3_2(4),
    DOT_3_3(5),
    DOT_3_4(6),
    DOT_4_1(7),
    DOT_4_2(8),
    DOT_5_1(9),
    DOT_5_2(10),
    DOT_5_3(11),
    DOT_5_4(12),
    DOT_6_1(13),
    DOT_6_2(14),
    DOT_6_3(15),
    DOT_6_4(16),
    DOT_6_5(17),
    DOT_6_6(18),
    DOT_7_1(19),
    DOT_7_2(20),
    DOT_7_3(21),
    DOT_7_4(22),
    DOT_7_5(23),
    DOT_7_6(24),
    DOT_8_1(25),
    DOT_8_2(26),
    DOT_8_3(27),
    DOT_8_4(28),
    DOT_8_5(29),
    DOT_8_6(30),
    DOT_8_7(31),
    DOT_9_1(32),
    DOT_9_2(33),
    DOT_9_3(34),
    HAM_1(35),
    HAM_2(36),
    HAM_3(37),
    HAM_4(38),
    HAM_5(39),
    HAM_6(40),
    Share(99999),
    Unknown(-1);
    
    private final int value;

    /* renamed from: com.nightonke.boommenu.Piece.PiecePlaceEnum.1 */
    static /* synthetic */ class C07601 {
        static final /* synthetic */ int[] $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum;

        static {
            $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum = new int[PiecePlaceEnum.values().length];
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_1.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.HAM_1.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_2_1.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_2_2.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.HAM_2.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_3_1.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_3_2.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_3_3.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_3_4.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.HAM_3.ordinal()] = 10;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_4_1.ordinal()] = 11;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_4_2.ordinal()] = 12;
            } catch (NoSuchFieldError e12) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.HAM_4.ordinal()] = 13;
            } catch (NoSuchFieldError e13) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_5_1.ordinal()] = 14;
            } catch (NoSuchFieldError e14) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_5_2.ordinal()] = 15;
            } catch (NoSuchFieldError e15) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_5_3.ordinal()] = 16;
            } catch (NoSuchFieldError e16) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_5_4.ordinal()] = 17;
            } catch (NoSuchFieldError e17) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.HAM_5.ordinal()] = 18;
            } catch (NoSuchFieldError e18) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_6_1.ordinal()] = 19;
            } catch (NoSuchFieldError e19) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_6_2.ordinal()] = 20;
            } catch (NoSuchFieldError e20) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_6_3.ordinal()] = 21;
            } catch (NoSuchFieldError e21) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_6_4.ordinal()] = 22;
            } catch (NoSuchFieldError e22) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_6_5.ordinal()] = 23;
            } catch (NoSuchFieldError e23) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_6_6.ordinal()] = 24;
            } catch (NoSuchFieldError e24) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.HAM_6.ordinal()] = 25;
            } catch (NoSuchFieldError e25) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_7_1.ordinal()] = 26;
            } catch (NoSuchFieldError e26) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_7_2.ordinal()] = 27;
            } catch (NoSuchFieldError e27) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_7_3.ordinal()] = 28;
            } catch (NoSuchFieldError e28) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_7_4.ordinal()] = 29;
            } catch (NoSuchFieldError e29) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_7_5.ordinal()] = 30;
            } catch (NoSuchFieldError e30) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_7_6.ordinal()] = 31;
            } catch (NoSuchFieldError e31) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_8_1.ordinal()] = 32;
            } catch (NoSuchFieldError e32) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_8_2.ordinal()] = 33;
            } catch (NoSuchFieldError e33) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_8_3.ordinal()] = 34;
            } catch (NoSuchFieldError e34) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_8_4.ordinal()] = 35;
            } catch (NoSuchFieldError e35) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_8_5.ordinal()] = 36;
            } catch (NoSuchFieldError e36) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_8_6.ordinal()] = 37;
            } catch (NoSuchFieldError e37) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_8_7.ordinal()] = 38;
            } catch (NoSuchFieldError e38) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_9_1.ordinal()] = 39;
            } catch (NoSuchFieldError e39) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_9_2.ordinal()] = 40;
            } catch (NoSuchFieldError e40) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_9_3.ordinal()] = 41;
            } catch (NoSuchFieldError e41) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.Share.ordinal()] = 42;
            } catch (NoSuchFieldError e42) {
            }
        }
    }

    private PiecePlaceEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static PiecePlaceEnum getEnum(int value) {
        if (value < 0 || value >= values().length) {
            return Unknown;
        }
        return values()[value];
    }

    public int pieceNumber() {
        switch (C07601.$SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[ordinal()]) {
            case OnSubscribeConcatMap.BOUNDARY /*1*/:
            case OnSubscribeConcatMap.END /*2*/:
                return 1;
            case ConnectionResult.SERVICE_DISABLED /*3*/:
            case ConnectionResult.SIGN_IN_REQUIRED /*4*/:
            case ConnectionResult.INVALID_ACCOUNT /*5*/:
                return 2;
            case ConnectionResult.RESOLUTION_REQUIRED /*6*/:
            case ConnectionResult.NETWORK_ERROR /*7*/:
            case ConnectionResult.INTERNAL_ERROR /*8*/:
            case ConnectionResult.SERVICE_INVALID /*9*/:
            case ConnectionResult.DEVELOPER_ERROR /*10*/:
                return 3;
            case ConnectionResult.LICENSE_CHECK_FAILED /*11*/:
            case C0801R.styleable.Toolbar_titleTextAppearance /*12*/:
            case ConnectionResult.CANCELED /*13*/:
                return 4;
            case ConnectionResult.TIMEOUT /*14*/:
            case ConnectionResult.INTERRUPTED /*15*/:
            case ConnectionResult.API_UNAVAILABLE /*16*/:
            case ConnectionResult.SIGN_IN_FAILED /*17*/:
            case ConnectionResult.SERVICE_UPDATING /*18*/:
                return 5;
            case ConnectionResult.SERVICE_MISSING_PERMISSION /*19*/:
            case ConnectionResult.RESTRICTED_PROFILE /*20*/:
            case C0801R.styleable.AppCompatTheme_actionBarWidgetTheme /*21*/:
            case C0801R.styleable.Toolbar_collapseIcon /*22*/:
            case C0801R.styleable.Toolbar_collapseContentDescription /*23*/:
            case C0801R.styleable.Toolbar_navigationIcon /*24*/:
            case MessagingStyle.MAXIMUM_RETAINED_MESSAGES /*25*/:
                return 6;
            case C0801R.styleable.Toolbar_logoDescription /*26*/:
            case C0801R.styleable.Toolbar_titleTextColor /*27*/:
            case C0801R.styleable.Toolbar_subtitleTextColor /*28*/:
            case C0801R.styleable.AppCompatTheme_actionModeBackground /*29*/:
            case C0801R.styleable.AppCompatTheme_actionModeSplitBackground /*30*/:
            case C0801R.styleable.AppCompatTheme_actionModeCloseDrawable /*31*/:
                return 7;
            case ItemTouchHelper.END /*32*/:
            case C0801R.styleable.AppCompatTheme_actionModeCopyDrawable /*33*/:
            case C0801R.styleable.AppCompatTheme_actionModePasteDrawable /*34*/:
            case C0801R.styleable.AppCompatTheme_actionModeSelectAllDrawable /*35*/:
            case C0801R.styleable.AppCompatTheme_actionModeShareDrawable /*36*/:
            case LangUtils.HASH_OFFSET /*37*/:
            case C0801R.styleable.AppCompatTheme_actionModeWebSearchDrawable /*38*/:
                return 8;
            case C0801R.styleable.AppCompatTheme_actionModePopupWindowStyle /*39*/:
            case C0801R.styleable.AppCompatTheme_textAppearanceLargePopupMenu /*40*/:
            case C0801R.styleable.AppCompatTheme_textAppearanceSmallPopupMenu /*41*/:
                return 9;
            default:
                return -1;
        }
    }

    public int minPieceNumber() {
        switch (C07601.$SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[ordinal()]) {
            case C0784R.styleable.AppCompatTheme_textAppearancePopupMenuHeader /*42*/:
                return 3;
            default:
                return -1;
        }
    }

    public int maxPieceNumber() {
        switch (C07601.$SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[ordinal()]) {
            case C0784R.styleable.AppCompatTheme_textAppearancePopupMenuHeader /*42*/:
                return 9;
            default:
                return -1;
        }
    }
}
