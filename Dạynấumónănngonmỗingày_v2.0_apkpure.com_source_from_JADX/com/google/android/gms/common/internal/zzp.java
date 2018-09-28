package com.google.android.gms.common.internal;

import android.content.Intent;
import android.net.Uri;
import android.net.Uri.Builder;
import android.support.annotation.Nullable;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.text.TextUtils;
import com.facebook.share.internal.ShareConstants;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class zzp {
    private static final Uri zzaEQ;
    private static final Uri zzaER;

    static {
        zzaEQ = Uri.parse("http://plus.google.com/");
        zzaER = zzaEQ.buildUpon().appendPath("circles").appendPath("find").build();
    }

    private static Uri zzB(String str, @Nullable String str2) {
        Builder appendQueryParameter = Uri.parse("market://details").buildUpon().appendQueryParameter(ShareConstants.WEB_DIALOG_PARAM_ID, str);
        if (!TextUtils.isEmpty(str2)) {
            appendQueryParameter.appendQueryParameter("pcampaignid", str2);
        }
        return appendQueryParameter.build();
    }

    public static Intent zzC(String str, @Nullable String str2) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setData(zzB(str, str2));
        intent.setPackage(GooglePlayServicesUtil.GOOGLE_PLAY_STORE_PACKAGE);
        intent.addFlags(AccessibilityNodeInfoCompat.ACTION_COLLAPSE);
        return intent;
    }

    public static Intent zzdt(String str) {
        Uri fromParts = Uri.fromParts("package", str, null);
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(fromParts);
        return intent;
    }

    public static Intent zzxu() {
        Intent intent = new Intent("com.google.android.clockwork.home.UPDATE_ANDROID_WEAR_ACTION");
        intent.setPackage("com.google.android.wearable.app");
        return intent;
    }
}
