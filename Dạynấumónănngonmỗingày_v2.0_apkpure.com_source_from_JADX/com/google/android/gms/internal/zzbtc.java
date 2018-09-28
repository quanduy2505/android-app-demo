package com.google.android.gms.internal;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class zzbtc implements zzbse {
    private final zzbsm zzcmD;
    private final zzbrk zzcmF;
    private final zzbsl zzcmu;

    static abstract class zzb {
        final String name;
        final boolean zzcov;
        final boolean zzcow;

        protected zzb(String str, boolean z, boolean z2) {
            this.name = str;
            this.zzcov = z;
            this.zzcow = z2;
        }

        abstract void zza(zzbti com_google_android_gms_internal_zzbti, Object obj) throws IOException, IllegalAccessException;

        abstract void zza(zzbtk com_google_android_gms_internal_zzbtk, Object obj) throws IOException, IllegalAccessException;

        abstract boolean zzaQ(Object obj) throws IOException, IllegalAccessException;
    }

    /* renamed from: com.google.android.gms.internal.zzbtc.1 */
    class C12011 extends zzb {
        final zzbsd<?> zzcoo;
        final /* synthetic */ zzbrl zzcop;
        final /* synthetic */ Field zzcoq;
        final /* synthetic */ zzbth zzcor;
        final /* synthetic */ boolean zzcos;
        final /* synthetic */ zzbtc zzcot;

        C12011(zzbtc com_google_android_gms_internal_zzbtc, String str, boolean z, boolean z2, zzbrl com_google_android_gms_internal_zzbrl, Field field, zzbth com_google_android_gms_internal_zzbth, boolean z3) {
            this.zzcot = com_google_android_gms_internal_zzbtc;
            this.zzcop = com_google_android_gms_internal_zzbrl;
            this.zzcoq = field;
            this.zzcor = com_google_android_gms_internal_zzbth;
            this.zzcos = z3;
            super(str, z, z2);
            this.zzcoo = this.zzcot.zza(this.zzcop, this.zzcoq, this.zzcor);
        }

        void zza(zzbti com_google_android_gms_internal_zzbti, Object obj) throws IOException, IllegalAccessException {
            Object zzb = this.zzcoo.zzb(com_google_android_gms_internal_zzbti);
            if (zzb != null || !this.zzcos) {
                this.zzcoq.set(obj, zzb);
            }
        }

        void zza(zzbtk com_google_android_gms_internal_zzbtk, Object obj) throws IOException, IllegalAccessException {
            new zzbtf(this.zzcop, this.zzcoo, this.zzcor.zzacc()).zza(com_google_android_gms_internal_zzbtk, this.zzcoq.get(obj));
        }

        public boolean zzaQ(Object obj) throws IOException, IllegalAccessException {
            return this.zzcov && this.zzcoq.get(obj) != obj;
        }
    }

    public static final class zza<T> extends zzbsd<T> {
        private final zzbsq<T> zzcob;
        private final Map<String, zzb> zzcou;

        private zza(zzbsq<T> com_google_android_gms_internal_zzbsq_T, Map<String, zzb> map) {
            this.zzcob = com_google_android_gms_internal_zzbsq_T;
            this.zzcou = map;
        }

        public void zza(zzbtk com_google_android_gms_internal_zzbtk, T t) throws IOException {
            if (t == null) {
                com_google_android_gms_internal_zzbtk.zzaca();
                return;
            }
            com_google_android_gms_internal_zzbtk.zzabY();
            try {
                for (zzb com_google_android_gms_internal_zzbtc_zzb : this.zzcou.values()) {
                    if (com_google_android_gms_internal_zzbtc_zzb.zzaQ(t)) {
                        com_google_android_gms_internal_zzbtk.zzjW(com_google_android_gms_internal_zzbtc_zzb.name);
                        com_google_android_gms_internal_zzbtc_zzb.zza(com_google_android_gms_internal_zzbtk, (Object) t);
                    }
                }
                com_google_android_gms_internal_zzbtk.zzabZ();
            } catch (IllegalAccessException e) {
                throw new AssertionError();
            }
        }

        public T zzb(zzbti com_google_android_gms_internal_zzbti) throws IOException {
            if (com_google_android_gms_internal_zzbti.zzabQ() == zzbtj.NULL) {
                com_google_android_gms_internal_zzbti.nextNull();
                return null;
            }
            T zzabJ = this.zzcob.zzabJ();
            try {
                com_google_android_gms_internal_zzbti.beginObject();
                while (com_google_android_gms_internal_zzbti.hasNext()) {
                    zzb com_google_android_gms_internal_zzbtc_zzb = (zzb) this.zzcou.get(com_google_android_gms_internal_zzbti.nextName());
                    if (com_google_android_gms_internal_zzbtc_zzb == null || !com_google_android_gms_internal_zzbtc_zzb.zzcow) {
                        com_google_android_gms_internal_zzbti.skipValue();
                    } else {
                        com_google_android_gms_internal_zzbtc_zzb.zza(com_google_android_gms_internal_zzbti, (Object) zzabJ);
                    }
                }
                com_google_android_gms_internal_zzbti.endObject();
                return zzabJ;
            } catch (Throwable e) {
                throw new zzbsa(e);
            } catch (IllegalAccessException e2) {
                throw new AssertionError(e2);
            }
        }
    }

    public zzbtc(zzbsl com_google_android_gms_internal_zzbsl, zzbrk com_google_android_gms_internal_zzbrk, zzbsm com_google_android_gms_internal_zzbsm) {
        this.zzcmu = com_google_android_gms_internal_zzbsl;
        this.zzcmF = com_google_android_gms_internal_zzbrk;
        this.zzcmD = com_google_android_gms_internal_zzbsm;
    }

    private zzbsd<?> zza(zzbrl com_google_android_gms_internal_zzbrl, Field field, zzbth<?> com_google_android_gms_internal_zzbth_) {
        zzbsf com_google_android_gms_internal_zzbsf = (zzbsf) field.getAnnotation(zzbsf.class);
        if (com_google_android_gms_internal_zzbsf != null) {
            zzbsd<?> zza = zzbsx.zza(this.zzcmu, com_google_android_gms_internal_zzbrl, com_google_android_gms_internal_zzbth_, com_google_android_gms_internal_zzbsf);
            if (zza != null) {
                return zza;
            }
        }
        return com_google_android_gms_internal_zzbrl.zza((zzbth) com_google_android_gms_internal_zzbth_);
    }

    private zzb zza(zzbrl com_google_android_gms_internal_zzbrl, Field field, String str, zzbth<?> com_google_android_gms_internal_zzbth_, boolean z, boolean z2) {
        return new C12011(this, str, z, z2, com_google_android_gms_internal_zzbrl, field, com_google_android_gms_internal_zzbth_, zzbsr.zzk(com_google_android_gms_internal_zzbth_.zzacb()));
    }

    static List<String> zza(zzbrk com_google_android_gms_internal_zzbrk, Field field) {
        zzbsg com_google_android_gms_internal_zzbsg = (zzbsg) field.getAnnotation(zzbsg.class);
        List<String> linkedList = new LinkedList();
        if (com_google_android_gms_internal_zzbsg == null) {
            linkedList.add(com_google_android_gms_internal_zzbrk.zzc(field));
        } else {
            linkedList.add(com_google_android_gms_internal_zzbsg.value());
            for (Object add : com_google_android_gms_internal_zzbsg.zzabH()) {
                linkedList.add(add);
            }
        }
        return linkedList;
    }

    private Map<String, zzb> zza(zzbrl com_google_android_gms_internal_zzbrl, zzbth<?> com_google_android_gms_internal_zzbth_, Class<?> cls) {
        Map<String, zzb> linkedHashMap = new LinkedHashMap();
        if (cls.isInterface()) {
            return linkedHashMap;
        }
        Type zzacc = com_google_android_gms_internal_zzbth_.zzacc();
        Class zzacb;
        while (zzacb != Object.class) {
            for (Field field : zzacb.getDeclaredFields()) {
                boolean zza = zza(field, true);
                boolean zza2 = zza(field, false);
                if (zza || zza2) {
                    field.setAccessible(true);
                    Type zza3 = zzbsk.zza(r19.zzacc(), zzacb, field.getGenericType());
                    List zzd = zzd(field);
                    zzb com_google_android_gms_internal_zzbtc_zzb = null;
                    int i = 0;
                    while (i < zzd.size()) {
                        String str = (String) zzd.get(i);
                        if (i != 0) {
                            zza = false;
                        }
                        zzb com_google_android_gms_internal_zzbtc_zzb2 = (zzb) linkedHashMap.put(str, zza(com_google_android_gms_internal_zzbrl, field, str, zzbth.zzl(zza3), zza, zza2));
                        if (com_google_android_gms_internal_zzbtc_zzb != null) {
                            com_google_android_gms_internal_zzbtc_zzb2 = com_google_android_gms_internal_zzbtc_zzb;
                        }
                        i++;
                        com_google_android_gms_internal_zzbtc_zzb = com_google_android_gms_internal_zzbtc_zzb2;
                    }
                    if (com_google_android_gms_internal_zzbtc_zzb != null) {
                        String valueOf = String.valueOf(zzacc);
                        String str2 = com_google_android_gms_internal_zzbtc_zzb.name;
                        throw new IllegalArgumentException(new StringBuilder((String.valueOf(valueOf).length() + 37) + String.valueOf(str2).length()).append(valueOf).append(" declares multiple JSON fields named ").append(str2).toString());
                    }
                }
            }
            zzbth zzl = zzbth.zzl(zzbsk.zza(zzl.zzacc(), zzacb, zzacb.getGenericSuperclass()));
            zzacb = zzl.zzacb();
        }
        return linkedHashMap;
    }

    static boolean zza(Field field, boolean z, zzbsm com_google_android_gms_internal_zzbsm) {
        return (com_google_android_gms_internal_zzbsm.zza(field.getType(), z) || com_google_android_gms_internal_zzbsm.zza(field, z)) ? false : true;
    }

    private List<String> zzd(Field field) {
        return zza(this.zzcmF, field);
    }

    public <T> zzbsd<T> zza(zzbrl com_google_android_gms_internal_zzbrl, zzbth<T> com_google_android_gms_internal_zzbth_T) {
        Class zzacb = com_google_android_gms_internal_zzbth_T.zzacb();
        return !Object.class.isAssignableFrom(zzacb) ? null : new zza(zza(com_google_android_gms_internal_zzbrl, (zzbth) com_google_android_gms_internal_zzbth_T, zzacb), null);
    }

    public boolean zza(Field field, boolean z) {
        return zza(field, z, this.zzcmD);
    }
}
