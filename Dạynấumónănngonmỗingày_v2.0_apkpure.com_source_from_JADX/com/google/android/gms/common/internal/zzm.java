package com.google.android.gms.common.internal;

import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

public final class zzm implements Callback {
    private final Handler mHandler;
    private volatile boolean zzaEA;
    private final AtomicInteger zzaEB;
    private boolean zzaEC;
    private final zza zzaEw;
    private final ArrayList<ConnectionCallbacks> zzaEx;
    final ArrayList<ConnectionCallbacks> zzaEy;
    private final ArrayList<OnConnectionFailedListener> zzaEz;
    private final Object zzrN;

    public interface zza {
        boolean isConnected();

        Bundle zzud();
    }

    public zzm(Looper looper, zza com_google_android_gms_common_internal_zzm_zza) {
        this.zzaEx = new ArrayList();
        this.zzaEy = new ArrayList();
        this.zzaEz = new ArrayList();
        this.zzaEA = false;
        this.zzaEB = new AtomicInteger(0);
        this.zzaEC = false;
        this.zzrN = new Object();
        this.zzaEw = com_google_android_gms_common_internal_zzm_zza;
        this.mHandler = new Handler(looper, this);
    }

    public boolean handleMessage(Message message) {
        if (message.what == 1) {
            ConnectionCallbacks connectionCallbacks = (ConnectionCallbacks) message.obj;
            synchronized (this.zzrN) {
                if (this.zzaEA && this.zzaEw.isConnected() && this.zzaEx.contains(connectionCallbacks)) {
                    connectionCallbacks.onConnected(this.zzaEw.zzud());
                }
            }
            return true;
        }
        Log.wtf("GmsClientEvents", "Don't know how to handle message: " + message.what, new Exception());
        return false;
    }

    public boolean isConnectionCallbacksRegistered(ConnectionCallbacks connectionCallbacks) {
        boolean contains;
        zzac.zzw(connectionCallbacks);
        synchronized (this.zzrN) {
            contains = this.zzaEx.contains(connectionCallbacks);
        }
        return contains;
    }

    public boolean isConnectionFailedListenerRegistered(OnConnectionFailedListener onConnectionFailedListener) {
        boolean contains;
        zzac.zzw(onConnectionFailedListener);
        synchronized (this.zzrN) {
            contains = this.zzaEz.contains(onConnectionFailedListener);
        }
        return contains;
    }

    public void registerConnectionCallbacks(ConnectionCallbacks connectionCallbacks) {
        zzac.zzw(connectionCallbacks);
        synchronized (this.zzrN) {
            if (this.zzaEx.contains(connectionCallbacks)) {
                String valueOf = String.valueOf(connectionCallbacks);
                Log.w("GmsClientEvents", new StringBuilder(String.valueOf(valueOf).length() + 62).append("registerConnectionCallbacks(): listener ").append(valueOf).append(" is already registered").toString());
            } else {
                this.zzaEx.add(connectionCallbacks);
            }
        }
        if (this.zzaEw.isConnected()) {
            this.mHandler.sendMessage(this.mHandler.obtainMessage(1, connectionCallbacks));
        }
    }

    public void registerConnectionFailedListener(OnConnectionFailedListener onConnectionFailedListener) {
        zzac.zzw(onConnectionFailedListener);
        synchronized (this.zzrN) {
            if (this.zzaEz.contains(onConnectionFailedListener)) {
                String valueOf = String.valueOf(onConnectionFailedListener);
                Log.w("GmsClientEvents", new StringBuilder(String.valueOf(valueOf).length() + 67).append("registerConnectionFailedListener(): listener ").append(valueOf).append(" is already registered").toString());
            } else {
                this.zzaEz.add(onConnectionFailedListener);
            }
        }
    }

    public void unregisterConnectionCallbacks(ConnectionCallbacks connectionCallbacks) {
        zzac.zzw(connectionCallbacks);
        synchronized (this.zzrN) {
            if (!this.zzaEx.remove(connectionCallbacks)) {
                String valueOf = String.valueOf(connectionCallbacks);
                Log.w("GmsClientEvents", new StringBuilder(String.valueOf(valueOf).length() + 52).append("unregisterConnectionCallbacks(): listener ").append(valueOf).append(" not found").toString());
            } else if (this.zzaEC) {
                this.zzaEy.add(connectionCallbacks);
            }
        }
    }

