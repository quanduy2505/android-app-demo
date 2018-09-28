package android.support.design.internal;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Build.VERSION;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.design.C0023R;
import android.support.v4.util.Pools.Pool;
import android.support.v4.util.Pools.SynchronizedPool;
import android.support.v4.view.ViewCompat;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.view.menu.MenuView;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

@RestrictTo({Scope.LIBRARY_GROUP})
public class BottomNavigationMenuView extends ViewGroup implements MenuView {
    private static final Pool<BottomNavigationItemView> sItemPool;
    private int mActiveButton;
    private final int mActiveItemMaxWidth;
    private final BottomNavigationAnimationHelperBase mAnimationHelper;
    private BottomNavigationItemView[] mButtons;
    private final int mInactiveItemMaxWidth;
    private final int mInactiveItemMinWidth;
    private int mItemBackgroundRes;
    private final int mItemHeight;
    private ColorStateList mItemIconTint;
    private ColorStateList mItemTextColor;
    private MenuBuilder mMenu;
    private final OnClickListener mOnClickListener;
    private BottomNavigationPresenter mPresenter;
    private boolean mShiftingMode;
    private int[] mTempChildWidths;

    /* renamed from: android.support.design.internal.BottomNavigationMenuView.1 */
    class C00241 implements OnClickListener {
        C00241() {
        }

        public void onClick(View v) {
            BottomNavigationItemView itemView = (BottomNavigationItemView) v;
            int itemPosition = itemView.getItemPosition();
            if (!BottomNavigationMenuView.this.mMenu.performItemAction(itemView.getItemData(), BottomNavigationMenuView.this.mPresenter, 0)) {
                BottomNavigationMenuView.this.activateNewButton(itemPosition);
            }
        }
    }

    static {
        sItemPool = new SynchronizedPool(5);
    }

    public BottomNavigationMenuView(Context context) {
        this(context, null);
    }

