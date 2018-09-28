package com.facebook.internal;

import com.facebook.FacebookException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class CollectionMapper {

    public interface Collection<T> {
        Object get(T t);

        Iterator<T> keyIterator();

        void set(T t, Object obj, OnErrorListener onErrorListener);
    }

    public interface OnErrorListener {
        void onError(FacebookException facebookException);
    }

    public interface ValueMapper {
        void mapValue(Object obj, OnMapValueCompleteListener onMapValueCompleteListener);
    }

    public interface OnMapValueCompleteListener extends OnErrorListener {
        void onComplete(Object obj);
    }

    public interface OnMapperCompleteListener extends OnErrorListener {
        void onComplete();
    }

    /* renamed from: com.facebook.internal.CollectionMapper.1 */
    static class C13211 implements OnMapperCompleteListener {
        final /* synthetic */ Mutable val$didReturnError;
        final /* synthetic */ OnMapperCompleteListener val$onMapperCompleteListener;
        final /* synthetic */ Mutable val$pendingJobCount;

        C13211(Mutable mutable, Mutable mutable2, OnMapperCompleteListener onMapperCompleteListener) {
            this.val$didReturnError = mutable;
            this.val$pendingJobCount = mutable2;
            this.val$onMapperCompleteListener = onMapperCompleteListener;
        }

        public void onComplete() {
            if (!((Boolean) this.val$didReturnError.value).booleanValue()) {
                Mutable mutable = this.val$pendingJobCount;
                Integer valueOf = Integer.valueOf(((Integer) this.val$pendingJobCount.value).intValue() - 1);
                mutable.value = valueOf;
                if (valueOf.intValue() == 0) {
                    this.val$onMapperCompleteListener.onComplete();
                }
            }
        }

        public void onError(FacebookException exception) {
            if (!((Boolean) this.val$didReturnError.value).booleanValue()) {
                this.val$didReturnError.value = Boolean.valueOf(true);
                this.val$onMapperCompleteListener.onError(exception);
            }
        }
    }

    /* renamed from: com.facebook.internal.CollectionMapper.2 */
    static class C13222 implements OnMapValueCompleteListener {
        final /* synthetic */ Collection val$collection;
        final /* synthetic */ OnMapperCompleteListener val$jobCompleteListener;
        final /* synthetic */ Object val$key;

        C13222(Collection collection, Object obj, OnMapperCompleteListener onMapperCompleteListener) {
            this.val$collection = collection;
            this.val$key = obj;
            this.val$jobCompleteListener = onMapperCompleteListener;
        }

        public void onComplete(Object mappedValue) {
            this.val$collection.set(this.val$key, mappedValue, this.val$jobCompleteListener);
            this.val$jobCompleteListener.onComplete();
        }

        public void onError(FacebookException exception) {
            this.val$jobCompleteListener.onError(exception);
        }
    }

    public static <T> void iterate(Collection<T> collection, ValueMapper valueMapper, OnMapperCompleteListener onMapperCompleteListener) {
        Mutable<Boolean> didReturnError = new Mutable(Boolean.valueOf(false));
        Mutable<Integer> pendingJobCount = new Mutable(Integer.valueOf(1));
        OnMapperCompleteListener jobCompleteListener = new C13211(didReturnError, pendingJobCount, onMapperCompleteListener);
        Iterator<T> keyIterator = collection.keyIterator();
        List<T> keys = new LinkedList();
        while (keyIterator.hasNext()) {
            keys.add(keyIterator.next());
        }
        for (T key : keys) {
            Object value = collection.get(key);
            OnMapValueCompleteListener onMapValueCompleteListener = new C13222(collection, key, jobCompleteListener);
            Integer num = (Integer) pendingJobCount.value;
            pendingJobCount.value = Integer.valueOf(((Integer) pendingJobCount.value).intValue() + 1);
            valueMapper.mapValue(value, onMapValueCompleteListener);
        }
        jobCompleteListener.onComplete();
    }

    private CollectionMapper() {
    }
}
