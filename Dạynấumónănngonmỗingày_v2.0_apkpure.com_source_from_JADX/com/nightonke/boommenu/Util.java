package com.nightonke.boommenu;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build.VERSION;
import android.util.Log;
import android.util.StateSet;
import android.view.View;
import java.util.ArrayList;
import java.util.Random;

public class Util {
    private static final int[] colors;
    private static Util ourInstance;
    private static final ArrayList<Integer> usedColor;

    static Activity scanForActivity(Context context) {
        if (context == null) {
            Log.w("BoomMenuButton", "scanForActivity: context is null!");
            return null;
        } else if (context instanceof Activity) {
            return (Activity) context;
        } else {
            if (context instanceof ContextWrapper) {
                return scanForActivity(((ContextWrapper) context).getBaseContext());
            }
            Log.w("BoomMenuButton", "scanForActivity: context is null!");
            return null;
        }
    }

    static void setVisibility(int visibility, View... views) {
        for (View view : views) {
            if (view != null) {
                view.setVisibility(visibility);
            }
        }
    }

    public static int dp2px(float dp) {
        return Math.round(dp * (((float) Resources.getSystem().getDisplayMetrics().densityDpi) / 160.0f));
    }

    public static int getColor(View view, int id, Theme theme) {
        if (VERSION.SDK_INT >= 23) {
            return view.getResources().getColor(id, theme);
        }
        return view.getResources().getColor(id);
    }

    public static int getColor(TypedArray typedArray, int id, Theme theme) {
        if (VERSION.SDK_INT >= 23) {
            return typedArray.getResources().getColor(id, theme);
        }
        return typedArray.getResources().getColor(id);
    }

    public static int getColor(View view, int id) {
        return getColor(view, id, null);
    }

    public static int getColor(TypedArray typedArray, int id) {
        return getColor(typedArray, id, null);
    }

    public static Drawable getSystemDrawable(Context context, int id) {
        TypedArray ta = context.obtainStyledAttributes(new int[]{id});
        Drawable drawable = ta.getDrawable(0);
        ta.recycle();
        return drawable;
    }

    public static Drawable getDrawable(View view, int id, Theme theme) {
        if (VERSION.SDK_INT >= 21) {
            return view.getResources().getDrawable(id, theme);
        }
        return view.getResources().getDrawable(id);
    }

    public static Drawable getDrawable(View view, int id) {
        if (VERSION.SDK_INT >= 21) {
            return view.getResources().getDrawable(id, null);
        }
        return view.getResources().getDrawable(id);
    }

    public static GradientDrawable getOvalDrawable(View view, int color) {
        GradientDrawable gradientDrawable = (GradientDrawable) getDrawable(view, C0763R.drawable.shape_oval_normal);
        gradientDrawable.setColor(color);
        return gradientDrawable;
    }

    public static BitmapDrawable getOvalBitmapDrawable(View view, int radius, int color) {
        Bitmap bitmap = Bitmap.createBitmap(radius * 2, radius * 2, Config.ARGB_8888);
        Canvas canvasPressed = new Canvas(bitmap);
        Paint paintPressed = new Paint();
        paintPressed.setAntiAlias(true);
        paintPressed.setColor(color);
        canvasPressed.drawCircle((float) radius, (float) radius, (float) radius, paintPressed);
        return new BitmapDrawable(view.getResources(), bitmap);
    }

    public static GradientDrawable getRectangleDrawable(View view, int cornerRadius, int color) {
        GradientDrawable gradientDrawable = (GradientDrawable) getDrawable(view, C0763R.drawable.shape_rectangle_normal);
        gradientDrawable.setCornerRadius((float) cornerRadius);
        gradientDrawable.setColor(color);
        return gradientDrawable;
    }

