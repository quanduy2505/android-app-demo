package rx.internal.operators;

import com.facebook.internal.AnalyticsEvents;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import rx.Observable.OnSubscribe;
import rx.Observable.Operator;
import rx.Producer;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.internal.producers.ProducerArbiter;
import rx.internal.util.RxRingBuffer;
import rx.internal.util.UtilityFunctions;
import rx.observables.GroupedObservable;
import rx.plugins.RxJavaPlugins;
import rx.subscriptions.Subscriptions;

public final class OperatorGroupBy<T, K, V> implements Operator<GroupedObservable<K, V>, T> {
    final int bufferSize;
    final boolean delayError;
    final Func1<? super T, ? extends K> keySelector;
    final Func1<? super T, ? extends V> valueSelector;

    public static final class GroupByProducer implements Producer {
        final GroupBySubscriber<?, ?, ?> parent;

        public GroupByProducer(GroupBySubscriber<?, ?, ?> parent) {
            this.parent = parent;
        }

        public void request(long n) {
            this.parent.requestMore(n);
        }
    }

    public static final class GroupBySubscriber<T, K, V> extends Subscriber<T> {
        static final AtomicIntegerFieldUpdater<GroupBySubscriber> CANCELLED;
        static final AtomicIntegerFieldUpdater<GroupBySubscriber> GROUP_COUNT;
        static final Object NULL_KEY;
        static final AtomicLongFieldUpdater<GroupBySubscriber> REQUESTED;
        static final AtomicIntegerFieldUpdater<GroupBySubscriber> WIP;
        final Subscriber<? super GroupedObservable<K, V>> actual;
        final int bufferSize;
        volatile int cancelled;
        final boolean delayError;
        volatile boolean done;
        Throwable error;
        volatile int groupCount;
        final Map<Object, GroupedUnicast<K, V>> groups;
        final Func1<? super T, ? extends K> keySelector;
        final GroupByProducer producer;
        final Queue<GroupedObservable<K, V>> queue;
        volatile long requested;
        final ProducerArbiter f39s;
        final Func1<? super T, ? extends V> valueSelector;
        volatile int wip;

        static {
            NULL_KEY = new Object();
            CANCELLED = AtomicIntegerFieldUpdater.newUpdater(GroupBySubscriber.class, AnalyticsEvents.PARAMETER_SHARE_OUTCOME_CANCELLED);
            REQUESTED = AtomicLongFieldUpdater.newUpdater(GroupBySubscriber.class, "requested");
            GROUP_COUNT = AtomicIntegerFieldUpdater.newUpdater(GroupBySubscriber.class, "groupCount");
            WIP = AtomicIntegerFieldUpdater.newUpdater(GroupBySubscriber.class, "wip");
        }

        public GroupBySubscriber(Subscriber<? super GroupedObservable<K, V>> actual, Func1<? super T, ? extends K> keySelector, Func1<? super T, ? extends V> valueSelector, int bufferSize, boolean delayError) {
            this.actual = actual;
            this.keySelector = keySelector;
            this.valueSelector = valueSelector;
            this.bufferSize = bufferSize;
            this.delayError = delayError;
            this.groups = new ConcurrentHashMap();
            this.queue = new ConcurrentLinkedQueue();
            GROUP_COUNT.lazySet(this, 1);
            this.f39s = new ProducerArbiter();
            this.f39s.request((long) bufferSize);
            this.producer = new GroupByProducer(this);
        }

        public void setProducer(Producer s) {
            this.f39s.setProducer(s);
        }

        public void onNext(T t) {
            if (!this.done) {
                Queue<GroupedObservable<K, V>> q = this.queue;
                Subscriber<? super GroupedObservable<K, V>> a = this.actual;
                try {
                    K key = this.keySelector.call(t);
                    boolean notNew = true;
                    Object mapKey = key != null ? key : NULL_KEY;
                    GroupedUnicast<K, V> group = (GroupedUnicast) this.groups.get(mapKey);
                    if (group == null) {
                        if (this.cancelled == 0) {
                            group = GroupedUnicast.createWith(key, this.bufferSize, this, this.delayError);
                            this.groups.put(mapKey, group);
                            GROUP_COUNT.getAndIncrement(this);
                            notNew = false;
                            q.offer(group);
                            drain();
                        } else {
                            return;
                        }
                    }
                    try {
                        group.onNext(this.valueSelector.call(t));
                        if (notNew) {
                            this.f39s.request(1);
                        }
                    } catch (Throwable ex) {
                        unsubscribe();
                        errorAll(a, q, ex);
                    }
                } catch (Throwable ex2) {
                    unsubscribe();
                    errorAll(a, q, ex2);
                }
            }
        }

        public void onError(Throwable t) {
            if (this.done) {
                RxJavaPlugins.getInstance().getErrorHandler().handleError(t);
                return;
            }
            this.error = t;
            this.done = true;
            GROUP_COUNT.decrementAndGet(this);
            drain();
        }

