package com.google.android.gms.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.ApiOptions.HasOptions;
import com.google.android.gms.common.api.Api.zzf;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.zzg;

public final class zzaxm {
    public static final Api<zzaxo> API;
    public static final Api<zza> zzaJq;
    public static final zzf<zzaxy> zzahc;
    public static final com.google.android.gms.common.api.Api.zza<zzaxy, zzaxo> zzahd;
    public static final Scope zzajd;
    public static final Scope zzaje;
    public static final zzf<zzaxy> zzbCd;
    static final com.google.android.gms.common.api.Api.zza<zzaxy, zza> zzbCe;

    /* renamed from: com.google.android.gms.internal.zzaxm.1 */
    class C13311 extends com.google.android.gms.common.api.Api.zza<zzaxy, zzaxo> {
        C13311() {
        }

        public zzaxy zza(Context context, Looper looper, zzg com_google_android_gms_common_internal_zzg, zzaxo com_google_android_gms_internal_zzaxo, ConnectionCallbacks connectionCallbacks, OnConnectionFailedListener onConnectionFailedListener) {
            return new zzaxy(context, looper, true, com_google_android_gms_common_internal_zzg, com_google_android_gms_internal_zzaxo == null ? zzaxo.zzbCg : com_google_android_gms_internal_zzaxo, connectionCallbacks, onConnectionFailedListener);
        }
    }

    /* renamed from: com.google.android.gms.internal.zzaxm.2 */
    class C13322 extends com.google.android.gms.common.api.Api.zza<zzaxy, zza> {
        C13322() {
        }

        public zzaxy zza(Context context, Looper looper, zzg com_google_android_gms_common_internal_zzg, zza com_google_android_gms_internal_zzaxm_zza, ConnectionCallbacks connectionCallbacks, OnConnectionFailedListener onConnectionFailedListener) {
            return new zzaxy(context, looper, false, com_google_android_gms_common_internal_zzg, com_google_android_gms_internal_zzaxm_zza.zzOd(), connectionCallbacks, onConnectionFailedListener);
        }
    }

    public static class zza implements HasOptions {
        private final Bundle zzbCf;

        public Bundle zzOd() {
            return this.zzbCf;
        }
    }

    static {
        zzahc = new zzf();
        zzbCd = new zzf();
        zzahd = new C13311();
        zzbCe = new C13322();
        zzajd = new Scope(Scopes.PROFILE);
        zzaje = new Scope(Scopes.EMAIL);
        API = new Api("SignIn.API", zzahd, zzahc);
        zzaJq = new Api("SignIn.INTERNAL_API", zzbCe, zzbCd);
    }
}
