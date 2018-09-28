package com.google.android.gms.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.ApiOptions;
import com.google.android.gms.common.api.Api.zze;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.zzc;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.internal.zzal;
import com.google.android.gms.common.internal.zzf.zzf;
import com.google.android.gms.common.internal.zzr;
import com.google.android.gms.internal.zzzq.zzd;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import rx.internal.operators.OnSubscribeConcatMap;

public class zzaap implements Callback {
    public static final Status zzaAO;
    private static final Status zzaAP;
    private static zzaap zzaAR;
    private static final Object zztU;
    private final Context mContext;
    private final Handler mHandler;
    private long zzaAQ;
    private int zzaAS;
    private final AtomicInteger zzaAT;
    private final AtomicInteger zzaAU;
    private zzaae zzaAV;
    private final Set<zzzs<?>> zzaAW;
    private final Set<zzzs<?>> zzaAX;
    private long zzaAn;
    private long zzaAo;
    private final GoogleApiAvailability zzaxX;
    private final Map<zzzs<?>, zza<?>> zzazt;

    private class zzb implements zzf, com.google.android.gms.internal.zzabj.zza {
        final /* synthetic */ zzaap zzaBg;
        private boolean zzaBj;
        private Set<Scope> zzajm;
        private final zzzs<?> zzaxH;
        private zzr zzazW;
        private final zze zzazq;

        /* renamed from: com.google.android.gms.internal.zzaap.zzb.1 */
        class C06311 implements Runnable {
            final /* synthetic */ ConnectionResult zzaBi;
            final /* synthetic */ zzb zzaBk;

            C06311(zzb com_google_android_gms_internal_zzaap_zzb, ConnectionResult connectionResult) {
                this.zzaBk = com_google_android_gms_internal_zzaap_zzb;
                this.zzaBi = connectionResult;
            }

            public void run() {
                if (this.zzaBi.isSuccess()) {
                    this.zzaBk.zzaBj = true;
                    if (this.zzaBk.zzazq.zzqD()) {
                        this.zzaBk.zzwi();
                        return;
                    } else {
                        this.zzaBk.zzazq.zza(null, Collections.emptySet());
                        return;
                    }
                }
                ((zza) this.zzaBk.zzaBg.zzazt.get(this.zzaBk.zzaxH)).onConnectionFailed(this.zzaBi);
            }
        }

        public zzb(zzaap com_google_android_gms_internal_zzaap, zze com_google_android_gms_common_api_Api_zze, zzzs<?> com_google_android_gms_internal_zzzs_) {
            this.zzaBg = com_google_android_gms_internal_zzaap;
            this.zzazW = null;
            this.zzajm = null;
            this.zzaBj = false;
            this.zzazq = com_google_android_gms_common_api_Api_zze;
            this.zzaxH = com_google_android_gms_internal_zzzs_;
        }

        @WorkerThread
        private void zzwi() {
            if (this.zzaBj && this.zzazW != null) {
                this.zzazq.zza(this.zzazW, this.zzajm);
            }
        }

        @WorkerThread
        public void zzb(zzr com_google_android_gms_common_internal_zzr, Set<Scope> set) {
            if (com_google_android_gms_common_internal_zzr == null || set == null) {
                Log.wtf("GoogleApiManager", "Received null response from onSignInSuccess", new Exception());
                zzi(new ConnectionResult(4));
                return;
            }
            this.zzazW = com_google_android_gms_common_internal_zzr;
            this.zzajm = set;
            zzwi();
        }

        public void zzg(@NonNull ConnectionResult connectionResult) {
            this.zzaBg.mHandler.post(new C06311(this, connectionResult));
        }

        @WorkerThread
        public void zzi(ConnectionResult connectionResult) {
            ((zza) this.zzaBg.zzazt.get(this.zzaxH)).zzi(connectionResult);
        }
    }

    public class zza<O extends ApiOptions> implements ConnectionCallbacks, OnConnectionFailedListener, zzzz {
        private final Queue<zzzq> zzaAY;
        private final com.google.android.gms.common.api.Api.zzb zzaAZ;
        private boolean zzaAm;
        private final zzaad zzaBa;
        private final Set<zzzu> zzaBb;
        private final Map<com.google.android.gms.internal.zzaaz.zzb<?>, zzabf> zzaBc;
        private final int zzaBd;
        private final zzabj zzaBe;
        private ConnectionResult zzaBf;
        final /* synthetic */ zzaap zzaBg;
        private final zzzs<O> zzaxH;
        private final zze zzazq;

