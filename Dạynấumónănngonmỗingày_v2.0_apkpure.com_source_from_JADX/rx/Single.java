package rx;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import rx.Completable.CompletableSubscriber;
import rx.Observable.Operator;
import rx.Scheduler.Worker;
import rx.annotations.Beta;
import rx.annotations.Experimental;
import rx.exceptions.Exceptions;
import rx.exceptions.OnErrorNotImplementedException;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.functions.Func3;
import rx.functions.Func4;
import rx.functions.Func5;
import rx.functions.Func6;
import rx.functions.Func7;
import rx.functions.Func8;
import rx.functions.Func9;
import rx.functions.FuncN;
import rx.internal.operators.OnSubscribeToObservableFuture;
import rx.internal.operators.OperatorDelay;
import rx.internal.operators.OperatorDoOnEach;
import rx.internal.operators.OperatorDoOnSubscribe;
import rx.internal.operators.OperatorDoOnUnsubscribe;
import rx.internal.operators.OperatorMap;
import rx.internal.operators.OperatorObserveOn;
import rx.internal.operators.OperatorOnErrorResumeNextViaFunction;
import rx.internal.operators.OperatorTimeout;
import rx.internal.operators.SingleDoAfterTerminate;
import rx.internal.operators.SingleOnSubscribeDelaySubscriptionOther;
import rx.internal.operators.SingleOnSubscribeUsing;
import rx.internal.operators.SingleOperatorOnErrorResumeNext;
import rx.internal.operators.SingleOperatorZip;
import rx.internal.producers.SingleDelayedProducer;
import rx.internal.util.ScalarSynchronousSingle;
import rx.internal.util.UtilityFunctions;
import rx.observers.SafeSubscriber;
import rx.observers.SerializedSubscriber;
import rx.plugins.RxJavaPlugins;
import rx.plugins.RxJavaSingleExecutionHook;
import rx.schedulers.Schedulers;
import rx.singles.BlockingSingle;

@Beta
public class Single<T> {
    static RxJavaSingleExecutionHook hook;
    final rx.Observable.OnSubscribe<T> onSubscribe;

    /* renamed from: rx.Single.23 */
    class AnonymousClass23 implements Observer<T> {
        final /* synthetic */ Action1 val$onError;

        AnonymousClass23(Action1 action1) {
            this.val$onError = action1;
        }

        public void onCompleted() {
        }

        public void onError(Throwable e) {
            this.val$onError.call(e);
        }

        public void onNext(T t) {
        }
    }

    /* renamed from: rx.Single.24 */
    class AnonymousClass24 implements Observer<T> {
        final /* synthetic */ Action1 val$onSuccess;

        AnonymousClass24(Action1 action1) {
            this.val$onSuccess = action1;
        }

        public void onCompleted() {
        }

        public void onError(Throwable e) {
        }

        public void onNext(T t) {
            this.val$onSuccess.call(t);
        }
    }

    /* renamed from: rx.Single.10 */
    static class AnonymousClass10 implements FuncN<R> {
        final /* synthetic */ Func6 val$zipFunction;

        AnonymousClass10(Func6 func6) {
            this.val$zipFunction = func6;
        }

        public R call(Object... args) {
            return this.val$zipFunction.call(args[0], args[1], args[2], args[3], args[4], args[5]);
        }
    }

    /* renamed from: rx.Single.11 */
    static class AnonymousClass11 implements FuncN<R> {
        final /* synthetic */ Func7 val$zipFunction;

        AnonymousClass11(Func7 func7) {
            this.val$zipFunction = func7;
        }

        public R call(Object... args) {
            return this.val$zipFunction.call(args[0], args[1], args[2], args[3], args[4], args[5], args[6]);
        }
    }

    /* renamed from: rx.Single.12 */
    static class AnonymousClass12 implements FuncN<R> {
        final /* synthetic */ Func8 val$zipFunction;

        AnonymousClass12(Func8 func8) {
            this.val$zipFunction = func8;
        }

