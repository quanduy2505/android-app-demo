package me.wangyuwei.loadingview;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Shader.TileMode;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

public class LoadingView extends View {
    private static final int MAX_DURATION = 120;
    private static final int MIN_DURATION = 1;
    private final int DEFAULT_DURATION;
    private final int DEFAULT_EXTERNAL_RADIUS;
    private final int DEFAULT_INTERNAL_RADIUS;
    private final int DEFAULT_RADIAN;
    private final int MAX_EXTERNAL_R;
    private final int MAX_INTERNAL_R;
    private final int MIN_EXTERNAL_R;
    private final int MIN_INTERNAL_R;
    int mAngle;
    private List<ValueAnimator> mAnimators;
    int[] mColors;
    int mCyclic;
    private int mDuration;
    private float mExternalR;
    private float mGetBiggerCircleRadius;
    private float mGetSmallerCircleRadius;
    private int mHeight;
    private float mInternalR;
    private Paint mPaint;
    private Path mPath;
    private List<PointF> mPoints;
    private int mRadian;
    private Subscription mTimer;
    private int mWidth;
    private float x0;
    private float y0;

    /* renamed from: me.wangyuwei.loadingview.LoadingView.3 */
    class C07993 implements AnimatorUpdateListener {
        C07993() {
        }

        public void onAnimationUpdate(ValueAnimator animation) {
            LoadingView.this.mGetSmallerCircleRadius = ((Float) animation.getAnimatedValue()).floatValue();
        }
    }

    /* renamed from: me.wangyuwei.loadingview.LoadingView.4 */
    class C08004 implements AnimatorUpdateListener {
        C08004() {
        }

        public void onAnimationUpdate(ValueAnimator animation) {
            LoadingView.this.mGetBiggerCircleRadius = ((Float) animation.getAnimatedValue()).floatValue();
        }
    }

    /* renamed from: me.wangyuwei.loadingview.LoadingView.1 */
    class C14961 implements Action1<Long> {
        C14961() {
        }

        public void call(Long aLong) {
            LoadingView.this.dealTimerBusiness();
        }
    }

    /* renamed from: me.wangyuwei.loadingview.LoadingView.2 */
    class C14972 implements Action1<Throwable> {
        C14972() {
        }

        public void call(Throwable throwable) {
        }
    }

    public LoadingView(Context context) {
        this(context, null);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.DEFAULT_DURATION = 15;
        this.DEFAULT_EXTERNAL_RADIUS = dp2px(82.0f);
        this.DEFAULT_INTERNAL_RADIUS = dp2px(8.0f);
        this.DEFAULT_RADIAN = 45;
        this.mPath = new Path();
        this.mAngle = 0;
        this.mCyclic = 0;
        this.mRadian = 45;
        this.MAX_INTERNAL_R = dp2px(18.0f);
        this.MIN_INTERNAL_R = dp2px(2.0f);
        this.MAX_EXTERNAL_R = dp2px(150.0f);
        this.MIN_EXTERNAL_R = dp2px(25.0f);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        this.mAnimators = new ArrayList();
        this.mPoints = new ArrayList();
        this.mPaint = new Paint();
        this.mPaint.setAntiAlias(true);
        this.mPaint.setStyle(Style.FILL);
        TypedArray typeArray = getContext().obtainStyledAttributes(attrs, C0801R.styleable.BezierLoadingView);
        this.mDuration = typeArray.getInt(C0801R.styleable.BezierLoadingView_lv_duration, 15);
        this.mInternalR = typeArray.getDimension(C0801R.styleable.BezierLoadingView_lv_internal_radius, (float) this.DEFAULT_INTERNAL_RADIUS);
        this.mExternalR = typeArray.getDimension(C0801R.styleable.BezierLoadingView_lv_external_radius, (float) this.DEFAULT_EXTERNAL_RADIUS);
        int startColor = typeArray.getColor(C0801R.styleable.BezierLoadingView_lv_start_color, 999999);
        int endColor = typeArray.getColor(C0801R.styleable.BezierLoadingView_lv_end_color, 999999);
        List<Integer> colorList = new ArrayList();
        if (startColor != 999999) {
            colorList.add(Integer.valueOf(startColor));
        }
        if (endColor != 999999) {
            colorList.add(Integer.valueOf(endColor));
        }
        if (colorList.size() == MIN_DURATION) {
            colorList.add(colorList.get(0));
        }
        if (colorList.size() == 0) {
            this.mColors = new int[]{ContextCompat.getColor(getContext(), C0801R.color.loading_yellow), ContextCompat.getColor(getContext(), C0801R.color.loading_pink)};
        } else {
            this.mColors = new int[colorList.size()];
            for (int i = 0; i < colorList.size(); i += MIN_DURATION) {
                this.mColors[i] = ((Integer) colorList.get(i)).intValue();
            }
        }
        typeArray.recycle();
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mWidth = w;
        this.mHeight = h;
        setShader();
        resetPoint();
    }

