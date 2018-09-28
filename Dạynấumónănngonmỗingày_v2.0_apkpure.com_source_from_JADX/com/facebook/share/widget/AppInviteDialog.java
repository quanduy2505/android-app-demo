package com.facebook.share.widget;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import com.facebook.FacebookCallback;
import com.facebook.internal.AppCall;
import com.facebook.internal.CallbackManagerImpl;
import com.facebook.internal.CallbackManagerImpl.Callback;
import com.facebook.internal.CallbackManagerImpl.RequestCodeOffset;
import com.facebook.internal.DialogFeature;
import com.facebook.internal.DialogPresenter;
import com.facebook.internal.DialogPresenter.ParameterProvider;
import com.facebook.internal.FacebookDialogBase;
import com.facebook.internal.FragmentWrapper;
import com.facebook.share.internal.AppInviteDialogFeature;
import com.facebook.share.internal.ResultProcessor;
import com.facebook.share.internal.ShareConstants;
import com.facebook.share.internal.ShareInternalUtility;
import com.facebook.share.model.AppInviteContent;
import com.google.firebase.analytics.FirebaseAnalytics.Param;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import rx.android.BuildConfig;

public class AppInviteDialog extends FacebookDialogBase<AppInviteContent, Result> {
    private static final int DEFAULT_REQUEST_CODE;
    private static final String TAG = "AppInviteDialog";

    public static final class Result {
        private final Bundle bundle;

        public Result(Bundle bundle) {
            this.bundle = bundle;
        }

        public Bundle getData() {
            return this.bundle;
        }
    }

    /* renamed from: com.facebook.share.widget.AppInviteDialog.1 */
    class C10481 extends ResultProcessor {
        final /* synthetic */ FacebookCallback val$callback;

        C10481(FacebookCallback callback, FacebookCallback facebookCallback) {
            this.val$callback = facebookCallback;
            super(callback);
        }

        public void onSuccess(AppCall appCall, Bundle results) {
            if ("cancel".equalsIgnoreCase(ShareInternalUtility.getNativeDialogCompletionGesture(results))) {
                this.val$callback.onCancel();
            } else {
                this.val$callback.onSuccess(new Result(results));
            }
        }
    }

    /* renamed from: com.facebook.share.widget.AppInviteDialog.2 */
    class C10492 implements Callback {
        final /* synthetic */ ResultProcessor val$resultProcessor;

        C10492(ResultProcessor resultProcessor) {
            this.val$resultProcessor = resultProcessor;
        }

        public boolean onActivityResult(int resultCode, Intent data) {
            return ShareInternalUtility.handleActivityResult(AppInviteDialog.this.getRequestCode(), resultCode, data, this.val$resultProcessor);
        }
    }

    private class NativeHandler extends ModeHandler {

        /* renamed from: com.facebook.share.widget.AppInviteDialog.NativeHandler.1 */
        class C10501 implements ParameterProvider {
            final /* synthetic */ AppInviteContent val$content;

            C10501(AppInviteContent appInviteContent) {
                this.val$content = appInviteContent;
            }

            public Bundle getParameters() {
                return AppInviteDialog.createParameters(this.val$content);
            }

            public Bundle getLegacyParameters() {
                Log.e(AppInviteDialog.TAG, "Attempting to present the AppInviteDialog with an outdated Facebook app on the device");
                return new Bundle();
            }
        }

        private NativeHandler() {
            super();
        }

        public boolean canShow(AppInviteContent content, boolean isBestEffort) {
            return AppInviteDialog.canShowNativeDialog();
        }

        public AppCall createAppCall(AppInviteContent content) {
            AppCall appCall = AppInviteDialog.this.createBaseAppCall();
            DialogPresenter.setupAppCallForNativeDialog(appCall, new C10501(content), AppInviteDialog.getFeature());
            return appCall;
        }
    }

    private class WebFallbackHandler extends ModeHandler {
        private WebFallbackHandler() {
            super();
        }

