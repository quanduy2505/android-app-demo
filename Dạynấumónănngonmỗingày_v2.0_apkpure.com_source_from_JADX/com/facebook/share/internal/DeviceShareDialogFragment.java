package com.facebook.share.internal;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.facebook.AccessToken;
import com.facebook.C0378R;
import com.facebook.FacebookRequestError;
import com.facebook.GraphRequest;
import com.facebook.GraphRequest.Callback;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.devicerequests.internal.DeviceRequestsHelper;
import com.facebook.internal.NativeProtocol;
import com.facebook.internal.ServerProtocol;
import com.facebook.internal.Validate;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.ShareOpenGraphContent;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.json.JSONException;
import org.json.JSONObject;
import rx.android.BuildConfig;

public class DeviceShareDialogFragment extends DialogFragment {
    private static final String DEVICE_SHARE_ENDPOINT = "device/share";
    private static final String REQUEST_STATE_KEY = "request_state";
    public static final String TAG = "DeviceShareDialogFragment";
    private static ScheduledThreadPoolExecutor backgroundExecutor;
    private volatile ScheduledFuture codeExpiredFuture;
    private TextView confirmationCode;
    private volatile RequestState currentRequestState;
    private Dialog dialog;
    private ProgressBar progressBar;
    private ShareContent shareContent;

    /* renamed from: com.facebook.share.internal.DeviceShareDialogFragment.1 */
    class C04511 implements OnClickListener {
        C04511() {
        }

        public void onClick(View v) {
            DeviceShareDialogFragment.this.dialog.dismiss();
        }
    }

    /* renamed from: com.facebook.share.internal.DeviceShareDialogFragment.3 */
    class C04523 implements Runnable {
        C04523() {
        }

        public void run() {
            DeviceShareDialogFragment.this.dialog.dismiss();
        }
    }

    private static class RequestState implements Parcelable {
        public static final Creator<RequestState> CREATOR;
        private long expiresIn;
        private String userCode;

        /* renamed from: com.facebook.share.internal.DeviceShareDialogFragment.RequestState.1 */
        static class C04531 implements Creator<RequestState> {
            C04531() {
            }

            public RequestState createFromParcel(Parcel in) {
                return new RequestState(in);
            }

            public RequestState[] newArray(int size) {
                return new RequestState[size];
            }
        }

        RequestState() {
        }

        public String getUserCode() {
            return this.userCode;
        }

        public void setUserCode(String userCode) {
            this.userCode = userCode;
        }

        public long getExpiresIn() {
            return this.expiresIn;
        }

        public void setExpiresIn(long expiresIn) {
            this.expiresIn = expiresIn;
        }

        protected RequestState(Parcel in) {
            this.userCode = in.readString();
            this.expiresIn = in.readLong();
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.userCode);
            dest.writeLong(this.expiresIn);
        }

