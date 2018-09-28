package rx;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import rx.Scheduler.Worker;
import rx.Single.OnSubscribe;
import rx.annotations.Experimental;
import rx.exceptions.CompositeException;
import rx.exceptions.Exceptions;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Actions;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.internal.operators.CompletableOnSubscribeConcat;
import rx.internal.operators.CompletableOnSubscribeConcatArray;
import rx.internal.operators.CompletableOnSubscribeConcatIterable;
import rx.internal.operators.CompletableOnSubscribeMerge;
import rx.internal.operators.CompletableOnSubscribeMergeArray;
import rx.internal.operators.CompletableOnSubscribeMergeDelayErrorArray;
import rx.internal.operators.CompletableOnSubscribeMergeDelayErrorIterable;
import rx.internal.operators.CompletableOnSubscribeMergeIterable;
import rx.internal.operators.CompletableOnSubscribeTimeout;
import rx.internal.util.SubscriptionList;
import rx.internal.util.UtilityFunctions;
import rx.plugins.RxJavaErrorHandler;
import rx.plugins.RxJavaPlugins;
import rx.schedulers.Schedulers;
import rx.subscriptions.BooleanSubscription;
import rx.subscriptions.CompositeSubscription;
import rx.subscriptions.MultipleAssignmentSubscription;
import rx.subscriptions.SerialSubscription;
import rx.subscriptions.Subscriptions;

@Experimental
public class Completable {
    static final Completable COMPLETE;
    static final RxJavaErrorHandler ERROR_HANDLER;
    static final Completable NEVER;
    private final CompletableOnSubscribe onSubscribe;

    public interface CompletableSubscriber {
        void onCompleted();

        void onError(Throwable th);

        void onSubscribe(Subscription subscription);
    }

    /* renamed from: rx.Completable.14 */
    class AnonymousClass14 implements CompletableSubscriber {
        final /* synthetic */ CountDownLatch val$cdl;
        final /* synthetic */ Throwable[] val$err;

        AnonymousClass14(CountDownLatch countDownLatch, Throwable[] thArr) {
            this.val$cdl = countDownLatch;
            this.val$err = thArr;
        }

        public void onCompleted() {
            this.val$cdl.countDown();
        }

        public void onError(Throwable e) {
            this.val$err[0] = e;
            this.val$cdl.countDown();
        }

        public void onSubscribe(Subscription d) {
        }
    }

    /* renamed from: rx.Completable.15 */
    class AnonymousClass15 implements CompletableSubscriber {
        final /* synthetic */ CountDownLatch val$cdl;
        final /* synthetic */ Throwable[] val$err;

        AnonymousClass15(CountDownLatch countDownLatch, Throwable[] thArr) {
            this.val$cdl = countDownLatch;
            this.val$err = thArr;
        }

        public void onCompleted() {
            this.val$cdl.countDown();
        }

        public void onError(Throwable e) {
            this.val$err[0] = e;
            this.val$cdl.countDown();
        }

        public void onSubscribe(Subscription d) {
        }
    }

    /* renamed from: rx.Completable.19 */
    class AnonymousClass19 implements CompletableSubscriber {
        final /* synthetic */ CountDownLatch val$cdl;
        final /* synthetic */ Throwable[] val$err;

        AnonymousClass19(CountDownLatch countDownLatch, Throwable[] thArr) {
            this.val$cdl = countDownLatch;
            this.val$err = thArr;
        }

        public void onCompleted() {
            this.val$cdl.countDown();
        }

        public void onError(Throwable e) {
            this.val$err[0] = e;
            this.val$cdl.countDown();
        }

        public void onSubscribe(Subscription d) {
        }
    }

    /* renamed from: rx.Completable.20 */
    class AnonymousClass20 implements CompletableSubscriber {
        final /* synthetic */ CountDownLatch val$cdl;
        final /* synthetic */ Throwable[] val$err;

        AnonymousClass20(CountDownLatch countDownLatch, Throwable[] thArr) {
            this.val$cdl = countDownLatch;
            this.val$err = thArr;
        }

        public void onCompleted() {
            this.val$cdl.countDown();
        }

        public void onError(Throwable e) {
            this.val$err[0] = e;
            this.val$cdl.countDown();
        }

        public void onSubscribe(Subscription d) {
        }
    }

    /* renamed from: rx.Completable.25 */
    class AnonymousClass25 implements CompletableSubscriber {
        final /* synthetic */ MultipleAssignmentSubscription val$mad;

        AnonymousClass25(MultipleAssignmentSubscription multipleAssignmentSubscription) {
            this.val$mad = multipleAssignmentSubscription;
        }

        public void onCompleted() {
            this.val$mad.unsubscribe();
        }

        public void onError(Throwable e) {
            Completable.ERROR_HANDLER.handleError(e);
            this.val$mad.unsubscribe();
            Completable.deliverUncaughtException(e);
        }

        public void onSubscribe(Subscription d) {
            this.val$mad.set(d);
        }
    }

    /* renamed from: rx.Completable.26 */
    class AnonymousClass26 implements CompletableSubscriber {
        final /* synthetic */ MultipleAssignmentSubscription val$mad;
        final /* synthetic */ Action0 val$onComplete;

        AnonymousClass26(Action0 action0, MultipleAssignmentSubscription multipleAssignmentSubscription) {
            this.val$onComplete = action0;
            this.val$mad = multipleAssignmentSubscription;
        }

        public void onCompleted() {
            try {
                this.val$onComplete.call();
            } catch (Throwable e) {
                Completable.ERROR_HANDLER.handleError(e);
                Completable.deliverUncaughtException(e);
            } finally {
                this.val$mad.unsubscribe();
            }
        }

        public void onError(Throwable e) {
            Completable.ERROR_HANDLER.handleError(e);
            this.val$mad.unsubscribe();
            Completable.deliverUncaughtException(e);
        }

        public void onSubscribe(Subscription d) {
            this.val$mad.set(d);
        }
    }

    /* renamed from: rx.Completable.27 */
    class AnonymousClass27 implements CompletableSubscriber {
        final /* synthetic */ MultipleAssignmentSubscription val$mad;
        final /* synthetic */ Action0 val$onComplete;
        final /* synthetic */ Action1 val$onError;

        AnonymousClass27(Action0 action0, MultipleAssignmentSubscription multipleAssignmentSubscription, Action1 action1) {
            this.val$onComplete = action0;
            this.val$mad = multipleAssignmentSubscription;
            this.val$onError = action1;
        }

        public void onCompleted() {
            try {
                this.val$onComplete.call();
                this.val$mad.unsubscribe();
            } catch (Throwable e) {
                onError(e);
            }
        }

        public void onError(Throwable e) {
            Throwable e2;
            Throwable th;
            try {
                this.val$onError.call(e);
                this.val$mad.unsubscribe();
            } catch (Throwable th2) {
                th = th2;
                e = e2;
                this.val$mad.unsubscribe();
                throw th;
            }
        }

        public void onSubscribe(Subscription d) {
            this.val$mad.set(d);
        }
    }

