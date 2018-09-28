package com.nightonke.boommenu.Animation;

import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.TypeEvaluator;
import android.graphics.Point;
import android.view.View;
import com.google.android.gms.common.ConnectionResult;
import com.nightonke.boommenu.BoomButtons.BoomButton;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import rx.internal.operators.OnSubscribeConcatMap;

public class AnimationManager {
    private static AnimationManager ourInstance;

    /* renamed from: com.nightonke.boommenu.Animation.AnimationManager.1 */
    static /* synthetic */ class C07371 {
        static final /* synthetic */ int[] $SwitchMap$com$nightonke$boommenu$Animation$BoomEnum;
        static final /* synthetic */ int[] $SwitchMap$com$nightonke$boommenu$Animation$OrderEnum;

        static {
            $SwitchMap$com$nightonke$boommenu$Animation$BoomEnum = new int[BoomEnum.values().length];
            try {
                $SwitchMap$com$nightonke$boommenu$Animation$BoomEnum[BoomEnum.LINE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Animation$BoomEnum[BoomEnum.PARABOLA_1.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Animation$BoomEnum[BoomEnum.PARABOLA_2.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Animation$BoomEnum[BoomEnum.PARABOLA_3.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Animation$BoomEnum[BoomEnum.PARABOLA_4.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Animation$BoomEnum[BoomEnum.HORIZONTAL_THROW_1.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Animation$BoomEnum[BoomEnum.HORIZONTAL_THROW_2.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Animation$BoomEnum[BoomEnum.RANDOM.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Animation$BoomEnum[BoomEnum.Unknown.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            $SwitchMap$com$nightonke$boommenu$Animation$OrderEnum = new int[OrderEnum.values().length];
            try {
                $SwitchMap$com$nightonke$boommenu$Animation$OrderEnum[OrderEnum.DEFAULT.ordinal()] = 1;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Animation$OrderEnum[OrderEnum.REVERSE.ordinal()] = 2;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Animation$OrderEnum[OrderEnum.RANDOM.ordinal()] = 3;
            } catch (NoSuchFieldError e12) {
            }
        }
    }

    public static ObjectAnimator animate(Object target, String property, long delay, long duration, TimeInterpolator interpolator, AnimatorListenerAdapter listenerAdapter, float... values) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(target, property, values);
        animator.setStartDelay(delay);
        animator.setDuration(duration);
        if (interpolator != null) {
            animator.setInterpolator(interpolator);
        }
        if (listenerAdapter != null) {
            animator.addListener(listenerAdapter);
        }
        animator.start();
        return animator;
    }

    public static ObjectAnimator animate(Object target, String property, long delay, long duration, TimeInterpolator interpolator, float... values) {
        return animate(target, property, delay, duration, interpolator, null, values);
    }

    public static void animate(String property, long delay, long duration, float[] values, TimeInterpolator interpolator, ArrayList<View> targets) {
        Iterator it = targets.iterator();
        while (it.hasNext()) {
            animate(it.next(), property, delay, duration, interpolator, null, values);
        }
    }

    public static void rotate(BoomButton boomButton, long delay, long duration, TimeInterpolator interpolator, float... degrees) {
        boomButton.setRotateAnchorPoints();
        for (int i = 0; i < boomButton.rotateViews().size(); i++) {
            animate((View) boomButton.rotateViews().get(i), "rotation", delay, duration, interpolator, null, degrees);
        }
    }

    public static ObjectAnimator animate(Object target, String property, long delay, long duration, TypeEvaluator evaluator, int... values) {
        return animate(target, property, delay, duration, evaluator, null, values);
    }

    public static ObjectAnimator animate(Object target, String property, long delay, long duration, TypeEvaluator evaluator, AnimatorListenerAdapter listenerAdapter, int... values) {
        ObjectAnimator animator = ObjectAnimator.ofInt(target, property, values);
        animator.setStartDelay(delay);
        animator.setDuration(duration);
        animator.setEvaluator(evaluator);
        if (listenerAdapter != null) {
            animator.addListener(listenerAdapter);
        }
        animator.start();
        return animator;
    }

    public static ArrayList<Integer> getOrderIndex(OrderEnum orderEnum, int size) {
        ArrayList<Integer> indexes = new ArrayList();
        int i;
        switch (C07371.$SwitchMap$com$nightonke$boommenu$Animation$OrderEnum[orderEnum.ordinal()]) {
            case OnSubscribeConcatMap.BOUNDARY /*1*/:
                for (i = 0; i < size; i++) {
                    indexes.add(Integer.valueOf(i));
                }
                break;
            case OnSubscribeConcatMap.END /*2*/:
                for (i = 0; i < size; i++) {
                    indexes.add(Integer.valueOf((size - i) - 1));
                }
                break;
            case ConnectionResult.SERVICE_DISABLED /*3*/:
                boolean[] used = new boolean[size];
                for (i = 0; i < used.length; i++) {
                    used[i] = false;
                }
                int count = 0;
                Random random = new Random();
                while (count < size) {
                    int r = random.nextInt(size);
                    if (!used[r]) {
                        used[r] = true;
                        indexes.add(Integer.valueOf(r));
                        count++;
                    }
                }
                break;
        }
        return indexes;
    }

    public static void calculateShowXY(BoomEnum boomEnum, Point parentSize, int frames, Point startPosition, Point endPosition, float[] xs, float[] ys) {
        if (startPosition.x == endPosition.x) {
            boomEnum = BoomEnum.LINE;
        }
        float x1 = (float) startPosition.x;
        float y1 = (float) startPosition.y;
        float x2 = (float) endPosition.x;
        float y2 = (float) endPosition.y;
        float p = 1.0f / ((float) frames);
        float xOffset = x2 - x1;
        float yOffset = y2 - y1;
        int i;
        float x3;
        float a;
        float b;
        float c;
        float y3;
        switch (C07371.$SwitchMap$com$nightonke$boommenu$Animation$BoomEnum[boomEnum.ordinal()]) {
            case OnSubscribeConcatMap.BOUNDARY /*1*/:
                for (i = 0; i < xs.length; i++) {
                    float offset = ((float) i) * p;
                    xs[i] = (offset * xOffset) + x1;
                    ys[i] = (offset * yOffset) + y1;
                }
            case OnSubscribeConcatMap.END /*2*/:
                x3 = (x1 + x2) / 2.0f;
                a = ((((x2 - x3) * y1) + ((x3 - x1) * y2)) + ((x1 - x2) * ((Math.min(y1, y2) * 3.0f) / 4.0f))) / ((((x1 * x1) * (x2 - x3)) + ((x2 * x2) * (x3 - x1))) + ((x3 * x3) * (x1 - x2)));
                b = ((y1 - y2) / (x1 - x2)) - ((x1 + x2) * a);
                c = (y1 - ((x1 * x1) * a)) - (x1 * b);
                for (i = 0; i < xs.length; i++) {
                    xs[i] = ((((float) i) * p) * xOffset) + x1;
                    ys[i] = (((xs[i] * a) * xs[i]) + (xs[i] * b)) + c;
                }
            case ConnectionResult.SERVICE_DISABLED /*3*/:
                x3 = (x1 + x2) / 2.0f;
                a = ((((x2 - x3) * y1) + ((x3 - x1) * y2)) + ((x1 - x2) * ((((float) parentSize.y) + Math.max(y1, y2)) / 2.0f))) / ((((x1 * x1) * (x2 - x3)) + ((x2 * x2) * (x3 - x1))) + ((x3 * x3) * (x1 - x2)));
                b = ((y1 - y2) / (x1 - x2)) - ((x1 + x2) * a);
                c = (y1 - ((x1 * x1) * a)) - (x1 * b);
                for (i = 0; i < xs.length; i++) {
                    xs[i] = ((((float) i) * p) * xOffset) + x1;
                    ys[i] = (((xs[i] * a) * xs[i]) + (xs[i] * b)) + c;
                }
            case ConnectionResult.SIGN_IN_REQUIRED /*4*/:
                y3 = (y1 + y2) / 2.0f;
                a = ((((y2 - y3) * x1) + ((y3 - y1) * x2)) + ((y1 - y2) * (Math.min(x1, x2) / 2.0f))) / ((((y1 * y1) * (y2 - y3)) + ((y2 * y2) * (y3 - y1))) + ((y3 * y3) * (y1 - y2)));
                b = ((x1 - x2) / (y1 - y2)) - ((y1 + y2) * a);
                c = (x1 - ((y1 * y1) * a)) - (y1 * b);
                for (i = 0; i < xs.length; i++) {
                    ys[i] = ((((float) i) * p) * yOffset) + y1;
                    xs[i] = (((ys[i] * a) * ys[i]) + (ys[i] * b)) + c;
                }
            case ConnectionResult.INVALID_ACCOUNT /*5*/:
                y3 = (y1 + y2) / 2.0f;
                a = ((((y2 - y3) * x1) + ((y3 - y1) * x2)) + ((y1 - y2) * ((((float) parentSize.x) + Math.max(x1, x2)) / 2.0f))) / ((((y1 * y1) * (y2 - y3)) + ((y2 * y2) * (y3 - y1))) + ((y3 * y3) * (y1 - y2)));
                b = ((x1 - x2) / (y1 - y2)) - ((y1 + y2) * a);
                c = (x1 - ((y1 * y1) * a)) - (y1 * b);
                for (i = 0; i < xs.length; i++) {
                    ys[i] = ((((float) i) * p) * yOffset) + y1;
                    xs[i] = (((ys[i] * a) * ys[i]) + (ys[i] * b)) + c;
                }
            case ConnectionResult.RESOLUTION_REQUIRED /*6*/:
                x3 = (2.0f * x2) - x1;
                y3 = y1;
                a = ((((x3 - x2) * y1) + ((x2 - x1) * y3)) + ((x1 - x3) * y2)) / ((((x1 * x1) * (x3 - x2)) + ((x3 * x3) * (x2 - x1))) + ((x2 * x2) * (x1 - x3)));
                b = ((y1 - y3) / (x1 - x3)) - ((x1 + x3) * a);
                c = (y1 - ((x1 * x1) * a)) - (x1 * b);
                for (i = 0; i < xs.length; i++) {
                    xs[i] = ((((float) i) * p) * xOffset) + x1;
                    ys[i] = (((xs[i] * a) * xs[i]) + (xs[i] * b)) + c;
                }
            case ConnectionResult.NETWORK_ERROR /*7*/:
                x2 = (float) startPosition.x;
                x1 = (float) endPosition.x;
                y1 = (float) endPosition.y;
                x3 = (2.0f * x2) - x1;
                y3 = y1;
                a = ((((x3 - x2) * y1) + ((x2 - x1) * y3)) + ((x1 - x3) * ((float) startPosition.y))) / ((((x1 * x1) * (x3 - x2)) + ((x3 * x3) * (x2 - x1))) + ((x2 * x2) * (x1 - x3)));
                b = ((y1 - y3) / (x1 - x3)) - ((x1 + x3) * a);
                c = (y1 - ((x1 * x1) * a)) - (x1 * b);
                for (i = 0; i < xs.length; i++) {
                    xs[i] = ((((float) i) * p) * xOffset) + x2;
                    ys[i] = (((xs[i] * a) * xs[i]) + (xs[i] * b)) + c;
                }
            case ConnectionResult.INTERNAL_ERROR /*8*/:
                calculateShowXY(BoomEnum.values()[new Random().nextInt(BoomEnum.RANDOM.getValue())], parentSize, frames, startPosition, endPosition, xs, ys);
            case ConnectionResult.SERVICE_INVALID /*9*/:
                throw new RuntimeException("Unknown boom-enum!");
            default:
        }
    }

    public static void calculateHideXY(BoomEnum boomEnum, Point parentSize, int frames, Point startPosition, Point endPosition, float[] xs, float[] ys) {
        int i = startPosition.x;
        int i2 = endPosition.x;
        if (i == r0) {
            boomEnum = BoomEnum.LINE;
        }
        float x1 = (float) startPosition.x;
        float y1 = (float) startPosition.y;
        float x2 = (float) endPosition.x;
        float y2 = (float) endPosition.y;
        float p = 1.0f / ((float) frames);
        float xOffset = x2 - x1;
        float yOffset = y2 - y1;
        float x3;
        float y3;
        float a;
        float b;
        float c;
        int i3;
        switch (C07371.$SwitchMap$com$nightonke$boommenu$Animation$BoomEnum[boomEnum.ordinal()]) {
            case OnSubscribeConcatMap.BOUNDARY /*1*/:
            case OnSubscribeConcatMap.END /*2*/:
            case ConnectionResult.SERVICE_DISABLED /*3*/:
            case ConnectionResult.SIGN_IN_REQUIRED /*4*/:
            case ConnectionResult.INVALID_ACCOUNT /*5*/:
            case ConnectionResult.INTERNAL_ERROR /*8*/:
            case ConnectionResult.SERVICE_INVALID /*9*/:
                calculateShowXY(boomEnum, parentSize, frames, startPosition, endPosition, xs, ys);
            case ConnectionResult.RESOLUTION_REQUIRED /*6*/:
                x2 = (float) startPosition.x;
                x1 = (float) endPosition.x;
                y1 = (float) endPosition.y;
                x3 = (2.0f * x2) - x1;
                y3 = y1;
                a = ((((x3 - x2) * y1) + ((x2 - x1) * y3)) + ((x1 - x3) * ((float) startPosition.y))) / ((((x1 * x1) * (x3 - x2)) + ((x3 * x3) * (x2 - x1))) + ((x2 * x2) * (x1 - x3)));
                b = ((y1 - y3) / (x1 - x3)) - ((x1 + x3) * a);
                c = (y1 - ((x1 * x1) * a)) - (x1 * b);
                for (i3 = 0; i3 < xs.length; i3++) {
                    xs[i3] = ((((float) i3) * p) * xOffset) + x2;
                    ys[i3] = (((xs[i3] * a) * xs[i3]) + (xs[i3] * b)) + c;
                }
            case ConnectionResult.NETWORK_ERROR /*7*/:
                x3 = (2.0f * x2) - x1;
                y3 = y1;
                a = ((((x3 - x2) * y1) + ((x2 - x1) * y3)) + ((x1 - x3) * y2)) / ((((x1 * x1) * (x3 - x2)) + ((x3 * x3) * (x2 - x1))) + ((x2 * x2) * (x1 - x3)));
                b = ((y1 - y3) / (x1 - x3)) - ((x1 + x3) * a);
                c = (y1 - ((x1 * x1) * a)) - (x1 * b);
                for (i3 = 0; i3 < xs.length; i3++) {
                    xs[i3] = ((((float) i3) * p) * xOffset) + x1;
                    ys[i3] = (((xs[i3] * a) * xs[i3]) + (xs[i3] * b)) + c;
                }
            default:
        }
    }

    static {
        ourInstance = new AnimationManager();
    }

    public static AnimationManager getInstance() {
        return ourInstance;
    }

    private AnimationManager() {
    }
}
