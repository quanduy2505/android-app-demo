package com.facebook.share.internal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.FacebookRequestError;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestBatch;
import com.facebook.GraphRequestBatch.Callback;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.LoggingBehavior;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.internal.AnalyticsEvents;
import com.facebook.internal.AppCall;
import com.facebook.internal.BundleJSONConverter;
import com.facebook.internal.CallbackManagerImpl;
import com.facebook.internal.CallbackManagerImpl.RequestCodeOffset;
import com.facebook.internal.FileLruCache;
import com.facebook.internal.FileLruCache.Limits;
import com.facebook.internal.FragmentWrapper;
import com.facebook.internal.Logger;
import com.facebook.internal.NativeProtocol;
import com.facebook.internal.PlatformServiceClient.CompletedListener;
import com.facebook.internal.Utility;
import com.facebook.internal.WorkQueue;
import com.facebook.share.internal.LikeContent.Builder;
import com.facebook.share.widget.LikeView.ObjectType;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import rx.android.BuildConfig;
import rx.internal.operators.OnSubscribeConcatMap;

public class LikeActionController {
    public static final String ACTION_LIKE_ACTION_CONTROLLER_DID_ERROR = "com.facebook.sdk.LikeActionController.DID_ERROR";
    public static final String ACTION_LIKE_ACTION_CONTROLLER_DID_RESET = "com.facebook.sdk.LikeActionController.DID_RESET";
    public static final String ACTION_LIKE_ACTION_CONTROLLER_UPDATED = "com.facebook.sdk.LikeActionController.UPDATED";
    public static final String ACTION_OBJECT_ID_KEY = "com.facebook.sdk.LikeActionController.OBJECT_ID";
    private static final int ERROR_CODE_OBJECT_ALREADY_LIKED = 3501;
    public static final String ERROR_INVALID_OBJECT_ID = "Invalid Object Id";
    public static final String ERROR_PUBLISH_ERROR = "Unable to publish the like/unlike action";
    private static final String JSON_BOOL_IS_OBJECT_LIKED_KEY = "is_object_liked";
    private static final String JSON_BUNDLE_FACEBOOK_DIALOG_ANALYTICS_BUNDLE = "facebook_dialog_analytics_bundle";
    private static final String JSON_INT_OBJECT_TYPE_KEY = "object_type";
    private static final String JSON_INT_VERSION_KEY = "com.facebook.share.internal.LikeActionController.version";
    private static final String JSON_STRING_LIKE_COUNT_WITHOUT_LIKE_KEY = "like_count_string_without_like";
    private static final String JSON_STRING_LIKE_COUNT_WITH_LIKE_KEY = "like_count_string_with_like";
    private static final String JSON_STRING_OBJECT_ID_KEY = "object_id";
    private static final String JSON_STRING_SOCIAL_SENTENCE_WITHOUT_LIKE_KEY = "social_sentence_without_like";
    private static final String JSON_STRING_SOCIAL_SENTENCE_WITH_LIKE_KEY = "social_sentence_with_like";
    private static final String JSON_STRING_UNLIKE_TOKEN_KEY = "unlike_token";
    private static final String LIKE_ACTION_CONTROLLER_STORE = "com.facebook.LikeActionController.CONTROLLER_STORE_KEY";
    private static final String LIKE_ACTION_CONTROLLER_STORE_OBJECT_SUFFIX_KEY = "OBJECT_SUFFIX";
    private static final String LIKE_ACTION_CONTROLLER_STORE_PENDING_OBJECT_ID_KEY = "PENDING_CONTROLLER_KEY";
    private static final int LIKE_ACTION_CONTROLLER_VERSION = 3;
    private static final String LIKE_DIALOG_RESPONSE_LIKE_COUNT_STRING_KEY = "like_count_string";
    private static final String LIKE_DIALOG_RESPONSE_OBJECT_IS_LIKED_KEY = "object_is_liked";
    private static final String LIKE_DIALOG_RESPONSE_SOCIAL_SENTENCE_KEY = "social_sentence";
    private static final String LIKE_DIALOG_RESPONSE_UNLIKE_TOKEN_KEY = "unlike_token";
    private static final int MAX_CACHE_SIZE = 128;
    private static final int MAX_OBJECT_SUFFIX = 1000;
    private static final String TAG;
    private static AccessTokenTracker accessTokenTracker;
    private static final ConcurrentHashMap<String, LikeActionController> cache;
    private static FileLruCache controllerDiskCache;
    private static WorkQueue diskIOWorkQueue;
    private static Handler handler;
    private static boolean isInitialized;
    private static WorkQueue mruCacheWorkQueue;
    private static String objectIdForPendingController;
    private static volatile int objectSuffix;
    private AppEventsLogger appEventsLogger;
    private Bundle facebookDialogAnalyticsBundle;
    private boolean isObjectLiked;
    private boolean isObjectLikedOnServer;
    private boolean isPendingLikeOrUnlike;
    private String likeCountStringWithLike;
    private String likeCountStringWithoutLike;
    private String objectId;
    private boolean objectIsPage;
    private ObjectType objectType;
    private String socialSentenceWithLike;
    private String socialSentenceWithoutLike;
    private String unlikeToken;
    private String verifiedObjectId;

