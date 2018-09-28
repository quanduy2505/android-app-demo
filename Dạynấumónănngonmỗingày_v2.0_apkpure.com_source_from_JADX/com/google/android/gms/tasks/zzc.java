package com.google.android.gms.tasks;

import android.support.annotation.NonNull;
import java.util.concurrent.Executor;

class zzc<TResult> implements zzf<TResult> {
    private final Executor zzbDK;
    private OnCompleteListener<TResult> zzbLx;
    private final Object zzrN;

    /* renamed from: com.google.android.gms.tasks.zzc.1 */
    class C07081 implements Runnable {
        final /* synthetic */ Task zzbLu;
        final /* synthetic */ zzc zzbLy;

        C07081(zzc com_google_android_gms_tasks_zzc, Task task) {
            this.zzbLy = com_google_android_gms_tasks_zzc;
            this.zzbLu = task;
        }

        public void run() {
            synchronized (this.zzbLy.zzrN) {
                if (this.zzbLy.zzbLx != null) {
                    this.zzbLy.zzbLx.onComplete(this.zzbLu);
                }
            }
        }
    }

    public zzc(@NonNull Executor executor, @NonNull OnCompleteListener<TResult> onCompleteListener) {
        this.zzrN = new Object();
        this.zzbDK = executor;
        this.zzbLx = onCompleteListener;
    }

    public void cancel() {
        synchronized (this.zzrN) {
            this.zzbLx = null;
        }
    }

    public void onComplete(@NonNull Task<TResult> task) {
        synchronized (this.zzrN) {
            if (this.zzbLx == null) {
                return;
            }
            this.zzbDK.execute(new C07081(this, task));
        }
    }
}
