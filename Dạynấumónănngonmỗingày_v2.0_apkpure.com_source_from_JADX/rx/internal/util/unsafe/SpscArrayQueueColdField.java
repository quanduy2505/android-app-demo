package rx.internal.util.unsafe;

import android.support.v7.widget.RecyclerView.ItemAnimator;

/* compiled from: SpscArrayQueue */
abstract class SpscArrayQueueColdField<E> extends ConcurrentCircularArrayQueue<E> {
    private static final Integer MAX_LOOK_AHEAD_STEP;
    protected final int lookAheadStep;

    static {
        MAX_LOOK_AHEAD_STEP = Integer.getInteger("jctools.spsc.max.lookahead.step", ItemAnimator.FLAG_APPEARED_IN_PRE_LAYOUT);
    }

    public SpscArrayQueueColdField(int capacity) {
        super(capacity);
        this.lookAheadStep = Math.min(capacity / 4, MAX_LOOK_AHEAD_STEP.intValue());
    }
}
