package com.google.android.gms.internal;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.gms.common.internal.zzac;
import com.google.firebase.auth.ProviderQueryResult;
import java.util.List;

public class zzbki implements ProviderQueryResult {
    private List<String> zzbWP;

    public zzbki(@NonNull zzbjj com_google_android_gms_internal_zzbjj) {
        zzac.zzw(com_google_android_gms_internal_zzbjj);
        this.zzbWP = com_google_android_gms_internal_zzbjj.getAllProviders();
    }

    @Nullable
    public List<String> getProviders() {
        return this.zzbWP;
    }
}
