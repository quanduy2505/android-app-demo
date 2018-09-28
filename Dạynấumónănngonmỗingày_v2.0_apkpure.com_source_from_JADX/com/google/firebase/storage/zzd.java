package com.google.firebase.storage;

import android.support.annotation.NonNull;
import android.support.v4.media.TransportMediator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class zzd {
    public static zzd zzcli;
    private static BlockingQueue<Runnable> zzclj;
    private static final ThreadPoolExecutor zzclk;
    private static BlockingQueue<Runnable> zzcll;
    private static final ThreadPoolExecutor zzclm;
    private static BlockingQueue<Runnable> zzcln;
    private static final ThreadPoolExecutor zzclo;
    private static BlockingQueue<Runnable> zzclp;
    private static final ThreadPoolExecutor zzclq;

    static class zza implements ThreadFactory {
        private final AtomicInteger zzbfT;
        private final String zzclr;

        zza(@NonNull String str) {
            this.zzbfT = new AtomicInteger(1);
            this.zzclr = str;
        }

        public Thread newThread(@NonNull Runnable runnable) {
            String str = this.zzclr;
            Thread thread = new Thread(runnable, new StringBuilder(String.valueOf(str).length() + 27).append("FirebaseStorage-").append(str).append(this.zzbfT.getAndIncrement()).toString());
            thread.setDaemon(false);
            thread.setPriority(9);
            return thread;
        }
    }

    static {
        zzcli = new zzd();
        zzclj = new LinkedBlockingQueue(TransportMediator.FLAG_KEY_MEDIA_NEXT);
        zzclk = new ThreadPoolExecutor(5, 5, 5, TimeUnit.SECONDS, zzclj, new zza("Command-"));
        zzcll = new LinkedBlockingQueue(TransportMediator.FLAG_KEY_MEDIA_NEXT);
        zzclm = new ThreadPoolExecutor(2, 2, 5, TimeUnit.SECONDS, zzcll, new zza("Upload-"));
        zzcln = new LinkedBlockingQueue(TransportMediator.FLAG_KEY_MEDIA_NEXT);
        zzclo = new ThreadPoolExecutor(3, 3, 5, TimeUnit.SECONDS, zzcln, new zza("Download-"));
        zzclp = new LinkedBlockingQueue(TransportMediator.FLAG_KEY_MEDIA_NEXT);
        zzclq = new ThreadPoolExecutor(1, 1, 5, TimeUnit.SECONDS, zzclp, new zza("Callbacks-"));
        zzclk.allowCoreThreadTimeOut(true);
        zzclm.allowCoreThreadTimeOut(true);
        zzclo.allowCoreThreadTimeOut(true);
        zzclq.allowCoreThreadTimeOut(true);
    }

    public static zzd zzaaW() {
        return zzcli;
    }

    public void zzt(Runnable runnable) {
        zzclk.execute(runnable);
    }

    public void zzu(Runnable runnable) {
        zzclm.execute(runnable);
    }

    public void zzv(Runnable runnable) {
        zzclo.execute(runnable);
    }

    public void zzw(Runnable runnable) {
        zzclq.execute(runnable);
    }
}
