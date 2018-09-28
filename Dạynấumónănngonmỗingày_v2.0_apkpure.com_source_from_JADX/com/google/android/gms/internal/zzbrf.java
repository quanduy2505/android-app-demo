package com.google.android.gms.internal;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.google.android.gms.dynamic.zze;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.storage.StorageException;
import java.io.InputStream;
import java.net.SocketException;
import org.json.JSONObject;

public class zzbrf {
    static final /* synthetic */ boolean $assertionsDisabled;
    private Exception zzbLK;
    private zzbrc zzcme;
    private int zzcmf;
    private Exception zzcmg;

    static {
        $assertionsDisabled = !zzbrf.class.desiredAssertionStatus();
    }

    public zzbrf(@NonNull zzbrc com_google_android_gms_internal_zzbrc) {
        this.zzcme = com_google_android_gms_internal_zzbrc;
    }

    private boolean zzce(Context context) {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            return true;
        }
        this.zzcmf = -2;
        this.zzcmg = new SocketException("Network subsystem is unavailable");
        return false;
    }

    @Nullable
    public Exception getException() {
        try {
            return this.zzcmg != null ? this.zzcmg : this.zzbLK != null ? this.zzbLK : (Exception) zze.zzE(this.zzcme.zzabm());
        } catch (Throwable e) {
            this.zzbLK = e;
            Log.e("NetworkRequestProxy", "getException failed with a RemoteException:", e);
            return null;
        }
    }

    public int getResultCode() {
        try {
            return this.zzcmf != 0 ? this.zzcmf : this.zzcme.getResultCode();
        } catch (Throwable e) {
            this.zzbLK = e;
            Log.e("NetworkRequestProxy", "getResultCode failed with a RemoteException:", e);
            return 0;
        }
    }

    @Nullable
    public InputStream getStream() {
        try {
            return (InputStream) zze.zzE(this.zzcme.zzabi());
        } catch (Throwable e) {
            this.zzbLK = e;
            Log.e("NetworkRequestProxy", "getStream failed with a RemoteException:", e);
            return null;
        }
    }

    public void reset() {
        try {
            this.zzcmf = 0;
            this.zzcmg = null;
            this.zzcme.reset();
        } catch (Throwable e) {
            this.zzbLK = e;
            Log.e("NetworkRequestProxy", "reset failed with a RemoteException:", e);
        }
    }

    public <TResult> void zza(TaskCompletionSource<TResult> taskCompletionSource, TResult tResult) {
        Throwable exception = getException();
        if (zzabn() && exception == null) {
            taskCompletionSource.setResult(tResult);
            return;
        }
        Exception fromExceptionAndHttpCode = StorageException.fromExceptionAndHttpCode(exception, getResultCode());
        if ($assertionsDisabled || fromExceptionAndHttpCode != null) {
            taskCompletionSource.setException(fromExceptionAndHttpCode);
            return;
        }
        throw new AssertionError();
    }

    public void zza(@Nullable String str, @NonNull Context context) {
        try {
            if (zzce(context)) {
                this.zzcme.zzjN(str);
            }
        } catch (Throwable e) {
            this.zzbLK = e;
            Log.e("NetworkRequestProxy", "performRequest failed with a RemoteException:", e);
        }
    }

    public void zzabh() {
        try {
            this.zzcme.zzabh();
        } catch (Throwable e) {
            this.zzbLK = e;
            Log.e("NetworkRequestProxy", "performRequestEnd failed with a RemoteException:", e);
        }
    }

    @Nullable
    public String zzabk() {
        try {
            this.zzcme.zzabk();
        } catch (Throwable e) {
            this.zzbLK = e;
            Log.e("NetworkRequestProxy", "getRawResult failed with a RemoteException:", e);
        }
        return null;
    }

    public boolean zzabn() {
        boolean z = false;
        try {
            if (this.zzcmf != -2 && this.zzcmg == null) {
                z = this.zzcme.zzabn();
            }
        } catch (Throwable e) {
            this.zzbLK = e;
            Log.e("NetworkRequestProxy", "isResultSuccess failed with a RemoteException:", e);
        }
        return z;
    }

    public int zzabo() {
        try {
            return this.zzcme.zzabo();
        } catch (Throwable e) {
            this.zzbLK = e;
            Log.e("NetworkRequestProxy", "getResultingContentLength failed with a RemoteException:", e);
            return 0;
        }
    }

    @NonNull
    public JSONObject zzabq() throws RemoteException {
        return (JSONObject) zze.zzE(this.zzcme.zzabj());
    }

    public void zzay(String str, String str2) {
        try {
            this.zzcme.zzay(str, str2);
        } catch (Throwable e) {
            Throwable th = e;
            String str3 = "NetworkRequestProxy";
            String str4 = "Caught remote exception setting custom header:";
            String valueOf = String.valueOf(str);
            Log.e(str3, valueOf.length() != 0 ? str4.concat(valueOf) : new String(str4), th);
        }
    }

    public void zzjO(@Nullable String str) {
        try {
            this.zzcme.zzjO(str);
        } catch (Throwable e) {
            this.zzbLK = e;
            Log.e("NetworkRequestProxy", "performRequestStart failed with a RemoteException:", e);
        }
    }

    @Nullable
    public String zzjP(String str) {
        try {
            return this.zzcme.zzjP(str);
        } catch (Throwable e) {
            Throwable th = e;
            String str2 = "NetworkRequestProxy";
            String str3 = "getResultString failed with a RemoteException:";
            String valueOf = String.valueOf(str);
            Log.e(str2, valueOf.length() != 0 ? str3.concat(valueOf) : new String(str3), th);
            return null;
        }
    }
}
