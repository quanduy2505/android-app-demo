package rx.internal.operators;

import rx.Observable;
import rx.Observer;
import rx.Scheduler.Worker;
import rx.Subscriber;
import rx.Subscription;
import rx.exceptions.Exceptions;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

public class OperatorTimeoutWithSelector<T, U, V> extends OperatorTimeoutBase<T> {

    /* renamed from: rx.internal.operators.OperatorTimeoutWithSelector.1 */
    class C15451 implements FirstTimeoutStub<T> {
        final /* synthetic */ Func0 val$firstTimeoutSelector;

        /* renamed from: rx.internal.operators.OperatorTimeoutWithSelector.1.1 */
        class C14441 extends Subscriber<U> {
            final /* synthetic */ Long val$seqId;
            final /* synthetic */ TimeoutSubscriber val$timeoutSubscriber;

            C14441(TimeoutSubscriber timeoutSubscriber, Long l) {
                this.val$timeoutSubscriber = timeoutSubscriber;
                this.val$seqId = l;
            }

            public void onCompleted() {
                this.val$timeoutSubscriber.onTimeout(this.val$seqId.longValue());
            }

            public void onError(Throwable e) {
                this.val$timeoutSubscriber.onError(e);
            }

            public void onNext(U u) {
                this.val$timeoutSubscriber.onTimeout(this.val$seqId.longValue());
            }
        }

        C15451(Func0 func0) {
            this.val$firstTimeoutSelector = func0;
        }

        public Subscription call(TimeoutSubscriber<T> timeoutSubscriber, Long seqId, Worker inner) {
            if (this.val$firstTimeoutSelector == null) {
                return Subscriptions.unsubscribed();
            }
            try {
                return ((Observable) this.val$firstTimeoutSelector.call()).unsafeSubscribe(new C14441(timeoutSubscriber, seqId));
            } catch (Throwable t) {
                Exceptions.throwOrReport(t, (Observer) timeoutSubscriber);
                return Subscriptions.unsubscribed();
            }
        }
    }

    /* renamed from: rx.internal.operators.OperatorTimeoutWithSelector.2 */
    class C15462 implements TimeoutStub<T> {
        final /* synthetic */ Func1 val$timeoutSelector;

        /* renamed from: rx.internal.operators.OperatorTimeoutWithSelector.2.1 */
        class C14451 extends Subscriber<V> {
            final /* synthetic */ Long val$seqId;
            final /* synthetic */ TimeoutSubscriber val$timeoutSubscriber;

            C14451(TimeoutSubscriber timeoutSubscriber, Long l) {
                this.val$timeoutSubscriber = timeoutSubscriber;
                this.val$seqId = l;
            }

            public void onCompleted() {
                this.val$timeoutSubscriber.onTimeout(this.val$seqId.longValue());
            }

            public void onError(Throwable e) {
                this.val$timeoutSubscriber.onError(e);
            }

            public void onNext(V v) {
                this.val$timeoutSubscriber.onTimeout(this.val$seqId.longValue());
            }
        }

        C15462(Func1 func1) {
            this.val$timeoutSelector = func1;
        }

        public Subscription call(TimeoutSubscriber<T> timeoutSubscriber, Long seqId, T value, Worker inner) {
            try {
                return ((Observable) this.val$timeoutSelector.call(value)).unsafeSubscribe(new C14451(timeoutSubscriber, seqId));
            } catch (Throwable t) {
                Exceptions.throwOrReport(t, (Observer) timeoutSubscriber);
                return Subscriptions.unsubscribed();
            }
        }
    }

    public /* bridge */ /* synthetic */ Subscriber call(Subscriber x0) {
        return super.call(x0);
    }

    public OperatorTimeoutWithSelector(Func0<? extends Observable<U>> firstTimeoutSelector, Func1<? super T, ? extends Observable<V>> timeoutSelector, Observable<? extends T> other) {
        super(new C15451(firstTimeoutSelector), new C15462(timeoutSelector), other, Schedulers.immediate());
    }
}
