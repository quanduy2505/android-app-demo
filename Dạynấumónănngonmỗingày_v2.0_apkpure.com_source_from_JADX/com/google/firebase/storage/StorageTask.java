package com.google.firebase.storage;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.media.TransportMediator;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v7.widget.helper.ItemTouchHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.RuntimeExecutionException;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.storage.zze.zza;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.Executor;
import rx.internal.operators.OnSubscribeConcatMap;

public abstract class StorageTask<TResult extends ProvideError> extends ControllableTask<TResult> {
    private static final HashMap<Integer, HashSet<Integer>> zzckU;
    private static final HashMap<Integer, HashSet<Integer>> zzckV;
    protected final Object mSyncObject;
    private volatile int zzMf;
    @VisibleForTesting
    final zze<OnSuccessListener<? super TResult>, TResult> zzckW;
    @VisibleForTesting
    final zze<OnFailureListener, TResult> zzckX;
    @VisibleForTesting
    final zze<OnCompleteListener<TResult>, TResult> zzckY;
    @VisibleForTesting
    final zze<OnProgressListener<? super TResult>, TResult> zzckZ;
    @VisibleForTesting
    final zze<OnPausedListener<? super TResult>, TResult> zzcla;
    private TResult zzclb;

    /* renamed from: com.google.firebase.storage.StorageTask.8 */
    class C07268 implements Runnable {
        final /* synthetic */ StorageTask zzclc;

        C07268(StorageTask storageTask) {
            this.zzclc = storageTask;
        }

        public void run() {
            try {
                this.zzclc.run();
            } finally {
                this.zzclc.zzaaU();
            }
        }
    }

    protected interface ProvideError {
        Exception getError();
    }

    /* renamed from: com.google.firebase.storage.StorageTask.1 */
    class C12241 implements zza<OnSuccessListener<? super TResult>, TResult> {
        final /* synthetic */ StorageTask zzclc;

        C12241(StorageTask storageTask) {
            this.zzclc = storageTask;
        }

        public void zza(@NonNull OnSuccessListener<? super TResult> onSuccessListener, @NonNull TResult tResult) {
            zzc.zzaaV().zzc(this.zzclc);
            onSuccessListener.onSuccess(tResult);
        }

        public /* synthetic */ void zzl(@NonNull Object obj, @NonNull Object obj2) {
            zza((OnSuccessListener) obj, (ProvideError) obj2);
        }
    }

    /* renamed from: com.google.firebase.storage.StorageTask.2 */
    class C12252 implements zza<OnFailureListener, TResult> {
        final /* synthetic */ StorageTask zzclc;

        C12252(StorageTask storageTask) {
            this.zzclc = storageTask;
        }

        public void zza(@NonNull OnFailureListener onFailureListener, @NonNull TResult tResult) {
            zzc.zzaaV().zzc(this.zzclc);
            onFailureListener.onFailure(tResult.getError());
        }

        public /* synthetic */ void zzl(@NonNull Object obj, @NonNull Object obj2) {
            zza((OnFailureListener) obj, (ProvideError) obj2);
        }
    }

    /* renamed from: com.google.firebase.storage.StorageTask.3 */
    class C12263 implements zza<OnCompleteListener<TResult>, TResult> {
        final /* synthetic */ StorageTask zzclc;

        C12263(StorageTask storageTask) {
            this.zzclc = storageTask;
        }

        public void zza(@NonNull OnCompleteListener<TResult> onCompleteListener, @NonNull TResult tResult) {
            zzc.zzaaV().zzc(this.zzclc);
            onCompleteListener.onComplete(this.zzclc);
        }

        public /* synthetic */ void zzl(@NonNull Object obj, @NonNull Object obj2) {
            zza((OnCompleteListener) obj, (ProvideError) obj2);
        }
    }

    /* renamed from: com.google.firebase.storage.StorageTask.4 */
    class C12274 implements zza<OnProgressListener<? super TResult>, TResult> {
        C12274(StorageTask storageTask) {
        }

