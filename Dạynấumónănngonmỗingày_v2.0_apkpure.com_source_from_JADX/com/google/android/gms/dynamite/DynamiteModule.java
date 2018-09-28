package com.google.android.gms.dynamite;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Base64;
import android.util.Log;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.util.DynamiteApi;
import com.google.android.gms.common.zzc;
import com.google.android.gms.dynamic.zzd;
import com.google.android.gms.dynamic.zze;
import dalvik.system.PathClassLoader;
import java.lang.reflect.Field;
import java.util.HashMap;

public final class DynamiteModule {
    public static final zzb zzaQA;
    private static zza zzaQr;
    private static final HashMap<String, byte[]> zzaQs;
    private static String zzaQt;
    private static final zza zzaQu;
    private static final zza zzaQv;
    public static final zzb zzaQw;
    public static final zzb zzaQx;
    public static final zzb zzaQy;
    public static final zzb zzaQz;
    private final Context zzaQB;

    /* renamed from: com.google.android.gms.dynamite.DynamiteModule.9 */
    class C06069 extends PathClassLoader {
        C06069(String str, ClassLoader classLoader) {
            super(str, classLoader);
        }

        protected Class<?> loadClass(String str, boolean z) throws ClassNotFoundException {
            if (!(str.startsWith("java.") || str.startsWith("android."))) {
                try {
                    return findClass(str);
                } catch (ClassNotFoundException e) {
                }
            }
            return super.loadClass(str, z);
        }
    }

    @DynamiteApi
    public static class DynamiteLoaderClassLoader {
        public static ClassLoader sClassLoader;
    }

    public static class zza extends Exception {
        private zza(String str) {
            super(str);
        }

        private zza(String str, Throwable th) {
            super(str, th);
        }
    }

    public interface zzb {

        public interface zza {
            int zzA(Context context, String str);

            DynamiteModule zza(Context context, String str, int i) throws zza;

            int zzb(Context context, String str, boolean z) throws zza;
        }

        public static class zzb {
            public int zzaQD;
            public int zzaQE;
            public int zzaQF;

            public zzb() {
                this.zzaQD = 0;
                this.zzaQE = 0;
                this.zzaQF = 0;
            }
        }

        zzb zza(Context context, String str, zza com_google_android_gms_dynamite_DynamiteModule_zzb_zza) throws zza;
    }

    /* renamed from: com.google.android.gms.dynamite.DynamiteModule.1 */
    class C11431 implements zza {
        C11431() {
        }

        public int zzA(Context context, String str) {
            return DynamiteModule.zzA(context, str);
        }

        public DynamiteModule zza(Context context, String str, int i) throws zza {
            return DynamiteModule.zza(context, str, i);
        }

        public int zzb(Context context, String str, boolean z) throws zza {
            return DynamiteModule.zzb(context, str, z);
        }
    }

    /* renamed from: com.google.android.gms.dynamite.DynamiteModule.2 */
    class C11442 implements zza {
        C11442() {
        }

        public int zzA(Context context, String str) {
            return DynamiteModule.zzA(context, str);
        }

        public DynamiteModule zza(Context context, String str, int i) throws zza {
            return DynamiteModule.zzb(context, str, i);
        }

        public int zzb(Context context, String str, boolean z) throws zza {
            return DynamiteModule.zzc(context, str, z);
        }
    }

    /* renamed from: com.google.android.gms.dynamite.DynamiteModule.3 */
    class C11453 implements zzb {
        C11453() {
        }

        public zzb zza(Context context, String str, zza com_google_android_gms_dynamite_DynamiteModule_zzb_zza) throws zza {
            zzb com_google_android_gms_dynamite_DynamiteModule_zzb_zzb = new zzb();
            com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaQE = com_google_android_gms_dynamite_DynamiteModule_zzb_zza.zzb(context, str, true);
            if (com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaQE != 0) {
                com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaQF = 1;
            } else {
                com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaQD = com_google_android_gms_dynamite_DynamiteModule_zzb_zza.zzA(context, str);
                if (com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaQD != 0) {
                    com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaQF = -1;
                }
            }
            return com_google_android_gms_dynamite_DynamiteModule_zzb_zzb;
        }
    }

    /* renamed from: com.google.android.gms.dynamite.DynamiteModule.4 */
    class C11464 implements zzb {
        C11464() {
        }

