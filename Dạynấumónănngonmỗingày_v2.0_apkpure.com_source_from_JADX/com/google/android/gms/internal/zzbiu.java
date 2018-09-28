package com.google.android.gms.internal;

import android.content.Context;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import com.google.android.gms.common.api.Api.ApiOptions;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.ActionCodeResult;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.auth.UserProfileChangeRequest;
import java.util.ArrayList;
import java.util.List;
import rx.internal.schedulers.NewThreadWorker;

public class zzbiu extends com.google.android.gms.common.api.zzc<com.google.android.gms.internal.zzbjc.zza> {

    static final class zza extends zzbjf<Void, zzbkb> {
        @NonNull
        private final String zzaZp;

        public zza(@NonNull String str) {
            super(7);
            this.zzaZp = zzac.zzh(str, "code cannot be null or empty");
        }

        public void dispatch() throws RemoteException {
            this.zzbWc.zzj(this.zzaZp, this.zzbWa);
        }

        public void zzUe() {
            zzUk();
        }
    }

    static final class zzb extends zzbjf<ActionCodeResult, zzbkb> {
        @NonNull
        private final String zzaZp;

        public zzb(@NonNull String str) {
            super(4);
            this.zzaZp = zzac.zzh(str, "code cannot be null or empty");
        }

        public void dispatch() throws RemoteException {
            this.zzbWc.zzi(this.zzaZp, this.zzbWa);
        }

        public void zzUe() {
            zzac(new zzbkd(this.zzbWi));
        }
    }

    static final class zzc extends zzbjf<Void, zzbkb> {
        @NonNull
        private final String zzaZp;
        @NonNull
        private final String zzbVN;

        public zzc(@NonNull String str, @NonNull String str2) {
            super(4);
            this.zzaZp = zzac.zzh(str, "code cannot be null or empty");
            this.zzbVN = zzac.zzh(str2, "new password cannot be null or empty");
        }

        public void dispatch() throws RemoteException {
            this.zzbWc.zzf(this.zzaZp, this.zzbVN, this.zzbWa);
        }

        public void zzUe() {
            zzUk();
        }
    }

    static final class zzd extends zzbjf<AuthResult, zzbkb> {
        @NonNull
        private String zzaiW;
        @NonNull
        private String zzaig;

        public zzd(@NonNull String str, @NonNull String str2) {
            super(2);
            this.zzaiW = zzac.zzh(str, "email cannot be null or empty");
            this.zzaig = zzac.zzh(str2, "password cannot be null or empty");
        }

        public void dispatch() throws RemoteException {
            this.zzbWc.zzc(this.zzaiW, this.zzaig, this.zzbWa);
        }

        public void zzUe() {
            FirebaseUser zzb = zzbiu.zza(this.zzbVx, this.zzbWg);
            ((zzbkb) this.zzbWd).zza(this.zzbWf, zzb);
            zzac(new zzbke(zzb));
        }
    }

    static final class zze extends zzbjf<Void, zzbkk> {
        public zze() {
            super(5);
        }

        public void dispatch() throws RemoteException {
            this.zzbWc.zzg(this.zzbWb.zzTX(), this.zzbWa);
        }

        public void zzUe() {
            ((zzbkk) this.zzbWd).zzTU();
            zzac(null);
        }
    }

    static final class zzf extends zzbjf<ProviderQueryResult, zzbkb> {
        @NonNull
        private final String zzaiW;

        public zzf(@NonNull String str) {
            super(3);
            this.zzaiW = zzac.zzh(str, "email cannot be null or empty");
        }

        public void dispatch() throws RemoteException {
            this.zzbWc.zzc(this.zzaiW, this.zzbWa);
        }

        public void zzUe() {
            zzac(new zzbki(this.zzbWh));
        }
    }

    static final class zzg extends zzbjf<GetTokenResult, zzbkb> {
        @NonNull
        private final String zzbVO;

        public zzg(@NonNull String str) {
            super(1);
            this.zzbVO = zzac.zzh(str, "refresh token cannot be null");
        }

        public void dispatch() throws RemoteException {
            this.zzbWc.zza(this.zzbVO, this.zzbWa);
        }