    /* renamed from: com.facebook.share.internal.LikeActionController.12 */
    static /* synthetic */ class AnonymousClass12 {
        static final /* synthetic */ int[] $SwitchMap$com$facebook$share$widget$LikeView$ObjectType;

        static {
            $SwitchMap$com$facebook$share$widget$LikeView$ObjectType = new int[ObjectType.values().length];
            try {
                $SwitchMap$com$facebook$share$widget$LikeView$ObjectType[ObjectType.PAGE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
        }
    }

    /* renamed from: com.facebook.share.internal.LikeActionController.2 */
    static class C04542 implements Runnable {
        final /* synthetic */ LikeActionController val$controllerToRefresh;

        C04542(LikeActionController likeActionController) {
            this.val$controllerToRefresh = likeActionController;
        }

        public void run() {
            this.val$controllerToRefresh.refreshStatusAsync();
        }
    }

    /* renamed from: com.facebook.share.internal.LikeActionController.4 */
    static class C04554 implements Runnable {
        final /* synthetic */ CreationCallback val$callback;
        final /* synthetic */ LikeActionController val$controller;
        final /* synthetic */ FacebookException val$error;

        C04554(CreationCallback creationCallback, LikeActionController likeActionController, FacebookException facebookException) {
            this.val$callback = creationCallback;
            this.val$controller = likeActionController;
            this.val$error = facebookException;
        }

        public void run() {
            this.val$callback.onComplete(this.val$controller, this.val$error);
        }
    }

    private static class CreateLikeActionControllerWorkItem implements Runnable {
        private CreationCallback callback;
        private String objectId;
        private ObjectType objectType;

        CreateLikeActionControllerWorkItem(String objectId, ObjectType objectType, CreationCallback callback) {
            this.objectId = objectId;
            this.objectType = objectType;
            this.callback = callback;
        }

        public void run() {
            LikeActionController.createControllerForObjectIdAndType(this.objectId, this.objectType, this.callback);
        }
    }

    public interface CreationCallback {
        void onComplete(LikeActionController likeActionController, FacebookException facebookException);
    }

    private static class MRUCacheWorkItem implements Runnable {
        private static ArrayList<String> mruCachedItems;
        private String cacheItem;
        private boolean shouldTrim;

        static {
            mruCachedItems = new ArrayList();
        }

        MRUCacheWorkItem(String cacheItem, boolean shouldTrim) {
            this.cacheItem = cacheItem;
            this.shouldTrim = shouldTrim;
        }

        public void run() {
            if (this.cacheItem != null) {
                mruCachedItems.remove(this.cacheItem);
                mruCachedItems.add(0, this.cacheItem);
            }
            if (this.shouldTrim && mruCachedItems.size() >= LikeActionController.MAX_CACHE_SIZE) {
                while (64 < mruCachedItems.size()) {
                    LikeActionController.cache.remove((String) mruCachedItems.remove(mruCachedItems.size() - 1));
                }
            }
        }
    }

    private interface RequestCompletionCallback {
        void onComplete();
    }

    private interface RequestWrapper {
        void addToBatch(GraphRequestBatch graphRequestBatch);

        FacebookRequestError getError();
    }

    private static class SerializeToDiskWorkItem implements Runnable {
        private String cacheKey;
        private String controllerJson;

        SerializeToDiskWorkItem(String cacheKey, String controllerJson) {
            this.cacheKey = cacheKey;
            this.controllerJson = controllerJson;
        }

        public void run() {
            LikeActionController.serializeToDiskSynchronously(this.cacheKey, this.controllerJson);
        }
    }

    /* renamed from: com.facebook.share.internal.LikeActionController.11 */
    class AnonymousClass11 implements Callback {
        final /* synthetic */ RequestCompletionCallback val$completionHandler;
        final /* synthetic */ GetOGObjectIdRequestWrapper val$objectIdRequest;
        final /* synthetic */ GetPageIdRequestWrapper val$pageIdRequest;

        AnonymousClass11(GetOGObjectIdRequestWrapper getOGObjectIdRequestWrapper, GetPageIdRequestWrapper getPageIdRequestWrapper, RequestCompletionCallback requestCompletionCallback) {
            this.val$objectIdRequest = getOGObjectIdRequestWrapper;
            this.val$pageIdRequest = getPageIdRequestWrapper;
            this.val$completionHandler = requestCompletionCallback;
        }

        public void onBatchCompleted(GraphRequestBatch batch) {
            LikeActionController.this.verifiedObjectId = this.val$objectIdRequest.verifiedObjectId;
            if (Utility.isNullOrEmpty(LikeActionController.this.verifiedObjectId)) {
                LikeActionController.this.verifiedObjectId = this.val$pageIdRequest.verifiedObjectId;
                LikeActionController.this.objectIsPage = this.val$pageIdRequest.objectIsPage;
            }
            if (Utility.isNullOrEmpty(LikeActionController.this.verifiedObjectId)) {
                FacebookRequestError error;
                Logger.log(LoggingBehavior.DEVELOPER_ERRORS, LikeActionController.TAG, "Unable to verify the FB id for '%s'. Verify that it is a valid FB object or page", LikeActionController.this.objectId);
                LikeActionController likeActionController = LikeActionController.this;
                String str = "get_verified_id";
                if (this.val$pageIdRequest.getError() != null) {
                    error = this.val$pageIdRequest.getError();
                } else {
                    error = this.val$objectIdRequest.getError();
                }
                likeActionController.logAppEventForError(str, error);
            }
            if (this.val$completionHandler != null) {
                this.val$completionHandler.onComplete();
            }
        }
    }

    /* renamed from: com.facebook.share.internal.LikeActionController.1 */
    static class C10251 implements CreationCallback {
        final /* synthetic */ Intent val$data;
        final /* synthetic */ int val$requestCode;
        final /* synthetic */ int val$resultCode;

        C10251(int i, int i2, Intent intent) {
            this.val$requestCode = i;
            this.val$resultCode = i2;
            this.val$data = intent;
        }

        public void onComplete(LikeActionController likeActionController, FacebookException error) {
            if (error == null) {
                likeActionController.onActivityResult(this.val$requestCode, this.val$resultCode, this.val$data);
            } else {
                Utility.logd(LikeActionController.TAG, (Exception) error);
            }
        }
    }

    /* renamed from: com.facebook.share.internal.LikeActionController.3 */
    static class C10263 implements CallbackManagerImpl.Callback {
        C10263() {
        }

        public boolean onActivityResult(int resultCode, Intent data) {
            return LikeActionController.handleOnActivityResult(RequestCodeOffset.Like.toRequestCode(), resultCode, data);
        }
    }

    /* renamed from: com.facebook.share.internal.LikeActionController.5 */
    static class C10275 extends AccessTokenTracker {
        C10275() {
        }

        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            Context appContext = FacebookSdk.getApplicationContext();
            if (currentAccessToken == null) {
                LikeActionController.objectSuffix = (LikeActionController.objectSuffix + 1) % LikeActionController.MAX_OBJECT_SUFFIX;
                appContext.getSharedPreferences(LikeActionController.LIKE_ACTION_CONTROLLER_STORE, 0).edit().putInt(LikeActionController.LIKE_ACTION_CONTROLLER_STORE_OBJECT_SUFFIX_KEY, LikeActionController.objectSuffix).apply();
                LikeActionController.cache.clear();
                LikeActionController.controllerDiskCache.clearCache();
            }
            LikeActionController.broadcastAction(null, LikeActionController.ACTION_LIKE_ACTION_CONTROLLER_DID_RESET);
        }
    }

    /* renamed from: com.facebook.share.internal.LikeActionController.6 */
    class C10286 extends ResultProcessor {
        final /* synthetic */ Bundle val$analyticsParameters;

        C10286(FacebookCallback callback, Bundle bundle) {
            this.val$analyticsParameters = bundle;
            super(callback);
        }

        public void onSuccess(AppCall appCall, Bundle data) {
            if (data != null && data.containsKey(LikeActionController.LIKE_DIALOG_RESPONSE_OBJECT_IS_LIKED_KEY)) {
                String unlikeToken;
                boolean isObjectLiked = data.getBoolean(LikeActionController.LIKE_DIALOG_RESPONSE_OBJECT_IS_LIKED_KEY);
                String likeCountStringWithLike = LikeActionController.this.likeCountStringWithLike;
                String likeCountStringWithoutLike = LikeActionController.this.likeCountStringWithoutLike;
                if (data.containsKey(LikeActionController.LIKE_DIALOG_RESPONSE_LIKE_COUNT_STRING_KEY)) {
                    likeCountStringWithLike = data.getString(LikeActionController.LIKE_DIALOG_RESPONSE_LIKE_COUNT_STRING_KEY);
                    likeCountStringWithoutLike = likeCountStringWithLike;
                }
                String socialSentenceWithLike = LikeActionController.this.socialSentenceWithLike;
                String socialSentenceWithoutWithoutLike = LikeActionController.this.socialSentenceWithoutLike;
                if (data.containsKey(LikeActionController.LIKE_DIALOG_RESPONSE_SOCIAL_SENTENCE_KEY)) {
                    socialSentenceWithLike = data.getString(LikeActionController.LIKE_DIALOG_RESPONSE_SOCIAL_SENTENCE_KEY);
                    socialSentenceWithoutWithoutLike = socialSentenceWithLike;
                }
                if (data.containsKey(LikeActionController.LIKE_DIALOG_RESPONSE_OBJECT_IS_LIKED_KEY)) {
                    unlikeToken = data.getString(LikeActionController.LIKE_DIALOG_RESPONSE_UNLIKE_TOKEN_KEY);
                } else {
                    unlikeToken = LikeActionController.this.unlikeToken;
                }
                Bundle logParams = this.val$analyticsParameters == null ? new Bundle() : this.val$analyticsParameters;
                logParams.putString(AnalyticsEvents.PARAMETER_CALL_ID, appCall.getCallId().toString());
                LikeActionController.this.getAppEventsLogger().logSdkEvent(AnalyticsEvents.EVENT_LIKE_VIEW_DIALOG_DID_SUCCEED, null, logParams);
                LikeActionController.this.updateState(isObjectLiked, likeCountStringWithLike, likeCountStringWithoutLike, socialSentenceWithLike, socialSentenceWithoutWithoutLike, unlikeToken);
            }
        }

        public void onError(AppCall appCall, FacebookException error) {
            Logger.log(LoggingBehavior.REQUESTS, LikeActionController.TAG, "Like Dialog failed with error : %s", error);
            Bundle logParams = this.val$analyticsParameters == null ? new Bundle() : this.val$analyticsParameters;
            logParams.putString(AnalyticsEvents.PARAMETER_CALL_ID, appCall.getCallId().toString());
            LikeActionController.this.logAppEventForError("present_dialog", logParams);
            LikeActionController.broadcastAction(LikeActionController.this, LikeActionController.ACTION_LIKE_ACTION_CONTROLLER_DID_ERROR, NativeProtocol.createBundleForException(error));
        }

        public void onCancel(AppCall appCall) {
            onError(appCall, new FacebookOperationCanceledException());
        }
    }

    /* renamed from: com.facebook.share.internal.LikeActionController.7 */
    class C10307 implements RequestCompletionCallback {
        final /* synthetic */ Bundle val$analyticsParameters;

        /* renamed from: com.facebook.share.internal.LikeActionController.7.1 */
        class C10291 implements Callback {
            final /* synthetic */ PublishLikeRequestWrapper val$likeRequest;

            C10291(PublishLikeRequestWrapper publishLikeRequestWrapper) {
                this.val$likeRequest = publishLikeRequestWrapper;
            }

            public void onBatchCompleted(GraphRequestBatch batch) {
                LikeActionController.this.isPendingLikeOrUnlike = false;
                if (this.val$likeRequest.getError() != null) {
                    LikeActionController.this.publishDidError(false);
                    return;
                }
                LikeActionController.this.unlikeToken = Utility.coerceValueIfNullOrEmpty(this.val$likeRequest.unlikeToken, null);
                LikeActionController.this.isObjectLikedOnServer = true;
                LikeActionController.this.getAppEventsLogger().logSdkEvent(AnalyticsEvents.EVENT_LIKE_VIEW_DID_LIKE, null, C10307.this.val$analyticsParameters);
                LikeActionController.this.publishAgainIfNeeded(C10307.this.val$analyticsParameters);
            }
        }

        C10307(Bundle bundle) {
            this.val$analyticsParameters = bundle;
        }

        public void onComplete() {
            if (Utility.isNullOrEmpty(LikeActionController.this.verifiedObjectId)) {
                Bundle errorBundle = new Bundle();
                errorBundle.putString(NativeProtocol.STATUS_ERROR_DESCRIPTION, LikeActionController.ERROR_INVALID_OBJECT_ID);
                LikeActionController.broadcastAction(LikeActionController.this, LikeActionController.ACTION_LIKE_ACTION_CONTROLLER_DID_ERROR, errorBundle);
                return;
            }
            GraphRequestBatch requestBatch = new GraphRequestBatch();
            PublishLikeRequestWrapper likeRequest = new PublishLikeRequestWrapper(LikeActionController.this.verifiedObjectId, LikeActionController.this.objectType);
            likeRequest.addToBatch(requestBatch);
            requestBatch.addCallback(new C10291(likeRequest));
            requestBatch.executeAsync();
        }
    }

    /* renamed from: com.facebook.share.internal.LikeActionController.8 */
    class C10318 implements Callback {
        final /* synthetic */ Bundle val$analyticsParameters;
        final /* synthetic */ PublishUnlikeRequestWrapper val$unlikeRequest;

        C10318(PublishUnlikeRequestWrapper publishUnlikeRequestWrapper, Bundle bundle) {
            this.val$unlikeRequest = publishUnlikeRequestWrapper;
            this.val$analyticsParameters = bundle;
        }

        public void onBatchCompleted(GraphRequestBatch batch) {
            LikeActionController.this.isPendingLikeOrUnlike = false;
            if (this.val$unlikeRequest.getError() != null) {
                LikeActionController.this.publishDidError(true);
                return;
            }
            LikeActionController.this.unlikeToken = null;
            LikeActionController.this.isObjectLikedOnServer = false;
            LikeActionController.this.getAppEventsLogger().logSdkEvent(AnalyticsEvents.EVENT_LIKE_VIEW_DID_UNLIKE, null, this.val$analyticsParameters);
            LikeActionController.this.publishAgainIfNeeded(this.val$analyticsParameters);
        }
    }

    /* renamed from: com.facebook.share.internal.LikeActionController.9 */
    class C10339 implements RequestCompletionCallback {

        /* renamed from: com.facebook.share.internal.LikeActionController.9.1 */
        class C10321 implements Callback {
            final /* synthetic */ GetEngagementRequestWrapper val$engagementRequest;
            final /* synthetic */ LikeRequestWrapper val$likeRequestWrapper;

            C10321(LikeRequestWrapper likeRequestWrapper, GetEngagementRequestWrapper getEngagementRequestWrapper) {
                this.val$likeRequestWrapper = likeRequestWrapper;
                this.val$engagementRequest = getEngagementRequestWrapper;
            }

            public void onBatchCompleted(GraphRequestBatch batch) {
                if (this.val$likeRequestWrapper.getError() == null && this.val$engagementRequest.getError() == null) {
                    LikeActionController.this.updateState(this.val$likeRequestWrapper.isObjectLiked(), this.val$engagementRequest.likeCountStringWithLike, this.val$engagementRequest.likeCountStringWithoutLike, this.val$engagementRequest.socialSentenceStringWithLike, this.val$engagementRequest.socialSentenceStringWithoutLike, this.val$likeRequestWrapper.getUnlikeToken());
                    return;
                }
                Logger.log(LoggingBehavior.REQUESTS, LikeActionController.TAG, "Unable to refresh like state for id: '%s'", LikeActionController.this.objectId);
            }
        }

        C10339() {
        }

        public void onComplete() {
            LikeRequestWrapper likeRequestWrapper;
            switch (AnonymousClass12.$SwitchMap$com$facebook$share$widget$LikeView$ObjectType[LikeActionController.this.objectType.ordinal()]) {
                case OnSubscribeConcatMap.BOUNDARY /*1*/:
                    likeRequestWrapper = new GetPageLikesRequestWrapper(LikeActionController.this.verifiedObjectId);
                    break;
                default:
                    likeRequestWrapper = new GetOGObjectLikesRequestWrapper(LikeActionController.this.verifiedObjectId, LikeActionController.this.objectType);
                    break;
            }
            GetEngagementRequestWrapper engagementRequest = new GetEngagementRequestWrapper(LikeActionController.this.verifiedObjectId, LikeActionController.this.objectType);
            GraphRequestBatch requestBatch = new GraphRequestBatch();
            likeRequestWrapper.addToBatch(requestBatch);
            engagementRequest.addToBatch(requestBatch);
            requestBatch.addCallback(new C10321(likeRequestWrapper, engagementRequest));
            requestBatch.executeAsync();
        }
    }

    private abstract class AbstractRequestWrapper implements RequestWrapper {
        protected FacebookRequestError error;
        protected String objectId;
        protected ObjectType objectType;
        private GraphRequest request;

        /* renamed from: com.facebook.share.internal.LikeActionController.AbstractRequestWrapper.1 */
        class C10341 implements GraphRequest.Callback {
            C10341() {
            }

            public void onCompleted(GraphResponse response) {
                AbstractRequestWrapper.this.error = response.getError();
                if (AbstractRequestWrapper.this.error != null) {
                    AbstractRequestWrapper.this.processError(AbstractRequestWrapper.this.error);
                } else {
                    AbstractRequestWrapper.this.processSuccess(response);
                }
            }
        }

        protected abstract void processSuccess(GraphResponse graphResponse);

        protected AbstractRequestWrapper(String objectId, ObjectType objectType) {
            this.objectId = objectId;
            this.objectType = objectType;
        }

        public void addToBatch(GraphRequestBatch batch) {
            batch.add(this.request);
        }

        public FacebookRequestError getError() {
            return this.error;
        }

        protected void setRequest(GraphRequest request) {
            this.request = request;
            request.setVersion(FacebookSdk.getGraphApiVersion());
            request.setCallback(new C10341());
        }

        protected void processError(FacebookRequestError error) {
            Object[] objArr = new Object[LikeActionController.LIKE_ACTION_CONTROLLER_VERSION];
            objArr[0] = this.objectId;
            objArr[1] = this.objectType;
            objArr[2] = error;
            Logger.log(LoggingBehavior.REQUESTS, LikeActionController.TAG, "Error running request for object '%s' with type '%s' : %s", objArr);
        }
    }

    private interface LikeRequestWrapper extends RequestWrapper {
        String getUnlikeToken();

        boolean isObjectLiked();
    }

    private class GetEngagementRequestWrapper extends AbstractRequestWrapper {
        String likeCountStringWithLike;
        String likeCountStringWithoutLike;
        String socialSentenceStringWithLike;
        String socialSentenceStringWithoutLike;

        GetEngagementRequestWrapper(String objectId, ObjectType objectType) {
            super(objectId, objectType);
            this.likeCountStringWithLike = LikeActionController.this.likeCountStringWithLike;
            this.likeCountStringWithoutLike = LikeActionController.this.likeCountStringWithoutLike;
            this.socialSentenceStringWithLike = LikeActionController.this.socialSentenceWithLike;
            this.socialSentenceStringWithoutLike = LikeActionController.this.socialSentenceWithoutLike;
            Bundle requestParams = new Bundle();
            requestParams.putString(GraphRequest.FIELDS_PARAM, "engagement.fields(count_string_with_like,count_string_without_like,social_sentence_with_like,social_sentence_without_like)");
            requestParams.putString("locale", Locale.getDefault().toString());
            setRequest(new GraphRequest(AccessToken.getCurrentAccessToken(), objectId, requestParams, HttpMethod.GET));
        }

        protected void processSuccess(GraphResponse response) {
            JSONObject engagementResults = Utility.tryGetJSONObjectFromResponse(response.getJSONObject(), "engagement");
            if (engagementResults != null) {
                this.likeCountStringWithLike = engagementResults.optString("count_string_with_like", this.likeCountStringWithLike);
                this.likeCountStringWithoutLike = engagementResults.optString("count_string_without_like", this.likeCountStringWithoutLike);
                this.socialSentenceStringWithLike = engagementResults.optString(LikeActionController.JSON_STRING_SOCIAL_SENTENCE_WITH_LIKE_KEY, this.socialSentenceStringWithLike);
                this.socialSentenceStringWithoutLike = engagementResults.optString(LikeActionController.JSON_STRING_SOCIAL_SENTENCE_WITHOUT_LIKE_KEY, this.socialSentenceStringWithoutLike);
            }
        }

        protected void processError(FacebookRequestError error) {
            Object[] objArr = new Object[LikeActionController.LIKE_ACTION_CONTROLLER_VERSION];
            objArr[0] = this.objectId;
            objArr[1] = this.objectType;
            objArr[2] = error;
            Logger.log(LoggingBehavior.REQUESTS, LikeActionController.TAG, "Error fetching engagement for object '%s' with type '%s' : %s", objArr);
            LikeActionController.this.logAppEventForError("get_engagement", error);
        }
    }

    private class GetOGObjectIdRequestWrapper extends AbstractRequestWrapper {
        String verifiedObjectId;

        GetOGObjectIdRequestWrapper(String objectId, ObjectType objectType) {
            super(objectId, objectType);
            Bundle objectIdRequestParams = new Bundle();
            objectIdRequestParams.putString(GraphRequest.FIELDS_PARAM, "og_object.fields(id)");
            objectIdRequestParams.putString("ids", objectId);
            setRequest(new GraphRequest(AccessToken.getCurrentAccessToken(), BuildConfig.VERSION_NAME, objectIdRequestParams, HttpMethod.GET));
        }

        protected void processError(FacebookRequestError error) {
            if (error.getErrorMessage().contains("og_object")) {
                this.error = null;
                return;
            }
            Object[] objArr = new Object[LikeActionController.LIKE_ACTION_CONTROLLER_VERSION];
            objArr[0] = this.objectId;
            objArr[1] = this.objectType;
            objArr[2] = error;
            Logger.log(LoggingBehavior.REQUESTS, LikeActionController.TAG, "Error getting the FB id for object '%s' with type '%s' : %s", objArr);
        }

        protected void processSuccess(GraphResponse response) {
            JSONObject results = Utility.tryGetJSONObjectFromResponse(response.getJSONObject(), this.objectId);
            if (results != null) {
                JSONObject ogObject = results.optJSONObject("og_object");
                if (ogObject != null) {
                    this.verifiedObjectId = ogObject.optString(ShareConstants.WEB_DIALOG_PARAM_ID);
                }
            }
        }
    }

    private class GetOGObjectLikesRequestWrapper extends AbstractRequestWrapper implements LikeRequestWrapper {
        private final String objectId;
        private boolean objectIsLiked;
        private final ObjectType objectType;
        private String unlikeToken;

        GetOGObjectLikesRequestWrapper(String objectId, ObjectType objectType) {
            super(objectId, objectType);
            this.objectIsLiked = LikeActionController.this.isObjectLiked;
            this.objectId = objectId;
            this.objectType = objectType;
            Bundle requestParams = new Bundle();
            requestParams.putString(GraphRequest.FIELDS_PARAM, "id,application");
            requestParams.putString("object", this.objectId);
            setRequest(new GraphRequest(AccessToken.getCurrentAccessToken(), "me/og.likes", requestParams, HttpMethod.GET));
        }

        protected void processSuccess(GraphResponse response) {
            JSONArray dataSet = Utility.tryGetJSONArrayFromResponse(response.getJSONObject(), ShareConstants.WEB_DIALOG_PARAM_DATA);
            if (dataSet != null) {
                for (int i = 0; i < dataSet.length(); i++) {
                    JSONObject data = dataSet.optJSONObject(i);
                    if (data != null) {
                        this.objectIsLiked = true;
                        JSONObject appData = data.optJSONObject("application");
                        AccessToken accessToken = AccessToken.getCurrentAccessToken();
                        if (!(appData == null || accessToken == null || !Utility.areObjectsEqual(accessToken.getApplicationId(), appData.optString(ShareConstants.WEB_DIALOG_PARAM_ID)))) {
                            this.unlikeToken = data.optString(ShareConstants.WEB_DIALOG_PARAM_ID);
                        }
                    }
                }
            }
        }

        protected void processError(FacebookRequestError error) {
            Object[] objArr = new Object[LikeActionController.LIKE_ACTION_CONTROLLER_VERSION];
            objArr[0] = this.objectId;
            objArr[1] = this.objectType;
            objArr[2] = error;
            Logger.log(LoggingBehavior.REQUESTS, LikeActionController.TAG, "Error fetching like status for object '%s' with type '%s' : %s", objArr);
            LikeActionController.this.logAppEventForError("get_og_object_like", error);
        }

        public boolean isObjectLiked() {
            return this.objectIsLiked;
        }

        public String getUnlikeToken() {
            return this.unlikeToken;
        }
    }

    private class GetPageIdRequestWrapper extends AbstractRequestWrapper {
        boolean objectIsPage;
        String verifiedObjectId;

        GetPageIdRequestWrapper(String objectId, ObjectType objectType) {
            super(objectId, objectType);
            Bundle pageIdRequestParams = new Bundle();
            pageIdRequestParams.putString(GraphRequest.FIELDS_PARAM, ShareConstants.WEB_DIALOG_PARAM_ID);
            pageIdRequestParams.putString("ids", objectId);
            setRequest(new GraphRequest(AccessToken.getCurrentAccessToken(), BuildConfig.VERSION_NAME, pageIdRequestParams, HttpMethod.GET));
        }

        protected void processSuccess(GraphResponse response) {
            JSONObject results = Utility.tryGetJSONObjectFromResponse(response.getJSONObject(), this.objectId);
            if (results != null) {
                this.verifiedObjectId = results.optString(ShareConstants.WEB_DIALOG_PARAM_ID);
                this.objectIsPage = !Utility.isNullOrEmpty(this.verifiedObjectId);
            }
        }

        protected void processError(FacebookRequestError error) {
            Object[] objArr = new Object[LikeActionController.LIKE_ACTION_CONTROLLER_VERSION];
            objArr[0] = this.objectId;
            objArr[1] = this.objectType;
            objArr[2] = error;
            Logger.log(LoggingBehavior.REQUESTS, LikeActionController.TAG, "Error getting the FB id for object '%s' with type '%s' : %s", objArr);
        }
    }

    private class GetPageLikesRequestWrapper extends AbstractRequestWrapper implements LikeRequestWrapper {
        private boolean objectIsLiked;
        private String pageId;

        GetPageLikesRequestWrapper(String pageId) {
            super(pageId, ObjectType.PAGE);
            this.objectIsLiked = LikeActionController.this.isObjectLiked;
            this.pageId = pageId;
            Bundle requestParams = new Bundle();
            requestParams.putString(GraphRequest.FIELDS_PARAM, ShareConstants.WEB_DIALOG_PARAM_ID);
            setRequest(new GraphRequest(AccessToken.getCurrentAccessToken(), "me/likes/" + pageId, requestParams, HttpMethod.GET));
        }

        protected void processSuccess(GraphResponse response) {
            JSONArray dataSet = Utility.tryGetJSONArrayFromResponse(response.getJSONObject(), ShareConstants.WEB_DIALOG_PARAM_DATA);
            if (dataSet != null && dataSet.length() > 0) {
                this.objectIsLiked = true;
            }
        }

        protected void processError(FacebookRequestError error) {
            Logger.log(LoggingBehavior.REQUESTS, LikeActionController.TAG, "Error fetching like status for page id '%s': %s", this.pageId, error);
            LikeActionController.this.logAppEventForError("get_page_like", error);
        }

        public boolean isObjectLiked() {
            return this.objectIsLiked;
        }

        public String getUnlikeToken() {
            return null;
        }
    }

    private class PublishLikeRequestWrapper extends AbstractRequestWrapper {
        String unlikeToken;

        PublishLikeRequestWrapper(String objectId, ObjectType objectType) {
            super(objectId, objectType);
            Bundle likeRequestParams = new Bundle();
            likeRequestParams.putString("object", objectId);
            setRequest(new GraphRequest(AccessToken.getCurrentAccessToken(), "me/og.likes", likeRequestParams, HttpMethod.POST));
        }

        protected void processSuccess(GraphResponse response) {
            this.unlikeToken = Utility.safeGetStringFromResponse(response.getJSONObject(), ShareConstants.WEB_DIALOG_PARAM_ID);
        }

        protected void processError(FacebookRequestError error) {
            if (error.getErrorCode() == LikeActionController.ERROR_CODE_OBJECT_ALREADY_LIKED) {
                this.error = null;
                return;
            }
            Object[] objArr = new Object[LikeActionController.LIKE_ACTION_CONTROLLER_VERSION];
            objArr[0] = this.objectId;
            objArr[1] = this.objectType;
            objArr[2] = error;
            Logger.log(LoggingBehavior.REQUESTS, LikeActionController.TAG, "Error liking object '%s' with type '%s' : %s", objArr);
            LikeActionController.this.logAppEventForError("publish_like", error);
        }
    }

    private class PublishUnlikeRequestWrapper extends AbstractRequestWrapper {
        private String unlikeToken;

        PublishUnlikeRequestWrapper(String unlikeToken) {
            super(null, null);
            this.unlikeToken = unlikeToken;
            setRequest(new GraphRequest(AccessToken.getCurrentAccessToken(), unlikeToken, null, HttpMethod.DELETE));
        }

        protected void processSuccess(GraphResponse response) {
        }

        protected void processError(FacebookRequestError error) {
            Logger.log(LoggingBehavior.REQUESTS, LikeActionController.TAG, "Error unliking object with unlike token '%s' : %s", this.unlikeToken, error);
            LikeActionController.this.logAppEventForError("publish_unlike", error);
        }
    }

    static {
        TAG = LikeActionController.class.getSimpleName();
        cache = new ConcurrentHashMap();
        mruCacheWorkQueue = new WorkQueue(1);
        diskIOWorkQueue = new WorkQueue(1);
    }

    public static boolean handleOnActivityResult(int requestCode, int resultCode, Intent data) {
        if (Utility.isNullOrEmpty(objectIdForPendingController)) {
            objectIdForPendingController = FacebookSdk.getApplicationContext().getSharedPreferences(LIKE_ACTION_CONTROLLER_STORE, 0).getString(LIKE_ACTION_CONTROLLER_STORE_PENDING_OBJECT_ID_KEY, null);
        }
        if (Utility.isNullOrEmpty(objectIdForPendingController)) {
            return false;
        }
        getControllerForObjectId(objectIdForPendingController, ObjectType.UNKNOWN, new C10251(requestCode, resultCode, data));
        return true;
    }

    public static void getControllerForObjectId(String objectId, ObjectType objectType, CreationCallback callback) {
        if (!isInitialized) {
            performFirstInitialize();
        }
        LikeActionController controllerForObject = getControllerFromInMemoryCache(objectId);
        if (controllerForObject != null) {
            verifyControllerAndInvokeCallback(controllerForObject, objectType, callback);
        } else {
            diskIOWorkQueue.addActiveWorkItem(new CreateLikeActionControllerWorkItem(objectId, objectType, callback));
        }
    }

    private static void verifyControllerAndInvokeCallback(LikeActionController likeActionController, ObjectType objectType, CreationCallback callback) {
        ObjectType bestObjectType = ShareInternalUtility.getMostSpecificObjectType(objectType, likeActionController.objectType);
        FacebookException error = null;
        if (bestObjectType == null) {
            Object[] objArr = new Object[LIKE_ACTION_CONTROLLER_VERSION];
            objArr[0] = likeActionController.objectId;
            objArr[1] = likeActionController.objectType.toString();
            objArr[2] = objectType.toString();
            error = new FacebookException("Object with id:\"%s\" is already marked as type:\"%s\". Cannot change the type to:\"%s\"", objArr);
            likeActionController = null;
        } else {
            likeActionController.objectType = bestObjectType;
        }
        invokeCallbackWithController(callback, likeActionController, error);
    }

    private static void createControllerForObjectIdAndType(String objectId, ObjectType objectType, CreationCallback callback) {
        LikeActionController controllerForObject = getControllerFromInMemoryCache(objectId);
        if (controllerForObject != null) {
            verifyControllerAndInvokeCallback(controllerForObject, objectType, callback);
            return;
        }
        controllerForObject = deserializeFromDiskSynchronously(objectId);
        if (controllerForObject == null) {
            controllerForObject = new LikeActionController(objectId, objectType);
            serializeToDiskAsync(controllerForObject);
        }
        putControllerInMemoryCache(objectId, controllerForObject);
        LikeActionController controllerToRefresh = controllerForObject;
        handler.post(new C04542(controllerToRefresh));
        invokeCallbackWithController(callback, controllerToRefresh, null);
    }

    private static synchronized void performFirstInitialize() {
        synchronized (LikeActionController.class) {
            if (!isInitialized) {
                handler = new Handler(Looper.getMainLooper());
                objectSuffix = FacebookSdk.getApplicationContext().getSharedPreferences(LIKE_ACTION_CONTROLLER_STORE, 0).getInt(LIKE_ACTION_CONTROLLER_STORE_OBJECT_SUFFIX_KEY, 1);
                controllerDiskCache = new FileLruCache(TAG, new Limits());
                registerAccessTokenTracker();
                CallbackManagerImpl.registerStaticCallback(RequestCodeOffset.Like.toRequestCode(), new C10263());
                isInitialized = true;
            }
        }
    }

    private static void invokeCallbackWithController(CreationCallback callback, LikeActionController controller, FacebookException error) {
        if (callback != null) {
            handler.post(new C04554(callback, controller, error));
        }
    }

    private static void registerAccessTokenTracker() {
        accessTokenTracker = new C10275();
    }

    private static void putControllerInMemoryCache(String objectId, LikeActionController controllerForObject) {
        String cacheKey = getCacheKeyForObjectId(objectId);
        mruCacheWorkQueue.addActiveWorkItem(new MRUCacheWorkItem(cacheKey, true));
        cache.put(cacheKey, controllerForObject);
    }

    private static LikeActionController getControllerFromInMemoryCache(String objectId) {
        String cacheKey = getCacheKeyForObjectId(objectId);
        LikeActionController controller = (LikeActionController) cache.get(cacheKey);
        if (controller != null) {
            mruCacheWorkQueue.addActiveWorkItem(new MRUCacheWorkItem(cacheKey, false));
        }
        return controller;
    }

    private static void serializeToDiskAsync(LikeActionController controller) {
        String controllerJson = serializeToJson(controller);
        String cacheKey = getCacheKeyForObjectId(controller.objectId);
        if (!Utility.isNullOrEmpty(controllerJson) && !Utility.isNullOrEmpty(cacheKey)) {
            diskIOWorkQueue.addActiveWorkItem(new SerializeToDiskWorkItem(cacheKey, controllerJson));
        }
    }

    private static void serializeToDiskSynchronously(String cacheKey, String controllerJson) {
        OutputStream outputStream = null;
        try {
            outputStream = controllerDiskCache.openPutStream(cacheKey);
            outputStream.write(controllerJson.getBytes());
            if (outputStream != null) {
                Utility.closeQuietly(outputStream);
            }
        } catch (IOException e) {
            Log.e(TAG, "Unable to serialize controller to disk", e);
            if (outputStream != null) {
                Utility.closeQuietly(outputStream);
            }
        } catch (Throwable th) {
            if (outputStream != null) {
                Utility.closeQuietly(outputStream);
            }
        }
    }

    private static LikeActionController deserializeFromDiskSynchronously(String objectId) {
        LikeActionController controller = null;
        try {
            InputStream inputStream = controllerDiskCache.get(getCacheKeyForObjectId(objectId));
            if (inputStream != null) {
                String controllerJsonString = Utility.readStreamToString(inputStream);
                if (!Utility.isNullOrEmpty(controllerJsonString)) {
                    controller = deserializeFromJson(controllerJsonString);
                }
            }
            if (inputStream != null) {
                Utility.closeQuietly(inputStream);
            }
        } catch (IOException e) {
            Log.e(TAG, "Unable to deserialize controller from disk", e);
            controller = null;
            if (null != null) {
                Utility.closeQuietly(null);
            }
        } catch (Throwable th) {
            if (null != null) {
                Utility.closeQuietly(null);
            }
        }
        return controller;
    }

    private static LikeActionController deserializeFromJson(String controllerJsonString) {
        try {
            JSONObject controllerJson = new JSONObject(controllerJsonString);
            if (controllerJson.optInt(JSON_INT_VERSION_KEY, -1) != LIKE_ACTION_CONTROLLER_VERSION) {
                return null;
            }
            LikeActionController controller = new LikeActionController(controllerJson.getString(JSON_STRING_OBJECT_ID_KEY), ObjectType.fromInt(controllerJson.optInt(JSON_INT_OBJECT_TYPE_KEY, ObjectType.UNKNOWN.getValue())));
            controller.likeCountStringWithLike = controllerJson.optString(JSON_STRING_LIKE_COUNT_WITH_LIKE_KEY, null);
            controller.likeCountStringWithoutLike = controllerJson.optString(JSON_STRING_LIKE_COUNT_WITHOUT_LIKE_KEY, null);
            controller.socialSentenceWithLike = controllerJson.optString(JSON_STRING_SOCIAL_SENTENCE_WITH_LIKE_KEY, null);
            controller.socialSentenceWithoutLike = controllerJson.optString(JSON_STRING_SOCIAL_SENTENCE_WITHOUT_LIKE_KEY, null);
            controller.isObjectLiked = controllerJson.optBoolean(JSON_BOOL_IS_OBJECT_LIKED_KEY);
            controller.unlikeToken = controllerJson.optString(LIKE_DIALOG_RESPONSE_UNLIKE_TOKEN_KEY, null);
            JSONObject analyticsJSON = controllerJson.optJSONObject(JSON_BUNDLE_FACEBOOK_DIALOG_ANALYTICS_BUNDLE);
            if (analyticsJSON == null) {
                return controller;
            }
            controller.facebookDialogAnalyticsBundle = BundleJSONConverter.convertToBundle(analyticsJSON);
            return controller;
        } catch (JSONException e) {
            Log.e(TAG, "Unable to deserialize controller from JSON", e);
            return null;
        }
    }

    private static String serializeToJson(LikeActionController controller) {
        JSONObject controllerJson = new JSONObject();
        try {
            controllerJson.put(JSON_INT_VERSION_KEY, LIKE_ACTION_CONTROLLER_VERSION);
            controllerJson.put(JSON_STRING_OBJECT_ID_KEY, controller.objectId);
            controllerJson.put(JSON_INT_OBJECT_TYPE_KEY, controller.objectType.getValue());
            controllerJson.put(JSON_STRING_LIKE_COUNT_WITH_LIKE_KEY, controller.likeCountStringWithLike);
            controllerJson.put(JSON_STRING_LIKE_COUNT_WITHOUT_LIKE_KEY, controller.likeCountStringWithoutLike);
            controllerJson.put(JSON_STRING_SOCIAL_SENTENCE_WITH_LIKE_KEY, controller.socialSentenceWithLike);
            controllerJson.put(JSON_STRING_SOCIAL_SENTENCE_WITHOUT_LIKE_KEY, controller.socialSentenceWithoutLike);
            controllerJson.put(JSON_BOOL_IS_OBJECT_LIKED_KEY, controller.isObjectLiked);
            controllerJson.put(LIKE_DIALOG_RESPONSE_UNLIKE_TOKEN_KEY, controller.unlikeToken);
            if (controller.facebookDialogAnalyticsBundle != null) {
                JSONObject analyticsJSON = BundleJSONConverter.convertToJSON(controller.facebookDialogAnalyticsBundle);
                if (analyticsJSON != null) {
                    controllerJson.put(JSON_BUNDLE_FACEBOOK_DIALOG_ANALYTICS_BUNDLE, analyticsJSON);
                }
            }
            return controllerJson.toString();
        } catch (JSONException e) {
            Log.e(TAG, "Unable to serialize controller to JSON", e);
            return null;
        }
    }

    private static String getCacheKeyForObjectId(String objectId) {
        String accessTokenPortion = null;
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null) {
            accessTokenPortion = accessToken.getToken();
        }
        if (accessTokenPortion != null) {
            accessTokenPortion = Utility.md5hash(accessTokenPortion);
        }
        Object[] objArr = new Object[LIKE_ACTION_CONTROLLER_VERSION];
        objArr[0] = objectId;
        objArr[1] = Utility.coerceValueIfNullOrEmpty(accessTokenPortion, BuildConfig.VERSION_NAME);
        objArr[2] = Integer.valueOf(objectSuffix);
        return String.format(Locale.ROOT, "%s|%s|com.fb.sdk.like|%d", objArr);
    }

