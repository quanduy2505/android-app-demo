package android.support.customtabs;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.customtabs.ICustomTabsCallback.Stub;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.List;

public class CustomTabsClient {
    private final ICustomTabsService mService;
    private final ComponentName mServiceComponentName;

    /* renamed from: android.support.customtabs.CustomTabsClient.1 */
    static class C08251 extends CustomTabsServiceConnection {
        final /* synthetic */ Context val$applicationContext;

        C08251(Context context) {
            this.val$applicationContext = context;
        }

        public final void onCustomTabsServiceConnected(ComponentName name, CustomTabsClient client) {
            client.warmup(0);
            this.val$applicationContext.unbindService(this);
        }

        public final void onServiceDisconnected(ComponentName componentName) {
        }
    }

    /* renamed from: android.support.customtabs.CustomTabsClient.2 */
    class C12882 extends Stub {
        final /* synthetic */ CustomTabsCallback val$callback;

        C12882(CustomTabsCallback customTabsCallback) {
            this.val$callback = customTabsCallback;
        }

        public void onNavigationEvent(int navigationEvent, Bundle extras) {
            if (this.val$callback != null) {
                this.val$callback.onNavigationEvent(navigationEvent, extras);
            }
        }

        public void extraCallback(String callbackName, Bundle args) throws RemoteException {
            if (this.val$callback != null) {
                this.val$callback.extraCallback(callbackName, args);
            }
        }
    }

    @RestrictTo({Scope.GROUP_ID})
    CustomTabsClient(ICustomTabsService service, ComponentName componentName) {
        this.mService = service;
        this.mServiceComponentName = componentName;
    }

    public static boolean bindCustomTabsService(Context context, String packageName, CustomTabsServiceConnection connection) {
        Intent intent = new Intent(CustomTabsService.ACTION_CUSTOM_TABS_CONNECTION);
        if (!TextUtils.isEmpty(packageName)) {
            intent.setPackage(packageName);
        }
        return context.bindService(intent, connection, 33);
    }

    public static String getPackageName(Context context, @Nullable List<String> packages) {
        return getPackageName(context, packages, false);
    }

    public static String getPackageName(Context context, @Nullable List<String> packages, boolean ignoreDefault) {
        PackageManager pm = context.getPackageManager();
        if (packages == null) {
            List packageNames = new ArrayList();
        } else {
            List<String> list = packages;
        }
        Intent activityIntent = new Intent("android.intent.action.VIEW", Uri.parse("http://"));
        if (!ignoreDefault) {
            ResolveInfo defaultViewHandlerInfo = pm.resolveActivity(activityIntent, 0);
            if (defaultViewHandlerInfo != null) {
                String packageName = defaultViewHandlerInfo.activityInfo.packageName;
                List<String> packageNames2 = new ArrayList(packageNames.size() + 1);
                packageNames2.add(packageName);
                if (packages != null) {
                    packageNames2.addAll(packages);
                }
                packageNames = packageNames2;
            }
        }
        Intent serviceIntent = new Intent(CustomTabsService.ACTION_CUSTOM_TABS_CONNECTION);
        for (String packageName2 : r3) {
            serviceIntent.setPackage(packageName2);
            if (pm.resolveService(serviceIntent, 0) != null) {
                return packageName2;
            }
        }
        return null;
    }

    public static boolean connectAndInitialize(Context context, String packageName) {
        boolean z = false;
        if (packageName != null) {
            Context applicationContext = context.getApplicationContext();
            try {
                z = bindCustomTabsService(applicationContext, packageName, new C08251(applicationContext));
            } catch (SecurityException e) {
            }
        }
        return z;
    }

    public boolean warmup(long flags) {
        try {
            return this.mService.warmup(flags);
        } catch (RemoteException e) {
            return false;
        }
    }

    public CustomTabsSession newSession(CustomTabsCallback callback) {
        Stub wrapper = new C12882(callback);
        try {
            if (this.mService.newSession(wrapper)) {
                return new CustomTabsSession(this.mService, wrapper, this.mServiceComponentName);
            }
            return null;
        } catch (RemoteException e) {
            return null;
        }
    }

    public Bundle extraCommand(String commandName, Bundle args) {
        try {
            return this.mService.extraCommand(commandName, args);
        } catch (RemoteException e) {
            return null;
        }
    }
}
