package com.facebook;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import com.facebook.internal.FacebookDialogFragment;
import com.facebook.internal.NativeProtocol;
import com.facebook.login.LoginFragment;
import com.facebook.share.internal.DeviceShareDialogFragment;
import com.facebook.share.model.ShareContent;

public class FacebookActivity extends FragmentActivity {
    private static String FRAGMENT_TAG;
    public static String PASS_THROUGH_CANCEL_ACTION;
    private static final String TAG;
    private Fragment singleFragment;

    static {
        PASS_THROUGH_CANCEL_ACTION = "PassThrough";
        FRAGMENT_TAG = "SingleFragment";
        TAG = FacebookActivity.class.getName();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (!FacebookSdk.isInitialized()) {
            Log.d(TAG, "Facebook SDK not initialized. Make sure you call sdkInitialize inside your Application's onCreate method.");
            FacebookSdk.sdkInitialize(getApplicationContext());
        }
        setContentView(C0378R.layout.com_facebook_activity_layout);
        if (PASS_THROUGH_CANCEL_ACTION.equals(intent.getAction())) {
            handlePassThroughError();
        } else {
            this.singleFragment = getFragment();
        }
    }

    protected Fragment getFragment() {
        Intent intent = getIntent();
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentByTag(FRAGMENT_TAG);
        if (fragment != null) {
            return fragment;
        }
        Fragment dialogFragment;
        if (FacebookDialogFragment.TAG.equals(intent.getAction())) {
            dialogFragment = new FacebookDialogFragment();
            dialogFragment.setRetainInstance(true);
            dialogFragment.show(manager, FRAGMENT_TAG);
            return dialogFragment;
        } else if (DeviceShareDialogFragment.TAG.equals(intent.getAction())) {
            dialogFragment = new DeviceShareDialogFragment();
            dialogFragment.setRetainInstance(true);
            dialogFragment.setShareContent((ShareContent) intent.getParcelableExtra("content"));
            dialogFragment.show(manager, FRAGMENT_TAG);
            return dialogFragment;
        } else {
            fragment = new LoginFragment();
            fragment.setRetainInstance(true);
            manager.beginTransaction().add(C0378R.id.com_facebook_fragment_container, fragment, FRAGMENT_TAG).commit();
            return fragment;
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.singleFragment != null) {
            this.singleFragment.onConfigurationChanged(newConfig);
        }
    }

    public Fragment getCurrentFragment() {
        return this.singleFragment;
    }

    private void handlePassThroughError() {
        setResult(0, NativeProtocol.createProtocolResultIntent(getIntent(), null, NativeProtocol.getExceptionFromErrorData(NativeProtocol.getMethodArgumentsFromIntent(getIntent()))));
        finish();
    }
}
