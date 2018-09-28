package com.google.android.gms.internal;

import android.support.annotation.NonNull;
import android.support.v4.view.PointerIconCompat;
import android.support.v7.widget.helper.ItemTouchHelper.Callback;
import android.util.Log;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.util.zze;
import com.google.android.gms.common.util.zzh;
import com.google.firebase.FirebaseApp;
import java.util.Random;
import org.apache.http.HttpStatus;

public class zzbqw {
    static zze zzaiV;
    private static Random zzbQY;
    static zzbqy zzclY;
    private volatile boolean zzJ;
    private FirebaseApp zzciR;
    private long zzclZ;

    static {
        zzclY = new zzbqz();
        zzaiV = zzh.zzyv();
        zzbQY = new Random();
    }

    public zzbqw(FirebaseApp firebaseApp, long j) {
        this.zzciR = firebaseApp;
        this.zzclZ = j;
    }

    public void cancel() {
        this.zzJ = true;
    }

    public void reset() {
        this.zzJ = false;
    }

    public void zza(@NonNull zzbrf com_google_android_gms_internal_zzbrf, boolean z) {
        zzac.zzw(com_google_android_gms_internal_zzbrf);
        long elapsedRealtime = zzaiV.elapsedRealtime() + this.zzclZ;
        if (z) {
            com_google_android_gms_internal_zzbrf.zza(zzbrb.zzi(this.zzciR), this.zzciR.getApplicationContext());
        } else {
            com_google_android_gms_internal_zzbrf.zzjO(zzbrb.zzi(this.zzciR));
        }
        int i = PointerIconCompat.TYPE_DEFAULT;
        while (zzaiV.elapsedRealtime() + ((long) i) <= elapsedRealtime && !com_google_android_gms_internal_zzbrf.zzabn() && zzqa(com_google_android_gms_internal_zzbrf.getResultCode())) {
            try {
                zzclY.zzqb(zzbQY.nextInt(Callback.DEFAULT_SWIPE_ANIMATION_DURATION) + i);
                if (i < 30000) {
                    if (com_google_android_gms_internal_zzbrf.getResultCode() != -2) {
                        i *= 2;
                        Log.w("ExponenentialBackoff", "network error occurred, backing off/sleeping.");
                    } else {
                        Log.w("ExponenentialBackoff", "network unavailable, sleeping.");
                        i = PointerIconCompat.TYPE_DEFAULT;
                    }
                }
                if (!this.zzJ) {
                    com_google_android_gms_internal_zzbrf.reset();
                    if (z) {
                        com_google_android_gms_internal_zzbrf.zza(zzbrb.zzi(this.zzciR), this.zzciR.getApplicationContext());
                    } else {
                        com_google_android_gms_internal_zzbrf.zzjO(zzbrb.zzi(this.zzciR));
                    }
                } else {
                    return;
                }
            } catch (InterruptedException e) {
                Log.w("ExponenentialBackoff", "thread interrupted during exponential backoff.");
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    public void zzd(@NonNull zzbrf com_google_android_gms_internal_zzbrf) {
        zza(com_google_android_gms_internal_zzbrf, true);
    }

    public boolean zzqa(int i) {
        return (i >= HttpStatus.SC_INTERNAL_SERVER_ERROR && i < 600) || i == -2 || i == 429 || i == HttpStatus.SC_REQUEST_TIMEOUT;
    }
}
