package rx.internal.operators;

import rx.Observable;
import rx.Observable.Operator;
import rx.Observer;
import rx.Subscriber;
import rx.exceptions.Exceptions;
import rx.functions.Func1;
import rx.observers.SerializedSubscriber;
import rx.observers.Subscribers;
import rx.subjects.PublishSubject;

public final class OperatorDelayWithSelector<T, V> implements Operator<T, T> {
    final Func1<? super T, ? extends Observable<V>> itemDelay;
    final Observable<? extends T> source;

    /* renamed from: rx.internal.operators.OperatorDelayWithSelector.1 */
    class C13961 extends Subscriber<T> {
        final /* synthetic */ SerializedSubscriber val$child;
        final /* synthetic */ PublishSubject val$delayedEmissions;

        /* renamed from: rx.internal.operators.OperatorDelayWithSelector.1.1 */
        class C13951 implements Func1<V, T> {
            final /* synthetic */ Object val$t;

            C13951(Object obj) {
                this.val$t = obj;
            }

            public T call(V v) {
                return this.val$t;
            }
        }

        C13961(Subscriber x0, PublishSubject publishSubject, SerializedSubscriber serializedSubscriber) {
            this.val$delayedEmissions = publishSubject;
            this.val$child = serializedSubscriber;
            super(x0);
        }

        public void onCompleted() {
            this.val$delayedEmissions.onCompleted();
        }

        public void onError(Throwable e) {
            this.val$child.onError(e);
        }

        public void onNext(T t) {
            try {
                this.val$delayedEmissions.onNext(((Observable) OperatorDelayWithSelector.this.itemDelay.call(t)).take(1).defaultIfEmpty(null).map(new C13951(t)));
            } catch (Throwable e) {
                Exceptions.throwOrReport(e, (Observer) this);
            }
        }
    }

    public OperatorDelayWithSelector(Observable<? extends T> source, Func1<? super T, ? extends Observable<V>> itemDelay) {
        this.source = source;
        this.itemDelay = itemDelay;
    }

    public Subscriber<? super T> call(Subscriber<? super T> _child) {
        SerializedSubscriber<T> child = new SerializedSubscriber(_child);
        Observable delayedEmissions = PublishSubject.create();
        _child.add(Observable.merge(delayedEmissions).unsafeSubscribe(Subscribers.from(child)));
        return new C13961(_child, delayedEmissions, child);
    }
}
