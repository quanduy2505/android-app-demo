package bolts;

import android.annotation.SuppressLint;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Looper;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

final class AndroidExecutors {
    static final int CORE_POOL_SIZE;
    private static final int CPU_COUNT;
    private static final AndroidExecutors INSTANCE;
    static final long KEEP_ALIVE_TIME = 1;
    static final int MAX_POOL_SIZE;
    private final Executor uiThread;

    private static class UIThreadExecutor implements Executor {
        private UIThreadExecutor() {
        }

        public void execute(Runnable command) {
            new Handler(Looper.getMainLooper()).post(command);
        }
    }

    static {
        INSTANCE = new AndroidExecutors();
        CPU_COUNT = Runtime.getRuntime().availableProcessors();
        CORE_POOL_SIZE = CPU_COUNT + 1;
        MAX_POOL_SIZE = (CPU_COUNT * 2) + 1;
    }

    private AndroidExecutors() {
        this.uiThread = new UIThreadExecutor();
    }

    public static ExecutorService newCachedThreadPool() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, new LinkedBlockingQueue());
        allowCoreThreadTimeout(executor, true);
        return executor;
    }

    public static ExecutorService newCachedThreadPool(ThreadFactory threadFactory) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, new LinkedBlockingQueue(), threadFactory);
        allowCoreThreadTimeout(executor, true);
        return executor;
    }

    @SuppressLint({"NewApi"})
    public static void allowCoreThreadTimeout(ThreadPoolExecutor executor, boolean value) {
        if (VERSION.SDK_INT >= 9) {
            executor.allowCoreThreadTimeOut(value);
        }
    }

    public static Executor uiThread() {
        return INSTANCE.uiThread;
    }
}