    /* renamed from: rx.Completable.28 */
    class AnonymousClass28 implements CompletableSubscriber {
        final /* synthetic */ Subscriber val$sw;

        AnonymousClass28(Subscriber subscriber) {
            this.val$sw = subscriber;
        }

        public void onCompleted() {
            this.val$sw.onCompleted();
        }

        public void onError(Throwable e) {
            this.val$sw.onError(e);
        }

        public void onSubscribe(Subscription d) {
            this.val$sw.add(d);
        }
    }

    /* renamed from: rx.Completable.32 */
    class AnonymousClass32 implements Func0<T> {
        final /* synthetic */ Object val$completionValue;

        AnonymousClass32(Object obj) {
            this.val$completionValue = obj;
        }

        public T call() {
            return this.val$completionValue;
        }
    }

    public interface CompletableOperator extends Func1<CompletableSubscriber, CompletableSubscriber> {
    }

    public interface CompletableTransformer extends Func1<Completable, Completable> {
    }

    /* renamed from: rx.Completable.18 */
    class AnonymousClass18 implements Action1<Throwable> {
        final /* synthetic */ Action0 val$onTerminate;

        AnonymousClass18(Action0 action0) {
            this.val$onTerminate = action0;
        }

        public void call(Throwable e) {
            this.val$onTerminate.call();
        }
    }

    public interface CompletableOnSubscribe extends Action1<CompletableSubscriber> {
    }

    /* renamed from: rx.Completable.10 */
    static class AnonymousClass10 implements CompletableOnSubscribe {
        final /* synthetic */ Observable val$flowable;

        /* renamed from: rx.Completable.10.1 */
        class C13401 extends Subscriber<Object> {
            final /* synthetic */ CompletableSubscriber val$cs;

            C13401(CompletableSubscriber completableSubscriber) {
                this.val$cs = completableSubscriber;
            }

            public void onCompleted() {
                this.val$cs.onCompleted();
            }

            public void onError(Throwable t) {
                this.val$cs.onError(t);
            }

            public void onNext(Object t) {
            }
        }

        AnonymousClass10(Observable observable) {
            this.val$flowable = observable;
        }

        public void call(CompletableSubscriber cs) {
            Subscriber<Object> subscriber = new C13401(cs);
            cs.onSubscribe(subscriber);
            this.val$flowable.unsafeSubscribe(subscriber);
        }
    }

    /* renamed from: rx.Completable.11 */
    static class AnonymousClass11 implements CompletableOnSubscribe {
        final /* synthetic */ Single val$single;

        /* renamed from: rx.Completable.11.1 */
        class C13411 extends SingleSubscriber<Object> {
            final /* synthetic */ CompletableSubscriber val$s;

            C13411(CompletableSubscriber completableSubscriber) {
                this.val$s = completableSubscriber;
            }

            public void onError(Throwable e) {
                this.val$s.onError(e);
            }

            public void onSuccess(Object value) {
                this.val$s.onCompleted();
            }
        }

        AnonymousClass11(Single single) {
            this.val$single = single;
        }

        public void call(CompletableSubscriber s) {
            SingleSubscriber te = new C13411(s);
            s.onSubscribe(te);
            this.val$single.subscribe(te);
        }
    }

    /* renamed from: rx.Completable.12 */
    static class AnonymousClass12 implements CompletableOnSubscribe {
        final /* synthetic */ long val$delay;
        final /* synthetic */ Scheduler val$scheduler;
        final /* synthetic */ TimeUnit val$unit;

        /* renamed from: rx.Completable.12.1 */
        class C14981 implements Action0 {
            final /* synthetic */ CompletableSubscriber val$s;
            final /* synthetic */ Worker val$w;

            C14981(CompletableSubscriber completableSubscriber, Worker worker) {
                this.val$s = completableSubscriber;
                this.val$w = worker;
            }

            public void call() {
                try {
                    this.val$s.onCompleted();
                } finally {
                    this.val$w.unsubscribe();
                }
            }
        }

        AnonymousClass12(Scheduler scheduler, long j, TimeUnit timeUnit) {
            this.val$scheduler = scheduler;
            this.val$delay = j;
            this.val$unit = timeUnit;
        }

        public void call(CompletableSubscriber s) {
            MultipleAssignmentSubscription mad = new MultipleAssignmentSubscription();
            s.onSubscribe(mad);
            if (!mad.isUnsubscribed()) {
                Worker w = this.val$scheduler.createWorker();
                mad.set(w);
                w.schedule(new C14981(s, w), this.val$delay, this.val$unit);
            }
        }
    }

    /* renamed from: rx.Completable.13 */
    static class AnonymousClass13 implements CompletableOnSubscribe {
        final /* synthetic */ Func1 val$completableFunc1;
        final /* synthetic */ Action1 val$disposer;
        final /* synthetic */ boolean val$eager;
        final /* synthetic */ Func0 val$resourceFunc0;

        /* renamed from: rx.Completable.13.1 */
        class C12391 implements CompletableSubscriber {
            Subscription f30d;
            final /* synthetic */ AtomicBoolean val$once;
            final /* synthetic */ Object val$resource;
            final /* synthetic */ CompletableSubscriber val$s;

            /* renamed from: rx.Completable.13.1.1 */
            class C14991 implements Action0 {
                C14991() {
                }

                public void call() {
                    C12391.this.dispose();
                }
            }

            C12391(AtomicBoolean atomicBoolean, Object obj, CompletableSubscriber completableSubscriber) {
                this.val$once = atomicBoolean;
                this.val$resource = obj;
                this.val$s = completableSubscriber;
            }

            void dispose() {
                this.f30d.unsubscribe();
                if (this.val$once.compareAndSet(false, true)) {
                    try {
                        AnonymousClass13.this.val$disposer.call(this.val$resource);
                    } catch (Throwable ex) {
                        Completable.ERROR_HANDLER.handleError(ex);
                    }
                }
            }

            public void onCompleted() {
                if (AnonymousClass13.this.val$eager && this.val$once.compareAndSet(false, true)) {
                    try {
                        AnonymousClass13.this.val$disposer.call(this.val$resource);
                    } catch (Throwable ex) {
                        this.val$s.onError(ex);
                        return;
                    }
                }
                this.val$s.onCompleted();
                if (!AnonymousClass13.this.val$eager) {
                    dispose();
                }
            }

            public void onError(Throwable e) {
                if (AnonymousClass13.this.val$eager && this.val$once.compareAndSet(false, true)) {
                    try {
                        AnonymousClass13.this.val$disposer.call(this.val$resource);
                    } catch (Throwable ex) {
                        e = new CompositeException(Arrays.asList(new Throwable[]{e, ex}));
                    }
                }
                this.val$s.onError(e);
                if (!AnonymousClass13.this.val$eager) {
                    dispose();
                }
            }

            public void onSubscribe(Subscription d) {
                this.f30d = d;
                this.val$s.onSubscribe(Subscriptions.create(new C14991()));
            }
        }

        AnonymousClass13(Func0 func0, Func1 func1, Action1 action1, boolean z) {
            this.val$resourceFunc0 = func0;
            this.val$completableFunc1 = func1;
            this.val$disposer = action1;
            this.val$eager = z;
        }

