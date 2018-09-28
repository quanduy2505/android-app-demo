package agency.tango.materialintroscreen.animations.translations;

import agency.tango.materialintroscreen.animations.IViewTranslation;
import android.support.annotation.FloatRange;
import android.view.View;

public class AlphaTranslation implements IViewTranslation {
    public void translate(View view, @FloatRange(from = 0.0d, to = 1.0d) float percentage) {
        view.setAlpha(1.0f - percentage);
    }
}
