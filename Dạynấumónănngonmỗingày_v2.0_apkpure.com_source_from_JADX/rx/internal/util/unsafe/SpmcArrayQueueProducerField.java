package rx.internal.util.unsafe;

/* compiled from: SpmcArrayQueue */
abstract class SpmcArrayQueueProducerField<E> extends SpmcArrayQueueL1Pad<E> {
    protected static final long P_INDEX_OFFSET;
    private volatile long producerIndex;

    static {
        P_INDEX_OFFSET = UnsafeAccess.addressOf(SpmcArrayQueueProducerField.class, "producerIndex");
    }

    protected final long lvProducerIndex() {
        return this.producerIndex;
    }

    protected final void soTail(long v) {
        UnsafeAccess.UNSAFE.putOrderedLong(this, P_INDEX_OFFSET, v);
    }

    public SpmcArrayQueueProducerField(int capacity) {
        super(capacity);
    }
}
