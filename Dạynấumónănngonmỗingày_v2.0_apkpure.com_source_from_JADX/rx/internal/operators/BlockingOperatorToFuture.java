package rx.internal.operators;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

public final class BlockingOperatorToFuture {

    /* renamed from: rx.internal.operators.BlockingOperatorToFuture.2 */
    static class C08102 implements Future<T> {
        private volatile boolean cancelled;
        final /* synthetic */ AtomicReference val$error;
        final /* synthetic */ CountDownLatch val$finished;
        final /* synthetic */ Subscription val$s;
        final /* synthetic */ AtomicReference val$value;

        C08102(CountDownLatch countDownLatch, Subscription subscription, AtomicReference atomicReference, AtomicReference atomicReference2) {
            this.val$finished = countDownLatch;
            this.val$s = subscription;
            this.val$error = atomicReference;
            this.val$value = atomicReference2;
            this.cancelled = false;
        }

        public boolean cancel(boolean mayInterruptIfRunning) {
            if (this.val$finished.getCount() <= 0) {
                return false;
            }
            this.cancelled = true;
            this.val$s.unsubscribe();
            this.val$finished.countDown();
            return true;
        }

        public boolean isCancelled() {
            return this.cancelled;
        }

        public boolean isDone() {
            return this.val$finished.getCount() == 0;
        }

        public T get() throws InterruptedException, ExecutionException {
            this.val$finished.await();
            return getValue();
        }

        public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            if (this.val$finished.await(timeout, unit)) {
                return getValue();
            }
            throw new TimeoutException("Timed out after " + unit.toMillis(timeout) + "ms waiting for underlying Observable.");
        }

        private T getValue() throws ExecutionException {
            Throwable throwable = (Throwable) this.val$error.get();
            if (throwable != null) {
                throw new ExecutionException("Observable onError", throwable);
            } else if (!this.cancelled) {
                return this.val$value.get();
            } else {
                throw new CancellationException("Subscription unsubscribed");
            }
        }
    }

    /* renamed from: rx.internal.operators.BlockingOperatorToFuture.1 */
    static class C13721 extends Subscriber<T> {
        final /* synthetic */ AtomicReference val$error;
        final /* synthetic */ CountDownLatch val$finished;
        final /* synthetic */ AtomicReference val$value;

        C13721(CountDownLatch countDownLatch, AtomicReference atomicReference, AtomicReference atomicReference2) {
            this.val$finished = countDownLatch;
            this.val$error = atomicReference;
            this.val$value = atomicReference2;
        }

        public void onCompleted() {
            this.val$finished.countDown();
        }

        public void onError(Throwable e) {
            this.val$error.compareAndSet(null, e);
            this.val$finished.countDown();
        }

        public void onNext(T v) {
            this.val$value.set(v);
        }
    }

    private BlockingOperatorToFuture() {
        throw new IllegalStateException("No instances!");
    }

    public static <T> Future<T> toFuture(Observable<? extends T> that) {
        CountDownLatch finished = new CountDownLatch(1);
        AtomicReference<T> value = new AtomicReference();
        AtomicReference<Throwable> error = new AtomicReference();
        return new C08102(finished, that.single().subscribe(new C13721(finished, error, value)), error, value);
    }
}
