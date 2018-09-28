package agency.tango.materialintroscreen.parallax;

import android.support.annotation.FloatRange;

public interface Parallaxable {
    void setOffset(@FloatRange(from = -1.0d, to = 1.0d) float f);
}
