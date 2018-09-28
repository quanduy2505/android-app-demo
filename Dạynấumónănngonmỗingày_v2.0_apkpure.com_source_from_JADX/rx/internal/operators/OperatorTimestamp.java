package rx.internal.operators;

import rx.Observable.Operator;
import rx.Scheduler;
import rx.Subscriber;
import rx.schedulers.Timestamped;

public final class OperatorTimestamp<T> implements Operator<Timestamped<T>, T> {
    final Scheduler scheduler;

    /* renamed from: rx.internal.operators.OperatorTimestamp.1 */
    class C14461 extends Subscriber<T> {
        final /* synthetic */ Subscriber val$o;

        C14461(Subscriber x0, Subscriber subscriber) {
            this.val$o = subscriber;
            super(x0);
        }

        public void onCompleted() {
            this.val$o.onCompleted();
        }

        public void onError(Throwable e) {
            this.val$o.onError(e);
        }

        public void onNext(T t) {
            this.val$o.onNext(new Timestamped(OperatorTimestamp.this.scheduler.now(), t));
        }
    }

    public OperatorTimestamp(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public Subscriber<? super T> call(Subscriber<? super Timestamped<T>> o) {
        return new C14461(o, o);
    }
}
