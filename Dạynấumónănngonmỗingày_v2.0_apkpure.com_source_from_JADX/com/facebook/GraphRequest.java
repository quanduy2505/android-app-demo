package com.facebook;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.location.Location;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.ParcelFileDescriptor.AutoCloseInputStream;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import com.facebook.appevents.AppEventsConstants;
import com.facebook.internal.AttributionIdentifiers;
import com.facebook.internal.InternalSettings;
import com.facebook.internal.Logger;
import com.facebook.internal.NativeProtocol;
import com.facebook.internal.ServerProtocol;
import com.facebook.internal.Utility;
import com.facebook.internal.Validate;
import com.facebook.share.internal.OpenGraphJSONUtility;
import com.facebook.share.internal.OpenGraphJSONUtility.PhotoJSONProcessor;
import com.facebook.share.internal.ShareConstants;
import com.facebook.share.model.ShareOpenGraphObject;
import com.facebook.share.model.SharePhoto;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import rx.android.BuildConfig;

public class GraphRequest {
    private static final String ACCEPT_LANGUAGE_HEADER = "Accept-Language";
    public static final String ACCESS_TOKEN_PARAM = "access_token";
    private static final String ATTACHED_FILES_PARAM = "attached_files";
    private static final String ATTACHMENT_FILENAME_PREFIX = "file";
    private static final String BATCH_APP_ID_PARAM = "batch_app_id";
    private static final String BATCH_BODY_PARAM = "body";
    private static final String BATCH_ENTRY_DEPENDS_ON_PARAM = "depends_on";
    private static final String BATCH_ENTRY_NAME_PARAM = "name";
    private static final String BATCH_ENTRY_OMIT_RESPONSE_ON_SUCCESS_PARAM = "omit_response_on_success";
    private static final String BATCH_METHOD_PARAM = "method";
    private static final String BATCH_PARAM = "batch";
    private static final String BATCH_RELATIVE_URL_PARAM = "relative_url";
    private static final String CAPTION_PARAM = "caption";
    private static final String CONTENT_ENCODING_HEADER = "Content-Encoding";
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String DEBUG_KEY = "__debug__";
    private static final String DEBUG_MESSAGES_KEY = "messages";
    private static final String DEBUG_MESSAGE_KEY = "message";
    private static final String DEBUG_MESSAGE_LINK_KEY = "link";
    private static final String DEBUG_MESSAGE_TYPE_KEY = "type";
    private static final String DEBUG_PARAM = "debug";
    private static final String DEBUG_SEVERITY_INFO = "info";
    private static final String DEBUG_SEVERITY_WARNING = "warning";
    public static final String FIELDS_PARAM = "fields";
    private static final String FORMAT_JSON = "json";
    private static final String FORMAT_PARAM = "format";
    private static final String GRAPH_PATH_FORMAT = "%s/%s";
    private static final String ISO_8601_FORMAT_STRING = "yyyy-MM-dd'T'HH:mm:ssZ";
    public static final int MAXIMUM_BATCH_SIZE = 50;
    private static final String ME = "me";
    private static final String MIME_BOUNDARY = "3i2ndDfv2rTHiSisAbouNdArYfORhtTPEefj3q2f";
    private static final String MY_FRIENDS = "me/friends";
    private static final String MY_PHOTOS = "me/photos";
    private static final String PICTURE_PARAM = "picture";
    private static final String SDK_ANDROID = "android";
    private static final String SDK_PARAM = "sdk";
    private static final String SEARCH = "search";
    public static final String TAG;
    private static final String USER_AGENT_BASE = "FBAndroidSDK";
    private static final String USER_AGENT_HEADER = "User-Agent";
    private static final String VIDEOS_SUFFIX = "/videos";
    private static String defaultBatchApplicationId;
    private static volatile String userAgent;
    private static Pattern versionPattern;
    private AccessToken accessToken;
    private String batchEntryDependsOn;
    private String batchEntryName;
    private boolean batchEntryOmitResultOnSuccess;
    private Callback callback;
    private JSONObject graphObject;
    private String graphPath;
    private HttpMethod httpMethod;
    private String overriddenURL;
    private Bundle parameters;
    private boolean skipClientToken;
    private Object tag;
    private String version;

    /* renamed from: com.facebook.GraphRequest.5 */
    static class C03735 implements Runnable {
        final /* synthetic */ ArrayList val$callbacks;
        final /* synthetic */ GraphRequestBatch val$requests;

        C03735(ArrayList arrayList, GraphRequestBatch graphRequestBatch) {
            this.val$callbacks = arrayList;
            this.val$requests = graphRequestBatch;
        }

        public void run() {
            Iterator it = this.val$callbacks.iterator();
            while (it.hasNext()) {
                Pair<Callback, GraphResponse> pair = (Pair) it.next();
                ((Callback) pair.first).onCompleted((GraphResponse) pair.second);
            }
            for (com.facebook.GraphRequestBatch.Callback batchCallback : this.val$requests.getCallbacks()) {
                batchCallback.onBatchCompleted(this.val$requests);
            }
        }
    }

    private static class Attachment {
        private final GraphRequest request;
        private final Object value;

        public Attachment(GraphRequest request, Object value) {
            this.request = request;
            this.value = value;
        }

        public GraphRequest getRequest() {
            return this.request;
        }

        public Object getValue() {
            return this.value;
        }
    }

    public interface Callback {
        void onCompleted(GraphResponse graphResponse);
    }

    public interface GraphJSONArrayCallback {
        void onCompleted(JSONArray jSONArray, GraphResponse graphResponse);
    }

    public interface GraphJSONObjectCallback {
        void onCompleted(JSONObject jSONObject, GraphResponse graphResponse);
    }

    private interface KeyValueSerializer {
        void writeString(String str, String str2) throws IOException;
    }

    public static class ParcelableResourceWithMimeType<RESOURCE extends Parcelable> implements Parcelable {
        public static final Creator<ParcelableResourceWithMimeType> CREATOR;
        private final String mimeType;
        private final RESOURCE resource;

        /* renamed from: com.facebook.GraphRequest.ParcelableResourceWithMimeType.1 */
        static class C03741 implements Creator<ParcelableResourceWithMimeType> {
            C03741() {
            }

            public ParcelableResourceWithMimeType createFromParcel(Parcel in) {
                return new ParcelableResourceWithMimeType(null);
            }

            public ParcelableResourceWithMimeType[] newArray(int size) {
                return new ParcelableResourceWithMimeType[size];
            }
        }

        public String getMimeType() {
            return this.mimeType;
        }

        public RESOURCE getResource() {
            return this.resource;
        }

        public int describeContents() {
            return 1;
        }

        public void writeToParcel(Parcel out, int flags) {
            out.writeString(this.mimeType);
            out.writeParcelable(this.resource, flags);
        }

        static {
            CREATOR = new C03741();
        }

        public ParcelableResourceWithMimeType(RESOURCE resource, String mimeType) {
            this.mimeType = mimeType;
            this.resource = resource;
        }

        private ParcelableResourceWithMimeType(Parcel in) {
            this.mimeType = in.readString();
            this.resource = in.readParcelable(FacebookSdk.getApplicationContext().getClassLoader());
        }
    }

