package com.firebase.client.utilities;

import com.firebase.client.RunLoop;
import com.firebase.client.core.ThreadInitializer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public abstract class DefaultRunLoop implements RunLoop {
    private ScheduledThreadPoolExecutor executor;

    /* renamed from: com.firebase.client.utilities.DefaultRunLoop.1 */
    class C05871 implements Runnable {
        final /* synthetic */ Runnable val$runnable;

        C05871(Runnable runnable) {
            this.val$runnable = runnable;
        }

        public void run() {
            try {
                this.val$runnable.run();
            } catch (Throwable e) {
                DefaultRunLoop.this.handleException(e);
            }
        }
    }

    /* renamed from: com.firebase.client.utilities.DefaultRunLoop.2 */
    class C05882 implements Runnable {
        final /* synthetic */ Runnable val$runnable;

        C05882(Runnable runnable) {
            this.val$runnable = runnable;
        }

        public void run() {
            try {
                this.val$runnable.run();
            } catch (Throwable e) {
                DefaultRunLoop.this.handleException(e);
            }
        }
    }

    private class FirebaseThreadFactory implements ThreadFactory {

        /* renamed from: com.firebase.client.utilities.DefaultRunLoop.FirebaseThreadFactory.1 */
        class C05891 implements UncaughtExceptionHandler {
            C05891() {
            }

            public void uncaughtException(Thread t, Throwable e) {
                DefaultRunLoop.this.handleException(e);
            }
        }

        private FirebaseThreadFactory() {
        }

        public Thread newThread(Runnable r) {
            Thread thread = DefaultRunLoop.this.getThreadFactory().newThread(r);
            ThreadInitializer initializer = DefaultRunLoop.this.getThreadInitializer();
            initializer.setName(thread, "FirebaseWorker");
            initializer.setDaemon(thread, true);
            initializer.setUncaughtExceptionHandler(thread, new C05891());
            return thread;
        }
    }

    public abstract void handleException(Throwable th);

    protected ThreadFactory getThreadFactory() {
        return Executors.defaultThreadFactory();
    }

    protected ThreadInitializer getThreadInitializer() {
        return ThreadInitializer.defaultInstance;
    }

    public DefaultRunLoop() {
        this.executor = new ScheduledThreadPoolExecutor(1, new FirebaseThreadFactory());
        this.executor.setKeepAliveTime(3, TimeUnit.SECONDS);
    }

    public void scheduleNow(Runnable runnable) {
        this.executor.execute(new C05871(runnable));
    }

    public ScheduledFuture schedule(Runnable runnable, long milliseconds) {
        return this.executor.schedule(new C05882(runnable), milliseconds, TimeUnit.MILLISECONDS);
    }

    public void shutdown() {
        this.executor.setCorePoolSize(0);
    }

    public void restart() {
        this.executor.setCorePoolSize(1);
    }
}
