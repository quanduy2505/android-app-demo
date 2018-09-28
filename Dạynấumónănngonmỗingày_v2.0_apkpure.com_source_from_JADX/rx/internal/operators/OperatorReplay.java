package rx.internal.operators;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Observer;
import rx.Producer;
import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.exceptions.Exceptions;
import rx.exceptions.OnErrorThrowable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.observables.ConnectableObservable;
import rx.schedulers.Timestamped;
import rx.subscriptions.Subscriptions;

public final class OperatorReplay<T> extends ConnectableObservable<T> {
    static final Func0 DEFAULT_UNBOUNDED_FACTORY;
    final Func0<? extends ReplayBuffer<T>> bufferFactory;
    final AtomicReference<ReplaySubscriber<T>> current;
    final Observable<? extends T> source;

    static final class Node extends AtomicReference<Node> {
        private static final long serialVersionUID = 245354315435971818L;
        final long index;
        final Object value;

        public Node(Object value, long index) {
            this.value = value;
            this.index = index;
        }
    }

    interface ReplayBuffer<T> {
        void complete();

        void error(Throwable th);

        void next(T t);

        void replay(InnerProducer<T> innerProducer);
    }

    static class BoundedReplayBuffer<T> extends AtomicReference<Node> implements ReplayBuffer<T> {
        private static final long serialVersionUID = 2346567790059478686L;
        long index;
        final NotificationLite<T> nl;
        int size;
        Node tail;

        public BoundedReplayBuffer() {
            this.nl = NotificationLite.instance();
            Node n = new Node(null, 0);
            this.tail = n;
            set(n);
        }

        final void addLast(Node n) {
            this.tail.set(n);
            this.tail = n;
            this.size++;
        }

        final void removeFirst() {
            Node next = (Node) ((Node) get()).get();
            if (next == null) {
                throw new IllegalStateException("Empty list!");
            }
            this.size--;
            setFirst(next);
        }

        final void removeSome(int n) {
            Node head = (Node) get();
            while (n > 0) {
                head = (Node) head.get();
                n--;
                this.size--;
            }
            setFirst(head);
        }

        final void setFirst(Node n) {
            set(n);
        }

        public final void next(T value) {
            Object o = enterTransform(this.nl.next(value));
            long j = this.index + 1;
            this.index = j;
            addLast(new Node(o, j));
            truncate();
        }

        public final void error(Throwable e) {
            Object o = enterTransform(this.nl.error(e));
            long j = this.index + 1;
            this.index = j;
            addLast(new Node(o, j));
            truncateFinal();
        }

        public final void complete() {
            Object o = enterTransform(this.nl.completed());
            long j = this.index + 1;
            this.index = j;
            addLast(new Node(o, j));
            truncateFinal();
        }

        public final void replay(InnerProducer<T> output) {
            synchronized (output) {
                if (output.emitting) {
                    output.missed = true;
                    return;
                }
                output.emitting = true;
                while (!output.isUnsubscribed()) {
                    long r = output.get();
                    boolean unbounded = r == Long.MAX_VALUE;
                    long e = 0;
                    Node node = (Node) output.index();
                    if (node == null) {
                        node = (Node) get();
                        output.index = node;
                        output.addTotalRequested(node.index);
                    }
                    if (!output.isUnsubscribed()) {
                        while (r != 0) {
                            Node v = (Node) node.get();
                            if (v == null) {
                                break;
                            }
                            Object o = leaveTransform(v.value);
                            try {
                                if (this.nl.accept(output.child, o)) {
                                    output.index = null;
                                    return;
                                }
                                e++;
                                r--;
                                node = v;
                                if (output.isUnsubscribed()) {
                                    return;
                                }
                            } catch (Throwable err) {
                                output.index = null;
                                Exceptions.throwIfFatal(err);
                                output.unsubscribe();
                                if (!this.nl.isError(o) && !this.nl.isCompleted(o)) {
                                    output.child.onError(OnErrorThrowable.addValueAsLastCause(err, this.nl.getValue(o)));
                                    return;
                                }
                                return;
                            }
                        }
                        if (e != 0) {
                            output.index = node;
                            if (!unbounded) {
                                output.produced(e);
                            }
                        }
                        synchronized (output) {
                            if (output.missed) {
                                output.missed = false;
                            } else {
                                output.emitting = false;
                                return;
                            }
                        }
                    }
                    return;
                }
            }
        }

