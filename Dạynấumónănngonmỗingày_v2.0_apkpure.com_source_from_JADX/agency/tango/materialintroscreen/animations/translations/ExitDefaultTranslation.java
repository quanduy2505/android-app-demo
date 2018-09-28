package agency.tango.materialintroscreen.animations.translations;

import agency.tango.materialintroscreen.C0005R;
import agency.tango.materialintroscreen.animations.IViewTranslation;
import android.support.annotation.FloatRange;
import android.view.View;

public class ExitDefaultTranslation implements IViewTranslation {
    public void translate(View view, @FloatRange(from = 0.0d, to = 1.0d) float percentage) {
        view.setTranslationY(((float) view.getResources().getDimensionPixelOffset(C0005R.dimen.y_offset)) * percentage);
    }
}
