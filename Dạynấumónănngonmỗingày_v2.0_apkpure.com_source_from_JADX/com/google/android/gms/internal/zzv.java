package com.google.android.gms.internal;

import android.os.SystemClock;
import android.support.v4.view.MotionEventCompat;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.http.protocol.HTTP;
import rx.android.BuildConfig;

public class zzv implements zzb {
    private final Map<String, zza> zzav;
    private long zzaw;
    private final File zzax;
    private final int zzay;

    static class zza {
        public String zza;
        public String zzaA;
        public long zzaz;
        public long zzb;
        public long zzc;
        public long zzd;
        public long zze;
        public Map<String, String> zzf;

        private zza() {
        }

        public zza(String str, com.google.android.gms.internal.zzb.zza com_google_android_gms_internal_zzb_zza) {
            this.zzaA = str;
            this.zzaz = (long) com_google_android_gms_internal_zzb_zza.data.length;
            this.zza = com_google_android_gms_internal_zzb_zza.zza;
            this.zzb = com_google_android_gms_internal_zzb_zza.zzb;
            this.zzc = com_google_android_gms_internal_zzb_zza.zzc;
            this.zzd = com_google_android_gms_internal_zzb_zza.zzd;
            this.zze = com_google_android_gms_internal_zzb_zza.zze;
            this.zzf = com_google_android_gms_internal_zzb_zza.zzf;
        }

        public static zza zzf(InputStream inputStream) throws IOException {
            zza com_google_android_gms_internal_zzv_zza = new zza();
            if (zzv.zzb(inputStream) != 538247942) {
                throw new IOException();
            }
            com_google_android_gms_internal_zzv_zza.zzaA = zzv.zzd(inputStream);
            com_google_android_gms_internal_zzv_zza.zza = zzv.zzd(inputStream);
            if (com_google_android_gms_internal_zzv_zza.zza.equals(BuildConfig.VERSION_NAME)) {
                com_google_android_gms_internal_zzv_zza.zza = null;
            }
            com_google_android_gms_internal_zzv_zza.zzb = zzv.zzc(inputStream);
            com_google_android_gms_internal_zzv_zza.zzc = zzv.zzc(inputStream);
            com_google_android_gms_internal_zzv_zza.zzd = zzv.zzc(inputStream);
            com_google_android_gms_internal_zzv_zza.zze = zzv.zzc(inputStream);
            com_google_android_gms_internal_zzv_zza.zzf = zzv.zze(inputStream);
            return com_google_android_gms_internal_zzv_zza;
        }

        public boolean zza(OutputStream outputStream) {
            try {
                zzv.zza(outputStream, 538247942);
                zzv.zza(outputStream, this.zzaA);
                zzv.zza(outputStream, this.zza == null ? BuildConfig.VERSION_NAME : this.zza);
                zzv.zza(outputStream, this.zzb);
                zzv.zza(outputStream, this.zzc);
                zzv.zza(outputStream, this.zzd);
                zzv.zza(outputStream, this.zze);
                zzv.zza(this.zzf, outputStream);
                outputStream.flush();
                return true;
            } catch (IOException e) {
                zzs.zzb("%s", e.toString());
                return false;
            }
        }

        public com.google.android.gms.internal.zzb.zza zzb(byte[] bArr) {
            com.google.android.gms.internal.zzb.zza com_google_android_gms_internal_zzb_zza = new com.google.android.gms.internal.zzb.zza();
            com_google_android_gms_internal_zzb_zza.data = bArr;
            com_google_android_gms_internal_zzb_zza.zza = this.zza;
            com_google_android_gms_internal_zzb_zza.zzb = this.zzb;
            com_google_android_gms_internal_zzb_zza.zzc = this.zzc;
            com_google_android_gms_internal_zzb_zza.zzd = this.zzd;
            com_google_android_gms_internal_zzb_zza.zze = this.zze;
            com_google_android_gms_internal_zzb_zza.zzf = this.zzf;
            return com_google_android_gms_internal_zzb_zza;
        }
    }

