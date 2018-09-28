package agency.tango.materialintroscreen;

import agency.tango.materialintroscreen.adapter.SlidesAdapter;
import agency.tango.materialintroscreen.animations.ViewTranslationWrapper;
import agency.tango.materialintroscreen.animations.wrappers.BackButtonTranslationWrapper;
import agency.tango.materialintroscreen.animations.wrappers.NextButtonTranslationWrapper;
import agency.tango.materialintroscreen.animations.wrappers.PageIndicatorTranslationWrapper;
import agency.tango.materialintroscreen.animations.wrappers.SkipButtonTranslationWrapper;
import agency.tango.materialintroscreen.animations.wrappers.ViewPagerTranslationWrapper;
import agency.tango.materialintroscreen.listeners.IFinishListener;
import agency.tango.materialintroscreen.listeners.IPageScrolledListener;
import agency.tango.materialintroscreen.listeners.IPageSelectedListener;
import agency.tango.materialintroscreen.listeners.MessageButtonBehaviourOnPageSelected;
import agency.tango.materialintroscreen.listeners.ViewBehavioursOnPageChangeListener;
import agency.tango.materialintroscreen.listeners.clickListeners.PermissionNotGrantedClickListener;
import agency.tango.materialintroscreen.listeners.scrollListeners.ParallaxScrollListener;
import agency.tango.materialintroscreen.widgets.InkPageIndicator;
import agency.tango.materialintroscreen.widgets.OverScrollViewPager;
import agency.tango.materialintroscreen.widgets.SwipeableViewPager;
import android.animation.ArgbEvaluator;
import android.content.res.ColorStateList;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.Snackbar.Callback;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import me.wangyuwei.loadingview.C0801R;

public abstract class MaterialIntroActivity extends AppCompatActivity {
    private SlidesAdapter adapter;
    private ArgbEvaluator argbEvaluator;
    private ImageButton backButton;
    private ViewTranslationWrapper backButtonTranslationWrapper;
    private CoordinatorLayout coordinatorLayout;
    private OnClickListener finishScreenClickListener;
    private Button messageButton;
    private MessageButtonBehaviourOnPageSelected messageButtonBehaviourOnPageSelected;
    private SparseArray<MessageButtonBehaviour> messageButtonBehaviours;
    private LinearLayout navigationView;
    private ImageButton nextButton;
    private ViewTranslationWrapper nextButtonTranslationWrapper;
    private OverScrollViewPager overScrollLayout;
    private InkPageIndicator pageIndicator;
    private ViewTranslationWrapper pageIndicatorTranslationWrapper;
    private OnClickListener permissionNotGrantedClickListener;
    private ImageButton skipButton;
    private ViewTranslationWrapper skipButtonTranslationWrapper;
    private SwipeableViewPager viewPager;
    private ViewTranslationWrapper viewPagerTranslationWrapper;

    /* renamed from: agency.tango.materialintroscreen.MaterialIntroActivity.1 */
    class C00001 implements Runnable {
        C00001() {
        }

        public void run() {
            if (MaterialIntroActivity.this.adapter.getCount() == 0) {
                MaterialIntroActivity.this.finish();
                return;
            }
            int currentItem = MaterialIntroActivity.this.viewPager.getCurrentItem();
            MaterialIntroActivity.this.messageButtonBehaviourOnPageSelected.pageSelected(currentItem);
            MaterialIntroActivity.this.nextButtonBehaviour(currentItem, MaterialIntroActivity.this.adapter.getItem(currentItem));
        }
    }

    /* renamed from: agency.tango.materialintroscreen.MaterialIntroActivity.2 */
    class C00012 implements OnClickListener {
        C00012() {
        }

        public void onClick(View v) {
            int position = MaterialIntroActivity.this.viewPager.getCurrentItem();
            while (position < MaterialIntroActivity.this.adapter.getCount()) {
                if (MaterialIntroActivity.this.adapter.getItem(position).canMoveFurther()) {
                    position++;
                } else {
                    MaterialIntroActivity.this.viewPager.setCurrentItem(position, true);
                    MaterialIntroActivity.this.showError(MaterialIntroActivity.this.adapter.getItem(position).cantMoveFurtherErrorMessage());
                    return;
                }
            }
            MaterialIntroActivity.this.viewPager.setCurrentItem(MaterialIntroActivity.this.adapter.getLastItemPosition(), true);
        }
    }

    /* renamed from: agency.tango.materialintroscreen.MaterialIntroActivity.3 */
    class C00023 implements OnClickListener {
        C00023() {
        }

