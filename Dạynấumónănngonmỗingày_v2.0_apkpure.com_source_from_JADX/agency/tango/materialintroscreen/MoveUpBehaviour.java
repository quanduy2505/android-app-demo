package agency.tango.materialintroscreen;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.CoordinatorLayout.Behavior;
import android.support.design.widget.Snackbar.SnackbarLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class MoveUpBehaviour extends Behavior<LinearLayout> {
    public MoveUpBehaviour(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean layoutDependsOn(CoordinatorLayout parent, LinearLayout child, View dependency) {
        return dependency instanceof SnackbarLayout;
    }

    public boolean onDependentViewChanged(CoordinatorLayout parent, LinearLayout child, View dependency) {
        child.setTranslationY(Math.min(0.0f, dependency.getTranslationY() - ((float) dependency.getHeight())));
        return true;
    }
}
