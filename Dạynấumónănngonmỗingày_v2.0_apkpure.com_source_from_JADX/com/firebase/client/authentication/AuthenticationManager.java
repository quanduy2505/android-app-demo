package com.firebase.client.authentication;

import com.facebook.internal.ServerProtocol;
import com.facebook.share.internal.ShareConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.firebase.client.AuthData;
import com.firebase.client.CredentialStore;
import com.firebase.client.Firebase;
import com.firebase.client.Firebase.AuthListener;
import com.firebase.client.Firebase.AuthResultHandler;
import com.firebase.client.Firebase.AuthStateListener;
import com.firebase.client.Firebase.CompletionListener;
import com.firebase.client.Firebase.ResultHandler;
import com.firebase.client.Firebase.ValueResultHandler;
import com.firebase.client.FirebaseError;
import com.firebase.client.core.AuthExpirationBehavior;
import com.firebase.client.core.Context;
import com.firebase.client.core.Path;
import com.firebase.client.core.PersistentConnection;
import com.firebase.client.core.Repo;
import com.firebase.client.core.RepoInfo;
import com.firebase.client.utilities.HttpUtilities;
import com.firebase.client.utilities.HttpUtilities.HttpRequestType;
import com.firebase.client.utilities.LogWrapper;
import com.firebase.client.utilities.Utilities;
import com.firebase.client.utilities.encoding.JsonHelpers;
import com.google.android.gms.common.Scopes;
import com.google.firebase.auth.EmailAuthProvider;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Semaphore;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.cookie.ClientCookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import rx.android.BuildConfig;

public class AuthenticationManager {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static final String AUTH_DATA_KEY = "authData";
    private static final int CONNECTION_TIMEOUT = 20000;
    private static final String CUSTOM_PROVIDER = "custom";
    private static final String ERROR_KEY = "error";
    private static final String LOG_TAG = "AuthenticationManager";
    private static final String TOKEN_KEY = "token";
    private static final String USER_DATA_KEY = "userData";
    private AuthData authData;
    private final PersistentConnection connection;
    private final Context context;
    private AuthAttempt currentAuthAttempt;
    private final Set<AuthStateListener> listenerSet;
    private final LogWrapper logger;
    private final Repo repo;
    private final RepoInfo repoInfo;
    private final CredentialStore store;

    /* renamed from: com.firebase.client.authentication.AuthenticationManager.12 */
    class AnonymousClass12 implements Runnable {
        final /* synthetic */ CompletionListener val$listener;
        final /* synthetic */ Semaphore val$semaphore;

        /* renamed from: com.firebase.client.authentication.AuthenticationManager.12.1 */
        class C10811 implements CompletionListener {
            C10811() {
            }

            public void onComplete(FirebaseError error, Firebase unusedRef) {
                if (AnonymousClass12.this.val$listener != null) {
                    AnonymousClass12.this.val$listener.onComplete(error, new Firebase(AuthenticationManager.this.repo, new Path(BuildConfig.VERSION_NAME)));
                }
            }
        }

        AnonymousClass12(Semaphore semaphore, CompletionListener completionListener) {
            this.val$semaphore = semaphore;
            this.val$listener = completionListener;
        }

        public void run() {
            AuthenticationManager.this.preemptAnyExistingAttempts();
            AuthenticationManager.this.updateAuthState(null);
            this.val$semaphore.release();
            AuthenticationManager.this.clearSession();
            AuthenticationManager.this.connection.unauth(new C10811());
            if (AuthenticationManager.this.connection.writesPaused()) {
                if (AuthenticationManager.this.logger.logsDebug()) {
                    AuthenticationManager.this.logger.debug("Unpausing writes after explicit unauth.");
                }
                AuthenticationManager.this.connection.unpauseWrites();
            }
        }
    }

    /* renamed from: com.firebase.client.authentication.AuthenticationManager.13 */
    class AnonymousClass13 implements Runnable {
        final /* synthetic */ AuthStateListener val$listener;

        /* renamed from: com.firebase.client.authentication.AuthenticationManager.13.1 */
        class C05311 implements Runnable {
            final /* synthetic */ AuthData val$authData;

            C05311(AuthData authData) {
                this.val$authData = authData;
            }

            public void run() {
                AnonymousClass13.this.val$listener.onAuthStateChanged(this.val$authData);
            }
        }

        AnonymousClass13(AuthStateListener authStateListener) {
            this.val$listener = authStateListener;
        }

        public void run() {
            AuthenticationManager.this.listenerSet.add(this.val$listener);
            AuthenticationManager.this.fireEvent(new C05311(AuthenticationManager.this.authData));
        }
    }

    /* renamed from: com.firebase.client.authentication.AuthenticationManager.14 */
    class AnonymousClass14 implements Runnable {
        final /* synthetic */ AuthStateListener val$listener;

        AnonymousClass14(AuthStateListener authStateListener) {
            this.val$listener = authStateListener;
        }

        public void run() {
            AuthenticationManager.this.listenerSet.remove(this.val$listener);
        }
    }

