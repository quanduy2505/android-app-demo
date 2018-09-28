package rx.internal.operators;

import java.util.concurrent.TimeUnit;
import rx.Observable.OnSubscribe;
import rx.Scheduler;
import rx.Scheduler.Worker;
import rx.Subscriber;
import rx.exceptions.Exceptions;
import rx.functions.Action0;

public final class OnSubscribeTimerOnce implements OnSubscribe<Long> {
    final Scheduler scheduler;
    final long time;
    final TimeUnit unit;

    /* renamed from: rx.internal.operators.OnSubscribeTimerOnce.1 */
    class C15201 implements Action0 {
        final /* synthetic */ Subscriber val$child;

        C15201(Subscriber subscriber) {
            this.val$child = subscriber;
        }

        public void call() {
            try {
                this.val$child.onNext(Long.valueOf(0));
                this.val$child.onCompleted();
            } catch (Throwable t) {
                Exceptions.throwOrReport(t, this.val$child);
            }
        }
    }

    public OnSubscribeTimerOnce(long time, TimeUnit unit, Scheduler scheduler) {
        this.time = time;
        this.unit = unit;
        this.scheduler = scheduler;
    }

    public void call(Subscriber<? super Long> child) {
        Worker worker = this.scheduler.createWorker();
        child.add(worker);
        worker.schedule(new C15201(child), this.time, this.unit);
    }
}