        public void call(CompletableSubscriber s) {
            try {
                R resource = this.val$resourceFunc0.call();
                try {
                    Completable cs = (Completable) this.val$completableFunc1.call(resource);
                    if (cs == null) {
                        try {
                            this.val$disposer.call(resource);
                            s.onSubscribe(Subscriptions.unsubscribed());
                            s.onError(new NullPointerException("The completable supplied is null"));
                            return;
                        } catch (Throwable ex) {
                            Exceptions.throwIfFatal(ex);
                            s.onSubscribe(Subscriptions.unsubscribed());
                            s.onError(new CompositeException(Arrays.asList(new Throwable[]{new NullPointerException("The completable supplied is null"), ex})));
                            return;
                        }
                    }
                    cs.subscribe(new C12391(new AtomicBoolean(), resource, s));
                } catch (Throwable ex2) {
                    Exceptions.throwIfFatal(e);
                    Exceptions.throwIfFatal(ex2);
                    s.onSubscribe(Subscriptions.unsubscribed());
                    s.onError(new CompositeException(Arrays.asList(new Throwable[]{e, ex2})));
                }
            } catch (Throwable e) {
                s.onSubscribe(Subscriptions.unsubscribed());
                s.onError(e);
            }
        }
    }

    /* renamed from: rx.Completable.16 */
    class AnonymousClass16 implements CompletableOnSubscribe {
        final /* synthetic */ long val$delay;
        final /* synthetic */ boolean val$delayError;
        final /* synthetic */ Scheduler val$scheduler;
        final /* synthetic */ TimeUnit val$unit;

        /* renamed from: rx.Completable.16.1 */
        class C12401 implements CompletableSubscriber {
            final /* synthetic */ CompletableSubscriber val$s;
            final /* synthetic */ CompositeSubscription val$set;
            final /* synthetic */ Worker val$w;

            /* renamed from: rx.Completable.16.1.1 */
            class C15001 implements Action0 {
                C15001() {
                }

                public void call() {
                    try {
                        C12401.this.val$s.onCompleted();
                    } finally {
                        C12401.this.val$w.unsubscribe();
                    }
                }
            }

            /* renamed from: rx.Completable.16.1.2 */
            class C15012 implements Action0 {
                final /* synthetic */ Throwable val$e;

                C15012(Throwable th) {
                    this.val$e = th;
                }

                public void call() {
                    try {
                        C12401.this.val$s.onError(this.val$e);
                    } finally {
                        C12401.this.val$w.unsubscribe();
                    }
                }
            }

            C12401(CompositeSubscription compositeSubscription, Worker worker, CompletableSubscriber completableSubscriber) {
                this.val$set = compositeSubscription;
                this.val$w = worker;
                this.val$s = completableSubscriber;
            }

            public void onCompleted() {
                this.val$set.add(this.val$w.schedule(new C15001(), AnonymousClass16.this.val$delay, AnonymousClass16.this.val$unit));
            }

            public void onError(Throwable e) {
                if (AnonymousClass16.this.val$delayError) {
                    this.val$set.add(this.val$w.schedule(new C15012(e), AnonymousClass16.this.val$delay, AnonymousClass16.this.val$unit));
                } else {
                    this.val$s.onError(e);
                }
            }

            public void onSubscribe(Subscription d) {
                this.val$set.add(d);
                this.val$s.onSubscribe(this.val$set);
            }
        }

        AnonymousClass16(Scheduler scheduler, long j, TimeUnit timeUnit, boolean z) {
            this.val$scheduler = scheduler;
            this.val$delay = j;
            this.val$unit = timeUnit;
            this.val$delayError = z;
        }

        public void call(CompletableSubscriber s) {
            CompositeSubscription set = new CompositeSubscription();
            Worker w = this.val$scheduler.createWorker();
            set.add(w);
            Completable.this.subscribe(new C12401(set, w, s));
        }
    }

    /* renamed from: rx.Completable.17 */
    class AnonymousClass17 implements CompletableOnSubscribe {
        final /* synthetic */ Action0 val$onAfterComplete;
        final /* synthetic */ Action0 val$onComplete;
        final /* synthetic */ Action1 val$onError;
        final /* synthetic */ Action1 val$onSubscribe;
        final /* synthetic */ Action0 val$onUnsubscribe;

        /* renamed from: rx.Completable.17.1 */
        class C12411 implements CompletableSubscriber {
            final /* synthetic */ CompletableSubscriber val$s;

            /* renamed from: rx.Completable.17.1.1 */
            class C15021 implements Action0 {
                final /* synthetic */ Subscription val$d;

                C15021(Subscription subscription) {
                    this.val$d = subscription;
                }

                public void call() {
                    try {
                        AnonymousClass17.this.val$onUnsubscribe.call();
                    } catch (Throwable e) {
                        Completable.ERROR_HANDLER.handleError(e);
                    }
                    this.val$d.unsubscribe();
                }
            }

            C12411(CompletableSubscriber completableSubscriber) {
                this.val$s = completableSubscriber;
            }

            public void onCompleted() {
                try {
                    AnonymousClass17.this.val$onComplete.call();
                    this.val$s.onCompleted();
                    try {
                        AnonymousClass17.this.val$onAfterComplete.call();
                    } catch (Throwable e) {
                        Completable.ERROR_HANDLER.handleError(e);
                    }
                } catch (Throwable e2) {
                    this.val$s.onError(e2);
                }
            }

            public void onError(Throwable e) {
                try {
                    AnonymousClass17.this.val$onError.call(e);
                } catch (Throwable ex) {
                    e = new CompositeException(Arrays.asList(new Throwable[]{e, ex}));
                }
                this.val$s.onError(e);
            }

            public void onSubscribe(Subscription d) {
                try {
                    AnonymousClass17.this.val$onSubscribe.call(d);
                    this.val$s.onSubscribe(Subscriptions.create(new C15021(d)));
                } catch (Throwable ex) {
                    d.unsubscribe();
                    this.val$s.onSubscribe(Subscriptions.unsubscribed());
                    this.val$s.onError(ex);
                }
            }
        }

        AnonymousClass17(Action0 action0, Action0 action02, Action1 action1, Action1 action12, Action0 action03) {
            this.val$onComplete = action0;
            this.val$onAfterComplete = action02;
            this.val$onError = action1;
            this.val$onSubscribe = action12;
            this.val$onUnsubscribe = action03;
        }

        public void call(CompletableSubscriber s) {
            Completable.this.subscribe(new C12411(s));
        }
    }

    /* renamed from: rx.Completable.1 */
    static class C15941 implements CompletableOnSubscribe {
        C15941() {
        }

        public void call(CompletableSubscriber s) {
            s.onSubscribe(Subscriptions.unsubscribed());
            s.onCompleted();
        }
    }

    /* renamed from: rx.Completable.21 */
    class AnonymousClass21 implements CompletableOnSubscribe {
        final /* synthetic */ CompletableOperator val$onLift;

