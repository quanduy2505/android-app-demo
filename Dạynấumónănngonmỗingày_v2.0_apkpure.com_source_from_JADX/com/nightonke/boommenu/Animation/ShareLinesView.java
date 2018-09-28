package com.nightonke.boommenu.Animation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.view.View;
import android.widget.FrameLayout.LayoutParams;
import com.nightonke.boommenu.Util;
import java.util.ArrayList;
import java.util.Iterator;

public class ShareLinesView extends View {
    private long animationHideDelay1;
    private long animationHideDelay2;
    private long animationHideDelay3;
    private long animationHideDuration1;
    private long animationHideDuration2;
    private long animationHideTotalDuration;
    private long animationShowDelay1;
    private long animationShowDelay2;
    private long animationShowDelay3;
    private long animationShowDuration1;
    private long animationShowDuration2;
    private long animationShowTotalDuration;
    private int line1Color;
    private int line2Color;
    private Paint linePaint;
    private int lineWidth;
    private ArrayList<PointF> piecePositions;
    private float processForLine1;
    private float processForLine2;

    public ShareLinesView(Context context) {
        super(context);
        this.processForLine1 = 1.0f;
        this.processForLine2 = 1.0f;
        this.line1Color = -1;
        this.line2Color = -1;
        this.lineWidth = 3;
        this.linePaint = new Paint();
        this.linePaint.setAntiAlias(true);
    }

    public void setData(ArrayList<Point> piecePositions, int dotRadius, long showDuration, long showDelay, long hideDuration, long hideDelay) {
        float xOffset = ((float) dotRadius) - (((float) this.lineWidth) / 4.0f);
        float yOffset = ((float) (((double) dotRadius) - ((((double) this.lineWidth) * Math.sqrt(3.0d)) / 4.0d))) + ((float) Util.dp2px(0.25f));
        this.piecePositions = new ArrayList();
        Iterator it = piecePositions.iterator();
        while (it.hasNext()) {
            Point piecePosition = (Point) it.next();
            boolean existed = false;
            Iterator it2 = this.piecePositions.iterator();
            while (it2.hasNext()) {
                if (((PointF) it2.next()).equals((float) piecePosition.x, (float) piecePosition.y)) {
                    existed = true;
                    break;
                }
            }
            if (!existed) {
                this.piecePositions.add(new PointF(piecePosition));
            }
        }
        it = this.piecePositions.iterator();
        while (it.hasNext()) {
            ((PointF) it.next()).offset(xOffset, yOffset);
        }
        int[] pieceNumbers = new int[3];
        int pieceNumber = piecePositions.size();
        for (int i = 0; i < pieceNumber; i++) {
            int i2 = i % 3;
            pieceNumbers[i2] = pieceNumbers[i2] + 1;
        }
        this.animationShowDelay1 = ((long) (pieceNumbers[0] - 1)) * showDelay;
        this.animationShowDuration1 = ((long) pieceNumbers[0]) * showDelay;
        this.animationShowDelay2 = ((long) ((pieceNumbers[0] - 1) + pieceNumbers[1])) * showDelay;
        this.animationShowDuration2 = ((long) (pieceNumbers[0] + pieceNumbers[1])) * showDelay;
        this.animationShowDelay3 = (((long) (((pieceNumbers[2] - 1) + pieceNumbers[1]) + pieceNumbers[0])) * showDelay) + showDuration;
        this.animationShowTotalDuration = this.animationShowDelay3;
        this.animationHideDelay1 = (((long) (pieceNumbers[2] - 1)) * hideDelay) + hideDuration;
        this.animationHideDuration1 = (((long) pieceNumbers[2]) * hideDelay) + hideDuration;
        this.animationHideDelay2 = (((long) ((pieceNumbers[2] - 1) + pieceNumbers[1])) * hideDelay) + hideDuration;
        this.animationHideDuration2 = (((long) (pieceNumbers[2] + pieceNumbers[1])) * hideDelay) + hideDuration;
        this.animationHideDelay3 = (((long) (((pieceNumbers[2] - 1) + pieceNumbers[1]) + pieceNumbers[0])) * hideDelay) + hideDuration;
        this.animationHideTotalDuration = this.animationHideDelay3;
    }

