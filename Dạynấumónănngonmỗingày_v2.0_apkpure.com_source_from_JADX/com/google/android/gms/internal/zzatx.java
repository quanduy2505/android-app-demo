package com.google.android.gms.internal;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.MainThread;
import com.google.android.gms.common.internal.zzac;

public final class zzatx {
    private final Context mContext;
    private final Handler mHandler;
    private final zza zzbuQ;

    /* renamed from: com.google.android.gms.internal.zzatx.1 */
    class C06811 implements Runnable {
        final /* synthetic */ int zzaaz;
        final /* synthetic */ zzatp zzbsD;
        final /* synthetic */ zzati zzbsG;
        final /* synthetic */ zzatx zzbuR;

        /* renamed from: com.google.android.gms.internal.zzatx.1.1 */
        class C06801 implements Runnable {
            final /* synthetic */ C06811 zzbuS;

            C06801(C06811 c06811) {
                this.zzbuS = c06811;
            }

            public void run() {
                if (this.zzbuS.zzbuR.zzbuQ.callServiceStopSelfResult(this.zzbuS.zzaaz)) {
                    this.zzbuS.zzbsD.zzJv().zzKk();
                    this.zzbuS.zzbsG.zzLg().log("Local AppMeasurementService processed last upload request");
                }
            }
        }

        C06811(zzatx com_google_android_gms_internal_zzatx, zzatp com_google_android_gms_internal_zzatp, int i, zzati com_google_android_gms_internal_zzati) {
            this.zzbuR = com_google_android_gms_internal_zzatx;
            this.zzbsD = com_google_android_gms_internal_zzatp;
            this.zzaaz = i;
            this.zzbsG = com_google_android_gms_internal_zzati;
        }

        public void run() {
            this.zzbsD.zzLL();
            this.zzbsD.zzLG();
            this.zzbuR.mHandler.post(new C06801(this));
        }
    }

    public interface zza {
        boolean callServiceStopSelfResult(int i);

        Context getContext();
    }

    public zzatx(zza com_google_android_gms_internal_zzatx_zza) {
        this.mContext = com_google_android_gms_internal_zzatx_zza.getContext();
        zzac.zzw(this.mContext);
        this.zzbuQ = com_google_android_gms_internal_zzatx_zza;
        this.mHandler = new Handler();
    }

    private zzati zzJt() {
        return zzatp.zzbu(this.mContext).zzJt();
    }

    public static boolean zzj(Context context, boolean z) {
        zzac.zzw(context);
        return zzaue.zzr(context, z ? "com.google.android.gms.measurement.PackageMeasurementService" : "com.google.android.gms.measurement.AppMeasurementService");
    }

    @MainThread
    public IBinder onBind(Intent intent) {
        if (intent == null) {
            zzJt().zzLa().log("onBind called with null intent");
            return null;
        }
        String action = intent.getAction();
        if ("com.google.android.gms.measurement.START".equals(action)) {
            return new zzatq(zzatp.zzbu(this.mContext));
        }
        zzJt().zzLc().zzj("onBind received unknown action", action);
        return null;
    }

    @MainThread
    public void onCreate() {
        zzatp zzbu = zzatp.zzbu(this.mContext);
        zzati zzJt = zzbu.zzJt();
        zzbu.zzJv().zzKk();
        zzJt.zzLg().log("Local AppMeasurementService is starting up");
    }

    @MainThread
    public void onDestroy() {
        zzatp zzbu = zzatp.zzbu(this.mContext);
        zzati zzJt = zzbu.zzJt();
        zzbu.zzJv().zzKk();
        zzJt.zzLg().log("Local AppMeasurementService is shutting down");
    }

    @MainThread
    public void onRebind(Intent intent) {
        if (intent == null) {
            zzJt().zzLa().log("onRebind called with null intent");
            return;
        }
        zzJt().zzLg().zzj("onRebind called. action", intent.getAction());
    }

    @MainThread
    public int onStartCommand(Intent intent, int i, int i2) {
        zzatp zzbu = zzatp.zzbu(this.mContext);
        zzati zzJt = zzbu.zzJt();
        if (intent == null) {
            zzJt.zzLc().log("AppMeasurementService started with null intent");
        } else {
            String action = intent.getAction();
            zzbu.zzJv().zzKk();
            zzJt.zzLg().zze("Local AppMeasurementService called. startId, action", Integer.valueOf(i2), action);
            if ("com.google.android.gms.measurement.UPLOAD".equals(action)) {
                zzbu.zzJs().zzm(new C06811(this, zzbu, i2, zzJt));
            }
        }
        return 2;
    }

    @MainThread
    public boolean onUnbind(Intent intent) {
        if (intent == null) {
            zzJt().zzLa().log("onUnbind called with null intent");
        } else {
            zzJt().zzLg().zzj("onUnbind called for intent. action", intent.getAction());
        }
        return true;
    }
}
