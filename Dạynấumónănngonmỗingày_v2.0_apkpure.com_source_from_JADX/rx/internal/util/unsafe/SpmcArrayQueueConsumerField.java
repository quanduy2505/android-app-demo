package rx.internal.util.unsafe;

/* compiled from: SpmcArrayQueue */
abstract class SpmcArrayQueueConsumerField<E> extends SpmcArrayQueueL2Pad<E> {
    protected static final long C_INDEX_OFFSET;
    private volatile long consumerIndex;

    static {
        C_INDEX_OFFSET = UnsafeAccess.addressOf(SpmcArrayQueueConsumerField.class, "consumerIndex");
    }

    public SpmcArrayQueueConsumerField(int capacity) {
        super(capacity);
    }

    protected final long lvConsumerIndex() {
        return this.consumerIndex;
    }

    protected final boolean casHead(long expect, long newValue) {
        return UnsafeAccess.UNSAFE.compareAndSwapLong(this, C_INDEX_OFFSET, expect, newValue);
    }
}