    private void setShader() {
        this.mPaint.setShader(new LinearGradient(((float) (this.mWidth / 2)) - this.mExternalR, ((float) (this.mHeight / 2)) - this.mExternalR, ((float) (this.mWidth / 2)) - this.mExternalR, ((float) (this.mHeight / 2)) + this.mExternalR, this.mColors, null, TileMode.CLAMP));
    }

    public void start() {
        if (this.mTimer == null || this.mTimer.isUnsubscribed()) {
            this.mTimer = Observable.interval((long) this.mDuration, TimeUnit.MILLISECONDS).subscribe(new C14961(), new C14972());
        }
        setVisibility(0);
    }

    public void stop() {
        if (this.mTimer != null) {
            this.mTimer.unsubscribe();
        }
        setVisibility(8);
    }

    private void dealTimerBusiness() {
        setOffset(((float) (this.mAngle % this.mRadian)) / ((float) this.mRadian));
        this.mAngle += MIN_DURATION;
        if (this.mAngle == 360) {
            this.mAngle = 0;
            this.mCyclic += MIN_DURATION;
        }
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawCircle(canvas);
        drawBezier(canvas);
    }

    private void drawCircle(Canvas canvas) {
        for (int i = 0; i < this.mPoints.size(); i += MIN_DURATION) {
            int index = this.mAngle / this.mRadian;
            if (isEvenCyclic()) {
                if (i == index) {
                    if (this.mAngle % this.mRadian == 0) {
                        canvas.drawCircle(getCircleX(this.mAngle), getCircleY(this.mAngle), getMaxInternalRadius(), this.mPaint);
                    } else if (this.mAngle % this.mRadian > 0) {
                        canvas.drawCircle(getCircleX(this.mAngle), getCircleY(this.mAngle), this.mGetSmallerCircleRadius < this.mInternalR ? this.mInternalR : this.mGetSmallerCircleRadius, this.mPaint);
                    }
                } else if (i == index + MIN_DURATION) {
                    if (this.mAngle % this.mRadian == 0) {
                        canvas.drawCircle(((PointF) this.mPoints.get(i)).x, ((PointF) this.mPoints.get(i)).y, this.mInternalR, this.mPaint);
                    } else {
                        canvas.drawCircle(((PointF) this.mPoints.get(i)).x, ((PointF) this.mPoints.get(i)).y, this.mGetBiggerCircleRadius < this.mInternalR ? this.mInternalR : this.mGetBiggerCircleRadius, this.mPaint);
                    }
                } else if (i > index + MIN_DURATION) {
                    canvas.drawCircle(((PointF) this.mPoints.get(i)).x, ((PointF) this.mPoints.get(i)).y, this.mInternalR, this.mPaint);
                }
            } else if (i < index) {
                canvas.drawCircle(((PointF) this.mPoints.get(i + MIN_DURATION)).x, ((PointF) this.mPoints.get(i + MIN_DURATION)).y, this.mInternalR, this.mPaint);
            } else if (i == index) {
                if (this.mAngle % this.mRadian == 0) {
                    canvas.drawCircle(getCircleX(this.mAngle), getCircleY(this.mAngle), getMaxInternalRadius(), this.mPaint);
                } else {
                    canvas.drawCircle(getCircleX(this.mAngle), getCircleY(this.mAngle), this.mGetSmallerCircleRadius < this.mInternalR ? this.mInternalR : this.mGetSmallerCircleRadius, this.mPaint);
                }
            } else if (i == index + MIN_DURATION) {
                if (this.mAngle % this.mRadian == 0) {
                    canvas.drawCircle(getCircleX(this.mAngle), getCircleY(this.mAngle), getMinInternalRadius(), this.mPaint);
                } else if (this.mAngle % this.mRadian > 0) {
                    canvas.drawCircle(getCircleX(this.mAngle), getCircleY(this.mAngle), this.mGetBiggerCircleRadius < this.mInternalR ? this.mInternalR : this.mGetBiggerCircleRadius, this.mPaint);
                }
            }
        }
    }

