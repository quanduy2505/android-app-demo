package rx.internal.operators;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import rx.Completable;
import rx.Completable.CompletableOnSubscribe;
import rx.Completable.CompletableSubscriber;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public final class CompletableOnSubscribeMergeDelayErrorArray implements CompletableOnSubscribe {
    final Completable[] sources;

    /* renamed from: rx.internal.operators.CompletableOnSubscribeMergeDelayErrorArray.1 */
    class C12541 implements CompletableSubscriber {
        final /* synthetic */ Queue val$q;
        final /* synthetic */ CompletableSubscriber val$s;
        final /* synthetic */ CompositeSubscription val$set;
        final /* synthetic */ AtomicInteger val$wip;

        C12541(CompositeSubscription compositeSubscription, Queue queue, AtomicInteger atomicInteger, CompletableSubscriber completableSubscriber) {
            this.val$set = compositeSubscription;
            this.val$q = queue;
            this.val$wip = atomicInteger;
            this.val$s = completableSubscriber;
        }

        public void onSubscribe(Subscription d) {
            this.val$set.add(d);
        }

        public void onError(Throwable e) {
            this.val$q.offer(e);
            tryTerminate();
        }

        public void onCompleted() {
            tryTerminate();
        }

        void tryTerminate() {
            if (this.val$wip.decrementAndGet() != 0) {
                return;
            }
            if (this.val$q.isEmpty()) {
                this.val$s.onCompleted();
            } else {
                this.val$s.onError(CompletableOnSubscribeMerge.collectErrors(this.val$q));
            }
        }
    }

    public CompletableOnSubscribeMergeDelayErrorArray(Completable[] sources) {
        this.sources = sources;
    }

    public void call(CompletableSubscriber s) {
        CompositeSubscription set = new CompositeSubscription();
        AtomicInteger wip = new AtomicInteger(this.sources.length + 1);
        Queue<Throwable> q = new ConcurrentLinkedQueue();
        s.onSubscribe(set);
        Completable[] arr$ = this.sources;
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            Completable c = arr$[i$];
            if (!set.isUnsubscribed()) {
                if (c == null) {
                    q.offer(new NullPointerException("A completable source is null"));
                    wip.decrementAndGet();
                } else {
                    c.subscribe(new C12541(set, q, wip, s));
                }
                i$++;
            } else {
                return;
            }
        }
        if (wip.decrementAndGet() != 0) {
            return;
        }
        if (q.isEmpty()) {
            s.onCompleted();
        } else {
            s.onError(CompletableOnSubscribeMerge.collectErrors(q));
        }
    }
}
