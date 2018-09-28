package agency.tango.materialintroscreen.animations.wrappers;

import agency.tango.materialintroscreen.C0005R;
import agency.tango.materialintroscreen.animations.ViewTranslationWrapper;
import agency.tango.materialintroscreen.animations.translations.DefaultPositionTranslation;
import agency.tango.materialintroscreen.animations.translations.ExitDefaultTranslation;
import android.view.View;

public class NextButtonTranslationWrapper extends ViewTranslationWrapper {
    public NextButtonTranslationWrapper(View view) {
        super(view);
        setExitTranslation(new ExitDefaultTranslation()).setDefaultTranslation(new DefaultPositionTranslation()).setErrorAnimation(C0005R.anim.shake_it);
    }
}
