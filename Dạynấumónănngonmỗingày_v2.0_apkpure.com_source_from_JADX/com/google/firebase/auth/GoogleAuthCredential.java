package com.google.firebase.auth;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.internal.zzbjz;

public class GoogleAuthCredential extends AuthCredential {
    private final String zzaix;
    private final String zzbBR;

    GoogleAuthCredential(@Nullable String str, @Nullable String str2) {
        if (str == null && str2 == null) {
            throw new IllegalArgumentException("Must specify an idToken or an accessToken.");
        }
        this.zzaix = zzaj(str, "idToken");
        this.zzbBR = zzaj(str2, "accessToken");
    }

    public static zzbjz zza(@NonNull GoogleAuthCredential googleAuthCredential) {
        zzac.zzw(googleAuthCredential);
        return new zzbjz(googleAuthCredential.getIdToken(), googleAuthCredential.getAccessToken(), googleAuthCredential.getProvider(), null, null);
    }

    private static String zzaj(String str, String str2) {
        if (str == null || !TextUtils.isEmpty(str)) {
            return str;
        }
        throw new IllegalArgumentException(String.valueOf(str2).concat(" must not be empty"));
    }

    @Nullable
    public String getAccessToken() {
        return this.zzbBR;
    }

    @Nullable
    public String getIdToken() {
        return this.zzaix;
    }

    public String getProvider() {
        return GoogleAuthProvider.PROVIDER_ID;
    }
}
