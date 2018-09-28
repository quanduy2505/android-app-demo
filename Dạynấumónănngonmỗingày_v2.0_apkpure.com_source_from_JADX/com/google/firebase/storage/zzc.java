package com.google.firebase.storage;

import android.support.annotation.NonNull;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

class zzc {
    private static final zzc zzclg;
    private final Object mSyncObject;
    private final Map<String, WeakReference<StorageTask>> zzclh;

    static {
        zzclg = new zzc();
    }

    zzc() {
        this.zzclh = new HashMap();
        this.mSyncObject = new Object();
    }

    static zzc zzaaV() {
        return zzclg;
    }

    public List<UploadTask> zza(@NonNull StorageReference storageReference) {
        List<UploadTask> unmodifiableList;
        synchronized (this.mSyncObject) {
            List arrayList = new ArrayList();
            String storageReference2 = storageReference.toString();
            for (Entry entry : this.zzclh.entrySet()) {
                if (((String) entry.getKey()).startsWith(storageReference2)) {
                    StorageTask storageTask = (StorageTask) ((WeakReference) entry.getValue()).get();
                    if (storageTask instanceof UploadTask) {
                        arrayList.add((UploadTask) storageTask);
                    }
                }
            }
            unmodifiableList = Collections.unmodifiableList(arrayList);
        }
        return unmodifiableList;
    }

    public List<FileDownloadTask> zzb(@NonNull StorageReference storageReference) {
        List<FileDownloadTask> unmodifiableList;
        synchronized (this.mSyncObject) {
            List arrayList = new ArrayList();
            String storageReference2 = storageReference.toString();
            for (Entry entry : this.zzclh.entrySet()) {
                if (((String) entry.getKey()).startsWith(storageReference2)) {
                    StorageTask storageTask = (StorageTask) ((WeakReference) entry.getValue()).get();
                    if (storageTask instanceof FileDownloadTask) {
                        arrayList.add((FileDownloadTask) storageTask);
                    }
                }
            }
            unmodifiableList = Collections.unmodifiableList(arrayList);
        }
        return unmodifiableList;
    }

    public void zzb(StorageTask storageTask) {
        synchronized (this.mSyncObject) {
            this.zzclh.put(storageTask.getStorage().toString(), new WeakReference(storageTask));
        }
    }

    public void zzc(StorageTask storageTask) {
        synchronized (this.mSyncObject) {
            String storageReference = storageTask.getStorage().toString();
            WeakReference weakReference = (WeakReference) this.zzclh.get(storageReference);
            StorageTask storageTask2 = weakReference != null ? (StorageTask) weakReference.get() : null;
            if (storageTask2 == null || storageTask2 == storageTask) {
                this.zzclh.remove(storageReference);
            }
        }
    }
}
