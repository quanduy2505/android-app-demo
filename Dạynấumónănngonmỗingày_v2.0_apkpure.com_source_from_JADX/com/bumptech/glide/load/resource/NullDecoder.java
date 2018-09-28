package com.bumptech.glide.load.resource;

import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import rx.android.BuildConfig;

public class NullDecoder<T, Z> implements ResourceDecoder<T, Z> {
    private static final NullDecoder<?, ?> NULL_DECODER;

    static {
        NULL_DECODER = new NullDecoder();
    }

    public static <T, Z> NullDecoder<T, Z> get() {
        return NULL_DECODER;
    }

    public Resource<Z> decode(T t, int width, int height) {
        return null;
    }

    public String getId() {
        return BuildConfig.VERSION_NAME;
    }
}
