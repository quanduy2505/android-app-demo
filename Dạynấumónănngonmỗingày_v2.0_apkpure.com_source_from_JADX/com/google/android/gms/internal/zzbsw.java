package com.google.android.gms.internal;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public final class zzbsw extends zzbsd<Date> {
    public static final zzbse zzcnX;
    private final DateFormat zzcmh;
    private final DateFormat zzcmi;
    private final DateFormat zzcmj;

    /* renamed from: com.google.android.gms.internal.zzbsw.1 */
    static class C11991 implements zzbse {
        C11991() {
        }

        public <T> zzbsd<T> zza(zzbrl com_google_android_gms_internal_zzbrl, zzbth<T> com_google_android_gms_internal_zzbth_T) {
            return com_google_android_gms_internal_zzbth_T.zzacb() == Date.class ? new zzbsw() : null;
        }
    }

    static {
        zzcnX = new C11991();
    }

    public zzbsw() {
        this.zzcmh = DateFormat.getDateTimeInstance(2, 2, Locale.US);
        this.zzcmi = DateFormat.getDateTimeInstance(2, 2);
        this.zzcmj = zzabP();
    }

    private static DateFormat zzabP() {
        DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return simpleDateFormat;
    }

    private synchronized Date zzjV(String str) {
        Date parse;
        try {
            parse = this.zzcmi.parse(str);
        } catch (ParseException e) {
            try {
                parse = this.zzcmh.parse(str);
            } catch (ParseException e2) {
                try {
                    parse = this.zzcmj.parse(str);
                } catch (Throwable e3) {
                    throw new zzbsa(str, e3);
                }
            }
        }
        return parse;
    }

    public synchronized void zza(zzbtk com_google_android_gms_internal_zzbtk, Date date) throws IOException {
        if (date == null) {
            com_google_android_gms_internal_zzbtk.zzaca();
        } else {
            com_google_android_gms_internal_zzbtk.zzjX(this.zzcmh.format(date));
        }
    }

    public /* synthetic */ Object zzb(zzbti com_google_android_gms_internal_zzbti) throws IOException {
        return zzk(com_google_android_gms_internal_zzbti);
    }

    public Date zzk(zzbti com_google_android_gms_internal_zzbti) throws IOException {
        if (com_google_android_gms_internal_zzbti.zzabQ() != zzbtj.NULL) {
            return zzjV(com_google_android_gms_internal_zzbti.nextString());
        }
        com_google_android_gms_internal_zzbti.nextNull();
        return null;
    }
}
