package com.google.android.gms.internal;

import android.content.Context;
import android.net.Uri;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.google.android.gms.dynamic.zze;
import com.google.android.gms.dynamite.DynamiteModule;
import com.google.android.gms.internal.zzbrd.zza;
import com.google.firebase.FirebaseApp;
import org.json.JSONObject;

public class zzbre {
    private static final Object zzcmb;
    static volatile zzbre zzcmc;
    private Context mContext;
    private FirebaseApp zzciR;
    private zzbrd zzcmd;

    static {
        zzcmb = new Object();
    }

    protected zzbre(@NonNull FirebaseApp firebaseApp) throws RemoteException {
        this.mContext = firebaseApp.getApplicationContext();
        this.zzciR = firebaseApp;
        try {
            this.zzcmd = zza.zzfM(DynamiteModule.zza(this.mContext, DynamiteModule.zzaQw, "com.google.android.gms.firebasestorage").zzdX("com.google.firebase.storage.network.NetworkRequestFactoryImpl"));
            if (this.zzcmd == null) {
                Log.e("NetworkRqFactoryProxy", "Unable to load Firebase Storage Network layer.");
                throw new RemoteException();
            }
        } catch (Throwable e) {
            Log.e("NetworkRqFactoryProxy", "NetworkRequestFactoryProxy failed with a RemoteException:", e);
            throw new RemoteException();
        }
    }

    private zzbrf zze(zzbrf com_google_android_gms_internal_zzbrf) {
        com_google_android_gms_internal_zzbrf.zzay("x-firebase-gmpid", this.zzciR.getOptions().getApplicationId());
        return com_google_android_gms_internal_zzbrf;
    }

    public static zzbre zzj(@NonNull FirebaseApp firebaseApp) throws RemoteException {
        if (zzcmc == null) {
            synchronized (zzcmb) {
                if (zzcmc == null) {
                    zzcmc = new zzbre(firebaseApp);
                }
            }
        }
        return zzcmc;
    }

    @Nullable
    public String zzA(Uri uri) {
        try {
            return this.zzcmd.zzA(uri);
        } catch (Throwable e) {
            Log.e("NetworkRqFactoryProxy", "getDefaultURL failed with a RemoteException:", e);
            return null;
        }
    }

    @NonNull
    public zzbrf zzC(Uri uri) throws RemoteException {
        return zze(new zzbrf(this.zzcmd.zza(uri, zze.zzA(this.mContext))));
    }

    @NonNull
    public zzbrf zzD(Uri uri) throws RemoteException {
        return zze(new zzbrf(this.zzcmd.zzb(uri, zze.zzA(this.mContext))));
    }

    @NonNull
    public zzbrf zza(Uri uri, long j) throws RemoteException {
        return zze(new zzbrf(this.zzcmd.zza(uri, zze.zzA(this.mContext), j)));
    }

    @Nullable
    public zzbrf zza(Uri uri, String str) throws RemoteException {
        return zze(new zzbrf(this.zzcmd.zzb(uri, zze.zzA(this.mContext), str)));
    }

    @NonNull
    public zzbrf zza(Uri uri, String str, byte[] bArr, long j, int i, boolean z) throws RemoteException {
        return zze(new zzbrf(this.zzcmd.zza(uri, zze.zzA(this.mContext), str, zze.zzA(bArr), j, i, z)));
    }

    @NonNull
    public zzbrf zza(Uri uri, JSONObject jSONObject) throws RemoteException {
        return zze(new zzbrf(this.zzcmd.zza(uri, zze.zzA(this.mContext), zze.zzA(jSONObject))));
    }

    @NonNull
    public zzbrf zza(Uri uri, JSONObject jSONObject, String str) throws RemoteException {
        return zze(new zzbrf(this.zzcmd.zza(uri, zze.zzA(this.mContext), zze.zzA(jSONObject), str)));
    }

    @Nullable
    public String zzabp() {
        try {
            return this.zzcmd.zzabp();
        } catch (Throwable e) {
            Log.e("NetworkRqFactoryProxy", "getBackendAuthority failed with a RemoteException:", e);
            return null;
        }
    }

    @NonNull
    public zzbrf zzb(Uri uri, String str) throws RemoteException {
        return zze(new zzbrf(this.zzcmd.zzc(uri, zze.zzA(this.mContext), str)));
    }
}
