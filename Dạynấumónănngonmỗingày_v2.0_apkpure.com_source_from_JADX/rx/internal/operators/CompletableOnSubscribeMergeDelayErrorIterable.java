package rx.internal.operators;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import rx.Completable;
import rx.Completable.CompletableOnSubscribe;
import rx.Completable.CompletableSubscriber;
import rx.Subscription;
import rx.internal.util.unsafe.MpscLinkedQueue;
import rx.subscriptions.CompositeSubscription;

public final class CompletableOnSubscribeMergeDelayErrorIterable implements CompletableOnSubscribe {
    final Iterable<? extends Completable> sources;

    /* renamed from: rx.internal.operators.CompletableOnSubscribeMergeDelayErrorIterable.1 */
    class C12551 implements CompletableSubscriber {
        final /* synthetic */ Queue val$queue;
        final /* synthetic */ CompletableSubscriber val$s;
        final /* synthetic */ CompositeSubscription val$set;
        final /* synthetic */ AtomicInteger val$wip;

        C12551(CompositeSubscription compositeSubscription, Queue queue, AtomicInteger atomicInteger, CompletableSubscriber completableSubscriber) {
            this.val$set = compositeSubscription;
            this.val$queue = queue;
            this.val$wip = atomicInteger;
            this.val$s = completableSubscriber;
        }

        public void onSubscribe(Subscription d) {
            this.val$set.add(d);
        }

        public void onError(Throwable e) {
            this.val$queue.offer(e);
            tryTerminate();
        }

        public void onCompleted() {
            tryTerminate();
        }

        void tryTerminate() {
            if (this.val$wip.decrementAndGet() != 0) {
                return;
            }
            if (this.val$queue.isEmpty()) {
                this.val$s.onCompleted();
            } else {
                this.val$s.onError(CompletableOnSubscribeMerge.collectErrors(this.val$queue));
            }
        }
    }

    public CompletableOnSubscribeMergeDelayErrorIterable(Iterable<? extends Completable> sources) {
        this.sources = sources;
    }

    public void call(CompletableSubscriber s) {
        CompositeSubscription set = new CompositeSubscription();
        AtomicInteger wip = new AtomicInteger(1);
        Queue<Throwable> queue = new MpscLinkedQueue();
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
                                        queue.offer(new NullPointerException("A completable source is null"));
                                        if (wip.decrementAndGet() != 0) {
                                            return;
                                        }
                                        if (queue.isEmpty()) {
                                            s.onCompleted();
                                            return;
                                        } else {
                                            s.onError(CompletableOnSubscribeMerge.collectErrors(queue));
                                            return;
                                        }
                                    }
                                    wip.getAndIncrement();
                                    c.subscribe(new C12551(set, queue, wip, s));
                                } else {
                                    return;
                                }
                            } catch (NullPointerException e) {
                                queue.offer(e);
                                if (wip.decrementAndGet() != 0) {
                                    return;
                                }
                                if (queue.isEmpty()) {
                                    s.onCompleted();
                                    return;
                                } else {
                                    s.onError(CompletableOnSubscribeMerge.collectErrors(queue));
                                    return;
                                }
                            }
                        }
                        return;
                    } else if (wip.decrementAndGet() != 0) {
                        return;
                    } else {
                        if (queue.isEmpty()) {
                            s.onCompleted();
                            return;
                        } else {
                            s.onError(CompletableOnSubscribeMerge.collectErrors(queue));
                            return;
                        }
                    }
                } catch (Throwable e2) {
                    queue.offer(e2);
                    if (wip.decrementAndGet() != 0) {
                        return;
                    }
                    if (queue.isEmpty()) {
                        s.onCompleted();
                        return;
                    } else {
                        s.onError(CompletableOnSubscribeMerge.collectErrors(queue));
                        return;
                    }
                }
            }
        } catch (Throwable e22) {
            s.onError(e22);
        }
    }
}
