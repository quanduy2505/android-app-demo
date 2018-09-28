package com.bumptech.glide.load.resource.transcode;

import com.bumptech.glide.load.engine.Resource;
import rx.android.BuildConfig;

public class UnitTranscoder<Z> implements ResourceTranscoder<Z, Z> {
    private static final UnitTranscoder<?> UNIT_TRANSCODER;

    static {
        UNIT_TRANSCODER = new UnitTranscoder();
    }

    public static <Z> ResourceTranscoder<Z, Z> get() {
        return UNIT_TRANSCODER;
    }

    public Resource<Z> transcode(Resource<Z> toTranscode) {
        return toTranscode;
    }

    public String getId() {
        return BuildConfig.VERSION_NAME;
    }
}
