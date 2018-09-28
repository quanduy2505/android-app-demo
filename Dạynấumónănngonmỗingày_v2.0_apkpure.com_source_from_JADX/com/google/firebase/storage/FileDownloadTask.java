package com.google.firebase.storage;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.TransportMediator;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.internal.zzbqw;
import com.google.android.gms.internal.zzbrf;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;

public class FileDownloadTask extends StorageTask<TaskSnapshot> {
    private int mResultCode;
    private long zzaKG;
    private volatile Exception zzbLK;
    private StorageReference zzcki;
    private zzbqw zzckk;
    private final Uri zzckl;
    private long zzckm;
    private String zzckn;
    private long zzcko;

    public class TaskSnapshot extends SnapshotBase {
        private final long zzckm;
        final /* synthetic */ FileDownloadTask zzckp;

        TaskSnapshot(FileDownloadTask fileDownloadTask, Exception exception, long j) {
            this.zzckp = fileDownloadTask;
            super(fileDownloadTask, exception);
            this.zzckm = j;
        }

        public long getBytesTransferred() {
            return this.zzckm;
        }

        @Nullable
        public /* bridge */ /* synthetic */ Exception getError() {
            return super.getError();
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
            return this.zzckp.getTotalBytes();
        }
    }

    FileDownloadTask(@NonNull StorageReference storageReference, @NonNull Uri uri) {
        this.zzaKG = -1;
        this.zzckn = null;
        this.zzbLK = null;
        this.zzcko = 0;
        this.zzcki = storageReference;
        this.zzckl = uri;
        this.zzckk = new zzbqw(this.zzcki.getStorage().getApp(), this.zzcki.getStorage().getMaxDownloadRetryTimeMillis());
    }

    private boolean zzpW(int i) {
        return i == 308 || (i >= HttpStatus.SC_OK && i < HttpStatus.SC_MULTIPLE_CHOICES);
    }

    @NonNull
    StorageReference getStorage() {
        return this.zzcki;
    }

    long getTotalBytes() {
        return this.zzaKG;
    }

    protected void onCanceled() {
        this.zzckk.cancel();
    }

    void run() {
        if (zzf(4, false)) {
            this.zzckk.reset();
            try {
                Object zzjP;
                zzbrf zza = this.zzcki.zzaaN().zza(this.zzcki.zzaaO(), this.zzcko);
                this.zzckk.zza(zza, false);
                this.mResultCode = zza.getResultCode();
                this.zzbLK = zza.getException() != null ? zza.getException() : this.zzbLK;
                Object obj = (zzpW(this.mResultCode) && this.zzbLK == null && zzaaQ() == 4) ? 1 : null;
                if (obj != null) {
                    this.zzaKG = (long) zza.zzabo();
                    zzjP = zza.zzjP(HttpHeaders.ETAG);
                    if (TextUtils.isEmpty(zzjP) || this.zzckn == null || this.zzckn.equals(zzjP)) {
                        this.zzckn = zzjP;
                        InputStream stream = zza.getStream();
                        if (stream != null) {
                            try {
                                String str;
                                String valueOf;
                                String str2;
                                OutputStream fileOutputStream;
                                File file = new File(this.zzckl.getPath());
                                if (!file.exists()) {
                                    if (this.zzcko > 0) {
                                        String str3 = "FileDownloadTask";
                                        str = "The file downloading to has been deleted:";
                                        valueOf = String.valueOf(file.getAbsolutePath());
                                        Log.e(str3, valueOf.length() != 0 ? str.concat(valueOf) : new String(str));
                                        throw new IllegalStateException("expected a file to resume from.");
                                    } else if (!file.createNewFile()) {
                                        str = "FileDownloadTask";
                                        str2 = "unable to create file:";
                                        valueOf = String.valueOf(file.getAbsolutePath());
                                        Log.w(str, valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2));
                                    }
                                }
                                if (this.zzcko > 0) {
                                    str = "FileDownloadTask";
                                    str2 = "Resuming download file ";
                                    valueOf = String.valueOf(file.getAbsolutePath());
                                    Log.d(str, valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2));
                                    fileOutputStream = new FileOutputStream(file, true);
                                } else {
                                    fileOutputStream = new FileOutputStream(file);
                                }
                                byte[] bArr = new byte[AccessibilityNodeInfoCompat.ACTION_EXPAND];
                                do {
                                    int read = stream.read(bArr);
                                    if (read == -1) {
                                        break;
                                    }
                                    fileOutputStream.write(bArr, 0, read);
                                    this.zzckm += (long) read;
                                } while (zzf(4, false));
                                fileOutputStream.flush();
                                fileOutputStream.close();
                                stream.close();
                            } catch (Throwable e) {
                                Log.e("FileDownloadTask", "Exception occurred during file download", e);
                                this.zzbLK = e;
                            }
                        } else {
                            this.zzbLK = new IllegalStateException("Unable to open Firebase Storage stream.");
                        }
                    } else {
                        Log.w("FileDownloadTask", "The file at the server has changed.  Restarting from the beginning.");
                        this.zzcko = 0;
                        this.zzckn = null;
                        zza.zzabh();
                        schedule();
                        return;
                    }
                }
                zza.zzabh();
                zzjP = (obj != null && this.zzbLK == null && zzaaQ() == 4) ? 1 : null;
                if (zzjP != null) {
                    zzf(TransportMediator.FLAG_KEY_MEDIA_NEXT, false);
                    return;
                }
                File file2 = new File(this.zzckl.getPath());
                if (file2.exists()) {
                    this.zzcko = file2.length();
                } else {
                    this.zzcko = 0;
                }
                if (zzaaQ() == 8) {
                    zzf(16, false);
                    return;
                }
                if (!zzf(zzaaQ() == 32 ? AccessibilityNodeInfoCompat.ACTION_NEXT_AT_MOVEMENT_GRANULARITY : 64, false)) {
                    Log.w("FileDownloadTask", "Unable to change download task to final state from " + zzaaQ());
                }
            } catch (Throwable e2) {
                Log.e("FileDownloadTask", "Unable to create firebase storage network request.", e2);
                this.zzbLK = e2;
                zzf(64, false);
            }
        }
    }

    protected void schedule() {
        zzd.zzaaW().zzv(zzTj());
    }

    @NonNull
    TaskSnapshot zzaaJ() {
        return new TaskSnapshot(this, StorageException.fromExceptionAndHttpCode(this.zzbLK, this.mResultCode), this.zzckm);
    }

    @NonNull
    /* synthetic */ ProvideError zzaaK() {
        return zzaaJ();
    }
}
