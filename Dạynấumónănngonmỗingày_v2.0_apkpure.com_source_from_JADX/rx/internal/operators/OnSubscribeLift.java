package rx.internal.operators;

import rx.Observable.OnSubscribe;
import rx.Observable.Operator;
import rx.Subscriber;
import rx.exceptions.Exceptions;
import rx.plugins.RxJavaObservableExecutionHook;
import rx.plugins.RxJavaPlugins;

public final class OnSubscribeLift<T, R> implements OnSubscribe<R> {
    static final RxJavaObservableExecutionHook hook;
    final Operator<? extends R, ? super T> operator;
    final OnSubscribe<T> parent;

    static {
        hook = RxJavaPlugins.getInstance().getObservableExecutionHook();
    }

    public OnSubscribeLift(OnSubscribe<T> parent, Operator<? extends R, ? super T> operator) {
        this.parent = parent;
        this.operator = operator;
    }

    public void call(Subscriber<? super R> o) {
        Subscriber<? super T> st;
        try {
            st = (Subscriber) hook.onLift(this.operator).call(o);
            st.onStart();
            this.parent.call(st);
        } catch (Throwable e) {
            Exceptions.throwIfFatal(e);
            o.onError(e);
        }
    }
}
