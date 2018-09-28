package com.fasterxml.jackson.annotation;

import com.google.android.gms.common.ConnectionResult;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import rx.internal.operators.OnSubscribeConcatMap;

@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@JacksonAnnotation
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonAutoDetect {

    /* renamed from: com.fasterxml.jackson.annotation.JsonAutoDetect.1 */
    static /* synthetic */ class C04841 {
        static final /* synthetic */ int[] f7x23d16a11;

        static {
            f7x23d16a11 = new int[Visibility.values().length];
            try {
                f7x23d16a11[Visibility.ANY.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f7x23d16a11[Visibility.NONE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f7x23d16a11[Visibility.NON_PRIVATE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f7x23d16a11[Visibility.PROTECTED_AND_PUBLIC.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                f7x23d16a11[Visibility.PUBLIC_ONLY.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
        }
    }

    public enum Visibility {
        ANY,
        NON_PRIVATE,
        PROTECTED_AND_PUBLIC,
        PUBLIC_ONLY,
        NONE,
        DEFAULT;

        public boolean isVisible(Member member) {
            switch (C04841.f7x23d16a11[ordinal()]) {
                case OnSubscribeConcatMap.BOUNDARY /*1*/:
                    return true;
                case OnSubscribeConcatMap.END /*2*/:
                    return false;
                case ConnectionResult.SERVICE_DISABLED /*3*/:
                    if (Modifier.isPrivate(member.getModifiers())) {
                        return false;
                    }
                    return true;
                case ConnectionResult.SIGN_IN_REQUIRED /*4*/:
                    if (Modifier.isProtected(member.getModifiers())) {
                        return true;
                    }
                    break;
                case ConnectionResult.INVALID_ACCOUNT /*5*/:
                    break;
                default:
                    return false;
            }
            return Modifier.isPublic(member.getModifiers());
        }
    }

    Visibility creatorVisibility() default Visibility.DEFAULT;

    Visibility fieldVisibility() default Visibility.DEFAULT;

    Visibility getterVisibility() default Visibility.DEFAULT;

    Visibility isGetterVisibility() default Visibility.DEFAULT;

    Visibility setterVisibility() default Visibility.DEFAULT;
}