        Object enterTransform(Object value) {
            return value;
        }

        Object leaveTransform(Object value) {
            return value;
        }

        void truncate() {
        }

        void truncateFinal() {
        }

        final void collect(Collection<? super T> output) {
            Node n = (Node) get();
            while (true) {
                Node next = (Node) n.get();
                if (next != null) {
                    Object v = leaveTransform(next.value);
                    if (!this.nl.isCompleted(v) && !this.nl.isError(v)) {
                        output.add(this.nl.getValue(v));
                        n = next;
                    } else {
                        return;
                    }
                }
                return;
            }
        }

        boolean hasError() {
            return this.tail.value != null && this.nl.isError(leaveTransform(this.tail.value));
        }

        boolean hasCompleted() {
            return this.tail.value != null && this.nl.isCompleted(leaveTransform(this.tail.value));
        }
    }

    static final class InnerProducer<T> extends AtomicLong implements Producer, Subscription {
        static final long UNSUBSCRIBED = Long.MIN_VALUE;
        private static final long serialVersionUID = -4453897557930727610L;
        final Subscriber<? super T> child;
        boolean emitting;
        Object index;
        boolean missed;
        final ReplaySubscriber<T> parent;
        final AtomicLong totalRequested;

        public InnerProducer(ReplaySubscriber<T> parent, Subscriber<? super T> child) {
            this.parent = parent;
            this.child = child;
            this.totalRequested = new AtomicLong();
        }

        public void request(long n) {
            if (n >= 0) {
                long r;
                long u;
                do {
                    r = get();
                    if (r == UNSUBSCRIBED) {
                        return;
                    }
                    if (r < 0 || n != 0) {
                        u = r + n;
                        if (u < 0) {
                            u = Long.MAX_VALUE;
                        }
                    } else {
                        return;
                    }
                } while (!compareAndSet(r, u));
                addTotalRequested(n);
                this.parent.manageRequests();
                this.parent.buffer.replay(this);
            }
        }

        void addTotalRequested(long n) {
            long r;
            long u;
            do {
                r = this.totalRequested.get();
                u = r + n;
                if (u < 0) {
                    u = Long.MAX_VALUE;
                }
            } while (!this.totalRequested.compareAndSet(r, u));
        }

        public long produced(long n) {
            if (n <= 0) {
                throw new IllegalArgumentException("Cant produce zero or less");
            }
            long u;
            long r;
            do {
                r = get();
                if (r == UNSUBSCRIBED) {
                    return UNSUBSCRIBED;
                }
                u = r - n;
                if (u < 0) {
                    throw new IllegalStateException("More produced (" + n + ") than requested (" + r + ")");
                }
            } while (!compareAndSet(r, u));
            return u;
        }

        public boolean isUnsubscribed() {
            return get() == UNSUBSCRIBED;
        }

        public void unsubscribe() {
            if (get() != UNSUBSCRIBED && getAndSet(UNSUBSCRIBED) != UNSUBSCRIBED) {
                this.parent.remove(this);
                this.parent.manageRequests();
            }
        }

        <U> U index() {
            return this.index;
        }
    }

    static final class UnboundedReplayBuffer<T> extends ArrayList<Object> implements ReplayBuffer<T> {
        private static final long serialVersionUID = 7063189396499112664L;
        final NotificationLite<T> nl;
        volatile int size;

        public UnboundedReplayBuffer(int capacityHint) {
            super(capacityHint);
            this.nl = NotificationLite.instance();
        }

        public void next(T value) {
            add(this.nl.next(value));
            this.size++;
        }

        public void error(Throwable e) {
            add(this.nl.error(e));
            this.size++;
        }

        public void complete() {
            add(this.nl.completed());
            this.size++;
        }

