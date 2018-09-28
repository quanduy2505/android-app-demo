package com.google.firebase.auth;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.internal.zzbjp;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import java.util.List;

public abstract class FirebaseUser implements UserInfo {

    /* renamed from: com.google.firebase.auth.FirebaseUser.1 */
    class C12181 implements Continuation<GetTokenResult, Task<Void>> {
        final /* synthetic */ FirebaseUser zzbVI;

        C12181(FirebaseUser firebaseUser) {
            this.zzbVI = firebaseUser;
        }

        public /* synthetic */ Object then(@NonNull Task task) throws Exception {
            return zze(task);
        }

        public Task<Void> zze(@NonNull Task<GetTokenResult> task) throws Exception {
            return this.zzbVI.zzTZ().zzix(((GetTokenResult) task.getResult()).getToken());
        }
    }

    private FirebaseAuth zzTZ() {
        return FirebaseAuth.getInstance(zzTV());
    }

    @NonNull
    public Task<Void> delete() {
        return zzTZ().zzc(this);
    }

    @Nullable
    public abstract String getDisplayName();

    @Nullable
    public abstract String getEmail();

    @Nullable
    public abstract Uri getPhotoUrl();

    @NonNull
    public abstract List<? extends UserInfo> getProviderData();

    @NonNull
    public abstract String getProviderId();

    @Nullable
    public abstract List<String> getProviders();

    @NonNull
    public Task<GetTokenResult> getToken(boolean z) {
        return zzTZ().zza(this, z);
    }

    @NonNull
    public abstract String getUid();

    public abstract boolean isAnonymous();

    @NonNull
    public Task<AuthResult> linkWithCredential(@NonNull AuthCredential authCredential) {
        zzac.zzw(authCredential);
        return zzTZ().zzb(this, authCredential);
    }

    public Task<Void> reauthenticate(@NonNull AuthCredential authCredential) {
        zzac.zzw(authCredential);
        return zzTZ().zza(this, authCredential);
    }

    @NonNull
    public Task<Void> reload() {
        return zzTZ().zzb(this);
    }

    @NonNull
    public Task<Void> sendEmailVerification() {
        return zzTZ().zza(this, false).continueWithTask(new C12181(this));
    }

    public Task<AuthResult> unlink(@NonNull String str) {
        zzac.zzdv(str);
        return zzTZ().zza(this, str);
    }

    @NonNull
    public Task<Void> updateEmail(@NonNull String str) {
        zzac.zzdv(str);
        return zzTZ().zzb(this, str);
    }

    @NonNull
    public Task<Void> updatePassword(@NonNull String str) {
        zzac.zzdv(str);
        return zzTZ().zzc(this, str);
    }

    @NonNull
    public Task<Void> updateProfile(@NonNull UserProfileChangeRequest userProfileChangeRequest) {
        zzac.zzw(userProfileChangeRequest);
        return zzTZ().zza(this, userProfileChangeRequest);
    }

    @NonNull
    public abstract FirebaseUser zzR(@NonNull List<? extends UserInfo> list);

    @NonNull
    public abstract FirebaseApp zzTV();

    @NonNull
    public abstract zzbjp zzTW();

    @NonNull
    public abstract String zzTX();

    @NonNull
    public abstract String zzTY();

    public abstract void zza(@NonNull zzbjp com_google_android_gms_internal_zzbjp);

    public abstract FirebaseUser zzaT(boolean z);
}
