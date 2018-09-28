package rx.internal.operators;

import java.util.ArrayDeque;
import java.util.concurrent.atomic.AtomicLong;
import rx.Observable.Operator;
import rx.Producer;
import rx.Subscriber;
import rx.functions.Func1;

public final class OperatorTakeLast<T> implements Operator<T, T> {
    final int count;

    /* renamed from: rx.internal.operators.OperatorTakeLast.1 */
    class C12721 implements Producer {
        final /* synthetic */ TakeLastSubscriber val$parent;

        C12721(TakeLastSubscriber takeLastSubscriber) {
            this.val$parent = takeLastSubscriber;
        }

        public void request(long n) {
            this.val$parent.requestMore(n);
        }
    }

    static final class TakeLastSubscriber<T> extends Subscriber<T> implements Func1<Object, T> {
        final Subscriber<? super T> actual;
        final int count;
        final NotificationLite<T> nl;
        final ArrayDeque<Object> queue;
        final AtomicLong requested;

        public TakeLastSubscriber(Subscriber<? super T> actual, int count) {
            this.actual = actual;
            this.count = count;
            this.requested = new AtomicLong();
            this.queue = new ArrayDeque();
            this.nl = NotificationLite.instance();
        }

        public void onNext(T t) {
            if (this.queue.size() == this.count) {
                this.queue.poll();
            }
            this.queue.offer(this.nl.next(t));
        }

        public void onError(Throwable e) {
            this.queue.clear();
            this.actual.onError(e);
        }

        public void onCompleted() {
            BackpressureUtils.postCompleteDone(this.requested, this.queue, this.actual, this);
        }

        public T call(Object t) {
            return this.nl.getValue(t);
        }

        void requestMore(long n) {
            if (n > 0) {
                BackpressureUtils.postCompleteRequest(this.requested, n, this.queue, this.actual, this);
            }
        }
    }

    public OperatorTakeLast(int count) {
        if (count < 0) {
            throw new IndexOutOfBoundsException("count cannot be negative");
        }
        this.count = count;
    }

    public Subscriber<? super T> call(Subscriber<? super T> subscriber) {
        TakeLastSubscriber<T> parent = new TakeLastSubscriber(subscriber, this.count);
        subscriber.add(parent);
        subscriber.setProducer(new C12721(parent));
        return parent;
    }
}
