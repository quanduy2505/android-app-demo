package com.google.android.gms.internal;

import android.os.Binder;

public abstract class zzabs<T> {
    private static String READ_PERMISSION;
    private static zza zzaCd;
    private static int zzaCe;
    private static final Object zztU;
    protected final String zzAH;
    protected final T zzAI;
    private T zzaCf;

    private interface zza {
        Long getLong(String str, Long l);

        String getString(String str, String str2);

        Boolean zza(String str, Boolean bool);

        Float zzb(String str, Float f);

        Integer zzb(String str, Integer num);
    }

    /* renamed from: com.google.android.gms.internal.zzabs.1 */
    class C11631 extends zzabs<Boolean> {
        C11631(String str, Boolean bool) {
            super(str, bool);
        }

        protected /* synthetic */ Object zzdd(String str) {
            return zzde(str);
        }

        protected Boolean zzde(String str) {
            zza com_google_android_gms_internal_zzabs_zza = null;
            return com_google_android_gms_internal_zzabs_zza.zza(this.zzAH, (Boolean) this.zzAI);
        }
    }

    /* renamed from: com.google.android.gms.internal.zzabs.2 */
    class C11642 extends zzabs<Long> {
        C11642(String str, Long l) {
            super(str, l);
        }

        protected /* synthetic */ Object zzdd(String str) {
            return zzdf(str);
        }

        protected Long zzdf(String str) {
            zza com_google_android_gms_internal_zzabs_zza = null;
            return com_google_android_gms_internal_zzabs_zza.getLong(this.zzAH, (Long) this.zzAI);
        }
    }

    /* renamed from: com.google.android.gms.internal.zzabs.3 */
    class C11653 extends zzabs<Integer> {
        C11653(String str, Integer num) {
            super(str, num);
        }

        protected /* synthetic */ Object zzdd(String str) {
            return zzdg(str);
        }

        protected Integer zzdg(String str) {
            zza com_google_android_gms_internal_zzabs_zza = null;
            return com_google_android_gms_internal_zzabs_zza.zzb(this.zzAH, (Integer) this.zzAI);
        }
    }

    /* renamed from: com.google.android.gms.internal.zzabs.4 */
    class C11664 extends zzabs<Float> {
        C11664(String str, Float f) {
            super(str, f);
        }

        protected /* synthetic */ Object zzdd(String str) {
            return zzdh(str);
        }

        protected Float zzdh(String str) {
            zza com_google_android_gms_internal_zzabs_zza = null;
            return com_google_android_gms_internal_zzabs_zza.zzb(this.zzAH, (Float) this.zzAI);
        }
    }

    /* renamed from: com.google.android.gms.internal.zzabs.5 */
    class C11675 extends zzabs<String> {
        C11675(String str, String str2) {
            super(str, str2);
        }

        protected /* synthetic */ Object zzdd(String str) {
            return zzdi(str);
        }

        protected String zzdi(String str) {
            zza com_google_android_gms_internal_zzabs_zza = null;
            return com_google_android_gms_internal_zzabs_zza.getString(this.zzAH, (String) this.zzAI);
        }
    }

    static {
        zztU = new Object();
        zzaCd = null;
        zzaCe = 0;
        READ_PERMISSION = "com.google.android.providers.gsf.permission.READ_GSERVICES";
    }

    protected zzabs(String str, T t) {
        this.zzaCf = null;
        this.zzAH = str;
        this.zzAI = t;
    }

    public static zzabs<String> zzA(String str, String str2) {
        return new C11675(str, str2);
    }

    public static zzabs<Float> zza(String str, Float f) {
        return new C11664(str, f);
    }

    public static zzabs<Integer> zza(String str, Integer num) {
        return new C11653(str, num);
    }

    public static zzabs<Long> zza(String str, Long l) {
        return new C11642(str, l);
    }

    public static zzabs<Boolean> zzj(String str, boolean z) {
        return new C11631(str, Boolean.valueOf(z));
    }

    public final T get() {
        T zzdd;
        long clearCallingIdentity;
        try {
            zzdd = zzdd(this.zzAH);
        } catch (SecurityException e) {
            clearCallingIdentity = Binder.clearCallingIdentity();
            zzdd = zzdd(this.zzAH);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
        return zzdd;
    }

    protected abstract T zzdd(String str);
}
