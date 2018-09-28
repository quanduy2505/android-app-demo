package com.google.firebase.storage;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.TransportMediator;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.util.Log;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.internal.zzbqw;
import com.google.android.gms.internal.zzbrf;
import java.io.IOException;
import java.io.InputStream;
import org.apache.http.HttpStatus;

public class StreamDownloadTask extends StorageTask<TaskSnapshot> {
    private volatile int mResultCode;
    private long zzaKG;
    private volatile Exception zzbLK;
    private InputStream zzbSc;
    private StorageReference zzcki;
    private zzbqw zzckk;
    private long zzckm;
    private StreamProcessor zzcls;
    private long zzclt;
    private zzbrf zzclu;

    public interface StreamProcessor {
        void doInBackground(TaskSnapshot taskSnapshot, InputStream inputStream) throws IOException;
    }

    private static class zza extends InputStream {
        private StreamDownloadTask zzclv;
        private InputStream zzclw;
        private int zzclx;

        public zza(@NonNull StreamDownloadTask streamDownloadTask, @NonNull InputStream inputStream) {
            this.zzclv = streamDownloadTask;
            this.zzclw = inputStream;
        }

        private void zzaaY() throws IOException {
            if (this.zzclv.zzaaQ() == 32) {
                throw StorageException.zzckv;
            }
        }

        public int available() throws IOException {
            zzaaY();
            return this.zzclw.available();
        }

        public void close() throws IOException {
            this.zzclw.close();
            if (this.zzclv.zzclu != null) {
                this.zzclv.zzclu.zzabh();
            }
            zzaaY();
        }

        public void mark(int i) {
            this.zzclx = i;
            this.zzclw.mark(i);
        }

        public boolean markSupported() {
            return this.zzclw.markSupported();
        }

        public int read() throws IOException {
            zzaaY();
            int read = this.zzclw.read();
            if (read != -1) {
                this.zzclv.zzaT(1);
            }
            return read;
        }

        public int read(@NonNull byte[] bArr, int i, int i2) throws IOException {
            zzaaY();
            int i3 = 0;
            int i4 = i2;
            int i5 = i;
            while (((long) i4) > 262144) {
                int read = this.zzclw.read(bArr, i5, AccessibilityNodeInfoCompat.ACTION_EXPAND);
                if (read != -1) {
                    i3 += read;
                    i5 += read;
                    i4 -= read;
                    this.zzclv.zzaT((long) read);
                    zzaaY();
                    if (((long) read) < 262144) {
                        break;
                    }
                }
                return i3 == 0 ? -1 : i3;
            }
            if (i4 > 0) {
                i4 = this.zzclw.read(bArr, i5, i4);
                if (i4 == -1) {
                    return i3 != 0 ? i3 : -1;
                } else {
                    i3 += i4;
                    this.zzclv.zzaT((long) i4);
                }
            }
            return i3;
        }

        public synchronized void reset() throws IOException {
            zzaaY();
            this.zzclv.zzaT((long) (-this.zzclx));
            this.zzclw.reset();
        }

        public long skip(long j) throws IOException {
            long skip;
            zzaaY();
            int i = 0;
            while (j > 262144) {
                skip = this.zzclw.skip(262144);
                i = (int) (((long) i) + skip);
                if (skip < 262144) {
                    this.zzclv.zzaT(skip);
                    return (long) i;
                }
                this.zzclv.zzaT(262144);
                j -= 262144;
                zzaaY();
            }
            skip = this.zzclw.skip(j);
            i = (int) (((long) i) + skip);
            this.zzclv.zzaT(skip);
            return (long) i;
        }
    }

    public class TaskSnapshot extends SnapshotBase {
        private final long zzckm;
        final /* synthetic */ StreamDownloadTask zzcly;

        TaskSnapshot(StreamDownloadTask streamDownloadTask, Exception exception, long j) {
            this.zzcly = streamDownloadTask;
            super(streamDownloadTask, exception);
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

        public InputStream getStream() {
            return this.zzcly.zzbSc;
        }

        @NonNull
        public /* bridge */ /* synthetic */ StorageTask getTask() {
            return super.getTask();
        }

        public long getTotalByteCount() {
            return this.zzcly.getTotalBytes();
        }
    }

    StreamDownloadTask(@NonNull StorageReference storageReference) {
        this.zzbLK = null;
        this.mResultCode = 0;
        this.zzcki = storageReference;
        this.zzckk = new zzbqw(this.zzcki.getApp(), this.zzcki.getStorage().getMaxDownloadRetryTimeMillis());
    }

    private void zzaT(long j) {
        this.zzckm += j;
        if (this.zzclt + 262144 <= this.zzckm) {
            zzf(4, false);
        }
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

    protected void onProgress() {
        this.zzclt = this.zzckm;
    }

    public boolean pause() {
        throw new UnsupportedOperationException("this operation is not supported on StreamDownloadTask.");
    }

    public boolean resume() {
        throw new UnsupportedOperationException("this operation is not supported on StreamDownloadTask.");
    }

    void run() {
        this.zzckk.reset();
        if (this.zzbLK != null) {
            zzf(64, false);
        } else if (zzf(4, false)) {
            try {
                this.zzclu = this.zzcki.zzaaN().zza(this.zzcki.zzaaO(), 0);
                this.zzckk.zza(this.zzclu, false);
                this.mResultCode = this.zzclu.getResultCode();
                this.zzbLK = this.zzclu.getException() != null ? this.zzclu.getException() : this.zzbLK;
                boolean z = zzpW(this.mResultCode) && this.zzbLK == null && zzaaQ() == 4;
                if (z) {
                    this.zzaKG = (long) this.zzclu.zzabo();
                    InputStream stream = this.zzclu.getStream();
                    if (stream != null) {
                        this.zzbSc = new zza(this, stream);
                        if (this.zzcls != null) {
                            try {
                                this.zzcls.doInBackground((TaskSnapshot) zzaaS(), this.zzbSc);
                            } catch (Throwable e) {
                                Log.w("StreamDownloadTask", "Exception occurred calling doInBackground.", e);
                                this.zzbLK = e;
                            }
                        }
                    } else {
                        this.zzbLK = new IOException("Could not open resulting stream.");
                    }
                }
                if (this.zzbSc == null) {
                    this.zzclu.zzabh();
                }
                boolean z2 = z && this.zzbLK == null && zzaaQ() == 4;
                if (z2) {
                    zzf(4, false);
                    zzf(TransportMediator.FLAG_KEY_MEDIA_NEXT, false);
                    return;
                }
                if (!zzf(zzaaQ() == 32 ? AccessibilityNodeInfoCompat.ACTION_NEXT_AT_MOVEMENT_GRANULARITY : 64, false)) {
                    Log.w("StreamDownloadTask", "Unable to change download task to final state from " + zzaaQ());
                }
            } catch (Throwable e2) {
                Log.e("StreamDownloadTask", "Unable to create firebase storage network request.", e2);
                this.zzbLK = e2;
                zzf(64, false);
            }
        }
    }

    protected void schedule() {
        zzd.zzaaW().zzv(zzTj());
    }

    StreamDownloadTask zza(@NonNull StreamProcessor streamProcessor) {
        zzac.zzw(streamProcessor);
        zzac.zzar(this.zzcls == null);
        this.zzcls = streamProcessor;
        return this;
    }

    @NonNull
    /* synthetic */ ProvideError zzaaK() {
        return zzaaX();
    }

    @NonNull
    TaskSnapshot zzaaX() {
        return new TaskSnapshot(this, StorageException.fromExceptionAndHttpCode(this.zzbLK, this.mResultCode), this.zzclt);
    }
}
