package agency.tango.materialintroscreen;

import agency.tango.materialintroscreen.parallax.ParallaxFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SlideFragment extends ParallaxFragment {
    private static final String BACKGROUND_COLOR = "background_color";
    private static final String BUTTONS_COLOR = "buttons_color";
    private static final String DESCRIPTION = "description";
    private static final String IMAGE = "image";
    private static final String NEEDED_PERMISSIONS = "needed_permission";
    private static final int PERMISSIONS_REQUEST_CODE = 15621;
    private static final String POSSIBLE_PERMISSIONS = "possible_permission";
    private static final String TITLE = "title";
    private int backgroundColor;
    private int buttonsColor;
    private String description;
    private TextView descriptionTextView;
    private int image;
    private ImageView imageView;
    private String[] neededPermissions;
    private String[] possiblePermissions;
    private String title;
    private TextView titleTextView;

    public static SlideFragment createInstance(SlideFragmentBuilder builder) {
        SlideFragment slideFragment = new SlideFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(BACKGROUND_COLOR, builder.backgroundColor);
        bundle.putInt(BUTTONS_COLOR, builder.buttonsColor);
        bundle.putInt(IMAGE, builder.image);
        bundle.putString(TITLE, builder.title);
        bundle.putString(DESCRIPTION, builder.description);
        bundle.putStringArray(NEEDED_PERMISSIONS, builder.neededPermissions);
        bundle.putStringArray(POSSIBLE_PERMISSIONS, builder.possiblePermissions);
        slideFragment.setArguments(bundle);
        return slideFragment;
    }

    public static boolean isNotNullOrEmpty(String string) {
        return (string == null || string.isEmpty()) ? false : true;
    }

    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(C0005R.layout.fragment_slide, container, false);
        this.titleTextView = (TextView) view.findViewById(C0005R.id.txt_title_slide);
        this.descriptionTextView = (TextView) view.findViewById(C0005R.id.txt_description_slide);
        this.imageView = (ImageView) view.findViewById(C0005R.id.image_slide);
        initializeView();
        return view;
    }

    public void initializeView() {
        Bundle bundle = getArguments();
        this.backgroundColor = bundle.getInt(BACKGROUND_COLOR);
        this.buttonsColor = bundle.getInt(BUTTONS_COLOR);
        this.image = bundle.getInt(IMAGE, 0);
        this.title = bundle.getString(TITLE);
        this.description = bundle.getString(DESCRIPTION);
        this.neededPermissions = bundle.getStringArray(NEEDED_PERMISSIONS);
        this.possiblePermissions = bundle.getStringArray(POSSIBLE_PERMISSIONS);
        updateViewWithValues();
    }

    public int backgroundColor() {
        return this.backgroundColor;
    }

    public int buttonsColor() {
        return this.buttonsColor;
    }

    public boolean hasAnyPermissionsToGrant() {
        boolean hasPermissionToGrant = hasPermissionsToGrant(this.neededPermissions);
        if (hasPermissionToGrant) {
            return hasPermissionToGrant;
        }
        return hasPermissionsToGrant(this.possiblePermissions);
    }

    public boolean hasNeededPermissionsToGrant() {
        return hasPermissionsToGrant(this.neededPermissions);
    }

    public boolean canMoveFurther() {
        return true;
    }

    public String cantMoveFurtherErrorMessage() {
        return getString(C0005R.string.impassable_slide);
    }

    private void updateViewWithValues() {
        this.titleTextView.setText(this.title);
        this.descriptionTextView.setText(this.description);
        if (this.image != 0) {
            this.imageView.setImageDrawable(ContextCompat.getDrawable(getActivity(), this.image));
            this.imageView.setVisibility(0);
        }
    }

    public void askForPermissions() {
        int i = 0;
        ArrayList<String> notGrantedPermissions = new ArrayList();
        if (this.neededPermissions != null) {
            for (String permission : this.neededPermissions) {
                String permission2;
                if (isNotNullOrEmpty(permission2) && ContextCompat.checkSelfPermission(getContext(), permission2) != 0) {
                    notGrantedPermissions.add(permission2);
                }
            }
        }
        if (this.possiblePermissions != null) {
            String[] strArr = this.possiblePermissions;
            int length = strArr.length;
            while (i < length) {
                permission2 = strArr[i];
                if (isNotNullOrEmpty(permission2) && ContextCompat.checkSelfPermission(getContext(), permission2) != 0) {
                    notGrantedPermissions.add(permission2);
                }
                i++;
            }
        }
        ActivityCompat.requestPermissions(getActivity(), removeEmptyAndNullStrings(notGrantedPermissions), PERMISSIONS_REQUEST_CODE);
    }

    private boolean hasPermissionsToGrant(String[] permissions) {
        if (permissions == null) {
            return false;
        }
        for (String permission : permissions) {
            if (isNotNullOrEmpty(permission) && ContextCompat.checkSelfPermission(getContext(), permission) != 0) {
                return true;
            }
        }
        return false;
    }

    private String[] removeEmptyAndNullStrings(ArrayList<String> permissions) {
        List<String> list = new ArrayList(permissions);
        list.removeAll(Collections.singleton(null));
        return (String[]) list.toArray(new String[list.size()]);
    }
}
