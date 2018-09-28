package rx.internal.schedulers;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicReference;
import rx.internal.util.RxThreadFactory;

public final class GenericScheduledExecutorService implements SchedulerLifecycle {
    public static final GenericScheduledExecutorService INSTANCE;
    private static final ScheduledExecutorService[] NONE;
    private static final ScheduledExecutorService SHUTDOWN;
    private static final RxThreadFactory THREAD_FACTORY;
    private static final String THREAD_NAME_PREFIX = "RxScheduledExecutorPool-";
    private static int roundRobin;
    private final AtomicReference<ScheduledExecutorService[]> executor;

    static {
        THREAD_FACTORY = new RxThreadFactory(THREAD_NAME_PREFIX);
        NONE = new ScheduledExecutorService[0];
        SHUTDOWN = Executors.newScheduledThreadPool(0);
        SHUTDOWN.shutdown();
        INSTANCE = new GenericScheduledExecutorService();
    }

    private GenericScheduledExecutorService() {
        this.executor = new AtomicReference(NONE);
        start();
    }

    public void start() {
        int count = Runtime.getRuntime().availableProcessors();
        if (count > 4) {
            count /= 2;
        }
        if (count > 8) {
            count = 8;
        }
        ScheduledExecutorService[] execs = new ScheduledExecutorService[count];
        for (int i = 0; i < count; i++) {
            execs[i] = Executors.newScheduledThreadPool(1, THREAD_FACTORY);
        }
        if (this.executor.compareAndSet(NONE, execs)) {
            for (ScheduledExecutorService exec : execs) {
                if (!NewThreadWorker.tryEnableCancelPolicy(exec) && (exec instanceof ScheduledThreadPoolExecutor)) {
                    NewThreadWorker.registerExecutor((ScheduledThreadPoolExecutor) exec);
                }
            }
            return;
        }
        for (ScheduledExecutorService exec2 : execs) {
            exec2.shutdownNow();
        }
    }

    public void shutdown() {
        ScheduledExecutorService[] execs;
        do {
            execs = (ScheduledExecutorService[]) this.executor.get();
            if (execs == NONE) {
                return;
            }
        } while (!this.executor.compareAndSet(execs, NONE));
        for (ScheduledExecutorService exec : execs) {
            NewThreadWorker.deregisterExecutor(exec);
            exec.shutdownNow();
        }
    }

    public static ScheduledExecutorService getInstance() {
        ScheduledExecutorService[] execs = (ScheduledExecutorService[]) INSTANCE.executor.get();
        if (execs == NONE) {
            return SHUTDOWN;
        }
        int r = roundRobin + 1;
        if (r >= execs.length) {
            r = 0;
        }
        roundRobin = r;
        return execs[r];
    }
}