        /* renamed from: com.google.android.gms.internal.zzaap.zza.1 */
        class C06281 implements Runnable {
            final /* synthetic */ zza zzaBh;

            C06281(zza com_google_android_gms_internal_zzaap_zza) {
                this.zzaBh = com_google_android_gms_internal_zzaap_zza;
            }

            public void run() {
                this.zzaBh.zzvZ();
            }
        }

        /* renamed from: com.google.android.gms.internal.zzaap.zza.2 */
        class C06292 implements Runnable {
            final /* synthetic */ zza zzaBh;

            C06292(zza com_google_android_gms_internal_zzaap_zza) {
                this.zzaBh = com_google_android_gms_internal_zzaap_zza;
            }

            public void run() {
                this.zzaBh.zzwa();
            }
        }

        /* renamed from: com.google.android.gms.internal.zzaap.zza.3 */
        class C06303 implements Runnable {
            final /* synthetic */ zza zzaBh;
            final /* synthetic */ ConnectionResult zzaBi;

            C06303(zza com_google_android_gms_internal_zzaap_zza, ConnectionResult connectionResult) {
                this.zzaBh = com_google_android_gms_internal_zzaap_zza;
                this.zzaBi = connectionResult;
            }

            public void run() {
                this.zzaBh.onConnectionFailed(this.zzaBi);
            }
        }

        @WorkerThread
        public zza(zzaap com_google_android_gms_internal_zzaap, zzc<O> com_google_android_gms_common_api_zzc_O) {
            this.zzaBg = com_google_android_gms_internal_zzaap;
            this.zzaAY = new LinkedList();
            this.zzaBb = new HashSet();
            this.zzaBc = new HashMap();
            this.zzaBf = null;
            this.zzazq = com_google_android_gms_common_api_zzc_O.buildApiClient(com_google_android_gms_internal_zzaap.mHandler.getLooper(), this);
            if (this.zzazq instanceof zzal) {
                this.zzaAZ = ((zzal) this.zzazq).zzxG();
            } else {
                this.zzaAZ = this.zzazq;
            }
            this.zzaxH = com_google_android_gms_common_api_zzc_O.getApiKey();
            this.zzaBa = new zzaad();
            this.zzaBd = com_google_android_gms_common_api_zzc_O.getInstanceId();
            if (this.zzazq.zzqD()) {
                this.zzaBe = com_google_android_gms_common_api_zzc_O.createSignInCoordinator(com_google_android_gms_internal_zzaap.mContext, com_google_android_gms_internal_zzaap.mHandler);
            } else {
                this.zzaBe = null;
            }
        }

        @WorkerThread
        private void zzb(zzzq com_google_android_gms_internal_zzzq) {
            com_google_android_gms_internal_zzzq.zza(this.zzaBa, zzqD());
            try {
                com_google_android_gms_internal_zzzq.zza(this);
            } catch (DeadObjectException e) {
                onConnectionSuspended(1);
                this.zzazq.disconnect();
            }
        }

        @WorkerThread
        private void zzj(ConnectionResult connectionResult) {
            for (zzzu zza : this.zzaBb) {
                zza.zza(this.zzaxH, connectionResult);
            }
            this.zzaBb.clear();
        }

        @WorkerThread
        private void zzvZ() {
            zzwd();
            zzj(ConnectionResult.zzawX);
            zzwf();
            Iterator it = this.zzaBc.values().iterator();
            while (it.hasNext()) {
                it.next();
                try {
                    TaskCompletionSource taskCompletionSource = new TaskCompletionSource();
                } catch (DeadObjectException e) {
                    onConnectionSuspended(1);
                    this.zzazq.disconnect();
                } catch (RemoteException e2) {
                }
            }
            zzwb();
            zzwg();
        }

