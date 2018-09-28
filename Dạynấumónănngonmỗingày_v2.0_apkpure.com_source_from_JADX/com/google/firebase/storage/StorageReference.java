package com.google.firebase.storage;

import android.net.Uri;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.internal.zzbqx;
import com.google.android.gms.internal.zzbre;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.StreamDownloadTask.StreamProcessor;
import com.google.firebase.storage.StreamDownloadTask.TaskSnapshot;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import rx.android.BuildConfig;

public class StorageReference {
    static final /* synthetic */ boolean $assertionsDisabled;
    private final Uri zzckP;
    private final FirebaseStorage zzckQ;

    /* renamed from: com.google.firebase.storage.StorageReference.1 */
    class C12191 implements OnSuccessListener<StorageMetadata> {
        final /* synthetic */ TaskCompletionSource zzckR;

        C12191(StorageReference storageReference, TaskCompletionSource taskCompletionSource) {
            this.zzckR = taskCompletionSource;
        }

        public /* synthetic */ void onSuccess(Object obj) {
            zzb((StorageMetadata) obj);
        }

        public void zzb(StorageMetadata storageMetadata) {
            this.zzckR.setResult(storageMetadata.getDownloadUrl());
        }
    }

    /* renamed from: com.google.firebase.storage.StorageReference.2 */
    class C12202 implements OnFailureListener {
        final /* synthetic */ TaskCompletionSource zzckR;

        C12202(StorageReference storageReference, TaskCompletionSource taskCompletionSource) {
            this.zzckR = taskCompletionSource;
        }

        public void onFailure(@NonNull Exception exception) {
            this.zzckR.setException(exception);
        }
    }

    /* renamed from: com.google.firebase.storage.StorageReference.3 */
    class C12213 implements OnFailureListener {
        static final /* synthetic */ boolean $assertionsDisabled;
        final /* synthetic */ TaskCompletionSource zzckS;

        static {
            $assertionsDisabled = !StorageReference.class.desiredAssertionStatus();
        }

        C12213(StorageReference storageReference, TaskCompletionSource taskCompletionSource) {
            this.zzckS = taskCompletionSource;
        }

        public void onFailure(@NonNull Exception exception) {
            Exception fromExceptionAndHttpCode = StorageException.fromExceptionAndHttpCode(exception, 0);
            if ($assertionsDisabled || fromExceptionAndHttpCode != null) {
                this.zzckS.setException(fromExceptionAndHttpCode);
                return;
            }
            throw new AssertionError();
        }
    }

    /* renamed from: com.google.firebase.storage.StorageReference.4 */
    class C12224 implements OnSuccessListener<TaskSnapshot> {
        final /* synthetic */ TaskCompletionSource zzckS;

        C12224(StorageReference storageReference, TaskCompletionSource taskCompletionSource) {
            this.zzckS = taskCompletionSource;
        }

        public /* synthetic */ void onSuccess(Object obj) {
            zza((TaskSnapshot) obj);
        }

        public void zza(TaskSnapshot taskSnapshot) {
            if (!this.zzckS.getTask().isComplete()) {
                Log.e("StorageReference", "getBytes 'succeeded', but failed to set a Result.");
                this.zzckS.setException(StorageException.fromErrorStatus(Status.zzayj));
            }
        }
    }

    /* renamed from: com.google.firebase.storage.StorageReference.5 */
    class C12235 implements StreamProcessor {
        final /* synthetic */ TaskCompletionSource zzckS;
        final /* synthetic */ long zzckT;

