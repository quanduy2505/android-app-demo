package com.facebook.internal;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.facebook.AccessToken;
import com.facebook.C0378R;
import com.facebook.FacebookDialogException;
import com.facebook.FacebookException;
import com.facebook.FacebookGraphResponseException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.FacebookRequestError;
import com.facebook.FacebookSdk;
import com.facebook.FacebookServiceException;
import com.facebook.GraphRequest.Callback;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.share.internal.ShareConstants;
import com.facebook.share.internal.ShareInternalUtility;
import com.facebook.share.widget.ShareDialog;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import org.json.JSONArray;
import org.json.JSONObject;

public class WebDialog extends Dialog {
    private static final int API_EC_DIALOG_CANCEL = 4201;
    private static final int BACKGROUND_GRAY = -872415232;
    static final String CANCEL_URI = "fbconnect://cancel";
    static final boolean DISABLE_SSL_CHECK_FOR_TESTING = false;
    private static final String DISPLAY_TOUCH = "touch";
    private static final String LOG_TAG = "FacebookSDK.WebDialog";
    private static final int MAX_PADDING_SCREEN_HEIGHT = 1280;
    private static final int MAX_PADDING_SCREEN_WIDTH = 800;
    private static final double MIN_SCALE_FACTOR = 0.5d;
    private static final int NO_PADDING_SCREEN_HEIGHT = 800;
    private static final int NO_PADDING_SCREEN_WIDTH = 480;
    static final String REDIRECT_URI = "fbconnect://success";
    private FrameLayout contentFrameLayout;
    private ImageView crossImageView;
    private String expectedRedirectUrl;
    private boolean isDetached;
    private boolean isPageFinished;
    private boolean listenerCalled;
    private OnCompleteListener onCompleteListener;
    private ProgressDialog spinner;
    private UploadStagingResourcesTask uploadTask;
    private String url;
    private WebView webView;

    /* renamed from: com.facebook.internal.WebDialog.1 */
    class C04221 implements OnCancelListener {
        C04221() {
        }

        public void onCancel(DialogInterface dialogInterface) {
            WebDialog.this.cancel();
        }
    }

    /* renamed from: com.facebook.internal.WebDialog.2 */
    class C04232 implements OnClickListener {
        C04232() {
        }

        public void onClick(View v) {
            WebDialog.this.cancel();
        }
    }

    /* renamed from: com.facebook.internal.WebDialog.3 */
    class C04243 extends WebView {
        C04243(Context x0) {
            super(x0);
        }

        public void onWindowFocusChanged(boolean hasWindowFocus) {
            try {
                super.onWindowFocusChanged(hasWindowFocus);
            } catch (NullPointerException e) {
            }
        }
    }

