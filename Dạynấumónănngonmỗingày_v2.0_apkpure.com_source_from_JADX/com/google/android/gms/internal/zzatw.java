package com.google.android.gms.internal;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.support.v4.view.PointerIconCompat;
import android.text.TextUtils;
import com.facebook.internal.NativeProtocol;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.internal.zzf.zzb;
import com.google.android.gms.common.internal.zzf.zzc;
import com.google.android.gms.common.util.zze;
import com.google.android.gms.measurement.AppMeasurement.zzf;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import rx.internal.operators.OnSubscribeConcatMap;
import rx.internal.schedulers.NewThreadWorker;

public class zzatw extends zzats {
    private zzate zzbuA;
    private Boolean zzbuB;
    private final zzasv zzbuC;
    private final zzatz zzbuD;
    private final List<Runnable> zzbuE;
    private final zzasv zzbuF;
    private final zza zzbuz;

    /* renamed from: com.google.android.gms.internal.zzatw.3 */
    class C06693 implements Runnable {
        final /* synthetic */ zzatw zzbuG;

        C06693(zzatw com_google_android_gms_internal_zzatw) {
            this.zzbuG = com_google_android_gms_internal_zzatw;
        }

        public void run() {
            zzate zzc = this.zzbuG.zzbuA;
            if (zzc == null) {
                this.zzbuG.zzJt().zzLa().log("Failed to send measurementEnabled to service");
                return;
            }
            try {
                zzc.zzb(this.zzbuG.zzJj().zzfH(this.zzbuG.zzJt().zzLh()));
                this.zzbuG.zznN();
            } catch (RemoteException e) {
                this.zzbuG.zzJt().zzLa().zzj("Failed to send measurementEnabled to the service", e);
            }
        }
    }

    /* renamed from: com.google.android.gms.internal.zzatw.4 */
    class C06704 implements Runnable {
        final /* synthetic */ String zzbky;
        final /* synthetic */ zzatb zzbtP;
        final /* synthetic */ zzatw zzbuG;
        final /* synthetic */ boolean zzbuH;
        final /* synthetic */ boolean zzbuI;

        C06704(zzatw com_google_android_gms_internal_zzatw, boolean z, boolean z2, zzatb com_google_android_gms_internal_zzatb, String str) {
            this.zzbuG = com_google_android_gms_internal_zzatw;
            this.zzbuH = z;
            this.zzbuI = z2;
            this.zzbtP = com_google_android_gms_internal_zzatb;
            this.zzbky = str;
        }

        public void run() {
            zzate zzc = this.zzbuG.zzbuA;
            if (zzc == null) {
                this.zzbuG.zzJt().zzLa().log("Discarding data. Failed to send event to service");
                return;
            }
            if (this.zzbuH) {
                this.zzbuG.zza(zzc, this.zzbuI ? null : this.zzbtP);
            } else {
                try {
                    if (TextUtils.isEmpty(this.zzbky)) {
                        zzc.zza(this.zzbtP, this.zzbuG.zzJj().zzfH(this.zzbuG.zzJt().zzLh()));
                    } else {
                        zzc.zza(this.zzbtP, this.zzbky, this.zzbuG.zzJt().zzLh());
                    }
                } catch (RemoteException e) {
                    this.zzbuG.zzJt().zzLa().zzj("Failed to send event to the service", e);
                }
            }
            this.zzbuG.zznN();
        }
    }

    /* renamed from: com.google.android.gms.internal.zzatw.5 */
    class C06715 implements Runnable {
        final /* synthetic */ zzaub zzbtQ;
        final /* synthetic */ zzatw zzbuG;
        final /* synthetic */ boolean zzbuI;

        C06715(zzatw com_google_android_gms_internal_zzatw, boolean z, zzaub com_google_android_gms_internal_zzaub) {
            this.zzbuG = com_google_android_gms_internal_zzatw;
            this.zzbuI = z;
            this.zzbtQ = com_google_android_gms_internal_zzaub;
        }

        public void run() {
            zzate zzc = this.zzbuG.zzbuA;
            if (zzc == null) {
                this.zzbuG.zzJt().zzLa().log("Discarding data. Failed to set user attribute");
                return;
            }
            this.zzbuG.zza(zzc, this.zzbuI ? null : this.zzbtQ);
            this.zzbuG.zznN();
        }
    }

