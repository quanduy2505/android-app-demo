package rx.internal.operators;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import rx.Completable;
import rx.Completable.CompletableOnSubscribe;
import rx.Completable.CompletableSubscriber;
import rx.Subscription;
import rx.plugins.RxJavaPlugins;
import rx.subscriptions.CompositeSubscription;

public final class CompletableOnSubscribeMergeIterable implements CompletableOnSubscribe {
    final Iterable<? extends Completable> sources;

    /* renamed from: rx.internal.operators.CompletableOnSubscribeMergeIterable.1 */
    class C12561 implements CompletableSubscriber {
        final /* synthetic */ AtomicBoolean val$once;
        final /* synthetic */ CompletableSubscriber val$s;
        final /* synthetic */ CompositeSubscription val$set;
        final /* synthetic */ AtomicInteger val$wip;

        C12561(CompositeSubscription compositeSubscription, AtomicBoolean atomicBoolean, CompletableSubscriber completableSubscriber, AtomicInteger atomicInteger) {
            this.val$set = compositeSubscription;
            this.val$once = atomicBoolean;
            this.val$s = completableSubscriber;
            this.val$wip = atomicInteger;
        }

        public void onSubscribe(Subscription d) {
            this.val$set.add(d);
        }

        public void onError(Throwable e) {
            this.val$set.unsubscribe();
            if (this.val$once.compareAndSet(false, true)) {
                this.val$s.onError(e);
            } else {
                RxJavaPlugins.getInstance().getErrorHandler().handleError(e);
            }
        }

        public void onCompleted() {
            if (this.val$wip.decrementAndGet() == 0 && this.val$once.compareAndSet(false, true)) {
                this.val$s.onCompleted();
            }
        }
    }

    public CompletableOnSubscribeMergeIterable(Iterable<? extends Completable> sources) {
        this.sources = sources;
    }

    public void call(CompletableSubscriber s) {
        CompositeSubscription set = new CompositeSubscription();
        AtomicInteger wip = new AtomicInteger(1);
        AtomicBoolean once = new AtomicBoolean();
        s.onSubscribe(set);
        try {
            Iterator<? extends Completable> iterator = this.sources.iterator();
            if (iterator == null) {
                s.onError(new NullPointerException("The source iterator returned is null"));
                return;
            }
            while (!set.isUnsubscribed()) {
                try {
                    if (iterator.hasNext()) {
                        if (!set.isUnsubscribed()) {
                            try {
                                Completable c = (Completable) iterator.next();
                                if (!set.isUnsubscribed()) {
                                    if (c == null) {
                                        set.unsubscribe();
                                        NullPointerException npe = new NullPointerException("A completable source is null");
                                        if (once.compareAndSet(false, true)) {
                                            s.onError(npe);
                                            return;
                                        } else {
                                            RxJavaPlugins.getInstance().getErrorHandler().handleError(npe);
                                            return;
                                        }
                                    }
                                    wip.getAndIncrement();
                                    c.subscribe(new C12561(set, once, s, wip));
                                } else {
                                    return;
                                }
                            } catch (Throwable e) {
                                set.unsubscribe();
                                if (once.compareAndSet(false, true)) {
                                    s.onError(e);
                                    return;
                                } else {
                                    RxJavaPlugins.getInstance().getErrorHandler().handleError(e);
                                    return;
                                }
                            }
                        }
                        return;
                    } else if (wip.decrementAndGet() == 0 && once.compareAndSet(false, true)) {
                        s.onCompleted();
                        return;
                    } else {
                        return;
                    }
                } catch (Throwable e2) {
                    set.unsubscribe();
                    if (once.compareAndSet(false, true)) {
                        s.onError(e2);
                        return;
                    } else {
                        RxJavaPlugins.getInstance().getErrorHandler().handleError(e2);
                        return;
                    }
                }
            }
        } catch (Throwable e22) {
            s.onError(e22);
        }
    }
}
