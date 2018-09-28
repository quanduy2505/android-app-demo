package com.google.android.gms.internal;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.util.SparseArray;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.safeparcel.zzb;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.util.zzc;
import com.google.android.gms.common.util.zzp;
import com.google.android.gms.common.util.zzq;
import com.google.android.gms.internal.zzack.zza;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import rx.internal.operators.OnSubscribeConcatMap;
import rx.internal.schedulers.NewThreadWorker;

public class zzacr extends zzacl {
    public static final Creator<zzacr> CREATOR;
    private final String mClassName;
    private final int mVersionCode;
    private final zzaco zzaFI;
    private final Parcel zzaFP;
    private final int zzaFQ;
    private int zzaFR;
    private int zzaFS;

    static {
        CREATOR = new zzacs();
    }

    zzacr(int i, Parcel parcel, zzaco com_google_android_gms_internal_zzaco) {
        this.mVersionCode = i;
        this.zzaFP = (Parcel) zzac.zzw(parcel);
        this.zzaFQ = 2;
        this.zzaFI = com_google_android_gms_internal_zzaco;
        if (this.zzaFI == null) {
            this.mClassName = null;
        } else {
            this.mClassName = this.zzaFI.zzxY();
        }
        this.zzaFR = 2;
    }

    private static SparseArray<Entry<String, zza<?, ?>>> zzX(Map<String, zza<?, ?>> map) {
        SparseArray<Entry<String, zza<?, ?>>> sparseArray = new SparseArray();
        for (Entry entry : map.entrySet()) {
            sparseArray.put(((zza) entry.getValue()).zzxQ(), entry);
        }
        return sparseArray;
    }

    private void zza(StringBuilder stringBuilder, int i, Object obj) {
        switch (i) {
            case NewThreadWorker.PURGE_FREQUENCY:
            case OnSubscribeConcatMap.BOUNDARY /*1*/:
            case OnSubscribeConcatMap.END /*2*/:
            case ConnectionResult.SERVICE_DISABLED /*3*/:
            case ConnectionResult.SIGN_IN_REQUIRED /*4*/:
            case ConnectionResult.INVALID_ACCOUNT /*5*/:
            case ConnectionResult.RESOLUTION_REQUIRED /*6*/:
                stringBuilder.append(obj);
            case ConnectionResult.NETWORK_ERROR /*7*/:
                stringBuilder.append("\"").append(zzp.zzdC(obj.toString())).append("\"");
            case ConnectionResult.INTERNAL_ERROR /*8*/:
                stringBuilder.append("\"").append(zzc.zzq((byte[]) obj)).append("\"");
            case ConnectionResult.SERVICE_INVALID /*9*/:
                stringBuilder.append("\"").append(zzc.zzr((byte[]) obj));
                stringBuilder.append("\"");
            case ConnectionResult.DEVELOPER_ERROR /*10*/:
                zzq.zza(stringBuilder, (HashMap) obj);
            case ConnectionResult.LICENSE_CHECK_FAILED /*11*/:
                throw new IllegalArgumentException("Method does not accept concrete type.");
            default:
                throw new IllegalArgumentException("Unknown type = " + i);
        }
    }