        public void replay(InnerProducer<T> output) {
            synchronized (output) {
                Object o;
                if (output.emitting) {
                    output.missed = true;
                    return;
                }
                output.emitting = true;
                while (!output.isUnsubscribed()) {
                    int sourceIndex = this.size;
                    Integer destIndexObject = (Integer) output.index();
                    int destIndex = destIndexObject != null ? destIndexObject.intValue() : 0;
                    long r = output.get();
                    long r0 = r;
                    long e = 0;
                    while (r != 0 && destIndex < sourceIndex) {
                        o = get(destIndex);
                        try {
                            if (!this.nl.accept(output.child, o) && !output.isUnsubscribed()) {
                                destIndex++;
                                r--;
                                e++;
                            } else {
                                return;
                            }
                        } catch (Throwable err) {
                            Exceptions.throwIfFatal(err);
                            output.unsubscribe();
                            if (!this.nl.isError(o) && !this.nl.isCompleted(o)) {
                                output.child.onError(OnErrorThrowable.addValueAsLastCause(err, this.nl.getValue(o)));
                                return;
                            }
                            return;
                        }
                    }
                    if (e != 0) {
                        output.index = Integer.valueOf(destIndex);
                        if (r0 != Long.MAX_VALUE) {
                            output.produced(e);
                        }
                    }
                    synchronized (output) {
                        if (output.missed) {
                            output.missed = false;
                        } else {
                            output.emitting = false;
                            return;
                        }
                    }
                }
            }
        }
    }

    /* renamed from: rx.internal.operators.OperatorReplay.1 */
    static class C14131 implements Func0 {
        C14131() {
        }

        public Object call() {
            return new UnboundedReplayBuffer(16);
        }
    }

    /* renamed from: rx.internal.operators.OperatorReplay.4 */
    static class C14154 extends ConnectableObservable<T> {
        final /* synthetic */ ConnectableObservable val$co;

        C14154(OnSubscribe x0, ConnectableObservable connectableObservable) {
            this.val$co = connectableObservable;
            super(x0);
        }

        public void connect(Action1<? super Subscription> connection) {
            this.val$co.connect(connection);
        }
    }

    /* renamed from: rx.internal.operators.OperatorReplay.5 */
    static class C14165 implements Func0<ReplayBuffer<T>> {
        final /* synthetic */ int val$bufferSize;

        C14165(int i) {
            this.val$bufferSize = i;
        }

        public ReplayBuffer<T> call() {
            return new SizeBoundReplayBuffer(this.val$bufferSize);
        }
    }

    /* renamed from: rx.internal.operators.OperatorReplay.6 */
    static class C14176 implements Func0<ReplayBuffer<T>> {
        final /* synthetic */ int val$bufferSize;
        final /* synthetic */ long val$maxAgeInMillis;
        final /* synthetic */ Scheduler val$scheduler;

        C14176(int i, long j, Scheduler scheduler) {
            this.val$bufferSize = i;
            this.val$maxAgeInMillis = j;
            this.val$scheduler = scheduler;
        }

        public ReplayBuffer<T> call() {
            return new SizeAndTimeBoundReplayBuffer(this.val$bufferSize, this.val$maxAgeInMillis, this.val$scheduler);
        }
    }

    static final class ReplaySubscriber<T> extends Subscriber<T> implements Subscription {
        static final InnerProducer[] EMPTY;
        static final InnerProducer[] TERMINATED;
        final ReplayBuffer<T> buffer;
        boolean done;
        boolean emitting;
        long maxChildRequested;
        long maxUpstreamRequested;
        boolean missed;
        final NotificationLite<T> nl;
        volatile Producer producer;
        final AtomicReference<InnerProducer[]> producers;
        final AtomicBoolean shouldConnect;

        /* renamed from: rx.internal.operators.OperatorReplay.ReplaySubscriber.1 */
        class C15351 implements Action0 {
            C15351() {
            }

            public void call() {
                ReplaySubscriber.this.producers.getAndSet(ReplaySubscriber.TERMINATED);
            }
        }

        static {
            EMPTY = new InnerProducer[0];
            TERMINATED = new InnerProducer[0];
        }

