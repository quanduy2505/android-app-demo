package rx.internal.operators;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import rx.Completable;
import rx.Completable.CompletableOnSubscribe;
import rx.Completable.CompletableSubscriber;
import rx.Subscription;
import rx.plugins.RxJavaPlugins;
import rx.subscriptions.CompositeSubscription;

public final class CompletableOnSubscribeMergeArray implements CompletableOnSubscribe {
    final Completable[] sources;

    /* renamed from: rx.internal.operators.CompletableOnSubscribeMergeArray.1 */
    class C12531 implements CompletableSubscriber {
        final /* synthetic */ AtomicBoolean val$once;
        final /* synthetic */ CompletableSubscriber val$s;
        final /* synthetic */ CompositeSubscription val$set;
        final /* synthetic */ AtomicInteger val$wip;

        C12531(CompositeSubscription compositeSubscription, AtomicBoolean atomicBoolean, CompletableSubscriber completableSubscriber, AtomicInteger atomicInteger) {
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

    public CompletableOnSubscribeMergeArray(Completable[] sources) {
        this.sources = sources;
    }

    public void call(CompletableSubscriber s) {
        CompositeSubscription set = new CompositeSubscription();
        AtomicInteger wip = new AtomicInteger(this.sources.length + 1);
        AtomicBoolean once = new AtomicBoolean();
        s.onSubscribe(set);
        Completable[] arr$ = this.sources;
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            Completable c = arr$[i$];
            if (!set.isUnsubscribed()) {
                if (c == null) {
                    set.unsubscribe();
                    NullPointerException npe = new NullPointerException("A completable source is null");
                    if (once.compareAndSet(false, true)) {
                        s.onError(npe);
                        return;
                    }
                    RxJavaPlugins.getInstance().getErrorHandler().handleError(npe);
                }
                c.subscribe(new C12531(set, once, s, wip));
                i$++;
            } else {
                return;
            }
        }
        if (wip.decrementAndGet() == 0 && once.compareAndSet(false, true)) {
            s.onCompleted();
        }
    }
}