    public static BitmapDrawable getRectangleBitmapDrawable(View view, int width, int height, int cornerRadius, int color) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvasPressed = new Canvas(bitmap);
        Paint paintPressed = new Paint();
        paintPressed.setAntiAlias(true);
        paintPressed.setColor(color);
        canvasPressed.drawRoundRect(new RectF(0.0f, 0.0f, (float) width, (float) height), (float) cornerRadius, (float) cornerRadius, paintPressed);
        return new BitmapDrawable(view.getResources(), bitmap);
    }

    public static float distance(Point a, Point b) {
        return (float) Math.sqrt((double) (((a.x - b.x) * (a.x - b.x)) + ((a.y - b.y) * (a.y - b.y))));
    }

    public static StateListDrawable getOvalStateListBitmapDrawable(View view, int radius, int normalColor, int highlightedColor, int unableColor) {
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{16842919}, getOvalBitmapDrawable(view, radius, highlightedColor));
        stateListDrawable.addState(new int[]{-16842910}, getOvalBitmapDrawable(view, radius, unableColor));
        stateListDrawable.addState(StateSet.WILD_CARD, getOvalBitmapDrawable(view, radius, normalColor));
        return stateListDrawable;
    }

    public static StateListDrawable getOvalStateListGradientDrawable(View view, int normalColor, int highlightedColor, int unableColor) {
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{16842919}, getOvalDrawable(view, highlightedColor));
        stateListDrawable.addState(new int[]{-16842910}, getOvalDrawable(view, unableColor));
        stateListDrawable.addState(StateSet.WILD_CARD, getOvalDrawable(view, normalColor));
        return stateListDrawable;
    }

    public static StateListDrawable getRectangleStateListBitmapDrawable(View view, int width, int height, int cornerRadius, int normalColor, int highlightedColor, int unableColor) {
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{16842919}, getRectangleBitmapDrawable(view, width, height, cornerRadius, highlightedColor));
        stateListDrawable.addState(new int[]{-16842910}, getRectangleBitmapDrawable(view, width, height, cornerRadius, unableColor));
        stateListDrawable.addState(StateSet.WILD_CARD, getRectangleBitmapDrawable(view, width, height, cornerRadius, normalColor));
        return stateListDrawable;
    }

    public static StateListDrawable getRectangleStateListGradientDrawable(View view, int cornerRadius, int normalColor, int highlightedColor, int unableColor) {
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{16842919}, getRectangleDrawable(view, cornerRadius, highlightedColor));
        stateListDrawable.addState(new int[]{-16842910}, getRectangleDrawable(view, cornerRadius, unableColor));
        stateListDrawable.addState(StateSet.WILD_CARD, getRectangleDrawable(view, cornerRadius, normalColor));
        return stateListDrawable;
    }

    public static int getInt(TypedArray typedArray, int id, int defaultId) {
        return typedArray.getInt(id, typedArray.getResources().getInteger(defaultId));
    }

    public static boolean getBoolean(TypedArray typedArray, int id, int defaultId) {
        return typedArray.getBoolean(id, typedArray.getResources().getBoolean(defaultId));
    }

    public static int getDimenSize(TypedArray typedArray, int id, int defaultId) {
        return typedArray.getDimensionPixelSize(id, typedArray.getResources().getDimensionPixelSize(defaultId));
    }

    public static int getDimenOffset(TypedArray typedArray, int id, int defaultId) {
        return typedArray.getDimensionPixelOffset(id, typedArray.getResources().getDimensionPixelOffset(defaultId));
    }

    public static int getColor(TypedArray typedArray, int id, int defaultId) {
        return typedArray.getColor(id, getColor(typedArray, defaultId));
    }

    public static int getColor(Context context, int id) {
        if (VERSION.SDK_INT >= 23) {
            return context.getResources().getColor(id, null);
        }
        return context.getResources().getColor(id);
    }

    public static int getColor(Context context, Integer id, int color) {
        return id == null ? color : getColor(context, id.intValue());
    }

    public static void setDrawable(View view, Drawable drawable) {
        if (VERSION.SDK_INT >= 16) {
            view.setBackground(drawable);
        } else {
            view.setBackgroundDrawable(drawable);
        }
    }

    public static int getDarkerColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] = hsv[2] * 0.9f;
        return Color.HSVToColor(hsv);
    }

    public static int getLighterColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] = hsv[2] * 1.1f;
        return Color.HSVToColor(hsv);
    }

    static {
        colors = new int[]{Color.parseColor("#F44336"), Color.parseColor("#E91E63"), Color.parseColor("#9C27B0"), Color.parseColor("#673AB7"), Color.parseColor("#3F51B5"), Color.parseColor("#2196F3"), Color.parseColor("#03A9F4"), Color.parseColor("#00BCD4"), Color.parseColor("#009688"), Color.parseColor("#4CAF50"), Color.parseColor("#009688"), Color.parseColor("#CDDC39"), Color.parseColor("#FFEB3B"), Color.parseColor("#FF9800"), Color.parseColor("#FF5722"), Color.parseColor("#795548"), Color.parseColor("#9E9E9E"), Color.parseColor("#607D8B")};
        usedColor = new ArrayList();
        ourInstance = new Util();
    }

    public static int getColor() {
        int colorIndex;
        Random random = new Random();
        do {
            colorIndex = random.nextInt(colors.length);
        } while (usedColor.contains(Integer.valueOf(colorIndex)));
        usedColor.add(Integer.valueOf(colorIndex));
        while (usedColor.size() > 6) {
            usedColor.remove(0);
        }
        return colors[colorIndex];
    }

    public static boolean pointInView(PointF point, View view) {
        return ((float) view.getLeft()) <= point.x && point.x <= ((float) view.getRight()) && ((float) view.getTop()) <= point.y && point.y <= ((float) view.getBottom());
    }

    public static Util getInstance() {
        return ourInstance;
    }

    private Util() {
    }
}
