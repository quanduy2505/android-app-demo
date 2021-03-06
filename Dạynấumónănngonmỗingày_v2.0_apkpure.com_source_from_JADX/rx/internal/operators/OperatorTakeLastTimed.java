package rx.internal.operators;

import java.util.ArrayDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import rx.Observable.Operator;
import rx.Producer;
import rx.Scheduler;
import rx.Subscriber;
import rx.functions.Func1;

public final class OperatorTakeLastTimed<T> implements Operator<T, T> {
    final long ageMillis;
    final int count;
    final Scheduler scheduler;

    /* renamed from: rx.internal.operators.OperatorTakeLastTimed.1 */
    class C12741 implements Producer {
        final /* synthetic */ TakeLastTimedSubscriber val$parent;

        C12741(TakeLastTimedSubscriber takeLastTimedSubscriber) {
            this.val$parent = takeLastTimedSubscriber;
        }

        public void request(long n) {
            this.val$parent.requestMore(n);
        }
    }

    static final class TakeLastTimedSubscriber<T> extends Subscriber<T> implements Func1<Object, T> {
        final Subscriber<? super T> actual;
        final long ageMillis;
        final int count;
        final NotificationLite<T> nl;
        final ArrayDeque<Object> queue;
        final ArrayDeque<Long> queueTimes;
        final AtomicLong requested;
        final Scheduler scheduler;

        public TakeLastTimedSubscriber(Subscriber<? super T> actual, int count, long ageMillis, Scheduler scheduler) {
            this.actual = actual;
            this.count = count;
            this.ageMillis = ageMillis;
            this.scheduler = scheduler;
            this.requested = new AtomicLong();
            this.queue = new ArrayDeque();
            this.queueTimes = new ArrayDeque();
            this.nl = NotificationLite.instance();
        }

        public void onNext(T t) {
            if (this.count != 0) {
                long now = this.scheduler.now();
                if (this.queue.size() == this.count) {
                    this.queue.poll();
                    this.queueTimes.poll();
                }
                evictOld(now);
                this.queue.offer(this.nl.next(t));
                this.queueTimes.offer(Long.valueOf(now));
            }
        }

        protected void evictOld(long now) {
            long minTime = now - this.ageMillis;
            while (true) {
                Long time = (Long) this.queueTimes.peek();
                if (time != null && time.longValue() < minTime) {
                    this.queue.poll();
                    this.queueTimes.poll();
                } else {
                    return;
                }
            }
        }

        public void onError(Throwable e) {
            this.queue.clear();
            this.queueTimes.clear();
            this.actual.onError(e);
        }

        public void onCompleted() {
            evictOld(this.scheduler.now());
            this.queueTimes.clear();
            BackpressureUtils.postCompleteDone(this.requested, this.queue, this.actual, this);
        }

        public T call(Object t) {
            return this.nl.getValue(t);
        }

        void requestMore(long n) {
            BackpressureUtils.postCompleteRequest(this.requested, n, this.queue, this.actual, this);
        }
    }

    public OperatorTakeLastTimed(long time, TimeUnit unit, Scheduler scheduler) {
        this.ageMillis = unit.toMillis(time);
        this.scheduler = scheduler;
        this.count = -1;
    }

    public OperatorTakeLastTimed(int count, long time, TimeUnit unit, Scheduler scheduler) {
        if (count < 0) {
            throw new IndexOutOfBoundsException("count could not be negative");
        }
        this.ageMillis = unit.toMillis(time);
        this.scheduler = scheduler;
        this.count = count;
    }

    public Subscriber<? super T> call(Subscriber<? super T> subscriber) {
        TakeLastTimedSubscriber<T> parent = new TakeLastTimedSubscriber(subscriber, this.count, this.ageMillis, this.scheduler);
        subscriber.add(parent);
        subscriber.setProducer(new C12741(parent));
        return parent;
    }
}
