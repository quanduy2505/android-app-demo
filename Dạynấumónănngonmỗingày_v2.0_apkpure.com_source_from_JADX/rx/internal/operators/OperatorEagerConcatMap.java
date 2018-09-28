package rx.internal.operators;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import rx.Observable;
import rx.Observable.Operator;
import rx.Producer;
import rx.Subscriber;
import rx.Subscription;
import rx.exceptions.Exceptions;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.internal.util.atomic.SpscAtomicArrayQueue;
import rx.internal.util.unsafe.SpscArrayQueue;
import rx.internal.util.unsafe.UnsafeAccess;
import rx.subscriptions.Subscriptions;

public final class OperatorEagerConcatMap<T, R> implements Operator<R, T> {
    final int bufferSize;
    final Func1<? super T, ? extends Observable<? extends R>> mapper;
    private final int maxConcurrent;

    static final class EagerOuterProducer extends AtomicLong implements Producer {
        private static final long serialVersionUID = -657299606803478389L;
        final EagerOuterSubscriber<?, ?> parent;

        public EagerOuterProducer(EagerOuterSubscriber<?, ?> parent) {
            this.parent = parent;
        }

        public void request(long n) {
            if (n < 0) {
                throw new IllegalStateException("n >= 0 required but it was " + n);
            } else if (n > 0) {
                BackpressureUtils.getAndAddRequest(this, n);
                this.parent.drain();
            }
        }
    }

    static final class EagerInnerSubscriber<T> extends Subscriber<T> {
        volatile boolean done;
        Throwable error;
        final NotificationLite<T> nl;
        final EagerOuterSubscriber<?, T> parent;
        final Queue<Object> queue;

        public EagerInnerSubscriber(EagerOuterSubscriber<?, T> parent, int bufferSize) {
            Queue<Object> q;
            this.parent = parent;
            if (UnsafeAccess.isUnsafeAvailable()) {
                q = new SpscArrayQueue(bufferSize);
            } else {
                q = new SpscAtomicArrayQueue(bufferSize);
            }
            this.queue = q;
            this.nl = NotificationLite.instance();
            request((long) bufferSize);
        }

        public void onNext(T t) {
            this.queue.offer(this.nl.next(t));
            this.parent.drain();
        }

        public void onError(Throwable e) {
            this.error = e;
            this.done = true;
            this.parent.drain();
        }

        public void onCompleted() {
            this.done = true;
            this.parent.drain();
        }

        void requestMore(long n) {
            request(n);
        }
    }

    static final class EagerOuterSubscriber<T, R> extends Subscriber<T> {
        final Subscriber<? super R> actual;
        final int bufferSize;
        volatile boolean cancelled;
        volatile boolean done;
        Throwable error;
        final Func1<? super T, ? extends Observable<? extends R>> mapper;
        private EagerOuterProducer sharedProducer;
        final LinkedList<EagerInnerSubscriber<R>> subscribers;
        final AtomicInteger wip;

        /* renamed from: rx.internal.operators.OperatorEagerConcatMap.EagerOuterSubscriber.1 */
        class C15301 implements Action0 {
            C15301() {
            }

            public void call() {
                EagerOuterSubscriber.this.cancelled = true;
                if (EagerOuterSubscriber.this.wip.getAndIncrement() == 0) {
                    EagerOuterSubscriber.this.cleanup();
                }
            }
        }

        public EagerOuterSubscriber(Func1<? super T, ? extends Observable<? extends R>> mapper, int bufferSize, int maxConcurrent, Subscriber<? super R> actual) {
            this.mapper = mapper;
            this.bufferSize = bufferSize;
            this.actual = actual;
            this.subscribers = new LinkedList();
            this.wip = new AtomicInteger();
            request(maxConcurrent == UrlImageViewHelper.CACHE_DURATION_INFINITE ? Long.MAX_VALUE : (long) maxConcurrent);
        }

