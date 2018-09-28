package com.google.android.gms.internal;

import com.google.android.gms.common.ConnectionResult;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import rx.internal.operators.OnSubscribeConcatMap;

public final class zzbtb extends zzbsd<Object> {
    public static final zzbse zzcnX;
    private final zzbrl zzcmT;

    /* renamed from: com.google.android.gms.internal.zzbtb.2 */
    static /* synthetic */ class C06922 {
        static final /* synthetic */ int[] zzcon;

        static {
            zzcon = new int[zzbtj.values().length];
            try {
                zzcon[zzbtj.BEGIN_ARRAY.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                zzcon[zzbtj.BEGIN_OBJECT.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                zzcon[zzbtj.STRING.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                zzcon[zzbtj.NUMBER.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                zzcon[zzbtj.BOOLEAN.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                zzcon[zzbtj.NULL.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
        }
    }

    /* renamed from: com.google.android.gms.internal.zzbtb.1 */
    static class C12001 implements zzbse {
        C12001() {
        }

        public <T> zzbsd<T> zza(zzbrl com_google_android_gms_internal_zzbrl, zzbth<T> com_google_android_gms_internal_zzbth_T) {
            return com_google_android_gms_internal_zzbth_T.zzacb() == Object.class ? new zzbtb(null) : null;
        }
    }

    static {
        zzcnX = new C12001();
    }

    private zzbtb(zzbrl com_google_android_gms_internal_zzbrl) {
        this.zzcmT = com_google_android_gms_internal_zzbrl;
    }

    public void zza(zzbtk com_google_android_gms_internal_zzbtk, Object obj) throws IOException {
        if (obj == null) {
            com_google_android_gms_internal_zzbtk.zzaca();
            return;
        }
        zzbsd zzj = this.zzcmT.zzj(obj.getClass());
        if (zzj instanceof zzbtb) {
            com_google_android_gms_internal_zzbtk.zzabY();
            com_google_android_gms_internal_zzbtk.zzabZ();
            return;
        }
        zzj.zza(com_google_android_gms_internal_zzbtk, obj);
    }

    public Object zzb(zzbti com_google_android_gms_internal_zzbti) throws IOException {
        switch (C06922.zzcon[com_google_android_gms_internal_zzbti.zzabQ().ordinal()]) {
            case OnSubscribeConcatMap.BOUNDARY /*1*/:
                List arrayList = new ArrayList();
                com_google_android_gms_internal_zzbti.beginArray();
                while (com_google_android_gms_internal_zzbti.hasNext()) {
                    arrayList.add(zzb(com_google_android_gms_internal_zzbti));
                }
                com_google_android_gms_internal_zzbti.endArray();
                return arrayList;
            case OnSubscribeConcatMap.END /*2*/:
                Map com_google_android_gms_internal_zzbsp = new zzbsp();
                com_google_android_gms_internal_zzbti.beginObject();
                while (com_google_android_gms_internal_zzbti.hasNext()) {
                    com_google_android_gms_internal_zzbsp.put(com_google_android_gms_internal_zzbti.nextName(), zzb(com_google_android_gms_internal_zzbti));
                }
                com_google_android_gms_internal_zzbti.endObject();
                return com_google_android_gms_internal_zzbsp;
            case ConnectionResult.SERVICE_DISABLED /*3*/:
                return com_google_android_gms_internal_zzbti.nextString();
            case ConnectionResult.SIGN_IN_REQUIRED /*4*/:
                return Double.valueOf(com_google_android_gms_internal_zzbti.nextDouble());
            case ConnectionResult.INVALID_ACCOUNT /*5*/:
                return Boolean.valueOf(com_google_android_gms_internal_zzbti.nextBoolean());
            case ConnectionResult.RESOLUTION_REQUIRED /*6*/:
                com_google_android_gms_internal_zzbti.nextNull();
                return null;
            default:
                throw new IllegalStateException();
        }
    }
}
