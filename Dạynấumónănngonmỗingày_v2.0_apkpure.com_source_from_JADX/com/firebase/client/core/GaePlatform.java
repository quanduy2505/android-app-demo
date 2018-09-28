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
import com.firebase.tubesock.ThreadInitializer;
import com.firebase.tubesock.WebSocket;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.ThreadFactory;

enum GaePlatform implements Platform {
    INSTANCE;
    
    static ThreadFactory threadFactoryInstance;
    static final ThreadInitializer threadInitializerInstance;

    /* renamed from: com.firebase.client.core.GaePlatform.4 */
    class C05474 implements Runnable {
        final /* synthetic */ Context val$ctx;
        final /* synthetic */ Runnable val$r;

        C05474(Runnable runnable, Context context) {
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

    /* renamed from: com.firebase.client.core.GaePlatform.1 */
    static class C10901 implements ThreadInitializer {
        C10901() {
        }

        public void setName(Thread t, String name) {
        }

        public void setDaemon(Thread t, boolean isDaemon) {
        }

        public void setUncaughtExceptionHandler(Thread t, UncaughtExceptionHandler handler) {
            t.setUncaughtExceptionHandler(handler);
        }
    }

    /* renamed from: com.firebase.client.core.GaePlatform.2 */
    class C10912 implements ThreadInitializer {
        C10912() {
        }

        public void setName(Thread thread, String s) {
            GaePlatform.threadInitializerInstance.setName(thread, s);
        }
    }

    /* renamed from: com.firebase.client.core.GaePlatform.3 */
    class C13263 extends DefaultRunLoop {
        final /* synthetic */ LogWrapper val$logger;

        C13263(LogWrapper logWrapper) {
            this.val$logger = logWrapper;
        }

        public void handleException(Throwable e) {
            this.val$logger.error("Uncaught exception in Firebase runloop (" + Firebase.getSdkVersion() + "). Please report to support@firebase.com", e);
        }

        protected ThreadFactory getThreadFactory() {
            return GaePlatform.threadFactoryInstance;
        }

        protected ThreadInitializer getThreadInitializer() {
            return GaePlatform.threadInitializerInstance;
        }
    }

    static {
        threadInitializerInstance = new C10901();
    }

    public Logger newLogger(Context ctx, Level level, List<String> components) {
        return new DefaultLogger(level, components);
    }

    private static ThreadFactory getGaeThreadFactory() {
        if (threadFactoryInstance == null) {
            try {
                Class c = Class.forName("com.google.appengine.api.ThreadManager");
                if (c != null) {
                    threadFactoryInstance = (ThreadFactory) c.getMethod("backgroundThreadFactory", new Class[0]).invoke(null, new Object[0]);
                }
            } catch (ClassNotFoundException e) {
                return null;
            } catch (InvocationTargetException e2) {
                throw new RuntimeException(e2);
            } catch (NoSuchMethodException e3) {
                throw new RuntimeException(e3);
            } catch (IllegalAccessException e4) {
                throw new RuntimeException(e4);
            }
        }
        return threadFactoryInstance;
    }

    public static boolean isActive() {
        return getGaeThreadFactory() != null;
    }

    public void initialize() {
        WebSocket.setThreadFactory(threadFactoryInstance, new C10912());
    }

    public EventTarget newEventTarget(Context ctx) {
        return new ThreadPoolEventTarget(getGaeThreadFactory(), threadInitializerInstance);
    }

    public RunLoop newRunLoop(Context context) {
        return new C13263(context.getLogger("RunLoop"));
    }

    public String getUserAgent(Context ctx) {
        return System.getProperty("java.specification.version", AnalyticsEvents.PARAMETER_DIALOG_OUTCOME_VALUE_UNKNOWN) + "/" + "AppEngine";
    }

    public String getPlatformVersion() {
        return "gae-" + Firebase.getSdkVersion();
    }

    public PersistenceManager createPersistenceManager(Context ctx, String namespace) {
        return null;
    }

    public CredentialStore newCredentialStore(Context ctx) {
        return new NoopCredentialStore(ctx);
    }

    public void runBackgroundTask(Context ctx, Runnable r) {
        threadFactoryInstance.newThread(new C05474(r, ctx)).start();
    }
}
