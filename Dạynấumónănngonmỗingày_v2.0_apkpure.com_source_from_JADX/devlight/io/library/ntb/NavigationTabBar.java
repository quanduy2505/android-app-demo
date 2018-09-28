package devlight.io.library.ntb;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.BaseSavedState;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;
import com.gigamole.navigationtabbar.C0597R;
import devlight.io.library.behavior.NavigationTabBarBehavior;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import rx.android.BuildConfig;
import rx.internal.operators.OnSubscribeConcatMap;

public class NavigationTabBar extends View implements OnPageChangeListener {
    protected static final Interpolator ACCELERATE_INTERPOLATOR;
    public static final int AUTO_COLOR = -3;
    public static final int AUTO_SCALE = -4;
    public static final int AUTO_SIZE = -2;
    protected static final float BADGE_HORIZONTAL_FRACTION = 0.5f;
    protected static final float BADGE_TITLE_SIZE_FRACTION = 0.9f;
    protected static final float BADGE_VERTICAL_FRACTION = 0.75f;
    protected static final float CENTER_FRACTION = 0.5f;
    protected static final Interpolator DECELERATE_INTERPOLATOR;
    protected static final int DEFAULT_ACTIVE_COLOR = -1;
    protected static final int DEFAULT_ANIMATION_DURATION = 300;
    protected static final int DEFAULT_BADGE_ANIMATION_DURATION = 200;
    protected static final int DEFAULT_BADGE_REFRESH_ANIMATION_DURATION = 100;
    protected static final int DEFAULT_BG_COLOR;
    protected static final float DEFAULT_ICON_SIZE_FRACTION = 0.5f;
    protected static final int DEFAULT_INACTIVE_COLOR;
    protected static final float DEFAULT_TITLE_ICON_SIZE_FRACTION = 0.5f;
    protected static final int FLAGS = 7;
    protected static final int INVALID_INDEX = -1;
    protected static final float LEFT_FRACTION = 0.25f;
    protected static final int MAX_ALPHA = 255;
    protected static final float MAX_FRACTION = 1.0f;
    protected static final int MIN_ALPHA = 0;
    protected static final float MIN_FRACTION = 0.0f;
    protected static final Interpolator OUT_SLOW_IN_INTERPOLATOR;
    protected static final String PREVIEW_BADGE = "0";
    protected static final String PREVIEW_TITLE = "Title";
    protected static final float RIGHT_FRACTION = 0.75f;
    protected static final float SCALED_FRACTION = 0.3f;
    protected static final float TITLE_ACTIVE_ICON_SCALE_BY = 0.2f;
    protected static final float TITLE_ACTIVE_SCALE_BY = 0.2f;
    protected static final float TITLE_MARGIN_FRACTION = 0.15f;
    protected static final float TITLE_MARGIN_SCALE_FRACTION = 0.25f;
    protected static final float TITLE_SIZE_FRACTION = 0.2f;
    protected int mActiveColor;
    protected boolean mAnimateHide;
    protected int mAnimationDuration;
    protected final ValueAnimator mAnimator;
    protected AnimatorListener mAnimatorListener;
    protected int mBadgeBgColor;
    protected final Rect mBadgeBounds;
    protected BadgeGravity mBadgeGravity;
    protected float mBadgeMargin;
    protected final Paint mBadgePaint;
    protected BadgePosition mBadgePosition;
    protected int mBadgeTitleColor;
    protected float mBadgeTitleSize;
    protected NavigationTabBarBehavior mBehavior;
    protected boolean mBehaviorEnabled;
    protected final RectF mBgBadgeBounds;
    protected final RectF mBgBounds;
    protected int mBgColor;
    protected final Paint mBgPaint;
    protected Bitmap mBitmap;
    protected final RectF mBounds;
    protected final Canvas mCanvas;
    protected float mCornersRadius;
    protected float mEndPointerX;
    protected float mFraction;
    protected final Paint mIconPaint;
    protected final Paint mIconPointerPaint;
    protected float mIconSize;
    protected float mIconSizeFraction;
    protected Bitmap mIconsBitmap;
    protected final Canvas mIconsCanvas;
    protected int mInactiveColor;
    protected int mIndex;
    protected boolean mIsActionDown;
    protected boolean mIsBadgeUseTypeface;
    protected boolean mIsBadged;
    protected boolean mIsBehaviorSet;
    protected boolean mIsHorizontalOrientation;
    protected boolean mIsPointerActionDown;
    protected boolean mIsResizeIn;
    protected boolean mIsScaled;
    protected boolean mIsSetIndexFromTabBar;
    protected boolean mIsSwiped;
    protected boolean mIsTinted;
    protected boolean mIsTitled;
    protected boolean mIsViewPagerMode;
    protected int mLastIndex;
    protected float mModelSize;
    protected final Paint mModelTitlePaint;
    protected float mModelTitleSize;
    protected final List<Model> mModels;
    protected boolean mNeedHide;
    protected OnPageChangeListener mOnPageChangeListener;
    protected OnTabBarSelectedIndexListener mOnTabBarSelectedIndexListener;
    protected final Paint mPaint;
    protected Bitmap mPointerBitmap;
    protected final RectF mPointerBounds;
    protected final Canvas mPointerCanvas;
    protected float mPointerLeftTop;
    protected final Paint mPointerPaint;
    protected float mPointerRightBottom;
    protected final ResizeInterpolator mResizeInterpolator;
    protected int mScrollState;
    protected final Paint mSelectedIconPaint;
    protected float mStartPointerX;
    protected float mTitleMargin;
    protected TitleMode mTitleMode;
    protected Bitmap mTitlesBitmap;
    protected final Canvas mTitlesCanvas;
    protected Typeface mTypeface;
    protected ViewPager mViewPager;

    /* renamed from: devlight.io.library.ntb.NavigationTabBar.10 */
    class AnonymousClass10 implements Runnable {
        final /* synthetic */ int val$tempIndex;

        AnonymousClass10(int i) {
            this.val$tempIndex = i;
        }

        public void run() {
            NavigationTabBar.this.setModelIndex(this.val$tempIndex, true);
        }
    }

    /* renamed from: devlight.io.library.ntb.NavigationTabBar.1 */
    class C07881 extends Paint {
        C07881(int x0) {
            super(x0);
            setStyle(Style.FILL);
        }
    }

    /* renamed from: devlight.io.library.ntb.NavigationTabBar.2 */
    class C07892 extends Paint {
        C07892(int x0) {
            super(x0);
            setStyle(Style.FILL);
        }
    }

    /* renamed from: devlight.io.library.ntb.NavigationTabBar.3 */
    class C07903 extends Paint {
        C07903(int x0) {
            super(x0);
            setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        }
    }

    /* renamed from: devlight.io.library.ntb.NavigationTabBar.4 */
    class C07914 extends Paint {
        C07914(int x0) {
            super(x0);
            setStyle(Style.FILL);
            setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        }
    }

    /* renamed from: devlight.io.library.ntb.NavigationTabBar.5 */
    class C07925 extends TextPaint {
        C07925(int x0) {
            super(x0);
            setColor(NavigationTabBar.INVALID_INDEX);
            setTextAlign(Align.CENTER);
        }
    }

    /* renamed from: devlight.io.library.ntb.NavigationTabBar.6 */
    class C07936 extends TextPaint {
        C07936(int x0) {
            super(x0);
            setTextAlign(Align.CENTER);
            setFakeBoldText(true);
        }
    }

    /* renamed from: devlight.io.library.ntb.NavigationTabBar.7 */
    class C07947 implements AnimatorUpdateListener {
        C07947() {
        }

        public void onAnimationUpdate(ValueAnimator animation) {
            NavigationTabBar.this.updateIndicatorPosition(((Float) animation.getAnimatedValue()).floatValue());
        }
    }

    /* renamed from: devlight.io.library.ntb.NavigationTabBar.8 */
    class C07958 implements AnimatorUpdateListener {
        final /* synthetic */ Model val$model;

        C07958(Model model) {
            this.val$model = model;
        }

        public void onAnimationUpdate(ValueAnimator animation) {
            this.val$model.mBadgeFraction = ((Float) animation.getAnimatedValue()).floatValue();
            NavigationTabBar.this.postInvalidate();
        }
    }

    /* renamed from: devlight.io.library.ntb.NavigationTabBar.9 */
    class C07969 extends AnimatorListenerAdapter {
        C07969() {
        }

