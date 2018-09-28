package com.google.firebase.auth;

import android.support.annotation.NonNull;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.internal.zzbjz;

public class FacebookAuthCredential extends AuthCredential {
    private final String zzbBR;

    FacebookAuthCredential(@NonNull String str) {
        this.zzbBR = zzac.zzdv(str);
    }

    public static zzbjz zza(@NonNull FacebookAuthCredential facebookAuthCredential) {
        zzac.zzw(facebookAuthCredential);
        return new zzbjz(null, facebookAuthCredential.getAccessToken(), facebookAuthCredential.getProvider(), null, null);
    }

    public String getAccessToken() {
        return this.zzbBR;
    }

    public String getProvider() {
        return FacebookAuthProvider.PROVIDER_ID;
    }
}