    private void zza(StringBuilder stringBuilder, zza<?, ?> com_google_android_gms_internal_zzack_zza___, Parcel parcel, int i) {
        switch (com_google_android_gms_internal_zzack_zza___.zzxN()) {
            case NewThreadWorker.PURGE_FREQUENCY:
                zzb(stringBuilder, (zza) com_google_android_gms_internal_zzack_zza___, zza(com_google_android_gms_internal_zzack_zza___, Integer.valueOf(zzb.zzg(parcel, i))));
            case OnSubscribeConcatMap.BOUNDARY /*1*/:
                zzb(stringBuilder, (zza) com_google_android_gms_internal_zzack_zza___, zza(com_google_android_gms_internal_zzack_zza___, zzb.zzk(parcel, i)));
            case OnSubscribeConcatMap.END /*2*/:
                zzb(stringBuilder, (zza) com_google_android_gms_internal_zzack_zza___, zza(com_google_android_gms_internal_zzack_zza___, Long.valueOf(zzb.zzi(parcel, i))));
            case ConnectionResult.SERVICE_DISABLED /*3*/:
                zzb(stringBuilder, (zza) com_google_android_gms_internal_zzack_zza___, zza(com_google_android_gms_internal_zzack_zza___, Float.valueOf(zzb.zzl(parcel, i))));
            case ConnectionResult.SIGN_IN_REQUIRED /*4*/:
                zzb(stringBuilder, (zza) com_google_android_gms_internal_zzack_zza___, zza(com_google_android_gms_internal_zzack_zza___, Double.valueOf(zzb.zzn(parcel, i))));
            case ConnectionResult.INVALID_ACCOUNT /*5*/:
                zzb(stringBuilder, (zza) com_google_android_gms_internal_zzack_zza___, zza(com_google_android_gms_internal_zzack_zza___, zzb.zzp(parcel, i)));
            case ConnectionResult.RESOLUTION_REQUIRED /*6*/:
                zzb(stringBuilder, (zza) com_google_android_gms_internal_zzack_zza___, zza(com_google_android_gms_internal_zzack_zza___, Boolean.valueOf(zzb.zzc(parcel, i))));
            case ConnectionResult.NETWORK_ERROR /*7*/:
                zzb(stringBuilder, (zza) com_google_android_gms_internal_zzack_zza___, zza(com_google_android_gms_internal_zzack_zza___, zzb.zzq(parcel, i)));
            case ConnectionResult.INTERNAL_ERROR /*8*/:
            case ConnectionResult.SERVICE_INVALID /*9*/:
                zzb(stringBuilder, (zza) com_google_android_gms_internal_zzack_zza___, zza(com_google_android_gms_internal_zzack_zza___, zzb.zzt(parcel, i)));
            case ConnectionResult.DEVELOPER_ERROR /*10*/:
                zzb(stringBuilder, (zza) com_google_android_gms_internal_zzack_zza___, zza(com_google_android_gms_internal_zzack_zza___, zzr(zzb.zzs(parcel, i))));
            case ConnectionResult.LICENSE_CHECK_FAILED /*11*/:
                throw new IllegalArgumentException("Method does not accept concrete type.");
            default:
                throw new IllegalArgumentException("Unknown field out type = " + com_google_android_gms_internal_zzack_zza___.zzxN());
        }
    }

    private void zza(StringBuilder stringBuilder, String str, zza<?, ?> com_google_android_gms_internal_zzack_zza___, Parcel parcel, int i) {
        stringBuilder.append("\"").append(str).append("\":");
        if (com_google_android_gms_internal_zzack_zza___.zzxT()) {
            zza(stringBuilder, com_google_android_gms_internal_zzack_zza___, parcel, i);
        } else {
            zzb(stringBuilder, com_google_android_gms_internal_zzack_zza___, parcel, i);
        }
    }

    private void zza(StringBuilder stringBuilder, Map<String, zza<?, ?>> map, Parcel parcel) {
        SparseArray zzX = zzX(map);
        stringBuilder.append('{');
        int zzaU = zzb.zzaU(parcel);
        Object obj = null;
        while (parcel.dataPosition() < zzaU) {
            int zzaT = zzb.zzaT(parcel);
            Entry entry = (Entry) zzX.get(zzb.zzcW(zzaT));
            if (entry != null) {
                if (obj != null) {
                    stringBuilder.append(",");
                }
                zza(stringBuilder, (String) entry.getKey(), (zza) entry.getValue(), parcel, zzaT);
                obj = 1;
            }
        }
        if (parcel.dataPosition() != zzaU) {
            throw new zzb.zza("Overread allowed size end=" + zzaU, parcel);
        }
        stringBuilder.append('}');
    }

