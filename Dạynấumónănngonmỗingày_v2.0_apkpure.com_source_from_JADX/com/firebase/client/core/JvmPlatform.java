package com.firebase.client.core;

import com.facebook.internal.AnalyticsEvents;
import com.firebase.client.CredentialStore;
import com.firebase.client.EventTarget;
import com.firebase.client.Firebase;
import com.firebase.client.Logger;
import com.firebase.client.Logger.Level;
import com.firebase.client.RunLoop;
import com.firebase.client.authentication.NoopCredentialStore;
import com.firebase.client.core.persistence.PersistenceManager;
import com.firebase.client.utilities.DefaultLogger;
import com.firebase.client.utilities.DefaultRunLoop;
import com.firebase.client.utilities.LogWrapper;
import java.util.List;
import java.util.concurrent.Executors;

enum JvmPlatform implements Platform {
    INSTANCE;

    /* renamed from: com.firebase.client.core.JvmPlatform.2 */
    class C05482 extends Thread {
        final /* synthetic */ Context val$ctx;
        final /* synthetic */ Runnable val$r;

        C05482(Runnable runnable, Context context) {
            this.val$r = runnable;
            this.val$ctx = context;
        }

        public void run() {
            try {
                this.val$r.run();
            } catch (OutOfMemoryError e) {
                throw e;
            } catch (Throwable e2) {
                this.val$ctx.getLogger("BackgroundTask").error("An unexpected error occurred. Please contact support@firebase.com. Details: ", e2);
                RuntimeException runtimeException = new RuntimeException(e2);
            }
        }
    }

    /* renamed from: com.firebase.client.core.JvmPlatform.1 */
    class C13271 extends DefaultRunLoop {
        final /* synthetic */ LogWrapper val$logger;

        C13271(LogWrapper logWrapper) {
            this.val$logger = logWrapper;
        }

        public void handleException(Throwable e) {
            this.val$logger.error("Uncaught exception in Firebase runloop (" + Firebase.getSdkVersion() + "). Please report to support@firebase.com", e);
        }
    }

    public Logger newLogger(Context ctx, Level level, List<String> components) {
        return new DefaultLogger(level, components);
    }

    public EventTarget newEventTarget(Context ctx) {
        return new ThreadPoolEventTarget(Executors.defaultThreadFactory(), ThreadInitializer.defaultInstance);
    }

    public RunLoop newRunLoop(Context context) {
        return new C13271(context.getLogger("RunLoop"));
    }

    public String getUserAgent(Context ctx) {
        return System.getProperty("java.specification.version", AnalyticsEvents.PARAMETER_DIALOG_OUTCOME_VALUE_UNKNOWN) + "/" + System.getProperty("java.vm.name", "Unknown JVM");
    }

    public String getPlatformVersion() {
        return "jvm-" + Firebase.getSdkVersion();
    }

    public PersistenceManager createPersistenceManager(Context ctx, String namespace) {
        return null;
    }

    public CredentialStore newCredentialStore(Context ctx) {
        return new NoopCredentialStore(ctx);
    }

    public void runBackgroundTask(Context ctx, Runnable r) {
        new C05482(r, ctx).start();
    }
}
