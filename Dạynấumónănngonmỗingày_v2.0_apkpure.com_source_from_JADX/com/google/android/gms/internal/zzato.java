package com.google.android.gms.internal;

import android.content.Context;
import android.os.Looper;
import android.support.annotation.NonNull;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.util.zze;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicLong;

public class zzato extends zzats {
    private static final AtomicLong zzbsV;
    private zzd zzbsM;
    private zzd zzbsN;
    private final PriorityBlockingQueue<FutureTask<?>> zzbsO;
    private final BlockingQueue<FutureTask<?>> zzbsP;
    private final UncaughtExceptionHandler zzbsQ;
    private final UncaughtExceptionHandler zzbsR;
    private final Object zzbsS;
    private final Semaphore zzbsT;
    private volatile boolean zzbsU;

    static class zza extends RuntimeException {
    }

    private final class zzb implements UncaughtExceptionHandler {
        private final String zzbsW;
        final /* synthetic */ zzato zzbsX;

        public zzb(zzato com_google_android_gms_internal_zzato, String str) {
            this.zzbsX = com_google_android_gms_internal_zzato;
            zzac.zzw(str);
            this.zzbsW = str;
        }

        public synchronized void uncaughtException(Thread thread, Throwable th) {
            this.zzbsX.zzJt().zzLa().zzj(this.zzbsW, th);
        }
    }

    private final class zzc<V> extends FutureTask<V> implements Comparable<zzc> {
        private final String zzbsW;
        final /* synthetic */ zzato zzbsX;
        private final long zzbsY;
        private final boolean zzbsZ;

        zzc(zzato com_google_android_gms_internal_zzato, Runnable runnable, boolean z, String str) {
            this.zzbsX = com_google_android_gms_internal_zzato;
            super(runnable, null);
            zzac.zzw(str);
            this.zzbsY = zzato.zzbsV.getAndIncrement();
            this.zzbsW = str;
            this.zzbsZ = z;
            if (this.zzbsY == Long.MAX_VALUE) {
                com_google_android_gms_internal_zzato.zzJt().zzLa().log("Tasks index overflow");
            }
        }

        zzc(zzato com_google_android_gms_internal_zzato, Callable<V> callable, boolean z, String str) {
            this.zzbsX = com_google_android_gms_internal_zzato;
            super(callable);
            zzac.zzw(str);
            this.zzbsY = zzato.zzbsV.getAndIncrement();
            this.zzbsW = str;
            this.zzbsZ = z;
            if (this.zzbsY == Long.MAX_VALUE) {
                com_google_android_gms_internal_zzato.zzJt().zzLa().log("Tasks index overflow");
            }
        }

        public /* synthetic */ int compareTo(@NonNull Object obj) {
            return zzb((zzc) obj);
        }