    private static class zzb extends FilterInputStream {
        private int zzaB;

        private zzb(InputStream inputStream) {
            super(inputStream);
            this.zzaB = 0;
        }

        public int read() throws IOException {
            int read = super.read();
            if (read != -1) {
                this.zzaB++;
            }
            return read;
        }

        public int read(byte[] bArr, int i, int i2) throws IOException {
            int read = super.read(bArr, i, i2);
            if (read != -1) {
                this.zzaB += read;
            }
            return read;
        }
    }

    public zzv(File file) {
        this(file, 5242880);
    }

    public zzv(File file, int i) {
        this.zzav = new LinkedHashMap(16, 0.75f, true);
        this.zzaw = 0;
        this.zzax = file;
        this.zzay = i;
    }

    private void removeEntry(String str) {
        zza com_google_android_gms_internal_zzv_zza = (zza) this.zzav.get(str);
        if (com_google_android_gms_internal_zzv_zza != null) {
            this.zzaw -= com_google_android_gms_internal_zzv_zza.zzaz;
            this.zzav.remove(str);
        }
    }

    private static int zza(InputStream inputStream) throws IOException {
        int read = inputStream.read();
        if (read != -1) {
            return read;
        }
        throw new EOFException();
    }

    static void zza(OutputStream outputStream, int i) throws IOException {
        outputStream.write((i >> 0) & MotionEventCompat.ACTION_MASK);
        outputStream.write((i >> 8) & MotionEventCompat.ACTION_MASK);
        outputStream.write((i >> 16) & MotionEventCompat.ACTION_MASK);
        outputStream.write((i >> 24) & MotionEventCompat.ACTION_MASK);
    }

    static void zza(OutputStream outputStream, long j) throws IOException {
        outputStream.write((byte) ((int) (j >>> null)));
        outputStream.write((byte) ((int) (j >>> 8)));
        outputStream.write((byte) ((int) (j >>> 16)));
        outputStream.write((byte) ((int) (j >>> 24)));
        outputStream.write((byte) ((int) (j >>> 32)));
        outputStream.write((byte) ((int) (j >>> 40)));
        outputStream.write((byte) ((int) (j >>> 48)));
        outputStream.write((byte) ((int) (j >>> 56)));
    }

    static void zza(OutputStream outputStream, String str) throws IOException {
        byte[] bytes = str.getBytes(HTTP.UTF_8);
        zza(outputStream, (long) bytes.length);
        outputStream.write(bytes, 0, bytes.length);
    }

    private void zza(String str, zza com_google_android_gms_internal_zzv_zza) {
        if (this.zzav.containsKey(str)) {
            zza com_google_android_gms_internal_zzv_zza2 = (zza) this.zzav.get(str);
            this.zzaw = (com_google_android_gms_internal_zzv_zza.zzaz - com_google_android_gms_internal_zzv_zza2.zzaz) + this.zzaw;
        } else {
            this.zzaw += com_google_android_gms_internal_zzv_zza.zzaz;
        }
        this.zzav.put(str, com_google_android_gms_internal_zzv_zza);
    }

    static void zza(Map<String, String> map, OutputStream outputStream) throws IOException {
        if (map != null) {
            zza(outputStream, map.size());
            for (Entry entry : map.entrySet()) {
                zza(outputStream, (String) entry.getKey());
                zza(outputStream, (String) entry.getValue());
            }
            return;
        }
        zza(outputStream, 0);
    }

    private static byte[] zza(InputStream inputStream, int i) throws IOException {
        byte[] bArr = new byte[i];
        int i2 = 0;
        while (i2 < i) {
            int read = inputStream.read(bArr, i2, i - i2);
            if (read == -1) {
                break;
            }
            i2 += read;
        }
        if (i2 == i) {
            return bArr;
        }
        throw new IOException("Expected " + i + " bytes, read " + i2 + " bytes");
    }

