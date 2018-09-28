package rx.plugins;

import rx.Observable;
import rx.Observable.Operator;
import rx.Single;
import rx.Single.OnSubscribe;
import rx.Subscription;

public abstract class RxJavaSingleExecutionHook {
    public <T> OnSubscribe<T> onCreate(OnSubscribe<T> f) {
        return f;
    }

    public <T> Observable.OnSubscribe<T> onSubscribeStart(Single<? extends T> single, Observable.OnSubscribe<T> onSubscribe) {
        return onSubscribe;
    }

    public <T> Subscription onSubscribeReturn(Subscription subscription) {
        return subscription;
    }

    public <T> Throwable onSubscribeError(Throwable e) {
        return e;
    }

    public <T, R> Operator<? extends R, ? super T> onLift(Operator<? extends R, ? super T> lift) {
        return lift;
    }
}