        public zzb zza(Context context, String str, zza com_google_android_gms_dynamite_DynamiteModule_zzb_zza) throws zza {
            zzb com_google_android_gms_dynamite_DynamiteModule_zzb_zzb = new zzb();
            com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaQD = com_google_android_gms_dynamite_DynamiteModule_zzb_zza.zzA(context, str);
            if (com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaQD != 0) {
                com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaQF = -1;
            } else {
                com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaQE = com_google_android_gms_dynamite_DynamiteModule_zzb_zza.zzb(context, str, true);
                if (com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaQE != 0) {
                    com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaQF = 1;
                }
            }
            return com_google_android_gms_dynamite_DynamiteModule_zzb_zzb;
        }
    }

    /* renamed from: com.google.android.gms.dynamite.DynamiteModule.5 */
    class C11475 implements zzb {
        C11475() {
        }

        public zzb zza(Context context, String str, zza com_google_android_gms_dynamite_DynamiteModule_zzb_zza) throws zza {
            zzb com_google_android_gms_dynamite_DynamiteModule_zzb_zzb = new zzb();
            com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaQD = com_google_android_gms_dynamite_DynamiteModule_zzb_zza.zzA(context, str);
            com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaQE = com_google_android_gms_dynamite_DynamiteModule_zzb_zza.zzb(context, str, true);
            if (com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaQD == 0 && com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaQE == 0) {
                com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaQF = 0;
            } else if (com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaQD >= com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaQE) {
                com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaQF = -1;
            } else {
                com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaQF = 1;
            }
            return com_google_android_gms_dynamite_DynamiteModule_zzb_zzb;
        }
    }

    /* renamed from: com.google.android.gms.dynamite.DynamiteModule.6 */
    class C11486 implements zzb {
        C11486() {
        }

        public zzb zza(Context context, String str, zza com_google_android_gms_dynamite_DynamiteModule_zzb_zza) throws zza {
            zzb com_google_android_gms_dynamite_DynamiteModule_zzb_zzb = new zzb();
            com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaQD = com_google_android_gms_dynamite_DynamiteModule_zzb_zza.zzA(context, str);
            com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaQE = com_google_android_gms_dynamite_DynamiteModule_zzb_zza.zzb(context, str, true);
            if (com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaQD == 0 && com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaQE == 0) {
                com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaQF = 0;
            } else if (com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaQE >= com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaQD) {
                com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaQF = 1;
            } else {
                com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaQF = -1;
            }
            return com_google_android_gms_dynamite_DynamiteModule_zzb_zzb;
        }
    }

    /* renamed from: com.google.android.gms.dynamite.DynamiteModule.7 */
    class C11497 implements zzb {
        C11497() {
        }

        public zzb zza(Context context, String str, zza com_google_android_gms_dynamite_DynamiteModule_zzb_zza) throws zza {
            zzb com_google_android_gms_dynamite_DynamiteModule_zzb_zzb = new zzb();
            com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaQD = com_google_android_gms_dynamite_DynamiteModule_zzb_zza.zzA(context, str);
            if (com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaQD != 0) {
                com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaQE = com_google_android_gms_dynamite_DynamiteModule_zzb_zza.zzb(context, str, false);
            } else {
                com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaQE = com_google_android_gms_dynamite_DynamiteModule_zzb_zza.zzb(context, str, true);
            }
            if (com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaQD == 0 && com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaQE == 0) {
                com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaQF = 0;
            } else if (com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaQE >= com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaQD) {
                com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaQF = 1;
            } else {
                com_google_android_gms_dynamite_DynamiteModule_zzb_zzb.zzaQF = -1;
            }
            return com_google_android_gms_dynamite_DynamiteModule_zzb_zzb;
        }
    }

    /* renamed from: com.google.android.gms.dynamite.DynamiteModule.8 */
    class C11508 implements zza {
        final /* synthetic */ int zzaQC;

        C11508(int i) {
            this.zzaQC = i;
        }

        public int zzA(Context context, String str) {
            return this.zzaQC;
        }

        public DynamiteModule zza(Context context, String str, int i) throws zza {
            throw new zza(null);
        }

        public int zzb(Context context, String str, boolean z) {
            return 0;
        }
    }

    static {
        zzaQs = new HashMap();
        zzaQu = new C11431();
        zzaQv = new C11442();
        zzaQw = new C11453();
        zzaQx = new C11464();
        zzaQy = new C11475();
        zzaQz = new C11486();
        zzaQA = new C11497();
    }

