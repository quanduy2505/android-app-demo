package com.davemorrissey.labs.subscaleview.decoder;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory.Options;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.text.TextUtils;
import java.util.List;

public class SkiaImageRegionDecoder implements ImageRegionDecoder {
    private static final String ASSET_PREFIX = "file:///android_asset/";
    private static final String FILE_PREFIX = "file://";
    private static final String RESOURCE_PREFIX = "android.resource://";
    private BitmapRegionDecoder decoder;
    private final Object decoderLock;

    public SkiaImageRegionDecoder() {
        this.decoderLock = new Object();
    }

    public Point init(Context context, Uri uri) throws Exception {
        String uriString = uri.toString();
        if (uriString.startsWith(RESOURCE_PREFIX)) {
            Resources res;
            String packageName = uri.getAuthority();
            if (context.getPackageName().equals(packageName)) {
                res = context.getResources();
            } else {
                res = context.getPackageManager().getResourcesForApplication(packageName);
            }
            int id = 0;
            List<String> segments = uri.getPathSegments();
            int size = segments.size();
            if (size == 2 && ((String) segments.get(0)).equals("drawable")) {
                id = res.getIdentifier((String) segments.get(1), "drawable", packageName);
            } else if (size == 1 && TextUtils.isDigitsOnly((CharSequence) segments.get(0))) {
                try {
                    id = Integer.parseInt((String) segments.get(0));
                } catch (NumberFormatException e) {
                }
            }
            this.decoder = BitmapRegionDecoder.newInstance(context.getResources().openRawResource(id), false);
        } else if (uriString.startsWith(ASSET_PREFIX)) {
            this.decoder = BitmapRegionDecoder.newInstance(context.getAssets().open(uriString.substring(ASSET_PREFIX.length()), 1), false);
        } else if (uriString.startsWith(FILE_PREFIX)) {
            this.decoder = BitmapRegionDecoder.newInstance(uriString.substring(FILE_PREFIX.length()), false);
        } else {
            this.decoder = BitmapRegionDecoder.newInstance(context.getContentResolver().openInputStream(uri), false);
        }
        return new Point(this.decoder.getWidth(), this.decoder.getHeight());
    }

    public Bitmap decodeRegion(Rect sRect, int sampleSize) {
        Bitmap bitmap;
        synchronized (this.decoderLock) {
            Options options = new Options();
            options.inSampleSize = sampleSize;
            options.inPreferredConfig = Config.RGB_565;
            bitmap = this.decoder.decodeRegion(sRect, options);
            if (bitmap == null) {
                throw new RuntimeException("Skia image decoder returned null bitmap - image format may not be supported");
            }
        }
        return bitmap;
    }

    public boolean isReady() {
        return (this.decoder == null || this.decoder.isRecycled()) ? false : true;
    }

    public void recycle() {
        this.decoder.recycle();
    }
}