    private void drawBezier(Canvas canvas) {
        int size;
        float leftX;
        float leftY;
        this.mPath.reset();
        int circleIndex = this.mAngle / this.mRadian;
        float rightX = getCircleX(this.mAngle);
        float rightY = getCircleY(this.mAngle);
        int index;
        List list;
        if (isEvenCyclic()) {
            index = circleIndex + MIN_DURATION;
            list = this.mPoints;
            if (index >= this.mPoints.size()) {
                size = this.mPoints.size() - 1;
            } else {
                size = index;
            }
            leftX = ((PointF) list.get(size)).x;
            list = this.mPoints;
            if (index >= this.mPoints.size()) {
                size = this.mPoints.size() - 1;
            } else {
                size = index;
            }
            leftY = ((PointF) list.get(size)).y;
        } else {
            index = circleIndex;
            list = this.mPoints;
            if (index < 0) {
                size = 0;
            } else {
                size = index;
            }
            leftX = ((PointF) list.get(size)).x;
            list = this.mPoints;
            if (index < 0) {
                size = 0;
            } else {
                size = index;
            }
            leftY = ((PointF) list.get(size)).y;
        }
        double theta = getTheta(new PointF(leftX, leftY), new PointF(rightX, rightY));
        float sinTheta = (float) Math.sin(theta);
        float cosTheta = (float) Math.cos(theta);
        PointF pointF1 = new PointF(leftX - (this.mInternalR * sinTheta), (this.mInternalR * cosTheta) + leftY);
        PointF pointF2 = new PointF(rightX - (this.mInternalR * sinTheta), (this.mInternalR * cosTheta) + rightY);
        PointF pointF3 = new PointF((this.mInternalR * sinTheta) + rightX, rightY - (this.mInternalR * cosTheta));
        PointF pointF4 = new PointF((this.mInternalR * sinTheta) + leftX, leftY - (this.mInternalR * cosTheta));
        Path path;
        float f;
        float f2;
        if (isEvenCyclic()) {
            if (this.mAngle % this.mRadian < this.mRadian / 2) {
                this.mPath.moveTo(pointF3.x, pointF3.y);
                path = this.mPath;
                f = (leftX - rightX) / ((float) (this.mRadian / 2));
                if (this.mAngle % this.mRadian > this.mRadian / 2) {
                    size = this.mRadian / 2;
                } else {
                    size = this.mAngle % this.mRadian;
                }
                f = rightX + (((float) size) * f);
                f2 = (leftY - rightY) / ((float) (this.mRadian / 2));
                if (this.mAngle % this.mRadian > this.mRadian / 2) {
                    size = this.mRadian / 2;
                } else {
                    size = this.mAngle % this.mRadian;
                }
                path.quadTo(f, (((float) size) * f2) + rightY, pointF2.x, pointF2.y);
                this.mPath.lineTo(pointF3.x, pointF3.y);
                this.mPath.moveTo(pointF4.x, pointF4.y);
                path = this.mPath;
                f = (rightX - leftX) / ((float) (this.mRadian / 2));
                if (this.mAngle % this.mRadian > this.mRadian / 2) {
                    size = this.mRadian / 2;
                } else {
                    size = this.mAngle % this.mRadian;
                }
                f = leftX + (((float) size) * f);
                f2 = (rightY - leftY) / ((float) (this.mRadian / 2));
                if (this.mAngle % this.mRadian > this.mRadian / 2) {
                    size = this.mRadian / 2;
                } else {
                    size = this.mAngle % this.mRadian;
                }
                path.quadTo(f, (((float) size) * f2) + leftY, pointF1.x, pointF1.y);
                this.mPath.lineTo(pointF4.x, pointF4.y);
                this.mPath.close();
                canvas.drawPath(this.mPath, this.mPaint);
                return;
            }
        } else if (circleIndex > 0) {
            if (this.mAngle % this.mRadian > this.mRadian / 2) {
                this.mPath.moveTo(pointF3.x, pointF3.y);
                path = this.mPath;
                f = (leftX - rightX) / ((float) (this.mRadian / 2));
                if (this.mRadian - (this.mAngle % this.mRadian) > this.mRadian / 2) {
                    size = this.mRadian / 2;
                } else {
                    size = this.mRadian - (this.mAngle % this.mRadian);
                }
                f = rightX + (((float) size) * f);
                f2 = (leftY - rightY) / ((float) (this.mRadian / 2));
                if (this.mRadian - (this.mAngle % this.mRadian) > this.mRadian / 2) {
                    size = this.mRadian / 2;
                } else {
                    size = this.mRadian - (this.mAngle % this.mRadian);
                }
                path.quadTo(f, (((float) size) * f2) + rightY, pointF2.x, pointF2.y);
                this.mPath.lineTo(pointF3.x, pointF3.y);
                this.mPath.moveTo(pointF4.x, pointF4.y);
                path = this.mPath;
                f = (rightX - leftX) / ((float) (this.mRadian / 2));
                if (this.mRadian - (this.mAngle % this.mRadian) > this.mRadian / 2) {
                    size = this.mRadian / 2;
                } else {
                    size = this.mRadian - (this.mAngle % this.mRadian);
                }
                f = leftX + (((float) size) * f);
                f2 = (rightY - leftY) / ((float) (this.mRadian / 2));
                if (this.mRadian - (this.mAngle % this.mRadian) > this.mRadian / 2) {
                    size = this.mRadian / 2;
                } else {
                    size = this.mRadian - (this.mAngle % this.mRadian);
                }
                path.quadTo(f, (((float) size) * f2) + leftY, pointF1.x, pointF1.y);
                this.mPath.lineTo(pointF4.x, pointF4.y);
                this.mPath.close();
                canvas.drawPath(this.mPath, this.mPaint);
                return;
            }
        }
        if (circleIndex != 0 || isEvenCyclic()) {
            this.mPath.moveTo(pointF1.x, pointF1.y);
            this.mPath.quadTo((leftX + rightX) / 2.0f, (leftY + rightY) / 2.0f, pointF2.x, pointF2.y);
            this.mPath.lineTo(pointF3.x, pointF3.y);
            this.mPath.quadTo((leftX + rightX) / 2.0f, (leftY + rightY) / 2.0f, pointF4.x, pointF4.y);
            this.mPath.lineTo(pointF1.x, pointF1.y);
            this.mPath.close();
            canvas.drawPath(this.mPath, this.mPaint);
        }
    }

