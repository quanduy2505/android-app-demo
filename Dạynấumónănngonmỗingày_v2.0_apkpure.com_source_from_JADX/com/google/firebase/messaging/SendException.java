package com.google.firebase.messaging;

import java.util.Locale;
import rx.internal.schedulers.NewThreadWorker;

public final class SendException extends Exception {
    public static final int ERROR_INVALID_PARAMETERS = 1;
    public static final int ERROR_SIZE = 2;
    public static final int ERROR_TOO_MANY_MESSAGES = 4;
    public static final int ERROR_TTL_EXCEEDED = 3;
    public static final int ERROR_UNKNOWN = 0;
    private final int zzPF;

    SendException(String str) {
        super(str);
        this.zzPF = zzjF(str);
    }

    private int zzjF(String str) {
        if (str == null) {
            return 0;
        }
        String toLowerCase = str.toLowerCase(Locale.US);
        int i = -1;
        switch (toLowerCase.hashCode()) {
            case -1743242157:
                if (toLowerCase.equals("service_not_available")) {
                    i = ERROR_TTL_EXCEEDED;
                    break;
                }
                break;
            case -1290953729:
                if (toLowerCase.equals("toomanymessages")) {
                    i = ERROR_TOO_MANY_MESSAGES;
                    break;
                }
                break;
            case -920906446:
                if (toLowerCase.equals("invalid_parameters")) {
                    i = 0;
                    break;
                }
                break;
            case -617027085:
                if (toLowerCase.equals("messagetoobig")) {
                    i = ERROR_SIZE;
                    break;
                }
                break;
            case -95047692:
                if (toLowerCase.equals("missing_to")) {
                    i = ERROR_INVALID_PARAMETERS;
                    break;
                }
                break;
        }
        switch (i) {
            case NewThreadWorker.PURGE_FREQUENCY /*0*/:
            case ERROR_INVALID_PARAMETERS /*1*/:
                return ERROR_INVALID_PARAMETERS;
            case ERROR_SIZE /*2*/:
                return ERROR_SIZE;
            case ERROR_TTL_EXCEEDED /*3*/:
                return ERROR_TTL_EXCEEDED;
            case ERROR_TOO_MANY_MESSAGES /*4*/:
                return ERROR_TOO_MANY_MESSAGES;
            default:
                return 0;
        }
    }

    public int getErrorCode() {
        return this.zzPF;
    }
}