    public void unregisterConnectionFailedListener(OnConnectionFailedListener onConnectionFailedListener) {
        zzac.zzw(onConnectionFailedListener);
        synchronized (this.zzrN) {
            if (!this.zzaEz.remove(onConnectionFailedListener)) {
                String valueOf = String.valueOf(onConnectionFailedListener);
                Log.w("GmsClientEvents", new StringBuilder(String.valueOf(valueOf).length() + 57).append("unregisterConnectionFailedListener(): listener ").append(valueOf).append(" not found").toString());
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void zzcP(int r6) {
        /*
        r5 = this;
        r0 = 0;
        r1 = 1;
        r2 = android.os.Looper.myLooper();
        r3 = r5.mHandler;
        r3 = r3.getLooper();
        if (r2 != r3) goto L_0x000f;
    L_0x000e:
        r0 = r1;
    L_0x000f:
        r2 = "onUnintentionalDisconnection must only be called on the Handler thread";
        com.google.android.gms.common.internal.zzac.zza(r0, r2);
        r0 = r5.mHandler;
        r0.removeMessages(r1);
        r1 = r5.zzrN;
        monitor-enter(r1);
        r0 = 1;
        r5.zzaEC = r0;	 Catch:{ all -> 0x005e }
        r0 = new java.util.ArrayList;	 Catch:{ all -> 0x005e }
        r2 = r5.zzaEx;	 Catch:{ all -> 0x005e }
        r0.<init>(r2);	 Catch:{ all -> 0x005e }
        r2 = r5.zzaEB;	 Catch:{ all -> 0x005e }
        r2 = r2.get();	 Catch:{ all -> 0x005e }
        r3 = r0.iterator();	 Catch:{ all -> 0x005e }
    L_0x0030:
        r0 = r3.hasNext();	 Catch:{ all -> 0x005e }
        if (r0 == 0) goto L_0x0048;
    L_0x0036:
        r0 = r3.next();	 Catch:{ all -> 0x005e }
        r0 = (com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks) r0;	 Catch:{ all -> 0x005e }
        r4 = r5.zzaEA;	 Catch:{ all -> 0x005e }
        if (r4 == 0) goto L_0x0048;
    L_0x0040:
        r4 = r5.zzaEB;	 Catch:{ all -> 0x005e }
        r4 = r4.get();	 Catch:{ all -> 0x005e }
        if (r4 == r2) goto L_0x0052;
    L_0x0048:
        r0 = r5.zzaEy;	 Catch:{ all -> 0x005e }
        r0.clear();	 Catch:{ all -> 0x005e }
        r0 = 0;
        r5.zzaEC = r0;	 Catch:{ all -> 0x005e }
        monitor-exit(r1);	 Catch:{ all -> 0x005e }
        return;
    L_0x0052:
        r4 = r5.zzaEx;	 Catch:{ all -> 0x005e }
        r4 = r4.contains(r0);	 Catch:{ all -> 0x005e }
        if (r4 == 0) goto L_0x0030;
    L_0x005a:
        r0.onConnectionSuspended(r6);	 Catch:{ all -> 0x005e }
        goto L_0x0030;
    L_0x005e:
        r0 = move-exception;
        monitor-exit(r1);	 Catch:{ all -> 0x005e }
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.common.internal.zzm.zzcP(int):void");
    }

    public void zzo(ConnectionResult connectionResult) {
        zzac.zza(Looper.myLooper() == this.mHandler.getLooper(), (Object) "onConnectionFailure must only be called on the Handler thread");
        this.mHandler.removeMessages(1);
        synchronized (this.zzrN) {
            ArrayList arrayList = new ArrayList(this.zzaEz);
            int i = this.zzaEB.get();
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                OnConnectionFailedListener onConnectionFailedListener = (OnConnectionFailedListener) it.next();
                if (!this.zzaEA || this.zzaEB.get() != i) {
                    return;
                } else if (this.zzaEz.contains(onConnectionFailedListener)) {
                    onConnectionFailedListener.onConnectionFailed(connectionResult);
                }
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void zzq(android.os.Bundle r6) {
        /*
        r5 = this;
        r2 = 0;
        r1 = 1;
        r0 = android.os.Looper.myLooper();
        r3 = r5.mHandler;
        r3 = r3.getLooper();
        if (r0 != r3) goto L_0x006e;
    L_0x000e:
        r0 = r1;
    L_0x000f:
        r3 = "onConnectionSuccess must only be called on the Handler thread";
        com.google.android.gms.common.internal.zzac.zza(r0, r3);
        r3 = r5.zzrN;
        monitor-enter(r3);
        r0 = r5.zzaEC;	 Catch:{ all -> 0x0080 }
        if (r0 != 0) goto L_0x0070;
    L_0x001b:
        r0 = r1;
    L_0x001c:
        com.google.android.gms.common.internal.zzac.zzar(r0);	 Catch:{ all -> 0x0080 }
        r0 = r5.mHandler;	 Catch:{ all -> 0x0080 }
        r4 = 1;
        r0.removeMessages(r4);	 Catch:{ all -> 0x0080 }
        r0 = 1;
        r5.zzaEC = r0;	 Catch:{ all -> 0x0080 }
        r0 = r5.zzaEy;	 Catch:{ all -> 0x0080 }
        r0 = r0.size();	 Catch:{ all -> 0x0080 }
        if (r0 != 0) goto L_0x0072;
    L_0x0030:
        com.google.android.gms.common.internal.zzac.zzar(r1);	 Catch:{ all -> 0x0080 }
        r0 = new java.util.ArrayList;	 Catch:{ all -> 0x0080 }
        r1 = r5.zzaEx;	 Catch:{ all -> 0x0080 }
        r0.<init>(r1);	 Catch:{ all -> 0x0080 }
        r1 = r5.zzaEB;	 Catch:{ all -> 0x0080 }
        r1 = r1.get();	 Catch:{ all -> 0x0080 }
        r2 = r0.iterator();	 Catch:{ all -> 0x0080 }
    L_0x0044:
        r0 = r2.hasNext();	 Catch:{ all -> 0x0080 }
        if (r0 == 0) goto L_0x0064;
    L_0x004a:
        r0 = r2.next();	 Catch:{ all -> 0x0080 }
        r0 = (com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks) r0;	 Catch:{ all -> 0x0080 }
        r4 = r5.zzaEA;	 Catch:{ all -> 0x0080 }
        if (r4 == 0) goto L_0x0064;
    L_0x0054:
        r4 = r5.zzaEw;	 Catch:{ all -> 0x0080 }
        r4 = r4.isConnected();	 Catch:{ all -> 0x0080 }
        if (r4 == 0) goto L_0x0064;
    L_0x005c:
        r4 = r5.zzaEB;	 Catch:{ all -> 0x0080 }
        r4 = r4.get();	 Catch:{ all -> 0x0080 }
        if (r4 == r1) goto L_0x0074;
    L_0x0064:
        r0 = r5.zzaEy;	 Catch:{ all -> 0x0080 }
        r0.clear();	 Catch:{ all -> 0x0080 }
        r0 = 0;
        r5.zzaEC = r0;	 Catch:{ all -> 0x0080 }
        monitor-exit(r3);	 Catch:{ all -> 0x0080 }
        return;
    L_0x006e:
        r0 = r2;
        goto L_0x000f;
    L_0x0070:
        r0 = r2;
        goto L_0x001c;
    L_0x0072:
        r1 = r2;
        goto L_0x0030;
    L_0x0074:
        r4 = r5.zzaEy;	 Catch:{ all -> 0x0080 }
        r4 = r4.contains(r0);	 Catch:{ all -> 0x0080 }
        if (r4 != 0) goto L_0x0044;
    L_0x007c:
        r0.onConnected(r6);	 Catch:{ all -> 0x0080 }
        goto L_0x0044;
    L_0x0080:
        r0 = move-exception;
        monitor-exit(r3);	 Catch:{ all -> 0x0080 }
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.common.internal.zzm.zzq(android.os.Bundle):void");
    }

    public void zzxq() {
        this.zzaEA = false;
        this.zzaEB.incrementAndGet();
    }

    public void zzxr() {
        this.zzaEA = true;
    }
}
