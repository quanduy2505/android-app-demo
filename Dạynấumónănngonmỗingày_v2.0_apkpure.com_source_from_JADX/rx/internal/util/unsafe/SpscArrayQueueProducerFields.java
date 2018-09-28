package rx.internal.util.unsafe;

/* compiled from: SpscArrayQueue */
abstract class SpscArrayQueueProducerFields<E> extends SpscArrayQueueL1Pad<E> {
    protected static final long P_INDEX_OFFSET;
    protected long producerIndex;
    protected long producerLookAhead;

    static {
        P_INDEX_OFFSET = UnsafeAccess.addressOf(SpscArrayQueueProducerFields.class, "producerIndex");
    }

    public SpscArrayQueueProducerFields(int capacity) {
        super(capacity);
    }
}
