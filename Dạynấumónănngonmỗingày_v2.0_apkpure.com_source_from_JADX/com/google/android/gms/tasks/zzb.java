package com.google.android.gms.tasks;

import android.support.annotation.NonNull;
import java.util.concurrent.Executor;

class zzb<TResult, TContinuationResult> implements OnFailureListener, OnSuccessListener<TContinuationResult>, zzf<TResult> {
    private final Executor zzbDK;
    private final Continuation<TResult, Task<TContinuationResult>> zzbLs;
    private final zzh<TContinuationResult> zzbLt;

    /* renamed from: com.google.android.gms.tasks.zzb.1 */
    class C07071 implements Runnable {
        final /* synthetic */ Task zzbLu;
        final /* synthetic */ zzb zzbLw;

        C07071(zzb com_google_android_gms_tasks_zzb, Task task) {
            this.zzbLw = com_google_android_gms_tasks_zzb;
            this.zzbLu = task;
        }

        public void run() {
            try {
                Task task = (Task) this.zzbLw.zzbLs.then(this.zzbLu);
                if (task == null) {
                    this.zzbLw.onFailure(new NullPointerException("Continuation returned null"));
                    return;
                }
                task.addOnSuccessListener(TaskExecutors.zzbLG, this.zzbLw);
                task.addOnFailureListener(TaskExecutors.zzbLG, this.zzbLw);
            } catch (Exception e) {
                if (e.getCause() instanceof Exception) {
                    this.zzbLw.zzbLt.setException((Exception) e.getCause());
                } else {
                    this.zzbLw.zzbLt.setException(e);
                }
            } catch (Exception e2) {
                this.zzbLw.zzbLt.setException(e2);
            }
        }
    }

    public zzb(@NonNull Executor executor, @NonNull Continuation<TResult, Task<TContinuationResult>> continuation, @NonNull zzh<TContinuationResult> com_google_android_gms_tasks_zzh_TContinuationResult) {
        this.zzbDK = executor;
        this.zzbLs = continuation;
        this.zzbLt = com_google_android_gms_tasks_zzh_TContinuationResult;
    }

    public void cancel() {
        throw new UnsupportedOperationException();
    }

    public void onComplete(@NonNull Task<TResult> task) {
        this.zzbDK.execute(new C07071(this, task));
    }

    public void onFailure(@NonNull Exception exception) {
        this.zzbLt.setException(exception);
    }

    public void onSuccess(TContinuationResult tContinuationResult) {
        this.zzbLt.setResult(tContinuationResult);
    }
}
