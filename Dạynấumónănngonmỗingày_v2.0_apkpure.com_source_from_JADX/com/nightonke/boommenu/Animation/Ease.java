package com.nightonke.boommenu.Animation;

import android.animation.TimeInterpolator;
import android.graphics.PointF;
import android.support.v4.app.NotificationCompat.MessagingStyle;
import com.google.android.gms.common.ConnectionResult;
import java.util.ArrayList;
import me.wangyuwei.loadingview.C0801R;
import rx.internal.operators.OnSubscribeConcatMap;

public class Ease implements TimeInterpolator {
    private static ArrayList<Ease> eases;
    private PointF f12a;
    private Boolean ableToDefineWithControlPoints;
    private PointF f13b;
    private PointF f14c;
    private EaseEnum easeEnum;

    /* renamed from: com.nightonke.boommenu.Animation.Ease.1 */
    static /* synthetic */ class C07381 {
        static final /* synthetic */ int[] $SwitchMap$com$nightonke$boommenu$Animation$EaseEnum;

        static {
            $SwitchMap$com$nightonke$boommenu$Animation$EaseEnum = new int[EaseEnum.values().length];
            try {
                $SwitchMap$com$nightonke$boommenu$Animation$EaseEnum[EaseEnum.EaseInBack.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Animation$EaseEnum[EaseEnum.EaseInCirc.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Animation$EaseEnum[EaseEnum.EaseInCubic.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Animation$EaseEnum[EaseEnum.EaseInExpo.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Animation$EaseEnum[EaseEnum.EaseInSine.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Animation$EaseEnum[EaseEnum.EaseInQuad.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Animation$EaseEnum[EaseEnum.EaseInQuint.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Animation$EaseEnum[EaseEnum.EaseInQuart.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Animation$EaseEnum[EaseEnum.EaseOutBack.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Animation$EaseEnum[EaseEnum.EaseOutCirc.ordinal()] = 10;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Animation$EaseEnum[EaseEnum.EaseOutCubic.ordinal()] = 11;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Animation$EaseEnum[EaseEnum.EaseOutExpo.ordinal()] = 12;
            } catch (NoSuchFieldError e12) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Animation$EaseEnum[EaseEnum.EaseOutSine.ordinal()] = 13;
            } catch (NoSuchFieldError e13) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Animation$EaseEnum[EaseEnum.EaseOutQuad.ordinal()] = 14;
            } catch (NoSuchFieldError e14) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Animation$EaseEnum[EaseEnum.EaseOutQuint.ordinal()] = 15;
            } catch (NoSuchFieldError e15) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Animation$EaseEnum[EaseEnum.EaseOutQuart.ordinal()] = 16;
            } catch (NoSuchFieldError e16) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Animation$EaseEnum[EaseEnum.EaseInOutBack.ordinal()] = 17;
            } catch (NoSuchFieldError e17) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Animation$EaseEnum[EaseEnum.EaseInOutCirc.ordinal()] = 18;
            } catch (NoSuchFieldError e18) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Animation$EaseEnum[EaseEnum.EaseInOutCubic.ordinal()] = 19;
            } catch (NoSuchFieldError e19) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Animation$EaseEnum[EaseEnum.EaseInOutExpo.ordinal()] = 20;
            } catch (NoSuchFieldError e20) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Animation$EaseEnum[EaseEnum.EaseInOutSine.ordinal()] = 21;
            } catch (NoSuchFieldError e21) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Animation$EaseEnum[EaseEnum.EaseInOutQuad.ordinal()] = 22;
            } catch (NoSuchFieldError e22) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Animation$EaseEnum[EaseEnum.EaseInOutQuint.ordinal()] = 23;
            } catch (NoSuchFieldError e23) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Animation$EaseEnum[EaseEnum.EaseInOutQuart.ordinal()] = 24;
            } catch (NoSuchFieldError e24) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Animation$EaseEnum[EaseEnum.Linear.ordinal()] = 25;
            } catch (NoSuchFieldError e25) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Animation$EaseEnum[EaseEnum.EaseInBounce.ordinal()] = 26;
            } catch (NoSuchFieldError e26) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Animation$EaseEnum[EaseEnum.EaseOutBounce.ordinal()] = 27;
            } catch (NoSuchFieldError e27) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Animation$EaseEnum[EaseEnum.EaseInOutBounce.ordinal()] = 28;
            } catch (NoSuchFieldError e28) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Animation$EaseEnum[EaseEnum.EaseInElastic.ordinal()] = 29;
            } catch (NoSuchFieldError e29) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Animation$EaseEnum[EaseEnum.EaseOutElastic.ordinal()] = 30;
            } catch (NoSuchFieldError e30) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Animation$EaseEnum[EaseEnum.EaseInOutElastic.ordinal()] = 31;
            } catch (NoSuchFieldError e31) {
            }
        }
    }

    public static Ease getInstance(EaseEnum easeEnum) {
        if (eases == null) {
            eases = new ArrayList(EaseEnum.values().length);
            for (int length = EaseEnum.values().length; length > 0; length--) {
                eases.add(null);
            }
        }
        Ease ease = (Ease) eases.get(easeEnum.getValue());
        if (ease != null) {
            return ease;
        }
        ease = new Ease(easeEnum);
        eases.set(easeEnum.getValue(), ease);
        return ease;
    }

    private Ease(EaseEnum easeEnum) {
        this.f12a = new PointF();
        this.f13b = new PointF();
        this.f14c = new PointF();
        this.ableToDefineWithControlPoints = Boolean.valueOf(true);
        switch (C07381.$SwitchMap$com$nightonke$boommenu$Animation$EaseEnum[easeEnum.ordinal()]) {
            case OnSubscribeConcatMap.BOUNDARY /*1*/:
                init(0.6d, -0.2d, 0.735d, 0.045d);
                break;
            case OnSubscribeConcatMap.END /*2*/:
                init(0.6d, 0.04d, 0.98d, 0.335d);
                break;
            case ConnectionResult.SERVICE_DISABLED /*3*/:
                init(0.55d, 0.055d, 0.675d, 0.19d);
                break;
            case ConnectionResult.SIGN_IN_REQUIRED /*4*/:
                init(0.95d, 0.05d, 0.795d, 0.035d);
                break;
            case ConnectionResult.INVALID_ACCOUNT /*5*/:
                init(0.47d, 0.0d, 0.745d, 0.715d);
                break;
            case ConnectionResult.RESOLUTION_REQUIRED /*6*/:
                init(0.55d, 0.085d, 0.68d, 0.53d);
                break;
            case ConnectionResult.NETWORK_ERROR /*7*/:
                init(0.755d, 0.05d, 0.855d, 0.06d);
                break;
            case ConnectionResult.INTERNAL_ERROR /*8*/:
                init(0.895d, 0.03d, 0.685d, 0.22d);
                break;
            case ConnectionResult.SERVICE_INVALID /*9*/:
                init(0.174d, 0.885d, 0.32d, 1.275d);
                break;
            case ConnectionResult.DEVELOPER_ERROR /*10*/:
                init(0.075d, 0.82d, 0.165d, 1.0d);
                break;
            case ConnectionResult.LICENSE_CHECK_FAILED /*11*/:
                init(0.215d, 0.61d, 0.355d, 1.0d);
                break;
            case C0801R.styleable.Toolbar_titleTextAppearance /*12*/:
                init(0.19d, 1.0d, 0.22d, 1.0d);
                break;
            case ConnectionResult.CANCELED /*13*/:
                init(0.39d, 0.575d, 0.565d, 1.0d);
                break;
            case ConnectionResult.TIMEOUT /*14*/:
                init(0.25d, 0.46d, 0.45d, 0.94d);
                break;
            case ConnectionResult.INTERRUPTED /*15*/:
                init(0.23d, 1.0d, 0.32d, 1.0d);
                break;
            case ConnectionResult.API_UNAVAILABLE /*16*/:
                init(0.165d, 0.84d, 0.44d, 1.0d);
                break;
            case ConnectionResult.SIGN_IN_FAILED /*17*/:
                init(0.68d, -0.55d, 0.265d, 1.55d);
                break;
            case ConnectionResult.SERVICE_UPDATING /*18*/:
                init(0.785d, 0.135d, 0.15d, 0.86d);
                break;
            case ConnectionResult.SERVICE_MISSING_PERMISSION /*19*/:
                init(0.645d, 0.045d, 0.335d, 1.0d);
                break;
            case ConnectionResult.RESTRICTED_PROFILE /*20*/:
                init(1.0f, 0.0f, 0.0f, 1.0f);
                break;
            case C0801R.styleable.AppCompatTheme_actionBarWidgetTheme /*21*/:
                init(0.445d, 0.05d, 0.55d, 0.95d);
                break;
            case C0801R.styleable.Toolbar_collapseIcon /*22*/:
                init(0.455d, 0.03d, 0.515d, 0.955d);
                break;
            case C0801R.styleable.Toolbar_collapseContentDescription /*23*/:
                init(0.86d, 0.0d, 0.07d, 1.0d);
                break;
            case C0801R.styleable.Toolbar_navigationIcon /*24*/:
                init(0.77d, 0.0d, 0.175d, 1.0d);
                break;
            case MessagingStyle.MAXIMUM_RETAINED_MESSAGES /*25*/:
                init(0.0f, 0.0f, 1.0f, 1.0f);
                break;
            case C0801R.styleable.Toolbar_logoDescription /*26*/:
            case C0801R.styleable.Toolbar_titleTextColor /*27*/:
            case C0801R.styleable.Toolbar_subtitleTextColor /*28*/:
            case C0801R.styleable.AppCompatTheme_actionModeBackground /*29*/:
            case C0801R.styleable.AppCompatTheme_actionModeSplitBackground /*30*/:
            case C0801R.styleable.AppCompatTheme_actionModeCloseDrawable /*31*/:
                this.ableToDefineWithControlPoints = Boolean.valueOf(false);
                break;
            default:
                throw new RuntimeException("Ease-enum not found!");
        }
        this.easeEnum = easeEnum;
    }

    public float getInterpolation(float offset) {
        if (this.ableToDefineWithControlPoints.booleanValue()) {
            return getBezierCoordinateY(getXForTime(offset));
        }
        switch (C07381.$SwitchMap$com$nightonke$boommenu$Animation$EaseEnum[this.easeEnum.ordinal()]) {
            case C0801R.styleable.Toolbar_logoDescription /*26*/:
                return getEaseInBounceOffset(offset);
            case C0801R.styleable.Toolbar_titleTextColor /*27*/:
                return getEaseOutBounceOffset(offset);
            case C0801R.styleable.Toolbar_subtitleTextColor /*28*/:
                return getEaseInOutBounceOffset(offset);
            case C0801R.styleable.AppCompatTheme_actionModeBackground /*29*/:
                return getEaseInElasticOffset(offset);
            case C0801R.styleable.AppCompatTheme_actionModeSplitBackground /*30*/:
                return getEaseOutElasticOffset(offset);
            case C0801R.styleable.AppCompatTheme_actionModeCloseDrawable /*31*/:
                return getEaseInOutElasticOffset(offset);
            default:
                throw new RuntimeException("Wrong ease-enum initialize method.");
        }
    }

    private void init(float startX, float startY, float endX, float endY) {
        PointF start = new PointF(startX, startY);
        PointF end = new PointF(endX, endY);
        this.f14c.x = start.x * 3.0f;
        this.f13b.x = ((end.x - start.x) * 3.0f) - this.f14c.x;
        this.f12a.x = (1.0f - this.f14c.x) - this.f13b.x;
        this.f14c.y = start.y * 3.0f;
        this.f13b.y = ((end.y - start.y) * 3.0f) - this.f14c.y;
        this.f12a.y = (1.0f - this.f14c.y) - this.f13b.y;
    }

    private void init(double startX, double startY, double endX, double endY) {
        init((float) startX, (float) startY, (float) endX, (float) endY);
    }

    private float getXForTime(float time) {
        float x = time;
        for (int i = 1; i < 14; i++) {
            float z = getBezierCoordinateX(x) - time;
            if (((double) Math.abs(z)) < 0.001d) {
                break;
            }
            x -= z / getX(x);
        }
        return x;
    }

    private float getBezierCoordinateY(float time) {
        return (this.f14c.y + ((this.f13b.y + (this.f12a.y * time)) * time)) * time;
    }

    private float getBezierCoordinateX(float time) {
        return (this.f14c.x + ((this.f13b.x + (this.f12a.x * time)) * time)) * time;
    }

    private float getX(float t) {
        return this.f14c.x + (((2.0f * this.f13b.x) + ((3.0f * this.f12a.x) * t)) * t);
    }

    private float getEaseInBounceOffset(float offset) {
        return (1.0f - getEaseBounceOffset2(1.0f - offset, 0.0f, 1.0f, 1.0f)) + 0.0f;
    }

    private float getEaseOutBounceOffset(float offset) {
        offset /= 1.0f;
        if (((double) offset) < 0.36363636363636365d) {
            return (((7.5625f * offset) * offset) * 1.0f) + 0.0f;
        }
        if (((double) offset) < 0.7272727272727273d) {
            offset = (float) (((double) offset) - 0.5454545454545454d);
            return ((((7.5625f * offset) * offset) + 0.75f) * 1.0f) + 0.0f;
        } else if (((double) offset) < 0.9090909090909091d) {
            offset = (float) (((double) offset) - 0.8181818181818182d);
            return ((((7.5625f * offset) * offset) + 0.9375f) * 1.0f) + 0.0f;
        } else {
            offset = (float) (((double) offset) - 0.9545454545454546d);
            return ((((7.5625f * offset) * offset) + 0.984375f) * 1.0f) + 0.0f;
        }
    }

    private float getEaseInOutBounceOffset(float offset) {
        if (offset < 1.0f / 2.0f) {
            return (getEaseBounceOffset1(offset * 2.0f, 0.0f, 1.0f, 1.0f) * 0.5f) + 0.0f;
        }
        return ((getEaseBounceOffset2(offset * 2.0f, 0.0f, 1.0f, 1.0f) * 0.5f) + (0.5f * 1.0f)) + 0.0f;
    }

    private float getEaseBounceOffset1(float t, float b, float c, float d) {
        return (c - getEaseBounceOffset2(d - t, 0.0f, c, d)) + b;
    }

    private float getEaseBounceOffset2(float t, float b, float c, float d) {
        t /= d;
        if (((double) t) < 0.36363636363636365d) {
            return (((7.5625f * t) * t) * c) + b;
        }
        if (((double) t) < 0.7272727272727273d) {
            t = (float) (((double) t) - 0.5454545454545454d);
            return ((((7.5625f * t) * t) + 0.75f) * c) + b;
        } else if (((double) t) < 0.7272727272727273d) {
            t = (float) (((double) t) - 0.5454545454545454d);
            return ((((7.5625f * t) * t) + 0.9375f) * c) + b;
        } else {
            t = (float) (((double) t) - 0.9545454545454546d);
            return ((((7.5625f * t) * t) + 0.984375f) * c) + b;
        }
    }

    private float getEaseInElasticOffset(float offset) {
        if (offset == 0.0f) {
            return 0.0f;
        }
        offset /= 1.0f;
        if (offset == 1.0f) {
            return 0.0f + 1.0f;
        }
        float p = 1.0f * 0.3f;
        offset -= 1.0f;
        return 0.0f + (-((((float) Math.pow(2.0d, (double) (10.0f * offset))) * 1.0f) * ((float) Math.sin((double) ((((offset * 1.0f) - (p / 4.0f)) * 6.2831855f) / p)))));
    }

    private float getEaseOutElasticOffset(float offset) {
        if (offset == 0.0f) {
            return 0.0f;
        }
        offset /= 1.0f;
        if (offset == 1.0f) {
            return 0.0f + 1.0f;
        }
        float p = 1.0f * 0.3f;
        return 0.0f + (((((float) Math.pow(2.0d, (double) (-10.0f * offset))) * 1.0f) * ((float) Math.sin((double) ((((offset * 1.0f) - (p / 4.0f)) * 6.2831855f) / p)))) + 1.0f);
    }

    private float getEaseInOutElasticOffset(float offset) {
        if (offset == 0.0f) {
            return 0.0f;
        }
        offset /= 1.0f / 2.0f;
        if (offset == 2.0f) {
            return 0.0f + 1.0f;
        }
        float p = 1.0f * 0.45f;
        float s = p / 4.0f;
        if (offset < 1.0f) {
            offset -= 1.0f;
            return 0.0f + (-0.5f * ((((float) Math.pow(2.0d, (double) (10.0f * offset))) * 1.0f) * ((float) Math.sin((double) ((((offset * 1.0f) - s) * 6.2831855f) / p)))));
        }
        offset -= 1.0f;
        return 0.0f + ((((((float) Math.pow(2.0d, (double) (-10.0f * offset))) * 1.0f) * ((float) Math.sin((double) ((((offset * 1.0f) - s) * 6.2831855f) / p)))) * 0.5f) + 1.0f);
    }
}