    /* renamed from: com.firebase.client.authentication.AuthenticationManager.15 */
    class AnonymousClass15 implements Runnable {
        final /* synthetic */ AuthResultHandler val$handler;

        AnonymousClass15(AuthResultHandler authResultHandler) {
            this.val$handler = authResultHandler;
        }

        public void run() {
            AuthenticationManager.this.makeAuthenticationRequest(Constants.FIREBASE_AUTH_ANONYMOUS_PATH, new HashMap(), this.val$handler);
        }
    }

    /* renamed from: com.firebase.client.authentication.AuthenticationManager.16 */
    class AnonymousClass16 implements Runnable {
        final /* synthetic */ String val$email;
        final /* synthetic */ AuthResultHandler val$handler;
        final /* synthetic */ String val$password;

        AnonymousClass16(String str, String str2, AuthResultHandler authResultHandler) {
            this.val$email = str;
            this.val$password = str2;
            this.val$handler = authResultHandler;
        }

        public void run() {
            Map<String, String> params = new HashMap();
            params.put(Scopes.EMAIL, this.val$email);
            params.put(EmailAuthProvider.PROVIDER_ID, this.val$password);
            AuthenticationManager.this.makeAuthenticationRequest(Constants.FIREBASE_AUTH_PASSWORD_PATH, params, this.val$handler);
        }
    }

    /* renamed from: com.firebase.client.authentication.AuthenticationManager.17 */
    class AnonymousClass17 implements Runnable {
        final /* synthetic */ AuthResultHandler val$handler;
        final /* synthetic */ String val$token;

        AnonymousClass17(AuthResultHandler authResultHandler, String str) {
            this.val$handler = authResultHandler;
            this.val$token = str;
        }

        public void run() {
            AuthenticationManager.this.authWithCredential(this.val$token, null, AuthenticationManager.this.newAuthAttempt(this.val$handler));
        }
    }

    /* renamed from: com.firebase.client.authentication.AuthenticationManager.18 */
    class AnonymousClass18 implements Runnable {
        final /* synthetic */ AuthListener val$listener;
        final /* synthetic */ String val$token;

        AnonymousClass18(AuthListener authListener, String str) {
            this.val$listener = authListener;
            this.val$token = str;
        }

        public void run() {
            AuthenticationManager.this.authWithCredential(this.val$token, null, AuthenticationManager.this.newAuthAttempt(this.val$listener));
        }
    }

    /* renamed from: com.firebase.client.authentication.AuthenticationManager.19 */
    class AnonymousClass19 implements Runnable {
        final /* synthetic */ AuthResultHandler val$handler;
        final /* synthetic */ Map val$params;
        final /* synthetic */ String val$provider;

        AnonymousClass19(String str, Map map, AuthResultHandler authResultHandler) {
            this.val$provider = str;
            this.val$params = map;
            this.val$handler = authResultHandler;
        }

        public void run() {
            AuthenticationManager.this.makeAuthenticationRequest(String.format(Constants.FIREBASE_AUTH_PROVIDER_PATH_FORMAT, new Object[]{this.val$provider}), this.val$params, this.val$handler);
        }
    }

    /* renamed from: com.firebase.client.authentication.AuthenticationManager.1 */
    class C05321 implements Runnable {
        final /* synthetic */ ValueResultHandler val$handler;
        final /* synthetic */ Object val$result;

        C05321(ValueResultHandler valueResultHandler, Object obj) {
            this.val$handler = valueResultHandler;
            this.val$result = obj;
        }

        public void run() {
            this.val$handler.onSuccess(this.val$result);
        }
    }

    /* renamed from: com.firebase.client.authentication.AuthenticationManager.20 */
    class AnonymousClass20 implements Runnable {
        final /* synthetic */ String val$email;
        final /* synthetic */ ValueResultHandler val$handler;
        final /* synthetic */ String val$password;

        AnonymousClass20(String str, String str2, ValueResultHandler valueResultHandler) {
            this.val$email = str;
            this.val$password = str2;
            this.val$handler = valueResultHandler;
        }

        public void run() {
            Map<String, String> requestParams = new HashMap();
            requestParams.put(Scopes.EMAIL, this.val$email);
            requestParams.put(EmailAuthProvider.PROVIDER_ID, this.val$password);
            AuthenticationManager.this.makeOperationRequestWithResult(Constants.FIREBASE_AUTH_CREATE_USER_PATH, HttpRequestType.POST, Collections.emptyMap(), requestParams, this.val$handler, AuthenticationManager.$assertionsDisabled);
        }
    }

    /* renamed from: com.firebase.client.authentication.AuthenticationManager.21 */
    class AnonymousClass21 implements Runnable {
        final /* synthetic */ String val$email;
        final /* synthetic */ ResultHandler val$handler;
        final /* synthetic */ String val$password;

        AnonymousClass21(String str, String str2, ResultHandler resultHandler) {
            this.val$password = str;
            this.val$email = str2;
            this.val$handler = resultHandler;
        }

