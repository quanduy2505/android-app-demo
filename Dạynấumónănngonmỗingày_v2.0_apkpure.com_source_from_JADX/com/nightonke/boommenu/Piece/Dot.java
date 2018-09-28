package com.nightonke.boommenu.Piece;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import com.nightonke.boommenu.C0763R;
import com.nightonke.boommenu.Util;

final class Dot extends BoomPiece {
    public Dot(Context context) {
        super(context);
    }

    public void init(int color) {
        Drawable backgroundDrawable = Util.getDrawable(this, C0763R.drawable.piece_dot, null);
        ((GradientDrawable) backgroundDrawable).setColor(color);
        Util.setDrawable(this, backgroundDrawable);
    }

    public void setColor(int color) {
        ((GradientDrawable) getBackground()).setColor(color);
    }
}
