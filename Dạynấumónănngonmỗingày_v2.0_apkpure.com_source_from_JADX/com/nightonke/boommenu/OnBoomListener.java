package com.nightonke.boommenu;

import com.nightonke.boommenu.BoomButtons.BoomButton;

public interface OnBoomListener {
    void onBackgroundClick();

    void onBoomDidHide();

    void onBoomDidShow();

    void onBoomWillHide();

    void onBoomWillShow();

    void onClicked(int i, BoomButton boomButton);
}
