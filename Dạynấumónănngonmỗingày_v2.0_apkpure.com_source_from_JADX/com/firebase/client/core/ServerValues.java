package com.firebase.client.core;

import com.firebase.client.core.SparseSnapshotTree.SparseSnapshotTreeVisitor;
import com.firebase.client.snapshot.ChildKey;
import com.firebase.client.snapshot.ChildrenNode;
import com.firebase.client.snapshot.ChildrenNode.ChildVisitor;
import com.firebase.client.snapshot.Node;
import com.firebase.client.snapshot.NodeUtilities;
import com.firebase.client.snapshot.PriorityUtilities;
import com.firebase.client.utilities.Clock;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import rx.android.BuildConfig;

public class ServerValues {
    public static final String NAME_SUBKEY_SERVERVALUE = ".sv";

    /* renamed from: com.firebase.client.core.ServerValues.1 */
    static class C11061 implements SparseSnapshotTreeVisitor {
        final /* synthetic */ SparseSnapshotTree val$resolvedTree;
        final /* synthetic */ Map val$serverValues;

        C11061(SparseSnapshotTree sparseSnapshotTree, Map map) {
            this.val$resolvedTree = sparseSnapshotTree;
            this.val$serverValues = map;
        }

        public void visitTree(Path prefixPath, Node tree) {
            this.val$resolvedTree.remember(prefixPath, ServerValues.resolveDeferredValueSnapshot(tree, this.val$serverValues));
        }
    }

    /* renamed from: com.firebase.client.core.ServerValues.2 */
    static class C14942 extends ChildVisitor {
        final /* synthetic */ SnapshotHolder val$holder;
        final /* synthetic */ Map val$serverValues;

        C14942(Map map, SnapshotHolder snapshotHolder) {
            this.val$serverValues = map;
            this.val$holder = snapshotHolder;
        }

        public void visitChild(ChildKey name, Node child) {
            Node newChildNode = ServerValues.resolveDeferredValueSnapshot(child, this.val$serverValues);
            if (newChildNode != child) {
                this.val$holder.update(new Path(name.asString()), newChildNode);
            }
        }
    }

    public static Map<String, Object> generateServerValues(Clock clock) {
        Map<String, Object> values = new HashMap();
        values.put("timestamp", Long.valueOf(clock.millis()));
        return values;
    }

    public static Object resolveDeferredValue(Object value, Map<String, Object> serverValues) {
        if (!(value instanceof Map)) {
            return value;
        }
        Map mapValue = (Map) value;
        if (!mapValue.containsKey(NAME_SUBKEY_SERVERVALUE)) {
            return value;
        }
        String serverValueKey = (String) mapValue.get(NAME_SUBKEY_SERVERVALUE);
        if (serverValues.containsKey(serverValueKey)) {
            return serverValues.get(serverValueKey);
        }
        return value;
    }

    public static SparseSnapshotTree resolveDeferredValueTree(SparseSnapshotTree tree, Map<String, Object> serverValues) {
        SparseSnapshotTree resolvedTree = new SparseSnapshotTree();
        tree.forEachTree(new Path(BuildConfig.VERSION_NAME), new C11061(resolvedTree, serverValues));
        return resolvedTree;
    }

    public static Node resolveDeferredValueSnapshot(Node data, Map<String, Object> serverValues) {
        Object priorityVal = data.getPriority().getValue();
        if (priorityVal instanceof Map) {
            Map priorityMapValue = (Map) priorityVal;
            if (priorityMapValue.containsKey(NAME_SUBKEY_SERVERVALUE)) {
                priorityVal = serverValues.get((String) priorityMapValue.get(NAME_SUBKEY_SERVERVALUE));
            }
        }
        Node priority = PriorityUtilities.parsePriority(priorityVal);
        if (data.isLeafNode()) {
            Object value = resolveDeferredValue(data.getValue(), serverValues);
            if (value.equals(data.getValue()) && priority.equals(data.getPriority())) {
                return data;
            }
            return NodeUtilities.NodeFromJSON(value, priority);
        } else if (data.isEmpty()) {
            return data;
        } else {
            ChildrenNode childNode = (ChildrenNode) data;
            SnapshotHolder holder = new SnapshotHolder(childNode);
            childNode.forEachChild(new C14942(serverValues, holder));
            if (holder.getRootNode().getPriority().equals(priority)) {
                return holder.getRootNode();
            }
            return holder.getRootNode().updatePriority(priority);
        }
    }

    public static CompoundWrite resolveDeferredValueMerge(CompoundWrite merge, Map<String, Object> serverValues) {
        CompoundWrite write = CompoundWrite.emptyWrite();
        Iterator i$ = merge.iterator();
        while (i$.hasNext()) {
            Entry<Path, Node> entry = (Entry) i$.next();
            write = write.addWrite((Path) entry.getKey(), resolveDeferredValueSnapshot((Node) entry.getValue(), serverValues));
        }
        return write;
    }
}