    /* renamed from: com.facebook.GraphRequest.1 */
    static class C09821 implements Callback {
        final /* synthetic */ GraphJSONObjectCallback val$callback;

        C09821(GraphJSONObjectCallback graphJSONObjectCallback) {
            this.val$callback = graphJSONObjectCallback;
        }

        public void onCompleted(GraphResponse response) {
            if (this.val$callback != null) {
                this.val$callback.onCompleted(response.getJSONObject(), response);
            }
        }
    }

    /* renamed from: com.facebook.GraphRequest.2 */
    static class C09832 implements Callback {
        final /* synthetic */ GraphJSONArrayCallback val$callback;

        C09832(GraphJSONArrayCallback graphJSONArrayCallback) {
            this.val$callback = graphJSONArrayCallback;
        }

        public void onCompleted(GraphResponse response) {
            if (this.val$callback != null) {
                JSONObject result = response.getJSONObject();
                this.val$callback.onCompleted(result != null ? result.optJSONArray(ShareConstants.WEB_DIALOG_PARAM_DATA) : null, response);
            }
        }
    }

    /* renamed from: com.facebook.GraphRequest.3 */
    static class C09843 implements Callback {
        final /* synthetic */ GraphJSONArrayCallback val$callback;

        C09843(GraphJSONArrayCallback graphJSONArrayCallback) {
            this.val$callback = graphJSONArrayCallback;
        }

        public void onCompleted(GraphResponse response) {
            if (this.val$callback != null) {
                JSONObject result = response.getJSONObject();
                this.val$callback.onCompleted(result != null ? result.optJSONArray(ShareConstants.WEB_DIALOG_PARAM_DATA) : null, response);
            }
        }
    }

    /* renamed from: com.facebook.GraphRequest.4 */
    class C09854 implements Callback {
        final /* synthetic */ Callback val$callback;

        C09854(Callback callback) {
            this.val$callback = callback;
        }

        public void onCompleted(GraphResponse response) {
            JSONObject debug;
            JSONArray debugMessages;
            JSONObject responseObject = response.getJSONObject();
            if (responseObject != null) {
                debug = responseObject.optJSONObject(GraphRequest.DEBUG_KEY);
            } else {
                debug = null;
            }
            if (debug != null) {
                debugMessages = debug.optJSONArray(GraphRequest.DEBUG_MESSAGES_KEY);
            } else {
                debugMessages = null;
            }
            if (debugMessages != null) {
                for (int i = 0; i < debugMessages.length(); i++) {
                    String debugMessage;
                    String debugMessageType;
                    String debugMessageLink;
                    JSONObject debugMessageObject = debugMessages.optJSONObject(i);
                    if (debugMessageObject != null) {
                        debugMessage = debugMessageObject.optString(GraphRequest.DEBUG_MESSAGE_KEY);
                    } else {
                        debugMessage = null;
                    }
                    if (debugMessageObject != null) {
                        debugMessageType = debugMessageObject.optString(GraphRequest.DEBUG_MESSAGE_TYPE_KEY);
                    } else {
                        debugMessageType = null;
                    }
                    if (debugMessageObject != null) {
                        debugMessageLink = debugMessageObject.optString(GraphRequest.DEBUG_MESSAGE_LINK_KEY);
                    } else {
                        debugMessageLink = null;
                    }
                    if (!(debugMessage == null || debugMessageType == null)) {
                        LoggingBehavior behavior = LoggingBehavior.GRAPH_API_DEBUG_INFO;
                        if (debugMessageType.equals(GraphRequest.DEBUG_SEVERITY_WARNING)) {
                            behavior = LoggingBehavior.GRAPH_API_DEBUG_WARNING;
                        }
                        if (!Utility.isNullOrEmpty(debugMessageLink)) {
                            debugMessage = debugMessage + " Link: " + debugMessageLink;
                        }
                        Logger.log(behavior, GraphRequest.TAG, debugMessage);
                    }
                }
            }
            if (this.val$callback != null) {
                this.val$callback.onCompleted(response);
            }
        }
    }

    /* renamed from: com.facebook.GraphRequest.6 */
    class C09866 implements KeyValueSerializer {
        final /* synthetic */ ArrayList val$keysAndValues;

        C09866(ArrayList arrayList) {
            this.val$keysAndValues = arrayList;
        }

        public void writeString(String key, String value) throws IOException {
            this.val$keysAndValues.add(String.format(Locale.US, "%s=%s", new Object[]{key, URLEncoder.encode(value, HTTP.UTF_8)}));
        }
    }

    /* renamed from: com.facebook.GraphRequest.7 */
    static class C09877 implements PhotoJSONProcessor {
        C09877() {
        }

        public JSONObject toJSONObject(SharePhoto photo) {
            Uri photoUri = photo.getImageUrl();
            JSONObject photoJSONObject = new JSONObject();
            try {
                photoJSONObject.put(NativeProtocol.WEB_DIALOG_URL, photoUri.toString());
                return photoJSONObject;
            } catch (Throwable e) {
                throw new FacebookException("Unable to attach images", e);
            }
        }
    }

    public interface OnProgressCallback extends Callback {
        void onProgress(long j, long j2);
    }

    private static class Serializer implements KeyValueSerializer {
        private boolean firstWrite;
        private final Logger logger;
        private final OutputStream outputStream;
        private boolean useUrlEncode;

        public Serializer(OutputStream outputStream, Logger logger, boolean useUrlEncode) {
            this.firstWrite = true;
            this.useUrlEncode = false;
            this.outputStream = outputStream;
            this.logger = logger;
            this.useUrlEncode = useUrlEncode;
        }

        public void writeObject(String key, Object value, GraphRequest request) throws IOException {
            if (this.outputStream instanceof RequestOutputStream) {
                ((RequestOutputStream) this.outputStream).setCurrentRequest(request);
            }
            if (GraphRequest.isSupportedParameterType(value)) {
                writeString(key, GraphRequest.parameterToString(value));
            } else if (value instanceof Bitmap) {
                writeBitmap(key, (Bitmap) value);
            } else if (value instanceof byte[]) {
                writeBytes(key, (byte[]) value);
            } else if (value instanceof Uri) {
                writeContentUri(key, (Uri) value, null);
            } else if (value instanceof ParcelFileDescriptor) {
                writeFile(key, (ParcelFileDescriptor) value, null);
            } else if (value instanceof ParcelableResourceWithMimeType) {
                ParcelableResourceWithMimeType resourceWithMimeType = (ParcelableResourceWithMimeType) value;
                Parcelable resource = resourceWithMimeType.getResource();
                String mimeType = resourceWithMimeType.getMimeType();
                if (resource instanceof ParcelFileDescriptor) {
                    writeFile(key, (ParcelFileDescriptor) resource, mimeType);
                } else if (resource instanceof Uri) {
                    writeContentUri(key, (Uri) resource, mimeType);
                } else {
                    throw getInvalidTypeError();
                }
            } else {
                throw getInvalidTypeError();
            }
        }

