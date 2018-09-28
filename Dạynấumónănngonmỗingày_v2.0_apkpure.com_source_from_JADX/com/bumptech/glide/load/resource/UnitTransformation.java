package com.bumptech.glide.load.resource;

import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import rx.android.BuildConfig;

public class UnitTransformation<T> implements Transformation<T> {
    private static final Transformation<?> TRANSFORMATION;

    static {
        TRANSFORMATION = new UnitTransformation();
    }

    public static <T> UnitTransformation<T> get() {
        return (UnitTransformation) TRANSFORMATION;
    }

    public Resource<T> transform(Resource<T> resource, int outWidth, int outHeight) {
        return resource;
    }

    public String getId() {
        return BuildConfig.VERSION_NAME;
    }
}