        void init() {
            this.sharedProducer = new EagerOuterProducer(this);
            add(Subscriptions.create(new C15301()));
            this.actual.add(this);
            this.actual.setProducer(this.sharedProducer);
        }

        void cleanup() {
            synchronized (this.subscribers) {
                List<Subscription> list = new ArrayList(this.subscribers);
                this.subscribers.clear();
            }
            for (Subscription s : list) {
                s.unsubscribe();
            }
        }

        public void onNext(T t) {
            try {
                Observable<? extends R> observable = (Observable) this.mapper.call(t);
                EagerInnerSubscriber<R> inner = new EagerInnerSubscriber(this, this.bufferSize);
                if (!this.cancelled) {
                    synchronized (this.subscribers) {
                        if (this.cancelled) {
                            return;
                        }
                        this.subscribers.add(inner);
                        if (!this.cancelled) {
                            observable.unsafeSubscribe(inner);
                            drain();
                        }
                    }
                }
            } catch (Throwable e) {
                Exceptions.throwOrReport(e, this.actual, t);
            }
        }

        public void onError(Throwable e) {
            this.error = e;
            this.done = true;
            drain();
        }

        public void onCompleted() {
            this.done = true;
            drain();
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        void drain() {
            /*
            r24 = this;
            r0 = r24;
            r0 = r0.wip;
            r22 = r0;
            r22 = r22.getAndIncrement();
            if (r22 == 0) goto L_0x000d;
        L_0x000c:
            return;
        L_0x000d:
            r14 = 1;
            r0 = r24;
            r0 = r0.sharedProducer;
            r17 = r0;
            r0 = r24;
            r4 = r0.actual;
            r15 = rx.internal.operators.NotificationLite.instance();
        L_0x001c:
            r0 = r24;
            r0 = r0.cancelled;
            r22 = r0;
            if (r22 == 0) goto L_0x0028;
        L_0x0024:
            r24.cleanup();
            goto L_0x000c;
        L_0x0028:
            r0 = r24;
            r0 = r0.done;
            r16 = r0;
            r0 = r24;
            r0 = r0.subscribers;
            r23 = r0;
            monitor-enter(r23);
            r0 = r24;
            r0 = r0.subscribers;	 Catch:{ all -> 0x0054 }
            r22 = r0;
            r13 = r22.peek();	 Catch:{ all -> 0x0054 }
            r13 = (rx.internal.operators.OperatorEagerConcatMap.EagerInnerSubscriber) r13;	 Catch:{ all -> 0x0054 }
            monitor-exit(r23);	 Catch:{ all -> 0x0054 }
            if (r13 != 0) goto L_0x0057;
        L_0x0044:
            r5 = 1;
        L_0x0045:
            if (r16 == 0) goto L_0x005f;
        L_0x0047:
            r0 = r24;
            r8 = r0.error;
            if (r8 == 0) goto L_0x0059;
        L_0x004d:
            r24.cleanup();
            r4.onError(r8);
            goto L_0x000c;
        L_0x0054:
            r22 = move-exception;
            monitor-exit(r23);	 Catch:{ all -> 0x0054 }
            throw r22;
        L_0x0057:
            r5 = 0;
            goto L_0x0045;
        L_0x0059:
            if (r5 == 0) goto L_0x005f;
        L_0x005b:
            r4.onCompleted();
            goto L_0x000c;
        L_0x005f:
            if (r5 != 0) goto L_0x00cb;
        L_0x0061:
            r18 = r17.get();
            r6 = 0;
            r22 = 9223372036854775807; // 0x7fffffffffffffff float:NaN double:NaN;
            r22 = (r18 > r22 ? 1 : (r18 == r22 ? 0 : -1));
            if (r22 != 0) goto L_0x008d;
        L_0x0070:
            r20 = 1;
        L_0x0072:
            r12 = r13.queue;
            r10 = 0;
        L_0x0075:
            r0 = r13.done;
            r16 = r0;
            r21 = r12.peek();
            if (r21 != 0) goto L_0x0090;
        L_0x007f:
            r5 = 1;
        L_0x0080:
            if (r16 == 0) goto L_0x00df;
        L_0x0082:
            r11 = r13.error;
            if (r11 == 0) goto L_0x0092;
        L_0x0086:
            r24.cleanup();
            r4.onError(r11);
            goto L_0x000c;
        L_0x008d:
            r20 = 0;
            goto L_0x0072;
        L_0x0090:
            r5 = 0;
            goto L_0x0080;
        L_0x0092:
            if (r5 == 0) goto L_0x00df;
        L_0x0094:
            r0 = r24;
            r0 = r0.subscribers;
            r23 = r0;
            monitor-enter(r23);
            r0 = r24;
            r0 = r0.subscribers;	 Catch:{ all -> 0x00dc }
            r22 = r0;
            r22.poll();	 Catch:{ all -> 0x00dc }
            monitor-exit(r23);	 Catch:{ all -> 0x00dc }
            r13.unsubscribe();
            r10 = 1;
            r22 = 1;
            r0 = r24;
            r1 = r22;
            r0.request(r1);
        L_0x00b2:
            r22 = 0;
            r22 = (r6 > r22 ? 1 : (r6 == r22 ? 0 : -1));
            if (r22 == 0) goto L_0x00c9;
        L_0x00b8:
            if (r20 != 0) goto L_0x00bf;
        L_0x00ba:
            r0 = r17;
            r0.addAndGet(r6);
        L_0x00bf:
            if (r10 != 0) goto L_0x00c9;
        L_0x00c1:
            r0 = -r6;
            r22 = r0;
            r0 = r22;
            r13.requestMore(r0);
        L_0x00c9:
            if (r10 != 0) goto L_0x001c;
        L_0x00cb:
            r0 = r24;
            r0 = r0.wip;
            r22 = r0;
            r0 = -r14;
            r23 = r0;
            r14 = r22.addAndGet(r23);
            if (r14 != 0) goto L_0x001c;
        L_0x00da:
            goto L_0x000c;
        L_0x00dc:
            r22 = move-exception;
            monitor-exit(r23);	 Catch:{ all -> 0x00dc }
            throw r22;
        L_0x00df:
            if (r5 != 0) goto L_0x00b2;
        L_0x00e1:
            r22 = 0;
            r22 = (r18 > r22 ? 1 : (r18 == r22 ? 0 : -1));
            if (r22 == 0) goto L_0x00b2;
        L_0x00e7:
            r12.poll();
            r0 = r21;
            r22 = r15.getValue(r0);	 Catch:{ Throwable -> 0x00ff }
            r0 = r22;
            r4.onNext(r0);	 Catch:{ Throwable -> 0x00ff }
            r22 = 1;
            r18 = r18 - r22;
            r22 = 1;
            r6 = r6 - r22;
            goto L_0x0075;
        L_0x00ff:
            r9 = move-exception;
            r0 = r21;
            rx.exceptions.Exceptions.throwOrReport(r9, r4, r0);
            goto L_0x000c;
            */
            throw new UnsupportedOperationException("Method not decompiled: rx.internal.operators.OperatorEagerConcatMap.EagerOuterSubscriber.drain():void");
        }
    }

    public OperatorEagerConcatMap(Func1<? super T, ? extends Observable<? extends R>> mapper, int bufferSize, int maxConcurrent) {
        this.mapper = mapper;
        this.bufferSize = bufferSize;
        this.maxConcurrent = maxConcurrent;
    }

    public Subscriber<? super T> call(Subscriber<? super R> t) {
        EagerOuterSubscriber<T, R> outer = new EagerOuterSubscriber(this.mapper, this.bufferSize, this.maxConcurrent, t);
        outer.init();
        return outer;
    }
}
