package com.app.tuan88291.testapp;

import com.nightonke.boommenu.BoomButtons.TextInsideCircleButton.Builder;
import com.nightonke.boommenu.BoomButtons.TextOutsideCircleButton;

public class Data_boom {
    private static String[] dongtext;
    private static int imageResourceIndex;
    private static int[] imageResources;
    private static int indextext;

    static {
        imageResources = new int[]{C0336R.drawable.bowl, C0336R.drawable.mydish, C0336R.drawable.dishsave, C0336R.drawable.restaurant};
        dongtext = new String[]{"Th\u00eam m\u00f3n \u0103n", "M\u00f3n \u0103n c\u1ee7a t\u00f4i", "M\u00f3n \u0103n \u0111\u00e3 l\u01b0u", "Chuy\u00ean m\u1ee5c"};
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

    static TextOutsideCircleButton.Builder getTextOutsideCircleButtonBuilderWithDifferentPieceColor() {
        return new TextOutsideCircleButton.Builder().normalImageRes(getImageResource()).normalTextRes(C0336R.string.text_inside_circle_button_text_normal).pieceColor(-1);
    }
}
