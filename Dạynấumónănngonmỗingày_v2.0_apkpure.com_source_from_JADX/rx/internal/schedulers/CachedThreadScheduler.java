package rx.internal.schedulers;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReference;
import rx.Scheduler;
import rx.Scheduler.Worker;
import rx.Subscription;
import rx.functions.Action0;
import rx.internal.util.RxThreadFactory;
import rx.subscriptions.CompositeSubscription;
import rx.subscriptions.Subscriptions;

public final class CachedThreadScheduler extends Scheduler implements SchedulerLifecycle {
    private static final long KEEP_ALIVE_TIME = 60;
    private static final TimeUnit KEEP_ALIVE_UNIT;
    static final CachedWorkerPool NONE;
    static final ThreadWorker SHUTDOWN_THREADWORKER;
    final AtomicReference<CachedWorkerPool> pool;
    final ThreadFactory threadFactory;

    private static final class CachedWorkerPool {
        private final CompositeSubscription allWorkers;
        private final ScheduledExecutorService evictorService;
        private final Future<?> evictorTask;
        private final ConcurrentLinkedQueue<ThreadWorker> expiringWorkerQueue;
        private final long keepAliveTime;
        private final ThreadFactory threadFactory;

        /* renamed from: rx.internal.schedulers.CachedThreadScheduler.CachedWorkerPool.1 */
        class C08151 implements ThreadFactory {
            final /* synthetic */ ThreadFactory val$threadFactory;

            C08151(ThreadFactory threadFactory) {
                this.val$threadFactory = threadFactory;
            }

            public Thread newThread(Runnable r) {
                Thread thread = this.val$threadFactory.newThread(r);
                thread.setName(thread.getName() + " (Evictor)");
                return thread;
            }
        }

        /* renamed from: rx.internal.schedulers.CachedThreadScheduler.CachedWorkerPool.2 */
        class C08162 implements Runnable {
            C08162() {
            }

            public void run() {
                CachedWorkerPool.this.evictExpiredWorkers();
            }
        }

        CachedWorkerPool(ThreadFactory threadFactory, long keepAliveTime, TimeUnit unit) {
            this.threadFactory = threadFactory;
            this.keepAliveTime = unit != null ? unit.toNanos(keepAliveTime) : 0;
            this.expiringWorkerQueue = new ConcurrentLinkedQueue();
            this.allWorkers = new CompositeSubscription();
            ScheduledExecutorService evictor = null;
            Future<?> task = null;
            if (unit != null) {
                evictor = Executors.newScheduledThreadPool(1, new C08151(threadFactory));
                NewThreadWorker.tryEnableCancelPolicy(evictor);
                task = evictor.scheduleWithFixedDelay(new C08162(), this.keepAliveTime, this.keepAliveTime, TimeUnit.NANOSECONDS);
            }
            this.evictorService = evictor;
            this.evictorTask = task;
        }

        ThreadWorker get() {
            if (this.allWorkers.isUnsubscribed()) {
                return CachedThreadScheduler.SHUTDOWN_THREADWORKER;
            }
            while (!this.expiringWorkerQueue.isEmpty()) {
                ThreadWorker threadWorker = (ThreadWorker) this.expiringWorkerQueue.poll();
                if (threadWorker != null) {
                    return threadWorker;
                }
            }
            ThreadWorker w = new ThreadWorker(this.threadFactory);
            this.allWorkers.add(w);
            return w;
        }

        void release(ThreadWorker threadWorker) {
            threadWorker.setExpirationTime(now() + this.keepAliveTime);
            this.expiringWorkerQueue.offer(threadWorker);
        }

        void evictExpiredWorkers() {
            if (!this.expiringWorkerQueue.isEmpty()) {
                long currentTimestamp = now();
                Iterator i$ = this.expiringWorkerQueue.iterator();
                while (i$.hasNext()) {
                    ThreadWorker threadWorker = (ThreadWorker) i$.next();
                    if (threadWorker.getExpirationTime() > currentTimestamp) {
                        return;
                    }
                    if (this.expiringWorkerQueue.remove(threadWorker)) {
                        this.allWorkers.remove(threadWorker);
                    }
                }
            }
        }

