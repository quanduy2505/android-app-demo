package rx.internal.operators;

import rx.Observable.Operator;
import rx.Subscriber;

public class OperatorIgnoreElements<T> implements Operator<T, T> {

    private static class Holder {
        static final OperatorIgnoreElements<?> INSTANCE;

        private Holder() {
        }

        static {
            INSTANCE = new OperatorIgnoreElements();
        }
    }

    /* renamed from: rx.internal.operators.OperatorIgnoreElements.1 */
    class C14031 extends Subscriber<T> {
        final /* synthetic */ Subscriber val$child;

        C14031(Subscriber subscriber) {
            this.val$child = subscriber;
        }

        public void onCompleted() {
            this.val$child.onCompleted();
        }

        public void onError(Throwable e) {
            this.val$child.onError(e);
        }

        public void onNext(T t) {
        }
    }

    public static <T> OperatorIgnoreElements<T> instance() {
        return Holder.INSTANCE;
    }

    OperatorIgnoreElements() {
    }

    public Subscriber<? super T> call(Subscriber<? super T> child) {
        Subscriber<T> parent = new C14031(child);
        child.add(parent);
        return parent;
    }
}
