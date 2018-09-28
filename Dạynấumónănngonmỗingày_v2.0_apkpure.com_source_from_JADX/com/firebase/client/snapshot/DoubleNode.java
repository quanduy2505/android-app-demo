package com.firebase.client.snapshot;

import com.firebase.client.snapshot.Node.HashVersion;
import com.firebase.client.utilities.Utilities;

public class DoubleNode extends LeafNode<DoubleNode> {
    static final /* synthetic */ boolean $assertionsDisabled;
    private final Double value;

    static {
        $assertionsDisabled = !DoubleNode.class.desiredAssertionStatus();
    }

    public DoubleNode(Double value, Node priority) {
        super(priority);
        this.value = value;
    }

    public Object getValue() {
        return this.value;
    }

    public String getHashRepresentation(HashVersion version) {
        return (getPriorityHash(version) + "number:") + Utilities.doubleToHashString(this.value.doubleValue());
    }

    public DoubleNode updatePriority(Node priority) {
        if ($assertionsDisabled || PriorityUtilities.isValidPriority(priority)) {
            return new DoubleNode(this.value, priority);
        }
        throw new AssertionError();
    }

    protected LeafType getLeafType() {
        return LeafType.Number;
    }

    protected int compareLeafValues(DoubleNode other) {
        return this.value.compareTo(other.value);
    }

    public boolean equals(Object other) {
        if (!(other instanceof DoubleNode)) {
            return false;
        }
        DoubleNode otherDoubleNode = (DoubleNode) other;
        if (this.value.equals(otherDoubleNode.value) && this.priority.equals(otherDoubleNode.priority)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return this.value.hashCode() + this.priority.hashCode();
    }
}
