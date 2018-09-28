package android.support.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.support.annotation.RequiresApi;
import android.support.transition.TransitionPort.TransitionListenerAdapter;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

@TargetApi(14)
@RequiresApi(14)
class FadePort extends VisibilityPort {
    private static boolean DBG = false;
    public static final int IN = 1;
    private static final String LOG_TAG = "Fade";
    public static final int OUT = 2;
    private static final String PROPNAME_SCREEN_X = "android:fade:screenX";
    private static final String PROPNAME_SCREEN_Y = "android:fade:screenY";
    private int mFadingMode;

    /* renamed from: android.support.transition.FadePort.2 */
    class C00492 extends AnimatorListenerAdapter {
        final /* synthetic */ View val$finalOverlayView;
        final /* synthetic */ ViewGroup val$finalSceneRoot;
        final /* synthetic */ View val$finalView;
        final /* synthetic */ View val$finalViewToKeep;
        final /* synthetic */ int val$finalVisibility;

        C00492(View view, View view2, int i, View view3, ViewGroup viewGroup) {
            this.val$finalView = view;
            this.val$finalViewToKeep = view2;
            this.val$finalVisibility = i;
            this.val$finalOverlayView = view3;
            this.val$finalSceneRoot = viewGroup;
        }

        public void onAnimationEnd(Animator animation) {
            this.val$finalView.setAlpha(1.0f);
            if (this.val$finalViewToKeep != null) {
                this.val$finalViewToKeep.setVisibility(this.val$finalVisibility);
            }
            if (this.val$finalOverlayView != null) {
                ViewGroupOverlay.createFrom(this.val$finalSceneRoot).remove(this.val$finalOverlayView);
            }
        }
    }

    /* renamed from: android.support.transition.FadePort.3 */
    class C00503 extends AnimatorListenerAdapter {
        boolean mCanceled;
        float mPausedAlpha;
        final /* synthetic */ View val$finalOverlayView;
        final /* synthetic */ ViewGroup val$finalSceneRoot;
        final /* synthetic */ View val$finalView;
        final /* synthetic */ View val$finalViewToKeep;
        final /* synthetic */ int val$finalVisibility;

        C00503(View view, View view2, int i, View view3, ViewGroup viewGroup) {
            this.val$finalView = view;
            this.val$finalViewToKeep = view2;
            this.val$finalVisibility = i;
            this.val$finalOverlayView = view3;
            this.val$finalSceneRoot = viewGroup;
            this.mCanceled = false;
            this.mPausedAlpha = -1.0f;
        }

        public void onAnimationCancel(Animator animation) {
            this.mCanceled = true;
            if (this.mPausedAlpha >= 0.0f) {
                this.val$finalView.setAlpha(this.mPausedAlpha);
            }
        }

        public void onAnimationEnd(Animator animation) {
            if (!this.mCanceled) {
                this.val$finalView.setAlpha(1.0f);
            }
            if (!(this.val$finalViewToKeep == null || this.mCanceled)) {
                this.val$finalViewToKeep.setVisibility(this.val$finalVisibility);
            }
            if (this.val$finalOverlayView != null) {
                ViewGroupOverlay.createFrom(this.val$finalSceneRoot).add(this.val$finalOverlayView);
            }
        }
    }

    /* renamed from: android.support.transition.FadePort.1 */
    class C12981 extends TransitionListenerAdapter {
        boolean mCanceled;
        float mPausedAlpha;
        final /* synthetic */ View val$endView;

        C12981(View view) {
            this.val$endView = view;
            this.mCanceled = false;
        }

        public void onTransitionCancel(TransitionPort transition) {
            this.val$endView.setAlpha(1.0f);
            this.mCanceled = true;
        }

        public void onTransitionEnd(TransitionPort transition) {
            if (!this.mCanceled) {
                this.val$endView.setAlpha(1.0f);
            }
        }

        public void onTransitionPause(TransitionPort transition) {
            this.mPausedAlpha = this.val$endView.getAlpha();
            this.val$endView.setAlpha(1.0f);
        }

        public void onTransitionResume(TransitionPort transition) {
            this.val$endView.setAlpha(this.mPausedAlpha);
        }
    }

    static {
        DBG = false;
    }

    public FadePort() {
        this(3);
    }

    public FadePort(int fadingMode) {
        this.mFadingMode = fadingMode;
    }