    private static void broadcastAction(LikeActionController controller, String action) {
        broadcastAction(controller, action, null);
    }

    private static void broadcastAction(LikeActionController controller, String action, Bundle data) {
        Intent broadcastIntent = new Intent(action);
        if (controller != null) {
            if (data == null) {
                data = new Bundle();
            }
            data.putString(ACTION_OBJECT_ID_KEY, controller.getObjectId());
        }
        if (data != null) {
            broadcastIntent.putExtras(data);
        }
        LocalBroadcastManager.getInstance(FacebookSdk.getApplicationContext()).sendBroadcast(broadcastIntent);
    }

    private LikeActionController(String objectId, ObjectType objectType) {
        this.objectId = objectId;
        this.objectType = objectType;
    }

    public String getObjectId() {
        return this.objectId;
    }

    public String getLikeCountString() {
        return this.isObjectLiked ? this.likeCountStringWithLike : this.likeCountStringWithoutLike;
    }

    public String getSocialSentence() {
        return this.isObjectLiked ? this.socialSentenceWithLike : this.socialSentenceWithoutLike;
    }

    public boolean isObjectLiked() {
        return this.isObjectLiked;
    }

    public boolean shouldEnableView() {
        if (LikeDialog.canShowNativeDialog() || LikeDialog.canShowWebFallback()) {
            return true;
        }
        if (this.objectIsPage || this.objectType == ObjectType.PAGE) {
            return false;
        }
        AccessToken token = AccessToken.getCurrentAccessToken();
        if (token == null || token.getPermissions() == null || !token.getPermissions().contains("publish_actions")) {
            return false;
        }
        return true;
    }