        @WorkerThread
        private void zzwa() {
            zzwd();
            this.zzaAm = true;
            this.zzaBa.zzvw();
            this.zzaBg.mHandler.sendMessageDelayed(Message.obtain(this.zzaBg.mHandler, 7, this.zzaxH), this.zzaBg.zzaAo);
            this.zzaBg.mHandler.sendMessageDelayed(Message.obtain(this.zzaBg.mHandler, 9, this.zzaxH), this.zzaBg.zzaAn);
            this.zzaBg.zzaAS = -1;
        }

        @WorkerThread
        private void zzwb() {
            while (this.zzazq.isConnected() && !this.zzaAY.isEmpty()) {
                zzb((zzzq) this.zzaAY.remove());
            }
        }

        @WorkerThread
        private void zzwf() {
            if (this.zzaAm) {
                this.zzaBg.mHandler.removeMessages(9, this.zzaxH);
                this.zzaBg.mHandler.removeMessages(7, this.zzaxH);
                this.zzaAm = false;
            }
        }

        private void zzwg() {
            this.zzaBg.mHandler.removeMessages(10, this.zzaxH);
            this.zzaBg.mHandler.sendMessageDelayed(this.zzaBg.mHandler.obtainMessage(10, this.zzaxH), this.zzaBg.zzaAQ);
        }

        @WorkerThread
        public void connect() {
            zzac.zza(this.zzaBg.mHandler);
            if (!this.zzazq.isConnected() && !this.zzazq.isConnecting()) {
                if (this.zzazq.zzuI() && this.zzaBg.zzaAS != 0) {
                    this.zzaBg.zzaAS = this.zzaBg.zzaxX.isGooglePlayServicesAvailable(this.zzaBg.mContext);
                    if (this.zzaBg.zzaAS != 0) {
                        onConnectionFailed(new ConnectionResult(this.zzaBg.zzaAS, null));
                        return;
                    }
                }
                Object com_google_android_gms_internal_zzaap_zzb = new zzb(this.zzaBg, this.zzazq, this.zzaxH);
                if (this.zzazq.zzqD()) {
                    this.zzaBe.zza(com_google_android_gms_internal_zzaap_zzb);
                }
                this.zzazq.zza(com_google_android_gms_internal_zzaap_zzb);
            }
        }

        public int getInstanceId() {
            return this.zzaBd;
        }

        boolean isConnected() {
            return this.zzazq.isConnected();
        }

        public void onConnected(@Nullable Bundle bundle) {
            if (Looper.myLooper() == this.zzaBg.mHandler.getLooper()) {
                zzvZ();
            } else {
                this.zzaBg.mHandler.post(new C06281(this));
            }
        }

        @WorkerThread
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            zzac.zza(this.zzaBg.mHandler);
            if (this.zzaBe != null) {
                this.zzaBe.zzwr();
            }
            zzwd();
            this.zzaBg.zzaAS = -1;
            zzj(connectionResult);
            if (connectionResult.getErrorCode() == 4) {
                zzC(zzaap.zzaAP);
            } else if (this.zzaAY.isEmpty()) {
                this.zzaBf = connectionResult;
            } else {
                synchronized (zzaap.zztU) {
                    if (this.zzaBg.zzaAV == null || !this.zzaBg.zzaAW.contains(this.zzaxH)) {
                        if (!this.zzaBg.zzc(connectionResult, this.zzaBd)) {
                            if (connectionResult.getErrorCode() == 18) {
                                this.zzaAm = true;
                            }
                            if (this.zzaAm) {
                                this.zzaBg.mHandler.sendMessageDelayed(Message.obtain(this.zzaBg.mHandler, 7, this.zzaxH), this.zzaBg.zzaAo);
                                return;
                            }
                            String valueOf = String.valueOf(this.zzaxH.zzuV());
                            zzC(new Status(17, new StringBuilder(String.valueOf(valueOf).length() + 38).append("API: ").append(valueOf).append(" is not available on this device.").toString()));
                            return;
                        }
                        return;
                    }
                    this.zzaBg.zzaAV.zzb(connectionResult, this.zzaBd);
                }
            }
        }

        public void onConnectionSuspended(int i) {
            if (Looper.myLooper() == this.zzaBg.mHandler.getLooper()) {
                zzwa();
            } else {
                this.zzaBg.mHandler.post(new C06292(this));
            }
        }

        @WorkerThread
        public void resume() {
            zzac.zza(this.zzaBg.mHandler);
            if (this.zzaAm) {
                connect();
            }
        }

