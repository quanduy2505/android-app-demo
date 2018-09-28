package rx.internal.operators;

import java.util.concurrent.atomic.AtomicReference;
import rx.Observable;
import rx.Observable.Operator;
import rx.Subscriber;
import rx.Subscription;
import rx.observers.SerializedSubscriber;

public final class OperatorSampleWithObservable<T, U> implements Operator<T, T> {
    static final Object EMPTY_TOKEN;
    final Observable<U> sampler;

    /* renamed from: rx.internal.operators.OperatorSampleWithObservable.1 */
    class C14191 extends Subscriber<U> {
        final /* synthetic */ AtomicReference val$main;
        final /* synthetic */ SerializedSubscriber val$s;
        final /* synthetic */ AtomicReference val$value;

        C14191(AtomicReference atomicReference, SerializedSubscriber serializedSubscriber, AtomicReference atomicReference2) {
            this.val$value = atomicReference;
            this.val$s = serializedSubscriber;
            this.val$main = atomicReference2;
        }

        public void onNext(U u) {
            T localValue = this.val$value.getAndSet(OperatorSampleWithObservable.EMPTY_TOKEN);
            if (localValue != OperatorSampleWithObservable.EMPTY_TOKEN) {
                this.val$s.onNext(localValue);
            }
        }

        public void onError(Throwable e) {
            this.val$s.onError(e);
            ((Subscription) this.val$main.get()).unsubscribe();
        }

        public void onCompleted() {
            onNext(null);
            this.val$s.onCompleted();
            ((Subscription) this.val$main.get()).unsubscribe();
        }
    }

    /* renamed from: rx.internal.operators.OperatorSampleWithObservable.2 */
    class C14202 extends Subscriber<T> {
        final /* synthetic */ SerializedSubscriber val$s;
        final /* synthetic */ Subscriber val$samplerSub;
        final /* synthetic */ AtomicReference val$value;

        C14202(AtomicReference atomicReference, SerializedSubscriber serializedSubscriber, Subscriber subscriber) {
            this.val$value = atomicReference;
            this.val$s = serializedSubscriber;
            this.val$samplerSub = subscriber;
        }

        public void onNext(T t) {
            this.val$value.set(t);
        }

        public void onError(Throwable e) {
            this.val$s.onError(e);
            this.val$samplerSub.unsubscribe();
        }

        public void onCompleted() {
            this.val$samplerSub.onNext(null);
            this.val$s.onCompleted();
            this.val$samplerSub.unsubscribe();
        }
    }

    static {
        EMPTY_TOKEN = new Object();
    }

    public OperatorSampleWithObservable(Observable<U> sampler) {
        this.sampler = sampler;
    }

    public Subscriber<? super T> call(Subscriber<? super T> child) {
        SerializedSubscriber<T> s = new SerializedSubscriber(child);
        AtomicReference<Object> value = new AtomicReference(EMPTY_TOKEN);
        AtomicReference<Subscription> main = new AtomicReference();
        Subscriber<U> samplerSub = new C14191(value, s, main);
        Subscriber<T> result = new C14202(value, s, samplerSub);
        main.lazySet(result);
        child.add(result);
        child.add(samplerSub);
        this.sampler.unsafeSubscribe(samplerSub);
        return result;
    }
}
