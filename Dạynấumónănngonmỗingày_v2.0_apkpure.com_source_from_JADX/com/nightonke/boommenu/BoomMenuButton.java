package com.nightonke.boommenu;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.graphics.drawable.RippleDrawable;
import android.os.Build.VERSION;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat.MessagingStyle;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import com.google.android.gms.common.ConnectionResult;
import com.nightonke.boommenu.Animation.AnimationManager;
import com.nightonke.boommenu.Animation.BoomEnum;
import com.nightonke.boommenu.Animation.Ease;
import com.nightonke.boommenu.Animation.EaseEnum;
import com.nightonke.boommenu.Animation.OrderEnum;
import com.nightonke.boommenu.Animation.ShareLinesView;
import com.nightonke.boommenu.BoomButtons.BoomButton;
import com.nightonke.boommenu.BoomButtons.BoomButtonBuilder;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceAlignmentEnum;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceManager;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomButtons.InnerOnBoomButtonClickListener;
import com.nightonke.boommenu.BoomButtons.SimpleCircleButton.Builder;
import com.nightonke.boommenu.BoomButtons.TextInsideCircleButton;
import com.nightonke.boommenu.BoomButtons.TextOutsideCircleButton;
import com.nightonke.boommenu.Piece.BoomPiece;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;
import com.nightonke.boommenu.Piece.PiecePlaceManager;
import com.tapadoo.android.C0784R;
import java.util.ArrayList;
import java.util.Iterator;
import me.wangyuwei.loadingview.C0801R;
import org.apache.http.util.LangUtils;
import rx.internal.operators.OnSubscribeConcatMap;

public class BoomMenuButton extends FrameLayout implements InnerOnBoomButtonClickListener {
    protected static final String TAG = "BoomMenuButton";
    private int animatingViewNumber;
    private boolean autoHide;
    private ViewGroup background;
    private boolean backgroundEffect;
    private ArrayList<BoomButtonBuilder> boomButtonBuilders;
    private ArrayList<BoomButton> boomButtons;
    private BoomEnum boomEnum;
    private boolean boomInWholeScreen;
    private BoomStateEnum boomStateEnum;
    private Float bottomHamButtonTopMargin;
    private FrameLayout button;
    private float buttonBottomMargin;
    private ButtonEnum buttonEnum;
    private float buttonHorizontalMargin;
    private float buttonInclinedMargin;
    private float buttonLeftMargin;
    private ButtonPlaceAlignmentEnum buttonPlaceAlignmentEnum;
    private ButtonPlaceEnum buttonPlaceEnum;
    private int buttonRadius;
    private float buttonRightMargin;
    private float buttonTopMargin;
    private float buttonVerticalMargin;
    private boolean cacheOptimization;
    private boolean cancelable;
    private Context context;
    private int dimColor;
    private int dotRadius;
    private ArrayList<Point> endPositions;
    private int frames;
    private float hamButtonHeight;
    private float hamButtonWidth;
    private int hamHeight;
    private int hamWidth;
    private long hideDelay;
    private long hideDuration;
    private EaseEnum hideMoveEaseEnum;
    private EaseEnum hideRotateEaseEnum;
    private EaseEnum hideScaleEaseEnum;
    private int highlightedColor;
    private boolean inFragment;
    private boolean inList;
    private boolean isBackPressListened;
    private Runnable layoutJobsRunnable;
    private boolean needToLayout;
    private int normalColor;
    private OnBoomListener onBoomListener;
    private OrderEnum orderEnum;
    private int pieceHorizontalMargin;
    private int pieceInclinedMargin;
    private PiecePlaceEnum piecePlaceEnum;
    private ArrayList<Point> piecePositions;
    private int pieceVerticalMargin;
    private ArrayList<BoomPiece> pieces;
    private boolean rippleEffect;
    private int rotateDegree;
    private BMBShadow shadow;
    private int shadowColor;
    private boolean shadowEffect;
    private int shadowOffsetX;
    private int shadowOffsetY;
    private int shadowRadius;
    private int shareLine1Color;
    private int shareLine2Color;
    private int shareLineLength;
    private int shareLineWidth;
    private ShareLinesView shareLinesView;
    private long showDelay;
    private long showDuration;
    private EaseEnum showMoveEaseEnum;
    private EaseEnum showRotateEaseEnum;
    private EaseEnum showScaleEaseEnum;
    private float simpleCircleButtonRadius;
    private ArrayList<Point> startPositions;
    private float textInsideCircleButtonRadius;
    private float textOutsideCircleButtonHeight;
    private float textOutsideCircleButtonWidth;
    private int unableColor;

    /* renamed from: com.nightonke.boommenu.BoomMenuButton.1 */
    class C07511 implements OnClickListener {
        C07511() {
        }

        public void onClick(View v) {
            BoomMenuButton.this.boom();
        }
    }