        public void onClick(View v) {
            MaterialIntroActivity.this.viewPager.setCurrentItem(MaterialIntroActivity.this.viewPager.getPreviousItem(), true);
        }
    }

    /* renamed from: agency.tango.materialintroscreen.MaterialIntroActivity.7 */
    class C00047 implements OnClickListener {
        final /* synthetic */ SlideFragment val$fragment;

        C00047(SlideFragment slideFragment) {
            this.val$fragment = slideFragment;
        }

        public void onClick(View v) {
            if (this.val$fragment.canMoveFurther()) {
                MaterialIntroActivity.this.viewPager.moveToNextPage();
            } else {
                MaterialIntroActivity.this.errorOccurred(this.val$fragment);
            }
        }
    }

    private class FinishScreenClickListener implements OnClickListener {
        private FinishScreenClickListener() {
        }

        public void onClick(View v) {
            SlideFragment slideFragment = MaterialIntroActivity.this.adapter.getItem(MaterialIntroActivity.this.adapter.getLastItemPosition());
            if (slideFragment.canMoveFurther()) {
                MaterialIntroActivity.this.performFinish();
            } else {
                MaterialIntroActivity.this.errorOccurred(slideFragment);
            }
        }
    }

    /* renamed from: agency.tango.materialintroscreen.MaterialIntroActivity.4 */
    class C08224 implements IFinishListener {
        C08224() {
        }

        public void doOnFinish() {
            MaterialIntroActivity.this.performFinish();
        }
    }

    /* renamed from: agency.tango.materialintroscreen.MaterialIntroActivity.5 */
    class C08235 implements IPageSelectedListener {
        C08235() {
        }

        public void pageSelected(int position) {
            MaterialIntroActivity.this.nextButtonBehaviour(position, MaterialIntroActivity.this.adapter.getItem(position));
            if (MaterialIntroActivity.this.adapter.shouldFinish(position)) {
                MaterialIntroActivity.this.performFinish();
            }
        }
    }

    /* renamed from: agency.tango.materialintroscreen.MaterialIntroActivity.6 */
    class C08246 implements IPageScrolledListener {

        /* renamed from: agency.tango.materialintroscreen.MaterialIntroActivity.6.1 */
        class C00031 implements Runnable {
            final /* synthetic */ int val$position;

            C00031(int i) {
                this.val$position = i;
            }

            public void run() {
                if (MaterialIntroActivity.this.adapter.getItem(this.val$position).hasNeededPermissionsToGrant() || !MaterialIntroActivity.this.adapter.getItem(this.val$position).canMoveFurther()) {
                    MaterialIntroActivity.this.viewPager.setCurrentItem(this.val$position, true);
                    MaterialIntroActivity.this.pageIndicator.clearJoiningFractions();
                }
            }
        }

        C08246() {
        }

        public void pageScrolled(int position, float offset) {
            MaterialIntroActivity.this.viewPager.post(new C00031(position));
        }
    }

    private class ColorTransitionScrollListener implements IPageScrolledListener {
        private ColorTransitionScrollListener() {
        }

        public void pageScrolled(int position, float offset) {
            if (position < MaterialIntroActivity.this.adapter.getCount() - 1) {
                setViewsColor(position, offset);
            } else if (MaterialIntroActivity.this.adapter.getCount() == 1) {
                MaterialIntroActivity.this.viewPager.setBackgroundColor(MaterialIntroActivity.this.adapter.getItem(position).backgroundColor());
                MaterialIntroActivity.this.messageButton.setTextColor(MaterialIntroActivity.this.adapter.getItem(position).backgroundColor());
                tintButtons(ColorStateList.valueOf(MaterialIntroActivity.this.adapter.getItem(position).buttonsColor()));
            }
        }

        private void setViewsColor(int position, float offset) {
            int backgroundColor = MaterialIntroActivity.this.getBackgroundColor(position, offset).intValue();
            MaterialIntroActivity.this.viewPager.setBackgroundColor(backgroundColor);
            MaterialIntroActivity.this.messageButton.setTextColor(backgroundColor);
            int buttonsColor = MaterialIntroActivity.this.getButtonsColor(position, offset).intValue();
            if (VERSION.SDK_INT >= 21) {
                MaterialIntroActivity.this.getWindow().setStatusBarColor(buttonsColor);
            }
            MaterialIntroActivity.this.pageIndicator.setPageIndicatorColor(buttonsColor);
            tintButtons(ColorStateList.valueOf(buttonsColor));
        }

