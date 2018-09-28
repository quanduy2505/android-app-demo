package com.facebook;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import com.facebook.internal.NativeProtocol;
import com.facebook.internal.ServerProtocol;
import com.facebook.internal.Utility;
import com.facebook.share.internal.ShareConstants;
import com.google.android.gms.common.Scopes;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import rx.android.BuildConfig;

public class TestUserManager {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static final String LOG_TAG = "TestUserManager";
    private Map<String, JSONObject> appTestAccounts;
    private String testApplicationId;
    private String testApplicationSecret;

    private enum Mode {
        PRIVATE,
        SHARED
    }

    static {
        $assertionsDisabled = !TestUserManager.class.desiredAssertionStatus() ? true : $assertionsDisabled;
    }

    public TestUserManager(String testApplicationSecret, String testApplicationId) {
        if (Utility.isNullOrEmpty(testApplicationId) || Utility.isNullOrEmpty(testApplicationSecret)) {
            throw new FacebookException("Must provide app ID and secret");
        }
        this.testApplicationSecret = testApplicationSecret;
        this.testApplicationId = testApplicationId;
    }

    public AccessToken getAccessTokenForPrivateUser(List<String> permissions) {
        return getAccessTokenForUser(permissions, Mode.PRIVATE, null);
    }

    public AccessToken getAccessTokenForSharedUser(List<String> permissions) {
        return getAccessTokenForSharedUser(permissions, null);
    }

    public AccessToken getAccessTokenForSharedUser(List<String> permissions, String uniqueUserTag) {
        return getAccessTokenForUser(permissions, Mode.SHARED, uniqueUserTag);
    }

    public synchronized String getTestApplicationId() {
        return this.testApplicationId;
    }

    public synchronized String getTestApplicationSecret() {
        return this.testApplicationSecret;
    }

    private AccessToken getAccessTokenForUser(List<String> permissions, Mode mode, String uniqueUserTag) {
        JSONObject testAccount;
        retrieveTestAccountsForAppIfNeeded();
        if (Utility.isNullOrEmpty((Collection) permissions)) {
            permissions = Arrays.asList(new String[]{Scopes.EMAIL, "publish_actions"});
        }
        if (mode == Mode.PRIVATE) {
            testAccount = createTestAccount(permissions, mode, uniqueUserTag);
        } else {
            testAccount = findOrCreateSharedTestAccount(permissions, mode, uniqueUserTag);
        }
        return new AccessToken(testAccount.optString(ServerProtocol.DIALOG_PARAM_ACCESS_TOKEN), this.testApplicationId, testAccount.optString(ShareConstants.WEB_DIALOG_PARAM_ID), permissions, null, AccessTokenSource.TEST_USER, null, null);
    }

