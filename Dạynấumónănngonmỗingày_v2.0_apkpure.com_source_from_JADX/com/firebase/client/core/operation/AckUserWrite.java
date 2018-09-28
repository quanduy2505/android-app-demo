package com.firebase.client.core.operation;

import com.firebase.client.core.Path;
import com.firebase.client.core.operation.Operation.OperationType;
import com.firebase.client.core.utilities.ImmutableTree;
import com.firebase.client.snapshot.ChildKey;
import com.firebase.client.utilities.Utilities;

public class AckUserWrite extends Operation {
    private final ImmutableTree<Boolean> affectedTree;
    private final boolean revert;

    public AckUserWrite(Path path, ImmutableTree<Boolean> affectedTree, boolean revert) {
        super(OperationType.AckUserWrite, OperationSource.USER, path);
        this.affectedTree = affectedTree;
        this.revert = revert;
    }

    public ImmutableTree<Boolean> getAffectedTree() {
        return this.affectedTree;
    }

    public boolean isRevert() {
        return this.revert;
    }

    public Operation operationForChild(ChildKey childKey) {
        if (!this.path.isEmpty()) {
            Utilities.hardAssert(this.path.getFront().equals(childKey), "operationForChild called for unrelated child.");
            return new AckUserWrite(this.path.popFront(), this.affectedTree, this.revert);
        } else if (this.affectedTree.getValue() != null) {
            Utilities.hardAssert(this.affectedTree.getChildren().isEmpty(), "affectedTree should not have overlapping affected paths.");
            return this;
        } else {
            return new AckUserWrite(Path.getEmptyPath(), this.affectedTree.subtree(new Path(childKey)), this.revert);
        }
    }

    public String toString() {
        return String.format("AckUserWrite { path=%s, revert=%s, affectedTree=%s }", new Object[]{getPath(), Boolean.valueOf(this.revert), this.affectedTree});
    }
}
