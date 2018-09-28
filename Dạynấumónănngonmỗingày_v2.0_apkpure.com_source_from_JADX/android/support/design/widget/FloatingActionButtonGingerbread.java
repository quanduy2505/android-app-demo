package android.support.design.widget;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.C0023R;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

class FloatingActionButtonGingerbread extends FloatingActionButtonImpl {
    ShadowDrawableWrapper mShadowDrawable;
    private final StateListAnimator mStateListAnimator;

    /* renamed from: android.support.design.widget.FloatingActionButtonGingerbread.1 */
    class C08501 extends AnimationListenerAdapter {
        final /* synthetic */ boolean val$fromUser;
        final /* synthetic */ InternalVisibilityChangedListener val$listener;

        C08501(boolean z, InternalVisibilityChangedListener internalVisibilityChangedListener) {
            this.val$fromUser = z;
            this.val$listener = internalVisibilityChangedListener;
        }

        public void onAnimationEnd(Animation animation) {
            FloatingActionButtonGingerbread.this.mAnimState = 0;
            FloatingActionButtonGingerbread.this.mView.internalSetVisibility(this.val$fromUser ? 8 : 4, this.val$fromUser);
            if (this.val$listener != null) {
                this.val$listener.onHidden();
            }
        }
    }

    /* renamed from: android.support.design.widget.FloatingActionButtonGingerbread.2 */
    class C08512 extends AnimationListenerAdapter {
        final /* synthetic */ InternalVisibilityChangedListener val$listener;

        C08512(InternalVisibilityChangedListener internalVisibilityChangedListener) {
            this.val$listener = internalVisibilityChangedListener;
        }

        public void onAnimationEnd(Animation animation) {
            FloatingActionButtonGingerbread.this.mAnimState = 0;
            if (this.val$listener != null) {
                this.val$listener.onShown();
            }
        }
    }

    private abstract class ShadowAnimatorImpl extends AnimatorListenerAdapter implements AnimatorUpdateListener {
        private float mShadowSizeEnd;
        private float mShadowSizeStart;
        private boolean mValidValues;

        protected abstract float getTargetShadowSize();

        private ShadowAnimatorImpl() {
        }

        public void onAnimationUpdate(ValueAnimatorCompat animator) {
            if (!this.mValidValues) {
                this.mShadowSizeStart = FloatingActionButtonGingerbread.this.mShadowDrawable.getShadowSize();
                this.mShadowSizeEnd = getTargetShadowSize();
                this.mValidValues = true;
            }
            FloatingActionButtonGingerbread.this.mShadowDrawable.setShadowSize(this.mShadowSizeStart + ((this.mShadowSizeEnd - this.mShadowSizeStart) * animator.getAnimatedFraction()));
        }

        public void onAnimationEnd(ValueAnimatorCompat animator) {
            FloatingActionButtonGingerbread.this.mShadowDrawable.setShadowSize(this.mShadowSizeEnd);
            this.mValidValues = false;
        }
    }

    private class DisabledElevationAnimation extends ShadowAnimatorImpl {
        DisabledElevationAnimation() {
            super(null);
        }

        protected float getTargetShadowSize() {
            return 0.0f;
        }
    }

    private class ElevateToTranslationZAnimation extends ShadowAnimatorImpl {
        ElevateToTranslationZAnimation() {
            super(null);
        }

        protected float getTargetShadowSize() {
            return FloatingActionButtonGingerbread.this.mElevation + FloatingActionButtonGingerbread.this.mPressedTranslationZ;
        }
    }

    private class ResetElevationAnimation extends ShadowAnimatorImpl {
        ResetElevationAnimation() {
            super(null);
        }

        protected float getTargetShadowSize() {
            return FloatingActionButtonGingerbread.this.mElevation;
        }
    }

    FloatingActionButtonGingerbread(VisibilityAwareImageButton view, ShadowViewDelegate shadowViewDelegate, Creator animatorCreator) {
        super(view, shadowViewDelegate, animatorCreator);
        this.mStateListAnimator = new StateListAnimator();
        this.mStateListAnimator.addState(PRESSED_ENABLED_STATE_SET, createAnimator(new ElevateToTranslationZAnimation()));
        this.mStateListAnimator.addState(FOCUSED_ENABLED_STATE_SET, createAnimator(new ElevateToTranslationZAnimation()));
        this.mStateListAnimator.addState(ENABLED_STATE_SET, createAnimator(new ResetElevationAnimation()));
        this.mStateListAnimator.addState(EMPTY_STATE_SET, createAnimator(new DisabledElevationAnimation()));
    }

