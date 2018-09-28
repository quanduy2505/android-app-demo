package rx.internal.operators;

import java.util.concurrent.atomic.AtomicInteger;
import rx.Observable;
import rx.Observable.Operator;
import rx.Producer;
import rx.Scheduler.Worker;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Func2;
import rx.internal.producers.ProducerArbiter;
import rx.schedulers.Schedulers;
import rx.subscriptions.SerialSubscription;

public final class OperatorRetryWithPredicate<T> implements Operator<T, Observable<T>> {
    final Func2<Integer, Throwable, Boolean> predicate;

    static final class SourceSubscriber<T> extends Subscriber<Observable<T>> {
        final AtomicInteger attempts;
        final Subscriber<? super T> child;
        final Worker inner;
        final ProducerArbiter pa;
        final Func2<Integer, Throwable, Boolean> predicate;
        final SerialSubscription serialSubscription;

        /* renamed from: rx.internal.operators.OperatorRetryWithPredicate.SourceSubscriber.1 */
        class C15361 implements Action0 {
            final /* synthetic */ Observable val$o;

            /* renamed from: rx.internal.operators.OperatorRetryWithPredicate.SourceSubscriber.1.1 */
            class C14181 extends Subscriber<T> {
                boolean done;
                final /* synthetic */ Action0 val$_self;

                C14181(Action0 action0) {
                    this.val$_self = action0;
                }

                public void onCompleted() {
                    if (!this.done) {
                        this.done = true;
                        SourceSubscriber.this.child.onCompleted();
                    }
                }

                public void onError(Throwable e) {
                    if (!this.done) {
                        this.done = true;
                        if (!((Boolean) SourceSubscriber.this.predicate.call(Integer.valueOf(SourceSubscriber.this.attempts.get()), e)).booleanValue() || SourceSubscriber.this.inner.isUnsubscribed()) {
                            SourceSubscriber.this.child.onError(e);
                        } else {
                            SourceSubscriber.this.inner.schedule(this.val$_self);
                        }
                    }
                }

                public void onNext(T v) {
                    if (!this.done) {
                        SourceSubscriber.this.child.onNext(v);
                        SourceSubscriber.this.pa.produced(1);
                    }
                }

                public void setProducer(Producer p) {
                    SourceSubscriber.this.pa.setProducer(p);
                }
            }

            C15361(Observable observable) {
                this.val$o = observable;
            }

            public void call() {
                SourceSubscriber.this.attempts.incrementAndGet();
                Subscriber<T> subscriber = new C14181(this);
                SourceSubscriber.this.serialSubscription.set(subscriber);
                this.val$o.unsafeSubscribe(subscriber);
            }
        }

        public SourceSubscriber(Subscriber<? super T> child, Func2<Integer, Throwable, Boolean> predicate, Worker inner, SerialSubscription serialSubscription, ProducerArbiter pa) {
            this.attempts = new AtomicInteger();
            this.child = child;
            this.predicate = predicate;
            this.inner = inner;
            this.serialSubscription = serialSubscription;
            this.pa = pa;
        }

        public void onCompleted() {
        }

        public void onError(Throwable e) {
            this.child.onError(e);
        }

        public void onNext(Observable<T> o) {
            this.inner.schedule(new C15361(o));
        }
    }

    public OperatorRetryWithPredicate(Func2<Integer, Throwable, Boolean> predicate) {
        this.predicate = predicate;
    }

    public Subscriber<? super Observable<T>> call(Subscriber<? super T> child) {
        Worker inner = Schedulers.trampoline().createWorker();
        child.add(inner);
        SerialSubscription serialSubscription = new SerialSubscription();
        child.add(serialSubscription);
        ProducerArbiter pa = new ProducerArbiter();
        child.setProducer(pa);
        return new SourceSubscriber(child, this.predicate, inner, serialSubscription, pa);
    }
}