        private RuntimeException getInvalidTypeError() {
            return new IllegalArgumentException("value is not a supported type.");
        }

        public void writeRequestsAsJson(String key, JSONArray requestJsonArray, Collection<GraphRequest> requests) throws IOException, JSONException {
            if (this.outputStream instanceof RequestOutputStream) {
                RequestOutputStream requestOutputStream = this.outputStream;
                writeContentDisposition(key, null, null);
                write("[", new Object[0]);
                int i = 0;
                for (GraphRequest request : requests) {
                    JSONObject requestJson = requestJsonArray.getJSONObject(i);
                    requestOutputStream.setCurrentRequest(request);
                    if (i > 0) {
                        write(",%s", requestJson.toString());
                    } else {
                        write("%s", requestJson.toString());
                    }
                    i++;
                }
                write("]", new Object[0]);
                if (this.logger != null) {
                    this.logger.appendKeyValue("    " + key, requestJsonArray.toString());
                    return;
                }
                return;
            }
            writeString(key, requestJsonArray.toString());
        }

        public void writeString(String key, String value) throws IOException {
            writeContentDisposition(key, null, null);
            writeLine("%s", value);
            writeRecordBoundary();
            if (this.logger != null) {
                this.logger.appendKeyValue("    " + key, value);
            }
        }

        public void writeBitmap(String key, Bitmap bitmap) throws IOException {
            writeContentDisposition(key, key, "image/png");
            bitmap.compress(CompressFormat.PNG, 100, this.outputStream);
            writeLine(BuildConfig.VERSION_NAME, new Object[0]);
            writeRecordBoundary();
            if (this.logger != null) {
                this.logger.appendKeyValue("    " + key, "<Image>");
            }
        }

        public void writeBytes(String key, byte[] bytes) throws IOException {
            writeContentDisposition(key, key, "content/unknown");
            this.outputStream.write(bytes);
            writeLine(BuildConfig.VERSION_NAME, new Object[0]);
            writeRecordBoundary();
            if (this.logger != null) {
                this.logger.appendKeyValue("    " + key, String.format(Locale.ROOT, "<Data: %d>", new Object[]{Integer.valueOf(bytes.length)}));
            }
        }

        public void writeContentUri(String key, Uri contentUri, String mimeType) throws IOException {
            if (mimeType == null) {
                mimeType = "content/unknown";
            }
            writeContentDisposition(key, key, mimeType);
            int totalBytes = 0;
            if (this.outputStream instanceof ProgressNoopOutputStream) {
                ((ProgressNoopOutputStream) this.outputStream).addProgress(Utility.getContentSize(contentUri));
            } else {
                totalBytes = 0 + Utility.copyAndCloseInputStream(FacebookSdk.getApplicationContext().getContentResolver().openInputStream(contentUri), this.outputStream);
            }
            writeLine(BuildConfig.VERSION_NAME, new Object[0]);
            writeRecordBoundary();
            if (this.logger != null) {
                this.logger.appendKeyValue("    " + key, String.format(Locale.ROOT, "<Data: %d>", new Object[]{Integer.valueOf(totalBytes)}));
            }
        }

        public void writeFile(String key, ParcelFileDescriptor descriptor, String mimeType) throws IOException {
            if (mimeType == null) {
                mimeType = "content/unknown";
            }
            writeContentDisposition(key, key, mimeType);
            int totalBytes = 0;
            if (this.outputStream instanceof ProgressNoopOutputStream) {
                ((ProgressNoopOutputStream) this.outputStream).addProgress(descriptor.getStatSize());
            } else {
                totalBytes = 0 + Utility.copyAndCloseInputStream(new AutoCloseInputStream(descriptor), this.outputStream);
            }
            writeLine(BuildConfig.VERSION_NAME, new Object[0]);
            writeRecordBoundary();
            if (this.logger != null) {
                this.logger.appendKeyValue("    " + key, String.format(Locale.ROOT, "<Data: %d>", new Object[]{Integer.valueOf(totalBytes)}));
            }
        }

        public void writeRecordBoundary() throws IOException {
            if (this.useUrlEncode) {
                this.outputStream.write("&".getBytes());
                return;
            }
            writeLine("--%s", GraphRequest.MIME_BOUNDARY);
        }

        public void writeContentDisposition(String name, String filename, String contentType) throws IOException {
            if (this.useUrlEncode) {
                this.outputStream.write(String.format("%s=", new Object[]{name}).getBytes());
                return;
            }
            write("Content-Disposition: form-data; name=\"%s\"", name);
            if (filename != null) {
                write("; filename=\"%s\"", filename);
            }
            writeLine(BuildConfig.VERSION_NAME, new Object[0]);
            if (contentType != null) {
                writeLine("%s: %s", GraphRequest.CONTENT_TYPE_HEADER, contentType);
            }
            writeLine(BuildConfig.VERSION_NAME, new Object[0]);
        }

        public void write(String format, Object... args) throws IOException {
            if (this.useUrlEncode) {
                this.outputStream.write(URLEncoder.encode(String.format(Locale.US, format, args), HTTP.UTF_8).getBytes());
                return;
            }
            if (this.firstWrite) {
                this.outputStream.write("--".getBytes());
                this.outputStream.write(GraphRequest.MIME_BOUNDARY.getBytes());
                this.outputStream.write("\r\n".getBytes());
                this.firstWrite = false;
            }
            this.outputStream.write(String.format(format, args).getBytes());
        }

        public void writeLine(String format, Object... args) throws IOException {
            write(format, args);
            if (!this.useUrlEncode) {
                write("\r\n", new Object[0]);
            }
        }
    }

    static {
        TAG = GraphRequest.class.getSimpleName();
        versionPattern = Pattern.compile("^/?v\\d+\\.\\d+/(.*)");
    }

    public GraphRequest() {
        this(null, null, null, null, null);
    }

    public GraphRequest(AccessToken accessToken, String graphPath) {
        this(accessToken, graphPath, null, null, null);
    }

    public GraphRequest(AccessToken accessToken, String graphPath, Bundle parameters, HttpMethod httpMethod) {
        this(accessToken, graphPath, parameters, httpMethod, null);
    }

    public GraphRequest(AccessToken accessToken, String graphPath, Bundle parameters, HttpMethod httpMethod, Callback callback) {
        this(accessToken, graphPath, parameters, httpMethod, callback, null);
    }

    public GraphRequest(AccessToken accessToken, String graphPath, Bundle parameters, HttpMethod httpMethod, Callback callback, String version) {
        this.batchEntryOmitResultOnSuccess = true;
        this.skipClientToken = false;
        this.accessToken = accessToken;
        this.graphPath = graphPath;
        this.version = version;
        setCallback(callback);
        setHttpMethod(httpMethod);
        if (parameters != null) {
            this.parameters = new Bundle(parameters);
        } else {
            this.parameters = new Bundle();
        }
        if (this.version == null) {
            this.version = FacebookSdk.getGraphApiVersion();
        }
    }

    GraphRequest(AccessToken accessToken, URL overriddenURL) {
        this.batchEntryOmitResultOnSuccess = true;
        this.skipClientToken = false;
        this.accessToken = accessToken;
        this.overriddenURL = overriddenURL.toString();
        setHttpMethod(HttpMethod.GET);
        this.parameters = new Bundle();
    }