        public void zzUe() {
            this.zzbWf.zziz(this.zzbVO);
            ((zzbkb) this.zzbWd).zza(this.zzbWf, this.zzbWb);
            zzac(new GetTokenResult(this.zzbWf.getAccessToken()));
        }
    }

    static final class zzh extends zzbjf<AuthResult, zzbkb> {
        @NonNull
        private final EmailAuthCredential zzbVP;

        public zzh(@NonNull EmailAuthCredential emailAuthCredential) {
            super(2);
            this.zzbVP = (EmailAuthCredential) zzac.zzb((Object) emailAuthCredential, (Object) "credential cannot be null");
        }

        public void dispatch() throws RemoteException {
            this.zzbWc.zza(this.zzbVP.getEmail(), this.zzbVP.getPassword(), this.zzbWb.zzTX(), this.zzbWa);
        }

        public void zzUe() {
            FirebaseUser zzb = zzbiu.zza(this.zzbVx, this.zzbWg);
            ((zzbkb) this.zzbWd).zza(this.zzbWf, zzb);
            zzac(new zzbke(zzb));
        }
    }

    static final class zzi extends zzbjf<AuthResult, zzbkb> {
        @NonNull
        private final zzbjz zzbVQ;

        public zzi(@NonNull AuthCredential authCredential) {
            super(2);
            zzac.zzb((Object) authCredential, (Object) "credential cannot be null");
            this.zzbVQ = zzbkc.zza(authCredential);
        }

        public void dispatch() throws RemoteException {
            this.zzbWc.zza(this.zzbWb.zzTX(), this.zzbVQ, this.zzbWa);
        }

        public void zzUe() {
            FirebaseUser zzb = zzbiu.zza(this.zzbVx, this.zzbWg);
            ((zzbkb) this.zzbWd).zza(this.zzbWf, zzb);
            zzac(new zzbke(zzb));
        }
    }

    private static class zzj<ResultT, CallbackT> extends zzabn<zzbiv, ResultT> implements zzbje<ResultT> {
        private TaskCompletionSource<ResultT> zzayo;
        private zzbjf<ResultT, CallbackT> zzbVR;

        public zzj(zzbjf<ResultT, CallbackT> com_google_android_gms_internal_zzbjf_ResultT__CallbackT) {
            this.zzbVR = com_google_android_gms_internal_zzbjf_ResultT__CallbackT;
            this.zzbVR.zza((zzbje) this);
        }

        protected void zza(zzbiv com_google_android_gms_internal_zzbiv, TaskCompletionSource<ResultT> taskCompletionSource) throws RemoteException {
            this.zzayo = taskCompletionSource;
            this.zzbVR.zza(com_google_android_gms_internal_zzbiv.zzUf());
        }

        public final void zza(ResultT resultT, Status status) {
            zzac.zzb(this.zzayo, (Object) "doExecute must be called before onComplete");
            if (status != null) {
                this.zzayo.setException(zzbix.zzcb(status));
            } else {
                this.zzayo.setResult(resultT);
            }
        }
    }

    static final class zzk extends zzbjf<Void, zzbkb> {
        @NonNull
        private final zzbjz zzbVQ;

        public zzk(@NonNull AuthCredential authCredential) {
            super(2);
            zzac.zzb((Object) authCredential, (Object) "credential cannot be null");
            this.zzbVQ = zzbkc.zza(authCredential);
        }

        public void dispatch() throws RemoteException {
            this.zzbWc.zza(this.zzbVQ, this.zzbWa);
        }

        public void zzUe() {
            FirebaseUser zzb = zzbiu.zza(this.zzbVx, this.zzbWg);
            if (this.zzbWb.getUid().equalsIgnoreCase(zzb.getUid())) {
                ((zzbkb) this.zzbWd).zza(this.zzbWf, zzb);
                zzUk();
                return;
            }
            zzcc(zzbkj.zzUH());
        }
    }

    static final class zzl extends zzbjf<Void, zzbkb> {
        @NonNull
        private final String zzaiW;
        @NonNull
        private final String zzaig;

