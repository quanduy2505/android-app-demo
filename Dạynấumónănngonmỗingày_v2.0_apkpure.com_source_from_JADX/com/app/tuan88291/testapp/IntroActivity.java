package com.app.tuan88291.testapp;

import agency.tango.materialintroscreen.MaterialIntroActivity;
import agency.tango.materialintroscreen.animations.IViewTranslation;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.view.View;

public class IntroActivity extends MaterialIntroActivity {

    /* renamed from: com.app.tuan88291.testapp.IntroActivity.1 */
    class C09591 implements IViewTranslation {
        C09591() {
        }

        public void translate(View view, @FloatRange(from = 0.0d, to = 1.0d) float percentage) {
            view.setAlpha(percentage);
        }
    }

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableLastSlideAlphaExitTransition(true);
        getBackButtonTranslationWrapper().setEnterTranslation(new C09591());
        addSlide(new CustomSlide1());
        addSlide(new CustomSlide2());
        addSlide(new CustomSlide3());
        addSlide(new CustomSlide4());
    }

    public void onFinish() {
        startActivity(new Intent(this, ContentView.class));
    }
}
