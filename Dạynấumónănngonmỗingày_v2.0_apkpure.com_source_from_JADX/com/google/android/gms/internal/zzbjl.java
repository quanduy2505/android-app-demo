package com.google.android.gms.internal;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.google.android.gms.common.internal.safeparcel.zza;
import java.util.List;

public class zzbjl extends zza {
    public static final Creator<zzbjl> CREATOR;
    @zzbjd
    public final int mVersionCode;
    @zzbsg("localId")
    private String zzaMA;
    @zzbsg("photoUrl")
    private String zzaPq;
    @zzbsg("email")
    private String zzaiW;
    @zzbsg("displayName")
    private String zzaiX;
    @zzbsg("passwordHash")
    private String zzaig;
    @zzbsg("emailVerified")
    private boolean zzbWs;
    @zzbsg("providerUserInfo")
    private zzbjt zzbWt;

    static {
        CREATOR = new zzbjm();
    }

    public zzbjl() {
        this.mVersionCode = 1;
        this.zzbWt = new zzbjt();
    }

    zzbjl(int i, String str, String str2, boolean z, String str3, String str4, zzbjt com_google_android_gms_internal_zzbjt, String str5) {
        this.mVersionCode = i;
        this.zzaMA = str;
        this.zzaiW = str2;
        this.zzbWs = z;
        this.zzaiX = str3;
        this.zzaPq = str4;
        this.zzbWt = com_google_android_gms_internal_zzbjt == null ? zzbjt.zzUx() : zzbjt.zza(com_google_android_gms_internal_zzbjt);
        this.zzaig = str5;
    }

    @Nullable
    public String getDisplayName() {
        return this.zzaiX;
    }

    @Nullable
    public String getEmail() {
        return this.zzaiW;
    }

    @NonNull
    public String getLocalId() {
        return this.zzaMA;
    }

    @Nullable
    public String getPassword() {
        return this.zzaig;
    }

    @Nullable
    public Uri getPhotoUri() {
        return !TextUtils.isEmpty(this.zzaPq) ? Uri.parse(this.zzaPq) : null;
    }

    public boolean isEmailVerified() {
        return this.zzbWs;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzbjm.zza(this, parcel, i);
    }

    @Nullable
    public String zzUb() {
        return this.zzaPq;
    }

    @NonNull
    public List<zzbjr> zzUp() {
        return this.zzbWt.zzUp();
    }

    public zzbjt zzUq() {
        return this.zzbWt;
    }
}
