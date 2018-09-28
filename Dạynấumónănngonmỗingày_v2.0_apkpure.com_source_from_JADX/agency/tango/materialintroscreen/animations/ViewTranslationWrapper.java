package agency.tango.materialintroscreen.animations;

import agency.tango.materialintroscreen.animations.translations.NoTranslation;
import android.support.annotation.AnimRes;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class ViewTranslationWrapper {
    private IViewTranslation defaultTranslation;
    private IViewTranslation enterTranslation;
    private Animation errorAnimation;
    private IViewTranslation exitTranslation;
    private View view;

    public ViewTranslationWrapper(View view) {
        this.view = view;
        this.enterTranslation = new NoTranslation();
        this.exitTranslation = new NoTranslation();
        setErrorAnimation(0);
    }

    public ViewTranslationWrapper setEnterTranslation(IViewTranslation enterTranslation) {
        this.enterTranslation = enterTranslation;
        return this;
    }

    public ViewTranslationWrapper setExitTranslation(IViewTranslation exitTranslation) {
        this.exitTranslation = exitTranslation;
        return this;
    }

    public ViewTranslationWrapper setDefaultTranslation(IViewTranslation defaultTranslation) {
        this.defaultTranslation = defaultTranslation;
        return this;
    }

    public ViewTranslationWrapper setErrorAnimation(@AnimRes int errorAnimation) {
        if (errorAnimation != 0) {
            this.errorAnimation = AnimationUtils.loadAnimation(this.view.getContext(), errorAnimation);
        }
        return this;
    }

    public void enterTranslate(float percentage) {
        this.enterTranslation.translate(this.view, percentage);
    }

    public void exitTranslate(float percentage) {
        this.exitTranslation.translate(this.view, percentage);
    }

    public void defaultTranslate(float percentage) {
        this.defaultTranslation.translate(this.view, percentage);
    }

    public void error() {
        if (this.errorAnimation != null) {
            this.view.startAnimation(this.errorAnimation);
        }
    }
}
