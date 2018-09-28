package rx.internal.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

public final class RxThreadFactory extends AtomicLong implements ThreadFactory {
    public static final ThreadFactory NONE;
    final String prefix;

    /* renamed from: rx.internal.util.RxThreadFactory.1 */
    static class C08201 implements ThreadFactory {
        C08201() {
        }

        public Thread newThread(Runnable r) {
            throw new AssertionError("No threads allowed.");
        }
    }

    static {
        NONE = new C08201();
    }

    public RxThreadFactory(String prefix) {
        this.prefix = prefix;
    }

    public Thread newThread(Runnable r) {
        Thread t = new Thread(r, this.prefix + incrementAndGet());
        t.setDaemon(true);
        return t;
    }
}