    private void setShowProcess(float process) {
        long current = (long) (((float) this.animationShowTotalDuration) * process);
        if (this.animationShowDelay1 < current && current <= this.animationShowDuration1) {
            this.processForLine1 = ((float) (this.animationShowDuration1 - current)) / ((float) (this.animationShowDuration1 - this.animationShowDelay1));
        } else if (this.animationShowDuration1 < current && current <= this.animationShowDelay2) {
            this.processForLine1 = 0.0f;
        } else if (this.animationShowDelay2 < current && current <= this.animationShowDuration2) {
            this.processForLine2 = ((float) (this.animationShowDuration2 - current)) / ((float) (this.animationShowDuration2 - this.animationShowDelay2));
        } else if (this.animationShowDuration2 <= current) {
            this.processForLine1 = 0.0f;
            this.processForLine2 = 0.0f;
        }
        invalidate();
    }

    private void setHideProcess(float process) {
        long current = (long) (((float) this.animationHideTotalDuration) * process);
        if (this.animationHideDelay1 < current && current <= this.animationHideDuration1) {
            this.processForLine2 = 1.0f - (((float) (this.animationHideDuration1 - current)) / ((float) (this.animationHideDuration1 - this.animationHideDelay1)));
        } else if (this.animationHideDuration1 < current && current <= this.animationHideDelay2) {
            this.processForLine2 = 1.0f;
        } else if (this.animationHideDelay2 < current && current <= this.animationHideDuration2) {
            this.processForLine1 = 1.0f - (((float) (this.animationHideDuration2 - current)) / ((float) (this.animationHideDuration2 - this.animationHideDelay2)));
        } else if (this.animationHideDuration2 <= current) {
            this.processForLine1 = 1.0f;
            this.processForLine2 = 1.0f;
        }
        invalidate();
    }

    public void setLine1Color(int line1Color) {
        this.line1Color = line1Color;
        this.linePaint.setColor(line1Color);
    }

    public void setLine2Color(int line2Color) {
        this.line2Color = line2Color;
    }

    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
        this.linePaint.setStrokeWidth((float) lineWidth);
    }

    protected void onDraw(Canvas canvas) {
        Canvas canvas2 = canvas;
        canvas2.drawLine(((PointF) this.piecePositions.get(1)).x, ((PointF) this.piecePositions.get(1)).y, (this.processForLine1 * (((PointF) this.piecePositions.get(0)).x - ((PointF) this.piecePositions.get(1)).x)) + ((PointF) this.piecePositions.get(1)).x, (this.processForLine1 * (((PointF) this.piecePositions.get(0)).y - ((PointF) this.piecePositions.get(1)).y)) + ((PointF) this.piecePositions.get(1)).y, this.linePaint);
        this.linePaint.setColor(this.line2Color);
        canvas2 = canvas;
        canvas2.drawLine(((PointF) this.piecePositions.get(2)).x, ((PointF) this.piecePositions.get(2)).y, (this.processForLine2 * (((PointF) this.piecePositions.get(1)).x - ((PointF) this.piecePositions.get(2)).x)) + ((PointF) this.piecePositions.get(2)).x, (this.processForLine2 * (((PointF) this.piecePositions.get(1)).y - ((PointF) this.piecePositions.get(2)).y)) + ((PointF) this.piecePositions.get(2)).y, this.linePaint);
        super.onDraw(canvas);
    }

    public void place(int left, int top, int width, int height) {
        LayoutParams layoutParams = (LayoutParams) getLayoutParams();
        if (layoutParams != null) {
            layoutParams.leftMargin = left;
            layoutParams.topMargin = top;
            layoutParams.width = width;
            layoutParams.height = height;
            setLayoutParams(layoutParams);
        }
    }
}
