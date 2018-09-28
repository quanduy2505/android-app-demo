package com.facebook.devicerequests.internal;

import android.annotation.TargetApi;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdManager.RegistrationListener;
import android.net.nsd.NsdServiceInfo;
import android.os.Build;
import android.os.Build.VERSION;
import com.facebook.FacebookSdk;
import com.facebook.internal.FetchedAppSettingsManager;
import com.facebook.internal.SmartLoginOption;
import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;

public class DeviceRequestsHelper {
    static final String DEVICE_INFO_DEVICE = "device";
    static final String DEVICE_INFO_MODEL = "model";
    public static final String DEVICE_INFO_PARAM = "device_info";
    static final String SDK_FLAVOR = "android";
    static final String SDK_HEADER = "fbsdk";
    static final String SERVICE_TYPE = "_fb._tcp.";
    private static HashMap<String, RegistrationListener> deviceRequestsListeners;

    /* renamed from: com.facebook.devicerequests.internal.DeviceRequestsHelper.1 */
    static class C04031 implements RegistrationListener {
        final /* synthetic */ String val$nsdServiceName;
        final /* synthetic */ String val$userCode;

        C04031(String str, String str2) {
            this.val$nsdServiceName = str;
            this.val$userCode = str2;
        }

        public void onServiceRegistered(NsdServiceInfo NsdServiceInfo) {
            if (!this.val$nsdServiceName.equals(NsdServiceInfo.getServiceName())) {
                DeviceRequestsHelper.cleanUpAdvertisementService(this.val$userCode);
            }
        }

        public void onServiceUnregistered(NsdServiceInfo serviceInfo) {
        }

        public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
            DeviceRequestsHelper.cleanUpAdvertisementService(this.val$userCode);
        }

        public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
        }
    }

    static {
        deviceRequestsListeners = new HashMap();
    }

    public static String getDeviceInfo() {
        JSONObject deviceInfo = new JSONObject();
        try {
            deviceInfo.put(DEVICE_INFO_DEVICE, Build.DEVICE);
            deviceInfo.put(DEVICE_INFO_MODEL, Build.MODEL);
        } catch (JSONException e) {
        }
        return deviceInfo.toString();
    }

    public static boolean startAdvertisementService(String userCode) {
        if (isAvailable()) {
            return startAdvertisementServiceImpl(userCode);
        }
        return false;
    }

    public static boolean isAvailable() {
        return VERSION.SDK_INT >= 16 && FetchedAppSettingsManager.getAppSettingsWithoutQuery(FacebookSdk.getApplicationId()).getSmartLoginOptions().contains(SmartLoginOption.Enabled);
    }

    public static void cleanUpAdvertisementService(String userCode) {
        cleanUpAdvertisementServiceImpl(userCode);
    }

    @TargetApi(16)
    private static boolean startAdvertisementServiceImpl(String userCode) {
        if (!deviceRequestsListeners.containsKey(userCode)) {
            String sdkVersion = FacebookSdk.getSdkVersion().replace('.', '|');
            r6 = new Object[3];
            r6[1] = String.format("%s-%s", new Object[]{SDK_FLAVOR, sdkVersion});
            r6[2] = userCode;
            String nsdServiceName = String.format("%s_%s_%s", r6);
            NsdServiceInfo nsdLoginAdvertisementService = new NsdServiceInfo();
            nsdLoginAdvertisementService.setServiceType(SERVICE_TYPE);
            nsdLoginAdvertisementService.setServiceName(nsdServiceName);
            nsdLoginAdvertisementService.setPort(80);
            NsdManager nsdManager = (NsdManager) FacebookSdk.getApplicationContext().getSystemService("servicediscovery");
            RegistrationListener nsdRegistrationListener = new C04031(nsdServiceName, userCode);
            deviceRequestsListeners.put(userCode, nsdRegistrationListener);
            nsdManager.registerService(nsdLoginAdvertisementService, 1, nsdRegistrationListener);
        }
        return true;
    }

    @TargetApi(16)
    private static void cleanUpAdvertisementServiceImpl(String userCode) {
        RegistrationListener nsdRegistrationListener = (RegistrationListener) deviceRequestsListeners.get(userCode);
        if (nsdRegistrationListener != null) {
            ((NsdManager) FacebookSdk.getApplicationContext().getSystemService("servicediscovery")).unregisterService(nsdRegistrationListener);
            deviceRequestsListeners.remove(userCode);
        }
    }
}
