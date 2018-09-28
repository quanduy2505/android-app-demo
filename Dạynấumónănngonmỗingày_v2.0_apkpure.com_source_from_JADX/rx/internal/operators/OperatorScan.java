package rx.internal.operators;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicLong;
import rx.Observable.Operator;
import rx.Observer;
import rx.Producer;
import rx.Subscriber;
import rx.exceptions.Exceptions;
import rx.functions.Func0;
import rx.functions.Func2;
import rx.internal.util.atomic.SpscLinkedAtomicQueue;
import rx.internal.util.unsafe.SpscLinkedQueue;
import rx.internal.util.unsafe.UnsafeAccess;

public final class OperatorScan<R, T> implements Operator<R, T> {
    private static final Object NO_INITIAL_VALUE;
    final Func2<R, ? super T, R> accumulator;
    private final Func0<R> initialValueFactory;

    static final class InitialProducer<R> implements Producer, Observer<R> {
        final Subscriber<? super R> child;
        volatile boolean done;
        boolean emitting;
        Throwable error;
        boolean missed;
        long missedRequested;
        volatile Producer producer;
        final Queue<Object> queue;
        final AtomicLong requested;

        public InitialProducer(R initialValue, Subscriber<? super R> child) {
            Queue<Object> q;
            this.child = child;
            if (UnsafeAccess.isUnsafeAvailable()) {
                q = new SpscLinkedQueue();
            } else {
                q = new SpscLinkedAtomicQueue();
            }
            this.queue = q;
            q.offer(NotificationLite.instance().next(initialValue));
            this.requested = new AtomicLong();
        }

        public void onNext(R t) {
            this.queue.offer(NotificationLite.instance().next(t));
            emit();
        }

        boolean checkTerminated(boolean d, boolean empty, Subscriber<? super R> child) {
            if (child.isUnsubscribed()) {
                return true;
            }
            if (d) {
                Throwable err = this.error;
                if (err != null) {
                    child.onError(err);
                    return true;
                } else if (empty) {
                    child.onCompleted();
                    return true;
                }
            }
            return false;
        }

        public void onError(Throwable e) {
            this.error = e;
            this.done = true;
            emit();
        }

        public void onCompleted() {
            this.done = true;
            emit();
        }

        public void request(long n) {
            if (n < 0) {
                throw new IllegalArgumentException("n >= required but it was " + n);
            } else if (n != 0) {
                BackpressureUtils.getAndAddRequest(this.requested, n);
                Producer p = this.producer;
                if (p == null) {
                    synchronized (this.requested) {
                        p = this.producer;
                        if (p == null) {
                            this.missedRequested = BackpressureUtils.addCap(this.missedRequested, n);
                        }
                    }
                }
                if (p != null) {
                    p.request(n);
                }
                emit();
            }
        }

        public void setProducer(Producer p) {
            if (p == null) {
                throw new NullPointerException();
            }
            synchronized (this.requested) {
                if (this.producer != null) {
                    throw new IllegalStateException("Can't set more than one Producer!");
                }
                long mr = this.missedRequested;
                if (mr != Long.MAX_VALUE) {
                    mr--;
                }
                this.missedRequested = 0;
                this.producer = p;
            }
            if (mr > 0) {
                p.request(mr);
            }
            emit();
        }

