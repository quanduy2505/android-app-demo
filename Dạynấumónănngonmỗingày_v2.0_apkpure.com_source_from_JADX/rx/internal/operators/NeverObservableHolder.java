package rx.internal.operators;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;

public enum NeverObservableHolder implements OnSubscribe<Object> {
    INSTANCE;
    
    static final Observable<Object> NEVER;

    static {
        NEVER = Observable.create(INSTANCE);
    }

    public static <T> Observable<T> instance() {
        return NEVER;
    }

    public void call(Subscriber<? super Object> subscriber) {
    }
}
