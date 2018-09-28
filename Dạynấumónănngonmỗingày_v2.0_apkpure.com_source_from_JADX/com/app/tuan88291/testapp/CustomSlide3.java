package com.app.tuan88291.testapp;

import agency.tango.materialintroscreen.SlideFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import me.wangyuwei.loadingview.C0801R;

public class CustomSlide3 extends SlideFragment {
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(C0336R.layout.fragment_custom_slide3, container, false);
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