    /* renamed from: com.google.android.gms.internal.zzatw.6 */
    class C06726 implements Runnable {
        final /* synthetic */ zzatw zzbuG;
        final /* synthetic */ AtomicReference zzbuJ;
        final /* synthetic */ boolean zzbuk;

        C06726(zzatw com_google_android_gms_internal_zzatw, AtomicReference atomicReference, boolean z) {
            this.zzbuG = com_google_android_gms_internal_zzatw;
            this.zzbuJ = atomicReference;
            this.zzbuk = z;
        }

        public void run() {
            synchronized (this.zzbuJ) {
                try {
                    zzate zzc = this.zzbuG.zzbuA;
                    if (zzc == null) {
                        this.zzbuG.zzJt().zzLa().log("Failed to get user properties");
                        return;
                    }
                    this.zzbuJ.set(zzc.zza(this.zzbuG.zzJj().zzfH(null), this.zzbuk));
                    this.zzbuG.zznN();
                    this.zzbuJ.notify();
                } catch (RemoteException e) {
                    this.zzbuG.zzJt().zzLa().zzj("Failed to get user properties", e);
                } finally {
                    this.zzbuJ.notify();
                }
            }
        }
    }

    /* renamed from: com.google.android.gms.internal.zzatw.7 */
    class C06737 implements Runnable {
        final /* synthetic */ zzatw zzbuG;
        final /* synthetic */ AtomicReference zzbuJ;

        C06737(zzatw com_google_android_gms_internal_zzatw, AtomicReference atomicReference) {
            this.zzbuG = com_google_android_gms_internal_zzatw;
            this.zzbuJ = atomicReference;
        }

        public void run() {
            synchronized (this.zzbuJ) {
                try {
                    zzate zzc = this.zzbuG.zzbuA;
                    if (zzc == null) {
                        this.zzbuG.zzJt().zzLa().log("Failed to get app instance id");
                        return;
                    }
                    this.zzbuJ.set(zzc.zzc(this.zzbuG.zzJj().zzfH(null)));
                    this.zzbuG.zznN();
                    this.zzbuJ.notify();
                } catch (RemoteException e) {
                    this.zzbuG.zzJt().zzLa().zzj("Failed to get app instance id", e);
                } finally {
                    this.zzbuJ.notify();
                }
            }
        }
    }

    /* renamed from: com.google.android.gms.internal.zzatw.8 */
    class C06748 implements Runnable {
        final /* synthetic */ zzatw zzbuG;

        C06748(zzatw com_google_android_gms_internal_zzatw) {
            this.zzbuG = com_google_android_gms_internal_zzatw;
        }

        public void run() {
            zzate zzc = this.zzbuG.zzbuA;
            if (zzc == null) {
                this.zzbuG.zzJt().zzLa().log("Discarding data. Failed to send app launch");
                return;
            }
            try {
                this.zzbuG.zza(zzc, null);
                zzc.zza(this.zzbuG.zzJj().zzfH(this.zzbuG.zzJt().zzLh()));
                this.zzbuG.zznN();
            } catch (RemoteException e) {
                this.zzbuG.zzJt().zzLa().zzj("Failed to send app launch to the service", e);
            }
        }
    }

    /* renamed from: com.google.android.gms.internal.zzatw.9 */
    class C06759 implements Runnable {
        final /* synthetic */ zzatw zzbuG;
        final /* synthetic */ zzf zzbuK;

        C06759(zzatw com_google_android_gms_internal_zzatw, zzf com_google_android_gms_measurement_AppMeasurement_zzf) {
            this.zzbuG = com_google_android_gms_internal_zzatw;
            this.zzbuK = com_google_android_gms_measurement_AppMeasurement_zzf;
        }

        public void run() {
            zzate zzc = this.zzbuG.zzbuA;
            if (zzc == null) {
                this.zzbuG.zzJt().zzLa().log("Failed to send current screen to service");
                return;
            }
            try {
                if (this.zzbuK == null) {
                    zzc.zza(0, null, null, this.zzbuG.getContext().getPackageName());
                } else {
                    zzc.zza(this.zzbuK.zzbpB, this.zzbuK.zzbpz, this.zzbuK.zzbpA, this.zzbuG.getContext().getPackageName());
                }
                this.zzbuG.zznN();
            } catch (RemoteException e) {
                this.zzbuG.zzJt().zzLa().zzj("Failed to send current screen to the service", e);
            }
        }
    }