        public void zza(@NonNull OnProgressListener<? super TResult> onProgressListener, @NonNull TResult tResult) {
            onProgressListener.onProgress(tResult);
        }

        public /* synthetic */ void zzl(@NonNull Object obj, @NonNull Object obj2) {
            zza((OnProgressListener) obj, (ProvideError) obj2);
        }
    }

    /* renamed from: com.google.firebase.storage.StorageTask.5 */
    class C12285 implements zza<OnPausedListener<? super TResult>, TResult> {
        C12285(StorageTask storageTask) {
        }

        public void zza(@NonNull OnPausedListener<? super TResult> onPausedListener, @NonNull TResult tResult) {
            onPausedListener.onPaused(tResult);
        }

        public /* synthetic */ void zzl(@NonNull Object obj, @NonNull Object obj2) {
            zza((OnPausedListener) obj, (ProvideError) obj2);
        }
    }

    /* renamed from: com.google.firebase.storage.StorageTask.6 */
    class C12296 implements OnCompleteListener<TResult> {
        final /* synthetic */ TaskCompletionSource zzaFb;
        final /* synthetic */ StorageTask zzclc;
        final /* synthetic */ Continuation zzcld;

        C12296(StorageTask storageTask, Continuation continuation, TaskCompletionSource taskCompletionSource) {
            this.zzclc = storageTask;
            this.zzcld = continuation;
            this.zzaFb = taskCompletionSource;
        }

        public void onComplete(@NonNull Task<TResult> task) {
            try {
                Object then = this.zzcld.then(this.zzclc);
                if (!this.zzaFb.getTask().isComplete()) {
                    this.zzaFb.setResult(then);
                }
            } catch (Exception e) {
                if (e.getCause() instanceof Exception) {
                    this.zzaFb.setException((Exception) e.getCause());
                } else {
                    this.zzaFb.setException(e);
                }
            } catch (Exception e2) {
                this.zzaFb.setException(e2);
            }
        }
    }

    /* renamed from: com.google.firebase.storage.StorageTask.7 */
    class C12327 implements OnCompleteListener<TResult> {
        final /* synthetic */ TaskCompletionSource zzaFb;
        final /* synthetic */ StorageTask zzclc;
        final /* synthetic */ Continuation zzcld;

        /* renamed from: com.google.firebase.storage.StorageTask.7.1 */
        class C12301 implements OnSuccessListener<TContinuationResult> {
            final /* synthetic */ C12327 zzcle;

            C12301(C12327 c12327) {
                this.zzcle = c12327;
            }

            public void onSuccess(TContinuationResult tContinuationResult) {
                this.zzcle.zzaFb.setResult(tContinuationResult);
            }
        }

        /* renamed from: com.google.firebase.storage.StorageTask.7.2 */
        class C12312 implements OnFailureListener {
            final /* synthetic */ C12327 zzcle;

            C12312(C12327 c12327) {
                this.zzcle = c12327;
            }

            public void onFailure(@NonNull Exception exception) {
                this.zzcle.zzaFb.setException(exception);
            }
        }

        C12327(StorageTask storageTask, Continuation continuation, TaskCompletionSource taskCompletionSource) {
            this.zzclc = storageTask;
            this.zzcld = continuation;
            this.zzaFb = taskCompletionSource;
        }

        public void onComplete(@NonNull Task<TResult> task) {
            try {
                Task task2 = (Task) this.zzcld.then(this.zzclc);
                if (!this.zzaFb.getTask().isComplete()) {
                    if (task2 == null) {
                        this.zzaFb.setException(new NullPointerException("Continuation returned null"));
                        return;
                    }
                    task2.addOnSuccessListener(new C12301(this));
                    task2.addOnFailureListener(new C12312(this));
                }
            } catch (Exception e) {
                if (e.getCause() instanceof Exception) {
                    this.zzaFb.setException((Exception) e.getCause());
                } else {
                    this.zzaFb.setException(e);
                }
            } catch (Exception e2) {
                this.zzaFb.setException(e2);
            }
        }
    }

