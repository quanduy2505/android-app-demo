package com.google.android.gms.internal;

import android.app.Activity;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class zzbqv {
    private static final zzbqv zzclU;
    private final Map<Object, zza> zzclV;
    private final Object zzclW;

    private static class zza {
        @NonNull
        private final Activity mActivity;
        @NonNull
        private final Object zzclX;
        @NonNull
        private final Runnable zzv;

        public zza(@NonNull Activity activity, @NonNull Runnable runnable, @NonNull Object obj) {
            this.mActivity = activity;
            this.zzv = runnable;
            this.zzclX = obj;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof zza)) {
                return false;
            }
            zza com_google_android_gms_internal_zzbqv_zza = (zza) obj;
            return com_google_android_gms_internal_zzbqv_zza.zzclX.equals(this.zzclX) && com_google_android_gms_internal_zzbqv_zza.zzv == this.zzv && com_google_android_gms_internal_zzbqv_zza.mActivity == this.mActivity;
        }

        @NonNull
        public Activity getActivity() {
            return this.mActivity;
        }

        public int hashCode() {
            return this.zzclX.hashCode();
        }

        @NonNull
        public Runnable zzTj() {
            return this.zzv;
        }

        @NonNull
        public Object zzabg() {
            return this.zzclX;
        }
    }

    private static class zzb extends zzaaw {
        private final List<zza> mListeners;

        private zzb(zzaax com_google_android_gms_internal_zzaax) {
            super(com_google_android_gms_internal_zzaax);
            this.mListeners = new ArrayList();
            this.zzaBs.zza("StorageOnStopCallback", (zzaaw) this);
        }

        public static zzb zzx(Activity activity) {
            zzaax zzc = zzaaw.zzc(new zzaav(activity));
            zzb com_google_android_gms_internal_zzbqv_zzb = (zzb) zzc.zza("StorageOnStopCallback", zzb.class);
            return com_google_android_gms_internal_zzbqv_zzb == null ? new zzb(zzc) : com_google_android_gms_internal_zzbqv_zzb;
        }

        @MainThread
        public void onStop() {
            ArrayList arrayList;
            synchronized (this.mListeners) {
                arrayList = new ArrayList(this.mListeners);
                this.mListeners.clear();
            }
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                zza com_google_android_gms_internal_zzbqv_zza = (zza) it.next();
                if (com_google_android_gms_internal_zzbqv_zza != null) {
                    Log.d("StorageOnStopCallback", "removing subscription from activity.");
                    com_google_android_gms_internal_zzbqv_zza.zzTj().run();
                    zzbqv.zzabf().zzaH(com_google_android_gms_internal_zzbqv_zza.zzabg());
                }
            }
        }

        public void zza(zza com_google_android_gms_internal_zzbqv_zza) {
            synchronized (this.mListeners) {
                this.mListeners.add(com_google_android_gms_internal_zzbqv_zza);
            }
        }

        public void zzb(zza com_google_android_gms_internal_zzbqv_zza) {
            synchronized (this.mListeners) {
                this.mListeners.remove(com_google_android_gms_internal_zzbqv_zza);
            }
        }
    }

    static {
        zzclU = new zzbqv();
    }

    private zzbqv() {
        this.zzclV = new HashMap();
        this.zzclW = new Object();
    }

    @NonNull
    public static zzbqv zzabf() {
        return zzclU;
    }

    public void zza(@NonNull Activity activity, @NonNull Object obj, @NonNull Runnable runnable) {
        synchronized (this.zzclW) {
            zza com_google_android_gms_internal_zzbqv_zza = new zza(activity, runnable, obj);
            zzb.zzx(activity).zza(com_google_android_gms_internal_zzbqv_zza);
            this.zzclV.put(obj, com_google_android_gms_internal_zzbqv_zza);
        }
    }

    public void zzaH(@NonNull Object obj) {
        synchronized (this.zzclW) {
            zza com_google_android_gms_internal_zzbqv_zza = (zza) this.zzclV.get(obj);
            if (com_google_android_gms_internal_zzbqv_zza != null) {
                zzb.zzx(com_google_android_gms_internal_zzbqv_zza.getActivity()).zzb(com_google_android_gms_internal_zzbqv_zza);
            }
        }
    }
}
