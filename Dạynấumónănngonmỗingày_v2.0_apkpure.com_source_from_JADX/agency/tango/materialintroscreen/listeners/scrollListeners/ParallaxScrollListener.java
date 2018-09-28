package agency.tango.materialintroscreen.listeners.scrollListeners;

import agency.tango.materialintroscreen.SlideFragment;
import agency.tango.materialintroscreen.adapter.SlidesAdapter;
import agency.tango.materialintroscreen.listeners.IPageScrolledListener;
import agency.tango.materialintroscreen.parallax.Parallaxable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

public class ParallaxScrollListener implements IPageScrolledListener {
    private SlidesAdapter adapter;

    public ParallaxScrollListener(SlidesAdapter adapter) {
        this.adapter = adapter;
    }

    public void pageScrolled(int position, float offset) {
        if (position != this.adapter.getCount()) {
            Fragment fragment = this.adapter.getItem(position);
            Fragment fragmentNext = getNextFragment(position);
            if (fragment != null && (fragment instanceof Parallaxable)) {
                ((Parallaxable) fragment).setOffset(offset);
            }
            if (fragmentNext != null && (fragment instanceof Parallaxable)) {
                ((Parallaxable) fragmentNext).setOffset(offset - 1.0f);
            }
        }
    }

    @Nullable
    private SlideFragment getNextFragment(int position) {
        if (position < this.adapter.getLastItemPosition()) {
            return this.adapter.getItem(position + 1);
        }
        return null;
    }
}
