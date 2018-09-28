package rx.internal.schedulers;

import android.support.v4.view.PointerIconCompat;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import rx.Scheduler.Worker;
import rx.Subscription;
import rx.exceptions.Exceptions;
import rx.functions.Action0;
import rx.internal.util.PlatformDependent;
import rx.internal.util.RxThreadFactory;
import rx.internal.util.SubscriptionList;
import rx.plugins.RxJavaPlugins;
import rx.plugins.RxJavaSchedulersHook;
import rx.subscriptions.CompositeSubscription;
import rx.subscriptions.Subscriptions;

public class NewThreadWorker extends Worker implements Subscription {
    private static final ConcurrentHashMap<ScheduledThreadPoolExecutor, ScheduledThreadPoolExecutor> EXECUTORS;
    private static final String FREQUENCY_KEY = "rx.scheduler.jdk6.purge-frequency-millis";
    private static final AtomicReference<ScheduledExecutorService> PURGE;
    private static final String PURGE_FORCE_KEY = "rx.scheduler.jdk6.purge-force";
    public static final int PURGE_FREQUENCY;
    private static final String PURGE_THREAD_PREFIX = "RxSchedulerPurge-";
    private static final Object SET_REMOVE_ON_CANCEL_POLICY_METHOD_NOT_SUPPORTED;
    private static final boolean SHOULD_TRY_ENABLE_CANCEL_POLICY;
    private static volatile Object cachedSetRemoveOnCancelPolicyMethod;
    private final ScheduledExecutorService executor;
    volatile boolean isUnsubscribed;
    private final RxJavaSchedulersHook schedulersHook;

    /* renamed from: rx.internal.schedulers.NewThreadWorker.1 */
    static class C08171 implements Runnable {
        C08171() {
        }

        public void run() {
            NewThreadWorker.purgeExecutors();
        }
    }

    static {
        boolean z;
        EXECUTORS = new ConcurrentHashMap();
        PURGE = new AtomicReference();
        PURGE_FREQUENCY = Integer.getInteger(FREQUENCY_KEY, PointerIconCompat.TYPE_DEFAULT).intValue();
        boolean purgeForce = Boolean.getBoolean(PURGE_FORCE_KEY);
        int androidApiVersion = PlatformDependent.getAndroidApiVersion();
        if (purgeForce || (androidApiVersion != 0 && androidApiVersion < 21)) {
            z = false;
        } else {
            z = true;
        }
        SHOULD_TRY_ENABLE_CANCEL_POLICY = z;
        SET_REMOVE_ON_CANCEL_POLICY_METHOD_NOT_SUPPORTED = new Object();
    }

    public static void registerExecutor(ScheduledThreadPoolExecutor service) {
        while (((ScheduledExecutorService) PURGE.get()) == null) {
            ScheduledExecutorService exec = Executors.newScheduledThreadPool(1, new RxThreadFactory(PURGE_THREAD_PREFIX));
            if (PURGE.compareAndSet(null, exec)) {
                exec.scheduleAtFixedRate(new C08171(), (long) PURGE_FREQUENCY, (long) PURGE_FREQUENCY, TimeUnit.MILLISECONDS);
                break;
            }
            exec.shutdownNow();
        }
        EXECUTORS.putIfAbsent(service, service);
    }

    public static void deregisterExecutor(ScheduledExecutorService service) {
        EXECUTORS.remove(service);
    }

    static void purgeExecutors() {
        try {
            Iterator<ScheduledThreadPoolExecutor> it = EXECUTORS.keySet().iterator();
            while (it.hasNext()) {
                ScheduledThreadPoolExecutor exec = (ScheduledThreadPoolExecutor) it.next();
                if (exec.isShutdown()) {
                    it.remove();
                } else {
                    exec.purge();
                }
            }
        } catch (Throwable t) {
            Exceptions.throwIfFatal(t);
            RxJavaPlugins.getInstance().getErrorHandler().handleError(t);
        }
    }

