package android.support.customtabs;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.RemoteException;
import android.support.customtabs.ICustomTabsService.Stub;
import android.support.v4.util.ArrayMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public abstract class CustomTabsService extends Service {
    public static final String ACTION_CUSTOM_TABS_CONNECTION = "android.support.customtabs.action.CustomTabsService";
    public static final String KEY_URL = "android.support.customtabs.otherurls.URL";
    private Stub mBinder;
    private final Map<IBinder, DeathRecipient> mDeathRecipientMap;

    /* renamed from: android.support.customtabs.CustomTabsService.1 */
    class C12891 extends Stub {

        /* renamed from: android.support.customtabs.CustomTabsService.1.1 */
        class C00211 implements DeathRecipient {
            final /* synthetic */ CustomTabsSessionToken val$sessionToken;

            C00211(CustomTabsSessionToken customTabsSessionToken) {
                this.val$sessionToken = customTabsSessionToken;
            }

            public void binderDied() {
                CustomTabsService.this.cleanUpSession(this.val$sessionToken);
            }
        }

        C12891() {
        }

        public boolean warmup(long flags) {
            return CustomTabsService.this.warmup(flags);
        }

        public boolean newSession(ICustomTabsCallback callback) {
            boolean z = false;
            CustomTabsSessionToken sessionToken = new CustomTabsSessionToken(callback);
            try {
                DeathRecipient deathRecipient = new C00211(sessionToken);
                synchronized (CustomTabsService.this.mDeathRecipientMap) {
                    callback.asBinder().linkToDeath(deathRecipient, 0);
                    CustomTabsService.this.mDeathRecipientMap.put(callback.asBinder(), deathRecipient);
                }
                z = CustomTabsService.this.newSession(sessionToken);
            } catch (RemoteException e) {
            }
            return z;
        }

        public boolean mayLaunchUrl(ICustomTabsCallback callback, Uri url, Bundle extras, List<Bundle> otherLikelyBundles) {
            return CustomTabsService.this.mayLaunchUrl(new CustomTabsSessionToken(callback), url, extras, otherLikelyBundles);
        }

        public Bundle extraCommand(String commandName, Bundle args) {
            return CustomTabsService.this.extraCommand(commandName, args);
        }

        public boolean updateVisuals(ICustomTabsCallback callback, Bundle bundle) {
            return CustomTabsService.this.updateVisuals(new CustomTabsSessionToken(callback), bundle);
        }
    }

    protected abstract Bundle extraCommand(String str, Bundle bundle);

    protected abstract boolean mayLaunchUrl(CustomTabsSessionToken customTabsSessionToken, Uri uri, Bundle bundle, List<Bundle> list);

    protected abstract boolean newSession(CustomTabsSessionToken customTabsSessionToken);

    protected abstract boolean updateVisuals(CustomTabsSessionToken customTabsSessionToken, Bundle bundle);

    protected abstract boolean warmup(long j);

    public CustomTabsService() {
        this.mDeathRecipientMap = new ArrayMap();
        this.mBinder = new C12891();
    }

    public IBinder onBind(Intent intent) {
        return this.mBinder;
    }

    protected boolean cleanUpSession(CustomTabsSessionToken sessionToken) {
        try {
            synchronized (this.mDeathRecipientMap) {
                IBinder binder = sessionToken.getCallbackBinder();
                binder.unlinkToDeath((DeathRecipient) this.mDeathRecipientMap.get(binder), 0);
                this.mDeathRecipientMap.remove(binder);
            }
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }
}
