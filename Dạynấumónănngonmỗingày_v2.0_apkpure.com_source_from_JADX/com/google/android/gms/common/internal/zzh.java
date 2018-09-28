package com.google.android.gms.common.internal;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.SimpleArrayMap;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.C0598R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.util.zzi;
import com.google.android.gms.internal.zzacx;
import rx.internal.operators.OnSubscribeConcatMap;

public final class zzh {
    private static final SimpleArrayMap<String, String> zzaEg;

    static {
        zzaEg = new SimpleArrayMap();
    }

    public static String zzaB(Context context) {
        String str = context.getApplicationInfo().name;
        if (TextUtils.isEmpty(str)) {
            str = context.getPackageName();
            try {
                str = zzacx.zzaQ(context).zzdE(context.getPackageName()).toString();
            } catch (NameNotFoundException e) {
            }
        }
        return str;
    }

    @Nullable
    public static String zzg(Context context, int i) {
        Resources resources = context.getResources();
        switch (i) {
            case OnSubscribeConcatMap.BOUNDARY /*1*/:
                return resources.getString(C0598R.string.common_google_play_services_install_title);
            case OnSubscribeConcatMap.END /*2*/:
                return resources.getString(C0598R.string.common_google_play_services_update_title);
            case ConnectionResult.SERVICE_DISABLED /*3*/:
                return resources.getString(C0598R.string.common_google_play_services_enable_title);
            case ConnectionResult.SIGN_IN_REQUIRED /*4*/:
            case ConnectionResult.RESOLUTION_REQUIRED /*6*/:
            case ConnectionResult.SERVICE_UPDATING /*18*/:
                return null;
            case ConnectionResult.INVALID_ACCOUNT /*5*/:
                Log.e("GoogleApiAvailability", "An invalid account was specified when connecting. Please provide a valid account.");
                return zzu(context, "common_google_play_services_invalid_account_title");
            case ConnectionResult.NETWORK_ERROR /*7*/:
                Log.e("GoogleApiAvailability", "Network error occurred. Please retry request later.");
                return zzu(context, "common_google_play_services_network_error_title");
            case ConnectionResult.INTERNAL_ERROR /*8*/:
                Log.e("GoogleApiAvailability", "Internal error occurred. Please see logs for detailed information");
                return null;
            case ConnectionResult.SERVICE_INVALID /*9*/:
                Log.e("GoogleApiAvailability", "Google Play services is invalid. Cannot recover.");
                return null;
            case ConnectionResult.DEVELOPER_ERROR /*10*/:
                Log.e("GoogleApiAvailability", "Developer error occurred. Please see logs for detailed information");
                return null;
            case ConnectionResult.LICENSE_CHECK_FAILED /*11*/:
                Log.e("GoogleApiAvailability", "The application is not licensed to the user.");
                return null;
            case ConnectionResult.API_UNAVAILABLE /*16*/:
                Log.e("GoogleApiAvailability", "One of the API components you attempted to connect to is not available.");
                return null;
            case ConnectionResult.SIGN_IN_FAILED /*17*/:
                Log.e("GoogleApiAvailability", "The specified account could not be signed in.");
                return zzu(context, "common_google_play_services_sign_in_failed_title");
            case ConnectionResult.RESTRICTED_PROFILE /*20*/:
                Log.e("GoogleApiAvailability", "The current user profile is restricted and could not use authenticated features.");
                return zzu(context, "common_google_play_services_restricted_profile_title");
            default:
                Log.e("GoogleApiAvailability", "Unexpected error code " + i);
                return null;
        }
    }

    private static String zzg(Context context, String str, String str2) {
        Resources resources = context.getResources();
        String zzu = zzu(context, str);
        if (zzu == null) {
            zzu = resources.getString(C0598R.string.common_google_play_services_unknown_issue);
        }
        return String.format(resources.getConfiguration().locale, zzu, new Object[]{str2});
    }

