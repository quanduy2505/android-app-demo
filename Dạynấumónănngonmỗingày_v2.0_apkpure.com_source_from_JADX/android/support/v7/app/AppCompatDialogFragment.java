package android.support.v7.app;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.v4.app.DialogFragment;
import com.google.android.gms.common.ConnectionResult;
import rx.internal.operators.OnSubscribeConcatMap;

public class AppCompatDialogFragment extends DialogFragment {
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AppCompatDialog(getContext(), getTheme());
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    public void setupDialog(Dialog dialog, int style) {
        if (dialog instanceof AppCompatDialog) {
            AppCompatDialog acd = (AppCompatDialog) dialog;
            switch (style) {
                case OnSubscribeConcatMap.BOUNDARY /*1*/:
                case OnSubscribeConcatMap.END /*2*/:
                    break;
                case ConnectionResult.SERVICE_DISABLED /*3*/:
                    dialog.getWindow().addFlags(24);
                    break;
                default:
                    return;
            }
            acd.supportRequestWindowFeature(1);
            return;
        }
        super.setupDialog(dialog, style);
    }
}
