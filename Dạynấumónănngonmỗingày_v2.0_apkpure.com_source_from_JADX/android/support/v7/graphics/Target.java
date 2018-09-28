package android.support.v7.graphics;

import android.support.annotation.FloatRange;

public final class Target {
    public static final Target DARK_MUTED;
    public static final Target DARK_VIBRANT;
    private static final int INDEX_MAX = 2;
    private static final int INDEX_MIN = 0;
    private static final int INDEX_TARGET = 1;
    private static final int INDEX_WEIGHT_LUMA = 1;
    private static final int INDEX_WEIGHT_POP = 2;
    private static final int INDEX_WEIGHT_SAT = 0;
    public static final Target LIGHT_MUTED;
    public static final Target LIGHT_VIBRANT;
    private static final float MAX_DARK_LUMA = 0.45f;
    private static final float MAX_MUTED_SATURATION = 0.4f;
    private static final float MAX_NORMAL_LUMA = 0.7f;
    private static final float MIN_LIGHT_LUMA = 0.55f;
    private static final float MIN_NORMAL_LUMA = 0.3f;
    private static final float MIN_VIBRANT_SATURATION = 0.35f;
    public static final Target MUTED;
    private static final float TARGET_DARK_LUMA = 0.26f;
    private static final float TARGET_LIGHT_LUMA = 0.74f;
    private static final float TARGET_MUTED_SATURATION = 0.3f;
    private static final float TARGET_NORMAL_LUMA = 0.5f;
    private static final float TARGET_VIBRANT_SATURATION = 1.0f;
    public static final Target VIBRANT;
    private static final float WEIGHT_LUMA = 0.52f;
    private static final float WEIGHT_POPULATION = 0.24f;
    private static final float WEIGHT_SATURATION = 0.24f;
    private boolean mIsExclusive;
    private final float[] mLightnessTargets;
    private final float[] mSaturationTargets;
    private final float[] mWeights;

    public static final class Builder {
        private final Target mTarget;

        public Builder() {
            this.mTarget = new Target();
        }

        public Builder(Target target) {
            this.mTarget = new Target(null);
        }

        public Builder setMinimumSaturation(@FloatRange(from = 0.0d, to = 1.0d) float value) {
            this.mTarget.mSaturationTargets[Target.INDEX_WEIGHT_SAT] = value;
            return this;
        }

        public Builder setTargetSaturation(@FloatRange(from = 0.0d, to = 1.0d) float value) {
            this.mTarget.mSaturationTargets[Target.INDEX_WEIGHT_LUMA] = value;
            return this;
        }

        public Builder setMaximumSaturation(@FloatRange(from = 0.0d, to = 1.0d) float value) {
            this.mTarget.mSaturationTargets[Target.INDEX_WEIGHT_POP] = value;
            return this;
        }

        public Builder setMinimumLightness(@FloatRange(from = 0.0d, to = 1.0d) float value) {
            this.mTarget.mLightnessTargets[Target.INDEX_WEIGHT_SAT] = value;
            return this;
        }

        public Builder setTargetLightness(@FloatRange(from = 0.0d, to = 1.0d) float value) {
            this.mTarget.mLightnessTargets[Target.INDEX_WEIGHT_LUMA] = value;
            return this;
        }

        public Builder setMaximumLightness(@FloatRange(from = 0.0d, to = 1.0d) float value) {
            this.mTarget.mLightnessTargets[Target.INDEX_WEIGHT_POP] = value;
            return this;
        }

        public Builder setSaturationWeight(@FloatRange(from = 0.0d) float weight) {
            this.mTarget.mWeights[Target.INDEX_WEIGHT_SAT] = weight;
            return this;
        }

        public Builder setLightnessWeight(@FloatRange(from = 0.0d) float weight) {
            this.mTarget.mWeights[Target.INDEX_WEIGHT_LUMA] = weight;
            return this;
        }

        public Builder setPopulationWeight(@FloatRange(from = 0.0d) float weight) {
            this.mTarget.mWeights[Target.INDEX_WEIGHT_POP] = weight;
            return this;
        }

        public Builder setExclusive(boolean exclusive) {
            this.mTarget.mIsExclusive = exclusive;
            return this;
        }

        public Target build() {
            return this.mTarget;
        }
    }

    static {
        LIGHT_VIBRANT = new Target();
        setDefaultLightLightnessValues(LIGHT_VIBRANT);
        setDefaultVibrantSaturationValues(LIGHT_VIBRANT);
        VIBRANT = new Target();
        setDefaultNormalLightnessValues(VIBRANT);
        setDefaultVibrantSaturationValues(VIBRANT);
        DARK_VIBRANT = new Target();
        setDefaultDarkLightnessValues(DARK_VIBRANT);
        setDefaultVibrantSaturationValues(DARK_VIBRANT);
        LIGHT_MUTED = new Target();
        setDefaultLightLightnessValues(LIGHT_MUTED);
        setDefaultMutedSaturationValues(LIGHT_MUTED);
        MUTED = new Target();
        setDefaultNormalLightnessValues(MUTED);
        setDefaultMutedSaturationValues(MUTED);
        DARK_MUTED = new Target();
        setDefaultDarkLightnessValues(DARK_MUTED);
        setDefaultMutedSaturationValues(DARK_MUTED);
    }

