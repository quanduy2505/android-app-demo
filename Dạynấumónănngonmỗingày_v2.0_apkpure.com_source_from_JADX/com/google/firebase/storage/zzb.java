package com.google.firebase.storage;

import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.internal.zzbqw;
import com.google.android.gms.internal.zzbrf;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.storage.StorageMetadata.Builder;
import org.json.JSONException;

class zzb implements Runnable {
    private StorageReference zzcki;
    private TaskCompletionSource<StorageMetadata> zzckj;
    private zzbqw zzckk;
    private StorageMetadata zzcku;

    public zzb(@NonNull StorageReference storageReference, @NonNull TaskCompletionSource<StorageMetadata> taskCompletionSource) {
        zzac.zzw(storageReference);
        zzac.zzw(taskCompletionSource);
        this.zzcki = storageReference;
        this.zzckj = taskCompletionSource;
        this.zzckk = new zzbqw(this.zzcki.getApp(), this.zzcki.getStorage().getMaxOperationRetryTimeMillis());
    }

    public void run() {
        Throwable e;
        String str;
        String str2;
        String valueOf;
        try {
            zzbrf zzD = this.zzcki.zzaaN().zzD(this.zzcki.zzaaO());
            this.zzckk.zzd(zzD);
            if (zzD.zzabn()) {
                try {
                    this.zzcku = new Builder(zzD.zzabq(), this.zzcki).build();
                } catch (JSONException e2) {
                    e = e2;
                    str = "GetMetadataTask";
                    str2 = "Unable to parse resulting metadata. ";
                    valueOf = String.valueOf(zzD.zzabk());
                    Log.e(str, valueOf.length() == 0 ? str2.concat(valueOf) : new String(str2), e);
                    this.zzckj.setException(StorageException.fromException(e));
                    return;
                } catch (RemoteException e3) {
                    e = e3;
                    str = "GetMetadataTask";
                    str2 = "Unable to parse resulting metadata. ";
                    valueOf = String.valueOf(zzD.zzabk());
                    if (valueOf.length() == 0) {
                    }
                    Log.e(str, valueOf.length() == 0 ? str2.concat(valueOf) : new String(str2), e);
                    this.zzckj.setException(StorageException.fromException(e));
                    return;
                }
            }
            if (this.zzckj != null) {
                zzD.zza(this.zzckj, this.zzcku);
            }
        } catch (Throwable e4) {
            Log.e("GetMetadataTask", "Unable to create firebase storage network request.", e4);
            this.zzckj.setException(StorageException.fromException(e4));
        }
    }
}