    public static boolean tryEnableCancelPolicy(ScheduledExecutorService executor) {
        if (SHOULD_TRY_ENABLE_CANCEL_POLICY) {
            Method methodToCall;
            if (executor instanceof ScheduledThreadPoolExecutor) {
                Object localSetRemoveOnCancelPolicyMethod = cachedSetRemoveOnCancelPolicyMethod;
                if (localSetRemoveOnCancelPolicyMethod == SET_REMOVE_ON_CANCEL_POLICY_METHOD_NOT_SUPPORTED) {
                    return false;
                }
                if (localSetRemoveOnCancelPolicyMethod == null) {
                    Method method = findSetRemoveOnCancelPolicyMethod(executor);
                    cachedSetRemoveOnCancelPolicyMethod = method != null ? method : SET_REMOVE_ON_CANCEL_POLICY_METHOD_NOT_SUPPORTED;
                    methodToCall = method;
                } else {
                    methodToCall = (Method) localSetRemoveOnCancelPolicyMethod;
                }
            } else {
                methodToCall = findSetRemoveOnCancelPolicyMethod(executor);
            }
            if (methodToCall != null) {
                try {
                    methodToCall.invoke(executor, new Object[]{Boolean.valueOf(true)});
                    return true;
                } catch (Exception e) {
                    RxJavaPlugins.getInstance().getErrorHandler().handleError(e);
                }
            }
        }
        return false;
    }

    static Method findSetRemoveOnCancelPolicyMethod(ScheduledExecutorService executor) {
        Method[] arr$ = executor.getClass().getMethods();
        int len$ = arr$.length;
        for (int i$ = PURGE_FREQUENCY; i$ < len$; i$++) {
            Method method = arr$[i$];
            if (method.getName().equals("setRemoveOnCancelPolicy")) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length == 1 && parameterTypes[PURGE_FREQUENCY] == Boolean.TYPE) {
                    return method;
                }
            }
        }
        return null;
    }

    public NewThreadWorker(ThreadFactory threadFactory) {
        ScheduledExecutorService exec = Executors.newScheduledThreadPool(1, threadFactory);
        if (!tryEnableCancelPolicy(exec) && (exec instanceof ScheduledThreadPoolExecutor)) {
            registerExecutor((ScheduledThreadPoolExecutor) exec);
        }
        this.schedulersHook = RxJavaPlugins.getInstance().getSchedulersHook();
        this.executor = exec;
    }

    public Subscription schedule(Action0 action) {
        return schedule(action, 0, null);
    }

    public Subscription schedule(Action0 action, long delayTime, TimeUnit unit) {
        if (this.isUnsubscribed) {
            return Subscriptions.unsubscribed();
        }
        return scheduleActual(action, delayTime, unit);
    }

    public ScheduledAction scheduleActual(Action0 action, long delayTime, TimeUnit unit) {
        Future f;
        ScheduledAction run = new ScheduledAction(this.schedulersHook.onSchedule(action));
        if (delayTime <= 0) {
            f = this.executor.submit(run);
        } else {
            f = this.executor.schedule(run, delayTime, unit);
        }
        run.add(f);
        return run;
    }

    public ScheduledAction scheduleActual(Action0 action, long delayTime, TimeUnit unit, CompositeSubscription parent) {
        Future f;
        ScheduledAction run = new ScheduledAction(this.schedulersHook.onSchedule(action), parent);
        parent.add(run);
        if (delayTime <= 0) {
            f = this.executor.submit(run);
        } else {
            f = this.executor.schedule(run, delayTime, unit);
        }
        run.add(f);
        return run;
    }

    public ScheduledAction scheduleActual(Action0 action, long delayTime, TimeUnit unit, SubscriptionList parent) {
        Future f;
        ScheduledAction run = new ScheduledAction(this.schedulersHook.onSchedule(action), parent);
        parent.add(run);
        if (delayTime <= 0) {
            f = this.executor.submit(run);
        } else {
            f = this.executor.schedule(run, delayTime, unit);
        }
        run.add(f);
        return run;
    }

    public void unsubscribe() {
        this.isUnsubscribed = true;
        this.executor.shutdownNow();
        deregisterExecutor(this.executor);
    }

    public boolean isUnsubscribed() {
        return this.isUnsubscribed;
    }
}