        static {
            CREATOR = new C04531();
        }
    }

    /* renamed from: com.facebook.share.internal.DeviceShareDialogFragment.2 */
    class C10242 implements Callback {
        C10242() {
        }

        public void onCompleted(GraphResponse response) {
            FacebookRequestError error = response.getError();
            if (error != null) {
                DeviceShareDialogFragment.this.finishActivityWithError(error);
                return;
            }
            JSONObject jsonObject = response.getJSONObject();
            RequestState requestState = new RequestState();
            try {
                requestState.setUserCode(jsonObject.getString("user_code"));
                requestState.setExpiresIn(jsonObject.getLong(AccessToken.EXPIRES_IN_KEY));
                DeviceShareDialogFragment.this.setCurrentRequestState(requestState);
            } catch (JSONException e) {
                DeviceShareDialogFragment.this.finishActivityWithError(new FacebookRequestError(0, BuildConfig.VERSION_NAME, "Malformed server response"));
            }
        }
    }

    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (savedInstanceState != null) {
            RequestState requestState = (RequestState) savedInstanceState.getParcelable(REQUEST_STATE_KEY);
            if (requestState != null) {
                setCurrentRequestState(requestState);
            }
        }
        return view;
    }

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        this.dialog = new Dialog(getActivity(), C0378R.style.com_facebook_auth_dialog);
        View view = getActivity().getLayoutInflater().inflate(C0378R.layout.com_facebook_device_auth_dialog_fragment, null);
        this.progressBar = (ProgressBar) view.findViewById(C0378R.id.progress_bar);
        this.confirmationCode = (TextView) view.findViewById(C0378R.id.confirmation_code);
        ((Button) view.findViewById(C0378R.id.cancel_button)).setOnClickListener(new C04511());
        ((TextView) view.findViewById(C0378R.id.com_facebook_device_auth_instructions)).setText(Html.fromHtml(getString(C0378R.string.com_facebook_device_auth_instructions)));
        this.dialog.setContentView(view);
        startShare();
        return this.dialog;
    }

    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (this.codeExpiredFuture != null) {
            this.codeExpiredFuture.cancel(true);
        }
        finishActivity(-1, new Intent());
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (this.currentRequestState != null) {
            outState.putParcelable(REQUEST_STATE_KEY, this.currentRequestState);
        }
    }

    private void finishActivity(int resultCode, Intent data) {
        DeviceRequestsHelper.cleanUpAdvertisementService(this.currentRequestState.getUserCode());
        if (isAdded()) {
            Activity activity = getActivity();
            activity.setResult(resultCode, data);
            activity.finish();
        }
    }

    private void detach() {
        if (isAdded()) {
            getFragmentManager().beginTransaction().remove(this).commit();
        }
    }

    public void setShareContent(ShareContent shareContent) {
        this.shareContent = shareContent;
    }

    private Bundle getGraphParametersForShareContent() {
        ShareContent content = this.shareContent;
        if (content == null) {
            return null;
        }
        if (content instanceof ShareLinkContent) {
            return WebDialogParameters.create((ShareLinkContent) content);
        }
        if (content instanceof ShareOpenGraphContent) {
            return WebDialogParameters.create((ShareOpenGraphContent) content);
        }
        return null;
    }

    private void startShare() {
        Bundle parameters = getGraphParametersForShareContent();
        if (parameters == null || parameters.size() == 0) {
            finishActivityWithError(new FacebookRequestError(0, BuildConfig.VERSION_NAME, "Failed to get share content"));
        }
        parameters.putString(ServerProtocol.DIALOG_PARAM_ACCESS_TOKEN, Validate.hasAppID() + "|" + Validate.hasClientToken());
        parameters.putString(DeviceRequestsHelper.DEVICE_INFO_PARAM, DeviceRequestsHelper.getDeviceInfo());
        new GraphRequest(null, DEVICE_SHARE_ENDPOINT, parameters, HttpMethod.POST, new C10242()).executeAsync();
    }

    private void finishActivityWithError(FacebookRequestError error) {
        detach();
        Intent intent = new Intent();
        intent.putExtra(NativeProtocol.BRIDGE_ARG_ERROR_BUNDLE, error);
        finishActivity(-1, intent);
    }

    private static synchronized ScheduledThreadPoolExecutor getBackgroundExecutor() {
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
        synchronized (DeviceShareDialogFragment.class) {
            if (backgroundExecutor == null) {
                backgroundExecutor = new ScheduledThreadPoolExecutor(1);
            }
            scheduledThreadPoolExecutor = backgroundExecutor;
        }
        return scheduledThreadPoolExecutor;
    }

    private void setCurrentRequestState(RequestState currentRequestState) {
        this.currentRequestState = currentRequestState;
        this.confirmationCode.setText(currentRequestState.getUserCode());
        this.confirmationCode.setVisibility(0);
        this.progressBar.setVisibility(8);
        this.codeExpiredFuture = getBackgroundExecutor().schedule(new C04523(), currentRequestState.getExpiresIn(), TimeUnit.SECONDS);
    }
}