    public BottomNavigationMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mShiftingMode = true;
        this.mActiveButton = 0;
        Resources res = getResources();
        this.mInactiveItemMaxWidth = res.getDimensionPixelSize(C0023R.dimen.design_bottom_navigation_item_max_width);
        this.mInactiveItemMinWidth = res.getDimensionPixelSize(C0023R.dimen.design_bottom_navigation_item_min_width);
        this.mActiveItemMaxWidth = res.getDimensionPixelSize(C0023R.dimen.design_bottom_navigation_active_item_max_width);
        this.mItemHeight = res.getDimensionPixelSize(C0023R.dimen.design_bottom_navigation_height);
        if (VERSION.SDK_INT >= 14) {
            this.mAnimationHelper = new BottomNavigationAnimationHelperIcs();
        } else {
            this.mAnimationHelper = new BottomNavigationAnimationHelperBase();
        }
        this.mOnClickListener = new C00241();
        this.mTempChildWidths = new int[5];
    }

    public void initialize(MenuBuilder menu) {
        this.mMenu = menu;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int i;
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int count = getChildCount();
        int heightSpec = MeasureSpec.makeMeasureSpec(this.mItemHeight, 1073741824);
        int i2;
        int extra;
        int[] iArr;
        if (this.mShiftingMode) {
            int inactiveCount = count - 1;
            i2 = this.mInactiveItemMinWidth;
            int activeWidth = Math.min(width - (r0 * inactiveCount), this.mActiveItemMaxWidth);
            int inactiveWidth = Math.min((width - activeWidth) / inactiveCount, this.mInactiveItemMaxWidth);
            extra = (width - activeWidth) - (inactiveWidth * inactiveCount);
            for (i = 0; i < count; i++) {
                int[] iArr2 = this.mTempChildWidths;
                i2 = this.mActiveButton;
                if (i == r0) {
                    i2 = activeWidth;
                } else {
                    i2 = inactiveWidth;
                }
                iArr2[i] = i2;
                if (extra > 0) {
                    iArr = this.mTempChildWidths;
                    iArr[i] = iArr[i] + 1;
                    extra--;
                }
            }
        } else {
            if (count == 0) {
                i2 = 1;
            } else {
                i2 = count;
            }
            int childWidth = Math.min(width / i2, this.mActiveItemMaxWidth);
            extra = width - (childWidth * count);
            for (i = 0; i < count; i++) {
                this.mTempChildWidths[i] = childWidth;
                if (extra > 0) {
                    iArr = this.mTempChildWidths;
                    iArr[i] = iArr[i] + 1;
                    extra--;
                }
            }
        }
        int totalWidth = 0;
        for (i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != 8) {
                child.measure(MeasureSpec.makeMeasureSpec(this.mTempChildWidths[i], 1073741824), heightSpec);
                LayoutParams params = child.getLayoutParams();
                params.width = child.getMeasuredWidth();
                totalWidth += child.getMeasuredWidth();
            }
        }
        setMeasuredDimension(ViewCompat.resolveSizeAndState(totalWidth, MeasureSpec.makeMeasureSpec(totalWidth, 1073741824), 0), ViewCompat.resolveSizeAndState(this.mItemHeight, heightSpec, 0));
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int count = getChildCount();
        int width = right - left;
        int height = bottom - top;
        int used = 0;
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != 8) {
                if (ViewCompat.getLayoutDirection(this) == 1) {
                    child.layout((width - used) - child.getMeasuredWidth(), 0, width - used, height);
                } else {
                    child.layout(used, 0, child.getMeasuredWidth() + used, height);
                }
                used += child.getMeasuredWidth();
            }
        }
    }

    public int getWindowAnimations() {
        return 0;
    }

    public void setIconTintList(ColorStateList tint) {
        this.mItemIconTint = tint;
        if (this.mButtons != null) {
            for (BottomNavigationItemView item : this.mButtons) {
                item.setIconTintList(tint);
            }
        }
    }

    @Nullable
    public ColorStateList getIconTintList() {
        return this.mItemIconTint;
    }

    public void setItemTextColor(ColorStateList color) {
        this.mItemTextColor = color;
        if (this.mButtons != null) {
            for (BottomNavigationItemView item : this.mButtons) {
                item.setTextColor(color);
            }
        }
    }

    public ColorStateList getItemTextColor() {
        return this.mItemTextColor;
    }

    public void setItemBackgroundRes(int background) {
        this.mItemBackgroundRes = background;
        if (this.mButtons != null) {
            for (BottomNavigationItemView item : this.mButtons) {
                item.setItemBackground(background);
            }
        }
    }

    public int getItemBackgroundRes() {
        return this.mItemBackgroundRes;
    }

    public void setPresenter(BottomNavigationPresenter presenter) {
        this.mPresenter = presenter;
    }

    public void buildMenuView() {
        if (this.mButtons != null) {
            for (BottomNavigationItemView item : this.mButtons) {
                sItemPool.release(item);
            }
        }
        removeAllViews();
        if (this.mMenu.size() != 0) {
            boolean z;
            this.mButtons = new BottomNavigationItemView[this.mMenu.size()];
            if (this.mMenu.size() > 3) {
                z = true;
            } else {
                z = false;
            }
            this.mShiftingMode = z;
            for (int i = 0; i < this.mMenu.size(); i++) {
                this.mPresenter.setUpdateSuspended(true);
                this.mMenu.getItem(i).setCheckable(true);
                this.mPresenter.setUpdateSuspended(false);
                BottomNavigationItemView child = getNewItem();
                this.mButtons[i] = child;
                child.setIconTintList(this.mItemIconTint);
                child.setTextColor(this.mItemTextColor);
                child.setItemBackground(this.mItemBackgroundRes);
                child.setShiftingMode(this.mShiftingMode);
                child.initialize((MenuItemImpl) this.mMenu.getItem(i), 0);
                child.setItemPosition(i);
                child.setOnClickListener(this.mOnClickListener);
                addView(child);
            }
            this.mActiveButton = Math.min(this.mMenu.size() - 1, this.mActiveButton);
            this.mMenu.getItem(this.mActiveButton).setChecked(true);
        }
    }

    public void updateMenuView() {
        int menuSize = this.mMenu.size();
        if (menuSize != this.mButtons.length) {
            buildMenuView();
            return;
        }
        for (int i = 0; i < menuSize; i++) {
            this.mPresenter.setUpdateSuspended(true);
            if (this.mMenu.getItem(i).isChecked()) {
                this.mActiveButton = i;
            }
            this.mButtons[i].initialize((MenuItemImpl) this.mMenu.getItem(i), 0);
            this.mPresenter.setUpdateSuspended(false);
        }
    }

    private void activateNewButton(int newButton) {
        if (this.mActiveButton != newButton) {
            this.mAnimationHelper.beginDelayedTransition(this);
            this.mMenu.getItem(newButton).setChecked(true);
            this.mActiveButton = newButton;
        }
    }

    private BottomNavigationItemView getNewItem() {
        BottomNavigationItemView item = (BottomNavigationItemView) sItemPool.acquire();
        if (item == null) {
            return new BottomNavigationItemView(getContext());
        }
        return item;
    }
}