        public void onCompleted() {
            if (!this.done) {
                for (GroupedUnicast<K, V> e : this.groups.values()) {
                    e.onComplete();
                }
                this.groups.clear();
                this.done = true;
                GROUP_COUNT.decrementAndGet(this);
                drain();
            }
        }

        public void requestMore(long n) {
            if (n < 0) {
                throw new IllegalArgumentException("n >= 0 required but it was " + n);
            }
            BackpressureUtils.getAndAddRequest(REQUESTED, this, n);
            drain();
        }

        public void cancel() {
            if (CANCELLED.compareAndSet(this, 0, 1) && GROUP_COUNT.decrementAndGet(this) == 0) {
                unsubscribe();
            }
        }

        public void cancel(K key) {
            if (this.groups.remove(key != null ? key : NULL_KEY) != null && GROUP_COUNT.decrementAndGet(this) == 0) {
                unsubscribe();
            }
        }

        void drain() {
            if (WIP.getAndIncrement(this) == 0) {
                int missed = 1;
                Queue<GroupedObservable<K, V>> q = this.queue;
                Subscriber<? super GroupedObservable<K, V>> a = this.actual;
                while (!checkTerminated(this.done, q.isEmpty(), a, q)) {
                    long r = this.requested;
                    boolean unbounded = r == Long.MAX_VALUE;
                    long e = 0;
                    while (r != 0) {
                        boolean d = this.done;
                        GroupedObservable<K, V> t = (GroupedObservable) q.poll();
                        boolean empty = t == null;
                        if (!checkTerminated(d, empty, a, q)) {
                            if (empty) {
                                break;
                            }
                            a.onNext(t);
                            r--;
                            e--;
                        } else {
                            return;
                        }
                    }
                    if (e != 0) {
                        if (!unbounded) {
                            REQUESTED.addAndGet(this, e);
                        }
                        this.f39s.request(-e);
                    }
                    missed = WIP.addAndGet(this, -missed);
                    if (missed == 0) {
                        return;
                    }
                }
            }
        }

        void errorAll(Subscriber<? super GroupedObservable<K, V>> a, Queue<?> q, Throwable ex) {
            q.clear();
            List<GroupedUnicast<K, V>> list = new ArrayList(this.groups.values());
            this.groups.clear();
            for (GroupedUnicast<K, V> e : list) {
                e.onError(ex);
            }
            a.onError(ex);
        }

        boolean checkTerminated(boolean d, boolean empty, Subscriber<? super GroupedObservable<K, V>> a, Queue<?> q) {
            if (d) {
                Throwable err = this.error;
                if (err != null) {
                    errorAll(a, q, err);
                    return true;
                } else if (empty) {
                    this.actual.onCompleted();
                    return true;
                }
            }
            return false;
        }
    }

    static final class GroupedUnicast<K, T> extends GroupedObservable<K, T> {
        final State<T, K> state;

        public static <T, K> GroupedUnicast<K, T> createWith(K key, int bufferSize, GroupBySubscriber<?, K, T> parent, boolean delayError) {
            return new GroupedUnicast(key, new State(bufferSize, parent, key, delayError));
        }

        protected GroupedUnicast(K key, State<T, K> state) {
            super(key, state);
            this.state = state;
        }

        public void onNext(T t) {
            this.state.onNext(t);
        }

        public void onError(Throwable e) {
            this.state.onError(e);
        }

        public void onComplete() {
            this.state.onComplete();
        }
    }

    /* renamed from: rx.internal.operators.OperatorGroupBy.1 */
    class C15311 implements Action0 {
        final /* synthetic */ GroupBySubscriber val$parent;

        C15311(GroupBySubscriber groupBySubscriber) {
            this.val$parent = groupBySubscriber;
        }

        public void call() {
            this.val$parent.cancel();
        }
    }

    static final class State<T, K> extends AtomicInteger implements Producer, Subscription, OnSubscribe<T> {
        static final AtomicReferenceFieldUpdater<State, Subscriber> ACTUAL;
        static final AtomicIntegerFieldUpdater<State> CANCELLED;
        static final AtomicIntegerFieldUpdater<State> ONCE;
        static final AtomicLongFieldUpdater<State> REQUESTED;
        private static final long serialVersionUID = -3852313036005250360L;
        volatile Subscriber<? super T> actual;
        volatile int cancelled;
        final boolean delayError;
        volatile boolean done;
        Throwable error;
        final K key;
        volatile int once;
        final GroupBySubscriber<?, K, T> parent;
        final Queue<Object> queue;
        volatile long requested;

