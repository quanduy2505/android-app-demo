package rx.internal.operators;

import rx.Observable;
import rx.Single;
import rx.Single.OnSubscribe;
import rx.SingleSubscriber;
import rx.Subscriber;
import rx.plugins.RxJavaPlugins;
import rx.subscriptions.SerialSubscription;

public final class SingleOnSubscribeDelaySubscriptionOther<T> implements OnSubscribe<T> {
    final Single<? extends T> main;
    final Observable<?> other;

    /* renamed from: rx.internal.operators.SingleOnSubscribeDelaySubscriptionOther.1 */
    class C14571 extends SingleSubscriber<T> {
        final /* synthetic */ SingleSubscriber val$subscriber;

        C14571(SingleSubscriber singleSubscriber) {
            this.val$subscriber = singleSubscriber;
        }

        public void onSuccess(T value) {
            this.val$subscriber.onSuccess(value);
        }

        public void onError(Throwable error) {
            this.val$subscriber.onError(error);
        }
    }

    /* renamed from: rx.internal.operators.SingleOnSubscribeDelaySubscriptionOther.2 */
    class C14582 extends Subscriber<Object> {
        boolean done;
        final /* synthetic */ SingleSubscriber val$child;
        final /* synthetic */ SerialSubscription val$serial;

        C14582(SingleSubscriber singleSubscriber, SerialSubscription serialSubscription) {
            this.val$child = singleSubscriber;
            this.val$serial = serialSubscription;
        }

        public void onNext(Object t) {
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
                this.val$serial.set(this.val$child);
                SingleOnSubscribeDelaySubscriptionOther.this.main.subscribe(this.val$child);
            }
        }
    }

    public SingleOnSubscribeDelaySubscriptionOther(Single<? extends T> main, Observable<?> other) {
        this.main = main;
        this.other = other;
    }

    public void call(SingleSubscriber<? super T> subscriber) {
        SingleSubscriber<T> child = new C14571(subscriber);
        SerialSubscription serial = new SerialSubscription();
        subscriber.add(serial);
        Subscriber otherSubscriber = new C14582(child, serial);
        serial.set(otherSubscriber);
        this.other.subscribe(otherSubscriber);
    }
}
