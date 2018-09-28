package rx.observables;

import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;
import rx.Observable;
import rx.Observer;
import rx.Producer;
import rx.Subscriber;
import rx.Subscription;
import rx.annotations.Experimental;
import rx.exceptions.OnErrorNotImplementedException;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Actions;
import rx.functions.Func1;
import rx.internal.operators.BlockingOperatorLatest;
import rx.internal.operators.BlockingOperatorMostRecent;
import rx.internal.operators.BlockingOperatorNext;
import rx.internal.operators.BlockingOperatorToFuture;
import rx.internal.operators.BlockingOperatorToIterator;
import rx.internal.operators.NotificationLite;
import rx.internal.util.BlockingUtils;
import rx.internal.util.UtilityFunctions;

public final class BlockingObservable<T> {
    static final Object ON_START;
    static final Object SET_PRODUCER;
    static final Object UNSUBSCRIBE;
    private final Observable<? extends T> f23o;

    /* renamed from: rx.observables.BlockingObservable.2 */
    class C08212 implements Iterable<T> {
        C08212() {
        }

        public Iterator<T> iterator() {
            return BlockingObservable.this.getIterator();
        }
    }

    /* renamed from: rx.observables.BlockingObservable.9 */
    class C12799 implements Observer<T> {
        final /* synthetic */ Action0 val$onCompleted;
        final /* synthetic */ Action1 val$onError;
        final /* synthetic */ Action1 val$onNext;

        C12799(Action1 action1, Action1 action12, Action0 action0) {
            this.val$onNext = action1;
            this.val$onError = action12;
            this.val$onCompleted = action0;
        }

        public void onNext(T t) {
            this.val$onNext.call(t);
        }

        public void onError(Throwable e) {
            this.val$onError.call(e);
        }

        public void onCompleted() {
            this.val$onCompleted.call();
        }
    }

    /* renamed from: rx.observables.BlockingObservable.1 */
    class C14771 extends Subscriber<T> {
        final /* synthetic */ AtomicReference val$exceptionFromOnError;
        final /* synthetic */ CountDownLatch val$latch;
        final /* synthetic */ Action1 val$onNext;

        C14771(CountDownLatch countDownLatch, AtomicReference atomicReference, Action1 action1) {
            this.val$latch = countDownLatch;
            this.val$exceptionFromOnError = atomicReference;
            this.val$onNext = action1;
        }

        public void onCompleted() {
            this.val$latch.countDown();
        }

        public void onError(Throwable e) {
            this.val$exceptionFromOnError.set(e);
            this.val$latch.countDown();
        }

        public void onNext(T args) {
            this.val$onNext.call(args);
        }
    }

    /* renamed from: rx.observables.BlockingObservable.3 */
    class C14783 extends Subscriber<T> {
        final /* synthetic */ CountDownLatch val$latch;
        final /* synthetic */ AtomicReference val$returnException;
        final /* synthetic */ AtomicReference val$returnItem;

        C14783(CountDownLatch countDownLatch, AtomicReference atomicReference, AtomicReference atomicReference2) {
            this.val$latch = countDownLatch;
            this.val$returnException = atomicReference;
            this.val$returnItem = atomicReference2;
        }

        public void onCompleted() {
            this.val$latch.countDown();
        }

        public void onError(Throwable e) {
            this.val$returnException.set(e);
            this.val$latch.countDown();
        }

        public void onNext(T item) {
            this.val$returnItem.set(item);
        }
    }

    /* renamed from: rx.observables.BlockingObservable.4 */
    class C14794 extends Subscriber<T> {
        final /* synthetic */ CountDownLatch val$cdl;
        final /* synthetic */ Throwable[] val$error;

        C14794(Throwable[] thArr, CountDownLatch countDownLatch) {
            this.val$error = thArr;
            this.val$cdl = countDownLatch;
        }

        public void onNext(T t) {
        }

        public void onError(Throwable e) {
            this.val$error[0] = e;
            this.val$cdl.countDown();
        }

        public void onCompleted() {
            this.val$cdl.countDown();
        }
    }

    /* renamed from: rx.observables.BlockingObservable.5 */
    class C14805 extends Subscriber<T> {
        final /* synthetic */ NotificationLite val$nl;
        final /* synthetic */ BlockingQueue val$queue;

        C14805(BlockingQueue blockingQueue, NotificationLite notificationLite) {
            this.val$queue = blockingQueue;
            this.val$nl = notificationLite;
        }

        public void onNext(T t) {
            this.val$queue.offer(this.val$nl.next(t));
        }

        public void onError(Throwable e) {
            this.val$queue.offer(this.val$nl.error(e));
        }

        public void onCompleted() {
            this.val$queue.offer(this.val$nl.completed());
        }
    }

    /* renamed from: rx.observables.BlockingObservable.6 */
    class C14816 extends Subscriber<T> {
        final /* synthetic */ NotificationLite val$nl;
        final /* synthetic */ BlockingQueue val$queue;
        final /* synthetic */ Producer[] val$theProducer;

