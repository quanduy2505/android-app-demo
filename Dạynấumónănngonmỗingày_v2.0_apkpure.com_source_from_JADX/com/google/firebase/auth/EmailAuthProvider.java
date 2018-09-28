package com.google.firebase.auth;

import android.support.annotation.NonNull;
import com.google.android.gms.common.internal.zzac;

public class EmailAuthProvider {
    public static final String PROVIDER_ID = "password";

    private EmailAuthProvider() {
    }

    public static AuthCredential getCredential(@NonNull String str, @NonNull String str2) {
        zzac.zzdv(str);
        zzac.zzdv(str2);
        return new EmailAuthCredential(str, str2);
    }
}
