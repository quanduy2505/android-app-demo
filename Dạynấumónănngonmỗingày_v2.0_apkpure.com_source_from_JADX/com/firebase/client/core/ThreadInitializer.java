package com.firebase.client.core;

import java.lang.Thread.UncaughtExceptionHandler;

public interface ThreadInitializer {
    public static final ThreadInitializer defaultInstance;

    /* renamed from: com.firebase.client.core.ThreadInitializer.1 */
    static class C11081 implements ThreadInitializer {
        C11081() {
        }

        public void setName(Thread t, String name) {
            t.setName(name);
        }

        public void setDaemon(Thread t, boolean isDaemon) {
            t.setDaemon(isDaemon);
        }

        public void setUncaughtExceptionHandler(Thread t, UncaughtExceptionHandler handler) {
            t.setUncaughtExceptionHandler(handler);
        }
    }

    void setDaemon(Thread thread, boolean z);

    void setName(Thread thread, String str);

    void setUncaughtExceptionHandler(Thread thread, UncaughtExceptionHandler uncaughtExceptionHandler);

    static {
        defaultInstance = new C11081();
    }
}
