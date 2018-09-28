package com.google.android.gms.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.MainThread;
import android.support.annotation.WorkerThread;
import com.google.android.gms.common.util.zze;

public class zzaty extends zzats {
    private Handler mHandler;
    protected long zzbuT;
    private final zzasv zzbuU;
    private final zzasv zzbuV;

    /* renamed from: com.google.android.gms.internal.zzaty.3 */
    class C06823 implements Runnable {
        final /* synthetic */ long zzbpI;
        final /* synthetic */ zzaty zzbuW;

        C06823(zzaty com_google_android_gms_internal_zzaty, long j) {
            this.zzbuW = com_google_android_gms_internal_zzaty;
            this.zzbpI = j;
        }

        public void run() {
            this.zzbuW.zzap(this.zzbpI);
        }
    }

    /* renamed from: com.google.android.gms.internal.zzaty.4 */
    class C06834 implements Runnable {
        final /* synthetic */ long zzbpI;
        final /* synthetic */ zzaty zzbuW;

        C06834(zzaty com_google_android_gms_internal_zzaty, long j) {
            this.zzbuW = com_google_android_gms_internal_zzaty;
            this.zzbpI = j;
        }

        public void run() {
            this.zzbuW.zzaq(this.zzbpI);
        }
    }

    /* renamed from: com.google.android.gms.internal.zzaty.1 */
    class C11721 extends zzasv {
        final /* synthetic */ zzaty zzbuW;

        C11721(zzaty com_google_android_gms_internal_zzaty, zzatp com_google_android_gms_internal_zzatp) {
            this.zzbuW = com_google_android_gms_internal_zzaty;
            super(com_google_android_gms_internal_zzatp);
        }

        @WorkerThread
        public void run() {
            this.zzbuW.zzMf();
        }
    }

    /* renamed from: com.google.android.gms.internal.zzaty.2 */
    class C11732 extends zzasv {
        final /* synthetic */ zzaty zzbuW;

        C11732(zzaty com_google_android_gms_internal_zzaty, zzatp com_google_android_gms_internal_zzatp) {
            this.zzbuW = com_google_android_gms_internal_zzaty;
            super(com_google_android_gms_internal_zzatp);
        }

        @WorkerThread
        public void run() {
            this.zzbuW.zzMg();
        }
    }

    zzaty(zzatp com_google_android_gms_internal_zzatp) {
        super(com_google_android_gms_internal_zzatp);
        this.zzbuU = new C11721(this, this.zzbpw);
        this.zzbuV = new C11732(this, this.zzbpw);
    }

    private void zzMd() {
        synchronized (this) {
            if (this.mHandler == null) {
                this.mHandler = new Handler(Looper.getMainLooper());
            }
        }
    }

    @WorkerThread
    private void zzMg() {
        zzmq();
        zzaJ(false);
        zzJg().zzV(zznq().elapsedRealtime());
    }

    @WorkerThread
    private void zzap(long j) {
        zzmq();
        zzMd();
        this.zzbuU.cancel();
        this.zzbuV.cancel();
        zzJt().zzLg().zzj("Activity resumed, time", Long.valueOf(j));
        this.zzbuT = j;
        if (zznq().currentTimeMillis() - zzJu().zzbsq.get() > zzJu().zzbss.get()) {
            zzJu().zzbsr.set(true);
            zzJu().zzbst.set(0);
        }
        if (zzJu().zzbsr.get()) {
            this.zzbuU.zzx(Math.max(0, zzJu().zzbsp.get() - zzJu().zzbst.get()));
        } else {
            this.zzbuV.zzx(Math.max(0, 3600000 - zzJu().zzbst.get()));
        }
    }

    @WorkerThread
    private void zzaq(long j) {
        zzmq();
        zzMd();
        this.zzbuU.cancel();
        this.zzbuV.cancel();
        zzJt().zzLg().zzj("Activity paused, time", Long.valueOf(j));
        if (this.zzbuT != 0) {
            zzJu().zzbst.set(zzJu().zzbst.get() + (j - this.zzbuT));
        }
        zzJu().zzbss.set(zznq().currentTimeMillis());
    }

