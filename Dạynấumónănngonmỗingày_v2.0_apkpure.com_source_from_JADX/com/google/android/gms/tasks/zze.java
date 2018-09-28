package com.google.android.gms.tasks;

import android.support.annotation.NonNull;
import java.util.concurrent.Executor;

class zze<TResult> implements zzf<TResult> {
    private final Executor zzbDK;
    private OnSuccessListener<? super TResult> zzbLB;
    private final Object zzrN;

    /* renamed from: com.google.android.gms.tasks.zze.1 */
    class C07101 implements Runnable {
        final /* synthetic */ zze zzbLC;
        final /* synthetic */ Task zzbLu;

        C07101(zze com_google_android_gms_tasks_zze, Task task) {
            this.zzbLC = com_google_android_gms_tasks_zze;
            this.zzbLu = task;
        }

        public void run() {
            synchronized (this.zzbLC.zzrN) {
                if (this.zzbLC.zzbLB != null) {
                    this.zzbLC.zzbLB.onSuccess(this.zzbLu.getResult());
                }
            }
        }
    }

    public zze(@NonNull Executor executor, @NonNull OnSuccessListener<? super TResult> onSuccessListener) {
        this.zzrN = new Object();
        this.zzbDK = executor;
        this.zzbLB = onSuccessListener;
    }

    public void cancel() {
        synchronized (this.zzrN) {
            this.zzbLB = null;
        }
    }

    public void onComplete(@NonNull Task<TResult> task) {
        if (task.isSuccessful()) {
            synchronized (this.zzrN) {
                if (this.zzbLB == null) {
                    return;
                }
                this.zzbDK.execute(new C07101(this, task));
            }
        }
    }
}
