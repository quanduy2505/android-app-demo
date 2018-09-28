package com.facebook.login;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.facebook.AccessTokenSource;
import com.facebook.CustomTabMainActivity;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.FacebookRequestError;
import com.facebook.FacebookSdk;
import com.facebook.FacebookServiceException;
import com.facebook.internal.AnalyticsEvents;
import com.facebook.internal.FetchedAppSettings;
import com.facebook.internal.FetchedAppSettingsManager;
import com.facebook.internal.NativeProtocol;
import com.facebook.internal.ServerProtocol;
import com.facebook.internal.Utility;
import com.facebook.internal.Validate;
import com.facebook.login.LoginClient.Request;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;

public class CustomTabLoginMethodHandler extends WebLoginMethodHandler {
    private static final int API_EC_DIALOG_CANCEL = 4201;
    private static final int CHALLENGE_LENGTH = 20;
    private static final String[] CHROME_PACKAGES;
    public static final Creator<CustomTabLoginMethodHandler> CREATOR;
    private static final String CUSTOM_TABS_SERVICE_ACTION = "android.support.customtabs.action.CustomTabsService";
    private static final int CUSTOM_TAB_REQUEST_CODE = 1;
    private String currentPackage;
    private String expectedChallenge;

    /* renamed from: com.facebook.login.CustomTabLoginMethodHandler.1 */
    static class C04271 implements Creator {
        C04271() {
        }

        public CustomTabLoginMethodHandler createFromParcel(Parcel source) {
            return new CustomTabLoginMethodHandler(source);
        }

        public CustomTabLoginMethodHandler[] newArray(int size) {
            return new CustomTabLoginMethodHandler[size];
        }
    }

    static {
        CHROME_PACKAGES = new String[]{"com.android.chrome", "com.chrome.beta", "com.chrome.dev"};
        CREATOR = new C04271();
    }

    CustomTabLoginMethodHandler(LoginClient loginClient) {
        super(loginClient);
        this.expectedChallenge = Utility.generateRandomString(CHALLENGE_LENGTH);
    }

    String getNameForLogging() {
        return "custom_tab";
    }

    AccessTokenSource getTokenSource() {
        return AccessTokenSource.CHROME_CUSTOM_TAB;
    }

    protected String getSSODevice() {
        return "chrome_custom_tab";
    }

    boolean tryAuthorize(Request request) {
        if (!isCustomTabsAllowed()) {
            return false;
        }
        Bundle parameters = addExtraParameters(getParameters(request), request);
        Intent intent = new Intent(this.loginClient.getActivity(), CustomTabMainActivity.class);
        intent.putExtra(CustomTabMainActivity.EXTRA_PARAMS, parameters);
        intent.putExtra(CustomTabMainActivity.EXTRA_CHROME_PACKAGE, getChromePackage());
        this.loginClient.getFragment().startActivityForResult(intent, CUSTOM_TAB_REQUEST_CODE);
        return true;
    }

    private boolean isCustomTabsAllowed() {
        return isCustomTabsEnabled() && getChromePackage() != null && Validate.hasCustomTabRedirectActivity(FacebookSdk.getApplicationContext());
    }

    private boolean isCustomTabsEnabled() {
        FetchedAppSettings settings = FetchedAppSettingsManager.getAppSettingsWithoutQuery(Utility.getMetadataApplicationId(this.loginClient.getActivity()));
        return settings != null && settings.getCustomTabsEnabled();
    }

    private String getChromePackage() {
        if (this.currentPackage != null) {
            return this.currentPackage;
        }
        Context context = this.loginClient.getActivity();
        List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentServices(new Intent(CUSTOM_TABS_SERVICE_ACTION), 0);
        if (resolveInfos != null) {
            Set<String> chromePackages = new HashSet(Arrays.asList(CHROME_PACKAGES));
            for (ResolveInfo resolveInfo : resolveInfos) {
                ServiceInfo serviceInfo = resolveInfo.serviceInfo;
                if (serviceInfo != null && chromePackages.contains(serviceInfo.packageName)) {
                    this.currentPackage = serviceInfo.packageName;
                    return this.currentPackage;
                }
            }
        }
        return null;
    }

    boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != CUSTOM_TAB_REQUEST_CODE) {
            return super.onActivityResult(requestCode, resultCode, data);
        }
        Request request = this.loginClient.getPendingRequest();
        if (resultCode == -1) {
            onCustomTabComplete(data.getStringExtra(CustomTabMainActivity.EXTRA_URL), request);
            return true;
        }
        super.onComplete(request, null, new FacebookOperationCanceledException());
        return false;
    }

    private void onCustomTabComplete(String url, Request request) {
        if (url != null && url.startsWith(CustomTabMainActivity.getRedirectUrl())) {
            Uri uri = Uri.parse(url);
            Bundle values = Utility.parseUrlQueryString(uri.getQuery());
            values.putAll(Utility.parseUrlQueryString(uri.getFragment()));
            if (validateChallengeParam(values)) {
                String error = values.getString(NativeProtocol.BRIDGE_ARG_ERROR_BUNDLE);
                if (error == null) {
                    error = values.getString(NativeProtocol.BRIDGE_ARG_ERROR_TYPE);
                }
                String errorMessage = values.getString("error_msg");
                if (errorMessage == null) {
                    errorMessage = values.getString(AnalyticsEvents.PARAMETER_SHARE_ERROR_MESSAGE);
                }
                if (errorMessage == null) {
                    errorMessage = values.getString(NativeProtocol.BRIDGE_ARG_ERROR_DESCRIPTION);
                }
                String errorCodeString = values.getString(NativeProtocol.BRIDGE_ARG_ERROR_CODE);
                int errorCode = -1;
                if (!Utility.isNullOrEmpty(errorCodeString)) {
                    try {
                        errorCode = Integer.parseInt(errorCodeString);
                    } catch (NumberFormatException e) {
                        errorCode = -1;
                    }
                }
                if (Utility.isNullOrEmpty(error) && Utility.isNullOrEmpty(errorMessage) && errorCode == -1) {
                    super.onComplete(request, values, null);
                    return;
                } else if (error != null && (error.equals("access_denied") || error.equals("OAuthAccessDeniedException"))) {
                    super.onComplete(request, null, new FacebookOperationCanceledException());
                    return;
                } else if (errorCode == API_EC_DIALOG_CANCEL) {
                    super.onComplete(request, null, new FacebookOperationCanceledException());
                    return;
                } else {
                    super.onComplete(request, null, new FacebookServiceException(new FacebookRequestError(errorCode, error, errorMessage), errorMessage));
                    return;
                }
            }
            super.onComplete(request, null, new FacebookException("Invalid state parameter"));
        }
    }

    protected void putChallengeParam(JSONObject param) throws JSONException {
        param.put("7_challenge", this.expectedChallenge);
    }

    private boolean validateChallengeParam(Bundle values) {
        boolean z = false;
        try {
            String stateString = values.getString(ServerProtocol.DIALOG_PARAM_STATE);
            if (stateString != null) {
                z = new JSONObject(stateString).getString("7_challenge").equals(this.expectedChallenge);
            }
        } catch (JSONException e) {
        }
        return z;
    }

    public int describeContents() {
        return 0;
    }

    CustomTabLoginMethodHandler(Parcel source) {
        super(source);
        this.expectedChallenge = source.readString();
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.expectedChallenge);
    }
}
