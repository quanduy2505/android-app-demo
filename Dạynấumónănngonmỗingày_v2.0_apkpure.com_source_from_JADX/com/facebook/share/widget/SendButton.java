package com.facebook.share.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.facebook.C0378R;
import com.facebook.internal.AnalyticsEvents;
import com.facebook.internal.CallbackManagerImpl.RequestCodeOffset;
import com.facebook.internal.FacebookDialogBase;
import com.facebook.share.Sharer.Result;
import com.facebook.share.model.ShareContent;

public final class SendButton extends ShareButtonBase {
    public SendButton(Context context) {
        super(context, null, 0, AnalyticsEvents.EVENT_SEND_BUTTON_CREATE, AnalyticsEvents.EVENT_SEND_BUTTON_DID_TAP);
    }

    public SendButton(Context context, AttributeSet attrs) {
        super(context, attrs, 0, AnalyticsEvents.EVENT_SEND_BUTTON_CREATE, AnalyticsEvents.EVENT_SEND_BUTTON_DID_TAP);
    }

    public SendButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr, AnalyticsEvents.EVENT_SEND_BUTTON_CREATE, AnalyticsEvents.EVENT_SEND_BUTTON_DID_TAP);
    }

    protected int getDefaultStyleResource() {
        return C0378R.style.com_facebook_button_send;
    }

    protected int getDefaultRequestCode() {
        return RequestCodeOffset.Message.toRequestCode();
    }

    protected FacebookDialogBase<ShareContent, Result> getDialog() {
        if (getFragment() != null) {
            return new MessageDialog(getFragment(), getRequestCode());
        }
        if (getNativeFragment() != null) {
            return new MessageDialog(getNativeFragment(), getRequestCode());
        }
        return new MessageDialog(getActivity(), getRequestCode());
    }
}
