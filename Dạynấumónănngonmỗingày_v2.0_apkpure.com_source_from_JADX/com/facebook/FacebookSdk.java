package com.facebook;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Base64;
import android.util.Log;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.internal.BoltsMeasurementEventListener;
import com.facebook.internal.FetchedAppSettingsManager;
import com.facebook.internal.LockOnGetVariable;
import com.facebook.internal.NativeProtocol;
import com.facebook.internal.ServerProtocol;
import com.facebook.internal.Utility;
import com.facebook.internal.Validate;
import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public final class FacebookSdk {
    public static final String APPLICATION_ID_PROPERTY = "com.facebook.sdk.ApplicationId";
    public static final String APPLICATION_NAME_PROPERTY = "com.facebook.sdk.ApplicationName";
    private static final String ATTRIBUTION_PREFERENCES = "com.facebook.sdk.attributionTracking";
    public static final String AUTO_LOG_APP_EVENTS_ENABLED_PROPERTY = "com.facebook.sdk.AutoLogAppEventsEnabled";
    static final String CALLBACK_OFFSET_CHANGED_AFTER_INIT = "The callback request code offset can't be updated once the SDK is initialized. Call FacebookSdk.setCallbackRequestCodeOffset inside your Application.onCreate method";
    static final String CALLBACK_OFFSET_NEGATIVE = "The callback request code offset can't be negative.";
    public static final String CALLBACK_OFFSET_PROPERTY = "com.facebook.sdk.CallbackOffset";
    public static final String CLIENT_TOKEN_PROPERTY = "com.facebook.sdk.ClientToken";
    private static final int DEFAULT_CALLBACK_REQUEST_CODE_OFFSET = 64206;
    private static final int DEFAULT_CORE_POOL_SIZE = 5;
    private static final int DEFAULT_KEEP_ALIVE = 1;
    private static final int DEFAULT_MAXIMUM_POOL_SIZE = 128;
    private static final int DEFAULT_THEME;
    private static final ThreadFactory DEFAULT_THREAD_FACTORY;
    private static final BlockingQueue<Runnable> DEFAULT_WORK_QUEUE;
    private static final String FACEBOOK_COM = "facebook.com";
    private static final Object LOCK;
    private static final int MAX_REQUEST_CODE_RANGE = 100;
    private static final String PUBLISH_ACTIVITY_PATH = "%s/activities";
    private static final String TAG;
    public static final String WEB_DIALOG_THEME = "com.facebook.sdk.WebDialogTheme";
    private static volatile String appClientToken;
    private static Context applicationContext;
    private static volatile String applicationId;
    private static volatile String applicationName;
    private static volatile Boolean autoLogAppEventsEnabled;
    private static LockOnGetVariable<File> cacheDir;
    private static int callbackRequestCodeOffset;
    private static volatile Executor executor;
    private static volatile String facebookDomain;
    private static String graphApiVersion;
    private static volatile boolean isDebugEnabled;
    private static boolean isLegacyTokenUpgradeSupported;
    private static final HashSet<LoggingBehavior> loggingBehaviors;
    private static AtomicLong onProgressThreshold;
    private static Boolean sdkInitialized;
    private static volatile int webDialogTheme;

    /* renamed from: com.facebook.FacebookSdk.1 */
    static class C03691 implements ThreadFactory {
        private final AtomicInteger counter;

        C03691() {
            this.counter = new AtomicInteger(FacebookSdk.DEFAULT_THEME);
        }

        public Thread newThread(Runnable runnable) {
            return new Thread(runnable, "FacebookSdk #" + this.counter.incrementAndGet());
        }
    }

    /* renamed from: com.facebook.FacebookSdk.2 */
    static class C03702 implements Callable<File> {
        C03702() {
        }

        public File call() throws Exception {
            return FacebookSdk.applicationContext.getCacheDir();
        }
    }

    /* renamed from: com.facebook.FacebookSdk.3 */
    static class C03713 implements Callable<Void> {
        final /* synthetic */ Context val$applicationContext;
        final /* synthetic */ InitializeCallback val$callback;

        C03713(InitializeCallback initializeCallback, Context context) {
            this.val$callback = initializeCallback;
            this.val$applicationContext = context;
        }

        public Void call() throws Exception {
            AccessTokenManager.getInstance().loadCurrentAccessToken();
            ProfileManager.getInstance().loadCurrentProfile();
            if (AccessToken.getCurrentAccessToken() != null && Profile.getCurrentProfile() == null) {
                Profile.fetchProfileForCurrentAccessToken();
            }
            if (this.val$callback != null) {
                this.val$callback.onInitialized();
            }
            AppEventsLogger.newLogger(this.val$applicationContext.getApplicationContext()).flush();
            return null;
        }
    }

    /* renamed from: com.facebook.FacebookSdk.4 */
    static class C03724 implements Runnable {
        final /* synthetic */ Context val$applicationContext;
        final /* synthetic */ String val$applicationId;

        C03724(Context context, String str) {
            this.val$applicationContext = context;
            this.val$applicationId = str;
        }

        public void run() {
            FacebookSdk.publishInstallAndWaitForResponse(this.val$applicationContext, this.val$applicationId);
        }
    }

    public interface InitializeCallback {
        void onInitialized();
    }

    static {
        TAG = FacebookSdk.class.getCanonicalName();
        LoggingBehavior[] loggingBehaviorArr = new LoggingBehavior[DEFAULT_KEEP_ALIVE];
        loggingBehaviorArr[DEFAULT_THEME] = LoggingBehavior.DEVELOPER_ERRORS;
        loggingBehaviors = new HashSet(Arrays.asList(loggingBehaviorArr));
        facebookDomain = FACEBOOK_COM;
        onProgressThreshold = new AtomicLong(PlaybackStateCompat.ACTION_PREPARE_FROM_SEARCH);
        isDebugEnabled = false;
        isLegacyTokenUpgradeSupported = false;
        callbackRequestCodeOffset = DEFAULT_CALLBACK_REQUEST_CODE_OFFSET;
        LOCK = new Object();
        DEFAULT_THEME = C0378R.style.com_facebook_activity_theme;
        graphApiVersion = ServerProtocol.getDefaultAPIVersion();
        DEFAULT_WORK_QUEUE = new LinkedBlockingQueue(10);
        DEFAULT_THREAD_FACTORY = new C03691();
        sdkInitialized = Boolean.valueOf(false);
    }

    @Deprecated
    public static synchronized void sdkInitialize(Context applicationContext, int callbackRequestCodeOffset) {
        synchronized (FacebookSdk.class) {
            sdkInitialize(applicationContext, callbackRequestCodeOffset, null);
        }
    }

    @Deprecated
    public static synchronized void sdkInitialize(Context applicationContext, int callbackRequestCodeOffset, InitializeCallback callback) {
        synchronized (FacebookSdk.class) {
            if (sdkInitialized.booleanValue() && callbackRequestCodeOffset != callbackRequestCodeOffset) {
                throw new FacebookException(CALLBACK_OFFSET_CHANGED_AFTER_INIT);
            } else if (callbackRequestCodeOffset < 0) {
                throw new FacebookException(CALLBACK_OFFSET_NEGATIVE);
            } else {
                callbackRequestCodeOffset = callbackRequestCodeOffset;
                sdkInitialize(applicationContext, callback);
            }
        }
    }

    @Deprecated
    public static synchronized void sdkInitialize(Context applicationContext) {
        synchronized (FacebookSdk.class) {
            sdkInitialize(applicationContext, null);
        }
    }

    @Deprecated
    public static synchronized void sdkInitialize(Context applicationContext, InitializeCallback callback) {
        synchronized (FacebookSdk.class) {
            if (!sdkInitialized.booleanValue()) {
                Validate.notNull(applicationContext, "applicationContext");
                Validate.hasFacebookActivity(applicationContext, false);
                Validate.hasInternetPermissions(applicationContext, false);
                applicationContext = applicationContext.getApplicationContext();
                loadDefaultsFromMetadata(applicationContext);
                if (Utility.isNullOrEmpty(applicationId)) {
                    throw new FacebookException("A valid Facebook app id must be set in the AndroidManifest.xml or set by calling FacebookSdk.setApplicationId before initializing the sdk.");
                }
                sdkInitialized = Boolean.valueOf(true);
                FetchedAppSettingsManager.loadAppSettingsAsync();
                NativeProtocol.updateAllAvailableProtocolVersionsAsync();
                BoltsMeasurementEventListener.getInstance(applicationContext);
                cacheDir = new LockOnGetVariable(new C03702());
                getExecutor().execute(new FutureTask(new C03713(callback, applicationContext)));
            } else if (callback != null) {
                callback.onInitialized();
            }
        }
    }

    public static synchronized boolean isInitialized() {
        boolean booleanValue;
        synchronized (FacebookSdk.class) {
            booleanValue = sdkInitialized.booleanValue();
        }
        return booleanValue;
    }

    public static Set<LoggingBehavior> getLoggingBehaviors() {
        Set<LoggingBehavior> unmodifiableSet;
        synchronized (loggingBehaviors) {
            unmodifiableSet = Collections.unmodifiableSet(new HashSet(loggingBehaviors));
        }
        return unmodifiableSet;
    }

    public static void addLoggingBehavior(LoggingBehavior behavior) {
        synchronized (loggingBehaviors) {
            loggingBehaviors.add(behavior);
            updateGraphDebugBehavior();
        }
    }

    public static void removeLoggingBehavior(LoggingBehavior behavior) {
        synchronized (loggingBehaviors) {
            loggingBehaviors.remove(behavior);
        }
    }

    public static void clearLoggingBehaviors() {
        synchronized (loggingBehaviors) {
            loggingBehaviors.clear();
        }
    }

    public static boolean isLoggingBehaviorEnabled(LoggingBehavior behavior) {
        boolean z;
        synchronized (loggingBehaviors) {
            z = isDebugEnabled() && loggingBehaviors.contains(behavior);
        }
        return z;
    }

    public static boolean isDebugEnabled() {
        return isDebugEnabled;
    }

    public static void setIsDebugEnabled(boolean enabled) {
        isDebugEnabled = enabled;
    }

    public static boolean isLegacyTokenUpgradeSupported() {
        return isLegacyTokenUpgradeSupported;
    }

    private static void updateGraphDebugBehavior() {
        if (loggingBehaviors.contains(LoggingBehavior.GRAPH_API_DEBUG_INFO) && !loggingBehaviors.contains(LoggingBehavior.GRAPH_API_DEBUG_WARNING)) {
            loggingBehaviors.add(LoggingBehavior.GRAPH_API_DEBUG_WARNING);
        }
    }

    public static void setLegacyTokenUpgradeSupported(boolean supported) {
        isLegacyTokenUpgradeSupported = supported;
    }

    public static Executor getExecutor() {
        synchronized (LOCK) {
            if (executor == null) {
                executor = AsyncTask.THREAD_POOL_EXECUTOR;
            }
        }
        return executor;
    }

    public static void setExecutor(Executor executor) {
        Validate.notNull(executor, "executor");
        synchronized (LOCK) {
            executor = executor;
        }
    }

    public static String getFacebookDomain() {
        return facebookDomain;
    }

    public static void setFacebookDomain(String facebookDomain) {
        Log.w(TAG, "WARNING: Calling setFacebookDomain from non-DEBUG code.");
        facebookDomain = facebookDomain;
    }

    public static Context getApplicationContext() {
        Validate.sdkInitialized();
        return applicationContext;
    }

    public static void setGraphApiVersion(String graphApiVersion) {
        if (!Utility.isNullOrEmpty(graphApiVersion) && !graphApiVersion.equals(graphApiVersion)) {
            graphApiVersion = graphApiVersion;
        }
    }

    public static String getGraphApiVersion() {
        return graphApiVersion;
    }

    public static void publishInstallAsync(Context context, String applicationId) {
        getExecutor().execute(new C03724(context.getApplicationContext(), applicationId));
    }

    static com.facebook.GraphResponse publishInstallAndWaitForResponse(android.content.Context r24, java.lang.String r25) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.JadxRuntimeException: Exception block dominator not found, method:com.facebook.FacebookSdk.publishInstallAndWaitForResponse(android.content.Context, java.lang.String):com.facebook.GraphResponse. bs: [B:2:0x0004, B:10:0x0079]
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:86)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:57)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:52)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
        /*
        if (r24 == 0) goto L_0x0004;
    L_0x0002:
        if (r25 != 0) goto L_0x0029;
    L_0x0004:
        r19 = new java.lang.IllegalArgumentException;	 Catch:{ Exception -> 0x000c }
        r20 = "Both context and applicationId must be non-null";	 Catch:{ Exception -> 0x000c }
        r19.<init>(r20);	 Catch:{ Exception -> 0x000c }
        throw r19;	 Catch:{ Exception -> 0x000c }
    L_0x000c:
        r4 = move-exception;
        r19 = "Facebook-publish";
        r0 = r19;
        com.facebook.internal.Utility.logd(r0, r4);
        r19 = new com.facebook.GraphResponse;
        r20 = 0;
        r21 = 0;
        r22 = new com.facebook.FacebookRequestError;
        r23 = 0;
        r0 = r22;
        r1 = r23;
        r0.<init>(r1, r4);
        r19.<init>(r20, r21, r22);
    L_0x0028:
        return r19;
    L_0x0029:
        r8 = com.facebook.internal.AttributionIdentifiers.getAttributionIdentifiers(r24);	 Catch:{ Exception -> 0x000c }
        r19 = "com.facebook.sdk.attributionTracking";	 Catch:{ Exception -> 0x000c }
        r20 = 0;	 Catch:{ Exception -> 0x000c }
        r0 = r24;	 Catch:{ Exception -> 0x000c }
        r1 = r19;	 Catch:{ Exception -> 0x000c }
        r2 = r20;	 Catch:{ Exception -> 0x000c }
        r14 = r0.getSharedPreferences(r1, r2);	 Catch:{ Exception -> 0x000c }
        r19 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x000c }
        r19.<init>();	 Catch:{ Exception -> 0x000c }
        r0 = r19;	 Catch:{ Exception -> 0x000c }
        r1 = r25;	 Catch:{ Exception -> 0x000c }
        r19 = r0.append(r1);	 Catch:{ Exception -> 0x000c }
        r20 = "ping";	 Catch:{ Exception -> 0x000c }
        r19 = r19.append(r20);	 Catch:{ Exception -> 0x000c }
        r13 = r19.toString();	 Catch:{ Exception -> 0x000c }
        r19 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x000c }
        r19.<init>();	 Catch:{ Exception -> 0x000c }
        r0 = r19;	 Catch:{ Exception -> 0x000c }
        r1 = r25;	 Catch:{ Exception -> 0x000c }
        r19 = r0.append(r1);	 Catch:{ Exception -> 0x000c }
        r20 = "json";	 Catch:{ Exception -> 0x000c }
        r19 = r19.append(r20);	 Catch:{ Exception -> 0x000c }
        r9 = r19.toString();	 Catch:{ Exception -> 0x000c }
        r20 = 0;	 Catch:{ Exception -> 0x000c }
        r0 = r20;	 Catch:{ Exception -> 0x000c }
        r10 = r14.getLong(r13, r0);	 Catch:{ Exception -> 0x000c }
        r19 = 0;	 Catch:{ Exception -> 0x000c }
        r0 = r19;	 Catch:{ Exception -> 0x000c }
        r12 = r14.getString(r9, r0);	 Catch:{ Exception -> 0x000c }
        r19 = com.facebook.internal.AppEventsLoggerUtility.GraphAPIActivityType.MOBILE_INSTALL_EVENT;	 Catch:{ JSONException -> 0x00e3 }
        r20 = com.facebook.appevents.AppEventsLogger.getAnonymousAppDeviceGUID(r24);	 Catch:{ JSONException -> 0x00e3 }
        r21 = getLimitEventAndDataUsage(r24);	 Catch:{ JSONException -> 0x00e3 }
        r0 = r19;	 Catch:{ JSONException -> 0x00e3 }
        r1 = r20;	 Catch:{ JSONException -> 0x00e3 }
        r2 = r21;	 Catch:{ JSONException -> 0x00e3 }
        r3 = r24;	 Catch:{ JSONException -> 0x00e3 }
        r15 = com.facebook.internal.AppEventsLoggerUtility.getJSONObjectForGraphAPICall(r0, r8, r1, r2, r3);	 Catch:{ JSONException -> 0x00e3 }
        r19 = "%s/activities";	 Catch:{ Exception -> 0x000c }
        r20 = 1;	 Catch:{ Exception -> 0x000c }
        r0 = r20;	 Catch:{ Exception -> 0x000c }
        r0 = new java.lang.Object[r0];	 Catch:{ Exception -> 0x000c }
        r20 = r0;	 Catch:{ Exception -> 0x000c }
        r21 = 0;	 Catch:{ Exception -> 0x000c }
        r20[r21] = r25;	 Catch:{ Exception -> 0x000c }
        r18 = java.lang.String.format(r19, r20);	 Catch:{ Exception -> 0x000c }
        r19 = 0;	 Catch:{ Exception -> 0x000c }
        r20 = 0;	 Catch:{ Exception -> 0x000c }
        r0 = r19;	 Catch:{ Exception -> 0x000c }
        r1 = r18;	 Catch:{ Exception -> 0x000c }
        r2 = r20;	 Catch:{ Exception -> 0x000c }
        r16 = com.facebook.GraphRequest.newPostRequest(r0, r1, r15, r2);	 Catch:{ Exception -> 0x000c }
        r20 = 0;
        r19 = (r10 > r20 ? 1 : (r10 == r20 ? 0 : -1));
        if (r19 == 0) goto L_0x0105;
    L_0x00b5:
        r6 = 0;
        if (r12 == 0) goto L_0x00be;
    L_0x00b8:
        r7 = new org.json.JSONObject;	 Catch:{ JSONException -> 0x012e }
        r7.<init>(r12);	 Catch:{ JSONException -> 0x012e }
        r6 = r7;
    L_0x00be:
        if (r6 != 0) goto L_0x00f0;
    L_0x00c0:
        r19 = "true";	 Catch:{ Exception -> 0x000c }
        r20 = 0;	 Catch:{ Exception -> 0x000c }
        r21 = new com.facebook.GraphRequestBatch;	 Catch:{ Exception -> 0x000c }
        r22 = 1;	 Catch:{ Exception -> 0x000c }
        r0 = r22;	 Catch:{ Exception -> 0x000c }
        r0 = new com.facebook.GraphRequest[r0];	 Catch:{ Exception -> 0x000c }
        r22 = r0;	 Catch:{ Exception -> 0x000c }
        r23 = 0;	 Catch:{ Exception -> 0x000c }
        r22[r23] = r16;	 Catch:{ Exception -> 0x000c }
        r21.<init>(r22);	 Catch:{ Exception -> 0x000c }
        r19 = com.facebook.GraphResponse.createResponsesFromString(r19, r20, r21);	 Catch:{ Exception -> 0x000c }
        r20 = 0;	 Catch:{ Exception -> 0x000c }
        r19 = r19.get(r20);	 Catch:{ Exception -> 0x000c }
        r19 = (com.facebook.GraphResponse) r19;	 Catch:{ Exception -> 0x000c }
        goto L_0x0028;	 Catch:{ Exception -> 0x000c }
    L_0x00e3:
        r4 = move-exception;	 Catch:{ Exception -> 0x000c }
        r19 = new com.facebook.FacebookException;	 Catch:{ Exception -> 0x000c }
        r20 = "An error occurred while publishing install.";	 Catch:{ Exception -> 0x000c }
        r0 = r19;	 Catch:{ Exception -> 0x000c }
        r1 = r20;	 Catch:{ Exception -> 0x000c }
        r0.<init>(r1, r4);	 Catch:{ Exception -> 0x000c }
        throw r19;	 Catch:{ Exception -> 0x000c }
    L_0x00f0:
        r19 = new com.facebook.GraphResponse;	 Catch:{ Exception -> 0x000c }
        r20 = 0;	 Catch:{ Exception -> 0x000c }
        r21 = 0;	 Catch:{ Exception -> 0x000c }
        r22 = 0;	 Catch:{ Exception -> 0x000c }
        r0 = r19;	 Catch:{ Exception -> 0x000c }
        r1 = r20;	 Catch:{ Exception -> 0x000c }
        r2 = r21;	 Catch:{ Exception -> 0x000c }
        r3 = r22;	 Catch:{ Exception -> 0x000c }
        r0.<init>(r1, r2, r3, r6);	 Catch:{ Exception -> 0x000c }
        goto L_0x0028;	 Catch:{ Exception -> 0x000c }
    L_0x0105:
        r17 = r16.executeAndWait();	 Catch:{ Exception -> 0x000c }
        r5 = r14.edit();	 Catch:{ Exception -> 0x000c }
        r10 = java.lang.System.currentTimeMillis();	 Catch:{ Exception -> 0x000c }
        r5.putLong(r13, r10);	 Catch:{ Exception -> 0x000c }
        r19 = r17.getJSONObject();	 Catch:{ Exception -> 0x000c }
        if (r19 == 0) goto L_0x0127;	 Catch:{ Exception -> 0x000c }
    L_0x011a:
        r19 = r17.getJSONObject();	 Catch:{ Exception -> 0x000c }
        r19 = r19.toString();	 Catch:{ Exception -> 0x000c }
        r0 = r19;	 Catch:{ Exception -> 0x000c }
        r5.putString(r9, r0);	 Catch:{ Exception -> 0x000c }
    L_0x0127:
        r5.apply();	 Catch:{ Exception -> 0x000c }
        r19 = r17;
        goto L_0x0028;
    L_0x012e:
        r19 = move-exception;
        goto L_0x00be;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.facebook.FacebookSdk.publishInstallAndWaitForResponse(android.content.Context, java.lang.String):com.facebook.GraphResponse");
    }

    public static String getSdkVersion() {
        return FacebookSdkVersion.BUILD;
    }

    public static boolean getLimitEventAndDataUsage(Context context) {
        Validate.sdkInitialized();
        return context.getSharedPreferences(AppEventsLogger.APP_EVENT_PREFERENCES, DEFAULT_THEME).getBoolean("limitEventUsage", false);
    }

    public static void setLimitEventAndDataUsage(Context context, boolean limitEventUsage) {
        context.getSharedPreferences(AppEventsLogger.APP_EVENT_PREFERENCES, DEFAULT_THEME).edit().putBoolean("limitEventUsage", limitEventUsage).apply();
    }

    public static long getOnProgressThreshold() {
        Validate.sdkInitialized();
        return onProgressThreshold.get();
    }

    public static void setOnProgressThreshold(long threshold) {
        onProgressThreshold.set(threshold);
    }

    static void loadDefaultsFromMetadata(Context context) {
        if (context != null) {
            try {
                ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), DEFAULT_MAXIMUM_POOL_SIZE);
                if (ai != null && ai.metaData != null) {
                    if (applicationId == null) {
                        String appId = ai.metaData.get(APPLICATION_ID_PROPERTY);
                        if (appId instanceof String) {
                            String appIdString = appId;
                            if (appIdString.toLowerCase(Locale.ROOT).startsWith("fb")) {
                                applicationId = appIdString.substring(2);
                            } else {
                                applicationId = appIdString;
                            }
                        } else if (appId instanceof Integer) {
                            throw new FacebookException("App Ids cannot be directly placed in the manifest.They must be prefixed by 'fb' or be placed in the string resource file.");
                        }
                    }
                    if (applicationName == null) {
                        applicationName = ai.metaData.getString(APPLICATION_NAME_PROPERTY);
                    }
                    if (appClientToken == null) {
                        appClientToken = ai.metaData.getString(CLIENT_TOKEN_PROPERTY);
                    }
                    if (webDialogTheme == 0) {
                        setWebDialogTheme(ai.metaData.getInt(WEB_DIALOG_THEME));
                    }
                    if (callbackRequestCodeOffset == DEFAULT_CALLBACK_REQUEST_CODE_OFFSET) {
                        callbackRequestCodeOffset = ai.metaData.getInt(CALLBACK_OFFSET_PROPERTY, DEFAULT_CALLBACK_REQUEST_CODE_OFFSET);
                    }
                    if (autoLogAppEventsEnabled == null) {
                        autoLogAppEventsEnabled = Boolean.valueOf(ai.metaData.getBoolean(AUTO_LOG_APP_EVENTS_ENABLED_PROPERTY, true));
                    }
                }
            } catch (NameNotFoundException e) {
            }
        }
    }

    public static String getApplicationSignature(Context context) {
        Validate.sdkInitialized();
        if (context == null) {
            return null;
        }
        PackageManager packageManager = context.getPackageManager();
        if (packageManager == null) {
            return null;
        }
        try {
            PackageInfo pInfo = packageManager.getPackageInfo(context.getPackageName(), 64);
            Signature[] signatures = pInfo.signatures;
            if (signatures == null || signatures.length == 0) {
                return null;
            }
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-1");
                md.update(pInfo.signatures[DEFAULT_THEME].toByteArray());
                return Base64.encodeToString(md.digest(), 9);
            } catch (NoSuchAlgorithmException e) {
                return null;
            }
        } catch (NameNotFoundException e2) {
            return null;
        }
    }

    public static String getApplicationId() {
        Validate.sdkInitialized();
        return applicationId;
    }

    public static void setApplicationId(String applicationId) {
        applicationId = applicationId;
    }

    public static String getApplicationName() {
        Validate.sdkInitialized();
        return applicationName;
    }

    public static void setApplicationName(String applicationName) {
        applicationName = applicationName;
    }

    public static String getClientToken() {
        Validate.sdkInitialized();
        return appClientToken;
    }

    public static void setClientToken(String clientToken) {
        appClientToken = clientToken;
    }

    public static int getWebDialogTheme() {
        Validate.sdkInitialized();
        return webDialogTheme;
    }

    public static void setWebDialogTheme(int theme) {
        if (theme == 0) {
            theme = DEFAULT_THEME;
        }
        webDialogTheme = theme;
    }

    public static boolean getAutoLogAppEventsEnabled() {
        Validate.sdkInitialized();
        return autoLogAppEventsEnabled.booleanValue();
    }

    public static void setAutoLogAppEventsEnabled(boolean flag) {
        autoLogAppEventsEnabled = Boolean.valueOf(flag);
    }

    public static File getCacheDir() {
        Validate.sdkInitialized();
        return (File) cacheDir.getValue();
    }

    public static void setCacheDir(File cacheDir) {
        cacheDir = new LockOnGetVariable((Object) cacheDir);
    }

    public static int getCallbackRequestCodeOffset() {
        Validate.sdkInitialized();
        return callbackRequestCodeOffset;
    }

    public static boolean isFacebookRequestCode(int requestCode) {
        return requestCode >= callbackRequestCodeOffset && requestCode < callbackRequestCodeOffset + MAX_REQUEST_CODE_RANGE;
    }
}