    private DynamiteModule(Context context) {
        this.zzaQB = (Context) zzac.zzw(context);
    }

    public static int zzA(Context context, String str) {
        String valueOf;
        String valueOf2;
        try {
            ClassLoader classLoader = context.getApplicationContext().getClassLoader();
            valueOf2 = String.valueOf("com.google.android.gms.dynamite.descriptors.");
            valueOf = String.valueOf("ModuleDescriptor");
            Class loadClass = classLoader.loadClass(new StringBuilder(((String.valueOf(valueOf2).length() + 1) + String.valueOf(str).length()) + String.valueOf(valueOf).length()).append(valueOf2).append(str).append(".").append(valueOf).toString());
            Field declaredField = loadClass.getDeclaredField("MODULE_ID");
            Field declaredField2 = loadClass.getDeclaredField("MODULE_VERSION");
            if (declaredField.get(null).equals(str)) {
                return declaredField2.getInt(null);
            }
            valueOf2 = String.valueOf(declaredField.get(null));
            Log.e("DynamiteModule", new StringBuilder((String.valueOf(valueOf2).length() + 51) + String.valueOf(str).length()).append("Module descriptor id '").append(valueOf2).append("' didn't match expected id '").append(str).append("'").toString());
            return 0;
        } catch (ClassNotFoundException e) {
            Log.w("DynamiteModule", new StringBuilder(String.valueOf(str).length() + 45).append("Local module descriptor class for ").append(str).append(" not found.").toString());
            return 0;
        } catch (Exception e2) {
            valueOf2 = "DynamiteModule";
            valueOf = "Failed to load module descriptor class: ";
            String valueOf3 = String.valueOf(e2.getMessage());
            Log.e(valueOf2, valueOf3.length() != 0 ? valueOf.concat(valueOf3) : new String(valueOf));
            return 0;
        }
    }

    public static int zzB(Context context, String str) {
        return zzb(context, str, false);
    }

    private static DynamiteModule zzC(Context context, String str) {
        String str2 = "DynamiteModule";
        String str3 = "Selected local version of ";
        String valueOf = String.valueOf(str);
        Log.i(str2, valueOf.length() != 0 ? str3.concat(valueOf) : new String(str3));
        return new DynamiteModule(context.getApplicationContext());
    }

    private static ClassLoader zzD(Context context, String str) throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException {
        ClassLoader classLoader;
        synchronized (DynamiteLoaderClassLoader.class) {
            if (DynamiteLoaderClassLoader.sClassLoader != null) {
                classLoader = DynamiteLoaderClassLoader.sClassLoader;
            } else {
                Class loadClass = context.getApplicationContext().getClassLoader().loadClass(DynamiteLoaderClassLoader.class.getName());
                Field declaredField = loadClass.getDeclaredField("sClassLoader");
                synchronized (loadClass) {
                    DynamiteLoaderClassLoader.sClassLoader = (ClassLoader) declaredField.get(null);
                    if (DynamiteLoaderClassLoader.sClassLoader != null) {
                        classLoader = DynamiteLoaderClassLoader.sClassLoader;
                    } else {
                        DynamiteLoaderClassLoader.sClassLoader = new C06069(str, ClassLoader.getSystemClassLoader());
                        declaredField.set(null, DynamiteLoaderClassLoader.sClassLoader);
                        classLoader = DynamiteLoaderClassLoader.sClassLoader;
                    }
                }
            }
        }
        return classLoader;
    }

    private static Context zza(Context context, String str, byte[] bArr, String str2) {
        if (str2 == null || str2.isEmpty()) {
            Log.e("DynamiteModule", "No valid DynamiteLoader APK path");
            return null;
        }
        try {
            return (Context) zze.zzE(com.google.android.gms.dynamite.zzb.zza.zzcf((IBinder) zzD(context, str2).loadClass("com.google.android.gms.dynamiteloader.DynamiteLoaderV2").getConstructor(new Class[0]).newInstance(new Object[0])).zza(zze.zzA(context), str, bArr));
        } catch (Exception e) {
            String str3 = "DynamiteModule";
            String str4 = "Failed to load DynamiteLoader: ";
            String valueOf = String.valueOf(e.toString());
            Log.e(str3, valueOf.length() != 0 ? str4.concat(valueOf) : new String(str4));
            return null;
        }
    }

