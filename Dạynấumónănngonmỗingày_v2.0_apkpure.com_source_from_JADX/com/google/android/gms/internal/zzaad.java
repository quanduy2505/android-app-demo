package com.google.android.gms.internal;

import android.support.annotation.NonNull;
import com.google.android.gms.common.api.PendingResult.zza;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.WeakHashMap;

public class zzaad {
    private final Map<zzzx<?>, Boolean> zzazC;
    private final Map<TaskCompletionSource<?>, Boolean> zzazD;

    /* renamed from: com.google.android.gms.internal.zzaad.1 */
    class C11511 implements zza {
        final /* synthetic */ zzzx zzazE;
        final /* synthetic */ zzaad zzazF;

        C11511(zzaad com_google_android_gms_internal_zzaad, zzzx com_google_android_gms_internal_zzzx) {
            this.zzazF = com_google_android_gms_internal_zzaad;
            this.zzazE = com_google_android_gms_internal_zzzx;
        }

        public void zzx(Status status) {
            this.zzazF.zzazC.remove(this.zzazE);
        }
    }

    /* renamed from: com.google.android.gms.internal.zzaad.2 */
    class C11522 implements OnCompleteListener<TResult> {
        final /* synthetic */ zzaad zzazF;
        final /* synthetic */ TaskCompletionSource zzazG;

        C11522(zzaad com_google_android_gms_internal_zzaad, TaskCompletionSource taskCompletionSource) {
            this.zzazF = com_google_android_gms_internal_zzaad;
            this.zzazG = taskCompletionSource;
        }

        public void onComplete(@NonNull Task<TResult> task) {
            this.zzazF.zzazD.remove(this.zzazG);
        }
    }

    public zzaad() {
        this.zzazC = Collections.synchronizedMap(new WeakHashMap());
        this.zzazD = Collections.synchronizedMap(new WeakHashMap());
    }

    private void zza(boolean z, Status status) {
        synchronized (this.zzazC) {
            Map hashMap = new HashMap(this.zzazC);
        }
        synchronized (this.zzazD) {
            Map hashMap2 = new HashMap(this.zzazD);
        }
        for (Entry entry : hashMap.entrySet()) {
            if (z || ((Boolean) entry.getValue()).booleanValue()) {
                ((zzzx) entry.getKey()).zzB(status);
            }
        }
        for (Entry entry2 : hashMap2.entrySet()) {
            if (z || ((Boolean) entry2.getValue()).booleanValue()) {
                ((TaskCompletionSource) entry2.getKey()).trySetException(new com.google.android.gms.common.api.zza(status));
            }
        }
    }

    void zza(zzzx<? extends Result> com_google_android_gms_internal_zzzx__extends_com_google_android_gms_common_api_Result, boolean z) {
        this.zzazC.put(com_google_android_gms_internal_zzzx__extends_com_google_android_gms_common_api_Result, Boolean.valueOf(z));
        com_google_android_gms_internal_zzzx__extends_com_google_android_gms_common_api_Result.zza(new C11511(this, com_google_android_gms_internal_zzzx__extends_com_google_android_gms_common_api_Result));
    }

    <TResult> void zza(TaskCompletionSource<TResult> taskCompletionSource, boolean z) {
        this.zzazD.put(taskCompletionSource, Boolean.valueOf(z));
        taskCompletionSource.getTask().addOnCompleteListener(new C11522(this, taskCompletionSource));
    }

    boolean zzvu() {
        return (this.zzazC.isEmpty() && this.zzazD.isEmpty()) ? false : true;
    }

    public void zzvv() {
        zza(false, zzaap.zzaAO);
    }

    public void zzvw() {
        zza(true, zzabq.zzaBV);
    }
}