    @VisibleForTesting
    class SnapshotBase implements ProvideError {
        final /* synthetic */ StorageTask zzclc;
        private final Exception zzclf;

        public SnapshotBase(StorageTask storageTask, Exception exception) {
            this.zzclc = storageTask;
            if (exception != null) {
                this.zzclf = exception;
            } else if (storageTask.isCanceled()) {
                this.zzclf = StorageException.fromErrorStatus(Status.zzayl);
            } else if (storageTask.zzaaQ() == 64) {
                this.zzclf = StorageException.fromErrorStatus(Status.zzayj);
            } else {
                this.zzclf = null;
            }
        }

        @Nullable
        public Exception getError() {
            return this.zzclf;
        }

        @NonNull
        public StorageReference getStorage() {
            return getTask().getStorage();
        }

        @NonNull
        public StorageTask<TResult> getTask() {
            return this.zzclc;
        }
    }

    static {
        zzckU = new HashMap();
        zzckV = new HashMap();
        zzckU.put(Integer.valueOf(1), new HashSet(Arrays.asList(new Integer[]{Integer.valueOf(16), Integer.valueOf(AccessibilityNodeInfoCompat.ACTION_NEXT_AT_MOVEMENT_GRANULARITY)})));
        zzckU.put(Integer.valueOf(2), new HashSet(Arrays.asList(new Integer[]{Integer.valueOf(8), Integer.valueOf(32)})));
        zzckU.put(Integer.valueOf(4), new HashSet(Arrays.asList(new Integer[]{Integer.valueOf(8), Integer.valueOf(32)})));
        zzckU.put(Integer.valueOf(16), new HashSet(Arrays.asList(new Integer[]{Integer.valueOf(2), Integer.valueOf(AccessibilityNodeInfoCompat.ACTION_NEXT_AT_MOVEMENT_GRANULARITY)})));
        zzckU.put(Integer.valueOf(64), new HashSet(Arrays.asList(new Integer[]{Integer.valueOf(2), Integer.valueOf(AccessibilityNodeInfoCompat.ACTION_NEXT_AT_MOVEMENT_GRANULARITY)})));
        zzckV.put(Integer.valueOf(1), new HashSet(Arrays.asList(new Integer[]{Integer.valueOf(2), Integer.valueOf(64)})));
        zzckV.put(Integer.valueOf(2), new HashSet(Arrays.asList(new Integer[]{Integer.valueOf(4), Integer.valueOf(64), Integer.valueOf(TransportMediator.FLAG_KEY_MEDIA_NEXT)})));
        zzckV.put(Integer.valueOf(4), new HashSet(Arrays.asList(new Integer[]{Integer.valueOf(4), Integer.valueOf(64), Integer.valueOf(TransportMediator.FLAG_KEY_MEDIA_NEXT)})));
        zzckV.put(Integer.valueOf(8), new HashSet(Arrays.asList(new Integer[]{Integer.valueOf(16), Integer.valueOf(64), Integer.valueOf(TransportMediator.FLAG_KEY_MEDIA_NEXT)})));
        zzckV.put(Integer.valueOf(32), new HashSet(Arrays.asList(new Integer[]{Integer.valueOf(AccessibilityNodeInfoCompat.ACTION_NEXT_AT_MOVEMENT_GRANULARITY), Integer.valueOf(64), Integer.valueOf(TransportMediator.FLAG_KEY_MEDIA_NEXT)})));
    }

    protected StorageTask() {
        this.mSyncObject = new Object();
        this.zzckW = new zze(this, TransportMediator.FLAG_KEY_MEDIA_NEXT, new C12241(this));
        this.zzckX = new zze(this, 320, new C12252(this));
        this.zzckY = new zze(this, 448, new C12263(this));
        this.zzckZ = new zze(this, -465, new C12274(this));
        this.zzcla = new zze(this, 16, new C12285(this));
        this.zzMf = 1;
    }

