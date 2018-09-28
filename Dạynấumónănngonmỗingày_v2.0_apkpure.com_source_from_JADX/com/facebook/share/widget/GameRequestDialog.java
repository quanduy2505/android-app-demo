package com.facebook.share.widget;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.facebook.FacebookCallback;
import com.facebook.internal.AppCall;
import com.facebook.internal.CallbackManagerImpl;
import com.facebook.internal.CallbackManagerImpl.Callback;
import com.facebook.internal.CallbackManagerImpl.RequestCodeOffset;
import com.facebook.internal.DialogPresenter;
import com.facebook.internal.FacebookDialogBase;
import com.facebook.internal.FragmentWrapper;
import com.facebook.share.internal.GameRequestValidation;
import com.facebook.share.internal.ResultProcessor;
import com.facebook.share.internal.ShareConstants;
import com.facebook.share.internal.ShareInternalUtility;
import com.facebook.share.internal.WebDialogParameters;
import com.facebook.share.model.GameRequestContent;
import java.util.ArrayList;
import java.util.List;

public class GameRequestDialog extends FacebookDialogBase<GameRequestContent, Result> {
    private static final int DEFAULT_REQUEST_CODE;
    private static final String GAME_REQUEST_DIALOG = "apprequests";

    public static final class Result {
        String requestId;
        List<String> to;

        private Result(Bundle results) {
            this.requestId = results.getString(ShareConstants.WEB_DIALOG_RESULT_PARAM_REQUEST_ID);
            this.to = new ArrayList();
            while (true) {
                if (results.containsKey(String.format(ShareConstants.WEB_DIALOG_RESULT_PARAM_TO_ARRAY_MEMBER, new Object[]{Integer.valueOf(this.to.size())}))) {
                    this.to.add(results.getString(String.format(ShareConstants.WEB_DIALOG_RESULT_PARAM_TO_ARRAY_MEMBER, new Object[]{Integer.valueOf(this.to.size())})));
                } else {
                    return;
                }
            }
        }

        public String getRequestId() {
            return this.requestId;
        }

        public List<String> getRequestRecipients() {
            return this.to;
        }
    }

    /* renamed from: com.facebook.share.widget.GameRequestDialog.1 */
    class C10531 extends ResultProcessor {
        final /* synthetic */ FacebookCallback val$callback;

        C10531(FacebookCallback callback, FacebookCallback facebookCallback) {
            this.val$callback = facebookCallback;
            super(callback);
        }

        public void onSuccess(AppCall appCall, Bundle results) {
            if (results != null) {
                this.val$callback.onSuccess(new Result(null));
            } else {
                onCancel(appCall);
            }
        }
    }

    /* renamed from: com.facebook.share.widget.GameRequestDialog.2 */
    class C10542 implements Callback {
        final /* synthetic */ ResultProcessor val$resultProcessor;

        C10542(ResultProcessor resultProcessor) {
            this.val$resultProcessor = resultProcessor;
        }

        public boolean onActivityResult(int resultCode, Intent data) {
            return ShareInternalUtility.handleActivityResult(GameRequestDialog.this.getRequestCode(), resultCode, data, this.val$resultProcessor);
        }
    }

    private class WebHandler extends ModeHandler {
        private WebHandler() {
            super();
        }

        public boolean canShow(GameRequestContent content, boolean isBestEffort) {
            return true;
        }

        public AppCall createAppCall(GameRequestContent content) {
            GameRequestValidation.validate(content);
            AppCall appCall = GameRequestDialog.this.createBaseAppCall();
            DialogPresenter.setupAppCallForWebDialog(appCall, GameRequestDialog.GAME_REQUEST_DIALOG, WebDialogParameters.create(content));
            return appCall;
        }
    }

    static {
        DEFAULT_REQUEST_CODE = RequestCodeOffset.GameRequest.toRequestCode();
    }

    public static boolean canShow() {
        return true;
    }

    public static void show(Activity activity, GameRequestContent gameRequestContent) {
        new GameRequestDialog(activity).show(gameRequestContent);
    }

    public static void show(Fragment fragment, GameRequestContent gameRequestContent) {
        show(new FragmentWrapper(fragment), gameRequestContent);
    }

    public static void show(android.app.Fragment fragment, GameRequestContent gameRequestContent) {
        show(new FragmentWrapper(fragment), gameRequestContent);
    }

    private static void show(FragmentWrapper fragmentWrapper, GameRequestContent gameRequestContent) {
        new GameRequestDialog(fragmentWrapper).show(gameRequestContent);
    }

    public GameRequestDialog(Activity activity) {
        super(activity, DEFAULT_REQUEST_CODE);
    }

    public GameRequestDialog(Fragment fragment) {
        this(new FragmentWrapper(fragment));
    }

    public GameRequestDialog(android.app.Fragment fragment) {
        this(new FragmentWrapper(fragment));
    }

    private GameRequestDialog(FragmentWrapper fragmentWrapper) {
        super(fragmentWrapper, DEFAULT_REQUEST_CODE);
    }

    protected void registerCallbackImpl(CallbackManagerImpl callbackManager, FacebookCallback<Result> callback) {
        ResultProcessor resultProcessor;
        if (callback == null) {
            resultProcessor = null;
        } else {
            resultProcessor = new C10531(callback, callback);
        }
        callbackManager.registerCallback(getRequestCode(), new C10542(resultProcessor));
    }

    protected AppCall createBaseAppCall() {
        return new AppCall(getRequestCode());
    }

    protected List<ModeHandler> getOrderedModeHandlers() {
        ArrayList<ModeHandler> handlers = new ArrayList();
        handlers.add(new WebHandler());
        return handlers;
    }
}
