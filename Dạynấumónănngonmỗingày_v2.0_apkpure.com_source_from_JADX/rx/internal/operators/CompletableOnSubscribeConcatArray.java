package rx.internal.operators;

import java.util.concurrent.atomic.AtomicInteger;
import rx.Completable;
import rx.Completable.CompletableOnSubscribe;
import rx.Completable.CompletableSubscriber;
import rx.Subscription;
import rx.subscriptions.SerialSubscription;

public final class CompletableOnSubscribeConcatArray implements CompletableOnSubscribe {
    final Completable[] sources;

    static final class ConcatInnerSubscriber extends AtomicInteger implements CompletableSubscriber {
        private static final long serialVersionUID = -7965400327305809232L;
        final CompletableSubscriber actual;
        int index;
        final SerialSubscription sd;
        final Completable[] sources;

        public ConcatInnerSubscriber(CompletableSubscriber actual, Completable[] sources) {
            this.actual = actual;
            this.sources = sources;
            this.sd = new SerialSubscription();
        }

        public void onSubscribe(Subscription d) {
            this.sd.set(d);
        }

        public void onError(Throwable e) {
            this.actual.onError(e);
        }

        public void onCompleted() {
            next();
        }

        void next() {
            if (!this.sd.isUnsubscribed() && getAndIncrement() == 0) {
                Completable[] a = this.sources;
                while (!this.sd.isUnsubscribed()) {
                    int idx = this.index;
                    this.index = idx + 1;
                    if (idx == a.length) {
                        this.actual.onCompleted();
                        return;
                    }
                    a[idx].subscribe((CompletableSubscriber) this);
                    if (decrementAndGet() == 0) {
                        return;
                    }
                }
            }
        }
    }

    public CompletableOnSubscribeConcatArray(Completable[] sources) {
        this.sources = sources;
    }

    public void call(CompletableSubscriber s) {
        ConcatInnerSubscriber inner = new ConcatInnerSubscriber(s, this.sources);
        s.onSubscribe(inner.sd);
        inner.next();
    }
}
