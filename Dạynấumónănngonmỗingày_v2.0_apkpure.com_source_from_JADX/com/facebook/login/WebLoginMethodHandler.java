package com.facebook.login;

import android.os.Bundle;
import android.os.Parcel;
import android.text.TextUtils;
import android.webkit.CookieSyncManager;
import com.facebook.AccessToken;
import com.facebook.AccessTokenSource;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.FacebookSdk;
import com.facebook.FacebookServiceException;
import com.facebook.appevents.AppEventsConstants;
import com.facebook.internal.ServerProtocol;
import com.facebook.internal.Utility;
import com.facebook.login.LoginClient.Request;
import com.facebook.login.LoginClient.Result;
import java.util.Locale;
import rx.android.BuildConfig;

abstract class WebLoginMethodHandler extends LoginMethodHandler {
    private static final String WEB_VIEW_AUTH_HANDLER_STORE = "com.facebook.login.AuthorizationClient.WebViewAuthHandler.TOKEN_STORE_KEY";
    private static final String WEB_VIEW_AUTH_HANDLER_TOKEN_KEY = "TOKEN";
    private String e2e;

    abstract AccessTokenSource getTokenSource();

    private static final String getRedirectUri() {
        return "fb" + FacebookSdk.getApplicationId() + "://authorize";
    }

    WebLoginMethodHandler(LoginClient loginClient) {
        super(loginClient);
    }

    WebLoginMethodHandler(Parcel source) {
        super(source);
    }

    protected String getSSODevice() {
        return null;
    }

    protected Bundle getParameters(Request request) {
        Bundle parameters = new Bundle();
        if (!Utility.isNullOrEmpty(request.getPermissions())) {
            String scope = TextUtils.join(",", request.getPermissions());
            parameters.putString(ServerProtocol.DIALOG_PARAM_SCOPE, scope);
            addLoggingExtra(ServerProtocol.DIALOG_PARAM_SCOPE, scope);
        }
        parameters.putString(ServerProtocol.DIALOG_PARAM_DEFAULT_AUDIENCE, request.getDefaultAudience().getNativeProtocolAudience());
        parameters.putString(ServerProtocol.DIALOG_PARAM_STATE, getClientState(request.getAuthId()));
        AccessToken previousToken = AccessToken.getCurrentAccessToken();
        String previousTokenString = previousToken != null ? previousToken.getToken() : null;
        if (previousTokenString == null || !previousTokenString.equals(loadCookieToken())) {
            Utility.clearFacebookCookies(this.loginClient.getActivity());
            addLoggingExtra(ServerProtocol.DIALOG_PARAM_ACCESS_TOKEN, AppEventsConstants.EVENT_PARAM_VALUE_NO);
        } else {
            parameters.putString(ServerProtocol.DIALOG_PARAM_ACCESS_TOKEN, previousTokenString);
            addLoggingExtra(ServerProtocol.DIALOG_PARAM_ACCESS_TOKEN, AppEventsConstants.EVENT_PARAM_VALUE_YES);
        }
        return parameters;
    }

    protected Bundle addExtraParameters(Bundle parameters, Request request) {
        parameters.putString(ServerProtocol.DIALOG_PARAM_REDIRECT_URI, getRedirectUri());
        parameters.putString(ServerProtocol.DIALOG_PARAM_CLIENT_ID, request.getApplicationId());
        String str = ServerProtocol.DIALOG_PARAM_E2E;
        LoginClient loginClient = this.loginClient;
        parameters.putString(str, LoginClient.getE2E());
        parameters.putString(ServerProtocol.DIALOG_PARAM_RESPONSE_TYPE, ServerProtocol.DIALOG_RESPONSE_TYPE_TOKEN_AND_SIGNED_REQUEST);
        parameters.putString(ServerProtocol.DIALOG_PARAM_RETURN_SCOPES, ServerProtocol.DIALOG_RETURN_SCOPES_TRUE);
        parameters.putString(ServerProtocol.DIALOG_PARAM_AUTH_TYPE, ServerProtocol.DIALOG_REREQUEST_AUTH_TYPE);
        if (getSSODevice() != null) {
            parameters.putString(ServerProtocol.DIALOG_PARAM_SSO_DEVICE, getSSODevice());
        }
        return parameters;
    }

    protected void onComplete(Request request, Bundle values, FacebookException error) {
        Result outcome;
        this.e2e = null;
        if (values != null) {
            if (values.containsKey(ServerProtocol.DIALOG_PARAM_E2E)) {
                this.e2e = values.getString(ServerProtocol.DIALOG_PARAM_E2E);
            }
            try {
                AccessToken token = LoginMethodHandler.createAccessTokenFromWebBundle(request.getPermissions(), values, getTokenSource(), request.getApplicationId());
                outcome = Result.createTokenResult(this.loginClient.getPendingRequest(), token);
                CookieSyncManager.createInstance(this.loginClient.getActivity()).sync();
                saveCookieToken(token.getToken());
            } catch (FacebookException ex) {
                outcome = Result.createErrorResult(this.loginClient.getPendingRequest(), null, ex.getMessage());
            }
        } else if (error instanceof FacebookOperationCanceledException) {
            outcome = Result.createCancelResult(this.loginClient.getPendingRequest(), "User canceled log in.");
        } else {
            this.e2e = null;
            String errorCode = null;
            String errorMessage = error.getMessage();
            if (error instanceof FacebookServiceException) {
                errorCode = String.format(Locale.ROOT, "%d", new Object[]{Integer.valueOf(((FacebookServiceException) error).getRequestError().getErrorCode())});
                errorMessage = requestError.toString();
            }
            outcome = Result.createErrorResult(this.loginClient.getPendingRequest(), null, errorMessage, errorCode);
        }
        if (!Utility.isNullOrEmpty(this.e2e)) {
            logWebLoginCompleted(this.e2e);
        }
        this.loginClient.completeAndValidate(outcome);
    }

    private String loadCookieToken() {
        return this.loginClient.getActivity().getSharedPreferences(WEB_VIEW_AUTH_HANDLER_STORE, 0).getString(WEB_VIEW_AUTH_HANDLER_TOKEN_KEY, BuildConfig.VERSION_NAME);
    }

    private void saveCookieToken(String token) {
        this.loginClient.getActivity().getSharedPreferences(WEB_VIEW_AUTH_HANDLER_STORE, 0).edit().putString(WEB_VIEW_AUTH_HANDLER_TOKEN_KEY, token).apply();
    }
}
