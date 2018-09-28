package com.firebase.client.android;

import android.content.Context;
import android.os.Build.VERSION;
import android.os.Handler;
import android.util.Log;
import com.firebase.client.CredentialStore;
import com.firebase.client.EventTarget;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseException;
import com.firebase.client.Logger;
import com.firebase.client.Logger.Level;
import com.firebase.client.RunLoop;
import com.firebase.client.core.Platform;
import com.firebase.client.core.persistence.DefaultPersistenceManager;
import com.firebase.client.core.persistence.LRUCachePolicy;
import com.firebase.client.core.persistence.PersistenceManager;
import com.firebase.client.utilities.DefaultRunLoop;
import com.firebase.client.utilities.LogWrapper;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AndroidPlatform implements Platform {
    private static final Object mutex;
    private static AndroidPlatform platform;
    private final Context applicationContext;
    private final Set<String> createdPersistenceCaches;

    /* renamed from: com.firebase.client.android.AndroidPlatform.2 */
    class C05292 extends Thread {
        final /* synthetic */ Runnable val$r;

        C05292(Runnable runnable) {
            this.val$r = runnable;
        }

        public void run() {
            try {
                this.val$r.run();
            } catch (OutOfMemoryError e) {
                throw e;
            } catch (Throwable e2) {
                Log.e("Firebase", "An unexpected error occurred. Please contact support@firebase.com. Details: ", e2);
                RuntimeException runtimeException = new RuntimeException(e2);
            }
        }
    }

    /* renamed from: com.firebase.client.android.AndroidPlatform.1 */
    class C13251 extends DefaultRunLoop {
        final /* synthetic */ LogWrapper val$logger;

        /* renamed from: com.firebase.client.android.AndroidPlatform.1.1 */
        class C05281 implements Runnable {
            final /* synthetic */ Throwable val$e;
            final /* synthetic */ String val$message;

            C05281(String str, Throwable th) {
                this.val$message = str;
                this.val$e = th;
            }

            public void run() {
                throw new RuntimeException(this.val$message, this.val$e);
            }
        }

        C13251(LogWrapper logWrapper) {
            this.val$logger = logWrapper;
        }

        public void handleException(Throwable e) {
            String message = "Uncaught exception in Firebase runloop (" + Firebase.getSdkVersion() + "). Please report to support@firebase.com";
            this.val$logger.error(message, e);
            new Handler(AndroidPlatform.this.applicationContext.getMainLooper()).post(new C05281(message, e));
        }
    }

    static {
        mutex = new Object();
    }

    public AndroidPlatform(Context context) {
        this.createdPersistenceCaches = new HashSet();
        this.applicationContext = context.getApplicationContext();
        synchronized (mutex) {
            if (platform == null) {
                platform = this;
            } else {
                throw new IllegalStateException("Created more than one AndroidPlatform instance!");
            }
        }
    }

    public EventTarget newEventTarget(com.firebase.client.core.Context context) {
        return new AndroidEventTarget();
    }

    public RunLoop newRunLoop(com.firebase.client.core.Context ctx) {
        return new C13251(ctx.getLogger("RunLoop"));
    }

    public Logger newLogger(com.firebase.client.core.Context context, Level component, List<String> enabledComponents) {
        return new AndroidLogger(component, enabledComponents);
    }

    public String getUserAgent(com.firebase.client.core.Context context) {
        return VERSION.SDK_INT + "/Android";
    }

    public void runBackgroundTask(com.firebase.client.core.Context context, Runnable r) {
        new C05292(r).start();
    }

    public String getPlatformVersion() {
        return "android-" + Firebase.getSdkVersion();
    }

    public synchronized PersistenceManager createPersistenceManager(com.firebase.client.core.Context firebaseContext, String firebaseId) {
        String cacheId;
        String sessionId = firebaseContext.getSessionPersistenceKey();
        cacheId = firebaseId + "_" + sessionId;
        if (this.createdPersistenceCaches.contains(cacheId)) {
            throw new FirebaseException("SessionPersistenceKey '" + sessionId + "' has already been used.");
        }
        this.createdPersistenceCaches.add(cacheId);
        return new DefaultPersistenceManager(firebaseContext, new SqlPersistenceStorageEngine(this.applicationContext, firebaseContext, cacheId), new LRUCachePolicy(firebaseContext.getPersistenceCacheSizeBytes()));
    }

    public CredentialStore newCredentialStore(com.firebase.client.core.Context context) {
        return new AndroidCredentialStore(this.applicationContext);
    }
}