        public void run() {
            Map<String, String> urlParams = new HashMap();
            urlParams.put(EmailAuthProvider.PROVIDER_ID, this.val$password);
            AuthenticationManager.this.makeOperationRequest(String.format(Constants.FIREBASE_AUTH_REMOVE_USER_PATH_FORMAT, new Object[]{this.val$email}), HttpRequestType.DELETE, urlParams, Collections.emptyMap(), this.val$handler, true);
        }
    }

    /* renamed from: com.firebase.client.authentication.AuthenticationManager.22 */
    class AnonymousClass22 implements Runnable {
        final /* synthetic */ String val$email;
        final /* synthetic */ ResultHandler val$handler;
        final /* synthetic */ String val$newPassword;
        final /* synthetic */ String val$oldPassword;

        AnonymousClass22(String str, String str2, String str3, ResultHandler resultHandler) {
            this.val$oldPassword = str;
            this.val$newPassword = str2;
            this.val$email = str3;
            this.val$handler = resultHandler;
        }

        public void run() {
            Map<String, String> urlParams = new HashMap();
            urlParams.put("oldPassword", this.val$oldPassword);
            Map<String, String> requestParams = new HashMap();
            requestParams.put(EmailAuthProvider.PROVIDER_ID, this.val$newPassword);
            AuthenticationManager.this.makeOperationRequest(String.format(Constants.FIREBASE_AUTH_PASSWORD_PATH_FORMAT, new Object[]{this.val$email}), HttpRequestType.PUT, urlParams, requestParams, this.val$handler, AuthenticationManager.$assertionsDisabled);
        }
    }

    /* renamed from: com.firebase.client.authentication.AuthenticationManager.23 */
    class AnonymousClass23 implements Runnable {
        final /* synthetic */ ResultHandler val$handler;
        final /* synthetic */ String val$newEmail;
        final /* synthetic */ String val$oldEmail;
        final /* synthetic */ String val$password;

        AnonymousClass23(String str, String str2, String str3, ResultHandler resultHandler) {
            this.val$password = str;
            this.val$newEmail = str2;
            this.val$oldEmail = str3;
            this.val$handler = resultHandler;
        }

        public void run() {
            Map<String, String> urlParams = new HashMap();
            urlParams.put(EmailAuthProvider.PROVIDER_ID, this.val$password);
            Map<String, String> requestParams = new HashMap();
            requestParams.put(Scopes.EMAIL, this.val$newEmail);
            AuthenticationManager.this.makeOperationRequest(String.format(Constants.FIREBASE_AUTH_EMAIL_PATH_FORMAT, new Object[]{this.val$oldEmail}), HttpRequestType.PUT, urlParams, requestParams, this.val$handler, AuthenticationManager.$assertionsDisabled);
        }
    }

    /* renamed from: com.firebase.client.authentication.AuthenticationManager.24 */
    class AnonymousClass24 implements Runnable {
        final /* synthetic */ String val$email;
        final /* synthetic */ ResultHandler val$handler;

        AnonymousClass24(String str, ResultHandler resultHandler) {
            this.val$email = str;
            this.val$handler = resultHandler;
        }

        public void run() {
            String url = String.format(Constants.FIREBASE_AUTH_PASSWORD_PATH_FORMAT, new Object[]{this.val$email});
            Map<String, String> params = Collections.emptyMap();
            AuthenticationManager.this.makeOperationRequest(url, HttpRequestType.POST, params, params, this.val$handler, AuthenticationManager.$assertionsDisabled);
        }
    }

    /* renamed from: com.firebase.client.authentication.AuthenticationManager.2 */
    class C05332 implements Runnable {
        final /* synthetic */ FirebaseError val$error;
        final /* synthetic */ ValueResultHandler val$handler;

        C05332(ValueResultHandler valueResultHandler, FirebaseError firebaseError) {
            this.val$handler = valueResultHandler;
            this.val$error = firebaseError;
        }

        public void run() {
            this.val$handler.onError(this.val$error);
        }
    }

    /* renamed from: com.firebase.client.authentication.AuthenticationManager.4 */
    class C05344 implements Runnable {
        final /* synthetic */ AuthAttempt val$attempt;
        final /* synthetic */ FirebaseError val$error;

        C05344(AuthAttempt authAttempt, FirebaseError firebaseError) {
            this.val$attempt = authAttempt;
            this.val$error = firebaseError;
        }

        public void run() {
            this.val$attempt.fireError(this.val$error);
        }
    }

    /* renamed from: com.firebase.client.authentication.AuthenticationManager.6 */
    class C05356 implements Runnable {
        final /* synthetic */ Map val$authDataObj;
        final /* synthetic */ String val$tokenString;
        final /* synthetic */ Map val$userData;

        /* renamed from: com.firebase.client.authentication.AuthenticationManager.6.1 */
        class C10841 implements AuthListener {
            C10841() {
            }

            public void onAuthError(FirebaseError error) {
                AuthenticationManager.this.handleBadAuthStatus(error, null, AuthenticationManager.$assertionsDisabled);
            }

            public void onAuthSuccess(Object authData) {
                AuthenticationManager.this.handleAuthSuccess(C05356.this.val$tokenString, C05356.this.val$authDataObj, C05356.this.val$userData, AuthenticationManager.$assertionsDisabled, null);
            }