    public static DynamiteModule zza(Context context, zzb com_google_android_gms_dynamite_DynamiteModule_zzb, String str) throws zza {
        if (GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_PACKAGE.equals(context.getApplicationContext().getPackageName())) {
            return zza(context, com_google_android_gms_dynamite_DynamiteModule_zzb, str, zzaQu);
        }
        try {
            return zza(context, com_google_android_gms_dynamite_DynamiteModule_zzb, str, zzaQv);
        } catch (zza e) {
            String str2 = "DynamiteModule";
            String str3 = "Failed to load module via fast route";
            String valueOf = String.valueOf(e.toString());
            Log.w(str2, valueOf.length() != 0 ? str3.concat(valueOf) : new String(str3));
            return zza(context, com_google_android_gms_dynamite_DynamiteModule_zzb, str, zzaQu);
        }
    }

    public static DynamiteModule zza(Context context, zzb com_google_android_gms_dynamite_DynamiteModule_zzb, String str, zza com_google_android_gms_dynamite_DynamiteModule_zzb_zza) throws zza {
        zzb zza = com_google_android_gms_dynamite_DynamiteModule_zzb.zza(context, str, com_google_android_gms_dynamite_DynamiteModule_zzb_zza);
        Log.i("DynamiteModule", new StringBuilder((String.valueOf(str).length() + 68) + String.valueOf(str).length()).append("Considering local module ").append(str).append(":").append(zza.zzaQD).append(" and remote module ").append(str).append(":").append(zza.zzaQE).toString());
        if (zza.zzaQF == 0 || ((zza.zzaQF == -1 && zza.zzaQD == 0) || (zza.zzaQF == 1 && zza.zzaQE == 0))) {
            throw new zza(null);
        } else if (zza.zzaQF == -1) {
            return zzC(context, str);
        } else {
            if (zza.zzaQF == 1) {
                try {
                    return com_google_android_gms_dynamite_DynamiteModule_zzb_zza.zza(context, str, zza.zzaQE);
                } catch (Throwable e) {
                    Throwable th = e;
                    String str2 = "DynamiteModule";
                    String str3 = "Failed to load remote module: ";
                    String valueOf = String.valueOf(th.getMessage());
                    Log.w(str2, valueOf.length() != 0 ? str3.concat(valueOf) : new String(str3));
                    if (zza.zzaQD != 0 && com_google_android_gms_dynamite_DynamiteModule_zzb.zza(context, str, new C11508(zza.zzaQD)).zzaQF == -1) {
                        return zzC(context, str);
                    }
                    throw new zza(th, null);
                }
            }
            throw new zza(null);
        }
    }

    private static DynamiteModule zza(Context context, String str, int i) throws zza {
        Log.i("DynamiteModule", new StringBuilder(String.valueOf(str).length() + 51).append("Selected remote version of ").append(str).append(", version >= ").append(i).toString());
        zza zzaU = zzaU(context);
        if (zzaU == null) {
            throw new zza(null);
        }
        try {
            zzd zza = zzaU.zza(zze.zzA(context), str, i);
            if (zze.zzE(zza) != null) {
                return new DynamiteModule((Context) zze.zzE(zza));
            }
            throw new zza(null);
        } catch (Throwable e) {
            throw new zza(e, null);
        }
    }

    private static zza zzaU(Context context) {
        synchronized (DynamiteModule.class) {
            zza com_google_android_gms_dynamite_zza;
            if (zzaQr != null) {
                com_google_android_gms_dynamite_zza = zzaQr;
                return com_google_android_gms_dynamite_zza;
            } else if (zzc.zzuz().isGooglePlayServicesAvailable(context) != 0) {
                return null;
            } else {
                try {
                    com_google_android_gms_dynamite_zza = com.google.android.gms.dynamite.zza.zza.zzce((IBinder) context.createPackageContext(GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_PACKAGE, 3).getClassLoader().loadClass("com.google.android.gms.chimera.container.DynamiteLoaderImpl").newInstance());
                    if (com_google_android_gms_dynamite_zza != null) {
                        zzaQr = com_google_android_gms_dynamite_zza;
                        return com_google_android_gms_dynamite_zza;
                    }
                } catch (Exception e) {
                    String str = "DynamiteModule";
                    String str2 = "Failed to load IDynamiteLoader from GmsCore: ";
                    String valueOf = String.valueOf(e.getMessage());
                    Log.e(str, valueOf.length() != 0 ? str2.concat(valueOf) : new String(str2));
                }
                return null;
            }
        }
    }

