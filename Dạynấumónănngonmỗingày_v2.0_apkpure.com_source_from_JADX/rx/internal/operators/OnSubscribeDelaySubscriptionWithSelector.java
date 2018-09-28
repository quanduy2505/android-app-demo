package rx.internal.operators;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Observer;
import rx.Subscriber;
import rx.exceptions.Exceptions;
import rx.functions.Func0;
import rx.observers.Subscribers;

public final class OnSubscribeDelaySubscriptionWithSelector<T, U> implements OnSubscribe<T> {
    final Observable<? extends T> source;
    final Func0<? extends Observable<U>> subscriptionDelay;

    /* renamed from: rx.internal.operators.OnSubscribeDelaySubscriptionWithSelector.1 */
    class C13751 extends Subscriber<U> {
        final /* synthetic */ Subscriber val$child;

        C13751(Subscriber subscriber) {
            this.val$child = subscriber;
        }

        public void onCompleted() {
            OnSubscribeDelaySubscriptionWithSelector.this.source.unsafeSubscribe(Subscribers.wrap(this.val$child));
        }

        public void onError(Throwable e) {
            this.val$child.onError(e);
        }

        public void onNext(U u) {
        }
    }

    public OnSubscribeDelaySubscriptionWithSelector(Observable<? extends T> source, Func0<? extends Observable<U>> subscriptionDelay) {
        this.source = source;
        this.subscriptionDelay = subscriptionDelay;
    }

    public void call(Subscriber<? super T> child) {
        try {
            ((Observable) this.subscriptionDelay.call()).take(1).unsafeSubscribe(new C13751(child));
        } catch (Throwable e) {
            Exceptions.throwOrReport(e, (Observer) child);
        }
    }
}