        public ReplaySubscriber(AtomicReference<ReplaySubscriber<T>> atomicReference, ReplayBuffer<T> buffer) {
            this.buffer = buffer;
            this.nl = NotificationLite.instance();
            this.producers = new AtomicReference(EMPTY);
            this.shouldConnect = new AtomicBoolean();
            request(0);
        }

        void init() {
            add(Subscriptions.create(new C15351()));
        }

        boolean add(InnerProducer<T> producer) {
            if (producer == null) {
                throw new NullPointerException();
            }
            InnerProducer[] c;
            InnerProducer[] u;
            do {
                c = (InnerProducer[]) this.producers.get();
                if (c == TERMINATED) {
                    return false;
                }
                int len = c.length;
                u = new InnerProducer[(len + 1)];
                System.arraycopy(c, 0, u, 0, len);
                u[len] = producer;
            } while (!this.producers.compareAndSet(c, u));
            return true;
        }

        void remove(InnerProducer<T> producer) {
            InnerProducer[] c;
            InnerProducer[] u;
            do {
                c = (InnerProducer[]) this.producers.get();
                if (c != EMPTY && c != TERMINATED) {
                    int j = -1;
                    int len = c.length;
                    for (int i = 0; i < len; i++) {
                        if (c[i].equals(producer)) {
                            j = i;
                            break;
                        }
                    }
                    if (j < 0) {
                        return;
                    }
                    if (len == 1) {
                        u = EMPTY;
                    } else {
                        u = new InnerProducer[(len - 1)];
                        System.arraycopy(c, 0, u, 0, j);
                        System.arraycopy(c, j + 1, u, j, (len - j) - 1);
                    }
                } else {
                    return;
                }
            } while (!this.producers.compareAndSet(c, u));
        }

        public void setProducer(Producer p) {
            if (this.producer != null) {
                throw new IllegalStateException("Only a single producer can be set on a Subscriber.");
            }
            this.producer = p;
            manageRequests();
            replay();
        }

        public void onNext(T t) {
            if (!this.done) {
                this.buffer.next(t);
                replay();
            }
        }

        public void onError(Throwable e) {
            if (!this.done) {
                this.done = true;
                try {
                    this.buffer.error(e);
                    replay();
                } finally {
                    unsubscribe();
                }
            }
        }

        public void onCompleted() {
            if (!this.done) {
                this.done = true;
                try {
                    this.buffer.complete();
                    replay();
                } finally {
                    unsubscribe();
                }
            }
        }

        void manageRequests() {
            if (!isUnsubscribed()) {
                synchronized (this) {
                    if (this.emitting) {
                        this.missed = true;
                        return;
                    }
                    this.emitting = true;
                    while (!isUnsubscribed()) {
                        InnerProducer[] a = (InnerProducer[]) this.producers.get();
                        long ri = this.maxChildRequested;
                        long maxTotalRequests = ri;
                        for (InnerProducer<T> rp : a) {
                            maxTotalRequests = Math.max(maxTotalRequests, rp.totalRequested.get());
                        }
                        long ur = this.maxUpstreamRequested;
                        Producer p = this.producer;
                        long diff = maxTotalRequests - ri;
                        if (diff != 0) {
                            this.maxChildRequested = maxTotalRequests;
                            if (p == null) {
                                long u = ur + diff;
                                if (u < 0) {
                                    u = Long.MAX_VALUE;
                                }
                                this.maxUpstreamRequested = u;
                            } else if (ur != 0) {
                                this.maxUpstreamRequested = 0;
                                p.request(ur + diff);
                            } else {
                                p.request(diff);
                            }
                        } else if (!(ur == 0 || p == null)) {
                            this.maxUpstreamRequested = 0;
                            p.request(ur);
                        }
                        synchronized (this) {
                            if (this.missed) {
                                this.missed = false;
                            } else {
                                this.emitting = false;
                                return;
                            }
                        }
                    }
                }
            }
        }

        void replay() {
            for (InnerProducer<T> rp : (InnerProducer[]) this.producers.get()) {
                this.buffer.replay(rp);
            }
        }
    }

