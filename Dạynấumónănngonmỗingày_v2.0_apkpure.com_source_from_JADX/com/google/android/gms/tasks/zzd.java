package com.google.android.gms.tasks;

import android.support.annotation.NonNull;
import java.util.concurrent.Executor;

class zzd<TResult> implements zzf<TResult> {
    private final Executor zzbDK;
    private OnFailureListener zzbLz;
    private final Object zzrN;

    /* renamed from: com.google.android.gms.tasks.zzd.1 */
    class C07091 implements Runnable {
        final /* synthetic */ zzd zzbLA;
        final /* synthetic */ Task zzbLu;

        C07091(zzd com_google_android_gms_tasks_zzd, Task task) {
            this.zzbLA = com_google_android_gms_tasks_zzd;
            this.zzbLu = task;
        }

        public void run() {
            synchronized (this.zzbLA.zzrN) {
                if (this.zzbLA.zzbLz != null) {
                    this.zzbLA.zzbLz.onFailure(this.zzbLu.getException());
                }
            }
        }
    }

    public zzd(@NonNull Executor executor, @NonNull OnFailureListener onFailureListener) {
        this.zzrN = new Object();
        this.zzbDK = executor;
        this.zzbLz = onFailureListener;
    }

    public void cancel() {
        synchronized (this.zzrN) {
            this.zzbLz = null;
        }
    }

    public void onComplete(@NonNull Task<TResult> task) {
        if (!task.isSuccessful()) {
            synchronized (this.zzrN) {
                if (this.zzbLz == null) {
                    return;
                }
                this.zzbDK.execute(new C07091(this, task));
            }
        }
    }
}