        public R call(Object... args) {
            return this.val$zipFunction.call(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7]);
        }
    }

    /* renamed from: rx.Single.13 */
    static class AnonymousClass13 implements FuncN<R> {
        final /* synthetic */ Func9 val$zipFunction;

        AnonymousClass13(Func9 func9) {
            this.val$zipFunction = func9;
        }

        public R call(Object... args) {
            return this.val$zipFunction.call(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8]);
        }
    }

    /* renamed from: rx.Single.15 */
    class AnonymousClass15 extends Subscriber<T> {
        final /* synthetic */ Action1 val$onSuccess;

        AnonymousClass15(Action1 action1) {
            this.val$onSuccess = action1;
        }

        public final void onCompleted() {
        }

        public final void onError(Throwable e) {
            throw new OnErrorNotImplementedException(e);
        }

        public final void onNext(T args) {
            this.val$onSuccess.call(args);
        }
    }

    /* renamed from: rx.Single.16 */
    class AnonymousClass16 extends Subscriber<T> {
        final /* synthetic */ Action1 val$onError;
        final /* synthetic */ Action1 val$onSuccess;

        AnonymousClass16(Action1 action1, Action1 action12) {
            this.val$onError = action1;
            this.val$onSuccess = action12;
        }

        public final void onCompleted() {
        }

        public final void onError(Throwable e) {
            this.val$onError.call(e);
        }

        public final void onNext(T args) {
            this.val$onSuccess.call(args);
        }
    }

    /* renamed from: rx.Single.17 */
    class AnonymousClass17 extends SingleSubscriber<T> {
        final /* synthetic */ Observer val$observer;

        AnonymousClass17(Observer observer) {
            this.val$observer = observer;
        }

        public void onSuccess(T value) {
            this.val$observer.onNext(value);
            this.val$observer.onCompleted();
        }

        public void onError(Throwable error) {
            this.val$observer.onError(error);
        }
    }

    /* renamed from: rx.Single.18 */
    class AnonymousClass18 extends Subscriber<T> {
        final /* synthetic */ SingleSubscriber val$te;

        AnonymousClass18(SingleSubscriber singleSubscriber) {
            this.val$te = singleSubscriber;
        }

        public void onCompleted() {
        }

        public void onError(Throwable e) {
            this.val$te.onError(e);
        }

        public void onNext(T t) {
            this.val$te.onSuccess(t);
        }
    }

    /* renamed from: rx.Single.6 */
    static class C13506 implements FuncN<R> {
        final /* synthetic */ Func2 val$zipFunction;

        C13506(Func2 func2) {
            this.val$zipFunction = func2;
        }

        public R call(Object... args) {
            return this.val$zipFunction.call(args[0], args[1]);
        }
    }

    /* renamed from: rx.Single.7 */
    static class C13517 implements FuncN<R> {
        final /* synthetic */ Func3 val$zipFunction;

        C13517(Func3 func3) {
            this.val$zipFunction = func3;
        }

        public R call(Object... args) {
            return this.val$zipFunction.call(args[0], args[1], args[2]);
        }
    }

    /* renamed from: rx.Single.8 */
    static class C13528 implements FuncN<R> {
        final /* synthetic */ Func4 val$zipFunction;

        C13528(Func4 func4) {
            this.val$zipFunction = func4;
        }

        public R call(Object... args) {
            return this.val$zipFunction.call(args[0], args[1], args[2], args[3]);
        }
    }

    /* renamed from: rx.Single.9 */
    static class C13539 implements FuncN<R> {
        final /* synthetic */ Func5 val$zipFunction;

        C13539(Func5 func5) {
            this.val$zipFunction = func5;
        }

        public R call(Object... args) {
            return this.val$zipFunction.call(args[0], args[1], args[2], args[3], args[4]);
        }
    }

    public interface Transformer<T, R> extends Func1<Single<T>, Single<R>> {
    }

    /* renamed from: rx.Single.20 */
    class AnonymousClass20 implements Operator<T, T> {
        final /* synthetic */ Completable val$other;

        /* renamed from: rx.Single.20.2 */
        class C12502 implements CompletableSubscriber {
            final /* synthetic */ Subscriber val$main;
            final /* synthetic */ Subscriber val$serial;

            C12502(Subscriber subscriber, Subscriber subscriber2) {
                this.val$main = subscriber;
                this.val$serial = subscriber2;
            }

            public void onCompleted() {
                onError(new CancellationException("Stream was canceled before emitting a terminal event."));
            }

            public void onError(Throwable e) {
                this.val$main.onError(e);
            }

            public void onSubscribe(Subscription d) {
                this.val$serial.add(d);
            }
        }

        /* renamed from: rx.Single.20.1 */
        class C13441 extends Subscriber<T> {
            final /* synthetic */ Subscriber val$serial;

            C13441(Subscriber x0, boolean x1, Subscriber subscriber) {
                this.val$serial = subscriber;
                super(x0, x1);
            }

            public void onNext(T t) {
                this.val$serial.onNext(t);
            }

            public void onError(Throwable e) {
                try {
                    this.val$serial.onError(e);
                } finally {
                    this.val$serial.unsubscribe();
                }
            }

            public void onCompleted() {
                try {
                    this.val$serial.onCompleted();
                } finally {
                    this.val$serial.unsubscribe();
                }
            }
        }

        AnonymousClass20(Completable completable) {
            this.val$other = completable;
        }

        public Subscriber<? super T> call(Subscriber<? super T> child) {
            Subscriber<T> serial = new SerializedSubscriber(child, false);
            Subscriber<T> main = new C13441(serial, false, serial);
            CompletableSubscriber so = new C12502(main, serial);
            serial.add(main);
            child.add(serial);
            this.val$other.subscribe(so);
            return main;
        }
    }

    /* renamed from: rx.Single.21 */
    class AnonymousClass21 implements Operator<T, T> {
        final /* synthetic */ Observable val$other;

        /* renamed from: rx.Single.21.1 */
        class C13451 extends Subscriber<T> {
            final /* synthetic */ Subscriber val$serial;

            C13451(Subscriber x0, boolean x1, Subscriber subscriber) {
                this.val$serial = subscriber;
                super(x0, x1);
            }

            public void onNext(T t) {
                this.val$serial.onNext(t);
            }

            public void onError(Throwable e) {
                try {
                    this.val$serial.onError(e);
                } finally {
                    this.val$serial.unsubscribe();
                }
            }

            public void onCompleted() {
                try {
                    this.val$serial.onCompleted();
                } finally {
                    this.val$serial.unsubscribe();
                }
            }
        }

        /* renamed from: rx.Single.21.2 */
        class C13462 extends Subscriber<E> {
            final /* synthetic */ Subscriber val$main;

            C13462(Subscriber subscriber) {
                this.val$main = subscriber;
            }

            public void onCompleted() {
                onError(new CancellationException("Stream was canceled before emitting a terminal event."));
            }

            public void onError(Throwable e) {
                this.val$main.onError(e);
            }

            public void onNext(E e) {
                onError(new CancellationException("Stream was canceled before emitting a terminal event."));
            }
        }

        AnonymousClass21(Observable observable) {
            this.val$other = observable;
        }

        public Subscriber<? super T> call(Subscriber<? super T> child) {
            Subscriber<T> serial = new SerializedSubscriber(child, false);
            Subscriber<T> main = new C13451(serial, false, serial);
            Subscriber<E> so = new C13462(main);
            serial.add(main);
            serial.add(so);
            child.add(serial);
            this.val$other.unsafeSubscribe(so);
            return main;
        }
    }

    public interface OnSubscribe<T> extends Action1<SingleSubscriber<? super T>> {
    }

    /* renamed from: rx.Single.19 */
    class AnonymousClass19 implements OnSubscribe<T> {
        final /* synthetic */ Scheduler val$scheduler;

        /* renamed from: rx.Single.19.1 */
        class C15091 implements Action0 {
            final /* synthetic */ SingleSubscriber val$t;
            final /* synthetic */ Worker val$w;

            /* renamed from: rx.Single.19.1.1 */
            class C13431 extends SingleSubscriber<T> {
                C13431() {
                }

                public void onSuccess(T value) {
                    try {
                        C15091.this.val$t.onSuccess(value);
                    } finally {
                        C15091.this.val$w.unsubscribe();
                    }
                }

                public void onError(Throwable error) {
                    try {
                        C15091.this.val$t.onError(error);
                    } finally {
                        C15091.this.val$w.unsubscribe();
                    }
                }
            }

            C15091(SingleSubscriber singleSubscriber, Worker worker) {
                this.val$t = singleSubscriber;
                this.val$w = worker;
            }

            public void call() {
                SingleSubscriber ssub = new C13431();
                this.val$t.add(ssub);
                Single.this.subscribe(ssub);
            }
        }

        AnonymousClass19(Scheduler scheduler) {
            this.val$scheduler = scheduler;
        }

        public void call(SingleSubscriber<? super T> t) {
            Worker w = this.val$scheduler.createWorker();
            t.add(w);
            w.schedule(new C15091(t, w));
        }
    }

    /* renamed from: rx.Single.1 */
    class C16031 implements rx.Observable.OnSubscribe<T> {
        final /* synthetic */ OnSubscribe val$f;

        /* renamed from: rx.Single.1.1 */
        class C13421 extends SingleSubscriber<T> {
            final /* synthetic */ Subscriber val$child;
            final /* synthetic */ SingleDelayedProducer val$producer;

            C13421(SingleDelayedProducer singleDelayedProducer, Subscriber subscriber) {
                this.val$producer = singleDelayedProducer;
                this.val$child = subscriber;
            }

            public void onSuccess(T value) {
                this.val$producer.setValue(value);
            }

            public void onError(Throwable error) {
                this.val$child.onError(error);
            }
        }

        C16031(OnSubscribe onSubscribe) {
            this.val$f = onSubscribe;
        }

        public void call(Subscriber<? super T> child) {
            SingleDelayedProducer<T> producer = new SingleDelayedProducer(child);
            child.setProducer(producer);
            SingleSubscriber<T> ss = new C13421(producer, child);
            child.add(ss);
            this.val$f.call(ss);
        }
    }

    /* renamed from: rx.Single.25 */
    static class AnonymousClass25 implements OnSubscribe<T> {
        final /* synthetic */ Callable val$singleFactory;

        AnonymousClass25(Callable callable) {
            this.val$singleFactory = callable;
        }

        public void call(SingleSubscriber<? super T> singleSubscriber) {
            try {
                ((Single) this.val$singleFactory.call()).subscribe((SingleSubscriber) singleSubscriber);
            } catch (Throwable t) {
                Exceptions.throwIfFatal(t);
                singleSubscriber.onError(t);
            }
        }
    }

    /* renamed from: rx.Single.2 */
    class C16042 implements rx.Observable.OnSubscribe<R> {
        final /* synthetic */ Operator val$lift;

        C16042(Operator operator) {
            this.val$lift = operator;
        }

        public void call(Subscriber<? super R> o) {
            Observer st;
            try {
                st = (Subscriber) Single.hook.onLift(this.val$lift).call(o);
                st.onStart();
                Single.this.onSubscribe.call(st);
            } catch (Throwable e) {
                Exceptions.throwOrReport(e, (Observer) o);
            }
        }
    }

    /* renamed from: rx.Single.3 */
    static class C16053 implements OnSubscribe<T> {
        final /* synthetic */ Throwable val$exception;

        C16053(Throwable th) {
            this.val$exception = th;
        }

        public void call(SingleSubscriber<? super T> te) {
            te.onError(this.val$exception);
        }
    }

    /* renamed from: rx.Single.4 */
    static class C16064 implements OnSubscribe<T> {
        final /* synthetic */ Callable val$func;

        C16064(Callable callable) {
            this.val$func = callable;
        }

        public void call(SingleSubscriber<? super T> singleSubscriber) {
            try {
                singleSubscriber.onSuccess(this.val$func.call());
            } catch (Throwable t) {
                Exceptions.throwIfFatal(t);
                singleSubscriber.onError(t);
            }
        }
    }

    /* renamed from: rx.Single.5 */
    static class C16075 implements OnSubscribe<T> {
        final /* synthetic */ Single val$source;

        /* renamed from: rx.Single.5.1 */
        class C13491 extends SingleSubscriber<Single<? extends T>> {
            final /* synthetic */ SingleSubscriber val$child;

            C13491(SingleSubscriber singleSubscriber) {
                this.val$child = singleSubscriber;
            }

            public void onSuccess(Single<? extends T> innerSingle) {
                innerSingle.subscribe(this.val$child);
            }

            public void onError(Throwable error) {
                this.val$child.onError(error);
            }
        }

        C16075(Single single) {
            this.val$source = single;
        }

        public void call(SingleSubscriber<? super T> child) {
            this.val$source.subscribe(new C13491(child));
        }
    }

    protected Single(OnSubscribe<T> f) {
        this.onSubscribe = new C16031(f);
    }

    private Single(rx.Observable.OnSubscribe<T> f) {
        this.onSubscribe = f;
    }

    static {
        hook = RxJavaPlugins.getInstance().getSingleExecutionHook();
    }

    public static <T> Single<T> create(OnSubscribe<T> f) {
        return new Single(hook.onCreate(f));
    }

    @Experimental
    public final <R> Single<R> lift(Operator<? extends R, ? super T> lift) {
        return new Single(new C16042(lift));
    }

    public <R> Single<R> compose(Transformer<? super T, ? extends R> transformer) {
        return (Single) transformer.call(this);
    }

    private static <T> Observable<T> asObservable(Single<T> t) {
        return Observable.create(t.onSubscribe);
    }

    private Single<Observable<T>> nest() {
        return just(asObservable(this));
    }

    public static <T> Observable<T> concat(Single<? extends T> t1, Single<? extends T> t2) {
        return Observable.concat(asObservable(t1), asObservable(t2));
    }

    public static <T> Observable<T> concat(Single<? extends T> t1, Single<? extends T> t2, Single<? extends T> t3) {
        return Observable.concat(asObservable(t1), asObservable(t2), asObservable(t3));
    }

    public static <T> Observable<T> concat(Single<? extends T> t1, Single<? extends T> t2, Single<? extends T> t3, Single<? extends T> t4) {
        return Observable.concat(asObservable(t1), asObservable(t2), asObservable(t3), asObservable(t4));
    }

    public static <T> Observable<T> concat(Single<? extends T> t1, Single<? extends T> t2, Single<? extends T> t3, Single<? extends T> t4, Single<? extends T> t5) {
        return Observable.concat(asObservable(t1), asObservable(t2), asObservable(t3), asObservable(t4), asObservable(t5));
    }

    public static <T> Observable<T> concat(Single<? extends T> t1, Single<? extends T> t2, Single<? extends T> t3, Single<? extends T> t4, Single<? extends T> t5, Single<? extends T> t6) {
        return Observable.concat(asObservable(t1), asObservable(t2), asObservable(t3), asObservable(t4), asObservable(t5), asObservable(t6));
    }

    public static <T> Observable<T> concat(Single<? extends T> t1, Single<? extends T> t2, Single<? extends T> t3, Single<? extends T> t4, Single<? extends T> t5, Single<? extends T> t6, Single<? extends T> t7) {
        return Observable.concat(asObservable(t1), asObservable(t2), asObservable(t3), asObservable(t4), asObservable(t5), asObservable(t6), asObservable(t7));
    }

    public static <T> Observable<T> concat(Single<? extends T> t1, Single<? extends T> t2, Single<? extends T> t3, Single<? extends T> t4, Single<? extends T> t5, Single<? extends T> t6, Single<? extends T> t7, Single<? extends T> t8) {
        return Observable.concat(asObservable(t1), asObservable(t2), asObservable(t3), asObservable(t4), asObservable(t5), asObservable(t6), asObservable(t7), asObservable(t8));
    }

    public static <T> Observable<T> concat(Single<? extends T> t1, Single<? extends T> t2, Single<? extends T> t3, Single<? extends T> t4, Single<? extends T> t5, Single<? extends T> t6, Single<? extends T> t7, Single<? extends T> t8, Single<? extends T> t9) {
        return Observable.concat(asObservable(t1), asObservable(t2), asObservable(t3), asObservable(t4), asObservable(t5), asObservable(t6), asObservable(t7), asObservable(t8), asObservable(t9));
    }

    public static <T> Single<T> error(Throwable exception) {
        return create(new C16053(exception));
    }

    public static <T> Single<T> from(Future<? extends T> future) {
        return new Single(OnSubscribeToObservableFuture.toObservableFuture(future));
    }

    public static <T> Single<T> from(Future<? extends T> future, long timeout, TimeUnit unit) {
        return new Single(OnSubscribeToObservableFuture.toObservableFuture(future, timeout, unit));
    }

    public static <T> Single<T> from(Future<? extends T> future, Scheduler scheduler) {
        return new Single(OnSubscribeToObservableFuture.toObservableFuture(future)).subscribeOn(scheduler);
    }

    @Beta
    public static <T> Single<T> fromCallable(Callable<? extends T> func) {
        return create(new C16064(func));
    }

    public static <T> Single<T> just(T value) {
        return ScalarSynchronousSingle.create(value);
    }

    public static <T> Single<T> merge(Single<? extends Single<? extends T>> source) {
        if (source instanceof ScalarSynchronousSingle) {
            return ((ScalarSynchronousSingle) source).scalarFlatMap(UtilityFunctions.identity());
        }
        return create(new C16075(source));
    }

    public static <T> Observable<T> merge(Single<? extends T> t1, Single<? extends T> t2) {
        return Observable.merge(asObservable(t1), asObservable(t2));
    }

    public static <T> Observable<T> merge(Single<? extends T> t1, Single<? extends T> t2, Single<? extends T> t3) {
        return Observable.merge(asObservable(t1), asObservable(t2), asObservable(t3));
    }

    public static <T> Observable<T> merge(Single<? extends T> t1, Single<? extends T> t2, Single<? extends T> t3, Single<? extends T> t4) {
        return Observable.merge(asObservable(t1), asObservable(t2), asObservable(t3), asObservable(t4));
    }

    public static <T> Observable<T> merge(Single<? extends T> t1, Single<? extends T> t2, Single<? extends T> t3, Single<? extends T> t4, Single<? extends T> t5) {
        return Observable.merge(asObservable(t1), asObservable(t2), asObservable(t3), asObservable(t4), asObservable(t5));
    }

    public static <T> Observable<T> merge(Single<? extends T> t1, Single<? extends T> t2, Single<? extends T> t3, Single<? extends T> t4, Single<? extends T> t5, Single<? extends T> t6) {
        return Observable.merge(asObservable(t1), asObservable(t2), asObservable(t3), asObservable(t4), asObservable(t5), asObservable(t6));
    }

    public static <T> Observable<T> merge(Single<? extends T> t1, Single<? extends T> t2, Single<? extends T> t3, Single<? extends T> t4, Single<? extends T> t5, Single<? extends T> t6, Single<? extends T> t7) {
        return Observable.merge(asObservable(t1), asObservable(t2), asObservable(t3), asObservable(t4), asObservable(t5), asObservable(t6), asObservable(t7));
    }

    public static <T> Observable<T> merge(Single<? extends T> t1, Single<? extends T> t2, Single<? extends T> t3, Single<? extends T> t4, Single<? extends T> t5, Single<? extends T> t6, Single<? extends T> t7, Single<? extends T> t8) {
        return Observable.merge(asObservable(t1), asObservable(t2), asObservable(t3), asObservable(t4), asObservable(t5), asObservable(t6), asObservable(t7), asObservable(t8));
    }

    public static <T> Observable<T> merge(Single<? extends T> t1, Single<? extends T> t2, Single<? extends T> t3, Single<? extends T> t4, Single<? extends T> t5, Single<? extends T> t6, Single<? extends T> t7, Single<? extends T> t8, Single<? extends T> t9) {
        return Observable.merge(asObservable(t1), asObservable(t2), asObservable(t3), asObservable(t4), asObservable(t5), asObservable(t6), asObservable(t7), asObservable(t8), asObservable(t9));
    }

    public static <T1, T2, R> Single<R> zip(Single<? extends T1> s1, Single<? extends T2> s2, Func2<? super T1, ? super T2, ? extends R> zipFunction) {
        return SingleOperatorZip.zip(new Single[]{s1, s2}, new C13506(zipFunction));
    }

    public static <T1, T2, T3, R> Single<R> zip(Single<? extends T1> s1, Single<? extends T2> s2, Single<? extends T3> s3, Func3<? super T1, ? super T2, ? super T3, ? extends R> zipFunction) {
        return SingleOperatorZip.zip(new Single[]{s1, s2, s3}, new C13517(zipFunction));
    }

    public static <T1, T2, T3, T4, R> Single<R> zip(Single<? extends T1> s1, Single<? extends T2> s2, Single<? extends T3> s3, Single<? extends T4> s4, Func4<? super T1, ? super T2, ? super T3, ? super T4, ? extends R> zipFunction) {
        return SingleOperatorZip.zip(new Single[]{s1, s2, s3, s4}, new C13528(zipFunction));
    }

    public static <T1, T2, T3, T4, T5, R> Single<R> zip(Single<? extends T1> s1, Single<? extends T2> s2, Single<? extends T3> s3, Single<? extends T4> s4, Single<? extends T5> s5, Func5<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? extends R> zipFunction) {
        return SingleOperatorZip.zip(new Single[]{s1, s2, s3, s4, s5}, new C13539(zipFunction));
    }

    public static <T1, T2, T3, T4, T5, T6, R> Single<R> zip(Single<? extends T1> s1, Single<? extends T2> s2, Single<? extends T3> s3, Single<? extends T4> s4, Single<? extends T5> s5, Single<? extends T6> s6, Func6<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? extends R> zipFunction) {
        return SingleOperatorZip.zip(new Single[]{s1, s2, s3, s4, s5, s6}, new AnonymousClass10(zipFunction));
    }

    public static <T1, T2, T3, T4, T5, T6, T7, R> Single<R> zip(Single<? extends T1> s1, Single<? extends T2> s2, Single<? extends T3> s3, Single<? extends T4> s4, Single<? extends T5> s5, Single<? extends T6> s6, Single<? extends T7> s7, Func7<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? extends R> zipFunction) {
        return SingleOperatorZip.zip(new Single[]{s1, s2, s3, s4, s5, s6, s7}, new AnonymousClass11(zipFunction));
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, R> Single<R> zip(Single<? extends T1> s1, Single<? extends T2> s2, Single<? extends T3> s3, Single<? extends T4> s4, Single<? extends T5> s5, Single<? extends T6> s6, Single<? extends T7> s7, Single<? extends T8> s8, Func8<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8, ? extends R> zipFunction) {
        return SingleOperatorZip.zip(new Single[]{s1, s2, s3, s4, s5, s6, s7, s8}, new AnonymousClass12(zipFunction));
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, R> Single<R> zip(Single<? extends T1> s1, Single<? extends T2> s2, Single<? extends T3> s3, Single<? extends T4> s4, Single<? extends T5> s5, Single<? extends T6> s6, Single<? extends T7> s7, Single<? extends T8> s8, Single<? extends T9> s9, Func9<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8, ? super T9, ? extends R> zipFunction) {
        return SingleOperatorZip.zip(new Single[]{s1, s2, s3, s4, s5, s6, s7, s8, s9}, new AnonymousClass13(zipFunction));
    }

    public static <R> Single<R> zip(Iterable<? extends Single<?>> singles, FuncN<? extends R> zipFunction) {
        return SingleOperatorZip.zip(iterableToArray(singles), zipFunction);
    }

    public final Observable<T> concatWith(Single<? extends T> t1) {
        return concat(this, t1);
    }

    public final <R> Single<R> flatMap(Func1<? super T, ? extends Single<? extends R>> func) {
        if (this instanceof ScalarSynchronousSingle) {
            return ((ScalarSynchronousSingle) this).scalarFlatMap(func);
        }
        return merge(map(func));
    }

    public final <R> Observable<R> flatMapObservable(Func1<? super T, ? extends Observable<? extends R>> func) {
        return Observable.merge(asObservable(map(func)));
    }

    public final <R> Single<R> map(Func1<? super T, ? extends R> func) {
        return lift(new OperatorMap(func));
    }

    public final Observable<T> mergeWith(Single<? extends T> t1) {
        return merge(this, t1);
    }

    public final Single<T> observeOn(Scheduler scheduler) {
        if (this instanceof ScalarSynchronousSingle) {
            return ((ScalarSynchronousSingle) this).scalarScheduleOn(scheduler);
        }
        return lift(new OperatorObserveOn(scheduler, false));
    }

    public final Single<T> onErrorReturn(Func1<Throwable, ? extends T> resumeFunction) {
        return lift(OperatorOnErrorResumeNextViaFunction.withSingle(resumeFunction));
    }

    @Experimental
    public final Single<T> onErrorResumeNext(Single<? extends T> resumeSingleInCaseOfError) {
        return new Single(SingleOperatorOnErrorResumeNext.withOther(this, resumeSingleInCaseOfError));
    }

    @Experimental
    public final Single<T> onErrorResumeNext(Func1<Throwable, ? extends Single<? extends T>> resumeFunctionInCaseOfError) {
        return new Single(SingleOperatorOnErrorResumeNext.withFunction(this, resumeFunctionInCaseOfError));
    }

    public final Subscription subscribe() {
        return subscribe(new Subscriber<T>() {
            public final void onCompleted() {
            }

            public final void onError(Throwable e) {
                throw new OnErrorNotImplementedException(e);
            }

            public final void onNext(T t) {
            }
        });
    }

    public final Subscription subscribe(Action1<? super T> onSuccess) {
        if (onSuccess != null) {
            return subscribe(new AnonymousClass15(onSuccess));
        }
        throw new IllegalArgumentException("onSuccess can not be null");
    }

    public final Subscription subscribe(Action1<? super T> onSuccess, Action1<Throwable> onError) {
        if (onSuccess == null) {
            throw new IllegalArgumentException("onSuccess can not be null");
        } else if (onError != null) {
            return subscribe(new AnonymousClass16(onError, onSuccess));
        } else {
            throw new IllegalArgumentException("onError can not be null");
        }
    }

    public final Subscription unsafeSubscribe(Subscriber<? super T> subscriber) {
        try {
            subscriber.onStart();
            hook.onSubscribeStart(this, this.onSubscribe).call(subscriber);
            return hook.onSubscribeReturn(subscriber);
        } catch (Throwable e2) {
            Exceptions.throwIfFatal(e2);
            hook.onSubscribeError(new RuntimeException("Error occurred attempting to subscribe [" + e.getMessage() + "] and then again while trying to pass to onError.", e2));
        }
    }

    public final Subscription subscribe(Observer<? super T> observer) {
        if (observer != null) {
            return subscribe(new AnonymousClass17(observer));
        }
        throw new NullPointerException("observer is null");
    }

    public final Subscription subscribe(Subscriber<? super T> subscriber) {
        if (subscriber == null) {
            throw new IllegalArgumentException("observer can not be null");
        } else if (this.onSubscribe == null) {
            throw new IllegalStateException("onSubscribe function can not be null.");
        } else {
            subscriber.onStart();
            if (!(subscriber instanceof SafeSubscriber)) {
                subscriber = new SafeSubscriber(subscriber);
            }
            try {
                hook.onSubscribeStart(this, this.onSubscribe).call(subscriber);
                return hook.onSubscribeReturn(subscriber);
            } catch (Throwable e2) {
                Exceptions.throwIfFatal(e2);
                hook.onSubscribeError(new RuntimeException("Error occurred attempting to subscribe [" + e.getMessage() + "] and then again while trying to pass to onError.", e2));
            }
        }
    }

    public final Subscription subscribe(SingleSubscriber<? super T> te) {
        Subscriber s = new AnonymousClass18(te);
        te.add(s);
        subscribe(s);
        return s;
    }

    public final Single<T> subscribeOn(Scheduler scheduler) {
        if (this instanceof ScalarSynchronousSingle) {
            return ((ScalarSynchronousSingle) this).scalarScheduleOn(scheduler);
        }
        return create(new AnonymousClass19(scheduler));
    }

    public final Single<T> takeUntil(Completable other) {
        return lift(new AnonymousClass20(other));
    }

    public final <E> Single<T> takeUntil(Observable<? extends E> other) {
        return lift(new AnonymousClass21(other));
    }

    public final <E> Single<T> takeUntil(Single<? extends E> other) {
        return lift(new Operator<T, T>() {
            final /* synthetic */ Single val$other;

            /* renamed from: rx.Single.22.1 */
            class C13471 extends Subscriber<T> {
                final /* synthetic */ Subscriber val$serial;

                C13471(Subscriber x0, boolean x1, Subscriber subscriber) {
                    this.val$serial = subscriber;
                    super(x0, x1);
                }

                public void onNext(T t) {
                    this.val$serial.onNext(t);
                }

                public void onError(Throwable e) {
                    try {
                        this.val$serial.onError(e);
                    } finally {
                        this.val$serial.unsubscribe();
                    }
                }

                public void onCompleted() {
                    try {
                        this.val$serial.onCompleted();
                    } finally {
                        this.val$serial.unsubscribe();
                    }
                }
            }

            /* renamed from: rx.Single.22.2 */
            class C13482 extends SingleSubscriber<E> {
                final /* synthetic */ Subscriber val$main;

                C13482(Subscriber subscriber) {
                    this.val$main = subscriber;
                }

                public void onSuccess(E e) {
                    onError(new CancellationException("Stream was canceled before emitting a terminal event."));
                }

                public void onError(Throwable e) {
                    this.val$main.onError(e);
                }
            }

            {
                this.val$other = r2;
            }

            public Subscriber<? super T> call(Subscriber<? super T> child) {
                Subscriber<T> serial = new SerializedSubscriber(child, false);
                Subscriber<T> main = new C13471(serial, false, serial);
                SingleSubscriber so = new C13482(main);
                serial.add(main);
                serial.add(so);
                child.add(serial);
                this.val$other.subscribe(so);
                return main;
            }
        });
    }

    public final Observable<T> toObservable() {
        return asObservable(this);
    }

    @Experimental
    public final Completable toCompletable() {
        return Completable.fromSingle(this);
    }

    public final Single<T> timeout(long timeout, TimeUnit timeUnit) {
        return timeout(timeout, timeUnit, null, Schedulers.computation());
    }

    public final Single<T> timeout(long timeout, TimeUnit timeUnit, Scheduler scheduler) {
        return timeout(timeout, timeUnit, null, scheduler);
    }

    public final Single<T> timeout(long timeout, TimeUnit timeUnit, Single<? extends T> other) {
        return timeout(timeout, timeUnit, other, Schedulers.computation());
    }

    public final Single<T> timeout(long timeout, TimeUnit timeUnit, Single<? extends T> other, Scheduler scheduler) {
        if (other == null) {
            other = error(new TimeoutException());
        }
        return lift(new OperatorTimeout(timeout, timeUnit, asObservable(other), scheduler));
    }

    @Experimental
    public final BlockingSingle<T> toBlocking() {
        return BlockingSingle.from(this);
    }

    public final <T2, R> Single<R> zipWith(Single<? extends T2> other, Func2<? super T, ? super T2, ? extends R> zipFunction) {
        return zip(this, other, zipFunction);
    }

    @Experimental
    public final Single<T> doOnError(Action1<Throwable> onError) {
        return lift(new OperatorDoOnEach(new AnonymousClass23(onError)));
    }

    @Experimental
    public final Single<T> doOnSuccess(Action1<? super T> onSuccess) {
        return lift(new OperatorDoOnEach(new AnonymousClass24(onSuccess)));
    }

    @Experimental
    public final Single<T> doOnSubscribe(Action0 subscribe) {
        return lift(new OperatorDoOnSubscribe(subscribe));
    }

    @Experimental
    public final Single<T> delay(long delay, TimeUnit unit, Scheduler scheduler) {
        return lift(new OperatorDelay(delay, unit, scheduler));
    }

    @Experimental
    public final Single<T> delay(long delay, TimeUnit unit) {
        return delay(delay, unit, Schedulers.computation());
    }

    @Experimental
    public static <T> Single<T> defer(Callable<Single<T>> singleFactory) {
        return create(new AnonymousClass25(singleFactory));
    }

    @Experimental
    public final Single<T> doOnUnsubscribe(Action0 action) {
        return lift(new OperatorDoOnUnsubscribe(action));
    }

    @Experimental
    public final Single<T> doAfterTerminate(Action0 action) {
        return create(new SingleDoAfterTerminate(this, action));
    }

    static <T> Single<? extends T>[] iterableToArray(Iterable<? extends Single<? extends T>> singlesIterable) {
        if (singlesIterable instanceof Collection) {
            Collection<? extends Single<? extends T>> list = (Collection) singlesIterable;
            return (Single[]) list.toArray(new Single[list.size()]);
        }
        Single<? extends T>[] tempArray = new Single[8];
        int count = 0;
        for (Single<? extends T> s : singlesIterable) {
            if (count == tempArray.length) {
                Single<? extends T>[] sb = new Single[((count >> 2) + count)];
                System.arraycopy(tempArray, 0, sb, 0, count);
                tempArray = sb;
            }
            tempArray[count] = s;
            count++;
        }
        if (tempArray.length == count) {
            return tempArray;
        }
        Single<? extends T>[] singlesArray = new Single[count];
        System.arraycopy(tempArray, 0, singlesArray, 0, count);
        return singlesArray;
    }

    public final Single<T> retry() {
        return toObservable().retry().toSingle();
    }

    public final Single<T> retry(long count) {
        return toObservable().retry(count).toSingle();
    }

    public final Single<T> retry(Func2<Integer, Throwable, Boolean> predicate) {
        return toObservable().retry((Func2) predicate).toSingle();
    }

    public final Single<T> retryWhen(Func1<Observable<? extends Throwable>, ? extends Observable<?>> notificationHandler) {
        return toObservable().retryWhen(notificationHandler).toSingle();
    }

    @Experimental
    public static <T, Resource> Single<T> using(Func0<Resource> resourceFactory, Func1<? super Resource, ? extends Single<? extends T>> observableFactory, Action1<? super Resource> disposeAction) {
        return using(resourceFactory, observableFactory, disposeAction, false);
    }

    @Experimental
    public static <T, Resource> Single<T> using(Func0<Resource> resourceFactory, Func1<? super Resource, ? extends Single<? extends T>> singleFactory, Action1<? super Resource> disposeAction, boolean disposeEagerly) {
        if (resourceFactory == null) {
            throw new NullPointerException("resourceFactory is null");
        } else if (singleFactory == null) {
            throw new NullPointerException("singleFactory is null");
        } else if (disposeAction != null) {
            return create(new SingleOnSubscribeUsing(resourceFactory, singleFactory, disposeAction, disposeEagerly));
        } else {
            throw new NullPointerException("disposeAction is null");
        }
    }

    @Experimental
    public final Single<T> delaySubscription(Observable<?> other) {
        if (other != null) {
            return create(new SingleOnSubscribeDelaySubscriptionOther(this, other));
        }
        throw new NullPointerException();
    }
}
