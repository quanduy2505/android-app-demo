package rx.internal.util;

import rx.Subscription;

public class SynchronizedSubscription implements Subscription {
    private final Subscription f37s;

    public SynchronizedSubscription(Subscription s) {
        this.f37s = s;
    }

    public synchronized void unsubscribe() {
        this.f37s.unsubscribe();
    }

    public synchronized boolean isUnsubscribed() {
        return this.f37s.isUnsubscribed();
    }
}