            public void onAuthRevoked(FirebaseError error) {
                AuthenticationManager.this.handleBadAuthStatus(error, null, true);
            }
        }

        C05356(String str, Map map, Map map2) {
            this.val$tokenString = str;
            this.val$authDataObj = map;
            this.val$userData = map2;
        }

        public void run() {
            AuthenticationManager.this.connection.auth(this.val$tokenString, new C10841());
        }
    }

    /* renamed from: com.firebase.client.authentication.AuthenticationManager.7 */
    class C05367 implements Runnable {
        final /* synthetic */ AuthData val$authData;
        final /* synthetic */ AuthStateListener val$listener;

        C05367(AuthStateListener authStateListener, AuthData authData) {
            this.val$listener = authStateListener;
            this.val$authData = authData;
        }

        public void run() {
            this.val$listener.onAuthStateChanged(this.val$authData);
        }
    }

    /* renamed from: com.firebase.client.authentication.AuthenticationManager.8 */
    class C05398 implements Runnable {
        final /* synthetic */ RequestHandler val$handler;
        final /* synthetic */ HttpUriRequest val$request;

        /* renamed from: com.firebase.client.authentication.AuthenticationManager.8.1 */
        class C05371 implements Runnable {
            final /* synthetic */ Map val$result;

            C05371(Map map) {
                this.val$result = map;
            }

            public void run() {
                C05398.this.val$handler.onResult(this.val$result);
            }
        }

        /* renamed from: com.firebase.client.authentication.AuthenticationManager.8.2 */
        class C05382 implements Runnable {
            final /* synthetic */ IOException val$e;

            C05382(IOException iOException) {
                this.val$e = iOException;
            }

            public void run() {
                C05398.this.val$handler.onError(this.val$e);
            }
        }

        C05398(HttpUriRequest httpUriRequest, RequestHandler requestHandler) {
            this.val$request = httpUriRequest;
            this.val$handler = requestHandler;
        }

