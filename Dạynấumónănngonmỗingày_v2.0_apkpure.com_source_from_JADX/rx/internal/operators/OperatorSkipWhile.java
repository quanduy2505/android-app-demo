package rx.internal.operators;

import rx.Observable.Operator;
import rx.Subscriber;
import rx.exceptions.Exceptions;
import rx.functions.Func1;
import rx.functions.Func2;

public final class OperatorSkipWhile<T> implements Operator<T, T> {
    final Func2<? super T, Integer, Boolean> predicate;

    /* renamed from: rx.internal.operators.OperatorSkipWhile.1 */
    class C14331 extends Subscriber<T> {
        int index;
        boolean skipping;
        final /* synthetic */ Subscriber val$child;

        C14331(Subscriber x0, Subscriber subscriber) {
            this.val$child = subscriber;
            super(x0);
            this.skipping = true;
        }

        public void onNext(T t) {
            if (this.skipping) {
                try {
                    Func2 func2 = OperatorSkipWhile.this.predicate;
                    int i = this.index;
                    this.index = i + 1;
                    if (((Boolean) func2.call(t, Integer.valueOf(i))).booleanValue()) {
                        request(1);
                        return;
                    }
                    this.skipping = false;
                    this.val$child.onNext(t);
                    return;
                } catch (Throwable e) {
                    Exceptions.throwOrReport(e, this.val$child, t);
                    return;
                }
            }
            this.val$child.onNext(t);
        }

        public void onError(Throwable e) {
            this.val$child.onError(e);
        }

        public void onCompleted() {
            this.val$child.onCompleted();
        }
    }

    /* renamed from: rx.internal.operators.OperatorSkipWhile.2 */
    static class C14342 implements Func2<T, Integer, Boolean> {
        final /* synthetic */ Func1 val$predicate;

        C14342(Func1 func1) {
            this.val$predicate = func1;
        }

        public Boolean call(T t1, Integer t2) {
            return (Boolean) this.val$predicate.call(t1);
        }
    }

    public OperatorSkipWhile(Func2<? super T, Integer, Boolean> predicate) {
        this.predicate = predicate;
    }

    public Subscriber<? super T> call(Subscriber<? super T> child) {
        return new C14331(child, child);
    }

    public static <T> Func2<T, Integer, Boolean> toPredicate2(Func1<? super T, Boolean> predicate) {
        return new C14342(predicate);
    }
}
