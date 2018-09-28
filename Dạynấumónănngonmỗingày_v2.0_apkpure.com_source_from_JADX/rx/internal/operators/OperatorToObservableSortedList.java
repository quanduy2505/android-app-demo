package rx.internal.operators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import rx.Observable.Operator;
import rx.Observer;
import rx.Subscriber;
import rx.exceptions.Exceptions;
import rx.functions.Func2;
import rx.internal.producers.SingleDelayedProducer;

public final class OperatorToObservableSortedList<T> implements Operator<List<T>, T> {
    private static Comparator DEFAULT_SORT_FUNCTION;
    final int initialCapacity;
    final Comparator<? super T> sortFunction;

    /* renamed from: rx.internal.operators.OperatorToObservableSortedList.1 */
    class C08141 implements Comparator<T> {
        final /* synthetic */ Func2 val$sortFunction;

        C08141(Func2 func2) {
            this.val$sortFunction = func2;
        }

        public int compare(T o1, T o2) {
            return ((Integer) this.val$sortFunction.call(o1, o2)).intValue();
        }
    }

    private static class DefaultComparableFunction implements Comparator<Object> {
        DefaultComparableFunction() {
        }

        public int compare(Object t1, Object t2) {
            return ((Comparable) t1).compareTo((Comparable) t2);
        }
    }

    /* renamed from: rx.internal.operators.OperatorToObservableSortedList.2 */
    class C14502 extends Subscriber<T> {
        boolean completed;
        List<T> list;
        final /* synthetic */ Subscriber val$child;
        final /* synthetic */ SingleDelayedProducer val$producer;

        C14502(SingleDelayedProducer singleDelayedProducer, Subscriber subscriber) {
            this.val$producer = singleDelayedProducer;
            this.val$child = subscriber;
            this.list = new ArrayList(OperatorToObservableSortedList.this.initialCapacity);
        }

        public void onStart() {
            request(Long.MAX_VALUE);
        }

        public void onCompleted() {
            if (!this.completed) {
                this.completed = true;
                List<T> a = this.list;
                this.list = null;
                try {
                    Collections.sort(a, OperatorToObservableSortedList.this.sortFunction);
                    this.val$producer.setValue(a);
                } catch (Throwable e) {
                    Exceptions.throwOrReport(e, (Observer) this);
                }
            }
        }

        public void onError(Throwable e) {
            this.val$child.onError(e);
        }

        public void onNext(T value) {
            if (!this.completed) {
                this.list.add(value);
            }
        }
    }

    public OperatorToObservableSortedList(int initialCapacity) {
        this.sortFunction = DEFAULT_SORT_FUNCTION;
        this.initialCapacity = initialCapacity;
    }

    public OperatorToObservableSortedList(Func2<? super T, ? super T, Integer> sortFunction, int initialCapacity) {
        this.initialCapacity = initialCapacity;
        this.sortFunction = new C08141(sortFunction);
    }

    public Subscriber<? super T> call(Subscriber<? super List<T>> child) {
        SingleDelayedProducer<List<T>> producer = new SingleDelayedProducer(child);
        Subscriber<T> result = new C14502(producer, child);
        child.add(result);
        child.setProducer(producer);
        return result;
    }

    static {
        DEFAULT_SORT_FUNCTION = new DefaultComparableFunction();
    }
}
