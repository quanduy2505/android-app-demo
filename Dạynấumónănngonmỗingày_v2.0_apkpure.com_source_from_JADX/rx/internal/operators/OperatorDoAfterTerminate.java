package rx.internal.operators;

import rx.Observable.Operator;
import rx.Subscriber;
import rx.exceptions.Exceptions;
import rx.functions.Action0;
import rx.plugins.RxJavaPlugins;

public final class OperatorDoAfterTerminate<T> implements Operator<T, T> {
    final Action0 action;

    /* renamed from: rx.internal.operators.OperatorDoAfterTerminate.1 */
    class C14001 extends Subscriber<T> {
        final /* synthetic */ Subscriber val$child;

        C14001(Subscriber x0, Subscriber subscriber) {
            this.val$child = subscriber;
            super(x0);
        }

        public void onNext(T t) {
            this.val$child.onNext(t);
        }

        public void onError(Throwable e) {
            try {
                this.val$child.onError(e);
            } finally {
                callAction();
            }
        }

        public void onCompleted() {
            try {
                this.val$child.onCompleted();
            } finally {
                callAction();
            }
        }

        void callAction() {
            try {
                OperatorDoAfterTerminate.this.action.call();
            } catch (Throwable ex) {
                Exceptions.throwIfFatal(ex);
                RxJavaPlugins.getInstance().getErrorHandler().handleError(ex);
            }
        }
    }

    public OperatorDoAfterTerminate(Action0 action) {
        if (action == null) {
            throw new NullPointerException("Action can not be null");
        }
        this.action = action;
    }

    public Subscriber<? super T> call(Subscriber<? super T> child) {
        return new C14001(child, child);
    }
}
