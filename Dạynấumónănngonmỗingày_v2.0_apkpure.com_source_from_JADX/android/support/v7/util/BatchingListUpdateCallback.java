package android.support.v7.util;

public class BatchingListUpdateCallback implements ListUpdateCallback {
    private static final int TYPE_ADD = 1;
    private static final int TYPE_CHANGE = 3;
    private static final int TYPE_NONE = 0;
    private static final int TYPE_REMOVE = 2;
    int mLastEventCount;
    Object mLastEventPayload;
    int mLastEventPosition;
    int mLastEventType;
    final ListUpdateCallback mWrapped;

    public BatchingListUpdateCallback(ListUpdateCallback callback) {
        this.mLastEventType = TYPE_NONE;
        this.mLastEventPosition = -1;
        this.mLastEventCount = -1;
        this.mLastEventPayload = null;
        this.mWrapped = callback;
    }

    public void dispatchLastEvent() {
        if (this.mLastEventType != 0) {
            switch (this.mLastEventType) {
                case TYPE_ADD /*1*/:
                    this.mWrapped.onInserted(this.mLastEventPosition, this.mLastEventCount);
                    break;
                case TYPE_REMOVE /*2*/:
                    this.mWrapped.onRemoved(this.mLastEventPosition, this.mLastEventCount);
                    break;
                case TYPE_CHANGE /*3*/:
                    this.mWrapped.onChanged(this.mLastEventPosition, this.mLastEventCount, this.mLastEventPayload);
                    break;
            }
            this.mLastEventPayload = null;
            this.mLastEventType = TYPE_NONE;
        }
    }

    public void onInserted(int position, int count) {
        if (this.mLastEventType != TYPE_ADD || position < this.mLastEventPosition || position > this.mLastEventPosition + this.mLastEventCount) {
            dispatchLastEvent();
            this.mLastEventPosition = position;
            this.mLastEventCount = count;
            this.mLastEventType = TYPE_ADD;
            return;
        }
        this.mLastEventCount += count;
        this.mLastEventPosition = Math.min(position, this.mLastEventPosition);
    }

    public void onRemoved(int position, int count) {
        if (this.mLastEventType != TYPE_REMOVE || this.mLastEventPosition < position || this.mLastEventPosition > position + count) {
            dispatchLastEvent();
            this.mLastEventPosition = position;
            this.mLastEventCount = count;
            this.mLastEventType = TYPE_REMOVE;
            return;
        }
        this.mLastEventCount += count;
        this.mLastEventPosition = position;
    }

    public void onMoved(int fromPosition, int toPosition) {
        dispatchLastEvent();
        this.mWrapped.onMoved(fromPosition, toPosition);
    }

    public void onChanged(int position, int count, Object payload) {
        if (this.mLastEventType != TYPE_CHANGE || position > this.mLastEventPosition + this.mLastEventCount || position + count < this.mLastEventPosition || this.mLastEventPayload != payload) {
            dispatchLastEvent();
            this.mLastEventPosition = position;
            this.mLastEventCount = count;
            this.mLastEventPayload = payload;
            this.mLastEventType = TYPE_CHANGE;
            return;
        }
        int previousEnd = this.mLastEventPosition + this.mLastEventCount;
        this.mLastEventPosition = Math.min(position, this.mLastEventPosition);
        this.mLastEventCount = Math.max(previousEnd, position + count) - this.mLastEventPosition;
    }
}
