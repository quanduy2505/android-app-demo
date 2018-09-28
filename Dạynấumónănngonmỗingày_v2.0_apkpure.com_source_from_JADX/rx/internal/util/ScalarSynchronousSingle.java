package rx.internal.util;

import rx.Scheduler;
import rx.Scheduler.Worker;
import rx.Single;
import rx.Single.OnSubscribe;
import rx.SingleSubscriber;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.internal.schedulers.EventLoopsScheduler;

public final class ScalarSynchronousSingle<T> extends Single<T> {
    final T value;

    static final class ScalarSynchronousSingleAction<T> implements Action0 {
        private final SingleSubscriber<? super T> subscriber;
        private final T value;

        ScalarSynchronousSingleAction(SingleSubscriber<? super T> subscriber, T value) {
            this.subscriber = subscriber;
            this.value = value;
        }

        public void call() {
            try {
                this.subscriber.onSuccess(this.value);
            } catch (Throwable t) {
                this.subscriber.onError(t);
            }
        }
    }

    /* renamed from: rx.internal.util.ScalarSynchronousSingle.1 */
    class C16171 implements OnSubscribe<T> {
        final /* synthetic */ Object val$t;

        C16171(Object obj) {
            this.val$t = obj;
        }

        public void call(SingleSubscriber<? super T> te) {
            te.onSuccess(this.val$t);
        }
    }

    /* renamed from: rx.internal.util.ScalarSynchronousSingle.2 */
    class C16182 implements OnSubscribe<R> {
        final /* synthetic */ Func1 val$func;

        /* renamed from: rx.internal.util.ScalarSynchronousSingle.2.1 */
        class C14681 extends Subscriber<R> {
            final /* synthetic */ SingleSubscriber val$child;

            C14681(SingleSubscriber singleSubscriber) {
                this.val$child = singleSubscriber;
            }

            public void onCompleted() {
            }

            public void onError(Throwable e) {
                this.val$child.onError(e);
            }

            public void onNext(R r) {
                this.val$child.onSuccess(r);
            }
        }

        C16182(Func1 func1) {
            this.val$func = func1;
        }

        public void call(SingleSubscriber<? super R> child) {
            Single<? extends R> o = (Single) this.val$func.call(ScalarSynchronousSingle.this.value);
            if (o instanceof ScalarSynchronousSingle) {
                child.onSuccess(((ScalarSynchronousSingle) o).value);
                return;
            }
            Subscriber<R> subscriber = new C14681(child);
            child.add(subscriber);
            o.unsafeSubscribe(subscriber);
        }
    }

    static final class DirectScheduledEmission<T> implements OnSubscribe<T> {
        private final EventLoopsScheduler es;
        private final T value;

        DirectScheduledEmission(EventLoopsScheduler es, T value) {
            this.es = es;
            this.value = value;
        }

        public void call(SingleSubscriber<? super T> singleSubscriber) {
            singleSubscriber.add(this.es.scheduleDirect(new ScalarSynchronousSingleAction(singleSubscriber, this.value)));
        }
    }

    static final class NormalScheduledEmission<T> implements OnSubscribe<T> {
        private final Scheduler scheduler;
        private final T value;

        NormalScheduledEmission(Scheduler scheduler, T value) {
            this.scheduler = scheduler;
            this.value = value;
        }

        public void call(SingleSubscriber<? super T> singleSubscriber) {
            Worker worker = this.scheduler.createWorker();
            singleSubscriber.add(worker);
            worker.schedule(new ScalarSynchronousSingleAction(singleSubscriber, this.value));
        }
    }

    public static final <T> ScalarSynchronousSingle<T> create(T t) {
        return new ScalarSynchronousSingle(t);
    }

    protected ScalarSynchronousSingle(T t) {
        super(new C16171(t));
        this.value = t;
    }

    public T get() {
        return this.value;
    }

    public Single<T> scalarScheduleOn(Scheduler scheduler) {
        if (scheduler instanceof EventLoopsScheduler) {
            return Single.create(new DirectScheduledEmission((EventLoopsScheduler) scheduler, this.value));
        }
        return Single.create(new NormalScheduledEmission(scheduler, this.value));
    }

    public <R> Single<R> scalarFlatMap(Func1<? super T, ? extends Single<? extends R>> func) {
        return Single.create(new C16182(func));
    }
}