        static {
            REQUESTED = AtomicLongFieldUpdater.newUpdater(State.class, "requested");
            CANCELLED = AtomicIntegerFieldUpdater.newUpdater(State.class, AnalyticsEvents.PARAMETER_SHARE_OUTCOME_CANCELLED);
            ACTUAL = AtomicReferenceFieldUpdater.newUpdater(State.class, Subscriber.class, "actual");
            ONCE = AtomicIntegerFieldUpdater.newUpdater(State.class, "once");
        }

        public State(int bufferSize, GroupBySubscriber<?, K, T> parent, K key, boolean delayError) {
            this.queue = new ConcurrentLinkedQueue();
            this.parent = parent;
            this.key = key;
            this.delayError = delayError;
        }

        public void request(long n) {
            if (n < 0) {
                throw new IllegalArgumentException("n >= required but it was " + n);
            } else if (n != 0) {
                BackpressureUtils.getAndAddRequest(REQUESTED, this, n);
                drain();
            }
        }

        public boolean isUnsubscribed() {
            return this.cancelled != 0;
        }

        public void unsubscribe() {
            if (CANCELLED.compareAndSet(this, 0, 1) && getAndIncrement() == 0) {
                this.parent.cancel(this.key);
            }
        }

        public void call(Subscriber<? super T> s) {
            if (ONCE.compareAndSet(this, 0, 1)) {
                s.add(this);
                s.setProducer(this);
                ACTUAL.lazySet(this, s);
                drain();
                return;
            }
            s.onError(new IllegalStateException("Only one Subscriber allowed!"));
        }

        public void onNext(T t) {
            if (t == null) {
                this.error = new NullPointerException();
                this.done = true;
            } else {
                this.queue.offer(NotificationLite.instance().next(t));
            }
            drain();
        }

        public void onError(Throwable e) {
            this.error = e;
            this.done = true;
            drain();
        }

        public void onComplete() {
            this.done = true;
            drain();
        }

        void drain() {
            if (getAndIncrement() == 0) {
                int missed = 1;
                Queue<Object> q = this.queue;
                boolean delayError = this.delayError;
                Subscriber<? super T> a = this.actual;
                NotificationLite<T> nl = NotificationLite.instance();
                while (true) {
                    if (a != null) {
                        if (!checkTerminated(this.done, q.isEmpty(), a, delayError)) {
                            long r = this.requested;
                            boolean unbounded = r == Long.MAX_VALUE;
                            long e = 0;
                            while (r != 0) {
                                boolean d = this.done;
                                Object v = q.poll();
                                boolean empty = v == null;
                                if (!checkTerminated(d, empty, a, delayError)) {
                                    if (empty) {
                                        break;
                                    }
                                    a.onNext(nl.getValue(v));
                                    r--;
                                    e--;
                                } else {
                                    return;
                                }
                            }
                            if (e != 0) {
                                if (!unbounded) {
                                    REQUESTED.addAndGet(this, e);
                                }
                                this.parent.f39s.request(-e);
                            }
                        } else {
                            return;
                        }
                    }
                    missed = addAndGet(-missed);
                    if (missed == 0) {
                        return;
                    }
                    if (a == null) {
                        a = this.actual;
                    }
                }
            }
        }

        boolean checkTerminated(boolean d, boolean empty, Subscriber<? super T> a, boolean delayError) {
            if (this.cancelled != 0) {
                this.queue.clear();
                this.parent.cancel(this.key);
                return true;
            }
            if (d) {
                Throwable e;
                if (!delayError) {
                    e = this.error;
                    if (e != null) {
                        this.queue.clear();
                        a.onError(e);
                        return true;
                    } else if (empty) {
                        a.onCompleted();
                        return true;
                    }
                } else if (empty) {
                    e = this.error;
                    if (e != null) {
                        a.onError(e);
                        return true;
                    }
                    a.onCompleted();
                    return true;
                }
            }
            return false;
        }
    }

    public OperatorGroupBy(Func1<? super T, ? extends K> keySelector) {
        this(keySelector, UtilityFunctions.identity(), RxRingBuffer.SIZE, false);
    }

    public OperatorGroupBy(Func1<? super T, ? extends K> keySelector, Func1<? super T, ? extends V> valueSelector) {
        this(keySelector, valueSelector, RxRingBuffer.SIZE, false);
    }

    public OperatorGroupBy(Func1<? super T, ? extends K> keySelector, Func1<? super T, ? extends V> valueSelector, int bufferSize, boolean delayError) {
        this.keySelector = keySelector;
        this.valueSelector = valueSelector;
        this.bufferSize = bufferSize;
        this.delayError = delayError;
    }

    public Subscriber<? super T> call(Subscriber<? super GroupedObservable<K, V>> t) {
        GroupBySubscriber<T, K, V> parent = new GroupBySubscriber(t, this.keySelector, this.valueSelector, this.bufferSize, this.delayError);
        t.add(Subscriptions.create(new C15311(parent)));
        t.setProducer(parent.producer);
        return parent;
    }
}
