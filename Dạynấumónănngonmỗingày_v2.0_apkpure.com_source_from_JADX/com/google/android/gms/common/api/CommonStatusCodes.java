package com.google.android.gms.common.api;

import android.support.annotation.NonNull;
import com.google.android.gms.iid.InstanceID;

public class CommonStatusCodes {
    public static final int API_NOT_CONNECTED = 17;
    public static final int CANCELED = 16;
    public static final int DEAD_CLIENT = 18;
    public static final int DEVELOPER_ERROR = 10;
    public static final int ERROR = 13;
    public static final int INTERNAL_ERROR = 8;
    public static final int INTERRUPTED = 14;
    public static final int INVALID_ACCOUNT = 5;
    public static final int NETWORK_ERROR = 7;
    public static final int RESOLUTION_REQUIRED = 6;
    @Deprecated
    public static final int SERVICE_DISABLED = 3;
    @Deprecated
    public static final int SERVICE_VERSION_UPDATE_REQUIRED = 2;
    public static final int SIGN_IN_REQUIRED = 4;
    public static final int SUCCESS = 0;
    public static final int SUCCESS_CACHE = -1;
    public static final int TIMEOUT = 15;

    protected CommonStatusCodes() {
    }

    @NonNull
    public static String getStatusCodeString(int i) {
        switch (i) {
            case SUCCESS_CACHE /*-1*/:
                return "SUCCESS_CACHE";
            case SUCCESS /*0*/:
                return "SUCCESS";
            case SERVICE_VERSION_UPDATE_REQUIRED /*2*/:
                return "SERVICE_VERSION_UPDATE_REQUIRED";
            case SERVICE_DISABLED /*3*/:
                return "SERVICE_DISABLED";
            case SIGN_IN_REQUIRED /*4*/:
                return "SIGN_IN_REQUIRED";
            case INVALID_ACCOUNT /*5*/:
                return "INVALID_ACCOUNT";
            case RESOLUTION_REQUIRED /*6*/:
                return "RESOLUTION_REQUIRED";
            case NETWORK_ERROR /*7*/:
                return "NETWORK_ERROR";
            case INTERNAL_ERROR /*8*/:
                return "INTERNAL_ERROR";
            case DEVELOPER_ERROR /*10*/:
                return "DEVELOPER_ERROR";
            case ERROR /*13*/:
                return "ERROR";
            case INTERRUPTED /*14*/:
                return "INTERRUPTED";
            case TIMEOUT /*15*/:
                return InstanceID.ERROR_TIMEOUT;
            case CANCELED /*16*/:
                return "CANCELED";
            case API_NOT_CONNECTED /*17*/:
                return "API_NOT_CONNECTED";
            case DEAD_CLIENT /*18*/:
                return "DEAD_CLIENT";
            default:
                return "unknown status code: " + i;
        }
    }
}
