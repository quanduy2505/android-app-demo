package rx.subjects;

import java.util.ArrayList;
import java.util.List;
import rx.Observable.OnSubscribe;
import rx.annotations.Beta;
import rx.exceptions.Exceptions;
import rx.functions.Action1;
import rx.internal.operators.NotificationLite;

public final class PublishSubject<T> extends Subject<T, T> {
    private final NotificationLite<T> nl;
    final SubjectSubscriptionManager<T> state;

    /* renamed from: rx.subjects.PublishSubject.1 */
    static class C15711 implements Action1<SubjectObserver<T>> {
        final /* synthetic */ SubjectSubscriptionManager val$state;

        C15711(SubjectSubscriptionManager subjectSubscriptionManager) {
            this.val$state = subjectSubscriptionManager;
        }

        public void call(SubjectObserver<T> o) {
            o.emitFirst(this.val$state.getLatest(), this.val$state.nl);
        }
    }

    public static <T> PublishSubject<T> create() {
        SubjectSubscriptionManager<T> state = new SubjectSubscriptionManager();
        state.onTerminated = new C15711(state);
        return new PublishSubject(state, state);
    }

    protected PublishSubject(OnSubscribe<T> onSubscribe, SubjectSubscriptionManager<T> state) {
        super(onSubscribe);
        this.nl = NotificationLite.instance();
        this.state = state;
    }

    public void onCompleted() {
        if (this.state.active) {
            Object n = this.nl.completed();
            for (SubjectObserver<T> bo : this.state.terminate(n)) {
                bo.emitNext(n, this.state.nl);
            }
        }
    }

    public void onError(Throwable e) {
        if (this.state.active) {
            Object n = this.nl.error(e);
            List<Throwable> errors = null;
            for (SubjectObserver<T> bo : this.state.terminate(n)) {
                try {
                    bo.emitNext(n, this.state.nl);
                } catch (Throwable e2) {
                    if (errors == null) {
                        errors = new ArrayList();
                    }
                    errors.add(e2);
                }
            }
            Exceptions.throwIfAny(errors);
        }
    }

    public void onNext(T v) {
        for (SubjectObserver<T> bo : this.state.observers()) {
            bo.onNext(v);
        }
    }

    public boolean hasObservers() {
        return this.state.observers().length > 0;
    }

    @Beta
    public boolean hasThrowable() {
        return this.nl.isError(this.state.getLatest());
    }

    @Beta
    public boolean hasCompleted() {
        Object o = this.state.getLatest();
        return (o == null || this.nl.isError(o)) ? false : true;
    }

    @Beta
    public Throwable getThrowable() {
        Object o = this.state.getLatest();
        if (this.nl.isError(o)) {
            return this.nl.getError(o);
        }
        return null;
    }
}