        @WorkerThread
        public void signOut() {
            zzac.zza(this.zzaBg.mHandler);
            zzC(zzaap.zzaAO);
            this.zzaBa.zzvv();
            for (com.google.android.gms.internal.zzaaz.zzb com_google_android_gms_internal_zzzq_zze : this.zzaBc.keySet()) {
                zza(new zzzq.zze(com_google_android_gms_internal_zzzq_zze, new TaskCompletionSource()));
            }
            this.zzazq.disconnect();
        }

        @WorkerThread
        public void zzC(Status status) {
            zzac.zza(this.zzaBg.mHandler);
            for (zzzq zzy : this.zzaAY) {
                zzy.zzy(status);
            }
            this.zzaAY.clear();
        }

        public void zza(ConnectionResult connectionResult, Api<?> api, int i) {
            if (Looper.myLooper() == this.zzaBg.mHandler.getLooper()) {
                onConnectionFailed(connectionResult);
            } else {
                this.zzaBg.mHandler.post(new C06303(this, connectionResult));
            }
        }

        @WorkerThread
        public void zza(zzzq com_google_android_gms_internal_zzzq) {
            zzac.zza(this.zzaBg.mHandler);
            if (this.zzazq.isConnected()) {
                zzb(com_google_android_gms_internal_zzzq);
                zzwg();
                return;
            }
            this.zzaAY.add(com_google_android_gms_internal_zzzq);
            if (this.zzaBf == null || !this.zzaBf.hasResolution()) {
                connect();
            } else {
                onConnectionFailed(this.zzaBf);
            }
        }

        @WorkerThread
        public void zzb(zzzu com_google_android_gms_internal_zzzu) {
            zzac.zza(this.zzaBg.mHandler);
            this.zzaBb.add(com_google_android_gms_internal_zzzu);
        }

        @WorkerThread
        public void zzi(@NonNull ConnectionResult connectionResult) {
            zzac.zza(this.zzaBg.mHandler);
            this.zzazq.disconnect();
            onConnectionFailed(connectionResult);
        }

        public boolean zzqD() {
            return this.zzazq.zzqD();
        }

        @WorkerThread
        public void zzvJ() {
            zzac.zza(this.zzaBg.mHandler);
            if (this.zzaAm) {
                zzwf();
                zzC(this.zzaBg.zzaxX.isGooglePlayServicesAvailable(this.zzaBg.mContext) == 18 ? new Status(8, "Connection timed out while waiting for Google Play services update to complete.") : new Status(8, "API failed to connect while resuming due to an unknown error."));
                this.zzazq.disconnect();
            }
        }

        public zze zzvr() {
            return this.zzazq;
        }

        public Map<com.google.android.gms.internal.zzaaz.zzb<?>, zzabf> zzwc() {
            return this.zzaBc;
        }

        @WorkerThread
        public void zzwd() {
            zzac.zza(this.zzaBg.mHandler);
            this.zzaBf = null;
        }

        @WorkerThread
        public ConnectionResult zzwe() {
            zzac.zza(this.zzaBg.mHandler);
            return this.zzaBf;
        }

