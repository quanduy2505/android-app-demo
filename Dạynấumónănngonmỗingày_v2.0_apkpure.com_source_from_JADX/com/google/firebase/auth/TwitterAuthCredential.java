package com.google.firebase.auth;

import android.support.annotation.NonNull;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.internal.zzbjz;

public class TwitterAuthCredential extends AuthCredential {
    private String zzahI;
    private String zzbVJ;

    TwitterAuthCredential(@NonNull String str, @NonNull String str2) {
        this.zzahI = zzac.zzdv(str);
        this.zzbVJ = zzac.zzdv(str2);
    }

    public static zzbjz zza(@NonNull TwitterAuthCredential twitterAuthCredential) {
        zzac.zzw(twitterAuthCredential);
        return new zzbjz(null, twitterAuthCredential.getToken(), twitterAuthCredential.getProvider(), null, twitterAuthCredential.zzUa());
    }

    public String getProvider() {
        return TwitterAuthProvider.PROVIDER_ID;
    }

    @NonNull
    public String getToken() {
        return this.zzahI;
    }

    @NonNull
    public String zzUa() {
        return this.zzbVJ;
    }
}
