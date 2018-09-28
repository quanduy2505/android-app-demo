package com.facebook;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.facebook.AccessToken.AccessTokenRefreshCallback;
import com.facebook.GraphRequest.Callback;
import com.facebook.internal.AnalyticsEvents;
import com.facebook.internal.ServerProtocol;
import com.facebook.internal.Utility;
import com.facebook.internal.Validate;
import com.facebook.share.internal.ShareConstants;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import org.json.JSONArray;
import org.json.JSONObject;

final class AccessTokenManager {
    static final String ACTION_CURRENT_ACCESS_TOKEN_CHANGED = "com.facebook.sdk.ACTION_CURRENT_ACCESS_TOKEN_CHANGED";
    static final String EXTRA_NEW_ACCESS_TOKEN = "com.facebook.sdk.EXTRA_NEW_ACCESS_TOKEN";
    static final String EXTRA_OLD_ACCESS_TOKEN = "com.facebook.sdk.EXTRA_OLD_ACCESS_TOKEN";
    private static final String ME_PERMISSIONS_GRAPH_PATH = "me/permissions";
    static final String SHARED_PREFERENCES_NAME = "com.facebook.AccessTokenManager.SharedPreferences";
    static final String TAG = "AccessTokenManager";
    private static final String TOKEN_EXTEND_GRAPH_PATH = "oauth/access_token";
    private static final int TOKEN_EXTEND_RETRY_SECONDS = 3600;
    private static final int TOKEN_EXTEND_THRESHOLD_SECONDS = 86400;
    private static volatile AccessTokenManager instance;
    private final AccessTokenCache accessTokenCache;
    private AccessToken currentAccessToken;
    private Date lastAttemptedTokenExtendDate;
    private final LocalBroadcastManager localBroadcastManager;
    private AtomicBoolean tokenRefreshInProgress;

    /* renamed from: com.facebook.AccessTokenManager.1 */
    class C03631 implements Runnable {
        final /* synthetic */ AccessTokenRefreshCallback val$callback;

        C03631(AccessTokenRefreshCallback accessTokenRefreshCallback) {
            this.val$callback = accessTokenRefreshCallback;
        }

        public void run() {
            AccessTokenManager.this.refreshCurrentAccessTokenImpl(this.val$callback);
        }
    }

    private static class RefreshResult {
        public String accessToken;
        public int expiresAt;

        private RefreshResult() {
        }
    }

    /* renamed from: com.facebook.AccessTokenManager.2 */
    class C09792 implements Callback {
        final /* synthetic */ Set val$declinedPermissions;
        final /* synthetic */ Set val$permissions;
        final /* synthetic */ AtomicBoolean val$permissionsCallSucceeded;

        C09792(AtomicBoolean atomicBoolean, Set set, Set set2) {
            this.val$permissionsCallSucceeded = atomicBoolean;
            this.val$permissions = set;
            this.val$declinedPermissions = set2;
        }

