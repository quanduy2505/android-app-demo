package com.google.android.gms.internal;

import java.io.IOException;

public final class zzbuw {
    public static final int[] zzcsi;
    public static final long[] zzcsj;
    public static final float[] zzcsk;
    public static final double[] zzcsl;
    public static final boolean[] zzcsm;
    public static final String[] zzcsn;
    public static final byte[][] zzcso;
    public static final byte[] zzcsp;

    static {
        zzcsi = new int[0];
        zzcsj = new long[0];
        zzcsk = new float[0];
        zzcsl = new double[0];
        zzcsm = new boolean[0];
        zzcsn = new String[0];
        zzcso = new byte[0][];
        zzcsp = new byte[0];
    }

    public static int zzK(int i, int i2) {
        return (i << 3) | i2;
    }

    public static boolean zzb(zzbul com_google_android_gms_internal_zzbul, int i) throws IOException {
        return com_google_android_gms_internal_zzbul.zzqh(i);
    }

    public static final int zzc(zzbul com_google_android_gms_internal_zzbul, int i) throws IOException {
        int i2 = 1;
        int position = com_google_android_gms_internal_zzbul.getPosition();
        com_google_android_gms_internal_zzbul.zzqh(i);
        while (com_google_android_gms_internal_zzbul.zzacu() == i) {
            com_google_android_gms_internal_zzbul.zzqh(i);
            i2++;
        }
        com_google_android_gms_internal_zzbul.zzql(position);
        return i2;
    }

    static int zzqA(int i) {
        return i & 7;
    }

    public static int zzqB(int i) {
        return i >>> 3;
    }
}
