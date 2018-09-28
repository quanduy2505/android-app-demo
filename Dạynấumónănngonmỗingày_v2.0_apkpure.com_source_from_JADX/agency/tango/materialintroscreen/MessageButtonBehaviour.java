package agency.tango.materialintroscreen;

import android.view.View.OnClickListener;

public class MessageButtonBehaviour {
    private OnClickListener clickListener;
    private String messageButtonText;

    public MessageButtonBehaviour(OnClickListener clickListener, String messageButtonText) {
        this.clickListener = clickListener;
        this.messageButtonText = messageButtonText;
    }

    public MessageButtonBehaviour(String messageButtonText) {
        this.messageButtonText = messageButtonText;
    }

    public OnClickListener getClickListener() {
        return this.clickListener;
    }

    public String getMessageButtonText() {
        return this.messageButtonText;
    }
}
