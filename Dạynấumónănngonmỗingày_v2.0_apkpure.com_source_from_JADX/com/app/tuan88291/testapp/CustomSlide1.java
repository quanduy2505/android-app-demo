package com.app.tuan88291.testapp;

import agency.tango.materialintroscreen.SlideFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.TaskStackBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import me.wangyuwei.loadingview.C0801R;

public class CustomSlide1 extends SlideFragment {
    private Button skip;

    /* renamed from: com.app.tuan88291.testapp.CustomSlide1.1 */
    class C03091 implements OnClickListener {
        C03091() {
        }

        public void onClick(View v) {
            TaskStackBuilder.create(CustomSlide1.this.getActivity()).addNextIntent(new Intent(CustomSlide1.this.getActivity(), ContentView.class)).startActivities();
        }
    }

    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(C0336R.layout.fragment_custom_slide1, container, false);
        this.skip = (Button) view.findViewById(C0336R.id.skip);
        this.skip.setOnClickListener(new C03091());
        return view;
    }

    public int backgroundColor() {
        return C0336R.color.custom_slide_background;
    }

    public int buttonsColor() {
        return C0336R.color.colorAccent;
    }

    public String cantMoveFurtherErrorMessage() {
        return getString(C0801R.string.app_name);
    }
}