    public static GraphRequest newDeleteObjectRequest(AccessToken accessToken, String id, Callback callback) {
        return new GraphRequest(accessToken, id, null, HttpMethod.DELETE, callback);
    }

    public static GraphRequest newMeRequest(AccessToken accessToken, GraphJSONObjectCallback callback) {
        return new GraphRequest(accessToken, ME, null, null, new C09821(callback));
    }

    public static GraphRequest newPostRequest(AccessToken accessToken, String graphPath, JSONObject graphObject, Callback callback) {
        GraphRequest request = new GraphRequest(accessToken, graphPath, null, HttpMethod.POST, callback);
        request.setGraphObject(graphObject);
        return request;
    }

    public static GraphRequest newMyFriendsRequest(AccessToken accessToken, GraphJSONArrayCallback callback) {
        return new GraphRequest(accessToken, MY_FRIENDS, null, null, new C09832(callback));
    }

    public static GraphRequest newGraphPathRequest(AccessToken accessToken, String graphPath, Callback callback) {
        return new GraphRequest(accessToken, graphPath, null, null, callback);
    }

    public static GraphRequest newPlacesSearchRequest(AccessToken accessToken, Location location, int radiusInMeters, int resultsLimit, String searchText, GraphJSONArrayCallback callback) {
        if (location == null && Utility.isNullOrEmpty(searchText)) {
            throw new FacebookException("Either location or searchText must be specified.");
        }
        Bundle parameters = new Bundle(5);
        parameters.putString(DEBUG_MESSAGE_TYPE_KEY, "place");
        parameters.putInt("limit", resultsLimit);
        if (location != null) {
            parameters.putString("center", String.format(Locale.US, "%f,%f", new Object[]{Double.valueOf(location.getLatitude()), Double.valueOf(location.getLongitude())}));
            parameters.putInt("distance", radiusInMeters);
        }
        if (!Utility.isNullOrEmpty(searchText)) {
            parameters.putString("q", searchText);
        }
        return new GraphRequest(accessToken, SEARCH, parameters, HttpMethod.GET, new C09843(callback));
    }

    public static GraphRequest newUploadPhotoRequest(AccessToken accessToken, String graphPath, Bitmap image, String caption, Bundle params, Callback callback) {
        graphPath = getDefaultPhotoPathIfNull(graphPath);
        Bundle parameters = new Bundle();
        if (params != null) {
            parameters.putAll(params);
        }
        parameters.putParcelable(PICTURE_PARAM, image);
        if (!(caption == null || caption.isEmpty())) {
            parameters.putString(CAPTION_PARAM, caption);
        }
        return new GraphRequest(accessToken, graphPath, parameters, HttpMethod.POST, callback);
    }

    public static GraphRequest newUploadPhotoRequest(AccessToken accessToken, String graphPath, File file, String caption, Bundle params, Callback callback) throws FileNotFoundException {
        graphPath = getDefaultPhotoPathIfNull(graphPath);
        ParcelFileDescriptor descriptor = ParcelFileDescriptor.open(file, 268435456);
        Bundle parameters = new Bundle();
        if (params != null) {
            parameters.putAll(params);
        }
        parameters.putParcelable(PICTURE_PARAM, descriptor);
        if (!(caption == null || caption.isEmpty())) {
            parameters.putString(CAPTION_PARAM, caption);
        }
        return new GraphRequest(accessToken, graphPath, parameters, HttpMethod.POST, callback);
    }

    public static GraphRequest newUploadPhotoRequest(AccessToken accessToken, String graphPath, Uri photoUri, String caption, Bundle params, Callback callback) throws FileNotFoundException {
        graphPath = getDefaultPhotoPathIfNull(graphPath);
        if (Utility.isFileUri(photoUri)) {
            return newUploadPhotoRequest(accessToken, graphPath, new File(photoUri.getPath()), caption, params, callback);
        } else if (Utility.isContentUri(photoUri)) {
            Bundle parameters = new Bundle();
            if (params != null) {
                parameters.putAll(params);
            }
            parameters.putParcelable(PICTURE_PARAM, photoUri);
            if (!(caption == null || caption.isEmpty())) {
                parameters.putString(CAPTION_PARAM, caption);
            }
            return new GraphRequest(accessToken, graphPath, parameters, HttpMethod.POST, callback);
        } else {
            throw new FacebookException("The photo Uri must be either a file:// or content:// Uri");
        }
    }

    public static GraphRequest newCustomAudienceThirdPartyIdRequest(AccessToken accessToken, Context context, String applicationId, Callback callback) {
        if (applicationId == null && accessToken != null) {
            applicationId = accessToken.getApplicationId();
        }
        if (applicationId == null) {
            applicationId = Utility.getMetadataApplicationId(context);
        }
        if (applicationId == null) {
            throw new FacebookException("Facebook App ID cannot be determined");
        }
        String endpoint = applicationId + "/custom_audience_third_party_id";
        AttributionIdentifiers attributionIdentifiers = AttributionIdentifiers.getAttributionIdentifiers(context);
        Bundle parameters = new Bundle();
        if (accessToken == null) {
            if (attributionIdentifiers == null) {
                throw new FacebookException("There is no access token and attribution identifiers could not be retrieved");
            }
            String udid;
            if (attributionIdentifiers.getAttributionId() != null) {
                udid = attributionIdentifiers.getAttributionId();
            } else {
                udid = attributionIdentifiers.getAndroidAdvertiserId();
            }
            if (attributionIdentifiers.getAttributionId() != null) {
                parameters.putString("udid", udid);
            }
        }
        if (FacebookSdk.getLimitEventAndDataUsage(context) || (attributionIdentifiers != null && attributionIdentifiers.isTrackingLimited())) {
            parameters.putString("limit_event_usage", AppEventsConstants.EVENT_PARAM_VALUE_YES);
        }
        return new GraphRequest(accessToken, endpoint, parameters, HttpMethod.GET, callback);
    }

    public static GraphRequest newCustomAudienceThirdPartyIdRequest(AccessToken accessToken, Context context, Callback callback) {
        return newCustomAudienceThirdPartyIdRequest(accessToken, context, null, callback);
    }

    public final JSONObject getGraphObject() {
        return this.graphObject;
    }

    public final void setGraphObject(JSONObject graphObject) {
        this.graphObject = graphObject;
    }

    public final String getGraphPath() {
        return this.graphPath;
    }

    public final void setGraphPath(String graphPath) {
        this.graphPath = graphPath;
    }

    public final HttpMethod getHttpMethod() {
        return this.httpMethod;
    }

    public final void setHttpMethod(HttpMethod httpMethod) {
        if (this.overriddenURL == null || httpMethod == HttpMethod.GET) {
            if (httpMethod == null) {
                httpMethod = HttpMethod.GET;
            }
            this.httpMethod = httpMethod;
            return;
        }
        throw new FacebookException("Can't change HTTP method on request with overridden URL.");
    }

    public final String getVersion() {
        return this.version;
    }

