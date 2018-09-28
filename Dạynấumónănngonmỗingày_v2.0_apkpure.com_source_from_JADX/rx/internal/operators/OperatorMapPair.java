package rx.internal.operators;

import rx.Observable;
import rx.Observable.Operator;
import rx.Producer;
import rx.Subscriber;
import rx.exceptions.Exceptions;
import rx.exceptions.OnErrorThrowable;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.internal.util.RxJavaPluginUtils;

public final class OperatorMapPair<T, U, R> implements Operator<Observable<? extends R>, T> {
    final Func1<? super T, ? extends Observable<? extends U>> collectionSelector;
    final Func2<? super T, ? super U, ? extends R> resultSelector;

    /* renamed from: rx.internal.operators.OperatorMapPair.1 */
    static class C14041 implements Func1<T, Observable<U>> {
        final /* synthetic */ Func1 val$selector;

        C14041(Func1 func1) {
            this.val$selector = func1;
        }

        public Observable<U> call(T t1) {
            return Observable.from((Iterable) this.val$selector.call(t1));
        }
    }

    static final class MapPairSubscriber<T, U, R> extends Subscriber<T> {
        final Subscriber<? super Observable<? extends R>> actual;
        final Func1<? super T, ? extends Observable<? extends U>> collectionSelector;
        boolean done;
        final Func2<? super T, ? super U, ? extends R> resultSelector;

        public MapPairSubscriber(Subscriber<? super Observable<? extends R>> actual, Func1<? super T, ? extends Observable<? extends U>> collectionSelector, Func2<? super T, ? super U, ? extends R> resultSelector) {
            this.actual = actual;
            this.collectionSelector = collectionSelector;
            this.resultSelector = resultSelector;
        }

        public void onNext(T outer) {
            try {
                this.actual.onNext(((Observable) this.collectionSelector.call(outer)).map(new OuterInnerMapper(outer, this.resultSelector)));
            } catch (Throwable ex) {
                Exceptions.throwIfFatal(ex);
                unsubscribe();
                onError(OnErrorThrowable.addValueAsLastCause(ex, outer));
            }
        }

        public void onError(Throwable e) {
            if (this.done) {
                RxJavaPluginUtils.handleException(e);
                return;
            }
            this.done = true;
            this.actual.onError(e);
        }

        public void onCompleted() {
            if (!this.done) {
                this.actual.onCompleted();
            }
        }

        public void setProducer(Producer p) {
            this.actual.setProducer(p);
        }
    }

    static final class OuterInnerMapper<T, U, R> implements Func1<U, R> {
        final T outer;
        final Func2<? super T, ? super U, ? extends R> resultSelector;

        public OuterInnerMapper(T outer, Func2<? super T, ? super U, ? extends R> resultSelector) {
            this.outer = outer;
            this.resultSelector = resultSelector;
        }

        public R call(U inner) {
            return this.resultSelector.call(this.outer, inner);
        }
    }

    public static <T, U> Func1<T, Observable<U>> convertSelector(Func1<? super T, ? extends Iterable<? extends U>> selector) {
        return new C14041(selector);
    }

    public OperatorMapPair(Func1<? super T, ? extends Observable<? extends U>> collectionSelector, Func2<? super T, ? super U, ? extends R> resultSelector) {
        this.collectionSelector = collectionSelector;
        this.resultSelector = resultSelector;
    }

    public Subscriber<? super T> call(Subscriber<? super Observable<? extends R>> o) {
        MapPairSubscriber<T, U, R> parent = new MapPairSubscriber(o, this.collectionSelector, this.resultSelector);
        o.add(parent);
        return parent;
    }
}
