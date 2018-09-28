package com.fasterxml.jackson.databind.introspect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import rx.android.BuildConfig;

public final class MemberKey {
    static final Class<?>[] NO_CLASSES;
    final Class<?>[] _argTypes;
    final String _name;

    static {
        NO_CLASSES = new Class[0];
    }

    public MemberKey(Method method) {
        this(method.getName(), method.getParameterTypes());
    }

    public MemberKey(Constructor<?> constructor) {
        this(BuildConfig.VERSION_NAME, constructor.getParameterTypes());
    }

    public MemberKey(String str, Class<?>[] clsArr) {
        this._name = str;
        if (clsArr == null) {
            clsArr = NO_CLASSES;
        }
        this._argTypes = clsArr;
    }

    public String toString() {
        return this._name + "(" + this._argTypes.length + "-args)";
    }

    public int hashCode() {
        return this._name.hashCode() + this._argTypes.length;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        MemberKey memberKey = (MemberKey) obj;
        if (!this._name.equals(memberKey._name)) {
            return false;
        }
        Class[] clsArr = memberKey._argTypes;
        int length = this._argTypes.length;
        if (clsArr.length != length) {
            return false;
        }
        for (int i = 0; i < length; i++) {
            Class cls = clsArr[i];
            Class cls2 = this._argTypes[i];
            if (cls != cls2 && !cls.isAssignableFrom(cls2) && !cls2.isAssignableFrom(cls)) {
                return false;
            }
        }
        return true;
    }
}