        public zzl(@NonNull String str, @NonNull String str2) {
            super(2);
            this.zzaiW = zzac.zzh(str, "email cannot be null or empty");
            this.zzaig = zzac.zzh(str2, "password cannot be null or empty");
        }

        public void dispatch() throws RemoteException {
            this.zzbWc.zzd(this.zzaiW, this.zzaig, this.zzbWa);
        }

        public void zzUe() {
            FirebaseUser zzb = zzbiu.zza(this.zzbVx, this.zzbWg);
            if (this.zzbWb.getUid().equalsIgnoreCase(zzb.getUid())) {
                ((zzbkb) this.zzbWd).zza(this.zzbWf, zzb);
                zzUk();
                return;
            }
            zzcc(zzbkj.zzUH());
        }
    }

    static final class zzm extends zzbjf<Void, zzbkb> {
        public zzm() {
            super(2);
        }

        public void dispatch() throws RemoteException {
            this.zzbWc.zzf(this.zzbWb.zzTX(), this.zzbWa);
        }

        public void zzUe() {
            ((zzbkb) this.zzbWd).zza(this.zzbWf, zzbiu.zza(this.zzbVx, this.zzbWg, this.zzbWb.isAnonymous()));
            zzac(null);
        }
    }

    static final class zzn extends zzbjf<Void, zzbkb> {
        @NonNull
        private String zzahI;

        public zzn(@NonNull String str) {
            super(6);
            this.zzahI = zzac.zzh(str, "token cannot be null or empty");
        }

        public void dispatch() throws RemoteException {
            this.zzbWc.zzh(this.zzahI, this.zzbWa);
        }

        public void zzUe() {
            zzUk();
        }
    }

    static final class zzo extends zzbjf<Void, zzbkb> {
        @NonNull
        private String zzaiW;

        public zzo(@NonNull String str) {
            super(4);
            this.zzaiW = zzac.zzh(str, "email cannot be null or empty");
        }

        public void dispatch() throws RemoteException {
            this.zzbWc.zzd(this.zzaiW, this.zzbWa);
        }

        public void zzUe() {
            zzUk();
        }
    }

    static final class zzp extends zzbjf<AuthResult, zzbkb> {
        public zzp() {
            super(2);
        }

        public void dispatch() throws RemoteException {
            this.zzbWc.zza(this.zzbWa);
        }

        public void zzUe() {
            FirebaseUser zzb = zzbiu.zza(this.zzbVx, this.zzbWg, true);
            ((zzbkb) this.zzbWd).zza(this.zzbWf, zzb);
            zzac(new zzbke(zzb));
        }
    }

    static final class zzq extends zzbjf<AuthResult, zzbkb> {
        @NonNull
        private final zzbjz zzbVQ;

        public zzq(@NonNull AuthCredential authCredential) {
            super(2);
            zzac.zzb((Object) authCredential, (Object) "credential cannot be null");
            this.zzbVQ = zzbkc.zza(authCredential);
        }

        public void dispatch() throws RemoteException {
            this.zzbWc.zza(this.zzbVQ, this.zzbWa);
        }

        public void zzUe() {
            FirebaseUser zzb = zzbiu.zza(this.zzbVx, this.zzbWg);
            ((zzbkb) this.zzbWd).zza(this.zzbWf, zzb);
            zzac(new zzbke(zzb));
        }
    }

    static final class zzr extends zzbjf<AuthResult, zzbkb> {
        @NonNull
        private final String zzahI;

        public zzr(@NonNull String str) {
            super(2);
            this.zzahI = zzac.zzh(str, "token cannot be null or empty");
        }

        public void dispatch() throws RemoteException {
            this.zzbWc.zzb(this.zzahI, this.zzbWa);
        }

        public void zzUe() {
            FirebaseUser zzb = zzbiu.zza(this.zzbVx, this.zzbWg);
            ((zzbkb) this.zzbWd).zza(this.zzbWf, zzb);
            zzac(new zzbke(zzb));
        }
    }

    static final class zzs extends zzbjf<AuthResult, zzbkb> {
        @NonNull
        private String zzaiW;
        @NonNull
        private String zzaig;