    static final class SizeAndTimeBoundReplayBuffer<T> extends BoundedReplayBuffer<T> {
        private static final long serialVersionUID = 3457957419649567404L;
        final int limit;
        final long maxAgeInMillis;
        final Scheduler scheduler;

        public SizeAndTimeBoundReplayBuffer(int limit, long maxAgeInMillis, Scheduler scheduler) {
            this.scheduler = scheduler;
            this.limit = limit;
            this.maxAgeInMillis = maxAgeInMillis;
        }

        Object enterTransform(Object value) {
            return new Timestamped(this.scheduler.now(), value);
        }

        Object leaveTransform(Object value) {
            return ((Timestamped) value).getValue();
        }

        void truncate() {
            long timeLimit = this.scheduler.now() - this.maxAgeInMillis;
            Node prev = (Node) get();
            Node next = (Node) prev.get();
            int e = 0;
            while (next != null) {
                if (this.size <= this.limit) {
                    if (next.value.getTimestampMillis() > timeLimit) {
                        break;
                    }
                    e++;
                    this.size--;
                    prev = next;
                    next = (Node) next.get();
                } else {
                    e++;
                    this.size--;
                    prev = next;
                    next = (Node) next.get();
                }
            }
            if (e != 0) {
                setFirst(prev);
            }
        }

        void truncateFinal() {
            long timeLimit = this.scheduler.now() - this.maxAgeInMillis;
            Node prev = (Node) get();
            Node next = (Node) prev.get();
            int e = 0;
            while (next != null && this.size > 1 && next.value.getTimestampMillis() <= timeLimit) {
                e++;
                this.size--;
                prev = next;
                next = (Node) next.get();
            }
            if (e != 0) {
                setFirst(prev);
            }
        }
    }

    static final class SizeBoundReplayBuffer<T> extends BoundedReplayBuffer<T> {
        private static final long serialVersionUID = -5898283885385201806L;
        final int limit;

        public SizeBoundReplayBuffer(int limit) {
            this.limit = limit;
        }

        void truncate() {
            if (this.size > this.limit) {
                removeFirst();
            }
        }
    }

    /* renamed from: rx.internal.operators.OperatorReplay.2 */
    static class C16112 implements OnSubscribe<R> {
        final /* synthetic */ Func0 val$connectableFactory;
        final /* synthetic */ Func1 val$selector;

        /* renamed from: rx.internal.operators.OperatorReplay.2.1 */
        class C15341 implements Action1<Subscription> {
            final /* synthetic */ Subscriber val$child;

            C15341(Subscriber subscriber) {
                this.val$child = subscriber;
            }

            public void call(Subscription t) {
                this.val$child.add(t);
            }
        }

        C16112(Func0 func0, Func1 func1) {
            this.val$connectableFactory = func0;
            this.val$selector = func1;
        }

        public void call(Subscriber<? super R> child) {
            try {
                ConnectableObservable<U> co = (ConnectableObservable) this.val$connectableFactory.call();
                ((Observable) this.val$selector.call(co)).subscribe((Subscriber) child);
                co.connect(new C15341(child));
            } catch (Throwable e) {
                Exceptions.throwOrReport(e, (Observer) child);
            }
        }
    }

    /* renamed from: rx.internal.operators.OperatorReplay.3 */
    static class C16123 implements OnSubscribe<T> {
        final /* synthetic */ Observable val$observable;

        /* renamed from: rx.internal.operators.OperatorReplay.3.1 */
        class C14141 extends Subscriber<T> {
            final /* synthetic */ Subscriber val$child;

            C14141(Subscriber x0, Subscriber subscriber) {
                this.val$child = subscriber;
                super(x0);
            }

            public void onNext(T t) {
                this.val$child.onNext(t);
            }

            public void onError(Throwable e) {
                this.val$child.onError(e);
            }

            public void onCompleted() {
                this.val$child.onCompleted();
            }
        }

        C16123(Observable observable) {
            this.val$observable = observable;
        }

        public void call(Subscriber<? super T> child) {
            this.val$observable.unsafeSubscribe(new C14141(child, child));
        }
    }