        long now() {
            return System.nanoTime();
        }

        void shutdown() {
            try {
                if (this.evictorTask != null) {
                    this.evictorTask.cancel(true);
                }
                if (this.evictorService != null) {
                    this.evictorService.shutdownNow();
                }
                this.allWorkers.unsubscribe();
            } catch (Throwable th) {
                this.allWorkers.unsubscribe();
            }
        }
    }

    private static final class EventLoopWorker extends Worker {
        static final AtomicIntegerFieldUpdater<EventLoopWorker> ONCE_UPDATER;
        private final CompositeSubscription innerSubscription;
        volatile int once;
        private final CachedWorkerPool pool;
        private final ThreadWorker threadWorker;

        /* renamed from: rx.internal.schedulers.CachedThreadScheduler.EventLoopWorker.1 */
        class C15531 implements Action0 {
            final /* synthetic */ Action0 val$action;

            C15531(Action0 action0) {
                this.val$action = action0;
            }

            public void call() {
                if (!EventLoopWorker.this.isUnsubscribed()) {
                    this.val$action.call();
                }
            }
        }

        static {
            ONCE_UPDATER = AtomicIntegerFieldUpdater.newUpdater(EventLoopWorker.class, "once");
        }

        EventLoopWorker(CachedWorkerPool pool) {
            this.innerSubscription = new CompositeSubscription();
            this.pool = pool;
            this.threadWorker = pool.get();
        }

        public void unsubscribe() {
            if (ONCE_UPDATER.compareAndSet(this, 0, 1)) {
                this.pool.release(this.threadWorker);
            }
            this.innerSubscription.unsubscribe();
        }

        public boolean isUnsubscribed() {
            return this.innerSubscription.isUnsubscribed();
        }

        public Subscription schedule(Action0 action) {
            return schedule(action, 0, null);
        }

        public Subscription schedule(Action0 action, long delayTime, TimeUnit unit) {
            if (this.innerSubscription.isUnsubscribed()) {
                return Subscriptions.unsubscribed();
            }
            Subscription s = this.threadWorker.scheduleActual(new C15531(action), delayTime, unit);
            this.innerSubscription.add(s);
            s.addParent(this.innerSubscription);
            return s;
        }
    }

    private static final class ThreadWorker extends NewThreadWorker {
        private long expirationTime;

        ThreadWorker(ThreadFactory threadFactory) {
            super(threadFactory);
            this.expirationTime = 0;
        }

        public long getExpirationTime() {
            return this.expirationTime;
        }

        public void setExpirationTime(long expirationTime) {
            this.expirationTime = expirationTime;
        }
    }

    static {
        KEEP_ALIVE_UNIT = TimeUnit.SECONDS;
        SHUTDOWN_THREADWORKER = new ThreadWorker(RxThreadFactory.NONE);
        SHUTDOWN_THREADWORKER.unsubscribe();
        NONE = new CachedWorkerPool(null, 0, null);
        NONE.shutdown();
    }

    public CachedThreadScheduler(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
        this.pool = new AtomicReference(NONE);
        start();
    }

    public void start() {
        CachedWorkerPool update = new CachedWorkerPool(this.threadFactory, KEEP_ALIVE_TIME, KEEP_ALIVE_UNIT);
        if (!this.pool.compareAndSet(NONE, update)) {
            update.shutdown();
        }
    }

    public void shutdown() {
        CachedWorkerPool curr;
        do {
            curr = (CachedWorkerPool) this.pool.get();
            if (curr == NONE) {
                return;
            }
        } while (!this.pool.compareAndSet(curr, NONE));
        curr.shutdown();
    }

    public Worker createWorker() {
        return new EventLoopWorker((CachedWorkerPool) this.pool.get());
    }
}
