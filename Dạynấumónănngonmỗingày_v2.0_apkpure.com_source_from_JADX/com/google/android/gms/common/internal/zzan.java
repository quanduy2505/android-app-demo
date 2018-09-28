package com.google.android.gms.common.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.zza;

@Deprecated
public class zzan extends zza {
    public static final Creator<zzan> CREATOR;
    final int mVersionCode;

    static {
        CREATOR = new zzao();
    }

    zzan(int i) {
        this.mVersionCode = i;
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzao.zza(this, parcel, i);
    }
}
