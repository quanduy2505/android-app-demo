package com.bumptech.glide.load.model.stream;

import android.content.Context;
import com.bumptech.glide.load.data.ByteArrayFetcher;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.GenericLoaderFactory;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import java.io.InputStream;
import rx.android.BuildConfig;

public class StreamByteArrayLoader implements StreamModelLoader<byte[]> {
    private final String id;

    public static class Factory implements ModelLoaderFactory<byte[], InputStream> {
        public ModelLoader<byte[], InputStream> build(Context context, GenericLoaderFactory factories) {
            return new StreamByteArrayLoader();
        }

        public void teardown() {
        }
    }

    public StreamByteArrayLoader() {
        this(BuildConfig.VERSION_NAME);
    }

    @Deprecated
    public StreamByteArrayLoader(String id) {
        this.id = id;
    }

    public DataFetcher<InputStream> getResourceFetcher(byte[] model, int width, int height) {
        return new ByteArrayFetcher(model, this.id);
    }
}
