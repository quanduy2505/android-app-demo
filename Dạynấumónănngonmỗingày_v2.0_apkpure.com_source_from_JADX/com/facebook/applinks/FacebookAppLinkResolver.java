package com.facebook.applinks;

import android.net.Uri;
import android.os.Bundle;
import bolts.AppLink;
import bolts.AppLink.Target;
import bolts.AppLinkResolver;
import bolts.Continuation;
import bolts.Task;
import bolts.Task.TaskCompletionSource;
import com.facebook.AccessToken;
import com.facebook.FacebookRequestError;
import com.facebook.GraphRequest;
import com.facebook.GraphRequest.Callback;
import com.facebook.GraphResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import rx.android.BuildConfig;

public class FacebookAppLinkResolver implements AppLinkResolver {
    private static final String APP_LINK_ANDROID_TARGET_KEY = "android";
    private static final String APP_LINK_KEY = "app_links";
    private static final String APP_LINK_TARGET_APP_NAME_KEY = "app_name";
    private static final String APP_LINK_TARGET_CLASS_KEY = "class";
    private static final String APP_LINK_TARGET_PACKAGE_KEY = "package";
    private static final String APP_LINK_TARGET_SHOULD_FALLBACK_KEY = "should_fallback";
    private static final String APP_LINK_TARGET_URL_KEY = "url";
    private static final String APP_LINK_WEB_TARGET_KEY = "web";
    private final HashMap<Uri, AppLink> cachedAppLinks;

    /* renamed from: com.facebook.applinks.FacebookAppLinkResolver.1 */
    class C09901 implements Continuation<Map<Uri, AppLink>, AppLink> {
        final /* synthetic */ Uri val$uri;

        C09901(Uri uri) {
            this.val$uri = uri;
        }

        public AppLink then(Task<Map<Uri, AppLink>> resolveUrisTask) throws Exception {
            return (AppLink) ((Map) resolveUrisTask.getResult()).get(this.val$uri);
        }
    }

    /* renamed from: com.facebook.applinks.FacebookAppLinkResolver.2 */
    class C09912 implements Callback {
        final /* synthetic */ Map val$appLinkResults;
        final /* synthetic */ TaskCompletionSource val$taskCompletionSource;
        final /* synthetic */ HashSet val$urisToRequest;

        C09912(TaskCompletionSource taskCompletionSource, Map map, HashSet hashSet) {
            this.val$taskCompletionSource = taskCompletionSource;
            this.val$appLinkResults = map;
            this.val$urisToRequest = hashSet;
        }

        public void onCompleted(GraphResponse response) {
            FacebookRequestError error = response.getError();
            if (error != null) {
                this.val$taskCompletionSource.setError(error.getException());
                return;
            }
            JSONObject responseJson = response.getJSONObject();
            if (responseJson == null) {
                this.val$taskCompletionSource.setResult(this.val$appLinkResults);
                return;
            }
            Iterator it = this.val$urisToRequest.iterator();
            while (it.hasNext()) {
                Uri uri = (Uri) it.next();
                if (responseJson.has(uri.toString())) {
                    try {
                        JSONObject appLinkData = responseJson.getJSONObject(uri.toString()).getJSONObject(FacebookAppLinkResolver.APP_LINK_KEY);
                        JSONArray rawTargets = appLinkData.getJSONArray(FacebookAppLinkResolver.APP_LINK_ANDROID_TARGET_KEY);
                        int targetsCount = rawTargets.length();
                        List<Target> targets = new ArrayList(targetsCount);
                        for (int i = 0; i < targetsCount; i++) {
                            Target target = FacebookAppLinkResolver.getAndroidTargetFromJson(rawTargets.getJSONObject(i));
                            if (target != null) {
                                targets.add(target);
                            }
                        }
                        AppLink appLink = new AppLink(uri, targets, FacebookAppLinkResolver.getWebFallbackUriFromJson(uri, appLinkData));
                        this.val$appLinkResults.put(uri, appLink);
                        synchronized (FacebookAppLinkResolver.this.cachedAppLinks) {
                            FacebookAppLinkResolver.this.cachedAppLinks.put(uri, appLink);
                        }
                    } catch (JSONException e) {
                    }
                }
            }
            this.val$taskCompletionSource.setResult(this.val$appLinkResults);
        }
    }

