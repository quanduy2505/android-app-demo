package com.bumptech.glide.load.resource;

import com.bumptech.glide.load.Encoder;
import java.io.OutputStream;
import rx.android.BuildConfig;

public class NullEncoder<T> implements Encoder<T> {
    private static final NullEncoder<?> NULL_ENCODER;

    static {
        NULL_ENCODER = new NullEncoder();
    }

    public static <T> Encoder<T> get() {
        return NULL_ENCODER;
    }

    public boolean encode(T t, OutputStream os) {
        return false;
    }

    public String getId() {
        return BuildConfig.VERSION_NAME;
    }
}