    static int zzb(InputStream inputStream) throws IOException {
        return ((((zza(inputStream) << 0) | 0) | (zza(inputStream) << 8)) | (zza(inputStream) << 16)) | (zza(inputStream) << 24);
    }

    static long zzc(InputStream inputStream) throws IOException {
        return (((((((0 | ((((long) zza(inputStream)) & 255) << null)) | ((((long) zza(inputStream)) & 255) << 8)) | ((((long) zza(inputStream)) & 255) << 16)) | ((((long) zza(inputStream)) & 255) << 24)) | ((((long) zza(inputStream)) & 255) << 32)) | ((((long) zza(inputStream)) & 255) << 40)) | ((((long) zza(inputStream)) & 255) << 48)) | ((((long) zza(inputStream)) & 255) << 56);
    }

    private void zzc(int i) {
        if (this.zzaw + ((long) i) >= ((long) this.zzay)) {
            int i2;
            if (zzs.DEBUG) {
                zzs.zza("Pruning old cache entries.", new Object[0]);
            }
            long j = this.zzaw;
            long elapsedRealtime = SystemClock.elapsedRealtime();
            Iterator it = this.zzav.entrySet().iterator();
            int i3 = 0;
            while (it.hasNext()) {
                zza com_google_android_gms_internal_zzv_zza = (zza) ((Entry) it.next()).getValue();
                if (zzf(com_google_android_gms_internal_zzv_zza.zzaA).delete()) {
                    this.zzaw -= com_google_android_gms_internal_zzv_zza.zzaz;
                } else {
                    zzs.zzb("Could not delete cache entry for key=%s, filename=%s", com_google_android_gms_internal_zzv_zza.zzaA, zze(com_google_android_gms_internal_zzv_zza.zzaA));
                }
                it.remove();
                i2 = i3 + 1;
                if (((float) (this.zzaw + ((long) i))) < ((float) this.zzay) * 0.9f) {
                    break;
                }
                i3 = i2;
            }
            i2 = i3;
            if (zzs.DEBUG) {
                zzs.zza("pruned %d files, %d bytes, %d ms", Integer.valueOf(i2), Long.valueOf(this.zzaw - j), Long.valueOf(SystemClock.elapsedRealtime() - elapsedRealtime));
            }
        }
    }

    static String zzd(InputStream inputStream) throws IOException {
        return new String(zza(inputStream, (int) zzc(inputStream)), HTTP.UTF_8);
    }

    private String zze(String str) {
        int length = str.length() / 2;
        String valueOf = String.valueOf(String.valueOf(str.substring(0, length).hashCode()));
        String valueOf2 = String.valueOf(String.valueOf(str.substring(length).hashCode()));
        return valueOf2.length() != 0 ? valueOf.concat(valueOf2) : new String(valueOf);
    }