    /* renamed from: rx.internal.operators.OperatorReplay.7 */
    static class C16137 implements OnSubscribe<T> {
        final /* synthetic */ Func0 val$bufferFactory;
        final /* synthetic */ AtomicReference val$curr;

        C16137(AtomicReference atomicReference, Func0 func0) {
            this.val$curr = atomicReference;
            this.val$bufferFactory = func0;
        }

        public void call(Subscriber<? super T> child) {
            ReplaySubscriber<T> r;
            ReplaySubscriber<T> u;
            do {
                r = (ReplaySubscriber) this.val$curr.get();
                if (r != null) {
                    break;
                }
                u = new ReplaySubscriber(this.val$curr, (ReplayBuffer) this.val$bufferFactory.call());
                u.init();
            } while (!this.val$curr.compareAndSet(r, u));
            r = u;
            InnerProducer<T> inner = new InnerProducer(r, child);
            r.add(inner);
            child.add(inner);
            r.buffer.replay(inner);
            child.setProducer(inner);
        }
    }

    static {
        DEFAULT_UNBOUNDED_FACTORY = new C14131();
    }

    public static <T, U, R> Observable<R> multicastSelector(Func0<? extends ConnectableObservable<U>> connectableFactory, Func1<? super Observable<U>, ? extends Observable<R>> selector) {
        return Observable.create(new C16112(connectableFactory, selector));
    }

    public static <T> ConnectableObservable<T> observeOn(ConnectableObservable<T> co, Scheduler scheduler) {
        return new C14154(new C16123(co.observeOn(scheduler)), co);
    }

    public static <T> ConnectableObservable<T> create(Observable<? extends T> source) {
        return create((Observable) source, DEFAULT_UNBOUNDED_FACTORY);
    }

    public static <T> ConnectableObservable<T> create(Observable<? extends T> source, int bufferSize) {
        if (bufferSize == UrlImageViewHelper.CACHE_DURATION_INFINITE) {
            return create(source);
        }
        return create((Observable) source, new C14165(bufferSize));
    }

    public static <T> ConnectableObservable<T> create(Observable<? extends T> source, long maxAge, TimeUnit unit, Scheduler scheduler) {
        return create(source, maxAge, unit, scheduler, UrlImageViewHelper.CACHE_DURATION_INFINITE);
    }

    public static <T> ConnectableObservable<T> create(Observable<? extends T> source, long maxAge, TimeUnit unit, Scheduler scheduler, int bufferSize) {
        return create((Observable) source, new C14176(bufferSize, unit.toMillis(maxAge), scheduler));
    }

    static <T> ConnectableObservable<T> create(Observable<? extends T> source, Func0<? extends ReplayBuffer<T>> bufferFactory) {
        AtomicReference<ReplaySubscriber<T>> curr = new AtomicReference();
        return new OperatorReplay(new C16137(curr, bufferFactory), source, curr, bufferFactory);
    }

    private OperatorReplay(OnSubscribe<T> onSubscribe, Observable<? extends T> source, AtomicReference<ReplaySubscriber<T>> current, Func0<? extends ReplayBuffer<T>> bufferFactory) {
        super(onSubscribe);
        this.source = source;
        this.current = current;
        this.bufferFactory = bufferFactory;
    }

    public void connect(Action1<? super Subscription> connection) {
        ReplaySubscriber<T> ps;
        ReplaySubscriber<T> u;
        boolean doConnect;
        do {
            ps = (ReplaySubscriber) this.current.get();
            if (ps != null && !ps.isUnsubscribed()) {
                break;
            }
            u = new ReplaySubscriber(this.current, (ReplayBuffer) this.bufferFactory.call());
            u.init();
        } while (!this.current.compareAndSet(ps, u));
        ps = u;
        if (ps.shouldConnect.get() || !ps.shouldConnect.compareAndSet(false, true)) {
            doConnect = false;
        } else {
            doConnect = true;
        }
        connection.call(ps);
        if (doConnect) {
            this.source.unsafeSubscribe(ps);
        }
    }
}