        void emit() {
            synchronized (this) {
                if (this.emitting) {
                    this.missed = true;
                    return;
                }
                this.emitting = true;
                emitLoop();
            }
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        void emitLoop() {
            /*
            r18 = this;
            r0 = r18;
            r2 = r0.child;
            r0 = r18;
            r11 = r0.queue;
            r9 = rx.internal.operators.NotificationLite.instance();
            r0 = r18;
            r14 = r0.requested;
            r12 = r14.get();
        L_0x0014:
            r16 = 9223372036854775807; // 0x7fffffffffffffff float:NaN double:NaN;
            r16 = (r12 > r16 ? 1 : (r12 == r16 ? 0 : -1));
            if (r16 != 0) goto L_0x002f;
        L_0x001d:
            r8 = 1;
        L_0x001e:
            r0 = r18;
            r3 = r0.done;
            r6 = r11.isEmpty();
            r0 = r18;
            r16 = r0.checkTerminated(r3, r6, r2);
            if (r16 == 0) goto L_0x0031;
        L_0x002e:
            return;
        L_0x002f:
            r8 = 0;
            goto L_0x001e;
        L_0x0031:
            r4 = 0;
        L_0x0033:
            r16 = 0;
            r16 = (r12 > r16 ? 1 : (r12 == r16 ? 0 : -1));
            if (r16 == 0) goto L_0x004e;
        L_0x0039:
            r0 = r18;
            r3 = r0.done;
            r10 = r11.poll();
            if (r10 != 0) goto L_0x0070;
        L_0x0043:
            r6 = 1;
        L_0x0044:
            r0 = r18;
            r16 = r0.checkTerminated(r3, r6, r2);
            if (r16 != 0) goto L_0x002e;
        L_0x004c:
            if (r6 == 0) goto L_0x0072;
        L_0x004e:
            r16 = 0;
            r16 = (r4 > r16 ? 1 : (r4 == r16 ? 0 : -1));
            if (r16 == 0) goto L_0x005a;
        L_0x0054:
            if (r8 != 0) goto L_0x005a;
        L_0x0056:
            r12 = r14.addAndGet(r4);
        L_0x005a:
            monitor-enter(r18);
            r0 = r18;
            r0 = r0.missed;	 Catch:{ all -> 0x006d }
            r16 = r0;
            if (r16 != 0) goto L_0x0087;
        L_0x0063:
            r16 = 0;
            r0 = r16;
            r1 = r18;
            r1.emitting = r0;	 Catch:{ all -> 0x006d }
            monitor-exit(r18);	 Catch:{ all -> 0x006d }
            goto L_0x002e;
        L_0x006d:
            r16 = move-exception;
            monitor-exit(r18);	 Catch:{ all -> 0x006d }
            throw r16;
        L_0x0070:
            r6 = 0;
            goto L_0x0044;
        L_0x0072:
            r15 = r9.getValue(r10);
            r2.onNext(r15);	 Catch:{ Throwable -> 0x0082 }
            r16 = 1;
            r12 = r12 - r16;
            r16 = 1;
            r4 = r4 - r16;
            goto L_0x0033;
        L_0x0082:
            r7 = move-exception;
            rx.exceptions.Exceptions.throwOrReport(r7, r2, r15);
            goto L_0x002e;
        L_0x0087:
            r16 = 0;
            r0 = r16;
            r1 = r18;
            r1.missed = r0;	 Catch:{ all -> 0x006d }
            monitor-exit(r18);	 Catch:{ all -> 0x006d }
            goto L_0x0014;
            */
            throw new UnsupportedOperationException("Method not decompiled: rx.internal.operators.OperatorScan.InitialProducer.emitLoop():void");
        }
    }

    /* renamed from: rx.internal.operators.OperatorScan.1 */
    class C14211 implements Func0<R> {
        final /* synthetic */ Object val$initialValue;

        C14211(Object obj) {
            this.val$initialValue = obj;
        }

        public R call() {
            return this.val$initialValue;
        }
    }

    /* renamed from: rx.internal.operators.OperatorScan.2 */
    class C14222 extends Subscriber<T> {
        boolean once;
        final /* synthetic */ Subscriber val$child;
        R value;

        C14222(Subscriber x0, Subscriber subscriber) {
            this.val$child = subscriber;
            super(x0);
        }

        public void onNext(T t) {
            R v;
            if (this.once) {
                try {
                    v = OperatorScan.this.accumulator.call(this.value, t);
                } catch (Throwable e) {
                    Exceptions.throwOrReport(e, this.val$child, t);
                    return;
                }
            }
            this.once = true;
            v = t;
            this.value = v;
            this.val$child.onNext(v);
        }

        public void onError(Throwable e) {
            this.val$child.onError(e);
        }

        public void onCompleted() {
            this.val$child.onCompleted();
        }
    }

    /* renamed from: rx.internal.operators.OperatorScan.3 */
    class C14233 extends Subscriber<T> {
        final /* synthetic */ Object val$initialValue;
        final /* synthetic */ InitialProducer val$ip;
        private R value;

        C14233(Object obj, InitialProducer initialProducer) {
            this.val$initialValue = obj;
            this.val$ip = initialProducer;
            this.value = this.val$initialValue;
        }

        public void onNext(T currentValue) {
            try {
                R v = OperatorScan.this.accumulator.call(this.value, currentValue);
                this.value = v;
                this.val$ip.onNext(v);
            } catch (Throwable e) {
                Exceptions.throwOrReport(e, this, currentValue);
            }
        }

        public void onError(Throwable e) {
            this.val$ip.onError(e);
        }

        public void onCompleted() {
            this.val$ip.onCompleted();
        }

        public void setProducer(Producer producer) {
            this.val$ip.setProducer(producer);
        }
    }

    static {
        NO_INITIAL_VALUE = new Object();
    }

    public OperatorScan(R initialValue, Func2<R, ? super T, R> accumulator) {
        this(new C14211(initialValue), (Func2) accumulator);
    }

    public OperatorScan(Func0<R> initialValueFactory, Func2<R, ? super T, R> accumulator) {
        this.initialValueFactory = initialValueFactory;
        this.accumulator = accumulator;
    }

    public OperatorScan(Func2<R, ? super T, R> accumulator) {
        this(NO_INITIAL_VALUE, (Func2) accumulator);
    }

    public Subscriber<? super T> call(Subscriber<? super R> child) {
        R initialValue = this.initialValueFactory.call();
        if (initialValue == NO_INITIAL_VALUE) {
            return new C14222(child, child);
        }
        InitialProducer<R> ip = new InitialProducer(initialValue, child);
        Subscriber<? super T> parent = new C14233(initialValue, ip);
        child.add(parent);
        child.setProducer(ip);
        return parent;
    }
}