        AnonymousClass21(CompletableOperator completableOperator) {
            this.val$onLift = completableOperator;
        }

        public void call(CompletableSubscriber s) {
            try {
                Completable.this.subscribe((CompletableSubscriber) this.val$onLift.call(s));
            } catch (NullPointerException ex) {
                throw ex;
            } catch (Throwable ex2) {
                NullPointerException toNpe = Completable.toNpe(ex2);
            }
        }
    }

    /* renamed from: rx.Completable.22 */
    class AnonymousClass22 implements CompletableOnSubscribe {
        final /* synthetic */ Scheduler val$scheduler;

        /* renamed from: rx.Completable.22.1 */
        class C12421 implements CompletableSubscriber {
            final /* synthetic */ SubscriptionList val$ad;
            final /* synthetic */ CompletableSubscriber val$s;
            final /* synthetic */ Worker val$w;

            /* renamed from: rx.Completable.22.1.1 */
            class C15031 implements Action0 {
                C15031() {
                }

                public void call() {
                    try {
                        C12421.this.val$s.onCompleted();
                    } finally {
                        C12421.this.val$ad.unsubscribe();
                    }
                }
            }

            /* renamed from: rx.Completable.22.1.2 */
            class C15042 implements Action0 {
                final /* synthetic */ Throwable val$e;

                C15042(Throwable th) {
                    this.val$e = th;
                }

                public void call() {
                    try {
                        C12421.this.val$s.onError(this.val$e);
                    } finally {
                        C12421.this.val$ad.unsubscribe();
                    }
                }
            }

            C12421(Worker worker, CompletableSubscriber completableSubscriber, SubscriptionList subscriptionList) {
                this.val$w = worker;
                this.val$s = completableSubscriber;
                this.val$ad = subscriptionList;
            }

            public void onCompleted() {
                this.val$w.schedule(new C15031());
            }

            public void onError(Throwable e) {
                this.val$w.schedule(new C15042(e));
            }

            public void onSubscribe(Subscription d) {
                this.val$ad.add(d);
            }
        }

        AnonymousClass22(Scheduler scheduler) {
            this.val$scheduler = scheduler;
        }

        public void call(CompletableSubscriber s) {
            SubscriptionList ad = new SubscriptionList();
            Worker w = this.val$scheduler.createWorker();
            ad.add(w);
            s.onSubscribe(ad);
            Completable.this.subscribe(new C12421(w, s, ad));
        }
    }

    /* renamed from: rx.Completable.23 */
    class AnonymousClass23 implements CompletableOnSubscribe {
        final /* synthetic */ Func1 val$predicate;

        /* renamed from: rx.Completable.23.1 */
        class C12431 implements CompletableSubscriber {
            final /* synthetic */ CompletableSubscriber val$s;

            C12431(CompletableSubscriber completableSubscriber) {
                this.val$s = completableSubscriber;
            }

            public void onCompleted() {
                this.val$s.onCompleted();
            }

            public void onError(Throwable e) {
                try {
                    if (((Boolean) AnonymousClass23.this.val$predicate.call(e)).booleanValue()) {
                        this.val$s.onCompleted();
                    } else {
                        this.val$s.onError(e);
                    }
                } catch (Throwable ex) {
                    e = new CompositeException(Arrays.asList(new Throwable[]{e, ex}));
                }
            }

            public void onSubscribe(Subscription d) {
                this.val$s.onSubscribe(d);
            }
        }

        AnonymousClass23(Func1 func1) {
            this.val$predicate = func1;
        }

        public void call(CompletableSubscriber s) {
            Completable.this.subscribe(new C12431(s));
        }
    }

    /* renamed from: rx.Completable.24 */
    class AnonymousClass24 implements CompletableOnSubscribe {
        final /* synthetic */ Func1 val$errorMapper;

        /* renamed from: rx.Completable.24.1 */
        class C12451 implements CompletableSubscriber {
            final /* synthetic */ CompletableSubscriber val$s;
            final /* synthetic */ SerialSubscription val$sd;

            /* renamed from: rx.Completable.24.1.1 */
            class C12441 implements CompletableSubscriber {
                C12441() {
                }

                public void onCompleted() {
                    C12451.this.val$s.onCompleted();
                }

                public void onError(Throwable e) {
                    C12451.this.val$s.onError(e);
                }

                public void onSubscribe(Subscription d) {
                    C12451.this.val$sd.set(d);
                }
            }

            C12451(CompletableSubscriber completableSubscriber, SerialSubscription serialSubscription) {
                this.val$s = completableSubscriber;
                this.val$sd = serialSubscription;
            }

            public void onCompleted() {
                this.val$s.onCompleted();
            }

            public void onError(Throwable e) {
                Throwable e2;
                try {
                    Completable c = (Completable) AnonymousClass24.this.val$errorMapper.call(e);
                    if (c == null) {
                        NullPointerException npe = new NullPointerException("The completable returned is null");
                        e2 = new CompositeException(Arrays.asList(new Throwable[]{e, npe}));
                        this.val$s.onError(e2);
                        e = e2;
                        return;
                    }
                    c.subscribe(new C12441());
                } catch (Throwable ex) {
                    e2 = new CompositeException(Arrays.asList(new Throwable[]{e, ex}));
                    this.val$s.onError(e2);
                    e = e2;
                }
            }

            public void onSubscribe(Subscription d) {
                this.val$sd.set(d);
            }
        }

        AnonymousClass24(Func1 func1) {
            this.val$errorMapper = func1;
        }

        public void call(CompletableSubscriber s) {
            Completable.this.subscribe(new C12451(s, new SerialSubscription()));
        }
    }

    /* renamed from: rx.Completable.29 */
    class AnonymousClass29 implements CompletableOnSubscribe {
        final /* synthetic */ Scheduler val$scheduler;

        /* renamed from: rx.Completable.29.1 */
        class C15051 implements Action0 {
            final /* synthetic */ CompletableSubscriber val$s;
            final /* synthetic */ Worker val$w;

            C15051(CompletableSubscriber completableSubscriber, Worker worker) {
                this.val$s = completableSubscriber;
                this.val$w = worker;
            }

            public void call() {
                try {
                    Completable.this.subscribe(this.val$s);
                } finally {
                    this.val$w.unsubscribe();
                }
            }
        }

        AnonymousClass29(Scheduler scheduler) {
            this.val$scheduler = scheduler;
        }

        public void call(CompletableSubscriber s) {
            Worker w = this.val$scheduler.createWorker();
            w.schedule(new C15051(s, w));
        }
    }

    /* renamed from: rx.Completable.2 */
    static class C15952 implements CompletableOnSubscribe {
        C15952() {
        }

        public void call(CompletableSubscriber s) {
            s.onSubscribe(Subscriptions.unsubscribed());
        }
    }

    /* renamed from: rx.Completable.31 */
    class AnonymousClass31 implements OnSubscribe<T> {
        final /* synthetic */ Func0 val$completionValueFunc0;

        /* renamed from: rx.Completable.31.1 */
        class C12471 implements CompletableSubscriber {
            final /* synthetic */ SingleSubscriber val$s;