        private void tintButtons(ColorStateList color) {
            ViewCompat.setBackgroundTintList(MaterialIntroActivity.this.nextButton, color);
            ViewCompat.setBackgroundTintList(MaterialIntroActivity.this.backButton, color);
            ViewCompat.setBackgroundTintList(MaterialIntroActivity.this.skipButton, color);
        }
    }

    /* renamed from: agency.tango.materialintroscreen.MaterialIntroActivity.8 */
    class C12878 extends Callback {
        C12878() {
        }

        public void onDismissed(Snackbar snackbar, int event) {
            MaterialIntroActivity.this.navigationView.setTranslationY(0.0f);
            super.onDismissed(snackbar, event);
        }
    }

    public MaterialIntroActivity() {
        this.argbEvaluator = new ArgbEvaluator();
        this.messageButtonBehaviours = new SparseArray();
    }

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (VERSION.SDK_INT >= 19) {
            getWindow().setFlags(67108864, 67108864);
        }
        setContentView(C0005R.layout.activity_material_intro);
        this.overScrollLayout = (OverScrollViewPager) findViewById(C0005R.id.view_pager_slides);
        this.viewPager = this.overScrollLayout.getOverScrollView();
        this.pageIndicator = (InkPageIndicator) findViewById(C0005R.id.indicator);
        this.backButton = (ImageButton) findViewById(C0005R.id.button_back);
        this.nextButton = (ImageButton) findViewById(C0005R.id.button_next);
        this.skipButton = (ImageButton) findViewById(C0005R.id.button_skip);
        this.messageButton = (Button) findViewById(C0005R.id.button_message);
        this.coordinatorLayout = (CoordinatorLayout) findViewById(C0005R.id.coordinator_layout_slide);
        this.navigationView = (LinearLayout) findViewById(C0005R.id.navigation_view);
        this.adapter = new SlidesAdapter(getSupportFragmentManager());
        this.viewPager.setAdapter(this.adapter);
        this.viewPager.setOffscreenPageLimit(2);
        this.pageIndicator.setViewPager(this.viewPager);
        this.nextButtonTranslationWrapper = new NextButtonTranslationWrapper(this.nextButton);
        initOnPageChangeListeners();
        this.permissionNotGrantedClickListener = new PermissionNotGrantedClickListener(this, this.nextButtonTranslationWrapper);
        this.finishScreenClickListener = new FinishScreenClickListener();
        setBackButtonVisible();
        this.viewPager.post(new C00001());
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        SlideFragment fragment = this.adapter.getItem(this.viewPager.getCurrentItem());
        if (fragment.hasNeededPermissionsToGrant()) {
            showPermissionsNotGrantedError();
        } else {
            this.viewPager.setSwipingRightAllowed(true);
            nextButtonBehaviour(this.viewPager.getCurrentItem(), fragment);
            this.messageButtonBehaviourOnPageSelected.pageSelected(this.viewPager.getCurrentItem());
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void onBackPressed() {
        moveBack();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case C0801R.styleable.AppCompatTheme_actionBarWidgetTheme /*21*/:
                moveBack();
                break;
            case C0801R.styleable.Toolbar_collapseIcon /*22*/:
                int position = this.viewPager.getCurrentItem();
                if (!this.adapter.isLastSlide(position) || !this.adapter.getItem(position).canMoveFurther()) {
                    if (!this.adapter.shouldLockSlide(position)) {
                        this.viewPager.moveToNextPage();
                        break;
                    }
                    errorOccurred(this.adapter.getItem(position));
                    break;
                }
                performFinish();
                break;
            case C0801R.styleable.Toolbar_collapseContentDescription /*23*/:
                if (this.messageButtonBehaviours.get(this.viewPager.getCurrentItem()) != null) {
                    this.messageButton.performClick();
                    break;
                }
                break;
            default:
                return super.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }

    public void showPermissionsNotGrantedError() {
        showError(getString(C0005R.string.please_grant_permissions));
    }

    public void addSlide(SlideFragment slideFragment) {
        this.adapter.addItem(slideFragment);
    }

    public void addSlide(SlideFragment slideFragment, MessageButtonBehaviour messageButtonBehaviour) {
        this.adapter.addItem(slideFragment);
        this.messageButtonBehaviours.put(this.adapter.getLastItemPosition(), messageButtonBehaviour);
    }

    public void setSkipButtonVisible() {
        this.backButton.setVisibility(8);
        this.skipButton.setVisibility(0);
        this.skipButton.setOnClickListener(new C00012());
    }

    public void setBackButtonVisible() {
        this.skipButton.setVisibility(8);
        this.backButton.setVisibility(0);
        this.backButton.setOnClickListener(new C00023());
    }

    public void hideBackButton() {
        this.backButton.setVisibility(4);
        this.skipButton.setVisibility(8);
    }

    public ViewTranslationWrapper getNextButtonTranslationWrapper() {
        return this.nextButtonTranslationWrapper;
    }

    public ViewTranslationWrapper getBackButtonTranslationWrapper() {
        return this.backButtonTranslationWrapper;
    }

    public ViewTranslationWrapper getPageIndicatorTranslationWrapper() {
        return this.pageIndicatorTranslationWrapper;
    }

    public ViewTranslationWrapper getViewPagerTranslationWrapper() {
        return this.viewPagerTranslationWrapper;
    }

    public ViewTranslationWrapper getSkipButtonTranslationWrapper() {
        return this.skipButtonTranslationWrapper;
    }

    public void enableLastSlideAlphaExitTransition(boolean enableAlphaExitTransition) {
        this.viewPager.alphaExitTransitionEnabled(enableAlphaExitTransition);
    }

    public void showMessage(String message) {
        showError(message);
    }

    public void onFinish() {
    }

    private void initOnPageChangeListeners() {
        this.messageButtonBehaviourOnPageSelected = new MessageButtonBehaviourOnPageSelected(this.messageButton, this.adapter, this.messageButtonBehaviours);
        this.backButtonTranslationWrapper = new BackButtonTranslationWrapper(this.backButton);
        this.pageIndicatorTranslationWrapper = new PageIndicatorTranslationWrapper(this.pageIndicator);
        this.viewPagerTranslationWrapper = new ViewPagerTranslationWrapper(this.viewPager);
        this.skipButtonTranslationWrapper = new SkipButtonTranslationWrapper(this.skipButton);
        this.overScrollLayout.registerFinishListener(new C08224());
        this.viewPager.addOnPageChangeListener(new ViewBehavioursOnPageChangeListener(this.adapter).registerViewTranslationWrapper(this.nextButtonTranslationWrapper).registerViewTranslationWrapper(this.backButtonTranslationWrapper).registerViewTranslationWrapper(this.pageIndicatorTranslationWrapper).registerViewTranslationWrapper(this.viewPagerTranslationWrapper).registerViewTranslationWrapper(this.skipButtonTranslationWrapper).registerOnPageScrolled(new C08246()).registerOnPageScrolled(new ColorTransitionScrollListener()).registerOnPageScrolled(new ParallaxScrollListener(this.adapter)).registerPageSelectedListener(this.messageButtonBehaviourOnPageSelected).registerPageSelectedListener(new C08235()));
    }

    private void nextButtonBehaviour(int position, SlideFragment fragment) {
        if (fragment.hasNeededPermissionsToGrant()) {
            this.nextButton.setImageDrawable(ContextCompat.getDrawable(this, C0005R.drawable.ic_next));
            this.nextButton.setOnClickListener(this.permissionNotGrantedClickListener);
        } else if (this.adapter.isLastSlide(position)) {
            this.nextButton.setImageDrawable(ContextCompat.getDrawable(this, C0005R.drawable.ic_finish));
            this.nextButton.setOnClickListener(this.finishScreenClickListener);
        } else {
            this.nextButton.setImageDrawable(ContextCompat.getDrawable(this, C0005R.drawable.ic_next));
            this.nextButton.setOnClickListener(new C00047(fragment));
        }
    }

    private void performFinish() {
        onFinish();
        finish();
    }

    private void moveBack() {
        if (this.viewPager.getCurrentItem() == 0) {
            finish();
        } else {
            this.viewPager.setCurrentItem(this.viewPager.getPreviousItem(), true);
        }
    }

    private void errorOccurred(SlideFragment slideFragment) {
        this.nextButtonTranslationWrapper.error();
        showError(slideFragment.cantMoveFurtherErrorMessage());
    }

    private void showError(String error) {
        Snackbar.make(this.coordinatorLayout, (CharSequence) error, -1).setCallback(new C12878()).show();
    }

    private Integer getBackgroundColor(int position, float positionOffset) {
        return (Integer) this.argbEvaluator.evaluate(positionOffset, Integer.valueOf(color(this.adapter.getItem(position).backgroundColor())), Integer.valueOf(color(this.adapter.getItem(position + 1).backgroundColor())));
    }

    private Integer getButtonsColor(int position, float positionOffset) {
        return (Integer) this.argbEvaluator.evaluate(positionOffset, Integer.valueOf(color(this.adapter.getItem(position).buttonsColor())), Integer.valueOf(color(this.adapter.getItem(position + 1).buttonsColor())));
    }

    private int color(@ColorRes int color) {
        return ContextCompat.getColor(this, color);
    }
}
