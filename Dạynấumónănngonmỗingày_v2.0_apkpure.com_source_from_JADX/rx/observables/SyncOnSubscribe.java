package rx.observables;

import java.util.concurrent.atomic.AtomicLong;
import rx.Observable.OnSubscribe;
import rx.Observer;
import rx.Producer;
import rx.Subscriber;
import rx.Subscription;
import rx.annotations.Beta;
import rx.exceptions.Exceptions;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Action2;
import rx.functions.Func0;
import rx.functions.Func2;
import rx.internal.operators.BackpressureUtils;
import rx.plugins.RxJavaPlugins;

@Beta
public abstract class SyncOnSubscribe<S, T> implements OnSubscribe<T> {

    private static class SubscriptionProducer<S, T> extends AtomicLong implements Producer, Subscription, Observer<T> {
        private static final long serialVersionUID = -3736864024352728072L;
        private final Subscriber<? super T> actualSubscriber;
        private boolean hasTerminated;
        private boolean onNextCalled;
        private final SyncOnSubscribe<S, T> parent;
        private S state;

        SubscriptionProducer(Subscriber<? super T> subscriber, SyncOnSubscribe<S, T> parent, S state) {
            this.actualSubscriber = subscriber;
            this.parent = parent;
            this.state = state;
        }

        public boolean isUnsubscribed() {
            return get() < 0;
        }

        public void unsubscribe() {
            long requestCount;
            do {
                requestCount = get();
                if (compareAndSet(0, -1)) {
                    doUnsubscribe();
                    return;
                }
            } while (!compareAndSet(requestCount, -2));
        }

        private boolean tryUnsubscribe() {
            if (!this.hasTerminated && get() >= -1) {
                return false;
            }
            set(-1);
            doUnsubscribe();
            return true;
        }

        private void doUnsubscribe() {
            try {
                this.parent.onUnsubscribe(this.state);
            } catch (Throwable e) {
                Exceptions.throwIfFatal(e);
                RxJavaPlugins.getInstance().getErrorHandler().handleError(e);
            }
        }

        public void request(long n) {
            if (n > 0 && BackpressureUtils.getAndAddRequest(this, n) == 0) {
                if (n == Long.MAX_VALUE) {
                    fastpath();
                } else {
                    slowPath(n);
                }
            }
        }

        private void fastpath() {
            SyncOnSubscribe<S, T> p = this.parent;
            Subscriber<? super T> a = this.actualSubscriber;
            do {
                try {
                    this.onNextCalled = false;
                    nextIteration(p);
                } catch (Throwable ex) {
                    handleThrownError(a, ex);
                    return;
                }
            } while (!tryUnsubscribe());
        }

        private void handleThrownError(Subscriber<? super T> a, Throwable ex) {
            if (this.hasTerminated) {
                RxJavaPlugins.getInstance().getErrorHandler().handleError(ex);
                return;
            }
            this.hasTerminated = true;
            a.onError(ex);
            unsubscribe();
        }

        private void slowPath(long n) {
            SyncOnSubscribe<S, T> p = this.parent;
            Subscriber<? super T> a = this.actualSubscriber;
            long numRequested = n;
            do {
                long numRemaining = numRequested;
                do {
                    try {
                        this.onNextCalled = false;
                        nextIteration(p);
                        if (!tryUnsubscribe()) {
                            if (this.onNextCalled) {
                                numRemaining--;
                            }
                        } else {
                            return;
                        }
                    } catch (Throwable ex) {
                        handleThrownError(a, ex);
                        return;
                    }
                } while (numRemaining != 0);
                numRequested = addAndGet(-numRequested);
            } while (numRequested > 0);
            tryUnsubscribe();
        }

        private void nextIteration(SyncOnSubscribe<S, T> parent) {
            this.state = parent.next(this.state, this);
        }

        public void onCompleted() {
            if (this.hasTerminated) {
                throw new IllegalStateException("Terminal event already emitted.");
            }
            this.hasTerminated = true;
            if (!this.actualSubscriber.isUnsubscribed()) {
                this.actualSubscriber.onCompleted();
            }
        }

        public void onError(Throwable e) {
            if (this.hasTerminated) {
                throw new IllegalStateException("Terminal event already emitted.");
            }
            this.hasTerminated = true;
            if (!this.actualSubscriber.isUnsubscribed()) {
                this.actualSubscriber.onError(e);
            }
        }

        public void onNext(T value) {
            if (this.onNextCalled) {
                throw new IllegalStateException("onNext called multiple times!");
            }
            this.onNextCalled = true;
            this.actualSubscriber.onNext(value);
        }
    }

    /* renamed from: rx.observables.SyncOnSubscribe.1 */
    static class C14821 implements Func2<S, Observer<? super T>, S> {
        final /* synthetic */ Action2 val$next;

        C14821(Action2 action2) {
            this.val$next = action2;
        }

        public S call(S state, Observer<? super T> subscriber) {
            this.val$next.call(state, subscriber);
            return state;
        }
    }

    /* renamed from: rx.observables.SyncOnSubscribe.2 */
    static class C14832 implements Func2<S, Observer<? super T>, S> {
        final /* synthetic */ Action2 val$next;