    public void toggleLike(Activity activity, FragmentWrapper fragment, Bundle analyticsParameters) {
        boolean shouldLikeObject;
        boolean z = true;
        if (this.isObjectLiked) {
            shouldLikeObject = false;
        } else {
            shouldLikeObject = true;
        }
        if (canUseOGPublish()) {
            updateLikeState(shouldLikeObject);
            if (this.isPendingLikeOrUnlike) {
                getAppEventsLogger().logSdkEvent(AnalyticsEvents.EVENT_LIKE_VIEW_DID_UNDO_QUICKLY, null, analyticsParameters);
                return;
            } else if (!publishLikeOrUnlikeAsync(shouldLikeObject, analyticsParameters)) {
                if (shouldLikeObject) {
                    z = false;
                }
                updateLikeState(z);
                presentLikeDialog(activity, fragment, analyticsParameters);
                return;
            } else {
                return;
            }
        }
        presentLikeDialog(activity, fragment, analyticsParameters);
    }

    private AppEventsLogger getAppEventsLogger() {
        if (this.appEventsLogger == null) {
            this.appEventsLogger = AppEventsLogger.newLogger(FacebookSdk.getApplicationContext());
        }
        return this.appEventsLogger;
    }

    private boolean publishLikeOrUnlikeAsync(boolean shouldLikeObject, Bundle analyticsParameters) {
        if (!canUseOGPublish()) {
            return false;
        }
        if (shouldLikeObject) {
            publishLikeAsync(analyticsParameters);
            return true;
        } else if (Utility.isNullOrEmpty(this.unlikeToken)) {
            return false;
        } else {
            publishUnlikeAsync(analyticsParameters);
            return true;
        }
    }

