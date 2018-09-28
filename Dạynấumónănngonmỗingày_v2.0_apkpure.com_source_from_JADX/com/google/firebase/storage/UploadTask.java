package com.google.firebase.storage;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.TransportMediator;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.internal.zzbqw;
import com.google.android.gms.internal.zzbrb;
import com.google.android.gms.internal.zzbre;
import com.google.android.gms.internal.zzbrf;
import com.google.firebase.storage.StorageMetadata.Builder;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.http.HttpStatus;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

public class UploadTask extends StorageTask<TaskSnapshot> {
    private volatile int mResultCode;
    private final Uri mUri;
    private volatile Exception zzbLK;
    private final byte[] zzbyp;
    private volatile StorageMetadata zzckN;
    private final StorageReference zzcki;
    private zzbqw zzckk;
    private final byte[] zzclJ;
    private final long zzclK;
    private final AtomicLong zzclL;
    private InputStream zzclM;
    private boolean zzclN;
    private volatile Uri zzclO;
    private volatile Exception zzclP;
    private volatile String zzclQ;

    /* renamed from: com.google.firebase.storage.UploadTask.1 */
    class C07271 implements Runnable {
        final /* synthetic */ zzbrf zzclR;
        final /* synthetic */ UploadTask zzclS;

        C07271(UploadTask uploadTask, zzbrf com_google_android_gms_internal_zzbrf) {
            this.zzclS = uploadTask;
            this.zzclR = com_google_android_gms_internal_zzbrf;
        }

        public void run() {
            this.zzclR.zza(zzbrb.zzi(this.zzclS.zzcki.getApp()), this.zzclS.zzcki.getApp().getApplicationContext());
        }
    }

    public class TaskSnapshot extends SnapshotBase {
        private final StorageMetadata zzckN;
        private final Uri zzclO;
        final /* synthetic */ UploadTask zzclS;
        private final long zzclT;

        TaskSnapshot(UploadTask uploadTask, Exception exception, long j, Uri uri, StorageMetadata storageMetadata) {
            this.zzclS = uploadTask;
            super(uploadTask, exception);
            this.zzclT = j;
            this.zzclO = uri;
            this.zzckN = storageMetadata;
        }

        public long getBytesTransferred() {
            return this.zzclT;
        }

        @Nullable
        public Uri getDownloadUrl() {
            StorageMetadata metadata = getMetadata();
            return metadata != null ? metadata.getDownloadUrl() : null;
        }

        @Nullable
        public /* bridge */ /* synthetic */ Exception getError() {
            return super.getError();
        }

        @Nullable
        public StorageMetadata getMetadata() {
            return this.zzckN;
        }

        @NonNull
        public /* bridge */ /* synthetic */ StorageReference getStorage() {
            return super.getStorage();
        }

        @NonNull
        public /* bridge */ /* synthetic */ StorageTask getTask() {
            return super.getTask();
        }

        public long getTotalByteCount() {
            return this.zzclS.getTotalByteCount();
        }

        @Nullable
        public Uri getUploadSessionUri() {
            return this.zzclO;
        }
    }

