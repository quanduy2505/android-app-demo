package rx.internal.operators;

import java.util.Arrays;
import rx.Single;
import rx.Single.OnSubscribe;
import rx.SingleSubscriber;
import rx.exceptions.CompositeException;
import rx.exceptions.Exceptions;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.plugins.RxJavaPlugins;

public final class SingleOnSubscribeUsing<T, Resource> implements OnSubscribe<T> {
    final Action1<? super Resource> disposeAction;
    final boolean disposeEagerly;
    final Func0<Resource> resourceFactory;
    final Func1<? super Resource, ? extends Single<? extends T>> singleFactory;

    /* renamed from: rx.internal.operators.SingleOnSubscribeUsing.1 */
    class C14591 extends SingleSubscriber<T> {
        final /* synthetic */ SingleSubscriber val$child;
        final /* synthetic */ Object val$resource;

        C14591(Object obj, SingleSubscriber singleSubscriber) {
            this.val$resource = obj;
            this.val$child = singleSubscriber;
        }

        public void onSuccess(T value) {
            if (SingleOnSubscribeUsing.this.disposeEagerly) {
                try {
                    SingleOnSubscribeUsing.this.disposeAction.call(this.val$resource);
                } catch (Throwable ex) {
                    Exceptions.throwIfFatal(ex);
                    this.val$child.onError(ex);
                    return;
                }
            }
            this.val$child.onSuccess(value);
            if (!SingleOnSubscribeUsing.this.disposeEagerly) {
                try {
                    SingleOnSubscribeUsing.this.disposeAction.call(this.val$resource);
                } catch (Throwable ex2) {
                    Exceptions.throwIfFatal(ex2);
                    RxJavaPlugins.getInstance().getErrorHandler().handleError(ex2);
                }
            }
        }

        public void onError(Throwable error) {
            SingleOnSubscribeUsing.this.handleSubscriptionTimeError(this.val$child, this.val$resource, error);
        }
    }

    public SingleOnSubscribeUsing(Func0<Resource> resourceFactory, Func1<? super Resource, ? extends Single<? extends T>> observableFactory, Action1<? super Resource> disposeAction, boolean disposeEagerly) {
        this.resourceFactory = resourceFactory;
        this.singleFactory = observableFactory;
        this.disposeAction = disposeAction;
        this.disposeEagerly = disposeEagerly;
    }

    public void call(SingleSubscriber<? super T> child) {
        try {
            Resource resource = this.resourceFactory.call();
            try {
                Single<? extends T> single = (Single) this.singleFactory.call(resource);
                if (single == null) {
                    handleSubscriptionTimeError(child, resource, new NullPointerException("The single"));
                    return;
                }
                SingleSubscriber parent = new C14591(resource, child);
                child.add(parent);
                single.subscribe(parent);
            } catch (Throwable ex) {
                handleSubscriptionTimeError(child, resource, ex);
            }
        } catch (Throwable ex2) {
            Exceptions.throwIfFatal(ex2);
            child.onError(ex2);
        }
    }

    void handleSubscriptionTimeError(SingleSubscriber<? super T> t, Resource resource, Throwable ex) {
        Exceptions.throwIfFatal(ex);
        if (this.disposeEagerly) {
            try {
                this.disposeAction.call(resource);
            } catch (Throwable ex2) {
                Exceptions.throwIfFatal(ex2);
                ex = new CompositeException(Arrays.asList(new Throwable[]{ex, ex2}));
            }
        }
        t.onError(ex);
        if (!this.disposeEagerly) {
            try {
                this.disposeAction.call(resource);
            } catch (Throwable ex22) {
                Exceptions.throwIfFatal(ex22);
                RxJavaPlugins.getInstance().getErrorHandler().handleError(ex22);
            }
        }
    }
}