    public final void setVersion(String version) {
        this.version = version;
    }

    public final void setSkipClientToken(boolean skipClientToken) {
        this.skipClientToken = skipClientToken;
    }

    public final Bundle getParameters() {
        return this.parameters;
    }

    public final void setParameters(Bundle parameters) {
        this.parameters = parameters;
    }

    public final AccessToken getAccessToken() {
        return this.accessToken;
    }

    public final void setAccessToken(AccessToken accessToken) {
        this.accessToken = accessToken;
    }

    public final String getBatchEntryName() {
        return this.batchEntryName;
    }

    public final void setBatchEntryName(String batchEntryName) {
        this.batchEntryName = batchEntryName;
    }

    public final String getBatchEntryDependsOn() {
        return this.batchEntryDependsOn;
    }

    public final void setBatchEntryDependsOn(String batchEntryDependsOn) {
        this.batchEntryDependsOn = batchEntryDependsOn;
    }

    public final boolean getBatchEntryOmitResultOnSuccess() {
        return this.batchEntryOmitResultOnSuccess;
    }

    public final void setBatchEntryOmitResultOnSuccess(boolean batchEntryOmitResultOnSuccess) {
        this.batchEntryOmitResultOnSuccess = batchEntryOmitResultOnSuccess;
    }

    public static final String getDefaultBatchApplicationId() {
        return defaultBatchApplicationId;
    }

    public static final void setDefaultBatchApplicationId(String applicationId) {
        defaultBatchApplicationId = applicationId;
    }

    public final Callback getCallback() {
        return this.callback;
    }

    public final void setCallback(Callback callback) {
        if (FacebookSdk.isLoggingBehaviorEnabled(LoggingBehavior.GRAPH_API_DEBUG_INFO) || FacebookSdk.isLoggingBehaviorEnabled(LoggingBehavior.GRAPH_API_DEBUG_WARNING)) {
            this.callback = new C09854(callback);
        } else {
            this.callback = callback;
        }
    }

    public final void setTag(Object tag) {
        this.tag = tag;
    }

    public final Object getTag() {
        return this.tag;
    }

    public final GraphResponse executeAndWait() {
        return executeAndWait(this);
    }

    public final GraphRequestAsyncTask executeAsync() {
        return executeBatchAsync(this);
    }

    public static HttpURLConnection toHttpConnection(GraphRequest... requests) {
        return toHttpConnection(Arrays.asList(requests));
    }

    public static HttpURLConnection toHttpConnection(Collection<GraphRequest> requests) {
        Validate.notEmptyAndContainsNoNulls(requests, "requests");
        return toHttpConnection(new GraphRequestBatch((Collection) requests));
    }

    public static HttpURLConnection toHttpConnection(GraphRequestBatch requests) {
        Throwable e;
        validateFieldsParamForGetRequests(requests);
        try {
            URL url;
            if (requests.size() == 1) {
                url = new URL(requests.get(0).getUrlForSingleRequest());
            } else {
                url = new URL(ServerProtocol.getGraphUrlBase());
            }
            HttpURLConnection httpURLConnection = null;
            try {
                httpURLConnection = createConnection(url);
                serializeToUrlConnection(requests, httpURLConnection);
                return httpURLConnection;
            } catch (IOException e2) {
                e = e2;
                Utility.disconnectQuietly(httpURLConnection);
                throw new FacebookException("could not construct request body", e);
            } catch (JSONException e3) {
                e = e3;
                Utility.disconnectQuietly(httpURLConnection);
                throw new FacebookException("could not construct request body", e);
            }
        } catch (Throwable e4) {
            throw new FacebookException("could not construct URL for request", e4);
        }
    }

    public static GraphResponse executeAndWait(GraphRequest request) {
        List<GraphResponse> responses = executeBatchAndWait(request);
        if (responses != null && responses.size() == 1) {
            return (GraphResponse) responses.get(0);
        }
        throw new FacebookException("invalid state: expected a single response");
    }

    public static List<GraphResponse> executeBatchAndWait(GraphRequest... requests) {
        Validate.notNull(requests, "requests");
        return executeBatchAndWait(Arrays.asList(requests));
    }

    public static List<GraphResponse> executeBatchAndWait(Collection<GraphRequest> requests) {
        return executeBatchAndWait(new GraphRequestBatch((Collection) requests));
    }

    public static List<GraphResponse> executeBatchAndWait(GraphRequestBatch requests) {
        List<GraphResponse> responses;
        Validate.notEmptyAndContainsNoNulls(requests, "requests");
        HttpURLConnection connection = null;
        try {
            connection = toHttpConnection(requests);
            responses = executeConnectionAndWait(connection, requests);
        } catch (Throwable ex) {
            responses = GraphResponse.constructErrorResponses(requests.getRequests(), null, new FacebookException(ex));
            runCallbacks(requests, responses);
        } finally {
            Utility.disconnectQuietly(connection);
        }
        return responses;
    }

    public static GraphRequestAsyncTask executeBatchAsync(GraphRequest... requests) {
        Validate.notNull(requests, "requests");
        return executeBatchAsync(Arrays.asList(requests));
    }

    public static GraphRequestAsyncTask executeBatchAsync(Collection<GraphRequest> requests) {
        return executeBatchAsync(new GraphRequestBatch((Collection) requests));
    }

    public static GraphRequestAsyncTask executeBatchAsync(GraphRequestBatch requests) {
        Validate.notEmptyAndContainsNoNulls(requests, "requests");
        GraphRequestAsyncTask asyncTask = new GraphRequestAsyncTask(requests);
        asyncTask.executeOnExecutor(FacebookSdk.getExecutor(), new Void[0]);
        return asyncTask;
    }

    public static List<GraphResponse> executeConnectionAndWait(HttpURLConnection connection, Collection<GraphRequest> requests) {
        return executeConnectionAndWait(connection, new GraphRequestBatch((Collection) requests));
    }

    public static List<GraphResponse> executeConnectionAndWait(HttpURLConnection connection, GraphRequestBatch requests) {
        List<GraphResponse> responses = GraphResponse.fromHttpConnection(connection, requests);
        Utility.disconnectQuietly(connection);
        if (requests.size() != responses.size()) {
            throw new FacebookException(String.format(Locale.US, "Received %d responses while expecting %d", new Object[]{Integer.valueOf(responses.size()), Integer.valueOf(requests.size())}));
        }
        runCallbacks(requests, responses);
        AccessTokenManager.getInstance().extendAccessTokenIfNeeded();
        return responses;
    }

    public static GraphRequestAsyncTask executeConnectionAsync(HttpURLConnection connection, GraphRequestBatch requests) {
        return executeConnectionAsync(null, connection, requests);
    }

    public static GraphRequestAsyncTask executeConnectionAsync(Handler callbackHandler, HttpURLConnection connection, GraphRequestBatch requests) {
        Validate.notNull(connection, "connection");
        GraphRequestAsyncTask asyncTask = new GraphRequestAsyncTask(connection, requests);
        requests.setCallbackHandler(callbackHandler);
        asyncTask.executeOnExecutor(FacebookSdk.getExecutor(), new Void[0]);
        return asyncTask;
    }