    private void zzb(StringBuilder stringBuilder, zza<?, ?> com_google_android_gms_internal_zzack_zza___, Parcel parcel, int i) {
        if (com_google_android_gms_internal_zzack_zza___.zzxO()) {
            stringBuilder.append("[");
            switch (com_google_android_gms_internal_zzack_zza___.zzxN()) {
                case NewThreadWorker.PURGE_FREQUENCY:
                    com.google.android.gms.common.util.zzb.zza(stringBuilder, zzb.zzw(parcel, i));
                    break;
                case OnSubscribeConcatMap.BOUNDARY /*1*/:
                    com.google.android.gms.common.util.zzb.zza(stringBuilder, zzb.zzy(parcel, i));
                    break;
                case OnSubscribeConcatMap.END /*2*/:
                    com.google.android.gms.common.util.zzb.zza(stringBuilder, zzb.zzx(parcel, i));
                    break;
                case ConnectionResult.SERVICE_DISABLED /*3*/:
                    com.google.android.gms.common.util.zzb.zza(stringBuilder, zzb.zzz(parcel, i));
                    break;
                case ConnectionResult.SIGN_IN_REQUIRED /*4*/:
                    com.google.android.gms.common.util.zzb.zza(stringBuilder, zzb.zzA(parcel, i));
                    break;
                case ConnectionResult.INVALID_ACCOUNT /*5*/:
                    com.google.android.gms.common.util.zzb.zza(stringBuilder, zzb.zzB(parcel, i));
                    break;
                case ConnectionResult.RESOLUTION_REQUIRED /*6*/:
                    com.google.android.gms.common.util.zzb.zza(stringBuilder, zzb.zzv(parcel, i));
                    break;
                case ConnectionResult.NETWORK_ERROR /*7*/:
                    com.google.android.gms.common.util.zzb.zza(stringBuilder, zzb.zzC(parcel, i));
                    break;
                case ConnectionResult.INTERNAL_ERROR /*8*/:
                case ConnectionResult.SERVICE_INVALID /*9*/:
                case ConnectionResult.DEVELOPER_ERROR /*10*/:
                    throw new UnsupportedOperationException("List of type BASE64, BASE64_URL_SAFE, or STRING_MAP is not supported");
                case ConnectionResult.LICENSE_CHECK_FAILED /*11*/:
                    Parcel[] zzG = zzb.zzG(parcel, i);
                    int length = zzG.length;
                    for (int i2 = 0; i2 < length; i2++) {
                        if (i2 > 0) {
                            stringBuilder.append(",");
                        }
                        zzG[i2].setDataPosition(0);
                        zza(stringBuilder, com_google_android_gms_internal_zzack_zza___.zzxV(), zzG[i2]);
                    }
                    break;
                default:
                    throw new IllegalStateException("Unknown field type out.");
            }
            stringBuilder.append("]");
            return;
        }
        switch (com_google_android_gms_internal_zzack_zza___.zzxN()) {
            case NewThreadWorker.PURGE_FREQUENCY:
                stringBuilder.append(zzb.zzg(parcel, i));
            case OnSubscribeConcatMap.BOUNDARY /*1*/:
                stringBuilder.append(zzb.zzk(parcel, i));
            case OnSubscribeConcatMap.END /*2*/:
                stringBuilder.append(zzb.zzi(parcel, i));
            case ConnectionResult.SERVICE_DISABLED /*3*/:
                stringBuilder.append(zzb.zzl(parcel, i));
            case ConnectionResult.SIGN_IN_REQUIRED /*4*/:
                stringBuilder.append(zzb.zzn(parcel, i));
            case ConnectionResult.INVALID_ACCOUNT /*5*/:
                stringBuilder.append(zzb.zzp(parcel, i));
            case ConnectionResult.RESOLUTION_REQUIRED /*6*/:
                stringBuilder.append(zzb.zzc(parcel, i));
            case ConnectionResult.NETWORK_ERROR /*7*/:
                stringBuilder.append("\"").append(zzp.zzdC(zzb.zzq(parcel, i))).append("\"");
            case ConnectionResult.INTERNAL_ERROR /*8*/:
                stringBuilder.append("\"").append(zzc.zzq(zzb.zzt(parcel, i))).append("\"");
            case ConnectionResult.SERVICE_INVALID /*9*/:
                stringBuilder.append("\"").append(zzc.zzr(zzb.zzt(parcel, i)));
                stringBuilder.append("\"");
            case ConnectionResult.DEVELOPER_ERROR /*10*/:
                Bundle zzs = zzb.zzs(parcel, i);
                Set<String> keySet = zzs.keySet();
                keySet.size();
                stringBuilder.append("{");
                int i3 = 1;
                for (String str : keySet) {
                    if (i3 == 0) {
                        stringBuilder.append(",");
                    }
                    stringBuilder.append("\"").append(str).append("\"");
                    stringBuilder.append(":");
                    stringBuilder.append("\"").append(zzp.zzdC(zzs.getString(str))).append("\"");
                    i3 = 0;
                }
                stringBuilder.append("}");
            case ConnectionResult.LICENSE_CHECK_FAILED /*11*/:
                Parcel zzF = zzb.zzF(parcel, i);
                zzF.setDataPosition(0);
                zza(stringBuilder, com_google_android_gms_internal_zzack_zza___.zzxV(), zzF);
            default:
                throw new IllegalStateException("Unknown field type out");
        }
    }

