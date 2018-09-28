package com.firebase.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.firebase.client.core.Path;
import com.firebase.client.snapshot.IndexedNode;
import com.firebase.client.snapshot.NamedNode;
import com.firebase.client.utilities.Validation;
import com.firebase.client.utilities.encoding.JsonHelpers;
import java.io.IOException;
import java.util.Iterator;

public class DataSnapshot {
    private final IndexedNode node;
    private final Firebase query;

    /* renamed from: com.firebase.client.DataSnapshot.1 */
    class C05101 implements Iterable<DataSnapshot> {
        final /* synthetic */ Iterator val$iter;

        /* renamed from: com.firebase.client.DataSnapshot.1.1 */
        class C05091 implements Iterator<DataSnapshot> {
            C05091() {
            }

            public boolean hasNext() {
                return C05101.this.val$iter.hasNext();
            }

            public DataSnapshot next() {
                NamedNode namedNode = (NamedNode) C05101.this.val$iter.next();
                return new DataSnapshot(DataSnapshot.this.query.child(namedNode.getName().asString()), IndexedNode.from(namedNode.getNode()));
            }

            public void remove() {
                throw new UnsupportedOperationException("remove called on immutable collection");
            }
        }

        C05101(Iterator it) {
            this.val$iter = it;
        }

        public Iterator<DataSnapshot> iterator() {
            return new C05091();
        }
    }

    public DataSnapshot(Firebase ref, IndexedNode node) {
        this.node = node;
        this.query = ref;
    }

    public DataSnapshot child(String path) {
        return new DataSnapshot(this.query.child(path), IndexedNode.from(this.node.getNode().getChild(new Path(path))));
    }

    public boolean hasChild(String path) {
        if (this.query.getParent() == null) {
            Validation.validateRootPathString(path);
        } else {
            Validation.validatePathString(path);
        }
        return !this.node.getNode().getChild(new Path(path)).isEmpty();
    }

    public boolean hasChildren() {
        return this.node.getNode().getChildCount() > 0;
    }

    public boolean exists() {
        return !this.node.getNode().isEmpty();
    }

    public Object getValue() {
        return this.node.getNode().getValue();
    }

    public Object getValue(boolean useExportFormat) {
        return this.node.getNode().getValue(useExportFormat);
    }

    public <T> T getValue(Class<T> valueType) {
        try {
            return JsonHelpers.getMapper().readValue(JsonHelpers.getMapper().writeValueAsString(this.node.getNode().getValue()), (Class) valueType);
        } catch (IOException e) {
            throw new FirebaseException("Failed to bounce to type", e);
        }
    }

    public <T> T getValue(GenericTypeIndicator<T> t) {
        try {
            return JsonHelpers.getMapper().readValue(JsonHelpers.getMapper().writeValueAsString(this.node.getNode().getValue()), (TypeReference) t);
        } catch (IOException e) {
            throw new FirebaseException("Failed to bounce to type", e);
        }
    }

    public long getChildrenCount() {
        return (long) this.node.getNode().getChildCount();
    }

    public Firebase getRef() {
        return this.query;
    }

    public String getKey() {
        return this.query.getKey();
    }

    public Iterable<DataSnapshot> getChildren() {
        return new C05101(this.node.iterator());
    }

    public Object getPriority() {
        Object value = this.node.getNode().getPriority().getValue();
        if (value instanceof Long) {
            return Double.valueOf((double) ((Long) value).longValue());
        }
        return value;
    }

    public String toString() {
        return "DataSnapshot { key = " + this.query.getKey() + ", value = " + this.node.getNode().getValue(true) + " }";
    }
}