    private void publishDidError(boolean oldLikeState) {
        updateLikeState(oldLikeState);
        Bundle errorBundle = new Bundle();
        errorBundle.putString(NativeProtocol.STATUS_ERROR_DESCRIPTION, ERROR_PUBLISH_ERROR);
        broadcastAction(this, ACTION_LIKE_ACTION_CONTROLLER_DID_ERROR, errorBundle);
    }

    private void updateLikeState(boolean isObjectLiked) {
        updateState(isObjectLiked, this.likeCountStringWithLike, this.likeCountStringWithoutLike, this.socialSentenceWithLike, this.socialSentenceWithoutLike, this.unlikeToken);
    }

    private void updateState(boolean isObjectLiked, String likeCountStringWithLike, String likeCountStringWithoutLike, String socialSentenceWithLike, String socialSentenceWithoutLike, String unlikeToken) {
        likeCountStringWithLike = Utility.coerceValueIfNullOrEmpty(likeCountStringWithLike, null);
        likeCountStringWithoutLike = Utility.coerceValueIfNullOrEmpty(likeCountStringWithoutLike, null);
        socialSentenceWithLike = Utility.coerceValueIfNullOrEmpty(socialSentenceWithLike, null);
        socialSentenceWithoutLike = Utility.coerceValueIfNullOrEmpty(socialSentenceWithoutLike, null);
        unlikeToken = Utility.coerceValueIfNullOrEmpty(unlikeToken, null);
        boolean stateChanged = (isObjectLiked == this.isObjectLiked && Utility.areObjectsEqual(likeCountStringWithLike, this.likeCountStringWithLike) && Utility.areObjectsEqual(likeCountStringWithoutLike, this.likeCountStringWithoutLike) && Utility.areObjectsEqual(socialSentenceWithLike, this.socialSentenceWithLike) && Utility.areObjectsEqual(socialSentenceWithoutLike, this.socialSentenceWithoutLike) && Utility.areObjectsEqual(unlikeToken, this.unlikeToken)) ? false : true;
        if (stateChanged) {
            this.isObjectLiked = isObjectLiked;
            this.likeCountStringWithLike = likeCountStringWithLike;
            this.likeCountStringWithoutLike = likeCountStringWithoutLike;
            this.socialSentenceWithLike = socialSentenceWithLike;
            this.socialSentenceWithoutLike = socialSentenceWithoutLike;
            this.unlikeToken = unlikeToken;
            serializeToDiskAsync(this);
            broadcastAction(this, ACTION_LIKE_ACTION_CONTROLLER_UPDATED);
        }
    }