        public boolean canShow(AppInviteContent content, boolean isBestEffort) {
            return AppInviteDialog.canShowWebFallback();
        }

        public AppCall createAppCall(AppInviteContent content) {
            AppCall appCall = AppInviteDialog.this.createBaseAppCall();
            DialogPresenter.setupAppCallForWebFallbackDialog(appCall, AppInviteDialog.createParameters(content), AppInviteDialog.getFeature());
            return appCall;
        }
    }

    static {
        DEFAULT_REQUEST_CODE = RequestCodeOffset.AppInvite.toRequestCode();
    }

    public static boolean canShow() {
        return canShowNativeDialog() || canShowWebFallback();
    }

    public static void show(Activity activity, AppInviteContent appInviteContent) {
        new AppInviteDialog(activity).show(appInviteContent);
    }

    public static void show(Fragment fragment, AppInviteContent appInviteContent) {
        show(new FragmentWrapper(fragment), appInviteContent);
    }

    public static void show(android.app.Fragment fragment, AppInviteContent appInviteContent) {
        show(new FragmentWrapper(fragment), appInviteContent);
    }

    private static void show(FragmentWrapper fragmentWrapper, AppInviteContent appInviteContent) {
        new AppInviteDialog(fragmentWrapper).show(appInviteContent);
    }

    private static boolean canShowNativeDialog() {
        return DialogPresenter.canPresentNativeDialogWithFeature(getFeature());
    }

    private static boolean canShowWebFallback() {
        return DialogPresenter.canPresentWebFallbackDialogWithFeature(getFeature());
    }

    public AppInviteDialog(Activity activity) {
        super(activity, DEFAULT_REQUEST_CODE);
    }

    public AppInviteDialog(Fragment fragment) {
        this(new FragmentWrapper(fragment));
    }

    public AppInviteDialog(android.app.Fragment fragment) {
        this(new FragmentWrapper(fragment));
    }

    private AppInviteDialog(FragmentWrapper fragment) {
        super(fragment, DEFAULT_REQUEST_CODE);
    }

    protected void registerCallbackImpl(CallbackManagerImpl callbackManager, FacebookCallback<Result> callback) {
        ResultProcessor resultProcessor;
        if (callback == null) {
            resultProcessor = null;
        } else {
            resultProcessor = new C10481(callback, callback);
        }
        callbackManager.registerCallback(getRequestCode(), new C10492(resultProcessor));
    }

    protected AppCall createBaseAppCall() {
        return new AppCall(getRequestCode());
    }

    protected List<ModeHandler> getOrderedModeHandlers() {
        ArrayList<ModeHandler> handlers = new ArrayList();
        handlers.add(new NativeHandler());
        handlers.add(new WebFallbackHandler());
        return handlers;
    }

    private static DialogFeature getFeature() {
        return AppInviteDialogFeature.APP_INVITES_DIALOG;
    }

    private static Bundle createParameters(AppInviteContent content) {
        Bundle params = new Bundle();
        params.putString(ShareConstants.APPLINK_URL, content.getApplinkUrl());
        params.putString(ShareConstants.PREVIEW_IMAGE_URL, content.getPreviewImageUrl());
        params.putString(Param.DESTINATION, content.getDestination().toString());
        String promoCode = content.getPromotionCode();
        if (promoCode == null) {
            promoCode = BuildConfig.VERSION_NAME;
        }
        String promoText = content.getPromotionText();
        if (!TextUtils.isEmpty(promoText)) {
            try {
                JSONObject deeplinkContent = new JSONObject();
                deeplinkContent.put(ShareConstants.PROMO_CODE, promoCode);
                deeplinkContent.put(ShareConstants.PROMO_TEXT, promoText);
                params.putString(ShareConstants.DEEPLINK_CONTEXT, deeplinkContent.toString());
                params.putString(ShareConstants.PROMO_CODE, promoCode);
                params.putString(ShareConstants.PROMO_TEXT, promoText);
            } catch (JSONException e) {
                Log.e(TAG, "Json Exception in creating deeplink context");
            }
        }
        return params;
    }
}