    /* renamed from: com.nightonke.boommenu.BoomMenuButton.2 */
    class C07522 extends AnimatorListenerAdapter {
        C07522() {
        }

        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            BoomMenuButton.this.boomStateEnum = BoomStateEnum.DidShow;
            if (BoomMenuButton.this.onBoomListener != null) {
                BoomMenuButton.this.onBoomListener.onBoomDidShow();
            }
        }
    }

    /* renamed from: com.nightonke.boommenu.BoomMenuButton.3 */
    class C07533 extends AnimatorListenerAdapter {
        C07533() {
        }

        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            BoomMenuButton.this.boomStateEnum = BoomStateEnum.DidHide;
            if (BoomMenuButton.this.onBoomListener != null) {
                BoomMenuButton.this.onBoomListener.onBoomDidHide();
            }
            BoomMenuButton.this.clearViewsAndValues();
        }
    }

    /* renamed from: com.nightonke.boommenu.BoomMenuButton.4 */
    class C07544 implements Runnable {
        final /* synthetic */ BoomButton val$boomButton;
        final /* synthetic */ int val$delayFactor;
        final /* synthetic */ Point val$endPosition;
        final /* synthetic */ boolean val$immediately;
        final /* synthetic */ BoomPiece val$piece;
        final /* synthetic */ Point val$startPosition;

        C07544(BoomPiece boomPiece, BoomButton boomButton, Point point, Point point2, int i, boolean z) {
            this.val$piece = boomPiece;
            this.val$boomButton = boomButton;
            this.val$startPosition = point;
            this.val$endPosition = point2;
            this.val$delayFactor = i;
            this.val$immediately = z;
        }

        public void run() {
            BoomMenuButton.this.innerStartEachShowAnimation(this.val$piece, this.val$boomButton, this.val$startPosition, this.val$endPosition, this.val$delayFactor, this.val$immediately);
        }
    }

    /* renamed from: com.nightonke.boommenu.BoomMenuButton.5 */
    class C07555 extends AnimatorListenerAdapter {
        final /* synthetic */ BoomButton val$boomButton;
        final /* synthetic */ BoomPiece val$piece;

        C07555(BoomPiece boomPiece, BoomButton boomButton) {
            this.val$piece = boomPiece;
            this.val$boomButton = boomButton;
        }

        public void onAnimationStart(Animator animation) {
            super.onAnimationStart(animation);
            Util.setVisibility(4, this.val$piece);
            Util.setVisibility(0, this.val$boomButton);
            this.val$boomButton.willShow();
        }

        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            this.val$boomButton.setClickable(true);
            this.val$boomButton.didShow();
            BoomMenuButton.this.animatingViewNumber = BoomMenuButton.this.animatingViewNumber - 1;
        }
    }

    /* renamed from: com.nightonke.boommenu.BoomMenuButton.6 */
    class C07566 extends AnimatorListenerAdapter {
        final /* synthetic */ BoomButton val$boomButton;
        final /* synthetic */ BoomPiece val$piece;

        C07566(BoomButton boomButton, BoomPiece boomPiece) {
            this.val$boomButton = boomButton;
            this.val$piece = boomPiece;
        }

        public void onAnimationStart(Animator animation) {
            super.onAnimationStart(animation);
            this.val$boomButton.willHide();
        }

        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            Util.setVisibility(0, this.val$piece);
            Util.setVisibility(4, this.val$boomButton);
            this.val$boomButton.didHide();
            this.val$boomButton.cleanListener();
            BoomMenuButton.this.this$0.animatingViewNumber = BoomMenuButton.this.animatingViewNumber - 1;
        }
    }

    /* renamed from: com.nightonke.boommenu.BoomMenuButton.7 */
    class C07577 implements OnClickListener {
        C07577() {
        }

        public void onClick(View v) {
            BoomMenuButton.this.onBackgroundClicked();
        }
    }

    /* renamed from: com.nightonke.boommenu.BoomMenuButton.8 */
    class C07588 implements Runnable {
        C07588() {
        }

        public void run() {
            BoomMenuButton.this.doLayoutJobs();
        }
    }

    /* renamed from: com.nightonke.boommenu.BoomMenuButton.9 */
    static /* synthetic */ class C07599 {
        static final /* synthetic */ int[] $SwitchMap$com$nightonke$boommenu$ButtonEnum;
        static final /* synthetic */ int[] $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum;

        static {
            $SwitchMap$com$nightonke$boommenu$ButtonEnum = new int[ButtonEnum.values().length];
            try {
                $SwitchMap$com$nightonke$boommenu$ButtonEnum[ButtonEnum.SimpleCircle.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$ButtonEnum[ButtonEnum.TextInsideCircle.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$ButtonEnum[ButtonEnum.TextOutsideCircle.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$ButtonEnum[ButtonEnum.Ham.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$ButtonEnum[ButtonEnum.Unknown.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum = new int[PiecePlaceEnum.values().length];
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_1.ordinal()] = 1;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_2_1.ordinal()] = 2;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_2_2.ordinal()] = 3;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_3_1.ordinal()] = 4;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_3_2.ordinal()] = 5;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_3_3.ordinal()] = 6;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_3_4.ordinal()] = 7;
            } catch (NoSuchFieldError e12) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_4_1.ordinal()] = 8;
            } catch (NoSuchFieldError e13) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_4_2.ordinal()] = 9;
            } catch (NoSuchFieldError e14) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_5_1.ordinal()] = 10;
            } catch (NoSuchFieldError e15) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_5_2.ordinal()] = 11;
            } catch (NoSuchFieldError e16) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_5_3.ordinal()] = 12;
            } catch (NoSuchFieldError e17) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_5_4.ordinal()] = 13;
            } catch (NoSuchFieldError e18) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_6_1.ordinal()] = 14;
            } catch (NoSuchFieldError e19) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_6_2.ordinal()] = 15;
            } catch (NoSuchFieldError e20) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_6_3.ordinal()] = 16;
            } catch (NoSuchFieldError e21) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_6_4.ordinal()] = 17;
            } catch (NoSuchFieldError e22) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_6_5.ordinal()] = 18;
            } catch (NoSuchFieldError e23) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_6_6.ordinal()] = 19;
            } catch (NoSuchFieldError e24) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_7_1.ordinal()] = 20;
            } catch (NoSuchFieldError e25) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_7_2.ordinal()] = 21;
            } catch (NoSuchFieldError e26) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_7_3.ordinal()] = 22;
            } catch (NoSuchFieldError e27) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_7_4.ordinal()] = 23;
            } catch (NoSuchFieldError e28) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_7_5.ordinal()] = 24;
            } catch (NoSuchFieldError e29) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_7_6.ordinal()] = 25;
            } catch (NoSuchFieldError e30) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_8_1.ordinal()] = 26;
            } catch (NoSuchFieldError e31) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_8_2.ordinal()] = 27;
            } catch (NoSuchFieldError e32) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_8_3.ordinal()] = 28;
            } catch (NoSuchFieldError e33) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_8_4.ordinal()] = 29;
            } catch (NoSuchFieldError e34) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_8_5.ordinal()] = 30;
            } catch (NoSuchFieldError e35) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_8_6.ordinal()] = 31;
            } catch (NoSuchFieldError e36) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_8_7.ordinal()] = 32;
            } catch (NoSuchFieldError e37) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_9_1.ordinal()] = 33;
            } catch (NoSuchFieldError e38) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_9_2.ordinal()] = 34;
            } catch (NoSuchFieldError e39) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.DOT_9_3.ordinal()] = 35;
            } catch (NoSuchFieldError e40) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.Share.ordinal()] = 36;
            } catch (NoSuchFieldError e41) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.HAM_1.ordinal()] = 37;
            } catch (NoSuchFieldError e42) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.HAM_2.ordinal()] = 38;
            } catch (NoSuchFieldError e43) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.HAM_3.ordinal()] = 39;
            } catch (NoSuchFieldError e44) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.HAM_4.ordinal()] = 40;
            } catch (NoSuchFieldError e45) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.HAM_5.ordinal()] = 41;
            } catch (NoSuchFieldError e46) {
            }
            try {
                $SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[PiecePlaceEnum.HAM_6.ordinal()] = 42;
            } catch (NoSuchFieldError e47) {
            }
        }
    }

    private void ___________________________1_Initialization() {
    }

    public BoomMenuButton(Context context) {
        super(context);
        this.needToLayout = true;
        this.isBackPressListened = true;
        this.buttonEnum = ButtonEnum.Unknown;
        this.piecePlaceEnum = PiecePlaceEnum.Unknown;
        this.animatingViewNumber = 0;
        this.boomStateEnum = BoomStateEnum.DidHide;
        this.boomButtons = new ArrayList();
        this.boomButtonBuilders = new ArrayList();
        this.buttonPlaceEnum = ButtonPlaceEnum.Unknown;
        this.buttonPlaceAlignmentEnum = ButtonPlaceAlignmentEnum.Center;
        init(context, null);
    }

    public BoomMenuButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.needToLayout = true;
        this.isBackPressListened = true;
        this.buttonEnum = ButtonEnum.Unknown;
        this.piecePlaceEnum = PiecePlaceEnum.Unknown;
        this.animatingViewNumber = 0;
        this.boomStateEnum = BoomStateEnum.DidHide;
        this.boomButtons = new ArrayList();
        this.boomButtonBuilders = new ArrayList();
        this.buttonPlaceEnum = ButtonPlaceEnum.Unknown;
        this.buttonPlaceAlignmentEnum = ButtonPlaceAlignmentEnum.Center;
        init(context, attrs);
    }

    public BoomMenuButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.needToLayout = true;
        this.isBackPressListened = true;
        this.buttonEnum = ButtonEnum.Unknown;
        this.piecePlaceEnum = PiecePlaceEnum.Unknown;
        this.animatingViewNumber = 0;
        this.boomStateEnum = BoomStateEnum.DidHide;
        this.boomButtons = new ArrayList();
        this.boomButtonBuilders = new ArrayList();
        this.buttonPlaceEnum = ButtonPlaceEnum.Unknown;
        this.buttonPlaceAlignmentEnum = ButtonPlaceAlignmentEnum.Center;
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        LayoutInflater.from(context).inflate(C0763R.layout.bmb, this, true);
        initAttrs(context, attrs);
        initShadow();
        initButton();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, C0763R.styleable.BoomMenuButton, 0, 0);
        if (typedArray != null) {
            try {
                this.cacheOptimization = Util.getBoolean(typedArray, C0763R.styleable.BoomMenuButton_bmb_cacheOptimization, C0763R.bool.default_bmb_cacheOptimization);
                this.boomInWholeScreen = Util.getBoolean(typedArray, C0763R.styleable.BoomMenuButton_bmb_boomInWholeScreen, C0763R.bool.default_bmb_boomInWholeScreen);
                this.inList = Util.getBoolean(typedArray, C0763R.styleable.BoomMenuButton_bmb_inList, C0763R.bool.default_bmb_inList);
                this.inFragment = Util.getBoolean(typedArray, C0763R.styleable.BoomMenuButton_bmb_inFragment, C0763R.bool.default_bmb_inFragment);
                this.isBackPressListened = Util.getBoolean(typedArray, C0763R.styleable.BoomMenuButton_bmb_backPressListened, C0763R.bool.default_bmb_backPressListened);
                this.shadowEffect = Util.getBoolean(typedArray, C0763R.styleable.BoomMenuButton_bmb_shadowEffect, C0763R.bool.default_bmb_shadow_effect);
                this.shadowRadius = Util.getDimenSize(typedArray, C0763R.styleable.BoomMenuButton_bmb_shadowRadius, C0763R.dimen.default_bmb_shadow_radius);
                this.shadowOffsetX = Util.getDimenOffset(typedArray, C0763R.styleable.BoomMenuButton_bmb_shadowOffsetX, C0763R.dimen.default_bmb_shadow_offset_x);
                this.shadowOffsetY = Util.getDimenOffset(typedArray, C0763R.styleable.BoomMenuButton_bmb_shadowOffsetY, C0763R.dimen.default_bmb_shadow_offset_y);
                this.shadowColor = Util.getColor(typedArray, C0763R.styleable.BoomMenuButton_bmb_shadowColor, C0763R.color.default_bmb_shadow_color);
                this.buttonRadius = Util.getDimenSize(typedArray, C0763R.styleable.BoomMenuButton_bmb_buttonRadius, C0763R.dimen.default_bmb_button_radius);
                this.buttonEnum = ButtonEnum.getEnum(Util.getInt(typedArray, C0763R.styleable.BoomMenuButton_bmb_buttonEnum, C0763R.integer.default_bmb_button_enum));
                this.backgroundEffect = Util.getBoolean(typedArray, C0763R.styleable.BoomMenuButton_bmb_backgroundEffect, C0763R.bool.default_bmb_background_effect);
                this.rippleEffect = Util.getBoolean(typedArray, C0763R.styleable.BoomMenuButton_bmb_rippleEffect, C0763R.bool.default_bmb_ripple_effect);
                this.normalColor = Util.getColor(typedArray, C0763R.styleable.BoomMenuButton_bmb_normalColor, C0763R.color.default_bmb_normal_color);
                this.highlightedColor = Util.getColor(typedArray, C0763R.styleable.BoomMenuButton_bmb_highlightedColor, C0763R.color.default_bmb_highlighted_color);
                if (this.highlightedColor == 0) {
                    this.highlightedColor = Util.getDarkerColor(this.normalColor);
                }
                this.unableColor = Util.getColor(typedArray, C0763R.styleable.BoomMenuButton_bmb_unableColor, C0763R.color.default_bmb_unable_color);
                if (this.unableColor == 0) {
                    this.unableColor = Util.getLighterColor(this.normalColor);
                }
                this.dotRadius = Util.getDimenSize(typedArray, C0763R.styleable.BoomMenuButton_bmb_dotRadius, C0763R.dimen.default_bmb_dotRadius);
                this.hamWidth = Util.getDimenSize(typedArray, C0763R.styleable.BoomMenuButton_bmb_hamWidth, C0763R.dimen.default_bmb_hamWidth);
                this.hamHeight = Util.getDimenSize(typedArray, C0763R.styleable.BoomMenuButton_bmb_hamHeight, C0763R.dimen.default_bmb_hamHeight);
                this.pieceHorizontalMargin = Util.getDimenOffset(typedArray, C0763R.styleable.BoomMenuButton_bmb_pieceHorizontalMargin, C0763R.dimen.default_bmb_pieceHorizontalMargin);
                this.pieceVerticalMargin = Util.getDimenOffset(typedArray, C0763R.styleable.BoomMenuButton_bmb_pieceVerticalMargin, C0763R.dimen.default_bmb_pieceVerticalMargin);
                this.pieceVerticalMargin = Util.getDimenOffset(typedArray, C0763R.styleable.BoomMenuButton_bmb_pieceInclinedMargin, C0763R.dimen.default_bmb_pieceInclinedMargin);
                this.shareLineLength = Util.getDimenSize(typedArray, C0763R.styleable.BoomMenuButton_bmb_sharedLineLength, C0763R.dimen.default_bmb_sharedLineLength);
                this.shareLine1Color = Util.getColor(typedArray, C0763R.styleable.BoomMenuButton_bmb_shareLine1Color, C0763R.color.default_bmb_shareLine1Color);
                this.shareLine2Color = Util.getColor(typedArray, C0763R.styleable.BoomMenuButton_bmb_shareLine2Color, C0763R.color.default_bmb_shareLine2Color);
                this.shareLineWidth = Util.getDimenSize(typedArray, C0763R.styleable.BoomMenuButton_bmb_shareLineWidth, C0763R.dimen.default_bmb_shareLineWidth);
                this.piecePlaceEnum = PiecePlaceEnum.getEnum(Util.getInt(typedArray, C0763R.styleable.BoomMenuButton_bmb_piecePlaceEnum, C0763R.integer.default_bmb_pieceEnum));
                this.dimColor = Util.getColor(typedArray, C0763R.styleable.BoomMenuButton_bmb_dimColor, C0763R.color.default_bmb_dimColor);
                this.showDuration = (long) Util.getInt(typedArray, C0763R.styleable.BoomMenuButton_bmb_showDuration, C0763R.integer.default_bmb_showDuration);
                this.showDelay = (long) Util.getInt(typedArray, C0763R.styleable.BoomMenuButton_bmb_showDelay, C0763R.integer.default_bmb_showDelay);
                this.hideDuration = (long) Util.getInt(typedArray, C0763R.styleable.BoomMenuButton_bmb_hideDuration, C0763R.integer.default_bmb_hideDuration);
                this.hideDelay = (long) Util.getInt(typedArray, C0763R.styleable.BoomMenuButton_bmb_hideDelay, C0763R.integer.default_bmb_hideDelay);
                this.cancelable = Util.getBoolean(typedArray, C0763R.styleable.BoomMenuButton_bmb_cancelable, C0763R.bool.default_bmb_cancelable);
                this.autoHide = Util.getBoolean(typedArray, C0763R.styleable.BoomMenuButton_bmb_autoHide, C0763R.bool.default_bmb_autoHide);
                this.orderEnum = OrderEnum.getEnum(Util.getInt(typedArray, C0763R.styleable.BoomMenuButton_bmb_orderEnum, C0763R.integer.default_bmb_orderEnum));
                this.frames = Util.getInt(typedArray, C0763R.styleable.BoomMenuButton_bmb_frames, C0763R.integer.default_bmb_frames);
                this.boomEnum = BoomEnum.getEnum(Util.getInt(typedArray, C0763R.styleable.BoomMenuButton_bmb_boomEnum, C0763R.integer.default_bmb_boomEnum));
                this.showMoveEaseEnum = EaseEnum.getEnum(Util.getInt(typedArray, C0763R.styleable.BoomMenuButton_bmb_showMoveEaseEnum, C0763R.integer.default_bmb_showMoveEaseEnum));
                this.showScaleEaseEnum = EaseEnum.getEnum(Util.getInt(typedArray, C0763R.styleable.BoomMenuButton_bmb_showScaleEaseEnum, C0763R.integer.default_bmb_showScaleEaseEnum));
                this.showRotateEaseEnum = EaseEnum.getEnum(Util.getInt(typedArray, C0763R.styleable.BoomMenuButton_bmb_showRotateEaseEnum, C0763R.integer.default_bmb_showRotateEaseEnum));
                this.hideMoveEaseEnum = EaseEnum.getEnum(Util.getInt(typedArray, C0763R.styleable.BoomMenuButton_bmb_hideMoveEaseEnum, C0763R.integer.default_bmb_hideMoveEaseEnum));
                this.hideScaleEaseEnum = EaseEnum.getEnum(Util.getInt(typedArray, C0763R.styleable.BoomMenuButton_bmb_hideScaleEaseEnum, C0763R.integer.default_bmb_hideScaleEaseEnum));
                this.hideRotateEaseEnum = EaseEnum.getEnum(Util.getInt(typedArray, C0763R.styleable.BoomMenuButton_bmb_hideRotateEaseEnum, C0763R.integer.default_bmb_hideRotateEaseEnum));
                this.rotateDegree = Util.getInt(typedArray, C0763R.styleable.BoomMenuButton_bmb_rotateDegree, C0763R.integer.default_bmb_rotateDegree);
                this.buttonPlaceEnum = ButtonPlaceEnum.getEnum(Util.getInt(typedArray, C0763R.styleable.BoomMenuButton_bmb_buttonPlaceEnum, C0763R.integer.default_bmb_buttonPlaceEnum));
                this.buttonPlaceAlignmentEnum = ButtonPlaceAlignmentEnum.getEnum(Util.getInt(typedArray, C0763R.styleable.BoomMenuButton_bmb_buttonPlaceAlignmentEnum, C0763R.integer.default_bmb_buttonPlaceAlignmentEnum));
                this.buttonHorizontalMargin = (float) Util.getDimenOffset(typedArray, C0763R.styleable.BoomMenuButton_bmb_buttonHorizontalMargin, C0763R.dimen.default_bmb_buttonHorizontalMargin);
                this.buttonVerticalMargin = (float) Util.getDimenOffset(typedArray, C0763R.styleable.BoomMenuButton_bmb_buttonVerticalMargin, C0763R.dimen.default_bmb_buttonVerticalMargin);
                this.buttonInclinedMargin = (float) Util.getDimenOffset(typedArray, C0763R.styleable.BoomMenuButton_bmb_buttonInclinedMargin, C0763R.dimen.default_bmb_buttonInclinedMargin);
                this.buttonTopMargin = (float) Util.getDimenOffset(typedArray, C0763R.styleable.BoomMenuButton_bmb_buttonTopMargin, C0763R.dimen.default_bmb_buttonTopMargin);
                this.buttonBottomMargin = (float) Util.getDimenOffset(typedArray, C0763R.styleable.BoomMenuButton_bmb_buttonBottomMargin, C0763R.dimen.default_bmb_buttonBottomMargin);
                this.buttonLeftMargin = (float) Util.getDimenOffset(typedArray, C0763R.styleable.BoomMenuButton_bmb_buttonLeftMargin, C0763R.dimen.default_bmb_buttonLeftMargin);
                this.buttonRightMargin = (float) Util.getDimenOffset(typedArray, C0763R.styleable.BoomMenuButton_bmb_buttonRightMargin, C0763R.dimen.default_bmb_buttonRightMargin);
                int valueFromResource = Util.getDimenOffset(typedArray, C0763R.styleable.BoomMenuButton_bmb_bottomHamButtonTopMargin, C0763R.dimen.default_bmb_bottomHamButtonTopMargin);
                if (valueFromResource == 0) {
                    this.bottomHamButtonTopMargin = null;
                } else {
                    this.bottomHamButtonTopMargin = Float.valueOf((float) valueFromResource);
                }
                typedArray.recycle();
            } catch (Throwable th) {
                typedArray.recycle();
            }
        }
    }

    private void initShadow() {
        if (this.shadow == null) {
            this.shadow = (BMBShadow) findViewById(C0763R.id.shadow);
        }
        boolean hasShadow = this.shadowEffect && this.backgroundEffect && !this.inList;
        this.shadow.setShadowEffect(hasShadow);
        if (hasShadow) {
            this.shadow.setShadowOffsetX(this.shadowOffsetX);
            this.shadow.setShadowOffsetY(this.shadowOffsetY);
            this.shadow.setShadowColor(this.shadowColor);
            this.shadow.setShadowRadius(this.shadowRadius);
            this.shadow.setShadowCornerRadius(this.shadowRadius + this.buttonRadius);
            return;
        }
        this.shadow.clearShadow();
    }

    private void initButton() {
        if (this.button == null) {
            this.button = (FrameLayout) findViewById(C0763R.id.button);
        }
        this.button.setOnClickListener(new C07511());
        setButtonSize();
        setButtonBackground();
    }

    private void setButtonSize() {
        LayoutParams params = (LayoutParams) this.button.getLayoutParams();
        params.width = this.buttonRadius * 2;
        params.height = this.buttonRadius * 2;
        this.button.setLayoutParams(params);
    }

    private void setButtonBackground() {
        if (!this.backgroundEffect || this.inList) {
            if (VERSION.SDK_INT >= 21) {
                Util.setDrawable(this.button, Util.getSystemDrawable(this.context, 16843868));
            } else {
                Util.setDrawable(this.button, Util.getSystemDrawable(this.context, 16843534));
            }
        } else if (!this.rippleEffect || VERSION.SDK_INT < 21) {
            Util.setDrawable(this.button, Util.getOvalStateListBitmapDrawable(this.button, this.buttonRadius, this.normalColor, this.highlightedColor, this.unableColor));
        } else {
            Util.setDrawable(this.button, new RippleDrawable(ColorStateList.valueOf(this.highlightedColor), Util.getOvalDrawable(this.button, this.normalColor), null));
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (4 != keyCode || !this.isBackPressListened || (this.boomStateEnum != BoomStateEnum.WillShow && this.boomStateEnum != BoomStateEnum.DidShow)) {
            return super.onKeyDown(keyCode, event);
        }
        reboom();
        return true;
    }

    private void ___________________________2_Place_Pieces() {
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (this.needToLayout) {
            delayToDoLayoutJobs();
        }
        this.needToLayout = false;
    }

    private void doLayoutJobs() {
        ExceptionManager.judge(this.piecePlaceEnum, this.buttonPlaceEnum, this.buttonEnum, this.boomEnum, this.boomButtonBuilders);
        removePieces();
        createPieces();
        placeShareLinesView();
        placePieces();
        placePiecesAtPositions();
        if (!(this.inList || this.inFragment)) {
            calculateStartPositions();
        }
        setShareLinesViewData();
    }

    private void removePieces() {
        this.button.removeAllViews();
        if (this.pieces != null) {
            this.pieces.clear();
        }
    }

    private void createPieces() {
        calculatePiecePositions();
        int pieceNumber = pieceNumber();
        this.pieces = new ArrayList(pieceNumber);
        for (int i = 0; i < pieceNumber; i++) {
            this.pieces.add(PiecePlaceManager.createPiece(this.context, this.piecePlaceEnum, ((BoomButtonBuilder) this.boomButtonBuilders.get(i)).pieceColor(this.context)));
        }
    }

    private void placePieces() {
        ArrayList<Integer> indexes;
        if (this.piecePlaceEnum == PiecePlaceEnum.Share) {
            indexes = AnimationManager.getOrderIndex(OrderEnum.DEFAULT, this.pieces.size());
        } else {
            indexes = AnimationManager.getOrderIndex(this.orderEnum, this.pieces.size());
        }
        for (int i = indexes.size() - 1; i >= 0; i--) {
            this.button.addView((View) this.pieces.get(((Integer) indexes.get(i)).intValue()));
        }
    }

    private void placePiecesAtPositions() {
        int w;
        int h;
        int pieceNumber = pieceNumber();
        switch (C07599.$SwitchMap$com$nightonke$boommenu$Piece$PiecePlaceEnum[this.piecePlaceEnum.ordinal()]) {
            case OnSubscribeConcatMap.BOUNDARY /*1*/:
            case OnSubscribeConcatMap.END /*2*/:
            case ConnectionResult.SERVICE_DISABLED /*3*/:
            case ConnectionResult.SIGN_IN_REQUIRED /*4*/:
            case ConnectionResult.INVALID_ACCOUNT /*5*/:
            case ConnectionResult.RESOLUTION_REQUIRED /*6*/:
            case ConnectionResult.NETWORK_ERROR /*7*/:
            case ConnectionResult.INTERNAL_ERROR /*8*/:
            case ConnectionResult.SERVICE_INVALID /*9*/:
            case ConnectionResult.DEVELOPER_ERROR /*10*/:
            case ConnectionResult.LICENSE_CHECK_FAILED /*11*/:
            case C0801R.styleable.Toolbar_titleTextAppearance /*12*/:
            case ConnectionResult.CANCELED /*13*/:
            case ConnectionResult.TIMEOUT /*14*/:
            case ConnectionResult.INTERRUPTED /*15*/:
            case ConnectionResult.API_UNAVAILABLE /*16*/:
            case ConnectionResult.SIGN_IN_FAILED /*17*/:
            case ConnectionResult.SERVICE_UPDATING /*18*/:
            case ConnectionResult.SERVICE_MISSING_PERMISSION /*19*/:
            case ConnectionResult.RESTRICTED_PROFILE /*20*/:
            case C0801R.styleable.AppCompatTheme_actionBarWidgetTheme /*21*/:
            case C0801R.styleable.Toolbar_collapseIcon /*22*/:
            case C0801R.styleable.Toolbar_collapseContentDescription /*23*/:
            case C0801R.styleable.Toolbar_navigationIcon /*24*/:
            case MessagingStyle.MAXIMUM_RETAINED_MESSAGES /*25*/:
            case C0801R.styleable.Toolbar_logoDescription /*26*/:
            case C0801R.styleable.Toolbar_titleTextColor /*27*/:
            case C0801R.styleable.Toolbar_subtitleTextColor /*28*/:
            case C0801R.styleable.AppCompatTheme_actionModeBackground /*29*/:
            case C0801R.styleable.AppCompatTheme_actionModeSplitBackground /*30*/:
            case C0801R.styleable.AppCompatTheme_actionModeCloseDrawable /*31*/:
            case ItemTouchHelper.END /*32*/:
            case C0801R.styleable.AppCompatTheme_actionModeCopyDrawable /*33*/:
            case C0801R.styleable.AppCompatTheme_actionModePasteDrawable /*34*/:
            case C0801R.styleable.AppCompatTheme_actionModeSelectAllDrawable /*35*/:
            case C0801R.styleable.AppCompatTheme_actionModeShareDrawable /*36*/:
                w = this.dotRadius * 2;
                h = this.dotRadius * 2;
                break;
            case LangUtils.HASH_OFFSET /*37*/:
            case C0801R.styleable.AppCompatTheme_actionModeWebSearchDrawable /*38*/:
            case C0801R.styleable.AppCompatTheme_actionModePopupWindowStyle /*39*/:
            case C0801R.styleable.AppCompatTheme_textAppearanceLargePopupMenu /*40*/:
            case C0801R.styleable.AppCompatTheme_textAppearanceSmallPopupMenu /*41*/:
            case C0784R.styleable.AppCompatTheme_textAppearancePopupMenuHeader /*42*/:
                w = this.hamWidth;
                h = this.hamHeight;
                break;
            default:
                throw new RuntimeException("Unknown piece-place-enum!");
        }
        for (int i = 0; i < pieceNumber; i++) {
            ((BoomPiece) this.pieces.get(i)).place(((Point) this.piecePositions.get(i)).x, ((Point) this.piecePositions.get(i)).y, w, h);
        }
    }

    private void calculatePiecePositions() {
        switch (C07599.$SwitchMap$com$nightonke$boommenu$ButtonEnum[this.buttonEnum.ordinal()]) {
            case OnSubscribeConcatMap.BOUNDARY /*1*/:
            case OnSubscribeConcatMap.END /*2*/:
            case ConnectionResult.SERVICE_DISABLED /*3*/:
                if (this.piecePlaceEnum == PiecePlaceEnum.Share) {
                    this.piecePositions = PiecePlaceManager.getShareDotPositions(new Point(this.button.getWidth(), this.button.getHeight()), this.dotRadius, this.boomButtonBuilders.size(), this.shareLineLength);
                } else {
                    this.piecePositions = PiecePlaceManager.getDotPositions(this.piecePlaceEnum, new Point(this.button.getWidth(), this.button.getHeight()), this.dotRadius, this.pieceHorizontalMargin, this.pieceVerticalMargin, this.pieceInclinedMargin);
                }
            case ConnectionResult.SIGN_IN_REQUIRED /*4*/:
                this.piecePositions = PiecePlaceManager.getHamPositions(this.piecePlaceEnum, new Point(this.button.getWidth(), this.button.getHeight()), this.hamWidth, this.hamHeight, this.pieceVerticalMargin);
            case ConnectionResult.INVALID_ACCOUNT /*5*/:
                throw new RuntimeException("The button-enum is unknown!");
            default:
        }
    }

    private void ___________________________3_Animation() {
    }

    public void boom() {
        innerBoom(false);
    }

    public void boomImmediately() {
        innerBoom(true);
    }

    private void innerBoom(boolean immediately) {
        ExceptionManager.judge(this.piecePlaceEnum, this.buttonPlaceEnum, this.buttonEnum, this.boomEnum, this.boomButtonBuilders);
        if (!isAnimating() && this.boomStateEnum == BoomStateEnum.DidHide) {
            this.boomStateEnum = BoomStateEnum.WillShow;
            if (this.onBoomListener != null) {
                this.onBoomListener.onBoomWillShow();
            }
            if (this.inList || this.inFragment) {
                calculateStartPositions();
            }
            createButtons();
            dimBackground(immediately);
            startShowAnimations(immediately);
            if (this.isBackPressListened) {
                setFocusable(true);
                setFocusableInTouchMode(true);
                requestFocus();
            }
        }
    }

    public void reboom() {
        innerReboom(false);
    }

    public void reboomImmediately() {
        innerReboom(true);
    }

    private void innerReboom(boolean immediately) {
        if (!isAnimating() && this.boomStateEnum == BoomStateEnum.DidShow) {
            this.boomStateEnum = BoomStateEnum.WillHide;
            if (this.onBoomListener != null) {
                this.onBoomListener.onBoomWillHide();
            }
            lightBackground(immediately);
            startHideAnimations(immediately);
            if (this.isBackPressListened) {
                setFocusable(false);
                setFocusableInTouchMode(false);
            }
        }
    }

    private void dimBackground(boolean immediately) {
        long j;
        createBackground();
        Util.setVisibility(0, this.background);
        Object obj = this.background;
        String str = "backgroundColor";
        if (immediately) {
            j = 1;
        } else {
            j = this.showDuration + (this.showDelay * ((long) (this.pieces.size() - 1)));
        }
        AnimationManager.animate(obj, str, 0, j, new ArgbEvaluator(), new C07522(), 0, this.dimColor);
        if (this.piecePlaceEnum == PiecePlaceEnum.Share) {
            obj = this.shareLinesView;
            str = "showProcess";
            if (immediately) {
                j = 1;
            } else {
                j = this.showDuration + (this.showDelay * ((long) (this.pieces.size() - 1)));
            }
            AnimationManager.animate(obj, str, 0, j, Ease.getInstance(EaseEnum.Linear), 0.0f, 1.0f);
        }
    }

    private void lightBackground(boolean immediately) {
        long j;
        createBackground();
        Object obj = this.background;
        String str = "backgroundColor";
        if (immediately) {
            j = 1;
        } else {
            j = this.hideDuration + (this.hideDelay * ((long) (this.pieces.size() - 1)));
        }
        AnimationManager.animate(obj, str, 0, j, new ArgbEvaluator(), new C07533(), this.dimColor, 0);
        if (this.piecePlaceEnum == PiecePlaceEnum.Share) {
            obj = this.shareLinesView;
            str = "hideProcess";
            if (immediately) {
                j = 1;
            } else {
                j = this.hideDuration + (this.hideDelay * ((long) (this.pieces.size() - 1)));
            }
            AnimationManager.animate(obj, str, 0, j, Ease.getInstance(EaseEnum.Linear), 0.0f, 1.0f);
        }
    }

    private void startShowAnimations(boolean immediately) {
        ArrayList<Integer> indexes;
        if (this.background != null) {
            this.background.removeAllViews();
        }
        calculateEndPositions();
        if (this.piecePlaceEnum == PiecePlaceEnum.Share) {
            indexes = AnimationManager.getOrderIndex(OrderEnum.DEFAULT, this.pieces.size());
        } else {
            indexes = AnimationManager.getOrderIndex(this.orderEnum, this.pieces.size());
        }
        for (int i = indexes.size() - 1; i >= 0; i--) {
            int index = ((Integer) indexes.get(i)).intValue();
            BoomButton boomButton = (BoomButton) this.boomButtons.get(index);
            Point startPosition = new Point((int) (((float) ((Point) this.startPositions.get(index)).x) - boomButton.centerPoint.x), (int) (((float) ((Point) this.startPositions.get(index)).y) - boomButton.centerPoint.y));
            putBoomButtonInBackground(boomButton, startPosition);
            startEachShowAnimation((BoomPiece) this.pieces.get(index), boomButton, startPosition, new Point((Point) this.endPositions.get(index)), i, immediately);
        }
    }

    private void startHideAnimations(boolean immediately) {
        ArrayList<Integer> indexes;
        if (this.piecePlaceEnum == PiecePlaceEnum.Share) {
            indexes = AnimationManager.getOrderIndex(OrderEnum.REVERSE, this.pieces.size());
        } else {
            indexes = AnimationManager.getOrderIndex(this.orderEnum, this.pieces.size());
        }
        Iterator it = indexes.iterator();
        while (it.hasNext()) {
            ((BoomButton) this.boomButtons.get(((Integer) it.next()).intValue())).bringToFront();
        }
        for (int i = 0; i < indexes.size(); i++) {
            int index = ((Integer) indexes.get(i)).intValue();
            BoomButton boomButton = (BoomButton) this.boomButtons.get(index);
            Point startPosition = new Point((int) (((float) ((Point) this.startPositions.get(index)).x) - boomButton.centerPoint.x), (int) (((float) ((Point) this.startPositions.get(index)).y) - boomButton.centerPoint.y));
            startEachHideAnimation((BoomPiece) this.pieces.get(index), boomButton, new Point((Point) this.endPositions.get(index)), startPosition, i, immediately);
        }
    }

    private void startEachShowAnimation(BoomPiece piece, BoomButton boomButton, Point startPosition, Point endPosition, int delayFactor, boolean immediately) {
        if (isBatterySaveModeTurnOn()) {
            post(new C07544(piece, boomButton, startPosition, endPosition, delayFactor, immediately));
        } else {
            innerStartEachShowAnimation(piece, boomButton, startPosition, endPosition, delayFactor, immediately);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void innerStartEachShowAnimation(com.nightonke.boommenu.Piece.BoomPiece r23, com.nightonke.boommenu.BoomButtons.BoomButton r24, android.graphics.Point r25, android.graphics.Point r26, int r27, boolean r28) {
        /*
        r22 = this;
        r0 = r22;
        r4 = r0.animatingViewNumber;
        r4 = r4 + 1;
        r0 = r22;
        r0.animatingViewNumber = r4;
        r0 = r22;
        r4 = r0.frames;
        r4 = r4 + 1;
        r9 = new float[r4];
        r0 = r22;
        r4 = r0.frames;
        r4 = r4 + 1;
        r10 = new float[r4];
        r4 = r23.getWidth();
        r4 = (float) r4;
        r5 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r4 = r4 * r5;
        r5 = r24.contentWidth();
        r5 = (float) r5;
        r11 = r4 / r5;
        r4 = r23.getHeight();
        r4 = (float) r4;
        r5 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r4 = r4 * r5;
        r5 = r24.contentHeight();
        r5 = (float) r5;
        r21 = r4 / r5;
        if (r28 == 0) goto L_0x0144;
    L_0x003a:
        r14 = 1;
    L_0x003c:
        if (r28 == 0) goto L_0x014f;
    L_0x003e:
        r16 = 1;
    L_0x0040:
        r24.setSelfScaleAnchorPoints();
        r0 = r24;
        r0.setScaleX(r11);
        r0 = r24;
        r1 = r21;
        r0.setScaleY(r1);
        r4 = 0;
        r0 = r24;
        r0.setClickable(r4);
        r0 = r22;
        r4 = r0.boomEnum;
        r5 = new android.graphics.Point;
        r0 = r22;
        r6 = r0.background;
        r6 = r6.getLayoutParams();
        r6 = r6.width;
        r0 = r22;
        r7 = r0.background;
        r7 = r7.getLayoutParams();
        r7 = r7.height;
        r5.<init>(r6, r7);
        r0 = r22;
        r6 = r0.frames;
        r7 = r25;
        r8 = r26;
        com.nightonke.boommenu.Animation.AnimationManager.calculateShowXY(r4, r5, r6, r7, r8, r9, r10);
        r4 = r24.isNeededColorAnimation();
        if (r4 == 0) goto L_0x00a7;
    L_0x0083:
        r4 = r24.prepareColorTransformAnimation();
        if (r4 == 0) goto L_0x0157;
    L_0x0089:
        r13 = "rippleButtonColor";
        r18 = com.nightonke.boommenu.Animation.ShowRgbEvaluator.getInstance();
        r4 = 2;
        r0 = new int[r4];
        r19 = r0;
        r4 = 0;
        r5 = r24.pieceColor();
        r19[r4] = r5;
        r4 = 1;
        r5 = r24.buttonColor();
        r19[r4] = r5;
        r12 = r24;
        com.nightonke.boommenu.Animation.AnimationManager.animate(r12, r13, r14, r16, r18, r19);
    L_0x00a7:
        r13 = "x";
        r0 = r22;
        r4 = r0.showMoveEaseEnum;
        r18 = com.nightonke.boommenu.Animation.Ease.getInstance(r4);
        r12 = r24;
        r19 = r9;
        com.nightonke.boommenu.Animation.AnimationManager.animate(r12, r13, r14, r16, r18, r19);
        r13 = "y";
        r0 = r22;
        r4 = r0.showMoveEaseEnum;
        r18 = com.nightonke.boommenu.Animation.Ease.getInstance(r4);
        r12 = r24;
        r19 = r10;
        com.nightonke.boommenu.Animation.AnimationManager.animate(r12, r13, r14, r16, r18, r19);
        r0 = r22;
        r4 = r0.showRotateEaseEnum;
        r18 = com.nightonke.boommenu.Animation.Ease.getInstance(r4);
        r4 = 2;
        r0 = new float[r4];
        r19 = r0;
        r4 = 0;
        r5 = 0;
        r19[r4] = r5;
        r4 = 1;
        r0 = r22;
        r5 = r0.rotateDegree;
        r5 = (float) r5;
        r19[r4] = r5;
        r13 = r24;
        com.nightonke.boommenu.Animation.AnimationManager.rotate(r13, r14, r16, r18, r19);
        r13 = "alpha";
        r4 = 2;
        r0 = new float[r4];
        r18 = r0;
        r18 = {0, 1065353216};
        r4 = com.nightonke.boommenu.Animation.EaseEnum.Linear;
        r19 = com.nightonke.boommenu.Animation.Ease.getInstance(r4);
        r20 = r24.goneViews();
        com.nightonke.boommenu.Animation.AnimationManager.animate(r13, r14, r16, r18, r19, r20);
        r13 = "scaleX";
        r0 = r22;
        r4 = r0.showScaleEaseEnum;
        r18 = com.nightonke.boommenu.Animation.Ease.getInstance(r4);
        r4 = 2;
        r0 = new float[r4];
        r19 = r0;
        r4 = 0;
        r19[r4] = r11;
        r4 = 1;
        r5 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r19[r4] = r5;
        r12 = r24;
        com.nightonke.boommenu.Animation.AnimationManager.animate(r12, r13, r14, r16, r18, r19);
        r13 = "scaleY";
        r0 = r22;
        r4 = r0.showScaleEaseEnum;
        r18 = com.nightonke.boommenu.Animation.Ease.getInstance(r4);
        r19 = new com.nightonke.boommenu.BoomMenuButton$5;
        r0 = r19;
        r1 = r22;
        r2 = r23;
        r3 = r24;
        r0.<init>(r2, r3);
        r4 = 2;
        r0 = new float[r4];
        r20 = r0;
        r4 = 0;
        r20[r4] = r21;
        r4 = 1;
        r5 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r20[r4] = r5;
        r12 = r24;
        com.nightonke.boommenu.Animation.AnimationManager.animate(r12, r13, r14, r16, r18, r19, r20);
        return;
    L_0x0144:
        r0 = r22;
        r4 = r0.showDelay;
        r0 = r27;
        r6 = (long) r0;
        r14 = r4 * r6;
        goto L_0x003c;
    L_0x014f:
        r0 = r22;
        r0 = r0.showDuration;
        r16 = r0;
        goto L_0x0040;
    L_0x0157:
        r13 = "nonRippleButtonColor";
        r18 = com.nightonke.boommenu.Animation.ShowRgbEvaluator.getInstance();
        r4 = 2;
        r0 = new int[r4];
        r19 = r0;
        r4 = 0;
        r5 = r24.pieceColor();
        r19[r4] = r5;
        r4 = 1;
        r5 = r24.buttonColor();
        r19[r4] = r5;
        r12 = r24;
        com.nightonke.boommenu.Animation.AnimationManager.animate(r12, r13, r14, r16, r18, r19);
        goto L_0x00a7;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.nightonke.boommenu.BoomMenuButton.innerStartEachShowAnimation(com.nightonke.boommenu.Piece.BoomPiece, com.nightonke.boommenu.BoomButtons.BoomButton, android.graphics.Point, android.graphics.Point, int, boolean):void");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void startEachHideAnimation(com.nightonke.boommenu.Piece.BoomPiece r23, com.nightonke.boommenu.BoomButtons.BoomButton r24, android.graphics.Point r25, android.graphics.Point r26, int r27, boolean r28) {
        /*
        r22 = this;
        r0 = r22;
        r4 = r0.animatingViewNumber;
        r4 = r4 + 1;
        r0 = r22;
        r0.animatingViewNumber = r4;
        r0 = r22;
        r4 = r0.frames;
        r4 = r4 + 1;
        r9 = new float[r4];
        r0 = r22;
        r4 = r0.frames;
        r4 = r4 + 1;
        r10 = new float[r4];
        r4 = r23.getWidth();
        r4 = (float) r4;
        r5 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r4 = r4 * r5;
        r5 = r24.contentWidth();
        r5 = (float) r5;
        r11 = r4 / r5;
        r4 = r23.getHeight();
        r4 = (float) r4;
        r5 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r4 = r4 * r5;
        r5 = r24.contentHeight();
        r5 = (float) r5;
        r21 = r4 / r5;
        if (r28 == 0) goto L_0x0135;
    L_0x003a:
        r14 = 1;
    L_0x003c:
        if (r28 == 0) goto L_0x0140;
    L_0x003e:
        r16 = 1;
    L_0x0040:
        r4 = 0;
        r0 = r24;
        r0.setClickable(r4);
        r0 = r22;
        r4 = r0.boomEnum;
        r5 = new android.graphics.Point;
        r0 = r22;
        r6 = r0.background;
        r6 = r6.getLayoutParams();
        r6 = r6.width;
        r0 = r22;
        r7 = r0.background;
        r7 = r7.getLayoutParams();
        r7 = r7.height;
        r5.<init>(r6, r7);
        r0 = r22;
        r6 = r0.frames;
        r7 = r25;
        r8 = r26;
        com.nightonke.boommenu.Animation.AnimationManager.calculateHideXY(r4, r5, r6, r7, r8, r9, r10);
        r4 = r24.isNeededColorAnimation();
        if (r4 == 0) goto L_0x0098;
    L_0x0074:
        r4 = r24.prepareColorTransformAnimation();
        if (r4 == 0) goto L_0x0148;
    L_0x007a:
        r13 = "rippleButtonColor";
        r18 = com.nightonke.boommenu.Animation.HideRgbEvaluator.getInstance();
        r4 = 2;
        r0 = new int[r4];
        r19 = r0;
        r4 = 0;
        r5 = r24.buttonColor();
        r19[r4] = r5;
        r4 = 1;
        r5 = r24.pieceColor();
        r19[r4] = r5;
        r12 = r24;
        com.nightonke.boommenu.Animation.AnimationManager.animate(r12, r13, r14, r16, r18, r19);
    L_0x0098:
        r13 = "x";
        r0 = r22;
        r4 = r0.hideMoveEaseEnum;
        r18 = com.nightonke.boommenu.Animation.Ease.getInstance(r4);
        r12 = r24;
        r19 = r9;
        com.nightonke.boommenu.Animation.AnimationManager.animate(r12, r13, r14, r16, r18, r19);
        r13 = "y";
        r0 = r22;
        r4 = r0.hideMoveEaseEnum;
        r18 = com.nightonke.boommenu.Animation.Ease.getInstance(r4);
        r12 = r24;
        r19 = r10;
        com.nightonke.boommenu.Animation.AnimationManager.animate(r12, r13, r14, r16, r18, r19);
        r0 = r22;
        r4 = r0.hideRotateEaseEnum;
        r18 = com.nightonke.boommenu.Animation.Ease.getInstance(r4);
        r4 = 2;
        r0 = new float[r4];
        r19 = r0;
        r4 = 0;
        r5 = 0;
        r19[r4] = r5;
        r4 = 1;
        r0 = r22;
        r5 = r0.rotateDegree;
        r5 = (float) r5;
        r19[r4] = r5;
        r13 = r24;
        com.nightonke.boommenu.Animation.AnimationManager.rotate(r13, r14, r16, r18, r19);
        r13 = "alpha";
        r4 = 2;
        r0 = new float[r4];
        r18 = r0;
        r18 = {1065353216, 0};
        r4 = com.nightonke.boommenu.Animation.EaseEnum.Linear;
        r19 = com.nightonke.boommenu.Animation.Ease.getInstance(r4);
        r20 = r24.goneViews();
        com.nightonke.boommenu.Animation.AnimationManager.animate(r13, r14, r16, r18, r19, r20);
        r13 = "scaleX";
        r0 = r22;
        r4 = r0.hideScaleEaseEnum;
        r18 = com.nightonke.boommenu.Animation.Ease.getInstance(r4);
        r4 = 2;
        r0 = new float[r4];
        r19 = r0;
        r4 = 0;
        r5 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r19[r4] = r5;
        r4 = 1;
        r19[r4] = r11;
        r12 = r24;
        com.nightonke.boommenu.Animation.AnimationManager.animate(r12, r13, r14, r16, r18, r19);
        r13 = "scaleY";
        r0 = r22;
        r4 = r0.hideScaleEaseEnum;
        r18 = com.nightonke.boommenu.Animation.Ease.getInstance(r4);
        r19 = new com.nightonke.boommenu.BoomMenuButton$6;
        r0 = r19;
        r1 = r22;
        r2 = r24;
        r3 = r23;
        r0.<init>(r2, r3);
        r4 = 2;
        r0 = new float[r4];
        r20 = r0;
        r4 = 0;
        r5 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r20[r4] = r5;
        r4 = 1;
        r20[r4] = r21;
        r12 = r24;
        com.nightonke.boommenu.Animation.AnimationManager.animate(r12, r13, r14, r16, r18, r19, r20);
        return;
    L_0x0135:
        r0 = r22;
        r4 = r0.hideDelay;
        r0 = r27;
        r6 = (long) r0;
        r14 = r4 * r6;
        goto L_0x003c;
    L_0x0140:
        r0 = r22;
        r0 = r0.hideDuration;
        r16 = r0;
        goto L_0x0040;
    L_0x0148:
        r13 = "nonRippleButtonColor";
        r18 = com.nightonke.boommenu.Animation.HideRgbEvaluator.getInstance();
        r4 = 2;
        r0 = new int[r4];
        r19 = r0;
        r4 = 0;
        r5 = r24.buttonColor();
        r19[r4] = r5;
        r4 = 1;
        r5 = r24.pieceColor();
        r19[r4] = r5;
        r12 = r24;
        com.nightonke.boommenu.Animation.AnimationManager.animate(r12, r13, r14, r16, r18, r19);
        goto L_0x0098;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.nightonke.boommenu.BoomMenuButton.startEachHideAnimation(com.nightonke.boommenu.Piece.BoomPiece, com.nightonke.boommenu.BoomButtons.BoomButton, android.graphics.Point, android.graphics.Point, int, boolean):void");
    }

    private void ___________________________4_Support_Methods() {
    }

    private void createBackground() {
        if (this.background == null) {
            ViewGroup rootView = getParentView();
            this.background = new FrameLayout(this.context);
            this.background.setLayoutParams(new ViewGroup.LayoutParams(rootView.getWidth(), rootView.getHeight()));
            this.background.setBackgroundColor(0);
            this.background.setOnClickListener(new C07577());
            this.background.setMotionEventSplittingEnabled(false);
            rootView.addView(this.background);
        }
    }

    private ViewGroup getParentView() {
        if (!this.boomInWholeScreen) {
            return (ViewGroup) getParent();
        }
        Activity activity = Util.scanForActivity(this.context);
        if (activity == null) {
            return (ViewGroup) getParent();
        }
        return (ViewGroup) activity.getWindow().getDecorView();
    }

    private void clearBackground() {
        Util.setVisibility(8, this.background);
        if (!this.cacheOptimization || this.inList || this.inFragment) {
            this.background.removeAllViews();
            ((ViewGroup) this.background.getParent()).removeView(this.background);
            this.background = null;
        }
    }

    private void createButtons() {
        this.boomButtons = new ArrayList(this.pieces.size());
        int buttonNumber = this.pieces.size();
        int i;
        switch (C07599.$SwitchMap$com$nightonke$boommenu$ButtonEnum[this.buttonEnum.ordinal()]) {
            case OnSubscribeConcatMap.BOUNDARY /*1*/:
                for (i = 0; i < buttonNumber; i++) {
                    Builder builder = (Builder) this.boomButtonBuilders.get(i);
                    builder.innerListener(this).index(i);
                    this.boomButtons.add(builder.build(this.context));
                    this.simpleCircleButtonRadius = (float) builder.getButtonRadius();
                }
            case OnSubscribeConcatMap.END /*2*/:
                for (i = 0; i < buttonNumber; i++) {
                    TextInsideCircleButton.Builder builder2 = (TextInsideCircleButton.Builder) this.boomButtonBuilders.get(i);
                    builder2.innerListener(this).index(i);
                    this.boomButtons.add(builder2.build(this.context));
                    this.textInsideCircleButtonRadius = (float) builder2.getButtonRadius();
                }
            case ConnectionResult.SERVICE_DISABLED /*3*/:
                for (i = 0; i < buttonNumber; i++) {
                    TextOutsideCircleButton.Builder builder3 = (TextOutsideCircleButton.Builder) this.boomButtonBuilders.get(i);
                    builder3.innerListener(this).index(i);
                    this.boomButtons.add(builder3.build(this.context));
                    this.textOutsideCircleButtonWidth = (float) builder3.getButtonContentWidth();
                    this.textOutsideCircleButtonHeight = (float) builder3.getButtonContentHeight();
                }
            case ConnectionResult.SIGN_IN_REQUIRED /*4*/:
                for (i = 0; i < buttonNumber; i++) {
                    HamButton.Builder builder4 = (HamButton.Builder) this.boomButtonBuilders.get(i);
                    builder4.innerListener(this).index(i);
                    this.boomButtons.add(builder4.build(this.context));
                    this.hamButtonWidth = (float) builder4.getButtonWidth();
                    this.hamButtonHeight = (float) builder4.getButtonHeight();
                }
            default:
        }
    }

    private void onBackgroundClicked() {
        if (!isAnimating()) {
            if (this.onBoomListener != null) {
                this.onBoomListener.onBackgroundClick();
            }
            if (this.cancelable) {
                reboom();
            }
        }
    }

    private boolean isAnimating() {
        return this.animatingViewNumber != 0;
    }

    private void calculateStartPositions() {
        this.startPositions = new ArrayList(pieceNumber());
        int[] rootViewLocation = new int[2];
        getParentView().getLocationOnScreen(rootViewLocation);
        for (int i = 0; i < this.pieces.size(); i++) {
            Point pieceCenterInRootView = new Point();
            int[] buttonLocation = new int[2];
            this.button.getLocationOnScreen(buttonLocation);
            pieceCenterInRootView.x = (((BoomPiece) this.pieces.get(i)).getLayoutParams().width / 2) + ((((Point) this.piecePositions.get(i)).x + buttonLocation[0]) - rootViewLocation[0]);
            pieceCenterInRootView.y = (((BoomPiece) this.pieces.get(i)).getLayoutParams().height / 2) + ((((Point) this.piecePositions.get(i)).y + buttonLocation[1]) - rootViewLocation[1]);
            this.startPositions.add(pieceCenterInRootView);
        }
    }

    private void calculateEndPositions() {
        switch (C07599.$SwitchMap$com$nightonke$boommenu$ButtonEnum[this.buttonEnum.ordinal()]) {
            case OnSubscribeConcatMap.BOUNDARY /*1*/:
                this.endPositions = ButtonPlaceManager.getCircleButtonPositions(this.buttonPlaceEnum, this.buttonPlaceAlignmentEnum, new Point(this.background.getLayoutParams().width, this.background.getLayoutParams().height), this.simpleCircleButtonRadius, this.boomButtonBuilders.size(), this.buttonHorizontalMargin, this.buttonVerticalMargin, this.buttonInclinedMargin, this.buttonTopMargin, this.buttonBottomMargin, this.buttonLeftMargin, this.buttonRightMargin);
                break;
            case OnSubscribeConcatMap.END /*2*/:
                this.endPositions = ButtonPlaceManager.getCircleButtonPositions(this.buttonPlaceEnum, this.buttonPlaceAlignmentEnum, new Point(this.background.getLayoutParams().width, this.background.getLayoutParams().height), this.textInsideCircleButtonRadius, this.boomButtonBuilders.size(), this.buttonHorizontalMargin, this.buttonVerticalMargin, this.buttonInclinedMargin, this.buttonTopMargin, this.buttonBottomMargin, this.buttonLeftMargin, this.buttonRightMargin);
                break;
            case ConnectionResult.SERVICE_DISABLED /*3*/:
                this.endPositions = ButtonPlaceManager.getCircleButtonPositions(this.buttonPlaceEnum, this.buttonPlaceAlignmentEnum, new Point(this.background.getLayoutParams().width, this.background.getLayoutParams().height), this.textOutsideCircleButtonWidth, this.textOutsideCircleButtonHeight, this.boomButtonBuilders.size(), this.buttonHorizontalMargin, this.buttonVerticalMargin, this.buttonInclinedMargin, this.buttonTopMargin, this.buttonBottomMargin, this.buttonLeftMargin, this.buttonRightMargin);
                break;
            case ConnectionResult.SIGN_IN_REQUIRED /*4*/:
                this.endPositions = ButtonPlaceManager.getHamButtonPositions(this.buttonPlaceEnum, this.buttonPlaceAlignmentEnum, new Point(this.background.getLayoutParams().width, this.background.getLayoutParams().height), this.hamButtonWidth, this.hamButtonHeight, this.boomButtonBuilders.size(), this.buttonHorizontalMargin, this.buttonVerticalMargin, this.buttonTopMargin, this.buttonBottomMargin, this.buttonLeftMargin, this.buttonRightMargin, this.bottomHamButtonTopMargin);
                break;
        }
        for (int i = 0; i < this.boomButtons.size(); i++) {
            Point point = (Point) this.endPositions.get(i);
            point.x = (int) (((float) point.x) - ((BoomButton) this.boomButtons.get(i)).centerPoint.x);
            point = (Point) this.endPositions.get(i);
            point.y = (int) (((float) point.y) - ((BoomButton) this.boomButtons.get(i)).centerPoint.y);
        }
    }

    private BoomButton putBoomButtonInBackground(BoomButton boomButton, Point position) {
        createBackground();
        boomButton.place(position.x, position.y, boomButton.trueWidth(), boomButton.trueHeight());
        boomButton.setVisibility(4);
        this.background.addView(boomButton);
        return boomButton;
    }

    private void clearViewsAndValues() {
        clearBackground();
        if (!this.cacheOptimization || this.inList || this.inFragment) {
            this.endPositions.clear();
            this.endPositions = null;
            this.boomButtons.clear();
            this.boomButtons = new ArrayList();
        }
    }

    private void toLayout() {
        if (!this.needToLayout) {
            this.needToLayout = true;
            if (VERSION.SDK_INT < 18) {
                requestLayout();
            } else if (!isInLayout()) {
                requestLayout();
            }
        }
    }

    private void delayToDoLayoutJobs() {
        if (this.layoutJobsRunnable == null) {
            this.layoutJobsRunnable = new C07588();
        }
        post(this.layoutJobsRunnable);
    }

    private int pieceNumber() {
        if (this.piecePlaceEnum == PiecePlaceEnum.Share) {
            return this.boomButtonBuilders.size();
        }
        return this.piecePlaceEnum.pieceNumber();
    }

    public void onButtonClick(int index, BoomButton boomButton) {
        if (!isAnimating()) {
            if (this.onBoomListener != null) {
                this.onBoomListener.onClicked(index, boomButton);
            }
            if (this.autoHide) {
                reboom();
            }
        }
    }

    private void placeShareLinesView() {
        if (this.piecePlaceEnum == PiecePlaceEnum.Share) {
            this.shareLinesView = new ShareLinesView(this.context);
            this.shareLinesView.setLine1Color(this.shareLine1Color);
            this.shareLinesView.setLine2Color(this.shareLine2Color);
            this.shareLinesView.setLineWidth(this.shareLineWidth);
            this.button.addView(this.shareLinesView);
            this.shareLinesView.place(0, 0, this.button.getWidth(), this.button.getHeight());
        }
    }

    private void setShareLinesViewData() {
        if (this.piecePlaceEnum == PiecePlaceEnum.Share) {
            this.shareLinesView.setData(this.piecePositions, this.dotRadius, this.showDuration, this.showDelay, this.hideDuration, this.hideDelay);
        }
    }

    private boolean isBatterySaveModeTurnOn() {
        return VERSION.SDK_INT >= 21 && ((PowerManager) getContext().getSystemService("power")).isPowerSaveMode();
    }

    private void ___________________________5_Builders_and_Buttons() {
    }

    public void addBuilder(BoomButtonBuilder builder) {
        this.boomButtonBuilders.add(builder);
        toLayout();
    }

    public void setBuilder(int index, BoomButtonBuilder builder) {
        this.boomButtonBuilders.set(index, builder);
        toLayout();
    }

    public void setBuilders(ArrayList<BoomButtonBuilder> builders) {
        this.boomButtonBuilders = builders;
        toLayout();
    }

    public void removeBuilder(BoomButtonBuilder builder) {
        this.boomButtonBuilders.remove(builder);
        toLayout();
    }

    public void removeBuilder(int index) {
        this.boomButtonBuilders.remove(index);
        toLayout();
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        this.button.setEnabled(enabled);
        setButtonBackground();
    }

    public void setEnable(int index, boolean enable) {
        if (index >= 0) {
            if (this.boomButtonBuilders != null && index < this.boomButtonBuilders.size()) {
                ((BoomButtonBuilder) this.boomButtonBuilders.get(index)).setUnable(!enable);
            }
            if (this.boomButtons != null && index < this.boomButtons.size()) {
                ((BoomButton) this.boomButtons.get(index)).setEnabled(enable);
            }
        }
    }

    public void clearBuilders() {
        this.boomButtonBuilders.clear();
    }

    public ArrayList<BoomButtonBuilder> getBuilders() {
        return this.boomButtonBuilders;
    }

    private void ___________________________6_Getters_and_Setters() {
    }

    public boolean isCacheOptimization() {
        return this.cacheOptimization;
    }

    public void setCacheOptimization(boolean cacheOptimization) {
        this.cacheOptimization = cacheOptimization;
    }

    public boolean isBoomInWholeScreen() {
        return this.boomInWholeScreen;
    }

    public void setBoomInWholeScreen(boolean boomInWholeScreen) {
        this.boomInWholeScreen = boomInWholeScreen;
    }

    public boolean isInList() {
        return this.inList;
    }

    public void setInList(boolean inList) {
        this.inList = inList;
    }

    public boolean isInFragment() {
        return this.inFragment;
    }

    public void setInFragment(boolean inFragment) {
        this.inFragment = inFragment;
    }

    public boolean isBackPressListened() {
        return this.isBackPressListened;
    }

    public void setBackPressListened(boolean backPressListened) {
        this.isBackPressListened = backPressListened;
    }

    public boolean isShadowEffect() {
        return this.shadowEffect;
    }

    public void setShadowEffect(boolean shadowEffect) {
        this.shadowEffect = shadowEffect;
        initShadow();
    }

    public int getShadowOffsetX() {
        return this.shadowOffsetX;
    }

    public void setShadowOffsetX(int shadowOffsetX) {
        this.shadowOffsetX = shadowOffsetX;
        initShadow();
    }

    public int getShadowOffsetY() {
        return this.shadowOffsetY;
    }

    public void setShadowOffsetY(int shadowOffsetY) {
        this.shadowOffsetY = shadowOffsetY;
        initShadow();
    }

    public int getShadowRadius() {
        return this.shadowRadius;
    }

    public void setShadowRadius(int shadowRadius) {
        this.shadowRadius = shadowRadius;
        initShadow();
    }

    public int getShadowColor() {
        return this.shadowColor;
    }

    public void setShadowColor(int shadowColor) {
        this.shadowColor = shadowColor;
        initShadow();
    }

    public int getButtonRadius() {
        return this.buttonRadius;
    }

    public void setButtonRadius(int buttonRadius) {
        this.buttonRadius = buttonRadius;
        initButton();
        toLayout();
    }

    public ButtonEnum getButtonEnum() {
        return this.buttonEnum;
    }

    public void setButtonEnum(ButtonEnum buttonEnum) {
        this.buttonEnum = buttonEnum;
        toLayout();
        clearBuilders();
    }

    public boolean isBackgroundEffect() {
        return this.backgroundEffect;
    }

    public void setBackgroundEffect(boolean backgroundEffect) {
        this.backgroundEffect = backgroundEffect;
        setButtonBackground();
        toLayout();
    }

    public boolean isRippleEffect() {
        return this.rippleEffect;
    }

    public void setRippleEffect(boolean rippleEffect) {
        this.rippleEffect = rippleEffect;
        setButtonBackground();
        toLayout();
    }

    public int getNormalColor() {
        return this.normalColor;
    }

    public void setNormalColor(int normalColor) {
        this.normalColor = normalColor;
        setButtonBackground();
        toLayout();
    }

    public int getHighlightedColor() {
        return this.highlightedColor;
    }

    public void setHighlightedColor(int highlightedColor) {
        this.highlightedColor = highlightedColor;
        setButtonBackground();
        toLayout();
    }

    public int getUnableColor() {
        return this.unableColor;
    }

    public void setUnableColor(int unableColor) {
        this.unableColor = unableColor;
        setButtonBackground();
        toLayout();
    }

    public int getDotRadius() {
        return this.dotRadius;
    }

    public void setDotRadius(int dotRadius) {
        this.dotRadius = dotRadius;
        toLayout();
    }

    public int getHamWidth() {
        return this.hamWidth;
    }

    public void setHamWidth(int hamWidth) {
        this.hamWidth = hamWidth;
        toLayout();
    }

    public int getHamHeight() {
        return this.hamHeight;
    }

    public void setHamHeight(int hamHeight) {
        this.hamHeight = hamHeight;
        toLayout();
    }

    public int getPieceHorizontalMargin() {
        return this.pieceHorizontalMargin;
    }

    public void setPieceHorizontalMargin(int pieceHorizontalMargin) {
        this.pieceHorizontalMargin = pieceHorizontalMargin;
        toLayout();
    }

    public int getPieceVerticalMargin() {
        return this.pieceVerticalMargin;
    }

    public void setPieceVerticalMargin(int pieceVerticalMargin) {
        this.pieceVerticalMargin = pieceVerticalMargin;
        toLayout();
    }

    public int getPieceInclinedMargin() {
        return this.pieceInclinedMargin;
    }

    public void setPieceInclinedMargin(int pieceInclinedMargin) {
        this.pieceInclinedMargin = pieceInclinedMargin;
        toLayout();
    }

    public int getShareLineLength() {
        return this.shareLineLength;
    }

    public void setShareLineLength(int shareLineLength) {
        this.shareLineLength = shareLineLength;
    }

    public int getShareLine1Color() {
        return this.shareLine1Color;
    }

    public void setShareLine1Color(int shareLine1Color) {
        this.shareLine1Color = shareLine1Color;
    }

    public int getShareLine2Color() {
        return this.shareLine2Color;
    }

    public void setShareLine2Color(int shareLine2Color) {
        this.shareLine2Color = shareLine2Color;
    }

    public int getShareLineWidth() {
        return this.shareLineWidth;
    }

    public void setShareLineWidth(int shareLineWidth) {
        this.shareLineWidth = shareLineWidth;
    }

    public PiecePlaceEnum getPiecePlaceEnum() {
        return this.piecePlaceEnum;
    }

    public ButtonPlaceEnum getButtonPlaceEnum() {
        return this.buttonPlaceEnum;
    }

    public void setPiecePlaceEnum(PiecePlaceEnum piecePlaceEnum) {
        this.piecePlaceEnum = piecePlaceEnum;
        toLayout();
    }

    public OnBoomListener getOnBoomListener() {
        return this.onBoomListener;
    }

    public void setOnBoomListener(OnBoomListener onBoomListener) {
        this.onBoomListener = onBoomListener;
    }

    public boolean isBoomed() {
        return this.boomStateEnum == BoomStateEnum.DidShow;
    }

    public boolean isReBoomed() {
        return this.boomStateEnum == BoomStateEnum.DidHide;
    }

    public int getDimColor() {
        return this.dimColor;
    }

    public void setDimColor(int dimColor) {
        this.dimColor = dimColor;
    }

    public long getShowDuration() {
        return this.showDuration;
    }

    public void setShowDuration(long showDuration) {
        this.showDuration = showDuration;
        setShareLinesViewData();
    }

    public long getShowDelay() {
        return this.showDelay;
    }

    public void setShowDelay(long showDelay) {
        this.showDelay = showDelay;
        setShareLinesViewData();
    }

    public long getHideDuration() {
        return this.hideDuration;
    }

    public void setHideDuration(long hideDuration) {
        this.hideDuration = hideDuration;
        setShareLinesViewData();
    }

    public long getHideDelay() {
        return this.hideDelay;
    }

    public void setHideDelay(long hideDelay) {
        this.hideDelay = hideDelay;
        setShareLinesViewData();
    }

    public boolean isCancelable() {
        return this.cancelable;
    }

    public void setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
    }

    public boolean isAutoHide() {
        return this.autoHide;
    }

    public void setAutoHide(boolean autoHide) {
        this.autoHide = autoHide;
    }

    public OrderEnum getOrderEnum() {
        return this.orderEnum;
    }

    public void setOrderEnum(OrderEnum orderEnum) {
        this.orderEnum = orderEnum;
    }

    public int getFrames() {
        return this.frames;
    }

    public void setFrames(int frames) {
        this.frames = frames;
    }

    public BoomEnum getBoomEnum() {
        return this.boomEnum;
    }

    public void setBoomEnum(BoomEnum boomEnum) {
        this.boomEnum = boomEnum;
    }

    public EaseEnum getShowMoveEaseEnum() {
        return this.showMoveEaseEnum;
    }

    public void setShowMoveEaseEnum(EaseEnum showMoveEaseEnum) {
        this.showMoveEaseEnum = showMoveEaseEnum;
    }

    public EaseEnum getShowScaleEaseEnum() {
        return this.showScaleEaseEnum;
    }

    public void setShowScaleEaseEnum(EaseEnum showScaleEaseEnum) {
        this.showScaleEaseEnum = showScaleEaseEnum;
    }

    public EaseEnum getShowRotateEaseEnum() {
        return this.showRotateEaseEnum;
    }

    public void setShowRotateEaseEnum(EaseEnum showRotateEaseEnum) {
        this.showRotateEaseEnum = showRotateEaseEnum;
    }

    public EaseEnum getHideMoveEaseEnum() {
        return this.hideMoveEaseEnum;
    }

    public void setHideMoveEaseEnum(EaseEnum hideMoveEaseEnum) {
        this.hideMoveEaseEnum = hideMoveEaseEnum;
    }

    public EaseEnum getHideScaleEaseEnum() {
        return this.hideScaleEaseEnum;
    }

    public void setHideScaleEaseEnum(EaseEnum hideScaleEaseEnum) {
        this.hideScaleEaseEnum = hideScaleEaseEnum;
    }

    public EaseEnum getHideRotateEaseEnum() {
        return this.hideRotateEaseEnum;
    }

    public void setHideRotateEaseEnum(EaseEnum hideRotateEaseEnum) {
        this.hideRotateEaseEnum = hideRotateEaseEnum;
    }

    public int getRotateDegree() {
        return this.rotateDegree;
    }

    public void setRotateDegree(int rotateDegree) {
        this.rotateDegree = rotateDegree;
    }

    public void setButtonPlaceEnum(ButtonPlaceEnum buttonPlaceEnum) {
        this.buttonPlaceEnum = buttonPlaceEnum;
    }

    public ButtonPlaceAlignmentEnum getButtonPlaceAlignmentEnum() {
        return this.buttonPlaceAlignmentEnum;
    }

    public void setButtonPlaceAlignmentEnum(ButtonPlaceAlignmentEnum buttonPlaceAlignmentEnum) {
        this.buttonPlaceAlignmentEnum = buttonPlaceAlignmentEnum;
    }

    public float getButtonHorizontalMargin() {
        return this.buttonHorizontalMargin;
    }

    public void setButtonHorizontalMargin(float buttonHorizontalMargin) {
        this.buttonHorizontalMargin = buttonHorizontalMargin;
    }

    public float getButtonVerticalMargin() {
        return this.buttonVerticalMargin;
    }

    public void setButtonVerticalMargin(float buttonVerticalMargin) {
        this.buttonVerticalMargin = buttonVerticalMargin;
        this.bottomHamButtonTopMargin = null;
    }

    public float getButtonInclinedMargin() {
        return this.buttonInclinedMargin;
    }

    public void setButtonInclinedMargin(float buttonInclinedMargin) {
        this.buttonInclinedMargin = buttonInclinedMargin;
    }

    public float getButtonTopMargin() {
        return this.buttonTopMargin;
    }

    public void setButtonTopMargin(float buttonTopMargin) {
        this.buttonTopMargin = buttonTopMargin;
    }

    public float getButtonBottomMargin() {
        return this.buttonBottomMargin;
    }

    public void setButtonBottomMargin(float buttonBottomMargin) {
        this.buttonBottomMargin = buttonBottomMargin;
    }

    public float getButtonLeftMargin() {
        return this.buttonLeftMargin;
    }

    public void setButtonLeftMargin(float buttonLeftMargin) {
        this.buttonLeftMargin = buttonLeftMargin;
    }

    public float getButtonRightMargin() {
        return this.buttonRightMargin;
    }

    public void setButtonRightMargin(float buttonRightMargin) {
        this.buttonRightMargin = buttonRightMargin;
    }

    public float getBottomHamButtonTopMargin() {
        return this.bottomHamButtonTopMargin.floatValue();
    }

    public void setBottomHamButtonTopMargin(float bottomHamButtonTopMargin) {
        this.bottomHamButtonTopMargin = Float.valueOf(bottomHamButtonTopMargin);
    }
}
