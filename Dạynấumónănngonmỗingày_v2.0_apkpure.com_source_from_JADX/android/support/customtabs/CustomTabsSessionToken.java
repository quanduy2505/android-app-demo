package android.support.customtabs;

import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.customtabs.ICustomTabsCallback.Stub;
import android.support.v4.app.BundleCompat;
import android.util.Log;

public class CustomTabsSessionToken {
    private static final String TAG = "CustomTabsSessionToken";
    private final CustomTabsCallback mCallback;
    private final ICustomTabsCallback mCallbackBinder;

    /* renamed from: android.support.customtabs.CustomTabsSessionToken.1 */
    class C08271 extends CustomTabsCallback {
        C08271() {
        }

        public void onNavigationEvent(int navigationEvent, Bundle extras) {
            try {
                CustomTabsSessionToken.this.mCallbackBinder.onNavigationEvent(navigationEvent, extras);
            } catch (RemoteException e) {
                Log.e(CustomTabsSessionToken.TAG, "RemoteException during ICustomTabsCallback transaction");
            }
        }
    }

    public static CustomTabsSessionToken getSessionTokenFromIntent(Intent intent) {
        IBinder binder = BundleCompat.getBinder(intent.getExtras(), CustomTabsIntent.EXTRA_SESSION);
        if (binder == null) {
            return null;
        }
        return new CustomTabsSessionToken(Stub.asInterface(binder));
    }

    CustomTabsSessionToken(ICustomTabsCallback callbackBinder) {
        this.mCallbackBinder = callbackBinder;
        this.mCallback = new C08271();
    }

    IBinder getCallbackBinder() {
        return this.mCallbackBinder.asBinder();
    }

    public int hashCode() {
        return getCallbackBinder().hashCode();
    }

    public boolean equals(Object o) {
        if (o instanceof CustomTabsSessionToken) {
            return ((CustomTabsSessionToken) o).getCallbackBinder().equals(this.mCallbackBinder.asBinder());
        }
        return false;
    }

    public CustomTabsCallback getCallback() {
        return this.mCallback;
    }
}
