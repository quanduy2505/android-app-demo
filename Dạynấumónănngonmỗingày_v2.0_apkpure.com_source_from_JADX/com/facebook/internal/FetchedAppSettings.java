package com.facebook.internal;

import android.net.Uri;
import java.util.EnumSet;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

public final class FetchedAppSettings {
    private boolean automaticLoggingEnabled;
    private boolean customTabsEnabled;
    private Map<String, Map<String, DialogFeatureConfig>> dialogConfigMap;
    private FacebookRequestErrorClassification errorClassification;
    private String nuxContent;
    private boolean nuxEnabled;
    private int sessionTimeoutInSeconds;
    private String smartLoginBookmarkIconURL;
    private String smartLoginMenuIconURL;
    private EnumSet<SmartLoginOption> smartLoginOptions;
    private boolean supportsImplicitLogging;

    public static class DialogFeatureConfig {
        private static final String DIALOG_CONFIG_DIALOG_NAME_FEATURE_NAME_SEPARATOR = "\\|";
        private static final String DIALOG_CONFIG_NAME_KEY = "name";
        private static final String DIALOG_CONFIG_URL_KEY = "url";
        private static final String DIALOG_CONFIG_VERSIONS_KEY = "versions";
        private String dialogName;
        private Uri fallbackUrl;
        private String featureName;
        private int[] featureVersionSpec;

        public static DialogFeatureConfig parseDialogConfig(JSONObject dialogConfigJSON) {
            String dialogNameWithFeature = dialogConfigJSON.optString(DIALOG_CONFIG_NAME_KEY);
            if (Utility.isNullOrEmpty(dialogNameWithFeature)) {
                return null;
            }
            String[] components = dialogNameWithFeature.split(DIALOG_CONFIG_DIALOG_NAME_FEATURE_NAME_SEPARATOR);
            if (components.length != 2) {
                return null;
            }
            String dialogName = components[0];
            String featureName = components[1];
            if (Utility.isNullOrEmpty(dialogName) || Utility.isNullOrEmpty(featureName)) {
                return null;
            }
            String urlString = dialogConfigJSON.optString(DIALOG_CONFIG_URL_KEY);
            Uri fallbackUri = null;
            if (!Utility.isNullOrEmpty(urlString)) {
                fallbackUri = Uri.parse(urlString);
            }
            return new DialogFeatureConfig(dialogName, featureName, fallbackUri, parseVersionSpec(dialogConfigJSON.optJSONArray(DIALOG_CONFIG_VERSIONS_KEY)));
        }

        private static int[] parseVersionSpec(JSONArray versionsJSON) {
            int[] versionSpec = null;
            if (versionsJSON != null) {
                int numVersions = versionsJSON.length();
                versionSpec = new int[numVersions];
                for (int i = 0; i < numVersions; i++) {
                    int version = versionsJSON.optInt(i, -1);
                    if (version == -1) {
                        String versionString = versionsJSON.optString(i);
                        if (!Utility.isNullOrEmpty(versionString)) {
                            try {
                                version = Integer.parseInt(versionString);
                            } catch (Exception nfe) {
                                Utility.logd("FacebookSDK", nfe);
                                version = -1;
                            }
                        }
                    }
                    versionSpec[i] = version;
                }
            }
            return versionSpec;
        }

        private DialogFeatureConfig(String dialogName, String featureName, Uri fallbackUrl, int[] featureVersionSpec) {
            this.dialogName = dialogName;
            this.featureName = featureName;
            this.fallbackUrl = fallbackUrl;
            this.featureVersionSpec = featureVersionSpec;
        }

        public String getDialogName() {
            return this.dialogName;
        }

        public String getFeatureName() {
            return this.featureName;
        }

        public Uri getFallbackUrl() {
            return this.fallbackUrl;
        }

        public int[] getVersionSpec() {
            return this.featureVersionSpec;
        }
    }

    public FetchedAppSettings(boolean supportsImplicitLogging, String nuxContent, boolean nuxEnabled, boolean customTabsEnabled, int sessionTimeoutInSeconds, EnumSet<SmartLoginOption> smartLoginOptions, Map<String, Map<String, DialogFeatureConfig>> dialogConfigMap, boolean automaticLoggingEnabled, FacebookRequestErrorClassification errorClassification, String smartLoginBookmarkIconURL, String smartLoginMenuIconURL) {
        this.supportsImplicitLogging = supportsImplicitLogging;
        this.nuxContent = nuxContent;
        this.nuxEnabled = nuxEnabled;
        this.customTabsEnabled = customTabsEnabled;
        this.dialogConfigMap = dialogConfigMap;
        this.errorClassification = errorClassification;
        this.sessionTimeoutInSeconds = sessionTimeoutInSeconds;
        this.automaticLoggingEnabled = automaticLoggingEnabled;
        this.smartLoginOptions = smartLoginOptions;
        this.smartLoginBookmarkIconURL = smartLoginBookmarkIconURL;
        this.smartLoginMenuIconURL = smartLoginMenuIconURL;
    }

    public boolean supportsImplicitLogging() {
        return this.supportsImplicitLogging;
    }

    public String getNuxContent() {
        return this.nuxContent;
    }

    public boolean getNuxEnabled() {
        return this.nuxEnabled;
    }

    public boolean getCustomTabsEnabled() {
        return this.customTabsEnabled;
    }

    public int getSessionTimeoutInSeconds() {
        return this.sessionTimeoutInSeconds;
    }

    public boolean getAutomaticLoggingEnabled() {
        return this.automaticLoggingEnabled;
    }

    public EnumSet<SmartLoginOption> getSmartLoginOptions() {
        return this.smartLoginOptions;
    }

    public Map<String, Map<String, DialogFeatureConfig>> getDialogConfigurations() {
        return this.dialogConfigMap;
    }

    public FacebookRequestErrorClassification getErrorClassification() {
        return this.errorClassification;
    }

    public String getSmartLoginBookmarkIconURL() {
        return this.smartLoginBookmarkIconURL;
    }

    public String getSmartLoginMenuIconURL() {
        return this.smartLoginMenuIconURL;
    }

    public static DialogFeatureConfig getDialogFeatureConfig(String applicationId, String actionName, String featureName) {
        if (Utility.isNullOrEmpty(actionName) || Utility.isNullOrEmpty(featureName)) {
            return null;
        }
        FetchedAppSettings settings = FetchedAppSettingsManager.getAppSettingsWithoutQuery(applicationId);
        if (settings == null) {
            return null;
        }
        Map<String, DialogFeatureConfig> featureMap = (Map) settings.getDialogConfigurations().get(actionName);
        if (featureMap != null) {
            return (DialogFeatureConfig) featureMap.get(featureName);
        }
        return null;
    }
}
