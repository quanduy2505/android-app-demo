package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.facebook.internal.ServerProtocol;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.zzac;

public class zzbjz extends zza {
    public static final Creator<zzbjz> CREATOR;
    @zzbjd
    public final int mVersionCode;
    @zzbsg("postBody")
    private String zzHF;
    @Nullable
    @zzbjd
    private String zzaiW;
    @zzbjd
    private String zzaix;
    @zzbjd
    private String zzbBR;
    @zzbsg("requestUri")
    private String zzbWE;
    @zzbsg("idToken")
    private String zzbWF;
    @zzbsg("oauthTokenSecret")
    private String zzbWG;
    @zzbsg("returnSecureToken")
    private boolean zzbWH;
    @zzbjd
    private String zzbWp;

    static {
        CREATOR = new zzbka();
    }

    public zzbjz() {
        this.mVersionCode = 2;
        this.zzbWH = true;
    }

    zzbjz(int i, String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, boolean z) {
        this.mVersionCode = i;
        this.zzbWE = str;
        this.zzbWF = str2;
        this.zzaix = str3;
        this.zzbBR = str4;
        this.zzbWp = str5;
        this.zzaiW = str6;
        this.zzHF = str7;
        this.zzbWG = str8;
        this.zzbWH = z;
    }

    public zzbjz(@Nullable String str, @Nullable String str2, String str3, @Nullable String str4, @Nullable String str5) {
        this.mVersionCode = 2;
        this.zzbWE = "http://localhost";
        this.zzaix = str;
        this.zzbBR = str2;
        this.zzbWG = str5;
        this.zzbWH = true;
        if (TextUtils.isEmpty(this.zzaix) && TextUtils.isEmpty(this.zzbBR)) {
            throw new IllegalArgumentException("Both idToken, and accessToken cannot be null");
        }
        this.zzbWp = zzac.zzdv(str3);
        this.zzaiW = str4;
        StringBuilder stringBuilder = new StringBuilder();
        if (!TextUtils.isEmpty(this.zzaix)) {
            stringBuilder.append("id_token").append("=").append(this.zzaix).append("&");
        }
        if (!TextUtils.isEmpty(this.zzbBR)) {
            stringBuilder.append(ServerProtocol.DIALOG_PARAM_ACCESS_TOKEN).append("=").append(this.zzbBR).append("&");
        }
        if (!TextUtils.isEmpty(this.zzaiW)) {
            stringBuilder.append("identifier").append("=").append(this.zzaiW).append("&");
        }
        if (!TextUtils.isEmpty(this.zzbWG)) {
            stringBuilder.append("oauth_token_secret").append("=").append(this.zzbWG).append("&");
        }
        stringBuilder.append("providerId").append("=").append(this.zzbWp);
        this.zzHF = stringBuilder.toString();
    }

    public String getAccessToken() {
        return this.zzbBR;
    }

    @Nullable
    public String getEmail() {
        return this.zzaiW;
    }

    public String getIdToken() {
        return this.zzaix;
    }

    public String getProviderId() {
        return this.zzbWp;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzbka.zza(this, parcel, i);
    }

    public String zzUC() {
        return this.zzbWE;
    }

    public String zzUD() {
        return this.zzbWF;
    }

    public String zzUE() {
        return this.zzbWG;
    }

    public boolean zzUF() {
        return this.zzbWH;
    }

    public String zzgc() {
        return this.zzHF;
    }
}
