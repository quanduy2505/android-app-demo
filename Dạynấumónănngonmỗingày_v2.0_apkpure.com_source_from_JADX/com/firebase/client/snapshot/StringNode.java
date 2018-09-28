package com.firebase.client.snapshot;

import com.firebase.client.snapshot.Node.HashVersion;
import com.firebase.client.utilities.Utilities;
import rx.internal.operators.OnSubscribeConcatMap;

public class StringNode extends LeafNode<StringNode> {
    private final String value;

    /* renamed from: com.firebase.client.snapshot.StringNode.1 */
    static /* synthetic */ class C05841 {
        static final /* synthetic */ int[] $SwitchMap$com$firebase$client$snapshot$Node$HashVersion;

        static {
            $SwitchMap$com$firebase$client$snapshot$Node$HashVersion = new int[HashVersion.values().length];
            try {
                $SwitchMap$com$firebase$client$snapshot$Node$HashVersion[HashVersion.V1.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$firebase$client$snapshot$Node$HashVersion[HashVersion.V2.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    public StringNode(String value, Node priority) {
        super(priority);
        this.value = value;
    }

    public Object getValue() {
        return this.value;
    }

    public String getHashRepresentation(HashVersion version) {
        switch (C05841.$SwitchMap$com$firebase$client$snapshot$Node$HashVersion[version.ordinal()]) {
            case OnSubscribeConcatMap.BOUNDARY /*1*/:
                return getPriorityHash(version) + "string:" + this.value;
            case OnSubscribeConcatMap.END /*2*/:
                return getPriorityHash(version) + "string:" + Utilities.stringHashV2Representation(this.value);
            default:
                throw new IllegalArgumentException("Invalid hash version for string node: " + version);
        }
    }

    public StringNode updatePriority(Node priority) {
        return new StringNode(this.value, priority);
    }

    protected LeafType getLeafType() {
        return LeafType.String;
    }

    protected int compareLeafValues(StringNode other) {
        return this.value.compareTo(other.value);
    }

    public boolean equals(Object other) {
        if (!(other instanceof StringNode)) {
            return false;
        }
        StringNode otherStringNode = (StringNode) other;
        if (this.value.equals(otherStringNode.value) && this.priority.equals(otherStringNode.priority)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return this.value.hashCode() + this.priority.hashCode();
    }
}
