package com.google.android.gms.location.places;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.facebook.internal.AnalyticsEvents;
import com.facebook.share.internal.ShareConstants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.common.internal.zzac;
import rx.internal.operators.OnSubscribeConcatMap;
import rx.internal.schedulers.NewThreadWorker;

public class PlaceReport extends zza implements ReflectedParcelable {
    public static final Creator<PlaceReport> CREATOR;
    private final String mTag;
    final int mVersionCode;
    private final String zzabN;
    private final String zzblg;

    static {
        CREATOR = new zzl();
    }

    PlaceReport(int i, String str, String str2, String str3) {
        this.mVersionCode = i;
        this.zzblg = str;
        this.mTag = str2;
        this.zzabN = str3;
    }

    public static PlaceReport create(String str, String str2) {
        return zzj(str, str2, AnalyticsEvents.PARAMETER_SHARE_OUTCOME_UNKNOWN);
    }

    private static boolean zzeY(String str) {
        boolean z = true;
        switch (str.hashCode()) {
            case -1436706272:
                if (str.equals("inferredGeofencing")) {
                    z = true;
                    break;
                }
                break;
            case -1194968642:
                if (str.equals("userReported")) {
                    z = true;
                    break;
                }
                break;
            case -284840886:
                if (str.equals(AnalyticsEvents.PARAMETER_SHARE_OUTCOME_UNKNOWN)) {
                    z = false;
                    break;
                }
                break;
            case -262743844:
                if (str.equals("inferredReverseGeocoding")) {
                    z = true;
                    break;
                }
                break;
            case 1164924125:
                if (str.equals("inferredSnappedToRoad")) {
                    z = true;
                    break;
                }
                break;
            case 1287171955:
                if (str.equals("inferredRadioSignals")) {
                    z = true;
                    break;
                }
                break;
        }
        switch (z) {
            case NewThreadWorker.PURGE_FREQUENCY:
            case OnSubscribeConcatMap.BOUNDARY /*1*/:
            case OnSubscribeConcatMap.END /*2*/:
            case ConnectionResult.SERVICE_DISABLED /*3*/:
            case ConnectionResult.SIGN_IN_REQUIRED /*4*/:
            case ConnectionResult.INVALID_ACCOUNT /*5*/:
                return true;
            default:
                return false;
        }
    }

    public static PlaceReport zzj(String str, String str2, String str3) {
        zzac.zzw(str);
        zzac.zzdv(str2);
        zzac.zzdv(str3);
        zzac.zzb(zzeY(str3), (Object) "Invalid source");
        return new PlaceReport(1, str, str2, str3);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof PlaceReport)) {
            return false;
        }
        PlaceReport placeReport = (PlaceReport) obj;
        return zzaa.equal(this.zzblg, placeReport.zzblg) && zzaa.equal(this.mTag, placeReport.mTag) && zzaa.equal(this.zzabN, placeReport.zzabN);
    }

    public String getPlaceId() {
        return this.zzblg;
    }

    public String getSource() {
        return this.zzabN;
    }

    public String getTag() {
        return this.mTag;
    }

    public int hashCode() {
        return zzaa.hashCode(this.zzblg, this.mTag, this.zzabN);
    }

    public String toString() {
        zzaa.zza zzv = zzaa.zzv(this);
        zzv.zzg("placeId", this.zzblg);
        zzv.zzg("tag", this.mTag);
        if (!AnalyticsEvents.PARAMETER_SHARE_OUTCOME_UNKNOWN.equals(this.zzabN)) {
            zzv.zzg(ShareConstants.FEED_SOURCE_PARAM, this.zzabN);
        }
        return zzv.toString();
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzl.zza(this, parcel, i);
    }
}
