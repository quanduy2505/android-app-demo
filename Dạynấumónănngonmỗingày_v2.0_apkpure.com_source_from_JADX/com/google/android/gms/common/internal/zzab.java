package com.google.android.gms.common.internal;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.zze;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import java.util.concurrent.TimeUnit;

public class zzab {
    private static final zzb zzaEZ;

    public interface zza<R extends Result, T> {
        T zzf(R r);
    }

    public interface zzb {
        com.google.android.gms.common.api.zza zzG(Status status);
    }

    /* renamed from: com.google.android.gms.common.internal.zzab.1 */
    class C11291 implements zzb {
        C11291() {
        }

        public com.google.android.gms.common.api.zza zzG(Status status) {
            return zzb.zzF(status);
        }
    }

    /* renamed from: com.google.android.gms.common.internal.zzab.2 */
    class C11302 implements com.google.android.gms.common.api.PendingResult.zza {
        final /* synthetic */ PendingResult zzaFa;
        final /* synthetic */ TaskCompletionSource zzaFb;
        final /* synthetic */ zza zzaFc;
        final /* synthetic */ zzb zzaFd;

        C11302(PendingResult pendingResult, TaskCompletionSource taskCompletionSource, zza com_google_android_gms_common_internal_zzab_zza, zzb com_google_android_gms_common_internal_zzab_zzb) {
            this.zzaFa = pendingResult;
            this.zzaFb = taskCompletionSource;
            this.zzaFc = com_google_android_gms_common_internal_zzab_zza;
            this.zzaFd = com_google_android_gms_common_internal_zzab_zzb;
        }

        public void zzx(Status status) {
            if (status.isSuccess()) {
                this.zzaFb.setResult(this.zzaFc.zzf(this.zzaFa.await(0, TimeUnit.MILLISECONDS)));
                return;
            }
            this.zzaFb.setException(this.zzaFd.zzG(status));
        }
    }

    /* renamed from: com.google.android.gms.common.internal.zzab.3 */
    class C11313 implements zza<R, T> {
        final /* synthetic */ zze zzaFe;

        C11313(zze com_google_android_gms_common_api_zze) {
            this.zzaFe = com_google_android_gms_common_api_zze;
        }

        public T zze(R r) {
            this.zzaFe.zzb(r);
            return this.zzaFe;
        }

        public /* synthetic */ Object zzf(Result result) {
            return zze(result);
        }
    }

    static {
        zzaEZ = new C11291();
    }

    public static <R extends Result, T extends zze<R>> Task<T> zza(PendingResult<R> pendingResult, T t) {
        return zza((PendingResult) pendingResult, new C11313(t));
    }

    public static <R extends Result, T> Task<T> zza(PendingResult<R> pendingResult, zza<R, T> com_google_android_gms_common_internal_zzab_zza_R__T) {
        return zza(pendingResult, com_google_android_gms_common_internal_zzab_zza_R__T, zzaEZ);
    }

    public static <R extends Result, T> Task<T> zza(PendingResult<R> pendingResult, zza<R, T> com_google_android_gms_common_internal_zzab_zza_R__T, zzb com_google_android_gms_common_internal_zzab_zzb) {
        TaskCompletionSource taskCompletionSource = new TaskCompletionSource();
        pendingResult.zza(new C11302(pendingResult, taskCompletionSource, com_google_android_gms_common_internal_zzab_zza_R__T, com_google_android_gms_common_internal_zzab_zzb));
        return taskCompletionSource.getTask();
    }
}
