package android.support.design.widget;

import android.support.annotation.NonNull;
import android.view.animation.Interpolator;

class ValueAnimatorCompat {
    private final Impl mImpl;

    interface AnimatorListener {
        void onAnimationCancel(ValueAnimatorCompat valueAnimatorCompat);

        void onAnimationEnd(ValueAnimatorCompat valueAnimatorCompat);

        void onAnimationStart(ValueAnimatorCompat valueAnimatorCompat);
    }

    interface AnimatorUpdateListener {
        void onAnimationUpdate(ValueAnimatorCompat valueAnimatorCompat);
    }

    interface Creator {
        @NonNull
        ValueAnimatorCompat createAnimator();
    }

    static abstract class Impl {

        interface AnimatorListenerProxy {
            void onAnimationCancel();

            void onAnimationEnd();

            void onAnimationStart();
        }

        interface AnimatorUpdateListenerProxy {
            void onAnimationUpdate();
        }

        abstract void addListener(AnimatorListenerProxy animatorListenerProxy);

        abstract void addUpdateListener(AnimatorUpdateListenerProxy animatorUpdateListenerProxy);

        abstract void cancel();

        abstract void end();

        abstract float getAnimatedFloatValue();

        abstract float getAnimatedFraction();

        abstract int getAnimatedIntValue();

        abstract long getDuration();

        abstract boolean isRunning();

        abstract void setDuration(long j);

        abstract void setFloatValues(float f, float f2);

        abstract void setIntValues(int i, int i2);

        abstract void setInterpolator(Interpolator interpolator);

        abstract void start();

        Impl() {
        }
    }

    /* renamed from: android.support.design.widget.ValueAnimatorCompat.1 */
    class C08591 implements AnimatorUpdateListenerProxy {
        final /* synthetic */ AnimatorUpdateListener val$updateListener;

        C08591(AnimatorUpdateListener animatorUpdateListener) {
            this.val$updateListener = animatorUpdateListener;
        }

        public void onAnimationUpdate() {
            this.val$updateListener.onAnimationUpdate(ValueAnimatorCompat.this);
        }
    }

    /* renamed from: android.support.design.widget.ValueAnimatorCompat.2 */
    class C08602 implements AnimatorListenerProxy {
        final /* synthetic */ AnimatorListener val$listener;

        C08602(AnimatorListener animatorListener) {
            this.val$listener = animatorListener;
        }

        public void onAnimationStart() {
            this.val$listener.onAnimationStart(ValueAnimatorCompat.this);
        }

        public void onAnimationEnd() {
            this.val$listener.onAnimationEnd(ValueAnimatorCompat.this);
        }

        public void onAnimationCancel() {
            this.val$listener.onAnimationCancel(ValueAnimatorCompat.this);
        }
    }

    static class AnimatorListenerAdapter implements AnimatorListener {
        AnimatorListenerAdapter() {
        }

        public void onAnimationStart(ValueAnimatorCompat animator) {
        }

        public void onAnimationEnd(ValueAnimatorCompat animator) {
        }

        public void onAnimationCancel(ValueAnimatorCompat animator) {
        }
    }

    ValueAnimatorCompat(Impl impl) {
        this.mImpl = impl;
    }

    public void start() {
        this.mImpl.start();
    }

    public boolean isRunning() {
        return this.mImpl.isRunning();
    }

    public void setInterpolator(Interpolator interpolator) {
        this.mImpl.setInterpolator(interpolator);
    }

    public void addUpdateListener(AnimatorUpdateListener updateListener) {
        if (updateListener != null) {
            this.mImpl.addUpdateListener(new C08591(updateListener));
        } else {
            this.mImpl.addUpdateListener(null);
        }
    }

    public void addListener(AnimatorListener listener) {
        if (listener != null) {
            this.mImpl.addListener(new C08602(listener));
        } else {
            this.mImpl.addListener(null);
        }
    }

    public void setIntValues(int from, int to) {
        this.mImpl.setIntValues(from, to);
    }

    public int getAnimatedIntValue() {
        return this.mImpl.getAnimatedIntValue();
    }

    public void setFloatValues(float from, float to) {
        this.mImpl.setFloatValues(from, to);
    }

    public float getAnimatedFloatValue() {
        return this.mImpl.getAnimatedFloatValue();
    }

    public void setDuration(long duration) {
        this.mImpl.setDuration(duration);
    }

    public void cancel() {
        this.mImpl.cancel();
    }

    public float getAnimatedFraction() {
        return this.mImpl.getAnimatedFraction();
    }

    public void end() {
        this.mImpl.end();
    }

    public long getDuration() {
        return this.mImpl.getDuration();
    }
}