        public zzs(String str, String str2) {
            super(2);
            this.zzaiW = zzac.zzh(str, "email cannot be null or empty");
            this.zzaig = zzac.zzh(str2, "password cannot be null or empty");
        }

        public void dispatch() throws RemoteException {
            this.zzbWc.zzd(this.zzaiW, this.zzaig, this.zzbWa);
        }

        public void zzUe() {
            FirebaseUser zzb = zzbiu.zza(this.zzbVx, this.zzbWg);
            ((zzbkb) this.zzbWd).zza(this.zzbWf, zzb);
            zzac(new zzbke(zzb));
        }
    }

    static final class zzt extends zzbjf<AuthResult, zzbkb> {
        public zzt() {
            super(2);
        }

        public void dispatch() throws RemoteException {
            this.zzbWc.zze(this.zzbWb.zzTX(), this.zzbWa);
        }

        public void zzUe() {
            FirebaseUser zzb = zzbiu.zza(this.zzbVx, this.zzbWg);
            ((zzbkb) this.zzbWd).zza(this.zzbWf, zzb);
            zzac(new zzbke(zzb));
        }
    }

    static final class zzu extends zzbjf<AuthResult, zzbkb> {
        @NonNull
        private String zzbVS;

        public zzu(@NonNull String str) {
            super(2);
            this.zzbVS = zzac.zzh(str, "provider cannot be null or empty");
        }

        public void dispatch() throws RemoteException {
            this.zzbWc.zze(this.zzbVS, this.zzbWb.zzTX(), this.zzbWa);
        }

        public void zzUe() {
            FirebaseUser zzb = zzbiu.zza(this.zzbVx, this.zzbWg);
            ((zzbkb) this.zzbWd).zza(this.zzbWf, zzb);
            zzac(new zzbke(zzb));
        }
    }

    static final class zzv extends zzbjf<Void, zzbkb> {
        @NonNull
        private final String zzaiW;

        public zzv(String str) {
            super(2);
            this.zzaiW = zzac.zzh(str, "email cannot be null or empty");
        }

        public void dispatch() throws RemoteException {
            this.zzbWc.zza(this.zzbWb.zzTX(), this.zzaiW, this.zzbWa);
        }

        public void zzUe() {
            ((zzbkb) this.zzbWd).zza(this.zzbWf, zzbiu.zza(this.zzbVx, this.zzbWg));
            zzUk();
        }
    }

    static final class zzw extends zzbjf<Void, zzbkb> {
        @NonNull
        private final String zzaig;

        public zzw(@NonNull String str) {
            super(2);
            this.zzaig = zzac.zzh(str, "password cannot be null or empty");
        }

        public void dispatch() throws RemoteException {
            this.zzbWc.zzb(this.zzbWb.zzTX(), this.zzaig, this.zzbWa);
        }

        public void zzUe() {
            ((zzbkb) this.zzbWd).zza(this.zzbWf, zzbiu.zza(this.zzbVx, this.zzbWg));
            zzUk();
        }
    }

    static final class zzx extends zzbjf<Void, zzbkb> {
        @NonNull
        private final UserProfileChangeRequest zzbVT;

        public zzx(UserProfileChangeRequest userProfileChangeRequest) {
            super(2);
            this.zzbVT = (UserProfileChangeRequest) zzac.zzb((Object) userProfileChangeRequest, (Object) "request cannot be null");
        }

        public void dispatch() throws RemoteException {
            this.zzbWc.zza(this.zzbWb.zzTX(), this.zzbVT, this.zzbWa);
        }

        public void zzUe() {
            ((zzbkb) this.zzbWd).zza(this.zzbWf, zzbiu.zza(this.zzbVx, this.zzbWg));
            zzUk();
        }
    }

    static final class zzy extends zzbjf<String, zzbkb> {
        @NonNull
        private final String zzaZp;

        public zzy(@NonNull String str) {
            super(4);
            this.zzaZp = zzac.zzh(str, "code cannot be null or empty");
        }

