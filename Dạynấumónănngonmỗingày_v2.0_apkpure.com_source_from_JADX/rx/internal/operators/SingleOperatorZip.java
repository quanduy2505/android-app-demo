package rx.internal.operators;

import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import rx.Single;
import rx.Single.OnSubscribe;
import rx.SingleSubscriber;
import rx.exceptions.Exceptions;
import rx.functions.FuncN;
import rx.plugins.RxJavaPlugins;
import rx.subscriptions.CompositeSubscription;

public class SingleOperatorZip {

    /* renamed from: rx.internal.operators.SingleOperatorZip.1 */
    static class C16141 implements OnSubscribe<R> {
        final /* synthetic */ Single[] val$singles;
        final /* synthetic */ FuncN val$zipper;

        /* renamed from: rx.internal.operators.SingleOperatorZip.1.1 */
        class C14621 extends SingleSubscriber<T> {
            final /* synthetic */ int val$j;
            final /* synthetic */ AtomicBoolean val$once;
            final /* synthetic */ SingleSubscriber val$subscriber;
            final /* synthetic */ Object[] val$values;
            final /* synthetic */ AtomicInteger val$wip;

            C14621(Object[] objArr, int i, AtomicInteger atomicInteger, SingleSubscriber singleSubscriber, AtomicBoolean atomicBoolean) {
                this.val$values = objArr;
                this.val$j = i;
                this.val$wip = atomicInteger;
                this.val$subscriber = singleSubscriber;
                this.val$once = atomicBoolean;
            }

            public void onSuccess(T value) {
                this.val$values[this.val$j] = value;
                if (this.val$wip.decrementAndGet() == 0) {
                    try {
                        this.val$subscriber.onSuccess(C16141.this.val$zipper.call(this.val$values));
                    } catch (Throwable e) {
                        Exceptions.throwIfFatal(e);
                        onError(e);
                    }
                }
            }

            public void onError(Throwable error) {
                if (this.val$once.compareAndSet(false, true)) {
                    this.val$subscriber.onError(error);
                } else {
                    RxJavaPlugins.getInstance().getErrorHandler().handleError(error);
                }
            }
        }

        C16141(Single[] singleArr, FuncN funcN) {
            this.val$singles = singleArr;
            this.val$zipper = funcN;
        }

        public void call(SingleSubscriber<? super R> subscriber) {
            if (this.val$singles.length == 0) {
                subscriber.onError(new NoSuchElementException("Can't zip 0 Singles."));
                return;
            }
            AtomicInteger wip = new AtomicInteger(this.val$singles.length);
            AtomicBoolean once = new AtomicBoolean();
            Object[] values = new Object[this.val$singles.length];
            CompositeSubscription compositeSubscription = new CompositeSubscription();
            subscriber.add(compositeSubscription);
            int i = 0;
            while (i < this.val$singles.length && !compositeSubscription.isUnsubscribed() && !once.get()) {
                SingleSubscriber singleSubscriber = new C14621(values, i, wip, subscriber, once);
                compositeSubscription.add(singleSubscriber);
                if (!compositeSubscription.isUnsubscribed() && !once.get()) {
                    this.val$singles[i].subscribe(singleSubscriber);
                    i++;
                } else {
                    return;
                }
            }
        }
    }

    public static <T, R> Single<R> zip(Single<? extends T>[] singles, FuncN<? extends R> zipper) {
        return Single.create(new C16141(singles, zipper));
    }
}
