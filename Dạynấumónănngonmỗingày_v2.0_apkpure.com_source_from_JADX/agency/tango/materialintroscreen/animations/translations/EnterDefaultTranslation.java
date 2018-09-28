package agency.tango.materialintroscreen.animations.translations;

import agency.tango.materialintroscreen.C0005R;
import agency.tango.materialintroscreen.animations.IViewTranslation;
import android.support.annotation.FloatRange;
import android.view.View;

public class EnterDefaultTranslation implements IViewTranslation {
    public void translate(View view, @FloatRange(from = 0.0d, to = 1.0d) float percentage) {
        view.setTranslationY((1.0f - percentage) * ((float) view.getResources().getDimensionPixelOffset(C0005R.dimen.y_offset)));
    }
}
