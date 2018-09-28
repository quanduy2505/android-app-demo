package com.google.firebase.storage;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzac;
import com.google.firebase.FirebaseException;
import devlight.io.library.ntb.NavigationTabBar;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.apache.http.HttpStatus;

public class StorageException extends FirebaseException {
    static final /* synthetic */ boolean $assertionsDisabled;
    public static final int ERROR_BUCKET_NOT_FOUND = -13011;
    public static final int ERROR_CANCELED = -13040;
    public static final int ERROR_INVALID_CHECKSUM = -13031;
    public static final int ERROR_NOT_AUTHENTICATED = -13020;
    public static final int ERROR_NOT_AUTHORIZED = -13021;
    public static final int ERROR_OBJECT_NOT_FOUND = -13010;
    public static final int ERROR_PROJECT_NOT_FOUND = -13012;
    public static final int ERROR_QUOTA_EXCEEDED = -13013;
    public static final int ERROR_RETRY_LIMIT_EXCEEDED = -13030;
    public static final int ERROR_UNKNOWN = -13000;
    static IOException zzckv;
    private final int zzPF;
    private final int zzckw;
    private String zzckx;
    private Throwable zzcky;

    @Retention(RetentionPolicy.SOURCE)
    public @interface ErrorCode {
    }

    static {
        $assertionsDisabled = !StorageException.class.desiredAssertionStatus() ? true : $assertionsDisabled;
        zzckv = new IOException("The operation was canceled.");
    }

    StorageException(int i, Throwable th, int i2) {
        this.zzckx = zzpY(i);
        this.zzcky = th;
        this.zzPF = i;
        this.zzckw = i2;
        String str = this.zzckx;
        String valueOf = String.valueOf(Integer.toString(this.zzPF));
        String valueOf2 = String.valueOf(Integer.toString(this.zzckw));
        Log.e("StorageException", new StringBuilder(((String.valueOf(str).length() + 52) + String.valueOf(valueOf).length()) + String.valueOf(valueOf2).length()).append("StorageException has occurred.\n").append(str).append("\n Code: ").append(valueOf).append(" HttpResult: ").append(valueOf2).toString());
        if (this.zzcky != null) {
            Log.e("StorageException", this.zzcky.getMessage(), this.zzcky);
        }
    }

    StorageException(Status status) {
        this(zzcd(status), null, 0);
    }

    StorageException(@Nullable Throwable th, int i) {
        this(zza(th, i), th, i);
    }

    @NonNull
    public static StorageException fromErrorStatus(@NonNull Status status) {
        zzac.zzw(status);
        zzac.zzas(!status.isSuccess() ? true : $assertionsDisabled);
        return new StorageException(status);
    }

    @NonNull
    public static StorageException fromException(@NonNull Throwable th) {
        StorageException fromExceptionAndHttpCode = fromExceptionAndHttpCode(th, 0);
        if ($assertionsDisabled || fromExceptionAndHttpCode != null) {
            return fromExceptionAndHttpCode;
        }
        throw new AssertionError();
    }

    @Nullable
    public static StorageException fromExceptionAndHttpCode(@Nullable Throwable th, int i) {
        return th instanceof StorageException ? (StorageException) th : (zzpX(i) && th == null) ? null : new StorageException(th, i);
    }

    private static int zza(@Nullable Throwable th, int i) {
        if (th == zzckv) {
            return ERROR_CANCELED;
        }
        switch (i) {
            case NavigationTabBar.AUTO_SIZE /*-2*/:
                return ERROR_RETRY_LIMIT_EXCEEDED;
            case HttpStatus.SC_UNAUTHORIZED /*401*/:
                return ERROR_NOT_AUTHENTICATED;
            case HttpStatus.SC_FORBIDDEN /*403*/:
                return ERROR_NOT_AUTHORIZED;
            case HttpStatus.SC_NOT_FOUND /*404*/:
                return ERROR_OBJECT_NOT_FOUND;
            default:
                return ERROR_UNKNOWN;
        }
    }

    private static int zzcd(Status status) {
        return status.isCanceled() ? ERROR_CANCELED : status == Status.zzayk ? ERROR_RETRY_LIMIT_EXCEEDED : ERROR_UNKNOWN;
    }

    private static boolean zzpX(int i) {
        return (i == 0 || (i >= HttpStatus.SC_OK && i < HttpStatus.SC_MULTIPLE_CHOICES)) ? true : $assertionsDisabled;
    }

    static String zzpY(int i) {
        switch (i) {
            case ERROR_CANCELED /*-13040*/:
                return "The operation was cancelled.";
            case ERROR_INVALID_CHECKSUM /*-13031*/:
                return "Object has a checksum which does not match. Please retry the operation.";
            case ERROR_RETRY_LIMIT_EXCEEDED /*-13030*/:
                return "The operation retry limit has been exceeded.";
            case ERROR_NOT_AUTHORIZED /*-13021*/:
                return "User does not have permission to access this object.";
            case ERROR_NOT_AUTHENTICATED /*-13020*/:
                return "User is not authenticated, please authenticate using Firebase Authentication and try again.";
            case ERROR_QUOTA_EXCEEDED /*-13013*/:
                return "Quota for bucket exceeded, please view quota on www.firebase.google.com/storage.";
            case ERROR_PROJECT_NOT_FOUND /*-13012*/:
                return "Project does not exist.";
            case ERROR_BUCKET_NOT_FOUND /*-13011*/:
                return "Bucket does not exist.";
            case ERROR_OBJECT_NOT_FOUND /*-13010*/:
                return "Object does not exist at location.";
            case ERROR_UNKNOWN /*-13000*/:
                return "An unknown error occurred, please check the HTTP result code and inner exception for server response.";
            default:
                return "An unknown error occurred, please check the HTTP result code and inner exception for server response.";
        }
    }

    public Throwable getCause() {
        return this.zzcky == this ? null : this.zzcky;
    }

    public int getErrorCode() {
        return this.zzPF;
    }

    public int getHttpResultCode() {
        return this.zzckw;
    }

    public boolean getIsRecoverableException() {
        return getErrorCode() == ERROR_RETRY_LIMIT_EXCEEDED ? true : $assertionsDisabled;
    }

    public String getMessage() {
        return this.zzckx;
    }
}