    private synchronized void retrieveTestAccountsForAppIfNeeded() {
        if (this.appTestAccounts == null) {
            this.appTestAccounts = new HashMap();
            GraphRequest.setDefaultBatchApplicationId(this.testApplicationId);
            Bundle parameters = new Bundle();
            parameters.putString(ServerProtocol.DIALOG_PARAM_ACCESS_TOKEN, getAppAccessToken());
            GraphRequest requestTestUsers = new GraphRequest(null, "app/accounts/test-users", parameters, null);
            requestTestUsers.setBatchEntryName("testUsers");
            requestTestUsers.setBatchEntryOmitResultOnSuccess($assertionsDisabled);
            Bundle testUserNamesParam = new Bundle();
            testUserNamesParam.putString(ServerProtocol.DIALOG_PARAM_ACCESS_TOKEN, getAppAccessToken());
            testUserNamesParam.putString("ids", "{result=testUsers:$.data.*.id}");
            testUserNamesParam.putString(GraphRequest.FIELDS_PARAM, ShareConstants.WEB_DIALOG_PARAM_NAME);
            new GraphRequest(null, BuildConfig.VERSION_NAME, testUserNamesParam, null).setBatchEntryDependsOn("testUsers");
            List<GraphResponse> responses = GraphRequest.executeBatchAndWait(requestTestUsers, requestTestUserNames);
            if (responses == null || responses.size() != 2) {
                throw new FacebookException("Unexpected number of results from TestUsers batch query");
            }
            populateTestAccounts(((GraphResponse) responses.get(0)).getJSONObject().optJSONArray(ShareConstants.WEB_DIALOG_PARAM_DATA), ((GraphResponse) responses.get(1)).getJSONObject());
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private synchronized void populateTestAccounts(org.json.JSONArray r7, org.json.JSONObject r8) {
        /*
        r6 = this;
        monitor-enter(r6);
        r1 = 0;
    L_0x0002:
        r4 = r7.length();	 Catch:{ all -> 0x0030 }
        if (r1 >= r4) goto L_0x0033;
    L_0x0008:
        r2 = r7.optJSONObject(r1);	 Catch:{ all -> 0x0030 }
        r4 = "id";
        r4 = r2.optString(r4);	 Catch:{ all -> 0x0030 }
        r3 = r8.optJSONObject(r4);	 Catch:{ all -> 0x0030 }
        r4 = "name";
        r5 = "name";
        r5 = r3.optString(r5);	 Catch:{ JSONException -> 0x0027 }
        r2.put(r4, r5);	 Catch:{ JSONException -> 0x0027 }
    L_0x0021:
        r6.storeTestAccount(r2);	 Catch:{ all -> 0x0030 }
        r1 = r1 + 1;
        goto L_0x0002;
    L_0x0027:
        r0 = move-exception;
        r4 = "TestUserManager";
        r5 = "Could not set name";
        android.util.Log.e(r4, r5, r0);	 Catch:{ all -> 0x0030 }
        goto L_0x0021;
    L_0x0030:
        r4 = move-exception;
        monitor-exit(r6);
        throw r4;
    L_0x0033:
        monitor-exit(r6);
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.facebook.TestUserManager.populateTestAccounts(org.json.JSONArray, org.json.JSONObject):void");
    }

    private synchronized void storeTestAccount(JSONObject testAccount) {
        this.appTestAccounts.put(testAccount.optString(ShareConstants.WEB_DIALOG_PARAM_ID), testAccount);
    }

    private synchronized JSONObject findTestAccountMatchingIdentifier(String identifier) {
        JSONObject testAccount;
        for (JSONObject testAccount2 : this.appTestAccounts.values()) {
            if (testAccount2.optString(ShareConstants.WEB_DIALOG_PARAM_NAME).contains(identifier)) {
                break;
            }
        }
        testAccount2 = null;
        return testAccount2;
    }

    final String getAppAccessToken() {
        return this.testApplicationId + "|" + this.testApplicationSecret;
    }

    private JSONObject findOrCreateSharedTestAccount(List<String> permissions, Mode mode, String uniqueUserTag) {
        JSONObject testAccount = findTestAccountMatchingIdentifier(getSharedTestAccountIdentifier(permissions, uniqueUserTag));
        return testAccount != null ? testAccount : createTestAccount(permissions, mode, uniqueUserTag);
    }

    private String getSharedTestAccountIdentifier(List<String> permissions, String uniqueUserTag) {
        return validNameStringFromInteger((((long) getPermissionsString(permissions).hashCode()) & 4294967295L) ^ (uniqueUserTag != null ? ((long) uniqueUserTag.hashCode()) & 4294967295L : 0));
    }

    private String validNameStringFromInteger(long i) {
        String s = Long.toString(i);
        StringBuilder result = new StringBuilder("Perm");
        char lastChar = '\u0000';
        for (char c : s.toCharArray()) {
            char c2;
            if (c2 == lastChar) {
                c2 = (char) (c2 + 10);
            }
            result.append((char) ((c2 + 97) - 48));
            lastChar = c2;
        }
        return result.toString();
    }

    private JSONObject createTestAccount(List<String> permissions, Mode mode, String uniqueUserTag) {
        Bundle parameters = new Bundle();
        parameters.putString("installed", ServerProtocol.DIALOG_RETURN_SCOPES_TRUE);
        parameters.putString(NativeProtocol.RESULT_ARGS_PERMISSIONS, getPermissionsString(permissions));
        parameters.putString(ServerProtocol.DIALOG_PARAM_ACCESS_TOKEN, getAppAccessToken());
        if (mode == Mode.SHARED) {
            parameters.putString(ShareConstants.WEB_DIALOG_PARAM_NAME, String.format("Shared %s Testuser", new Object[]{getSharedTestAccountIdentifier(permissions, uniqueUserTag)}));
        }
        GraphResponse response = new GraphRequest(null, String.format("%s/accounts/test-users", new Object[]{this.testApplicationId}), parameters, HttpMethod.POST).executeAndWait();
        FacebookRequestError error = response.getError();
        JSONObject testAccount = response.getJSONObject();
        if (error != null) {
            return null;
        }
        if (!$assertionsDisabled && testAccount == null) {
            throw new AssertionError();
        } else if (mode != Mode.SHARED) {
            return testAccount;
        } else {
            try {
                testAccount.put(ShareConstants.WEB_DIALOG_PARAM_NAME, parameters.getString(ShareConstants.WEB_DIALOG_PARAM_NAME));
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Could not set name", e);
            }
            storeTestAccount(testAccount);
            return testAccount;
        }
    }

    private String getPermissionsString(List<String> permissions) {
        return TextUtils.join(",", permissions);
    }
}