    static Map<String, String> zze(InputStream inputStream) throws IOException {
        int zzb = zzb(inputStream);
        Map<String, String> emptyMap = zzb == 0 ? Collections.emptyMap() : new HashMap(zzb);
        for (int i = 0; i < zzb; i++) {
            emptyMap.put(zzd(inputStream).intern(), zzd(inputStream).intern());
        }
        return emptyMap;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void initialize() {
        /*
        r9 = this;
        r0 = 0;
        monitor-enter(r9);
        r1 = r9.zzax;	 Catch:{ all -> 0x006a }
        r1 = r1.exists();	 Catch:{ all -> 0x006a }
        if (r1 != 0) goto L_0x0025;
    L_0x000a:
        r0 = r9.zzax;	 Catch:{ all -> 0x006a }
        r0 = r0.mkdirs();	 Catch:{ all -> 0x006a }
        if (r0 != 0) goto L_0x0023;
    L_0x0012:
        r0 = "Unable to create cache dir %s";
        r1 = 1;
        r1 = new java.lang.Object[r1];	 Catch:{ all -> 0x006a }
        r2 = 0;
        r3 = r9.zzax;	 Catch:{ all -> 0x006a }
        r3 = r3.getAbsolutePath();	 Catch:{ all -> 0x006a }
        r1[r2] = r3;	 Catch:{ all -> 0x006a }
        com.google.android.gms.internal.zzs.zzc(r0, r1);	 Catch:{ all -> 0x006a }
    L_0x0023:
        monitor-exit(r9);
        return;
    L_0x0025:
        r1 = r9.zzax;	 Catch:{ all -> 0x006a }
        r3 = r1.listFiles();	 Catch:{ all -> 0x006a }
        if (r3 == 0) goto L_0x0023;
    L_0x002d:
        r4 = r3.length;	 Catch:{ all -> 0x006a }
        r2 = r0;
    L_0x002f:
        if (r2 >= r4) goto L_0x0023;
    L_0x0031:
        r5 = r3[r2];	 Catch:{ all -> 0x006a }
        r1 = 0;
        r0 = new java.io.BufferedInputStream;	 Catch:{ IOException -> 0x0054, all -> 0x0063 }
        r6 = new java.io.FileInputStream;	 Catch:{ IOException -> 0x0054, all -> 0x0063 }
        r6.<init>(r5);	 Catch:{ IOException -> 0x0054, all -> 0x0063 }
        r0.<init>(r6);	 Catch:{ IOException -> 0x0054, all -> 0x0063 }
        r1 = com.google.android.gms.internal.zzv.zza.zzf(r0);	 Catch:{ IOException -> 0x0076 }
        r6 = r5.length();	 Catch:{ IOException -> 0x0076 }
        r1.zzaz = r6;	 Catch:{ IOException -> 0x0076 }
        r6 = r1.zzaA;	 Catch:{ IOException -> 0x0076 }
        r9.zza(r6, r1);	 Catch:{ IOException -> 0x0076 }
        r0.close();	 Catch:{ IOException -> 0x006d }
    L_0x0050:
        r0 = r2 + 1;
        r2 = r0;
        goto L_0x002f;
    L_0x0054:
        r0 = move-exception;
        r0 = r1;
    L_0x0056:
        if (r5 == 0) goto L_0x005b;
    L_0x0058:
        r5.delete();	 Catch:{ all -> 0x0071 }
    L_0x005b:
        if (r0 == 0) goto L_0x0050;
    L_0x005d:
        r0.close();	 Catch:{ IOException -> 0x0061 }
        goto L_0x0050;
    L_0x0061:
        r0 = move-exception;
        goto L_0x0050;
    L_0x0063:
        r0 = move-exception;
    L_0x0064:
        if (r1 == 0) goto L_0x0069;
    L_0x0066:
        r1.close();	 Catch:{ IOException -> 0x006f }
    L_0x0069:
        throw r0;	 Catch:{ all -> 0x006a }
    L_0x006a:
        r0 = move-exception;
        monitor-exit(r9);
        throw r0;
    L_0x006d:
        r0 = move-exception;
        goto L_0x0050;
    L_0x006f:
        r1 = move-exception;
        goto L_0x0069;
    L_0x0071:
        r1 = move-exception;
        r8 = r1;
        r1 = r0;
        r0 = r8;
        goto L_0x0064;
    L_0x0076:
        r1 = move-exception;
        goto L_0x0056;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.zzv.initialize():void");
    }

    public synchronized void remove(String str) {
        boolean delete = zzf(str).delete();
        removeEntry(str);
        if (!delete) {
            zzs.zzb("Could not delete cache entry for key=%s, filename=%s", str, zze(str));
        }
    }

    public synchronized com.google.android.gms.internal.zzb.zza zza(String str) {
        com.google.android.gms.internal.zzb.zza com_google_android_gms_internal_zzb_zza;
        IOException e;
        Throwable th;
        zza com_google_android_gms_internal_zzv_zza = (zza) this.zzav.get(str);
        if (com_google_android_gms_internal_zzv_zza == null) {
            com_google_android_gms_internal_zzb_zza = null;
        } else {
            File zzf = zzf(str);
            zzb com_google_android_gms_internal_zzv_zzb;
            try {
                com_google_android_gms_internal_zzv_zzb = new zzb(null);
                try {
                    zza.zzf(com_google_android_gms_internal_zzv_zzb);
                    com_google_android_gms_internal_zzb_zza = com_google_android_gms_internal_zzv_zza.zzb(zza((InputStream) com_google_android_gms_internal_zzv_zzb, (int) (zzf.length() - ((long) com_google_android_gms_internal_zzv_zzb.zzaB))));
                    try {
                        com_google_android_gms_internal_zzv_zzb.close();
                    } catch (IOException e2) {
                        com_google_android_gms_internal_zzb_zza = null;
                    }
                } catch (IOException e3) {
                    e = e3;
                    try {
                        zzs.zzb("%s: %s", zzf.getAbsolutePath(), e.toString());
                        remove(str);
                        if (com_google_android_gms_internal_zzv_zzb != null) {
                            try {
                                com_google_android_gms_internal_zzv_zzb.close();
                            } catch (IOException e4) {
                                com_google_android_gms_internal_zzb_zza = null;
                            }
                        }
                        com_google_android_gms_internal_zzb_zza = null;
                        return com_google_android_gms_internal_zzb_zza;
                    } catch (Throwable th2) {
                        th = th2;
                        if (com_google_android_gms_internal_zzv_zzb != null) {
                            try {
                                com_google_android_gms_internal_zzv_zzb.close();
                            } catch (IOException e5) {
                                com_google_android_gms_internal_zzb_zza = null;
                            }
                        }
                        throw th;
                    }
                }
            } catch (IOException e6) {
                e = e6;
                com_google_android_gms_internal_zzv_zzb = null;
                zzs.zzb("%s: %s", zzf.getAbsolutePath(), e.toString());
                remove(str);
                if (com_google_android_gms_internal_zzv_zzb != null) {
                    com_google_android_gms_internal_zzv_zzb.close();
                }
                com_google_android_gms_internal_zzb_zza = null;
                return com_google_android_gms_internal_zzb_zza;
            } catch (Throwable th3) {
                th = th3;
                com_google_android_gms_internal_zzv_zzb = null;
                if (com_google_android_gms_internal_zzv_zzb != null) {
                    com_google_android_gms_internal_zzv_zzb.close();
                }
                throw th;
            }
        }
        return com_google_android_gms_internal_zzb_zza;
    }

    public synchronized void zza(String str, com.google.android.gms.internal.zzb.zza com_google_android_gms_internal_zzb_zza) {
        zzc(com_google_android_gms_internal_zzb_zza.data.length);
        File zzf = zzf(str);
        try {
            OutputStream fileOutputStream = new FileOutputStream(zzf);
            zza com_google_android_gms_internal_zzv_zza = new zza(str, com_google_android_gms_internal_zzb_zza);
            if (com_google_android_gms_internal_zzv_zza.zza(fileOutputStream)) {
                fileOutputStream.write(com_google_android_gms_internal_zzb_zza.data);
                fileOutputStream.close();
                zza(str, com_google_android_gms_internal_zzv_zza);
            } else {
                fileOutputStream.close();
                zzs.zzb("Failed to write header for %s", zzf.getAbsolutePath());
                throw new IOException();
            }
        } catch (IOException e) {
            if (!zzf.delete()) {
                zzs.zzb("Could not clean up file %s", zzf.getAbsolutePath());
            }
        }
    }

    public File zzf(String str) {
        return new File(this.zzax, zze(str));
    }
}
