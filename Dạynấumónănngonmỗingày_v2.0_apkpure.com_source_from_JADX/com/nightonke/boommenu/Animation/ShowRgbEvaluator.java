package com.nightonke.boommenu.Animation;

import android.animation.TypeEvaluator;
import android.support.v4.view.MotionEventCompat;

public class ShowRgbEvaluator implements TypeEvaluator {
    private static final ShowRgbEvaluator sInstance;

    static {
        sInstance = new ShowRgbEvaluator();
    }

    public static ShowRgbEvaluator getInstance() {
        return sInstance;
    }

    public Object evaluate(float fraction, Object startValue, Object endValue) {
        int startInt = ((Integer) startValue).intValue();
        int startA = (startInt >> 24) & MotionEventCompat.ACTION_MASK;
        int startR = (startInt >> 16) & MotionEventCompat.ACTION_MASK;
        int startG = (startInt >> 8) & MotionEventCompat.ACTION_MASK;
        int startB = startInt & MotionEventCompat.ACTION_MASK;
        int endInt = ((Integer) endValue).intValue();
        int endA = (endInt >> 24) & MotionEventCompat.ACTION_MASK;
        int endR = (endInt >> 16) & MotionEventCompat.ACTION_MASK;
        int endG = (endInt >> 8) & MotionEventCompat.ACTION_MASK;
        int endB = endInt & MotionEventCompat.ACTION_MASK;
        float trueFraction = speedMap(fraction);
        return Integer.valueOf(((((((int) (((float) (endA - startA)) * trueFraction)) + startA) << 24) | ((((int) (((float) (endR - startR)) * trueFraction)) + startR) << 16)) | ((((int) (((float) (endG - startG)) * trueFraction)) + startG) << 8)) | (((int) (((float) (endB - startB)) * trueFraction)) + startB));
    }

    private float speedMap(float fraction) {
        float trueSpeed = fraction * 2.0f;
        if (trueSpeed > 1.0f) {
            trueSpeed = 1.0f;
        }
        if (trueSpeed < 0.0f) {
            return 0.0f;
        }
        return trueSpeed;
    }
}