        public void onAnimationStart(Animator animation) {
            if (NavigationTabBar.this.mOnTabBarSelectedIndexListener != null) {
                NavigationTabBar.this.mOnTabBarSelectedIndexListener.onStartTabSelected((Model) NavigationTabBar.this.mModels.get(NavigationTabBar.this.mIndex), NavigationTabBar.this.mIndex);
            }
            animation.removeListener(this);
            animation.addListener(this);
        }

        public void onAnimationEnd(Animator animation) {
            if (!NavigationTabBar.this.mIsViewPagerMode) {
                animation.removeListener(this);
                animation.addListener(this);
                if (NavigationTabBar.this.mOnTabBarSelectedIndexListener != null) {
                    NavigationTabBar.this.mOnTabBarSelectedIndexListener.onEndTabSelected((Model) NavigationTabBar.this.mModels.get(NavigationTabBar.this.mIndex), NavigationTabBar.this.mIndex);
                }
            }
        }
    }

    public enum BadgeGravity {
        TOP,
        BOTTOM;
        
        public static final int BOTTOM_INDEX = 1;
        public static final int TOP_INDEX = 0;
    }

    public enum BadgePosition {
        LEFT(NavigationTabBar.TITLE_MARGIN_SCALE_FRACTION),
        CENTER(NavigationTabBar.DEFAULT_TITLE_ICON_SIZE_FRACTION),
        RIGHT(NavigationTabBar.RIGHT_FRACTION);
        
        public static final int CENTER_INDEX = 1;
        public static final int LEFT_INDEX = 0;
        public static final int RIGHT_INDEX = 2;
        private final float mPositionFraction;

        private BadgePosition(float positionFraction) {
            this.mPositionFraction = positionFraction;
        }
    }

    public static class Model {
        private float mActiveIconScaleBy;
        private final ValueAnimator mBadgeAnimator;
        private float mBadgeFraction;
        private String mBadgeTitle;
        private int mColor;
        private final Bitmap mIcon;
        private final Matrix mIconMatrix;
        private float mInactiveIconScale;
        private boolean mIsBadgeShowed;
        private boolean mIsBadgeUpdated;
        private final Bitmap mSelectedIcon;
        private String mTempBadgeTitle;
        private String mTitle;

        /* renamed from: devlight.io.library.ntb.NavigationTabBar.Model.1 */
        class C07971 extends AnimatorListenerAdapter {
            C07971() {
            }

            public void onAnimationStart(Animator animation) {
                animation.removeListener(this);
                animation.addListener(this);
            }

            public void onAnimationEnd(Animator animation) {
                boolean z = false;
                animation.removeListener(this);
                animation.addListener(this);
                if (Model.this.mIsBadgeUpdated) {
                    Model.this.mIsBadgeUpdated = false;
                    return;
                }
                Model model = Model.this;
                if (!Model.this.mIsBadgeShowed) {
                    z = true;
                }
                model.mIsBadgeShowed = z;
            }

            public void onAnimationRepeat(Animator animation) {
                if (Model.this.mIsBadgeUpdated) {
                    Model.this.mBadgeTitle = Model.this.mTempBadgeTitle;
                }
            }
        }

        public static class Builder {
            private String mBadgeTitle;
            private final int mColor;
            private final Bitmap mIcon;
            private Bitmap mSelectedIcon;
            private String mTitle;

            public Builder(Drawable icon, int color) {
                this.mColor = color;
                if (icon == null) {
                    this.mIcon = Bitmap.createBitmap(1, 1, Config.RGB_565);
                } else if (icon instanceof BitmapDrawable) {
                    this.mIcon = ((BitmapDrawable) icon).getBitmap();
                } else {
                    this.mIcon = Bitmap.createBitmap(icon.getIntrinsicWidth(), icon.getIntrinsicHeight(), Config.ARGB_8888);
                    Canvas canvas = new Canvas(this.mIcon);
                    icon.setBounds(NavigationTabBar.MIN_ALPHA, NavigationTabBar.MIN_ALPHA, canvas.getWidth(), canvas.getHeight());
                    icon.draw(canvas);
                }
            }

            public Builder selectedIcon(Drawable selectedIcon) {
                if (selectedIcon == null) {
                    this.mSelectedIcon = null;
                } else if (selectedIcon instanceof BitmapDrawable) {
                    this.mSelectedIcon = ((BitmapDrawable) selectedIcon).getBitmap();
                } else {
                    this.mSelectedIcon = Bitmap.createBitmap(selectedIcon.getIntrinsicWidth(), selectedIcon.getIntrinsicHeight(), Config.ARGB_8888);
                    Canvas canvas = new Canvas(this.mSelectedIcon);
                    selectedIcon.setBounds(NavigationTabBar.MIN_ALPHA, NavigationTabBar.MIN_ALPHA, canvas.getWidth(), canvas.getHeight());
                    selectedIcon.draw(canvas);
                }
                return this;
            }

            public Builder title(String title) {
                this.mTitle = title;
                return this;
            }

            public Builder badgeTitle(String title) {
                this.mBadgeTitle = title;
                return this;
            }

            public Model build() {
                return new Model(this);
            }
        }

        Model(Builder builder) {
            this.mIconMatrix = new Matrix();
            this.mTitle = BuildConfig.VERSION_NAME;
            this.mBadgeTitle = BuildConfig.VERSION_NAME;
            this.mTempBadgeTitle = BuildConfig.VERSION_NAME;
            this.mBadgeAnimator = new ValueAnimator();
            this.mColor = builder.mColor;
            this.mIcon = builder.mIcon;
            this.mSelectedIcon = builder.mSelectedIcon;
            this.mTitle = builder.mTitle;
            this.mBadgeTitle = builder.mBadgeTitle;
            this.mBadgeAnimator.addListener(new C07971());
        }

        public String getTitle() {
            return this.mTitle;
        }

        public void setTitle(String title) {
            this.mTitle = title;
        }

        public int getColor() {
            return this.mColor;
        }

        public void setColor(int color) {
            this.mColor = color;
        }

        public boolean isBadgeShowed() {
            return this.mIsBadgeShowed;
        }

        public String getBadgeTitle() {
            return this.mBadgeTitle;
        }

        public void setBadgeTitle(String badgeTitle) {
            this.mBadgeTitle = badgeTitle;
        }

        public void updateBadgeTitle(String badgeTitle) {
            if (this.mIsBadgeShowed) {
                if (this.mBadgeAnimator.isRunning()) {
                    this.mBadgeAnimator.end();
                }
                this.mTempBadgeTitle = badgeTitle;
                this.mIsBadgeUpdated = true;
                this.mBadgeAnimator.setFloatValues(new float[]{NavigationTabBar.MAX_FRACTION, NavigationTabBar.MIN_FRACTION});
                this.mBadgeAnimator.setDuration(100);
                this.mBadgeAnimator.setRepeatMode(2);
                this.mBadgeAnimator.setRepeatCount(1);
                this.mBadgeAnimator.start();
            }
        }

        public void toggleBadge() {
            if (this.mBadgeAnimator.isRunning()) {
                this.mBadgeAnimator.end();
            }
            if (this.mIsBadgeShowed) {
                hideBadge();
            } else {
                showBadge();
            }
        }

        public void showBadge() {
            this.mIsBadgeUpdated = false;
            if (this.mBadgeAnimator.isRunning()) {
                this.mBadgeAnimator.end();
            }
            if (!this.mIsBadgeShowed) {
                this.mBadgeAnimator.setFloatValues(new float[]{NavigationTabBar.MIN_FRACTION, NavigationTabBar.MAX_FRACTION});
                this.mBadgeAnimator.setInterpolator(NavigationTabBar.DECELERATE_INTERPOLATOR);
                this.mBadgeAnimator.setDuration(200);
                this.mBadgeAnimator.setRepeatMode(1);
                this.mBadgeAnimator.setRepeatCount(NavigationTabBar.MIN_ALPHA);
                this.mBadgeAnimator.start();
            }
        }

        public void hideBadge() {
            this.mIsBadgeUpdated = false;
            if (this.mBadgeAnimator.isRunning()) {
                this.mBadgeAnimator.end();
            }
            if (this.mIsBadgeShowed) {
                this.mBadgeAnimator.setFloatValues(new float[]{NavigationTabBar.MAX_FRACTION, NavigationTabBar.MIN_FRACTION});
                this.mBadgeAnimator.setInterpolator(NavigationTabBar.ACCELERATE_INTERPOLATOR);
                this.mBadgeAnimator.setDuration(200);
                this.mBadgeAnimator.setRepeatMode(1);
                this.mBadgeAnimator.setRepeatCount(NavigationTabBar.MIN_ALPHA);
                this.mBadgeAnimator.start();
            }
        }
    }