    private Target() {
        this.mSaturationTargets = new float[3];
        this.mLightnessTargets = new float[3];
        this.mWeights = new float[3];
        this.mIsExclusive = true;
        setTargetDefaultValues(this.mSaturationTargets);
        setTargetDefaultValues(this.mLightnessTargets);
        setDefaultWeights();
    }

    private Target(Target from) {
        this.mSaturationTargets = new float[3];
        this.mLightnessTargets = new float[3];
        this.mWeights = new float[3];
        this.mIsExclusive = true;
        System.arraycopy(from.mSaturationTargets, INDEX_WEIGHT_SAT, this.mSaturationTargets, INDEX_WEIGHT_SAT, this.mSaturationTargets.length);
        System.arraycopy(from.mLightnessTargets, INDEX_WEIGHT_SAT, this.mLightnessTargets, INDEX_WEIGHT_SAT, this.mLightnessTargets.length);
        System.arraycopy(from.mWeights, INDEX_WEIGHT_SAT, this.mWeights, INDEX_WEIGHT_SAT, this.mWeights.length);
    }

    @FloatRange(from = 0.0d, to = 1.0d)
    public float getMinimumSaturation() {
        return this.mSaturationTargets[INDEX_WEIGHT_SAT];
    }

    @FloatRange(from = 0.0d, to = 1.0d)
    public float getTargetSaturation() {
        return this.mSaturationTargets[INDEX_WEIGHT_LUMA];
    }

    @FloatRange(from = 0.0d, to = 1.0d)
    public float getMaximumSaturation() {
        return this.mSaturationTargets[INDEX_WEIGHT_POP];
    }

    @FloatRange(from = 0.0d, to = 1.0d)
    public float getMinimumLightness() {
        return this.mLightnessTargets[INDEX_WEIGHT_SAT];
    }

    @FloatRange(from = 0.0d, to = 1.0d)
    public float getTargetLightness() {
        return this.mLightnessTargets[INDEX_WEIGHT_LUMA];
    }

    @FloatRange(from = 0.0d, to = 1.0d)
    public float getMaximumLightness() {
        return this.mLightnessTargets[INDEX_WEIGHT_POP];
    }

    public float getSaturationWeight() {
        return this.mWeights[INDEX_WEIGHT_SAT];
    }

    public float getLightnessWeight() {
        return this.mWeights[INDEX_WEIGHT_LUMA];
    }

    public float getPopulationWeight() {
        return this.mWeights[INDEX_WEIGHT_POP];
    }

    public boolean isExclusive() {
        return this.mIsExclusive;
    }

    private static void setTargetDefaultValues(float[] values) {
        values[INDEX_WEIGHT_SAT] = 0.0f;
        values[INDEX_WEIGHT_LUMA] = TARGET_NORMAL_LUMA;
        values[INDEX_WEIGHT_POP] = TARGET_VIBRANT_SATURATION;
    }

    private void setDefaultWeights() {
        this.mWeights[INDEX_WEIGHT_SAT] = WEIGHT_SATURATION;
        this.mWeights[INDEX_WEIGHT_LUMA] = WEIGHT_LUMA;
        this.mWeights[INDEX_WEIGHT_POP] = WEIGHT_SATURATION;
    }

    void normalizeWeights() {
        int i;
        float sum = 0.0f;
        int z = this.mWeights.length;
        for (i = INDEX_WEIGHT_SAT; i < z; i += INDEX_WEIGHT_LUMA) {
            float weight = this.mWeights[i];
            if (weight > 0.0f) {
                sum += weight;
            }
        }
        if (sum != 0.0f) {
            z = this.mWeights.length;
            for (i = INDEX_WEIGHT_SAT; i < z; i += INDEX_WEIGHT_LUMA) {
                if (this.mWeights[i] > 0.0f) {
                    float[] fArr = this.mWeights;
                    fArr[i] = fArr[i] / sum;
                }
            }
        }
    }

    private static void setDefaultDarkLightnessValues(Target target) {
        target.mLightnessTargets[INDEX_WEIGHT_LUMA] = TARGET_DARK_LUMA;
        target.mLightnessTargets[INDEX_WEIGHT_POP] = MAX_DARK_LUMA;
    }

    private static void setDefaultNormalLightnessValues(Target target) {
        target.mLightnessTargets[INDEX_WEIGHT_SAT] = TARGET_MUTED_SATURATION;
        target.mLightnessTargets[INDEX_WEIGHT_LUMA] = TARGET_NORMAL_LUMA;
        target.mLightnessTargets[INDEX_WEIGHT_POP] = MAX_NORMAL_LUMA;
    }

    private static void setDefaultLightLightnessValues(Target target) {
        target.mLightnessTargets[INDEX_WEIGHT_SAT] = MIN_LIGHT_LUMA;
        target.mLightnessTargets[INDEX_WEIGHT_LUMA] = TARGET_LIGHT_LUMA;
    }

    private static void setDefaultVibrantSaturationValues(Target target) {
        target.mSaturationTargets[INDEX_WEIGHT_SAT] = MIN_VIBRANT_SATURATION;
        target.mSaturationTargets[INDEX_WEIGHT_LUMA] = TARGET_VIBRANT_SATURATION;
    }

    private static void setDefaultMutedSaturationValues(Target target) {
        target.mSaturationTargets[INDEX_WEIGHT_LUMA] = TARGET_MUTED_SATURATION;
        target.mSaturationTargets[INDEX_WEIGHT_POP] = MAX_MUTED_SATURATION;
    }
}