    public /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    public /* bridge */ /* synthetic */ void zzJd() {
        super.zzJd();
    }

    public /* bridge */ /* synthetic */ void zzJe() {
        super.zzJe();
    }

    public /* bridge */ /* synthetic */ void zzJf() {
        super.zzJf();
    }

    public /* bridge */ /* synthetic */ zzaso zzJg() {
        return super.zzJg();
    }

    public /* bridge */ /* synthetic */ zzass zzJh() {
        return super.zzJh();
    }

    public /* bridge */ /* synthetic */ zzatu zzJi() {
        return super.zzJi();
    }

    public /* bridge */ /* synthetic */ zzatf zzJj() {
        return super.zzJj();
    }

    public /* bridge */ /* synthetic */ zzasw zzJk() {
        return super.zzJk();
    }

    public /* bridge */ /* synthetic */ zzatw zzJl() {
        return super.zzJl();
    }

    public /* bridge */ /* synthetic */ zzatv zzJm() {
        return super.zzJm();
    }

    public /* bridge */ /* synthetic */ zzatg zzJn() {
        return super.zzJn();
    }

    public /* bridge */ /* synthetic */ zzasu zzJo() {
        return super.zzJo();
    }

    public /* bridge */ /* synthetic */ zzaue zzJp() {
        return super.zzJp();
    }

    public /* bridge */ /* synthetic */ zzatn zzJq() {
        return super.zzJq();
    }

    public /* bridge */ /* synthetic */ zzaty zzJr() {
        return super.zzJr();
    }

    public /* bridge */ /* synthetic */ zzato zzJs() {
        return super.zzJs();
    }

    public /* bridge */ /* synthetic */ zzati zzJt() {
        return super.zzJt();
    }

    public /* bridge */ /* synthetic */ zzatl zzJu() {
        return super.zzJu();
    }

    public /* bridge */ /* synthetic */ zzast zzJv() {
        return super.zzJv();
    }

    @MainThread
    protected void zzMc() {
        zzJs().zzm(new C06823(this, zznq().elapsedRealtime()));
    }

    @MainThread
    protected void zzMe() {
        zzJs().zzm(new C06834(this, zznq().elapsedRealtime()));
    }

    @WorkerThread
    protected void zzMf() {
        zzmq();
        zzJt().zzLg().zzj("Session started, time", Long.valueOf(zznq().elapsedRealtime()));
        zzJu().zzbsr.set(false);
        zzJi().zze("auto", "_s", new Bundle());
    }

    @WorkerThread
    public boolean zzaJ(boolean z) {
        zzmq();
        zznA();
        long elapsedRealtime = zznq().elapsedRealtime();
        if (this.zzbuT == 0) {
            this.zzbuT = elapsedRealtime - 3600000;
        }
        long j = elapsedRealtime - this.zzbuT;
        if (z || j >= 1000) {
            zzJu().zzbst.set(j);
            zzJt().zzLg().zzj("Recording user engagement, ms", Long.valueOf(j));
            Bundle bundle = new Bundle();
            bundle.putLong("_et", j);
            zzatv.zza(zzJm().zzLU(), bundle);
            zzJi().zze("auto", "_e", bundle);
            this.zzbuT = elapsedRealtime;
            this.zzbuV.cancel();
            this.zzbuV.zzx(Math.max(0, 3600000 - zzJu().zzbst.get()));
            return true;
        }
        zzJt().zzLg().zzj("Screen exposed for less than 1000 ms. Event not sent. time", Long.valueOf(j));
        return false;
    }

    public /* bridge */ /* synthetic */ void zzmq() {
        super.zzmq();
    }

    protected void zzmr() {
    }

    public /* bridge */ /* synthetic */ zze zznq() {
        return super.zznq();
    }
}
