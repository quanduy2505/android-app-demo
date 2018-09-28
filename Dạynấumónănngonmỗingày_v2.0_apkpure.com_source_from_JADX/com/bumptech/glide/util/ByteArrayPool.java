package com.bumptech.glide.util;

import android.util.Log;
import java.util.Queue;

public final class ByteArrayPool {
    private static final ByteArrayPool BYTE_ARRAY_POOL;
    private static final int MAX_BYTE_ARRAY_COUNT = 32;
    private static final int MAX_SIZE = 2146304;
    private static final String TAG = "ByteArrayPool";
    private static final int TEMP_BYTES_SIZE = 65536;
    private final Queue<byte[]> tempQueue;

    static {
        BYTE_ARRAY_POOL = new ByteArrayPool();
    }

    public static ByteArrayPool get() {
        return BYTE_ARRAY_POOL;
    }

    private ByteArrayPool() {
        this.tempQueue = Util.createQueue(0);
    }

    public void clear() {
        synchronized (this.tempQueue) {
            this.tempQueue.clear();
        }
    }

    public byte[] getBytes() {
        byte[] result;
        synchronized (this.tempQueue) {
            result = (byte[]) this.tempQueue.poll();
        }
        if (result == null) {
            result = new byte[TEMP_BYTES_SIZE];
            if (Log.isLoggable(TAG, 3)) {
                Log.d(TAG, "Created temp bytes");
            }
        }
        return result;
    }

    public boolean releaseBytes(byte[] bytes) {
        if (bytes.length != TEMP_BYTES_SIZE) {
            return false;
        }
        boolean accepted = false;
        synchronized (this.tempQueue) {
            if (this.tempQueue.size() < MAX_BYTE_ARRAY_COUNT) {
                accepted = true;
                this.tempQueue.offer(bytes);
            }
        }
        return accepted;
    }
}
