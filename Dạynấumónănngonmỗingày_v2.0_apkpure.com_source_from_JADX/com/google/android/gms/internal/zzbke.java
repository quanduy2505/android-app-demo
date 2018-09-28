package com.google.android.gms.internal;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.gms.common.internal.zzac;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

public class zzbke implements AuthResult {
    private zzbkh zzbWJ;

    public zzbke(@NonNull zzbkh com_google_android_gms_internal_zzbkh) {
        this.zzbWJ = (zzbkh) zzac.zzw(com_google_android_gms_internal_zzbkh);
    }

    @Nullable
    public FirebaseUser getUser() {
        return this.zzbWJ;
    }
}
