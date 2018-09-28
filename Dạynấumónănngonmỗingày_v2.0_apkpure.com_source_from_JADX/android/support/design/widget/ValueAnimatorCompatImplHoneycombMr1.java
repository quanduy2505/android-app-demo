package android.support.design.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.TargetApi;
import android.support.annotation.RequiresApi;
import android.view.animation.Interpolator;

@TargetApi(12)
@RequiresApi(12)
class ValueAnimatorCompatImplHoneycombMr1 extends Impl {
    private final ValueAnimator mValueAnimator;

    /* renamed from: android.support.design.widget.ValueAnimatorCompatImplHoneycombMr1.1 */
    class C00401 implements AnimatorUpdateListener {
        final /* synthetic */ AnimatorUpdateListenerProxy val$updateListener;

        C00401(AnimatorUpdateListenerProxy animatorUpdateListenerProxy) {
            this.val$updateListener = animatorUpdateListenerProxy;
        }

        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            this.val$updateListener.onAnimationUpdate();
        }
    }

    /* renamed from: android.support.design.widget.ValueAnimatorCompatImplHoneycombMr1.2 */
    class C00412 extends AnimatorListenerAdapter {
        final /* synthetic */ AnimatorListenerProxy val$listener;

        C00412(AnimatorListenerProxy animatorListenerProxy) {
            this.val$listener = animatorListenerProxy;
        }

        public void onAnimationStart(Animator animator) {
            this.val$listener.onAnimationStart();
        }

        public void onAnimationEnd(Animator animator) {
            this.val$listener.onAnimationEnd();
        }

        public void onAnimationCancel(Animator animator) {
            this.val$listener.onAnimationCancel();
        }
    }

    ValueAnimatorCompatImplHoneycombMr1() {
        this.mValueAnimator = new ValueAnimator();
    }

    public void start() {
        this.mValueAnimator.start();
    }

    public boolean isRunning() {
        return this.mValueAnimator.isRunning();
    }

    public void setInterpolator(Interpolator interpolator) {
        this.mValueAnimator.setInterpolator(interpolator);
    }

    public void addUpdateListener(AnimatorUpdateListenerProxy updateListener) {
        this.mValueAnimator.addUpdateListener(new C00401(updateListener));
    }

    public void addListener(AnimatorListenerProxy listener) {
        this.mValueAnimator.addListener(new C00412(listener));
    }

    public void setIntValues(int from, int to) {
        this.mValueAnimator.setIntValues(new int[]{from, to});
    }

    public int getAnimatedIntValue() {
        return ((Integer) this.mValueAnimator.getAnimatedValue()).intValue();
    }

    public void setFloatValues(float from, float to) {
        this.mValueAnimator.setFloatValues(new float[]{from, to});
    }

    public float getAnimatedFloatValue() {
        return ((Float) this.mValueAnimator.getAnimatedValue()).floatValue();
    }

    public void setDuration(long duration) {
        this.mValueAnimator.setDuration(duration);
    }

    public void cancel() {
        this.mValueAnimator.cancel();
    }

    public float getAnimatedFraction() {
        return this.mValueAnimator.getAnimatedFraction();
    }

    public void end() {
        this.mValueAnimator.end();
    }

    public long getDuration() {
        return this.mValueAnimator.getDuration();
    }
}
