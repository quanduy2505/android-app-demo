package agency.tango.materialintroscreen.listeners;

import agency.tango.materialintroscreen.adapter.SlidesAdapter;
import agency.tango.materialintroscreen.animations.ViewTranslationWrapper;
import android.support.v4.view.CustomViewPager.OnPageChangeListener;
import java.util.ArrayList;
import java.util.List;

public class ViewBehavioursOnPageChangeListener implements OnPageChangeListener {
    private final SlidesAdapter adapter;
    private List<IPageSelectedListener> listeners;
    private List<IPageScrolledListener> pageScrolledListeners;
    private List<ViewTranslationWrapper> wrappers;

    public ViewBehavioursOnPageChangeListener(SlidesAdapter adapter) {
        this.listeners = new ArrayList();
        this.wrappers = new ArrayList();
        this.pageScrolledListeners = new ArrayList();
        this.adapter = adapter;
    }

    public ViewBehavioursOnPageChangeListener registerPageSelectedListener(IPageSelectedListener pageSelectedListener) {
        this.listeners.add(pageSelectedListener);
        return this;
    }

    public ViewBehavioursOnPageChangeListener registerViewTranslationWrapper(ViewTranslationWrapper wrapper) {
        this.wrappers.add(wrapper);
        return this;
    }

    public ViewBehavioursOnPageChangeListener registerOnPageScrolled(IPageScrolledListener pageScrolledListener) {
        this.pageScrolledListeners.add(pageScrolledListener);
        return this;
    }

    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (isFirstSlide(position)) {
            for (ViewTranslationWrapper wrapper : this.wrappers) {
                wrapper.enterTranslate(positionOffset);
            }
        } else if (this.adapter.isLastSlide(position)) {
            for (ViewTranslationWrapper wrapper2 : this.wrappers) {
                wrapper2.exitTranslate(positionOffset);
            }
        } else {
            for (ViewTranslationWrapper wrapper22 : this.wrappers) {
                wrapper22.defaultTranslate(positionOffset);
            }
        }
        for (IPageScrolledListener pageScrolledListener : this.pageScrolledListeners) {
            pageScrolledListener.pageScrolled(position, positionOffset);
        }
    }

    public void onPageSelected(int position) {
        for (IPageSelectedListener pageSelectedListener : this.listeners) {
            pageSelectedListener.pageSelected(position);
        }
    }

    public void onPageScrollStateChanged(int state) {
    }

    private boolean isFirstSlide(int position) {
        return position == 0;
    }
}