    public interface OnTabBarSelectedIndexListener {
        void onEndTabSelected(Model model, int i);

        void onStartTabSelected(Model model, int i);
    }

    protected class ResizeInterpolator implements Interpolator {
        private static final float FACTOR = 1.0f;
        private boolean mResizeIn;

        protected ResizeInterpolator() {
        }

        public float getInterpolation(float input) {
            if (this.mResizeIn) {
                return (float) (1.0d - Math.pow((double) (FACTOR - input), 2.0d));
            }
            return (float) Math.pow((double) input, 2.0d);
        }

        private float getResizeInterpolation(float input, boolean resizeIn) {
            this.mResizeIn = resizeIn;
            return getInterpolation(input);
        }
    }

    protected class ResizeViewPagerScroller extends Scroller {
        ResizeViewPagerScroller(Context context) {
            super(context, new AccelerateDecelerateInterpolator());
        }

        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, NavigationTabBar.this.mAnimationDuration);
        }

        public void startScroll(int startX, int startY, int dx, int dy) {
            super.startScroll(startX, startY, dx, dy, NavigationTabBar.this.mAnimationDuration);
        }
    }

    protected static class SavedState extends BaseSavedState {
        public static final Creator<SavedState> CREATOR;
        private int index;

        /* renamed from: devlight.io.library.ntb.NavigationTabBar.SavedState.1 */
        static class C07981 implements Creator<SavedState> {
            C07981() {
            }

            public SavedState createFromParcel(Parcel in) {
                return new SavedState(null);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        }

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            this.index = in.readInt();
        }

        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(this.index);
        }

        static {
            CREATOR = new C07981();
        }
    }

    public enum TitleMode {
        ALL,
        ACTIVE;
        
        public static final int ACTIVE_INDEX = 1;
        public static final int ALL_INDEX = 0;
    }

    static {
        DEFAULT_INACTIVE_COLOR = Color.parseColor("#9f90af");
        DEFAULT_BG_COLOR = Color.parseColor("#605271");
        DECELERATE_INTERPOLATOR = new DecelerateInterpolator();
        ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
        OUT_SLOW_IN_INTERPOLATOR = new LinearOutSlowInInterpolator();
    }

    public NavigationTabBar(Context context) {
        this(context, null);
    }

    public NavigationTabBar(Context context, AttributeSet attrs) {
        this(context, attrs, MIN_ALPHA);
    }

    public NavigationTabBar(Context context, AttributeSet attrs, int defStyleAttr) {
        String[] previewColors;
        int len$;
        int i$;
        super(context, attrs, defStyleAttr);
        this.mBounds = new RectF();
        this.mBgBounds = new RectF();
        this.mPointerBounds = new RectF();
        this.mBadgeBounds = new Rect();
        this.mBgBadgeBounds = new RectF();
        this.mCanvas = new Canvas();
        this.mIconsCanvas = new Canvas();
        this.mTitlesCanvas = new Canvas();
        this.mPointerCanvas = new Canvas();
        this.mPaint = new C07881(FLAGS);
        this.mBgPaint = new C07892(FLAGS);
        this.mPointerPaint = new C07903(FLAGS);
        this.mIconPaint = new Paint(FLAGS);
        this.mSelectedIconPaint = new Paint(FLAGS);
        this.mIconPointerPaint = new C07914(FLAGS);
        this.mModelTitlePaint = new C07925(FLAGS);
        this.mBadgePaint = new C07936(FLAGS);
        this.mAnimator = new ValueAnimator();
        this.mResizeInterpolator = new ResizeInterpolator();
        this.mModels = new ArrayList();
        this.mModelTitleSize = -2.0f;
        this.mBadgeTitleSize = -2.0f;
        this.mBadgeTitleColor = AUTO_COLOR;
        this.mBadgeBgColor = AUTO_COLOR;
        this.mLastIndex = INVALID_INDEX;
        this.mIndex = INVALID_INDEX;
        setWillNotDraw(false);
        ViewCompat.setLayerType(this, 1, null);
        setLayerType(1, null);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, C0597R.styleable.NavigationTabBar);
        String[] arr$;
        try {
            setIsTitled(typedArray.getBoolean(C0597R.styleable.NavigationTabBar_ntb_titled, false));
            setIsBadged(typedArray.getBoolean(C0597R.styleable.NavigationTabBar_ntb_badged, false));
            setIsScaled(typedArray.getBoolean(C0597R.styleable.NavigationTabBar_ntb_scaled, true));
            setIsTinted(typedArray.getBoolean(C0597R.styleable.NavigationTabBar_ntb_tinted, true));
            setIsSwiped(typedArray.getBoolean(C0597R.styleable.NavigationTabBar_ntb_swiped, true));
            setTitleSize(typedArray.getDimension(C0597R.styleable.NavigationTabBar_ntb_title_size, -2.0f));
            setIsBadgeUseTypeface(typedArray.getBoolean(C0597R.styleable.NavigationTabBar_ntb_badge_use_typeface, false));
            setTitleMode(typedArray.getInt(C0597R.styleable.NavigationTabBar_ntb_title_mode, MIN_ALPHA));
            setBadgeSize(typedArray.getDimension(C0597R.styleable.NavigationTabBar_ntb_badge_size, -2.0f));
            setBadgePosition(typedArray.getInt(C0597R.styleable.NavigationTabBar_ntb_badge_position, 2));
            setBadgeGravity(typedArray.getInt(C0597R.styleable.NavigationTabBar_ntb_badge_gravity, MIN_ALPHA));
            setBadgeBgColor(typedArray.getColor(C0597R.styleable.NavigationTabBar_ntb_badge_bg_color, AUTO_COLOR));
            setBadgeTitleColor(typedArray.getColor(C0597R.styleable.NavigationTabBar_ntb_badge_title_color, AUTO_COLOR));
            setTypeface(typedArray.getString(C0597R.styleable.NavigationTabBar_ntb_typeface));
            setInactiveColor(typedArray.getColor(C0597R.styleable.NavigationTabBar_ntb_inactive_color, DEFAULT_INACTIVE_COLOR));
            setActiveColor(typedArray.getColor(C0597R.styleable.NavigationTabBar_ntb_active_color, INVALID_INDEX));
            setBgColor(typedArray.getColor(C0597R.styleable.NavigationTabBar_ntb_bg_color, DEFAULT_BG_COLOR));
            setAnimationDuration(typedArray.getInteger(C0597R.styleable.NavigationTabBar_ntb_animation_duration, DEFAULT_ANIMATION_DURATION));
            setCornersRadius(typedArray.getDimension(C0597R.styleable.NavigationTabBar_ntb_corners_radius, MIN_FRACTION));
            setIconSizeFraction(typedArray.getFloat(C0597R.styleable.NavigationTabBar_ntb_icon_size_fraction, -4.0f));
            this.mAnimator.setFloatValues(new float[]{MIN_FRACTION, MAX_FRACTION});
            this.mAnimator.setInterpolator(new LinearInterpolator());
            this.mAnimator.addUpdateListener(new C07947());
            if (isInEditMode()) {
                int previewColorsId = typedArray.getResourceId(C0597R.styleable.NavigationTabBar_ntb_preview_colors, MIN_ALPHA);
                if (previewColorsId == 0) {
                    previewColors = null;
                } else {
                    previewColors = typedArray.getResources().getStringArray(previewColorsId);
                }
                if (previewColors == null) {
                    previewColors = typedArray.getResources().getStringArray(C0597R.array.default_preview);
                }
                arr$ = previewColors;
                len$ = arr$.length;
                for (i$ = MIN_ALPHA; i$ < len$; i$++) {
                    this.mModels.add(new Builder(null, Color.parseColor(arr$[i$])).build());
                }
                requestLayout();
            }
        } catch (Exception exception) {
            previewColors = null;
            exception.printStackTrace();
            if (MIN_ALPHA == null) {
                previewColors = typedArray.getResources().getStringArray(C0597R.array.default_preview);
            }
            arr$ = previewColors;
            len$ = arr$.length;
            for (i$ = MIN_ALPHA; i$ < len$; i$++) {
                this.mModels.add(new Builder(null, Color.parseColor(arr$[i$])).build());
            }
            requestLayout();
        } catch (Throwable th) {
            typedArray.recycle();
        }
        typedArray.recycle();
    }

    public int getAnimationDuration() {
        return this.mAnimationDuration;
    }

    public void setAnimationDuration(int animationDuration) {
        this.mAnimationDuration = animationDuration;
        this.mAnimator.setDuration((long) this.mAnimationDuration);
        resetScroller();
    }

    public List<Model> getModels() {
        return this.mModels;
    }

    public void setModels(List<Model> models) {
        for (Model model : models) {
            model.mBadgeAnimator.removeAllUpdateListeners();
            model.mBadgeAnimator.addUpdateListener(new C07958(model));
        }
        this.mModels.clear();
        this.mModels.addAll(models);
        requestLayout();
    }

    public boolean isTitled() {
        return this.mIsTitled;
    }

    public void setIsTitled(boolean isTitled) {
        this.mIsTitled = isTitled;
        requestLayout();
    }

    public boolean isBadged() {
        return this.mIsBadged;
    }

    public void setIsBadged(boolean isBadged) {
        this.mIsBadged = isBadged;
        requestLayout();
    }

    public boolean isScaled() {
        return this.mIsScaled;
    }

    public void setIsScaled(boolean isScaled) {
        this.mIsScaled = isScaled;
        requestLayout();
    }

    public boolean isTinted() {
        return this.mIsTinted;
    }

    public void setIsTinted(boolean isTinted) {
        this.mIsTinted = isTinted;
        updateTint();
    }

    public boolean isSwiped() {
        return this.mIsSwiped;
    }

    public void setIsSwiped(boolean swiped) {
        this.mIsSwiped = swiped;
    }

    public float getTitleSize() {
        return this.mModelTitleSize;
    }

    public void setTitleSize(float modelTitleSize) {
        this.mModelTitleSize = modelTitleSize;
        if (modelTitleSize == -2.0f) {
            requestLayout();
        }
    }

    public boolean isBadgeUseTypeface() {
        return this.mIsBadgeUseTypeface;
    }

    public void setIsBadgeUseTypeface(boolean isBadgeUseTypeface) {
        this.mIsBadgeUseTypeface = isBadgeUseTypeface;
        setBadgeTypeface();
        postInvalidate();
    }

    public TitleMode getTitleMode() {
        return this.mTitleMode;
    }

    protected void setTitleMode(int index) {
        switch (index) {
            case OnSubscribeConcatMap.BOUNDARY /*1*/:
                setTitleMode(TitleMode.ACTIVE);
            default:
                setTitleMode(TitleMode.ALL);
        }
    }

    public void setTitleMode(TitleMode titleMode) {
        this.mTitleMode = titleMode;
        postInvalidate();
    }

    public BadgePosition getBadgePosition() {
        return this.mBadgePosition;
    }

    protected void setBadgePosition(int index) {
        switch (index) {
            case MIN_ALPHA /*0*/:
                setBadgePosition(BadgePosition.LEFT);
            case OnSubscribeConcatMap.BOUNDARY /*1*/:
                setBadgePosition(BadgePosition.CENTER);
            default:
                setBadgePosition(BadgePosition.RIGHT);
        }
    }

    public void setBadgePosition(BadgePosition badgePosition) {
        this.mBadgePosition = badgePosition;
        postInvalidate();
    }

    public BadgeGravity getBadgeGravity() {
        return this.mBadgeGravity;
    }

    protected void setBadgeGravity(int index) {
        switch (index) {
            case OnSubscribeConcatMap.BOUNDARY /*1*/:
                setBadgeGravity(BadgeGravity.BOTTOM);
            default:
                setBadgeGravity(BadgeGravity.TOP);
        }
    }

    public void setBadgeGravity(BadgeGravity badgeGravity) {
        this.mBadgeGravity = badgeGravity;
        requestLayout();
    }

    public int getBadgeBgColor() {
        return this.mBadgeBgColor;
    }

    public void setBadgeBgColor(int badgeBgColor) {
        this.mBadgeBgColor = badgeBgColor;
    }

    public int getBadgeTitleColor() {
        return this.mBadgeTitleColor;
    }

    public void setBadgeTitleColor(int badgeTitleColor) {
        this.mBadgeTitleColor = badgeTitleColor;
    }

    public float getBadgeSize() {
        return this.mBadgeTitleSize;
    }

    public void setBadgeSize(float badgeTitleSize) {
        this.mBadgeTitleSize = badgeTitleSize;
        if (this.mBadgeTitleSize == -2.0f) {
            requestLayout();
        }
    }

    public Typeface getTypeface() {
        return this.mTypeface;
    }

    public void setTypeface(String typeface) {
        if (!TextUtils.isEmpty(typeface)) {
            Typeface tempTypeface;
            try {
                tempTypeface = Typeface.createFromAsset(getContext().getAssets(), typeface);
            } catch (Exception e) {
                tempTypeface = Typeface.create(Typeface.DEFAULT, MIN_ALPHA);
                e.printStackTrace();
            }
            setTypeface(tempTypeface);
        }
    }

    public void setTypeface(Typeface typeface) {
        this.mTypeface = typeface;
        this.mModelTitlePaint.setTypeface(typeface);
        setBadgeTypeface();
        postInvalidate();
    }

    protected void setBadgeTypeface() {
        this.mBadgePaint.setTypeface(this.mIsBadgeUseTypeface ? this.mTypeface : Typeface.create(Typeface.DEFAULT, MIN_ALPHA));
    }

    public int getActiveColor() {
        return this.mActiveColor;
    }

    public void setActiveColor(int activeColor) {
        this.mActiveColor = activeColor;
        this.mIconPointerPaint.setColor(this.mActiveColor);
        updateTint();
    }

    public int getInactiveColor() {
        return this.mInactiveColor;
    }

    public void setInactiveColor(int inactiveColor) {
        this.mInactiveColor = inactiveColor;
        this.mModelTitlePaint.setColor(this.mInactiveColor);
        updateTint();
    }

    public int getBgColor() {
        return this.mBgColor;
    }

    public void setBgColor(int bgColor) {
        this.mBgColor = bgColor;
        this.mBgPaint.setColor(this.mBgColor);
        postInvalidate();
    }

    public float getCornersRadius() {
        return this.mCornersRadius;
    }

    public void setCornersRadius(float cornersRadius) {
        this.mCornersRadius = cornersRadius;
        postInvalidate();
    }

    public float getIconSizeFraction() {
        return this.mIconSizeFraction;
    }

    public void setIconSizeFraction(float iconSizeFraction) {
        this.mIconSizeFraction = iconSizeFraction;
        requestLayout();
    }

    public float getBadgeMargin() {
        return this.mBadgeMargin;
    }

    public float getBarHeight() {
        return this.mBounds.height();
    }

    public OnTabBarSelectedIndexListener getOnTabBarSelectedIndexListener() {
        return this.mOnTabBarSelectedIndexListener;
    }

    public void setOnTabBarSelectedIndexListener(OnTabBarSelectedIndexListener onTabBarSelectedIndexListener) {
        this.mOnTabBarSelectedIndexListener = onTabBarSelectedIndexListener;
        if (this.mAnimatorListener == null) {
            this.mAnimatorListener = new C07969();
        }
        this.mAnimator.removeListener(this.mAnimatorListener);
        this.mAnimator.addListener(this.mAnimatorListener);
    }

    public void setViewPager(ViewPager viewPager) {
        if (viewPager == null) {
            this.mIsViewPagerMode = false;
        } else if (!viewPager.equals(this.mViewPager)) {
            if (this.mViewPager != null) {
                this.mViewPager.setOnPageChangeListener(null);
            }
            if (viewPager.getAdapter() == null) {
                throw new IllegalStateException("ViewPager does not provide adapter instance.");
            }
            this.mIsViewPagerMode = true;
            this.mViewPager = viewPager;
            this.mViewPager.removeOnPageChangeListener(this);
            this.mViewPager.addOnPageChangeListener(this);
            resetScroller();
            postInvalidate();
        }
    }

    public void setViewPager(ViewPager viewPager, int index) {
        setViewPager(viewPager);
        this.mIndex = index;
        if (this.mIsViewPagerMode) {
            this.mViewPager.setCurrentItem(index, true);
        }
        postInvalidate();
    }

    protected void resetScroller() {
        if (this.mViewPager != null) {
            try {
                Field scrollerField = ViewPager.class.getDeclaredField("mScroller");
                scrollerField.setAccessible(true);
                scrollerField.set(this.mViewPager, new ResizeViewPagerScroller(getContext()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setOnPageChangeListener(OnPageChangeListener listener) {
        this.mOnPageChangeListener = listener;
    }

    public boolean isBehaviorEnabled() {
        return this.mBehaviorEnabled;
    }

    public void setBehaviorEnabled(boolean enabled) {
        this.mBehaviorEnabled = enabled;
        if (getParent() != null && (getParent() instanceof CoordinatorLayout)) {
            LayoutParams params = getLayoutParams();
            if (this.mBehavior == null) {
                this.mBehavior = new NavigationTabBarBehavior(enabled);
            } else {
                this.mBehavior.setBehaviorTranslationEnabled(enabled);
            }
            ((CoordinatorLayout.LayoutParams) params).setBehavior(this.mBehavior);
            if (this.mNeedHide) {
                this.mNeedHide = false;
                this.mBehavior.hideView(this, (int) getBarHeight(), this.mAnimateHide);
            }
        }
    }

    public int getModelIndex() {
        return this.mIndex;
    }

    public void setModelIndex(int index) {
        setModelIndex(index, false);
    }

    public void setModelIndex(int modelIndex, boolean isForce) {
        boolean z = true;
        if (!this.mAnimator.isRunning() && !this.mModels.isEmpty()) {
            int index = modelIndex;
            boolean force = isForce;
            if (this.mIndex == INVALID_INDEX) {
                force = true;
            }
            if (index == this.mIndex) {
                force = true;
            }
            index = Math.max(MIN_ALPHA, Math.min(index, this.mModels.size() + INVALID_INDEX));
            this.mIsResizeIn = index < this.mIndex;
            this.mLastIndex = this.mIndex;
            this.mIndex = index;
            this.mIsSetIndexFromTabBar = true;
            if (this.mIsViewPagerMode) {
                if (this.mViewPager == null) {
                    throw new IllegalStateException("ViewPager is null.");
                }
                ViewPager viewPager = this.mViewPager;
                if (force) {
                    z = false;
                }
                viewPager.setCurrentItem(index, z);
            }
            if (force) {
                this.mStartPointerX = ((float) this.mIndex) * this.mModelSize;
                this.mEndPointerX = this.mStartPointerX;
            } else {
                this.mStartPointerX = this.mPointerLeftTop;
                this.mEndPointerX = ((float) this.mIndex) * this.mModelSize;
            }
            if (force) {
                updateIndicatorPosition(MAX_FRACTION);
                if (this.mOnTabBarSelectedIndexListener != null) {
                    this.mOnTabBarSelectedIndexListener.onStartTabSelected((Model) this.mModels.get(this.mIndex), this.mIndex);
                }
                if (this.mIsViewPagerMode) {
                    if (!this.mViewPager.isFakeDragging()) {
                        this.mViewPager.beginFakeDrag();
                    }
                    if (this.mViewPager.isFakeDragging()) {
                        this.mViewPager.fakeDragBy(MIN_FRACTION);
                    }
                    if (this.mViewPager.isFakeDragging()) {
                        this.mViewPager.endFakeDrag();
                        return;
                    }
                    return;
                } else if (this.mOnTabBarSelectedIndexListener != null) {
                    this.mOnTabBarSelectedIndexListener.onEndTabSelected((Model) this.mModels.get(this.mIndex), this.mIndex);
                    return;
                } else {
                    return;
                }
            }
            this.mAnimator.start();
        }
    }

    public void deselect() {
        this.mLastIndex = INVALID_INDEX;
        this.mIndex = INVALID_INDEX;
        this.mStartPointerX = -1.0f * this.mModelSize;
        this.mEndPointerX = this.mStartPointerX;
        updateIndicatorPosition(MIN_FRACTION);
    }

    protected void updateIndicatorPosition(float fraction) {
        this.mFraction = fraction;
        this.mPointerLeftTop = this.mStartPointerX + (this.mResizeInterpolator.getResizeInterpolation(fraction, this.mIsResizeIn) * (this.mEndPointerX - this.mStartPointerX));
        this.mPointerRightBottom = (this.mResizeInterpolator.getResizeInterpolation(fraction, !this.mIsResizeIn) * (this.mEndPointerX - this.mStartPointerX)) + (this.mModelSize + this.mStartPointerX);
        postInvalidate();
    }

    protected void notifyDataSetChanged() {
        requestLayout();
        postInvalidate();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r5) {
        /*
        r4 = this;
        r0 = 0;
        r1 = 1;
        r2 = r4.mAnimator;
        r2 = r2.isRunning();
        if (r2 == 0) goto L_0x000b;
    L_0x000a:
        return r1;
    L_0x000b:
        r2 = r4.mScrollState;
        if (r2 != 0) goto L_0x000a;
    L_0x000f:
        r2 = r5.getAction();
        switch(r2) {
            case 0: goto L_0x001b;
            case 1: goto L_0x0071;
            case 2: goto L_0x0049;
            default: goto L_0x0016;
        };
    L_0x0016:
        r4.mIsPointerActionDown = r0;
        r4.mIsActionDown = r0;
        goto L_0x000a;
    L_0x001b:
        r4.mIsActionDown = r1;
        r2 = r4.mIsViewPagerMode;
        if (r2 == 0) goto L_0x000a;
    L_0x0021:
        r2 = r4.mIsSwiped;
        if (r2 == 0) goto L_0x000a;
    L_0x0025:
        r2 = r4.mIsHorizontalOrientation;
        if (r2 == 0) goto L_0x0039;
    L_0x0029:
        r2 = r5.getX();
        r3 = r4.mModelSize;
        r2 = r2 / r3;
        r2 = (int) r2;
        r3 = r4.mIndex;
        if (r2 != r3) goto L_0x0036;
    L_0x0035:
        r0 = r1;
    L_0x0036:
        r4.mIsPointerActionDown = r0;
        goto L_0x000a;
    L_0x0039:
        r2 = r5.getY();
        r3 = r4.mModelSize;
        r2 = r2 / r3;
        r2 = (int) r2;
        r3 = r4.mIndex;
        if (r2 != r3) goto L_0x0046;
    L_0x0045:
        r0 = r1;
    L_0x0046:
        r4.mIsPointerActionDown = r0;
        goto L_0x000a;
    L_0x0049:
        r2 = r4.mIsPointerActionDown;
        if (r2 == 0) goto L_0x006d;
    L_0x004d:
        r0 = r4.mIsHorizontalOrientation;
        if (r0 == 0) goto L_0x005f;
    L_0x0051:
        r0 = r4.mViewPager;
        r2 = r5.getX();
        r3 = r4.mModelSize;
        r2 = r2 / r3;
        r2 = (int) r2;
        r0.setCurrentItem(r2, r1);
        goto L_0x000a;
    L_0x005f:
        r0 = r4.mViewPager;
        r2 = r5.getY();
        r3 = r4.mModelSize;
        r2 = r2 / r3;
        r2 = (int) r2;
        r0.setCurrentItem(r2, r1);
        goto L_0x000a;
    L_0x006d:
        r2 = r4.mIsActionDown;
        if (r2 != 0) goto L_0x000a;
    L_0x0071:
        r2 = r4.mIsActionDown;
        if (r2 == 0) goto L_0x0016;
    L_0x0075:
        r4.playSoundEffect(r0);
        r2 = r4.mIsHorizontalOrientation;
        if (r2 == 0) goto L_0x0088;
    L_0x007c:
        r2 = r5.getX();
        r3 = r4.mModelSize;
        r2 = r2 / r3;
        r2 = (int) r2;
        r4.setModelIndex(r2);
        goto L_0x0016;
    L_0x0088:
        r2 = r5.getY();
        r3 = r4.mModelSize;
        r2 = r2 / r3;
        r2 = (int) r2;
        r4.setModelIndex(r2);
        goto L_0x0016;
        */
        throw new UnsupportedOperationException("Method not decompiled: devlight.io.library.ntb.NavigationTabBar.onTouchEvent(android.view.MotionEvent):boolean");
    }

    @SuppressLint({"DrawAllocation"})
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (!this.mModels.isEmpty() && width != 0 && height != 0) {
            float f;
            Model model;
            if (width > height) {
                this.mIsHorizontalOrientation = true;
                this.mModelSize = ((float) width) / ((float) this.mModels.size());
                float side = this.mModelSize > ((float) height) ? (float) height : this.mModelSize;
                if (this.mIsBadged) {
                    side -= TITLE_SIZE_FRACTION * side;
                }
                f = this.mIconSizeFraction != -4.0f ? this.mIconSizeFraction : this.mIsTitled ? DEFAULT_TITLE_ICON_SIZE_FRACTION : DEFAULT_TITLE_ICON_SIZE_FRACTION;
                this.mIconSize = f * side;
                if (this.mModelTitleSize == -2.0f) {
                    this.mModelTitleSize = TITLE_SIZE_FRACTION * side;
                }
                this.mTitleMargin = TITLE_MARGIN_FRACTION * side;
                if (this.mIsBadged) {
                    if (this.mBadgeTitleSize == -2.0f) {
                        this.mBadgeTitleSize = (TITLE_SIZE_FRACTION * side) * BADGE_TITLE_SIZE_FRACTION;
                    }
                    Rect badgeBounds = new Rect();
                    this.mBadgePaint.setTextSize(this.mBadgeTitleSize);
                    this.mBadgePaint.getTextBounds(PREVIEW_BADGE, MIN_ALPHA, 1, badgeBounds);
                    this.mBadgeMargin = (((float) badgeBounds.height()) * DEFAULT_TITLE_ICON_SIZE_FRACTION) + ((this.mBadgeTitleSize * DEFAULT_TITLE_ICON_SIZE_FRACTION) * RIGHT_FRACTION);
                }
            } else {
                this.mBehaviorEnabled = false;
                this.mIsHorizontalOrientation = false;
                this.mIsTitled = false;
                this.mIsBadged = false;
                this.mModelSize = ((float) height) / ((float) this.mModels.size());
                this.mIconSize = (float) ((int) ((this.mModelSize > ((float) width) ? (float) width : this.mModelSize) * (this.mIconSizeFraction == -4.0f ? DEFAULT_TITLE_ICON_SIZE_FRACTION : this.mIconSizeFraction)));
            }
            this.mBounds.set(MIN_FRACTION, MIN_FRACTION, (float) width, ((float) height) - this.mBadgeMargin);
            float barBadgeMargin = this.mBadgeGravity == BadgeGravity.TOP ? this.mBadgeMargin : MIN_FRACTION;
            this.mBgBounds.set(MIN_FRACTION, barBadgeMargin, this.mBounds.width(), this.mBounds.height() + barBadgeMargin);
            for (Model model2 : this.mModels) {
                model2.mInactiveIconScale = this.mIconSize / (model2.mIcon.getWidth() > model2.mIcon.getHeight() ? (float) model2.mIcon.getWidth() : (float) model2.mIcon.getHeight());
                float access$400 = model2.mInactiveIconScale;
                if (this.mIsTitled) {
                    f = TITLE_SIZE_FRACTION;
                } else {
                    f = SCALED_FRACTION;
                }
                model2.mActiveIconScaleBy = f * access$400;
            }
            this.mBitmap = null;
            this.mPointerBitmap = null;
            this.mIconsBitmap = null;
            if (this.mIsTitled) {
                this.mTitlesBitmap = null;
            }
            if (isInEditMode() || !this.mIsViewPagerMode) {
                this.mIsSetIndexFromTabBar = true;
                if (isInEditMode()) {
                    this.mIndex = new Random().nextInt(this.mModels.size());
                    if (this.mIsBadged) {
                        for (int i = MIN_ALPHA; i < this.mModels.size(); i++) {
                            model2 = (Model) this.mModels.get(i);
                            if (i == this.mIndex) {
                                model2.mBadgeFraction = MAX_FRACTION;
                                model2.showBadge();
                            } else {
                                model2.mBadgeFraction = MIN_FRACTION;
                                model2.hideBadge();
                            }
                        }
                    }
                }
                this.mStartPointerX = ((float) this.mIndex) * this.mModelSize;
                this.mEndPointerX = this.mStartPointerX;
                updateIndicatorPosition(MAX_FRACTION);
            }
            if (!this.mIsBehaviorSet) {
                setBehaviorEnabled(this.mBehaviorEnabled);
                this.mIsBehaviorSet = true;
            }
        }
    }

    protected void onDraw(Canvas canvas) {
        int i;
        int mBadgedHeight = (int) (this.mBounds.height() + this.mBadgeMargin);
        if (this.mBitmap == null || this.mBitmap.isRecycled()) {
            this.mBitmap = Bitmap.createBitmap((int) this.mBounds.width(), mBadgedHeight, Config.ARGB_8888);
            this.mCanvas.setBitmap(this.mBitmap);
        }
        if (this.mPointerBitmap == null || this.mPointerBitmap.isRecycled()) {
            this.mPointerBitmap = Bitmap.createBitmap((int) this.mBounds.width(), mBadgedHeight, Config.ARGB_8888);
            this.mPointerCanvas.setBitmap(this.mPointerBitmap);
        }
        if (this.mIconsBitmap == null || this.mIconsBitmap.isRecycled()) {
            this.mIconsBitmap = Bitmap.createBitmap((int) this.mBounds.width(), mBadgedHeight, Config.ARGB_8888);
            this.mIconsCanvas.setBitmap(this.mIconsBitmap);
        }
        if (!this.mIsTitled) {
            this.mTitlesBitmap = null;
        } else if (this.mTitlesBitmap == null || this.mTitlesBitmap.isRecycled()) {
            this.mTitlesBitmap = Bitmap.createBitmap((int) this.mBounds.width(), mBadgedHeight, Config.ARGB_8888);
            this.mTitlesCanvas.setBitmap(this.mTitlesBitmap);
        }
        this.mCanvas.drawColor(MIN_ALPHA, Mode.CLEAR);
        this.mPointerCanvas.drawColor(MIN_ALPHA, Mode.CLEAR);
        this.mIconsCanvas.drawColor(MIN_ALPHA, Mode.CLEAR);
        if (this.mIsTitled) {
            this.mTitlesCanvas.drawColor(MIN_ALPHA, Mode.CLEAR);
        }
        if (this.mCornersRadius == MIN_FRACTION) {
            canvas.drawRect(this.mBgBounds, this.mBgPaint);
        } else {
            canvas.drawRoundRect(this.mBgBounds, this.mCornersRadius, this.mCornersRadius, this.mBgPaint);
        }
        float barBadgeMargin = this.mBadgeGravity == BadgeGravity.TOP ? this.mBadgeMargin : MIN_FRACTION;
        for (i = MIN_ALPHA; i < this.mModels.size(); i++) {
            this.mPaint.setColor(((Model) this.mModels.get(i)).getColor());
            if (this.mIsHorizontalOrientation) {
                float left = this.mModelSize * ((float) i);
                this.mCanvas.drawRect(left, barBadgeMargin, left + this.mModelSize, this.mBounds.height() + barBadgeMargin, this.mPaint);
            } else {
                float top = this.mModelSize * ((float) i);
                this.mCanvas.drawRect(MIN_FRACTION, top, this.mBounds.width(), top + this.mModelSize, this.mPaint);
            }
        }
        if (this.mIsHorizontalOrientation) {
            this.mPointerBounds.set(this.mPointerLeftTop, barBadgeMargin, this.mPointerRightBottom, this.mBounds.height() + barBadgeMargin);
        } else {
            this.mPointerBounds.set(MIN_FRACTION, this.mPointerLeftTop, this.mBounds.width(), this.mPointerRightBottom);
        }
        if (this.mCornersRadius == MIN_FRACTION) {
            this.mPointerCanvas.drawRect(this.mPointerBounds, this.mPaint);
        } else {
            this.mPointerCanvas.drawRoundRect(this.mPointerBounds, this.mCornersRadius, this.mCornersRadius, this.mPaint);
        }
        this.mCanvas.drawBitmap(this.mPointerBitmap, MIN_FRACTION, MIN_FRACTION, this.mPointerPaint);
        float iconMarginTitleHeight = (this.mIconSize + this.mTitleMargin) + this.mModelTitleSize;
        for (i = MIN_ALPHA; i < this.mModels.size(); i++) {
            float leftOffset;
            float topOffset;
            float f;
            float titleLastScale;
            Model model = (Model) this.mModels.get(i);
            float leftTitleOffset = (this.mModelSize * ((float) i)) + (this.mModelSize * DEFAULT_TITLE_ICON_SIZE_FRACTION);
            float topTitleOffset = this.mBounds.height() - ((this.mBounds.height() - iconMarginTitleHeight) * DEFAULT_TITLE_ICON_SIZE_FRACTION);
            if (this.mIsHorizontalOrientation) {
                leftOffset = (this.mModelSize * ((float) i)) + ((this.mModelSize - ((float) model.mIcon.getWidth())) * DEFAULT_TITLE_ICON_SIZE_FRACTION);
                topOffset = (this.mBounds.height() - ((float) model.mIcon.getHeight())) * DEFAULT_TITLE_ICON_SIZE_FRACTION;
            } else {
                leftOffset = (this.mBounds.width() - ((float) model.mIcon.getWidth())) * DEFAULT_TITLE_ICON_SIZE_FRACTION;
                topOffset = (this.mModelSize * ((float) i)) + ((this.mModelSize - ((float) model.mIcon.getHeight())) * DEFAULT_TITLE_ICON_SIZE_FRACTION);
            }
            float matrixCenterX = leftOffset + (((float) model.mIcon.getWidth()) * DEFAULT_TITLE_ICON_SIZE_FRACTION);
            float matrixCenterY = topOffset + (((float) model.mIcon.getHeight()) * DEFAULT_TITLE_ICON_SIZE_FRACTION);
            float titleTranslate = topOffset - (((float) model.mIcon.getHeight()) * TITLE_MARGIN_SCALE_FRACTION);
            Matrix access$600 = model.mIconMatrix;
            if (this.mIsTitled && this.mTitleMode == TitleMode.ALL) {
                f = titleTranslate;
            } else {
                f = topOffset;
            }
            access$600.setTranslate(leftOffset, f);
            float interpolation = this.mResizeInterpolator.getResizeInterpolation(this.mFraction, true);
            float lastInterpolation = this.mResizeInterpolator.getResizeInterpolation(this.mFraction, false);
            float matrixScale = model.mActiveIconScaleBy * interpolation;
            float matrixLastScale = model.mActiveIconScaleBy * lastInterpolation;
            int titleAlpha = (int) (255.0f * interpolation);
            int titleLastAlpha = 255 - ((int) (255.0f * lastInterpolation));
            float titleScale = this.mIsScaled ? MAX_FRACTION + (TITLE_SIZE_FRACTION * interpolation) : MAX_FRACTION;
            if (this.mIsScaled) {
                titleLastScale = 1.2f - (TITLE_SIZE_FRACTION * lastInterpolation);
            } else {
                titleLastScale = titleScale;
            }
            this.mIconPaint.setAlpha(MAX_ALPHA);
            if (model.mSelectedIcon != null) {
                this.mSelectedIconPaint.setAlpha(MAX_ALPHA);
            }
            if (this.mIsSetIndexFromTabBar) {
                if (this.mIndex == i) {
                    updateCurrentModel(model, leftOffset, topOffset, titleTranslate, interpolation, matrixCenterX, matrixCenterY, matrixScale, titleScale, titleAlpha);
                } else if (this.mLastIndex == i) {
                    updateLastModel(model, leftOffset, topOffset, titleTranslate, lastInterpolation, matrixCenterX, matrixCenterY, matrixLastScale, titleLastScale, titleLastAlpha);
                } else {
                    updateInactiveModel(model, leftOffset, topOffset, titleScale, matrixScale, matrixCenterX, matrixCenterY);
                }
            } else if (i == this.mIndex + 1) {
                updateCurrentModel(model, leftOffset, topOffset, titleTranslate, interpolation, matrixCenterX, matrixCenterY, matrixScale, titleScale, titleAlpha);
            } else if (i == this.mIndex) {
                updateLastModel(model, leftOffset, topOffset, titleTranslate, lastInterpolation, matrixCenterX, matrixCenterY, matrixLastScale, titleLastScale, titleLastAlpha);
            } else {
                updateInactiveModel(model, leftOffset, topOffset, titleScale, matrixScale, matrixCenterX, matrixCenterY);
            }
            if (model.mSelectedIcon == null) {
                if (!(model.mIcon == null || model.mIcon.isRecycled())) {
                    this.mIconsCanvas.drawBitmap(model.mIcon, model.mIconMatrix, this.mIconPaint);
                }
            } else if (!(this.mIconPaint.getAlpha() == 0 || model.mIcon == null || model.mIcon.isRecycled())) {
                this.mIconsCanvas.drawBitmap(model.mIcon, model.mIconMatrix, this.mIconPaint);
            }
            if (!(this.mSelectedIconPaint.getAlpha() == 0 || model.mSelectedIcon == null || model.mSelectedIcon.isRecycled())) {
                this.mIconsCanvas.drawBitmap(model.mSelectedIcon, model.mIconMatrix, this.mSelectedIconPaint);
            }
            if (this.mIsTitled) {
                String str;
                Canvas canvas2 = this.mTitlesCanvas;
                if (isInEditMode()) {
                    str = PREVIEW_TITLE;
                } else {
                    str = model.getTitle();
                }
                canvas2.drawText(str, leftTitleOffset, topTitleOffset, this.mModelTitlePaint);
            }
        }
        if (this.mIsHorizontalOrientation) {
            this.mPointerBounds.set(this.mPointerLeftTop, MIN_FRACTION, this.mPointerRightBottom, this.mBounds.height());
        }
        if (this.mCornersRadius == MIN_FRACTION) {
            if (this.mIsTinted) {
                this.mIconsCanvas.drawRect(this.mPointerBounds, this.mIconPointerPaint);
            }
            if (this.mIsTitled) {
                this.mTitlesCanvas.drawRect(this.mPointerBounds, this.mIconPointerPaint);
            }
        } else {
            if (this.mIsTinted) {
                this.mIconsCanvas.drawRoundRect(this.mPointerBounds, this.mCornersRadius, this.mCornersRadius, this.mIconPointerPaint);
            }
            if (this.mIsTitled) {
                this.mTitlesCanvas.drawRoundRect(this.mPointerBounds, this.mCornersRadius, this.mCornersRadius, this.mIconPointerPaint);
            }
        }
        canvas.drawBitmap(this.mBitmap, MIN_FRACTION, MIN_FRACTION, null);
        canvas.drawBitmap(this.mIconsBitmap, MIN_FRACTION, barBadgeMargin, null);
        if (this.mIsTitled) {
            canvas.drawBitmap(this.mTitlesBitmap, MIN_FRACTION, barBadgeMargin, null);
        }
        if (this.mIsBadged) {
            float modelBadgeOffset;
            float modelBadgeMargin = this.mBadgeGravity == BadgeGravity.TOP ? this.mBadgeMargin : this.mBounds.height();
            if (this.mBadgeGravity == BadgeGravity.TOP) {
                modelBadgeOffset = MIN_FRACTION;
            } else {
                modelBadgeOffset = this.mBounds.height() - this.mBadgeMargin;
            }
            for (i = MIN_ALPHA; i < this.mModels.size(); i++) {
                model = (Model) this.mModels.get(i);
                if (isInEditMode() || TextUtils.isEmpty(model.getBadgeTitle())) {
                    model.setBadgeTitle(PREVIEW_BADGE);
                }
                this.mBadgePaint.setTextSize(this.mBadgeTitleSize * model.mBadgeFraction);
                this.mBadgePaint.getTextBounds(model.getBadgeTitle(), MIN_ALPHA, model.getBadgeTitle().length(), this.mBadgeBounds);
                float horizontalPadding = this.mBadgeTitleSize * DEFAULT_TITLE_ICON_SIZE_FRACTION;
                float verticalPadding = horizontalPadding * RIGHT_FRACTION;
                float badgeBoundsHorizontalOffset = (this.mModelSize * ((float) i)) + (this.mModelSize * this.mBadgePosition.mPositionFraction);
                float badgeMargin = this.mBadgeMargin * model.mBadgeFraction;
                if (model.getBadgeTitle().length() == 1) {
                    this.mBgBadgeBounds.set(badgeBoundsHorizontalOffset - badgeMargin, modelBadgeMargin - badgeMargin, badgeBoundsHorizontalOffset + badgeMargin, modelBadgeMargin + badgeMargin);
                } else {
                    this.mBgBadgeBounds.set(badgeBoundsHorizontalOffset - Math.max(badgeMargin, ((float) this.mBadgeBounds.centerX()) + horizontalPadding), modelBadgeMargin - badgeMargin, Math.max(badgeMargin, ((float) this.mBadgeBounds.centerX()) + horizontalPadding) + badgeBoundsHorizontalOffset, ((2.0f * verticalPadding) + modelBadgeOffset) + ((float) this.mBadgeBounds.height()));
                }
                if (model.mBadgeFraction == MIN_FRACTION) {
                    this.mBadgePaint.setColor(MIN_ALPHA);
                } else {
                    this.mBadgePaint.setColor(this.mBadgeBgColor == AUTO_COLOR ? this.mActiveColor : this.mBadgeBgColor);
                }
                this.mBadgePaint.setAlpha((int) (255.0f * model.mBadgeFraction));
                float cornerRadius = this.mBgBadgeBounds.height() * DEFAULT_TITLE_ICON_SIZE_FRACTION;
                canvas.drawRoundRect(this.mBgBadgeBounds, cornerRadius, cornerRadius, this.mBadgePaint);
                if (model.mBadgeFraction == MIN_FRACTION) {
                    this.mBadgePaint.setColor(MIN_ALPHA);
                } else {
                    this.mBadgePaint.setColor(this.mBadgeTitleColor == AUTO_COLOR ? model.getColor() : this.mBadgeTitleColor);
                }
                this.mBadgePaint.setAlpha((int) (255.0f * model.mBadgeFraction));
                canvas.drawText(model.getBadgeTitle(), badgeBoundsHorizontalOffset, (((float) this.mBadgeBounds.height()) + ((((this.mBgBadgeBounds.height() * DEFAULT_TITLE_ICON_SIZE_FRACTION) + (((float) this.mBadgeBounds.height()) * DEFAULT_TITLE_ICON_SIZE_FRACTION)) - ((float) this.mBadgeBounds.bottom)) + modelBadgeOffset)) - (((float) this.mBadgeBounds.height()) * model.mBadgeFraction), this.mBadgePaint);
            }
        }
    }

    protected void updateCurrentModel(Model model, float leftOffset, float topOffset, float titleTranslate, float interpolation, float matrixCenterX, float matrixCenterY, float matrixScale, float titleScale, int titleAlpha) {
        if (this.mIsTitled && this.mTitleMode == TitleMode.ACTIVE) {
            model.mIconMatrix.setTranslate(leftOffset, topOffset - ((topOffset - titleTranslate) * interpolation));
        }
        float access$400 = model.mInactiveIconScale;
        if (!this.mIsScaled) {
            matrixScale = MIN_FRACTION;
        }
        float scale = access$400 + matrixScale;
        model.mIconMatrix.postScale(scale, scale, matrixCenterX, matrixCenterY);
        this.mModelTitlePaint.setTextSize(this.mModelTitleSize * titleScale);
        if (this.mTitleMode == TitleMode.ACTIVE) {
            this.mModelTitlePaint.setAlpha(titleAlpha);
        }
        if (model.mSelectedIcon == null) {
            this.mIconPaint.setAlpha(MAX_ALPHA);
            return;
        }
        float iconAlpha;
        float selectedIconAlpha;
        if (interpolation <= 0.475f) {
            iconAlpha = MAX_FRACTION - (2.1f * interpolation);
            selectedIconAlpha = MIN_FRACTION;
        } else if (interpolation >= 0.525f) {
            iconAlpha = MIN_FRACTION;
            selectedIconAlpha = (interpolation - 0.55f) * 1.9f;
        } else {
            iconAlpha = MIN_FRACTION;
            selectedIconAlpha = MIN_FRACTION;
        }
        this.mIconPaint.setAlpha((int) (255.0f * clampValue(iconAlpha)));
        this.mSelectedIconPaint.setAlpha((int) (255.0f * clampValue(selectedIconAlpha)));
    }

    protected void updateLastModel(Model model, float leftOffset, float topOffset, float titleTranslate, float lastInterpolation, float matrixCenterX, float matrixCenterY, float matrixLastScale, float titleLastScale, int titleLastAlpha) {
        if (this.mIsTitled && this.mTitleMode == TitleMode.ACTIVE) {
            model.mIconMatrix.setTranslate(leftOffset, ((topOffset - titleTranslate) * lastInterpolation) + titleTranslate);
        }
        float scale = model.mInactiveIconScale + (this.mIsScaled ? model.mActiveIconScaleBy - matrixLastScale : MIN_FRACTION);
        model.mIconMatrix.postScale(scale, scale, matrixCenterX, matrixCenterY);
        this.mModelTitlePaint.setTextSize(this.mModelTitleSize * titleLastScale);
        if (this.mTitleMode == TitleMode.ACTIVE) {
            this.mModelTitlePaint.setAlpha(titleLastAlpha);
        }
        if (model.mSelectedIcon == null) {
            this.mIconPaint.setAlpha(MAX_ALPHA);
            return;
        }
        float iconAlpha;
        float selectedIconAlpha;
        if (lastInterpolation <= 0.475f) {
            iconAlpha = MIN_FRACTION;
            selectedIconAlpha = MAX_FRACTION - (2.1f * lastInterpolation);
        } else if (lastInterpolation >= 0.525f) {
            iconAlpha = (lastInterpolation - 0.55f) * 1.9f;
            selectedIconAlpha = MIN_FRACTION;
        } else {
            iconAlpha = MIN_FRACTION;
            selectedIconAlpha = MIN_FRACTION;
        }
        this.mIconPaint.setAlpha((int) (255.0f * clampValue(iconAlpha)));
        this.mSelectedIconPaint.setAlpha((int) (255.0f * clampValue(selectedIconAlpha)));
    }

    protected void updateInactiveModel(Model model, float leftOffset, float topOffset, float textScale, float matrixScale, float matrixCenterX, float matrixCenterY) {
        if (this.mIsTitled && this.mTitleMode == TitleMode.ACTIVE) {
            model.mIconMatrix.setTranslate(leftOffset, topOffset);
        }
        model.mIconMatrix.postScale(model.mInactiveIconScale, model.mInactiveIconScale, matrixCenterX, matrixCenterY);
        this.mModelTitlePaint.setTextSize(this.mModelTitleSize);
        if (this.mTitleMode == TitleMode.ACTIVE) {
            this.mModelTitlePaint.setAlpha(MIN_ALPHA);
        }
        if (model.mSelectedIcon == null) {
            this.mIconPaint.setAlpha(MAX_ALPHA);
        } else {
            this.mSelectedIconPaint.setAlpha(MIN_ALPHA);
        }
    }

    protected void updateTint() {
        if (this.mIsTinted) {
            PorterDuffColorFilter colorFilter = new PorterDuffColorFilter(this.mInactiveColor, Mode.SRC_IN);
            this.mIconPaint.setColorFilter(colorFilter);
            this.mSelectedIconPaint.setColorFilter(colorFilter);
        } else {
            this.mIconPaint.reset();
            this.mSelectedIconPaint.reset();
        }
        postInvalidate();
    }

    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (this.mOnPageChangeListener != null) {
            this.mOnPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
        if (!this.mIsSetIndexFromTabBar) {
            this.mIsResizeIn = position < this.mIndex;
            this.mLastIndex = this.mIndex;
            this.mIndex = position;
            this.mStartPointerX = ((float) position) * this.mModelSize;
            this.mEndPointerX = this.mStartPointerX + this.mModelSize;
            updateIndicatorPosition(positionOffset);
        }
        if (!this.mAnimator.isRunning() && this.mIsSetIndexFromTabBar) {
            this.mFraction = MIN_FRACTION;
            this.mIsSetIndexFromTabBar = false;
        }
    }

    public void onPageSelected(int position) {
    }

    public void onPageScrollStateChanged(int state) {
        this.mScrollState = state;
        if (state == 0) {
            if (this.mOnPageChangeListener != null) {
                this.mOnPageChangeListener.onPageSelected(this.mIndex);
            }
            if (this.mIsViewPagerMode && this.mOnTabBarSelectedIndexListener != null) {
                this.mOnTabBarSelectedIndexListener.onEndTabSelected((Model) this.mModels.get(this.mIndex), this.mIndex);
            }
        }
        if (this.mOnPageChangeListener != null) {
            this.mOnPageChangeListener.onPageScrollStateChanged(state);
        }
    }

    public void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        this.mIndex = savedState.index;
        requestLayout();
    }

    public Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.index = this.mIndex;
        return savedState;
    }

    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        requestLayout();
        int tempIndex = this.mIndex;
        deselect();
        post(new AnonymousClass10(tempIndex));
    }

    protected float clampValue(float value) {
        return Math.max(Math.min(value, MAX_FRACTION), MIN_FRACTION);
    }

    public void hide() {
        if (this.mBehavior != null) {
            this.mBehavior.hideView(this, (int) getBarHeight(), true);
        } else if (getParent() == null || !(getParent() instanceof CoordinatorLayout)) {
            scrollDown();
        } else {
            this.mNeedHide = true;
            this.mAnimateHide = true;
        }
    }

    public void show() {
        if (this.mBehavior != null) {
            this.mBehavior.resetOffset(this, true);
        } else {
            scrollUp();
        }
    }

    protected void scrollDown() {
        ViewCompat.animate(this).translationY(getBarHeight()).setInterpolator(new LinearOutSlowInInterpolator()).setDuration(300).start();
    }

    protected void scrollUp() {
        ViewCompat.animate(this).translationY(MIN_FRACTION).setInterpolator(OUT_SLOW_IN_INTERPOLATOR).setDuration(300).start();
    }
}
