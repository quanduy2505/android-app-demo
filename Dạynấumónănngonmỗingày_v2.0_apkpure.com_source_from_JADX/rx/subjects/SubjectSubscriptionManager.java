package rx.subjects;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import rx.Observable.OnSubscribe;
import rx.Observer;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Actions;
import rx.internal.operators.NotificationLite;
import rx.subscriptions.Subscriptions;

final class SubjectSubscriptionManager<T> extends AtomicReference<State<T>> implements OnSubscribe<T> {
    boolean active;
    volatile Object latest;
    public final NotificationLite<T> nl;
    Action1<SubjectObserver<T>> onAdded;
    Action1<SubjectObserver<T>> onStart;
    Action1<SubjectObserver<T>> onTerminated;

    protected static final class State<T> {
        static final State EMPTY;
        static final SubjectObserver[] NO_OBSERVERS;
        static final State TERMINATED;
        final SubjectObserver[] observers;
        final boolean terminated;

        static {
            NO_OBSERVERS = new SubjectObserver[0];
            TERMINATED = new State(true, NO_OBSERVERS);
            EMPTY = new State(false, NO_OBSERVERS);
        }

        public State(boolean terminated, SubjectObserver[] observers) {
            this.terminated = terminated;
            this.observers = observers;
        }

        public State add(SubjectObserver o) {
            int n = this.observers.length;
            SubjectObserver[] b = new SubjectObserver[(n + 1)];
            System.arraycopy(this.observers, 0, b, 0, n);
            b[n] = o;
            return new State(this.terminated, b);
        }

        public State remove(SubjectObserver o) {
            SubjectObserver[] a = this.observers;
            int n = a.length;
            if (n == 1 && a[0] == o) {
                return EMPTY;
            }
            if (n == 0) {
                return this;
            }
            SubjectObserver[] b = new SubjectObserver[(n - 1)];
            int i = 0;
            int j = 0;
            while (i < n) {
                int j2;
                SubjectObserver ai = a[i];
                if (ai == o) {
                    j2 = j;
                } else if (j == n - 1) {
                    return this;
                } else {
                    j2 = j + 1;
                    b[j] = ai;
                }
                i++;
                j = j2;
            }
            if (j == 0) {
                return EMPTY;
            }
            if (j < n - 1) {
                SubjectObserver[] c = new SubjectObserver[j];
                System.arraycopy(b, 0, c, 0, j);
                b = c;
            }
            return new State(this.terminated, b);
        }
    }

    protected static final class SubjectObserver<T> implements Observer<T> {
        final Subscriber<? super T> actual;
        protected volatile boolean caughtUp;
        boolean emitting;
        boolean fastPath;
        boolean first;
        private volatile Object index;
        List<Object> queue;

        public SubjectObserver(Subscriber<? super T> actual) {
            this.first = true;
            this.actual = actual;
        }

        public void onNext(T t) {
            this.actual.onNext(t);
        }

        public void onError(Throwable e) {
            this.actual.onError(e);
        }

        public void onCompleted() {
            this.actual.onCompleted();
        }

        protected void emitNext(Object n, NotificationLite<T> nl) {
            if (!this.fastPath) {
                synchronized (this) {
                    this.first = false;
                    if (this.emitting) {
                        if (this.queue == null) {
                            this.queue = new ArrayList();
                        }
                        this.queue.add(n);
                        return;
                    }
                    this.fastPath = true;
                }
            }
            nl.accept(this.actual, n);
        }