        public void dispatch() throws RemoteException {
            this.zzbWc.zzi(this.zzaZp, this.zzbWa);
        }

        public void zzUe() {
            if (new zzbkd(this.zzbWi).getOperation() != 0) {
                zzcc(new Status(17499));
            } else {
                zzac(this.zzbWi.getEmail());
            }
        }
    }

    zzbiu(@NonNull Context context, @NonNull com.google.android.gms.internal.zzbjc.zza com_google_android_gms_internal_zzbjc_zza) {
        super(context, zzbjc.zzbVY, (ApiOptions) com_google_android_gms_internal_zzbjc_zza, new com.google.firebase.zza());
    }

    private <ResultT, CallbackT> zzj<ResultT, CallbackT> zza(zzbjf<ResultT, CallbackT> com_google_android_gms_internal_zzbjf_ResultT__CallbackT) {
        return new zzj(com_google_android_gms_internal_zzbjf_ResultT__CallbackT);
    }

    @NonNull
    private static zzbkh zza(@NonNull FirebaseApp firebaseApp, @NonNull zzbjl com_google_android_gms_internal_zzbjl) {
        return zza(firebaseApp, com_google_android_gms_internal_zzbjl, false);
    }

    @NonNull
    private static zzbkh zza(@NonNull FirebaseApp firebaseApp, @NonNull zzbjl com_google_android_gms_internal_zzbjl, boolean z) {
        zzac.zzw(firebaseApp);
        zzac.zzw(com_google_android_gms_internal_zzbjl);
        List arrayList = new ArrayList();
        arrayList.add(new zzbkf(com_google_android_gms_internal_zzbjl, FirebaseAuthProvider.PROVIDER_ID));
        List zzUp = com_google_android_gms_internal_zzbjl.zzUp();
        if (!(zzUp == null || zzUp.isEmpty())) {
            for (int i = 0; i < zzUp.size(); i++) {
                arrayList.add(new zzbkf((zzbjr) zzUp.get(i)));
            }
        }
        zzbkh com_google_android_gms_internal_zzbkh = new zzbkh(firebaseApp, arrayList);
        com_google_android_gms_internal_zzbkh.zzaT(z);
        return com_google_android_gms_internal_zzbkh;
    }

    @NonNull
    private Task<AuthResult> zza(@NonNull FirebaseApp firebaseApp, @NonNull AuthCredential authCredential, @NonNull FirebaseUser firebaseUser, @NonNull zzbkb com_google_android_gms_internal_zzbkb) {
        zzac.zzw(firebaseApp);
        zzac.zzw(authCredential);
        zzac.zzw(firebaseUser);
        zzac.zzw(com_google_android_gms_internal_zzbkb);
        List providers = firebaseUser.getProviders();
        return (providers == null || !providers.contains(authCredential.getProvider())) ? doWrite(zza(new zzi(authCredential).zze(firebaseApp).zze(firebaseUser).zzab(com_google_android_gms_internal_zzbkb))) : Tasks.forException(zzbix.zzcb(new Status(17015)));
    }

    @NonNull
    private Task<AuthResult> zza(@NonNull FirebaseApp firebaseApp, @NonNull EmailAuthCredential emailAuthCredential, @NonNull FirebaseUser firebaseUser, @NonNull zzbkb com_google_android_gms_internal_zzbkb) {
        return doWrite(zza(new zzh(emailAuthCredential).zze(firebaseApp).zze(firebaseUser).zzab(com_google_android_gms_internal_zzbkb)));
    }

    @NonNull
    private Task<AuthResult> zza(@NonNull FirebaseApp firebaseApp, @NonNull FirebaseUser firebaseUser, @NonNull zzbkb com_google_android_gms_internal_zzbkb) {
        return doWrite(zza(new zzt().zze(firebaseApp).zze(firebaseUser).zzab(com_google_android_gms_internal_zzbkb)));
    }

    @NonNull
    private Task<AuthResult> zza(@NonNull FirebaseApp firebaseApp, @NonNull String str, @NonNull FirebaseUser firebaseUser, @NonNull zzbkb com_google_android_gms_internal_zzbkb) {
        return doWrite(zza(new zzu(str).zze(firebaseApp).zze(firebaseUser).zzab(com_google_android_gms_internal_zzbkb)));
    }

