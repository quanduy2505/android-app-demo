package android.support.customtabs;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.customtabs.ICustomTabsService.Stub;

public abstract class CustomTabsServiceConnection implements ServiceConnection {

    /* renamed from: android.support.customtabs.CustomTabsServiceConnection.1 */
    class C08261 extends CustomTabsClient {
        C08261(ICustomTabsService service, ComponentName componentName) {
            super(service, componentName);
        }
    }

    public abstract void onCustomTabsServiceConnected(ComponentName componentName, CustomTabsClient customTabsClient);

    public final void onServiceConnected(ComponentName name, IBinder service) {
        onCustomTabsServiceConnected(name, new C08261(Stub.asInterface(service), name));
    }
}
