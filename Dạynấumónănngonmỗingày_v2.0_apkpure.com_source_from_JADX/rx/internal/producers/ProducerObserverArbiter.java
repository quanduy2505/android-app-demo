package rx.internal.producers;

import java.util.ArrayList;
import java.util.List;
import rx.Observer;
import rx.Producer;
import rx.Subscriber;

public final class ProducerObserverArbiter<T> implements Producer, Observer<T> {
    static final Producer NULL_PRODUCER;
    final Subscriber<? super T> child;
    Producer currentProducer;
    boolean emitting;
    volatile boolean hasError;
    Producer missedProducer;
    long missedRequested;
    Object missedTerminal;
    List<T> queue;
    long requested;

    /* renamed from: rx.internal.producers.ProducerObserverArbiter.1 */
    static class C12781 implements Producer {
        C12781() {
        }

        public void request(long n) {
        }
    }

    static {
        NULL_PRODUCER = new C12781();
    }

    public ProducerObserverArbiter(Subscriber<? super T> child) {
        this.child = child;
    }

    public void onNext(T t) {
        synchronized (this) {
            if (this.emitting) {
                List<T> q = this.queue;
                if (q == null) {
                    q = new ArrayList(4);
                    this.queue = q;
                }
                q.add(t);
                return;
            }
            try {
                this.child.onNext(t);
                long r = this.requested;
                if (r != Long.MAX_VALUE) {
                    this.requested = r - 1;
                }
                emitLoop();
                if (!true) {
                    synchronized (this) {
                        this.emitting = false;
                    }
                }
            } catch (Throwable th) {
                if (!false) {
                    synchronized (this) {
                    }
                    this.emitting = false;
                }
            }
        }
    }

    public void onError(Throwable e) {
        boolean emit;
        synchronized (this) {
            if (this.emitting) {
                this.missedTerminal = e;
                emit = false;
            } else {
                this.emitting = true;
                emit = true;
            }
        }
        if (emit) {
            this.child.onError(e);
        } else {
            this.hasError = true;
        }
    }

    public void onCompleted() {
        synchronized (this) {
            if (this.emitting) {
                this.missedTerminal = Boolean.valueOf(true);
                return;
            }
            this.emitting = true;
            this.child.onCompleted();
        }
    }

    public void request(long n) {
        if (n < 0) {
            throw new IllegalArgumentException("n >= 0 required");
        } else if (n != 0) {
            synchronized (this) {
                if (this.emitting) {
                    this.missedRequested += n;
                    return;
                }
                this.emitting = true;
                Producer p = this.currentProducer;
                try {
                    long u = this.requested + n;
                    if (u < 0) {
                        u = Long.MAX_VALUE;
                    }
                    this.requested = u;
                    emitLoop();
                    if (!true) {
                        synchronized (this) {
                            this.emitting = false;
                        }
                    }
                    if (p != null) {
                        p.request(n);
                    }
                } catch (Throwable th) {
                    if (!false) {
                        synchronized (this) {
                        }
                        this.emitting = false;
                    }
                }
            }
        }
    }

