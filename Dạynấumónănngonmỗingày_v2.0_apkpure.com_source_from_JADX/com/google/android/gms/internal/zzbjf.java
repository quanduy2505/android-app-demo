package com.google.android.gms.internal;

import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzac;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseUser;

abstract class zzbjf<SuccessT, CallbackT> {
    private boolean zzbLI;
    protected final int zzbVZ;
    protected FirebaseApp zzbVx;
    protected final zza zzbWa;
    protected FirebaseUser zzbWb;
    protected zzbjb zzbWc;
    protected CallbackT zzbWd;
    protected zzbje<SuccessT> zzbWe;
    protected zzbjp zzbWf;
    protected zzbjl zzbWg;
    protected zzbjj zzbWh;
    protected zzbjv zzbWi;
    protected String zzbWj;
    boolean zzbWk;
    SuccessT zzbWl;
    Status zzbWm;

    private class zza extends com.google.android.gms.internal.zzbja.zza {
        final /* synthetic */ zzbjf zzbWn;

        private zza(zzbjf com_google_android_gms_internal_zzbjf) {
            this.zzbWn = com_google_android_gms_internal_zzbjf;
        }

        public void onFailure(@NonNull Status status) throws RemoteException {
            this.zzbWn.zzcc(status);
        }

        public void zzUh() throws RemoteException {
            zzac.zza(this.zzbWn.zzbVZ == 5, "Unexpected response type " + this.zzbWn.zzbVZ);
            this.zzbWn.zzUl();
        }

        public void zzUi() throws RemoteException {
            zzac.zza(this.zzbWn.zzbVZ == 6, "Unexpected response type " + this.zzbWn.zzbVZ);
            this.zzbWn.zzUl();
        }

        public void zza(@NonNull zzbjj com_google_android_gms_internal_zzbjj) throws RemoteException {
            zzac.zza(this.zzbWn.zzbVZ == 3, "Unexpected response type " + this.zzbWn.zzbVZ);
            this.zzbWn.zzbWh = com_google_android_gms_internal_zzbjj;
            this.zzbWn.zzUl();
        }

        public void zza(@NonNull zzbjp com_google_android_gms_internal_zzbjp, @NonNull zzbjl com_google_android_gms_internal_zzbjl) throws RemoteException {
            zzac.zza(this.zzbWn.zzbVZ == 2, "Unexpected response type: " + this.zzbWn.zzbVZ);
            this.zzbWn.zzbWf = com_google_android_gms_internal_zzbjp;
            this.zzbWn.zzbWg = com_google_android_gms_internal_zzbjl;
            this.zzbWn.zzUl();
        }

        public void zza(@Nullable zzbjv com_google_android_gms_internal_zzbjv) throws RemoteException {
            zzac.zza(this.zzbWn.zzbVZ == 4, "Unexpected response type " + this.zzbWn.zzbVZ);
            this.zzbWn.zzbWi = com_google_android_gms_internal_zzbjv;
            this.zzbWn.zzUl();
        }

        public void zzb(@NonNull zzbjp com_google_android_gms_internal_zzbjp) throws RemoteException {
            boolean z = true;
            if (this.zzbWn.zzbVZ != 1) {
                z = false;
            }
            zzac.zza(z, "Unexpected response type: " + this.zzbWn.zzbVZ);
            this.zzbWn.zzbWf = com_google_android_gms_internal_zzbjp;
            this.zzbWn.zzUl();
        }

        public void zziy(@NonNull String str) throws RemoteException {
            zzac.zza(this.zzbWn.zzbVZ == 7, "Unexpected response type " + this.zzbWn.zzbVZ);
            this.zzbWn.zzbWj = str;
            this.zzbWn.zzUl();
        }
    }

    public zzbjf(int i) {
        this.zzbWa = new zza();
        this.zzbVZ = i;
    }

    private void zzUl() {
        zzUe();
        zzac.zza(this.zzbLI, (Object) "no success or failure set on method implementation");
    }

    protected abstract void dispatch() throws RemoteException;

    public abstract void zzUe();

    public void zzUk() {
        zzac(null);
    }

    public zzbjf<SuccessT, CallbackT> zza(zzbje<SuccessT> com_google_android_gms_internal_zzbje_SuccessT) {
        this.zzbWe = com_google_android_gms_internal_zzbje_SuccessT;
        return this;
    }

    public void zza(zzbjb com_google_android_gms_internal_zzbjb) throws RemoteException {
        this.zzbWc = com_google_android_gms_internal_zzbjb;
        dispatch();
    }

    public zzbjf<SuccessT, CallbackT> zzab(CallbackT callbackT) {
        this.zzbWd = zzac.zzb((Object) callbackT, (Object) "external callback cannot be null");
        return this;
    }

    public void zzac(SuccessT successT) {
        this.zzbLI = true;
        this.zzbWk = true;
        this.zzbWl = successT;
        this.zzbWe.zza(successT, null);
    }

    public void zzcc(Status status) {
        this.zzbLI = true;
        this.zzbWk = false;
        this.zzbWm = status;
        this.zzbWe.zza(null, status);
    }

    public zzbjf<SuccessT, CallbackT> zze(FirebaseApp firebaseApp) {
        this.zzbVx = (FirebaseApp) zzac.zzb((Object) firebaseApp, (Object) "firebaseApp cannot be null");
        return this;
    }

    public zzbjf<SuccessT, CallbackT> zze(FirebaseUser firebaseUser) {
        this.zzbWb = (FirebaseUser) zzac.zzb((Object) firebaseUser, (Object) "firebaseUser cannot be null");
        return this;
    }
}
