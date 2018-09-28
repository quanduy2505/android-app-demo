package com.google.android.gms.common.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.media.TransportMediator;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.internal.zzacx;

public class zzd {
    public static int zza(PackageInfo packageInfo) {
        if (packageInfo == null || packageInfo.applicationInfo == null) {
            return -1;
        }
        Bundle bundle = packageInfo.applicationInfo.metaData;
        return bundle != null ? bundle.getInt("com.google.android.gms.version", -1) : -1;
    }

    public static int zzv(Context context, String str) {
        return zza(zzw(context, str));
    }

    @Nullable
    public static PackageInfo zzw(Context context, String str) {
        try {
            return zzacx.zzaQ(context).getPackageInfo(str, TransportMediator.FLAG_KEY_MEDIA_NEXT);
        } catch (NameNotFoundException e) {
            return null;
        }
    }

    @TargetApi(12)
    public static boolean zzx(Context context, String str) {
        if (!zzs.zzyy()) {
            return false;
        }
        GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_PACKAGE.equals(str);
        try {
            return (zzacx.zzaQ(context).getApplicationInfo(str, 0).flags & AccessibilityNodeInfoCompat.ACTION_SET_TEXT) != 0;
        } catch (NameNotFoundException e) {
            return false;
        }
    }
}
