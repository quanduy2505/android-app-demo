package android.support.v4.net;

import android.annotation.TargetApi;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.RequiresApi;
import com.google.android.gms.common.ConnectionResult;
import rx.internal.operators.OnSubscribeConcatMap;
import rx.internal.schedulers.NewThreadWorker;

@TargetApi(13)
@RequiresApi(13)
class ConnectivityManagerCompatHoneycombMR2 {
    ConnectivityManagerCompatHoneycombMR2() {
    }

    public static boolean isActiveNetworkMetered(ConnectivityManager cm) {
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) {
            return true;
        }
        switch (info.getType()) {
            case NewThreadWorker.PURGE_FREQUENCY /*0*/:
            case OnSubscribeConcatMap.END /*2*/:
            case ConnectionResult.SERVICE_DISABLED /*3*/:
            case ConnectionResult.SIGN_IN_REQUIRED /*4*/:
            case ConnectionResult.INVALID_ACCOUNT /*5*/:
            case ConnectionResult.RESOLUTION_REQUIRED /*6*/:
                return true;
            case OnSubscribeConcatMap.BOUNDARY /*1*/:
            case ConnectionResult.NETWORK_ERROR /*7*/:
            case ConnectionResult.SERVICE_INVALID /*9*/:
                return false;
            default:
                return true;
        }
    }
}
