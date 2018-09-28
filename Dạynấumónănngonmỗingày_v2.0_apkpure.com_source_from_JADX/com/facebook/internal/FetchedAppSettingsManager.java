package com.facebook.internal;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.appevents.internal.AutomaticAnalyticsLogger;
import com.facebook.appevents.internal.Constants;
import com.facebook.internal.FetchedAppSettings.DialogFeatureConfig;
import com.facebook.share.internal.ShareConstants;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import org.json.JSONArray;
import org.json.JSONObject;
import rx.android.BuildConfig;

public final class FetchedAppSettingsManager {
    private static final String APPLICATION_FIELDS = "fields";
    private static final String APP_SETTINGS_PREFS_KEY_FORMAT = "com.facebook.internal.APP_SETTINGS.%s";
    private static final String APP_SETTINGS_PREFS_STORE = "com.facebook.internal.preferences.APP_SETTINGS";
    private static final String APP_SETTING_ANDROID_SDK_ERROR_CATEGORIES = "android_sdk_error_categories";
    private static final String APP_SETTING_APP_EVENTS_FEATURE_BITMASK = "app_events_feature_bitmask";
    private static final String APP_SETTING_APP_EVENTS_SESSION_TIMEOUT = "app_events_session_timeout";
    private static final String APP_SETTING_CUSTOM_TABS_ENABLED = "gdpv4_chrome_custom_tabs_enabled";
    private static final String APP_SETTING_DIALOG_CONFIGS = "android_dialog_configs";
    private static final String[] APP_SETTING_FIELDS;
    private static final String APP_SETTING_NUX_CONTENT = "gdpv4_nux_content";
    private static final String APP_SETTING_NUX_ENABLED = "gdpv4_nux_enabled";
    private static final String APP_SETTING_SMART_LOGIN_OPTIONS = "seamless_login";
    private static final String APP_SETTING_SUPPORTS_IMPLICIT_SDK_LOGGING = "supports_implicit_sdk_logging";
    private static final int AUTOMATIC_LOGGING_ENABLED_BITMASK_FIELD = 8;
    private static final String SMART_LOGIN_BOOKMARK_ICON_URL = "smart_login_bookmark_icon_url";
    private static final String SMART_LOGIN_MENU_ICON_URL = "smart_login_menu_icon_url";
    private static Map<String, FetchedAppSettings> fetchedAppSettings;
    private static AtomicBoolean loadingSettings;

    /* renamed from: com.facebook.internal.FetchedAppSettingsManager.1 */
    static class C04101 implements Runnable {
        final /* synthetic */ String val$applicationId;
        final /* synthetic */ Context val$context;
        final /* synthetic */ String val$settingsKey;

        C04101(Context context, String str, String str2) {
            this.val$context = context;
            this.val$settingsKey = str;
            this.val$applicationId = str2;
        }

        public void run() {
            SharedPreferences sharedPrefs = this.val$context.getSharedPreferences(FetchedAppSettingsManager.APP_SETTINGS_PREFS_STORE, 0);
            String settingsJSONString = sharedPrefs.getString(this.val$settingsKey, null);
            if (!Utility.isNullOrEmpty(settingsJSONString)) {
                JSONObject settingsJSON = null;
                try {
                    settingsJSON = new JSONObject(settingsJSONString);
                } catch (Exception je) {
                    Utility.logd("FacebookSDK", je);
                }
                if (settingsJSON != null) {
                    FetchedAppSettingsManager.parseAppSettingsFromJSON(this.val$applicationId, settingsJSON);
                }
            }
            JSONObject resultJSON = FetchedAppSettingsManager.getAppSettingsQueryResponse(this.val$applicationId);
            if (resultJSON != null) {
                FetchedAppSettingsManager.parseAppSettingsFromJSON(this.val$applicationId, resultJSON);
                sharedPrefs.edit().putString(this.val$settingsKey, resultJSON.toString()).apply();
            }
            AutomaticAnalyticsLogger.logActivateAppEvent();
            FetchedAppSettingsManager.loadingSettings.set(false);
        }
    }

    static {
        APP_SETTING_FIELDS = new String[]{APP_SETTING_SUPPORTS_IMPLICIT_SDK_LOGGING, APP_SETTING_NUX_CONTENT, APP_SETTING_NUX_ENABLED, APP_SETTING_CUSTOM_TABS_ENABLED, APP_SETTING_DIALOG_CONFIGS, APP_SETTING_ANDROID_SDK_ERROR_CATEGORIES, APP_SETTING_APP_EVENTS_SESSION_TIMEOUT, APP_SETTING_APP_EVENTS_FEATURE_BITMASK, APP_SETTING_SMART_LOGIN_OPTIONS, SMART_LOGIN_BOOKMARK_ICON_URL, SMART_LOGIN_MENU_ICON_URL};
        fetchedAppSettings = new ConcurrentHashMap();
        loadingSettings = new AtomicBoolean(false);
    }

