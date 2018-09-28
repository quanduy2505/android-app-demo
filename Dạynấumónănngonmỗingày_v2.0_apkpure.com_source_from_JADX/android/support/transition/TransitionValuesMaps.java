package android.support.transition;

import android.annotation.TargetApi;
import android.support.annotation.RequiresApi;
import android.support.v4.util.ArrayMap;
import android.support.v4.util.LongSparseArray;
import android.util.SparseArray;
import android.view.View;

@TargetApi(14)
@RequiresApi(14)
class TransitionValuesMaps {
    public SparseArray<TransitionValues> idValues;
    public LongSparseArray<TransitionValues> itemIdValues;
    public ArrayMap<View, TransitionValues> viewValues;

    TransitionValuesMaps() {
        this.viewValues = new ArrayMap();
        this.idValues = new SparseArray();
        this.itemIdValues = new LongSparseArray();
    }
}
