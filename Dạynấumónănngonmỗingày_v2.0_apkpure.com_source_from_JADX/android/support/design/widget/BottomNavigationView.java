package android.support.design.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build.VERSION;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.C0023R;
import android.support.design.internal.BottomNavigationMenu;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.internal.BottomNavigationPresenter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.appcompat.C0181R;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.view.SupportMenuInflater;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuBuilder.Callback;
import android.support.v7.widget.TintTypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

public class BottomNavigationView extends FrameLayout {
    private static final int[] CHECKED_STATE_SET;
    private static final int[] DISABLED_STATE_SET;
    private OnNavigationItemSelectedListener mListener;
    private final MenuBuilder mMenu;
    private MenuInflater mMenuInflater;
    private final BottomNavigationMenuView mMenuView;
    private final BottomNavigationPresenter mPresenter;

    public interface OnNavigationItemSelectedListener {
        boolean onNavigationItemSelected(@NonNull MenuItem menuItem);
    }

    /* renamed from: android.support.design.widget.BottomNavigationView.1 */
    class C08391 implements Callback {
        C08391() {
        }

        public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
            return (BottomNavigationView.this.mListener == null || BottomNavigationView.this.mListener.onNavigationItemSelected(item)) ? false : true;
        }

        public void onMenuModeChange(MenuBuilder menu) {
        }
    }

    static {
        CHECKED_STATE_SET = new int[]{16842912};
        DISABLED_STATE_SET = new int[]{-16842910};
    }

    public BottomNavigationView(Context context) {
        this(context, null);
    }

    public BottomNavigationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomNavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mPresenter = new BottomNavigationPresenter();
        ThemeUtils.checkAppCompatTheme(context);
        this.mMenu = new BottomNavigationMenu(context);
        this.mMenuView = new BottomNavigationMenuView(context);
        LayoutParams params = new LayoutParams(-2, -2);
        params.gravity = 17;
        this.mMenuView.setLayoutParams(params);
        this.mPresenter.setBottomNavigationMenuView(this.mMenuView);
        this.mMenuView.setPresenter(this.mPresenter);
        this.mMenu.addMenuPresenter(this.mPresenter);
        this.mPresenter.initForMenu(getContext(), this.mMenu);
        TintTypedArray a = TintTypedArray.obtainStyledAttributes(context, attrs, C0023R.styleable.BottomNavigationView, defStyleAttr, C0023R.style.Widget_Design_BottomNavigationView);
        if (a.hasValue(C0023R.styleable.BottomNavigationView_itemIconTint)) {
            this.mMenuView.setIconTintList(a.getColorStateList(C0023R.styleable.BottomNavigationView_itemIconTint));
        } else {
            this.mMenuView.setIconTintList(createDefaultColorStateList(16842808));
        }
        if (a.hasValue(C0023R.styleable.BottomNavigationView_itemTextColor)) {
            this.mMenuView.setItemTextColor(a.getColorStateList(C0023R.styleable.BottomNavigationView_itemTextColor));
        } else {
            this.mMenuView.setItemTextColor(createDefaultColorStateList(16842808));
        }
        if (a.hasValue(C0023R.styleable.BottomNavigationView_elevation)) {
            ViewCompat.setElevation(this, (float) a.getDimensionPixelSize(C0023R.styleable.BottomNavigationView_elevation, 0));
        }
        this.mMenuView.setItemBackgroundRes(a.getResourceId(C0023R.styleable.BottomNavigationView_itemBackground, 0));
        if (a.hasValue(C0023R.styleable.BottomNavigationView_menu)) {
            inflateMenu(a.getResourceId(C0023R.styleable.BottomNavigationView_menu, 0));
        }
        a.recycle();
        addView(this.mMenuView, params);
        if (VERSION.SDK_INT < 21) {
            addCompatibilityTopDivider(context);
        }
        this.mMenu.setCallback(new C08391());
    }

    public void setOnNavigationItemSelectedListener(@Nullable OnNavigationItemSelectedListener listener) {
        this.mListener = listener;
    }

    @NonNull
    public Menu getMenu() {
        return this.mMenu;
    }

    public void inflateMenu(int resId) {
        this.mPresenter.setUpdateSuspended(true);
        getMenuInflater().inflate(resId, this.mMenu);
        this.mPresenter.setUpdateSuspended(false);
        this.mPresenter.updateMenuView(true);
    }

    public int getMaxItemCount() {
        return 5;
    }

    @Nullable
    public ColorStateList getItemIconTintList() {
        return this.mMenuView.getIconTintList();
    }

    public void setItemIconTintList(@Nullable ColorStateList tint) {
        this.mMenuView.setIconTintList(tint);
    }

    @Nullable
    public ColorStateList getItemTextColor() {
        return this.mMenuView.getItemTextColor();
    }

    public void setItemTextColor(@Nullable ColorStateList textColor) {
        this.mMenuView.setItemTextColor(textColor);
    }

    @DrawableRes
    public int getItemBackgroundResource() {
        return this.mMenuView.getItemBackgroundRes();
    }

    public void setItemBackgroundResource(@DrawableRes int resId) {
        this.mMenuView.setItemBackgroundRes(resId);
    }

    private void addCompatibilityTopDivider(Context context) {
        View divider = new View(context);
        divider.setBackgroundColor(ContextCompat.getColor(context, C0023R.color.design_bottom_navigation_shadow_color));
        divider.setLayoutParams(new LayoutParams(-1, getResources().getDimensionPixelSize(C0023R.dimen.design_bottom_navigation_shadow_height)));
        addView(divider);
    }

    private MenuInflater getMenuInflater() {
        if (this.mMenuInflater == null) {
            this.mMenuInflater = new SupportMenuInflater(getContext());
        }
        return this.mMenuInflater;
    }

    private ColorStateList createDefaultColorStateList(int baseColorThemeAttr) {
        TypedValue value = new TypedValue();
        if (!getContext().getTheme().resolveAttribute(baseColorThemeAttr, value, true)) {
            return null;
        }
        ColorStateList baseColor = AppCompatResources.getColorStateList(getContext(), value.resourceId);
        if (!getContext().getTheme().resolveAttribute(C0181R.attr.colorPrimary, value, true)) {
            return null;
        }
        int colorPrimary = value.data;
        int defaultColor = baseColor.getDefaultColor();
        return new ColorStateList(new int[][]{DISABLED_STATE_SET, CHECKED_STATE_SET, EMPTY_STATE_SET}, new int[]{baseColor.getColorForState(DISABLED_STATE_SET, defaultColor), colorPrimary, defaultColor});
    }
}