    @NonNull
    private <TContinuationResult> Task<TContinuationResult> zza(@Nullable Executor executor, @NonNull Continuation<TResult, TContinuationResult> continuation) {
        TaskCompletionSource taskCompletionSource = new TaskCompletionSource();
        this.zzckY.zza(null, executor, new C12296(this, continuation, taskCompletionSource));
        return taskCompletionSource.getTask();
    }

    private TResult zzaaT() {
        if (this.zzclb != null) {
            return this.zzclb;
        }
        if (!isComplete()) {
            return null;
        }
        if (this.zzclb == null) {
            this.zzclb = zzaaS();
        }
        return this.zzclb;
    }

    private void zzaaU() {
        if (!isComplete() && !isPaused() && zzaaQ() != 2 && !zzf(AccessibilityNodeInfoCompat.ACTION_NEXT_AT_MOVEMENT_GRANULARITY, false)) {
            zzf(64, false);
        }
    }

    @NonNull
    private <TContinuationResult> Task<TContinuationResult> zzb(@Nullable Executor executor, @NonNull Continuation<TResult, Task<TContinuationResult>> continuation) {
        TaskCompletionSource taskCompletionSource = new TaskCompletionSource();
        this.zzckY.zza(null, executor, new C12327(this, continuation, taskCompletionSource));
        return taskCompletionSource.getTask();
    }

    private String zzpZ(int i) {
        switch (i) {
            case OnSubscribeConcatMap.BOUNDARY /*1*/:
                return "INTERNAL_STATE_NOT_STARTED";
            case OnSubscribeConcatMap.END /*2*/:
                return "INTERNAL_STATE_QUEUED";
            case ConnectionResult.SIGN_IN_REQUIRED /*4*/:
                return "INTERNAL_STATE_IN_PROGRESS";
            case ConnectionResult.INTERNAL_ERROR /*8*/:
                return "INTERNAL_STATE_PAUSING";
            case ConnectionResult.API_UNAVAILABLE /*16*/:
                return "INTERNAL_STATE_PAUSED";
            case ItemTouchHelper.END /*32*/:
                return "INTERNAL_STATE_CANCELING";
            case TransportMediator.FLAG_KEY_MEDIA_FAST_FORWARD /*64*/:
                return "INTERNAL_STATE_FAILURE";
            case TransportMediator.FLAG_KEY_MEDIA_NEXT /*128*/:
                return "INTERNAL_STATE_SUCCESS";
            case AccessibilityNodeInfoCompat.ACTION_NEXT_AT_MOVEMENT_GRANULARITY /*256*/:
                return "INTERNAL_STATE_CANCELED";
            default:
                return "Unknown Internal State!";
        }
    }

    @NonNull
    public StorageTask<TResult> addOnCompleteListener(@NonNull Activity activity, @NonNull OnCompleteListener<TResult> onCompleteListener) {
        zzac.zzw(onCompleteListener);
        zzac.zzw(activity);
        this.zzckY.zza(activity, null, onCompleteListener);
        return this;
    }

    @NonNull
    public StorageTask<TResult> addOnCompleteListener(@NonNull OnCompleteListener<TResult> onCompleteListener) {
        zzac.zzw(onCompleteListener);
        this.zzckY.zza(null, null, onCompleteListener);
        return this;
    }

    @NonNull
    public StorageTask<TResult> addOnCompleteListener(@NonNull Executor executor, @NonNull OnCompleteListener<TResult> onCompleteListener) {
        zzac.zzw(onCompleteListener);
        zzac.zzw(executor);
        this.zzckY.zza(null, executor, onCompleteListener);
        return this;
    }

    @NonNull
    public StorageTask<TResult> addOnFailureListener(@NonNull Activity activity, @NonNull OnFailureListener onFailureListener) {
        zzac.zzw(onFailureListener);
        zzac.zzw(activity);
        this.zzckX.zza(activity, null, onFailureListener);
        return this;
    }

    @NonNull
    public StorageTask<TResult> addOnFailureListener(@NonNull OnFailureListener onFailureListener) {
        zzac.zzw(onFailureListener);
        this.zzckX.zza(null, null, onFailureListener);
        return this;
    }

