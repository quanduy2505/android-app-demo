package com.google.firebase.auth;

import android.support.annotation.NonNull;
import com.google.android.gms.common.internal.zzac;

public class EmailAuthCredential extends AuthCredential {
    private String zzaiW;
    private String zzaig;

    EmailAuthCredential(@NonNull String str, @NonNull String str2) {
        this.zzaiW = zzac.zzdv(str);
        this.zzaig = zzac.zzdv(str2);
    }

    @NonNull
    public String getEmail() {
        return this.zzaiW;
    }

    @NonNull
    public String getPassword() {
        return this.zzaig;
    }

    @NonNull
    public String getProvider() {
        return EmailAuthProvider.PROVIDER_ID;
    }
}
