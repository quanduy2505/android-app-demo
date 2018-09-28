package agency.tango.materialintroscreen.animations.wrappers;

import agency.tango.materialintroscreen.animations.ViewTranslationWrapper;
import agency.tango.materialintroscreen.animations.translations.DefaultPositionTranslation;
import agency.tango.materialintroscreen.animations.translations.EnterDefaultTranslation;
import agency.tango.materialintroscreen.animations.translations.ExitDefaultTranslation;
import android.view.View;

public class BackButtonTranslationWrapper extends ViewTranslationWrapper {
    public BackButtonTranslationWrapper(View view) {
        super(view);
        setEnterTranslation(new EnterDefaultTranslation()).setDefaultTranslation(new DefaultPositionTranslation()).setExitTranslation(new ExitDefaultTranslation());
    }
}