    @NonNull
    public StorageTask<TResult> addOnFailureListener(@NonNull Executor executor, @NonNull OnFailureListener onFailureListener) {
        zzac.zzw(onFailureListener);
        zzac.zzw(executor);
        this.zzckX.zza(null, executor, onFailureListener);
        return this;
    }

    public StorageTask<TResult> addOnPausedListener(@NonNull Activity activity, @NonNull OnPausedListener<? super TResult> onPausedListener) {
        zzac.zzw(onPausedListener);
        zzac.zzw(activity);
        this.zzcla.zza(activity, null, onPausedListener);
        return this;
    }

    public StorageTask<TResult> addOnPausedListener(@NonNull OnPausedListener<? super TResult> onPausedListener) {
        zzac.zzw(onPausedListener);
        this.zzcla.zza(null, null, onPausedListener);
        return this;
    }

    public StorageTask<TResult> addOnPausedListener(@NonNull Executor executor, @NonNull OnPausedListener<? super TResult> onPausedListener) {
        zzac.zzw(onPausedListener);
        zzac.zzw(executor);
        this.zzcla.zza(null, executor, onPausedListener);
        return this;
    }

    public StorageTask<TResult> addOnProgressListener(@NonNull Activity activity, @NonNull OnProgressListener<? super TResult> onProgressListener) {
        zzac.zzw(onProgressListener);
        zzac.zzw(activity);
        this.zzckZ.zza(activity, null, onProgressListener);
        return this;
    }

    public StorageTask<TResult> addOnProgressListener(@NonNull OnProgressListener<? super TResult> onProgressListener) {
        zzac.zzw(onProgressListener);
        this.zzckZ.zza(null, null, onProgressListener);
        return this;
    }

    public StorageTask<TResult> addOnProgressListener(@NonNull Executor executor, @NonNull OnProgressListener<? super TResult> onProgressListener) {
        zzac.zzw(onProgressListener);
        zzac.zzw(executor);
        this.zzckZ.zza(null, executor, onProgressListener);
        return this;
    }

    @NonNull
    public StorageTask<TResult> addOnSuccessListener(@NonNull Activity activity, @NonNull OnSuccessListener<? super TResult> onSuccessListener) {
        zzac.zzw(activity);
        zzac.zzw(onSuccessListener);
        this.zzckW.zza(activity, null, onSuccessListener);
        return this;
    }

    @NonNull
    public StorageTask<TResult> addOnSuccessListener(@NonNull OnSuccessListener<? super TResult> onSuccessListener) {
        zzac.zzw(onSuccessListener);
        this.zzckW.zza(null, null, onSuccessListener);
        return this;
    }

    @NonNull
    public StorageTask<TResult> addOnSuccessListener(@NonNull Executor executor, @NonNull OnSuccessListener<? super TResult> onSuccessListener) {
        zzac.zzw(executor);
        zzac.zzw(onSuccessListener);
        this.zzckW.zza(null, executor, onSuccessListener);
        return this;
    }

    public boolean cancel() {
        return zzf(AccessibilityNodeInfoCompat.ACTION_NEXT_AT_MOVEMENT_GRANULARITY, true) || zzf(32, true);
    }

    @NonNull
    public <TContinuationResult> Task<TContinuationResult> continueWith(@NonNull Continuation<TResult, TContinuationResult> continuation) {
        return zza(null, continuation);
    }

    @NonNull
    public <TContinuationResult> Task<TContinuationResult> continueWith(@NonNull Executor executor, @NonNull Continuation<TResult, TContinuationResult> continuation) {
        return zza(executor, continuation);
    }

    @NonNull
    public <TContinuationResult> Task<TContinuationResult> continueWithTask(@NonNull Continuation<TResult, Task<TContinuationResult>> continuation) {
        return zzb(null, continuation);
    }

    @NonNull
    public <TContinuationResult> Task<TContinuationResult> continueWithTask(@NonNull Executor executor, @NonNull Continuation<TResult, Task<TContinuationResult>> continuation) {
        return zzb(executor, continuation);
    }

