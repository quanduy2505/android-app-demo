package com.app.tuan88291.testapp;

import com.nightonke.boommenu.BoomButtons.TextInsideCircleButton.Builder;

public class Data_boom_detail {
    private static String[] dongtext;
    private static int imageResourceIndex;
    private static int[] imageResources;
    private static int indextext;

    static {
        imageResources = new int[]{C0336R.drawable.luu, C0336R.drawable.comment, C0336R.drawable.traitim};
        dongtext = new String[]{"L\u01b0u l\u1ea1i", "B\u00ecnh lu\u1eadn", "Y\u00eau th\u00edch m\u00f3n n\u00e0y"};
        imageResourceIndex = 0;
        indextext = 0;
    }

    static String getdongtext() {
        if (indextext >= dongtext.length) {
            indextext = 0;
        }
        String[] strArr = dongtext;
        int i = indextext;
        indextext = i + 1;
        return strArr[i];
    }

    static int getImageResource() {
        if (imageResourceIndex >= imageResources.length) {
            imageResourceIndex = 0;
        }
        int[] iArr = imageResources;
        int i = imageResourceIndex;
        imageResourceIndex = i + 1;
        return iArr[i];
    }

    static Builder getTextInsideCircleButtonBuilderWithDifferentPieceColor() {
        return new Builder().normalImageRes(getImageResource()).normalTextRes(C0336R.string.text_inside_circle_button_text_normal).pieceColor(-1);
    }
}