    public static int zzb(Context context, String str, boolean z) {
        zza zzaU = zzaU(context);
        if (zzaU == null) {
            return 0;
        }
        try {
            return zzaU.zza(zze.zzA(context), str, z);
        } catch (RemoteException e) {
            String str2 = "DynamiteModule";
            String str3 = "Failed to retrieve remote module version: ";
            String valueOf = String.valueOf(e.getMessage());
            Log.w(str2, valueOf.length() != 0 ? str3.concat(valueOf) : new String(str3));
            return 0;
        }
    }

    private static DynamiteModule zzb(Context context, String str, int i) throws zza {
        Log.i("DynamiteModule", new StringBuilder(String.valueOf(str).length() + 51).append("Selected remote version of ").append(str).append(", version >= ").append(i).toString());
        synchronized (DynamiteModule.class) {
            byte[] bArr = (byte[]) zzaQs.get(new StringBuilder(String.valueOf(str).length() + 12).append(str).append(":").append(i).toString());
            String str2 = zzaQt;
        }
        if (bArr == null) {
            throw new zza(null);
        }
        Context zza = zza(context.getApplicationContext(), str, bArr, str2);
        if (zza != null) {
            return new DynamiteModule(zza);
        }
        throw new zza(null);
    }

    public static int zzc(Context context, String str, boolean z) throws zza {
        String str2;
        Throwable e;
        Cursor cursor;
        Cursor cursor2 = null;
        if (z) {
            try {
                str2 = "api_force_staging";
            } catch (Exception e2) {
                e = e2;
                cursor = null;
                try {
                    if (e instanceof zza) {
                        throw e;
                    }
                    throw new zza(e, null);
                } catch (Throwable th) {
                    e = th;
                    cursor2 = cursor;
                    if (cursor2 != null) {
                        cursor2.close();
                    }
                    throw e;
                }
            } catch (Throwable th2) {
                e = th2;
                if (cursor2 != null) {
                    cursor2.close();
                }
                throw e;
            }
        }
        str2 = "api";
        String valueOf = String.valueOf("content://com.google.android.gms.chimera/");
        Uri parse = Uri.parse(new StringBuilder(((String.valueOf(valueOf).length() + 1) + String.valueOf(str2).length()) + String.valueOf(str).length()).append(valueOf).append(str2).append("/").append(str).toString());
        if (context != null) {
            ContentResolver contentResolver = context.getContentResolver();
            if (contentResolver != null) {
                cursor = contentResolver.query(parse, null, null, null, null);
                if (cursor != null) {
                    try {
                        if (cursor.moveToFirst()) {
                            int i = cursor.getInt(0);
                            if (i > 0) {
                                synchronized (DynamiteModule.class) {
                                    zzaQs.put(new StringBuilder(String.valueOf(str).length() + 12).append(str).append(":").append(i).toString(), Base64.decode(cursor.getString(3), 0));
                                    zzaQt = cursor.getString(2);
                                }
                            }
                            if (cursor != null) {
                                cursor.close();
                            }
                            return i;
                        }
                    } catch (Exception e3) {
                        e = e3;
                    }
                }
                Log.w("DynamiteModule", "Failed to retrieve remote module version.");
                throw new zza(null);
            }
        }
        throw new zza(null);
    }

    public Context zzBd() {
        return this.zzaQB;
    }

    public IBinder zzdX(String str) throws zza {
        Throwable e;
        String str2;
        String valueOf;
        try {
            return (IBinder) this.zzaQB.getClassLoader().loadClass(str).newInstance();
        } catch (ClassNotFoundException e2) {
            e = e2;
            str2 = "Failed to instantiate module class: ";
            valueOf = String.valueOf(str);
            throw new zza(e, null);
        } catch (InstantiationException e3) {
            e = e3;
            str2 = "Failed to instantiate module class: ";
            valueOf = String.valueOf(str);
            if (valueOf.length() != 0) {
            }
            throw new zza(e, null);
        } catch (IllegalAccessException e4) {
            e = e4;
            str2 = "Failed to instantiate module class: ";
            valueOf = String.valueOf(str);
            if (valueOf.length() != 0) {
            }
            throw new zza(e, null);
        }
    }
}