    /* renamed from: com.google.android.gms.internal.zzatw.1 */
    class C11701 extends zzasv {
        final /* synthetic */ zzatw zzbuG;

        C11701(zzatw com_google_android_gms_internal_zzatw, zzatp com_google_android_gms_internal_zzatp) {
            this.zzbuG = com_google_android_gms_internal_zzatw;
            super(com_google_android_gms_internal_zzatp);
        }

        public void run() {
            this.zzbuG.zznO();
        }
    }

    /* renamed from: com.google.android.gms.internal.zzatw.2 */
    class C11712 extends zzasv {
        final /* synthetic */ zzatw zzbuG;

        C11712(zzatw com_google_android_gms_internal_zzatw, zzatp com_google_android_gms_internal_zzatp) {
            this.zzbuG = com_google_android_gms_internal_zzatw;
            super(com_google_android_gms_internal_zzatp);
        }

        public void run() {
            this.zzbuG.zzJt().zzLc().log("Tasks have been queued for a long time");
        }
    }

    protected class zza implements ServiceConnection, zzb, zzc {
        final /* synthetic */ zzatw zzbuG;
        private volatile boolean zzbuL;
        private volatile zzath zzbuM;

        /* renamed from: com.google.android.gms.internal.zzatw.zza.1 */
        class C06761 implements Runnable {
            final /* synthetic */ zzate zzbuN;
            final /* synthetic */ zza zzbuO;

            C06761(zza com_google_android_gms_internal_zzatw_zza, zzate com_google_android_gms_internal_zzate) {
                this.zzbuO = com_google_android_gms_internal_zzatw_zza;
                this.zzbuN = com_google_android_gms_internal_zzate;
            }

            public void run() {
                synchronized (this.zzbuO) {
                    this.zzbuO.zzbuL = false;
                    if (!this.zzbuO.zzbuG.isConnected()) {
                        this.zzbuO.zzbuG.zzJt().zzLg().log("Connected to service");
                        this.zzbuO.zzbuG.zza(this.zzbuN);
                    }
                }
            }
        }

        /* renamed from: com.google.android.gms.internal.zzatw.zza.2 */
        class C06772 implements Runnable {
            final /* synthetic */ ComponentName val$name;
            final /* synthetic */ zza zzbuO;

            C06772(zza com_google_android_gms_internal_zzatw_zza, ComponentName componentName) {
                this.zzbuO = com_google_android_gms_internal_zzatw_zza;
                this.val$name = componentName;
            }

            public void run() {
                this.zzbuO.zzbuG.onServiceDisconnected(this.val$name);
            }
        }

        /* renamed from: com.google.android.gms.internal.zzatw.zza.3 */
        class C06783 implements Runnable {
            final /* synthetic */ zza zzbuO;
            final /* synthetic */ zzate zzbuP;

            C06783(zza com_google_android_gms_internal_zzatw_zza, zzate com_google_android_gms_internal_zzate) {
                this.zzbuO = com_google_android_gms_internal_zzatw_zza;
                this.zzbuP = com_google_android_gms_internal_zzate;
            }

            public void run() {
                synchronized (this.zzbuO) {
                    this.zzbuO.zzbuL = false;
                    if (!this.zzbuO.zzbuG.isConnected()) {
                        this.zzbuO.zzbuG.zzJt().zzLf().log("Connected to remote service");
                        this.zzbuO.zzbuG.zza(this.zzbuP);
                    }
                }
            }
        }

        /* renamed from: com.google.android.gms.internal.zzatw.zza.4 */
        class C06794 implements Runnable {
            final /* synthetic */ zza zzbuO;

            C06794(zza com_google_android_gms_internal_zzatw_zza) {
                this.zzbuO = com_google_android_gms_internal_zzatw_zza;
            }

            public void run() {
                zzatw com_google_android_gms_internal_zzatw = this.zzbuO.zzbuG;
                Context context = this.zzbuO.zzbuG.getContext();
                this.zzbuO.zzbuG.zzJv().zzKk();
                com_google_android_gms_internal_zzatw.onServiceDisconnected(new ComponentName(context, "com.google.android.gms.measurement.AppMeasurementService"));
            }
        }

