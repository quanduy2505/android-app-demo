package com.google.firebase.storage;

import android.app.Activity;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.internal.zzbqv;
import com.google.android.gms.internal.zzbra;
import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;

class zze<TListenerType, TResult extends ProvideError> {
    private final HashMap<TListenerType, zzbra> zzclA;
    private StorageTask<TResult> zzclB;
    private int zzclC;
    private zza<TListenerType, TResult> zzclD;
    private final Queue<TListenerType> zzclz;

    /* renamed from: com.google.firebase.storage.zze.1 */
    class C07281 implements Runnable {
        final /* synthetic */ Object zzclE;
        final /* synthetic */ zze zzclF;

        C07281(zze com_google_firebase_storage_zze, Object obj) {
            this.zzclF = com_google_firebase_storage_zze;
            this.zzclE = obj;
        }

        public void run() {
            this.zzclF.zzaG(this.zzclE);
        }
    }

    /* renamed from: com.google.firebase.storage.zze.2 */
    class C07292 implements Runnable {
        final /* synthetic */ zze zzclF;
        final /* synthetic */ Object zzclG;
        final /* synthetic */ ProvideError zzclH;

        C07292(zze com_google_firebase_storage_zze, Object obj, ProvideError provideError) {
            this.zzclF = com_google_firebase_storage_zze;
            this.zzclG = obj;
            this.zzclH = provideError;
        }

        public void run() {
            this.zzclF.zzclD.zzl(this.zzclG, this.zzclH);
        }
    }

    public interface zza<TListenerType, TResult> {
        void zzl(@NonNull TListenerType tListenerType, @NonNull TResult tResult);
    }

    public zze(@NonNull StorageTask<TResult> storageTask, int i, @NonNull zza<TListenerType, TResult> com_google_firebase_storage_zze_zza_TListenerType__TResult) {
        this.zzclz = new ConcurrentLinkedQueue();
        this.zzclA = new HashMap();
        this.zzclB = storageTask;
        this.zzclC = i;
        this.zzclD = com_google_firebase_storage_zze_zza_TListenerType__TResult;
    }

    public void zza(@Nullable Activity activity, @Nullable Executor executor, @NonNull TListenerType tListenerType) {
        boolean z = true;
        zzac.zzw(tListenerType);
        synchronized (this.zzclB.zzaaR()) {
            boolean z2 = (this.zzclB.zzaaQ() & this.zzclC) != 0;
            this.zzclz.add(tListenerType);
            this.zzclA.put(tListenerType, new zzbra(executor));
            if (activity != null) {
                if (VERSION.SDK_INT >= 17) {
                    if (activity.isDestroyed()) {
                        z = false;
                    }
                    zzac.zzb(z, (Object) "Activity is already destroyed!");
                }
                zzbqv.zzabf().zza(activity, tListenerType, new C07281(this, tListenerType));
            }
        }
        if (z2) {
            this.zzclD.zzl(tListenerType, this.zzclB.zzaaS());
        }
    }

    public void zzaG(@NonNull TListenerType tListenerType) {
        zzac.zzw(tListenerType);
        synchronized (this.zzclB.zzaaR()) {
            this.zzclA.remove(tListenerType);
            this.zzclz.remove(tListenerType);
            zzbqv.zzabf().zzaH(tListenerType);
        }
    }

    public void zzaaZ() {
        if ((this.zzclB.zzaaQ() & this.zzclC) != 0) {
            ProvideError zzaaS = this.zzclB.zzaaS();
            for (Object next : this.zzclz) {
                zzbra com_google_android_gms_internal_zzbra = (zzbra) this.zzclA.get(next);
                if (com_google_android_gms_internal_zzbra != null) {
                    com_google_android_gms_internal_zzbra.zzx(new C07292(this, next, zzaaS));
                }
            }
        }
    }
}