    /* renamed from: com.facebook.internal.WebDialog.4 */
    class C04254 implements OnTouchListener {
        C04254() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            if (!v.hasFocus()) {
                v.requestFocus();
            }
            return WebDialog.DISABLE_SSL_CHECK_FOR_TESTING;
        }
    }

    public static class Builder {
        private AccessToken accessToken;
        private String action;
        private String applicationId;
        private Context context;
        private OnCompleteListener listener;
        private Bundle parameters;
        private int theme;

        public Builder(Context context, String action, Bundle parameters) {
            this.accessToken = AccessToken.getCurrentAccessToken();
            if (this.accessToken == null) {
                String applicationId = Utility.getMetadataApplicationId(context);
                if (applicationId != null) {
                    this.applicationId = applicationId;
                } else {
                    throw new FacebookException("Attempted to create a builder without a valid access token or a valid default Application ID.");
                }
            }
            finishInit(context, action, parameters);
        }

        public Builder(Context context, String applicationId, String action, Bundle parameters) {
            if (applicationId == null) {
                applicationId = Utility.getMetadataApplicationId(context);
            }
            Validate.notNullOrEmpty(applicationId, "applicationId");
            this.applicationId = applicationId;
            finishInit(context, action, parameters);
        }

        public Builder setTheme(int theme) {
            this.theme = theme;
            return this;
        }

        public Builder setOnCompleteListener(OnCompleteListener listener) {
            this.listener = listener;
            return this;
        }

        public WebDialog build() {
            if (this.accessToken != null) {
                this.parameters.putString(ServerProtocol.FALLBACK_DIALOG_PARAM_APP_ID, this.accessToken.getApplicationId());
                this.parameters.putString(ServerProtocol.DIALOG_PARAM_ACCESS_TOKEN, this.accessToken.getToken());
            } else {
                this.parameters.putString(ServerProtocol.FALLBACK_DIALOG_PARAM_APP_ID, this.applicationId);
            }
            return new WebDialog(this.context, this.action, this.parameters, this.theme, this.listener);
        }

        public String getApplicationId() {
            return this.applicationId;
        }

        public Context getContext() {
            return this.context;
        }

        public int getTheme() {
            return this.theme;
        }

        public Bundle getParameters() {
            return this.parameters;
        }

        public OnCompleteListener getListener() {
            return this.listener;
        }

        private void finishInit(Context context, String action, Bundle parameters) {
            this.context = context;
            this.action = action;
            if (parameters != null) {
                this.parameters = parameters;
            } else {
                this.parameters = new Bundle();
            }
        }
    }

    private class DialogWebViewClient extends WebViewClient {
        private DialogWebViewClient() {
        }

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Utility.logd(WebDialog.LOG_TAG, "Redirect URL: " + url);
            if (url.startsWith(WebDialog.this.expectedRedirectUrl)) {
                Bundle values = WebDialog.this.parseResponseUri(url);
                String error = values.getString(NativeProtocol.BRIDGE_ARG_ERROR_BUNDLE);
                if (error == null) {
                    error = values.getString(NativeProtocol.BRIDGE_ARG_ERROR_TYPE);
                }
                String errorMessage = values.getString("error_msg");
                if (errorMessage == null) {
                    errorMessage = values.getString(AnalyticsEvents.PARAMETER_SHARE_ERROR_MESSAGE);
                }
                if (errorMessage == null) {
                    errorMessage = values.getString(NativeProtocol.BRIDGE_ARG_ERROR_DESCRIPTION);
                }
                String errorCodeString = values.getString(NativeProtocol.BRIDGE_ARG_ERROR_CODE);
                int errorCode = -1;
                if (!Utility.isNullOrEmpty(errorCodeString)) {
                    try {
                        errorCode = Integer.parseInt(errorCodeString);
                    } catch (NumberFormatException e) {
                        errorCode = -1;
                    }
                }
                if (Utility.isNullOrEmpty(error) && Utility.isNullOrEmpty(errorMessage) && errorCode == -1) {
                    WebDialog.this.sendSuccessToListener(values);
                } else if (error != null && (error.equals("access_denied") || error.equals("OAuthAccessDeniedException"))) {
                    WebDialog.this.cancel();
                } else if (errorCode == WebDialog.API_EC_DIALOG_CANCEL) {
                    WebDialog.this.cancel();
                } else {
                    WebDialog.this.sendErrorToListener(new FacebookServiceException(new FacebookRequestError(errorCode, error, errorMessage), errorMessage));
                }
                return true;
            } else if (url.startsWith(WebDialog.CANCEL_URI)) {
                WebDialog.this.cancel();
                return true;
            } else if (url.contains(WebDialog.DISPLAY_TOUCH)) {
                return WebDialog.DISABLE_SSL_CHECK_FOR_TESTING;
            } else {
                try {
                    WebDialog.this.getContext().startActivity(new Intent("android.intent.action.VIEW", Uri.parse(url)));
                    return true;
                } catch (ActivityNotFoundException e2) {
                    return WebDialog.DISABLE_SSL_CHECK_FOR_TESTING;
                }
            }
        }

        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            WebDialog.this.sendErrorToListener(new FacebookDialogException(description, errorCode, failingUrl));
        }

        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            super.onReceivedSslError(view, handler, error);
            handler.cancel();
            WebDialog.this.sendErrorToListener(new FacebookDialogException(null, -11, null));
        }

        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Utility.logd(WebDialog.LOG_TAG, "Webview loading URL: " + url);
            super.onPageStarted(view, url, favicon);
            if (!WebDialog.this.isDetached) {
                WebDialog.this.spinner.show();
            }
        }

        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (!WebDialog.this.isDetached) {
                WebDialog.this.spinner.dismiss();
            }
            WebDialog.this.contentFrameLayout.setBackgroundColor(0);
            WebDialog.this.webView.setVisibility(0);
            WebDialog.this.crossImageView.setVisibility(0);
            WebDialog.this.isPageFinished = true;
        }
    }

    public interface OnCompleteListener {
        void onComplete(Bundle bundle, FacebookException facebookException);
    }

    private class UploadStagingResourcesTask extends AsyncTask<Void, Void, String[]> {
        private String action;
        private Exception[] exceptions;
        private Bundle parameters;

        /* renamed from: com.facebook.internal.WebDialog.UploadStagingResourcesTask.1 */
        class C10031 implements Callback {
            final /* synthetic */ CountDownLatch val$latch;
            final /* synthetic */ String[] val$results;
            final /* synthetic */ int val$writeIndex;

            C10031(String[] strArr, int i, CountDownLatch countDownLatch) {
                this.val$results = strArr;
                this.val$writeIndex = i;
                this.val$latch = countDownLatch;
            }

            public void onCompleted(GraphResponse response) {
                try {
                    FacebookRequestError error = response.getError();
                    if (error != null) {
                        String message = error.getErrorMessage();
                        if (message == null) {
                            message = "Error staging photo.";
                        }
                        throw new FacebookGraphResponseException(response, message);
                    }
                    JSONObject data = response.getJSONObject();
                    if (data == null) {
                        throw new FacebookException("Error staging photo.");
                    }
                    String stagedImageUri = data.optString(ShareConstants.MEDIA_URI);
                    if (stagedImageUri == null) {
                        throw new FacebookException("Error staging photo.");
                    }
                    this.val$results[this.val$writeIndex] = stagedImageUri;
                    this.val$latch.countDown();
                } catch (Exception e) {
                    UploadStagingResourcesTask.this.exceptions[this.val$writeIndex] = e;
                }
            }
        }

        UploadStagingResourcesTask(String action, Bundle parameters) {
            this.action = action;
            this.parameters = parameters;
        }

        protected String[] doInBackground(Void... args) {
            Iterator it;
            String[] params = this.parameters.getStringArray(ShareConstants.WEB_DIALOG_PARAM_MEDIA);
            String[] results = new String[params.length];
            this.exceptions = new Exception[params.length];
            CountDownLatch latch = new CountDownLatch(params.length);
            ConcurrentLinkedQueue<GraphRequestAsyncTask> tasks = new ConcurrentLinkedQueue();
            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            int i = 0;
            while (i < params.length) {
                try {
                    if (isCancelled()) {
                        it = tasks.iterator();
                        while (it.hasNext()) {
                            ((AsyncTask) it.next()).cancel(true);
                        }
                        return null;
                    }
                    Uri uri = Uri.parse(params[i]);
                    int writeIndex = i;
                    if (Utility.isWebUri(uri)) {
                        results[writeIndex] = uri.toString();
                        latch.countDown();
                    } else {
                        tasks.add(ShareInternalUtility.newUploadStagingResourceWithImageRequest(accessToken, uri, new C10031(results, writeIndex, latch)).executeAsync());
                    }
                    i++;
                } catch (Exception e) {
                    it = tasks.iterator();
                    while (it.hasNext()) {
                        ((AsyncTask) it.next()).cancel(true);
                    }
                    return null;
                }
            }
            latch.await();
            return results;
        }

        protected void onPostExecute(String[] results) {
            WebDialog.this.spinner.dismiss();
            for (Exception e : this.exceptions) {
                if (e != null) {
                    WebDialog.this.sendErrorToListener(e);
                    return;
                }
            }
            if (results == null) {
                WebDialog.this.sendErrorToListener(new FacebookException("Failed to stage photos for web dialog"));
                return;
            }
            List<String> resultList = Arrays.asList(results);
            if (resultList.contains(null)) {
                WebDialog.this.sendErrorToListener(new FacebookException("Failed to stage photos for web dialog"));
                return;
            }
            Utility.putJSONValueInBundle(this.parameters, ShareConstants.WEB_DIALOG_PARAM_MEDIA, new JSONArray(resultList));
            WebDialog.this.url = Utility.buildUri(ServerProtocol.getDialogAuthority(), FacebookSdk.getGraphApiVersion() + "/" + ServerProtocol.DIALOG_PATH + this.action, this.parameters).toString();
            WebDialog.this.setUpWebView((WebDialog.this.crossImageView.getDrawable().getIntrinsicWidth() / 2) + 1);
        }
    }

    public WebDialog(Context context, String url) {
        this(context, url, FacebookSdk.getWebDialogTheme());
    }

    public WebDialog(Context context, String url, int theme) {
        if (theme == 0) {
            theme = FacebookSdk.getWebDialogTheme();
        }
        super(context, theme);
        this.expectedRedirectUrl = REDIRECT_URI;
        this.listenerCalled = DISABLE_SSL_CHECK_FOR_TESTING;
        this.isDetached = DISABLE_SSL_CHECK_FOR_TESTING;
        this.isPageFinished = DISABLE_SSL_CHECK_FOR_TESTING;
        this.url = url;
    }

    public WebDialog(Context context, String action, Bundle parameters, int theme, OnCompleteListener listener) {
        if (theme == 0) {
            theme = FacebookSdk.getWebDialogTheme();
        }
        super(context, theme);
        this.expectedRedirectUrl = REDIRECT_URI;
        this.listenerCalled = DISABLE_SSL_CHECK_FOR_TESTING;
        this.isDetached = DISABLE_SSL_CHECK_FOR_TESTING;
        this.isPageFinished = DISABLE_SSL_CHECK_FOR_TESTING;
        if (parameters == null) {
            parameters = new Bundle();
        }
        parameters.putString(ServerProtocol.DIALOG_PARAM_REDIRECT_URI, REDIRECT_URI);
        parameters.putString(ServerProtocol.DIALOG_PARAM_DISPLAY, DISPLAY_TOUCH);
        parameters.putString(ServerProtocol.DIALOG_PARAM_SDK_VERSION, String.format(Locale.ROOT, "android-%s", new Object[]{FacebookSdk.getSdkVersion()}));
        this.onCompleteListener = listener;
        if (action.equals(ShareDialog.WEB_SHARE_DIALOG) && parameters.containsKey(ShareConstants.WEB_DIALOG_PARAM_MEDIA)) {
            this.uploadTask = new UploadStagingResourcesTask(action, parameters);
        } else {
            this.url = Utility.buildUri(ServerProtocol.getDialogAuthority(), FacebookSdk.getGraphApiVersion() + "/" + ServerProtocol.DIALOG_PATH + action, parameters).toString();
        }
    }

    public void setOnCompleteListener(OnCompleteListener listener) {
        this.onCompleteListener = listener;
    }

    public OnCompleteListener getOnCompleteListener() {
        return this.onCompleteListener;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4) {
            cancel();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void dismiss() {
        if (this.webView != null) {
            this.webView.stopLoading();
        }
        if (!(this.isDetached || this.spinner == null || !this.spinner.isShowing())) {
            this.spinner.dismiss();
        }
        super.dismiss();
    }

    protected void onStart() {
        super.onStart();
        if (this.uploadTask == null || this.uploadTask.getStatus() != Status.PENDING) {
            resize();
            return;
        }
        this.uploadTask.execute(new Void[0]);
        this.spinner.show();
    }

    protected void onStop() {
        if (this.uploadTask != null) {
            this.uploadTask.cancel(true);
            this.spinner.dismiss();
        }
        super.onStop();
    }

    public void onDetachedFromWindow() {
        this.isDetached = true;
        super.onDetachedFromWindow();
    }

    public void onAttachedToWindow() {
        this.isDetached = DISABLE_SSL_CHECK_FOR_TESTING;
        super.onAttachedToWindow();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.spinner = new ProgressDialog(getContext());
        this.spinner.requestWindowFeature(1);
        this.spinner.setMessage(getContext().getString(C0378R.string.com_facebook_loading));
        this.spinner.setCanceledOnTouchOutside(DISABLE_SSL_CHECK_FOR_TESTING);
        this.spinner.setOnCancelListener(new C04221());
        requestWindowFeature(1);
        this.contentFrameLayout = new FrameLayout(getContext());
        resize();
        getWindow().setGravity(17);
        getWindow().setSoftInputMode(16);
        createCrossImage();
        if (this.url != null) {
            setUpWebView((this.crossImageView.getDrawable().getIntrinsicWidth() / 2) + 1);
        }
        this.contentFrameLayout.addView(this.crossImageView, new LayoutParams(-2, -2));
        setContentView(this.contentFrameLayout);
    }

    protected void setExpectedRedirectUrl(String expectedRedirectUrl) {
        this.expectedRedirectUrl = expectedRedirectUrl;
    }

    protected Bundle parseResponseUri(String urlString) {
        Uri u = Uri.parse(urlString);
        Bundle b = Utility.parseUrlQueryString(u.getQuery());
        b.putAll(Utility.parseUrlQueryString(u.getFragment()));
        return b;
    }

    protected boolean isListenerCalled() {
        return this.listenerCalled;
    }

    protected boolean isPageFinished() {
        return this.isPageFinished;
    }

    protected WebView getWebView() {
        return this.webView;
    }

    public void resize() {
        Display display = ((WindowManager) getContext().getSystemService("window")).getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        getWindow().setLayout(Math.min(getScaledSize(metrics.widthPixels < metrics.heightPixels ? metrics.widthPixels : metrics.heightPixels, metrics.density, NO_PADDING_SCREEN_WIDTH, NO_PADDING_SCREEN_HEIGHT), metrics.widthPixels), Math.min(getScaledSize(metrics.widthPixels < metrics.heightPixels ? metrics.heightPixels : metrics.widthPixels, metrics.density, NO_PADDING_SCREEN_HEIGHT, MAX_PADDING_SCREEN_HEIGHT), metrics.heightPixels));
    }

    private int getScaledSize(int screenSize, float density, int noPaddingSize, int maxPaddingSize) {
        double scaleFactor;
        int scaledSize = (int) (((float) screenSize) / density);
        if (scaledSize <= noPaddingSize) {
            scaleFactor = 1.0d;
        } else if (scaledSize >= maxPaddingSize) {
            scaleFactor = MIN_SCALE_FACTOR;
        } else {
            scaleFactor = MIN_SCALE_FACTOR + ((((double) (maxPaddingSize - scaledSize)) / ((double) (maxPaddingSize - noPaddingSize))) * MIN_SCALE_FACTOR);
        }
        return (int) (((double) screenSize) * scaleFactor);
    }

    protected void sendSuccessToListener(Bundle values) {
        if (this.onCompleteListener != null && !this.listenerCalled) {
            this.listenerCalled = true;
            this.onCompleteListener.onComplete(values, null);
            dismiss();
        }
    }

    protected void sendErrorToListener(Throwable error) {
        if (this.onCompleteListener != null && !this.listenerCalled) {
            FacebookException facebookException;
            this.listenerCalled = true;
            if (error instanceof FacebookException) {
                facebookException = (FacebookException) error;
            } else {
                facebookException = new FacebookException(error);
            }
            this.onCompleteListener.onComplete(null, facebookException);
            dismiss();
        }
    }

    public void cancel() {
        if (this.onCompleteListener != null && !this.listenerCalled) {
            sendErrorToListener(new FacebookOperationCanceledException());
        }
    }

    private void createCrossImage() {
        this.crossImageView = new ImageView(getContext());
        this.crossImageView.setOnClickListener(new C04232());
        this.crossImageView.setImageDrawable(getContext().getResources().getDrawable(C0378R.drawable.com_facebook_close));
        this.crossImageView.setVisibility(4);
    }

    @SuppressLint({"SetJavaScriptEnabled"})
    private void setUpWebView(int margin) {
        LinearLayout webViewContainer = new LinearLayout(getContext());
        this.webView = new C04243(getContext().getApplicationContext());
        this.webView.setVerticalScrollBarEnabled(DISABLE_SSL_CHECK_FOR_TESTING);
        this.webView.setHorizontalScrollBarEnabled(DISABLE_SSL_CHECK_FOR_TESTING);
        this.webView.setWebViewClient(new DialogWebViewClient());
        this.webView.getSettings().setJavaScriptEnabled(true);
        this.webView.loadUrl(this.url);
        this.webView.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        this.webView.setVisibility(4);
        this.webView.getSettings().setSavePassword(DISABLE_SSL_CHECK_FOR_TESTING);
        this.webView.getSettings().setSaveFormData(DISABLE_SSL_CHECK_FOR_TESTING);
        this.webView.setFocusable(true);
        this.webView.setFocusableInTouchMode(true);
        this.webView.setOnTouchListener(new C04254());
        webViewContainer.setPadding(margin, margin, margin, margin);
        webViewContainer.addView(this.webView);
        webViewContainer.setBackgroundColor(BACKGROUND_GRAY);
        this.contentFrameLayout.addView(webViewContainer);
    }
}