    private void zzb(StringBuilder stringBuilder, zza<?, ?> com_google_android_gms_internal_zzack_zza___, Object obj) {
        if (com_google_android_gms_internal_zzack_zza___.zzxM()) {
            zzb(stringBuilder, (zza) com_google_android_gms_internal_zzack_zza___, (ArrayList) obj);
        } else {
            zza(stringBuilder, com_google_android_gms_internal_zzack_zza___.zzxL(), obj);
        }
    }

    private void zzb(StringBuilder stringBuilder, zza<?, ?> com_google_android_gms_internal_zzack_zza___, ArrayList<?> arrayList) {
        stringBuilder.append("[");
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            if (i != 0) {
                stringBuilder.append(",");
            }
            zza(stringBuilder, com_google_android_gms_internal_zzack_zza___.zzxL(), arrayList.get(i));
        }
        stringBuilder.append("]");
    }

    public static HashMap<String, String> zzr(Bundle bundle) {
        HashMap<String, String> hashMap = new HashMap();
        for (String str : bundle.keySet()) {
            hashMap.put(str, bundle.getString(str));
        }
        return hashMap;
    }

    public int getVersionCode() {
        return this.mVersionCode;
    }

    public String toString() {
        zzac.zzb(this.zzaFI, (Object) "Cannot convert to JSON on client side.");
        Parcel zzya = zzya();
        zzya.setDataPosition(0);
        StringBuilder stringBuilder = new StringBuilder(100);
        zza(stringBuilder, this.zzaFI.zzdA(this.mClassName), zzya);
        return stringBuilder.toString();
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzacs.zza(this, parcel, i);
    }

    public Object zzdw(String str) {
        throw new UnsupportedOperationException("Converting to JSON does not require this method.");
    }

    public boolean zzdx(String str) {
        throw new UnsupportedOperationException("Converting to JSON does not require this method.");
    }

    public Map<String, zza<?, ?>> zzxK() {
        return this.zzaFI == null ? null : this.zzaFI.zzdA(this.mClassName);
    }

    public Parcel zzya() {
        switch (this.zzaFR) {
            case NewThreadWorker.PURGE_FREQUENCY:
                this.zzaFS = com.google.android.gms.common.internal.safeparcel.zzc.zzaV(this.zzaFP);
                com.google.android.gms.common.internal.safeparcel.zzc.zzJ(this.zzaFP, this.zzaFS);
                this.zzaFR = 2;
                break;
            case OnSubscribeConcatMap.BOUNDARY /*1*/:
                com.google.android.gms.common.internal.safeparcel.zzc.zzJ(this.zzaFP, this.zzaFS);
                this.zzaFR = 2;
                break;
        }
        return this.zzaFP;
    }

    zzaco zzyb() {
        switch (this.zzaFQ) {
            case NewThreadWorker.PURGE_FREQUENCY:
                return null;
            case OnSubscribeConcatMap.BOUNDARY /*1*/:
                return this.zzaFI;
            case OnSubscribeConcatMap.END /*2*/:
                return this.zzaFI;
            default:
                throw new IllegalStateException("Invalid creation type: " + this.zzaFQ);
        }
    }
}