        C14816(BlockingQueue blockingQueue, NotificationLite notificationLite, Producer[] producerArr) {
            this.val$queue = blockingQueue;
            this.val$nl = notificationLite;
            this.val$theProducer = producerArr;
        }

        public void onNext(T t) {
            this.val$queue.offer(this.val$nl.next(t));
        }

        public void onError(Throwable e) {
            this.val$queue.offer(this.val$nl.error(e));
        }

        public void onCompleted() {
            this.val$queue.offer(this.val$nl.completed());
        }

        public void setProducer(Producer p) {
            this.val$theProducer[0] = p;
            this.val$queue.offer(BlockingObservable.SET_PRODUCER);
        }

        public void onStart() {
            this.val$queue.offer(BlockingObservable.ON_START);
        }
    }

    /* renamed from: rx.observables.BlockingObservable.7 */
    class C15637 implements Action0 {
        final /* synthetic */ BlockingQueue val$queue;

        C15637(BlockingQueue blockingQueue) {
            this.val$queue = blockingQueue;
        }

        public void call() {
            this.val$queue.offer(BlockingObservable.UNSUBSCRIBE);
        }
    }

    /* renamed from: rx.observables.BlockingObservable.8 */
    class C15648 implements Action1<Throwable> {
        C15648() {
        }

        public void call(Throwable t) {
            throw new OnErrorNotImplementedException(t);
        }
    }

    private BlockingObservable(Observable<? extends T> o) {
        this.f23o = o;
    }

    public static <T> BlockingObservable<T> from(Observable<? extends T> o) {
        return new BlockingObservable(o);
    }

    public void forEach(Action1<? super T> onNext) {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Throwable> exceptionFromOnError = new AtomicReference();
        BlockingUtils.awaitForComplete(latch, this.f23o.subscribe(new C14771(latch, exceptionFromOnError, onNext)));
        if (exceptionFromOnError.get() == null) {
            return;
        }
        if (exceptionFromOnError.get() instanceof RuntimeException) {
            throw ((RuntimeException) exceptionFromOnError.get());
        }
        throw new RuntimeException((Throwable) exceptionFromOnError.get());
    }

    public Iterator<T> getIterator() {
        return BlockingOperatorToIterator.toIterator(this.f23o);
    }

    public T first() {
        return blockForSingle(this.f23o.first());
    }

    public T first(Func1<? super T, Boolean> predicate) {
        return blockForSingle(this.f23o.first(predicate));
    }

    public T firstOrDefault(T defaultValue) {
        return blockForSingle(this.f23o.map(UtilityFunctions.identity()).firstOrDefault(defaultValue));
    }

    public T firstOrDefault(T defaultValue, Func1<? super T, Boolean> predicate) {
        return blockForSingle(this.f23o.filter(predicate).map(UtilityFunctions.identity()).firstOrDefault(defaultValue));
    }

    public T last() {
        return blockForSingle(this.f23o.last());
    }

    public T last(Func1<? super T, Boolean> predicate) {
        return blockForSingle(this.f23o.last(predicate));
    }

    public T lastOrDefault(T defaultValue) {
        return blockForSingle(this.f23o.map(UtilityFunctions.identity()).lastOrDefault(defaultValue));
    }

    public T lastOrDefault(T defaultValue, Func1<? super T, Boolean> predicate) {
        return blockForSingle(this.f23o.filter(predicate).map(UtilityFunctions.identity()).lastOrDefault(defaultValue));
    }

    public Iterable<T> mostRecent(T initialValue) {
        return BlockingOperatorMostRecent.mostRecent(this.f23o, initialValue);
    }

    public Iterable<T> next() {
        return BlockingOperatorNext.next(this.f23o);
    }

    public Iterable<T> latest() {
        return BlockingOperatorLatest.latest(this.f23o);
    }

    public T single() {
        return blockForSingle(this.f23o.single());
    }

    public T single(Func1<? super T, Boolean> predicate) {
        return blockForSingle(this.f23o.single(predicate));
    }

    public T singleOrDefault(T defaultValue) {
        return blockForSingle(this.f23o.map(UtilityFunctions.identity()).singleOrDefault(defaultValue));
    }

    public T singleOrDefault(T defaultValue, Func1<? super T, Boolean> predicate) {
        return blockForSingle(this.f23o.filter(predicate).map(UtilityFunctions.identity()).singleOrDefault(defaultValue));
    }

    public Future<T> toFuture() {
        return BlockingOperatorToFuture.toFuture(this.f23o);
    }

    public Iterable<T> toIterable() {
        return new C08212();
    }

    private T blockForSingle(Observable<? extends T> observable) {
        AtomicReference<T> returnItem = new AtomicReference();
        AtomicReference<Throwable> returnException = new AtomicReference();
        CountDownLatch latch = new CountDownLatch(1);
        BlockingUtils.awaitForComplete(latch, observable.subscribe(new C14783(latch, returnException, returnItem)));
        if (returnException.get() == null) {
            return returnItem.get();
        }
        if (returnException.get() instanceof RuntimeException) {
            throw ((RuntimeException) returnException.get());
        }
        throw new RuntimeException((Throwable) returnException.get());
    }

