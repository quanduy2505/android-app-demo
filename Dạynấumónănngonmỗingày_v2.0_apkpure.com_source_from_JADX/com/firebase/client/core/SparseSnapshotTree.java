package com.firebase.client.core;

import com.firebase.client.snapshot.ChildKey;
import com.firebase.client.snapshot.ChildrenNode;
import com.firebase.client.snapshot.ChildrenNode.ChildVisitor;
import com.firebase.client.snapshot.Node;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

class SparseSnapshotTree {
    private Map<ChildKey, SparseSnapshotTree> children;
    private Node value;

    public interface SparseSnapshotChildVisitor {
        void visitChild(ChildKey childKey, SparseSnapshotTree sparseSnapshotTree);
    }

    public interface SparseSnapshotTreeVisitor {
        void visitTree(Path path, Node node);
    }

    /* renamed from: com.firebase.client.core.SparseSnapshotTree.2 */
    class C11072 implements SparseSnapshotChildVisitor {
        final /* synthetic */ Path val$prefixPath;
        final /* synthetic */ SparseSnapshotTreeVisitor val$visitor;

        C11072(Path path, SparseSnapshotTreeVisitor sparseSnapshotTreeVisitor) {
            this.val$prefixPath = path;
            this.val$visitor = sparseSnapshotTreeVisitor;
        }

        public void visitChild(ChildKey key, SparseSnapshotTree tree) {
            tree.forEachTree(this.val$prefixPath.child(key), this.val$visitor);
        }
    }

    /* renamed from: com.firebase.client.core.SparseSnapshotTree.1 */
    class C14951 extends ChildVisitor {
        final /* synthetic */ Path val$path;

        C14951(Path path) {
            this.val$path = path;
        }

        public void visitChild(ChildKey name, Node child) {
            SparseSnapshotTree.this.remember(this.val$path.child(name), child);
        }
    }

    public SparseSnapshotTree() {
        this.value = null;
        this.children = null;
    }

    public void remember(Path path, Node data) {
        if (path.isEmpty()) {
            this.value = data;
            this.children = null;
        } else if (this.value != null) {
            this.value = this.value.updateChild(path, data);
        } else {
            if (this.children == null) {
                this.children = new HashMap();
            }
            ChildKey childKey = path.getFront();
            if (!this.children.containsKey(childKey)) {
                this.children.put(childKey, new SparseSnapshotTree());
            }
            ((SparseSnapshotTree) this.children.get(childKey)).remember(path.popFront(), data);
        }
    }

    public boolean forget(Path path) {
        if (path.isEmpty()) {
            this.value = null;
            this.children = null;
            return true;
        } else if (this.value != null) {
            if (this.value.isLeafNode()) {
                return false;
            }
            ChildrenNode childrenNode = this.value;
            this.value = null;
            childrenNode.forEachChild(new C14951(path));
            return forget(path);
        } else if (this.children == null) {
            return true;
        } else {
            ChildKey childKey = path.getFront();
            Path childPath = path.popFront();
            if (this.children.containsKey(childKey) && ((SparseSnapshotTree) this.children.get(childKey)).forget(childPath)) {
                this.children.remove(childKey);
            }
            if (!this.children.isEmpty()) {
                return false;
            }
            this.children = null;
            return true;
        }
    }

    public void forEachTree(Path prefixPath, SparseSnapshotTreeVisitor visitor) {
        if (this.value != null) {
            visitor.visitTree(prefixPath, this.value);
        } else {
            forEachChild(new C11072(prefixPath, visitor));
        }
    }

    public void forEachChild(SparseSnapshotChildVisitor visitor) {
        if (this.children != null) {
            for (Entry<ChildKey, SparseSnapshotTree> entry : this.children.entrySet()) {
                visitor.visitChild((ChildKey) entry.getKey(), (SparseSnapshotTree) entry.getValue());
            }
        }
    }
}
