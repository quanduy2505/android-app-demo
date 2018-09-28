package com.facebook.internal;

import com.facebook.FacebookSdk;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;

public class LockOnGetVariable<T> {
    private CountDownLatch initLatch;
    private T value;

    /* renamed from: com.facebook.internal.LockOnGetVariable.1 */
    class C04171 implements Callable<Void> {
        final /* synthetic */ Callable val$callable;

        C04171(Callable callable) {
            this.val$callable = callable;
        }

        public Void call() throws Exception {
            try {
                LockOnGetVariable.this.value = this.val$callable.call();
                return null;
            } finally {
                LockOnGetVariable.this.initLatch.countDown();
            }
        }
    }

    public LockOnGetVariable(T value) {
        this.value = value;
    }

    public LockOnGetVariable(Callable<T> callable) {
        this.initLatch = new CountDownLatch(1);
        FacebookSdk.getExecutor().execute(new FutureTask(new C04171(callable)));
    }

    public T getValue() {
        waitOnInit();
        return this.value;
    }

    private void waitOnInit() {
        if (this.initLatch != null) {
            try {
                this.initLatch.await();
            } catch (InterruptedException e) {
            }
        }
    }
}