    @Experimental
    public void subscribe() {
        CountDownLatch cdl = new CountDownLatch(1);
        Throwable[] error = new Throwable[]{null};
        BlockingUtils.awaitForComplete(cdl, this.f23o.subscribe(new C14794(error, cdl)));
        Throwable e = error[0];
        if (e == null) {
            return;
        }
        if (e instanceof RuntimeException) {
            throw ((RuntimeException) e);
        }
        throw new RuntimeException(e);
    }

    @Experimental
    public void subscribe(Observer<? super T> observer) {
        NotificationLite<T> nl = NotificationLite.instance();
        BlockingQueue<Object> queue = new LinkedBlockingQueue();
        Subscription s = this.f23o.subscribe(new C14805(queue, nl));
        while (true) {
            try {
                Object o = queue.poll();
                if (o == null) {
                    o = queue.take();
                }
                if (nl.accept(observer, o)) {
                    break;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                observer.onError(e);
            } finally {
                s.unsubscribe();
            }
        }
    }

    static {
        ON_START = new Object();
        SET_PRODUCER = new Object();
        UNSUBSCRIBE = new Object();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    @rx.annotations.Experimental
    public void subscribe(rx.Subscriber<? super T> r9) {
        /*
        r8 = this;
        r7 = 0;
        r1 = rx.internal.operators.NotificationLite.instance();
        r3 = new java.util.concurrent.LinkedBlockingQueue;
        r3.<init>();
        r6 = 1;
        r5 = new rx.Producer[r6];
        r6 = 0;
        r5[r7] = r6;
        r4 = new rx.observables.BlockingObservable$6;
        r4.<init>(r3, r1, r5);
        r9.add(r4);
        r6 = new rx.observables.BlockingObservable$7;
        r6.<init>(r3);
        r6 = rx.subscriptions.Subscriptions.create(r6);
        r9.add(r6);
        r6 = r8.f23o;
        r6.subscribe(r4);
    L_0x0029:
        r6 = r9.isUnsubscribed();	 Catch:{ InterruptedException -> 0x004f }
        if (r6 == 0) goto L_0x0033;
    L_0x002f:
        r4.unsubscribe();
    L_0x0032:
        return;
    L_0x0033:
        r2 = r3.poll();	 Catch:{ InterruptedException -> 0x004f }
        if (r2 != 0) goto L_0x003d;
    L_0x0039:
        r2 = r3.take();	 Catch:{ InterruptedException -> 0x004f }
    L_0x003d:
        r6 = r9.isUnsubscribed();	 Catch:{ InterruptedException -> 0x004f }
        if (r6 != 0) goto L_0x002f;
    L_0x0043:
        r6 = UNSUBSCRIBE;	 Catch:{ InterruptedException -> 0x004f }
        if (r2 == r6) goto L_0x002f;
    L_0x0047:
        r6 = ON_START;	 Catch:{ InterruptedException -> 0x004f }
        if (r2 != r6) goto L_0x005e;
    L_0x004b:
        r9.onStart();	 Catch:{ InterruptedException -> 0x004f }
        goto L_0x0029;
    L_0x004f:
        r0 = move-exception;
        r6 = java.lang.Thread.currentThread();	 Catch:{ all -> 0x0069 }
        r6.interrupt();	 Catch:{ all -> 0x0069 }
        r9.onError(r0);	 Catch:{ all -> 0x0069 }
        r4.unsubscribe();
        goto L_0x0032;
    L_0x005e:
        r6 = SET_PRODUCER;	 Catch:{ InterruptedException -> 0x004f }
        if (r2 != r6) goto L_0x006e;
    L_0x0062:
        r6 = 0;
        r6 = r5[r6];	 Catch:{ InterruptedException -> 0x004f }
        r9.setProducer(r6);	 Catch:{ InterruptedException -> 0x004f }
        goto L_0x0029;
    L_0x0069:
        r6 = move-exception;
        r4.unsubscribe();
        throw r6;
    L_0x006e:
        r6 = r1.accept(r9, r2);	 Catch:{ InterruptedException -> 0x004f }
        if (r6 == 0) goto L_0x0029;
    L_0x0074:
        r4.unsubscribe();
        goto L_0x0032;
        */
        throw new UnsupportedOperationException("Method not decompiled: rx.observables.BlockingObservable.subscribe(rx.Subscriber):void");
    }

    @Experimental
    public void subscribe(Action1<? super T> onNext) {
        subscribe(onNext, new C15648(), Actions.empty());
    }

    @Experimental
    public void subscribe(Action1<? super T> onNext, Action1<? super Throwable> onError) {
        subscribe(onNext, onError, Actions.empty());
    }

    @Experimental
    public void subscribe(Action1<? super T> onNext, Action1<? super Throwable> onError, Action0 onCompleted) {
        subscribe(new C12799(onNext, onError, onCompleted));
    }
}
