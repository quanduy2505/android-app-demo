package com.davemorrissey.labs.subscaleview;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import org.apache.http.protocol.HTTP;

public final class ImageSource {
    static final String ASSET_SCHEME = "file:///android_asset/";
    static final String FILE_SCHEME = "file:///";
    private final Bitmap bitmap;
    private boolean cached;
    private final Integer resource;
    private int sHeight;
    private Rect sRegion;
    private int sWidth;
    private boolean tile;
    private final Uri uri;

    private ImageSource(Bitmap bitmap, boolean cached) {
        this.bitmap = bitmap;
        this.uri = null;
        this.resource = null;
        this.tile = false;
        this.sWidth = bitmap.getWidth();
        this.sHeight = bitmap.getHeight();
        this.cached = cached;
    }

    private ImageSource(Uri uri) {
        String uriString = uri.toString();
        if (uriString.startsWith(FILE_SCHEME) && !new File(uriString.substring(FILE_SCHEME.length() - 1)).exists()) {
            try {
                uri = Uri.parse(URLDecoder.decode(uriString, HTTP.UTF_8));
            } catch (UnsupportedEncodingException e) {
            }
        }
        this.bitmap = null;
        this.uri = uri;
        this.resource = null;
        this.tile = true;
    }

    private ImageSource(int resource) {
        this.bitmap = null;
        this.uri = null;
        this.resource = Integer.valueOf(resource);
        this.tile = true;
    }

    public static ImageSource resource(int resId) {
        return new ImageSource(resId);
    }

    public static ImageSource asset(String assetName) {
        if (assetName != null) {
            return uri(ASSET_SCHEME + assetName);
        }
        throw new NullPointerException("Asset name must not be null");
    }

    public static ImageSource uri(String uri) {
        if (uri == null) {
            throw new NullPointerException("Uri must not be null");
        }
        if (!uri.contains("://")) {
            if (uri.startsWith("/")) {
                uri = uri.substring(1);
            }
            uri = FILE_SCHEME + uri;
        }
        return new ImageSource(Uri.parse(uri));
    }

    public static ImageSource uri(Uri uri) {
        if (uri != null) {
            return new ImageSource(uri);
        }
        throw new NullPointerException("Uri must not be null");
    }

    public static ImageSource bitmap(Bitmap bitmap) {
        if (bitmap != null) {
            return new ImageSource(bitmap, false);
        }
        throw new NullPointerException("Bitmap must not be null");
    }

    public static ImageSource cachedBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            return new ImageSource(bitmap, true);
        }
        throw new NullPointerException("Bitmap must not be null");
    }

    public ImageSource tilingEnabled() {
        return tiling(true);
    }

    public ImageSource tilingDisabled() {
        return tiling(false);
    }

    public ImageSource tiling(boolean tile) {
        this.tile = tile;
        return this;
    }

    public ImageSource region(Rect sRegion) {
        this.sRegion = sRegion;
        setInvariants();
        return this;
    }

    public ImageSource dimensions(int sWidth, int sHeight) {
        if (this.bitmap == null) {
            this.sWidth = sWidth;
            this.sHeight = sHeight;
        }
        setInvariants();
        return this;
    }

    private void setInvariants() {
        if (this.sRegion != null) {
            this.tile = true;
            this.sWidth = this.sRegion.width();
            this.sHeight = this.sRegion.height();
        }
    }

    protected final Uri getUri() {
        return this.uri;
    }

    protected final Bitmap getBitmap() {
        return this.bitmap;
    }

    protected final Integer getResource() {
        return this.resource;
    }

    protected final boolean getTile() {
        return this.tile;
    }

    protected final int getSWidth() {
        return this.sWidth;
    }

    protected final int getSHeight() {
        return this.sHeight;
    }

    protected final Rect getSRegion() {
        return this.sRegion;
    }

    protected final boolean isCached() {
        return this.cached;
    }
}
