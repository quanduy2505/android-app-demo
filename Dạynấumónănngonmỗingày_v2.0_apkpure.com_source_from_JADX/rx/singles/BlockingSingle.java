package rx.singles;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;
import rx.Single;
import rx.SingleSubscriber;
import rx.annotations.Experimental;
import rx.internal.operators.BlockingOperatorToFuture;
import rx.internal.util.BlockingUtils;

@Experimental
public class BlockingSingle<T> {
    private final Single<? extends T> single;

    /* renamed from: rx.singles.BlockingSingle.1 */
    class C14911 extends SingleSubscriber<T> {
        final /* synthetic */ CountDownLatch val$latch;
        final /* synthetic */ AtomicReference val$returnException;
        final /* synthetic */ AtomicReference val$returnItem;

        C14911(AtomicReference atomicReference, CountDownLatch countDownLatch, AtomicReference atomicReference2) {
            this.val$returnItem = atomicReference;
            this.val$latch = countDownLatch;
            this.val$returnException = atomicReference2;
        }

        public void onSuccess(T value) {
            this.val$returnItem.set(value);
            this.val$latch.countDown();
        }

        public void onError(Throwable error) {
            this.val$returnException.set(error);
            this.val$latch.countDown();
        }
    }

    private BlockingSingle(Single<? extends T> single) {
        this.single = single;
    }

    @Experimental
    public static <T> BlockingSingle<T> from(Single<? extends T> single) {
        return new BlockingSingle(single);
    }

    @Experimental
    public T value() {
        AtomicReference<T> returnItem = new AtomicReference();
        AtomicReference<Throwable> returnException = new AtomicReference();
        CountDownLatch latch = new CountDownLatch(1);
        BlockingUtils.awaitForComplete(latch, this.single.subscribe(new C14911(returnItem, latch, returnException)));
        Throwable throwable = (Throwable) returnException.get();
        if (throwable == null) {
            return returnItem.get();
        }
        if (throwable instanceof RuntimeException) {
            throw ((RuntimeException) throwable);
        }
        throw new RuntimeException(throwable);
    }

    @Experimental
    public Future<T> toFuture() {
        return BlockingOperatorToFuture.toFuture(this.single.toObservable());
    }
}