    @NonNull
    public static String zzh(Context context, int i) {
        String zzu = i == 6 ? zzu(context, "common_google_play_services_resolution_required_title") : zzg(context, i);
        return zzu == null ? context.getResources().getString(C0598R.string.common_google_play_services_notification_ticker) : zzu;
    }

    @NonNull
    public static String zzi(Context context, int i) {
        Resources resources = context.getResources();
        String zzaB = zzaB(context);
        switch (i) {
            case OnSubscribeConcatMap.BOUNDARY /*1*/:
                return resources.getString(C0598R.string.common_google_play_services_install_text, new Object[]{zzaB});
            case OnSubscribeConcatMap.END /*2*/:
                if (zzi.zzaJ(context)) {
                    return resources.getString(C0598R.string.common_google_play_services_wear_update_text);
                }
                return resources.getString(C0598R.string.common_google_play_services_update_text, new Object[]{zzaB});
            case ConnectionResult.SERVICE_DISABLED /*3*/:
                return resources.getString(C0598R.string.common_google_play_services_enable_text, new Object[]{zzaB});
            case ConnectionResult.INVALID_ACCOUNT /*5*/:
                return zzg(context, "common_google_play_services_invalid_account_text", zzaB);
            case ConnectionResult.NETWORK_ERROR /*7*/:
                return zzg(context, "common_google_play_services_network_error_text", zzaB);
            case ConnectionResult.SERVICE_INVALID /*9*/:
                return resources.getString(C0598R.string.common_google_play_services_unsupported_text, new Object[]{zzaB});
            case ConnectionResult.API_UNAVAILABLE /*16*/:
                return zzg(context, "common_google_play_services_api_unavailable_text", zzaB);
            case ConnectionResult.SIGN_IN_FAILED /*17*/:
                return zzg(context, "common_google_play_services_sign_in_failed_text", zzaB);
            case ConnectionResult.SERVICE_UPDATING /*18*/:
                return resources.getString(C0598R.string.common_google_play_services_updating_text, new Object[]{zzaB});
            case ConnectionResult.RESTRICTED_PROFILE /*20*/:
                return zzg(context, "common_google_play_services_restricted_profile_text", zzaB);
            default:
                return resources.getString(C0598R.string.common_google_play_services_unknown_issue, new Object[]{zzaB});
        }
    }

    @NonNull
    public static String zzj(Context context, int i) {
        return i == 6 ? zzg(context, "common_google_play_services_resolution_required_text", zzaB(context)) : zzi(context, i);
    }

    @NonNull
    public static String zzk(Context context, int i) {
        Resources resources = context.getResources();
        switch (i) {
            case OnSubscribeConcatMap.BOUNDARY /*1*/:
                return resources.getString(C0598R.string.common_google_play_services_install_button);
            case OnSubscribeConcatMap.END /*2*/:
                return resources.getString(C0598R.string.common_google_play_services_update_button);
            case ConnectionResult.SERVICE_DISABLED /*3*/:
                return resources.getString(C0598R.string.common_google_play_services_enable_button);
            default:
                return resources.getString(17039370);
        }
    }

    @Nullable
    private static String zzu(Context context, String str) {
        synchronized (zzaEg) {
            String str2 = (String) zzaEg.get(str);
            if (str2 != null) {
                return str2;
            }
            Resources remoteResource = GooglePlayServicesUtil.getRemoteResource(context);
            if (remoteResource == null) {
                return null;
            }
            int identifier = remoteResource.getIdentifier(str, "string", GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_PACKAGE);
            if (identifier == 0) {
                String str3 = "GoogleApiAvailability";
                String str4 = "Missing resource: ";
                str2 = String.valueOf(str);
                Log.w(str3, str2.length() != 0 ? str4.concat(str2) : new String(str4));
                return null;
            }
            Object string = remoteResource.getString(identifier);
            if (TextUtils.isEmpty(string)) {
                str3 = "GoogleApiAvailability";
                str4 = "Got empty resource: ";
                str2 = String.valueOf(str);
                Log.w(str3, str2.length() != 0 ? str4.concat(str2) : new String(str4));
                return null;
            }
            zzaEg.put(str, string);
            return string;
        }
    }
}
