package rx.internal.operators;

import rx.Single;
import rx.Single.OnSubscribe;
import rx.SingleSubscriber;
import rx.exceptions.Exceptions;
import rx.functions.Func1;

public class SingleOperatorOnErrorResumeNext<T> implements OnSubscribe<T> {
    private final Single<? extends T> originalSingle;
    private final Func1<Throwable, ? extends Single<? extends T>> resumeFunctionInCaseOfError;

    /* renamed from: rx.internal.operators.SingleOperatorOnErrorResumeNext.1 */
    static class C14601 implements Func1<Throwable, Single<? extends T>> {
        final /* synthetic */ Single val$resumeSingleInCaseOfError;

        C14601(Single single) {
            this.val$resumeSingleInCaseOfError = single;
        }

        public Single<? extends T> call(Throwable throwable) {
            return this.val$resumeSingleInCaseOfError;
        }
    }

    /* renamed from: rx.internal.operators.SingleOperatorOnErrorResumeNext.2 */
    class C14612 extends SingleSubscriber<T> {
        final /* synthetic */ SingleSubscriber val$child;

        C14612(SingleSubscriber singleSubscriber) {
            this.val$child = singleSubscriber;
        }

        public void onSuccess(T value) {
            this.val$child.onSuccess(value);
        }

        public void onError(Throwable error) {
            try {
                ((Single) SingleOperatorOnErrorResumeNext.this.resumeFunctionInCaseOfError.call(error)).subscribe(this.val$child);
            } catch (Throwable innerError) {
                Exceptions.throwOrReport(innerError, this.val$child);
            }
        }
    }

    private SingleOperatorOnErrorResumeNext(Single<? extends T> originalSingle, Func1<Throwable, ? extends Single<? extends T>> resumeFunctionInCaseOfError) {
        if (originalSingle == null) {
            throw new NullPointerException("originalSingle must not be null");
        } else if (resumeFunctionInCaseOfError == null) {
            throw new NullPointerException("resumeFunctionInCaseOfError must not be null");
        } else {
            this.originalSingle = originalSingle;
            this.resumeFunctionInCaseOfError = resumeFunctionInCaseOfError;
        }
    }

    public static <T> SingleOperatorOnErrorResumeNext<T> withFunction(Single<? extends T> originalSingle, Func1<Throwable, ? extends Single<? extends T>> resumeFunctionInCaseOfError) {
        return new SingleOperatorOnErrorResumeNext(originalSingle, resumeFunctionInCaseOfError);
    }

    public static <T> SingleOperatorOnErrorResumeNext<T> withOther(Single<? extends T> originalSingle, Single<? extends T> resumeSingleInCaseOfError) {
        if (resumeSingleInCaseOfError != null) {
            return new SingleOperatorOnErrorResumeNext(originalSingle, new C14601(resumeSingleInCaseOfError));
        }
        throw new NullPointerException("resumeSingleInCaseOfError must not be null");
    }

    public void call(SingleSubscriber<? super T> child) {
        SingleSubscriber parent = new C14612(child);
        child.add(parent);
        this.originalSingle.subscribe(parent);
    }
}
