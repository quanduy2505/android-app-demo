package com.firebase.client.core.operation;

import com.firebase.client.core.Path;
import com.firebase.client.snapshot.ChildKey;

public abstract class Operation {
    protected final Path path;
    protected final OperationSource source;
    protected final OperationType type;

    public enum OperationType {
        Overwrite,
        Merge,
        AckUserWrite,
        ListenComplete
    }

    public abstract Operation operationForChild(ChildKey childKey);

    protected Operation(OperationType type, OperationSource source, Path path) {
        this.type = type;
        this.source = source;
        this.path = path;
    }

    public Path getPath() {
        return this.path;
    }

    public OperationSource getSource() {
        return this.source;
    }

    public OperationType getType() {
        return this.type;
    }
}
