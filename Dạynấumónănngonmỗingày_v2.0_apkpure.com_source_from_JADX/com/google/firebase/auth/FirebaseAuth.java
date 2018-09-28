package com.google.firebase.auth;

import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.internal.zzbiu;
import com.google.android.gms.internal.zzbix;
import com.google.android.gms.internal.zzbiz;
import com.google.android.gms.internal.zzbjc;
import com.google.android.gms.internal.zzbjp;
import com.google.android.gms.internal.zzbkb;
import com.google.android.gms.internal.zzbke;
import com.google.android.gms.internal.zzbkg;
import com.google.android.gms.internal.zzbkh;
import com.google.android.gms.internal.zzbkk;
import com.google.android.gms.internal.zzbkl;
import com.google.android.gms.internal.zzbkm;
import com.google.android.gms.internal.zzbql;
import com.google.android.gms.internal.zzbqm;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class FirebaseAuth implements zzbql {
    private static FirebaseAuth zzbVC;
    private static Map<String, FirebaseAuth> zzbha;
    private List<AuthStateListener> mListeners;
    private zzbkl zzbVA;
    private zzbkm zzbVB;
    private FirebaseApp zzbVx;
    private zzbiu zzbVy;
    private FirebaseUser zzbVz;

    /* renamed from: com.google.firebase.auth.FirebaseAuth.1 */
    class C07131 implements Runnable {
        final /* synthetic */ AuthStateListener zzbVD;
        final /* synthetic */ FirebaseAuth zzbVE;

        C07131(FirebaseAuth firebaseAuth, AuthStateListener authStateListener) {
            this.zzbVE = firebaseAuth;
            this.zzbVD = authStateListener;
        }

        public void run() {
            this.zzbVD.onAuthStateChanged(this.zzbVE);
        }
    }

    /* renamed from: com.google.firebase.auth.FirebaseAuth.2 */
    class C07142 implements Runnable {
        final /* synthetic */ FirebaseAuth zzbVE;
        final /* synthetic */ zzbqm zzbVF;

        C07142(FirebaseAuth firebaseAuth, zzbqm com_google_android_gms_internal_zzbqm) {
            this.zzbVE = firebaseAuth;
            this.zzbVF = com_google_android_gms_internal_zzbqm;
        }

        public void run() {
            this.zzbVE.zzbVx.zza(this.zzbVF);
            for (AuthStateListener onAuthStateChanged : this.zzbVE.mListeners) {
                onAuthStateChanged.onAuthStateChanged(this.zzbVE);
            }
        }
    }

    public interface AuthStateListener {
        void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth);
    }

    /* renamed from: com.google.firebase.auth.FirebaseAuth.3 */
    class C12163 implements zzbkb {
        final /* synthetic */ FirebaseAuth zzbVE;

        C12163(FirebaseAuth firebaseAuth) {
            this.zzbVE = firebaseAuth;
        }

        public void zza(@NonNull zzbjp com_google_android_gms_internal_zzbjp, @NonNull FirebaseUser firebaseUser) {
            this.zzbVE.zza(firebaseUser, com_google_android_gms_internal_zzbjp, true);
        }
    }

    /* renamed from: com.google.firebase.auth.FirebaseAuth.4 */
    class C12174 implements zzbkk {
        final /* synthetic */ FirebaseAuth zzbVE;
        final /* synthetic */ FirebaseUser zzbVG;

        C12174(FirebaseAuth firebaseAuth, FirebaseUser firebaseUser) {
            this.zzbVE = firebaseAuth;
            this.zzbVG = firebaseUser;
        }

        public void zzTU() {
            if (this.zzbVE.zzbVz.getUid().equalsIgnoreCase(this.zzbVG.getUid())) {
                this.zzbVE.zzTS();
            }
        }
    }

    class zza implements zzbkb {
        final /* synthetic */ FirebaseAuth zzbVE;

        zza(FirebaseAuth firebaseAuth) {
            this.zzbVE = firebaseAuth;
        }

        public void zza(@NonNull zzbjp com_google_android_gms_internal_zzbjp, @NonNull FirebaseUser firebaseUser) {
            zzac.zzw(com_google_android_gms_internal_zzbjp);
            zzac.zzw(firebaseUser);
            firebaseUser.zza(com_google_android_gms_internal_zzbjp);
            this.zzbVE.zza(firebaseUser, com_google_android_gms_internal_zzbjp, true);
        }
    }

    static {
        zzbha = new ArrayMap();
    }

    public FirebaseAuth(FirebaseApp firebaseApp) {
        this(firebaseApp, zzb(firebaseApp), new zzbkl(firebaseApp.getApplicationContext(), firebaseApp.zzTu(), zzbiz.zzUg()));
    }

    FirebaseAuth(FirebaseApp firebaseApp, zzbiu com_google_android_gms_internal_zzbiu, zzbkl com_google_android_gms_internal_zzbkl) {
        this.zzbVx = (FirebaseApp) zzac.zzw(firebaseApp);
        this.zzbVy = (zzbiu) zzac.zzw(com_google_android_gms_internal_zzbiu);
        this.zzbVA = (zzbkl) zzac.zzw(com_google_android_gms_internal_zzbkl);
        this.mListeners = new CopyOnWriteArrayList();
        this.zzbVB = zzbkm.zzUK();
        zzTT();
    }

    public static FirebaseAuth getInstance() {
        return zzc(FirebaseApp.getInstance());
    }

    @Keep
    public static FirebaseAuth getInstance(@NonNull FirebaseApp firebaseApp) {
        return zzc(firebaseApp);
    }

    static zzbiu zzb(FirebaseApp firebaseApp) {
        return zzbjc.zza(firebaseApp.getApplicationContext(), new com.google.android.gms.internal.zzbjc.zza.zza(firebaseApp.getOptions().getApiKey()).zzUj());
    }

    private static FirebaseAuth zzc(@NonNull FirebaseApp firebaseApp) {
        return zzd(firebaseApp);
    }

    private static synchronized FirebaseAuth zzd(@NonNull FirebaseApp firebaseApp) {
        FirebaseAuth firebaseAuth;
        synchronized (FirebaseAuth.class) {
            firebaseAuth = (FirebaseAuth) zzbha.get(firebaseApp.zzTu());
            if (firebaseAuth == null) {
                firebaseAuth = new zzbkg(firebaseApp);
                firebaseApp.zza((zzbql) firebaseAuth);
                if (zzbVC == null) {
                    zzbVC = firebaseAuth;
                }
                zzbha.put(firebaseApp.zzTu(), firebaseAuth);
            }
        }
        return firebaseAuth;
    }

    public void addAuthStateListener(@NonNull AuthStateListener authStateListener) {
        this.mListeners.add(authStateListener);
        this.zzbVB.execute(new C07131(this, authStateListener));
    }

    @NonNull
    public Task<Void> applyActionCode(@NonNull String str) {
        zzac.zzdv(str);
        return this.zzbVy.zze(this.zzbVx, str);
    }

    @NonNull
    public Task<ActionCodeResult> checkActionCode(@NonNull String str) {
        zzac.zzdv(str);
        return this.zzbVy.zzd(this.zzbVx, str);
    }

    @NonNull
    public Task<Void> confirmPasswordReset(@NonNull String str, @NonNull String str2) {
        zzac.zzdv(str);
        zzac.zzdv(str2);
        return this.zzbVy.zza(this.zzbVx, str, str2);
    }

    @NonNull
    public Task<AuthResult> createUserWithEmailAndPassword(@NonNull String str, @NonNull String str2) {
        zzac.zzdv(str);
        zzac.zzdv(str2);
        return this.zzbVy.zza(this.zzbVx, str, str2, new zza(this));
    }

    @NonNull
    public Task<ProviderQueryResult> fetchProvidersForEmail(@NonNull String str) {
        zzac.zzdv(str);
        return this.zzbVy.zza(this.zzbVx, str);
    }

    @Nullable
    public FirebaseUser getCurrentUser() {
        return this.zzbVz;
    }

    public void removeAuthStateListener(@NonNull AuthStateListener authStateListener) {
        this.mListeners.remove(authStateListener);
    }

    @NonNull
    public Task<Void> sendPasswordResetEmail(@NonNull String str) {
        zzac.zzdv(str);
        return this.zzbVy.zzb(this.zzbVx, str);
    }

    @NonNull
    public Task<AuthResult> signInAnonymously() {
        return (this.zzbVz == null || !this.zzbVz.isAnonymous()) ? this.zzbVy.zza(this.zzbVx, new zza(this)) : Tasks.forResult(new zzbke((zzbkh) this.zzbVz));
    }

    @NonNull
    public Task<AuthResult> signInWithCredential(@NonNull AuthCredential authCredential) {
        zzac.zzw(authCredential);
        if (!EmailAuthCredential.class.isAssignableFrom(authCredential.getClass())) {
            return this.zzbVy.zza(this.zzbVx, authCredential, new zza(this));
        }
        EmailAuthCredential emailAuthCredential = (EmailAuthCredential) authCredential;
        return this.zzbVy.zzb(this.zzbVx, emailAuthCredential.getEmail(), emailAuthCredential.getPassword(), new zza(this));
    }

    @NonNull
    public Task<AuthResult> signInWithCustomToken(@NonNull String str) {
        zzac.zzdv(str);
        return this.zzbVy.zza(this.zzbVx, str, new zza(this));
    }

    @NonNull
    public Task<AuthResult> signInWithEmailAndPassword(@NonNull String str, @NonNull String str2) {
        zzac.zzdv(str);
        zzac.zzdv(str2);
        return this.zzbVy.zzb(this.zzbVx, str, str2, new zza(this));
    }

    public void signOut() {
        zzTS();
    }

    @NonNull
    public Task<String> verifyPasswordResetCode(@NonNull String str) {
        zzac.zzdv(str);
        return this.zzbVy.zzf(this.zzbVx, str);
    }

    public void zzTS() {
        if (this.zzbVz != null) {
            this.zzbVA.zzh(this.zzbVz);
            this.zzbVz = null;
        }
        this.zzbVA.zzUJ();
        zza(null);
    }

    protected void zzTT() {
        this.zzbVz = this.zzbVA.zzUI();
        if (this.zzbVz != null) {
            zzbjp zzg = this.zzbVA.zzg(this.zzbVz);
            if (zzg != null) {
                zza(this.zzbVz, zzg, false);
            }
        }
    }

    @NonNull
    public Task<Void> zza(@NonNull FirebaseUser firebaseUser, @NonNull AuthCredential authCredential) {
        zzac.zzw(firebaseUser);
        zzac.zzw(authCredential);
        if (!EmailAuthCredential.class.isAssignableFrom(authCredential.getClass())) {
            return this.zzbVy.zza(this.zzbVx, firebaseUser, authCredential, new zza(this));
        }
        EmailAuthCredential emailAuthCredential = (EmailAuthCredential) authCredential;
        return this.zzbVy.zza(this.zzbVx, firebaseUser, emailAuthCredential.getEmail(), emailAuthCredential.getPassword(), new zza(this));
    }

    @NonNull
    public Task<Void> zza(@NonNull FirebaseUser firebaseUser, @NonNull UserProfileChangeRequest userProfileChangeRequest) {
        zzac.zzw(firebaseUser);
        zzac.zzw(userProfileChangeRequest);
        return this.zzbVy.zza(this.zzbVx, firebaseUser, userProfileChangeRequest, new zza(this));
    }

    @NonNull
    public Task<AuthResult> zza(@NonNull FirebaseUser firebaseUser, @NonNull String str) {
        zzac.zzdv(str);
        zzac.zzw(firebaseUser);
        return this.zzbVy.zzd(this.zzbVx, firebaseUser, str, new zza(this));
    }

    @NonNull
    public Task<GetTokenResult> zza(@Nullable FirebaseUser firebaseUser, boolean z) {
        if (firebaseUser == null) {
            return Tasks.forException(zzbix.zzcb(new Status(17495)));
        }
        zzbjp zzTW = this.zzbVz.zzTW();
        return (!zzTW.isValid() || z) ? this.zzbVy.zza(this.zzbVx, firebaseUser, zzTW.zzUs(), new C12163(this)) : Tasks.forResult(new GetTokenResult(zzTW.getAccessToken()));
    }

    public void zza(@Nullable FirebaseUser firebaseUser) {
        if (firebaseUser != null) {
            String valueOf = String.valueOf(firebaseUser.getUid());
            Log.d("FirebaseAuth", new StringBuilder(String.valueOf(valueOf).length() + 36).append("Notifying listeners about user ( ").append(valueOf).append(" ).").toString());
        } else {
            Log.d("FirebaseAuth", "Notifying listeners about a sign-out event.");
        }
        this.zzbVB.execute(new C07142(this, new zzbqm(firebaseUser != null ? firebaseUser.zzTY() : null)));
    }

    public void zza(@NonNull FirebaseUser firebaseUser, @NonNull zzbjp com_google_android_gms_internal_zzbjp, boolean z) {
        boolean z2 = true;
        zzac.zzw(firebaseUser);
        zzac.zzw(com_google_android_gms_internal_zzbjp);
        if (this.zzbVz != null) {
            boolean z3 = !this.zzbVz.zzTW().getAccessToken().equals(com_google_android_gms_internal_zzbjp.getAccessToken());
            if (this.zzbVz.getUid().equals(firebaseUser.getUid()) && !z3) {
                z2 = false;
            }
        }
        if (z2) {
            if (this.zzbVz != null) {
                this.zzbVz.zza(com_google_android_gms_internal_zzbjp);
            }
            zza(firebaseUser, z, false);
            zza(this.zzbVz);
        }
        if (z) {
            this.zzbVA.zza(firebaseUser, com_google_android_gms_internal_zzbjp);
        }
    }

    public void zza(@NonNull FirebaseUser firebaseUser, boolean z, boolean z2) {
        zzac.zzw(firebaseUser);
        if (this.zzbVz == null) {
            this.zzbVz = firebaseUser;
        } else {
            this.zzbVz.zzaT(firebaseUser.isAnonymous());
            this.zzbVz.zzR(firebaseUser.getProviderData());
        }
        if (z) {
            this.zzbVA.zzf(this.zzbVz);
        }
        if (z2) {
            zza(this.zzbVz);
        }
    }

    @NonNull
    public Task<GetTokenResult> zzaS(boolean z) {
        return zza(this.zzbVz, z);
    }

    @NonNull
    public Task<Void> zzb(@NonNull FirebaseUser firebaseUser) {
        zzac.zzw(firebaseUser);
        return this.zzbVy.zzb(this.zzbVx, firebaseUser, new zza(this));
    }

    @NonNull
    public Task<AuthResult> zzb(@NonNull FirebaseUser firebaseUser, @NonNull AuthCredential authCredential) {
        zzac.zzw(authCredential);
        zzac.zzw(firebaseUser);
        return this.zzbVy.zzb(this.zzbVx, firebaseUser, authCredential, new zza(this));
    }

    @NonNull
    public Task<Void> zzb(@NonNull FirebaseUser firebaseUser, @NonNull String str) {
        zzac.zzw(firebaseUser);
        zzac.zzdv(str);
        return this.zzbVy.zzb(this.zzbVx, firebaseUser, str, new zza(this));
    }

    @NonNull
    public Task<Void> zzc(@NonNull FirebaseUser firebaseUser) {
        zzac.zzw(firebaseUser);
        return this.zzbVy.zza(firebaseUser, new C12174(this, firebaseUser));
    }

    @NonNull
    public Task<Void> zzc(@NonNull FirebaseUser firebaseUser, @NonNull String str) {
        zzac.zzw(firebaseUser);
        zzac.zzdv(str);
        return this.zzbVy.zzc(this.zzbVx, firebaseUser, str, new zza(this));
    }

    @NonNull
    public Task<Void> zzix(@NonNull String str) {
        zzac.zzdv(str);
        return this.zzbVy.zzc(this.zzbVx, str);
    }
}