    public void setProducer(Producer p) {
        synchronized (this) {
            if (this.emitting) {
                if (p == null) {
                    p = NULL_PRODUCER;
                }
                this.missedProducer = p;
                return;
            }
            this.emitting = true;
            this.currentProducer = p;
            long r = this.requested;
            try {
                emitLoop();
                if (!true) {
                    synchronized (this) {
                        this.emitting = false;
                    }
                }
                if (p != null && r != 0) {
                    p.request(r);
                }
            } catch (Throwable th) {
                if (!false) {
                    synchronized (this) {
                    }
                    this.emitting = false;
                }
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    void emitLoop() {
        /*
        r28 = this;
        r0 = r28;
        r4 = r0.child;
        r20 = 0;
        r17 = 0;
    L_0x0008:
        r16 = 0;
        monitor-enter(r28);
        r0 = r28;
        r12 = r0.missedRequested;	 Catch:{ all -> 0x0065 }
        r0 = r28;
        r10 = r0.missedProducer;	 Catch:{ all -> 0x0065 }
        r0 = r28;
        r11 = r0.missedTerminal;	 Catch:{ all -> 0x0065 }
        r0 = r28;
        r15 = r0.queue;	 Catch:{ all -> 0x0065 }
        r26 = 0;
        r25 = (r12 > r26 ? 1 : (r12 == r26 ? 0 : -1));
        if (r25 != 0) goto L_0x0044;
    L_0x0021:
        if (r10 != 0) goto L_0x0044;
    L_0x0023:
        if (r15 != 0) goto L_0x0044;
    L_0x0025:
        if (r11 != 0) goto L_0x0044;
    L_0x0027:
        r25 = 0;
        r0 = r25;
        r1 = r28;
        r1.emitting = r0;	 Catch:{ all -> 0x0065 }
        r16 = 1;
    L_0x0031:
        monitor-exit(r28);	 Catch:{ all -> 0x0065 }
        if (r16 == 0) goto L_0x0068;
    L_0x0034:
        r26 = 0;
        r25 = (r20 > r26 ? 1 : (r20 == r26 ? 0 : -1));
        if (r25 == 0) goto L_0x0043;
    L_0x003a:
        if (r17 == 0) goto L_0x0043;
    L_0x003c:
        r0 = r17;
        r1 = r20;
        r0.request(r1);
    L_0x0043:
        return;
    L_0x0044:
        r26 = 0;
        r0 = r26;
        r2 = r28;
        r2.missedRequested = r0;	 Catch:{ all -> 0x0065 }
        r25 = 0;
        r0 = r25;
        r1 = r28;
        r1.missedProducer = r0;	 Catch:{ all -> 0x0065 }
        r25 = 0;
        r0 = r25;
        r1 = r28;
        r1.queue = r0;	 Catch:{ all -> 0x0065 }
        r25 = 0;
        r0 = r25;
        r1 = r28;
        r1.missedTerminal = r0;	 Catch:{ all -> 0x0065 }
        goto L_0x0031;
    L_0x0065:
        r25 = move-exception;
        monitor-exit(r28);	 Catch:{ all -> 0x0065 }
        throw r25;
    L_0x0068:
        if (r15 == 0) goto L_0x0070;
    L_0x006a:
        r25 = r15.isEmpty();
        if (r25 == 0) goto L_0x007f;
    L_0x0070:
        r5 = 1;
    L_0x0071:
        if (r11 == 0) goto L_0x0087;
    L_0x0073:
        r25 = java.lang.Boolean.TRUE;
        r0 = r25;
        if (r11 == r0) goto L_0x0081;
    L_0x0079:
        r11 = (java.lang.Throwable) r11;
        r4.onError(r11);
        goto L_0x0043;
    L_0x007f:
        r5 = 0;
        goto L_0x0071;
    L_0x0081:
        if (r5 == 0) goto L_0x0087;
    L_0x0083:
        r4.onCompleted();
        goto L_0x0043;
    L_0x0087:
        r6 = 0;
        if (r15 == 0) goto L_0x00bf;
    L_0x008b:
        r9 = r15.iterator();
    L_0x008f:
        r25 = r9.hasNext();
        if (r25 == 0) goto L_0x00b4;
    L_0x0095:
        r24 = r9.next();
        r25 = r4.isUnsubscribed();
        if (r25 != 0) goto L_0x0043;
    L_0x009f:
        r0 = r28;
        r0 = r0.hasError;
        r25 = r0;
        if (r25 != 0) goto L_0x0008;
    L_0x00a7:
        r0 = r24;
        r4.onNext(r0);	 Catch:{ Throwable -> 0x00ad }
        goto L_0x008f;
    L_0x00ad:
        r8 = move-exception;
        r0 = r24;
        rx.exceptions.Exceptions.throwOrReport(r8, r4, r0);
        goto L_0x0043;
    L_0x00b4:
        r25 = r15.size();
        r0 = r25;
        r0 = (long) r0;
        r26 = r0;
        r6 = r6 + r26;
    L_0x00bf:
        r0 = r28;
        r0 = r0.requested;
        r18 = r0;
        r26 = 9223372036854775807; // 0x7fffffffffffffff float:NaN double:NaN;
        r25 = (r18 > r26 ? 1 : (r18 == r26 ? 0 : -1));
        if (r25 == 0) goto L_0x010a;
    L_0x00ce:
        r26 = 0;
        r25 = (r12 > r26 ? 1 : (r12 == r26 ? 0 : -1));
        if (r25 == 0) goto L_0x00e3;
    L_0x00d4:
        r22 = r18 + r12;
        r26 = 0;
        r25 = (r22 > r26 ? 1 : (r22 == r26 ? 0 : -1));
        if (r25 >= 0) goto L_0x00e1;
    L_0x00dc:
        r22 = 9223372036854775807; // 0x7fffffffffffffff float:NaN double:NaN;
    L_0x00e1:
        r18 = r22;
    L_0x00e3:
        r26 = 0;
        r25 = (r6 > r26 ? 1 : (r6 == r26 ? 0 : -1));
        if (r25 == 0) goto L_0x0104;
    L_0x00e9:
        r26 = 9223372036854775807; // 0x7fffffffffffffff float:NaN double:NaN;
        r25 = (r18 > r26 ? 1 : (r18 == r26 ? 0 : -1));
        if (r25 == 0) goto L_0x0104;
    L_0x00f2:
        r22 = r18 - r6;
        r26 = 0;
        r25 = (r22 > r26 ? 1 : (r22 == r26 ? 0 : -1));
        if (r25 >= 0) goto L_0x0102;
    L_0x00fa:
        r25 = new java.lang.IllegalStateException;
        r26 = "More produced than requested";
        r25.<init>(r26);
        throw r25;
    L_0x0102:
        r18 = r22;
    L_0x0104:
        r0 = r18;
        r2 = r28;
        r2.requested = r0;
    L_0x010a:
        if (r10 == 0) goto L_0x0132;
    L_0x010c:
        r25 = NULL_PRODUCER;
        r0 = r25;
        if (r10 != r0) goto L_0x011c;
    L_0x0112:
        r25 = 0;
        r0 = r25;
        r1 = r28;
        r1.currentProducer = r0;
        goto L_0x0008;
    L_0x011c:
        r0 = r28;
        r0.currentProducer = r10;
        r26 = 0;
        r25 = (r18 > r26 ? 1 : (r18 == r26 ? 0 : -1));
        if (r25 == 0) goto L_0x0008;
    L_0x0126:
        r0 = r20;
        r2 = r18;
        r20 = rx.internal.operators.BackpressureUtils.addCap(r0, r2);
        r17 = r10;
        goto L_0x0008;
    L_0x0132:
        r0 = r28;
        r14 = r0.currentProducer;
        if (r14 == 0) goto L_0x0008;
    L_0x0138:
        r26 = 0;
        r25 = (r12 > r26 ? 1 : (r12 == r26 ? 0 : -1));
        if (r25 == 0) goto L_0x0008;
    L_0x013e:
        r0 = r20;
        r20 = rx.internal.operators.BackpressureUtils.addCap(r0, r12);
        r17 = r14;
        goto L_0x0008;
        */
        throw new UnsupportedOperationException("Method not decompiled: rx.internal.producers.ProducerObserverArbiter.emitLoop():void");
    }
}
