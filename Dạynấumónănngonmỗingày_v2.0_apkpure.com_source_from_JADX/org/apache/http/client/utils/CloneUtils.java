package org.apache.http.client.utils;

import java.lang.reflect.InvocationTargetException;
import org.apache.http.annotation.Immutable;

@Immutable
public class CloneUtils {
    public static Object clone(Object obj) throws CloneNotSupportedException {
        Object obj2 = null;
        if (obj != null) {
            if (obj instanceof Cloneable) {
                try {
                    try {
                        obj2 = obj.getClass().getMethod("clone", (Class[]) null).invoke(obj, (Object[]) null);
                    } catch (InvocationTargetException ex) {
                        Throwable cause = ex.getCause();
                        if (cause instanceof CloneNotSupportedException) {
                            throw ((CloneNotSupportedException) cause);
                        }
                        throw new Error("Unexpected exception", cause);
                    } catch (IllegalAccessException ex2) {
                        throw new IllegalAccessError(ex2.getMessage());
                    }
                } catch (NoSuchMethodException ex3) {
                    throw new NoSuchMethodError(ex3.getMessage());
                }
            }
            throw new CloneNotSupportedException();
        }
        return obj2;
    }

    private CloneUtils() {
    }
}