        public void run() {
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, AuthenticationManager.CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpParameters, AuthenticationManager.CONNECTION_TIMEOUT);
            DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
            httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
            try {
                Map<String, Object> result = (Map) httpClient.execute(this.val$request, new JsonBasicResponseHandler());
                if (result == null) {
                    throw new IOException("Authentication server did not respond with a valid response");
                }
                AuthenticationManager.this.scheduleNow(new C05371(result));
            } catch (IOException e) {
                AuthenticationManager.this.scheduleNow(new C05382(e));
            }
        }
    }

    private class AuthAttempt {
        private AuthResultHandler handler;
        private final AuthListener legacyListener;

        /* renamed from: com.firebase.client.authentication.AuthenticationManager.AuthAttempt.1 */
        class C05401 implements Runnable {
            final /* synthetic */ FirebaseError val$error;

            C05401(FirebaseError firebaseError) {
                this.val$error = firebaseError;
            }

            public void run() {
                if (AuthAttempt.this.legacyListener != null) {
                    AuthAttempt.this.legacyListener.onAuthError(this.val$error);
                } else if (AuthAttempt.this.handler != null) {
                    AuthAttempt.this.handler.onAuthenticationError(this.val$error);
                    AuthAttempt.this.handler = null;
                }
            }
        }

        /* renamed from: com.firebase.client.authentication.AuthenticationManager.AuthAttempt.2 */
        class C05412 implements Runnable {
            final /* synthetic */ AuthData val$authData;

            C05412(AuthData authData) {
                this.val$authData = authData;
            }

            public void run() {
                if (AuthAttempt.this.legacyListener != null) {
                    AuthAttempt.this.legacyListener.onAuthSuccess(this.val$authData);
                } else if (AuthAttempt.this.handler != null) {
                    AuthAttempt.this.handler.onAuthenticated(this.val$authData);
                    AuthAttempt.this.handler = null;
                }
            }
        }

        /* renamed from: com.firebase.client.authentication.AuthenticationManager.AuthAttempt.3 */
        class C05423 implements Runnable {
            final /* synthetic */ FirebaseError val$error;

            C05423(FirebaseError firebaseError) {
                this.val$error = firebaseError;
            }

            public void run() {
                AuthAttempt.this.legacyListener.onAuthRevoked(this.val$error);
            }
        }

        AuthAttempt(AuthResultHandler handler) {
            this.handler = handler;
            this.legacyListener = null;
        }

        AuthAttempt(AuthListener legacyListener) {
            this.legacyListener = legacyListener;
            this.handler = null;
        }

        public void fireError(FirebaseError error) {
            if (this.legacyListener != null || this.handler != null) {
                AuthenticationManager.this.fireEvent(new C05401(error));
            }
        }

        public void fireSuccess(AuthData authData) {
            if (this.legacyListener != null || this.handler != null) {
                AuthenticationManager.this.fireEvent(new C05412(authData));
            }
        }

        public void fireRevoked(FirebaseError error) {
            if (this.legacyListener != null) {
                AuthenticationManager.this.fireEvent(new C05423(error));
            }
        }
    }

    /* renamed from: com.firebase.client.authentication.AuthenticationManager.10 */
    class AnonymousClass10 implements RequestHandler {
        final /* synthetic */ ValueResultHandler val$handler;
        final /* synthetic */ boolean val$logUserOut;

        /* renamed from: com.firebase.client.authentication.AuthenticationManager.10.1 */
        class C05301 implements Runnable {
            final /* synthetic */ Map val$result;

            C05301(Map map) {
                this.val$result = map;
            }

            public void run() {
                AuthenticationManager.this.fireOnSuccess(AnonymousClass10.this.val$handler, this.val$result);
            }
        }

        AnonymousClass10(boolean z, ValueResultHandler valueResultHandler) {
            this.val$logUserOut = z;
            this.val$handler = valueResultHandler;
        }

        public void onResult(Map<String, Object> result) {
            Object errorResponse = result.get(AuthenticationManager.ERROR_KEY);
            if (errorResponse == null) {
                if (this.val$logUserOut) {
                    String uid = (String) Utilities.getOrNull(result, "uid", String.class);
                    if (!(uid == null || AuthenticationManager.this.authData == null || !uid.equals(AuthenticationManager.this.authData.getUid()))) {
                        AuthenticationManager.this.unauth(null, AuthenticationManager.$assertionsDisabled);
                    }
                }
                AuthenticationManager.this.scheduleNow(new C05301(result));
                return;
            }
            AuthenticationManager.this.fireOnError(this.val$handler, AuthenticationManager.this.decodeErrorResponse(errorResponse));
        }

        public void onError(IOException e) {
            AuthenticationManager.this.fireOnError(this.val$handler, new FirebaseError(-24, "There was an exception while performing the request: " + e.getLocalizedMessage()));
        }
    }

    /* renamed from: com.firebase.client.authentication.AuthenticationManager.11 */
    class AnonymousClass11 implements AuthListener {
        final /* synthetic */ AuthAttempt val$attempt;
        final /* synthetic */ String val$credential;
        final /* synthetic */ Map val$optionalUserData;

        AnonymousClass11(String str, Map map, AuthAttempt authAttempt) {
            this.val$credential = str;
            this.val$optionalUserData = map;
            this.val$attempt = authAttempt;
        }

        public void onAuthSuccess(Object authData) {
            AuthenticationManager.this.handleAuthSuccess(this.val$credential, (Map) authData, this.val$optionalUserData, true, this.val$attempt);
        }

        public void onAuthRevoked(FirebaseError error) {
            AuthenticationManager.this.handleBadAuthStatus(error, this.val$attempt, true);
        }

        public void onAuthError(FirebaseError error) {
            AuthenticationManager.this.handleBadAuthStatus(error, this.val$attempt, AuthenticationManager.$assertionsDisabled);
        }
    }

    /* renamed from: com.firebase.client.authentication.AuthenticationManager.3 */
    class C10823 implements ValueResultHandler {
        final /* synthetic */ ResultHandler val$handler;

        C10823(ResultHandler resultHandler) {
            this.val$handler = resultHandler;
        }

        public void onSuccess(Object result) {
            this.val$handler.onSuccess();
        }

        public void onError(FirebaseError error) {
            this.val$handler.onError(error);
        }
    }

    /* renamed from: com.firebase.client.authentication.AuthenticationManager.5 */
    class C10835 extends TypeReference<Map<String, Object>> {
        C10835() {
        }
    }

    /* renamed from: com.firebase.client.authentication.AuthenticationManager.9 */
    class C10859 implements RequestHandler {
        final /* synthetic */ AuthAttempt val$attempt;

        C10859(AuthAttempt authAttempt) {
            this.val$attempt = authAttempt;
        }

        public void onResult(Map<String, Object> result) {
            Object errorResponse = result.get(AuthenticationManager.ERROR_KEY);
            String token = (String) Utilities.getOrNull(result, AuthenticationManager.TOKEN_KEY, String.class);
            if (errorResponse != null || token == null) {
                AuthenticationManager.this.fireAuthErrorIfNotPreempted(AuthenticationManager.this.decodeErrorResponse(errorResponse), this.val$attempt);
            } else if (!AuthenticationManager.this.attemptHasBeenPreempted(this.val$attempt)) {
                AuthenticationManager.this.authWithCredential(token, result, this.val$attempt);
            }
        }

        public void onError(IOException e) {
            AuthenticationManager.this.fireAuthErrorIfNotPreempted(new FirebaseError(-24, "There was an exception while connecting to the authentication server: " + e.getLocalizedMessage()), this.val$attempt);
        }
    }

    static {
        $assertionsDisabled = !AuthenticationManager.class.desiredAssertionStatus() ? true : $assertionsDisabled;
    }

    public AuthenticationManager(Context context, Repo repo, RepoInfo repoInfo, PersistentConnection connection) {
        this.context = context;
        this.repo = repo;
        this.repoInfo = repoInfo;
        this.connection = connection;
        this.authData = null;
        this.store = context.getCredentialStore();
        this.logger = context.getLogger(LOG_TAG);
        this.listenerSet = new HashSet();
    }

    private void fireEvent(Runnable r) {
        this.context.getEventTarget().postEvent(r);
    }

    private void fireOnSuccess(ValueResultHandler handler, Object result) {
        if (handler != null) {
            fireEvent(new C05321(handler, result));
        }
    }

    private void fireOnError(ValueResultHandler handler, FirebaseError error) {
        if (handler != null) {
            fireEvent(new C05332(handler, error));
        }
    }

    private ValueResultHandler ignoreResultValueHandler(ResultHandler handler) {
        return new C10823(handler);
    }

    private void preemptAnyExistingAttempts() {
        if (this.currentAuthAttempt != null) {
            this.currentAuthAttempt.fireError(new FirebaseError(-5, "Due to another authentication attempt, this authentication attempt was aborted before it could complete."));
            this.currentAuthAttempt = null;
        }
    }

    private FirebaseError decodeErrorResponse(Object errorResponse) {
        String code = (String) Utilities.getOrNull(errorResponse, "code", String.class);
        String message = (String) Utilities.getOrNull(errorResponse, ShareConstants.WEB_DIALOG_PARAM_MESSAGE, String.class);
        String details = (String) Utilities.getOrNull(errorResponse, "details", String.class);
        if (code != null) {
            return FirebaseError.fromStatus(code, message, details);
        }
        String errorMessage;
        if (message == null) {
            errorMessage = "Error while authenticating.";
        } else {
            errorMessage = message;
        }
        return new FirebaseError(FirebaseError.UNKNOWN_ERROR, errorMessage, details);
    }

    private boolean attemptHasBeenPreempted(AuthAttempt attempt) {
        return attempt != this.currentAuthAttempt ? true : $assertionsDisabled;
    }

    private AuthAttempt newAuthAttempt(AuthResultHandler handler) {
        preemptAnyExistingAttempts();
        this.currentAuthAttempt = new AuthAttempt(handler);
        return this.currentAuthAttempt;
    }

    private AuthAttempt newAuthAttempt(AuthListener listener) {
        preemptAnyExistingAttempts();
        this.currentAuthAttempt = new AuthAttempt(listener);
        return this.currentAuthAttempt;
    }

    private void fireAuthErrorIfNotPreempted(FirebaseError error, AuthAttempt attempt) {
        if (!attemptHasBeenPreempted(attempt)) {
            if (attempt != null) {
                fireEvent(new C05344(attempt, error));
            }
            this.currentAuthAttempt = null;
        }
    }

    private void checkServerSettings() {
        if (this.repoInfo.isDemoHost()) {
            this.logger.warn("Firebase authentication is supported on production Firebases only (*.firebaseio.com). To secure your Firebase, create a production Firebase at https://www.firebase.com.");
        } else if (this.repoInfo.isCustomHost() && !this.context.isCustomAuthenticationServerSet()) {
            throw new IllegalStateException("For a custom firebase host you must first set your authentication server before using authentication features!");
        }
    }

    private String getFirebaseCredentialIdentifier() {
        return this.repoInfo.host;
    }

    private void scheduleNow(Runnable r) {
        this.context.getRunLoop().scheduleNow(r);
    }

    private AuthData parseAuthData(String token, Map<String, Object> rawAuthData, Map<String, Object> userData) {
        long expires;
        Map<String, Object> authData = (Map) Utilities.getOrNull(rawAuthData, "auth", Map.class);
        if (authData == null) {
            this.logger.warn("Received invalid auth data: " + rawAuthData);
        }
        Object expiresObj = rawAuthData.get(ClientCookie.EXPIRES_ATTR);
        if (expiresObj == null) {
            expires = 0;
        } else if (expiresObj instanceof Integer) {
            expires = (long) ((Integer) expiresObj).intValue();
        } else if (expiresObj instanceof Long) {
            expires = ((Long) expiresObj).longValue();
        } else if (expiresObj instanceof Double) {
            expires = ((Double) expiresObj).longValue();
        } else {
            expires = 0;
        }
        String uid = (String) Utilities.getOrNull(authData, "uid", String.class);
        if (uid == null) {
            uid = (String) Utilities.getOrNull(userData, "uid", String.class);
        }
        String provider = (String) Utilities.getOrNull(authData, "provider", String.class);
        if (provider == null) {
            provider = (String) Utilities.getOrNull(userData, "provider", String.class);
        }
        if (provider == null) {
            provider = CUSTOM_PROVIDER;
        }
        if (uid == null || uid.isEmpty()) {
            this.logger.warn("Received invalid auth data: " + authData);
        }
        Map<String, Object> providerData = (Map) Utilities.getOrNull(userData, provider, Map.class);
        if (providerData == null) {
            providerData = new HashMap();
        }
        return new AuthData(token, expires, uid, provider, authData, providerData);
    }

    private void handleBadAuthStatus(FirebaseError error, AuthAttempt attempt, boolean revoked) {
        if ((error.getCode() == -6 ? true : $assertionsDisabled) && this.context.getAuthExpirationBehavior() == AuthExpirationBehavior.PAUSE_WRITES_UNTIL_REAUTH) {
            if (this.logger.logsDebug()) {
                this.logger.debug("Pausing writes due to expired token.");
            }
            this.connection.pauseWrites();
        } else if (!this.connection.writesPaused()) {
            clearSession();
        } else if (!$assertionsDisabled && this.context.getAuthExpirationBehavior() != AuthExpirationBehavior.PAUSE_WRITES_UNTIL_REAUTH) {
            throw new AssertionError();
        } else if (this.logger.logsDebug()) {
            this.logger.debug("Invalid auth while writes are paused; keeping existing session.");
        }
        updateAuthState(null);
        if (attempt == null) {
            return;
        }
        if (revoked) {
            attempt.fireRevoked(error);
        } else {
            attempt.fireError(error);
        }
    }

    private void handleAuthSuccess(String credential, Map<String, Object> authDataMap, Map<String, Object> optionalUserData, boolean isNewSession, AuthAttempt attempt) {
        if (isNewSession && !((authDataMap.get("auth") == null && authDataMap.get(ClientCookie.EXPIRES_ATTR) == null) || saveSession(credential, authDataMap, optionalUserData))) {
            this.logger.warn("Failed to store credentials! Authentication will not be persistent!");
        }
        AuthData authData = parseAuthData(credential, authDataMap, optionalUserData);
        updateAuthState(authData);
        if (attempt != null) {
            attempt.fireSuccess(authData);
        }
        if (this.connection.writesPaused()) {
            if (this.logger.logsDebug()) {
                this.logger.debug("Unpausing writes after successful login.");
            }
            this.connection.unpauseWrites();
        }
    }

    public void resumeSession() {
        try {
            String credentialData = this.store.loadCredential(getFirebaseCredentialIdentifier(), this.context.getSessionPersistenceKey());
            if (credentialData != null) {
                Map<String, Object> credentials = (Map) JsonHelpers.getMapper().readValue(credentialData, new C10835());
                String tokenString = (String) Utilities.getOrNull(credentials, TOKEN_KEY, String.class);
                Map<String, Object> authDataObj = (Map) Utilities.getOrNull(credentials, AUTH_DATA_KEY, Map.class);
                Map<String, Object> userData = (Map) Utilities.getOrNull(credentials, USER_DATA_KEY, Map.class);
                if (authDataObj != null) {
                    updateAuthState(parseAuthData(tokenString, authDataObj, userData));
                    this.context.getRunLoop().scheduleNow(new C05356(tokenString, authDataObj, userData));
                }
            }
        } catch (IOException e) {
            this.logger.warn("Failed resuming authentication session!", e);
            clearSession();
        }
    }

    private boolean saveSession(String token, Map<String, Object> authData, Map<String, Object> userData) {
        String firebaseId = getFirebaseCredentialIdentifier();
        String sessionId = this.context.getSessionPersistenceKey();
        this.store.clearCredential(firebaseId, sessionId);
        Map<String, Object> sessionMap = new HashMap();
        sessionMap.put(TOKEN_KEY, token);
        sessionMap.put(AUTH_DATA_KEY, authData);
        sessionMap.put(USER_DATA_KEY, userData);
        try {
            if (this.logger.logsDebug()) {
                this.logger.debug("Storing credentials for Firebase \"" + firebaseId + "\" and session \"" + sessionId + "\".");
            }
            return this.store.storeCredential(firebaseId, sessionId, JsonHelpers.getMapper().writeValueAsString(sessionMap));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean clearSession() {
        String firebaseId = getFirebaseCredentialIdentifier();
        String sessionId = this.context.getSessionPersistenceKey();
        if (this.logger.logsDebug()) {
            this.logger.debug("Clearing credentials for Firebase \"" + firebaseId + "\" and session \"" + sessionId + "\".");
        }
        return this.store.clearCredential(firebaseId, sessionId);
    }

    private void updateAuthState(AuthData authData) {
        boolean changed = true;
        if (this.authData == null) {
            if (authData == null) {
                changed = $assertionsDisabled;
            }
        } else if (this.authData.equals(authData)) {
            changed = $assertionsDisabled;
        }
        this.authData = authData;
        if (changed) {
            for (AuthStateListener listener : this.listenerSet) {
                fireEvent(new C05367(listener, authData));
            }
        }
    }

    private String buildUrlPath(String urlPath) {
        StringBuilder path = new StringBuilder();
        path.append("/v2/");
        path.append(this.repoInfo.namespace);
        if (!urlPath.startsWith("/")) {
            path.append("/");
        }
        path.append(urlPath);
        return path.toString();
    }

    private void makeRequest(String urlPath, HttpRequestType type, Map<String, String> urlParams, Map<String, String> requestParams, RequestHandler handler) {
        Map<String, String> actualUrlParams = new HashMap(urlParams);
        actualUrlParams.put(NotificationCompatApi24.CATEGORY_TRANSPORT, "json");
        actualUrlParams.put("v", this.context.getPlatformVersion());
        HttpUriRequest request = HttpUtilities.requestWithType(this.context.getAuthenticationServer(), buildUrlPath(urlPath), type, actualUrlParams, requestParams);
        if (this.logger.logsDebug()) {
            URI uri = request.getURI();
            String scheme = uri.getScheme();
            String authority = uri.getAuthority();
            String path = uri.getPath();
            int numQueryParams = uri.getQuery().split("&").length;
            this.logger.debug(String.format("Sending request to %s://%s%s with %d query params", new Object[]{scheme, authority, path, Integer.valueOf(numQueryParams)}));
        }
        this.context.runBackgroundTask(new C05398(request, handler));
    }

    private void makeAuthenticationRequest(String urlPath, Map<String, String> params, AuthResultHandler handler) {
        String str = urlPath;
        Map<String, String> map = params;
        makeRequest(str, HttpRequestType.GET, map, Collections.emptyMap(), new C10859(newAuthAttempt(handler)));
    }

    private void makeOperationRequest(String urlPath, HttpRequestType type, Map<String, String> urlParams, Map<String, String> requestParams, ResultHandler handler, boolean logUserOut) {
        makeOperationRequestWithResult(urlPath, type, urlParams, requestParams, ignoreResultValueHandler(handler), logUserOut);
    }

    private void makeOperationRequestWithResult(String urlPath, HttpRequestType type, Map<String, String> urlParams, Map<String, String> requestParams, ValueResultHandler<Map<String, Object>> handler, boolean logUserOut) {
        makeRequest(urlPath, type, urlParams, requestParams, new AnonymousClass10(logUserOut, handler));
    }

    private void authWithCredential(String credential, Map<String, Object> optionalUserData, AuthAttempt attempt) {
        if (attempt != this.currentAuthAttempt) {
            throw new IllegalStateException("Ooops. We messed up tracking which authentications are running!");
        }
        if (this.logger.logsDebug()) {
            this.logger.debug("Authenticating with credential of length " + credential.length());
        }
        this.currentAuthAttempt = null;
        this.connection.auth(credential, new AnonymousClass11(credential, optionalUserData, attempt));
    }

    public AuthData getAuth() {
        return this.authData;
    }

    public void unauth() {
        checkServerSettings();
        unauth(null);
    }

    public void unauth(CompletionListener listener) {
        unauth(listener, true);
    }

    public void unauth(CompletionListener listener, boolean waitForCompletion) {
        checkServerSettings();
        Semaphore semaphore = new Semaphore(0);
        scheduleNow(new AnonymousClass12(semaphore, listener));
        if (waitForCompletion) {
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void addAuthStateListener(AuthStateListener listener) {
        checkServerSettings();
        scheduleNow(new AnonymousClass13(listener));
    }

    public void removeAuthStateListener(AuthStateListener listener) {
        checkServerSettings();
        scheduleNow(new AnonymousClass14(listener));
    }

    public void authAnonymously(AuthResultHandler handler) {
        checkServerSettings();
        scheduleNow(new AnonymousClass15(handler));
    }

    public void authWithPassword(String email, String password, AuthResultHandler handler) {
        checkServerSettings();
        scheduleNow(new AnonymousClass16(email, password, handler));
    }

    public void authWithCustomToken(String token, AuthResultHandler handler) {
        scheduleNow(new AnonymousClass17(handler, token));
    }

    public void authWithFirebaseToken(String token, AuthListener listener) {
        scheduleNow(new AnonymousClass18(listener, token));
    }

    public void authWithOAuthToken(String provider, String token, AuthResultHandler handler) {
        if (token == null) {
            throw new IllegalArgumentException("Token must not be null!");
        }
        Map params = new HashMap();
        params.put(ServerProtocol.DIALOG_PARAM_ACCESS_TOKEN, token);
        authWithOAuthToken(provider, params, handler);
    }

    public void authWithOAuthToken(String provider, Map<String, String> params, AuthResultHandler handler) {
        checkServerSettings();
        scheduleNow(new AnonymousClass19(provider, params, handler));
    }

    public void createUser(String email, String password, ResultHandler handler) {
        createUser(email, password, ignoreResultValueHandler(handler));
    }

    public void createUser(String email, String password, ValueResultHandler<Map<String, Object>> handler) {
        checkServerSettings();
        scheduleNow(new AnonymousClass20(email, password, handler));
    }

    public void removeUser(String email, String password, ResultHandler handler) {
        checkServerSettings();
        scheduleNow(new AnonymousClass21(password, email, handler));
    }

    public void changePassword(String email, String oldPassword, String newPassword, ResultHandler handler) {
        checkServerSettings();
        scheduleNow(new AnonymousClass22(oldPassword, newPassword, email, handler));
    }

    public void changeEmail(String oldEmail, String password, String newEmail, ResultHandler handler) {
        checkServerSettings();
        scheduleNow(new AnonymousClass23(password, newEmail, oldEmail, handler));
    }

    public void resetPassword(String email, ResultHandler handler) {
        checkServerSettings();
        scheduleNow(new AnonymousClass24(email, handler));
    }
}
