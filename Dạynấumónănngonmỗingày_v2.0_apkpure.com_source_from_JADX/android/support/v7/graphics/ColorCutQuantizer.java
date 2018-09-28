package android.support.v7.graphics;

import android.graphics.Color;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v7.graphics.Palette.Filter;
import android.support.v7.graphics.Palette.Swatch;
import android.util.TimingLogger;
import com.bumptech.glide.request.target.Target;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

final class ColorCutQuantizer {
    private static final int COMPONENT_BLUE = -1;
    private static final int COMPONENT_GREEN = -2;
    private static final int COMPONENT_RED = -3;
    private static final String LOG_TAG = "ColorCutQuantizer";
    private static final boolean LOG_TIMINGS = false;
    private static final int QUANTIZE_WORD_MASK = 31;
    private static final int QUANTIZE_WORD_WIDTH = 5;
    private static final Comparator<Vbox> VBOX_COMPARATOR_VOLUME;
    final int[] mColors;
    final Filter[] mFilters;
    final int[] mHistogram;
    final List<Swatch> mQuantizedColors;
    private final float[] mTempHsl;
    final TimingLogger mTimingLogger;

    /* renamed from: android.support.v7.graphics.ColorCutQuantizer.1 */
    static class C01831 implements Comparator<Vbox> {
        C01831() {
        }

        public int compare(Vbox lhs, Vbox rhs) {
            return rhs.getVolume() - lhs.getVolume();
        }
    }

    private class Vbox {
        private int mLowerIndex;
        private int mMaxBlue;
        private int mMaxGreen;
        private int mMaxRed;
        private int mMinBlue;
        private int mMinGreen;
        private int mMinRed;
        private int mPopulation;
        private int mUpperIndex;

        Vbox(int lowerIndex, int upperIndex) {
            this.mLowerIndex = lowerIndex;
            this.mUpperIndex = upperIndex;
            fitBox();
        }

        final int getVolume() {
            return (((this.mMaxRed - this.mMinRed) + 1) * ((this.mMaxGreen - this.mMinGreen) + 1)) * ((this.mMaxBlue - this.mMinBlue) + 1);
        }

        final boolean canSplit() {
            return getColorCount() > 1 ? true : ColorCutQuantizer.LOG_TIMINGS;
        }

        final int getColorCount() {
            return (this.mUpperIndex + 1) - this.mLowerIndex;
        }

        final void fitBox() {
            int[] colors = ColorCutQuantizer.this.mColors;
            int[] hist = ColorCutQuantizer.this.mHistogram;
            int minBlue = UrlImageViewHelper.CACHE_DURATION_INFINITE;
            int minGreen = UrlImageViewHelper.CACHE_DURATION_INFINITE;
            int minRed = UrlImageViewHelper.CACHE_DURATION_INFINITE;
            int maxBlue = Target.SIZE_ORIGINAL;
            int maxGreen = Target.SIZE_ORIGINAL;
            int maxRed = Target.SIZE_ORIGINAL;
            int count = 0;
            for (int i = this.mLowerIndex; i <= this.mUpperIndex; i++) {
                int color = colors[i];
                count += hist[color];
                int r = ColorCutQuantizer.quantizedRed(color);
                int g = ColorCutQuantizer.quantizedGreen(color);
                int b = ColorCutQuantizer.quantizedBlue(color);
                if (r > maxRed) {
                    maxRed = r;
                }
                if (r < minRed) {
                    minRed = r;
                }
                if (g > maxGreen) {
                    maxGreen = g;
                }
                if (g < minGreen) {
                    minGreen = g;
                }
                if (b > maxBlue) {
                    maxBlue = b;
                }
                if (b < minBlue) {
                    minBlue = b;
                }
            }
            this.mMinRed = minRed;
            this.mMaxRed = maxRed;
            this.mMinGreen = minGreen;
            this.mMaxGreen = maxGreen;
            this.mMinBlue = minBlue;
            this.mMaxBlue = maxBlue;
            this.mPopulation = count;
        }

        final Vbox splitBox() {
            if (canSplit()) {
                int splitPoint = findSplitPoint();
                Vbox newBox = new Vbox(splitPoint + 1, this.mUpperIndex);
                this.mUpperIndex = splitPoint;
                fitBox();
                return newBox;
            }
            throw new IllegalStateException("Can not split a box with only 1 color");
        }