    public FacebookAppLinkResolver() {
        this.cachedAppLinks = new HashMap();
    }

    public Task<AppLink> getAppLinkFromUrlInBackground(Uri uri) {
        ArrayList<Uri> uris = new ArrayList();
        uris.add(uri);
        return getAppLinkFromUrlsInBackground(uris).onSuccess(new C09901(uri));
    }

    public Task<Map<Uri, AppLink>> getAppLinkFromUrlsInBackground(List<Uri> uris) {
        Map<Uri, AppLink> appLinkResults = new HashMap();
        HashSet<Uri> urisToRequest = new HashSet();
        StringBuilder graphRequestFields = new StringBuilder();
        for (Uri uri : uris) {
            synchronized (this.cachedAppLinks) {
                AppLink appLink = (AppLink) this.cachedAppLinks.get(uri);
            }
            if (appLink != null) {
                appLinkResults.put(uri, appLink);
            } else {
                if (!urisToRequest.isEmpty()) {
                    graphRequestFields.append(',');
                }
                graphRequestFields.append(uri.toString());
                urisToRequest.add(uri);
            }
        }
        if (urisToRequest.isEmpty()) {
            return Task.forResult(appLinkResults);
        }
        TaskCompletionSource taskCompletionSource = Task.create();
        Bundle appLinkRequestParameters = new Bundle();
        appLinkRequestParameters.putString("ids", graphRequestFields.toString());
        appLinkRequestParameters.putString(GraphRequest.FIELDS_PARAM, String.format("%s.fields(%s,%s)", new Object[]{APP_LINK_KEY, APP_LINK_ANDROID_TARGET_KEY, APP_LINK_WEB_TARGET_KEY}));
        new GraphRequest(AccessToken.getCurrentAccessToken(), BuildConfig.VERSION_NAME, appLinkRequestParameters, null, new C09912(taskCompletionSource, appLinkResults, urisToRequest)).executeAsync();
        return taskCompletionSource.getTask();
    }

    private static Target getAndroidTargetFromJson(JSONObject targetJson) {
        String packageName = tryGetStringFromJson(targetJson, APP_LINK_TARGET_PACKAGE_KEY, null);
        if (packageName == null) {
            return null;
        }
        String className = tryGetStringFromJson(targetJson, APP_LINK_TARGET_CLASS_KEY, null);
        String appName = tryGetStringFromJson(targetJson, APP_LINK_TARGET_APP_NAME_KEY, null);
        String targetUrlString = tryGetStringFromJson(targetJson, APP_LINK_TARGET_URL_KEY, null);
        Uri targetUri = null;
        if (targetUrlString != null) {
            targetUri = Uri.parse(targetUrlString);
        }
        return new Target(packageName, className, targetUri, appName);
    }

    private static Uri getWebFallbackUriFromJson(Uri sourceUrl, JSONObject urlData) {
        try {
            JSONObject webTarget = urlData.getJSONObject(APP_LINK_WEB_TARGET_KEY);
            if (!tryGetBooleanFromJson(webTarget, APP_LINK_TARGET_SHOULD_FALLBACK_KEY, true)) {
                return null;
            }
            String webTargetUrlString = tryGetStringFromJson(webTarget, APP_LINK_TARGET_URL_KEY, null);
            Uri webUri = null;
            if (webTargetUrlString != null) {
                webUri = Uri.parse(webTargetUrlString);
            }
            if (webUri == null) {
                return sourceUrl;
            }
            return webUri;
        } catch (JSONException e) {
            return sourceUrl;
        }
    }

    private static String tryGetStringFromJson(JSONObject json, String propertyName, String defaultValue) {
        try {
            defaultValue = json.getString(propertyName);
        } catch (JSONException e) {
        }
        return defaultValue;
    }

    private static boolean tryGetBooleanFromJson(JSONObject json, String propertyName, boolean defaultValue) {
        try {
            defaultValue = json.getBoolean(propertyName);
        } catch (JSONException e) {
        }
        return defaultValue;
    }
}
