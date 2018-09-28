package com.google.android.gms.internal;

import android.net.Uri;
import android.net.Uri.Builder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.GetTokenResult;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.http.HttpHost;
import rx.android.BuildConfig;

public class zzbrb {
    public static boolean equals(@Nullable Object obj, @Nullable Object obj2) {
        return zzaa.equal(obj, obj2);
    }

    @Nullable
    public static Uri zzg(@NonNull FirebaseApp firebaseApp, @Nullable String str) throws UnsupportedEncodingException {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        if (str.toLowerCase().startsWith("gs://")) {
            String str2 = "gs://";
            String valueOf = String.valueOf(zzbqx.zzjJ(zzbqx.zzjL(str.substring(5))));
            return Uri.parse(valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2));
        }
        Uri parse = Uri.parse(str);
        valueOf = parse.getScheme().toLowerCase();
        if (equals(valueOf, HttpHost.DEFAULT_SCHEME_NAME) || equals(valueOf, "https")) {
            try {
                int indexOf = parse.getAuthority().toLowerCase().indexOf(zzh(firebaseApp));
                valueOf = zzbqx.zzjK(parse.getEncodedPath());
                if (indexOf == 0 && valueOf.startsWith("/")) {
                    int indexOf2 = valueOf.indexOf("/b/", 0);
                    indexOf = valueOf.indexOf("/", indexOf2 + 3);
                    int indexOf3 = valueOf.indexOf("/o/", 0);
                    if (indexOf2 == -1 || indexOf == -1) {
                        Log.w("StorageUtil", "Only URLs to firebasestorage.googleapis.com are supported.");
                        throw new IllegalArgumentException("Only URLs to firebasestorage.googleapis.com are supported.");
                    }
                    str2 = valueOf.substring(indexOf2 + 3, indexOf);
                    valueOf = indexOf3 != -1 ? valueOf.substring(indexOf3 + 3) : BuildConfig.VERSION_NAME;
                } else if (indexOf > 1) {
                    str2 = parse.getAuthority().substring(0, indexOf - 1);
                } else {
                    Log.w("StorageUtil", "Only URLs to firebasestorage.googleapis.com are supported.");
                    throw new IllegalArgumentException("Only URLs to firebasestorage.googleapis.com are supported.");
                }
                zzac.zzh(str2, "No bucket specified");
                return new Builder().scheme("gs").authority(str2).encodedPath(valueOf).build();
            } catch (RemoteException e) {
                throw new UnsupportedEncodingException("Could not parse Url because the Storage network layer did not load");
            }
        }
        str2 = "StorageUtil";
        String str3 = "FirebaseStorage is unable to support the scheme:";
        valueOf = String.valueOf(valueOf);
        Log.w(str2, valueOf.length() != 0 ? str3.concat(valueOf) : new String(str3));
        throw new IllegalArgumentException("Uri scheme");
    }

    private static String zzh(FirebaseApp firebaseApp) throws RemoteException {
        return zzbre.zzj(firebaseApp).zzabp();
    }

    @Nullable
    public static String zzi(FirebaseApp firebaseApp) {
        Object token;
        String valueOf;
        try {
            token = ((GetTokenResult) Tasks.await(firebaseApp.getToken(false), 30000, TimeUnit.MILLISECONDS)).getToken();
            if (!TextUtils.isEmpty(token)) {
                return token;
            }
            Log.w("StorageUtil", "no auth token for request");
            return null;
        } catch (ExecutionException e) {
            token = e;
            valueOf = String.valueOf(token);
            Log.e("StorageUtil", new StringBuilder(String.valueOf(valueOf).length() + 20).append("error getting token ").append(valueOf).toString());
            return null;
        } catch (InterruptedException e2) {
            token = e2;
            valueOf = String.valueOf(token);
            Log.e("StorageUtil", new StringBuilder(String.valueOf(valueOf).length() + 20).append("error getting token ").append(valueOf).toString());
            return null;
        } catch (TimeoutException e3) {
            token = e3;
            valueOf = String.valueOf(token);
            Log.e("StorageUtil", new StringBuilder(String.valueOf(valueOf).length() + 20).append("error getting token ").append(valueOf).toString());
            return null;
        }
    }

    public static long zzjM(@Nullable String str) {
        if (str == null) {
            return 0;
        }
        String replaceAll = str.replaceAll("Z$", "-0000");
        try {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault()).parse(replaceAll).getTime();
        } catch (Throwable e) {
            Throwable th = e;
            String str2 = "StorageUtil";
            String str3 = "unable to parse datetime:";
            String valueOf = String.valueOf(replaceAll);
            Log.w(str2, valueOf.length() != 0 ? str3.concat(valueOf) : new String(str3), th);
            return 0;
        }
    }
}
