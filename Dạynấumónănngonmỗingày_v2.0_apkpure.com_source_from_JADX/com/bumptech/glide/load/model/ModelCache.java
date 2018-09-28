package com.bumptech.glide.load.model;

import com.bumptech.glide.util.LruCache;
import com.bumptech.glide.util.Util;
import java.util.Queue;

public class ModelCache<A, B> {
    private static final int DEFAULT_SIZE = 250;
    private final LruCache<ModelKey<A>, B> cache;

    static final class ModelKey<A> {
        private static final Queue<ModelKey<?>> KEY_QUEUE;
        private int height;
        private A model;
        private int width;

        static {
            KEY_QUEUE = Util.createQueue(0);
        }

        static <A> ModelKey<A> get(A model, int width, int height) {
            ModelKey<A> modelKey = (ModelKey) KEY_QUEUE.poll();
            if (modelKey == null) {
                modelKey = new ModelKey();
            }
            modelKey.init(model, width, height);
            return modelKey;
        }

        private ModelKey() {
        }

        private void init(A model, int width, int height) {
            this.model = model;
            this.width = width;
            this.height = height;
        }

        public void release() {
            KEY_QUEUE.offer(this);
        }

        public boolean equals(Object o) {
            if (!(o instanceof ModelKey)) {
                return false;
            }
            ModelKey other = (ModelKey) o;
            if (this.width == other.width && this.height == other.height && this.model.equals(other.model)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return (((this.height * 31) + this.width) * 31) + this.model.hashCode();
        }
    }

    /* renamed from: com.bumptech.glide.load.model.ModelCache.1 */
    class C09771 extends LruCache<ModelKey<A>, B> {
        C09771(int x0) {
            super(x0);
        }

        protected void onItemEvicted(ModelKey<A> key, B b) {
            key.release();
        }
    }

    public ModelCache() {
        this(DEFAULT_SIZE);
    }

    public ModelCache(int size) {
        this.cache = new C09771(size);
    }

    public B get(A model, int width, int height) {
        ModelKey<A> key = ModelKey.get(model, width, height);
        B result = this.cache.get(key);
        key.release();
        return result;
    }

    public void put(A model, int width, int height, B value) {
        this.cache.put(ModelKey.get(model, width, height), value);
    }
}