        protected zza(zzatw com_google_android_gms_internal_zzatw) {
            this.zzbuG = com_google_android_gms_internal_zzatw;
        }

        @MainThread
        public void onConnected(@Nullable Bundle bundle) {
            zzac.zzdn("MeasurementServiceConnection.onConnected");
            synchronized (this) {
                try {
                    zzate com_google_android_gms_internal_zzate = (zzate) this.zzbuM.zzwW();
                    this.zzbuM = null;
                    this.zzbuG.zzJs().zzm(new C06783(this, com_google_android_gms_internal_zzate));
                } catch (DeadObjectException e) {
                    this.zzbuM = null;
                    this.zzbuL = false;
                } catch (IllegalStateException e2) {
                    this.zzbuM = null;
                    this.zzbuL = false;
                }
            }
        }

        @MainThread
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            zzac.zzdn("MeasurementServiceConnection.onConnectionFailed");
            zzati zzLu = this.zzbuG.zzbpw.zzLu();
            if (zzLu != null) {
                zzLu.zzLc().zzj("Service connection failed", connectionResult);
            }
            synchronized (this) {
                this.zzbuL = false;
                this.zzbuM = null;
            }
        }

        @MainThread
        public void onConnectionSuspended(int i) {
            zzac.zzdn("MeasurementServiceConnection.onConnectionSuspended");
            this.zzbuG.zzJt().zzLf().log("Service connection suspended");
            this.zzbuG.zzJs().zzm(new C06794(this));
        }

