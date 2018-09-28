package rx;

import java.util.concurrent.TimeUnit;
import rx.functions.Action0;
import rx.subscriptions.MultipleAssignmentSubscription;

public abstract class Scheduler {
    static final long CLOCK_DRIFT_TOLERANCE_NANOS;

    public static abstract class Worker implements Subscription {

        /* renamed from: rx.Scheduler.Worker.1 */
        class C15081 implements Action0 {
            long count;
            long lastNowNanos;
            long startInNanos;
            final /* synthetic */ Action0 val$action;
            final /* synthetic */ long val$firstNowNanos;
            final /* synthetic */ long val$firstStartInNanos;
            final /* synthetic */ MultipleAssignmentSubscription val$mas;
            final /* synthetic */ long val$periodInNanos;

            C15081(long j, long j2, MultipleAssignmentSubscription multipleAssignmentSubscription, Action0 action0, long j3) {
                this.val$firstNowNanos = j;
                this.val$firstStartInNanos = j2;
                this.val$mas = multipleAssignmentSubscription;
                this.val$action = action0;
                this.val$periodInNanos = j3;
                this.lastNowNanos = this.val$firstNowNanos;
                this.startInNanos = this.val$firstStartInNanos;
            }

            public void call() {
                if (!this.val$mas.isUnsubscribed()) {
                    long nextTick;
                    this.val$action.call();
                    long nowNanos = TimeUnit.MILLISECONDS.toNanos(Worker.this.now());
                    long j;
                    long j2;
                    if (Scheduler.CLOCK_DRIFT_TOLERANCE_NANOS + nowNanos < this.lastNowNanos || nowNanos >= (this.lastNowNanos + this.val$periodInNanos) + Scheduler.CLOCK_DRIFT_TOLERANCE_NANOS) {
                        nextTick = nowNanos + this.val$periodInNanos;
                        j = this.val$periodInNanos;
                        j2 = this.count + 1;
                        this.count = j2;
                        this.startInNanos = nextTick - (j * j2);
                    } else {
                        j = this.startInNanos;
                        j2 = this.count + 1;
                        this.count = j2;
                        nextTick = j + (j2 * this.val$periodInNanos);
                    }
                    this.lastNowNanos = nowNanos;
                    this.val$mas.set(Worker.this.schedule(this, nextTick - nowNanos, TimeUnit.NANOSECONDS));
                }
            }
        }

        public abstract Subscription schedule(Action0 action0);

        public abstract Subscription schedule(Action0 action0, long j, TimeUnit timeUnit);

        public Subscription schedulePeriodically(Action0 action, long initialDelay, long period, TimeUnit unit) {
            long periodInNanos = unit.toNanos(period);
            long firstNowNanos = TimeUnit.MILLISECONDS.toNanos(now());
            long firstStartInNanos = firstNowNanos + unit.toNanos(initialDelay);
            MultipleAssignmentSubscription mas = new MultipleAssignmentSubscription();
            Action0 recursiveAction = new C15081(firstNowNanos, firstStartInNanos, mas, action, periodInNanos);
            MultipleAssignmentSubscription s = new MultipleAssignmentSubscription();
            mas.set(s);
            s.set(schedule(recursiveAction, initialDelay, unit));
            return mas;
        }

        public long now() {
            return System.currentTimeMillis();
        }
    }

    public abstract Worker createWorker();

    static {
        CLOCK_DRIFT_TOLERANCE_NANOS = TimeUnit.MINUTES.toNanos(Long.getLong("rx.scheduler.drift-tolerance", 15).longValue());
    }

    public long now() {
        return System.currentTimeMillis();
    }
}
