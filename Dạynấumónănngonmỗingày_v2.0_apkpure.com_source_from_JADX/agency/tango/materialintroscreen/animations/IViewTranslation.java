package agency.tango.materialintroscreen.animations;

import android.support.annotation.FloatRange;
import android.view.View;

public interface IViewTranslation {
    void translate(View view, @FloatRange(from = 0.0d, to = 1.0d) float f);
}
