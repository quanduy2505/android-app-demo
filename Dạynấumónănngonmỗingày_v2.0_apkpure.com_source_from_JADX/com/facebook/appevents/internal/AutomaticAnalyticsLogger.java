package com.facebook.appevents.internal;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.internal.FetchedAppSettings;
import com.facebook.internal.FetchedAppSettingsManager;
import com.facebook.internal.Validate;

public class AutomaticAnalyticsLogger {
    private static final String TAG;

    static {
        TAG = AutomaticAnalyticsLogger.class.getCanonicalName();
    }

    public static void logActivateAppEvent() {
        Context context = FacebookSdk.getApplicationContext();
        String appId = FacebookSdk.getApplicationId();
        boolean autoLogAppEvents = FacebookSdk.getAutoLogAppEventsEnabled();
        Validate.notNull(context, "context");
        if (!autoLogAppEvents) {
            return;
        }
        if (context instanceof Application) {
            AppEventsLogger.activateApp((Application) context, appId);
        } else {
            Log.w(TAG, "Automatic logging of basic events will not happen, because FacebookSdk.getApplicationContext() returns object that is not instance of android.app.Application. Make sure you call FacebookSdk.sdkInitialize() from Application class and pass application context.");
        }
    }

    public static void logActivityTimeSpentEvent(String activityName, long timeSpentInSeconds) {
        Context context = FacebookSdk.getApplicationContext();
        String appId = FacebookSdk.getApplicationId();
        Validate.notNull(context, "context");
        FetchedAppSettings settings = FetchedAppSettingsManager.queryAppSettings(appId, false);
        if (settings != null && settings.getAutomaticLoggingEnabled() && timeSpentInSeconds > 0) {
            AppEventsLogger appEventsLogger = AppEventsLogger.newLogger(context);
            Bundle params = new Bundle(1);
            params.putCharSequence(Constants.AA_TIME_SPENT_SCREEN_PARAMETER_NAME, activityName);
            appEventsLogger.logEvent(Constants.AA_TIME_SPENT_EVENT_NAME, (double) timeSpentInSeconds, params);
        }
    }
}
