package rx.subscriptions;

import java.util.concurrent.Future;
import rx.Subscription;
import rx.functions.Action0;

public final class Subscriptions {
    private static final Unsubscribed UNSUBSCRIBED;

    private static final class FutureSubscription implements Subscription {
        final Future<?> f38f;

        public FutureSubscription(Future<?> f) {
            this.f38f = f;
        }

        public void unsubscribe() {
            this.f38f.cancel(true);
        }

        public boolean isUnsubscribed() {
            return this.f38f.isCancelled();
        }
    }

    static final class Unsubscribed implements Subscription {
        Unsubscribed() {
        }

        public void unsubscribe() {
        }

        public boolean isUnsubscribed() {
            return true;
        }
    }

    private Subscriptions() {
        throw new IllegalStateException("No instances!");
    }

    public static Subscription empty() {
        return BooleanSubscription.create();
    }

    public static Subscription unsubscribed() {
        return UNSUBSCRIBED;
    }

    public static Subscription create(Action0 unsubscribe) {
        return BooleanSubscription.create(unsubscribe);
    }

    public static Subscription from(Future<?> f) {
        return new FutureSubscription(f);
    }

    public static CompositeSubscription from(Subscription... subscriptions) {
        return new CompositeSubscription(subscriptions);
    }

    static {
        UNSUBSCRIBED = new Unsubscribed();
    }
}