    private void presentLikeDialog(Activity activity, FragmentWrapper fragmentWrapper, Bundle analyticsParameters) {
        String analyticsEvent = null;
        if (LikeDialog.canShowNativeDialog()) {
            analyticsEvent = AnalyticsEvents.EVENT_LIKE_VIEW_DID_PRESENT_DIALOG;
        } else if (LikeDialog.canShowWebFallback()) {
            analyticsEvent = AnalyticsEvents.EVENT_LIKE_VIEW_DID_PRESENT_FALLBACK;
        } else {
            logAppEventForError("present_dialog", analyticsParameters);
            Utility.logd(TAG, "Cannot show the Like Dialog on this device.");
            broadcastAction(null, ACTION_LIKE_ACTION_CONTROLLER_UPDATED);
        }
        if (analyticsEvent != null) {
            String objectTypeString;
            if (this.objectType != null) {
                objectTypeString = this.objectType.toString();
            } else {
                objectTypeString = ObjectType.UNKNOWN.toString();
            }
            LikeContent likeContent = new Builder().setObjectId(this.objectId).setObjectType(objectTypeString).build();
            if (fragmentWrapper != null) {
                new LikeDialog(fragmentWrapper).show(likeContent);
            } else {
                new LikeDialog(activity).show(likeContent);
            }
            saveState(analyticsParameters);
            getAppEventsLogger().logSdkEvent(AnalyticsEvents.EVENT_LIKE_VIEW_DID_PRESENT_DIALOG, null, analyticsParameters);
        }
    }