    public Task<AuthResult> zza(@NonNull FirebaseApp firebaseApp, @NonNull zzbkb com_google_android_gms_internal_zzbkb) {
        return doWrite(zza(new zzp().zze(firebaseApp).zzab(com_google_android_gms_internal_zzbkb)));
    }

    public Task<AuthResult> zza(@NonNull FirebaseApp firebaseApp, @NonNull AuthCredential authCredential, @NonNull zzbkb com_google_android_gms_internal_zzbkb) {
        return doWrite(zza(new zzq(authCredential).zze(firebaseApp).zzab(com_google_android_gms_internal_zzbkb)));
    }

    public Task<Void> zza(@NonNull FirebaseApp firebaseApp, @NonNull FirebaseUser firebaseUser, @NonNull AuthCredential authCredential, @NonNull zzbkb com_google_android_gms_internal_zzbkb) {
        return doWrite(zza(new zzk(authCredential).zze(firebaseApp).zze(firebaseUser).zzab(com_google_android_gms_internal_zzbkb)));
    }

    public Task<Void> zza(@NonNull FirebaseApp firebaseApp, @NonNull FirebaseUser firebaseUser, @NonNull UserProfileChangeRequest userProfileChangeRequest, @NonNull zzbkb com_google_android_gms_internal_zzbkb) {
        return doWrite(zza(new zzx(userProfileChangeRequest).zze(firebaseApp).zze(firebaseUser).zzab(com_google_android_gms_internal_zzbkb)));
    }

    public Task<GetTokenResult> zza(@NonNull FirebaseApp firebaseApp, @NonNull FirebaseUser firebaseUser, @NonNull String str, @NonNull zzbkb com_google_android_gms_internal_zzbkb) {
        return doRead(zza(new zzg(str).zze(firebaseApp).zze(firebaseUser).zzab(com_google_android_gms_internal_zzbkb)));
    }

    public Task<Void> zza(@NonNull FirebaseApp firebaseApp, @NonNull FirebaseUser firebaseUser, @NonNull String str, @NonNull String str2, @NonNull zzbkb com_google_android_gms_internal_zzbkb) {
        return doWrite(zza(new zzl(str, str2).zze(firebaseApp).zze(firebaseUser).zzab(com_google_android_gms_internal_zzbkb)));
    }

    public Task<ProviderQueryResult> zza(@NonNull FirebaseApp firebaseApp, @NonNull String str) {
        return doRead(zza(new zzf(str).zze(firebaseApp)));
    }

    public Task<AuthResult> zza(@NonNull FirebaseApp firebaseApp, @NonNull String str, @NonNull zzbkb com_google_android_gms_internal_zzbkb) {
        return doWrite(zza(new zzr(str).zze(firebaseApp).zzab(com_google_android_gms_internal_zzbkb)));
    }

    public Task<Void> zza(@NonNull FirebaseApp firebaseApp, @NonNull String str, @NonNull String str2) {
        return doWrite(zza(new zzc(str, str2).zze(firebaseApp)));
    }

    public Task<AuthResult> zza(@NonNull FirebaseApp firebaseApp, @NonNull String str, @NonNull String str2, @NonNull zzbkb com_google_android_gms_internal_zzbkb) {
        return doWrite(zza(new zzd(str, str2).zze(firebaseApp).zzab(com_google_android_gms_internal_zzbkb)));
    }

    @NonNull
    public Task<Void> zza(@NonNull FirebaseUser firebaseUser, @NonNull zzbkk com_google_android_gms_internal_zzbkk) {
        return doWrite(zza(new zze().zze(firebaseUser).zzab(com_google_android_gms_internal_zzbkk)));
    }

    @NonNull
    public Task<Void> zzb(@NonNull FirebaseApp firebaseApp, @NonNull FirebaseUser firebaseUser, @NonNull zzbkb com_google_android_gms_internal_zzbkb) {
        return doRead(zza(new zzm().zze(firebaseApp).zze(firebaseUser).zzab(com_google_android_gms_internal_zzbkb)));
    }

