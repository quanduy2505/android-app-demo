package com.facebook.login;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.facebook.AccessToken;
import com.facebook.AccessTokenSource;
import com.facebook.login.LoginClient.Request;
import com.facebook.login.LoginClient.Result;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.ScheduledThreadPoolExecutor;

class DeviceAuthMethodHandler extends LoginMethodHandler {
    public static final Creator<DeviceAuthMethodHandler> CREATOR;
    private static ScheduledThreadPoolExecutor backgroundExecutor;

    /* renamed from: com.facebook.login.DeviceAuthMethodHandler.1 */
    static class C04331 implements Creator {
        C04331() {
        }

        public DeviceAuthMethodHandler createFromParcel(Parcel source) {
            return new DeviceAuthMethodHandler(source);
        }

        public DeviceAuthMethodHandler[] newArray(int size) {
            return new DeviceAuthMethodHandler[size];
        }
    }

    DeviceAuthMethodHandler(LoginClient loginClient) {
        super(loginClient);
    }

    boolean tryAuthorize(Request request) {
        showDialog(request);
        return true;
    }

    private void showDialog(Request request) {
        DeviceAuthDialog dialog = new DeviceAuthDialog();
        dialog.show(this.loginClient.getActivity().getSupportFragmentManager(), "login_with_facebook");
        dialog.startLogin(request);
    }

    public void onCancel() {
        this.loginClient.completeAndValidate(Result.createCancelResult(this.loginClient.getPendingRequest(), "User canceled log in."));
    }

    public void onError(Exception ex) {
        this.loginClient.completeAndValidate(Result.createErrorResult(this.loginClient.getPendingRequest(), null, ex.getMessage()));
    }

    public void onSuccess(String accessToken, String applicationId, String userId, Collection<String> permissions, Collection<String> declinedPermissions, AccessTokenSource accessTokenSource, Date expirationTime, Date lastRefreshTime) {
        this.loginClient.completeAndValidate(Result.createTokenResult(this.loginClient.getPendingRequest(), new AccessToken(accessToken, applicationId, userId, permissions, declinedPermissions, accessTokenSource, expirationTime, lastRefreshTime)));
    }

    public static synchronized ScheduledThreadPoolExecutor getBackgroundExecutor() {
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
        synchronized (DeviceAuthMethodHandler.class) {
            if (backgroundExecutor == null) {
                backgroundExecutor = new ScheduledThreadPoolExecutor(1);
            }
            scheduledThreadPoolExecutor = backgroundExecutor;
        }
        return scheduledThreadPoolExecutor;
    }

    protected DeviceAuthMethodHandler(Parcel parcel) {
        super(parcel);
    }

    String getNameForLogging() {
        return "device_auth";
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    static {
        CREATOR = new C04331();
    }
}
