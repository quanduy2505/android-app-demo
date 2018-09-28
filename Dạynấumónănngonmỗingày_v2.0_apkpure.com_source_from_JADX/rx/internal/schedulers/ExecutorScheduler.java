package rx.internal.schedulers;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import rx.Scheduler;
import rx.Scheduler.Worker;
import rx.Subscription;
import rx.functions.Action0;
import rx.plugins.RxJavaPlugins;
import rx.subscriptions.CompositeSubscription;
import rx.subscriptions.MultipleAssignmentSubscription;
import rx.subscriptions.Subscriptions;

public final class ExecutorScheduler extends Scheduler {
    final Executor executor;

    static final class ExecutorSchedulerWorker extends Worker implements Runnable {
        final Executor executor;
        final ConcurrentLinkedQueue<ScheduledAction> queue;
        final ScheduledExecutorService service;
        final CompositeSubscription tasks;
        final AtomicInteger wip;

        /* renamed from: rx.internal.schedulers.ExecutorScheduler.ExecutorSchedulerWorker.1 */
        class C15561 implements Action0 {
            final /* synthetic */ MultipleAssignmentSubscription val$mas;

            C15561(MultipleAssignmentSubscription multipleAssignmentSubscription) {
                this.val$mas = multipleAssignmentSubscription;
            }

            public void call() {
                ExecutorSchedulerWorker.this.tasks.remove(this.val$mas);
            }
        }

        /* renamed from: rx.internal.schedulers.ExecutorScheduler.ExecutorSchedulerWorker.2 */
        class C15572 implements Action0 {
            final /* synthetic */ Action0 val$action;
            final /* synthetic */ MultipleAssignmentSubscription val$mas;
            final /* synthetic */ Subscription val$removeMas;

            C15572(MultipleAssignmentSubscription multipleAssignmentSubscription, Action0 action0, Subscription subscription) {
                this.val$mas = multipleAssignmentSubscription;
                this.val$action = action0;
                this.val$removeMas = subscription;
            }

            public void call() {
                if (!this.val$mas.isUnsubscribed()) {
                    Subscription s2 = ExecutorSchedulerWorker.this.schedule(this.val$action);
                    this.val$mas.set(s2);
                    if (s2.getClass() == ScheduledAction.class) {
                        ((ScheduledAction) s2).add(this.val$removeMas);
                    }
                }
            }
        }

        public ExecutorSchedulerWorker(Executor executor) {
            this.executor = executor;
            this.queue = new ConcurrentLinkedQueue();
            this.wip = new AtomicInteger();
            this.tasks = new CompositeSubscription();
            this.service = GenericScheduledExecutorService.getInstance();
        }

        public Subscription schedule(Action0 action) {
            if (isUnsubscribed()) {
                return Subscriptions.unsubscribed();
            }
            Subscription ea = new ScheduledAction(action, this.tasks);
            this.tasks.add(ea);
            this.queue.offer(ea);
            if (this.wip.getAndIncrement() != 0) {
                return ea;
            }
            try {
                this.executor.execute(this);
                return ea;
            } catch (RejectedExecutionException t) {
                this.tasks.remove(ea);
                this.wip.decrementAndGet();
                RxJavaPlugins.getInstance().getErrorHandler().handleError(t);
                throw t;
            }
        }

        public void run() {
            while (!this.tasks.isUnsubscribed()) {
                ScheduledAction sa = (ScheduledAction) this.queue.poll();
                if (sa != null) {
                    if (!sa.isUnsubscribed()) {
                        if (this.tasks.isUnsubscribed()) {
                            this.queue.clear();
                            return;
                        }
                        sa.run();
                    }
                    if (this.wip.decrementAndGet() == 0) {
                        return;
                    }
                }
                return;
            }
            this.queue.clear();
        }

        public Subscription schedule(Action0 action, long delayTime, TimeUnit unit) {
            if (delayTime <= 0) {
                return schedule(action);
            }
            if (isUnsubscribed()) {
                return Subscriptions.unsubscribed();
            }
            MultipleAssignmentSubscription first = new MultipleAssignmentSubscription();
            MultipleAssignmentSubscription mas = new MultipleAssignmentSubscription();
            mas.set(first);
            this.tasks.add(mas);
            Subscription removeMas = Subscriptions.create(new C15561(mas));
            ScheduledAction ea = new ScheduledAction(new C15572(mas, action, removeMas));
            first.set(ea);
            try {
                ea.add(this.service.schedule(ea, delayTime, unit));
                return removeMas;
            } catch (RejectedExecutionException t) {
                RxJavaPlugins.getInstance().getErrorHandler().handleError(t);
                throw t;
            }
        }

        public boolean isUnsubscribed() {
            return this.tasks.isUnsubscribed();
        }

        public void unsubscribe() {
            this.tasks.unsubscribe();
            this.queue.clear();
        }
    }

    public ExecutorScheduler(Executor executor) {
        this.executor = executor;
    }

    public Worker createWorker() {
        return new ExecutorSchedulerWorker(this.executor);
    }
}