    public String toString() {
        return "{Request: " + " accessToken: " + (this.accessToken == null ? "null" : this.accessToken) + ", graphPath: " + this.graphPath + ", graphObject: " + this.graphObject + ", httpMethod: " + this.httpMethod + ", parameters: " + this.parameters + "}";
    }

    static void runCallbacks(GraphRequestBatch requests, List<GraphResponse> responses) {
        int numRequests = requests.size();
        ArrayList<Pair<Callback, GraphResponse>> callbacks = new ArrayList();
        for (int i = 0; i < numRequests; i++) {
            GraphRequest request = requests.get(i);
            if (request.callback != null) {
                callbacks.add(new Pair(request.callback, responses.get(i)));
            }
        }
        if (callbacks.size() > 0) {
            Runnable runnable = new C03735(callbacks, requests);
            Handler callbackHandler = requests.getCallbackHandler();
            if (callbackHandler == null) {
                runnable.run();
            } else {
                callbackHandler.post(runnable);
            }
        }
    }

    private static String getDefaultPhotoPathIfNull(String graphPath) {
        return graphPath == null ? MY_PHOTOS : graphPath;
    }

    private static HttpURLConnection createConnection(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty(USER_AGENT_HEADER, getUserAgent());
        connection.setRequestProperty(ACCEPT_LANGUAGE_HEADER, Locale.getDefault().toString());
        connection.setChunkedStreamingMode(0);
        return connection;
    }

    private void addCommonParameters() {
        if (this.accessToken != null) {
            if (!this.parameters.containsKey(ACCESS_TOKEN_PARAM)) {
                String token = this.accessToken.getToken();
                Logger.registerAccessToken(token);
                this.parameters.putString(ACCESS_TOKEN_PARAM, token);
            }
        } else if (!(this.skipClientToken || this.parameters.containsKey(ACCESS_TOKEN_PARAM))) {
            String appID = FacebookSdk.getApplicationId();
            String clientToken = FacebookSdk.getClientToken();
            if (Utility.isNullOrEmpty(appID) || Utility.isNullOrEmpty(clientToken)) {
                Log.d(TAG, "Warning: Request without access token missing application ID or client token.");
            } else {
                this.parameters.putString(ACCESS_TOKEN_PARAM, appID + "|" + clientToken);
            }
        }
        this.parameters.putString(SDK_PARAM, SDK_ANDROID);
        this.parameters.putString(FORMAT_PARAM, FORMAT_JSON);
        if (FacebookSdk.isLoggingBehaviorEnabled(LoggingBehavior.GRAPH_API_DEBUG_INFO)) {
            this.parameters.putString(DEBUG_PARAM, DEBUG_SEVERITY_INFO);
        } else if (FacebookSdk.isLoggingBehaviorEnabled(LoggingBehavior.GRAPH_API_DEBUG_WARNING)) {
            this.parameters.putString(DEBUG_PARAM, DEBUG_SEVERITY_WARNING);
        }
    }

    private String appendParametersToBaseUrl(String baseUrl) {
        Builder uriBuilder = Uri.parse(baseUrl).buildUpon();
        for (String key : this.parameters.keySet()) {
            Object value = this.parameters.get(key);
            if (value == null) {
                value = BuildConfig.VERSION_NAME;
            }
            if (isSupportedParameterType(value)) {
                uriBuilder.appendQueryParameter(key, parameterToString(value).toString());
            } else if (this.httpMethod == HttpMethod.GET) {
                throw new IllegalArgumentException(String.format(Locale.US, "Unsupported parameter type for GET request: %s", new Object[]{value.getClass().getSimpleName()}));
            }
        }
        return uriBuilder.toString();
    }

    final String getRelativeUrlForBatchedRequest() {
        if (this.overriddenURL != null) {
            throw new FacebookException("Can't override URL for a batch request");
        }
        String baseUrl = String.format(GRAPH_PATH_FORMAT, new Object[]{ServerProtocol.getGraphUrlBase(), getGraphPathWithVersion()});
        addCommonParameters();
        Uri uri = Uri.parse(appendParametersToBaseUrl(baseUrl));
        return String.format("%s?%s", new Object[]{uri.getPath(), uri.getQuery()});
    }

    final String getUrlForSingleRequest() {
        if (this.overriddenURL != null) {
            return this.overriddenURL.toString();
        }
        String graphBaseUrlBase;
        if (getHttpMethod() == HttpMethod.POST && this.graphPath != null && this.graphPath.endsWith(VIDEOS_SUFFIX)) {
            graphBaseUrlBase = ServerProtocol.getGraphVideoUrlBase();
        } else {
            graphBaseUrlBase = ServerProtocol.getGraphUrlBase();
        }
        String baseUrl = String.format(GRAPH_PATH_FORMAT, new Object[]{graphBaseUrlBase, getGraphPathWithVersion()});
        addCommonParameters();
        return appendParametersToBaseUrl(baseUrl);
    }

    private String getGraphPathWithVersion() {
        if (versionPattern.matcher(this.graphPath).matches()) {
            return this.graphPath;
        }
        return String.format(GRAPH_PATH_FORMAT, new Object[]{this.version, this.graphPath});
    }

    private void serializeToBatch(JSONArray batch, Map<String, Attachment> attachments) throws JSONException, IOException {
        JSONObject batchEntry = new JSONObject();
        if (this.batchEntryName != null) {
            batchEntry.put(BATCH_ENTRY_NAME_PARAM, this.batchEntryName);
            batchEntry.put(BATCH_ENTRY_OMIT_RESPONSE_ON_SUCCESS_PARAM, this.batchEntryOmitResultOnSuccess);
        }
        if (this.batchEntryDependsOn != null) {
            batchEntry.put(BATCH_ENTRY_DEPENDS_ON_PARAM, this.batchEntryDependsOn);
        }
        String relativeURL = getRelativeUrlForBatchedRequest();
        batchEntry.put(BATCH_RELATIVE_URL_PARAM, relativeURL);
        batchEntry.put(BATCH_METHOD_PARAM, this.httpMethod);
        if (this.accessToken != null) {
            Logger.registerAccessToken(this.accessToken.getToken());
        }
        ArrayList<String> attachmentNames = new ArrayList();
        for (String key : this.parameters.keySet()) {
            Object value = this.parameters.get(key);
            if (isSupportedAttachmentType(value)) {
                String name = String.format(Locale.ROOT, "%s%d", new Object[]{ATTACHMENT_FILENAME_PREFIX, Integer.valueOf(attachments.size())});
                attachmentNames.add(name);
                attachments.put(name, new Attachment(this, value));
            }
        }
        if (!attachmentNames.isEmpty()) {
            batchEntry.put(ATTACHED_FILES_PARAM, TextUtils.join(",", attachmentNames));
        }
        if (this.graphObject != null) {
            ArrayList<String> keysAndValues = new ArrayList();
            processGraphObject(this.graphObject, relativeURL, new C09866(keysAndValues));
            batchEntry.put(BATCH_BODY_PARAM, TextUtils.join("&", keysAndValues));
        }
        batch.put(batchEntry);
    }