    UploadTask(StorageReference storageReference, StorageMetadata storageMetadata, Uri uri, Uri uri2) {
        long j;
        InputStream inputStream;
        Exception exception;
        String str;
        String str2;
        String valueOf;
        long j2;
        InputStream inputStream2;
        this.zzclJ = new byte[AccessibilityNodeInfoCompat.ACTION_EXPAND];
        this.zzclL = new AtomicLong(0);
        this.zzclO = null;
        this.zzbLK = null;
        this.zzclP = null;
        this.mResultCode = 0;
        zzac.zzw(storageReference);
        zzac.zzw(uri);
        this.zzbyp = null;
        this.zzcki = storageReference;
        this.zzckN = storageMetadata;
        this.mUri = uri;
        this.zzckk = new zzbqw(this.zzcki.getApp(), this.zzcki.getStorage().getMaxUploadRetryTimeMillis());
        long j3 = -1;
        try {
            ContentResolver contentResolver = this.zzcki.getStorage().getApp().getApplicationContext().getContentResolver();
            try {
                ParcelFileDescriptor openFileDescriptor = contentResolver.openFileDescriptor(this.mUri, "r");
                if (openFileDescriptor != null) {
                    j3 = openFileDescriptor.getStatSize();
                    openFileDescriptor.close();
                }
            } catch (Throwable e) {
                Throwable th = e;
                j = -1;
                try {
                    Log.w("UploadTask", "NullPointerException during file size calculation.", th);
                    j3 = -1;
                } catch (Exception e2) {
                    inputStream = null;
                    exception = e2;
                    str = "UploadTask";
                    str2 = "could not locate file for uploading:";
                    valueOf = String.valueOf(this.mUri.toString());
                    Log.e(str, valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2));
                    this.zzbLK = exception;
                    j2 = j;
                    inputStream2 = inputStream;
                    j3 = j2;
                    this.zzclK = j3;
                    this.zzclM = inputStream2;
                    this.zzclN = true;
                    this.zzclO = uri2;
                }
            } catch (IOException e3) {
                String str3 = "UploadTask";
                str2 = "could not retrieve file size for upload ";
                String valueOf2 = String.valueOf(this.mUri.toString());
                Log.w(str3, valueOf2.length() != 0 ? str2.concat(valueOf2) : new String(str2));
            }
            InputStream openInputStream = contentResolver.openInputStream(this.mUri);
            if (openInputStream != null) {
                if (j3 == -1) {
                    try {
                        int available = openInputStream.available();
                        if (available > 0) {
                            j3 = (long) available;
                        }
                    } catch (IOException e4) {
                    }
                }
                try {
                    inputStream2 = new BufferedInputStream(openInputStream);
                } catch (Exception e5) {
                    exception = e5;
                    inputStream = openInputStream;
                    j = j3;
                    str = "UploadTask";
                    str2 = "could not locate file for uploading:";
                    valueOf = String.valueOf(this.mUri.toString());
                    if (valueOf.length() != 0) {
                    }
                    Log.e(str, valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2));
                    this.zzbLK = exception;
                    j2 = j;
                    inputStream2 = inputStream;
                    j3 = j2;
                    this.zzclK = j3;
                    this.zzclM = inputStream2;
                    this.zzclN = true;
                    this.zzclO = uri2;
                }
            }
            inputStream2 = openInputStream;
        } catch (Exception e52) {
            Exception exception2 = e52;
            j = j3;
            inputStream = null;
            exception = exception2;
            str = "UploadTask";
            str2 = "could not locate file for uploading:";
            valueOf = String.valueOf(this.mUri.toString());
            if (valueOf.length() != 0) {
            }
            Log.e(str, valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2));
            this.zzbLK = exception;
            j2 = j;
            inputStream2 = inputStream;
            j3 = j2;
            this.zzclK = j3;
            this.zzclM = inputStream2;
            this.zzclN = true;
            this.zzclO = uri2;
        }
        this.zzclK = j3;
        this.zzclM = inputStream2;
        this.zzclN = true;
        this.zzclO = uri2;
    }

    UploadTask(StorageReference storageReference, StorageMetadata storageMetadata, InputStream inputStream) {
        this.zzclJ = new byte[AccessibilityNodeInfoCompat.ACTION_EXPAND];
        this.zzclL = new AtomicLong(0);
        this.zzclO = null;
        this.zzbLK = null;
        this.zzclP = null;
        this.mResultCode = 0;
        zzac.zzw(storageReference);
        zzac.zzw(inputStream);
        this.zzclK = -1;
        this.zzbyp = null;
        this.zzcki = storageReference;
        this.zzckN = storageMetadata;
        this.zzclM = new BufferedInputStream(inputStream, AccessibilityNodeInfoCompat.ACTION_EXPAND);
        this.zzclN = false;
        this.mUri = null;
        this.zzckk = new zzbqw(this.zzcki.getApp(), this.zzcki.getStorage().getMaxUploadRetryTimeMillis());
    }

    UploadTask(StorageReference storageReference, StorageMetadata storageMetadata, byte[] bArr) {
        this.zzclJ = new byte[AccessibilityNodeInfoCompat.ACTION_EXPAND];
        this.zzclL = new AtomicLong(0);
        this.zzclO = null;
        this.zzbLK = null;
        this.zzclP = null;
        this.mResultCode = 0;
        zzac.zzw(storageReference);
        zzac.zzw(bArr);
        this.zzbyp = bArr;
        this.zzclK = (long) this.zzbyp.length;
        this.zzcki = storageReference;
        this.zzckN = storageMetadata;
        this.mUri = null;
        this.zzclM = new BufferedInputStream(new ByteArrayInputStream(this.zzbyp), AccessibilityNodeInfoCompat.ACTION_EXPAND);
        this.zzclN = true;
        this.zzckk = new zzbqw(this.zzcki.getApp(), this.zzcki.getStorage().getMaxUploadRetryTimeMillis());
    }

    private boolean zza(zzbrf com_google_android_gms_internal_zzbrf) {
        com_google_android_gms_internal_zzbrf.zza(zzbrb.zzi(this.zzcki.getApp()), this.zzcki.getApp().getApplicationContext());
        return zzc(com_google_android_gms_internal_zzbrf);
    }

    private void zzaba() {
        Throwable e;
        JSONObject jSONObject = null;
        if (this.zzckN != null) {
            String contentType = this.zzckN.getContentType();
        } else {
            CharSequence charSequence = null;
        }
        if (this.mUri != null && TextUtils.isEmpty(r0)) {
            contentType = this.zzcki.getStorage().getApp().getApplicationContext().getContentResolver().getType(this.mUri);
        }
        if (TextUtils.isEmpty(contentType)) {
            contentType = HTTP.OCTET_STREAM_TYPE;
        }
        try {
            zzbre zzaaN = this.zzcki.zzaaN();
            Uri zzaaO = this.zzcki.zzaaO();
            if (this.zzckN != null) {
                jSONObject = this.zzckN.zzaaM();
            }
            zzbrf zza = zzaaN.zza(zzaaO, jSONObject, contentType);
            if (zzb(zza)) {
                Object zzjP = zza.zzjP("X-Goog-Upload-URL");
                if (!TextUtils.isEmpty(zzjP)) {
                    this.zzclO = Uri.parse(zzjP);
                }
            }
        } catch (JSONException e2) {
            e = e2;
            Log.e("UploadTask", "Unable to create a network request from metadata", e);
            this.zzbLK = e;
        } catch (RemoteException e3) {
            e = e3;
            Log.e("UploadTask", "Unable to create a network request from metadata", e);
            this.zzbLK = e;
        }
    }

    private boolean zzabb() {
        if (zzaaQ() == TransportMediator.FLAG_KEY_MEDIA_NEXT) {
            return false;
        }
        if (Thread.interrupted()) {
            this.zzbLK = new InterruptedException();
            zzf(64, false);
            return false;
        } else if (zzaaQ() == 32) {
            zzf(AccessibilityNodeInfoCompat.ACTION_NEXT_AT_MOVEMENT_GRANULARITY, false);
            return false;
        } else if (zzaaQ() == 8) {
            zzf(16, false);
            return false;
        } else if (!zzabc()) {
            return false;
        } else {
            if (this.zzclO == null) {
                if (this.zzbLK == null) {
                    this.zzbLK = new IllegalStateException("Unable to obtain an upload URL.");
                }
                zzf(64, false);
                return false;
            } else if (this.zzbLK != null) {
                zzf(64, false);
                return false;
            } else {
                boolean z = this.zzclP != null || this.mResultCode < HttpStatus.SC_OK || this.mResultCode >= HttpStatus.SC_MULTIPLE_CHOICES;
                if (!z || zzbd(true)) {
                    return true;
                }
                if (!zzabc()) {
                    return false;
                }
                zzf(64, false);
                return false;
            }
        }
    }

    private boolean zzabc() {
        if (!"final".equals(this.zzclQ)) {
            return true;
        }
        if (this.zzbLK == null) {
            this.zzbLK = new IOException("The server has terminated the upload session");
        }
        zzf(64, false);
        return false;
    }

    private void zzabd() {
        Throwable e;
        String str;
        String str2;
        String valueOf;
        this.zzclM.mark(this.zzclJ.length + 1);
        try {
            int read = this.zzclM.read(this.zzclJ);
            try {
                zzbrf zza = this.zzcki.zzaaN().zza(this.zzcki.zzaaO(), this.zzclO.toString(), this.zzclJ, this.zzclL.get(), read, ((long) read) != 262144);
                if (zza(zza)) {
                    if (read != -1) {
                        this.zzclL.getAndAdd((long) read);
                    }
                    if (((long) read) != 262144) {
                        try {
                            this.zzckN = new Builder(zza.zzabq(), this.zzcki).build();
                            zzf(4, false);
                            zzf(TransportMediator.FLAG_KEY_MEDIA_NEXT, false);
                            return;
                        } catch (JSONException e2) {
                            e = e2;
                            str = "UploadTask";
                            str2 = "Unable to parse resulting metadata from upload:";
                            valueOf = String.valueOf(zza.zzabk());
                            Log.e(str, valueOf.length() == 0 ? str2.concat(valueOf) : new String(str2), e);
                            this.zzbLK = e;
                            return;
                        } catch (RemoteException e3) {
                            e = e3;
                            str = "UploadTask";
                            str2 = "Unable to parse resulting metadata from upload:";
                            valueOf = String.valueOf(zza.zzabk());
                            if (valueOf.length() == 0) {
                            }
                            Log.e(str, valueOf.length() == 0 ? str2.concat(valueOf) : new String(str2), e);
                            this.zzbLK = e;
                            return;
                        }
                    }
                    return;
                }
                try {
                    this.zzclM.reset();
                } catch (Throwable e4) {
                    Log.w("UploadTask", "Unable to reset the stream for error recovery.", e4);
                    this.zzbLK = e4;
                }
            } catch (Throwable e42) {
                Log.e("UploadTask", "Unable to create chunk upload request", e42);
                this.zzbLK = e42;
            }
        } catch (Throwable e422) {
            Log.e("UploadTask", "Unable to read bytes for uploading", e422);
            this.zzbLK = e422;
        }
    }

    private boolean zzb(zzbrf com_google_android_gms_internal_zzbrf) {
        this.zzckk.zzd(com_google_android_gms_internal_zzbrf);
        return zzc(com_google_android_gms_internal_zzbrf);
    }

    private boolean zzbd(boolean z) {
        try {
            zzbrf zzb = this.zzcki.zzaaN().zzb(this.zzcki.zzaaO(), this.zzclO.toString());
            if ("final".equals(this.zzclQ)) {
                return false;
            }
            if (z) {
                if (!zzb(zzb)) {
                    return false;
                }
            } else if (!zza(zzb)) {
                return false;
            }
            if ("final".equals(zzb.zzjP("X-Goog-Upload-Status"))) {
                this.zzbLK = new IOException("The server has terminated the upload session");
                return false;
            }
            Object zzjP = zzb.zzjP("X-Goog-Upload-Size-Received");
            long parseLong = !TextUtils.isEmpty(zzjP) ? Long.parseLong(zzjP) : 0;
            long j = this.zzclL.get();
            if (j > parseLong) {
                this.zzbLK = new IOException("Unexpected error. The server lost a chunk update.");
                return false;
            }
            if (j < parseLong) {
                try {
                    if (this.zzclM.skip(parseLong - j) != parseLong - j) {
                        this.zzbLK = new IOException("Unexpected end of stream encountered.");
                        return false;
                    } else if (!this.zzclL.compareAndSet(j, parseLong)) {
                        Log.e("UploadTask", "Somehow, the uploaded bytes changed during an uploaded.  This should nothappen");
                        this.zzbLK = new IllegalStateException("uploaded bytes changed unexpectedly.");
                        return false;
                    }
                } catch (Throwable e) {
                    Log.e("UploadTask", "Unable to recover position in Stream during resumable upload", e);
                    this.zzbLK = e;
                    return false;
                }
            }
            return true;
        } catch (Throwable e2) {
            Log.e("UploadTask", "Unable to recover status during resumable upload", e2);
            this.zzbLK = e2;
            return false;
        }
    }

    private boolean zzc(zzbrf com_google_android_gms_internal_zzbrf) {
        int resultCode = com_google_android_gms_internal_zzbrf.getResultCode();
        if (this.zzckk.zzqa(resultCode)) {
            resultCode = -2;
        }
        this.mResultCode = resultCode;
        this.zzclP = com_google_android_gms_internal_zzbrf.getException();
        this.zzclQ = com_google_android_gms_internal_zzbrf.zzjP("X-Goog-Upload-Status");
        return zzpW(this.mResultCode) && this.zzclP == null;
    }

    private boolean zzpW(int i) {
        return i == 308 || (i >= HttpStatus.SC_OK && i < HttpStatus.SC_MULTIPLE_CHOICES);
    }

    StorageReference getStorage() {
        return this.zzcki;
    }

    long getTotalByteCount() {
        return this.zzclK;
    }

    protected void onCanceled() {
        zzbrf zza;
        this.zzckk.cancel();
        if (this.zzclO != null) {
            try {
                zza = this.zzcki.zzaaN().zza(this.zzcki.zzaaO(), this.zzclO.toString());
            } catch (Throwable e) {
                Log.e("UploadTask", "Unable to create chunk upload request", e);
            }
            if (zza != null) {
                zzd.zzaaW().zzt(new C07271(this, zza));
            }
            this.zzbLK = StorageException.fromErrorStatus(Status.zzayl);
            super.onCanceled();
        }
        zza = null;
        if (zza != null) {
            zzd.zzaaW().zzt(new C07271(this, zza));
        }
        this.zzbLK = StorageException.fromErrorStatus(Status.zzayl);
        super.onCanceled();
    }

    protected void resetState() {
        this.zzbLK = null;
        this.zzclP = null;
        this.mResultCode = 0;
        this.zzclQ = null;
    }

    void run() {
        this.zzckk.reset();
        if (zzf(4, false)) {
            if (this.zzcki.getParent() == null) {
                this.zzbLK = new IllegalArgumentException("Cannot upload to getRoot. You should upload to a storage location such as .getReference('image.png').putFile...");
            }
            if (this.zzbLK == null) {
                if (this.zzclO == null) {
                    zzaba();
                } else {
                    zzbd(false);
                }
                boolean zzabb = zzabb();
                while (zzabb) {
                    zzabd();
                    zzabb = zzabb();
                    if (zzabb) {
                        zzf(4, false);
                    }
                }
                if (this.zzclN) {
                    try {
                        this.zzclM.close();
                        return;
                    } catch (Throwable e) {
                        Log.e("UploadTask", "Unable to close stream.", e);
                        return;
                    }
                }
                return;
            }
            return;
        }
        Log.d("UploadTask", "The upload cannot continue as it is not in a valid state.");
    }

    protected void schedule() {
        zzd.zzaaW().zzu(zzTj());
    }

    @NonNull
    /* synthetic */ ProvideError zzaaK() {
        return zzabe();
    }

    @NonNull
    TaskSnapshot zzabe() {
        return new TaskSnapshot(this, StorageException.fromExceptionAndHttpCode(this.zzbLK != null ? this.zzbLK : this.zzclP, this.mResultCode), this.zzclL.get(), this.zzclO, this.zzckN);
    }
}