    void setBackgroundDrawable(ColorStateList backgroundTint, Mode backgroundTintMode, int rippleColor, int borderWidth) {
        Drawable[] layers;
        this.mShapeDrawable = DrawableCompat.wrap(createShapeDrawable());
        DrawableCompat.setTintList(this.mShapeDrawable, backgroundTint);
        if (backgroundTintMode != null) {
            DrawableCompat.setTintMode(this.mShapeDrawable, backgroundTintMode);
        }
        this.mRippleDrawable = DrawableCompat.wrap(createShapeDrawable());
        DrawableCompat.setTintList(this.mRippleDrawable, createColorStateList(rippleColor));
        if (borderWidth > 0) {
            this.mBorderDrawable = createBorderDrawable(borderWidth, backgroundTint);
            layers = new Drawable[]{this.mBorderDrawable, this.mShapeDrawable, this.mRippleDrawable};
        } else {
            this.mBorderDrawable = null;
            layers = new Drawable[]{this.mShapeDrawable, this.mRippleDrawable};
        }
        this.mContentBackground = new LayerDrawable(layers);
        this.mShadowDrawable = new ShadowDrawableWrapper(this.mView.getContext(), this.mContentBackground, this.mShadowViewDelegate.getRadius(), this.mElevation, this.mElevation + this.mPressedTranslationZ);
        this.mShadowDrawable.setAddPaddingForCorners(false);
        this.mShadowViewDelegate.setBackgroundDrawable(this.mShadowDrawable);
    }

    void setBackgroundTintList(ColorStateList tint) {
        if (this.mShapeDrawable != null) {
            DrawableCompat.setTintList(this.mShapeDrawable, tint);
        }
        if (this.mBorderDrawable != null) {
            this.mBorderDrawable.setBorderTint(tint);
        }
    }

    void setBackgroundTintMode(Mode tintMode) {
        if (this.mShapeDrawable != null) {
            DrawableCompat.setTintMode(this.mShapeDrawable, tintMode);
        }
    }

    void setRippleColor(int rippleColor) {
        if (this.mRippleDrawable != null) {
            DrawableCompat.setTintList(this.mRippleDrawable, createColorStateList(rippleColor));
        }
    }

    float getElevation() {
        return this.mElevation;
    }

    void onElevationsChanged(float elevation, float pressedTranslationZ) {
        if (this.mShadowDrawable != null) {
            this.mShadowDrawable.setShadowSize(elevation, this.mPressedTranslationZ + elevation);
            updatePadding();
        }
    }

    void onDrawableStateChanged(int[] state) {
        this.mStateListAnimator.setState(state);
    }

    void jumpDrawableToCurrentState() {
        this.mStateListAnimator.jumpToCurrentState();
    }

    void hide(@Nullable InternalVisibilityChangedListener listener, boolean fromUser) {
        if (!isOrWillBeHidden()) {
            this.mAnimState = 1;
            Animation anim = AnimationUtils.loadAnimation(this.mView.getContext(), C0023R.anim.design_fab_out);
            anim.setInterpolator(AnimationUtils.FAST_OUT_LINEAR_IN_INTERPOLATOR);
            anim.setDuration(200);
            anim.setAnimationListener(new C08501(fromUser, listener));
            this.mView.startAnimation(anim);
        }
    }

    void show(@Nullable InternalVisibilityChangedListener listener, boolean fromUser) {
        if (!isOrWillBeShown()) {
            this.mAnimState = 2;
            this.mView.internalSetVisibility(0, fromUser);
            Animation anim = AnimationUtils.loadAnimation(this.mView.getContext(), C0023R.anim.design_fab_in);
            anim.setDuration(200);
            anim.setInterpolator(AnimationUtils.LINEAR_OUT_SLOW_IN_INTERPOLATOR);
            anim.setAnimationListener(new C08512(listener));
            this.mView.startAnimation(anim);
        }
    }

    void onCompatShadowChanged() {
    }

    void getPadding(Rect rect) {
        this.mShadowDrawable.getPadding(rect);
    }

    private ValueAnimatorCompat createAnimator(@NonNull ShadowAnimatorImpl impl) {
        ValueAnimatorCompat animator = this.mAnimatorCreator.createAnimator();
        animator.setInterpolator(ANIM_INTERPOLATOR);
        animator.setDuration(100);
        animator.addListener(impl);
        animator.addUpdateListener(impl);
        animator.setFloatValues(0.0f, 1.0f);
        return animator;
    }

    private static ColorStateList createColorStateList(int selectedColor) {
        states = new int[3][];
        int[] colors = new int[3];
        states[0] = FOCUSED_ENABLED_STATE_SET;
        colors[0] = selectedColor;
        int i = 0 + 1;
        states[i] = PRESSED_ENABLED_STATE_SET;
        colors[i] = selectedColor;
        i++;
        states[i] = new int[0];
        colors[i] = 0;
        i++;
        return new ColorStateList(states, colors);
    }
}
