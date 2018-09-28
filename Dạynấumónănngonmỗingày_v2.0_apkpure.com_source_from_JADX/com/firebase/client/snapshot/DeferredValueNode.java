package com.firebase.client.snapshot;

import com.firebase.client.snapshot.Node.HashVersion;
import java.util.Map;

public class DeferredValueNode extends LeafNode<DeferredValueNode> {
    static final /* synthetic */ boolean $assertionsDisabled;
    private Map<Object, Object> value;

    static {
        $assertionsDisabled = !DeferredValueNode.class.desiredAssertionStatus();
    }

    public DeferredValueNode(Map<Object, Object> value, Node priority) {
        super(priority);
        this.value = value;
    }

    public Object getValue() {
        return this.value;
    }

    public String getHashRepresentation(HashVersion version) {
        return getPriorityHash(version) + "deferredValue:" + this.value;
    }

    public DeferredValueNode updatePriority(Node priority) {
        if ($assertionsDisabled || PriorityUtilities.isValidPriority(priority)) {
            return new DeferredValueNode(this.value, priority);
        }
        throw new AssertionError();
    }

    protected LeafType getLeafType() {
        return LeafType.DeferredValue;
    }

    protected int compareLeafValues(DeferredValueNode other) {
        return 0;
    }

    public boolean equals(Object other) {
        if (!(other instanceof DeferredValueNode)) {
            return false;
        }
        DeferredValueNode otherDeferredValueNode = (DeferredValueNode) other;
        if (this.value.equals(otherDeferredValueNode.value) && this.priority.equals(otherDeferredValueNode.priority)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return this.value.hashCode() + this.priority.hashCode();
    }
}