    public static void loadAppSettingsAsync() {
        Context context = FacebookSdk.getApplicationContext();
        String applicationId = FacebookSdk.getApplicationId();
        boolean canStartLoading = loadingSettings.compareAndSet(false, true);
        if (!Utility.isNullOrEmpty(applicationId) && !fetchedAppSettings.containsKey(applicationId) && canStartLoading) {
            FacebookSdk.getExecutor().execute(new C04101(context, String.format(APP_SETTINGS_PREFS_KEY_FORMAT, new Object[]{applicationId}), applicationId));
        }
    }

    public static FetchedAppSettings getAppSettingsWithoutQuery(String applicationId) {
        return applicationId != null ? (FetchedAppSettings) fetchedAppSettings.get(applicationId) : null;
    }

    public static FetchedAppSettings queryAppSettings(String applicationId, boolean forceRequery) {
        if (!forceRequery && fetchedAppSettings.containsKey(applicationId)) {
            return (FetchedAppSettings) fetchedAppSettings.get(applicationId);
        }
        JSONObject response = getAppSettingsQueryResponse(applicationId);
        if (response == null) {
            return null;
        }
        return parseAppSettingsFromJSON(applicationId, response);
    }

    private static FetchedAppSettings parseAppSettingsFromJSON(String applicationId, JSONObject settingsJSON) {
        FacebookRequestErrorClassification errorClassification;
        boolean automaticLoggingEnabled;
        JSONArray errorClassificationJSON = settingsJSON.optJSONArray(APP_SETTING_ANDROID_SDK_ERROR_CATEGORIES);
        if (errorClassificationJSON == null) {
            errorClassification = FacebookRequestErrorClassification.getDefaultErrorClassification();
        } else {
            errorClassification = FacebookRequestErrorClassification.createFromJSON(errorClassificationJSON);
        }
        if ((settingsJSON.optInt(APP_SETTING_APP_EVENTS_FEATURE_BITMASK, 0) & AUTOMATIC_LOGGING_ENABLED_BITMASK_FIELD) != 0) {
            automaticLoggingEnabled = true;
        } else {
            automaticLoggingEnabled = false;
        }
        FetchedAppSettings result = new FetchedAppSettings(settingsJSON.optBoolean(APP_SETTING_SUPPORTS_IMPLICIT_SDK_LOGGING, false), settingsJSON.optString(APP_SETTING_NUX_CONTENT, BuildConfig.VERSION_NAME), settingsJSON.optBoolean(APP_SETTING_NUX_ENABLED, false), settingsJSON.optBoolean(APP_SETTING_CUSTOM_TABS_ENABLED, false), settingsJSON.optInt(APP_SETTING_APP_EVENTS_SESSION_TIMEOUT, Constants.getDefaultAppEventsSessionTimeoutInSeconds()), SmartLoginOption.parseOptions(settingsJSON.optLong(APP_SETTING_SMART_LOGIN_OPTIONS)), parseDialogConfigurations(settingsJSON.optJSONObject(APP_SETTING_DIALOG_CONFIGS)), automaticLoggingEnabled, errorClassification, settingsJSON.optString(SMART_LOGIN_BOOKMARK_ICON_URL), settingsJSON.optString(SMART_LOGIN_MENU_ICON_URL));
        fetchedAppSettings.put(applicationId, result);
        return result;
    }

    private static JSONObject getAppSettingsQueryResponse(String applicationId) {
        Bundle appSettingsParams = new Bundle();
        appSettingsParams.putString(APPLICATION_FIELDS, TextUtils.join(",", APP_SETTING_FIELDS));
        GraphRequest request = GraphRequest.newGraphPathRequest(null, applicationId, null);
        request.setSkipClientToken(true);
        request.setParameters(appSettingsParams);
        return request.executeAndWait().getJSONObject();
    }

    private static Map<String, Map<String, DialogFeatureConfig>> parseDialogConfigurations(JSONObject dialogConfigResponse) {
        HashMap<String, Map<String, DialogFeatureConfig>> dialogConfigMap = new HashMap();
        if (dialogConfigResponse != null) {
            JSONArray dialogConfigData = dialogConfigResponse.optJSONArray(ShareConstants.WEB_DIALOG_PARAM_DATA);
            if (dialogConfigData != null) {
                for (int i = 0; i < dialogConfigData.length(); i++) {
                    DialogFeatureConfig dialogConfig = DialogFeatureConfig.parseDialogConfig(dialogConfigData.optJSONObject(i));
                    if (dialogConfig != null) {
                        String dialogName = dialogConfig.getDialogName();
                        Map<String, DialogFeatureConfig> featureMap = (Map) dialogConfigMap.get(dialogName);
                        if (featureMap == null) {
                            featureMap = new HashMap();
                            dialogConfigMap.put(dialogName, featureMap);
                        }
                        featureMap.put(dialogConfig.getFeatureName(), dialogConfig);
                    }
                }
            }
        }
        return dialogConfigMap;
    }
}