    private void createAnimator() {
        if (!this.mPoints.isEmpty()) {
            this.mAnimators.clear();
            ValueAnimator circleGetSmallerAnimator = ValueAnimator.ofFloat(new float[]{getMaxInternalRadius(), getMinInternalRadius()});
            circleGetSmallerAnimator.setDuration(5000);
            circleGetSmallerAnimator.addUpdateListener(new C07993());
            this.mAnimators.add(circleGetSmallerAnimator);
            ValueAnimator circleGetBiggerAnimator = ValueAnimator.ofFloat(new float[]{getMinInternalRadius(), getMaxInternalRadius()});
            circleGetBiggerAnimator.setDuration(5000);
            circleGetBiggerAnimator.addUpdateListener(new C08004());
            this.mAnimators.add(circleGetBiggerAnimator);
        }
    }

    private void seekAnimator(float offset) {
        for (ValueAnimator animator : this.mAnimators) {
            animator.setCurrentPlayTime((long) (5000.0f * offset));
        }
    }

    public void setOffset(float offSet) {
        createAnimator();
        seekAnimator(offSet);
        postInvalidate();
    }

    private void resetPoint() {
        this.x0 = (float) (this.mWidth / 2);
        this.y0 = (float) (this.mHeight / 2);
        createPoints();
        if (!this.mPoints.isEmpty()) {
            this.mGetBiggerCircleRadius = getMaxInternalRadius();
            this.mGetSmallerCircleRadius = getMinInternalRadius();
            postInvalidate();
        }
    }

    private void createPoints() {
        this.mPoints.clear();
        for (int i = 0; i <= 360; i += MIN_DURATION) {
            if (i % this.mRadian == 0) {
                this.mPoints.add(new PointF(getCircleX(i), getCircleY(i)));
            }
        }
    }

    private boolean isEvenCyclic() {
        return this.mCyclic % 2 == 0;
    }

    private float getCircleY(int angle) {
        return this.y0 + (this.mExternalR * ((float) Math.sin((((double) angle) * 3.14d) / 180.0d)));
    }

    private float getCircleX(int angle) {
        return this.x0 + (this.mExternalR * ((float) Math.cos((((double) angle) * 3.14d) / 180.0d)));
    }

    private double getTheta(PointF pointCenterLeft, PointF pointCenterRight) {
        return Math.atan((double) ((pointCenterRight.y - pointCenterLeft.y) / (pointCenterRight.x - pointCenterLeft.x)));
    }

    public void setExternalRadius(int progress) {
        int R = (int) ((((float) progress) / 100.0f) * ((float) this.MAX_EXTERNAL_R));
        this.mExternalR = R < this.MIN_EXTERNAL_R ? (float) this.MIN_EXTERNAL_R : (float) R;
        setShader();
        createPoints();
    }

    public void setInternalRadius(int progress) {
        int r = (int) ((((float) progress) / 100.0f) * ((float) this.MAX_INTERNAL_R));
        this.mInternalR = r < this.MIN_INTERNAL_R ? (float) this.MIN_INTERNAL_R : (float) r;
    }

    public void setDuration(int progress) {
        stop();
        int duration = (int) ((1.0f - (((float) progress) / 100.0f)) * 120.0f);
        if (duration < MIN_DURATION) {
            duration = MIN_DURATION;
        }
        this.mDuration = duration;
        start();
    }

    private float getMaxInternalRadius() {
        return (this.mInternalR / 10.0f) * 14.0f;
    }

    private float getMinInternalRadius() {
        return this.mInternalR / 10.0f;
    }

    private int dp2px(float dp) {
        return (int) ((dp * getContext().getResources().getDisplayMetrics().density) + 0.5f);
    }
}
