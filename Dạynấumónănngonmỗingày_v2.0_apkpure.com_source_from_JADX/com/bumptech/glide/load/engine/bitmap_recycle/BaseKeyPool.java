package com.bumptech.glide.load.engine.bitmap_recycle;

import com.bumptech.glide.util.Util;
import java.util.Queue;

abstract class BaseKeyPool<T extends Poolable> {
    private static final int MAX_SIZE = 20;
    private final Queue<T> keyPool;

    protected abstract T create();

    BaseKeyPool() {
        this.keyPool = Util.createQueue(MAX_SIZE);
    }

    protected T get() {
        Poolable result = (Poolable) this.keyPool.poll();
        if (result == null) {
            return create();
        }
        return result;
    }

    public void offer(T key) {
        if (this.keyPool.size() < MAX_SIZE) {
            this.keyPool.offer(key);
        }
    }
}
