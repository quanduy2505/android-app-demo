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
import com.facebook.share.internal.ResultProcessor;
import com.facebook.share.internal.ShareConstants;
import com.facebook.share.internal.ShareInternalUtility;
import java.util.ArrayList;
import java.util.List;

@Deprecated
public class JoinAppGroupDialog extends FacebookDialogBase<String, Result> {
    private static final int DEFAULT_REQUEST_CODE;
    private static final String JOIN_GAME_GROUP_DIALOG = "game_group_join";

    @Deprecated
    public static final class Result {
        private final Bundle data;

        private Result(Bundle bundle) {
            this.data = bundle;
        }

        public Bundle getData() {
            return this.data;
        }
    }

    /* renamed from: com.facebook.share.widget.JoinAppGroupDialog.1 */
    class C10551 extends ResultProcessor {
        final /* synthetic */ FacebookCallback val$callback;

        C10551(FacebookCallback callback, FacebookCallback facebookCallback) {
            this.val$callback = facebookCallback;
            super(callback);
        }

        public void onSuccess(AppCall appCall, Bundle results) {
            this.val$callback.onSuccess(new Result(null));
        }
    }

    /* renamed from: com.facebook.share.widget.JoinAppGroupDialog.2 */
    class C10562 implements Callback {
        final /* synthetic */ ResultProcessor val$resultProcessor;

        C10562(ResultProcessor resultProcessor) {
            this.val$resultProcessor = resultProcessor;
        }

        public boolean onActivityResult(int resultCode, Intent data) {
            return ShareInternalUtility.handleActivityResult(JoinAppGroupDialog.this.getRequestCode(), resultCode, data, this.val$resultProcessor);
        }
    }

    private class WebHandler extends ModeHandler {
        private WebHandler() {
            super();
        }

        public boolean canShow(String content, boolean isBestEffort) {
            return true;
        }

        public AppCall createAppCall(String content) {
            AppCall appCall = JoinAppGroupDialog.this.createBaseAppCall();
            Bundle params = new Bundle();
            params.putString(ShareConstants.WEB_DIALOG_PARAM_ID, content);
            DialogPresenter.setupAppCallForWebDialog(appCall, JoinAppGroupDialog.JOIN_GAME_GROUP_DIALOG, params);
            return appCall;
        }
    }

    static {
        DEFAULT_REQUEST_CODE = RequestCodeOffset.AppGroupJoin.toRequestCode();
    }

    @Deprecated
    public static boolean canShow() {
        return true;
    }

    @Deprecated
    public static void show(Activity activity, String groupId) {
        new JoinAppGroupDialog(activity).show(groupId);
    }

    @Deprecated
    public static void show(Fragment fragment, String groupId) {
        show(new FragmentWrapper(fragment), groupId);
    }

    @Deprecated
    public static void show(android.app.Fragment fragment, String groupId) {
        show(new FragmentWrapper(fragment), groupId);
    }

    private static void show(FragmentWrapper fragmentWrapper, String groupId) {
        new JoinAppGroupDialog(fragmentWrapper).show(groupId);
    }

    @Deprecated
    public JoinAppGroupDialog(Activity activity) {
        super(activity, DEFAULT_REQUEST_CODE);
    }

    @Deprecated
    public JoinAppGroupDialog(Fragment fragment) {
        this(new FragmentWrapper(fragment));
    }

    @Deprecated
    public JoinAppGroupDialog(android.app.Fragment fragment) {
        this(new FragmentWrapper(fragment));
    }

    private JoinAppGroupDialog(FragmentWrapper fragmentWrapper) {
        super(fragmentWrapper, DEFAULT_REQUEST_CODE);
    }

    protected void registerCallbackImpl(CallbackManagerImpl callbackManager, FacebookCallback<Result> callback) {
        callbackManager.registerCallback(getRequestCode(), new C10562(callback == null ? null : new C10551(callback, callback)));
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
