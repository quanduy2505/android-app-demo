package com.google.android.gms.internal;

import android.os.Handler;
import android.os.Looper;
import com.google.android.gms.common.internal.zzac;

abstract class zzasv {
    private static volatile Handler zzaec;
    private volatile long zzaed;
    private final zzatp zzbpw;
    private boolean zzbqB;
    private final Runnable zzv;

    /* renamed from: com.google.android.gms.internal.zzasv.1 */
    class C06411 implements Runnable {
        final /* synthetic */ zzasv zzbqC;

        C06411(zzasv com_google_android_gms_internal_zzasv) {
            this.zzbqC = com_google_android_gms_internal_zzasv;
        }

        public void run() {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                this.zzbqC.zzbpw.zzJs().zzm(this);
                return;
            }
            boolean zzcv = this.zzbqC.zzcv();
            this.zzbqC.zzaed = 0;
            if (zzcv && this.zzbqC.zzbqB) {
                this.zzbqC.run();
            }
        }
    }

    zzasv(zzatp com_google_android_gms_internal_zzatp) {
        zzac.zzw(com_google_android_gms_internal_zzatp);
        this.zzbpw = com_google_android_gms_internal_zzatp;
        this.zzbqB = true;
        this.zzv = new C06411(this);
    }

    private Handler getHandler() {
        if (zzaec != null) {
            return zzaec;
        }
        Handler handler;
        synchronized (zzasv.class) {
            if (zzaec == null) {
                zzaec = new Handler(this.zzbpw.getContext().getMainLooper());
            }
            handler = zzaec;
        }
        return handler;
    }

    public void cancel() {
        this.zzaed = 0;
        getHandler().removeCallbacks(this.zzv);
    }

    public abstract void run();

    public boolean zzcv() {
        return this.zzaed != 0;
    }

    public void zzx(long j) {
        cancel();
        if (j >= 0) {
            this.zzaed = this.zzbpw.zznq().currentTimeMillis();
            if (!getHandler().postDelayed(this.zzv, j)) {
                this.zzbpw.zzJt().zzLa().zzj("Failed to schedule delayed post. time", Long.valueOf(j));
            }
        }
    }
}
