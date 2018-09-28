package com.google.firebase.auth;

import android.support.annotation.NonNull;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.internal.zzbjz;

public class GithubAuthCredential extends AuthCredential {
    private String zzahI;

    GithubAuthCredential(@NonNull String str) {
        this.zzahI = zzac.zzdv(str);
    }

    public static zzbjz zza(@NonNull GithubAuthCredential githubAuthCredential) {
        zzac.zzw(githubAuthCredential);
        return new zzbjz(null, githubAuthCredential.getToken(), githubAuthCredential.getProvider(), null, null);
    }

    public String getProvider() {
        return GithubAuthProvider.PROVIDER_ID;
    }

    public String getToken() {
        return this.zzahI;
    }
}
