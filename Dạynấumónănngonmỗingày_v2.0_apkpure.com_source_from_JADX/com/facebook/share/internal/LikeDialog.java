package com.facebook.share.internal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import java.util.ArrayList;
import java.util.List;

public class LikeDialog extends FacebookDialogBase<LikeContent, Result> {
    private static final int DEFAULT_REQUEST_CODE;
    private static final String TAG = "LikeDialog";

    public static final class Result {
        private final Bundle bundle;

        public Result(Bundle bundle) {
            this.bundle = bundle;
        }

        public Bundle getData() {
            return this.bundle;
        }
    }

    /* renamed from: com.facebook.share.internal.LikeDialog.1 */
    class C10351 extends ResultProcessor {
        final /* synthetic */ FacebookCallback val$callback;

        C10351(FacebookCallback callback, FacebookCallback facebookCallback) {
            this.val$callback = facebookCallback;
            super(callback);
        }

        public void onSuccess(AppCall appCall, Bundle results) {
            this.val$callback.onSuccess(new Result(results));
        }
    }

    /* renamed from: com.facebook.share.internal.LikeDialog.2 */
    class C10362 implements Callback {
        final /* synthetic */ ResultProcessor val$resultProcessor;

        C10362(ResultProcessor resultProcessor) {
            this.val$resultProcessor = resultProcessor;
        }

        public boolean onActivityResult(int resultCode, Intent data) {
            return ShareInternalUtility.handleActivityResult(LikeDialog.this.getRequestCode(), resultCode, data, this.val$resultProcessor);
        }
    }

    private class NativeHandler extends ModeHandler {

        /* renamed from: com.facebook.share.internal.LikeDialog.NativeHandler.1 */
        class C10371 implements ParameterProvider {
            final /* synthetic */ LikeContent val$content;

            C10371(LikeContent likeContent) {
                this.val$content = likeContent;
            }

            public Bundle getParameters() {
                return LikeDialog.createParameters(this.val$content);
            }

            public Bundle getLegacyParameters() {
                Log.e(LikeDialog.TAG, "Attempting to present the Like Dialog with an outdated Facebook app on the device");
                return new Bundle();
            }
        }

        private NativeHandler() {
            super();
        }

        public boolean canShow(LikeContent content, boolean isBestEffort) {
            return content != null && LikeDialog.canShowNativeDialog();
        }

        public AppCall createAppCall(LikeContent content) {
            AppCall appCall = LikeDialog.this.createBaseAppCall();
            DialogPresenter.setupAppCallForNativeDialog(appCall, new C10371(content), LikeDialog.getFeature());
            return appCall;
        }
    }

    private class WebFallbackHandler extends ModeHandler {
        private WebFallbackHandler() {
            super();
        }

        public boolean canShow(LikeContent content, boolean isBestEffort) {
            return content != null && LikeDialog.canShowWebFallback();
        }

        public AppCall createAppCall(LikeContent content) {
            AppCall appCall = LikeDialog.this.createBaseAppCall();
            DialogPresenter.setupAppCallForWebFallbackDialog(appCall, LikeDialog.createParameters(content), LikeDialog.getFeature());
            return appCall;
        }
    }

    static {
        DEFAULT_REQUEST_CODE = RequestCodeOffset.Like.toRequestCode();
    }

    public static boolean canShowNativeDialog() {
        return DialogPresenter.canPresentNativeDialogWithFeature(getFeature());
    }

    public static boolean canShowWebFallback() {
        return DialogPresenter.canPresentWebFallbackDialogWithFeature(getFeature());
    }

    public LikeDialog(Activity activity) {
        super(activity, DEFAULT_REQUEST_CODE);
    }

    public LikeDialog(Fragment fragment) {
        this(new FragmentWrapper(fragment));
    }

    public LikeDialog(android.app.Fragment fragment) {
        this(new FragmentWrapper(fragment));
    }

    public LikeDialog(FragmentWrapper fragmentWrapper) {
        super(fragmentWrapper, DEFAULT_REQUEST_CODE);
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

    protected void registerCallbackImpl(CallbackManagerImpl callbackManager, FacebookCallback<Result> callback) {
        callbackManager.registerCallback(getRequestCode(), new C10362(callback == null ? null : new C10351(callback, callback)));
    }

    private static DialogFeature getFeature() {
        return LikeDialogFeature.LIKE_DIALOG;
    }

    private static Bundle createParameters(LikeContent likeContent) {
        Bundle params = new Bundle();
        params.putString(ShareConstants.WEB_DIALOG_PARAM_OBJECT_ID, likeContent.getObjectId());
        params.putString(ShareConstants.OBJECT_TYPE, likeContent.getObjectType());
        return params;
    }
}
