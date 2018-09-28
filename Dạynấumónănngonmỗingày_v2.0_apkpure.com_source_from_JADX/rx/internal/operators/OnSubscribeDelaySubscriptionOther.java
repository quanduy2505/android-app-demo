package rx.internal.operators;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.observers.Subscribers;
import rx.plugins.RxJavaPlugins;
import rx.subscriptions.SerialSubscription;
import rx.subscriptions.Subscriptions;

public final class OnSubscribeDelaySubscriptionOther<T, U> implements OnSubscribe<T> {
    final Observable<? extends T> main;
    final Observable<U> other;

    /* renamed from: rx.internal.operators.OnSubscribeDelaySubscriptionOther.1 */
    class C13741 extends Subscriber<U> {
        boolean done;
        final /* synthetic */ Subscriber val$child;
        final /* synthetic */ SerialSubscription val$serial;

        C13741(Subscriber subscriber, SerialSubscription serialSubscription) {
            this.val$child = subscriber;
            this.val$serial = serialSubscription;
        }

        public void onNext(U u) {
            onCompleted();
        }

        public void onError(Throwable e) {
            if (this.done) {
                RxJavaPlugins.getInstance().getErrorHandler().handleError(e);
                return;
            }
            this.done = true;
            this.val$child.onError(e);
        }

        public void onCompleted() {
            if (!this.done) {
                this.done = true;
                this.val$serial.set(Subscriptions.unsubscribed());
                OnSubscribeDelaySubscriptionOther.this.main.unsafeSubscribe(this.val$child);
            }
        }
    }

    public OnSubscribeDelaySubscriptionOther(Observable<? extends T> main, Observable<U> other) {
        this.main = main;
        this.other = other;
    }

    public void call(Subscriber<? super T> t) {
        SerialSubscription serial = new SerialSubscription();
        t.add(serial);
        Subscriber<U> otherSubscriber = new C13741(Subscribers.wrap(t), serial);
        serial.set(otherSubscriber);
        this.other.unsafeSubscribe(otherSubscriber);
    }
}
