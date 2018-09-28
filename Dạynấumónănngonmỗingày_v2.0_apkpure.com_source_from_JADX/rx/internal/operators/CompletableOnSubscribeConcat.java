package rx.internal.operators;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import rx.Completable;
import rx.Completable.CompletableOnSubscribe;
import rx.Completable.CompletableSubscriber;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.exceptions.MissingBackpressureException;
import rx.internal.util.unsafe.SpscArrayQueue;
import rx.plugins.RxJavaPlugins;
import rx.subscriptions.SerialSubscription;

public final class CompletableOnSubscribeConcat implements CompletableOnSubscribe {
    final int prefetch;
    final Observable<Completable> sources;

    static final class CompletableConcatSubscriber extends Subscriber<Completable> {
        static final AtomicIntegerFieldUpdater<CompletableConcatSubscriber> ONCE;
        final CompletableSubscriber actual;
        volatile boolean done;
        final ConcatInnerSubscriber inner;
        volatile int once;
        final int prefetch;
        final SpscArrayQueue<Completable> queue;
        final SerialSubscription sr;
        final AtomicInteger wip;

        final class ConcatInnerSubscriber implements CompletableSubscriber {
            ConcatInnerSubscriber() {
            }

            public void onSubscribe(Subscription d) {
                CompletableConcatSubscriber.this.sr.set(d);
            }

            public void onError(Throwable e) {
                CompletableConcatSubscriber.this.innerError(e);
            }

            public void onCompleted() {
                CompletableConcatSubscriber.this.innerComplete();
            }
        }

        static {
            ONCE = AtomicIntegerFieldUpdater.newUpdater(CompletableConcatSubscriber.class, "once");
        }

        public CompletableConcatSubscriber(CompletableSubscriber actual, int prefetch) {
            this.actual = actual;
            this.prefetch = prefetch;
            this.queue = new SpscArrayQueue(prefetch);
            this.sr = new SerialSubscription();
            this.inner = new ConcatInnerSubscriber();
            this.wip = new AtomicInteger();
            add(this.sr);
            request((long) prefetch);
        }

        public void onNext(Completable t) {
            if (!this.queue.offer(t)) {
                onError(new MissingBackpressureException());
            } else if (this.wip.getAndIncrement() == 0) {
                next();
            }
        }

        public void onError(Throwable t) {
            if (ONCE.compareAndSet(this, 0, 1)) {
                this.actual.onError(t);
            } else {
                RxJavaPlugins.getInstance().getErrorHandler().handleError(t);
            }
        }

        public void onCompleted() {
            if (!this.done) {
                this.done = true;
                if (this.wip.getAndIncrement() == 0) {
                    next();
                }
            }
        }

        void innerError(Throwable e) {
            unsubscribe();
            onError(e);
        }

        void innerComplete() {
            if (this.wip.decrementAndGet() != 0) {
                next();
            }
            if (!this.done) {
                request(1);
            }
        }

        void next() {
            boolean d = this.done;
            Completable c = (Completable) this.queue.poll();
            if (c != null) {
                c.subscribe(this.inner);
            } else if (!d) {
                RxJavaPlugins.getInstance().getErrorHandler().handleError(new IllegalStateException("Queue is empty?!"));
            } else if (ONCE.compareAndSet(this, 0, 1)) {
                this.actual.onCompleted();
            }
        }
    }

    public CompletableOnSubscribeConcat(Observable<? extends Completable> sources, int prefetch) {
        this.sources = sources;
        this.prefetch = prefetch;
    }

    public void call(CompletableSubscriber s) {
        Subscriber parent = new CompletableConcatSubscriber(s, this.prefetch);
        s.onSubscribe(parent);
        this.sources.subscribe(parent);
    }
}