        C14832(Action2 action2) {
            this.val$next = action2;
        }

        public S call(S state, Observer<? super T> subscriber) {
            this.val$next.call(state, subscriber);
            return state;
        }
    }

    /* renamed from: rx.observables.SyncOnSubscribe.3 */
    static class C14843 implements Func2<Void, Observer<? super T>, Void> {
        final /* synthetic */ Action1 val$next;

        C14843(Action1 action1) {
            this.val$next = action1;
        }

        public Void call(Void state, Observer<? super T> subscriber) {
            this.val$next.call(subscriber);
            return state;
        }
    }

    /* renamed from: rx.observables.SyncOnSubscribe.4 */
    static class C14854 implements Func2<Void, Observer<? super T>, Void> {
        final /* synthetic */ Action1 val$next;

        C14854(Action1 action1) {
            this.val$next = action1;
        }

        public Void call(Void state, Observer<? super T> subscriber) {
            this.val$next.call(subscriber);
            return null;
        }
    }

    /* renamed from: rx.observables.SyncOnSubscribe.5 */
    static class C15665 implements Action1<Void> {
        final /* synthetic */ Action0 val$onUnsubscribe;

        C15665(Action0 action0) {
            this.val$onUnsubscribe = action0;
        }

        public void call(Void t) {
            this.val$onUnsubscribe.call();
        }
    }

    private static final class SyncOnSubscribeImpl<S, T> extends SyncOnSubscribe<S, T> {
        private final Func0<? extends S> generator;
        private final Func2<? super S, ? super Observer<? super T>, ? extends S> next;
        private final Action1<? super S> onUnsubscribe;

        public /* bridge */ /* synthetic */ void call(Object x0) {
            super.call((Subscriber) x0);
        }

        SyncOnSubscribeImpl(Func0<? extends S> generator, Func2<? super S, ? super Observer<? super T>, ? extends S> next, Action1<? super S> onUnsubscribe) {
            this.generator = generator;
            this.next = next;
            this.onUnsubscribe = onUnsubscribe;
        }

        public SyncOnSubscribeImpl(Func0<? extends S> generator, Func2<? super S, ? super Observer<? super T>, ? extends S> next) {
            this(generator, next, null);
        }

        public SyncOnSubscribeImpl(Func2<S, Observer<? super T>, S> next, Action1<? super S> onUnsubscribe) {
            this(null, next, onUnsubscribe);
        }

        public SyncOnSubscribeImpl(Func2<S, Observer<? super T>, S> nextFunc) {
            this(null, nextFunc, null);
        }

        protected S generateState() {
            return this.generator == null ? null : this.generator.call();
        }

        protected S next(S state, Observer<? super T> observer) {
            return this.next.call(state, observer);
        }

        protected void onUnsubscribe(S state) {
            if (this.onUnsubscribe != null) {
                this.onUnsubscribe.call(state);
            }
        }
    }

    protected abstract S generateState();

    protected abstract S next(S s, Observer<? super T> observer);

    public final void call(Subscriber<? super T> subscriber) {
        try {
            SubscriptionProducer<S, T> p = new SubscriptionProducer(subscriber, this, generateState());
            subscriber.add(p);
            subscriber.setProducer(p);
        } catch (Throwable e) {
            Exceptions.throwIfFatal(e);
            subscriber.onError(e);
        }
    }

    protected void onUnsubscribe(S s) {
    }

    @Beta
    public static <S, T> SyncOnSubscribe<S, T> createSingleState(Func0<? extends S> generator, Action2<? super S, ? super Observer<? super T>> next) {
        return new SyncOnSubscribeImpl((Func0) generator, new C14821(next));
    }

    @Beta
    public static <S, T> SyncOnSubscribe<S, T> createSingleState(Func0<? extends S> generator, Action2<? super S, ? super Observer<? super T>> next, Action1<? super S> onUnsubscribe) {
        return new SyncOnSubscribeImpl(generator, new C14832(next), onUnsubscribe);
    }

    @Beta
    public static <S, T> SyncOnSubscribe<S, T> createStateful(Func0<? extends S> generator, Func2<? super S, ? super Observer<? super T>, ? extends S> next, Action1<? super S> onUnsubscribe) {
        return new SyncOnSubscribeImpl(generator, next, onUnsubscribe);
    }

    @Beta
    public static <S, T> SyncOnSubscribe<S, T> createStateful(Func0<? extends S> generator, Func2<? super S, ? super Observer<? super T>, ? extends S> next) {
        return new SyncOnSubscribeImpl((Func0) generator, (Func2) next);
    }

    @Beta
    public static <T> SyncOnSubscribe<Void, T> createStateless(Action1<? super Observer<? super T>> next) {
        return new SyncOnSubscribeImpl(new C14843(next));
    }

    @Beta
    public static <T> SyncOnSubscribe<Void, T> createStateless(Action1<? super Observer<? super T>> next, Action0 onUnsubscribe) {
        return new SyncOnSubscribeImpl(new C14854(next), new C15665(onUnsubscribe));
    }
}
