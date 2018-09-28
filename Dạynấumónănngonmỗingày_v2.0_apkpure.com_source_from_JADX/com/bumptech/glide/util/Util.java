package com.bumptech.glide.util;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Build.VERSION;
import android.os.Looper;
import android.support.v4.view.MotionEventCompat;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.common.ConnectionResult;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import rx.internal.operators.OnSubscribeConcatMap;

public final class Util {
    private static final char[] HEX_CHAR_ARRAY;
    private static final char[] SHA_1_CHARS;
    private static final char[] SHA_256_CHARS;

    /* renamed from: com.bumptech.glide.util.Util.1 */
    static /* synthetic */ class C03581 {
        static final /* synthetic */ int[] $SwitchMap$android$graphics$Bitmap$Config;

        static {
            $SwitchMap$android$graphics$Bitmap$Config = new int[Config.values().length];
            try {
                $SwitchMap$android$graphics$Bitmap$Config[Config.ALPHA_8.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$android$graphics$Bitmap$Config[Config.RGB_565.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$android$graphics$Bitmap$Config[Config.ARGB_4444.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$android$graphics$Bitmap$Config[Config.ARGB_8888.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    static {
        HEX_CHAR_ARRAY = "0123456789abcdef".toCharArray();
        SHA_256_CHARS = new char[64];
        SHA_1_CHARS = new char[40];
    }

    private Util() {
    }

    public static String sha256BytesToHex(byte[] bytes) {
        String bytesToHex;
        synchronized (SHA_256_CHARS) {
            bytesToHex = bytesToHex(bytes, SHA_256_CHARS);
        }
        return bytesToHex;
    }

    public static String sha1BytesToHex(byte[] bytes) {
        String bytesToHex;
        synchronized (SHA_1_CHARS) {
            bytesToHex = bytesToHex(bytes, SHA_1_CHARS);
        }
        return bytesToHex;
    }

    private static String bytesToHex(byte[] bytes, char[] hexChars) {
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & MotionEventCompat.ACTION_MASK;
            hexChars[j * 2] = HEX_CHAR_ARRAY[v >>> 4];
            hexChars[(j * 2) + 1] = HEX_CHAR_ARRAY[v & 15];
        }
        return new String(hexChars);
    }

    @Deprecated
    public static int getSize(Bitmap bitmap) {
        return getBitmapByteSize(bitmap);
    }

    @TargetApi(19)
    public static int getBitmapByteSize(Bitmap bitmap) {
        if (VERSION.SDK_INT >= 19) {
            try {
                return bitmap.getAllocationByteCount();
            } catch (NullPointerException e) {
            }
        }
        return bitmap.getHeight() * bitmap.getRowBytes();
    }

    public static int getBitmapByteSize(int width, int height, Config config) {
        return (width * height) * getBytesPerPixel(config);
    }

    private static int getBytesPerPixel(Config config) {
        if (config == null) {
            config = Config.ARGB_8888;
        }
        switch (C03581.$SwitchMap$android$graphics$Bitmap$Config[config.ordinal()]) {
            case OnSubscribeConcatMap.BOUNDARY /*1*/:
                return 1;
            case OnSubscribeConcatMap.END /*2*/:
            case ConnectionResult.SERVICE_DISABLED /*3*/:
                return 2;
            default:
                return 4;
        }
    }

    public static boolean isValidDimensions(int width, int height) {
        return isValidDimension(width) && isValidDimension(height);
    }

    private static boolean isValidDimension(int dimen) {
        return dimen > 0 || dimen == Target.SIZE_ORIGINAL;
    }

    public static void assertMainThread() {
        if (!isOnMainThread()) {
            throw new IllegalArgumentException("You must call this method on the main thread");
        }
    }

    public static void assertBackgroundThread() {
        if (!isOnBackgroundThread()) {
            throw new IllegalArgumentException("YOu must call this method on a background thread");
        }
    }

    public static boolean isOnMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public static boolean isOnBackgroundThread() {
        return !isOnMainThread();
    }

    public static <T> Queue<T> createQueue(int size) {
        return new ArrayDeque(size);
    }

    public static <T> List<T> getSnapshot(Collection<T> other) {
        List<T> result = new ArrayList(other.size());
        for (T item : other) {
            result.add(item);
        }
        return result;
    }
}
