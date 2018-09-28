package com.google.android.gms.common.internal;

import com.google.android.gms.common.internal.safeparcel.zza;

public abstract class DowngradeableSafeParcel extends zza implements ReflectedParcelable {
    private static final Object zzaEi;
    private static ClassLoader zzaEj;
    private static Integer zzaEk;
    private boolean zzaEl;

    static {
        zzaEi = new Object();
        zzaEj = null;
        zzaEk = null;
    }

    public DowngradeableSafeParcel() {
        this.zzaEl = false;
    }

    protected static boolean zzdp(String str) {
        zzxn();
        return true;
    }

    protected static ClassLoader zzxn() {
        synchronized (zzaEi) {
        }
        return null;
    }

    protected static Integer zzxo() {
        synchronized (zzaEi) {
        }
        return null;
    }
}
