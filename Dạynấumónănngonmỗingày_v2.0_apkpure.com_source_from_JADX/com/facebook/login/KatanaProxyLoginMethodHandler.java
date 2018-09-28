package com.facebook.login;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.facebook.internal.NativeProtocol;
import com.facebook.internal.ServerProtocol;
import com.facebook.login.LoginClient.Request;

class KatanaProxyLoginMethodHandler extends NativeAppLoginMethodHandler {
    public static final Creator<KatanaProxyLoginMethodHandler> CREATOR;

    /* renamed from: com.facebook.login.KatanaProxyLoginMethodHandler.1 */
    static class C04361 implements Creator {
        C04361() {
        }

        public KatanaProxyLoginMethodHandler createFromParcel(Parcel source) {
            return new KatanaProxyLoginMethodHandler(source);
        }

        public KatanaProxyLoginMethodHandler[] newArray(int size) {
            return new KatanaProxyLoginMethodHandler[size];
        }
    }

    KatanaProxyLoginMethodHandler(LoginClient loginClient) {
        super(loginClient);
    }

    String getNameForLogging() {
        return "katana_proxy_auth";
    }

    boolean tryAuthorize(Request request) {
        String e2e = LoginClient.getE2E();
        Intent intent = NativeProtocol.createProxyAuthIntent(this.loginClient.getActivity(), request.getApplicationId(), request.getPermissions(), e2e, request.isRerequest(), request.hasPublishPermission(), request.getDefaultAudience(), getClientState(request.getAuthId()));
        addLoggingExtra(ServerProtocol.DIALOG_PARAM_E2E, e2e);
        return tryIntent(intent, LoginClient.getLoginRequestCode());
    }

    KatanaProxyLoginMethodHandler(Parcel source) {
        super(source);
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    static {
        CREATOR = new C04361();
    }
}
