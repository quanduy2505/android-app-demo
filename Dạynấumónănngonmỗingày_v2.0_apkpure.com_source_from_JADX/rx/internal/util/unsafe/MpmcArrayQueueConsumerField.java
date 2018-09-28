package rx.internal.util.unsafe;

/* compiled from: MpmcArrayQueue */
abstract class MpmcArrayQueueConsumerField<E> extends MpmcArrayQueueL2Pad<E> {
    private static final long C_INDEX_OFFSET;
    private volatile long consumerIndex;

    static {
        C_INDEX_OFFSET = UnsafeAccess.addressOf(MpmcArrayQueueConsumerField.class, "consumerIndex");
    }

    public MpmcArrayQueueConsumerField(int capacity) {
        super(capacity);
    }

    protected final long lvConsumerIndex() {
        return this.consumerIndex;
    }

    protected final boolean casConsumerIndex(long expect, long newValue) {
        return UnsafeAccess.UNSAFE.compareAndSwapLong(this, C_INDEX_OFFSET, expect, newValue);
    }
}