    @Nullable
    public Exception getException() {
        return zzaaT() == null ? null : zzaaT().getError();
    }

    public TResult getResult() {
        if (zzaaT() == null) {
            throw new IllegalStateException();
        }
        Throwable error = zzaaT().getError();
        if (error == null) {
            return zzaaT();
        }
        throw new RuntimeExecutionException(error);
    }

    public <X extends Throwable> TResult getResult(@NonNull Class<X> cls) throws Throwable {
        if (zzaaT() == null) {
            throw new IllegalStateException();
        } else if (cls.isInstance(zzaaT().getError())) {
            throw ((Throwable) cls.cast(zzaaT().getError()));
        } else {
            Throwable error = zzaaT().getError();
            if (error == null) {
                return zzaaT();
            }
            throw new RuntimeExecutionException(error);
        }
    }

    public TResult getSnapshot() {
        return zzaaS();
    }

    @VisibleForTesting
    abstract StorageReference getStorage();

    public boolean isCanceled() {
        return zzaaQ() == AccessibilityNodeInfoCompat.ACTION_NEXT_AT_MOVEMENT_GRANULARITY;
    }

    public boolean isComplete() {
        return ((zzaaQ() & TransportMediator.FLAG_KEY_MEDIA_NEXT) == 0 && (zzaaQ() & 320) == 0) ? false : true;
    }

    public boolean isInProgress() {
        return (zzaaQ() & -465) != 0;
    }

    public boolean isPaused() {
        return (zzaaQ() & 16) != 0;
    }

    public boolean isSuccessful() {
        return (zzaaQ() & TransportMediator.FLAG_KEY_MEDIA_NEXT) != 0;
    }

    protected void onCanceled() {
    }

    protected void onFailure() {
    }

    protected void onPaused() {
    }

    protected void onProgress() {
    }

    protected void onQueued() {
    }

    protected void onSuccess() {
    }

    public boolean pause() {
        return zzf(16, true) || zzf(8, true);
    }

    public StorageTask<TResult> removeOnCompleteListener(@NonNull OnCompleteListener<TResult> onCompleteListener) {
        zzac.zzw(onCompleteListener);
        this.zzckY.zzaG(onCompleteListener);
        return this;
    }

    public StorageTask<TResult> removeOnFailureListener(@NonNull OnFailureListener onFailureListener) {
        zzac.zzw(onFailureListener);
        this.zzckX.zzaG(onFailureListener);
        return this;
    }

    public StorageTask<TResult> removeOnPausedListener(@NonNull OnPausedListener<? super TResult> onPausedListener) {
        zzac.zzw(onPausedListener);
        this.zzcla.zzaG(onPausedListener);
        return this;
    }

    public StorageTask<TResult> removeOnProgressListener(@NonNull OnProgressListener<? super TResult> onProgressListener) {
        zzac.zzw(onProgressListener);
        this.zzckZ.zzaG(onProgressListener);
        return this;
    }

    public StorageTask<TResult> removeOnSuccessListener(@NonNull OnSuccessListener<? super TResult> onSuccessListener) {
        zzac.zzw(onSuccessListener);
        this.zzckW.zzaG(onSuccessListener);
        return this;
    }

    @VisibleForTesting
    void resetState() {
    }

    public boolean resume() {
        if (!zzf(2, true)) {
            return false;
        }
        resetState();
        schedule();
        return true;
    }

    @VisibleForTesting
    abstract void run();

    @VisibleForTesting
    abstract void schedule();

    @VisibleForTesting
    Runnable zzTj() {
        return new C07268(this);
    }

    @VisibleForTesting
    @NonNull
    abstract TResult zzaaK();

    @VisibleForTesting
    boolean zzaaP() {
        if (!zzf(2, false)) {
            return false;
        }
        schedule();
        return true;
    }

    @VisibleForTesting
    int zzaaQ() {
        return this.zzMf;
    }

    @VisibleForTesting
    Object zzaaR() {
        return this.mSyncObject;
    }

