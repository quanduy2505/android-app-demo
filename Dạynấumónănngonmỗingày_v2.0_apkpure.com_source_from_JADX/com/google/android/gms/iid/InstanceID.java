package com.google.android.gms.iid;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.view.MotionEventCompat;
import android.util.Base64;
import android.util.Log;
import com.facebook.appevents.AppEventsConstants;
import com.facebook.internal.ServerProtocol;
import com.facebook.share.internal.ShareConstants;
import java.io.IOException;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import me.wangyuwei.loadingview.C0801R;
import rx.android.BuildConfig;

public class InstanceID {
    public static final String ERROR_BACKOFF = "RETRY_LATER";
    public static final String ERROR_MAIN_THREAD = "MAIN_THREAD";
    public static final String ERROR_MISSING_INSTANCEID_SERVICE = "MISSING_INSTANCEID_SERVICE";
    public static final String ERROR_SERVICE_NOT_AVAILABLE = "SERVICE_NOT_AVAILABLE";
    public static final String ERROR_TIMEOUT = "TIMEOUT";
    static Map<String, InstanceID> zzbha;
    private static zzd zzbhb;
    private static zzc zzbhc;
    static String zzbhg;
    Context mContext;
    KeyPair zzbhd;
    String zzbhe;
    long zzbhf;

    static {
        zzbha = new HashMap();
    }

    protected InstanceID(Context context, String str, Bundle bundle) {
        this.zzbhe = BuildConfig.VERSION_NAME;
        this.mContext = context.getApplicationContext();
        this.zzbhe = str;
    }

    public static InstanceID getInstance(Context context) {
        return zza(context, null);
    }

    public static synchronized InstanceID zza(Context context, Bundle bundle) {
        InstanceID instanceID;
        synchronized (InstanceID.class) {
            String string = bundle == null ? BuildConfig.VERSION_NAME : bundle.getString("subtype");
            String str = string == null ? BuildConfig.VERSION_NAME : string;
            Context applicationContext = context.getApplicationContext();
            if (zzbhb == null) {
                zzbhb = new zzd(applicationContext);
                zzbhc = new zzc(applicationContext);
            }
            zzbhg = Integer.toString(zzbf(applicationContext));
            instanceID = (InstanceID) zzbha.get(str);
            if (instanceID == null) {
                instanceID = new InstanceID(applicationContext, str, bundle);
                zzbha.put(str, instanceID);
            }
        }
        return instanceID;
    }

