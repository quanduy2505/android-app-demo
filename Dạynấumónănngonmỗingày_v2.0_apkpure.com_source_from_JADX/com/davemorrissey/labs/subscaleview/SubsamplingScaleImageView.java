package com.davemorrissey.labs.subscaleview;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v7.widget.RecyclerView.ItemAnimator;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnLongClickListener;
import com.davemorrissey.labs.subscaleview.decoder.CompatDecoderFactory;
import com.davemorrissey.labs.subscaleview.decoder.DecoderFactory;
import com.davemorrissey.labs.subscaleview.decoder.ImageDecoder;
import com.davemorrissey.labs.subscaleview.decoder.ImageRegionDecoder;
import com.davemorrissey.labs.subscaleview.decoder.SkiaImageDecoder;
import com.davemorrissey.labs.subscaleview.decoder.SkiaImageRegionDecoder;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executor;

public class SubsamplingScaleImageView extends View {
    public static final int EASE_IN_OUT_QUAD = 2;
    public static final int EASE_OUT_QUAD = 1;
    private static final int MESSAGE_LONG_CLICK = 1;
    public static final int ORIENTATION_0 = 0;
    public static final int ORIENTATION_180 = 180;
    public static final int ORIENTATION_270 = 270;
    public static final int ORIENTATION_90 = 90;
    public static final int ORIENTATION_USE_EXIF = -1;
    public static final int PAN_LIMIT_CENTER = 3;
    public static final int PAN_LIMIT_INSIDE = 1;
    public static final int PAN_LIMIT_OUTSIDE = 2;
    public static final int SCALE_TYPE_CENTER_CROP = 2;
    public static final int SCALE_TYPE_CENTER_INSIDE = 1;
    public static final int SCALE_TYPE_CUSTOM = 3;
    private static final String TAG;
    private static final List<Integer> VALID_EASING_STYLES;
    private static final List<Integer> VALID_ORIENTATIONS;
    private static final List<Integer> VALID_PAN_LIMITS;
    private static final List<Integer> VALID_SCALE_TYPES;
    private static final List<Integer> VALID_ZOOM_STYLES;
    public static final int ZOOM_FOCUS_CENTER = 2;
    public static final int ZOOM_FOCUS_CENTER_IMMEDIATE = 3;
    public static final int ZOOM_FOCUS_FIXED = 1;
    private Anim anim;
    private Bitmap bitmap;
    private DecoderFactory<? extends ImageDecoder> bitmapDecoderFactory;
    private boolean bitmapIsCached;
    private boolean bitmapIsPreview;
    private Paint bitmapPaint;
    private boolean debug;
    private Paint debugPaint;
    private ImageRegionDecoder decoder;
    private final Object decoderLock;
    private GestureDetector detector;
    private float doubleTapZoomScale;
    private int doubleTapZoomStyle;
    private float[] dstArray;
    private int fullImageSampleSize;
    private Handler handler;
    private boolean imageLoadedSent;
    private boolean isPanning;
    private boolean isQuickScaling;
    private boolean isZooming;
    private Matrix matrix;
    private float maxScale;
    private int maxTouchCount;
    private float minScale;
    private int minimumScaleType;
    private int minimumTileDpi;
    private OnImageEventListener onImageEventListener;
    private OnLongClickListener onLongClickListener;
    private int orientation;
    private Rect pRegion;
    private boolean panEnabled;
    private int panLimit;
    private boolean parallelLoadingEnabled;
    private Float pendingScale;
    private PointF quickScaleCenter;
    private boolean quickScaleEnabled;
    private float quickScaleLastDistance;
    private PointF quickScaleLastPoint;
    private boolean quickScaleMoved;
    private final float quickScaleThreshold;
    private boolean readySent;
    private DecoderFactory<? extends ImageRegionDecoder> regionDecoderFactory;
    private int sHeight;
    private int sOrientation;
    private PointF sPendingCenter;
    private RectF sRect;
    private Rect sRegion;
    private PointF sRequestedCenter;
    private int sWidth;
    private ScaleAndTranslate satTemp;
    private float scale;
    private float scaleStart;
    private float[] srcArray;
    private Paint tileBgPaint;
    private Map<Integer, List<Tile>> tileMap;
    private Uri uri;
    private PointF vCenterStart;
    private float vDistStart;
    private PointF vTranslate;
    private PointF vTranslateStart;
    private boolean zoomEnabled;

    /* renamed from: com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView.1 */
    class C03601 implements Callback {
        C03601() {
        }

        public boolean handleMessage(Message message) {
            if (message.what == SubsamplingScaleImageView.ZOOM_FOCUS_FIXED && SubsamplingScaleImageView.this.onLongClickListener != null) {
                SubsamplingScaleImageView.this.maxTouchCount = SubsamplingScaleImageView.ORIENTATION_0;
                super.setOnLongClickListener(SubsamplingScaleImageView.this.onLongClickListener);
                SubsamplingScaleImageView.this.performLongClick();
                super.setOnLongClickListener(null);
            }
            return true;
        }
    }

    /* renamed from: com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView.2 */
    class C03612 extends SimpleOnGestureListener {
        final /* synthetic */ Context val$context;

        C03612(Context context) {
            this.val$context = context;
        }

        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (!SubsamplingScaleImageView.this.panEnabled || !SubsamplingScaleImageView.this.readySent || SubsamplingScaleImageView.this.vTranslate == null || e1 == null || e2 == null || ((Math.abs(e1.getX() - e2.getX()) <= 50.0f && Math.abs(e1.getY() - e2.getY()) <= 50.0f) || ((Math.abs(velocityX) <= 500.0f && Math.abs(velocityY) <= 500.0f) || SubsamplingScaleImageView.this.isZooming))) {
                return super.onFling(e1, e2, velocityX, velocityY);
            }
            PointF vTranslateEnd = new PointF(SubsamplingScaleImageView.this.vTranslate.x + (velocityX * 0.25f), SubsamplingScaleImageView.this.vTranslate.y + (0.25f * velocityY));
            new AnimationBuilder(new PointF((((float) (SubsamplingScaleImageView.this.getWidth() / SubsamplingScaleImageView.ZOOM_FOCUS_CENTER)) - vTranslateEnd.x) / SubsamplingScaleImageView.this.scale, (((float) (SubsamplingScaleImageView.this.getHeight() / SubsamplingScaleImageView.ZOOM_FOCUS_CENTER)) - vTranslateEnd.y) / SubsamplingScaleImageView.this.scale), null).withEasing(SubsamplingScaleImageView.ZOOM_FOCUS_FIXED).withPanLimited(false).start();
            return true;
        }

        public boolean onSingleTapConfirmed(MotionEvent e) {
            SubsamplingScaleImageView.this.performClick();
            return true;
        }