    public Task<AuthResult> zzb(@NonNull FirebaseApp firebaseApp, @NonNull FirebaseUser firebaseUser, @NonNull AuthCredential authCredential, @NonNull zzbkb com_google_android_gms_internal_zzbkb) {
        zzac.zzw(firebaseApp);
        zzac.zzw(authCredential);
        zzac.zzw(firebaseUser);
        zzac.zzw(com_google_android_gms_internal_zzbkb);
        return EmailAuthCredential.class.isAssignableFrom(authCredential.getClass()) ? zza(firebaseApp, (EmailAuthCredential) authCredential, firebaseUser, com_google_android_gms_internal_zzbkb) : zza(firebaseApp, authCredential, firebaseUser, com_google_android_gms_internal_zzbkb);
    }

    public Task<Void> zzb(@NonNull FirebaseApp firebaseApp, @NonNull FirebaseUser firebaseUser, @NonNull String str, @NonNull zzbkb com_google_android_gms_internal_zzbkb) {
        return doWrite(zza(new zzv(str).zze(firebaseApp).zze(firebaseUser).zzab(com_google_android_gms_internal_zzbkb)));
    }

    public Task<Void> zzb(@NonNull FirebaseApp firebaseApp, @NonNull String str) {
        return doWrite(zza(new zzo(str).zze(firebaseApp)));
    }

    public Task<AuthResult> zzb(@NonNull FirebaseApp firebaseApp, @NonNull String str, @NonNull String str2, @NonNull zzbkb com_google_android_gms_internal_zzbkb) {
        return doWrite(zza(new zzs(str, str2).zze(firebaseApp).zzab(com_google_android_gms_internal_zzbkb)));
    }

    public Task<Void> zzc(@NonNull FirebaseApp firebaseApp, @NonNull FirebaseUser firebaseUser, @NonNull String str, @NonNull zzbkb com_google_android_gms_internal_zzbkb) {
        return doWrite(zza(new zzw(str).zze(firebaseApp).zze(firebaseUser).zzab(com_google_android_gms_internal_zzbkb)));
    }

    public Task<Void> zzc(@NonNull FirebaseApp firebaseApp, @NonNull String str) {
        return doWrite(zza(new zzn(str).zze(firebaseApp)));
    }

    public Task<AuthResult> zzd(@NonNull FirebaseApp firebaseApp, @NonNull FirebaseUser firebaseUser, @NonNull String str, @NonNull zzbkb com_google_android_gms_internal_zzbkb) {
        zzac.zzw(firebaseApp);
        zzac.zzdv(str);
        zzac.zzw(firebaseUser);
        zzac.zzw(com_google_android_gms_internal_zzbkb);
        List providers = firebaseUser.getProviders();
        if ((providers != null && !providers.contains(str)) || firebaseUser.isAnonymous()) {
            return Tasks.forException(zzbix.zzcb(new Status(17016, str)));
        }
        Object obj = -1;
        switch (str.hashCode()) {
            case 1216985755:
                if (str.equals(EmailAuthProvider.PROVIDER_ID)) {
                    obj = null;
                    break;
                }
                break;
        }
        switch (obj) {
            case NewThreadWorker.PURGE_FREQUENCY /*0*/:
                return zza(firebaseApp, firebaseUser, com_google_android_gms_internal_zzbkb);
            default:
                return zza(firebaseApp, str, firebaseUser, com_google_android_gms_internal_zzbkb);
        }
    }

    public Task<ActionCodeResult> zzd(@NonNull FirebaseApp firebaseApp, @NonNull String str) {
        return doWrite(zza(new zzb(str).zze(firebaseApp)));
    }

    public Task<Void> zze(@NonNull FirebaseApp firebaseApp, @NonNull String str) {
        return doWrite(zza(new zza(str).zze(firebaseApp)));
    }

    public Task<String> zzf(@NonNull FirebaseApp firebaseApp, @NonNull String str) {
        return doWrite(zza(new zzy(str).zze(firebaseApp)));
    }
}
