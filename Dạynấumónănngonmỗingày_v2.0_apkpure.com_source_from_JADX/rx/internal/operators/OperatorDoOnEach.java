package rx.internal.operators;

import java.util.Arrays;
import rx.Observable.Operator;
import rx.Observer;
import rx.Subscriber;
import rx.exceptions.CompositeException;
import rx.exceptions.Exceptions;

public class OperatorDoOnEach<T> implements Operator<T, T> {
    final Observer<? super T> doOnEachObserver;

    /* renamed from: rx.internal.operators.OperatorDoOnEach.1 */
    class C14011 extends Subscriber<T> {
        private boolean done;
        final /* synthetic */ Subscriber val$observer;

        C14011(Subscriber x0, Subscriber subscriber) {
            this.val$observer = subscriber;
            super(x0);
            this.done = false;
        }

        public void onCompleted() {
            if (!this.done) {
                try {
                    OperatorDoOnEach.this.doOnEachObserver.onCompleted();
                    this.done = true;
                    this.val$observer.onCompleted();
                } catch (Throwable e) {
                    Exceptions.throwOrReport(e, (Observer) this);
                }
            }
        }

        public void onError(Throwable e) {
            Exceptions.throwIfFatal(e);
            if (!this.done) {
                this.done = true;
                try {
                    OperatorDoOnEach.this.doOnEachObserver.onError(e);
                    this.val$observer.onError(e);
                } catch (Throwable e2) {
                    Exceptions.throwIfFatal(e2);
                    this.val$observer.onError(new CompositeException(Arrays.asList(new Throwable[]{e, e2})));
                }
            }
        }

        public void onNext(T value) {
            if (!this.done) {
                try {
                    OperatorDoOnEach.this.doOnEachObserver.onNext(value);
                    this.val$observer.onNext(value);
                } catch (Throwable e) {
                    Exceptions.throwOrReport(e, this, value);
                }
            }
        }
    }

    public OperatorDoOnEach(Observer<? super T> doOnEachObserver) {
        this.doOnEachObserver = doOnEachObserver;
    }

    public Subscriber<? super T> call(Subscriber<? super T> observer) {
        return new C14011(observer, observer);
    }
}
