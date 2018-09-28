package com.google.android.gms.internal;

import com.google.android.gms.common.ConnectionResult;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.sql.Timestamp;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.UUID;
import rx.internal.operators.OnSubscribeConcatMap;

public final class zzbtg {
    public static final zzbsd<Class> zzcoA;
    public static final zzbse zzcoB;
    public static final zzbsd<BitSet> zzcoC;
    public static final zzbse zzcoD;
    public static final zzbsd<Boolean> zzcoE;
    public static final zzbsd<Boolean> zzcoF;
    public static final zzbse zzcoG;
    public static final zzbsd<Number> zzcoH;
    public static final zzbse zzcoI;
    public static final zzbsd<Number> zzcoJ;
    public static final zzbse zzcoK;
    public static final zzbsd<Number> zzcoL;
    public static final zzbse zzcoM;
    public static final zzbsd<Number> zzcoN;
    public static final zzbsd<Number> zzcoO;
    public static final zzbsd<Number> zzcoP;
    public static final zzbsd<Number> zzcoQ;
    public static final zzbse zzcoR;
    public static final zzbsd<Character> zzcoS;
    public static final zzbse zzcoT;
    public static final zzbsd<String> zzcoU;
    public static final zzbsd<BigDecimal> zzcoV;
    public static final zzbsd<BigInteger> zzcoW;
    public static final zzbse zzcoX;
    public static final zzbsd<StringBuilder> zzcoY;
    public static final zzbse zzcoZ;
    public static final zzbsd<StringBuffer> zzcpa;
    public static final zzbse zzcpb;
    public static final zzbsd<URL> zzcpc;
    public static final zzbse zzcpd;
    public static final zzbsd<URI> zzcpe;
    public static final zzbse zzcpf;
    public static final zzbsd<InetAddress> zzcpg;
    public static final zzbse zzcph;
    public static final zzbsd<UUID> zzcpi;
    public static final zzbse zzcpj;
    public static final zzbse zzcpk;
    public static final zzbsd<Calendar> zzcpl;
    public static final zzbse zzcpm;
    public static final zzbsd<Locale> zzcpn;
    public static final zzbse zzcpo;
    public static final zzbsd<zzbrr> zzcpp;
    public static final zzbse zzcpq;
    public static final zzbse zzcpr;