            C12471(SingleSubscriber singleSubscriber) {
                this.val$s = singleSubscriber;
            }

            public void onCompleted() {
                try {
                    T v = AnonymousClass31.this.val$completionValueFunc0.call();
                    if (v == null) {
                        this.val$s.onError(new NullPointerException("The value supplied is null"));
                    } else {
                        this.val$s.onSuccess(v);
                    }
                } catch (Throwable e) {
                    this.val$s.onError(e);
                }
            }

            public void onError(Throwable e) {
                this.val$s.onError(e);
            }

            public void onSubscribe(Subscription d) {
                this.val$s.add(d);
            }
        }

        AnonymousClass31(Func0 func0) {
            this.val$completionValueFunc0 = func0;
        }

        public void call(SingleSubscriber<? super T> s) {
            Completable.this.subscribe(new C12471(s));
        }
    }

    /* renamed from: rx.Completable.33 */
    class AnonymousClass33 implements CompletableOnSubscribe {
        final /* synthetic */ Scheduler val$scheduler;

        /* renamed from: rx.Completable.33.1 */
        class C12481 implements CompletableSubscriber {
            final /* synthetic */ CompletableSubscriber val$s;

            /* renamed from: rx.Completable.33.1.1 */
            class C15071 implements Action0 {
                final /* synthetic */ Subscription val$d;

                /* renamed from: rx.Completable.33.1.1.1 */
                class C15061 implements Action0 {
                    final /* synthetic */ Worker val$w;

                    C15061(Worker worker) {
                        this.val$w = worker;
                    }

                    public void call() {
                        try {
                            C15071.this.val$d.unsubscribe();
                        } finally {
                            this.val$w.unsubscribe();
                        }
                    }
                }

                C15071(Subscription subscription) {
                    this.val$d = subscription;
                }

                public void call() {
                    Worker w = AnonymousClass33.this.val$scheduler.createWorker();
                    w.schedule(new C15061(w));
                }
            }

            C12481(CompletableSubscriber completableSubscriber) {
                this.val$s = completableSubscriber;
            }

            public void onCompleted() {
                this.val$s.onCompleted();
            }

            public void onError(Throwable e) {
                this.val$s.onError(e);
            }

            public void onSubscribe(Subscription d) {
                this.val$s.onSubscribe(Subscriptions.create(new C15071(d)));
            }
        }

        AnonymousClass33(Scheduler scheduler) {
            this.val$scheduler = scheduler;
        }

        public void call(CompletableSubscriber s) {
            Completable.this.subscribe(new C12481(s));
        }
    }

    /* renamed from: rx.Completable.3 */
    static class C15963 implements CompletableOnSubscribe {
        final /* synthetic */ Completable[] val$sources;

        /* renamed from: rx.Completable.3.1 */
        class C12461 implements CompletableSubscriber {
            final /* synthetic */ AtomicBoolean val$once;
            final /* synthetic */ CompletableSubscriber val$s;
            final /* synthetic */ CompositeSubscription val$set;

            C12461(AtomicBoolean atomicBoolean, CompositeSubscription compositeSubscription, CompletableSubscriber completableSubscriber) {
                this.val$once = atomicBoolean;
                this.val$set = compositeSubscription;
                this.val$s = completableSubscriber;
            }

            public void onCompleted() {
                if (this.val$once.compareAndSet(false, true)) {
                    this.val$set.unsubscribe();
                    this.val$s.onCompleted();
                }
            }

            public void onError(Throwable e) {
                if (this.val$once.compareAndSet(false, true)) {
                    this.val$set.unsubscribe();
                    this.val$s.onError(e);
                    return;
                }
                Completable.ERROR_HANDLER.handleError(e);
            }

            public void onSubscribe(Subscription d) {
                this.val$set.add(d);
            }
        }

        C15963(Completable[] completableArr) {
            this.val$sources = completableArr;
        }

        public void call(CompletableSubscriber s) {
            CompositeSubscription set = new CompositeSubscription();
            s.onSubscribe(set);
            AtomicBoolean once = new AtomicBoolean();
            CompletableSubscriber inner = new C12461(once, set, s);
            Completable[] arr$ = this.val$sources;
            int len$ = arr$.length;
            int i$ = 0;
            while (i$ < len$) {
                Completable c = arr$[i$];
                if (!set.isUnsubscribed()) {
                    if (c == null) {
                        NullPointerException npe = new NullPointerException("One of the sources is null");
                        if (once.compareAndSet(false, true)) {
                            set.unsubscribe();
                            s.onError(npe);
                            return;
                        }
                        Completable.ERROR_HANDLER.handleError(npe);
                        return;
                    } else if (!once.get() && !set.isUnsubscribed()) {
                        c.subscribe(inner);
                        i$++;
                    } else {
                        return;
                    }
                }
                return;
            }
        }
    }

    /* renamed from: rx.Completable.4 */
    static class C15974 implements CompletableOnSubscribe {
        final /* synthetic */ Iterable val$sources;

        /* renamed from: rx.Completable.4.1 */
        class C12491 implements CompletableSubscriber {
            final /* synthetic */ AtomicBoolean val$once;
            final /* synthetic */ CompletableSubscriber val$s;
            final /* synthetic */ CompositeSubscription val$set;

            C12491(AtomicBoolean atomicBoolean, CompositeSubscription compositeSubscription, CompletableSubscriber completableSubscriber) {
                this.val$once = atomicBoolean;
                this.val$set = compositeSubscription;
                this.val$s = completableSubscriber;
            }

            public void onCompleted() {
                if (this.val$once.compareAndSet(false, true)) {
                    this.val$set.unsubscribe();
                    this.val$s.onCompleted();
                }
            }

            public void onError(Throwable e) {
                if (this.val$once.compareAndSet(false, true)) {
                    this.val$set.unsubscribe();
                    this.val$s.onError(e);
                    return;
                }
                Completable.ERROR_HANDLER.handleError(e);
            }

            public void onSubscribe(Subscription d) {
                this.val$set.add(d);
            }
        }

        C15974(Iterable iterable) {
            this.val$sources = iterable;
        }

        public void call(CompletableSubscriber s) {
            CompositeSubscription set = new CompositeSubscription();
            s.onSubscribe(set);
            AtomicBoolean once = new AtomicBoolean();
            CompletableSubscriber inner = new C12491(once, set, s);
            try {
                Iterator<? extends Completable> it = this.val$sources.iterator();
                if (it == null) {
                    s.onError(new NullPointerException("The iterator returned is null"));
                    return;
                }
                boolean empty = true;
                while (!once.get() && !set.isUnsubscribed()) {
                    try {
                        if (it.hasNext()) {
                            empty = false;
                            if (!once.get() && !set.isUnsubscribed()) {
                                try {
                                    Completable c = (Completable) it.next();
                                    if (c == null) {
                                        NullPointerException npe = new NullPointerException("One of the sources is null");
                                        if (once.compareAndSet(false, true)) {
                                            set.unsubscribe();
                                            s.onError(npe);
                                            return;
                                        }
                                        Completable.ERROR_HANDLER.handleError(npe);
                                        return;
                                    } else if (!once.get() && !set.isUnsubscribed()) {
                                        c.subscribe(inner);
                                    } else {
                                        return;
                                    }
                                } catch (Throwable e) {
                                    if (once.compareAndSet(false, true)) {
                                        set.unsubscribe();
                                        s.onError(e);
                                        return;
                                    }
                                    Completable.ERROR_HANDLER.handleError(e);
                                    return;
                                }
                            }
                            return;
                        } else if (empty) {
                            s.onCompleted();
                            return;
                        } else {
                            return;
                        }
                    } catch (Throwable e2) {
                        if (once.compareAndSet(false, true)) {
                            set.unsubscribe();
                            s.onError(e2);
                            return;
                        }
                        Completable.ERROR_HANDLER.handleError(e2);
                        return;
                    }
                }
            } catch (Throwable e22) {
                s.onError(e22);
            }
        }
    }

