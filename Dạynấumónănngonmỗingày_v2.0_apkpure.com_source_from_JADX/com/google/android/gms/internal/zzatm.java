package com.google.android.gms.internal;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.MainThread;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.measurement.AppMeasurement;

public final class zzatm {
    private final zza zzbsC;

    /* renamed from: com.google.android.gms.internal.zzatm.1 */
    class C06471 implements Runnable {
        final /* synthetic */ zzatp zzbsD;
        final /* synthetic */ long zzbsE;
        final /* synthetic */ Bundle zzbsF;
        final /* synthetic */ zzati zzbsG;
        final /* synthetic */ Context zztd;

        C06471(zzatm com_google_android_gms_internal_zzatm, zzatp com_google_android_gms_internal_zzatp, long j, Bundle bundle, Context context, zzati com_google_android_gms_internal_zzati) {
            this.zzbsD = com_google_android_gms_internal_zzatp;
            this.zzbsE = j;
            this.zzbsF = bundle;
            this.zztd = context;
            this.zzbsG = com_google_android_gms_internal_zzati;
        }

        public void run() {
            zzaud zzR = this.zzbsD.zzJo().zzR(this.zzbsD.zzJj().zzjI(), "_fot");
            long longValue = (zzR == null || !(zzR.zzYe instanceof Long)) ? 0 : ((Long) zzR.zzYe).longValue();
            long j = this.zzbsE;
            longValue = (longValue <= 0 || (j < longValue && j > 0)) ? j : longValue - 1;
            if (longValue > 0) {
                this.zzbsF.putLong("click_timestamp", longValue);
            }
            AppMeasurement.getInstance(this.zztd).logEventInternal("auto", "_cmp", this.zzbsF);
            this.zzbsG.zzLg().log("Install campaign recorded");
        }
    }

    public interface zza {
        void doStartService(Context context, Intent intent);
    }

    public zzatm(zza com_google_android_gms_internal_zzatm_zza) {
        zzac.zzw(com_google_android_gms_internal_zzatm_zza);
        this.zzbsC = com_google_android_gms_internal_zzatm_zza;
    }

    public static boolean zzi(Context context, boolean z) {
        zzac.zzw(context);
        return zzaue.zza(context, z ? "com.google.android.gms.measurement.PackageMeasurementReceiver" : "com.google.android.gms.measurement.AppMeasurementReceiver", false);
    }

    @MainThread
    public void onReceive(Context context, Intent intent) {
        zzatp zzbu = zzatp.zzbu(context);
        zzati zzJt = zzbu.zzJt();
        if (intent == null) {
            zzJt.zzLc().log("Receiver called with null intent");
            return;
        }
        zzbu.zzJv().zzKk();
        String action = intent.getAction();
        zzJt.zzLg().zzj("Local receiver got", action);
        if ("com.google.android.gms.measurement.UPLOAD".equals(action)) {
            zzatx.zzj(context, false);
            Intent className = new Intent().setClassName(context, "com.google.android.gms.measurement.AppMeasurementService");
            className.setAction("com.google.android.gms.measurement.UPLOAD");
            this.zzbsC.doStartService(context, className);
        } else if ("com.android.vending.INSTALL_REFERRER".equals(action)) {
            action = intent.getStringExtra("referrer");
            if (action == null) {
                zzJt.zzLg().log("Install referrer extras are null");
                return;
            }
            Bundle zzu = zzbu.zzJp().zzu(Uri.parse(action));
            if (zzu == null) {
                zzJt.zzLg().log("No campaign defined in install referrer broadcast");
                return;
            }
            long longExtra = 1000 * intent.getLongExtra("referrer_timestamp_seconds", 0);
            if (longExtra == 0) {
                zzJt.zzLc().log("Install referrer is missing timestamp");
            }
            zzbu.zzJs().zzm(new C06471(this, zzbu, longExtra, zzu, context, zzJt));
        }
    }
}
