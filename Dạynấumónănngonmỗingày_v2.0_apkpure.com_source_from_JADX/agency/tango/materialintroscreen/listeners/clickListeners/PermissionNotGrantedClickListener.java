package agency.tango.materialintroscreen.listeners.clickListeners;

import agency.tango.materialintroscreen.MaterialIntroActivity;
import agency.tango.materialintroscreen.animations.ViewTranslationWrapper;
import android.view.View;
import android.view.View.OnClickListener;

public class PermissionNotGrantedClickListener implements OnClickListener {
    private final MaterialIntroActivity activity;
    private final ViewTranslationWrapper translationWrapper;

    public PermissionNotGrantedClickListener(MaterialIntroActivity activity, ViewTranslationWrapper translationWrapper) {
        this.activity = activity;
        this.translationWrapper = translationWrapper;
    }

    public void onClick(View v) {
        this.translationWrapper.error();
        this.activity.showPermissionsNotGrantedError();
    }
}
