package com.facebook.login;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import com.facebook.AccessTokenSource;
import com.facebook.FacebookException;
import com.facebook.internal.AnalyticsEvents;
import com.facebook.internal.NativeProtocol;
import com.facebook.internal.ServerProtocol;
import com.facebook.internal.Utility;
import com.facebook.login.LoginClient.Request;
import com.facebook.login.LoginClient.Result;

abstract class NativeAppLoginMethodHandler extends LoginMethodHandler {
    abstract boolean tryAuthorize(Request request);

    NativeAppLoginMethodHandler(LoginClient loginClient) {
        super(loginClient);
    }

    NativeAppLoginMethodHandler(Parcel source) {
        super(source);
    }

    boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        Result outcome;
        Request request = this.loginClient.getPendingRequest();
        if (data == null) {
            outcome = Result.createCancelResult(request, "Operation canceled");
        } else if (resultCode == 0) {
            outcome = handleResultCancel(request, data);
        } else if (resultCode != -1) {
            outcome = Result.createErrorResult(request, "Unexpected resultCode from authorization.", null);
        } else {
            outcome = handleResultOk(request, data);
        }
        if (outcome != null) {
            this.loginClient.completeAndValidate(outcome);
        } else {
            this.loginClient.tryNextHandler();
        }
        return true;
    }

    private Result handleResultOk(Request request, Intent data) {
        Result result = null;
        Bundle extras = data.getExtras();
        String error = getError(extras);
        String errorCode = extras.getString(NativeProtocol.BRIDGE_ARG_ERROR_CODE);
        String errorMessage = getErrorMessage(extras);
        String e2e = extras.getString(ServerProtocol.DIALOG_PARAM_E2E);
        if (!Utility.isNullOrEmpty(e2e)) {
            logWebLoginCompleted(e2e);
        }
        if (error == null && errorCode == null && errorMessage == null) {
            try {
                return Result.createTokenResult(request, LoginMethodHandler.createAccessTokenFromWebBundle(request.getPermissions(), extras, AccessTokenSource.FACEBOOK_APPLICATION_WEB, request.getApplicationId()));
            } catch (FacebookException ex) {
                return Result.createErrorResult(request, result, ex.getMessage());
            }
        } else if (ServerProtocol.errorsProxyAuthDisabled.contains(error)) {
            return result;
        } else {
            if (ServerProtocol.errorsUserCanceled.contains(error)) {
                return Result.createCancelResult(request, result);
            }
            return Result.createErrorResult(request, error, errorMessage, errorCode);
        }
    }

    private Result handleResultCancel(Request request, Intent data) {
        Bundle extras = data.getExtras();
        String error = getError(extras);
        String errorCode = extras.getString(NativeProtocol.BRIDGE_ARG_ERROR_CODE);
        if (ServerProtocol.errorConnectionFailure.equals(errorCode)) {
            return Result.createErrorResult(request, error, getErrorMessage(extras), errorCode);
        }
        return Result.createCancelResult(request, error);
    }

    private String getError(Bundle extras) {
        String error = extras.getString(NativeProtocol.BRIDGE_ARG_ERROR_BUNDLE);
        if (error == null) {
            return extras.getString(NativeProtocol.BRIDGE_ARG_ERROR_TYPE);
        }
        return error;
    }

    private String getErrorMessage(Bundle extras) {
        String errorMessage = extras.getString(AnalyticsEvents.PARAMETER_SHARE_ERROR_MESSAGE);
        if (errorMessage == null) {
            return extras.getString(NativeProtocol.BRIDGE_ARG_ERROR_DESCRIPTION);
        }
        return errorMessage;
    }

    protected boolean tryIntent(Intent intent, int requestCode) {
        if (intent == null) {
            return false;
        }
        try {
            this.loginClient.getFragment().startActivityForResult(intent, requestCode);
            return true;
        } catch (ActivityNotFoundException e) {
            return false;
        }
    }
}