        public boolean onDoubleTap(MotionEvent e) {
            if (!SubsamplingScaleImageView.this.zoomEnabled || !SubsamplingScaleImageView.this.readySent || SubsamplingScaleImageView.this.vTranslate == null) {
                return super.onDoubleTapEvent(e);
            }
            SubsamplingScaleImageView.this.setGestureDetector(this.val$context);
            if (SubsamplingScaleImageView.this.quickScaleEnabled) {
                SubsamplingScaleImageView.this.vCenterStart = new PointF(e.getX(), e.getY());
                SubsamplingScaleImageView.this.vTranslateStart = new PointF(SubsamplingScaleImageView.this.vTranslate.x, SubsamplingScaleImageView.this.vTranslate.y);
                SubsamplingScaleImageView.this.scaleStart = SubsamplingScaleImageView.this.scale;
                SubsamplingScaleImageView.this.isQuickScaling = true;
                SubsamplingScaleImageView.this.isZooming = true;
                SubsamplingScaleImageView.this.quickScaleCenter = SubsamplingScaleImageView.this.viewToSourceCoord(SubsamplingScaleImageView.this.vCenterStart);
                SubsamplingScaleImageView.this.quickScaleLastDistance = -1.0f;
                SubsamplingScaleImageView.this.quickScaleLastPoint = new PointF(SubsamplingScaleImageView.this.quickScaleCenter.x, SubsamplingScaleImageView.this.quickScaleCenter.y);
                SubsamplingScaleImageView.this.quickScaleMoved = false;
                return false;
            }
            SubsamplingScaleImageView.this.doubleTapZoom(SubsamplingScaleImageView.this.viewToSourceCoord(new PointF(e.getX(), e.getY())), new PointF(e.getX(), e.getY()));
            return true;
        }
    }

    private static class Anim {
        private long duration;
        private int easing;
        private boolean interruptible;
        private PointF sCenterEnd;
        private PointF sCenterEndRequested;
        private PointF sCenterStart;
        private float scaleEnd;
        private float scaleStart;
        private long time;
        private PointF vFocusEnd;
        private PointF vFocusStart;

        private Anim() {
            this.duration = 500;
            this.interruptible = true;
            this.easing = SubsamplingScaleImageView.ZOOM_FOCUS_CENTER;
            this.time = System.currentTimeMillis();
        }
    }

    public final class AnimationBuilder {
        private long duration;
        private int easing;
        private boolean interruptible;
        private boolean panLimited;
        private final PointF targetSCenter;
        private final float targetScale;
        private final PointF vFocus;

        private AnimationBuilder(PointF sCenter) {
            this.duration = 500;
            this.easing = SubsamplingScaleImageView.ZOOM_FOCUS_CENTER;
            this.interruptible = true;
            this.panLimited = true;
            this.targetScale = SubsamplingScaleImageView.this.scale;
            this.targetSCenter = sCenter;
            this.vFocus = null;
        }

        private AnimationBuilder(float scale) {
            this.duration = 500;
            this.easing = SubsamplingScaleImageView.ZOOM_FOCUS_CENTER;
            this.interruptible = true;
            this.panLimited = true;
            this.targetScale = scale;
            this.targetSCenter = SubsamplingScaleImageView.this.getCenter();
            this.vFocus = null;
        }

        private AnimationBuilder(float scale, PointF sCenter) {
            this.duration = 500;
            this.easing = SubsamplingScaleImageView.ZOOM_FOCUS_CENTER;
            this.interruptible = true;
            this.panLimited = true;
            this.targetScale = scale;
            this.targetSCenter = sCenter;
            this.vFocus = null;
        }

        private AnimationBuilder(float scale, PointF sCenter, PointF vFocus) {
            this.duration = 500;
            this.easing = SubsamplingScaleImageView.ZOOM_FOCUS_CENTER;
            this.interruptible = true;
            this.panLimited = true;
            this.targetScale = scale;
            this.targetSCenter = sCenter;
            this.vFocus = vFocus;
        }

        public AnimationBuilder withDuration(long duration) {
            this.duration = duration;
            return this;
        }

        public AnimationBuilder withInterruptible(boolean interruptible) {
            this.interruptible = interruptible;
            return this;
        }

        public AnimationBuilder withEasing(int easing) {
            if (SubsamplingScaleImageView.VALID_EASING_STYLES.contains(Integer.valueOf(easing))) {
                this.easing = easing;
                return this;
            }
            throw new IllegalArgumentException("Unknown easing type: " + easing);
        }

        private AnimationBuilder withPanLimited(boolean panLimited) {
            this.panLimited = panLimited;
            return this;
        }

        public void start() {
            int vxCenter = SubsamplingScaleImageView.this.getPaddingLeft() + (((SubsamplingScaleImageView.this.getWidth() - SubsamplingScaleImageView.this.getPaddingRight()) - SubsamplingScaleImageView.this.getPaddingLeft()) / SubsamplingScaleImageView.ZOOM_FOCUS_CENTER);
            int vyCenter = SubsamplingScaleImageView.this.getPaddingTop() + (((SubsamplingScaleImageView.this.getHeight() - SubsamplingScaleImageView.this.getPaddingBottom()) - SubsamplingScaleImageView.this.getPaddingTop()) / SubsamplingScaleImageView.ZOOM_FOCUS_CENTER);
            float targetScale = SubsamplingScaleImageView.this.limitedScale(this.targetScale);
            PointF targetSCenter = this.panLimited ? SubsamplingScaleImageView.this.limitedSCenter(this.targetSCenter.x, this.targetSCenter.y, targetScale, new PointF()) : this.targetSCenter;
            SubsamplingScaleImageView.this.anim = new Anim();
            SubsamplingScaleImageView.this.anim.scaleStart = SubsamplingScaleImageView.this.scale;
            SubsamplingScaleImageView.this.anim.scaleEnd = targetScale;
            SubsamplingScaleImageView.this.anim.time = System.currentTimeMillis();
            SubsamplingScaleImageView.this.anim.sCenterEndRequested = targetSCenter;
            SubsamplingScaleImageView.this.anim.sCenterStart = SubsamplingScaleImageView.this.getCenter();
            SubsamplingScaleImageView.this.anim.sCenterEnd = targetSCenter;
            SubsamplingScaleImageView.this.anim.vFocusStart = SubsamplingScaleImageView.this.sourceToViewCoord(targetSCenter);
            SubsamplingScaleImageView.this.anim.vFocusEnd = new PointF((float) vxCenter, (float) vyCenter);
            SubsamplingScaleImageView.this.anim.duration = this.duration;
            SubsamplingScaleImageView.this.anim.interruptible = this.interruptible;
            SubsamplingScaleImageView.this.anim.easing = this.easing;
            SubsamplingScaleImageView.this.anim.time = System.currentTimeMillis();
            if (this.vFocus != null) {
                float vTranslateXEnd = this.vFocus.x - (SubsamplingScaleImageView.this.anim.sCenterStart.x * targetScale);
                float vTranslateYEnd = this.vFocus.y - (SubsamplingScaleImageView.this.anim.sCenterStart.y * targetScale);
                ScaleAndTranslate satEnd = new ScaleAndTranslate(new PointF(vTranslateXEnd, vTranslateYEnd), null);
                SubsamplingScaleImageView.this.fitToBounds(true, satEnd);
                SubsamplingScaleImageView.this.anim.vFocusEnd = new PointF(this.vFocus.x + (satEnd.vTranslate.x - vTranslateXEnd), this.vFocus.y + (satEnd.vTranslate.y - vTranslateYEnd));
            }
            SubsamplingScaleImageView.this.invalidate();
        }
    }

    private static class BitmapLoadTask extends AsyncTask<Void, Void, Integer> {
        private Bitmap bitmap;
        private final WeakReference<Context> contextRef;
        private final WeakReference<DecoderFactory<? extends ImageDecoder>> decoderFactoryRef;
        private Exception exception;
        private final boolean preview;
        private final Uri source;
        private final WeakReference<SubsamplingScaleImageView> viewRef;

        public BitmapLoadTask(SubsamplingScaleImageView view, Context context, DecoderFactory<? extends ImageDecoder> decoderFactory, Uri source, boolean preview) {
            this.viewRef = new WeakReference(view);
            this.contextRef = new WeakReference(context);
            this.decoderFactoryRef = new WeakReference(decoderFactory);
            this.source = source;
            this.preview = preview;
        }

        protected Integer doInBackground(Void... params) {
            try {
                String sourceUri = this.source.toString();
                Context context = (Context) this.contextRef.get();
                DecoderFactory<? extends ImageDecoder> decoderFactory = (DecoderFactory) this.decoderFactoryRef.get();
                SubsamplingScaleImageView subsamplingScaleImageView = (SubsamplingScaleImageView) this.viewRef.get();
                if (!(context == null || decoderFactory == null || subsamplingScaleImageView == null)) {
                    this.bitmap = ((ImageDecoder) decoderFactory.make()).decode(context, this.source);
                    return Integer.valueOf(subsamplingScaleImageView.getExifOrientation(sourceUri));
                }
            } catch (Exception e) {
                Log.e(SubsamplingScaleImageView.TAG, "Failed to load bitmap", e);
                this.exception = e;
            }
            return null;
        }

        protected void onPostExecute(Integer orientation) {
            SubsamplingScaleImageView subsamplingScaleImageView = (SubsamplingScaleImageView) this.viewRef.get();
            if (subsamplingScaleImageView == null) {
                return;
            }
            if (this.bitmap == null || orientation == null) {
                if (this.exception != null && subsamplingScaleImageView.onImageEventListener != null) {
                    if (this.preview) {
                        subsamplingScaleImageView.onImageEventListener.onPreviewLoadError(this.exception);
                    } else {
                        subsamplingScaleImageView.onImageEventListener.onImageLoadError(this.exception);
                    }
                }
            } else if (this.preview) {
                subsamplingScaleImageView.onPreviewLoaded(this.bitmap);
            } else {
                subsamplingScaleImageView.onImageLoaded(this.bitmap, orientation.intValue(), false);
            }
        }
    }

    public interface OnImageEventListener {
        void onImageLoadError(Exception exception);

        void onImageLoaded();

        void onPreviewLoadError(Exception exception);

        void onReady();

        void onTileLoadError(Exception exception);
    }

    private static class ScaleAndTranslate {
        private float scale;
        private PointF vTranslate;

        private ScaleAndTranslate(float scale, PointF vTranslate) {
            this.scale = scale;
            this.vTranslate = vTranslate;
        }
    }

    private static class Tile {
        private Bitmap bitmap;
        private Rect fileSRect;
        private boolean loading;
        private Rect sRect;
        private int sampleSize;
        private Rect vRect;
        private boolean visible;

        private Tile() {
        }
    }

    private static class TileLoadTask extends AsyncTask<Void, Void, Bitmap> {
        private final WeakReference<ImageRegionDecoder> decoderRef;
        private Exception exception;
        private final WeakReference<Tile> tileRef;
        private final WeakReference<SubsamplingScaleImageView> viewRef;

        public TileLoadTask(SubsamplingScaleImageView view, ImageRegionDecoder decoder, Tile tile) {
            this.viewRef = new WeakReference(view);
            this.decoderRef = new WeakReference(decoder);
            this.tileRef = new WeakReference(tile);
            tile.loading = true;
        }

        protected Bitmap doInBackground(Void... params) {
            try {
                SubsamplingScaleImageView view = (SubsamplingScaleImageView) this.viewRef.get();
                ImageRegionDecoder decoder = (ImageRegionDecoder) this.decoderRef.get();
                Tile tile = (Tile) this.tileRef.get();
                if (decoder == null || tile == null || view == null || !decoder.isReady() || !tile.visible) {
                    if (tile != null) {
                        tile.loading = false;
                    }
                    return null;
                }
                Bitmap decodeRegion;
                synchronized (view.decoderLock) {
                    view.fileSRect(tile.sRect, tile.fileSRect);
                    if (view.sRegion != null) {
                        tile.fileSRect.offset(view.sRegion.left, view.sRegion.top);
                    }
                    decodeRegion = decoder.decodeRegion(tile.fileSRect, tile.sampleSize);
                }
                return decodeRegion;
            } catch (Exception e) {
                Log.e(SubsamplingScaleImageView.TAG, "Failed to decode tile", e);
                this.exception = e;
            }
        }

        protected void onPostExecute(Bitmap bitmap) {
            SubsamplingScaleImageView subsamplingScaleImageView = (SubsamplingScaleImageView) this.viewRef.get();
            Tile tile = (Tile) this.tileRef.get();
            if (subsamplingScaleImageView != null && tile != null) {
                if (bitmap != null) {
                    tile.bitmap = bitmap;
                    tile.loading = false;
                    subsamplingScaleImageView.onTileLoaded();
                } else if (this.exception != null && subsamplingScaleImageView.onImageEventListener != null) {
                    subsamplingScaleImageView.onImageEventListener.onTileLoadError(this.exception);
                }
            }
        }
    }

    private static class TilesInitTask extends AsyncTask<Void, Void, int[]> {
        private final WeakReference<Context> contextRef;
        private ImageRegionDecoder decoder;
        private final WeakReference<DecoderFactory<? extends ImageRegionDecoder>> decoderFactoryRef;
        private Exception exception;
        private final Uri source;
        private final WeakReference<SubsamplingScaleImageView> viewRef;

        public TilesInitTask(SubsamplingScaleImageView view, Context context, DecoderFactory<? extends ImageRegionDecoder> decoderFactory, Uri source) {
            this.viewRef = new WeakReference(view);
            this.contextRef = new WeakReference(context);
            this.decoderFactoryRef = new WeakReference(decoderFactory);
            this.source = source;
        }

        protected int[] doInBackground(Void... params) {
            try {
                String sourceUri = this.source.toString();
                Context context = (Context) this.contextRef.get();
                DecoderFactory<? extends ImageRegionDecoder> decoderFactory = (DecoderFactory) this.decoderFactoryRef.get();
                SubsamplingScaleImageView view = (SubsamplingScaleImageView) this.viewRef.get();
                if (!(context == null || decoderFactory == null || view == null)) {
                    this.decoder = (ImageRegionDecoder) decoderFactory.make();
                    Point dimensions = this.decoder.init(context, this.source);
                    int sWidth = dimensions.x;
                    int sHeight = dimensions.y;
                    int exifOrientation = view.getExifOrientation(sourceUri);
                    if (view.sRegion != null) {
                        sWidth = view.sRegion.width();
                        sHeight = view.sRegion.height();
                    }
                    int[] iArr = new int[SubsamplingScaleImageView.ZOOM_FOCUS_CENTER_IMMEDIATE];
                    iArr[SubsamplingScaleImageView.ORIENTATION_0] = sWidth;
                    iArr[SubsamplingScaleImageView.ZOOM_FOCUS_FIXED] = sHeight;
                    iArr[SubsamplingScaleImageView.ZOOM_FOCUS_CENTER] = exifOrientation;
                    return iArr;
                }
            } catch (Exception e) {
                Log.e(SubsamplingScaleImageView.TAG, "Failed to initialise bitmap decoder", e);
                this.exception = e;
            }
            return null;
        }

        protected void onPostExecute(int[] xyo) {
            SubsamplingScaleImageView view = (SubsamplingScaleImageView) this.viewRef.get();
            if (view == null) {
                return;
            }
            if (this.decoder != null && xyo != null && xyo.length == SubsamplingScaleImageView.ZOOM_FOCUS_CENTER_IMMEDIATE) {
                view.onTilesInited(this.decoder, xyo[SubsamplingScaleImageView.ORIENTATION_0], xyo[SubsamplingScaleImageView.ZOOM_FOCUS_FIXED], xyo[SubsamplingScaleImageView.ZOOM_FOCUS_CENTER]);
            } else if (this.exception != null && view.onImageEventListener != null) {
                view.onImageEventListener.onImageLoadError(this.exception);
            }
        }
    }

    public static class DefaultOnImageEventListener implements OnImageEventListener {
        public void onReady() {
        }

        public void onImageLoaded() {
        }

        public void onPreviewLoadError(Exception e) {
        }

        public void onImageLoadError(Exception e) {
        }

        public void onTileLoadError(Exception e) {
        }
    }

    static {
        TAG = SubsamplingScaleImageView.class.getSimpleName();
        VALID_ORIENTATIONS = Arrays.asList(new Integer[]{Integer.valueOf(ORIENTATION_0), Integer.valueOf(ORIENTATION_90), Integer.valueOf(ORIENTATION_180), Integer.valueOf(ORIENTATION_270), Integer.valueOf(ORIENTATION_USE_EXIF)});
        Integer[] numArr = new Integer[ZOOM_FOCUS_CENTER_IMMEDIATE];
        numArr[ORIENTATION_0] = Integer.valueOf(ZOOM_FOCUS_FIXED);
        numArr[ZOOM_FOCUS_FIXED] = Integer.valueOf(ZOOM_FOCUS_CENTER);
        numArr[ZOOM_FOCUS_CENTER] = Integer.valueOf(ZOOM_FOCUS_CENTER_IMMEDIATE);
        VALID_ZOOM_STYLES = Arrays.asList(numArr);
        numArr = new Integer[ZOOM_FOCUS_CENTER];
        numArr[ORIENTATION_0] = Integer.valueOf(ZOOM_FOCUS_CENTER);
        numArr[ZOOM_FOCUS_FIXED] = Integer.valueOf(ZOOM_FOCUS_FIXED);
        VALID_EASING_STYLES = Arrays.asList(numArr);
        numArr = new Integer[ZOOM_FOCUS_CENTER_IMMEDIATE];
        numArr[ORIENTATION_0] = Integer.valueOf(ZOOM_FOCUS_FIXED);
        numArr[ZOOM_FOCUS_FIXED] = Integer.valueOf(ZOOM_FOCUS_CENTER);
        numArr[ZOOM_FOCUS_CENTER] = Integer.valueOf(ZOOM_FOCUS_CENTER_IMMEDIATE);
        VALID_PAN_LIMITS = Arrays.asList(numArr);
        numArr = new Integer[ZOOM_FOCUS_CENTER_IMMEDIATE];
        numArr[ORIENTATION_0] = Integer.valueOf(ZOOM_FOCUS_CENTER);
        numArr[ZOOM_FOCUS_FIXED] = Integer.valueOf(ZOOM_FOCUS_FIXED);
        numArr[ZOOM_FOCUS_CENTER] = Integer.valueOf(ZOOM_FOCUS_CENTER_IMMEDIATE);
        VALID_SCALE_TYPES = Arrays.asList(numArr);
    }

    public SubsamplingScaleImageView(Context context, AttributeSet attr) {
        super(context, attr);
        this.orientation = ORIENTATION_0;
        this.maxScale = 2.0f;
        this.minScale = minScale();
        this.minimumTileDpi = ORIENTATION_USE_EXIF;
        this.panLimit = ZOOM_FOCUS_FIXED;
        this.minimumScaleType = ZOOM_FOCUS_FIXED;
        this.panEnabled = true;
        this.zoomEnabled = true;
        this.quickScaleEnabled = true;
        this.doubleTapZoomScale = 1.0f;
        this.doubleTapZoomStyle = ZOOM_FOCUS_FIXED;
        this.decoderLock = new Object();
        this.bitmapDecoderFactory = new CompatDecoderFactory(SkiaImageDecoder.class);
        this.regionDecoderFactory = new CompatDecoderFactory(SkiaImageRegionDecoder.class);
        this.srcArray = new float[8];
        this.dstArray = new float[8];
        setMinimumDpi(160);
        setDoubleTapZoomDpi(160);
        setGestureDetector(context);
        this.handler = new Handler(new C03601());
        if (attr != null) {
            TypedArray typedAttr = getContext().obtainStyledAttributes(attr, C0359R.styleable.SubsamplingScaleImageView);
            if (typedAttr.hasValue(ZOOM_FOCUS_FIXED)) {
                String assetName = typedAttr.getString(ZOOM_FOCUS_FIXED);
                if (assetName != null && assetName.length() > 0) {
                    setImage(ImageSource.asset(assetName).tilingEnabled());
                }
            }
            if (typedAttr.hasValue(ORIENTATION_0)) {
                int resId = typedAttr.getResourceId(ORIENTATION_0, ORIENTATION_0);
                if (resId > 0) {
                    setImage(ImageSource.resource(resId).tilingEnabled());
                }
            }
            if (typedAttr.hasValue(ZOOM_FOCUS_CENTER)) {
                setPanEnabled(typedAttr.getBoolean(ZOOM_FOCUS_CENTER, true));
            }
            if (typedAttr.hasValue(ZOOM_FOCUS_CENTER_IMMEDIATE)) {
                setZoomEnabled(typedAttr.getBoolean(ZOOM_FOCUS_CENTER_IMMEDIATE, true));
            }
            if (typedAttr.hasValue(4)) {
                setQuickScaleEnabled(typedAttr.getBoolean(4, true));
            }
            if (typedAttr.hasValue(5)) {
                setTileBackgroundColor(typedAttr.getColor(5, Color.argb(ORIENTATION_0, ORIENTATION_0, ORIENTATION_0, ORIENTATION_0)));
            }
            typedAttr.recycle();
        }
        this.quickScaleThreshold = TypedValue.applyDimension(ZOOM_FOCUS_FIXED, 20.0f, context.getResources().getDisplayMetrics());
    }

    public SubsamplingScaleImageView(Context context) {
        this(context, null);
    }

    public final void setOrientation(int orientation) {
        if (VALID_ORIENTATIONS.contains(Integer.valueOf(orientation))) {
            this.orientation = orientation;
            reset(false);
            invalidate();
            requestLayout();
            return;
        }
        throw new IllegalArgumentException("Invalid orientation: " + orientation);
    }

    public final void setImage(ImageSource imageSource) {
        setImage(imageSource, null, null);
    }

    public final void setImage(ImageSource imageSource, ImageViewState state) {
        setImage(imageSource, null, state);
    }

    public final void setImage(ImageSource imageSource, ImageSource previewSource) {
        setImage(imageSource, previewSource, null);
    }

    public final void setImage(ImageSource imageSource, ImageSource previewSource, ImageViewState state) {
        if (imageSource == null) {
            throw new NullPointerException("imageSource must not be null");
        }
        reset(true);
        if (state != null) {
            restoreState(state);
        }
        if (previewSource != null) {
            if (imageSource.getBitmap() != null) {
                throw new IllegalArgumentException("Preview image cannot be used when a bitmap is provided for the main image");
            } else if (imageSource.getSWidth() <= 0 || imageSource.getSHeight() <= 0) {
                throw new IllegalArgumentException("Preview image cannot be used unless dimensions are provided for the main image");
            } else {
                this.sWidth = imageSource.getSWidth();
                this.sHeight = imageSource.getSHeight();
                this.pRegion = previewSource.getSRegion();
                if (previewSource.getBitmap() != null) {
                    this.bitmapIsCached = previewSource.isCached();
                    onPreviewLoaded(previewSource.getBitmap());
                } else {
                    Uri uri = previewSource.getUri();
                    if (uri == null && previewSource.getResource() != null) {
                        uri = Uri.parse("android.resource://" + getContext().getPackageName() + "/" + previewSource.getResource());
                    }
                    execute(new BitmapLoadTask(this, getContext(), this.bitmapDecoderFactory, uri, true));
                }
            }
        }
        if (imageSource.getBitmap() != null && imageSource.getSRegion() != null) {
            onImageLoaded(Bitmap.createBitmap(imageSource.getBitmap(), imageSource.getSRegion().left, imageSource.getSRegion().top, imageSource.getSRegion().width(), imageSource.getSRegion().height()), ORIENTATION_0, false);
        } else if (imageSource.getBitmap() != null) {
            onImageLoaded(imageSource.getBitmap(), ORIENTATION_0, imageSource.isCached());
        } else {
            this.sRegion = imageSource.getSRegion();
            this.uri = imageSource.getUri();
            if (this.uri == null && imageSource.getResource() != null) {
                this.uri = Uri.parse("android.resource://" + getContext().getPackageName() + "/" + imageSource.getResource());
            }
            if (imageSource.getTile() || this.sRegion != null) {
                execute(new TilesInitTask(this, getContext(), this.regionDecoderFactory, this.uri));
            } else {
                execute(new BitmapLoadTask(this, getContext(), this.bitmapDecoderFactory, this.uri, false));
            }
        }
    }

    private void reset(boolean newImage) {
        this.scale = 0.0f;
        this.scaleStart = 0.0f;
        this.vTranslate = null;
        this.vTranslateStart = null;
        this.pendingScale = Float.valueOf(0.0f);
        this.sPendingCenter = null;
        this.sRequestedCenter = null;
        this.isZooming = false;
        this.isPanning = false;
        this.isQuickScaling = false;
        this.maxTouchCount = ORIENTATION_0;
        this.fullImageSampleSize = ORIENTATION_0;
        this.vCenterStart = null;
        this.vDistStart = 0.0f;
        this.quickScaleCenter = null;
        this.quickScaleLastDistance = 0.0f;
        this.quickScaleLastPoint = null;
        this.quickScaleMoved = false;
        this.anim = null;
        this.satTemp = null;
        this.matrix = null;
        this.sRect = null;
        if (newImage) {
            this.uri = null;
            if (this.decoder != null) {
                synchronized (this.decoderLock) {
                    this.decoder.recycle();
                    this.decoder = null;
                }
            }
            if (!(this.bitmap == null || this.bitmapIsCached)) {
                this.bitmap.recycle();
            }
            this.sWidth = ORIENTATION_0;
            this.sHeight = ORIENTATION_0;
            this.sOrientation = ORIENTATION_0;
            this.sRegion = null;
            this.pRegion = null;
            this.readySent = false;
            this.imageLoadedSent = false;
            this.bitmap = null;
            this.bitmapIsPreview = false;
            this.bitmapIsCached = false;
        }
        if (this.tileMap != null) {
            for (Entry<Integer, List<Tile>> tileMapEntry : this.tileMap.entrySet()) {
                for (Tile tile : (List) tileMapEntry.getValue()) {
                    tile.visible = false;
                    if (tile.bitmap != null) {
                        tile.bitmap.recycle();
                        tile.bitmap = null;
                    }
                }
            }
            this.tileMap = null;
        }
        setGestureDetector(getContext());
    }

    private void setGestureDetector(Context context) {
        this.detector = new GestureDetector(context, new C03612(context));
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        PointF sCenter = getCenter();
        if (this.readySent && sCenter != null) {
            this.anim = null;
            this.pendingScale = Float.valueOf(this.scale);
            this.sPendingCenter = sCenter;
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        boolean resizeWidth;
        boolean resizeHeight = true;
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        if (widthSpecMode != 1073741824) {
            resizeWidth = true;
        } else {
            resizeWidth = false;
        }
        if (heightSpecMode == 1073741824) {
            resizeHeight = false;
        }
        int width = parentWidth;
        int height = parentHeight;
        if (this.sWidth > 0 && this.sHeight > 0) {
            if (resizeWidth && resizeHeight) {
                width = sWidth();
                height = sHeight();
            } else if (resizeHeight) {
                height = (int) ((((double) sHeight()) / ((double) sWidth())) * ((double) width));
            } else if (resizeWidth) {
                width = (int) ((((double) sWidth()) / ((double) sHeight())) * ((double) height));
            }
        }
        setMeasuredDimension(Math.max(width, getSuggestedMinimumWidth()), Math.max(height, getSuggestedMinimumHeight()));
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(@android.support.annotation.NonNull android.view.MotionEvent r33) {
        /*
        r32 = this;
        r0 = r32;
        r0 = r0.anim;
        r27 = r0;
        if (r27 == 0) goto L_0x0020;
    L_0x0008:
        r0 = r32;
        r0 = r0.anim;
        r27 = r0;
        r27 = r27.interruptible;
        if (r27 != 0) goto L_0x0020;
    L_0x0014:
        r27 = r32.getParent();
        r28 = 1;
        r27.requestDisallowInterceptTouchEvent(r28);
        r27 = 1;
    L_0x001f:
        return r27;
    L_0x0020:
        r27 = 0;
        r0 = r27;
        r1 = r32;
        r1.anim = r0;
        r0 = r32;
        r0 = r0.vTranslate;
        r27 = r0;
        if (r27 != 0) goto L_0x0033;
    L_0x0030:
        r27 = 1;
        goto L_0x001f;
    L_0x0033:
        r0 = r32;
        r0 = r0.isQuickScaling;
        r27 = r0;
        if (r27 != 0) goto L_0x006e;
    L_0x003b:
        r0 = r32;
        r0 = r0.detector;
        r27 = r0;
        if (r27 == 0) goto L_0x0053;
    L_0x0043:
        r0 = r32;
        r0 = r0.detector;
        r27 = r0;
        r0 = r27;
        r1 = r33;
        r27 = r0.onTouchEvent(r1);
        if (r27 == 0) goto L_0x006e;
    L_0x0053:
        r27 = 0;
        r0 = r27;
        r1 = r32;
        r1.isZooming = r0;
        r27 = 0;
        r0 = r27;
        r1 = r32;
        r1.isPanning = r0;
        r27 = 0;
        r0 = r27;
        r1 = r32;
        r1.maxTouchCount = r0;
        r27 = 1;
        goto L_0x001f;
    L_0x006e:
        r0 = r32;
        r0 = r0.vTranslateStart;
        r27 = r0;
        if (r27 != 0) goto L_0x0085;
    L_0x0076:
        r27 = new android.graphics.PointF;
        r28 = 0;
        r29 = 0;
        r27.<init>(r28, r29);
        r0 = r27;
        r1 = r32;
        r1.vTranslateStart = r0;
    L_0x0085:
        r0 = r32;
        r0 = r0.vCenterStart;
        r27 = r0;
        if (r27 != 0) goto L_0x009c;
    L_0x008d:
        r27 = new android.graphics.PointF;
        r28 = 0;
        r29 = 0;
        r27.<init>(r28, r29);
        r0 = r27;
        r1 = r32;
        r1.vCenterStart = r0;
    L_0x009c:
        r18 = r33.getPointerCount();
        r27 = r33.getAction();
        switch(r27) {
            case 0: goto L_0x00ad;
            case 1: goto L_0x0830;
            case 2: goto L_0x01ec;
            case 5: goto L_0x00ad;
            case 6: goto L_0x0830;
            case 261: goto L_0x00ad;
            case 262: goto L_0x0830;
            default: goto L_0x00a7;
        };
    L_0x00a7:
        r27 = super.onTouchEvent(r33);
        goto L_0x001f;
    L_0x00ad:
        r27 = 0;
        r0 = r27;
        r1 = r32;
        r1.anim = r0;
        r27 = r32.getParent();
        r28 = 1;
        r27.requestDisallowInterceptTouchEvent(r28);
        r0 = r32;
        r0 = r0.maxTouchCount;
        r27 = r0;
        r0 = r27;
        r1 = r18;
        r27 = java.lang.Math.max(r0, r1);
        r0 = r27;
        r1 = r32;
        r1.maxTouchCount = r0;
        r27 = 2;
        r0 = r18;
        r1 = r27;
        if (r0 < r1) goto L_0x019e;
    L_0x00da:
        r0 = r32;
        r0 = r0.zoomEnabled;
        r27 = r0;
        if (r27 == 0) goto L_0x0195;
    L_0x00e2:
        r27 = 0;
        r0 = r33;
        r1 = r27;
        r27 = r0.getX(r1);
        r28 = 1;
        r0 = r33;
        r1 = r28;
        r28 = r0.getX(r1);
        r29 = 0;
        r0 = r33;
        r1 = r29;
        r29 = r0.getY(r1);
        r30 = 1;
        r0 = r33;
        r1 = r30;
        r30 = r0.getY(r1);
        r0 = r32;
        r1 = r27;
        r2 = r28;
        r3 = r29;
        r4 = r30;
        r9 = r0.distance(r1, r2, r3, r4);
        r0 = r32;
        r0 = r0.scale;
        r27 = r0;
        r0 = r27;
        r1 = r32;
        r1.scaleStart = r0;
        r0 = r32;
        r0.vDistStart = r9;
        r0 = r32;
        r0 = r0.vTranslateStart;
        r27 = r0;
        r0 = r32;
        r0 = r0.vTranslate;
        r28 = r0;
        r0 = r28;
        r0 = r0.x;
        r28 = r0;
        r0 = r32;
        r0 = r0.vTranslate;
        r29 = r0;
        r0 = r29;
        r0 = r0.y;
        r29 = r0;
        r27.set(r28, r29);
        r0 = r32;
        r0 = r0.vCenterStart;
        r27 = r0;
        r28 = 0;
        r0 = r33;
        r1 = r28;
        r28 = r0.getX(r1);
        r29 = 1;
        r0 = r33;
        r1 = r29;
        r29 = r0.getX(r1);
        r28 = r28 + r29;
        r29 = 1073741824; // 0x40000000 float:2.0 double:5.304989477E-315;
        r28 = r28 / r29;
        r29 = 0;
        r0 = r33;
        r1 = r29;
        r29 = r0.getY(r1);
        r30 = 1;
        r0 = r33;
        r1 = r30;
        r30 = r0.getY(r1);
        r29 = r29 + r30;
        r30 = 1073741824; // 0x40000000 float:2.0 double:5.304989477E-315;
        r29 = r29 / r30;
        r27.set(r28, r29);
    L_0x0186:
        r0 = r32;
        r0 = r0.handler;
        r27 = r0;
        r28 = 1;
        r27.removeMessages(r28);
    L_0x0191:
        r27 = 1;
        goto L_0x001f;
    L_0x0195:
        r27 = 0;
        r0 = r27;
        r1 = r32;
        r1.maxTouchCount = r0;
        goto L_0x0186;
    L_0x019e:
        r0 = r32;
        r0 = r0.isQuickScaling;
        r27 = r0;
        if (r27 != 0) goto L_0x0191;
    L_0x01a6:
        r0 = r32;
        r0 = r0.vTranslateStart;
        r27 = r0;
        r0 = r32;
        r0 = r0.vTranslate;
        r28 = r0;
        r0 = r28;
        r0 = r0.x;
        r28 = r0;
        r0 = r32;
        r0 = r0.vTranslate;
        r29 = r0;
        r0 = r29;
        r0 = r0.y;
        r29 = r0;
        r27.set(r28, r29);
        r0 = r32;
        r0 = r0.vCenterStart;
        r27 = r0;
        r28 = r33.getX();
        r29 = r33.getY();
        r27.set(r28, r29);
        r0 = r32;
        r0 = r0.handler;
        r27 = r0;
        r28 = 1;
        r30 = 600; // 0x258 float:8.41E-43 double:2.964E-321;
        r0 = r27;
        r1 = r28;
        r2 = r30;
        r0.sendEmptyMessageDelayed(r1, r2);
        goto L_0x0191;
    L_0x01ec:
        r7 = 0;
        r0 = r32;
        r0 = r0.maxTouchCount;
        r27 = r0;
        if (r27 <= 0) goto L_0x0334;
    L_0x01f5:
        r27 = 2;
        r0 = r18;
        r1 = r27;
        if (r0 < r1) goto L_0x0480;
    L_0x01fd:
        r27 = 0;
        r0 = r33;
        r1 = r27;
        r27 = r0.getX(r1);
        r28 = 1;
        r0 = r33;
        r1 = r28;
        r28 = r0.getX(r1);
        r29 = 0;
        r0 = r33;
        r1 = r29;
        r29 = r0.getY(r1);
        r30 = 1;
        r0 = r33;
        r1 = r30;
        r30 = r0.getY(r1);
        r0 = r32;
        r1 = r27;
        r2 = r28;
        r3 = r29;
        r4 = r30;
        r21 = r0.distance(r1, r2, r3, r4);
        r27 = 0;
        r0 = r33;
        r1 = r27;
        r27 = r0.getX(r1);
        r28 = 1;
        r0 = r33;
        r1 = r28;
        r28 = r0.getX(r1);
        r27 = r27 + r28;
        r28 = 1073741824; // 0x40000000 float:2.0 double:5.304989477E-315;
        r19 = r27 / r28;
        r27 = 0;
        r0 = r33;
        r1 = r27;
        r27 = r0.getY(r1);
        r28 = 1;
        r0 = r33;
        r1 = r28;
        r28 = r0.getY(r1);
        r27 = r27 + r28;
        r28 = 1073741824; // 0x40000000 float:2.0 double:5.304989477E-315;
        r20 = r27 / r28;
        r0 = r32;
        r0 = r0.zoomEnabled;
        r27 = r0;
        if (r27 == 0) goto L_0x0334;
    L_0x026f:
        r0 = r32;
        r0 = r0.vCenterStart;
        r27 = r0;
        r0 = r27;
        r0 = r0.x;
        r27 = r0;
        r0 = r32;
        r0 = r0.vCenterStart;
        r28 = r0;
        r0 = r28;
        r0 = r0.y;
        r28 = r0;
        r0 = r32;
        r1 = r27;
        r2 = r19;
        r3 = r28;
        r4 = r20;
        r27 = r0.distance(r1, r2, r3, r4);
        r28 = 1084227584; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r27 = (r27 > r28 ? 1 : (r27 == r28 ? 0 : -1));
        if (r27 > 0) goto L_0x02b5;
    L_0x029b:
        r0 = r32;
        r0 = r0.vDistStart;
        r27 = r0;
        r27 = r21 - r27;
        r27 = java.lang.Math.abs(r27);
        r28 = 1084227584; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r27 = (r27 > r28 ? 1 : (r27 == r28 ? 0 : -1));
        if (r27 > 0) goto L_0x02b5;
    L_0x02ad:
        r0 = r32;
        r0 = r0.isPanning;
        r27 = r0;
        if (r27 == 0) goto L_0x0334;
    L_0x02b5:
        r27 = 1;
        r0 = r27;
        r1 = r32;
        r1.isZooming = r0;
        r27 = 1;
        r0 = r27;
        r1 = r32;
        r1.isPanning = r0;
        r7 = 1;
        r0 = r32;
        r0 = r0.maxScale;
        r27 = r0;
        r0 = r32;
        r0 = r0.vDistStart;
        r28 = r0;
        r28 = r21 / r28;
        r0 = r32;
        r0 = r0.scaleStart;
        r29 = r0;
        r28 = r28 * r29;
        r27 = java.lang.Math.min(r27, r28);
        r0 = r27;
        r1 = r32;
        r1.scale = r0;
        r0 = r32;
        r0 = r0.scale;
        r27 = r0;
        r28 = r32.minScale();
        r27 = (r27 > r28 ? 1 : (r27 == r28 ? 0 : -1));
        if (r27 > 0) goto L_0x0348;
    L_0x02f4:
        r0 = r21;
        r1 = r32;
        r1.vDistStart = r0;
        r27 = r32.minScale();
        r0 = r27;
        r1 = r32;
        r1.scaleStart = r0;
        r0 = r32;
        r0 = r0.vCenterStart;
        r27 = r0;
        r0 = r27;
        r1 = r19;
        r2 = r20;
        r0.set(r1, r2);
        r0 = r32;
        r0 = r0.vTranslateStart;
        r27 = r0;
        r0 = r32;
        r0 = r0.vTranslate;
        r28 = r0;
        r27.set(r28);
    L_0x0322:
        r27 = 1;
        r0 = r32;
        r1 = r27;
        r0.fitToBounds(r1);
        r27 = 0;
        r0 = r32;
        r1 = r27;
        r0.refreshRequiredTiles(r1);
    L_0x0334:
        if (r7 == 0) goto L_0x00a7;
    L_0x0336:
        r0 = r32;
        r0 = r0.handler;
        r27 = r0;
        r28 = 1;
        r27.removeMessages(r28);
        r32.invalidate();
        r27 = 1;
        goto L_0x001f;
    L_0x0348:
        r0 = r32;
        r0 = r0.panEnabled;
        r27 = r0;
        if (r27 == 0) goto L_0x03c2;
    L_0x0350:
        r0 = r32;
        r0 = r0.vCenterStart;
        r27 = r0;
        r0 = r27;
        r0 = r0.x;
        r27 = r0;
        r0 = r32;
        r0 = r0.vTranslateStart;
        r28 = r0;
        r0 = r28;
        r0 = r0.x;
        r28 = r0;
        r23 = r27 - r28;
        r0 = r32;
        r0 = r0.vCenterStart;
        r27 = r0;
        r0 = r27;
        r0 = r0.y;
        r27 = r0;
        r0 = r32;
        r0 = r0.vTranslateStart;
        r28 = r0;
        r0 = r28;
        r0 = r0.y;
        r28 = r0;
        r25 = r27 - r28;
        r0 = r32;
        r0 = r0.scale;
        r27 = r0;
        r0 = r32;
        r0 = r0.scaleStart;
        r28 = r0;
        r27 = r27 / r28;
        r22 = r23 * r27;
        r0 = r32;
        r0 = r0.scale;
        r27 = r0;
        r0 = r32;
        r0 = r0.scaleStart;
        r28 = r0;
        r27 = r27 / r28;
        r24 = r25 * r27;
        r0 = r32;
        r0 = r0.vTranslate;
        r27 = r0;
        r28 = r19 - r22;
        r0 = r28;
        r1 = r27;
        r1.x = r0;
        r0 = r32;
        r0 = r0.vTranslate;
        r27 = r0;
        r28 = r20 - r24;
        r0 = r28;
        r1 = r27;
        r1.y = r0;
        goto L_0x0322;
    L_0x03c2:
        r0 = r32;
        r0 = r0.sRequestedCenter;
        r27 = r0;
        if (r27 == 0) goto L_0x0426;
    L_0x03ca:
        r0 = r32;
        r0 = r0.vTranslate;
        r27 = r0;
        r28 = r32.getWidth();
        r28 = r28 / 2;
        r0 = r28;
        r0 = (float) r0;
        r28 = r0;
        r0 = r32;
        r0 = r0.scale;
        r29 = r0;
        r0 = r32;
        r0 = r0.sRequestedCenter;
        r30 = r0;
        r0 = r30;
        r0 = r0.x;
        r30 = r0;
        r29 = r29 * r30;
        r28 = r28 - r29;
        r0 = r28;
        r1 = r27;
        r1.x = r0;
        r0 = r32;
        r0 = r0.vTranslate;
        r27 = r0;
        r28 = r32.getHeight();
        r28 = r28 / 2;
        r0 = r28;
        r0 = (float) r0;
        r28 = r0;
        r0 = r32;
        r0 = r0.scale;
        r29 = r0;
        r0 = r32;
        r0 = r0.sRequestedCenter;
        r30 = r0;
        r0 = r30;
        r0 = r0.y;
        r30 = r0;
        r29 = r29 * r30;
        r28 = r28 - r29;
        r0 = r28;
        r1 = r27;
        r1.y = r0;
        goto L_0x0322;
    L_0x0426:
        r0 = r32;
        r0 = r0.vTranslate;
        r27 = r0;
        r28 = r32.getWidth();
        r28 = r28 / 2;
        r0 = r28;
        r0 = (float) r0;
        r28 = r0;
        r0 = r32;
        r0 = r0.scale;
        r29 = r0;
        r30 = r32.sWidth();
        r30 = r30 / 2;
        r0 = r30;
        r0 = (float) r0;
        r30 = r0;
        r29 = r29 * r30;
        r28 = r28 - r29;
        r0 = r28;
        r1 = r27;
        r1.x = r0;
        r0 = r32;
        r0 = r0.vTranslate;
        r27 = r0;
        r28 = r32.getHeight();
        r28 = r28 / 2;
        r0 = r28;
        r0 = (float) r0;
        r28 = r0;
        r0 = r32;
        r0 = r0.scale;
        r29 = r0;
        r30 = r32.sHeight();
        r30 = r30 / 2;
        r0 = r30;
        r0 = (float) r0;
        r30 = r0;
        r29 = r29 * r30;
        r28 = r28 - r29;
        r0 = r28;
        r1 = r27;
        r1.y = r0;
        goto L_0x0322;
    L_0x0480:
        r0 = r32;
        r0 = r0.isQuickScaling;
        r27 = r0;
        if (r27 == 0) goto L_0x06ad;
    L_0x0488:
        r0 = r32;
        r0 = r0.vCenterStart;
        r27 = r0;
        r0 = r27;
        r0 = r0.y;
        r27 = r0;
        r28 = r33.getY();
        r27 = r27 - r28;
        r27 = java.lang.Math.abs(r27);
        r28 = 1073741824; // 0x40000000 float:2.0 double:5.304989477E-315;
        r27 = r27 * r28;
        r0 = r32;
        r0 = r0.quickScaleThreshold;
        r28 = r0;
        r8 = r27 + r28;
        r0 = r32;
        r0 = r0.quickScaleLastDistance;
        r27 = r0;
        r28 = -1082130432; // 0xffffffffbf800000 float:-1.0 double:NaN;
        r27 = (r27 > r28 ? 1 : (r27 == r28 ? 0 : -1));
        if (r27 != 0) goto L_0x04ba;
    L_0x04b6:
        r0 = r32;
        r0.quickScaleLastDistance = r8;
    L_0x04ba:
        r27 = r33.getY();
        r0 = r32;
        r0 = r0.quickScaleLastPoint;
        r28 = r0;
        r0 = r28;
        r0 = r0.y;
        r28 = r0;
        r27 = (r27 > r28 ? 1 : (r27 == r28 ? 0 : -1));
        if (r27 <= 0) goto L_0x05e6;
    L_0x04ce:
        r13 = 1;
    L_0x04cf:
        r0 = r32;
        r0 = r0.quickScaleLastPoint;
        r27 = r0;
        r28 = 0;
        r29 = r33.getY();
        r27.set(r28, r29);
        r27 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r0 = r32;
        r0 = r0.quickScaleLastDistance;
        r28 = r0;
        r28 = r8 / r28;
        r27 = r27 - r28;
        r27 = java.lang.Math.abs(r27);
        r28 = 1056964608; // 0x3f000000 float:0.5 double:5.222099017E-315;
        r17 = r27 * r28;
        r27 = 1022739087; // 0x3cf5c28f float:0.03 double:5.053002475E-315;
        r27 = (r17 > r27 ? 1 : (r17 == r27 ? 0 : -1));
        if (r27 > 0) goto L_0x0501;
    L_0x04f9:
        r0 = r32;
        r0 = r0.quickScaleMoved;
        r27 = r0;
        if (r27 == 0) goto L_0x05cd;
    L_0x0501:
        r27 = 1;
        r0 = r27;
        r1 = r32;
        r1.quickScaleMoved = r0;
        r16 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r0 = r32;
        r0 = r0.quickScaleLastDistance;
        r27 = r0;
        r28 = 0;
        r27 = (r27 > r28 ? 1 : (r27 == r28 ? 0 : -1));
        if (r27 <= 0) goto L_0x051d;
    L_0x0517:
        if (r13 == 0) goto L_0x05e9;
    L_0x0519:
        r27 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r16 = r27 + r17;
    L_0x051d:
        r27 = r32.minScale();
        r0 = r32;
        r0 = r0.maxScale;
        r28 = r0;
        r0 = r32;
        r0 = r0.scale;
        r29 = r0;
        r29 = r29 * r16;
        r28 = java.lang.Math.min(r28, r29);
        r27 = java.lang.Math.max(r27, r28);
        r0 = r27;
        r1 = r32;
        r1.scale = r0;
        r0 = r32;
        r0 = r0.panEnabled;
        r27 = r0;
        if (r27 == 0) goto L_0x05ef;
    L_0x0545:
        r0 = r32;
        r0 = r0.vCenterStart;
        r27 = r0;
        r0 = r27;
        r0 = r0.x;
        r27 = r0;
        r0 = r32;
        r0 = r0.vTranslateStart;
        r28 = r0;
        r0 = r28;
        r0 = r0.x;
        r28 = r0;
        r23 = r27 - r28;
        r0 = r32;
        r0 = r0.vCenterStart;
        r27 = r0;
        r0 = r27;
        r0 = r0.y;
        r27 = r0;
        r0 = r32;
        r0 = r0.vTranslateStart;
        r28 = r0;
        r0 = r28;
        r0 = r0.y;
        r28 = r0;
        r25 = r27 - r28;
        r0 = r32;
        r0 = r0.scale;
        r27 = r0;
        r0 = r32;
        r0 = r0.scaleStart;
        r28 = r0;
        r27 = r27 / r28;
        r22 = r23 * r27;
        r0 = r32;
        r0 = r0.scale;
        r27 = r0;
        r0 = r32;
        r0 = r0.scaleStart;
        r28 = r0;
        r27 = r27 / r28;
        r24 = r25 * r27;
        r0 = r32;
        r0 = r0.vTranslate;
        r27 = r0;
        r0 = r32;
        r0 = r0.vCenterStart;
        r28 = r0;
        r0 = r28;
        r0 = r0.x;
        r28 = r0;
        r28 = r28 - r22;
        r0 = r28;
        r1 = r27;
        r1.x = r0;
        r0 = r32;
        r0 = r0.vTranslate;
        r27 = r0;
        r0 = r32;
        r0 = r0.vCenterStart;
        r28 = r0;
        r0 = r28;
        r0 = r0.y;
        r28 = r0;
        r28 = r28 - r24;
        r0 = r28;
        r1 = r27;
        r1.y = r0;
    L_0x05cd:
        r0 = r32;
        r0.quickScaleLastDistance = r8;
        r27 = 1;
        r0 = r32;
        r1 = r27;
        r0.fitToBounds(r1);
        r27 = 0;
        r0 = r32;
        r1 = r27;
        r0.refreshRequiredTiles(r1);
        r7 = 1;
        goto L_0x0334;
    L_0x05e6:
        r13 = 0;
        goto L_0x04cf;
    L_0x05e9:
        r27 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r16 = r27 - r17;
        goto L_0x051d;
    L_0x05ef:
        r0 = r32;
        r0 = r0.sRequestedCenter;
        r27 = r0;
        if (r27 == 0) goto L_0x0653;
    L_0x05f7:
        r0 = r32;
        r0 = r0.vTranslate;
        r27 = r0;
        r28 = r32.getWidth();
        r28 = r28 / 2;
        r0 = r28;
        r0 = (float) r0;
        r28 = r0;
        r0 = r32;
        r0 = r0.scale;
        r29 = r0;
        r0 = r32;
        r0 = r0.sRequestedCenter;
        r30 = r0;
        r0 = r30;
        r0 = r0.x;
        r30 = r0;
        r29 = r29 * r30;
        r28 = r28 - r29;
        r0 = r28;
        r1 = r27;
        r1.x = r0;
        r0 = r32;
        r0 = r0.vTranslate;
        r27 = r0;
        r28 = r32.getHeight();
        r28 = r28 / 2;
        r0 = r28;
        r0 = (float) r0;
        r28 = r0;
        r0 = r32;
        r0 = r0.scale;
        r29 = r0;
        r0 = r32;
        r0 = r0.sRequestedCenter;
        r30 = r0;
        r0 = r30;
        r0 = r0.y;
        r30 = r0;
        r29 = r29 * r30;
        r28 = r28 - r29;
        r0 = r28;
        r1 = r27;
        r1.y = r0;
        goto L_0x05cd;
    L_0x0653:
        r0 = r32;
        r0 = r0.vTranslate;
        r27 = r0;
        r28 = r32.getWidth();
        r28 = r28 / 2;
        r0 = r28;
        r0 = (float) r0;
        r28 = r0;
        r0 = r32;
        r0 = r0.scale;
        r29 = r0;
        r30 = r32.sWidth();
        r30 = r30 / 2;
        r0 = r30;
        r0 = (float) r0;
        r30 = r0;
        r29 = r29 * r30;
        r28 = r28 - r29;
        r0 = r28;
        r1 = r27;
        r1.x = r0;
        r0 = r32;
        r0 = r0.vTranslate;
        r27 = r0;
        r28 = r32.getHeight();
        r28 = r28 / 2;
        r0 = r28;
        r0 = (float) r0;
        r28 = r0;
        r0 = r32;
        r0 = r0.scale;
        r29 = r0;
        r30 = r32.sHeight();
        r30 = r30 / 2;
        r0 = r30;
        r0 = (float) r0;
        r30 = r0;
        r29 = r29 * r30;
        r28 = r28 - r29;
        r0 = r28;
        r1 = r27;
        r1.y = r0;
        goto L_0x05cd;
    L_0x06ad:
        r0 = r32;
        r0 = r0.isZooming;
        r27 = r0;
        if (r27 != 0) goto L_0x0334;
    L_0x06b5:
        r27 = r33.getX();
        r0 = r32;
        r0 = r0.vCenterStart;
        r28 = r0;
        r0 = r28;
        r0 = r0.x;
        r28 = r0;
        r27 = r27 - r28;
        r10 = java.lang.Math.abs(r27);
        r27 = r33.getY();
        r0 = r32;
        r0 = r0.vCenterStart;
        r28 = r0;
        r0 = r28;
        r0 = r0.y;
        r28 = r0;
        r27 = r27 - r28;
        r11 = java.lang.Math.abs(r27);
        r27 = 1084227584; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r27 = (r10 > r27 ? 1 : (r10 == r27 ? 0 : -1));
        if (r27 > 0) goto L_0x06f5;
    L_0x06e7:
        r27 = 1084227584; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r27 = (r11 > r27 ? 1 : (r11 == r27 ? 0 : -1));
        if (r27 > 0) goto L_0x06f5;
    L_0x06ed:
        r0 = r32;
        r0 = r0.isPanning;
        r27 = r0;
        if (r27 == 0) goto L_0x0334;
    L_0x06f5:
        r7 = 1;
        r0 = r32;
        r0 = r0.vTranslate;
        r27 = r0;
        r0 = r32;
        r0 = r0.vTranslateStart;
        r28 = r0;
        r0 = r28;
        r0 = r0.x;
        r28 = r0;
        r29 = r33.getX();
        r0 = r32;
        r0 = r0.vCenterStart;
        r30 = r0;
        r0 = r30;
        r0 = r0.x;
        r30 = r0;
        r29 = r29 - r30;
        r28 = r28 + r29;
        r0 = r28;
        r1 = r27;
        r1.x = r0;
        r0 = r32;
        r0 = r0.vTranslate;
        r27 = r0;
        r0 = r32;
        r0 = r0.vTranslateStart;
        r28 = r0;
        r0 = r28;
        r0 = r0.y;
        r28 = r0;
        r29 = r33.getY();
        r0 = r32;
        r0 = r0.vCenterStart;
        r30 = r0;
        r0 = r30;
        r0 = r0.y;
        r30 = r0;
        r29 = r29 - r30;
        r28 = r28 + r29;
        r0 = r28;
        r1 = r27;
        r1.y = r0;
        r0 = r32;
        r0 = r0.vTranslate;
        r27 = r0;
        r0 = r27;
        r14 = r0.x;
        r0 = r32;
        r0 = r0.vTranslate;
        r27 = r0;
        r0 = r27;
        r15 = r0.y;
        r27 = 1;
        r0 = r32;
        r1 = r27;
        r0.fitToBounds(r1);
        r0 = r32;
        r0 = r0.vTranslate;
        r27 = r0;
        r0 = r27;
        r0 = r0.x;
        r27 = r0;
        r27 = (r14 > r27 ? 1 : (r14 == r27 ? 0 : -1));
        if (r27 == 0) goto L_0x0805;
    L_0x077b:
        r6 = 1;
    L_0x077c:
        if (r6 == 0) goto L_0x0808;
    L_0x077e:
        r27 = (r10 > r11 ? 1 : (r10 == r11 ? 0 : -1));
        if (r27 <= 0) goto L_0x0808;
    L_0x0782:
        r0 = r32;
        r0 = r0.isPanning;
        r27 = r0;
        if (r27 != 0) goto L_0x0808;
    L_0x078a:
        r12 = 1;
    L_0x078b:
        r0 = r32;
        r0 = r0.vTranslate;
        r27 = r0;
        r0 = r27;
        r0 = r0.y;
        r27 = r0;
        r27 = (r15 > r27 ? 1 : (r15 == r27 ? 0 : -1));
        if (r27 != 0) goto L_0x080a;
    L_0x079b:
        r27 = 1097859072; // 0x41700000 float:15.0 double:5.424144515E-315;
        r27 = (r11 > r27 ? 1 : (r11 == r27 ? 0 : -1));
        if (r27 <= 0) goto L_0x080a;
    L_0x07a1:
        r26 = 1;
    L_0x07a3:
        if (r12 != 0) goto L_0x080d;
    L_0x07a5:
        if (r6 == 0) goto L_0x07b1;
    L_0x07a7:
        if (r26 != 0) goto L_0x07b1;
    L_0x07a9:
        r0 = r32;
        r0 = r0.isPanning;
        r27 = r0;
        if (r27 == 0) goto L_0x080d;
    L_0x07b1:
        r27 = 1;
        r0 = r27;
        r1 = r32;
        r1.isPanning = r0;
    L_0x07b9:
        r0 = r32;
        r0 = r0.panEnabled;
        r27 = r0;
        if (r27 != 0) goto L_0x07fa;
    L_0x07c1:
        r0 = r32;
        r0 = r0.vTranslate;
        r27 = r0;
        r0 = r32;
        r0 = r0.vTranslateStart;
        r28 = r0;
        r0 = r28;
        r0 = r0.x;
        r28 = r0;
        r0 = r28;
        r1 = r27;
        r1.x = r0;
        r0 = r32;
        r0 = r0.vTranslate;
        r27 = r0;
        r0 = r32;
        r0 = r0.vTranslateStart;
        r28 = r0;
        r0 = r28;
        r0 = r0.y;
        r28 = r0;
        r0 = r28;
        r1 = r27;
        r1.y = r0;
        r27 = r32.getParent();
        r28 = 0;
        r27.requestDisallowInterceptTouchEvent(r28);
    L_0x07fa:
        r27 = 0;
        r0 = r32;
        r1 = r27;
        r0.refreshRequiredTiles(r1);
        goto L_0x0334;
    L_0x0805:
        r6 = 0;
        goto L_0x077c;
    L_0x0808:
        r12 = 0;
        goto L_0x078b;
    L_0x080a:
        r26 = 0;
        goto L_0x07a3;
    L_0x080d:
        r27 = 1084227584; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r27 = (r10 > r27 ? 1 : (r10 == r27 ? 0 : -1));
        if (r27 <= 0) goto L_0x07b9;
    L_0x0813:
        r27 = 0;
        r0 = r27;
        r1 = r32;
        r1.maxTouchCount = r0;
        r0 = r32;
        r0 = r0.handler;
        r27 = r0;
        r28 = 1;
        r27.removeMessages(r28);
        r27 = r32.getParent();
        r28 = 0;
        r27.requestDisallowInterceptTouchEvent(r28);
        goto L_0x07b9;
    L_0x0830:
        r0 = r32;
        r0 = r0.handler;
        r27 = r0;
        r28 = 1;
        r27.removeMessages(r28);
        r0 = r32;
        r0 = r0.isQuickScaling;
        r27 = r0;
        if (r27 == 0) goto L_0x0868;
    L_0x0843:
        r27 = 0;
        r0 = r27;
        r1 = r32;
        r1.isQuickScaling = r0;
        r0 = r32;
        r0 = r0.quickScaleMoved;
        r27 = r0;
        if (r27 != 0) goto L_0x0868;
    L_0x0853:
        r0 = r32;
        r0 = r0.quickScaleCenter;
        r27 = r0;
        r0 = r32;
        r0 = r0.vCenterStart;
        r28 = r0;
        r0 = r32;
        r1 = r27;
        r2 = r28;
        r0.doubleTapZoom(r1, r2);
    L_0x0868:
        r0 = r32;
        r0 = r0.maxTouchCount;
        r27 = r0;
        if (r27 <= 0) goto L_0x0935;
    L_0x0870:
        r0 = r32;
        r0 = r0.isZooming;
        r27 = r0;
        if (r27 != 0) goto L_0x0880;
    L_0x0878:
        r0 = r32;
        r0 = r0.isPanning;
        r27 = r0;
        if (r27 == 0) goto L_0x0935;
    L_0x0880:
        r0 = r32;
        r0 = r0.isZooming;
        r27 = r0;
        if (r27 == 0) goto L_0x08e2;
    L_0x0888:
        r27 = 2;
        r0 = r18;
        r1 = r27;
        if (r0 != r1) goto L_0x08e2;
    L_0x0890:
        r27 = 1;
        r0 = r27;
        r1 = r32;
        r1.isPanning = r0;
        r0 = r32;
        r0 = r0.vTranslateStart;
        r27 = r0;
        r0 = r32;
        r0 = r0.vTranslate;
        r28 = r0;
        r0 = r28;
        r0 = r0.x;
        r28 = r0;
        r0 = r32;
        r0 = r0.vTranslate;
        r29 = r0;
        r0 = r29;
        r0 = r0.y;
        r29 = r0;
        r27.set(r28, r29);
        r27 = r33.getActionIndex();
        r28 = 1;
        r0 = r27;
        r1 = r28;
        if (r0 != r1) goto L_0x0917;
    L_0x08c5:
        r0 = r32;
        r0 = r0.vCenterStart;
        r27 = r0;
        r28 = 0;
        r0 = r33;
        r1 = r28;
        r28 = r0.getX(r1);
        r29 = 0;
        r0 = r33;
        r1 = r29;
        r29 = r0.getY(r1);
        r27.set(r28, r29);
    L_0x08e2:
        r27 = 3;
        r0 = r18;
        r1 = r27;
        if (r0 >= r1) goto L_0x08f2;
    L_0x08ea:
        r27 = 0;
        r0 = r27;
        r1 = r32;
        r1.isZooming = r0;
    L_0x08f2:
        r27 = 2;
        r0 = r18;
        r1 = r27;
        if (r0 >= r1) goto L_0x090a;
    L_0x08fa:
        r27 = 0;
        r0 = r27;
        r1 = r32;
        r1.isPanning = r0;
        r27 = 0;
        r0 = r27;
        r1 = r32;
        r1.maxTouchCount = r0;
    L_0x090a:
        r27 = 1;
        r0 = r32;
        r1 = r27;
        r0.refreshRequiredTiles(r1);
        r27 = 1;
        goto L_0x001f;
    L_0x0917:
        r0 = r32;
        r0 = r0.vCenterStart;
        r27 = r0;
        r28 = 1;
        r0 = r33;
        r1 = r28;
        r28 = r0.getX(r1);
        r29 = 1;
        r0 = r33;
        r1 = r29;
        r29 = r0.getY(r1);
        r27.set(r28, r29);
        goto L_0x08e2;
    L_0x0935:
        r27 = 1;
        r0 = r18;
        r1 = r27;
        if (r0 != r1) goto L_0x0955;
    L_0x093d:
        r27 = 0;
        r0 = r27;
        r1 = r32;
        r1.isZooming = r0;
        r27 = 0;
        r0 = r27;
        r1 = r32;
        r1.isPanning = r0;
        r27 = 0;
        r0 = r27;
        r1 = r32;
        r1.maxTouchCount = r0;
    L_0x0955:
        r27 = 1;
        goto L_0x001f;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView.onTouchEvent(android.view.MotionEvent):boolean");
    }

    private void doubleTapZoom(PointF sCenter, PointF vFocus) {
        if (!this.panEnabled) {
            if (this.sRequestedCenter != null) {
                sCenter.x = this.sRequestedCenter.x;
                sCenter.y = this.sRequestedCenter.y;
            } else {
                sCenter.x = (float) (sWidth() / ZOOM_FOCUS_CENTER);
                sCenter.y = (float) (sHeight() / ZOOM_FOCUS_CENTER);
            }
        }
        float doubleTapZoomScale = Math.min(this.maxScale, this.doubleTapZoomScale);
        boolean zoomIn = ((double) this.scale) <= ((double) doubleTapZoomScale) * 0.9d;
        float targetScale = zoomIn ? doubleTapZoomScale : minScale();
        if (this.doubleTapZoomStyle == ZOOM_FOCUS_CENTER_IMMEDIATE) {
            setScaleAndCenter(targetScale, sCenter);
        } else if (this.doubleTapZoomStyle == ZOOM_FOCUS_CENTER || !zoomIn || !this.panEnabled) {
            new AnimationBuilder(targetScale, sCenter, null).withInterruptible(false).start();
        } else if (this.doubleTapZoomStyle == ZOOM_FOCUS_FIXED) {
            new AnimationBuilder(targetScale, sCenter, vFocus, null).withInterruptible(false).start();
        }
        invalidate();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        createPaints();
        if (this.sWidth != 0 && this.sHeight != 0 && getWidth() != 0 && getHeight() != 0) {
            if (this.tileMap == null && this.decoder != null) {
                initialiseBaseLayer(getMaxBitmapDimensions(canvas));
            }
            if (checkReady()) {
                preDraw();
                if (this.anim != null) {
                    long scaleElapsed = System.currentTimeMillis() - this.anim.time;
                    boolean finished = scaleElapsed > this.anim.duration;
                    scaleElapsed = Math.min(scaleElapsed, this.anim.duration);
                    this.scale = ease(this.anim.easing, scaleElapsed, this.anim.scaleStart, this.anim.scaleEnd - this.anim.scaleStart, this.anim.duration);
                    float vFocusNowX = ease(this.anim.easing, scaleElapsed, this.anim.vFocusStart.x, this.anim.vFocusEnd.x - this.anim.vFocusStart.x, this.anim.duration);
                    float vFocusNowY = ease(this.anim.easing, scaleElapsed, this.anim.vFocusStart.y, this.anim.vFocusEnd.y - this.anim.vFocusStart.y, this.anim.duration);
                    PointF pointF = this.vTranslate;
                    pointF.x -= sourceToViewX(this.anim.sCenterEnd.x) - vFocusNowX;
                    pointF = this.vTranslate;
                    pointF.y -= sourceToViewY(this.anim.sCenterEnd.y) - vFocusNowY;
                    boolean z = finished || this.anim.scaleStart == this.anim.scaleEnd;
                    fitToBounds(z);
                    refreshRequiredTiles(finished);
                    if (finished) {
                        this.anim = null;
                    }
                    invalidate();
                }
                if (this.tileMap != null && isBaseLayerReady()) {
                    int sampleSize = Math.min(this.fullImageSampleSize, calculateInSampleSize(this.scale));
                    boolean hasMissingTiles = false;
                    for (Entry<Integer, List<Tile>> tileMapEntry : this.tileMap.entrySet()) {
                        if (((Integer) tileMapEntry.getKey()).intValue() == sampleSize) {
                            for (Tile tile : (List) tileMapEntry.getValue()) {
                                if (tile.visible && (tile.loading || tile.bitmap == null)) {
                                    hasMissingTiles = true;
                                }
                            }
                        }
                    }
                    for (Entry<Integer, List<Tile>> tileMapEntry2 : this.tileMap.entrySet()) {
                        if (((Integer) tileMapEntry2.getKey()).intValue() == sampleSize || hasMissingTiles) {
                            for (Tile tile2 : (List) tileMapEntry2.getValue()) {
                                sourceToViewRect(tile2.sRect, tile2.vRect);
                                if (!tile2.loading && tile2.bitmap != null) {
                                    if (this.tileBgPaint != null) {
                                        canvas.drawRect(tile2.vRect, this.tileBgPaint);
                                    }
                                    if (this.matrix == null) {
                                        this.matrix = new Matrix();
                                    }
                                    this.matrix.reset();
                                    setMatrixArray(this.srcArray, 0.0f, 0.0f, (float) tile2.bitmap.getWidth(), 0.0f, (float) tile2.bitmap.getWidth(), (float) tile2.bitmap.getHeight(), 0.0f, (float) tile2.bitmap.getHeight());
                                    if (getRequiredRotation() == 0) {
                                        setMatrixArray(this.dstArray, (float) tile2.vRect.left, (float) tile2.vRect.top, (float) tile2.vRect.right, (float) tile2.vRect.top, (float) tile2.vRect.right, (float) tile2.vRect.bottom, (float) tile2.vRect.left, (float) tile2.vRect.bottom);
                                    } else if (getRequiredRotation() == ORIENTATION_90) {
                                        setMatrixArray(this.dstArray, (float) tile2.vRect.right, (float) tile2.vRect.top, (float) tile2.vRect.right, (float) tile2.vRect.bottom, (float) tile2.vRect.left, (float) tile2.vRect.bottom, (float) tile2.vRect.left, (float) tile2.vRect.top);
                                    } else if (getRequiredRotation() == ORIENTATION_180) {
                                        setMatrixArray(this.dstArray, (float) tile2.vRect.right, (float) tile2.vRect.bottom, (float) tile2.vRect.left, (float) tile2.vRect.bottom, (float) tile2.vRect.left, (float) tile2.vRect.top, (float) tile2.vRect.right, (float) tile2.vRect.top);
                                    } else if (getRequiredRotation() == ORIENTATION_270) {
                                        setMatrixArray(this.dstArray, (float) tile2.vRect.left, (float) tile2.vRect.bottom, (float) tile2.vRect.left, (float) tile2.vRect.top, (float) tile2.vRect.right, (float) tile2.vRect.top, (float) tile2.vRect.right, (float) tile2.vRect.bottom);
                                    }
                                    this.matrix.setPolyToPoly(this.srcArray, ORIENTATION_0, this.dstArray, ORIENTATION_0, 4);
                                    canvas.drawBitmap(tile2.bitmap, this.matrix, this.bitmapPaint);
                                    if (this.debug) {
                                        canvas.drawRect(tile2.vRect, this.debugPaint);
                                    }
                                } else if (tile2.loading && this.debug) {
                                    canvas.drawText("LOADING", (float) (tile2.vRect.left + 5), (float) (tile2.vRect.top + 35), this.debugPaint);
                                }
                                if (tile2.visible && this.debug) {
                                    canvas.drawText("ISS " + tile2.sampleSize + " RECT " + tile2.sRect.top + "," + tile2.sRect.left + "," + tile2.sRect.bottom + "," + tile2.sRect.right, (float) (tile2.vRect.left + 5), (float) (tile2.vRect.top + 15), this.debugPaint);
                                }
                            }
                        }
                    }
                    if (this.debug) {
                        StringBuilder append = new StringBuilder().append("Scale: ");
                        Object[] objArr = new Object[ZOOM_FOCUS_FIXED];
                        objArr[ORIENTATION_0] = Float.valueOf(this.scale);
                        canvas.drawText(append.append(String.format("%.2f", objArr)).toString(), 5.0f, 15.0f, this.debugPaint);
                        append = new StringBuilder().append("Translate: ");
                        objArr = new Object[ZOOM_FOCUS_FIXED];
                        objArr[ORIENTATION_0] = Float.valueOf(this.vTranslate.x);
                        append = append.append(String.format("%.2f", objArr)).append(":");
                        objArr = new Object[ZOOM_FOCUS_FIXED];
                        objArr[ORIENTATION_0] = Float.valueOf(this.vTranslate.y);
                        canvas.drawText(append.append(String.format("%.2f", objArr)).toString(), 5.0f, 35.0f, this.debugPaint);
                        PointF center = getCenter();
                        append = new StringBuilder().append("Source center: ");
                        objArr = new Object[ZOOM_FOCUS_FIXED];
                        objArr[ORIENTATION_0] = Float.valueOf(center.x);
                        append = append.append(String.format("%.2f", objArr)).append(":");
                        objArr = new Object[ZOOM_FOCUS_FIXED];
                        objArr[ORIENTATION_0] = Float.valueOf(center.y);
                        canvas.drawText(append.append(String.format("%.2f", objArr)).toString(), 5.0f, 55.0f, this.debugPaint);
                        if (this.anim != null) {
                            PointF vCenterStart = sourceToViewCoord(this.anim.sCenterStart);
                            PointF vCenterEndRequested = sourceToViewCoord(this.anim.sCenterEndRequested);
                            PointF vCenterEnd = sourceToViewCoord(this.anim.sCenterEnd);
                            canvas.drawCircle(vCenterStart.x, vCenterStart.y, 10.0f, this.debugPaint);
                            canvas.drawCircle(vCenterEndRequested.x, vCenterEndRequested.y, 20.0f, this.debugPaint);
                            canvas.drawCircle(vCenterEnd.x, vCenterEnd.y, 25.0f, this.debugPaint);
                            canvas.drawCircle((float) (getWidth() / ZOOM_FOCUS_CENTER), (float) (getHeight() / ZOOM_FOCUS_CENTER), 30.0f, this.debugPaint);
                        }
                    }
                } else if (this.bitmap != null) {
                    float xScale = this.scale;
                    float yScale = this.scale;
                    if (this.bitmapIsPreview) {
                        xScale = this.scale * (((float) this.sWidth) / ((float) this.bitmap.getWidth()));
                        yScale = this.scale * (((float) this.sHeight) / ((float) this.bitmap.getHeight()));
                    }
                    if (this.matrix == null) {
                        this.matrix = new Matrix();
                    }
                    this.matrix.reset();
                    this.matrix.postScale(xScale, yScale);
                    this.matrix.postRotate((float) getRequiredRotation());
                    this.matrix.postTranslate(this.vTranslate.x, this.vTranslate.y);
                    if (getRequiredRotation() == ORIENTATION_180) {
                        this.matrix.postTranslate(this.scale * ((float) this.sWidth), this.scale * ((float) this.sHeight));
                    } else if (getRequiredRotation() == ORIENTATION_90) {
                        this.matrix.postTranslate(this.scale * ((float) this.sHeight), 0.0f);
                    } else if (getRequiredRotation() == ORIENTATION_270) {
                        this.matrix.postTranslate(0.0f, this.scale * ((float) this.sWidth));
                    }
                    if (this.tileBgPaint != null) {
                        if (this.sRect == null) {
                            this.sRect = new RectF();
                        }
                        this.sRect.set(0.0f, 0.0f, (float) this.sWidth, (float) this.sHeight);
                        this.matrix.mapRect(this.sRect);
                        canvas.drawRect(this.sRect, this.tileBgPaint);
                    }
                    canvas.drawBitmap(this.bitmap, this.matrix, this.bitmapPaint);
                }
            }
        }
    }

    private void setMatrixArray(float[] array, float f0, float f1, float f2, float f3, float f4, float f5, float f6, float f7) {
        array[ORIENTATION_0] = f0;
        array[ZOOM_FOCUS_FIXED] = f1;
        array[ZOOM_FOCUS_CENTER] = f2;
        array[ZOOM_FOCUS_CENTER_IMMEDIATE] = f3;
        array[4] = f4;
        array[5] = f5;
        array[6] = f6;
        array[7] = f7;
    }

    private boolean isBaseLayerReady() {
        if (this.bitmap != null && !this.bitmapIsPreview) {
            return true;
        }
        if (this.tileMap == null) {
            return false;
        }
        boolean baseLayerReady = true;
        for (Entry<Integer, List<Tile>> tileMapEntry : this.tileMap.entrySet()) {
            if (((Integer) tileMapEntry.getKey()).intValue() == this.fullImageSampleSize) {
                for (Tile tile : (List) tileMapEntry.getValue()) {
                    if (tile.loading || tile.bitmap == null) {
                        baseLayerReady = false;
                    }
                }
            }
        }
        return baseLayerReady;
    }

    private boolean checkReady() {
        boolean ready = getWidth() > 0 && getHeight() > 0 && this.sWidth > 0 && this.sHeight > 0 && (this.bitmap != null || isBaseLayerReady());
        if (!this.readySent && ready) {
            preDraw();
            this.readySent = true;
            onReady();
            if (this.onImageEventListener != null) {
                this.onImageEventListener.onReady();
            }
        }
        return ready;
    }

    private boolean checkImageLoaded() {
        boolean imageLoaded = isBaseLayerReady();
        if (!this.imageLoadedSent && imageLoaded) {
            preDraw();
            this.imageLoadedSent = true;
            onImageLoaded();
            if (this.onImageEventListener != null) {
                this.onImageEventListener.onImageLoaded();
            }
        }
        return imageLoaded;
    }

    private void createPaints() {
        if (this.bitmapPaint == null) {
            this.bitmapPaint = new Paint();
            this.bitmapPaint.setAntiAlias(true);
            this.bitmapPaint.setFilterBitmap(true);
            this.bitmapPaint.setDither(true);
        }
        if (this.debugPaint == null && this.debug) {
            this.debugPaint = new Paint();
            this.debugPaint.setTextSize(18.0f);
            this.debugPaint.setColor(-65281);
            this.debugPaint.setStyle(Style.STROKE);
        }
    }

    private synchronized void initialiseBaseLayer(Point maxTileDimensions) {
        this.satTemp = new ScaleAndTranslate(new PointF(0.0f, 0.0f), null);
        fitToBounds(true, this.satTemp);
        this.fullImageSampleSize = calculateInSampleSize(this.satTemp.scale);
        if (this.fullImageSampleSize > ZOOM_FOCUS_FIXED) {
            this.fullImageSampleSize /= ZOOM_FOCUS_CENTER;
        }
        if (this.fullImageSampleSize != ZOOM_FOCUS_FIXED || this.sRegion != null || sWidth() >= maxTileDimensions.x || sHeight() >= maxTileDimensions.y) {
            initialiseTileMap(maxTileDimensions);
            for (Tile baseTile : (List) this.tileMap.get(Integer.valueOf(this.fullImageSampleSize))) {
                execute(new TileLoadTask(this, this.decoder, baseTile));
            }
            refreshRequiredTiles(true);
        } else {
            this.decoder.recycle();
            this.decoder = null;
            execute(new BitmapLoadTask(this, getContext(), this.bitmapDecoderFactory, this.uri, false));
        }
    }

    private void refreshRequiredTiles(boolean load) {
        if (this.decoder != null && this.tileMap != null) {
            int sampleSize = Math.min(this.fullImageSampleSize, calculateInSampleSize(this.scale));
            for (Entry<Integer, List<Tile>> tileMapEntry : this.tileMap.entrySet()) {
                for (Tile tile : (List) tileMapEntry.getValue()) {
                    if (tile.sampleSize < sampleSize || (tile.sampleSize > sampleSize && tile.sampleSize != this.fullImageSampleSize)) {
                        tile.visible = false;
                        if (tile.bitmap != null) {
                            tile.bitmap.recycle();
                            tile.bitmap = null;
                        }
                    }
                    if (tile.sampleSize == sampleSize) {
                        if (tileVisible(tile)) {
                            tile.visible = true;
                            if (!tile.loading && tile.bitmap == null && load) {
                                execute(new TileLoadTask(this, this.decoder, tile));
                            }
                        } else if (tile.sampleSize != this.fullImageSampleSize) {
                            tile.visible = false;
                            if (tile.bitmap != null) {
                                tile.bitmap.recycle();
                                tile.bitmap = null;
                            }
                        }
                    } else if (tile.sampleSize == this.fullImageSampleSize) {
                        tile.visible = true;
                    }
                }
            }
        }
    }

    private boolean tileVisible(Tile tile) {
        return viewToSourceX(0.0f) <= ((float) tile.sRect.right) && ((float) tile.sRect.left) <= viewToSourceX((float) getWidth()) && viewToSourceY(0.0f) <= ((float) tile.sRect.bottom) && ((float) tile.sRect.top) <= viewToSourceY((float) getHeight());
    }

    private void preDraw() {
        if (getWidth() != 0 && getHeight() != 0 && this.sWidth > 0 && this.sHeight > 0) {
            if (!(this.sPendingCenter == null || this.pendingScale == null)) {
                this.scale = this.pendingScale.floatValue();
                if (this.vTranslate == null) {
                    this.vTranslate = new PointF();
                }
                this.vTranslate.x = ((float) (getWidth() / ZOOM_FOCUS_CENTER)) - (this.scale * this.sPendingCenter.x);
                this.vTranslate.y = ((float) (getHeight() / ZOOM_FOCUS_CENTER)) - (this.scale * this.sPendingCenter.y);
                this.sPendingCenter = null;
                this.pendingScale = null;
                fitToBounds(true);
                refreshRequiredTiles(true);
            }
            fitToBounds(false);
        }
    }

    private int calculateInSampleSize(float scale) {
        if (this.minimumTileDpi > 0) {
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            scale *= ((float) this.minimumTileDpi) / ((metrics.xdpi + metrics.ydpi) / 2.0f);
        }
        int reqWidth = (int) (((float) sWidth()) * scale);
        int reqHeight = (int) (((float) sHeight()) * scale);
        int inSampleSize = ZOOM_FOCUS_FIXED;
        if (reqWidth == 0 || reqHeight == 0) {
            return 32;
        }
        if (sHeight() > reqHeight || sWidth() > reqWidth) {
            int heightRatio = Math.round(((float) sHeight()) / ((float) reqHeight));
            int widthRatio = Math.round(((float) sWidth()) / ((float) reqWidth));
            if (heightRatio < widthRatio) {
                inSampleSize = heightRatio;
            } else {
                inSampleSize = widthRatio;
            }
        }
        int power = ZOOM_FOCUS_FIXED;
        while (power * ZOOM_FOCUS_CENTER < inSampleSize) {
            power *= ZOOM_FOCUS_CENTER;
        }
        return power;
    }

    private void fitToBounds(boolean center, ScaleAndTranslate sat) {
        float maxTx;
        float maxTy;
        if (this.panLimit == ZOOM_FOCUS_CENTER && isReady()) {
            center = false;
        }
        PointF vTranslate = sat.vTranslate;
        float scale = limitedScale(sat.scale);
        float scaleWidth = scale * ((float) sWidth());
        float scaleHeight = scale * ((float) sHeight());
        if (this.panLimit == ZOOM_FOCUS_CENTER_IMMEDIATE && isReady()) {
            vTranslate.x = Math.max(vTranslate.x, ((float) (getWidth() / ZOOM_FOCUS_CENTER)) - scaleWidth);
            vTranslate.y = Math.max(vTranslate.y, ((float) (getHeight() / ZOOM_FOCUS_CENTER)) - scaleHeight);
        } else if (center) {
            vTranslate.x = Math.max(vTranslate.x, ((float) getWidth()) - scaleWidth);
            vTranslate.y = Math.max(vTranslate.y, ((float) getHeight()) - scaleHeight);
        } else {
            vTranslate.x = Math.max(vTranslate.x, -scaleWidth);
            vTranslate.y = Math.max(vTranslate.y, -scaleHeight);
        }
        float xPaddingRatio = (getPaddingLeft() > 0 || getPaddingRight() > 0) ? ((float) getPaddingLeft()) / ((float) (getPaddingLeft() + getPaddingRight())) : 0.5f;
        float yPaddingRatio = (getPaddingTop() > 0 || getPaddingBottom() > 0) ? ((float) getPaddingTop()) / ((float) (getPaddingTop() + getPaddingBottom())) : 0.5f;
        if (this.panLimit == ZOOM_FOCUS_CENTER_IMMEDIATE && isReady()) {
            maxTx = (float) Math.max(ORIENTATION_0, getWidth() / ZOOM_FOCUS_CENTER);
            maxTy = (float) Math.max(ORIENTATION_0, getHeight() / ZOOM_FOCUS_CENTER);
        } else if (center) {
            maxTx = Math.max(0.0f, (((float) getWidth()) - scaleWidth) * xPaddingRatio);
            maxTy = Math.max(0.0f, (((float) getHeight()) - scaleHeight) * yPaddingRatio);
        } else {
            maxTx = (float) Math.max(ORIENTATION_0, getWidth());
            maxTy = (float) Math.max(ORIENTATION_0, getHeight());
        }
        vTranslate.x = Math.min(vTranslate.x, maxTx);
        vTranslate.y = Math.min(vTranslate.y, maxTy);
        sat.scale = scale;
    }

    private void fitToBounds(boolean center) {
        boolean init = false;
        if (this.vTranslate == null) {
            init = true;
            this.vTranslate = new PointF(0.0f, 0.0f);
        }
        if (this.satTemp == null) {
            this.satTemp = new ScaleAndTranslate(new PointF(0.0f, 0.0f), null);
        }
        this.satTemp.scale = this.scale;
        this.satTemp.vTranslate.set(this.vTranslate);
        fitToBounds(center, this.satTemp);
        this.scale = this.satTemp.scale;
        this.vTranslate.set(this.satTemp.vTranslate);
        if (init) {
            this.vTranslate.set(vTranslateForSCenter((float) (sWidth() / ZOOM_FOCUS_CENTER), (float) (sHeight() / ZOOM_FOCUS_CENTER), this.scale));
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void initialiseTileMap(android.graphics.Point r21) {
        /*
        r20 = this;
        r13 = new java.util.LinkedHashMap;
        r13.<init>();
        r0 = r20;
        r0.tileMap = r13;
        r0 = r20;
        r4 = r0.fullImageSampleSize;
        r10 = 1;
        r12 = 1;
    L_0x000f:
        r13 = r20.sWidth();
        r3 = r13 / r10;
        r13 = r20.sHeight();
        r2 = r13 / r12;
        r6 = r3 / r4;
        r5 = r2 / r4;
    L_0x001f:
        r13 = r6 + r10;
        r13 = r13 + 1;
        r0 = r21;
        r14 = r0.x;
        if (r13 > r14) goto L_0x003f;
    L_0x0029:
        r14 = (double) r6;
        r13 = r20.getWidth();
        r0 = (double) r13;
        r16 = r0;
        r18 = 4608308318706860032; // 0x3ff4000000000000 float:0.0 double:1.25;
        r16 = r16 * r18;
        r13 = (r14 > r16 ? 1 : (r14 == r16 ? 0 : -1));
        if (r13 <= 0) goto L_0x004a;
    L_0x0039:
        r0 = r20;
        r13 = r0.fullImageSampleSize;
        if (r4 >= r13) goto L_0x004a;
    L_0x003f:
        r10 = r10 + 1;
        r13 = r20.sWidth();
        r3 = r13 / r10;
        r6 = r3 / r4;
        goto L_0x001f;
    L_0x004a:
        r13 = r5 + r12;
        r13 = r13 + 1;
        r0 = r21;
        r14 = r0.y;
        if (r13 > r14) goto L_0x006a;
    L_0x0054:
        r14 = (double) r5;
        r13 = r20.getHeight();
        r0 = (double) r13;
        r16 = r0;
        r18 = 4608308318706860032; // 0x3ff4000000000000 float:0.0 double:1.25;
        r16 = r16 * r18;
        r13 = (r14 > r16 ? 1 : (r14 == r16 ? 0 : -1));
        if (r13 <= 0) goto L_0x0075;
    L_0x0064:
        r0 = r20;
        r13 = r0.fullImageSampleSize;
        if (r4 >= r13) goto L_0x0075;
    L_0x006a:
        r12 = r12 + 1;
        r13 = r20.sHeight();
        r2 = r13 / r12;
        r5 = r2 / r4;
        goto L_0x004a;
    L_0x0075:
        r8 = new java.util.ArrayList;
        r13 = r10 * r12;
        r8.<init>(r13);
        r9 = 0;
    L_0x007d:
        if (r9 >= r10) goto L_0x00e4;
    L_0x007f:
        r11 = 0;
    L_0x0080:
        if (r11 >= r12) goto L_0x00e1;
    L_0x0082:
        r7 = new com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView$Tile;
        r13 = 0;
        r7.<init>();
        r7.sampleSize = r4;
        r0 = r20;
        r13 = r0.fullImageSampleSize;
        if (r4 != r13) goto L_0x00d6;
    L_0x0091:
        r13 = 1;
    L_0x0092:
        r7.visible = r13;
        r15 = new android.graphics.Rect;
        r16 = r9 * r3;
        r17 = r11 * r2;
        r13 = r10 + -1;
        if (r9 != r13) goto L_0x00d8;
    L_0x009f:
        r13 = r20.sWidth();
        r14 = r13;
    L_0x00a4:
        r13 = r12 + -1;
        if (r11 != r13) goto L_0x00dd;
    L_0x00a8:
        r13 = r20.sHeight();
    L_0x00ac:
        r0 = r16;
        r1 = r17;
        r15.<init>(r0, r1, r14, r13);
        r7.sRect = r15;
        r13 = new android.graphics.Rect;
        r14 = 0;
        r15 = 0;
        r16 = 0;
        r17 = 0;
        r13.<init>(r14, r15, r16, r17);
        r7.vRect = r13;
        r13 = new android.graphics.Rect;
        r14 = r7.sRect;
        r13.<init>(r14);
        r7.fileSRect = r13;
        r8.add(r7);
        r11 = r11 + 1;
        goto L_0x0080;
    L_0x00d6:
        r13 = 0;
        goto L_0x0092;
    L_0x00d8:
        r13 = r9 + 1;
        r13 = r13 * r3;
        r14 = r13;
        goto L_0x00a4;
    L_0x00dd:
        r13 = r11 + 1;
        r13 = r13 * r2;
        goto L_0x00ac;
    L_0x00e1:
        r9 = r9 + 1;
        goto L_0x007d;
    L_0x00e4:
        r0 = r20;
        r13 = r0.tileMap;
        r14 = java.lang.Integer.valueOf(r4);
        r13.put(r14, r8);
        r13 = 1;
        if (r4 != r13) goto L_0x00f3;
    L_0x00f2:
        return;
    L_0x00f3:
        r4 = r4 / 2;
        goto L_0x000f;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView.initialiseTileMap(android.graphics.Point):void");
    }

    private synchronized void onTilesInited(ImageRegionDecoder decoder, int sWidth, int sHeight, int sOrientation) {
        if (this.sWidth > 0 && this.sHeight > 0 && !(this.sWidth == sWidth && this.sHeight == sHeight)) {
            reset(false);
            if (this.bitmap != null) {
                if (!this.bitmapIsCached) {
                    this.bitmap.recycle();
                }
                this.bitmap = null;
                this.bitmapIsPreview = false;
                this.bitmapIsCached = false;
            }
        }
        this.decoder = decoder;
        this.sWidth = sWidth;
        this.sHeight = sHeight;
        this.sOrientation = sOrientation;
        checkReady();
        checkImageLoaded();
        invalidate();
        requestLayout();
    }

    private synchronized void onTileLoaded() {
        checkReady();
        checkImageLoaded();
        if (isBaseLayerReady() && this.bitmap != null) {
            if (!this.bitmapIsCached) {
                this.bitmap.recycle();
            }
            this.bitmap = null;
            this.bitmapIsPreview = false;
            this.bitmapIsCached = false;
        }
        invalidate();
    }

    private synchronized void onPreviewLoaded(Bitmap previewBitmap) {
        if (this.bitmap != null || this.imageLoadedSent) {
            previewBitmap.recycle();
        } else {
            if (this.pRegion != null) {
                this.bitmap = Bitmap.createBitmap(previewBitmap, this.pRegion.left, this.pRegion.top, this.pRegion.width(), this.pRegion.height());
            } else {
                this.bitmap = previewBitmap;
            }
            this.bitmapIsPreview = true;
            if (checkReady()) {
                invalidate();
                requestLayout();
            }
        }
    }

    private synchronized void onImageLoaded(Bitmap bitmap, int sOrientation, boolean bitmapIsCached) {
        if (this.sWidth > 0 && this.sHeight > 0 && !(this.sWidth == bitmap.getWidth() && this.sHeight == bitmap.getHeight())) {
            reset(false);
        }
        if (!(this.bitmap == null || this.bitmapIsCached)) {
            this.bitmap.recycle();
        }
        this.bitmapIsPreview = false;
        this.bitmapIsCached = bitmapIsCached;
        this.bitmap = bitmap;
        this.sWidth = bitmap.getWidth();
        this.sHeight = bitmap.getHeight();
        this.sOrientation = sOrientation;
        boolean ready = checkReady();
        boolean imageLoaded = checkImageLoaded();
        if (ready || imageLoaded) {
            invalidate();
            requestLayout();
        }
    }

    private int getExifOrientation(String sourceUri) {
        int exifOrientation = ORIENTATION_0;
        if (sourceUri.startsWith("content")) {
            try {
                String[] columns = new String[ZOOM_FOCUS_FIXED];
                columns[ORIENTATION_0] = "orientation";
                Cursor cursor = getContext().getContentResolver().query(Uri.parse(sourceUri), columns, null, null, null);
                if (cursor == null) {
                    return ORIENTATION_0;
                }
                if (cursor.moveToFirst()) {
                    int orientation = cursor.getInt(ORIENTATION_0);
                    if (!VALID_ORIENTATIONS.contains(Integer.valueOf(orientation)) || orientation == ORIENTATION_USE_EXIF) {
                        Log.w(TAG, "Unsupported orientation: " + orientation);
                    } else {
                        exifOrientation = orientation;
                    }
                }
                cursor.close();
                return exifOrientation;
            } catch (Exception e) {
                Log.w(TAG, "Could not get orientation of image from media store");
                return ORIENTATION_0;
            }
        } else if (!sourceUri.startsWith("file:///") || sourceUri.startsWith("file:///android_asset/")) {
            return ORIENTATION_0;
        } else {
            try {
                int orientationAttr = new ExifInterface(sourceUri.substring("file:///".length() + ORIENTATION_USE_EXIF)).getAttributeInt("Orientation", ZOOM_FOCUS_FIXED);
                if (orientationAttr == ZOOM_FOCUS_FIXED || orientationAttr == 0) {
                    return ORIENTATION_0;
                }
                if (orientationAttr == 6) {
                    return ORIENTATION_90;
                }
                if (orientationAttr == ZOOM_FOCUS_CENTER_IMMEDIATE) {
                    return ORIENTATION_180;
                }
                if (orientationAttr == 8) {
                    return ORIENTATION_270;
                }
                Log.w(TAG, "Unsupported EXIF orientation: " + orientationAttr);
                return ORIENTATION_0;
            } catch (Exception e2) {
                Log.w(TAG, "Could not get EXIF orientation of image");
                return ORIENTATION_0;
            }
        }
    }

    private void execute(AsyncTask<Void, Void, ?> asyncTask) {
        if (this.parallelLoadingEnabled && VERSION.SDK_INT >= 11) {
            try {
                Executor executor = (Executor) AsyncTask.class.getField("THREAD_POOL_EXECUTOR").get(null);
                Class[] clsArr = new Class[ZOOM_FOCUS_CENTER];
                clsArr[ORIENTATION_0] = Executor.class;
                clsArr[ZOOM_FOCUS_FIXED] = Object[].class;
                Method executeMethod = AsyncTask.class.getMethod("executeOnExecutor", clsArr);
                Object[] objArr = new Object[ZOOM_FOCUS_CENTER];
                objArr[ORIENTATION_0] = executor;
                objArr[ZOOM_FOCUS_FIXED] = null;
                executeMethod.invoke(asyncTask, objArr);
                return;
            } catch (Exception e) {
                Log.i(TAG, "Failed to execute AsyncTask on thread pool executor, falling back to single threaded executor", e);
            }
        }
        asyncTask.execute(new Void[ORIENTATION_0]);
    }

    private void restoreState(ImageViewState state) {
        if (state != null && state.getCenter() != null && VALID_ORIENTATIONS.contains(Integer.valueOf(state.getOrientation()))) {
            this.orientation = state.getOrientation();
            this.pendingScale = Float.valueOf(state.getScale());
            this.sPendingCenter = state.getCenter();
            invalidate();
        }
    }

    private Point getMaxBitmapDimensions(Canvas canvas) {
        if (VERSION.SDK_INT >= 14) {
            try {
                return new Point(((Integer) Canvas.class.getMethod("getMaximumBitmapWidth", new Class[ORIENTATION_0]).invoke(canvas, new Object[ORIENTATION_0])).intValue(), ((Integer) Canvas.class.getMethod("getMaximumBitmapHeight", new Class[ORIENTATION_0]).invoke(canvas, new Object[ORIENTATION_0])).intValue());
            } catch (Exception e) {
            }
        }
        return new Point(ItemAnimator.FLAG_MOVED, ItemAnimator.FLAG_MOVED);
    }

    private int sWidth() {
        int rotation = getRequiredRotation();
        if (rotation == ORIENTATION_90 || rotation == ORIENTATION_270) {
            return this.sHeight;
        }
        return this.sWidth;
    }

    private int sHeight() {
        int rotation = getRequiredRotation();
        if (rotation == ORIENTATION_90 || rotation == ORIENTATION_270) {
            return this.sWidth;
        }
        return this.sHeight;
    }

    private void fileSRect(Rect sRect, Rect target) {
        if (getRequiredRotation() == 0) {
            target.set(sRect);
        } else if (getRequiredRotation() == ORIENTATION_90) {
            target.set(sRect.top, this.sHeight - sRect.right, sRect.bottom, this.sHeight - sRect.left);
        } else if (getRequiredRotation() == ORIENTATION_180) {
            target.set(this.sWidth - sRect.right, this.sHeight - sRect.bottom, this.sWidth - sRect.left, this.sHeight - sRect.top);
        } else {
            target.set(this.sWidth - sRect.bottom, sRect.left, this.sWidth - sRect.top, sRect.right);
        }
    }

    private int getRequiredRotation() {
        if (this.orientation == ORIENTATION_USE_EXIF) {
            return this.sOrientation;
        }
        return this.orientation;
    }

    private float distance(float x0, float x1, float y0, float y1) {
        float x = x0 - x1;
        float y = y0 - y1;
        return (float) Math.sqrt((double) ((x * x) + (y * y)));
    }

    public void recycle() {
        reset(true);
        this.bitmapPaint = null;
        this.debugPaint = null;
        this.tileBgPaint = null;
    }

    private float viewToSourceX(float vx) {
        if (this.vTranslate == null) {
            return Float.NaN;
        }
        return (vx - this.vTranslate.x) / this.scale;
    }

    private float viewToSourceY(float vy) {
        if (this.vTranslate == null) {
            return Float.NaN;
        }
        return (vy - this.vTranslate.y) / this.scale;
    }

    public final PointF viewToSourceCoord(PointF vxy) {
        return viewToSourceCoord(vxy.x, vxy.y, new PointF());
    }

    public final PointF viewToSourceCoord(float vx, float vy) {
        return viewToSourceCoord(vx, vy, new PointF());
    }

    public final PointF viewToSourceCoord(PointF vxy, PointF sTarget) {
        return viewToSourceCoord(vxy.x, vxy.y, sTarget);
    }

    public final PointF viewToSourceCoord(float vx, float vy, PointF sTarget) {
        if (this.vTranslate == null) {
            return null;
        }
        sTarget.set(viewToSourceX(vx), viewToSourceY(vy));
        return sTarget;
    }

    private float sourceToViewX(float sx) {
        if (this.vTranslate == null) {
            return Float.NaN;
        }
        return (this.scale * sx) + this.vTranslate.x;
    }

    private float sourceToViewY(float sy) {
        if (this.vTranslate == null) {
            return Float.NaN;
        }
        return (this.scale * sy) + this.vTranslate.y;
    }

    public final PointF sourceToViewCoord(PointF sxy) {
        return sourceToViewCoord(sxy.x, sxy.y, new PointF());
    }

    public final PointF sourceToViewCoord(float sx, float sy) {
        return sourceToViewCoord(sx, sy, new PointF());
    }

    public final PointF sourceToViewCoord(PointF sxy, PointF vTarget) {
        return sourceToViewCoord(sxy.x, sxy.y, vTarget);
    }

    public final PointF sourceToViewCoord(float sx, float sy, PointF vTarget) {
        if (this.vTranslate == null) {
            return null;
        }
        vTarget.set(sourceToViewX(sx), sourceToViewY(sy));
        return vTarget;
    }

    private Rect sourceToViewRect(Rect sRect, Rect vTarget) {
        vTarget.set((int) sourceToViewX((float) sRect.left), (int) sourceToViewY((float) sRect.top), (int) sourceToViewX((float) sRect.right), (int) sourceToViewY((float) sRect.bottom));
        return vTarget;
    }

    private PointF vTranslateForSCenter(float sCenterX, float sCenterY, float scale) {
        int vxCenter = getPaddingLeft() + (((getWidth() - getPaddingRight()) - getPaddingLeft()) / ZOOM_FOCUS_CENTER);
        int vyCenter = getPaddingTop() + (((getHeight() - getPaddingBottom()) - getPaddingTop()) / ZOOM_FOCUS_CENTER);
        if (this.satTemp == null) {
            this.satTemp = new ScaleAndTranslate(new PointF(0.0f, 0.0f), null);
        }
        this.satTemp.scale = scale;
        this.satTemp.vTranslate.set(((float) vxCenter) - (sCenterX * scale), ((float) vyCenter) - (sCenterY * scale));
        fitToBounds(true, this.satTemp);
        return this.satTemp.vTranslate;
    }

    private PointF limitedSCenter(float sCenterX, float sCenterY, float scale, PointF sTarget) {
        PointF vTranslate = vTranslateForSCenter(sCenterX, sCenterY, scale);
        sTarget.set((((float) (getPaddingLeft() + (((getWidth() - getPaddingRight()) - getPaddingLeft()) / ZOOM_FOCUS_CENTER))) - vTranslate.x) / scale, (((float) (getPaddingTop() + (((getHeight() - getPaddingBottom()) - getPaddingTop()) / ZOOM_FOCUS_CENTER))) - vTranslate.y) / scale);
        return sTarget;
    }

    private float minScale() {
        int vPadding = getPaddingBottom() + getPaddingTop();
        int hPadding = getPaddingLeft() + getPaddingRight();
        if (this.minimumScaleType == ZOOM_FOCUS_CENTER) {
            return Math.max(((float) (getWidth() - hPadding)) / ((float) sWidth()), ((float) (getHeight() - vPadding)) / ((float) sHeight()));
        }
        if (this.minimumScaleType != ZOOM_FOCUS_CENTER_IMMEDIATE || this.minScale <= 0.0f) {
            return Math.min(((float) (getWidth() - hPadding)) / ((float) sWidth()), ((float) (getHeight() - vPadding)) / ((float) sHeight()));
        }
        return this.minScale;
    }

    private float limitedScale(float targetScale) {
        return Math.min(this.maxScale, Math.max(minScale(), targetScale));
    }

    private float ease(int type, long time, float from, float change, long duration) {
        switch (type) {
            case ZOOM_FOCUS_FIXED /*1*/:
                return easeOutQuad(time, from, change, duration);
            case ZOOM_FOCUS_CENTER /*2*/:
                return easeInOutQuad(time, from, change, duration);
            default:
                throw new IllegalStateException("Unexpected easing type: " + type);
        }
    }

    private float easeOutQuad(long time, float from, float change, long duration) {
        float progress = ((float) time) / ((float) duration);
        return (((-change) * progress) * (progress - 2.0f)) + from;
    }

    private float easeInOutQuad(long time, float from, float change, long duration) {
        float timeF = ((float) time) / (((float) duration) / 2.0f);
        if (timeF < 1.0f) {
            return (((change / 2.0f) * timeF) * timeF) + from;
        }
        timeF -= 1.0f;
        return (((-change) / 2.0f) * (((timeF - 2.0f) * timeF) - 1.0f)) + from;
    }

    public final void setRegionDecoderClass(Class<? extends ImageRegionDecoder> regionDecoderClass) {
        if (regionDecoderClass == null) {
            throw new IllegalArgumentException("Decoder class cannot be set to null");
        }
        this.regionDecoderFactory = new CompatDecoderFactory(regionDecoderClass);
    }

    public final void setRegionDecoderFactory(DecoderFactory<? extends ImageRegionDecoder> regionDecoderFactory) {
        if (regionDecoderFactory == null) {
            throw new IllegalArgumentException("Decoder factory cannot be set to null");
        }
        this.regionDecoderFactory = regionDecoderFactory;
    }

    public final void setBitmapDecoderClass(Class<? extends ImageDecoder> bitmapDecoderClass) {
        if (bitmapDecoderClass == null) {
            throw new IllegalArgumentException("Decoder class cannot be set to null");
        }
        this.bitmapDecoderFactory = new CompatDecoderFactory(bitmapDecoderClass);
    }

    public final void setBitmapDecoderFactory(DecoderFactory<? extends ImageDecoder> bitmapDecoderFactory) {
        if (bitmapDecoderFactory == null) {
            throw new IllegalArgumentException("Decoder factory cannot be set to null");
        }
        this.bitmapDecoderFactory = bitmapDecoderFactory;
    }

    public final void setPanLimit(int panLimit) {
        if (VALID_PAN_LIMITS.contains(Integer.valueOf(panLimit))) {
            this.panLimit = panLimit;
            if (isReady()) {
                fitToBounds(true);
                invalidate();
                return;
            }
            return;
        }
        throw new IllegalArgumentException("Invalid pan limit: " + panLimit);
    }

    public final void setMinimumScaleType(int scaleType) {
        if (VALID_SCALE_TYPES.contains(Integer.valueOf(scaleType))) {
            this.minimumScaleType = scaleType;
            if (isReady()) {
                fitToBounds(true);
                invalidate();
                return;
            }
            return;
        }
        throw new IllegalArgumentException("Invalid scale type: " + scaleType);
    }

    public final void setMaxScale(float maxScale) {
        this.maxScale = maxScale;
    }

    public final void setMinScale(float minScale) {
        this.minScale = minScale;
    }

    public final void setMinimumDpi(int dpi) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        setMaxScale(((metrics.xdpi + metrics.ydpi) / 2.0f) / ((float) dpi));
    }

    public final void setMaximumDpi(int dpi) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        setMinScale(((metrics.xdpi + metrics.ydpi) / 2.0f) / ((float) dpi));
    }

    public float getMaxScale() {
        return this.maxScale;
    }

    public final float getMinScale() {
        return minScale();
    }

    public void setMinimumTileDpi(int minimumTileDpi) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        this.minimumTileDpi = (int) Math.min((metrics.xdpi + metrics.ydpi) / 2.0f, (float) minimumTileDpi);
        if (isReady()) {
            reset(false);
            invalidate();
        }
    }

    public final PointF getCenter() {
        return viewToSourceCoord((float) (getWidth() / ZOOM_FOCUS_CENTER), (float) (getHeight() / ZOOM_FOCUS_CENTER));
    }

    public final float getScale() {
        return this.scale;
    }

    public final void setScaleAndCenter(float scale, PointF sCenter) {
        this.anim = null;
        this.pendingScale = Float.valueOf(scale);
        this.sPendingCenter = sCenter;
        this.sRequestedCenter = sCenter;
        invalidate();
    }

    public final void resetScaleAndCenter() {
        this.anim = null;
        this.pendingScale = Float.valueOf(limitedScale(0.0f));
        if (isReady()) {
            this.sPendingCenter = new PointF((float) (sWidth() / ZOOM_FOCUS_CENTER), (float) (sHeight() / ZOOM_FOCUS_CENTER));
        } else {
            this.sPendingCenter = new PointF(0.0f, 0.0f);
        }
        invalidate();
    }

    public final boolean isReady() {
        return this.readySent;
    }

    protected void onReady() {
    }

    public final boolean isImageLoaded() {
        return this.imageLoadedSent;
    }

    protected void onImageLoaded() {
    }

    public final int getSWidth() {
        return this.sWidth;
    }

    public final int getSHeight() {
        return this.sHeight;
    }

    public final int getOrientation() {
        return this.orientation;
    }

    public final int getAppliedOrientation() {
        return getRequiredRotation();
    }

    public final ImageViewState getState() {
        if (this.vTranslate == null || this.sWidth <= 0 || this.sHeight <= 0) {
            return null;
        }
        return new ImageViewState(getScale(), getCenter(), getOrientation());
    }

    public final boolean isZoomEnabled() {
        return this.zoomEnabled;
    }

    public final void setZoomEnabled(boolean zoomEnabled) {
        this.zoomEnabled = zoomEnabled;
    }

    public final boolean isQuickScaleEnabled() {
        return this.quickScaleEnabled;
    }

    public final void setQuickScaleEnabled(boolean quickScaleEnabled) {
        this.quickScaleEnabled = quickScaleEnabled;
    }

    public final boolean isPanEnabled() {
        return this.panEnabled;
    }

    public final void setPanEnabled(boolean panEnabled) {
        this.panEnabled = panEnabled;
        if (!panEnabled && this.vTranslate != null) {
            this.vTranslate.x = ((float) (getWidth() / ZOOM_FOCUS_CENTER)) - (this.scale * ((float) (sWidth() / ZOOM_FOCUS_CENTER)));
            this.vTranslate.y = ((float) (getHeight() / ZOOM_FOCUS_CENTER)) - (this.scale * ((float) (sHeight() / ZOOM_FOCUS_CENTER)));
            if (isReady()) {
                refreshRequiredTiles(true);
                invalidate();
            }
        }
    }

    public final void setTileBackgroundColor(int tileBgColor) {
        if (Color.alpha(tileBgColor) == 0) {
            this.tileBgPaint = null;
        } else {
            this.tileBgPaint = new Paint();
            this.tileBgPaint.setStyle(Style.FILL);
            this.tileBgPaint.setColor(tileBgColor);
        }
        invalidate();
    }

    public final void setDoubleTapZoomScale(float doubleTapZoomScale) {
        this.doubleTapZoomScale = doubleTapZoomScale;
    }

    public final void setDoubleTapZoomDpi(int dpi) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        setDoubleTapZoomScale(((metrics.xdpi + metrics.ydpi) / 2.0f) / ((float) dpi));
    }

    public final void setDoubleTapZoomStyle(int doubleTapZoomStyle) {
        if (VALID_ZOOM_STYLES.contains(Integer.valueOf(doubleTapZoomStyle))) {
            this.doubleTapZoomStyle = doubleTapZoomStyle;
            return;
        }
        throw new IllegalArgumentException("Invalid zoom style: " + doubleTapZoomStyle);
    }

    public void setParallelLoadingEnabled(boolean parallelLoadingEnabled) {
        this.parallelLoadingEnabled = parallelLoadingEnabled;
    }

    public final void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void setOnLongClickListener(OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }

    public void setOnImageEventListener(OnImageEventListener onImageEventListener) {
        this.onImageEventListener = onImageEventListener;
    }

    public AnimationBuilder animateCenter(PointF sCenter) {
        if (isReady()) {
            return new AnimationBuilder(sCenter, null);
        }
        return null;
    }

    public AnimationBuilder animateScale(float scale) {
        if (isReady()) {
            return new AnimationBuilder(scale, null);
        }
        return null;
    }

    public AnimationBuilder animateScaleAndCenter(float scale, PointF sCenter) {
        if (isReady()) {
            return new AnimationBuilder(scale, sCenter, null);
        }
        return null;
    }
}