    private static boolean hasOnProgressCallbacks(GraphRequestBatch requests) {
        for (com.facebook.GraphRequestBatch.Callback callback : requests.getCallbacks()) {
            if (callback instanceof com.facebook.GraphRequestBatch.OnProgressCallback) {
                return true;
            }
        }
        Iterator it = requests.iterator();
        while (it.hasNext()) {
            if (((GraphRequest) it.next()).getCallback() instanceof OnProgressCallback) {
                return true;
            }
        }
        return false;
    }

    private static void setConnectionContentType(HttpURLConnection connection, boolean shouldUseGzip) {
        if (shouldUseGzip) {
            connection.setRequestProperty(CONTENT_TYPE_HEADER, URLEncodedUtils.CONTENT_TYPE);
            connection.setRequestProperty(CONTENT_ENCODING_HEADER, "gzip");
            return;
        }
        connection.setRequestProperty(CONTENT_TYPE_HEADER, getMimeContentType());
    }

    private static boolean isGzipCompressible(GraphRequestBatch requests) {
        Iterator it = requests.iterator();
        while (it.hasNext()) {
            GraphRequest request = (GraphRequest) it.next();
            for (String key : request.parameters.keySet()) {
                if (isSupportedAttachmentType(request.parameters.get(key))) {
                    return false;
                }
            }
        }
        return true;
    }

    static final boolean shouldWarnOnMissingFieldsParam(GraphRequest request) {
        boolean z = false;
        String version = request.getVersion();
        if (Utility.isNullOrEmpty(version)) {
            return true;
        }
        if (version.startsWith("v")) {
            version = version.substring(1);
        }
        String[] versionParts = version.split("\\.");
        if ((versionParts.length >= 2 && Integer.parseInt(versionParts[0]) > 2) || (Integer.parseInt(versionParts[0]) >= 2 && Integer.parseInt(versionParts[1]) >= 4)) {
            z = true;
        }
        return z;
    }

    static final void validateFieldsParamForGetRequests(GraphRequestBatch requests) {
        Iterator it = requests.iterator();
        while (it.hasNext()) {
            GraphRequest request = (GraphRequest) it.next();
            if (HttpMethod.GET.equals(request.getHttpMethod()) && shouldWarnOnMissingFieldsParam(request)) {
                Bundle params = request.getParameters();
                if (!params.containsKey(FIELDS_PARAM) || Utility.isNullOrEmpty(params.getString(FIELDS_PARAM))) {
                    Logger.log(LoggingBehavior.DEVELOPER_ERRORS, 5, "Request", "starting with Graph API v2.4, GET requests for /%s should contain an explicit \"fields\" parameter.", request.getGraphPath());
                }
            }
        }
    }

    static final void serializeToUrlConnection(GraphRequestBatch requests, HttpURLConnection connection) throws IOException, JSONException {
        Throwable th;
        Logger logger = new Logger(LoggingBehavior.REQUESTS, "Request");
        int numRequests = requests.size();
        boolean shouldUseGzip = isGzipCompressible(requests);
        HttpMethod connectionHttpMethod = numRequests == 1 ? requests.get(0).httpMethod : HttpMethod.POST;
        connection.setRequestMethod(connectionHttpMethod.name());
        setConnectionContentType(connection, shouldUseGzip);
        URL url = connection.getURL();
        logger.append("Request:\n");
        logger.appendKeyValue("Id", requests.getId());
        logger.appendKeyValue("URL", url);
        logger.appendKeyValue("Method", connection.getRequestMethod());
        logger.appendKeyValue(USER_AGENT_HEADER, connection.getRequestProperty(USER_AGENT_HEADER));
        logger.appendKeyValue(CONTENT_TYPE_HEADER, connection.getRequestProperty(CONTENT_TYPE_HEADER));
        connection.setConnectTimeout(requests.getTimeout());
        connection.setReadTimeout(requests.getTimeout());
        if (connectionHttpMethod == HttpMethod.POST) {
            connection.setDoOutput(true);
            OutputStream outputStream = null;
            try {
                OutputStream outputStream2 = new BufferedOutputStream(connection.getOutputStream());
                if (shouldUseGzip) {
                    try {
                        outputStream2 = new GZIPOutputStream(outputStream2);
                    } catch (Throwable th2) {
                        th = th2;
                        outputStream = outputStream2;
                        if (outputStream != null) {
                            outputStream.close();
                        }
                        throw th;
                    }
                }
                if (hasOnProgressCallbacks(requests)) {
                    ProgressNoopOutputStream countingStream = new ProgressNoopOutputStream(requests.getCallbackHandler());
                    processRequest(requests, null, numRequests, url, countingStream, shouldUseGzip);
                    outputStream = new ProgressOutputStream(outputStream2, requests, countingStream.getProgressMap(), (long) countingStream.getMaxProgress());
                } else {
                    outputStream = outputStream2;
                }
                processRequest(requests, logger, numRequests, url, outputStream, shouldUseGzip);
                if (outputStream != null) {
                    outputStream.close();
                }
                logger.log();
                return;
            } catch (Throwable th3) {
                th = th3;
                if (outputStream != null) {
                    outputStream.close();
                }
                throw th;
            }
        }
        logger.log();
    }

    private static void processRequest(GraphRequestBatch requests, Logger logger, int numRequests, URL url, OutputStream outputStream, boolean shouldUseGzip) throws IOException, JSONException {
        Serializer serializer = new Serializer(outputStream, logger, shouldUseGzip);
        Map<String, Attachment> attachments;
        if (numRequests == 1) {
            GraphRequest request = requests.get(0);
            attachments = new HashMap();
            for (String key : request.parameters.keySet()) {
                Object value = request.parameters.get(key);
                if (isSupportedAttachmentType(value)) {
                    attachments.put(key, new Attachment(request, value));
                }
            }
            if (logger != null) {
                logger.append("  Parameters:\n");
            }
            serializeParameters(request.parameters, serializer, request);
            if (logger != null) {
                logger.append("  Attachments:\n");
            }
            serializeAttachments(attachments, serializer);
            if (request.graphObject != null) {
                processGraphObject(request.graphObject, url.getPath(), serializer);
                return;
            }
            return;
        }
        String batchAppID = getBatchAppId(requests);
        if (Utility.isNullOrEmpty(batchAppID)) {
            throw new FacebookException("App ID was not specified at the request or Settings.");
        }
        serializer.writeString(BATCH_APP_ID_PARAM, batchAppID);
        attachments = new HashMap();
        serializeRequestsAsJSON(serializer, requests, attachments);
        if (logger != null) {
            logger.append("  Attachments:\n");
        }
        serializeAttachments(attachments, serializer);
    }

    private static boolean isMeRequest(String path) {
        Matcher matcher = versionPattern.matcher(path);
        if (matcher.matches()) {
            path = matcher.group(1);
        }
        if (path.startsWith("me/") || path.startsWith("/me/")) {
            return true;
        }
        return false;
    }

