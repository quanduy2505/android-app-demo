package rx.internal.util;

import java.security.AccessController;
import java.security.PrivilegedAction;

public final class PlatformDependent {
    private static final int ANDROID_API_VERSION;
    public static final int ANDROID_API_VERSION_IS_NOT_ANDROID = 0;
    private static final boolean IS_ANDROID;

    /* renamed from: rx.internal.util.PlatformDependent.1 */
    static class C08191 implements PrivilegedAction<ClassLoader> {
        C08191() {
        }

        public ClassLoader run() {
            return ClassLoader.getSystemClassLoader();
        }
    }

    static {
        ANDROID_API_VERSION = resolveAndroidApiVersion();
        IS_ANDROID = ANDROID_API_VERSION != 0;
    }

    public static boolean isAndroid() {
        return IS_ANDROID;
    }

    public static int getAndroidApiVersion() {
        return ANDROID_API_VERSION;
    }

    private static int resolveAndroidApiVersion() {
        try {
            return ((Integer) Class.forName("android.os.Build$VERSION", true, getSystemClassLoader()).getField("SDK_INT").get(null)).intValue();
        } catch (Exception e) {
            return 0;
        }
    }

    static ClassLoader getSystemClassLoader() {
        if (System.getSecurityManager() == null) {
            return ClassLoader.getSystemClassLoader();
        }
        return (ClassLoader) AccessController.doPrivileged(new C08191());
    }
}
