package com.facebook.internal;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsIntent.Builder;
import com.facebook.FacebookSdk;

public class CustomTab {
    private Uri uri;

    public CustomTab(String action, Bundle parameters) {
        if (parameters == null) {
            parameters = new Bundle();
        }
        this.uri = Utility.buildUri(ServerProtocol.getDialogAuthority(), FacebookSdk.getGraphApiVersion() + "/" + ServerProtocol.DIALOG_PATH + action, parameters);
    }

    public void openCustomTab(Activity activity, String packageName) {
        CustomTabsIntent customTabsIntent = new Builder().build();
        customTabsIntent.intent.setPackage(packageName);
        customTabsIntent.intent.addFlags(1073741824);
        customTabsIntent.launchUrl(activity, this.uri);
    }
}
