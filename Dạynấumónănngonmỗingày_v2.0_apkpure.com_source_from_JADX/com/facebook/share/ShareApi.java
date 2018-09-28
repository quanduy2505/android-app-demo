package com.facebook.share;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import com.facebook.AccessToken;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookGraphResponseException;
import com.facebook.FacebookRequestError;
import com.facebook.GraphRequest;
import com.facebook.GraphRequest.Callback;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.internal.CollectionMapper;
import com.facebook.internal.CollectionMapper.Collection;
import com.facebook.internal.CollectionMapper.OnErrorListener;
import com.facebook.internal.CollectionMapper.OnMapValueCompleteListener;
import com.facebook.internal.CollectionMapper.OnMapperCompleteListener;
import com.facebook.internal.CollectionMapper.ValueMapper;
import com.facebook.internal.Mutable;
import com.facebook.internal.NativeProtocol;
import com.facebook.internal.Utility;
import com.facebook.share.Sharer.Result;
import com.facebook.share.internal.ShareConstants;
import com.facebook.share.internal.ShareContentValidation;
import com.facebook.share.internal.ShareInternalUtility;
import com.facebook.share.internal.VideoUploader;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.ShareOpenGraphAction;
import com.facebook.share.model.ShareOpenGraphContent;
import com.facebook.share.model.ShareOpenGraphObject;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.model.ShareVideoContent;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class ShareApi {
    private static final String DEFAULT_CHARSET = "UTF-8";
    private static final String DEFAULT_GRAPH_NODE = "me";
    private static final String GRAPH_PATH_FORMAT = "%s/%s";
    private static final String PHOTOS_EDGE = "photos";
    private static final String TAG = "ShareApi";
    private String graphNode;
    private String message;
    private final ShareContent shareContent;

    /* renamed from: com.facebook.share.ShareApi.10 */
    class AnonymousClass10 implements Callback {
        final /* synthetic */ OnMapValueCompleteListener val$onOpenGraphObjectStagedListener;

        AnonymousClass10(OnMapValueCompleteListener onMapValueCompleteListener) {
            this.val$onOpenGraphObjectStagedListener = onMapValueCompleteListener;
        }

        public void onCompleted(GraphResponse response) {
            FacebookRequestError error = response.getError();
            if (error != null) {
                String message = error.getErrorMessage();
                if (message == null) {
                    message = "Error staging Open Graph object.";
                }
                this.val$onOpenGraphObjectStagedListener.onError(new FacebookGraphResponseException(response, message));
                return;
            }
            JSONObject data = response.getJSONObject();
            if (data == null) {
                this.val$onOpenGraphObjectStagedListener.onError(new FacebookGraphResponseException(response, "Error staging Open Graph object."));
                return;
            }
            String stagedObjectId = data.optString(ShareConstants.WEB_DIALOG_PARAM_ID);
            if (stagedObjectId == null) {
                this.val$onOpenGraphObjectStagedListener.onError(new FacebookGraphResponseException(response, "Error staging Open Graph object."));
            } else {
                this.val$onOpenGraphObjectStagedListener.onComplete(stagedObjectId);
            }
        }
    }

    /* renamed from: com.facebook.share.ShareApi.12 */
    class AnonymousClass12 implements Callback {
        final /* synthetic */ OnMapValueCompleteListener val$onPhotoStagedListener;
        final /* synthetic */ SharePhoto val$photo;

        AnonymousClass12(OnMapValueCompleteListener onMapValueCompleteListener, SharePhoto sharePhoto) {
            this.val$onPhotoStagedListener = onMapValueCompleteListener;
            this.val$photo = sharePhoto;
        }

        public void onCompleted(GraphResponse response) {
            String message;
            FacebookRequestError error = response.getError();
            if (error != null) {
                message = error.getErrorMessage();
                if (message == null) {
                    message = "Error staging photo.";
                }
                this.val$onPhotoStagedListener.onError(new FacebookGraphResponseException(response, message));
                return;
            }
            JSONObject data = response.getJSONObject();
            if (data == null) {
                this.val$onPhotoStagedListener.onError(new FacebookException("Error staging photo."));
                return;
            }
            String stagedImageUri = data.optString(ShareConstants.MEDIA_URI);
            if (stagedImageUri == null) {
                this.val$onPhotoStagedListener.onError(new FacebookException("Error staging photo."));
                return;
            }
            JSONObject stagedObject = new JSONObject();
            try {
                stagedObject.put(NativeProtocol.WEB_DIALOG_URL, stagedImageUri);
                stagedObject.put(NativeProtocol.IMAGE_USER_GENERATED_KEY, this.val$photo.getUserGenerated());
                this.val$onPhotoStagedListener.onComplete(stagedObject);
            } catch (JSONException ex) {
                message = ex.getLocalizedMessage();
                if (message == null) {
                    message = "Error staging photo.";
                }
                this.val$onPhotoStagedListener.onError(new FacebookException(message));
            }
        }
    }

    /* renamed from: com.facebook.share.ShareApi.1 */
    class C10171 implements Callback {
        final /* synthetic */ FacebookCallback val$callback;

        C10171(FacebookCallback facebookCallback) {
            this.val$callback = facebookCallback;
        }

        public void onCompleted(GraphResponse response) {
            JSONObject data = response.getJSONObject();
            ShareInternalUtility.invokeCallbackWithResults(this.val$callback, data == null ? null : data.optString(ShareConstants.WEB_DIALOG_PARAM_ID), response);
        }
    }

    /* renamed from: com.facebook.share.ShareApi.3 */
    class C10183 implements Callback {
        final /* synthetic */ FacebookCallback val$callback;
        final /* synthetic */ ArrayList val$errorResponses;
        final /* synthetic */ Mutable val$requestCount;
        final /* synthetic */ ArrayList val$results;

        C10183(ArrayList arrayList, ArrayList arrayList2, Mutable mutable, FacebookCallback facebookCallback) {
            this.val$results = arrayList;
            this.val$errorResponses = arrayList2;
            this.val$requestCount = mutable;
            this.val$callback = facebookCallback;
        }

        public void onCompleted(GraphResponse response) {
            JSONObject result = response.getJSONObject();
            if (result != null) {
                this.val$results.add(result);
            }
            if (response.getError() != null) {
                this.val$errorResponses.add(response);
            }
            this.val$requestCount.value = Integer.valueOf(((Integer) this.val$requestCount.value).intValue() - 1);
            if (((Integer) this.val$requestCount.value).intValue() != 0) {
                return;
            }
            if (!this.val$errorResponses.isEmpty()) {
                ShareInternalUtility.invokeCallbackWithResults(this.val$callback, null, (GraphResponse) this.val$errorResponses.get(0));
            } else if (!this.val$results.isEmpty()) {
                ShareInternalUtility.invokeCallbackWithResults(this.val$callback, ((JSONObject) this.val$results.get(0)).optString(ShareConstants.WEB_DIALOG_PARAM_ID), response);
            }
        }
    }

    /* renamed from: com.facebook.share.ShareApi.4 */
    class C10194 implements Callback {
        final /* synthetic */ FacebookCallback val$callback;

        C10194(FacebookCallback facebookCallback) {
            this.val$callback = facebookCallback;
        }

        public void onCompleted(GraphResponse response) {
            JSONObject data = response.getJSONObject();
            ShareInternalUtility.invokeCallbackWithResults(this.val$callback, data == null ? null : data.optString(ShareConstants.WEB_DIALOG_PARAM_ID), response);
        }
    }

    /* renamed from: com.facebook.share.ShareApi.5 */
    class C10205 implements Collection<Integer> {
        final /* synthetic */ ArrayList val$arrayList;
        final /* synthetic */ JSONArray val$stagedObject;

        /* renamed from: com.facebook.share.ShareApi.5.1 */
        class C04501 implements Iterator<Integer> {
            final /* synthetic */ Mutable val$current;
            final /* synthetic */ int val$size;

            C04501(Mutable mutable, int i) {
                this.val$current = mutable;
                this.val$size = i;
            }

            public boolean hasNext() {
                return ((Integer) this.val$current.value).intValue() < this.val$size;
            }

            public Integer next() {
                Integer num = (Integer) this.val$current.value;
                Mutable mutable = this.val$current;
                mutable.value = Integer.valueOf(((Integer) mutable.value).intValue() + 1);
                return num;
            }

            public void remove() {
            }
        }

        C10205(ArrayList arrayList, JSONArray jSONArray) {
            this.val$arrayList = arrayList;
            this.val$stagedObject = jSONArray;
        }

        public Iterator<Integer> keyIterator() {
            return new C04501(new Mutable(Integer.valueOf(0)), this.val$arrayList.size());
        }

        public Object get(Integer key) {
            return this.val$arrayList.get(key.intValue());
        }

        public void set(Integer key, Object value, OnErrorListener onErrorListener) {
            try {
                this.val$stagedObject.put(key.intValue(), value);
            } catch (JSONException ex) {
                String message = ex.getLocalizedMessage();
                if (message == null) {
                    message = "Error staging object.";
                }
                onErrorListener.onError(new FacebookException(message));
            }
        }
    }

    /* renamed from: com.facebook.share.ShareApi.7 */
    class C10217 implements ValueMapper {
        C10217() {
        }

        public void mapValue(Object value, OnMapValueCompleteListener onMapValueCompleteListener) {
            if (value instanceof ArrayList) {
                ShareApi.this.stageArrayList((ArrayList) value, onMapValueCompleteListener);
            } else if (value instanceof ShareOpenGraphObject) {
                ShareApi.this.stageOpenGraphObject((ShareOpenGraphObject) value, onMapValueCompleteListener);
            } else if (value instanceof SharePhoto) {
                ShareApi.this.stagePhoto((SharePhoto) value, onMapValueCompleteListener);
            } else {
                onMapValueCompleteListener.onComplete(value);
            }
        }
    }

    /* renamed from: com.facebook.share.ShareApi.8 */
    class C10228 implements Collection<String> {
        final /* synthetic */ Bundle val$parameters;

        C10228(Bundle bundle) {
            this.val$parameters = bundle;
        }

        public Iterator<String> keyIterator() {
            return this.val$parameters.keySet().iterator();
        }

        public Object get(String key) {
            return this.val$parameters.get(key);
        }

        public void set(String key, Object value, OnErrorListener onErrorListener) {
            if (!Utility.putJSONValueInBundle(this.val$parameters, key, value)) {
                onErrorListener.onError(new FacebookException("Unexpected value: " + value.toString()));
            }
        }
    }

    /* renamed from: com.facebook.share.ShareApi.9 */
    class C10239 implements Collection<String> {
        final /* synthetic */ ShareOpenGraphObject val$object;
        final /* synthetic */ JSONObject val$stagedObject;

        C10239(ShareOpenGraphObject shareOpenGraphObject, JSONObject jSONObject) {
            this.val$object = shareOpenGraphObject;
            this.val$stagedObject = jSONObject;
        }

        public Iterator<String> keyIterator() {
            return this.val$object.keySet().iterator();
        }

        public Object get(String key) {
            return this.val$object.get(key);
        }

        public void set(String key, Object value, OnErrorListener onErrorListener) {
            try {
                this.val$stagedObject.put(key, value);
            } catch (JSONException ex) {
                String message = ex.getLocalizedMessage();
                if (message == null) {
                    message = "Error staging object.";
                }
                onErrorListener.onError(new FacebookException(message));
            }
        }
    }

    /* renamed from: com.facebook.share.ShareApi.11 */
    class AnonymousClass11 implements OnMapperCompleteListener {
        final /* synthetic */ String val$ogType;
        final /* synthetic */ OnMapValueCompleteListener val$onOpenGraphObjectStagedListener;
        final /* synthetic */ Callback val$requestCallback;
        final /* synthetic */ JSONObject val$stagedObject;

        AnonymousClass11(JSONObject jSONObject, String str, Callback callback, OnMapValueCompleteListener onMapValueCompleteListener) {
            this.val$stagedObject = jSONObject;
            this.val$ogType = str;
            this.val$requestCallback = callback;
            this.val$onOpenGraphObjectStagedListener = onMapValueCompleteListener;
        }

        public void onComplete() {
            String objectString = this.val$stagedObject.toString();
            Bundle parameters = new Bundle();
            parameters.putString("object", objectString);
            try {
                new GraphRequest(AccessToken.getCurrentAccessToken(), ShareApi.this.getGraphPath("objects/" + URLEncoder.encode(this.val$ogType, ShareApi.DEFAULT_CHARSET)), parameters, HttpMethod.POST, this.val$requestCallback).executeAsync();
            } catch (UnsupportedEncodingException ex) {
                String message = ex.getLocalizedMessage();
                if (message == null) {
                    message = "Error staging Open Graph object.";
                }
                this.val$onOpenGraphObjectStagedListener.onError(new FacebookException(message));
            }
        }

        public void onError(FacebookException exception) {
            this.val$onOpenGraphObjectStagedListener.onError(exception);
        }
    }

    /* renamed from: com.facebook.share.ShareApi.2 */
    class C13232 implements OnMapperCompleteListener {
        final /* synthetic */ ShareOpenGraphAction val$action;
        final /* synthetic */ FacebookCallback val$callback;
        final /* synthetic */ Bundle val$parameters;
        final /* synthetic */ Callback val$requestCallback;

        C13232(Bundle bundle, ShareOpenGraphAction shareOpenGraphAction, Callback callback, FacebookCallback facebookCallback) {
            this.val$parameters = bundle;
            this.val$action = shareOpenGraphAction;
            this.val$requestCallback = callback;
            this.val$callback = facebookCallback;
        }

        public void onComplete() {
            try {
                ShareApi.handleImagesOnAction(this.val$parameters);
                new GraphRequest(AccessToken.getCurrentAccessToken(), ShareApi.this.getGraphPath(URLEncoder.encode(this.val$action.getActionType(), ShareApi.DEFAULT_CHARSET)), this.val$parameters, HttpMethod.POST, this.val$requestCallback).executeAsync();
            } catch (UnsupportedEncodingException ex) {
                ShareInternalUtility.invokeCallbackWithException(this.val$callback, ex);
            }
        }

        public void onError(FacebookException exception) {
            ShareInternalUtility.invokeCallbackWithException(this.val$callback, exception);
        }
    }

    /* renamed from: com.facebook.share.ShareApi.6 */
    class C13246 implements OnMapperCompleteListener {
        final /* synthetic */ OnMapValueCompleteListener val$onArrayListStagedListener;
        final /* synthetic */ JSONArray val$stagedObject;

        C13246(OnMapValueCompleteListener onMapValueCompleteListener, JSONArray jSONArray) {
            this.val$onArrayListStagedListener = onMapValueCompleteListener;
            this.val$stagedObject = jSONArray;
        }

        public void onComplete() {
            this.val$onArrayListStagedListener.onComplete(this.val$stagedObject);
        }

        public void onError(FacebookException exception) {
            this.val$onArrayListStagedListener.onError(exception);
        }
    }

    public static void share(ShareContent shareContent, FacebookCallback<Result> callback) {
        new ShareApi(shareContent).share(callback);
    }

    public ShareApi(ShareContent shareContent) {
        this.shareContent = shareContent;
        this.graphNode = DEFAULT_GRAPH_NODE;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getGraphNode() {
        return this.graphNode;
    }

    public void setGraphNode(String graphNode) {
        this.graphNode = graphNode;
    }

    public ShareContent getShareContent() {
        return this.shareContent;
    }

    public boolean canShare() {
        if (getShareContent() == null) {
            return false;
        }
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken == null) {
            return false;
        }
        Set<String> permissions = accessToken.getPermissions();
        if (permissions == null || !permissions.contains("publish_actions")) {
            Log.w(TAG, "The publish_actions permissions are missing, the share will fail unless this app was authorized to publish in another installation.");
        }
        return true;
    }

    public void share(FacebookCallback<Result> callback) {
        if (canShare()) {
            ShareContent shareContent = getShareContent();
            try {
                ShareContentValidation.validateForApiShare(shareContent);
                if (shareContent instanceof ShareLinkContent) {
                    shareLinkContent((ShareLinkContent) shareContent, callback);
                    return;
                } else if (shareContent instanceof SharePhotoContent) {
                    sharePhotoContent((SharePhotoContent) shareContent, callback);
                    return;
                } else if (shareContent instanceof ShareVideoContent) {
                    shareVideoContent((ShareVideoContent) shareContent, callback);
                    return;
                } else if (shareContent instanceof ShareOpenGraphContent) {
                    shareOpenGraphContent((ShareOpenGraphContent) shareContent, callback);
                    return;
                } else {
                    return;
                }
            } catch (FacebookException ex) {
                ShareInternalUtility.invokeCallbackWithException(callback, ex);
                return;
            }
        }
        ShareInternalUtility.invokeCallbackWithError(callback, "Insufficient permissions for sharing content via Api.");
    }

    private String getGraphPath(String pathAfterGraphNode) {
        try {
            return String.format(Locale.ROOT, GRAPH_PATH_FORMAT, new Object[]{URLEncoder.encode(getGraphNode(), DEFAULT_CHARSET), pathAfterGraphNode});
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    private void addCommonParameters(Bundle bundle, ShareContent shareContent) {
        java.util.Collection peopleIds = shareContent.getPeopleIds();
        if (!Utility.isNullOrEmpty(peopleIds)) {
            bundle.putString("tags", TextUtils.join(", ", peopleIds));
        }
        if (!Utility.isNullOrEmpty(shareContent.getPlaceId())) {
            bundle.putString("place", shareContent.getPlaceId());
        }
        if (!Utility.isNullOrEmpty(shareContent.getRef())) {
            bundle.putString("ref", shareContent.getRef());
        }
    }

    private void shareOpenGraphContent(ShareOpenGraphContent openGraphContent, FacebookCallback<Result> callback) {
        Callback requestCallback = new C10171(callback);
        ShareOpenGraphAction action = openGraphContent.getAction();
        Bundle parameters = action.getBundle();
        addCommonParameters(parameters, openGraphContent);
        if (!Utility.isNullOrEmpty(getMessage())) {
            parameters.putString(ShareConstants.WEB_DIALOG_PARAM_MESSAGE, getMessage());
        }
        stageOpenGraphAction(parameters, new C13232(parameters, action, requestCallback, callback));
    }

    private static void handleImagesOnAction(Bundle parameters) {
        String imageStr = parameters.getString("image");
        if (imageStr != null) {
            try {
                JSONArray images = new JSONArray(imageStr);
                for (int i = 0; i < images.length(); i++) {
                    JSONObject jsonImage = images.optJSONObject(i);
                    if (jsonImage != null) {
                        putImageInBundleWithArrayFormat(parameters, i, jsonImage);
                    } else {
                        parameters.putString(String.format(Locale.ROOT, "image[%d][url]", new Object[]{Integer.valueOf(i)}), images.getString(i));
                    }
                }
                parameters.remove("image");
            } catch (JSONException e) {
                try {
                    putImageInBundleWithArrayFormat(parameters, 0, new JSONObject(imageStr));
                    parameters.remove("image");
                } catch (JSONException e2) {
                }
            }
        }
    }

    private static void putImageInBundleWithArrayFormat(Bundle parameters, int index, JSONObject image) throws JSONException {
        Iterator<String> keys = image.keys();
        while (keys.hasNext()) {
            Object[] objArr = new Object[]{Integer.valueOf(index), (String) keys.next()};
            parameters.putString(String.format(Locale.ROOT, "image[%d][%s]", objArr), image.get((String) keys.next()).toString());
        }
    }

    private void sharePhotoContent(SharePhotoContent photoContent, FacebookCallback<Result> callback) {
        Mutable<Integer> requestCount = new Mutable(Integer.valueOf(0));
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        ArrayList<GraphRequest> requests = new ArrayList();
        Callback requestCallback = new C10183(new ArrayList(), new ArrayList(), requestCount, callback);
        try {
            for (SharePhoto photo : photoContent.getPhotos()) {
                try {
                    Bundle params = getSharePhotoCommonParameters(photo, photoContent);
                    Bitmap bitmap = photo.getBitmap();
                    Uri photoUri = photo.getImageUrl();
                    String caption = photo.getCaption();
                    if (caption == null) {
                        caption = getMessage();
                    }
                    ArrayList<GraphRequest> arrayList;
                    if (bitmap != null) {
                        arrayList = requests;
                        arrayList.add(GraphRequest.newUploadPhotoRequest(accessToken, getGraphPath(PHOTOS_EDGE), bitmap, caption, params, requestCallback));
                    } else if (photoUri != null) {
                        arrayList = requests;
                        arrayList.add(GraphRequest.newUploadPhotoRequest(accessToken, getGraphPath(PHOTOS_EDGE), photoUri, caption, params, requestCallback));
                    }
                } catch (Exception e) {
                    ShareInternalUtility.invokeCallbackWithException(callback, e);
                    return;
                }
            }
            requestCount.value = Integer.valueOf(((Integer) requestCount.value).intValue() + requests.size());
            Iterator it = requests.iterator();
            while (it.hasNext()) {
                ((GraphRequest) it.next()).executeAsync();
            }
        } catch (Exception ex) {
            ShareInternalUtility.invokeCallbackWithException(callback, ex);
        }
    }

    private void shareLinkContent(ShareLinkContent linkContent, FacebookCallback<Result> callback) {
        Callback requestCallback = new C10194(callback);
        Bundle parameters = new Bundle();
        addCommonParameters(parameters, linkContent);
        parameters.putString(ShareConstants.WEB_DIALOG_PARAM_MESSAGE, getMessage());
        parameters.putString(ShareConstants.WEB_DIALOG_PARAM_LINK, Utility.getUriString(linkContent.getContentUrl()));
        parameters.putString(ShareConstants.WEB_DIALOG_PARAM_PICTURE, Utility.getUriString(linkContent.getImageUrl()));
        parameters.putString(ShareConstants.WEB_DIALOG_PARAM_NAME, linkContent.getContentTitle());
        parameters.putString(ShareConstants.WEB_DIALOG_PARAM_DESCRIPTION, linkContent.getContentDescription());
        parameters.putString("ref", linkContent.getRef());
        new GraphRequest(AccessToken.getCurrentAccessToken(), getGraphPath("feed"), parameters, HttpMethod.POST, requestCallback).executeAsync();
    }

    private void shareVideoContent(ShareVideoContent videoContent, FacebookCallback<Result> callback) {
        try {
            VideoUploader.uploadAsync(videoContent, getGraphNode(), callback);
        } catch (FileNotFoundException ex) {
            ShareInternalUtility.invokeCallbackWithException(callback, ex);
        }
    }

    private Bundle getSharePhotoCommonParameters(SharePhoto photo, SharePhotoContent photoContent) throws JSONException {
        Bundle params = photo.getParameters();
        if (!(params.containsKey("place") || Utility.isNullOrEmpty(photoContent.getPlaceId()))) {
            params.putString("place", photoContent.getPlaceId());
        }
        if (!(params.containsKey("tags") || Utility.isNullOrEmpty(photoContent.getPeopleIds()))) {
            java.util.Collection<String> peopleIds = photoContent.getPeopleIds();
            if (!Utility.isNullOrEmpty((java.util.Collection) peopleIds)) {
                JSONArray tags = new JSONArray();
                for (String id : peopleIds) {
                    JSONObject tag = new JSONObject();
                    tag.put("tag_uid", id);
                    tags.put(tag);
                }
                params.putString("tags", tags.toString());
            }
        }
        if (!(params.containsKey("ref") || Utility.isNullOrEmpty(photoContent.getRef()))) {
            params.putString("ref", photoContent.getRef());
        }
        return params;
    }

    private void stageArrayList(ArrayList arrayList, OnMapValueCompleteListener onArrayListStagedListener) {
        JSONArray stagedObject = new JSONArray();
        stageCollectionValues(new C10205(arrayList, stagedObject), new C13246(onArrayListStagedListener, stagedObject));
    }

    private <T> void stageCollectionValues(Collection<T> collection, OnMapperCompleteListener onCollectionValuesStagedListener) {
        CollectionMapper.iterate(collection, new C10217(), onCollectionValuesStagedListener);
    }

    private void stageOpenGraphAction(Bundle parameters, OnMapperCompleteListener onOpenGraphActionStagedListener) {
        stageCollectionValues(new C10228(parameters), onOpenGraphActionStagedListener);
    }

    private void stageOpenGraphObject(ShareOpenGraphObject object, OnMapValueCompleteListener onOpenGraphObjectStagedListener) {
        String type = object.getString(ShareConstants.MEDIA_TYPE);
        if (type == null) {
            type = object.getString("og:type");
        }
        if (type == null) {
            onOpenGraphObjectStagedListener.onError(new FacebookException("Open Graph objects must contain a type value."));
            return;
        }
        JSONObject stagedObject = new JSONObject();
        stageCollectionValues(new C10239(object, stagedObject), new AnonymousClass11(stagedObject, type, new AnonymousClass10(onOpenGraphObjectStagedListener), onOpenGraphObjectStagedListener));
    }

    private void stagePhoto(SharePhoto photo, OnMapValueCompleteListener onPhotoStagedListener) {
        Bitmap bitmap = photo.getBitmap();
        Uri imageUrl = photo.getImageUrl();
        if (bitmap == null && imageUrl == null) {
            onPhotoStagedListener.onError(new FacebookException("Photos must have an imageURL or bitmap."));
            return;
        }
        Callback requestCallback = new AnonymousClass12(onPhotoStagedListener, photo);
        if (bitmap != null) {
            ShareInternalUtility.newUploadStagingResourceWithImageRequest(AccessToken.getCurrentAccessToken(), bitmap, requestCallback).executeAsync();
            return;
        }
        try {
            ShareInternalUtility.newUploadStagingResourceWithImageRequest(AccessToken.getCurrentAccessToken(), imageUrl, requestCallback).executeAsync();
        } catch (FileNotFoundException ex) {
            String message = ex.getLocalizedMessage();
            if (message == null) {
                message = "Error staging photo.";
            }
            onPhotoStagedListener.onError(new FacebookException(message));
        }
    }
}
