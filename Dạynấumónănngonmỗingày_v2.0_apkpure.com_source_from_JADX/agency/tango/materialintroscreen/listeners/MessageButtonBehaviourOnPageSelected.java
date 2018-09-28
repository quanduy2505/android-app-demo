package agency.tango.materialintroscreen.listeners;

import agency.tango.materialintroscreen.C0005R;
import agency.tango.materialintroscreen.MessageButtonBehaviour;
import agency.tango.materialintroscreen.SlideFragment;
import agency.tango.materialintroscreen.adapter.SlidesAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;

public class MessageButtonBehaviourOnPageSelected implements IPageSelectedListener {
    private SlidesAdapter adapter;
    private Button messageButton;
    private SparseArray<MessageButtonBehaviour> messageButtonBehaviours;

    /* renamed from: agency.tango.materialintroscreen.listeners.MessageButtonBehaviourOnPageSelected.1 */
    class C00061 implements OnClickListener {
        final /* synthetic */ SlideFragment val$slideFragment;

        C00061(SlideFragment slideFragment) {
            this.val$slideFragment = slideFragment;
        }

        public void onClick(View view) {
            this.val$slideFragment.askForPermissions();
        }
    }

    public MessageButtonBehaviourOnPageSelected(Button messageButton, SlidesAdapter adapter, SparseArray<MessageButtonBehaviour> messageButtonBehaviours) {
        this.messageButton = messageButton;
        this.adapter = adapter;
        this.messageButtonBehaviours = messageButtonBehaviours;
    }

    public void pageSelected(int position) {
        SlideFragment slideFragment = this.adapter.getItem(position);
        if (slideFragment.hasAnyPermissionsToGrant()) {
            showMessageButton(slideFragment);
            this.messageButton.setText(slideFragment.getActivity().getString(C0005R.string.grant_permissions));
            this.messageButton.setOnClickListener(new C00061(slideFragment));
        } else if (checkIfMessageButtonHasBehaviour(position)) {
            showMessageButton(slideFragment);
            this.messageButton.setText(((MessageButtonBehaviour) this.messageButtonBehaviours.get(position)).getMessageButtonText());
            this.messageButton.setOnClickListener(((MessageButtonBehaviour) this.messageButtonBehaviours.get(position)).getClickListener());
        } else if (this.messageButton.getVisibility() != 4) {
            this.messageButton.startAnimation(AnimationUtils.loadAnimation(slideFragment.getContext(), C0005R.anim.fade_out));
            this.messageButton.setVisibility(4);
        }
    }

    private boolean checkIfMessageButtonHasBehaviour(int position) {
        return this.messageButtonBehaviours.get(position) != null && SlideFragment.isNotNullOrEmpty(((MessageButtonBehaviour) this.messageButtonBehaviours.get(position)).getMessageButtonText());
    }

    private void showMessageButton(SlideFragment fragment) {
        if (this.messageButton.getVisibility() != 0) {
            this.messageButton.setVisibility(0);
            if (fragment.getActivity() != null) {
                this.messageButton.startAnimation(AnimationUtils.loadAnimation(fragment.getActivity(), C0005R.anim.fade_in));
            }
        }
    }
}
