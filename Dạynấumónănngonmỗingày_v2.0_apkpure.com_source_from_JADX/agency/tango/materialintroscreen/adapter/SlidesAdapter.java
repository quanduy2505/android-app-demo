package agency.tango.materialintroscreen.adapter;

import agency.tango.materialintroscreen.SlideFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;
import java.util.ArrayList;

public class SlidesAdapter extends FragmentStatePagerAdapter {
    private ArrayList<SlideFragment> fragments;

    public SlidesAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
        this.fragments = new ArrayList();
    }

    public SlideFragment getItem(int position) {
        return (SlideFragment) this.fragments.get(position);
    }

    public Object instantiateItem(ViewGroup container, int position) {
        SlideFragment fragment = (SlideFragment) super.instantiateItem(container, position);
        this.fragments.set(position, fragment);
        return fragment;
    }

    public int getCount() {
        return this.fragments.size();
    }

    public void addItem(SlideFragment fragment) {
        this.fragments.add(getCount(), fragment);
        notifyDataSetChanged();
    }

    public int getLastItemPosition() {
        return getCount() - 1;
    }

    public boolean isLastSlide(int position) {
        return position == getCount() + -1;
    }

    public boolean shouldFinish(int position) {
        return position == getCount() && getItem(getCount() - 1).canMoveFurther();
    }

    public boolean shouldLockSlide(int position) {
        SlideFragment fragment = getItem(position);
        return !fragment.canMoveFurther() || fragment.hasNeededPermissionsToGrant();
    }
}
