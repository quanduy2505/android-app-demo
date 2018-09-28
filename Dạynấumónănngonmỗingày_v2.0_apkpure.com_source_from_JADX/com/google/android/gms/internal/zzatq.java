package com.google.android.gms.internal;

import android.os.Binder;
import android.support.annotation.BinderThread;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.util.zzx;
import com.google.android.gms.common.zze;
import com.google.android.gms.internal.zzate.zza;
import com.google.android.gms.measurement.AppMeasurement.zzf;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class zzatq extends zza {
    private final zzatp zzbpw;
    private Boolean zzbtL;
    @Nullable
    private String zzbtM;

    /* renamed from: com.google.android.gms.internal.zzatq.1 */
    class C06511 implements Runnable {
        final /* synthetic */ zzasq zzbtN;
        final /* synthetic */ zzatq zzbtO;

        C06511(zzatq com_google_android_gms_internal_zzatq, zzasq com_google_android_gms_internal_zzasq) {
            this.zzbtO = com_google_android_gms_internal_zzatq;
            this.zzbtN = com_google_android_gms_internal_zzasq;
        }

        public void run() {
            this.zzbtO.zzbpw.zzLL();
            this.zzbtO.zzbpw.zzd(this.zzbtN);
        }
    }

    /* renamed from: com.google.android.gms.internal.zzatq.2 */
    class C06522 implements Runnable {
        final /* synthetic */ zzasq zzbtN;
        final /* synthetic */ zzatq zzbtO;
        final /* synthetic */ zzatb zzbtP;

        C06522(zzatq com_google_android_gms_internal_zzatq, zzatb com_google_android_gms_internal_zzatb, zzasq com_google_android_gms_internal_zzasq) {
            this.zzbtO = com_google_android_gms_internal_zzatq;
            this.zzbtP = com_google_android_gms_internal_zzatb;
            this.zzbtN = com_google_android_gms_internal_zzasq;
        }

        public void run() {
            this.zzbtO.zzbpw.zzLL();
            this.zzbtO.zzbpw.zzb(this.zzbtP, this.zzbtN);
        }
    }

    /* renamed from: com.google.android.gms.internal.zzatq.3 */
    class C06533 implements Runnable {
        final /* synthetic */ String zzbky;
        final /* synthetic */ zzatq zzbtO;
        final /* synthetic */ zzatb zzbtP;

        C06533(zzatq com_google_android_gms_internal_zzatq, zzatb com_google_android_gms_internal_zzatb, String str) {
            this.zzbtO = com_google_android_gms_internal_zzatq;
            this.zzbtP = com_google_android_gms_internal_zzatb;
            this.zzbky = str;
        }

        public void run() {
            this.zzbtO.zzbpw.zzLL();
            this.zzbtO.zzbpw.zzb(this.zzbtP, this.zzbky);
        }
    }

    /* renamed from: com.google.android.gms.internal.zzatq.4 */
    class C06544 implements Callable<byte[]> {
        final /* synthetic */ String zzbky;
        final /* synthetic */ zzatq zzbtO;
        final /* synthetic */ zzatb zzbtP;

        C06544(zzatq com_google_android_gms_internal_zzatq, zzatb com_google_android_gms_internal_zzatb, String str) {
            this.zzbtO = com_google_android_gms_internal_zzatq;
            this.zzbtP = com_google_android_gms_internal_zzatb;
            this.zzbky = str;
        }

        public /* synthetic */ Object call() throws Exception {
            return zzLN();
        }

        public byte[] zzLN() throws Exception {
            this.zzbtO.zzbpw.zzLL();
            return this.zzbtO.zzbpw.zza(this.zzbtP, this.zzbky);
        }
    }

    /* renamed from: com.google.android.gms.internal.zzatq.5 */
    class C06555 implements Runnable {
        final /* synthetic */ zzasq zzbtN;
        final /* synthetic */ zzatq zzbtO;
        final /* synthetic */ zzaub zzbtQ;

        C06555(zzatq com_google_android_gms_internal_zzatq, zzaub com_google_android_gms_internal_zzaub, zzasq com_google_android_gms_internal_zzasq) {
            this.zzbtO = com_google_android_gms_internal_zzatq;
            this.zzbtQ = com_google_android_gms_internal_zzaub;
            this.zzbtN = com_google_android_gms_internal_zzasq;
        }

        public void run() {
            this.zzbtO.zzbpw.zzLL();
            this.zzbtO.zzbpw.zzc(this.zzbtQ, this.zzbtN);
        }
    }

    /* renamed from: com.google.android.gms.internal.zzatq.6 */
    class C06566 implements Runnable {
        final /* synthetic */ zzasq zzbtN;
        final /* synthetic */ zzatq zzbtO;
        final /* synthetic */ zzaub zzbtQ;

        C06566(zzatq com_google_android_gms_internal_zzatq, zzaub com_google_android_gms_internal_zzaub, zzasq com_google_android_gms_internal_zzasq) {
            this.zzbtO = com_google_android_gms_internal_zzatq;
            this.zzbtQ = com_google_android_gms_internal_zzaub;
            this.zzbtN = com_google_android_gms_internal_zzasq;
        }

        public void run() {
            this.zzbtO.zzbpw.zzLL();
            this.zzbtO.zzbpw.zzb(this.zzbtQ, this.zzbtN);
        }
    }

    /* renamed from: com.google.android.gms.internal.zzatq.7 */
    class C06577 implements Callable<List<zzaud>> {
        final /* synthetic */ zzasq zzbtN;
        final /* synthetic */ zzatq zzbtO;

        C06577(zzatq com_google_android_gms_internal_zzatq, zzasq com_google_android_gms_internal_zzasq) {
            this.zzbtO = com_google_android_gms_internal_zzatq;
            this.zzbtN = com_google_android_gms_internal_zzasq;
        }

        public /* synthetic */ Object call() throws Exception {
            return zzLO();
        }

        public List<zzaud> zzLO() throws Exception {
            this.zzbtO.zzbpw.zzLL();
            return this.zzbtO.zzbpw.zzJo().zzfx(this.zzbtN.packageName);
        }
    }

    /* renamed from: com.google.android.gms.internal.zzatq.8 */
    class C06588 implements Runnable {
        final /* synthetic */ zzasq zzbtN;
        final /* synthetic */ zzatq zzbtO;

        C06588(zzatq com_google_android_gms_internal_zzatq, zzasq com_google_android_gms_internal_zzasq) {
            this.zzbtO = com_google_android_gms_internal_zzatq;
            this.zzbtN = com_google_android_gms_internal_zzasq;
        }

        public void run() {
            this.zzbtO.zzbpw.zzLL();
            this.zzbtO.zzbpw.zze(this.zzbtN);
        }
    }

    /* renamed from: com.google.android.gms.internal.zzatq.9 */
    class C06599 implements Runnable {
        final /* synthetic */ String zzbky;
        final /* synthetic */ zzatq zzbtO;
        final /* synthetic */ String zzbtR;
        final /* synthetic */ String zzbtS;
        final /* synthetic */ long zzbtT;

        C06599(zzatq com_google_android_gms_internal_zzatq, String str, String str2, String str3, long j) {
            this.zzbtO = com_google_android_gms_internal_zzatq;
            this.zzbtR = str;
            this.zzbky = str2;
            this.zzbtS = str3;
            this.zzbtT = j;
        }

        public void run() {
            if (this.zzbtR == null) {
                this.zzbtO.zzbpw.zzJm().zza(this.zzbky, null);
                return;
            }
            zzf com_google_android_gms_measurement_AppMeasurement_zzf = new zzf();
            com_google_android_gms_measurement_AppMeasurement_zzf.zzbpz = this.zzbtS;
            com_google_android_gms_measurement_AppMeasurement_zzf.zzbpA = this.zzbtR;
            com_google_android_gms_measurement_AppMeasurement_zzf.zzbpB = this.zzbtT;
            this.zzbtO.zzbpw.zzJm().zza(this.zzbky, com_google_android_gms_measurement_AppMeasurement_zzf);
        }
    }

    public zzatq(zzatp com_google_android_gms_internal_zzatp) {
        this(com_google_android_gms_internal_zzatp, null);
    }

    public zzatq(zzatp com_google_android_gms_internal_zzatp, @Nullable String str) {
        zzac.zzw(com_google_android_gms_internal_zzatp);
        this.zzbpw = com_google_android_gms_internal_zzatp;
        this.zzbtM = str;
    }

    @BinderThread
    private void zzb(zzasq com_google_android_gms_internal_zzasq, boolean z) {
        zzac.zzw(com_google_android_gms_internal_zzasq);
        zzm(com_google_android_gms_internal_zzasq.packageName, z);
        this.zzbpw.zzJp().zzgd(com_google_android_gms_internal_zzasq.zzbqf);
    }

    @BinderThread
    private void zzm(String str, boolean z) {
        if (TextUtils.isEmpty(str)) {
            this.zzbpw.zzJt().zzLa().log("Measurement Service called without app package");
            throw new SecurityException("Measurement Service called without app package");
        }
        try {
            zzn(str, z);
        } catch (SecurityException e) {
            this.zzbpw.zzJt().zzLa().zzj("Measurement Service called with invalid calling package. appId", zzati.zzfI(str));
            throw e;
        }
    }

    @BinderThread
    public List<zzaub> zza(zzasq com_google_android_gms_internal_zzasq, boolean z) {
        Object e;
        zzb(com_google_android_gms_internal_zzasq, false);
        try {
            List<zzaud> list = (List) this.zzbpw.zzJs().zzd(new C06577(this, com_google_android_gms_internal_zzasq)).get();
            List<zzaub> arrayList = new ArrayList(list.size());
            for (zzaud com_google_android_gms_internal_zzaud : list) {
                if (z || !zzaue.zzgg(com_google_android_gms_internal_zzaud.mName)) {
                    arrayList.add(new zzaub(com_google_android_gms_internal_zzaud));
                }
            }
            return arrayList;
        } catch (InterruptedException e2) {
            e = e2;
            this.zzbpw.zzJt().zzLa().zze("Failed to get user attributes. appId", zzati.zzfI(com_google_android_gms_internal_zzasq.packageName), e);
            return null;
        } catch (ExecutionException e3) {
            e = e3;
            this.zzbpw.zzJt().zzLa().zze("Failed to get user attributes. appId", zzati.zzfI(com_google_android_gms_internal_zzasq.packageName), e);
            return null;
        }
    }

    @BinderThread
    public void zza(long j, String str, String str2, String str3) {
        this.zzbpw.zzJs().zzm(new C06599(this, str2, str3, str, j));
    }

    @BinderThread
    public void zza(zzasq com_google_android_gms_internal_zzasq) {
        zzb(com_google_android_gms_internal_zzasq, false);
        this.zzbpw.zzJs().zzm(new C06588(this, com_google_android_gms_internal_zzasq));
    }

    @BinderThread
    public void zza(zzatb com_google_android_gms_internal_zzatb, zzasq com_google_android_gms_internal_zzasq) {
        zzac.zzw(com_google_android_gms_internal_zzatb);
        zzb(com_google_android_gms_internal_zzasq, false);
        this.zzbpw.zzJs().zzm(new C06522(this, com_google_android_gms_internal_zzatb, com_google_android_gms_internal_zzasq));
    }

    @BinderThread
    public void zza(zzatb com_google_android_gms_internal_zzatb, String str, String str2) {
        zzac.zzw(com_google_android_gms_internal_zzatb);
        zzac.zzdv(str);
        zzm(str, true);
        this.zzbpw.zzJs().zzm(new C06533(this, com_google_android_gms_internal_zzatb, str));
    }

    @BinderThread
    public void zza(zzaub com_google_android_gms_internal_zzaub, zzasq com_google_android_gms_internal_zzasq) {
        zzac.zzw(com_google_android_gms_internal_zzaub);
        zzb(com_google_android_gms_internal_zzasq, false);
        if (com_google_android_gms_internal_zzaub.getValue() == null) {
            this.zzbpw.zzJs().zzm(new C06555(this, com_google_android_gms_internal_zzaub, com_google_android_gms_internal_zzasq));
        } else {
            this.zzbpw.zzJs().zzm(new C06566(this, com_google_android_gms_internal_zzaub, com_google_android_gms_internal_zzasq));
        }
    }

    @BinderThread
    public byte[] zza(zzatb com_google_android_gms_internal_zzatb, String str) {
        Object e;
        zzac.zzdv(str);
        zzac.zzw(com_google_android_gms_internal_zzatb);
        zzm(str, true);
        this.zzbpw.zzJt().zzLf().zzj("Log and bundle. event", com_google_android_gms_internal_zzatb.name);
        long nanoTime = this.zzbpw.zznq().nanoTime() / 1000000;
        try {
            byte[] bArr = (byte[]) this.zzbpw.zzJs().zze(new C06544(this, com_google_android_gms_internal_zzatb, str)).get();
            if (bArr == null) {
                this.zzbpw.zzJt().zzLa().zzj("Log and bundle returned null. appId", zzati.zzfI(str));
                bArr = new byte[0];
            }
            this.zzbpw.zzJt().zzLf().zzd("Log and bundle processed. event, size, time_ms", com_google_android_gms_internal_zzatb.name, Integer.valueOf(bArr.length), Long.valueOf((this.zzbpw.zznq().nanoTime() / 1000000) - nanoTime));
            return bArr;
        } catch (InterruptedException e2) {
            e = e2;
            this.zzbpw.zzJt().zzLa().zzd("Failed to log and bundle. appId, event, error", zzati.zzfI(str), com_google_android_gms_internal_zzatb.name, e);
            return null;
        } catch (ExecutionException e3) {
            e = e3;
            this.zzbpw.zzJt().zzLa().zzd("Failed to log and bundle. appId, event, error", zzati.zzfI(str), com_google_android_gms_internal_zzatb.name, e);
            return null;
        }
    }

    @BinderThread
    public void zzb(zzasq com_google_android_gms_internal_zzasq) {
        zzb(com_google_android_gms_internal_zzasq, false);
        this.zzbpw.zzJs().zzm(new C06511(this, com_google_android_gms_internal_zzasq));
    }

    @BinderThread
    public String zzc(zzasq com_google_android_gms_internal_zzasq) {
        zzb(com_google_android_gms_internal_zzasq, false);
        return this.zzbpw.zzfR(com_google_android_gms_internal_zzasq.packageName);
    }

    protected void zzn(String str, boolean z) throws SecurityException {
        if (z) {
            if (this.zzbtL == null) {
                boolean z2 = GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_PACKAGE.equals(this.zzbtM) || zzx.zzf(this.zzbpw.getContext(), Binder.getCallingUid()) || com.google.android.gms.common.zzf.zzav(this.zzbpw.getContext()).zza(this.zzbpw.getContext().getPackageManager(), Binder.getCallingUid());
                this.zzbtL = Boolean.valueOf(z2);
            }
            if (this.zzbtL.booleanValue()) {
                return;
            }
        }
        if (this.zzbtM == null && zze.zzc(this.zzbpw.getContext(), Binder.getCallingUid(), str)) {
            this.zzbtM = str;
        }
        if (!str.equals(this.zzbtM)) {
            throw new SecurityException(String.format("Unknown calling package name '%s'.", new Object[]{str}));
        }
    }
}
