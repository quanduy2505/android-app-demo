package com.google.android.gms.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.util.zzh;

public class zzbjp extends zza {
    public static final Creator<zzbjp> CREATOR;
    @zzbjd
    public final int mVersionCode;
    @zzbsg("access_token")
    private String zzbBR;
    @zzbsg("refresh_token")
    private String zzbVO;
    @zzbsg("expires_in")
    private Long zzbWv;
    @zzbsg("token_type")
    private String zzbWw;
    @zzbsg("issued_at")
    private Long zzbWx;

    static {
        CREATOR = new zzbjq();
    }

    public zzbjp() {
        this.mVersionCode = 1;
        this.zzbWx = Long.valueOf(System.currentTimeMillis());
    }

    zzbjp(int i, String str, String str2, Long l, String str3, Long l2) {
        this.mVersionCode = i;
        this.zzbVO = str;
        this.zzbBR = str2;
        this.zzbWv = l;
        this.zzbWw = str3;
        this.zzbWx = l2;
    }

    public String getAccessToken() {
        return this.zzbBR;
    }

    public boolean isValid() {
        return zzh.zzyv().currentTimeMillis() + 300000 < this.zzbWx.longValue() + (this.zzbWv.longValue() * 1000);
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzbjq.zza(this, parcel, i);
    }

    public String zzUs() {
        return this.zzbVO;
    }

    public long zzUt() {
        return this.zzbWv == null ? 0 : this.zzbWv.longValue();
    }

    @Nullable
    public String zzUu() {
        return this.zzbWw;
    }

    public long zzUv() {
        return this.zzbWx.longValue();
    }

    public void zziz(@NonNull String str) {
        this.zzbVO = zzac.zzdv(str);
    }
}
