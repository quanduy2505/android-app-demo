package rx.internal.operators;

import java.util.Iterator;
import rx.Observable.Operator;
import rx.Observer;
import rx.Subscriber;
import rx.exceptions.Exceptions;
import rx.functions.Func2;
import rx.observers.Subscribers;

public final class OperatorZipIterable<T1, T2, R> implements Operator<R, T1> {
    final Iterable<? extends T2> iterable;
    final Func2<? super T1, ? super T2, ? extends R> zipFunction;

    /* renamed from: rx.internal.operators.OperatorZipIterable.1 */
    class C14561 extends Subscriber<T1> {
        boolean done;
        final /* synthetic */ Iterator val$iterator;
        final /* synthetic */ Subscriber val$subscriber;

        C14561(Subscriber x0, Subscriber subscriber, Iterator it) {
            this.val$subscriber = subscriber;
            this.val$iterator = it;
            super(x0);
        }

        public void onCompleted() {
            if (!this.done) {
                this.done = true;
                this.val$subscriber.onCompleted();
            }
        }

        public void onError(Throwable e) {
            if (this.done) {
                Exceptions.throwIfFatal(e);
                return;
            }
            this.done = true;
            this.val$subscriber.onError(e);
        }

        public void onNext(T1 t) {
            if (!this.done) {
                try {
                    this.val$subscriber.onNext(OperatorZipIterable.this.zipFunction.call(t, this.val$iterator.next()));
                    if (!this.val$iterator.hasNext()) {
                        onCompleted();
                    }
                } catch (Throwable e) {
                    Exceptions.throwOrReport(e, (Observer) this);
                }
            }
        }
    }

    public OperatorZipIterable(Iterable<? extends T2> iterable, Func2<? super T1, ? super T2, ? extends R> zipFunction) {
        this.iterable = iterable;
        this.zipFunction = zipFunction;
    }

    public Subscriber<? super T1> call(Subscriber<? super R> subscriber) {
        Iterator<? extends T2> iterator = this.iterable.iterator();
        try {
            if (iterator.hasNext()) {
                return new C14561(subscriber, subscriber, iterator);
            }
            subscriber.onCompleted();
            return Subscribers.empty();
        } catch (Throwable e) {
            Exceptions.throwOrReport(e, (Observer) subscriber);
            return Subscribers.empty();
        }
    }
}
