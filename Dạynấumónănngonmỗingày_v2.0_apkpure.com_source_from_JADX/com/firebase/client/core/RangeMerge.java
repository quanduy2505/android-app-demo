package com.firebase.client.core;

import com.firebase.client.snapshot.ChildKey;
import com.firebase.client.snapshot.EmptyNode;
import com.firebase.client.snapshot.NamedNode;
import com.firebase.client.snapshot.Node;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RangeMerge {
    static final /* synthetic */ boolean $assertionsDisabled;
    private final Path optExclusiveStart;
    private final Path optInclusiveEnd;
    private final Node snap;

    static {
        $assertionsDisabled = !RangeMerge.class.desiredAssertionStatus();
    }

    public RangeMerge(Path optExclusiveStart, Path optInclusiveEnd, Node snap) {
        this.optExclusiveStart = optExclusiveStart;
        this.optInclusiveEnd = optInclusiveEnd;
        this.snap = snap;
    }

    public Node applyTo(Node node) {
        return updateRangeInNode(Path.getEmptyPath(), node, this.snap);
    }

    Path getStart() {
        return this.optExclusiveStart;
    }

    Path getEnd() {
        return this.optInclusiveEnd;
    }

    private Node updateRangeInNode(Path currentPath, Node node, Node updateNode) {
        boolean startInNode;
        boolean endInNode;
        Set<ChildKey> allChildren;
        List<ChildKey> inOrder;
        Node newNode;
        Node currentChild;
        Node updatedChild;
        int startComparison = this.optExclusiveStart == null ? 1 : currentPath.compareTo(this.optExclusiveStart);
        int endComparison = this.optInclusiveEnd == null ? -1 : currentPath.compareTo(this.optInclusiveEnd);
        if (this.optExclusiveStart != null) {
            if (currentPath.contains(this.optExclusiveStart)) {
                startInNode = true;
                if (this.optInclusiveEnd != null) {
                    if (currentPath.contains(this.optInclusiveEnd)) {
                        endInNode = true;
                        if (startComparison <= 0 && endComparison < 0 && !endInNode) {
                            return updateNode;
                        }
                        if (startComparison <= 0 && endInNode && updateNode.isLeafNode()) {
                            return updateNode;
                        }
                        if (startComparison > 0 || endComparison != 0) {
                            if (!startInNode || endInNode) {
                                allChildren = new HashSet();
                                for (NamedNode child : node) {
                                    allChildren.add(child.getName());
                                }
                                for (NamedNode child2 : updateNode) {
                                    allChildren.add(child2.getName());
                                }
                                inOrder = new ArrayList(allChildren.size() + 1);
                                inOrder.addAll(allChildren);
                                if (!(updateNode.getPriority().isEmpty() && node.getPriority().isEmpty())) {
                                    inOrder.add(ChildKey.getPriorityKey());
                                }
                                newNode = node;
                                for (ChildKey key : inOrder) {
                                    currentChild = node.getImmediateChild(key);
                                    updatedChild = updateRangeInNode(currentPath.child(key), node.getImmediateChild(key), updateNode.getImmediateChild(key));
                                    if (updatedChild == currentChild) {
                                        newNode = newNode.updateImmediateChild(key, updatedChild);
                                    }
                                }
                                return newNode;
                            } else if ($assertionsDisabled || endComparison > 0 || startComparison <= 0) {
                                return node;
                            } else {
                                throw new AssertionError();
                            }
                        } else if (!$assertionsDisabled && !endInNode) {
                            throw new AssertionError();
                        } else if ($assertionsDisabled || !updateNode.isLeafNode()) {
                            return node.isLeafNode() ? EmptyNode.Empty() : node;
                        } else {
                            throw new AssertionError();
                        }
                    }
                }
                endInNode = false;
                if (startComparison <= 0) {
                }
                if (startComparison <= 0) {
                }
                if (startComparison > 0) {
                }
                if (startInNode) {
                }
                allChildren = new HashSet();
                while (i$.hasNext()) {
                    allChildren.add(child2.getName());
                }
                while (i$.hasNext()) {
                    allChildren.add(child2.getName());
                }
                inOrder = new ArrayList(allChildren.size() + 1);
                inOrder.addAll(allChildren);
                inOrder.add(ChildKey.getPriorityKey());
                newNode = node;
                for (ChildKey key2 : inOrder) {
                    currentChild = node.getImmediateChild(key2);
                    updatedChild = updateRangeInNode(currentPath.child(key2), node.getImmediateChild(key2), updateNode.getImmediateChild(key2));
                    if (updatedChild == currentChild) {
                        newNode = newNode.updateImmediateChild(key2, updatedChild);
                    }
                }
                return newNode;
            }
        }
        startInNode = false;
        if (this.optInclusiveEnd != null) {
            if (currentPath.contains(this.optInclusiveEnd)) {
                endInNode = true;
                if (startComparison <= 0) {
                }
                if (startComparison <= 0) {
                }
                if (startComparison > 0) {
                }
                if (startInNode) {
                }
                allChildren = new HashSet();
                while (i$.hasNext()) {
                    allChildren.add(child2.getName());
                }
                while (i$.hasNext()) {
                    allChildren.add(child2.getName());
                }
                inOrder = new ArrayList(allChildren.size() + 1);
                inOrder.addAll(allChildren);
                inOrder.add(ChildKey.getPriorityKey());
                newNode = node;
                for (ChildKey key22 : inOrder) {
                    currentChild = node.getImmediateChild(key22);
                    updatedChild = updateRangeInNode(currentPath.child(key22), node.getImmediateChild(key22), updateNode.getImmediateChild(key22));
                    if (updatedChild == currentChild) {
                        newNode = newNode.updateImmediateChild(key22, updatedChild);
                    }
                }
                return newNode;
            }
        }
        endInNode = false;
        if (startComparison <= 0) {
        }
        if (startComparison <= 0) {
        }
        if (startComparison > 0) {
        }
        if (startInNode) {
        }
        allChildren = new HashSet();
        while (i$.hasNext()) {
            allChildren.add(child2.getName());
        }
        while (i$.hasNext()) {
            allChildren.add(child2.getName());
        }
        inOrder = new ArrayList(allChildren.size() + 1);
        inOrder.addAll(allChildren);
        inOrder.add(ChildKey.getPriorityKey());
        newNode = node;
        for (ChildKey key222 : inOrder) {
            currentChild = node.getImmediateChild(key222);
            updatedChild = updateRangeInNode(currentPath.child(key222), node.getImmediateChild(key222), updateNode.getImmediateChild(key222));
            if (updatedChild == currentChild) {
                newNode = newNode.updateImmediateChild(key222, updatedChild);
            }
        }
        return newNode;
    }

    public String toString() {
        return "RangeMerge{optExclusiveStart=" + this.optExclusiveStart + ", optInclusiveEnd=" + this.optInclusiveEnd + ", snap=" + this.snap + '}';
    }
}