        @WorkerThread
        public void zzwh() {
            zzac.zza(this.zzaBg.mHandler);
            if (!this.zzazq.isConnected() || this.zzaBc.size() != 0) {
                return;
            }
            if (this.zzaBa.zzvu()) {
                zzwg();
            } else {
                this.zzazq.disconnect();
            }
        }
    }

    static {
        zzaAO = new Status(4, "Sign-out occurred while this API call was in progress.");
        zzaAP = new Status(4, "The user must be signed in to make this API call.");
        zztU = new Object();
    }

    private zzaap(Context context, Looper looper, GoogleApiAvailability googleApiAvailability) {
        this.zzaAo = 5000;
        this.zzaAn = 120000;
        this.zzaAQ = 10000;
        this.zzaAS = -1;
        this.zzaAT = new AtomicInteger(1);
        this.zzaAU = new AtomicInteger(0);
        this.zzazt = new ConcurrentHashMap(5, 0.75f, 1);
        this.zzaAV = null;
        this.zzaAW = new com.google.android.gms.common.util.zza();
        this.zzaAX = new com.google.android.gms.common.util.zza();
        this.mContext = context;
        this.mHandler = new Handler(looper, this);
        this.zzaxX = googleApiAvailability;
    }

    @WorkerThread
    private void zza(int i, ConnectionResult connectionResult) {
        for (zza com_google_android_gms_internal_zzaap_zza : this.zzazt.values()) {
            if (com_google_android_gms_internal_zzaap_zza.getInstanceId() == i) {
                break;
            }
        }
        zza com_google_android_gms_internal_zzaap_zza2 = null;
        if (com_google_android_gms_internal_zzaap_zza2 != null) {
            String valueOf = String.valueOf(this.zzaxX.getErrorString(connectionResult.getErrorCode()));
            String valueOf2 = String.valueOf(connectionResult.getErrorMessage());
            com_google_android_gms_internal_zzaap_zza2.zzC(new Status(17, new StringBuilder((String.valueOf(valueOf).length() + 69) + String.valueOf(valueOf2).length()).append("Error resolution was canceled by the user, original error message: ").append(valueOf).append(": ").append(valueOf2).toString()));
            return;
        }
        Log.wtf("GoogleApiManager", "Could not find API instance " + i + " while trying to fail enqueued calls.", new Exception());
    }

    @WorkerThread
    private void zza(zzabd com_google_android_gms_internal_zzabd) {
        zza com_google_android_gms_internal_zzaap_zza = (zza) this.zzazt.get(com_google_android_gms_internal_zzabd.zzaBF.getApiKey());
        if (com_google_android_gms_internal_zzaap_zza == null) {
            zzb(com_google_android_gms_internal_zzabd.zzaBF);
            com_google_android_gms_internal_zzaap_zza = (zza) this.zzazt.get(com_google_android_gms_internal_zzabd.zzaBF.getApiKey());
        }
        if (!com_google_android_gms_internal_zzaap_zza.zzqD() || this.zzaAU.get() == com_google_android_gms_internal_zzabd.zzaBE) {
            com_google_android_gms_internal_zzaap_zza.zza(com_google_android_gms_internal_zzabd.zzaBD);
            return;
        }
        com_google_android_gms_internal_zzabd.zzaBD.zzy(zzaAO);
        com_google_android_gms_internal_zzaap_zza.signOut();
    }

    @WorkerThread
    private void zza(zzzu com_google_android_gms_internal_zzzu) {
        for (zzzs com_google_android_gms_internal_zzzs : com_google_android_gms_internal_zzzu.zzuY()) {
            zza com_google_android_gms_internal_zzaap_zza = (zza) this.zzazt.get(com_google_android_gms_internal_zzzs);
            if (com_google_android_gms_internal_zzaap_zza == null) {
                com_google_android_gms_internal_zzzu.zza(com_google_android_gms_internal_zzzs, new ConnectionResult(13));
                return;
            } else if (com_google_android_gms_internal_zzaap_zza.isConnected()) {
                com_google_android_gms_internal_zzzu.zza(com_google_android_gms_internal_zzzs, ConnectionResult.zzawX);
            } else if (com_google_android_gms_internal_zzaap_zza.zzwe() != null) {
                com_google_android_gms_internal_zzzu.zza(com_google_android_gms_internal_zzzs, com_google_android_gms_internal_zzaap_zza.zzwe());
            } else {
                com_google_android_gms_internal_zzaap_zza.zzb(com_google_android_gms_internal_zzzu);
            }
        }
    }

    public static zzaap zzax(Context context) {
        zzaap com_google_android_gms_internal_zzaap;
        synchronized (zztU) {
            if (zzaAR == null) {
                zzaAR = new zzaap(context.getApplicationContext(), zzvT(), GoogleApiAvailability.getInstance());
            }
            com_google_android_gms_internal_zzaap = zzaAR;
        }
        return com_google_android_gms_internal_zzaap;
    }

    @WorkerThread
    private void zzb(zzc<?> com_google_android_gms_common_api_zzc_) {
        zzzs apiKey = com_google_android_gms_common_api_zzc_.getApiKey();
        if (!this.zzazt.containsKey(apiKey)) {
            this.zzazt.put(apiKey, new zza(this, com_google_android_gms_common_api_zzc_));
        }
        zza com_google_android_gms_internal_zzaap_zza = (zza) this.zzazt.get(apiKey);
        if (com_google_android_gms_internal_zzaap_zza.zzqD()) {
            this.zzaAX.add(apiKey);
        }
        com_google_android_gms_internal_zzaap_zza.connect();
    }

    public static zzaap zzvS() {
        zzaap com_google_android_gms_internal_zzaap;
        synchronized (zztU) {
            zzac.zzb(zzaAR, (Object) "Must guarantee manager is non-null before using getInstance");
            com_google_android_gms_internal_zzaap = zzaAR;
        }
        return com_google_android_gms_internal_zzaap;
    }

    private static Looper zzvT() {
        HandlerThread handlerThread = new HandlerThread("GoogleApiHandler", 9);
        handlerThread.start();
        return handlerThread.getLooper();
    }

    @WorkerThread
    private void zzvV() {
        for (zza com_google_android_gms_internal_zzaap_zza : this.zzazt.values()) {
            com_google_android_gms_internal_zzaap_zza.zzwd();
            com_google_android_gms_internal_zzaap_zza.connect();
        }
    }

    @WorkerThread
    private void zzvW() {
        for (zzzs remove : this.zzaAX) {
            ((zza) this.zzazt.remove(remove)).signOut();
        }
        this.zzaAX.clear();
    }

    @WorkerThread
    public boolean handleMessage(Message message) {
        switch (message.what) {
            case OnSubscribeConcatMap.BOUNDARY /*1*/:
                zza((zzzu) message.obj);
                break;
            case OnSubscribeConcatMap.END /*2*/:
                zzvV();
                break;
            case ConnectionResult.SERVICE_DISABLED /*3*/:
            case ConnectionResult.RESOLUTION_REQUIRED /*6*/:
            case ConnectionResult.LICENSE_CHECK_FAILED /*11*/:
                zza((zzabd) message.obj);
                break;
            case ConnectionResult.SIGN_IN_REQUIRED /*4*/:
                zza(message.arg1, (ConnectionResult) message.obj);
                break;
            case ConnectionResult.INVALID_ACCOUNT /*5*/:
                zzb((zzc) message.obj);
                break;
            case ConnectionResult.NETWORK_ERROR /*7*/:
                if (this.zzazt.containsKey(message.obj)) {
                    ((zza) this.zzazt.get(message.obj)).resume();
                    break;
                }
                break;
            case ConnectionResult.INTERNAL_ERROR /*8*/:
                zzvW();
                break;
            case ConnectionResult.SERVICE_INVALID /*9*/:
                if (this.zzazt.containsKey(message.obj)) {
                    ((zza) this.zzazt.get(message.obj)).zzvJ();
                    break;
                }
                break;
            case ConnectionResult.DEVELOPER_ERROR /*10*/:
                if (this.zzazt.containsKey(message.obj)) {
                    ((zza) this.zzazt.get(message.obj)).zzwh();
                    break;
                }
                break;
            default:
                Log.w("GoogleApiManager", "Unknown message id: " + message.what);
                return false;
        }
        return true;
    }

    public <O extends ApiOptions> Task<Void> zza(@NonNull zzc<O> com_google_android_gms_common_api_zzc_O, @NonNull com.google.android.gms.internal.zzaaz.zzb<?> com_google_android_gms_internal_zzaaz_zzb_) {
        TaskCompletionSource taskCompletionSource = new TaskCompletionSource();
        this.mHandler.sendMessage(this.mHandler.obtainMessage(11, new zzabd(new zzzq.zze(com_google_android_gms_internal_zzaaz_zzb_, taskCompletionSource), this.zzaAU.get(), com_google_android_gms_common_api_zzc_O)));
        return taskCompletionSource.getTask();
    }

    public <O extends ApiOptions> Task<Void> zza(@NonNull zzc<O> com_google_android_gms_common_api_zzc_O, @NonNull zzabe<com.google.android.gms.common.api.Api.zzb, ?> com_google_android_gms_internal_zzabe_com_google_android_gms_common_api_Api_zzb__, @NonNull zzabr<com.google.android.gms.common.api.Api.zzb, ?> com_google_android_gms_internal_zzabr_com_google_android_gms_common_api_Api_zzb__) {
        TaskCompletionSource taskCompletionSource = new TaskCompletionSource();
        this.mHandler.sendMessage(this.mHandler.obtainMessage(6, new zzabd(new zzzq.zzc(new zzabf(com_google_android_gms_internal_zzabe_com_google_android_gms_common_api_Api_zzb__, com_google_android_gms_internal_zzabr_com_google_android_gms_common_api_Api_zzb__), taskCompletionSource), this.zzaAU.get(), com_google_android_gms_common_api_zzc_O)));
        return taskCompletionSource.getTask();
    }

    public Task<Void> zza(Iterable<zzc<?>> iterable) {
        zzzu com_google_android_gms_internal_zzzu = new zzzu(iterable);
        for (zzc apiKey : iterable) {
            zza com_google_android_gms_internal_zzaap_zza = (zza) this.zzazt.get(apiKey.getApiKey());
            if (com_google_android_gms_internal_zzaap_zza != null) {
                if (!com_google_android_gms_internal_zzaap_zza.isConnected()) {
                }
            }
            this.mHandler.sendMessage(this.mHandler.obtainMessage(1, com_google_android_gms_internal_zzzu));
            return com_google_android_gms_internal_zzzu.getTask();
        }
        com_google_android_gms_internal_zzzu.zzuZ();
        return com_google_android_gms_internal_zzzu.getTask();
    }

    public void zza(ConnectionResult connectionResult, int i) {
        if (!zzc(connectionResult, i)) {
            this.mHandler.sendMessage(this.mHandler.obtainMessage(4, i, 0, connectionResult));
        }
    }

    public void zza(zzc<?> com_google_android_gms_common_api_zzc_) {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(5, com_google_android_gms_common_api_zzc_));
    }

    public <O extends ApiOptions, TResult> void zza(zzc<O> com_google_android_gms_common_api_zzc_O, int i, zzabn<com.google.android.gms.common.api.Api.zzb, TResult> com_google_android_gms_internal_zzabn_com_google_android_gms_common_api_Api_zzb__TResult, TaskCompletionSource<TResult> taskCompletionSource, zzabk com_google_android_gms_internal_zzabk) {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(3, new zzabd(new zzd(i, com_google_android_gms_internal_zzabn_com_google_android_gms_common_api_Api_zzb__TResult, taskCompletionSource, com_google_android_gms_internal_zzabk), this.zzaAU.get(), com_google_android_gms_common_api_zzc_O)));
    }

    public <O extends ApiOptions> void zza(zzc<O> com_google_android_gms_common_api_zzc_O, int i, com.google.android.gms.internal.zzzv.zza<? extends Result, com.google.android.gms.common.api.Api.zzb> com_google_android_gms_internal_zzzv_zza__extends_com_google_android_gms_common_api_Result__com_google_android_gms_common_api_Api_zzb) {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(3, new zzabd(new com.google.android.gms.internal.zzzq.zzb(i, com_google_android_gms_internal_zzzv_zza__extends_com_google_android_gms_common_api_Result__com_google_android_gms_common_api_Api_zzb), this.zzaAU.get(), com_google_android_gms_common_api_zzc_O)));
    }

    public void zza(@NonNull zzaae com_google_android_gms_internal_zzaae) {
        synchronized (zztU) {
            if (this.zzaAV != com_google_android_gms_internal_zzaae) {
                this.zzaAV = com_google_android_gms_internal_zzaae;
                this.zzaAW.clear();
                this.zzaAW.addAll(com_google_android_gms_internal_zzaae.zzvx());
            }
        }
    }

    void zzb(@NonNull zzaae com_google_android_gms_internal_zzaae) {
        synchronized (zztU) {
            if (this.zzaAV == com_google_android_gms_internal_zzaae) {
                this.zzaAV = null;
                this.zzaAW.clear();
            }
        }
    }

    boolean zzc(ConnectionResult connectionResult, int i) {
        if (!connectionResult.hasResolution() && !this.zzaxX.isUserResolvableError(connectionResult.getErrorCode())) {
            return false;
        }
        this.zzaxX.zza(this.mContext, connectionResult, i);
        return true;
    }

    public void zzuW() {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(2));
    }

    public int zzvU() {
        return this.zzaAT.getAndIncrement();
    }
}