    private Animator createAnimation(View view, float startAlpha, float endAlpha, AnimatorListenerAdapter listener) {
        Animator animator = null;
        if (startAlpha != endAlpha) {
            float[] fArr = new float[OUT];
            fArr[0] = startAlpha;
            fArr[IN] = endAlpha;
            animator = ObjectAnimator.ofFloat(view, "alpha", fArr);
            if (DBG) {
                Log.d(LOG_TAG, "Created animator " + animator);
            }
            if (listener != null) {
                animator.addListener(listener);
            }
        } else if (listener != null) {
            listener.onAnimationEnd(null);
        }
        return animator;
    }

    private void captureValues(TransitionValues transitionValues) {
        int[] loc = new int[OUT];
        transitionValues.view.getLocationOnScreen(loc);
        transitionValues.values.put(PROPNAME_SCREEN_X, Integer.valueOf(loc[0]));
        transitionValues.values.put(PROPNAME_SCREEN_Y, Integer.valueOf(loc[IN]));
    }

    public void captureStartValues(TransitionValues transitionValues) {
        super.captureStartValues(transitionValues);
        captureValues(transitionValues);
    }

    public Animator onAppear(ViewGroup sceneRoot, TransitionValues startValues, int startVisibility, TransitionValues endValues, int endVisibility) {
        if ((this.mFadingMode & IN) != IN || endValues == null) {
            return null;
        }
        View endView = endValues.view;
        if (DBG) {
            View startView;
            if (startValues != null) {
                startView = startValues.view;
            } else {
                startView = null;
            }
            Log.d(LOG_TAG, "Fade.onAppear: startView, startVis, endView, endVis = " + startView + ", " + startVisibility + ", " + endView + ", " + endVisibility);
        }
        endView.setAlpha(0.0f);
        addListener(new C12981(endView));
        return createAnimation(endView, 0.0f, 1.0f, null);
    }

    public Animator onDisappear(ViewGroup sceneRoot, TransitionValues startValues, int startVisibility, TransitionValues endValues, int endVisibility) {
        if ((this.mFadingMode & OUT) != OUT) {
            return null;
        }
        View view = null;
        View startView = startValues != null ? startValues.view : null;
        View endView = endValues != null ? endValues.view : null;
        if (DBG) {
            Log.d(LOG_TAG, "Fade.onDisappear: startView, startVis, endView, endVis = " + startView + ", " + startVisibility + ", " + endView + ", " + endVisibility);
        }
        View overlayView = null;
        View viewToKeep = null;
        if (endView == null || endView.getParent() == null) {
            if (endView != null) {
                overlayView = endView;
                view = endView;
            } else if (startView != null) {
                if (startView.getParent() == null) {
                    overlayView = startView;
                    view = startView;
                } else if ((startView.getParent() instanceof View) && startView.getParent().getParent() == null) {
                    int id = ((View) startView.getParent()).getId();
                    if (!(id == -1 || sceneRoot.findViewById(id) == null || !this.mCanRemoveViews)) {
                        overlayView = startView;
                        view = startView;
                    }
                }
            }
        } else if (endVisibility == 4) {
            view = endView;
            viewToKeep = view;
        } else if (startView == endView) {
            view = endView;
            viewToKeep = view;
        } else {
            view = startView;
            overlayView = view;
        }
        int finalVisibility = endVisibility;
        if (overlayView != null) {
            int screenX = ((Integer) startValues.values.get(PROPNAME_SCREEN_X)).intValue();
            int screenY = ((Integer) startValues.values.get(PROPNAME_SCREEN_Y)).intValue();
            int[] loc = new int[OUT];
            sceneRoot.getLocationOnScreen(loc);
            ViewCompat.offsetLeftAndRight(overlayView, (screenX - loc[0]) - overlayView.getLeft());
            ViewCompat.offsetTopAndBottom(overlayView, (screenY - loc[IN]) - overlayView.getTop());
            ViewGroupOverlay.createFrom(sceneRoot).add(overlayView);
            return createAnimation(view, 1.0f, 0.0f, new C00492(view, viewToKeep, finalVisibility, overlayView, sceneRoot));
        } else if (viewToKeep == null) {
            return null;
        } else {
            viewToKeep.setVisibility(0);
            return createAnimation(view, 1.0f, 0.0f, new C00503(view, viewToKeep, finalVisibility, overlayView, sceneRoot));
        }
    }
}