        protected void setException(Throwable th) {
            this.zzbsX.zzJt().zzLa().zzj(this.zzbsW, th);
            if (th instanceof zza) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), th);
            }
            super.setException(th);
        }

        public int zzb(@NonNull zzc com_google_android_gms_internal_zzato_zzc) {
            if (this.zzbsZ != com_google_android_gms_internal_zzato_zzc.zzbsZ) {
                return this.zzbsZ ? -1 : 1;
            } else {
                if (this.zzbsY < com_google_android_gms_internal_zzato_zzc.zzbsY) {
                    return -1;
                }
                if (this.zzbsY > com_google_android_gms_internal_zzato_zzc.zzbsY) {
                    return 1;
                }
                this.zzbsX.zzJt().zzLb().zzj("Two tasks share the same index. index", Long.valueOf(this.zzbsY));
                return 0;
            }
        }
    }

    private final class zzd extends Thread {
        final /* synthetic */ zzato zzbsX;
        private final Object zzbta;
        private final BlockingQueue<FutureTask<?>> zzbtb;

        public zzd(zzato com_google_android_gms_internal_zzato, String str, BlockingQueue<FutureTask<?>> blockingQueue) {
            this.zzbsX = com_google_android_gms_internal_zzato;
            zzac.zzw(str);
            zzac.zzw(blockingQueue);
            this.zzbta = new Object();
            this.zzbtb = blockingQueue;
            setName(str);
        }

        private void zza(InterruptedException interruptedException) {
            this.zzbsX.zzJt().zzLc().zzj(String.valueOf(getName()).concat(" was interrupted"), interruptedException);
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
            r4 = this;
            r0 = 0;
            r1 = r0;
        L_0x0002:
            if (r1 != 0) goto L_0x0015;
        L_0x0004:
            r0 = r4.zzbsX;	 Catch:{ InterruptedException -> 0x0010 }
            r0 = r0.zzbsT;	 Catch:{ InterruptedException -> 0x0010 }
            r0.acquire();	 Catch:{ InterruptedException -> 0x0010 }
            r0 = 1;
            r1 = r0;
            goto L_0x0002;
        L_0x0010:
            r0 = move-exception;
            r4.zza(r0);
            goto L_0x0002;
        L_0x0015:
            r0 = r4.zzbtb;	 Catch:{ all -> 0x0023 }
            r0 = r0.poll();	 Catch:{ all -> 0x0023 }
            r0 = (java.util.concurrent.FutureTask) r0;	 Catch:{ all -> 0x0023 }
            if (r0 == 0) goto L_0x004d;
        L_0x001f:
            r0.run();	 Catch:{ all -> 0x0023 }
            goto L_0x0015;
        L_0x0023:
            r0 = move-exception;
            r1 = r4.zzbsX;
            r1 = r1.zzbsS;
            monitor-enter(r1);
            r2 = r4.zzbsX;	 Catch:{ all -> 0x00e1 }
            r2 = r2.zzbsT;	 Catch:{ all -> 0x00e1 }
            r2.release();	 Catch:{ all -> 0x00e1 }
            r2 = r4.zzbsX;	 Catch:{ all -> 0x00e1 }
            r2 = r2.zzbsS;	 Catch:{ all -> 0x00e1 }
            r2.notifyAll();	 Catch:{ all -> 0x00e1 }
            r2 = r4.zzbsX;	 Catch:{ all -> 0x00e1 }
            r2 = r2.zzbsM;	 Catch:{ all -> 0x00e1 }
            if (r4 != r2) goto L_0x00d1;
        L_0x0045:
            r2 = r4.zzbsX;	 Catch:{ all -> 0x00e1 }
            r3 = 0;
            r2.zzbsM = r3;	 Catch:{ all -> 0x00e1 }
        L_0x004b:
            monitor-exit(r1);	 Catch:{ all -> 0x00e1 }
            throw r0;
        L_0x004d:
            r1 = r4.zzbta;	 Catch:{ all -> 0x0023 }
            monitor-enter(r1);	 Catch:{ all -> 0x0023 }
            r0 = r4.zzbtb;	 Catch:{ all -> 0x00a6 }
            r0 = r0.peek();	 Catch:{ all -> 0x00a6 }
            if (r0 != 0) goto L_0x0067;
        L_0x0058:
            r0 = r4.zzbsX;	 Catch:{ all -> 0x00a6 }
            r0 = r0.zzbsU;	 Catch:{ all -> 0x00a6 }
            if (r0 != 0) goto L_0x0067;
        L_0x0060:
            r0 = r4.zzbta;	 Catch:{ InterruptedException -> 0x00a1 }
            r2 = 30000; // 0x7530 float:4.2039E-41 double:1.4822E-319;
            r0.wait(r2);	 Catch:{ InterruptedException -> 0x00a1 }
        L_0x0067:
            monitor-exit(r1);	 Catch:{ all -> 0x00a6 }
            r0 = r4.zzbsX;	 Catch:{ all -> 0x0023 }
            r1 = r0.zzbsS;	 Catch:{ all -> 0x0023 }
            monitor-enter(r1);	 Catch:{ all -> 0x0023 }
            r0 = r4.zzbtb;	 Catch:{ all -> 0x00ce }
            r0 = r0.peek();	 Catch:{ all -> 0x00ce }
            if (r0 != 0) goto L_0x00cb;
        L_0x0077:
            monitor-exit(r1);	 Catch:{ all -> 0x00ce }
            r0 = r4.zzbsX;
            r1 = r0.zzbsS;
            monitor-enter(r1);
            r0 = r4.zzbsX;	 Catch:{ all -> 0x00b8 }
            r0 = r0.zzbsT;	 Catch:{ all -> 0x00b8 }
            r0.release();	 Catch:{ all -> 0x00b8 }
            r0 = r4.zzbsX;	 Catch:{ all -> 0x00b8 }
            r0 = r0.zzbsS;	 Catch:{ all -> 0x00b8 }
            r0.notifyAll();	 Catch:{ all -> 0x00b8 }
            r0 = r4.zzbsX;	 Catch:{ all -> 0x00b8 }
            r0 = r0.zzbsM;	 Catch:{ all -> 0x00b8 }
            if (r4 != r0) goto L_0x00a9;
        L_0x0099:
            r0 = r4.zzbsX;	 Catch:{ all -> 0x00b8 }
            r2 = 0;
            r0.zzbsM = r2;	 Catch:{ all -> 0x00b8 }
        L_0x009f:
            monitor-exit(r1);	 Catch:{ all -> 0x00b8 }
            return;
        L_0x00a1:
            r0 = move-exception;
            r4.zza(r0);	 Catch:{ all -> 0x00a6 }
            goto L_0x0067;
        L_0x00a6:
            r0 = move-exception;
            monitor-exit(r1);	 Catch:{ all -> 0x00a6 }
            throw r0;	 Catch:{ all -> 0x0023 }
        L_0x00a9:
            r0 = r4.zzbsX;	 Catch:{ all -> 0x00b8 }
            r0 = r0.zzbsN;	 Catch:{ all -> 0x00b8 }
            if (r4 != r0) goto L_0x00bb;
        L_0x00b1:
            r0 = r4.zzbsX;	 Catch:{ all -> 0x00b8 }
            r2 = 0;
            r0.zzbsN = r2;	 Catch:{ all -> 0x00b8 }
            goto L_0x009f;
        L_0x00b8:
            r0 = move-exception;
            monitor-exit(r1);	 Catch:{ all -> 0x00b8 }
            throw r0;
        L_0x00bb:
            r0 = r4.zzbsX;	 Catch:{ all -> 0x00b8 }
            r0 = r0.zzJt();	 Catch:{ all -> 0x00b8 }
            r0 = r0.zzLa();	 Catch:{ all -> 0x00b8 }
            r2 = "Current scheduler thread is neither worker nor network";
            r0.log(r2);	 Catch:{ all -> 0x00b8 }
            goto L_0x009f;
        L_0x00cb:
            monitor-exit(r1);	 Catch:{ all -> 0x00ce }
            goto L_0x0015;
        L_0x00ce:
            r0 = move-exception;
            monitor-exit(r1);	 Catch:{ all -> 0x00ce }
            throw r0;	 Catch:{ all -> 0x0023 }
        L_0x00d1:
            r2 = r4.zzbsX;	 Catch:{ all -> 0x00e1 }
            r2 = r2.zzbsN;	 Catch:{ all -> 0x00e1 }
            if (r4 != r2) goto L_0x00e4;
        L_0x00d9:
            r2 = r4.zzbsX;	 Catch:{ all -> 0x00e1 }
            r3 = 0;
            r2.zzbsN = r3;	 Catch:{ all -> 0x00e1 }
            goto L_0x004b;
        L_0x00e1:
            r0 = move-exception;
            monitor-exit(r1);	 Catch:{ all -> 0x00e1 }
            throw r0;
        L_0x00e4:
            r2 = r4.zzbsX;	 Catch:{ all -> 0x00e1 }
            r2 = r2.zzJt();	 Catch:{ all -> 0x00e1 }
            r2 = r2.zzLa();	 Catch:{ all -> 0x00e1 }
            r3 = "Current scheduler thread is neither worker nor network";
            r2.log(r3);	 Catch:{ all -> 0x00e1 }
            goto L_0x004b;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.zzato.zzd.run():void");
        }

        public void zzhf() {
            synchronized (this.zzbta) {
                this.zzbta.notifyAll();
            }
        }
    }

    static {
        zzbsV = new AtomicLong(Long.MIN_VALUE);
    }

    zzato(zzatp com_google_android_gms_internal_zzatp) {
        super(com_google_android_gms_internal_zzatp);
        this.zzbsS = new Object();
        this.zzbsT = new Semaphore(2);
        this.zzbsO = new PriorityBlockingQueue();
        this.zzbsP = new LinkedBlockingQueue();
        this.zzbsQ = new zzb(this, "Thread death: Uncaught exception on worker thread");
        this.zzbsR = new zzb(this, "Thread death: Uncaught exception on network thread");
    }

    private void zza(zzc<?> com_google_android_gms_internal_zzato_zzc_) {
        synchronized (this.zzbsS) {
            this.zzbsO.add(com_google_android_gms_internal_zzato_zzc_);
            if (this.zzbsM == null) {
                this.zzbsM = new zzd(this, "Measurement Worker", this.zzbsO);
                this.zzbsM.setUncaughtExceptionHandler(this.zzbsQ);
                this.zzbsM.start();
            } else {
                this.zzbsM.zzhf();
            }
        }
    }

    private void zza(FutureTask<?> futureTask) {
        synchronized (this.zzbsS) {
            this.zzbsP.add(futureTask);
            if (this.zzbsN == null) {
                this.zzbsN = new zzd(this, "Measurement Network", this.zzbsP);
                this.zzbsN.setUncaughtExceptionHandler(this.zzbsR);
                this.zzbsN.start();
            } else {
                this.zzbsN.zzhf();
            }
        }
    }

    public /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    public /* bridge */ /* synthetic */ void zzJd() {
        super.zzJd();
    }

    public /* bridge */ /* synthetic */ void zzJe() {
        super.zzJe();
    }

    public void zzJf() {
        if (Thread.currentThread() != this.zzbsN) {
            throw new IllegalStateException("Call expected from network thread");
        }
    }

    public /* bridge */ /* synthetic */ zzaso zzJg() {
        return super.zzJg();
    }

    public /* bridge */ /* synthetic */ zzass zzJh() {
        return super.zzJh();
    }

    public /* bridge */ /* synthetic */ zzatu zzJi() {
        return super.zzJi();
    }

    public /* bridge */ /* synthetic */ zzatf zzJj() {
        return super.zzJj();
    }

    public /* bridge */ /* synthetic */ zzasw zzJk() {
        return super.zzJk();
    }

    public /* bridge */ /* synthetic */ zzatw zzJl() {
        return super.zzJl();
    }

    public /* bridge */ /* synthetic */ zzatv zzJm() {
        return super.zzJm();
    }

    public /* bridge */ /* synthetic */ zzatg zzJn() {
        return super.zzJn();
    }

    public /* bridge */ /* synthetic */ zzasu zzJo() {
        return super.zzJo();
    }

    public /* bridge */ /* synthetic */ zzaue zzJp() {
        return super.zzJp();
    }

    public /* bridge */ /* synthetic */ zzatn zzJq() {
        return super.zzJq();
    }

    public /* bridge */ /* synthetic */ zzaty zzJr() {
        return super.zzJr();
    }

    public /* bridge */ /* synthetic */ zzato zzJs() {
        return super.zzJs();
    }

    public /* bridge */ /* synthetic */ zzati zzJt() {
        return super.zzJt();
    }

    public /* bridge */ /* synthetic */ zzatl zzJu() {
        return super.zzJu();
    }

    public /* bridge */ /* synthetic */ zzast zzJv() {
        return super.zzJv();
    }

    public boolean zzLr() {
        return Thread.currentThread() == this.zzbsM;
    }

    public boolean zzbd() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public <V> Future<V> zzd(Callable<V> callable) throws IllegalStateException {
        zznA();
        zzac.zzw(callable);
        zzc com_google_android_gms_internal_zzato_zzc = new zzc(this, (Callable) callable, false, "Task exception on worker thread");
        if (Thread.currentThread() == this.zzbsM) {
            com_google_android_gms_internal_zzato_zzc.run();
        } else {
            zza(com_google_android_gms_internal_zzato_zzc);
        }
        return com_google_android_gms_internal_zzato_zzc;
    }

    public <V> Future<V> zze(Callable<V> callable) throws IllegalStateException {
        zznA();
        zzac.zzw(callable);
        zzc com_google_android_gms_internal_zzato_zzc = new zzc(this, (Callable) callable, true, "Task exception on worker thread");
        if (Thread.currentThread() == this.zzbsM) {
            com_google_android_gms_internal_zzato_zzc.run();
        } else {
            zza(com_google_android_gms_internal_zzato_zzc);
        }
        return com_google_android_gms_internal_zzato_zzc;
    }

    public void zzm(Runnable runnable) throws IllegalStateException {
        zznA();
        zzac.zzw(runnable);
        zza(new zzc(this, runnable, false, "Task exception on worker thread"));
    }

    public void zzmq() {
        if (Thread.currentThread() != this.zzbsM) {
            throw new IllegalStateException("Call expected from worker thread");
        }
    }

    protected void zzmr() {
    }

    public void zzn(Runnable runnable) throws IllegalStateException {
        zznA();
        zzac.zzw(runnable);
        zza(new zzc(this, runnable, false, "Task exception on network thread"));
    }

    public /* bridge */ /* synthetic */ zze zznq() {
        return super.zznq();
    }
}
