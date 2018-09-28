package com.facebook.internal;

import android.content.Context;
import com.facebook.LoggingBehavior;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

public class AppEventsLoggerUtility {
    private static final Map<GraphAPIActivityType, String> API_ACTIVITY_TYPE_TO_STRING;

    /* renamed from: com.facebook.internal.AppEventsLoggerUtility.1 */
    static class C04041 extends HashMap<GraphAPIActivityType, String> {
        C04041() {
            put(GraphAPIActivityType.MOBILE_INSTALL_EVENT, "MOBILE_APP_INSTALL");
            put(GraphAPIActivityType.CUSTOM_APP_EVENTS, "CUSTOM_APP_EVENTS");
        }
    }

    public enum GraphAPIActivityType {
        MOBILE_INSTALL_EVENT,
        CUSTOM_APP_EVENTS
    }

    static {
        API_ACTIVITY_TYPE_TO_STRING = new C04041();
    }

    public static JSONObject getJSONObjectForGraphAPICall(GraphAPIActivityType activityType, AttributionIdentifiers attributionIdentifiers, String anonymousAppDeviceGUID, boolean limitEventUsage, Context context) throws JSONException {
        JSONObject publishParams = new JSONObject();
        publishParams.put(NotificationCompatApi24.CATEGORY_EVENT, API_ACTIVITY_TYPE_TO_STRING.get(activityType));
        Utility.setAppEventAttributionParameters(publishParams, attributionIdentifiers, anonymousAppDeviceGUID, limitEventUsage);
        try {
            Utility.setAppEventExtendedDeviceInfoParameters(publishParams, context);
        } catch (Exception e) {
            Logger.log(LoggingBehavior.APP_EVENTS, "AppEvents", "Fetching extended device info parameters failed: '%s'", e.toString());
        }
        publishParams.put("application_package_name", context.getPackageName());
        return publishParams;
    }
}
