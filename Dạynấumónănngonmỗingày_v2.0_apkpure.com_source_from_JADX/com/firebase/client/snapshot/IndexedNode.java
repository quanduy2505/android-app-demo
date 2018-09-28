package com.firebase.client.snapshot;

import com.firebase.client.collection.ImmutableSortedSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class IndexedNode implements Iterable<NamedNode> {
    private static final ImmutableSortedSet<NamedNode> FALLBACK_INDEX;
    private final Index index;
    private ImmutableSortedSet<NamedNode> indexed;
    private final Node node;

    static {
        FALLBACK_INDEX = new ImmutableSortedSet(Collections.emptyList(), null);
    }

    private IndexedNode(Node node, Index index) {
        this.index = index;
        this.node = node;
        this.indexed = null;
    }

    private IndexedNode(Node node, Index index, ImmutableSortedSet<NamedNode> indexed) {
        this.index = index;
        this.node = node;
        this.indexed = indexed;
    }

    private void ensureIndexed() {
        if (this.indexed != null) {
            return;
        }
        if (this.index.equals(KeyIndex.getInstance())) {
            this.indexed = FALLBACK_INDEX;
            return;
        }
        List<NamedNode> children = new ArrayList();
        boolean sawIndexedValue = false;
        for (NamedNode entry : this.node) {
            sawIndexedValue = sawIndexedValue || this.index.isDefinedOn(entry.getNode());
            children.add(new NamedNode(entry.getName(), entry.getNode()));
        }
        if (sawIndexedValue) {
            this.indexed = new ImmutableSortedSet(children, this.index);
        } else {
            this.indexed = FALLBACK_INDEX;
        }
    }

    public static IndexedNode from(Node node) {
        return new IndexedNode(node, PriorityIndex.getInstance());
    }

    public static IndexedNode from(Node node, Index index) {
        return new IndexedNode(node, index);
    }

    public boolean hasIndex(Index index) {
        return this.index.equals(index);
    }

    public Node getNode() {
        return this.node;
    }

    public Iterator<NamedNode> iterator() {
        ensureIndexed();
        if (this.indexed == FALLBACK_INDEX) {
            return this.node.iterator();
        }
        return this.indexed.iterator();
    }

    public Iterator<NamedNode> reverseIterator() {
        ensureIndexed();
        if (this.indexed == FALLBACK_INDEX) {
            return this.node.reverseIterator();
        }
        return this.indexed.reverseIterator();
    }

    public IndexedNode updateChild(ChildKey key, Node child) {
        Node newNode = this.node.updateImmediateChild(key, child);
        if (this.indexed == FALLBACK_INDEX && !this.index.isDefinedOn(child)) {
            return new IndexedNode(newNode, this.index, FALLBACK_INDEX);
        }
        if (this.indexed == null || this.indexed == FALLBACK_INDEX) {
            return new IndexedNode(newNode, this.index, null);
        }
        ImmutableSortedSet<NamedNode> newIndexed = this.indexed.remove(new NamedNode(key, this.node.getImmediateChild(key)));
        if (!child.isEmpty()) {
            newIndexed = newIndexed.insert(new NamedNode(key, child));
        }
        return new IndexedNode(newNode, this.index, newIndexed);
    }

    public IndexedNode updatePriority(Node priority) {
        return new IndexedNode(this.node.updatePriority(priority), this.index, this.indexed);
    }

    public NamedNode getFirstChild() {
        if (!(this.node instanceof ChildrenNode)) {
            return null;
        }
        ensureIndexed();
        if (this.indexed != FALLBACK_INDEX) {
            return (NamedNode) this.indexed.getMinEntry();
        }
        ChildKey firstKey = ((ChildrenNode) this.node).getFirstChildKey();
        return new NamedNode(firstKey, this.node.getImmediateChild(firstKey));
    }

    public NamedNode getLastChild() {
        if (!(this.node instanceof ChildrenNode)) {
            return null;
        }
        ensureIndexed();
        if (this.indexed != FALLBACK_INDEX) {
            return (NamedNode) this.indexed.getMaxEntry();
        }
        ChildKey lastKey = ((ChildrenNode) this.node).getLastChildKey();
        return new NamedNode(lastKey, this.node.getImmediateChild(lastKey));
    }

    public ChildKey getPredecessorChildName(ChildKey childKey, Node childNode, Index index) {
        if (this.index.equals(KeyIndex.getInstance()) || this.index.equals(index)) {
            ensureIndexed();
            if (this.indexed == FALLBACK_INDEX) {
                return this.node.getPredecessorChildKey(childKey);
            }
            NamedNode node = (NamedNode) this.indexed.getPredecessorEntry(new NamedNode(childKey, childNode));
            return node != null ? node.getName() : null;
        } else {
            throw new IllegalArgumentException("Index not available in IndexedNode!");
        }
    }
}