    /* renamed from: rx.Completable.5 */
    static class C15985 implements CompletableOnSubscribe {
        final /* synthetic */ Func0 val$completableFunc0;

        C15985(Func0 func0) {
            this.val$completableFunc0 = func0;
        }

        public void call(CompletableSubscriber s) {
            try {
                Completable c = (Completable) this.val$completableFunc0.call();
                if (c == null) {
                    s.onSubscribe(Subscriptions.unsubscribed());
                    s.onError(new NullPointerException("The completable returned is null"));
                    return;
                }
                c.subscribe(s);
            } catch (Throwable e) {
                s.onSubscribe(Subscriptions.unsubscribed());
                s.onError(e);
            }
        }
    }

    /* renamed from: rx.Completable.6 */
    static class C15996 implements CompletableOnSubscribe {
        final /* synthetic */ Func0 val$errorFunc0;

        C15996(Func0 func0) {
            this.val$errorFunc0 = func0;
        }

        public void call(CompletableSubscriber s) {
            Throwable error;
            s.onSubscribe(Subscriptions.unsubscribed());
            try {
                error = (Throwable) this.val$errorFunc0.call();
            } catch (Throwable e) {
                error = e;
            }
            if (error == null) {
                error = new NullPointerException("The error supplied is null");
            }
            s.onError(error);
        }
    }

    /* renamed from: rx.Completable.7 */
    static class C16007 implements CompletableOnSubscribe {
        final /* synthetic */ Throwable val$error;

        C16007(Throwable th) {
            this.val$error = th;
        }

        public void call(CompletableSubscriber s) {
            s.onSubscribe(Subscriptions.unsubscribed());
            s.onError(this.val$error);
        }
    }

    /* renamed from: rx.Completable.8 */
    static class C16018 implements CompletableOnSubscribe {
        final /* synthetic */ Action0 val$action;

        C16018(Action0 action0) {
            this.val$action = action0;
        }

        public void call(CompletableSubscriber s) {
            BooleanSubscription bs = new BooleanSubscription();
            s.onSubscribe(bs);
            try {
                this.val$action.call();
                if (!bs.isUnsubscribed()) {
                    s.onCompleted();
                }
            } catch (Throwable e) {
                if (!bs.isUnsubscribed()) {
                    s.onError(e);
                }
            }
        }
    }

    /* renamed from: rx.Completable.9 */
    static class C16029 implements CompletableOnSubscribe {
        final /* synthetic */ Callable val$callable;

        C16029(Callable callable) {
            this.val$callable = callable;
        }

        public void call(CompletableSubscriber s) {
            BooleanSubscription bs = new BooleanSubscription();
            s.onSubscribe(bs);
            try {
                this.val$callable.call();
                if (!bs.isUnsubscribed()) {
                    s.onCompleted();
                }
            } catch (Throwable e) {
                if (!bs.isUnsubscribed()) {
                    s.onError(e);
                }
            }
        }
    }

    static {
        COMPLETE = create(new C15941());
        NEVER = create(new C15952());
        ERROR_HANDLER = RxJavaPlugins.getInstance().getErrorHandler();
    }

    public static Completable amb(Completable... sources) {
        requireNonNull(sources);
        if (sources.length == 0) {
            return complete();
        }
        if (sources.length == 1) {
            return sources[0];
        }
        return create(new C15963(sources));
    }

    public static Completable amb(Iterable<? extends Completable> sources) {
        requireNonNull(sources);
        return create(new C15974(sources));
    }

    public static Completable complete() {
        return COMPLETE;
    }

    public static Completable concat(Completable... sources) {
        requireNonNull(sources);
        if (sources.length == 0) {
            return complete();
        }
        if (sources.length == 1) {
            return sources[0];
        }
        return create(new CompletableOnSubscribeConcatArray(sources));
    }

    public static Completable concat(Iterable<? extends Completable> sources) {
        requireNonNull(sources);
        return create(new CompletableOnSubscribeConcatIterable(sources));
    }

    public static Completable concat(Observable<? extends Completable> sources) {
        return concat(sources, 2);
    }

    public static Completable concat(Observable<? extends Completable> sources, int prefetch) {
        requireNonNull(sources);
        if (prefetch >= 1) {
            return create(new CompletableOnSubscribeConcat(sources, prefetch));
        }
        throw new IllegalArgumentException("prefetch > 0 required but it was " + prefetch);
    }

    public static Completable create(CompletableOnSubscribe onSubscribe) {
        requireNonNull(onSubscribe);
        try {
            return new Completable(onSubscribe);
        } catch (NullPointerException ex) {
            throw ex;
        } catch (Throwable ex2) {
            ERROR_HANDLER.handleError(ex2);
            NullPointerException toNpe = toNpe(ex2);
        }
    }

    public static Completable defer(Func0<? extends Completable> completableFunc0) {
        requireNonNull(completableFunc0);
        return create(new C15985(completableFunc0));
    }

    public static Completable error(Func0<? extends Throwable> errorFunc0) {
        requireNonNull(errorFunc0);
        return create(new C15996(errorFunc0));
    }

    public static Completable error(Throwable error) {
        requireNonNull(error);
        return create(new C16007(error));
    }

    public static Completable fromAction(Action0 action) {
        requireNonNull(action);
        return create(new C16018(action));
    }

    public static Completable fromCallable(Callable<?> callable) {
        requireNonNull(callable);
        return create(new C16029(callable));
    }

    public static Completable fromFuture(Future<?> future) {
        requireNonNull(future);
        return fromObservable(Observable.from((Future) future));
    }

    public static Completable fromObservable(Observable<?> flowable) {
        requireNonNull(flowable);
        return create(new AnonymousClass10(flowable));
    }

    public static Completable fromSingle(Single<?> single) {
        requireNonNull(single);
        return create(new AnonymousClass11(single));
    }

    public static Completable merge(Completable... sources) {
        requireNonNull(sources);
        if (sources.length == 0) {
            return complete();
        }
        if (sources.length == 1) {
            return sources[0];
        }
        return create(new CompletableOnSubscribeMergeArray(sources));
    }

    public static Completable merge(Iterable<? extends Completable> sources) {
        requireNonNull(sources);
        return create(new CompletableOnSubscribeMergeIterable(sources));
    }

