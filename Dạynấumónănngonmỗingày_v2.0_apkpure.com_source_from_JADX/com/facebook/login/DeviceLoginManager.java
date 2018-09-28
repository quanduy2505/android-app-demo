package com.facebook.login;

import android.net.Uri;
import com.facebook.login.LoginClient.Request;
import java.util.Collection;

public class DeviceLoginManager extends LoginManager {
    private static volatile DeviceLoginManager instance;
    private Uri deviceRedirectUri;

    public static DeviceLoginManager getInstance() {
        if (instance == null) {
            synchronized (DeviceLoginManager.class) {
                if (instance == null) {
                    instance = new DeviceLoginManager();
                }
            }
        }
        return instance;
    }

    public void setDeviceRedirectUri(Uri uri) {
        this.deviceRedirectUri = uri;
    }

    public Uri getDeviceRedirectUri() {
        return this.deviceRedirectUri;
    }

    protected Request createLoginRequest(Collection<String> permissions) {
        Request request = super.createLoginRequest(permissions);
        Uri redirectUri = getDeviceRedirectUri();
        if (redirectUri != null) {
            request.setDeviceRedirectUriString(redirectUri.toString());
        }
        return request;
    }
}
