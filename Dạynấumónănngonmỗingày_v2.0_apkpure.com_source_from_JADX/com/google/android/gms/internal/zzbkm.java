package com.google.android.gms.internal;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import java.util.concurrent.Executor;

public class zzbkm implements Executor {
    private static zzbkm zzbWV;
    private Handler mHandler;

    static {
        zzbWV = new zzbkm();
    }

    private zzbkm() {
        this.mHandler = new Handler(Looper.getMainLooper());
    }

    public static zzbkm zzUK() {
        return zzbWV;
    }

    public void execute(@NonNull Runnable runnable) {
        this.mHandler.post(runnable);
    }
}