    public static Completable merge(Observable<? extends Completable> sources) {
        return merge0(sources, UrlImageViewHelper.CACHE_DURATION_INFINITE, false);
    }

    public static Completable merge(Observable<? extends Completable> sources, int maxConcurrency) {
        return merge0(sources, maxConcurrency, false);
    }

    protected static Completable merge0(Observable<? extends Completable> sources, int maxConcurrency, boolean delayErrors) {
        requireNonNull(sources);
        if (maxConcurrency >= 1) {
            return create(new CompletableOnSubscribeMerge(sources, maxConcurrency, delayErrors));
        }
        throw new IllegalArgumentException("maxConcurrency > 0 required but it was " + maxConcurrency);
    }

    public static Completable mergeDelayError(Completable... sources) {
        requireNonNull(sources);
        return create(new CompletableOnSubscribeMergeDelayErrorArray(sources));
    }

    public static Completable mergeDelayError(Iterable<? extends Completable> sources) {
        requireNonNull(sources);
        return create(new CompletableOnSubscribeMergeDelayErrorIterable(sources));
    }

    public static Completable mergeDelayError(Observable<? extends Completable> sources) {
        return merge0(sources, UrlImageViewHelper.CACHE_DURATION_INFINITE, true);
    }

    public static Completable mergeDelayError(Observable<? extends Completable> sources, int maxConcurrency) {
        return merge0(sources, maxConcurrency, true);
    }

    public static Completable never() {
        return NEVER;
    }

    static <T> T requireNonNull(T o) {
        if (o != null) {
            return o;
        }
        throw new NullPointerException();
    }

    public static Completable timer(long delay, TimeUnit unit) {
        return timer(delay, unit, Schedulers.computation());
    }

    public static Completable timer(long delay, TimeUnit unit, Scheduler scheduler) {
        requireNonNull(unit);
        requireNonNull(scheduler);
        return create(new AnonymousClass12(scheduler, delay, unit));
    }

    static NullPointerException toNpe(Throwable ex) {
        NullPointerException npe = new NullPointerException("Actually not, but can't pass out an exception otherwise...");
        npe.initCause(ex);
        return npe;
    }

    public static <R> Completable using(Func0<R> resourceFunc0, Func1<? super R, ? extends Completable> completableFunc1, Action1<? super R> disposer) {
        return using(resourceFunc0, completableFunc1, disposer, true);
    }

    public static <R> Completable using(Func0<R> resourceFunc0, Func1<? super R, ? extends Completable> completableFunc1, Action1<? super R> disposer, boolean eager) {
        requireNonNull(resourceFunc0);
        requireNonNull(completableFunc1);
        requireNonNull(disposer);
        return create(new AnonymousClass13(resourceFunc0, completableFunc1, disposer, eager));
    }

    protected Completable(CompletableOnSubscribe onSubscribe) {
        this.onSubscribe = onSubscribe;
    }

    public final Completable ambWith(Completable other) {
        requireNonNull(other);
        return amb(this, other);
    }

    public final void await() {
        CountDownLatch cdl = new CountDownLatch(1);
        Throwable[] err = new Throwable[1];
        subscribe(new AnonymousClass14(cdl, err));
        if (cdl.getCount() != 0) {
            try {
                cdl.await();
                if (err[0] != null) {
                    Exceptions.propagate(err[0]);
                }
            } catch (InterruptedException ex) {
                throw Exceptions.propagate(ex);
            }
        } else if (err[0] != null) {
            Exceptions.propagate(err[0]);
        }
    }

    public final boolean await(long timeout, TimeUnit unit) {
        boolean z = true;
        requireNonNull(unit);
        CountDownLatch cdl = new CountDownLatch(1);
        Throwable[] err = new Throwable[1];
        subscribe(new AnonymousClass15(cdl, err));
        if (cdl.getCount() != 0) {
            try {
                z = cdl.await(timeout, unit);
                if (z && err[0] != null) {
                    Exceptions.propagate(err[0]);
                }
            } catch (InterruptedException ex) {
                throw Exceptions.propagate(ex);
            }
        } else if (err[0] != null) {
            Exceptions.propagate(err[0]);
        }
        return z;
    }

    public final Completable compose(CompletableTransformer transformer) {
        return (Completable) to(transformer);
    }

    public final <T> Observable<T> andThen(Observable<T> next) {
        requireNonNull(next);
        return next.delaySubscription(toObservable());
    }

    public final <T> Single<T> andThen(Single<T> next) {
        requireNonNull(next);
        return next.delaySubscription(toObservable());
    }

    public final Completable concatWith(Completable other) {
        requireNonNull(other);
        return concat(this, other);
    }

    public final Completable delay(long delay, TimeUnit unit) {
        return delay(delay, unit, Schedulers.computation(), false);
    }

    public final Completable delay(long delay, TimeUnit unit, Scheduler scheduler) {
        return delay(delay, unit, scheduler, false);
    }

    public final Completable delay(long delay, TimeUnit unit, Scheduler scheduler, boolean delayError) {
        requireNonNull(unit);
        requireNonNull(scheduler);
        return create(new AnonymousClass16(scheduler, delay, unit, delayError));
    }

    @Deprecated
    public final Completable doOnComplete(Action0 onComplete) {
        return doOnCompleted(onComplete);
    }

    public final Completable doOnCompleted(Action0 onCompleted) {
        return doOnLifecycle(Actions.empty(), Actions.empty(), onCompleted, Actions.empty(), Actions.empty());
    }

    public final Completable doOnUnsubscribe(Action0 onUnsubscribe) {
        return doOnLifecycle(Actions.empty(), Actions.empty(), Actions.empty(), Actions.empty(), onUnsubscribe);
    }

    public final Completable doOnError(Action1<? super Throwable> onError) {
        return doOnLifecycle(Actions.empty(), onError, Actions.empty(), Actions.empty(), Actions.empty());
    }

    protected final Completable doOnLifecycle(Action1<? super Subscription> onSubscribe, Action1<? super Throwable> onError, Action0 onComplete, Action0 onAfterComplete, Action0 onUnsubscribe) {
        requireNonNull(onSubscribe);
        requireNonNull(onError);
        requireNonNull(onComplete);
        requireNonNull(onAfterComplete);
        requireNonNull(onUnsubscribe);
        return create(new AnonymousClass17(onComplete, onAfterComplete, onError, onSubscribe, onUnsubscribe));
    }

    public final Completable doOnSubscribe(Action1<? super Subscription> onSubscribe) {
        return doOnLifecycle(onSubscribe, Actions.empty(), Actions.empty(), Actions.empty(), Actions.empty());
    }

    public final Completable doOnTerminate(Action0 onTerminate) {
        return doOnLifecycle(Actions.empty(), new AnonymousClass18(onTerminate), onTerminate, Actions.empty(), Actions.empty());
    }

    public final Completable endWith(Completable other) {
        return concatWith(other);
    }

    public final <T> Observable<T> endWith(Observable<T> next) {
        return next.startWith(toObservable());
    }

