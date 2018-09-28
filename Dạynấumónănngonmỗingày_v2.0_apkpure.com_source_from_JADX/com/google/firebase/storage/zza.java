package com.google.firebase.storage;

import android.support.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.internal.zzbqw;
import com.google.android.gms.internal.zzbrf;
import com.google.android.gms.tasks.TaskCompletionSource;

class zza implements Runnable {
    private StorageReference zzcki;
    private TaskCompletionSource<Void> zzckj;
    private zzbqw zzckk;

    public zza(@NonNull StorageReference storageReference, @NonNull TaskCompletionSource<Void> taskCompletionSource) {
        zzac.zzw(storageReference);
        zzac.zzw(taskCompletionSource);
        this.zzcki = storageReference;
        this.zzckj = taskCompletionSource;
        this.zzckk = new zzbqw(this.zzcki.getStorage().getApp(), this.zzcki.getStorage().getMaxOperationRetryTimeMillis());
    }

    public void run() {
        try {
            zzbrf zzC = this.zzcki.zzaaN().zzC(this.zzcki.zzaaO());
            this.zzckk.zzd(zzC);
            zzC.zza(this.zzckj, null);
        } catch (Throwable e) {
            Log.e("DeleteStorageTask", "Unable to create Firebase Storage network request.", e);
            this.zzckj.setException(StorageException.fromException(e));
        }
    }
}