        public void onCompleted(GraphResponse response) {
            JSONObject result = response.getJSONObject();
            if (result != null) {
                JSONArray permissionsArray = result.optJSONArray(ShareConstants.WEB_DIALOG_PARAM_DATA);
                if (permissionsArray != null) {
                    this.val$permissionsCallSucceeded.set(true);
                    for (int i = 0; i < permissionsArray.length(); i++) {
                        JSONObject permissionEntry = permissionsArray.optJSONObject(i);
                        if (permissionEntry != null) {
                            String permission = permissionEntry.optString("permission");
                            String status = permissionEntry.optString(AnalyticsEvents.PARAMETER_SHARE_DIALOG_CONTENT_STATUS);
                            if (!(Utility.isNullOrEmpty(permission) || Utility.isNullOrEmpty(status))) {
                                status = status.toLowerCase(Locale.US);
                                if (status.equals("granted")) {
                                    this.val$permissions.add(permission);
                                } else if (status.equals("declined")) {
                                    this.val$declinedPermissions.add(permission);
                                } else {
                                    Log.w(AccessTokenManager.TAG, "Unexpected status: " + status);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /* renamed from: com.facebook.AccessTokenManager.3 */
    class C09803 implements Callback {
        final /* synthetic */ RefreshResult val$refreshResult;

        C09803(RefreshResult refreshResult) {
            this.val$refreshResult = refreshResult;
        }

        public void onCompleted(GraphResponse response) {
            JSONObject data = response.getJSONObject();
            if (data != null) {
                this.val$refreshResult.accessToken = data.optString(ServerProtocol.DIALOG_PARAM_ACCESS_TOKEN);
                this.val$refreshResult.expiresAt = data.optInt("expires_at");
            }
        }
    }

    /* renamed from: com.facebook.AccessTokenManager.4 */
    class C09814 implements GraphRequestBatch.Callback {
        final /* synthetic */ AccessToken val$accessToken;
        final /* synthetic */ AccessTokenRefreshCallback val$callback;
        final /* synthetic */ Set val$declinedPermissions;
        final /* synthetic */ Set val$permissions;
        final /* synthetic */ AtomicBoolean val$permissionsCallSucceeded;
        final /* synthetic */ RefreshResult val$refreshResult;

        C09814(AccessToken accessToken, AccessTokenRefreshCallback accessTokenRefreshCallback, AtomicBoolean atomicBoolean, RefreshResult refreshResult, Set set, Set set2) {
            this.val$accessToken = accessToken;
            this.val$callback = accessTokenRefreshCallback;
            this.val$permissionsCallSucceeded = atomicBoolean;
            this.val$refreshResult = refreshResult;
            this.val$permissions = set;
            this.val$declinedPermissions = set2;
        }

        public void onBatchCompleted(GraphRequestBatch batch) {
            Throwable th;
            AccessToken newAccessToken;
            try {
                if (AccessTokenManager.getInstance().getCurrentAccessToken() == null || AccessTokenManager.getInstance().getCurrentAccessToken().getUserId() != this.val$accessToken.getUserId()) {
                    if (this.val$callback != null) {
                        this.val$callback.OnTokenRefreshFailed(new FacebookException("No current access token to refresh"));
                    }
                    AccessTokenManager.this.tokenRefreshInProgress.set(false);
                    if (!(this.val$callback == null || null == null)) {
                        this.val$callback.OnTokenRefreshed(null);
                    }
                    newAccessToken = null;
                } else if (!this.val$permissionsCallSucceeded.get() && this.val$refreshResult.accessToken == null && this.val$refreshResult.expiresAt == 0) {
                    if (this.val$callback != null) {
                        this.val$callback.OnTokenRefreshFailed(new FacebookException("Failed to refresh access token"));
                    }
                    AccessTokenManager.this.tokenRefreshInProgress.set(false);
                    if (!(this.val$callback == null || null == null)) {
                        this.val$callback.OnTokenRefreshed(null);
                    }
                    newAccessToken = null;
                } else {
                    String str;
                    Collection collection;
                    Collection collection2;
                    Date date;
                    if (this.val$refreshResult.accessToken != null) {
                        str = this.val$refreshResult.accessToken;
                    } else {
                        str = this.val$accessToken.getToken();
                    }
                    String applicationId = this.val$accessToken.getApplicationId();
                    String userId = this.val$accessToken.getUserId();
                    if (this.val$permissionsCallSucceeded.get()) {
                        collection = this.val$permissions;
                    } else {
                        collection = this.val$accessToken.getPermissions();
                    }
                    if (this.val$permissionsCallSucceeded.get()) {
                        collection2 = this.val$declinedPermissions;
                    } else {
                        collection2 = this.val$accessToken.getDeclinedPermissions();
                    }
                    AccessTokenSource source = this.val$accessToken.getSource();
                    if (this.val$refreshResult.expiresAt != 0) {
                        date = new Date(((long) this.val$refreshResult.expiresAt) * 1000);
                    } else {
                        date = this.val$accessToken.getExpires();
                    }
                    newAccessToken = new AccessToken(str, applicationId, userId, collection, collection2, source, date, new Date());
                    try {
                        AccessTokenManager.getInstance().setCurrentAccessToken(newAccessToken);
                        AccessTokenManager.this.tokenRefreshInProgress.set(false);
                        if (this.val$callback != null && newAccessToken != null) {
                            this.val$callback.OnTokenRefreshed(newAccessToken);
                        }
                    } catch (Throwable th2) {
                        th = th2;
                        AccessTokenManager.this.tokenRefreshInProgress.set(false);
                        this.val$callback.OnTokenRefreshed(newAccessToken);
                        throw th;
                    }
                }
            } catch (Throwable th3) {
                th = th3;
                newAccessToken = null;
                AccessTokenManager.this.tokenRefreshInProgress.set(false);
                if (!(this.val$callback == null || newAccessToken == null)) {
                    this.val$callback.OnTokenRefreshed(newAccessToken);
                }
                throw th;
            }
        }
    }

    AccessTokenManager(LocalBroadcastManager localBroadcastManager, AccessTokenCache accessTokenCache) {
        this.tokenRefreshInProgress = new AtomicBoolean(false);
        this.lastAttemptedTokenExtendDate = new Date(0);
        Validate.notNull(localBroadcastManager, "localBroadcastManager");
        Validate.notNull(accessTokenCache, "accessTokenCache");
        this.localBroadcastManager = localBroadcastManager;
        this.accessTokenCache = accessTokenCache;
    }

    static AccessTokenManager getInstance() {
        if (instance == null) {
            synchronized (AccessTokenManager.class) {
                if (instance == null) {
                    instance = new AccessTokenManager(LocalBroadcastManager.getInstance(FacebookSdk.getApplicationContext()), new AccessTokenCache());
                }
            }
        }
        return instance;
    }

    AccessToken getCurrentAccessToken() {
        return this.currentAccessToken;
    }

    boolean loadCurrentAccessToken() {
        AccessToken accessToken = this.accessTokenCache.load();
        if (accessToken == null) {
            return false;
        }
        setCurrentAccessToken(accessToken, false);
        return true;
    }

    void setCurrentAccessToken(AccessToken currentAccessToken) {
        setCurrentAccessToken(currentAccessToken, true);
    }

    private void setCurrentAccessToken(AccessToken currentAccessToken, boolean saveToCache) {
        AccessToken oldAccessToken = this.currentAccessToken;
        this.currentAccessToken = currentAccessToken;
        this.tokenRefreshInProgress.set(false);
        this.lastAttemptedTokenExtendDate = new Date(0);
        if (saveToCache) {
            if (currentAccessToken != null) {
                this.accessTokenCache.save(currentAccessToken);
            } else {
                this.accessTokenCache.clear();
                Utility.clearFacebookCookies(FacebookSdk.getApplicationContext());
            }
        }
        if (!Utility.areObjectsEqual(oldAccessToken, currentAccessToken)) {
            sendCurrentAccessTokenChangedBroadcast(oldAccessToken, currentAccessToken);
        }
    }

    private void sendCurrentAccessTokenChangedBroadcast(AccessToken oldAccessToken, AccessToken currentAccessToken) {
        Intent intent = new Intent(ACTION_CURRENT_ACCESS_TOKEN_CHANGED);
        intent.putExtra(EXTRA_OLD_ACCESS_TOKEN, oldAccessToken);
        intent.putExtra(EXTRA_NEW_ACCESS_TOKEN, currentAccessToken);
        this.localBroadcastManager.sendBroadcast(intent);
    }

    void extendAccessTokenIfNeeded() {
        if (shouldExtendAccessToken()) {
            refreshCurrentAccessToken(null);
        }
    }

    private boolean shouldExtendAccessToken() {
        if (this.currentAccessToken == null) {
            return false;
        }
        Long now = Long.valueOf(new Date().getTime());
        if (!this.currentAccessToken.getSource().canExtendToken() || now.longValue() - this.lastAttemptedTokenExtendDate.getTime() <= 3600000 || now.longValue() - this.currentAccessToken.getLastRefresh().getTime() <= 86400000) {
            return false;
        }
        return true;
    }

    private static GraphRequest createGrantedPermissionsRequest(AccessToken accessToken, Callback callback) {
        return new GraphRequest(accessToken, ME_PERMISSIONS_GRAPH_PATH, new Bundle(), HttpMethod.GET, callback);
    }

    private static GraphRequest createExtendAccessTokenRequest(AccessToken accessToken, Callback callback) {
        Bundle parameters = new Bundle();
        parameters.putString("grant_type", "fb_extend_sso_token");
        return new GraphRequest(accessToken, TOKEN_EXTEND_GRAPH_PATH, parameters, HttpMethod.GET, callback);
    }

    void refreshCurrentAccessToken(AccessTokenRefreshCallback callback) {
        if (Looper.getMainLooper().equals(Looper.myLooper())) {
            refreshCurrentAccessTokenImpl(callback);
        } else {
            new Handler(Looper.getMainLooper()).post(new C03631(callback));
        }
    }

    private void refreshCurrentAccessTokenImpl(AccessTokenRefreshCallback callback) {
        AccessToken accessToken = this.currentAccessToken;
        if (accessToken == null) {
            if (callback != null) {
                callback.OnTokenRefreshFailed(new FacebookException("No current access token to refresh"));
            }
        } else if (this.tokenRefreshInProgress.compareAndSet(false, true)) {
            this.lastAttemptedTokenExtendDate = new Date();
            Set<String> permissions = new HashSet();
            Set<String> declinedPermissions = new HashSet();
            GraphRequestBatch batch = new GraphRequestBatch(createGrantedPermissionsRequest(accessToken, new C09792(new AtomicBoolean(false), permissions, declinedPermissions)), createExtendAccessTokenRequest(accessToken, new C09803(new RefreshResult())));
            batch.addCallback(new C09814(accessToken, callback, permissionsCallSucceeded, refreshResult, permissions, declinedPermissions));
            batch.executeAsync();
        } else if (callback != null) {
            callback.OnTokenRefreshFailed(new FacebookException("Refresh already in progress"));
        }
    }
}
