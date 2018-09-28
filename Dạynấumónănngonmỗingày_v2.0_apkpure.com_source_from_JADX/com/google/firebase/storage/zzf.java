package com.google.firebase.storage;

import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.internal.zzbqw;
import com.google.android.gms.internal.zzbrf;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.storage.StorageMetadata.Builder;
import org.json.JSONException;

class zzf implements Runnable {
    private final StorageReference zzcki;
    private final TaskCompletionSource<StorageMetadata> zzckj;
    private zzbqw zzckk;
    private StorageMetadata zzcku;
    private final StorageMetadata zzclI;

    public zzf(@NonNull StorageReference storageReference, @NonNull TaskCompletionSource<StorageMetadata> taskCompletionSource, @NonNull StorageMetadata storageMetadata) {
        this.zzcku = null;
        this.zzcki = storageReference;
        this.zzckj = taskCompletionSource;
        this.zzclI = storageMetadata;
        this.zzckk = new zzbqw(this.zzcki.getApp(), this.zzcki.getStorage().getMaxOperationRetryTimeMillis());
    }

    public void run() {
        Throwable e;
        String str;
        String str2;
        String valueOf;
        try {
            zzbrf zza = this.zzcki.zzaaN().zza(this.zzcki.zzaaO(), this.zzclI.zzaaM());
            this.zzckk.zzd(zza);
            if (zza.zzabn()) {
                try {
                    this.zzcku = new Builder(zza.zzabq(), this.zzcki).build();
                } catch (JSONException e2) {
                    e = e2;
                    str = "UpdateMetadataTask";
                    str2 = "Unable to parse a valid JSON object from resulting metadata:";
                    valueOf = String.valueOf(zza.zzabk());
                    Log.e(str, valueOf.length() == 0 ? str2.concat(valueOf) : new String(str2), e);
                    this.zzckj.setException(StorageException.fromException(e));
                    return;
                } catch (RemoteException e3) {
                    e = e3;
                    str = "UpdateMetadataTask";
                    str2 = "Unable to parse a valid JSON object from resulting metadata:";
                    valueOf = String.valueOf(zza.zzabk());
                    if (valueOf.length() == 0) {
                    }
                    Log.e(str, valueOf.length() == 0 ? str2.concat(valueOf) : new String(str2), e);
                    this.zzckj.setException(StorageException.fromException(e));
                    return;
                }
            }
            if (this.zzckj != null) {
                zza.zza(this.zzckj, this.zzcku);
            }
        } catch (JSONException e4) {
            e = e4;
            Log.e("UpdateMetadataTask", "Unable to create the request from metadata.", e);
            this.zzckj.setException(StorageException.fromException(e));
        } catch (RemoteException e5) {
            e = e5;
            Log.e("UpdateMetadataTask", "Unable to create the request from metadata.", e);
            this.zzckj.setException(StorageException.fromException(e));
        }
    }
}