        final int getLongestColorDimension() {
            int redLength = this.mMaxRed - this.mMinRed;
            int greenLength = this.mMaxGreen - this.mMinGreen;
            int blueLength = this.mMaxBlue - this.mMinBlue;
            if (redLength >= greenLength && redLength >= blueLength) {
                return ColorCutQuantizer.COMPONENT_RED;
            }
            if (greenLength < redLength || greenLength < blueLength) {
                return ColorCutQuantizer.COMPONENT_BLUE;
            }
            return ColorCutQuantizer.COMPONENT_GREEN;
        }

        final int findSplitPoint() {
            int longestDimension = getLongestColorDimension();
            int[] colors = ColorCutQuantizer.this.mColors;
            int[] hist = ColorCutQuantizer.this.mHistogram;
            ColorCutQuantizer.modifySignificantOctet(colors, longestDimension, this.mLowerIndex, this.mUpperIndex);
            Arrays.sort(colors, this.mLowerIndex, this.mUpperIndex + 1);
            ColorCutQuantizer.modifySignificantOctet(colors, longestDimension, this.mLowerIndex, this.mUpperIndex);
            int midPoint = this.mPopulation / 2;
            int count = 0;
            for (int i = this.mLowerIndex; i <= this.mUpperIndex; i++) {
                count += hist[colors[i]];
                if (count >= midPoint) {
                    return i;
                }
            }
            return this.mLowerIndex;
        }

        final Swatch getAverageColor() {
            int[] colors = ColorCutQuantizer.this.mColors;
            int[] hist = ColorCutQuantizer.this.mHistogram;
            int redSum = 0;
            int greenSum = 0;
            int blueSum = 0;
            int totalPopulation = 0;
            for (int i = this.mLowerIndex; i <= this.mUpperIndex; i++) {
                int color = colors[i];
                int colorPopulation = hist[color];
                totalPopulation += colorPopulation;
                redSum += ColorCutQuantizer.quantizedRed(color) * colorPopulation;
                greenSum += ColorCutQuantizer.quantizedGreen(color) * colorPopulation;
                blueSum += ColorCutQuantizer.quantizedBlue(color) * colorPopulation;
            }
            return new Swatch(ColorCutQuantizer.approximateToRgb888(Math.round(((float) redSum) / ((float) totalPopulation)), Math.round(((float) greenSum) / ((float) totalPopulation)), Math.round(((float) blueSum) / ((float) totalPopulation))), totalPopulation);
        }
    }

    ColorCutQuantizer(int[] pixels, int maxColors, Filter[] filters) {
        this.mTempHsl = new float[3];
        this.mTimingLogger = null;
        this.mFilters = filters;
        int[] hist = new int[AccessibilityNodeInfoCompat.ACTION_PASTE];
        this.mHistogram = hist;
        for (int i = 0; i < pixels.length; i++) {
            int quantizedColor = quantizeFromRgb888(pixels[i]);
            pixels[i] = quantizedColor;
            hist[quantizedColor] = hist[quantizedColor] + 1;
        }
        int distinctColorCount = 0;
        int color = 0;
        while (color < hist.length) {
            if (hist[color] > 0 && shouldIgnoreColor(color)) {
                hist[color] = 0;
            }
            if (hist[color] > 0) {
                distinctColorCount++;
            }
            color++;
        }
        int[] colors = new int[distinctColorCount];
        this.mColors = colors;
        int distinctColorIndex = 0;
        for (color = 0; color < hist.length; color++) {
            if (hist[color] > 0) {
                int distinctColorIndex2 = distinctColorIndex + 1;
                colors[distinctColorIndex] = color;
                distinctColorIndex = distinctColorIndex2;
            }
        }
        if (distinctColorCount <= maxColors) {
            this.mQuantizedColors = new ArrayList();
            for (int color2 : colors) {
                this.mQuantizedColors.add(new Swatch(approximateToRgb888(color2), hist[color2]));
            }
            return;
        }
        this.mQuantizedColors = quantizePixels(maxColors);
    }

    List<Swatch> getQuantizedColors() {
        return this.mQuantizedColors;
    }

