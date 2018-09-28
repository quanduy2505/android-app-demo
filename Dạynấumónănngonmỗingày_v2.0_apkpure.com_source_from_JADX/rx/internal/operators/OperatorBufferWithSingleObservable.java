package rx.internal.operators;

import java.util.ArrayList;
import java.util.List;
import rx.Observable;
import rx.Observable.Operator;
import rx.Observer;
import rx.Subscriber;
import rx.exceptions.Exceptions;
import rx.functions.Func0;
import rx.observers.SerializedSubscriber;
import rx.observers.Subscribers;

public final class OperatorBufferWithSingleObservable<T, TClosing> implements Operator<List<T>, T> {
    final Func0<? extends Observable<? extends TClosing>> bufferClosingSelector;
    final int initialCapacity;

    /* renamed from: rx.internal.operators.OperatorBufferWithSingleObservable.1 */
    class C13871 implements Func0<Observable<? extends TClosing>> {
        final /* synthetic */ Observable val$bufferClosing;

        C13871(Observable observable) {
            this.val$bufferClosing = observable;
        }

        public Observable<? extends TClosing> call() {
            return this.val$bufferClosing;
        }
    }

    /* renamed from: rx.internal.operators.OperatorBufferWithSingleObservable.2 */
    class C13882 extends Subscriber<TClosing> {
        final /* synthetic */ BufferingSubscriber val$bsub;

        C13882(BufferingSubscriber bufferingSubscriber) {
            this.val$bsub = bufferingSubscriber;
        }

        public void onNext(TClosing tClosing) {
            this.val$bsub.emit();
        }

        public void onError(Throwable e) {
            this.val$bsub.onError(e);
        }

        public void onCompleted() {
            this.val$bsub.onCompleted();
        }
    }

    final class BufferingSubscriber extends Subscriber<T> {
        final Subscriber<? super List<T>> child;
        List<T> chunk;
        boolean done;

        public BufferingSubscriber(Subscriber<? super List<T>> child) {
            this.child = child;
            this.chunk = new ArrayList(OperatorBufferWithSingleObservable.this.initialCapacity);
        }

        public void onNext(T t) {
            synchronized (this) {
                if (this.done) {
                    return;
                }
                this.chunk.add(t);
            }
        }

        public void onError(Throwable e) {
            synchronized (this) {
                if (this.done) {
                    return;
                }
                this.done = true;
                this.chunk = null;
                this.child.onError(e);
                unsubscribe();
            }
        }

        public void onCompleted() {
            try {
                synchronized (this) {
                    if (this.done) {
                        return;
                    }
                    this.done = true;
                    List<T> toEmit = this.chunk;
                    this.chunk = null;
                    this.child.onNext(toEmit);
                    this.child.onCompleted();
                    unsubscribe();
                }
            } catch (Throwable t) {
                Exceptions.throwOrReport(t, this.child);
            }
        }

        void emit() {
            synchronized (this) {
                if (this.done) {
                    return;
                }
                List<T> toEmit = this.chunk;
                this.chunk = new ArrayList(OperatorBufferWithSingleObservable.this.initialCapacity);
                try {
                    this.child.onNext(toEmit);
                } catch (Throwable t) {
                    unsubscribe();
                    synchronized (this) {
                    }
                    if (!this.done) {
                        this.done = true;
                        Exceptions.throwOrReport(t, this.child);
                    }
                }
            }
        }
    }

    public OperatorBufferWithSingleObservable(Func0<? extends Observable<? extends TClosing>> bufferClosingSelector, int initialCapacity) {
        this.bufferClosingSelector = bufferClosingSelector;
        this.initialCapacity = initialCapacity;
    }

    public OperatorBufferWithSingleObservable(Observable<? extends TClosing> bufferClosing, int initialCapacity) {
        this.bufferClosingSelector = new C13871(bufferClosing);
        this.initialCapacity = initialCapacity;
    }

    public Subscriber<? super T> call(Subscriber<? super List<T>> child) {
        try {
            Observable<? extends TClosing> closing = (Observable) this.bufferClosingSelector.call();
            BufferingSubscriber bsub = new BufferingSubscriber(new SerializedSubscriber(child));
            Subscriber<TClosing> closingSubscriber = new C13882(bsub);
            child.add(closingSubscriber);
            child.add(bsub);
            closing.unsafeSubscribe(closingSubscriber);
            return bsub;
        } catch (Throwable t) {
            Exceptions.throwOrReport(t, (Observer) child);
            return Subscribers.empty();
        }
    }
}