    public final Completable doAfterTerminate(Action0 onAfterComplete) {
        return doOnLifecycle(Actions.empty(), Actions.empty(), Actions.empty(), onAfterComplete, Actions.empty());
    }

    public final Throwable get() {
        CountDownLatch cdl = new CountDownLatch(1);
        Throwable[] err = new Throwable[1];
        subscribe(new AnonymousClass19(cdl, err));
        if (cdl.getCount() == 0) {
            return err[0];
        }
        try {
            cdl.await();
            return err[0];
        } catch (InterruptedException ex) {
            throw Exceptions.propagate(ex);
        }
    }

    public final Throwable get(long timeout, TimeUnit unit) {
        requireNonNull(unit);
        CountDownLatch cdl = new CountDownLatch(1);
        Throwable[] err = new Throwable[1];
        subscribe(new AnonymousClass20(cdl, err));
        if (cdl.getCount() == 0) {
            return err[0];
        }
        try {
            if (cdl.await(timeout, unit)) {
                return err[0];
            }
            Exceptions.propagate(new TimeoutException());
            return null;
        } catch (InterruptedException ex) {
            throw Exceptions.propagate(ex);
        }
    }

    public final Completable lift(CompletableOperator onLift) {
        requireNonNull(onLift);
        return create(new AnonymousClass21(onLift));
    }

    public final Completable mergeWith(Completable other) {
        requireNonNull(other);
        return merge(this, other);
    }

    public final Completable observeOn(Scheduler scheduler) {
        requireNonNull(scheduler);
        return create(new AnonymousClass22(scheduler));
    }

    public final Completable onErrorComplete() {
        return onErrorComplete(UtilityFunctions.alwaysTrue());
    }

    public final Completable onErrorComplete(Func1<? super Throwable, Boolean> predicate) {
        requireNonNull(predicate);
        return create(new AnonymousClass23(predicate));
    }

    public final Completable onErrorResumeNext(Func1<? super Throwable, ? extends Completable> errorMapper) {
        requireNonNull(errorMapper);
        return create(new AnonymousClass24(errorMapper));
    }

    public final Completable repeat() {
        return fromObservable(toObservable().repeat());
    }

    public final Completable repeat(long times) {
        return fromObservable(toObservable().repeat(times));
    }

    public final Completable repeatWhen(Func1<? super Observable<? extends Void>, ? extends Observable<?>> handler) {
        requireNonNull(handler);
        return fromObservable(toObservable().repeatWhen(handler));
    }

    public final Completable retry() {
        return fromObservable(toObservable().retry());
    }

    public final Completable retry(Func2<Integer, Throwable, Boolean> predicate) {
        return fromObservable(toObservable().retry((Func2) predicate));
    }

    public final Completable retry(long times) {
        return fromObservable(toObservable().retry(times));
    }

    public final Completable retryWhen(Func1<? super Observable<? extends Throwable>, ? extends Observable<?>> handler) {
        return fromObservable(toObservable().retryWhen(handler));
    }

    public final Completable startWith(Completable other) {
        requireNonNull(other);
        return concat(other, this);
    }

    public final <T> Observable<T> startWith(Observable<T> other) {
        requireNonNull(other);
        return toObservable().startWith((Observable) other);
    }

    public final Subscription subscribe() {
        MultipleAssignmentSubscription mad = new MultipleAssignmentSubscription();
        subscribe(new AnonymousClass25(mad));
        return mad;
    }

    public final Subscription subscribe(Action0 onComplete) {
        requireNonNull(onComplete);
        MultipleAssignmentSubscription mad = new MultipleAssignmentSubscription();
        subscribe(new AnonymousClass26(onComplete, mad));
        return mad;
    }

    public final Subscription subscribe(Action1<? super Throwable> onError, Action0 onComplete) {
        requireNonNull(onError);
        requireNonNull(onComplete);
        MultipleAssignmentSubscription mad = new MultipleAssignmentSubscription();
        subscribe(new AnonymousClass27(onComplete, mad, onError));
        return mad;
    }

    private static void deliverUncaughtException(Throwable e) {
        Thread thread = Thread.currentThread();
        thread.getUncaughtExceptionHandler().uncaughtException(thread, e);
    }

    public final void subscribe(CompletableSubscriber s) {
        requireNonNull(s);
        try {
            this.onSubscribe.call(s);
        } catch (NullPointerException ex) {
            throw ex;
        } catch (Throwable ex2) {
            ERROR_HANDLER.handleError(ex2);
            Exceptions.throwIfFatal(ex2);
            NullPointerException toNpe = toNpe(ex2);
        }
    }

    public final <T> void subscribe(Subscriber<T> s) {
        requireNonNull(s);
        Subscriber<?> sw = s;
        if (sw == null) {
            try {
                throw new NullPointerException("The RxJavaPlugins.onSubscribe returned a null Subscriber");
            } catch (NullPointerException ex) {
                throw ex;
            } catch (Throwable ex2) {
                ERROR_HANDLER.handleError(ex2);
                NullPointerException toNpe = toNpe(ex2);
            }
        } else {
            subscribe(new AnonymousClass28(sw));
        }
    }

    public final Completable subscribeOn(Scheduler scheduler) {
        requireNonNull(scheduler);
        return create(new AnonymousClass29(scheduler));
    }

    public final Completable timeout(long timeout, TimeUnit unit) {
        return timeout0(timeout, unit, Schedulers.computation(), null);
    }

    public final Completable timeout(long timeout, TimeUnit unit, Completable other) {
        requireNonNull(other);
        return timeout0(timeout, unit, Schedulers.computation(), other);
    }

    public final Completable timeout(long timeout, TimeUnit unit, Scheduler scheduler) {
        return timeout0(timeout, unit, scheduler, null);
    }

    public final Completable timeout(long timeout, TimeUnit unit, Scheduler scheduler, Completable other) {
        requireNonNull(other);
        return timeout0(timeout, unit, scheduler, other);
    }

    public final Completable timeout0(long timeout, TimeUnit unit, Scheduler scheduler, Completable other) {
        requireNonNull(unit);
        requireNonNull(scheduler);
        return create(new CompletableOnSubscribeTimeout(this, timeout, unit, scheduler, other));
    }

    public final <U> U to(Func1<? super Completable, U> converter) {
        return converter.call(this);
    }

    public final <T> Observable<T> toObservable() {
        return Observable.create(new Observable.OnSubscribe<T>() {
            public void call(Subscriber<? super T> s) {
                Completable.this.subscribe((Subscriber) s);
            }
        });
    }

    public final <T> Single<T> toSingle(Func0<? extends T> completionValueFunc0) {
        requireNonNull(completionValueFunc0);
        return Single.create(new AnonymousClass31(completionValueFunc0));
    }

    public final <T> Single<T> toSingleDefault(T completionValue) {
        requireNonNull(completionValue);
        return toSingle(new AnonymousClass32(completionValue));
    }

    public final Completable unsubscribeOn(Scheduler scheduler) {
        requireNonNull(scheduler);
        return create(new AnonymousClass33(scheduler));
    }
}