    private List<Swatch> quantizePixels(int maxColors) {
        PriorityQueue<Vbox> pq = new PriorityQueue(maxColors, VBOX_COMPARATOR_VOLUME);
        pq.offer(new Vbox(0, this.mColors.length + COMPONENT_BLUE));
        splitBoxes(pq, maxColors);
        return generateAverageColors(pq);
    }

    private void splitBoxes(PriorityQueue<Vbox> queue, int maxSize) {
        while (queue.size() < maxSize) {
            Vbox vbox = (Vbox) queue.poll();
            if (vbox != null && vbox.canSplit()) {
                queue.offer(vbox.splitBox());
                queue.offer(vbox);
            } else {
                return;
            }
        }
    }

    private List<Swatch> generateAverageColors(Collection<Vbox> vboxes) {
        ArrayList<Swatch> colors = new ArrayList(vboxes.size());
        for (Vbox vbox : vboxes) {
            Swatch swatch = vbox.getAverageColor();
            if (!shouldIgnoreColor(swatch)) {
                colors.add(swatch);
            }
        }
        return colors;
    }

    private static void modifySignificantOctet(int[] a, int dimension, int lower, int upper) {
        int i;
        int color;
        switch (dimension) {
            case COMPONENT_GREEN /*-2*/:
                for (i = lower; i <= upper; i++) {
                    color = a[i];
                    a[i] = ((quantizedGreen(color) << 10) | (quantizedRed(color) << QUANTIZE_WORD_WIDTH)) | quantizedBlue(color);
                }
            case COMPONENT_BLUE /*-1*/:
                for (i = lower; i <= upper; i++) {
                    color = a[i];
                    a[i] = ((quantizedBlue(color) << 10) | (quantizedGreen(color) << QUANTIZE_WORD_WIDTH)) | quantizedRed(color);
                }
            default:
        }
    }

    private boolean shouldIgnoreColor(int color565) {
        int rgb = approximateToRgb888(color565);
        ColorUtils.colorToHSL(rgb, this.mTempHsl);
        return shouldIgnoreColor(rgb, this.mTempHsl);
    }

    private boolean shouldIgnoreColor(Swatch color) {
        return shouldIgnoreColor(color.getRgb(), color.getHsl());
    }

    private boolean shouldIgnoreColor(int rgb, float[] hsl) {
        if (this.mFilters != null && this.mFilters.length > 0) {
            for (Filter isAllowed : this.mFilters) {
                if (!isAllowed.isAllowed(rgb, hsl)) {
                    return true;
                }
            }
        }
        return LOG_TIMINGS;
    }

    static {
        VBOX_COMPARATOR_VOLUME = new C01831();
    }

    private static int quantizeFromRgb888(int color) {
        int r = modifyWordWidth(Color.red(color), 8, QUANTIZE_WORD_WIDTH);
        int g = modifyWordWidth(Color.green(color), 8, QUANTIZE_WORD_WIDTH);
        return ((r << 10) | (g << QUANTIZE_WORD_WIDTH)) | modifyWordWidth(Color.blue(color), 8, QUANTIZE_WORD_WIDTH);
    }

    private static int approximateToRgb888(int r, int g, int b) {
        return Color.rgb(modifyWordWidth(r, QUANTIZE_WORD_WIDTH, 8), modifyWordWidth(g, QUANTIZE_WORD_WIDTH, 8), modifyWordWidth(b, QUANTIZE_WORD_WIDTH, 8));
    }

    private static int approximateToRgb888(int color) {
        return approximateToRgb888(quantizedRed(color), quantizedGreen(color), quantizedBlue(color));
    }

    private static int quantizedRed(int color) {
        return (color >> 10) & QUANTIZE_WORD_MASK;
    }

    private static int quantizedGreen(int color) {
        return (color >> QUANTIZE_WORD_WIDTH) & QUANTIZE_WORD_MASK;
    }

    private static int quantizedBlue(int color) {
        return color & QUANTIZE_WORD_MASK;
    }

    private static int modifyWordWidth(int value, int currentWidth, int targetWidth) {
        int newValue;
        if (targetWidth > currentWidth) {
            newValue = (((1 << targetWidth) + COMPONENT_BLUE) * value) / ((1 << currentWidth) + COMPONENT_BLUE);
        } else {
            newValue = value >> (currentWidth - targetWidth);
        }
        return ((1 << targetWidth) + COMPONENT_BLUE) & newValue;
    }
}