        C12235(StorageReference storageReference, long j, TaskCompletionSource taskCompletionSource) {
            this.zzckT = j;
            this.zzckS = taskCompletionSource;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void doInBackground(com.google.firebase.storage.StreamDownloadTask.TaskSnapshot r9, java.io.InputStream r10) throws java.io.IOException {
            /*
            r8 = this;
            r0 = 0;
            r1 = new java.io.ByteArrayOutputStream;	 Catch:{ all -> 0x002b }
            r1.<init>();	 Catch:{ all -> 0x002b }
            r2 = 16384; // 0x4000 float:2.2959E-41 double:8.0948E-320;
            r2 = new byte[r2];	 Catch:{ all -> 0x002b }
        L_0x000a:
            r3 = 0;
            r4 = 16384; // 0x4000 float:2.2959E-41 double:8.0948E-320;
            r3 = r10.read(r2, r3, r4);	 Catch:{ all -> 0x002b }
            r4 = -1;
            if (r3 == r4) goto L_0x0035;
        L_0x0014:
            r0 = r0 + r3;
            r4 = (long) r0;	 Catch:{ all -> 0x002b }
            r6 = r8.zzckT;	 Catch:{ all -> 0x002b }
            r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
            if (r4 <= 0) goto L_0x0030;
        L_0x001c:
            r0 = "StorageReference";
            r1 = "the maximum allowed buffer size was exceeded.";
            android.util.Log.e(r0, r1);	 Catch:{ all -> 0x002b }
            r0 = new java.lang.IndexOutOfBoundsException;	 Catch:{ all -> 0x002b }
            r1 = "the maximum allowed buffer size was exceeded.";
            r0.<init>(r1);	 Catch:{ all -> 0x002b }
            throw r0;	 Catch:{ all -> 0x002b }
        L_0x002b:
            r0 = move-exception;
            r10.close();
            throw r0;
        L_0x0030:
            r4 = 0;
            r1.write(r2, r4, r3);	 Catch:{ all -> 0x002b }
            goto L_0x000a;
        L_0x0035:
            r1.flush();	 Catch:{ all -> 0x002b }
            r0 = r8.zzckS;	 Catch:{ all -> 0x002b }
            r1 = r1.toByteArray();	 Catch:{ all -> 0x002b }
            r0.setResult(r1);	 Catch:{ all -> 0x002b }
            r10.close();
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.firebase.storage.StorageReference.5.doInBackground(com.google.firebase.storage.StreamDownloadTask$TaskSnapshot, java.io.InputStream):void");
        }
    }

    static {
        $assertionsDisabled = !StorageReference.class.desiredAssertionStatus();
    }

    StorageReference(@NonNull Uri uri, @NonNull FirebaseStorage firebaseStorage) {
        boolean z = true;
        zzac.zzb(uri != null, (Object) "storageUri cannot be null");
        if (firebaseStorage == null) {
            z = false;
        }
        zzac.zzb(z, (Object) "FirebaseApp cannot be null");
        this.zzckP = uri;
        this.zzckQ = firebaseStorage;
    }

    @NonNull
    public StorageReference child(@NonNull String str) {
        zzac.zzb(!TextUtils.isEmpty(str), (Object) "childName cannot be null or empty");
        String zzjL = zzbqx.zzjL(str);
        try {
            return new StorageReference(this.zzckP.buildUpon().appendEncodedPath(zzbqx.zzjJ(zzjL)).build(), this.zzckQ);
        } catch (Throwable e) {
            Throwable th = e;
            String str2 = "StorageReference";
            String str3 = "Unable to create a valid default Uri. ";
            String valueOf = String.valueOf(zzjL);
            Log.e(str2, valueOf.length() != 0 ? str3.concat(valueOf) : new String(str3), th);
            throw new IllegalArgumentException("childName");
        }
    }

    public Task<Void> delete() {
        TaskCompletionSource taskCompletionSource = new TaskCompletionSource();
        zzd.zzaaW().zzt(new zza(this, taskCompletionSource));
        return taskCompletionSource.getTask();
    }

    public boolean equals(Object obj) {
        return !(obj instanceof StorageReference) ? false : ((StorageReference) obj).toString().equals(toString());
    }

    @NonNull
    public List<FileDownloadTask> getActiveDownloadTasks() {
        return zzc.zzaaV().zzb(this);
    }

    @NonNull
    public List<UploadTask> getActiveUploadTasks() {
        return zzc.zzaaV().zza(this);
    }

    @NonNull
    FirebaseApp getApp() {
        return getStorage().getApp();
    }

    @NonNull
    public String getBucket() {
        return this.zzckP.getAuthority();
    }

    @NonNull
    public Task<byte[]> getBytes(long j) {
        TaskCompletionSource taskCompletionSource = new TaskCompletionSource();
        StreamDownloadTask streamDownloadTask = new StreamDownloadTask(this);
        ((StorageTask) streamDownloadTask.zza(new C12235(this, j, taskCompletionSource)).addOnSuccessListener(new C12224(this, taskCompletionSource))).addOnFailureListener(new C12213(this, taskCompletionSource));
        streamDownloadTask.zzaaP();
        return taskCompletionSource.getTask();
    }

    @NonNull
    public Task<Uri> getDownloadUrl() {
        TaskCompletionSource taskCompletionSource = new TaskCompletionSource();
        Task metadata = getMetadata();
        metadata.addOnSuccessListener(new C12191(this, taskCompletionSource));
        metadata.addOnFailureListener(new C12202(this, taskCompletionSource));
        return taskCompletionSource.getTask();
    }

    @NonNull
    public FileDownloadTask getFile(@NonNull Uri uri) {
        FileDownloadTask fileDownloadTask = new FileDownloadTask(this, uri);
        fileDownloadTask.zzaaP();
        return fileDownloadTask;
    }

    @NonNull
    public FileDownloadTask getFile(@NonNull File file) {
        return getFile(Uri.fromFile(file));
    }

    @NonNull
    public Task<StorageMetadata> getMetadata() {
        TaskCompletionSource taskCompletionSource = new TaskCompletionSource();
        zzd.zzaaW().zzt(new zzb(this, taskCompletionSource));
        return taskCompletionSource.getTask();
    }

    @NonNull
    public String getName() {
        String path = this.zzckP.getPath();
        if ($assertionsDisabled || path != null) {
            int lastIndexOf = path.lastIndexOf(47);
            return lastIndexOf != -1 ? path.substring(lastIndexOf + 1) : path;
        } else {
            throw new AssertionError();
        }
    }

    @Nullable
    public StorageReference getParent() {
        String path = this.zzckP.getPath();
        if (path == null || path.equals("/")) {
            return null;
        }
        int lastIndexOf = path.lastIndexOf(47);
        return new StorageReference(this.zzckP.buildUpon().path(lastIndexOf == -1 ? "/" : path.substring(0, lastIndexOf)).build(), this.zzckQ);
    }

    @NonNull
    public String getPath() {
        String path = this.zzckP.getPath();
        if ($assertionsDisabled || path != null) {
            return path;
        }
        throw new AssertionError();
    }

    @NonNull
    public StorageReference getRoot() {
        return new StorageReference(this.zzckP.buildUpon().path(BuildConfig.VERSION_NAME).build(), this.zzckQ);
    }

    @NonNull
    public FirebaseStorage getStorage() {
        return this.zzckQ;
    }

    @NonNull
    public StreamDownloadTask getStream() {
        StreamDownloadTask streamDownloadTask = new StreamDownloadTask(this);
        streamDownloadTask.zzaaP();
        return streamDownloadTask;
    }

    @NonNull
    public StreamDownloadTask getStream(@NonNull StreamProcessor streamProcessor) {
        StreamDownloadTask streamDownloadTask = new StreamDownloadTask(this);
        streamDownloadTask.zza(streamProcessor);
        streamDownloadTask.zzaaP();
        return streamDownloadTask;
    }

    public int hashCode() {
        return toString().hashCode();
    }

    @NonNull
    public UploadTask putBytes(@NonNull byte[] bArr) {
        zzac.zzb(bArr != null, (Object) "bytes cannot be null");
        UploadTask uploadTask = new UploadTask(this, null, bArr);
        uploadTask.zzaaP();
        return uploadTask;
    }

    @NonNull
    public UploadTask putBytes(@NonNull byte[] bArr, @NonNull StorageMetadata storageMetadata) {
        boolean z = true;
        zzac.zzb(bArr != null, (Object) "bytes cannot be null");
        if (storageMetadata == null) {
            z = false;
        }
        zzac.zzb(z, (Object) "metadata cannot be null");
        UploadTask uploadTask = new UploadTask(this, storageMetadata, bArr);
        uploadTask.zzaaP();
        return uploadTask;
    }

    @NonNull
    public UploadTask putFile(@NonNull Uri uri) {
        zzac.zzb(uri != null, (Object) "uri cannot be null");
        UploadTask uploadTask = new UploadTask(this, null, uri, null);
        uploadTask.zzaaP();
        return uploadTask;
    }

    @NonNull
    public UploadTask putFile(@NonNull Uri uri, @NonNull StorageMetadata storageMetadata) {
        boolean z = true;
        zzac.zzb(uri != null, (Object) "uri cannot be null");
        if (storageMetadata == null) {
            z = false;
        }
        zzac.zzb(z, (Object) "metadata cannot be null");
        UploadTask uploadTask = new UploadTask(this, storageMetadata, uri, null);
        uploadTask.zzaaP();
        return uploadTask;
    }

    @NonNull
    public UploadTask putFile(@NonNull Uri uri, @Nullable StorageMetadata storageMetadata, @Nullable Uri uri2) {
        boolean z = true;
        zzac.zzb(uri != null, (Object) "uri cannot be null");
        if (storageMetadata == null) {
            z = false;
        }
        zzac.zzb(z, (Object) "metadata cannot be null");
        UploadTask uploadTask = new UploadTask(this, storageMetadata, uri, uri2);
        uploadTask.zzaaP();
        return uploadTask;
    }

    @NonNull
    public UploadTask putStream(@NonNull InputStream inputStream) {
        zzac.zzb(inputStream != null, (Object) "stream cannot be null");
        UploadTask uploadTask = new UploadTask(this, null, inputStream);
        uploadTask.zzaaP();
        return uploadTask;
    }

    @NonNull
    public UploadTask putStream(@NonNull InputStream inputStream, @NonNull StorageMetadata storageMetadata) {
        boolean z = true;
        zzac.zzb(inputStream != null, (Object) "stream cannot be null");
        if (storageMetadata == null) {
            z = false;
        }
        zzac.zzb(z, (Object) "metadata cannot be null");
        UploadTask uploadTask = new UploadTask(this, storageMetadata, inputStream);
        uploadTask.zzaaP();
        return uploadTask;
    }

    public String toString() {
        String valueOf = String.valueOf(this.zzckP.getAuthority());
        String valueOf2 = String.valueOf(this.zzckP.getPath());
        return new StringBuilder((String.valueOf(valueOf).length() + 5) + String.valueOf(valueOf2).length()).append("gs://").append(valueOf).append(valueOf2).toString();
    }

    @NonNull
    public Task<StorageMetadata> updateMetadata(@NonNull StorageMetadata storageMetadata) {
        zzac.zzw(storageMetadata);
        TaskCompletionSource taskCompletionSource = new TaskCompletionSource();
        zzd.zzaaW().zzt(new zzf(this, taskCompletionSource, storageMetadata));
        return taskCompletionSource.getTask();
    }

    @NonNull
    zzbre zzaaN() throws RemoteException {
        return zzbre.zzj(getApp());
    }

    @NonNull
    Uri zzaaO() {
        return this.zzckP;
    }
}