    static String zza(KeyPair keyPair) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA1").digest(keyPair.getPublic().getEncoded());
            digest[0] = (byte) (((digest[0] & 15) + C0801R.styleable.AppCompatTheme_spinnerStyle) & MotionEventCompat.ACTION_MASK);
            return Base64.encodeToString(digest, 0, 8, 11);
        } catch (NoSuchAlgorithmException e) {
            Log.w("InstanceID", "Unexpected error, device missing required alghorithms");
            return null;
        }
    }

    static int zzbf(Context context) {
        int i = 0;
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            String valueOf = String.valueOf(e);
            Log.w("InstanceID", new StringBuilder(String.valueOf(valueOf).length() + 38).append("Never happens: can't find own package ").append(valueOf).toString());
            return i;
        }
    }

    static String zzbg(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            String valueOf = String.valueOf(e);
            Log.w("InstanceID", new StringBuilder(String.valueOf(valueOf).length() + 38).append("Never happens: can't find own package ").append(valueOf).toString());
            return null;
        }
    }

    static String zzv(byte[] bArr) {
        return Base64.encodeToString(bArr, 11);
    }

    public void deleteInstanceID() throws IOException {
        zzb("*", "*", null);
        zzGu();
    }

    public void deleteToken(String str, String str2) throws IOException {
        zzb(str, str2, null);
    }

    public long getCreationTime() {
        if (this.zzbhf == 0) {
            String str = zzbhb.get(this.zzbhe, "cre");
            if (str != null) {
                this.zzbhf = Long.parseLong(str);
            }
        }
        return this.zzbhf;
    }

    public String getId() {
        return zza(zzGt());
    }

    public String getToken(String str, String str2) throws IOException {
        return getToken(str, str2, null);
    }

    public String getToken(String str, String str2, Bundle bundle) throws IOException {
        Object obj = null;
        if (Looper.getMainLooper() == Looper.myLooper()) {
            throw new IOException(ERROR_MAIN_THREAD);
        }
        Object obj2 = 1;
        String zzh = zzGx() ? null : zzbhb.zzh(this.zzbhe, str, str2);
        if (zzh == null) {
            if (bundle == null) {
                bundle = new Bundle();
            }
            if (bundle.getString("ttl") != null) {
                obj2 = null;
            }
            if (!"jwt".equals(bundle.getString(ShareConstants.MEDIA_TYPE))) {
                obj = obj2;
            }
            zzh = zzc(str, str2, bundle);
            if (!(zzh == null || r1 == null)) {
                zzbhb.zza(this.zzbhe, str, str2, zzh, zzbhg);
            }
        }
        return zzh;
    }

    KeyPair zzGt() {
        if (this.zzbhd == null) {
            this.zzbhd = zzbhb.zzeM(this.zzbhe);
        }
        if (this.zzbhd == null) {
            this.zzbhf = System.currentTimeMillis();
            this.zzbhd = zzbhb.zze(this.zzbhe, this.zzbhf);
        }
        return this.zzbhd;
    }

    public void zzGu() {
        this.zzbhf = 0;
        zzbhb.zzeN(this.zzbhe);
        this.zzbhd = null;
    }

    public zzd zzGv() {
        return zzbhb;
    }

    public zzc zzGw() {
        return zzbhc;
    }

    boolean zzGx() {
        String str = zzbhb.get("appVersion");
        if (str == null || !str.equals(zzbhg)) {
            return true;
        }
        str = zzbhb.get("lastToken");
        if (str == null) {
            return true;
        }
        return (System.currentTimeMillis() / 1000) - Long.valueOf(Long.parseLong(str)).longValue() > 604800;
    }

    public void zzb(String str, String str2, Bundle bundle) throws IOException {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            throw new IOException(ERROR_MAIN_THREAD);
        }
        zzbhb.zzi(this.zzbhe, str, str2);
        if (bundle == null) {
            bundle = new Bundle();
        }
        bundle.putString("sender", str);
        if (str2 != null) {
            bundle.putString(ServerProtocol.DIALOG_PARAM_SCOPE, str2);
        }
        bundle.putString("subscription", str);
        bundle.putString("delete", AppEventsConstants.EVENT_PARAM_VALUE_YES);
        bundle.putString("X-delete", AppEventsConstants.EVENT_PARAM_VALUE_YES);
        bundle.putString("subtype", BuildConfig.VERSION_NAME.equals(this.zzbhe) ? str : this.zzbhe);
        String str3 = "X-subtype";
        if (!BuildConfig.VERSION_NAME.equals(this.zzbhe)) {
            str = this.zzbhe;
        }
        bundle.putString(str3, str);
        zzbhc.zzt(zzbhc.zza(bundle, zzGt()));
    }

    public String zzc(String str, String str2, Bundle bundle) throws IOException {
        if (str2 != null) {
            bundle.putString(ServerProtocol.DIALOG_PARAM_SCOPE, str2);
        }
        bundle.putString("sender", str);
        String str3 = BuildConfig.VERSION_NAME.equals(this.zzbhe) ? str : this.zzbhe;
        if (!bundle.containsKey("legacy.register")) {
            bundle.putString("subscription", str);
            bundle.putString("subtype", str3);
            bundle.putString("X-subscription", str);
            bundle.putString("X-subtype", str3);
        }
        return zzbhc.zzt(zzbhc.zza(bundle, zzGt()));
    }
}