    private static void processGraphObject(JSONObject graphObject, String path, KeyValueSerializer serializer) throws IOException {
        boolean isOGAction = false;
        if (isMeRequest(path)) {
            int colonLocation = path.indexOf(":");
            int questionMarkLocation = path.indexOf("?");
            if (colonLocation <= 3 || (questionMarkLocation != -1 && colonLocation >= questionMarkLocation)) {
                isOGAction = false;
            } else {
                isOGAction = true;
            }
        }
        Iterator<String> keyIterator = graphObject.keys();
        while (keyIterator.hasNext()) {
            boolean passByValue;
            String key = (String) keyIterator.next();
            Object value = graphObject.opt(key);
            if (isOGAction && key.equalsIgnoreCase("image")) {
                passByValue = true;
            } else {
                passByValue = false;
            }
            processGraphObjectProperty(key, value, serializer, passByValue);
        }
    }

    public static GraphRequest createOpenGraphObject(ShareOpenGraphObject openGraphObject) throws FacebookException {
        String type = openGraphObject.getString(DEBUG_MESSAGE_TYPE_KEY);
        if (type == null) {
            type = openGraphObject.getString("og:type");
        }
        if (type == null) {
            throw new FacebookException("Open graph object type cannot be null");
        }
        try {
            JSONObject stagedObject = (JSONObject) OpenGraphJSONUtility.toJSONValue(openGraphObject, new C09877());
            String ogType = type;
            Bundle parameters = new Bundle();
            parameters.putString("object", stagedObject.toString());
            return new GraphRequest(AccessToken.getCurrentAccessToken(), String.format(Locale.ROOT, GRAPH_PATH_FORMAT, new Object[]{ME, "objects/" + ogType}), parameters, HttpMethod.POST);
        } catch (JSONException e) {
            throw new FacebookException(e.getMessage());
        }
    }

    private static void processGraphObjectProperty(String key, Object value, KeyValueSerializer serializer, boolean passByValue) throws IOException {
        Class<?> valueClass = value.getClass();
        if (JSONObject.class.isAssignableFrom(valueClass)) {
            JSONObject jsonObject = (JSONObject) value;
            if (passByValue) {
                Iterator<String> keys = jsonObject.keys();
                while (keys.hasNext()) {
                    Object[] objArr = new Object[]{key, (String) keys.next()};
                    processGraphObjectProperty(String.format("%s[%s]", objArr), jsonObject.opt((String) keys.next()), serializer, passByValue);
                }
            } else if (jsonObject.has(ShareConstants.WEB_DIALOG_PARAM_ID)) {
                processGraphObjectProperty(key, jsonObject.optString(ShareConstants.WEB_DIALOG_PARAM_ID), serializer, passByValue);
            } else if (jsonObject.has(NativeProtocol.WEB_DIALOG_URL)) {
                processGraphObjectProperty(key, jsonObject.optString(NativeProtocol.WEB_DIALOG_URL), serializer, passByValue);
            } else if (jsonObject.has(NativeProtocol.OPEN_GRAPH_CREATE_OBJECT_KEY)) {
                processGraphObjectProperty(key, jsonObject.toString(), serializer, passByValue);
            }
        } else if (JSONArray.class.isAssignableFrom(valueClass)) {
            JSONArray jsonArray = (JSONArray) value;
            int length = jsonArray.length();
            for (int i = 0; i < length; i++) {
                processGraphObjectProperty(String.format(Locale.ROOT, "%s[%d]", new Object[]{key, Integer.valueOf(i)}), jsonArray.opt(i), serializer, passByValue);
            }
        } else if (String.class.isAssignableFrom(valueClass) || Number.class.isAssignableFrom(valueClass) || Boolean.class.isAssignableFrom(valueClass)) {
            serializer.writeString(key, value.toString());
        } else if (Date.class.isAssignableFrom(valueClass)) {
            KeyValueSerializer keyValueSerializer = serializer;
            String str = key;
            keyValueSerializer.writeString(str, new SimpleDateFormat(ISO_8601_FORMAT_STRING, Locale.US).format((Date) value));
        }
    }

    private static void serializeParameters(Bundle bundle, Serializer serializer, GraphRequest request) throws IOException {
        for (String key : bundle.keySet()) {
            Object value = bundle.get(key);
            if (isSupportedParameterType(value)) {
                serializer.writeObject(key, value, request);
            }
        }
    }

    private static void serializeAttachments(Map<String, Attachment> attachments, Serializer serializer) throws IOException {
        for (String key : attachments.keySet()) {
            Attachment attachment = (Attachment) attachments.get(key);
            if (isSupportedAttachmentType(attachment.getValue())) {
                serializer.writeObject(key, attachment.getValue(), attachment.getRequest());
            }
        }
    }

    private static void serializeRequestsAsJSON(Serializer serializer, Collection<GraphRequest> requests, Map<String, Attachment> attachments) throws JSONException, IOException {
        JSONArray batch = new JSONArray();
        for (GraphRequest request : requests) {
            request.serializeToBatch(batch, attachments);
        }
        serializer.writeRequestsAsJson(BATCH_PARAM, batch, requests);
    }

    private static String getMimeContentType() {
        return String.format("multipart/form-data; boundary=%s", new Object[]{MIME_BOUNDARY});
    }

    private static String getUserAgent() {
        if (userAgent == null) {
            userAgent = String.format("%s.%s", new Object[]{USER_AGENT_BASE, FacebookSdkVersion.BUILD});
            if (!Utility.isNullOrEmpty(InternalSettings.getCustomUserAgent())) {
                userAgent = String.format(Locale.ROOT, GRAPH_PATH_FORMAT, new Object[]{userAgent, customUserAgent});
            }
        }
        return userAgent;
    }

    private static String getBatchAppId(GraphRequestBatch batch) {
        if (!Utility.isNullOrEmpty(batch.getBatchApplicationId())) {
            return batch.getBatchApplicationId();
        }
        Iterator it = batch.iterator();
        while (it.hasNext()) {
            AccessToken accessToken = ((GraphRequest) it.next()).accessToken;
            if (accessToken != null) {
                String applicationId = accessToken.getApplicationId();
                if (applicationId != null) {
                    return applicationId;
                }
            }
        }
        if (Utility.isNullOrEmpty(defaultBatchApplicationId)) {
            return FacebookSdk.getApplicationId();
        }
        return defaultBatchApplicationId;
    }

    private static boolean isSupportedAttachmentType(Object value) {
        return (value instanceof Bitmap) || (value instanceof byte[]) || (value instanceof Uri) || (value instanceof ParcelFileDescriptor) || (value instanceof ParcelableResourceWithMimeType);
    }

    private static boolean isSupportedParameterType(Object value) {
        return (value instanceof String) || (value instanceof Boolean) || (value instanceof Number) || (value instanceof Date);
    }

    private static String parameterToString(Object value) {
        if (value instanceof String) {
            return (String) value;
        }
        if ((value instanceof Boolean) || (value instanceof Number)) {
            return value.toString();
        }
        if (value instanceof Date) {
            return new SimpleDateFormat(ISO_8601_FORMAT_STRING, Locale.US).format(value);
        }
        throw new IllegalArgumentException("Unsupported parameter type.");
    }
}
