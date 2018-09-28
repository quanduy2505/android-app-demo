package com.google.android.gms.internal;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.gms.common.internal.zzac;
import com.google.firebase.storage.zzd;
import java.util.concurrent.Executor;

public class zzbra {
    static boolean zzcma;
    private final Handler mHandler;
    private final Executor zzbDK;

    static {
        zzcma = false;
    }

    public zzbra(@Nullable Executor executor) {
        this.zzbDK = executor;
        if (this.zzbDK != null) {
            this.mHandler = null;
        } else if (Looper.myLooper() != null) {
            this.mHandler = new Handler();
        } else {
            this.mHandler = null;
        }
    }

    public void zzx(@NonNull Runnable runnable) {
        zzac.zzw(runnable);
        if (this.mHandler != null) {
            this.mHandler.post(runnable);
        } else if (this.zzbDK != null) {
            this.zzbDK.execute(runnable);
        } else {
            zzd.zzaaW().zzw(runnable);
        }
    }
}
