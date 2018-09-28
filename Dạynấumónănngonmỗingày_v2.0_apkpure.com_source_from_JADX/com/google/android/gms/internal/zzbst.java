package com.google.android.gms.internal;

import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public abstract class zzbst {

    /* renamed from: com.google.android.gms.internal.zzbst.1 */
    static class C11941 extends zzbst {
        final /* synthetic */ Method zzcnT;
        final /* synthetic */ Object zzcnU;

        C11941(Method method, Object obj) {
            this.zzcnT = method;
            this.zzcnU = obj;
        }

        public <T> T zze(Class<T> cls) throws Exception {
            return this.zzcnT.invoke(this.zzcnU, new Object[]{cls});
        }
    }

    /* renamed from: com.google.android.gms.internal.zzbst.2 */
    static class C11952 extends zzbst {
        final /* synthetic */ Method zzcnV;
        final /* synthetic */ int zzcnW;

        C11952(Method method, int i) {
            this.zzcnV = method;
            this.zzcnW = i;
        }

        public <T> T zze(Class<T> cls) throws Exception {
            return this.zzcnV.invoke(null, new Object[]{cls, Integer.valueOf(this.zzcnW)});
        }
    }

    /* renamed from: com.google.android.gms.internal.zzbst.3 */
    static class C11963 extends zzbst {
        final /* synthetic */ Method zzcnV;

        C11963(Method method) {
            this.zzcnV = method;
        }

        public <T> T zze(Class<T> cls) throws Exception {
            return this.zzcnV.invoke(null, new Object[]{cls, Object.class});
        }
    }

    /* renamed from: com.google.android.gms.internal.zzbst.4 */
    static class C11974 extends zzbst {
        C11974() {
        }

        public <T> T zze(Class<T> cls) {
            String valueOf = String.valueOf(cls);
            throw new UnsupportedOperationException(new StringBuilder(String.valueOf(valueOf).length() + 16).append("Cannot allocate ").append(valueOf).toString());
        }
    }

    public static zzbst zzabO() {
        try {
            Class cls = Class.forName("sun.misc.Unsafe");
            Field declaredField = cls.getDeclaredField("theUnsafe");
            declaredField.setAccessible(true);
            return new C11941(cls.getMethod("allocateInstance", new Class[]{Class.class}), declaredField.get(null));
        } catch (Exception e) {
            try {
                Method declaredMethod = ObjectStreamClass.class.getDeclaredMethod("getConstructorId", new Class[]{Class.class});
                declaredMethod.setAccessible(true);
                int intValue = ((Integer) declaredMethod.invoke(null, new Object[]{Object.class})).intValue();
                Method declaredMethod2 = ObjectStreamClass.class.getDeclaredMethod("newInstance", new Class[]{Class.class, Integer.TYPE});
                declaredMethod2.setAccessible(true);
                return new C11952(declaredMethod2, intValue);
            } catch (Exception e2) {
                try {
                    Method declaredMethod3 = ObjectInputStream.class.getDeclaredMethod("newInstance", new Class[]{Class.class, Class.class});
                    declaredMethod3.setAccessible(true);
                    return new C11963(declaredMethod3);
                } catch (Exception e3) {
                    return new C11974();
                }
            }
        }
    }

    public abstract <T> T zze(Class<T> cls) throws Exception;
}