    @VisibleForTesting
    @NonNull
    TResult zzaaS() {
        TResult zzaaK;
        synchronized (this.mSyncObject) {
            zzaaK = zzaaK();
        }
        return zzaaK;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    @android.support.annotation.VisibleForTesting
    boolean zzf(int r8, boolean r9) {
        /*
        r7 = this;
        r1 = r7.mSyncObject;
        monitor-enter(r1);
        r0 = "StorageTask";
        r2 = 3;
        r0 = android.util.Log.isLoggable(r0, r2);	 Catch:{ all -> 0x00ac }
        if (r0 == 0) goto L_0x005d;
    L_0x000c:
        r0 = "StorageTask";
        r2 = r7.zzpZ(r8);	 Catch:{ all -> 0x00ac }
        r2 = java.lang.String.valueOf(r2);	 Catch:{ all -> 0x00ac }
        r3 = r7.zzMf;	 Catch:{ all -> 0x00ac }
        r3 = r7.zzpZ(r3);	 Catch:{ all -> 0x00ac }
        r3 = java.lang.String.valueOf(r3);	 Catch:{ all -> 0x00ac }
        r4 = new java.lang.StringBuilder;	 Catch:{ all -> 0x00ac }
        r5 = java.lang.String.valueOf(r2);	 Catch:{ all -> 0x00ac }
        r5 = r5.length();	 Catch:{ all -> 0x00ac }
        r5 = r5 + 54;
        r6 = java.lang.String.valueOf(r3);	 Catch:{ all -> 0x00ac }
        r6 = r6.length();	 Catch:{ all -> 0x00ac }
        r5 = r5 + r6;
        r4.<init>(r5);	 Catch:{ all -> 0x00ac }
        r5 = "changing internal state to: ";
        r4 = r4.append(r5);	 Catch:{ all -> 0x00ac }
        r2 = r4.append(r2);	 Catch:{ all -> 0x00ac }
        r4 = " isUser: ";
        r2 = r2.append(r4);	 Catch:{ all -> 0x00ac }
        r2 = r2.append(r9);	 Catch:{ all -> 0x00ac }
        r4 = " from state:";
        r2 = r2.append(r4);	 Catch:{ all -> 0x00ac }
        r2 = r2.append(r3);	 Catch:{ all -> 0x00ac }
        r2 = r2.toString();	 Catch:{ all -> 0x00ac }
        android.util.Log.d(r0, r2);	 Catch:{ all -> 0x00ac }
    L_0x005d:
        if (r9 == 0) goto L_0x009e;
    L_0x005f:
        r0 = zzckU;	 Catch:{ all -> 0x00ac }
    L_0x0061:
        r2 = r7.zzaaQ();	 Catch:{ all -> 0x00ac }
        r2 = java.lang.Integer.valueOf(r2);	 Catch:{ all -> 0x00ac }
        r0 = r0.get(r2);	 Catch:{ all -> 0x00ac }
        r0 = (java.util.HashSet) r0;	 Catch:{ all -> 0x00ac }
        if (r0 == 0) goto L_0x00c3;
    L_0x0071:
        r2 = java.lang.Integer.valueOf(r8);	 Catch:{ all -> 0x00ac }
        r0 = r0.contains(r2);	 Catch:{ all -> 0x00ac }
        if (r0 == 0) goto L_0x00c3;
    L_0x007b:
        r7.zzMf = r8;	 Catch:{ all -> 0x00ac }
        r0 = r7.zzMf;	 Catch:{ all -> 0x00ac }
        switch(r0) {
            case 2: goto L_0x00a1;
            case 4: goto L_0x00af;
            case 16: goto L_0x00b3;
            case 64: goto L_0x00b7;
            case 128: goto L_0x00bb;
            case 256: goto L_0x00bf;
            default: goto L_0x0082;
        };	 Catch:{ all -> 0x00ac }
    L_0x0082:
        r0 = r7.zzckW;	 Catch:{ all -> 0x00ac }
        r0.zzaaZ();	 Catch:{ all -> 0x00ac }
        r0 = r7.zzckX;	 Catch:{ all -> 0x00ac }
        r0.zzaaZ();	 Catch:{ all -> 0x00ac }
        r0 = r7.zzckY;	 Catch:{ all -> 0x00ac }
        r0.zzaaZ();	 Catch:{ all -> 0x00ac }
        r0 = r7.zzcla;	 Catch:{ all -> 0x00ac }
        r0.zzaaZ();	 Catch:{ all -> 0x00ac }
        r0 = r7.zzckZ;	 Catch:{ all -> 0x00ac }
        r0.zzaaZ();	 Catch:{ all -> 0x00ac }
        r0 = 1;
        monitor-exit(r1);	 Catch:{ all -> 0x00ac }
    L_0x009d:
        return r0;
    L_0x009e:
        r0 = zzckV;	 Catch:{ all -> 0x00ac }
        goto L_0x0061;
    L_0x00a1:
        r0 = com.google.firebase.storage.zzc.zzaaV();	 Catch:{ all -> 0x00ac }
        r0.zzb(r7);	 Catch:{ all -> 0x00ac }
        r7.onQueued();	 Catch:{ all -> 0x00ac }
        goto L_0x0082;
    L_0x00ac:
        r0 = move-exception;
        monitor-exit(r1);	 Catch:{ all -> 0x00ac }
        throw r0;
    L_0x00af:
        r7.onProgress();	 Catch:{ all -> 0x00ac }
        goto L_0x0082;
    L_0x00b3:
        r7.onPaused();	 Catch:{ all -> 0x00ac }
        goto L_0x0082;
    L_0x00b7:
        r7.onFailure();	 Catch:{ all -> 0x00ac }
        goto L_0x0082;
    L_0x00bb:
        r7.onSuccess();	 Catch:{ all -> 0x00ac }
        goto L_0x0082;
    L_0x00bf:
        r7.onCanceled();	 Catch:{ all -> 0x00ac }
        goto L_0x0082;
    L_0x00c3:
        r0 = "StorageTask";
        r2 = r7.zzpZ(r8);	 Catch:{ all -> 0x00ac }
        r2 = java.lang.String.valueOf(r2);	 Catch:{ all -> 0x00ac }
        r3 = r7.zzMf;	 Catch:{ all -> 0x00ac }
        r3 = r7.zzpZ(r3);	 Catch:{ all -> 0x00ac }
        r3 = java.lang.String.valueOf(r3);	 Catch:{ all -> 0x00ac }
        r4 = new java.lang.StringBuilder;	 Catch:{ all -> 0x00ac }
        r5 = java.lang.String.valueOf(r2);	 Catch:{ all -> 0x00ac }
        r5 = r5.length();	 Catch:{ all -> 0x00ac }
        r5 = r5 + 62;
        r6 = java.lang.String.valueOf(r3);	 Catch:{ all -> 0x00ac }
        r6 = r6.length();	 Catch:{ all -> 0x00ac }
        r5 = r5 + r6;
        r4.<init>(r5);	 Catch:{ all -> 0x00ac }
        r5 = "unable to change internal state to: ";
        r4 = r4.append(r5);	 Catch:{ all -> 0x00ac }
        r2 = r4.append(r2);	 Catch:{ all -> 0x00ac }
        r4 = " isUser: ";
        r2 = r2.append(r4);	 Catch:{ all -> 0x00ac }
        r2 = r2.append(r9);	 Catch:{ all -> 0x00ac }
        r4 = " from state:";
        r2 = r2.append(r4);	 Catch:{ all -> 0x00ac }
        r2 = r2.append(r3);	 Catch:{ all -> 0x00ac }
        r2 = r2.toString();	 Catch:{ all -> 0x00ac }
        android.util.Log.w(r0, r2);	 Catch:{ all -> 0x00ac }
        r0 = 0;
        monitor-exit(r1);	 Catch:{ all -> 0x00ac }
        goto L_0x009d;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.firebase.storage.StorageTask.zzf(int, boolean):boolean");
    }
}
