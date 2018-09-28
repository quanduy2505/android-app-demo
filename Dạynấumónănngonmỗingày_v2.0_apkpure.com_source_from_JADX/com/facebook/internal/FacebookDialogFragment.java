package com.facebook.internal;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.internal.WebDialog.Builder;
import com.facebook.internal.WebDialog.OnCompleteListener;

public class FacebookDialogFragment extends DialogFragment {
    public static final String TAG = "FacebookDialogFragment";
    private Dialog dialog;

    /* renamed from: com.facebook.internal.FacebookDialogFragment.1 */
    class C09991 implements OnCompleteListener {
        C09991() {
        }

        public void onComplete(Bundle values, FacebookException error) {
            FacebookDialogFragment.this.onCompleteWebDialog(values, error);
        }
    }

    /* renamed from: com.facebook.internal.FacebookDialogFragment.2 */
    class C10002 implements OnCompleteListener {
        C10002() {
        }

        public void onComplete(Bundle values, FacebookException error) {
            FacebookDialogFragment.this.onCompleteWebFallbackDialog(values);
        }
    }

    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.dialog == null) {
            WebDialog webDialog;
            FragmentActivity activity = getActivity();
            Bundle params = NativeProtocol.getMethodArgumentsFromIntent(activity.getIntent());
            if (params.getBoolean(NativeProtocol.WEB_DIALOG_IS_FALLBACK, false)) {
                String url = params.getString(NativeProtocol.WEB_DIALOG_URL);
                if (Utility.isNullOrEmpty(url)) {
                    Utility.logd(TAG, "Cannot start a fallback WebDialog with an empty/missing 'url'");
                    activity.finish();
                    return;
                }
                webDialog = new FacebookWebFallbackDialog(activity, url, String.format("fb%s://bridge/", new Object[]{FacebookSdk.getApplicationId()}));
                webDialog.setOnCompleteListener(new C10002());
            } else {
                String actionName = params.getString(NativeProtocol.WEB_DIALOG_ACTION);
                Bundle webParams = params.getBundle(NativeProtocol.WEB_DIALOG_PARAMS);
                if (Utility.isNullOrEmpty(actionName)) {
                    Utility.logd(TAG, "Cannot start a WebDialog with an empty/missing 'actionName'");
                    activity.finish();
                    return;
                }
                webDialog = new Builder(activity, actionName, webParams).setOnCompleteListener(new C09991()).build();
            }
            this.dialog = webDialog;
        }
    }

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (this.dialog == null) {
            onCompleteWebDialog(null, null);
            setShowsDialog(false);
        }
        return this.dialog;
    }

    public void onResume() {
        super.onResume();
        if (this.dialog instanceof WebDialog) {
            ((WebDialog) this.dialog).resize();
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if ((this.dialog instanceof WebDialog) && isResumed()) {
            ((WebDialog) this.dialog).resize();
        }
    }

    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance()) {
            getDialog().setDismissMessage(null);
        }
        super.onDestroyView();
    }

    private void onCompleteWebDialog(Bundle values, FacebookException error) {
        FragmentActivity fragmentActivity = getActivity();
        fragmentActivity.setResult(error == null ? -1 : 0, NativeProtocol.createProtocolResultIntent(fragmentActivity.getIntent(), values, error));
        fragmentActivity.finish();
    }

    private void onCompleteWebFallbackDialog(Bundle values) {
        FragmentActivity fragmentActivity = getActivity();
        Intent resultIntent = new Intent();
        if (values == null) {
            values = new Bundle();
        }
        resultIntent.putExtras(values);
        fragmentActivity.setResult(-1, resultIntent);
        fragmentActivity.finish();
    }
}
