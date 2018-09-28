package rx.internal.operators;

import java.util.concurrent.atomic.AtomicLong;
import rx.Notification;
import rx.Observable.Operator;
import rx.Producer;
import rx.Subscriber;
import rx.plugins.RxJavaPlugins;

public final class OperatorMaterialize<T> implements Operator<Notification<T>, T> {

    private static final class Holder {
        static final OperatorMaterialize<Object> INSTANCE;

        private Holder() {
        }

        static {
            INSTANCE = new OperatorMaterialize();
        }
    }

    /* renamed from: rx.internal.operators.OperatorMaterialize.1 */
    class C12661 implements Producer {
        final /* synthetic */ ParentSubscriber val$parent;

        C12661(ParentSubscriber parentSubscriber) {
            this.val$parent = parentSubscriber;
        }

        public void request(long n) {
            if (n > 0) {
                this.val$parent.requestMore(n);
            }
        }
    }

    private static class ParentSubscriber<T> extends Subscriber<T> {
        private boolean busy;
        private final Subscriber<? super Notification<T>> child;
        private boolean missed;
        private final AtomicLong requested;
        private volatile Notification<T> terminalNotification;

        ParentSubscriber(Subscriber<? super Notification<T>> child) {
            this.busy = false;
            this.missed = false;
            this.requested = new AtomicLong();
            this.child = child;
        }

        public void onStart() {
            request(0);
        }

        void requestMore(long n) {
            BackpressureUtils.getAndAddRequest(this.requested, n);
            request(n);
            drain();
        }

        public void onCompleted() {
            this.terminalNotification = Notification.createOnCompleted();
            drain();
        }

        public void onError(Throwable e) {
            this.terminalNotification = Notification.createOnError(e);
            RxJavaPlugins.getInstance().getErrorHandler().handleError(e);
            drain();
        }

        public void onNext(T t) {
            this.child.onNext(Notification.createOnNext(t));
            decrementRequested();
        }

        private void decrementRequested() {
            AtomicLong localRequested = this.requested;
            long r;
            do {
                r = localRequested.get();
                if (r == Long.MAX_VALUE) {
                    return;
                }
            } while (!localRequested.compareAndSet(r, r - 1));
        }

        private void drain() {
            synchronized (this) {
                if (this.busy) {
                    this.missed = true;
                    return;
                }
                AtomicLong localRequested = this.requested;
                while (!this.child.isUnsubscribed()) {
                    Notification<T> tn = this.terminalNotification;
                    if (tn == null || localRequested.get() <= 0) {
                        synchronized (this) {
                            if (this.missed) {
                            } else {
                                this.busy = false;
                                return;
                            }
                        }
                    }
                    this.terminalNotification = null;
                    this.child.onNext(tn);
                    if (!this.child.isUnsubscribed()) {
                        this.child.onCompleted();
                        return;
                    }
                    return;
                }
            }
        }
    }

    public static <T> OperatorMaterialize<T> instance() {
        return Holder.INSTANCE;
    }

    OperatorMaterialize() {
    }

    public Subscriber<? super T> call(Subscriber<? super Notification<T>> child) {
        ParentSubscriber<T> parent = new ParentSubscriber(child);
        child.add(parent);
        child.setProducer(new C12661(parent));
        return parent;
    }
}
