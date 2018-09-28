package com.firebase.client.core.utilities;

public interface Predicate<T> {
    public static final Predicate<Object> TRUE;

    /* renamed from: com.firebase.client.core.utilities.Predicate.1 */
    static class C11231 implements Predicate<Object> {
        C11231() {
        }

        public boolean evaluate(Object object) {
            return true;
        }
    }

    boolean evaluate(T t);

    static {
        TRUE = new C11231();
    }
}