    /* renamed from: com.google.android.gms.internal.zzbtg.26 */
    static /* synthetic */ class AnonymousClass26 {
        static final /* synthetic */ int[] zzcon;

        static {
            zzcon = new int[zzbtj.values().length];
            try {
                zzcon[zzbtj.NUMBER.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                zzcon[zzbtj.BOOLEAN.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                zzcon[zzbtj.STRING.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                zzcon[zzbtj.NULL.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                zzcon[zzbtj.BEGIN_ARRAY.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                zzcon[zzbtj.BEGIN_OBJECT.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                zzcon[zzbtj.END_DOCUMENT.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                zzcon[zzbtj.NAME.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                zzcon[zzbtj.END_OBJECT.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            try {
                zzcon[zzbtj.END_ARRAY.ordinal()] = 10;
            } catch (NoSuchFieldError e10) {
            }
        }
    }

    /* renamed from: com.google.android.gms.internal.zzbtg.1 */
    static class C12051 extends zzbsd<Class> {
        C12051() {
        }

        public void zza(zzbtk com_google_android_gms_internal_zzbtk, Class cls) throws IOException {
            if (cls == null) {
                com_google_android_gms_internal_zzbtk.zzaca();
            } else {
                String valueOf = String.valueOf(cls.getName());
                throw new UnsupportedOperationException(new StringBuilder(String.valueOf(valueOf).length() + 76).append("Attempted to serialize java.lang.Class: ").append(valueOf).append(". Forgot to register a type adapter?").toString());
            }
        }

        public /* synthetic */ Object zzb(zzbti com_google_android_gms_internal_zzbti) throws IOException {
            return zzo(com_google_android_gms_internal_zzbti);
        }

        public Class zzo(zzbti com_google_android_gms_internal_zzbti) throws IOException {
            if (com_google_android_gms_internal_zzbti.zzabQ() == zzbtj.NULL) {
                com_google_android_gms_internal_zzbti.nextNull();
                return null;
            }
            throw new UnsupportedOperationException("Attempted to deserialize a java.lang.Class. Forgot to register a type adapter?");
        }
    }

    /* renamed from: com.google.android.gms.internal.zzbtg.20 */
    static class AnonymousClass20 implements zzbse {
        final /* synthetic */ zzbth zzcnw;
        final /* synthetic */ zzbsd zzcpu;

        AnonymousClass20(zzbth com_google_android_gms_internal_zzbth, zzbsd com_google_android_gms_internal_zzbsd) {
            this.zzcnw = com_google_android_gms_internal_zzbth;
            this.zzcpu = com_google_android_gms_internal_zzbsd;
        }

        public <T> zzbsd<T> zza(zzbrl com_google_android_gms_internal_zzbrl, zzbth<T> com_google_android_gms_internal_zzbth_T) {
            return com_google_android_gms_internal_zzbth_T.equals(this.zzcnw) ? this.zzcpu : null;
        }
    }

    /* renamed from: com.google.android.gms.internal.zzbtg.21 */
    static class AnonymousClass21 implements zzbse {
        final /* synthetic */ zzbsd zzcpu;
        final /* synthetic */ Class zzcpv;

        AnonymousClass21(Class cls, zzbsd com_google_android_gms_internal_zzbsd) {
            this.zzcpv = cls;
            this.zzcpu = com_google_android_gms_internal_zzbsd;
        }

        public String toString() {
            String valueOf = String.valueOf(this.zzcpv.getName());
            String valueOf2 = String.valueOf(this.zzcpu);
            return new StringBuilder((String.valueOf(valueOf).length() + 23) + String.valueOf(valueOf2).length()).append("Factory[type=").append(valueOf).append(",adapter=").append(valueOf2).append("]").toString();
        }

        public <T> zzbsd<T> zza(zzbrl com_google_android_gms_internal_zzbrl, zzbth<T> com_google_android_gms_internal_zzbth_T) {
            return com_google_android_gms_internal_zzbth_T.zzacb() == this.zzcpv ? this.zzcpu : null;
        }
    }

    /* renamed from: com.google.android.gms.internal.zzbtg.22 */
    static class AnonymousClass22 implements zzbse {
        final /* synthetic */ zzbsd zzcpu;
        final /* synthetic */ Class zzcpw;
        final /* synthetic */ Class zzcpx;

        AnonymousClass22(Class cls, Class cls2, zzbsd com_google_android_gms_internal_zzbsd) {
            this.zzcpw = cls;
            this.zzcpx = cls2;
            this.zzcpu = com_google_android_gms_internal_zzbsd;
        }

        public String toString() {
            String valueOf = String.valueOf(this.zzcpx.getName());
            String valueOf2 = String.valueOf(this.zzcpw.getName());
            String valueOf3 = String.valueOf(this.zzcpu);
            return new StringBuilder(((String.valueOf(valueOf).length() + 24) + String.valueOf(valueOf2).length()) + String.valueOf(valueOf3).length()).append("Factory[type=").append(valueOf).append("+").append(valueOf2).append(",adapter=").append(valueOf3).append("]").toString();
        }

        public <T> zzbsd<T> zza(zzbrl com_google_android_gms_internal_zzbrl, zzbth<T> com_google_android_gms_internal_zzbth_T) {
            Class zzacb = com_google_android_gms_internal_zzbth_T.zzacb();
            return (zzacb == this.zzcpw || zzacb == this.zzcpx) ? this.zzcpu : null;
        }
    }

    /* renamed from: com.google.android.gms.internal.zzbtg.24 */
    static class AnonymousClass24 implements zzbse {
        final /* synthetic */ zzbsd zzcpu;
        final /* synthetic */ Class zzcpy;
        final /* synthetic */ Class zzcpz;

        AnonymousClass24(Class cls, Class cls2, zzbsd com_google_android_gms_internal_zzbsd) {
            this.zzcpy = cls;
            this.zzcpz = cls2;
            this.zzcpu = com_google_android_gms_internal_zzbsd;
        }

        public String toString() {
            String valueOf = String.valueOf(this.zzcpy.getName());
            String valueOf2 = String.valueOf(this.zzcpz.getName());
            String valueOf3 = String.valueOf(this.zzcpu);
            return new StringBuilder(((String.valueOf(valueOf).length() + 24) + String.valueOf(valueOf2).length()) + String.valueOf(valueOf3).length()).append("Factory[type=").append(valueOf).append("+").append(valueOf2).append(",adapter=").append(valueOf3).append("]").toString();
        }

        public <T> zzbsd<T> zza(zzbrl com_google_android_gms_internal_zzbrl, zzbth<T> com_google_android_gms_internal_zzbth_T) {
            Class zzacb = com_google_android_gms_internal_zzbth_T.zzacb();
            return (zzacb == this.zzcpy || zzacb == this.zzcpz) ? this.zzcpu : null;
        }
    }

    /* renamed from: com.google.android.gms.internal.zzbtg.25 */
    static class AnonymousClass25 implements zzbse {
        final /* synthetic */ Class zzcpA;
        final /* synthetic */ zzbsd zzcpu;

        AnonymousClass25(Class cls, zzbsd com_google_android_gms_internal_zzbsd) {
            this.zzcpA = cls;
            this.zzcpu = com_google_android_gms_internal_zzbsd;
        }

        public String toString() {
            String valueOf = String.valueOf(this.zzcpA.getName());
            String valueOf2 = String.valueOf(this.zzcpu);
            return new StringBuilder((String.valueOf(valueOf).length() + 32) + String.valueOf(valueOf2).length()).append("Factory[typeHierarchy=").append(valueOf).append(",adapter=").append(valueOf2).append("]").toString();
        }

        public <T> zzbsd<T> zza(zzbrl com_google_android_gms_internal_zzbrl, zzbth<T> com_google_android_gms_internal_zzbth_T) {
            return this.zzcpA.isAssignableFrom(com_google_android_gms_internal_zzbth_T.zzacb()) ? this.zzcpu : null;
        }
    }

    /* renamed from: com.google.android.gms.internal.zzbtg.2 */
    static class C12062 extends zzbsd<Number> {
        C12062() {
        }

        public void zza(zzbtk com_google_android_gms_internal_zzbtk, Number number) throws IOException {
            com_google_android_gms_internal_zzbtk.zza(number);
        }

        public /* synthetic */ Object zzb(zzbti com_google_android_gms_internal_zzbti) throws IOException {
            return zzg(com_google_android_gms_internal_zzbti);
        }

        public Number zzg(zzbti com_google_android_gms_internal_zzbti) throws IOException {
            if (com_google_android_gms_internal_zzbti.zzabQ() != zzbtj.NULL) {
                return Double.valueOf(com_google_android_gms_internal_zzbti.nextDouble());
            }
            com_google_android_gms_internal_zzbti.nextNull();
            return null;
        }
    }

    /* renamed from: com.google.android.gms.internal.zzbtg.3 */
    static class C12073 extends zzbsd<Number> {
        C12073() {
        }

        public void zza(zzbtk com_google_android_gms_internal_zzbtk, Number number) throws IOException {
            com_google_android_gms_internal_zzbtk.zza(number);
        }

        public /* synthetic */ Object zzb(zzbti com_google_android_gms_internal_zzbti) throws IOException {
            return zzg(com_google_android_gms_internal_zzbti);
        }

        public Number zzg(zzbti com_google_android_gms_internal_zzbti) throws IOException {
            zzbtj zzabQ = com_google_android_gms_internal_zzbti.zzabQ();
            switch (AnonymousClass26.zzcon[zzabQ.ordinal()]) {
                case OnSubscribeConcatMap.BOUNDARY /*1*/:
                    return new zzbso(com_google_android_gms_internal_zzbti.nextString());
                case ConnectionResult.SIGN_IN_REQUIRED /*4*/:
                    com_google_android_gms_internal_zzbti.nextNull();
                    return null;
                default:
                    String valueOf = String.valueOf(zzabQ);
                    throw new zzbsa(new StringBuilder(String.valueOf(valueOf).length() + 23).append("Expecting number, got: ").append(valueOf).toString());
            }
        }
    }

    /* renamed from: com.google.android.gms.internal.zzbtg.4 */
    static class C12084 extends zzbsd<Character> {
        C12084() {
        }

        public void zza(zzbtk com_google_android_gms_internal_zzbtk, Character ch) throws IOException {
            com_google_android_gms_internal_zzbtk.zzjX(ch == null ? null : String.valueOf(ch));
        }

        public /* synthetic */ Object zzb(zzbti com_google_android_gms_internal_zzbti) throws IOException {
            return zzp(com_google_android_gms_internal_zzbti);
        }

        public Character zzp(zzbti com_google_android_gms_internal_zzbti) throws IOException {
            if (com_google_android_gms_internal_zzbti.zzabQ() == zzbtj.NULL) {
                com_google_android_gms_internal_zzbti.nextNull();
                return null;
            }
            String nextString = com_google_android_gms_internal_zzbti.nextString();
            if (nextString.length() == 1) {
                return Character.valueOf(nextString.charAt(0));
            }
            String str = "Expecting character, got: ";
            nextString = String.valueOf(nextString);
            throw new zzbsa(nextString.length() != 0 ? str.concat(nextString) : new String(str));
        }
    }

    /* renamed from: com.google.android.gms.internal.zzbtg.5 */
    static class C12095 extends zzbsd<String> {
        C12095() {
        }

        public void zza(zzbtk com_google_android_gms_internal_zzbtk, String str) throws IOException {
            com_google_android_gms_internal_zzbtk.zzjX(str);
        }

        public /* synthetic */ Object zzb(zzbti com_google_android_gms_internal_zzbti) throws IOException {
            return zzq(com_google_android_gms_internal_zzbti);
        }

        public String zzq(zzbti com_google_android_gms_internal_zzbti) throws IOException {
            zzbtj zzabQ = com_google_android_gms_internal_zzbti.zzabQ();
            if (zzabQ != zzbtj.NULL) {
                return zzabQ == zzbtj.BOOLEAN ? Boolean.toString(com_google_android_gms_internal_zzbti.nextBoolean()) : com_google_android_gms_internal_zzbti.nextString();
            } else {
                com_google_android_gms_internal_zzbti.nextNull();
                return null;
            }
        }
    }

    /* renamed from: com.google.android.gms.internal.zzbtg.6 */
    static class C12106 extends zzbsd<BigDecimal> {
        C12106() {
        }

        public void zza(zzbtk com_google_android_gms_internal_zzbtk, BigDecimal bigDecimal) throws IOException {
            com_google_android_gms_internal_zzbtk.zza(bigDecimal);
        }

        public /* synthetic */ Object zzb(zzbti com_google_android_gms_internal_zzbti) throws IOException {
            return zzr(com_google_android_gms_internal_zzbti);
        }

        public BigDecimal zzr(zzbti com_google_android_gms_internal_zzbti) throws IOException {
            if (com_google_android_gms_internal_zzbti.zzabQ() == zzbtj.NULL) {
                com_google_android_gms_internal_zzbti.nextNull();
                return null;
            }
            try {
                return new BigDecimal(com_google_android_gms_internal_zzbti.nextString());
            } catch (Throwable e) {
                throw new zzbsa(e);
            }
        }
    }

    /* renamed from: com.google.android.gms.internal.zzbtg.7 */
    static class C12117 extends zzbsd<BigInteger> {
        C12117() {
        }

        public void zza(zzbtk com_google_android_gms_internal_zzbtk, BigInteger bigInteger) throws IOException {
            com_google_android_gms_internal_zzbtk.zza(bigInteger);
        }

        public /* synthetic */ Object zzb(zzbti com_google_android_gms_internal_zzbti) throws IOException {
            return zzs(com_google_android_gms_internal_zzbti);
        }

        public BigInteger zzs(zzbti com_google_android_gms_internal_zzbti) throws IOException {
            if (com_google_android_gms_internal_zzbti.zzabQ() == zzbtj.NULL) {
                com_google_android_gms_internal_zzbti.nextNull();
                return null;
            }
            try {
                return new BigInteger(com_google_android_gms_internal_zzbti.nextString());
            } catch (Throwable e) {
                throw new zzbsa(e);
            }
        }
    }

    /* renamed from: com.google.android.gms.internal.zzbtg.8 */
    static class C12128 extends zzbsd<StringBuilder> {
        C12128() {
        }

        public void zza(zzbtk com_google_android_gms_internal_zzbtk, StringBuilder stringBuilder) throws IOException {
            com_google_android_gms_internal_zzbtk.zzjX(stringBuilder == null ? null : stringBuilder.toString());
        }

        public /* synthetic */ Object zzb(zzbti com_google_android_gms_internal_zzbti) throws IOException {
            return zzt(com_google_android_gms_internal_zzbti);
        }

        public StringBuilder zzt(zzbti com_google_android_gms_internal_zzbti) throws IOException {
            if (com_google_android_gms_internal_zzbti.zzabQ() != zzbtj.NULL) {
                return new StringBuilder(com_google_android_gms_internal_zzbti.nextString());
            }
            com_google_android_gms_internal_zzbti.nextNull();
            return null;
        }
    }

    /* renamed from: com.google.android.gms.internal.zzbtg.9 */
    static class C12139 extends zzbsd<StringBuffer> {
        C12139() {
        }

        public void zza(zzbtk com_google_android_gms_internal_zzbtk, StringBuffer stringBuffer) throws IOException {
            com_google_android_gms_internal_zzbtk.zzjX(stringBuffer == null ? null : stringBuffer.toString());
        }

        public /* synthetic */ Object zzb(zzbti com_google_android_gms_internal_zzbti) throws IOException {
            return zzu(com_google_android_gms_internal_zzbti);
        }

        public StringBuffer zzu(zzbti com_google_android_gms_internal_zzbti) throws IOException {
            if (com_google_android_gms_internal_zzbti.zzabQ() != zzbtj.NULL) {
                return new StringBuffer(com_google_android_gms_internal_zzbti.nextString());
            }
            com_google_android_gms_internal_zzbti.nextNull();
            return null;
        }
    }

    private static final class zza<T extends Enum<T>> extends zzbsd<T> {
        private final Map<String, T> zzcpB;
        private final Map<T, String> zzcpC;

        public zza(Class<T> cls) {
            this.zzcpB = new HashMap();
            this.zzcpC = new HashMap();
            try {
                for (Enum enumR : (Enum[]) cls.getEnumConstants()) {
                    String name = enumR.name();
                    zzbsg com_google_android_gms_internal_zzbsg = (zzbsg) cls.getField(name).getAnnotation(zzbsg.class);
                    if (com_google_android_gms_internal_zzbsg != null) {
                        name = com_google_android_gms_internal_zzbsg.value();
                        for (Object put : com_google_android_gms_internal_zzbsg.zzabH()) {
                            this.zzcpB.put(put, enumR);
                        }
                    }
                    String str = name;
                    this.zzcpB.put(str, enumR);
                    this.zzcpC.put(enumR, str);
                }
            } catch (NoSuchFieldException e) {
                throw new AssertionError();
            }
        }

        public T zzF(zzbti com_google_android_gms_internal_zzbti) throws IOException {
            if (com_google_android_gms_internal_zzbti.zzabQ() != zzbtj.NULL) {
                return (Enum) this.zzcpB.get(com_google_android_gms_internal_zzbti.nextString());
            }
            com_google_android_gms_internal_zzbti.nextNull();
            return null;
        }

        public void zza(zzbtk com_google_android_gms_internal_zzbtk, T t) throws IOException {
            com_google_android_gms_internal_zzbtk.zzjX(t == null ? null : (String) this.zzcpC.get(t));
        }

        public /* synthetic */ Object zzb(zzbti com_google_android_gms_internal_zzbti) throws IOException {
            return zzF(com_google_android_gms_internal_zzbti);
        }
    }

    static {
        zzcoA = new C12051();
        zzcoB = zza(Class.class, zzcoA);
        zzcoC = new zzbsd<BitSet>() {
            public void zza(zzbtk com_google_android_gms_internal_zzbtk, BitSet bitSet) throws IOException {
                if (bitSet == null) {
                    com_google_android_gms_internal_zzbtk.zzaca();
                    return;
                }
                com_google_android_gms_internal_zzbtk.zzabW();
                for (int i = 0; i < bitSet.length(); i++) {
                    com_google_android_gms_internal_zzbtk.zzaU((long) (bitSet.get(i) ? 1 : 0));
                }
                com_google_android_gms_internal_zzbtk.zzabX();
            }

            public /* synthetic */ Object zzb(zzbti com_google_android_gms_internal_zzbti) throws IOException {
                return zzx(com_google_android_gms_internal_zzbti);
            }

            public BitSet zzx(zzbti com_google_android_gms_internal_zzbti) throws IOException {
                String valueOf;
                if (com_google_android_gms_internal_zzbti.zzabQ() == zzbtj.NULL) {
                    com_google_android_gms_internal_zzbti.nextNull();
                    return null;
                }
                BitSet bitSet = new BitSet();
                com_google_android_gms_internal_zzbti.beginArray();
                zzbtj zzabQ = com_google_android_gms_internal_zzbti.zzabQ();
                int i = 0;
                while (zzabQ != zzbtj.END_ARRAY) {
                    boolean z;
                    switch (AnonymousClass26.zzcon[zzabQ.ordinal()]) {
                        case OnSubscribeConcatMap.BOUNDARY /*1*/:
                            if (com_google_android_gms_internal_zzbti.nextInt() == 0) {
                                z = false;
                                break;
                            }
                            z = true;
                            break;
                        case OnSubscribeConcatMap.END /*2*/:
                            z = com_google_android_gms_internal_zzbti.nextBoolean();
                            break;
                        case ConnectionResult.SERVICE_DISABLED /*3*/:
                            Object nextString = com_google_android_gms_internal_zzbti.nextString();
                            try {
                                if (Integer.parseInt(nextString) == 0) {
                                    z = false;
                                    break;
                                }
                                z = true;
                                break;
                            } catch (NumberFormatException e) {
                                String str = "Error: Expecting: bitset number value (1, 0), Found: ";
                                valueOf = String.valueOf(nextString);
                                throw new zzbsa(valueOf.length() != 0 ? str.concat(valueOf) : new String(str));
                            }
                        default:
                            valueOf = String.valueOf(zzabQ);
                            throw new zzbsa(new StringBuilder(String.valueOf(valueOf).length() + 27).append("Invalid bitset value type: ").append(valueOf).toString());
                    }
                    if (z) {
                        bitSet.set(i);
                    }
                    i++;
                    zzabQ = com_google_android_gms_internal_zzbti.zzabQ();
                }
                com_google_android_gms_internal_zzbti.endArray();
                return bitSet;
            }
        };
        zzcoD = zza(BitSet.class, zzcoC);
        zzcoE = new zzbsd<Boolean>() {
            public Boolean zzE(zzbti com_google_android_gms_internal_zzbti) throws IOException {
                if (com_google_android_gms_internal_zzbti.zzabQ() != zzbtj.NULL) {
                    return com_google_android_gms_internal_zzbti.zzabQ() == zzbtj.STRING ? Boolean.valueOf(Boolean.parseBoolean(com_google_android_gms_internal_zzbti.nextString())) : Boolean.valueOf(com_google_android_gms_internal_zzbti.nextBoolean());
                } else {
                    com_google_android_gms_internal_zzbti.nextNull();
                    return null;
                }
            }

            public void zza(zzbtk com_google_android_gms_internal_zzbtk, Boolean bool) throws IOException {
                if (bool == null) {
                    com_google_android_gms_internal_zzbtk.zzaca();
                } else {
                    com_google_android_gms_internal_zzbtk.zzbg(bool.booleanValue());
                }
            }

            public /* synthetic */ Object zzb(zzbti com_google_android_gms_internal_zzbti) throws IOException {
                return zzE(com_google_android_gms_internal_zzbti);
            }
        };
        zzcoF = new zzbsd<Boolean>() {
            public Boolean zzE(zzbti com_google_android_gms_internal_zzbti) throws IOException {
                if (com_google_android_gms_internal_zzbti.zzabQ() != zzbtj.NULL) {
                    return Boolean.valueOf(com_google_android_gms_internal_zzbti.nextString());
                }
                com_google_android_gms_internal_zzbti.nextNull();
                return null;
            }

            public void zza(zzbtk com_google_android_gms_internal_zzbtk, Boolean bool) throws IOException {
                com_google_android_gms_internal_zzbtk.zzjX(bool == null ? "null" : bool.toString());
            }

            public /* synthetic */ Object zzb(zzbti com_google_android_gms_internal_zzbti) throws IOException {
                return zzE(com_google_android_gms_internal_zzbti);
            }
        };
        zzcoG = zza(Boolean.TYPE, Boolean.class, zzcoE);
        zzcoH = new zzbsd<Number>() {
            public void zza(zzbtk com_google_android_gms_internal_zzbtk, Number number) throws IOException {
                com_google_android_gms_internal_zzbtk.zza(number);
            }

            public /* synthetic */ Object zzb(zzbti com_google_android_gms_internal_zzbti) throws IOException {
                return zzg(com_google_android_gms_internal_zzbti);
            }

            public Number zzg(zzbti com_google_android_gms_internal_zzbti) throws IOException {
                if (com_google_android_gms_internal_zzbti.zzabQ() == zzbtj.NULL) {
                    com_google_android_gms_internal_zzbti.nextNull();
                    return null;
                }
                try {
                    return Byte.valueOf((byte) com_google_android_gms_internal_zzbti.nextInt());
                } catch (Throwable e) {
                    throw new zzbsa(e);
                }
            }
        };
        zzcoI = zza(Byte.TYPE, Byte.class, zzcoH);
        zzcoJ = new zzbsd<Number>() {
            public void zza(zzbtk com_google_android_gms_internal_zzbtk, Number number) throws IOException {
                com_google_android_gms_internal_zzbtk.zza(number);
            }

            public /* synthetic */ Object zzb(zzbti com_google_android_gms_internal_zzbti) throws IOException {
                return zzg(com_google_android_gms_internal_zzbti);
            }

            public Number zzg(zzbti com_google_android_gms_internal_zzbti) throws IOException {
                if (com_google_android_gms_internal_zzbti.zzabQ() == zzbtj.NULL) {
                    com_google_android_gms_internal_zzbti.nextNull();
                    return null;
                }
                try {
                    return Short.valueOf((short) com_google_android_gms_internal_zzbti.nextInt());
                } catch (Throwable e) {
                    throw new zzbsa(e);
                }
            }
        };
        zzcoK = zza(Short.TYPE, Short.class, zzcoJ);
        zzcoL = new zzbsd<Number>() {
            public void zza(zzbtk com_google_android_gms_internal_zzbtk, Number number) throws IOException {
                com_google_android_gms_internal_zzbtk.zza(number);
            }

            public /* synthetic */ Object zzb(zzbti com_google_android_gms_internal_zzbti) throws IOException {
                return zzg(com_google_android_gms_internal_zzbti);
            }

            public Number zzg(zzbti com_google_android_gms_internal_zzbti) throws IOException {
                if (com_google_android_gms_internal_zzbti.zzabQ() == zzbtj.NULL) {
                    com_google_android_gms_internal_zzbti.nextNull();
                    return null;
                }
                try {
                    return Integer.valueOf(com_google_android_gms_internal_zzbti.nextInt());
                } catch (Throwable e) {
                    throw new zzbsa(e);
                }
            }
        };
        zzcoM = zza(Integer.TYPE, Integer.class, zzcoL);
        zzcoN = new zzbsd<Number>() {
            public void zza(zzbtk com_google_android_gms_internal_zzbtk, Number number) throws IOException {
                com_google_android_gms_internal_zzbtk.zza(number);
            }

            public /* synthetic */ Object zzb(zzbti com_google_android_gms_internal_zzbti) throws IOException {
                return zzg(com_google_android_gms_internal_zzbti);
            }

            public Number zzg(zzbti com_google_android_gms_internal_zzbti) throws IOException {
                if (com_google_android_gms_internal_zzbti.zzabQ() == zzbtj.NULL) {
                    com_google_android_gms_internal_zzbti.nextNull();
                    return null;
                }
                try {
                    return Long.valueOf(com_google_android_gms_internal_zzbti.nextLong());
                } catch (Throwable e) {
                    throw new zzbsa(e);
                }
            }
        };
        zzcoO = new zzbsd<Number>() {
            public void zza(zzbtk com_google_android_gms_internal_zzbtk, Number number) throws IOException {
                com_google_android_gms_internal_zzbtk.zza(number);
            }

            public /* synthetic */ Object zzb(zzbti com_google_android_gms_internal_zzbti) throws IOException {
                return zzg(com_google_android_gms_internal_zzbti);
            }

            public Number zzg(zzbti com_google_android_gms_internal_zzbti) throws IOException {
                if (com_google_android_gms_internal_zzbti.zzabQ() != zzbtj.NULL) {
                    return Float.valueOf((float) com_google_android_gms_internal_zzbti.nextDouble());
                }
                com_google_android_gms_internal_zzbti.nextNull();
                return null;
            }
        };
        zzcoP = new C12062();
        zzcoQ = new C12073();
        zzcoR = zza(Number.class, zzcoQ);
        zzcoS = new C12084();
        zzcoT = zza(Character.TYPE, Character.class, zzcoS);
        zzcoU = new C12095();
        zzcoV = new C12106();
        zzcoW = new C12117();
        zzcoX = zza(String.class, zzcoU);
        zzcoY = new C12128();
        zzcoZ = zza(StringBuilder.class, zzcoY);
        zzcpa = new C12139();
        zzcpb = zza(StringBuffer.class, zzcpa);
        zzcpc = new zzbsd<URL>() {
            public void zza(zzbtk com_google_android_gms_internal_zzbtk, URL url) throws IOException {
                com_google_android_gms_internal_zzbtk.zzjX(url == null ? null : url.toExternalForm());
            }

            public /* synthetic */ Object zzb(zzbti com_google_android_gms_internal_zzbti) throws IOException {
                return zzv(com_google_android_gms_internal_zzbti);
            }

            public URL zzv(zzbti com_google_android_gms_internal_zzbti) throws IOException {
                if (com_google_android_gms_internal_zzbti.zzabQ() == zzbtj.NULL) {
                    com_google_android_gms_internal_zzbti.nextNull();
                    return null;
                }
                String nextString = com_google_android_gms_internal_zzbti.nextString();
                return !"null".equals(nextString) ? new URL(nextString) : null;
            }
        };
        zzcpd = zza(URL.class, zzcpc);
        zzcpe = new zzbsd<URI>() {
            public void zza(zzbtk com_google_android_gms_internal_zzbtk, URI uri) throws IOException {
                com_google_android_gms_internal_zzbtk.zzjX(uri == null ? null : uri.toASCIIString());
            }

            public /* synthetic */ Object zzb(zzbti com_google_android_gms_internal_zzbti) throws IOException {
                return zzw(com_google_android_gms_internal_zzbti);
            }

            public URI zzw(zzbti com_google_android_gms_internal_zzbti) throws IOException {
                URI uri = null;
                if (com_google_android_gms_internal_zzbti.zzabQ() == zzbtj.NULL) {
                    com_google_android_gms_internal_zzbti.nextNull();
                } else {
                    try {
                        String nextString = com_google_android_gms_internal_zzbti.nextString();
                        if (!"null".equals(nextString)) {
                            uri = new URI(nextString);
                        }
                    } catch (Throwable e) {
                        throw new zzbrs(e);
                    }
                }
                return uri;
            }
        };
        zzcpf = zza(URI.class, zzcpe);
        zzcpg = new zzbsd<InetAddress>() {
            public void zza(zzbtk com_google_android_gms_internal_zzbtk, InetAddress inetAddress) throws IOException {
                com_google_android_gms_internal_zzbtk.zzjX(inetAddress == null ? null : inetAddress.getHostAddress());
            }

            public /* synthetic */ Object zzb(zzbti com_google_android_gms_internal_zzbti) throws IOException {
                return zzy(com_google_android_gms_internal_zzbti);
            }

            public InetAddress zzy(zzbti com_google_android_gms_internal_zzbti) throws IOException {
                if (com_google_android_gms_internal_zzbti.zzabQ() != zzbtj.NULL) {
                    return InetAddress.getByName(com_google_android_gms_internal_zzbti.nextString());
                }
                com_google_android_gms_internal_zzbti.nextNull();
                return null;
            }
        };
        zzcph = zzb(InetAddress.class, zzcpg);
        zzcpi = new zzbsd<UUID>() {
            public void zza(zzbtk com_google_android_gms_internal_zzbtk, UUID uuid) throws IOException {
                com_google_android_gms_internal_zzbtk.zzjX(uuid == null ? null : uuid.toString());
            }

            public /* synthetic */ Object zzb(zzbti com_google_android_gms_internal_zzbti) throws IOException {
                return zzz(com_google_android_gms_internal_zzbti);
            }

            public UUID zzz(zzbti com_google_android_gms_internal_zzbti) throws IOException {
                if (com_google_android_gms_internal_zzbti.zzabQ() != zzbtj.NULL) {
                    return UUID.fromString(com_google_android_gms_internal_zzbti.nextString());
                }
                com_google_android_gms_internal_zzbti.nextNull();
                return null;
            }
        };
        zzcpj = zza(UUID.class, zzcpi);
        zzcpk = new zzbse() {

            /* renamed from: com.google.android.gms.internal.zzbtg.15.1 */
            class C12041 extends zzbsd<Timestamp> {
                final /* synthetic */ zzbsd zzcps;
                final /* synthetic */ AnonymousClass15 zzcpt;

                C12041(AnonymousClass15 anonymousClass15, zzbsd com_google_android_gms_internal_zzbsd) {
                    this.zzcpt = anonymousClass15;
                    this.zzcps = com_google_android_gms_internal_zzbsd;
                }

                public Timestamp zzA(zzbti com_google_android_gms_internal_zzbti) throws IOException {
                    Date date = (Date) this.zzcps.zzb(com_google_android_gms_internal_zzbti);
                    return date != null ? new Timestamp(date.getTime()) : null;
                }

                public void zza(zzbtk com_google_android_gms_internal_zzbtk, Timestamp timestamp) throws IOException {
                    this.zzcps.zza(com_google_android_gms_internal_zzbtk, timestamp);
                }

                public /* synthetic */ Object zzb(zzbti com_google_android_gms_internal_zzbti) throws IOException {
                    return zzA(com_google_android_gms_internal_zzbti);
                }
            }

            public <T> zzbsd<T> zza(zzbrl com_google_android_gms_internal_zzbrl, zzbth<T> com_google_android_gms_internal_zzbth_T) {
                return com_google_android_gms_internal_zzbth_T.zzacb() != Timestamp.class ? null : new C12041(this, com_google_android_gms_internal_zzbrl.zzj(Date.class));
            }
        };
        zzcpl = new zzbsd<Calendar>() {
            public Calendar zzB(zzbti com_google_android_gms_internal_zzbti) throws IOException {
                int i = 0;
                if (com_google_android_gms_internal_zzbti.zzabQ() == zzbtj.NULL) {
                    com_google_android_gms_internal_zzbti.nextNull();
                    return null;
                }
                com_google_android_gms_internal_zzbti.beginObject();
                int i2 = 0;
                int i3 = 0;
                int i4 = 0;
                int i5 = 0;
                int i6 = 0;
                while (com_google_android_gms_internal_zzbti.zzabQ() != zzbtj.END_OBJECT) {
                    String nextName = com_google_android_gms_internal_zzbti.nextName();
                    int nextInt = com_google_android_gms_internal_zzbti.nextInt();
                    if ("year".equals(nextName)) {
                        i6 = nextInt;
                    } else if ("month".equals(nextName)) {
                        i5 = nextInt;
                    } else if ("dayOfMonth".equals(nextName)) {
                        i4 = nextInt;
                    } else if ("hourOfDay".equals(nextName)) {
                        i3 = nextInt;
                    } else if ("minute".equals(nextName)) {
                        i2 = nextInt;
                    } else if ("second".equals(nextName)) {
                        i = nextInt;
                    }
                }
                com_google_android_gms_internal_zzbti.endObject();
                return new GregorianCalendar(i6, i5, i4, i3, i2, i);
            }

            public void zza(zzbtk com_google_android_gms_internal_zzbtk, Calendar calendar) throws IOException {
                if (calendar == null) {
                    com_google_android_gms_internal_zzbtk.zzaca();
                    return;
                }
                com_google_android_gms_internal_zzbtk.zzabY();
                com_google_android_gms_internal_zzbtk.zzjW("year");
                com_google_android_gms_internal_zzbtk.zzaU((long) calendar.get(1));
                com_google_android_gms_internal_zzbtk.zzjW("month");
                com_google_android_gms_internal_zzbtk.zzaU((long) calendar.get(2));
                com_google_android_gms_internal_zzbtk.zzjW("dayOfMonth");
                com_google_android_gms_internal_zzbtk.zzaU((long) calendar.get(5));
                com_google_android_gms_internal_zzbtk.zzjW("hourOfDay");
                com_google_android_gms_internal_zzbtk.zzaU((long) calendar.get(11));
                com_google_android_gms_internal_zzbtk.zzjW("minute");
                com_google_android_gms_internal_zzbtk.zzaU((long) calendar.get(12));
                com_google_android_gms_internal_zzbtk.zzjW("second");
                com_google_android_gms_internal_zzbtk.zzaU((long) calendar.get(13));
                com_google_android_gms_internal_zzbtk.zzabZ();
            }

            public /* synthetic */ Object zzb(zzbti com_google_android_gms_internal_zzbti) throws IOException {
                return zzB(com_google_android_gms_internal_zzbti);
            }
        };
        zzcpm = zzb(Calendar.class, GregorianCalendar.class, zzcpl);
        zzcpn = new zzbsd<Locale>() {
            public Locale zzC(zzbti com_google_android_gms_internal_zzbti) throws IOException {
                if (com_google_android_gms_internal_zzbti.zzabQ() == zzbtj.NULL) {
                    com_google_android_gms_internal_zzbti.nextNull();
                    return null;
                }
                StringTokenizer stringTokenizer = new StringTokenizer(com_google_android_gms_internal_zzbti.nextString(), "_");
                String nextToken = stringTokenizer.hasMoreElements() ? stringTokenizer.nextToken() : null;
                String nextToken2 = stringTokenizer.hasMoreElements() ? stringTokenizer.nextToken() : null;
                String nextToken3 = stringTokenizer.hasMoreElements() ? stringTokenizer.nextToken() : null;
                return (nextToken2 == null && nextToken3 == null) ? new Locale(nextToken) : nextToken3 == null ? new Locale(nextToken, nextToken2) : new Locale(nextToken, nextToken2, nextToken3);
            }

            public void zza(zzbtk com_google_android_gms_internal_zzbtk, Locale locale) throws IOException {
                com_google_android_gms_internal_zzbtk.zzjX(locale == null ? null : locale.toString());
            }

            public /* synthetic */ Object zzb(zzbti com_google_android_gms_internal_zzbti) throws IOException {
                return zzC(com_google_android_gms_internal_zzbti);
            }
        };
        zzcpo = zza(Locale.class, zzcpn);
        zzcpp = new zzbsd<zzbrr>() {
            public zzbrr zzD(zzbti com_google_android_gms_internal_zzbti) throws IOException {
                zzbrr com_google_android_gms_internal_zzbro;
                switch (AnonymousClass26.zzcon[com_google_android_gms_internal_zzbti.zzabQ().ordinal()]) {
                    case OnSubscribeConcatMap.BOUNDARY /*1*/:
                        return new zzbrx(new zzbso(com_google_android_gms_internal_zzbti.nextString()));
                    case OnSubscribeConcatMap.END /*2*/:
                        return new zzbrx(Boolean.valueOf(com_google_android_gms_internal_zzbti.nextBoolean()));
                    case ConnectionResult.SERVICE_DISABLED /*3*/:
                        return new zzbrx(com_google_android_gms_internal_zzbti.nextString());
                    case ConnectionResult.SIGN_IN_REQUIRED /*4*/:
                        com_google_android_gms_internal_zzbti.nextNull();
                        return zzbrt.zzcmL;
                    case ConnectionResult.INVALID_ACCOUNT /*5*/:
                        com_google_android_gms_internal_zzbro = new zzbro();
                        com_google_android_gms_internal_zzbti.beginArray();
                        while (com_google_android_gms_internal_zzbti.hasNext()) {
                            com_google_android_gms_internal_zzbro.zzc((zzbrr) zzb(com_google_android_gms_internal_zzbti));
                        }
                        com_google_android_gms_internal_zzbti.endArray();
                        return com_google_android_gms_internal_zzbro;
                    case ConnectionResult.RESOLUTION_REQUIRED /*6*/:
                        com_google_android_gms_internal_zzbro = new zzbru();
                        com_google_android_gms_internal_zzbti.beginObject();
                        while (com_google_android_gms_internal_zzbti.hasNext()) {
                            com_google_android_gms_internal_zzbro.zza(com_google_android_gms_internal_zzbti.nextName(), (zzbrr) zzb(com_google_android_gms_internal_zzbti));
                        }
                        com_google_android_gms_internal_zzbti.endObject();
                        return com_google_android_gms_internal_zzbro;
                    default:
                        throw new IllegalArgumentException();
                }
            }

            public void zza(zzbtk com_google_android_gms_internal_zzbtk, zzbrr com_google_android_gms_internal_zzbrr) throws IOException {
                if (com_google_android_gms_internal_zzbrr == null || com_google_android_gms_internal_zzbrr.zzaby()) {
                    com_google_android_gms_internal_zzbtk.zzaca();
                } else if (com_google_android_gms_internal_zzbrr.zzabx()) {
                    zzbrx zzabB = com_google_android_gms_internal_zzbrr.zzabB();
                    if (zzabB.zzabE()) {
                        com_google_android_gms_internal_zzbtk.zza(zzabB.zzabt());
                    } else if (zzabB.zzabD()) {
                        com_google_android_gms_internal_zzbtk.zzbg(zzabB.getAsBoolean());
                    } else {
                        com_google_android_gms_internal_zzbtk.zzjX(zzabB.zzabu());
                    }
                } else if (com_google_android_gms_internal_zzbrr.zzabv()) {
                    com_google_android_gms_internal_zzbtk.zzabW();
                    Iterator it = com_google_android_gms_internal_zzbrr.zzabA().iterator();
                    while (it.hasNext()) {
                        zza(com_google_android_gms_internal_zzbtk, (zzbrr) it.next());
                    }
                    com_google_android_gms_internal_zzbtk.zzabX();
                } else if (com_google_android_gms_internal_zzbrr.zzabw()) {
                    com_google_android_gms_internal_zzbtk.zzabY();
                    for (Entry entry : com_google_android_gms_internal_zzbrr.zzabz().entrySet()) {
                        com_google_android_gms_internal_zzbtk.zzjW((String) entry.getKey());
                        zza(com_google_android_gms_internal_zzbtk, (zzbrr) entry.getValue());
                    }
                    com_google_android_gms_internal_zzbtk.zzabZ();
                } else {
                    String valueOf = String.valueOf(com_google_android_gms_internal_zzbrr.getClass());
                    throw new IllegalArgumentException(new StringBuilder(String.valueOf(valueOf).length() + 15).append("Couldn't write ").append(valueOf).toString());
                }
            }

            public /* synthetic */ Object zzb(zzbti com_google_android_gms_internal_zzbti) throws IOException {
                return zzD(com_google_android_gms_internal_zzbti);
            }
        };
        zzcpq = zzb(zzbrr.class, zzcpp);
        zzcpr = new zzbse() {
            public <T> zzbsd<T> zza(zzbrl com_google_android_gms_internal_zzbrl, zzbth<T> com_google_android_gms_internal_zzbth_T) {
                Class zzacb = com_google_android_gms_internal_zzbth_T.zzacb();
                if (!Enum.class.isAssignableFrom(zzacb) || zzacb == Enum.class) {
                    return null;
                }
                if (!zzacb.isEnum()) {
                    zzacb = zzacb.getSuperclass();
                }
                return new zza(zzacb);
            }
        };
    }

    public static <TT> zzbse zza(zzbth<TT> com_google_android_gms_internal_zzbth_TT, zzbsd<TT> com_google_android_gms_internal_zzbsd_TT) {
        return new AnonymousClass20(com_google_android_gms_internal_zzbth_TT, com_google_android_gms_internal_zzbsd_TT);
    }

    public static <TT> zzbse zza(Class<TT> cls, zzbsd<TT> com_google_android_gms_internal_zzbsd_TT) {
        return new AnonymousClass21(cls, com_google_android_gms_internal_zzbsd_TT);
    }

    public static <TT> zzbse zza(Class<TT> cls, Class<TT> cls2, zzbsd<? super TT> com_google_android_gms_internal_zzbsd__super_TT) {
        return new AnonymousClass22(cls, cls2, com_google_android_gms_internal_zzbsd__super_TT);
    }

    public static <TT> zzbse zzb(Class<TT> cls, zzbsd<TT> com_google_android_gms_internal_zzbsd_TT) {
        return new AnonymousClass25(cls, com_google_android_gms_internal_zzbsd_TT);
    }

    public static <TT> zzbse zzb(Class<TT> cls, Class<? extends TT> cls2, zzbsd<? super TT> com_google_android_gms_internal_zzbsd__super_TT) {
        return new AnonymousClass24(cls, cls2, com_google_android_gms_internal_zzbsd__super_TT);
    }
}
