package com.google.android.gms.internal;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzac;
import com.google.firebase.auth.UserInfo;

public class zzbkf implements UserInfo {
    @Nullable
    @zzbsg("photoUrl")
    private String zzaPq;
    @zzbsg("userId")
    @NonNull
    private String zzach;
    @Nullable
    @zzbsg("email")
    private String zzaiW;
    @Nullable
    @zzbsg("displayName")
    private String zzaiX;
    @Nullable
    @zzbjd
    private Uri zzbVM;
    @zzbsg("providerId")
    @NonNull
    private String zzbWp;
    @zzbsg("isEmailVerified")
    private boolean zzbWs;
    @Nullable
    @zzbsg("rawUserInfo")
    private String zzbWz;

    public zzbkf(@NonNull zzbjl com_google_android_gms_internal_zzbjl, @NonNull String str) {
        zzac.zzw(com_google_android_gms_internal_zzbjl);
        zzac.zzdv(str);
        this.zzach = zzac.zzdv(com_google_android_gms_internal_zzbjl.getLocalId());
        this.zzbWp = str;
        this.zzaiW = com_google_android_gms_internal_zzbjl.getEmail();
        this.zzaiX = com_google_android_gms_internal_zzbjl.getDisplayName();
        Uri photoUri = com_google_android_gms_internal_zzbjl.getPhotoUri();
        if (photoUri != null) {
            this.zzaPq = photoUri.toString();
            this.zzbVM = photoUri;
        }
        this.zzbWs = com_google_android_gms_internal_zzbjl.isEmailVerified();
        this.zzbWz = null;
    }

    public zzbkf(@NonNull zzbjr com_google_android_gms_internal_zzbjr) {
        zzac.zzw(com_google_android_gms_internal_zzbjr);
        this.zzach = zzac.zzdv(com_google_android_gms_internal_zzbjr.zzUw());
        this.zzbWp = zzac.zzdv(com_google_android_gms_internal_zzbjr.getProviderId());
        this.zzaiX = com_google_android_gms_internal_zzbjr.getDisplayName();
        Uri photoUri = com_google_android_gms_internal_zzbjr.getPhotoUri();
        if (photoUri != null) {
            this.zzaPq = photoUri.toString();
            this.zzbVM = photoUri;
        }
        this.zzaiW = null;
        this.zzbWs = false;
        this.zzbWz = com_google_android_gms_internal_zzbjr.getRawUserInfo();
    }

    @Nullable
    public String getDisplayName() {
        return this.zzaiX;
    }

    @Nullable
    public String getEmail() {
        return this.zzaiW;
    }

    @Nullable
    public Uri getPhotoUrl() {
        if (!TextUtils.isEmpty(this.zzaPq) && this.zzbVM == null) {
            this.zzbVM = Uri.parse(this.zzaPq);
        }
        return this.zzbVM;
    }

    @NonNull
    public String getProviderId() {
        return this.zzbWp;
    }

    @NonNull
    public String getUid() {
        return this.zzach;
    }

    public boolean isEmailVerified() {
        return this.zzbWs;
    }
}