    private void onActivityResult(int requestCode, int resultCode, Intent data) {
        ShareInternalUtility.handleActivityResult(requestCode, resultCode, data, getResultProcessor(this.facebookDialogAnalyticsBundle));
        clearState();
    }

    private ResultProcessor getResultProcessor(Bundle analyticsParameters) {
        return new C10286(null, analyticsParameters);
    }

    private void saveState(Bundle analyticsParameters) {
        storeObjectIdForPendingController(this.objectId);
        this.facebookDialogAnalyticsBundle = analyticsParameters;
        serializeToDiskAsync(this);
    }

    private void clearState() {
        this.facebookDialogAnalyticsBundle = null;
        storeObjectIdForPendingController(null);
    }

    private static void storeObjectIdForPendingController(String objectId) {
        objectIdForPendingController = objectId;
        FacebookSdk.getApplicationContext().getSharedPreferences(LIKE_ACTION_CONTROLLER_STORE, 0).edit().putString(LIKE_ACTION_CONTROLLER_STORE_PENDING_OBJECT_ID_KEY, objectIdForPendingController).apply();
    }

    private boolean canUseOGPublish() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return (this.objectIsPage || this.verifiedObjectId == null || accessToken == null || accessToken.getPermissions() == null || !accessToken.getPermissions().contains("publish_actions")) ? false : true;
    }

    private void publishLikeAsync(Bundle analyticsParameters) {
        this.isPendingLikeOrUnlike = true;
        fetchVerifiedObjectId(new C10307(analyticsParameters));
    }

    private void publishUnlikeAsync(Bundle analyticsParameters) {
        this.isPendingLikeOrUnlike = true;
        GraphRequestBatch requestBatch = new GraphRequestBatch();
        PublishUnlikeRequestWrapper unlikeRequest = new PublishUnlikeRequestWrapper(this.unlikeToken);
        unlikeRequest.addToBatch(requestBatch);
        requestBatch.addCallback(new C10318(unlikeRequest, analyticsParameters));
        requestBatch.executeAsync();
    }

    private void refreshStatusAsync() {
        if (AccessToken.getCurrentAccessToken() == null) {
            refreshStatusViaService();
        } else {
            fetchVerifiedObjectId(new C10339());
        }
    }

    private void refreshStatusViaService() {
        LikeStatusClient likeStatusClient = new LikeStatusClient(FacebookSdk.getApplicationContext(), FacebookSdk.getApplicationId(), this.objectId);
        if (likeStatusClient.start()) {
            likeStatusClient.setCompletedListener(new CompletedListener() {
                public void completed(Bundle result) {
                    if (result != null && result.containsKey(ShareConstants.EXTRA_OBJECT_IS_LIKED)) {
                        String likeCountWithLike;
                        String likeCountWithoutLike;
                        String socialSentenceWithLike;
                        String socialSentenceWithoutLike;
                        String unlikeToken;
                        boolean objectIsLiked = result.getBoolean(ShareConstants.EXTRA_OBJECT_IS_LIKED);
                        if (result.containsKey(ShareConstants.EXTRA_LIKE_COUNT_STRING_WITH_LIKE)) {
                            likeCountWithLike = result.getString(ShareConstants.EXTRA_LIKE_COUNT_STRING_WITH_LIKE);
                        } else {
                            likeCountWithLike = LikeActionController.this.likeCountStringWithLike;
                        }
                        if (result.containsKey(ShareConstants.EXTRA_LIKE_COUNT_STRING_WITHOUT_LIKE)) {
                            likeCountWithoutLike = result.getString(ShareConstants.EXTRA_LIKE_COUNT_STRING_WITHOUT_LIKE);
                        } else {
                            likeCountWithoutLike = LikeActionController.this.likeCountStringWithoutLike;
                        }
                        if (result.containsKey(ShareConstants.EXTRA_SOCIAL_SENTENCE_WITH_LIKE)) {
                            socialSentenceWithLike = result.getString(ShareConstants.EXTRA_SOCIAL_SENTENCE_WITH_LIKE);
                        } else {
                            socialSentenceWithLike = LikeActionController.this.socialSentenceWithLike;
                        }
                        if (result.containsKey(ShareConstants.EXTRA_SOCIAL_SENTENCE_WITHOUT_LIKE)) {
                            socialSentenceWithoutLike = result.getString(ShareConstants.EXTRA_SOCIAL_SENTENCE_WITHOUT_LIKE);
                        } else {
                            socialSentenceWithoutLike = LikeActionController.this.socialSentenceWithoutLike;
                        }
                        if (result.containsKey(ShareConstants.EXTRA_UNLIKE_TOKEN)) {
                            unlikeToken = result.getString(ShareConstants.EXTRA_UNLIKE_TOKEN);
                        } else {
                            unlikeToken = LikeActionController.this.unlikeToken;
                        }
                        LikeActionController.this.updateState(objectIsLiked, likeCountWithLike, likeCountWithoutLike, socialSentenceWithLike, socialSentenceWithoutLike, unlikeToken);
                    }
                }
            });
        }
    }

    private void publishAgainIfNeeded(Bundle analyticsParameters) {
        if (this.isObjectLiked != this.isObjectLikedOnServer && !publishLikeOrUnlikeAsync(this.isObjectLiked, analyticsParameters)) {
            publishDidError(!this.isObjectLiked);
        }
    }

    private void fetchVerifiedObjectId(RequestCompletionCallback completionHandler) {
        if (Utility.isNullOrEmpty(this.verifiedObjectId)) {
            GetOGObjectIdRequestWrapper objectIdRequest = new GetOGObjectIdRequestWrapper(this.objectId, this.objectType);
            GetPageIdRequestWrapper pageIdRequest = new GetPageIdRequestWrapper(this.objectId, this.objectType);
            GraphRequestBatch requestBatch = new GraphRequestBatch();
            objectIdRequest.addToBatch(requestBatch);
            pageIdRequest.addToBatch(requestBatch);
            requestBatch.addCallback(new AnonymousClass11(objectIdRequest, pageIdRequest, completionHandler));
            requestBatch.executeAsync();
        } else if (completionHandler != null) {
            completionHandler.onComplete();
        }
    }

    private void logAppEventForError(String action, Bundle parameters) {
        Bundle logParams = new Bundle(parameters);
        logParams.putString(JSON_STRING_OBJECT_ID_KEY, this.objectId);
        logParams.putString(JSON_INT_OBJECT_TYPE_KEY, this.objectType.toString());
        logParams.putString(AnalyticsEvents.PARAMETER_LIKE_VIEW_CURRENT_ACTION, action);
        getAppEventsLogger().logSdkEvent(AnalyticsEvents.EVENT_LIKE_VIEW_ERROR, null, logParams);
    }

    private void logAppEventForError(String action, FacebookRequestError error) {
        Bundle logParams = new Bundle();
        if (error != null) {
            JSONObject requestResult = error.getRequestResult();
            if (requestResult != null) {
                logParams.putString(NativeProtocol.BRIDGE_ARG_ERROR_BUNDLE, requestResult.toString());
            }
        }
        logAppEventForError(action, logParams);
    }
}
