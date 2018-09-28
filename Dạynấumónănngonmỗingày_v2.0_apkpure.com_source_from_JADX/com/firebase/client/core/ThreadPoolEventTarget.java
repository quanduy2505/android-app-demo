package com.firebase.client.core;

import com.firebase.client.EventTarget;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

class ThreadPoolEventTarget implements EventTarget {
    private final ThreadPoolExecutor executor;

    /* renamed from: com.firebase.client.core.ThreadPoolEventTarget.1 */
    class C05681 implements ThreadFactory {
        final /* synthetic */ ThreadInitializer val$threadInitializer;
        final /* synthetic */ ThreadFactory val$wrappedFactory;

        C05681(ThreadFactory threadFactory, ThreadInitializer threadInitializer) {
            this.val$wrappedFactory = threadFactory;
            this.val$threadInitializer = threadInitializer;
        }

        public Thread newThread(Runnable r) {
            Thread thread = this.val$wrappedFactory.newThread(r);
            this.val$threadInitializer.setName(thread, "FirebaseEventTarget");
            this.val$threadInitializer.setDaemon(thread, true);
            return thread;
        }
    }

    public ThreadPoolEventTarget(ThreadFactory wrappedFactory, ThreadInitializer threadInitializer) {
        int i = 1;
        this.executor = new ThreadPoolExecutor(1, i, 3, TimeUnit.SECONDS, new LinkedBlockingQueue(), new C05681(wrappedFactory, threadInitializer));
    }

    public void postEvent(Runnable r) {
        this.executor.execute(r);
    }

    public void shutdown() {
        this.executor.setCorePoolSize(0);
    }

    public void restart() {
        this.executor.setCorePoolSize(1);
    }
}