        @MainThread
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            zzac.zzdn("MeasurementServiceConnection.onServiceConnected");
            synchronized (this) {
                if (iBinder == null) {
                    this.zzbuL = false;
                    this.zzbuG.zzJt().zzLa().log("Service connected with null binder");
                    return;
                }
                zzate com_google_android_gms_internal_zzate = null;
                try {
                    String interfaceDescriptor = iBinder.getInterfaceDescriptor();
                    if ("com.google.android.gms.measurement.internal.IMeasurementService".equals(interfaceDescriptor)) {
                        com_google_android_gms_internal_zzate = com.google.android.gms.internal.zzate.zza.zzer(iBinder);
                        this.zzbuG.zzJt().zzLg().log("Bound to IMeasurementService interface");
                    } else {
                        this.zzbuG.zzJt().zzLa().zzj("Got binder with a wrong descriptor", interfaceDescriptor);
                    }
                } catch (RemoteException e) {
                    this.zzbuG.zzJt().zzLa().log("Service connect failed to get IMeasurementService");
                }
                if (com_google_android_gms_internal_zzate == null) {
                    this.zzbuL = false;
                    try {
                        com.google.android.gms.common.stats.zza.zzyc().zza(this.zzbuG.getContext(), this.zzbuG.zzbuz);
                    } catch (IllegalArgumentException e2) {
                    }
                } else {
                    this.zzbuG.zzJs().zzm(new C06761(this, com_google_android_gms_internal_zzate));
                }
                return;
            }
        }

        @MainThread
        public void onServiceDisconnected(ComponentName componentName) {
            zzac.zzdn("MeasurementServiceConnection.onServiceDisconnected");
            this.zzbuG.zzJt().zzLf().log("Service disconnected");
            this.zzbuG.zzJs().zzm(new C06772(this, componentName));
        }

        @WorkerThread
        public void zzC(Intent intent) {
            this.zzbuG.zzmq();
            Context context = this.zzbuG.getContext();
            com.google.android.gms.common.stats.zza zzyc = com.google.android.gms.common.stats.zza.zzyc();
            synchronized (this) {
                if (this.zzbuL) {
                    this.zzbuG.zzJt().zzLg().log("Connection attempt already in progress");
                    return;
                }
                this.zzbuL = true;
                zzyc.zza(context, intent, this.zzbuG.zzbuz, 129);
            }
        }

        @WorkerThread
        public void zzMb() {
            this.zzbuG.zzmq();
            Context context = this.zzbuG.getContext();
            synchronized (this) {
                if (this.zzbuL) {
                    this.zzbuG.zzJt().zzLg().log("Connection attempt already in progress");
                } else if (this.zzbuM != null) {
                    this.zzbuG.zzJt().zzLg().log("Already awaiting connection attempt");
                } else {
                    this.zzbuM = new zzath(context, Looper.getMainLooper(), this, this);
                    this.zzbuG.zzJt().zzLg().log("Connecting to remote service");
                    this.zzbuL = true;
                    this.zzbuM.zzwT();
                }
            }
        }
    }

    protected zzatw(zzatp com_google_android_gms_internal_zzatp) {
        super(com_google_android_gms_internal_zzatp);
        this.zzbuE = new ArrayList();
        this.zzbuD = new zzatz(com_google_android_gms_internal_zzatp.zznq());
        this.zzbuz = new zza(this);
        this.zzbuC = new C11701(this, com_google_android_gms_internal_zzatp);
        this.zzbuF = new C11712(this, com_google_android_gms_internal_zzatp);
    }

    @WorkerThread
    private void onServiceDisconnected(ComponentName componentName) {
        zzmq();
        if (this.zzbuA != null) {
            this.zzbuA = null;
            zzJt().zzLg().zzj("Disconnected from device MeasurementService", componentName);
            zzLZ();
        }
    }

    private boolean zzLX() {
        zzJv().zzKk();
        List queryIntentServices = getContext().getPackageManager().queryIntentServices(new Intent().setClassName(getContext(), "com.google.android.gms.measurement.AppMeasurementService"), NativeProtocol.MESSAGE_GET_ACCESS_TOKEN_REQUEST);
        return queryIntentServices != null && queryIntentServices.size() > 0;
    }

    @WorkerThread
    private void zzLZ() {
        zzmq();
        zzoc();
    }

    @WorkerThread
    private void zzMa() {
        zzmq();
        zzJt().zzLg().zzj("Processing queued up service tasks", Integer.valueOf(this.zzbuE.size()));
        for (Runnable zzm : this.zzbuE) {
            zzJs().zzm(zzm);
        }
        this.zzbuE.clear();
        this.zzbuF.cancel();
    }

    @WorkerThread
    private void zza(zzate com_google_android_gms_internal_zzate) {
        zzmq();
        zzac.zzw(com_google_android_gms_internal_zzate);
        this.zzbuA = com_google_android_gms_internal_zzate;
        zznN();
        zzMa();
    }

    @WorkerThread
    private void zznN() {
        zzmq();
        this.zzbuD.start();
        this.zzbuC.zzx(zzJv().zzoQ());
    }

    @WorkerThread
    private void zznO() {
        zzmq();
        if (isConnected()) {
            zzJt().zzLg().log("Inactivity, disconnecting from the service");
            disconnect();
        }
    }

    @WorkerThread
    private void zzo(Runnable runnable) throws IllegalStateException {
        zzmq();
        if (isConnected()) {
            runnable.run();
        } else if (((long) this.zzbuE.size()) >= zzJv().zzKq()) {
            zzJt().zzLa().log("Discarding data. Max runnable queue size reached");
        } else {
            this.zzbuE.add(runnable);
            this.zzbuF.zzx(60000);
            zzoc();
        }
    }

    @WorkerThread
    public void disconnect() {
        zzmq();
        zznA();
        try {
            com.google.android.gms.common.stats.zza.zzyc().zza(getContext(), this.zzbuz);
        } catch (IllegalStateException e) {
        } catch (IllegalArgumentException e2) {
        }
        this.zzbuA = null;
    }

    public /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    @WorkerThread
    public boolean isConnected() {
        zzmq();
        zznA();
        return this.zzbuA != null;
    }

    public /* bridge */ /* synthetic */ void zzJd() {
        super.zzJd();
    }

    public /* bridge */ /* synthetic */ void zzJe() {
        super.zzJe();
    }

    public /* bridge */ /* synthetic */ void zzJf() {
        super.zzJf();
    }

    public /* bridge */ /* synthetic */ zzaso zzJg() {
        return super.zzJg();
    }

    public /* bridge */ /* synthetic */ zzass zzJh() {
        return super.zzJh();
    }

    public /* bridge */ /* synthetic */ zzatu zzJi() {
        return super.zzJi();
    }

    public /* bridge */ /* synthetic */ zzatf zzJj() {
        return super.zzJj();
    }

    public /* bridge */ /* synthetic */ zzasw zzJk() {
        return super.zzJk();
    }

    public /* bridge */ /* synthetic */ zzatw zzJl() {
        return super.zzJl();
    }

    public /* bridge */ /* synthetic */ zzatv zzJm() {
        return super.zzJm();
    }

    public /* bridge */ /* synthetic */ zzatg zzJn() {
        return super.zzJn();
    }

    public /* bridge */ /* synthetic */ zzasu zzJo() {
        return super.zzJo();
    }

    public /* bridge */ /* synthetic */ zzaue zzJp() {
        return super.zzJp();
    }

    public /* bridge */ /* synthetic */ zzatn zzJq() {
        return super.zzJq();
    }

    public /* bridge */ /* synthetic */ zzaty zzJr() {
        return super.zzJr();
    }

    public /* bridge */ /* synthetic */ zzato zzJs() {
        return super.zzJs();
    }

    public /* bridge */ /* synthetic */ zzati zzJt() {
        return super.zzJt();
    }

    public /* bridge */ /* synthetic */ zzatl zzJu() {
        return super.zzJu();
    }

    public /* bridge */ /* synthetic */ zzast zzJv() {
        return super.zzJv();
    }

    @WorkerThread
    protected void zzLR() {
        zzmq();
        zznA();
        zzo(new C06748(this));
    }

    @WorkerThread
    protected void zzLW() {
        zzmq();
        zznA();
        zzo(new C06693(this));
    }

    @WorkerThread
    protected boolean zzLY() {
        zzmq();
        zznA();
        zzJv().zzKk();
        zzJt().zzLg().log("Checking service availability");
        switch (com.google.android.gms.common.zzc.zzuz().isGooglePlayServicesAvailable(getContext())) {
            case NewThreadWorker.PURGE_FREQUENCY:
                zzJt().zzLg().log("Service available");
                return true;
            case OnSubscribeConcatMap.BOUNDARY /*1*/:
                zzJt().zzLg().log("Service missing");
                return false;
            case OnSubscribeConcatMap.END /*2*/:
                zzJt().zzLf().log("Service container out of date");
                return true;
            case ConnectionResult.SERVICE_DISABLED /*3*/:
                zzJt().zzLc().log("Service disabled");
                return false;
            case ConnectionResult.SERVICE_INVALID /*9*/:
                zzJt().zzLc().log("Service invalid");
                return false;
            case ConnectionResult.SERVICE_UPDATING /*18*/:
                zzJt().zzLc().log("Service updating");
                return true;
            default:
                return false;
        }
    }

    @WorkerThread
    void zza(zzate com_google_android_gms_internal_zzate, com.google.android.gms.common.internal.safeparcel.zza com_google_android_gms_common_internal_safeparcel_zza) {
        Object obj;
        zzmq();
        zzJe();
        zznA();
        if (VERSION.SDK_INT >= 11) {
            zzJv().zzKk();
            obj = 1;
        } else {
            obj = null;
        }
        List<com.google.android.gms.common.internal.safeparcel.zza> arrayList = new ArrayList();
        zzJv().zzKt();
        int i = 100;
        for (int i2 = 0; i2 < PointerIconCompat.TYPE_CONTEXT_MENU && r3 == 100; i2++) {
            if (obj != null) {
                Object zzls = zzJn().zzls(100);
                if (zzls != null) {
                    arrayList.addAll(zzls);
                    i = zzls.size();
                    if (com_google_android_gms_common_internal_safeparcel_zza != null && r3 < 100) {
                        arrayList.add(com_google_android_gms_common_internal_safeparcel_zza);
                    }
                    for (com.google.android.gms.common.internal.safeparcel.zza com_google_android_gms_common_internal_safeparcel_zza2 : arrayList) {
                        if (com_google_android_gms_common_internal_safeparcel_zza2 instanceof zzatb) {
                            try {
                                com_google_android_gms_internal_zzate.zza((zzatb) com_google_android_gms_common_internal_safeparcel_zza2, zzJj().zzfH(zzJt().zzLh()));
                            } catch (RemoteException e) {
                                zzJt().zzLa().zzj("Failed to send event to the service", e);
                            }
                        } else if (com_google_android_gms_common_internal_safeparcel_zza2 instanceof zzaub) {
                            zzJt().zzLa().log("Discarding data. Unrecognized parcel type.");
                        } else {
                            try {
                                com_google_android_gms_internal_zzate.zza((zzaub) com_google_android_gms_common_internal_safeparcel_zza2, zzJj().zzfH(zzJt().zzLh()));
                            } catch (RemoteException e2) {
                                zzJt().zzLa().zzj("Failed to send attribute to the service", e2);
                            }
                        }
                    }
                }
            }
            i = 0;
            arrayList.add(com_google_android_gms_common_internal_safeparcel_zza);
            for (com.google.android.gms.common.internal.safeparcel.zza com_google_android_gms_common_internal_safeparcel_zza22 : arrayList) {
                if (com_google_android_gms_common_internal_safeparcel_zza22 instanceof zzatb) {
                    com_google_android_gms_internal_zzate.zza((zzatb) com_google_android_gms_common_internal_safeparcel_zza22, zzJj().zzfH(zzJt().zzLh()));
                } else if (com_google_android_gms_common_internal_safeparcel_zza22 instanceof zzaub) {
                    zzJt().zzLa().log("Discarding data. Unrecognized parcel type.");
                } else {
                    com_google_android_gms_internal_zzate.zza((zzaub) com_google_android_gms_common_internal_safeparcel_zza22, zzJj().zzfH(zzJt().zzLh()));
                }
            }
        }
    }

    @WorkerThread
    protected void zza(zzf com_google_android_gms_measurement_AppMeasurement_zzf) {
        zzmq();
        zznA();
        zzo(new C06759(this, com_google_android_gms_measurement_AppMeasurement_zzf));
    }

    @WorkerThread
    public void zza(AtomicReference<String> atomicReference) {
        zzmq();
        zznA();
        zzo(new C06737(this, atomicReference));
    }

    @WorkerThread
    protected void zza(AtomicReference<List<zzaub>> atomicReference, boolean z) {
        zzmq();
        zznA();
        zzo(new C06726(this, atomicReference, z));
    }

    @WorkerThread
    protected void zzb(zzaub com_google_android_gms_internal_zzaub) {
        boolean z;
        boolean z2 = true;
        zzmq();
        zznA();
        if (VERSION.SDK_INT >= 11) {
            zzJv().zzKk();
            z = true;
        } else {
            z = false;
        }
        if (!(z && zzJn().zza(com_google_android_gms_internal_zzaub))) {
            z2 = false;
        }
        zzo(new C06715(this, z2, com_google_android_gms_internal_zzaub));
    }

    @WorkerThread
    protected void zzc(zzatb com_google_android_gms_internal_zzatb, String str) {
        boolean z;
        boolean z2 = true;
        zzac.zzw(com_google_android_gms_internal_zzatb);
        zzmq();
        zznA();
        if (VERSION.SDK_INT >= 11) {
            zzJv().zzKk();
            z = true;
        } else {
            z = false;
        }
        if (!(z && zzJn().zza(com_google_android_gms_internal_zzatb))) {
            z2 = false;
        }
        zzo(new C06704(this, z, z2, com_google_android_gms_internal_zzatb, str));
    }

    public /* bridge */ /* synthetic */ void zzmq() {
        super.zzmq();
    }

    protected void zzmr() {
    }

    public /* bridge */ /* synthetic */ zze zznq() {
        return super.zznq();
    }

    @WorkerThread
    void zzoc() {
        zzmq();
        zznA();
        if (!isConnected()) {
            if (this.zzbuB == null) {
                this.zzbuB = zzJu().zzLn();
                if (this.zzbuB == null) {
                    zzJt().zzLg().log("State of service unknown");
                    this.zzbuB = Boolean.valueOf(zzLY());
                    zzJu().zzaF(this.zzbuB.booleanValue());
                }
            }
            if (this.zzbuB.booleanValue()) {
                zzJt().zzLg().log("Using measurement service");
                this.zzbuz.zzMb();
            } else if (zzLX()) {
                zzJt().zzLg().log("Using local app measurement service");
                Intent intent = new Intent("com.google.android.gms.measurement.START");
                Context context = getContext();
                zzJv().zzKk();
                intent.setComponent(new ComponentName(context, "com.google.android.gms.measurement.AppMeasurementService"));
                this.zzbuz.zzC(intent);
            } else {
                zzJt().zzLa().log("Unable to use remote or local measurement implementation. Please register the AppMeasurementService service in the app manifest");
            }
        }
    }
}