        protected void emitFirst(Object n, NotificationLite<T> nl) {
            boolean z = false;
            synchronized (this) {
                if (!this.first || this.emitting) {
                    return;
                }
                this.first = false;
                if (n != null) {
                    z = true;
                }
                this.emitting = z;
                if (n != null) {
                    emitLoop(null, n, nl);
                }
            }
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        protected void emitLoop(java.util.List<java.lang.Object> r7, java.lang.Object r8, rx.internal.operators.NotificationLite<T> r9) {
            /*
            r6 = this;
            r2 = 1;
            r3 = 0;
        L_0x0002:
            if (r7 == 0) goto L_0x001f;
        L_0x0004:
            r0 = r7.iterator();	 Catch:{ all -> 0x0016 }
        L_0x0008:
            r4 = r0.hasNext();	 Catch:{ all -> 0x0016 }
            if (r4 == 0) goto L_0x001f;
        L_0x000e:
            r1 = r0.next();	 Catch:{ all -> 0x0016 }
            r6.accept(r1, r9);	 Catch:{ all -> 0x0016 }
            goto L_0x0008;
        L_0x0016:
            r4 = move-exception;
            if (r3 != 0) goto L_0x001e;
        L_0x0019:
            monitor-enter(r6);
            r5 = 0;
            r6.emitting = r5;	 Catch:{ all -> 0x0042 }
            monitor-exit(r6);	 Catch:{ all -> 0x0042 }
        L_0x001e:
            throw r4;
        L_0x001f:
            if (r2 == 0) goto L_0x0025;
        L_0x0021:
            r2 = 0;
            r6.accept(r8, r9);	 Catch:{ all -> 0x0016 }
        L_0x0025:
            monitor-enter(r6);	 Catch:{ all -> 0x0016 }
            r7 = r6.queue;	 Catch:{ all -> 0x003c }
            r4 = 0;
            r6.queue = r4;	 Catch:{ all -> 0x003c }
            if (r7 != 0) goto L_0x003a;
        L_0x002d:
            r4 = 0;
            r6.emitting = r4;	 Catch:{ all -> 0x003c }
            r3 = 1;
            monitor-exit(r6);	 Catch:{ all -> 0x003c }
            if (r3 != 0) goto L_0x0039;
        L_0x0034:
            monitor-enter(r6);
            r4 = 0;
            r6.emitting = r4;	 Catch:{ all -> 0x003f }
            monitor-exit(r6);	 Catch:{ all -> 0x003f }
        L_0x0039:
            return;
        L_0x003a:
            monitor-exit(r6);	 Catch:{ all -> 0x003c }
            goto L_0x0002;
        L_0x003c:
            r4 = move-exception;
            monitor-exit(r6);	 Catch:{ all -> 0x003c }
            throw r4;	 Catch:{ all -> 0x0016 }
        L_0x003f:
            r4 = move-exception;
            monitor-exit(r6);	 Catch:{ all -> 0x003f }
            throw r4;
        L_0x0042:
            r4 = move-exception;
            monitor-exit(r6);	 Catch:{ all -> 0x0042 }
            throw r4;
            */
            throw new UnsupportedOperationException("Method not decompiled: rx.subjects.SubjectSubscriptionManager.SubjectObserver.emitLoop(java.util.List, java.lang.Object, rx.internal.operators.NotificationLite):void");
        }

        protected void accept(Object n, NotificationLite<T> nl) {
            if (n != null) {
                nl.accept(this.actual, n);
            }
        }

        protected Observer<? super T> getActual() {
            return this.actual;
        }

        public <I> I index() {
            return this.index;
        }

        public void index(Object newIndex) {
            this.index = newIndex;
        }
    }

    /* renamed from: rx.subjects.SubjectSubscriptionManager.1 */
    class C15771 implements Action0 {
        final /* synthetic */ SubjectObserver val$bo;

        C15771(SubjectObserver subjectObserver) {
            this.val$bo = subjectObserver;
        }

        public void call() {
            SubjectSubscriptionManager.this.remove(this.val$bo);
        }
    }

    public SubjectSubscriptionManager() {
        super(State.EMPTY);
        this.active = true;
        this.onStart = Actions.empty();
        this.onAdded = Actions.empty();
        this.onTerminated = Actions.empty();
        this.nl = NotificationLite.instance();
    }

    public void call(Subscriber<? super T> child) {
        SubjectObserver<T> bo = new SubjectObserver(child);
        addUnsubscriber(child, bo);
        this.onStart.call(bo);
        if (!child.isUnsubscribed() && add(bo) && child.isUnsubscribed()) {
            remove(bo);
        }
    }

    void addUnsubscriber(Subscriber<? super T> child, SubjectObserver<T> bo) {
        child.add(Subscriptions.create(new C15771(bo)));
    }

    void setLatest(Object value) {
        this.latest = value;
    }

    Object getLatest() {
        return this.latest;
    }

    SubjectObserver<T>[] observers() {
        return ((State) get()).observers;
    }

    boolean add(SubjectObserver<T> o) {
        State oldState;
        do {
            oldState = (State) get();
            if (oldState.terminated) {
                this.onTerminated.call(o);
                return false;
            }
        } while (!compareAndSet(oldState, oldState.add(o)));
        this.onAdded.call(o);
        return true;
    }

    void remove(SubjectObserver<T> o) {
        State oldState;
        State newState;
        do {
            oldState = (State) get();
            if (!oldState.terminated) {
                newState = oldState.remove(o);
                if (newState == oldState) {
                    return;
                }
            } else {
                return;
            }
        } while (!compareAndSet(oldState, newState));
    }

    SubjectObserver<T>[] next(Object n) {
        setLatest(n);
        return ((State) get()).observers;
    }

    SubjectObserver<T>[] terminate(Object n) {
        setLatest(n);
        this.active = false;
        if (((State) get()).terminated) {
            return State.NO_OBSERVERS;
        }
        return ((State) getAndSet(State.TERMINATED)).observers;
    }
}
