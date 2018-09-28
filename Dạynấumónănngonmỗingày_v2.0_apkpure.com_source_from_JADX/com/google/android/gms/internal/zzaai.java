package com.google.android.gms.internal;

import android.os.Bundle;
import android.os.DeadObjectException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.zzb;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzal;
import com.google.android.gms.internal.zzzv.zza;

public class zzaai implements zzaam {
    private final zzaan zzazK;
    private boolean zzazL;

    /* renamed from: com.google.android.gms.internal.zzaai.1 */
    class C11531 extends zza {
        final /* synthetic */ zzaai zzazM;

        C11531(zzaai com_google_android_gms_internal_zzaai, zzaam com_google_android_gms_internal_zzaam) {
            this.zzazM = com_google_android_gms_internal_zzaai;
            super(com_google_android_gms_internal_zzaam);
        }

        public void zzvA() {
            this.zzazM.onConnectionSuspended(1);
        }
    }

    /* renamed from: com.google.android.gms.internal.zzaai.2 */
    class C11542 extends zza {
        final /* synthetic */ zzaai zzazM;

        C11542(zzaai com_google_android_gms_internal_zzaai, zzaam com_google_android_gms_internal_zzaam) {
            this.zzazM = com_google_android_gms_internal_zzaai;
            super(com_google_android_gms_internal_zzaam);
        }

        public void zzvA() {
            this.zzazM.zzazK.zzaAK.zzo(null);
        }
    }

    public zzaai(zzaan com_google_android_gms_internal_zzaan) {
        this.zzazL = false;
        this.zzazK = com_google_android_gms_internal_zzaan;
    }

    private <A extends zzb> void zzd(zza<? extends Result, A> com_google_android_gms_internal_zzzv_zza__extends_com_google_android_gms_common_api_Result__A) throws DeadObjectException {
        this.zzazK.zzazd.zzaAx.zzb(com_google_android_gms_internal_zzzv_zza__extends_com_google_android_gms_common_api_Result__A);
        zzb zzb = this.zzazK.zzazd.zzb(com_google_android_gms_internal_zzzv_zza__extends_com_google_android_gms_common_api_Result__A.zzuH());
        if (zzb.isConnected() || !this.zzazK.zzaAG.containsKey(com_google_android_gms_internal_zzzv_zza__extends_com_google_android_gms_common_api_Result__A.zzuH())) {
            if (zzb instanceof zzal) {
                zzb = ((zzal) zzb).zzxG();
            }
            com_google_android_gms_internal_zzzv_zza__extends_com_google_android_gms_common_api_Result__A.zzb(zzb);
            return;
        }
        com_google_android_gms_internal_zzzv_zza__extends_com_google_android_gms_common_api_Result__A.zzA(new Status(17));
    }

    public void begin() {
    }

    public void connect() {
        if (this.zzazL) {
            this.zzazL = false;
            this.zzazK.zza(new C11542(this, this));
        }
    }

    public boolean disconnect() {
        if (this.zzazL) {
            return false;
        }
        if (this.zzazK.zzazd.zzvM()) {
            this.zzazL = true;
            for (zzabp zzwu : this.zzazK.zzazd.zzaAw) {
                zzwu.zzwu();
            }
            return false;
        }
        this.zzazK.zzh(null);
        return true;
    }

    public void onConnected(Bundle bundle) {
    }

    public void onConnectionSuspended(int i) {
        this.zzazK.zzh(null);
        this.zzazK.zzaAK.zzc(i, this.zzazL);
    }

    public <A extends zzb, R extends Result, T extends zza<R, A>> T zza(T t) {
        return zzb(t);
    }

    public void zza(ConnectionResult connectionResult, Api<?> api, int i) {
    }

    public <A extends zzb, T extends zza<? extends Result, A>> T zzb(T t) {
        try {
            zzd(t);
        } catch (DeadObjectException e) {
            this.zzazK.zza(new C11531(this, this));
        }
        return t;
    }

    void zzvz() {
        if (this.zzazL) {
            this.zzazL = false;
            this.zzazK.zzazd.zzaAx.release();
            disconnect();
        }
    }
}
