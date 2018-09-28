package com.firebase.client.core;

import com.firebase.client.FirebaseError;
import com.firebase.client.annotations.NotNull;
import com.firebase.client.annotations.Nullable;
import com.firebase.client.collection.LLRBNode.NodeVisitor;
import com.firebase.client.core.operation.AckUserWrite;
import com.firebase.client.core.operation.ListenComplete;
import com.firebase.client.core.operation.Merge;
import com.firebase.client.core.operation.Operation;
import com.firebase.client.core.operation.OperationSource;
import com.firebase.client.core.operation.Overwrite;
import com.firebase.client.core.persistence.PersistenceManager;
import com.firebase.client.core.utilities.ImmutableTree;
import com.firebase.client.core.utilities.ImmutableTree.TreeVisitor;
import com.firebase.client.core.view.CacheNode;
import com.firebase.client.core.view.Change;
import com.firebase.client.core.view.DataEvent;
import com.firebase.client.core.view.Event;
import com.firebase.client.core.view.Event.EventType;
import com.firebase.client.core.view.QuerySpec;
import com.firebase.client.core.view.View;
import com.firebase.client.snapshot.ChildKey;
import com.firebase.client.snapshot.EmptyNode;
import com.firebase.client.snapshot.IndexedNode;
import com.firebase.client.snapshot.NamedNode;
import com.firebase.client.snapshot.Node;
import com.firebase.client.utilities.Clock;
import com.firebase.client.utilities.LogWrapper;
import com.firebase.client.utilities.NodeSizeEstimator;
import com.firebase.client.utilities.Utilities;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import rx.android.BuildConfig;

public class SyncTree {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static final long SIZE_THRESHOLD_FOR_COMPOUND_HASH = 1024;
    private final Set<QuerySpec> keepSyncedQueries;
    private final ListenProvider listenProvider;
    private final LogWrapper logger;
    private long nextQueryTag;
    private final WriteTree pendingWriteTree;
    private final PersistenceManager persistenceManager;
    private final Map<QuerySpec, Tag> queryToTagMap;
    private ImmutableTree<SyncPoint> syncPointTree;
    private final Map<Tag, QuerySpec> tagToQueryMap;

    /* renamed from: com.firebase.client.core.SyncTree.10 */
    class AnonymousClass10 implements Callable<List<? extends Event>> {
        final /* synthetic */ Map val$changedChildren;
        final /* synthetic */ Path val$path;
        final /* synthetic */ Tag val$tag;

        AnonymousClass10(Tag tag, Path path, Map map) {
            this.val$tag = tag;
            this.val$path = path;
            this.val$changedChildren = map;
        }

        public List<? extends Event> call() {
            QuerySpec query = SyncTree.this.queryForTag(this.val$tag);
            if (query == null) {
                return Collections.emptyList();
            }
            Path relativePath = Path.getRelative(query.getPath(), this.val$path);
            CompoundWrite merge = CompoundWrite.fromPathMerge(this.val$changedChildren);
            SyncTree.this.persistenceManager.updateServerCache(this.val$path, merge);
            return SyncTree.this.applyTaggedOperation(query, new Merge(OperationSource.forServerTaggedQuery(query.getParams()), relativePath, merge));
        }
    }

    /* renamed from: com.firebase.client.core.SyncTree.11 */
    class AnonymousClass11 implements Callable<List<? extends Event>> {
        static final /* synthetic */ boolean $assertionsDisabled;
        final /* synthetic */ EventRegistration val$eventRegistration;

        static {
            $assertionsDisabled = !SyncTree.class.desiredAssertionStatus() ? true : SyncTree.$assertionsDisabled;
        }

        AnonymousClass11(EventRegistration eventRegistration) {
            this.val$eventRegistration = eventRegistration;
        }

        public List<? extends Event> call() {
            CacheNode serverCache;
            QuerySpec query = this.val$eventRegistration.getQuerySpec();
            Path path = query.getPath();
            Node serverCacheNode = null;
            boolean foundAncestorDefaultView = SyncTree.$assertionsDisabled;
            ImmutableTree<SyncPoint> tree = SyncTree.this.syncPointTree;
            Path currentPath = path;
            while (!tree.isEmpty()) {
                SyncPoint currentSyncPoint = (SyncPoint) tree.getValue();
                if (currentSyncPoint != null) {
                    if (serverCacheNode == null) {
                        serverCacheNode = currentSyncPoint.getCompleteServerCache(currentPath);
                    }
                    foundAncestorDefaultView = (foundAncestorDefaultView || currentSyncPoint.hasCompleteView()) ? true : SyncTree.$assertionsDisabled;
                }
                tree = tree.getChild(currentPath.isEmpty() ? ChildKey.fromString(BuildConfig.VERSION_NAME) : currentPath.getFront());
                currentPath = currentPath.popFront();
            }
            SyncPoint syncPoint = (SyncPoint) SyncTree.this.syncPointTree.get(path);
            if (syncPoint == null) {
                SyncPoint syncPoint2 = new SyncPoint(SyncTree.this.persistenceManager);
                SyncTree.this.syncPointTree = SyncTree.this.syncPointTree.set(path, syncPoint2);
            } else {
                foundAncestorDefaultView = (foundAncestorDefaultView || syncPoint.hasCompleteView()) ? true : SyncTree.$assertionsDisabled;
                if (serverCacheNode == null) {
                    serverCacheNode = syncPoint.getCompleteServerCache(Path.getEmptyPath());
                }
            }
            SyncTree.this.persistenceManager.setQueryActive(query);
            CacheNode cacheNode;
            if (serverCacheNode != null) {
                cacheNode = new CacheNode(IndexedNode.from(serverCacheNode, query.getIndex()), true, SyncTree.$assertionsDisabled);
            } else {
                CacheNode persistentServerCache = SyncTree.this.persistenceManager.serverCache(query);
                if (persistentServerCache.isFullyInitialized()) {
                    serverCache = persistentServerCache;
                } else {
                    serverCacheNode = EmptyNode.Empty();
                    Iterator i$ = SyncTree.this.syncPointTree.subtree(path).getChildren().iterator();
                    while (i$.hasNext()) {
                        Entry<ChildKey, ImmutableTree<SyncPoint>> child = (Entry) i$.next();
                        SyncPoint childSyncPoint = (SyncPoint) ((ImmutableTree) child.getValue()).getValue();
                        if (childSyncPoint != null) {
                            Node completeCache = childSyncPoint.getCompleteServerCache(Path.getEmptyPath());
                            if (completeCache != null) {
                                serverCacheNode = serverCacheNode.updateImmediateChild((ChildKey) child.getKey(), completeCache);
                            }
                        }
                    }
                    for (NamedNode child2 : persistentServerCache.getNode()) {
                        if (!serverCacheNode.hasChild(child2.getName())) {
                            serverCacheNode = serverCacheNode.updateImmediateChild(child2.getName(), child2.getNode());
                        }
                    }
                    cacheNode = new CacheNode(IndexedNode.from(serverCacheNode, query.getIndex()), SyncTree.$assertionsDisabled, SyncTree.$assertionsDisabled);
                }
            }
            boolean viewAlreadyExists = syncPoint.viewExistsForQuery(query);
            if (!(viewAlreadyExists || query.loadsAllData())) {
                if (!$assertionsDisabled) {
                    if (SyncTree.this.queryToTagMap.containsKey(query)) {
                        throw new AssertionError("View does not exist but we have a tag");
                    }
                }
                Tag tag = SyncTree.this.getNextQueryTag();
                SyncTree.this.queryToTagMap.put(query, tag);
                SyncTree.this.tagToQueryMap.put(tag, query);
            }
            List<? extends Event> events = syncPoint.addEventRegistration(this.val$eventRegistration, SyncTree.this.pendingWriteTree.childWrites(path), serverCache);
            if (!(viewAlreadyExists || foundAncestorDefaultView)) {
                SyncTree.this.setupListener(query, syncPoint.viewForQuery(query));
            }
            return events;
        }
    }

    /* renamed from: com.firebase.client.core.SyncTree.12 */
    class AnonymousClass12 implements Callable<List<Event>> {
        static final /* synthetic */ boolean $assertionsDisabled;
        final /* synthetic */ FirebaseError val$cancelError;
        final /* synthetic */ EventRegistration val$eventRegistration;
        final /* synthetic */ QuerySpec val$query;

        static {
            $assertionsDisabled = !SyncTree.class.desiredAssertionStatus() ? true : SyncTree.$assertionsDisabled;
        }

        AnonymousClass12(QuerySpec querySpec, EventRegistration eventRegistration, FirebaseError firebaseError) {
            this.val$query = querySpec;
            this.val$eventRegistration = eventRegistration;
            this.val$cancelError = firebaseError;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public java.util.List<com.firebase.client.core.view.Event> call() {
            /*
            r24 = this;
            r0 = r24;
            r0 = r0.val$query;
            r21 = r0;
            r12 = r21.getPath();
            r0 = r24;
            r0 = com.firebase.client.core.SyncTree.this;
            r21 = r0;
            r21 = r21.syncPointTree;
            r0 = r21;
            r9 = r0.get(r12);
            r9 = (com.firebase.client.core.SyncPoint) r9;
            r3 = new java.util.ArrayList;
            r3.<init>();
            if (r9 == 0) goto L_0x01b5;
        L_0x0023:
            r0 = r24;
            r0 = r0.val$query;
            r21 = r0;
            r21 = r21.isDefault();
            if (r21 != 0) goto L_0x003d;
        L_0x002f:
            r0 = r24;
            r0 = r0.val$query;
            r21 = r0;
            r0 = r21;
            r21 = r9.viewExistsForQuery(r0);
            if (r21 == 0) goto L_0x01b5;
        L_0x003d:
            r0 = r24;
            r0 = r0.val$query;
            r21 = r0;
            r0 = r24;
            r0 = r0.val$eventRegistration;
            r22 = r0;
            r0 = r24;
            r0 = r0.val$cancelError;
            r23 = r0;
            r0 = r21;
            r1 = r22;
            r2 = r23;
            r16 = r9.removeEventRegistration(r0, r1, r2);
            r21 = r9.isEmpty();
            if (r21 == 0) goto L_0x0078;
        L_0x005f:
            r0 = r24;
            r0 = com.firebase.client.core.SyncTree.this;
            r21 = r0;
            r0 = r24;
            r0 = com.firebase.client.core.SyncTree.this;
            r22 = r0;
            r22 = r22.syncPointTree;
            r0 = r22;
            r22 = r0.remove(r12);
            r21.syncPointTree = r22;
        L_0x0078:
            r15 = r16.getFirst();
            r15 = (java.util.List) r15;
            r3 = r16.getSecond();
            r3 = (java.util.List) r3;
            r17 = 0;
            r8 = r15.iterator();
        L_0x008a:
            r21 = r8.hasNext();
            if (r21 == 0) goto L_0x00b7;
        L_0x0090:
            r13 = r8.next();
            r13 = (com.firebase.client.core.view.QuerySpec) r13;
            r0 = r24;
            r0 = com.firebase.client.core.SyncTree.this;
            r21 = r0;
            r21 = r21.persistenceManager;
            r0 = r24;
            r0 = r0.val$query;
            r22 = r0;
            r21.setQueryInactive(r22);
            if (r17 != 0) goto L_0x00b1;
        L_0x00ab:
            r21 = r13.loadsAllData();
            if (r21 == 0) goto L_0x00b4;
        L_0x00b1:
            r17 = 1;
        L_0x00b3:
            goto L_0x008a;
        L_0x00b4:
            r17 = 0;
            goto L_0x00b3;
        L_0x00b7:
            r0 = r24;
            r0 = com.firebase.client.core.SyncTree.this;
            r21 = r0;
            r7 = r21.syncPointTree;
            r21 = r7.getValue();
            if (r21 == 0) goto L_0x0174;
        L_0x00c7:
            r21 = r7.getValue();
            r21 = (com.firebase.client.core.SyncPoint) r21;
            r21 = r21.hasCompleteView();
            if (r21 == 0) goto L_0x0174;
        L_0x00d3:
            r6 = 1;
        L_0x00d4:
            r8 = r12.iterator();
        L_0x00d8:
            r21 = r8.hasNext();
            if (r21 == 0) goto L_0x0105;
        L_0x00de:
            r4 = r8.next();
            r4 = (com.firebase.client.snapshot.ChildKey) r4;
            r7 = r7.getChild(r4);
            if (r6 != 0) goto L_0x00fc;
        L_0x00ea:
            r21 = r7.getValue();
            if (r21 == 0) goto L_0x0177;
        L_0x00f0:
            r21 = r7.getValue();
            r21 = (com.firebase.client.core.SyncPoint) r21;
            r21 = r21.hasCompleteView();
            if (r21 == 0) goto L_0x0177;
        L_0x00fc:
            r6 = 1;
        L_0x00fd:
            if (r6 != 0) goto L_0x0105;
        L_0x00ff:
            r21 = r7.isEmpty();
            if (r21 == 0) goto L_0x00d8;
        L_0x0105:
            if (r17 == 0) goto L_0x0179;
        L_0x0107:
            if (r6 != 0) goto L_0x0179;
        L_0x0109:
            r0 = r24;
            r0 = com.firebase.client.core.SyncTree.this;
            r21 = r0;
            r21 = r21.syncPointTree;
            r0 = r21;
            r18 = r0.subtree(r12);
            r21 = r18.isEmpty();
            if (r21 != 0) goto L_0x0179;
        L_0x011f:
            r0 = r24;
            r0 = com.firebase.client.core.SyncTree.this;
            r21 = r0;
            r0 = r21;
            r1 = r18;
            r11 = r0.collectDistinctViewsForSubTree(r1);
            r8 = r11.iterator();
        L_0x0131:
            r21 = r8.hasNext();
            if (r21 == 0) goto L_0x0179;
        L_0x0137:
            r20 = r8.next();
            r20 = (com.firebase.client.core.view.View) r20;
            r5 = new com.firebase.client.core.SyncTree$ListenContainer;
            r0 = r24;
            r0 = com.firebase.client.core.SyncTree.this;
            r21 = r0;
            r0 = r21;
            r1 = r20;
            r5.<init>(r1);
            r10 = r20.getQuery();
            r0 = r24;
            r0 = com.firebase.client.core.SyncTree.this;
            r21 = r0;
            r21 = r21.listenProvider;
            r0 = r24;
            r0 = com.firebase.client.core.SyncTree.this;
            r22 = r0;
            r0 = r22;
            r22 = r0.queryForListening(r10);
            r23 = r5.tag;
            r0 = r21;
            r1 = r22;
            r2 = r23;
            r0.startListening(r1, r2, r5, r5);
            goto L_0x0131;
        L_0x0174:
            r6 = 0;
            goto L_0x00d4;
        L_0x0177:
            r6 = 0;
            goto L_0x00fd;
        L_0x0179:
            if (r6 != 0) goto L_0x01aa;
        L_0x017b:
            r21 = r15.isEmpty();
            if (r21 != 0) goto L_0x01aa;
        L_0x0181:
            r0 = r24;
            r0 = r0.val$cancelError;
            r21 = r0;
            if (r21 != 0) goto L_0x01aa;
        L_0x0189:
            if (r17 == 0) goto L_0x01b6;
        L_0x018b:
            r0 = r24;
            r0 = com.firebase.client.core.SyncTree.this;
            r21 = r0;
            r21 = r21.listenProvider;
            r0 = r24;
            r0 = com.firebase.client.core.SyncTree.this;
            r22 = r0;
            r0 = r24;
            r0 = r0.val$query;
            r23 = r0;
            r22 = r22.queryForListening(r23);
            r23 = 0;
            r21.stopListening(r22, r23);
        L_0x01aa:
            r0 = r24;
            r0 = com.firebase.client.core.SyncTree.this;
            r21 = r0;
            r0 = r21;
            r0.removeTags(r15);
        L_0x01b5:
            return r3;
        L_0x01b6:
            r8 = r15.iterator();
        L_0x01ba:
            r21 = r8.hasNext();
            if (r21 == 0) goto L_0x01aa;
        L_0x01c0:
            r14 = r8.next();
            r14 = (com.firebase.client.core.view.QuerySpec) r14;
            r0 = r24;
            r0 = com.firebase.client.core.SyncTree.this;
            r21 = r0;
            r0 = r21;
            r19 = r0.tagForQuery(r14);
            r21 = $assertionsDisabled;
            if (r21 != 0) goto L_0x01de;
        L_0x01d6:
            if (r19 != 0) goto L_0x01de;
        L_0x01d8:
            r21 = new java.lang.AssertionError;
            r21.<init>();
            throw r21;
        L_0x01de:
            r0 = r24;
            r0 = com.firebase.client.core.SyncTree.this;
            r21 = r0;
            r21 = r21.listenProvider;
            r0 = r24;
            r0 = com.firebase.client.core.SyncTree.this;
            r22 = r0;
            r0 = r22;
            r22 = r0.queryForListening(r14);
            r0 = r21;
            r1 = r22;
            r2 = r19;
            r0.stopListening(r1, r2);
            goto L_0x01ba;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.firebase.client.core.SyncTree.12.call():java.util.List<com.firebase.client.core.view.Event>");
        }
    }

    /* renamed from: com.firebase.client.core.SyncTree.1 */
    class C05591 implements Callable<List<? extends Event>> {
        final /* synthetic */ Node val$newData;
        final /* synthetic */ Node val$newDataUnresolved;
        final /* synthetic */ Path val$path;
        final /* synthetic */ boolean val$persist;
        final /* synthetic */ boolean val$visible;
        final /* synthetic */ long val$writeId;

        C05591(boolean z, Path path, Node node, long j, Node node2, boolean z2) {
            this.val$persist = z;
            this.val$path = path;
            this.val$newDataUnresolved = node;
            this.val$writeId = j;
            this.val$newData = node2;
            this.val$visible = z2;
        }

        public List<? extends Event> call() {
            if (this.val$persist) {
                SyncTree.this.persistenceManager.saveUserOverwrite(this.val$path, this.val$newDataUnresolved, this.val$writeId);
            }
            SyncTree.this.pendingWriteTree.addOverwrite(this.val$path, this.val$newData, Long.valueOf(this.val$writeId), this.val$visible);
            if (this.val$visible) {
                return SyncTree.this.applyOperationToSyncPoints(new Overwrite(OperationSource.USER, this.val$path, this.val$newData));
            }
            return Collections.emptyList();
        }
    }

    /* renamed from: com.firebase.client.core.SyncTree.2 */
    class C05602 implements Callable<List<? extends Event>> {
        final /* synthetic */ CompoundWrite val$children;
        final /* synthetic */ Path val$path;
        final /* synthetic */ boolean val$persist;
        final /* synthetic */ CompoundWrite val$unresolvedChildren;
        final /* synthetic */ long val$writeId;

        C05602(boolean z, Path path, CompoundWrite compoundWrite, long j, CompoundWrite compoundWrite2) {
            this.val$persist = z;
            this.val$path = path;
            this.val$unresolvedChildren = compoundWrite;
            this.val$writeId = j;
            this.val$children = compoundWrite2;
        }

        public List<? extends Event> call() throws Exception {
            if (this.val$persist) {
                SyncTree.this.persistenceManager.saveUserMerge(this.val$path, this.val$unresolvedChildren, this.val$writeId);
            }
            SyncTree.this.pendingWriteTree.addMerge(this.val$path, this.val$children, Long.valueOf(this.val$writeId));
            return SyncTree.this.applyOperationToSyncPoints(new Merge(OperationSource.USER, this.val$path, this.val$children));
        }
    }

    /* renamed from: com.firebase.client.core.SyncTree.3 */
    class C05613 implements Callable<List<? extends Event>> {
        final /* synthetic */ boolean val$persist;
        final /* synthetic */ boolean val$revert;
        final /* synthetic */ Clock val$serverClock;
        final /* synthetic */ long val$writeId;

        C05613(boolean z, long j, boolean z2, Clock clock) {
            this.val$persist = z;
            this.val$writeId = j;
            this.val$revert = z2;
            this.val$serverClock = clock;
        }

        public List<? extends Event> call() {
            if (this.val$persist) {
                SyncTree.this.persistenceManager.removeUserWrite(this.val$writeId);
            }
            UserWriteRecord write = SyncTree.this.pendingWriteTree.getWrite(this.val$writeId);
            boolean needToReevaluate = SyncTree.this.pendingWriteTree.removeWrite(this.val$writeId);
            if (write.isVisible() && !this.val$revert) {
                Map<String, Object> serverValues = ServerValues.generateServerValues(this.val$serverClock);
                if (write.isOverwrite()) {
                    SyncTree.this.persistenceManager.applyUserWriteToServerCache(write.getPath(), ServerValues.resolveDeferredValueSnapshot(write.getOverwrite(), serverValues));
                } else {
                    SyncTree.this.persistenceManager.applyUserWriteToServerCache(write.getPath(), ServerValues.resolveDeferredValueMerge(write.getMerge(), serverValues));
                }
            }
            if (!needToReevaluate) {
                return Collections.emptyList();
            }
            ImmutableTree<Boolean> affectedTree = ImmutableTree.emptyInstance();
            if (write.isOverwrite()) {
                affectedTree = affectedTree.set(Path.getEmptyPath(), Boolean.valueOf(true));
            } else {
                Iterator i$ = write.getMerge().iterator();
                while (i$.hasNext()) {
                    affectedTree = affectedTree.set((Path) ((Entry) i$.next()).getKey(), Boolean.valueOf(true));
                }
            }
            return SyncTree.this.applyOperationToSyncPoints(new AckUserWrite(write.getPath(), affectedTree, this.val$revert));
        }
    }

    /* renamed from: com.firebase.client.core.SyncTree.4 */
    class C05624 implements Callable<List<? extends Event>> {
        C05624() {
        }

        public List<? extends Event> call() throws Exception {
            SyncTree.this.persistenceManager.removeAllUserWrites();
            if (SyncTree.this.pendingWriteTree.purgeAllWrites().isEmpty()) {
                return Collections.emptyList();
            }
            return SyncTree.this.applyOperationToSyncPoints(new AckUserWrite(Path.getEmptyPath(), new ImmutableTree(Boolean.valueOf(true)), true));
        }
    }

    /* renamed from: com.firebase.client.core.SyncTree.5 */
    class C05635 implements Callable<List<? extends Event>> {
        final /* synthetic */ Node val$newData;
        final /* synthetic */ Path val$path;

        C05635(Path path, Node node) {
            this.val$path = path;
            this.val$newData = node;
        }

        public List<? extends Event> call() {
            SyncTree.this.persistenceManager.updateServerCache(QuerySpec.defaultQueryAtPath(this.val$path), this.val$newData);
            return SyncTree.this.applyOperationToSyncPoints(new Overwrite(OperationSource.SERVER, this.val$path, this.val$newData));
        }
    }

    /* renamed from: com.firebase.client.core.SyncTree.6 */
    class C05646 implements Callable<List<? extends Event>> {
        final /* synthetic */ Map val$changedChildren;
        final /* synthetic */ Path val$path;

        C05646(Map map, Path path) {
            this.val$changedChildren = map;
            this.val$path = path;
        }

        public List<? extends Event> call() {
            CompoundWrite merge = CompoundWrite.fromPathMerge(this.val$changedChildren);
            SyncTree.this.persistenceManager.updateServerCache(this.val$path, merge);
            return SyncTree.this.applyOperationToSyncPoints(new Merge(OperationSource.SERVER, this.val$path, merge));
        }
    }

    /* renamed from: com.firebase.client.core.SyncTree.7 */
    class C05657 implements Callable<List<? extends Event>> {
        final /* synthetic */ Path val$path;

        C05657(Path path) {
            this.val$path = path;
        }

        public List<? extends Event> call() {
            SyncTree.this.persistenceManager.setQueryComplete(QuerySpec.defaultQueryAtPath(this.val$path));
            return SyncTree.this.applyOperationToSyncPoints(new ListenComplete(OperationSource.SERVER, this.val$path));
        }
    }

    /* renamed from: com.firebase.client.core.SyncTree.8 */
    class C05668 implements Callable<List<? extends Event>> {
        final /* synthetic */ Tag val$tag;

        C05668(Tag tag) {
            this.val$tag = tag;
        }

        public List<? extends Event> call() {
            QuerySpec query = SyncTree.this.queryForTag(this.val$tag);
            if (query == null) {
                return Collections.emptyList();
            }
            SyncTree.this.persistenceManager.setQueryComplete(query);
            return SyncTree.this.applyTaggedOperation(query, new ListenComplete(OperationSource.forServerTaggedQuery(query.getParams()), Path.getEmptyPath()));
        }
    }

    /* renamed from: com.firebase.client.core.SyncTree.9 */
    class C05679 implements Callable<List<? extends Event>> {
        final /* synthetic */ Path val$path;
        final /* synthetic */ Node val$snap;
        final /* synthetic */ Tag val$tag;

        C05679(Tag tag, Path path, Node node) {
            this.val$tag = tag;
            this.val$path = path;
            this.val$snap = node;
        }

        public List<? extends Event> call() {
            QuerySpec query = SyncTree.this.queryForTag(this.val$tag);
            if (query == null) {
                return Collections.emptyList();
            }
            Path relativePath = Path.getRelative(query.getPath(), this.val$path);
            SyncTree.this.persistenceManager.updateServerCache(relativePath.isEmpty() ? query : QuerySpec.defaultQueryAtPath(this.val$path), this.val$snap);
            return SyncTree.this.applyTaggedOperation(query, new Overwrite(OperationSource.forServerTaggedQuery(query.getParams()), relativePath, this.val$snap));
        }
    }

    public interface CompletionListener {
        List<? extends Event> onListenComplete(FirebaseError firebaseError);
    }

    public interface ListenProvider {
        void startListening(QuerySpec querySpec, Tag tag, SyncTreeHash syncTreeHash, CompletionListener completionListener);

        void stopListening(QuerySpec querySpec, Tag tag);
    }

    public interface SyncTreeHash {
        CompoundHash getCompoundHash();

        String getSimpleHash();

        boolean shouldIncludeCompoundHash();
    }

    private static class KeepSyncedEventRegistration extends EventRegistration {
        private QuerySpec spec;

        public KeepSyncedEventRegistration(@NotNull QuerySpec spec) {
            this.spec = spec;
        }

        public boolean respondsTo(EventType eventType) {
            return SyncTree.$assertionsDisabled;
        }

        public DataEvent createEvent(Change change, QuerySpec query) {
            return null;
        }

        public void fireEvent(DataEvent dataEvent) {
        }

        public void fireCancelEvent(FirebaseError error) {
        }

        public EventRegistration clone(QuerySpec newQuery) {
            return new KeepSyncedEventRegistration(newQuery);
        }

        public boolean isSameListener(EventRegistration other) {
            return other instanceof KeepSyncedEventRegistration;
        }

        @NotNull
        public QuerySpec getQuerySpec() {
            return this.spec;
        }

        public boolean equals(Object other) {
            return ((other instanceof KeepSyncedEventRegistration) && ((KeepSyncedEventRegistration) other).spec.equals(this.spec)) ? true : SyncTree.$assertionsDisabled;
        }

        public int hashCode() {
            return this.spec.hashCode();
        }
    }

    private class ListenContainer implements SyncTreeHash, CompletionListener {
        private final Tag tag;
        private final View view;

        public ListenContainer(View view) {
            this.view = view;
            this.tag = SyncTree.this.tagForQuery(view.getQuery());
        }

        public CompoundHash getCompoundHash() {
            return CompoundHash.fromNode(this.view.getServerCache());
        }

        public String getSimpleHash() {
            return this.view.getServerCache().getHash();
        }

        public boolean shouldIncludeCompoundHash() {
            return NodeSizeEstimator.estimateSerializedNodeSize(this.view.getServerCache()) > SyncTree.SIZE_THRESHOLD_FOR_COMPOUND_HASH ? true : SyncTree.$assertionsDisabled;
        }

        public List<? extends Event> onListenComplete(FirebaseError error) {
            if (error == null) {
                QuerySpec query = this.view.getQuery();
                if (this.tag != null) {
                    return SyncTree.this.applyTaggedListenComplete(this.tag);
                }
                return SyncTree.this.applyListenComplete(query.getPath());
            }
            SyncTree.this.logger.warn("Listen at " + this.view.getQuery().getPath() + " failed: " + error.toString());
            return SyncTree.this.removeAllEventRegistrations(this.view.getQuery(), error);
        }
    }

    /* renamed from: com.firebase.client.core.SyncTree.14 */
    class AnonymousClass14 extends NodeVisitor<ChildKey, ImmutableTree<SyncPoint>> {
        final /* synthetic */ List val$events;
        final /* synthetic */ Operation val$operation;
        final /* synthetic */ Node val$resolvedServerCache;
        final /* synthetic */ WriteTreeRef val$writesCache;

        AnonymousClass14(Node node, WriteTreeRef writeTreeRef, Operation operation, List list) {
            this.val$resolvedServerCache = node;
            this.val$writesCache = writeTreeRef;
            this.val$operation = operation;
            this.val$events = list;
        }

        public void visitEntry(ChildKey key, ImmutableTree<SyncPoint> childTree) {
            Node childServerCache = null;
            if (this.val$resolvedServerCache != null) {
                childServerCache = this.val$resolvedServerCache.getImmediateChild(key);
            }
            WriteTreeRef childWritesCache = this.val$writesCache.child(key);
            Operation childOperation = this.val$operation.operationForChild(key);
            if (childOperation != null) {
                this.val$events.addAll(SyncTree.this.applyOperationDescendantsHelper(childOperation, childTree, childServerCache, childWritesCache));
            }
        }
    }

    static {
        $assertionsDisabled = !SyncTree.class.desiredAssertionStatus() ? true : $assertionsDisabled;
    }

    public SyncTree(Context context, PersistenceManager persistenceManager, ListenProvider listenProvider) {
        this.nextQueryTag = 1;
        this.syncPointTree = ImmutableTree.emptyInstance();
        this.pendingWriteTree = new WriteTree();
        this.tagToQueryMap = new HashMap();
        this.queryToTagMap = new HashMap();
        this.keepSyncedQueries = new HashSet();
        this.listenProvider = listenProvider;
        this.persistenceManager = persistenceManager;
        this.logger = context.getLogger("SyncTree");
    }

    public boolean isEmpty() {
        return this.syncPointTree.isEmpty();
    }

    public List<? extends Event> applyUserOverwrite(Path path, Node newDataUnresolved, Node newData, long writeId, boolean visible, boolean persist) {
        boolean z = (visible || !persist) ? true : $assertionsDisabled;
        Utilities.hardAssert(z, "We shouldn't be persisting non-visible writes.");
        return (List) this.persistenceManager.runInTransaction(new C05591(persist, path, newDataUnresolved, writeId, newData, visible));
    }

    public List<? extends Event> applyUserMerge(Path path, CompoundWrite unresolvedChildren, CompoundWrite children, long writeId, boolean persist) {
        return (List) this.persistenceManager.runInTransaction(new C05602(persist, path, unresolvedChildren, writeId, children));
    }

    public List<? extends Event> ackUserWrite(long writeId, boolean revert, boolean persist, Clock serverClock) {
        return (List) this.persistenceManager.runInTransaction(new C05613(persist, writeId, revert, serverClock));
    }

    public List<? extends Event> removeAllWrites() {
        return (List) this.persistenceManager.runInTransaction(new C05624());
    }

    public List<? extends Event> applyServerOverwrite(Path path, Node newData) {
        return (List) this.persistenceManager.runInTransaction(new C05635(path, newData));
    }

    public List<? extends Event> applyServerMerge(Path path, Map<Path, Node> changedChildren) {
        return (List) this.persistenceManager.runInTransaction(new C05646(changedChildren, path));
    }

    public List<? extends Event> applyServerRangeMerges(Path path, List<RangeMerge> rangeMerges) {
        SyncPoint syncPoint = (SyncPoint) this.syncPointTree.get(path);
        if (syncPoint == null) {
            return Collections.emptyList();
        }
        View view = syncPoint.getCompleteView();
        if (view == null) {
            return Collections.emptyList();
        }
        Node serverNode = view.getServerCache();
        for (RangeMerge merge : rangeMerges) {
            serverNode = merge.applyTo(serverNode);
        }
        return applyServerOverwrite(path, serverNode);
    }

    public List<? extends Event> applyTaggedRangeMerges(Path path, List<RangeMerge> rangeMerges, Tag tag) {
        QuerySpec query = queryForTag(tag);
        if (query == null) {
            return Collections.emptyList();
        }
        if ($assertionsDisabled || path.equals(query.getPath())) {
            SyncPoint syncPoint = (SyncPoint) this.syncPointTree.get(query.getPath());
            if ($assertionsDisabled || syncPoint != null) {
                View view = syncPoint.viewForQuery(query);
                if ($assertionsDisabled || view != null) {
                    Node serverNode = view.getServerCache();
                    for (RangeMerge merge : rangeMerges) {
                        serverNode = merge.applyTo(serverNode);
                    }
                    return applyTaggedQueryOverwrite(path, serverNode, tag);
                }
                throw new AssertionError("Missing view for query tag that we're tracking");
            }
            throw new AssertionError("Missing sync point for query tag that we're tracking");
        }
        throw new AssertionError();
    }

    public List<? extends Event> applyListenComplete(Path path) {
        return (List) this.persistenceManager.runInTransaction(new C05657(path));
    }

    public List<? extends Event> applyTaggedListenComplete(Tag tag) {
        return (List) this.persistenceManager.runInTransaction(new C05668(tag));
    }

    private List<? extends Event> applyTaggedOperation(QuerySpec query, Operation operation) {
        Path queryPath = query.getPath();
        SyncPoint syncPoint = (SyncPoint) this.syncPointTree.get(queryPath);
        if ($assertionsDisabled || syncPoint != null) {
            return syncPoint.applyOperation(operation, this.pendingWriteTree.childWrites(queryPath), null);
        }
        throw new AssertionError("Missing sync point for query tag that we're tracking");
    }

    public List<? extends Event> applyTaggedQueryOverwrite(Path path, Node snap, Tag tag) {
        return (List) this.persistenceManager.runInTransaction(new C05679(tag, path, snap));
    }

    public List<? extends Event> applyTaggedQueryMerge(Path path, Map<Path, Node> changedChildren, Tag tag) {
        return (List) this.persistenceManager.runInTransaction(new AnonymousClass10(tag, path, changedChildren));
    }

    public List<? extends Event> addEventRegistration(@NotNull EventRegistration eventRegistration) {
        return (List) this.persistenceManager.runInTransaction(new AnonymousClass11(eventRegistration));
    }

    public List<Event> removeEventRegistration(@NotNull EventRegistration eventRegistration) {
        return removeEventRegistration(eventRegistration.getQuerySpec(), eventRegistration, null);
    }

    public List<Event> removeAllEventRegistrations(@NotNull QuerySpec query, @NotNull FirebaseError error) {
        return removeEventRegistration(query, null, error);
    }

    private List<Event> removeEventRegistration(@NotNull QuerySpec query, @Nullable EventRegistration eventRegistration, @Nullable FirebaseError cancelError) {
        return (List) this.persistenceManager.runInTransaction(new AnonymousClass12(query, eventRegistration, cancelError));
    }

    public void keepSynced(QuerySpec query, boolean keep) {
        if (keep && !this.keepSyncedQueries.contains(query)) {
            addEventRegistration(new KeepSyncedEventRegistration(query));
            this.keepSyncedQueries.add(query);
        } else if (!keep && this.keepSyncedQueries.contains(query)) {
            removeEventRegistration(new KeepSyncedEventRegistration(query));
            this.keepSyncedQueries.remove(query);
        }
    }

    private List<View> collectDistinctViewsForSubTree(ImmutableTree<SyncPoint> subtree) {
        ArrayList<View> accumulator = new ArrayList();
        collectDistinctViewsForSubTree(subtree, accumulator);
        return accumulator;
    }

    private void collectDistinctViewsForSubTree(ImmutableTree<SyncPoint> subtree, List<View> accumulator) {
        SyncPoint maybeSyncPoint = (SyncPoint) subtree.getValue();
        if (maybeSyncPoint == null || !maybeSyncPoint.hasCompleteView()) {
            if (maybeSyncPoint != null) {
                accumulator.addAll(maybeSyncPoint.getQueryViews());
            }
            Iterator i$ = subtree.getChildren().iterator();
            while (i$.hasNext()) {
                collectDistinctViewsForSubTree((ImmutableTree) ((Entry) i$.next()).getValue(), accumulator);
            }
            return;
        }
        accumulator.add(maybeSyncPoint.getCompleteView());
    }

    private void removeTags(List<QuerySpec> queries) {
        for (QuerySpec removedQuery : queries) {
            if (!removedQuery.loadsAllData()) {
                Tag tag = tagForQuery(removedQuery);
                if ($assertionsDisabled || tag != null) {
                    this.queryToTagMap.remove(removedQuery);
                    this.tagToQueryMap.remove(tag);
                } else {
                    throw new AssertionError();
                }
            }
        }
    }

    private QuerySpec queryForListening(QuerySpec query) {
        if (!query.loadsAllData() || query.isDefault()) {
            return query;
        }
        return QuerySpec.defaultQueryAtPath(query.getPath());
    }

    private void setupListener(QuerySpec query, View view) {
        Path path = query.getPath();
        Tag tag = tagForQuery(query);
        ListenContainer container = new ListenContainer(view);
        this.listenProvider.startListening(queryForListening(query), tag, container, container);
        ImmutableTree<SyncPoint> subtree = this.syncPointTree.subtree(path);
        if (tag == null) {
            subtree.foreach(new TreeVisitor<SyncPoint, Void>() {
                public Void onNodeValue(Path relativePath, SyncPoint maybeChildSyncPoint, Void accum) {
                    if (relativePath.isEmpty() || !maybeChildSyncPoint.hasCompleteView()) {
                        for (View syncPointView : maybeChildSyncPoint.getQueryViews()) {
                            QuerySpec childQuery = syncPointView.getQuery();
                            SyncTree.this.listenProvider.stopListening(SyncTree.this.queryForListening(childQuery), SyncTree.this.tagForQuery(childQuery));
                        }
                    } else {
                        QuerySpec query = maybeChildSyncPoint.getCompleteView().getQuery();
                        SyncTree.this.listenProvider.stopListening(SyncTree.this.queryForListening(query), SyncTree.this.tagForQuery(query));
                    }
                    return null;
                }
            });
        } else if (!$assertionsDisabled && ((SyncPoint) subtree.getValue()).hasCompleteView()) {
            throw new AssertionError("If we're adding a query, it shouldn't be shadowed");
        }
    }

    private QuerySpec queryForTag(Tag tag) {
        return (QuerySpec) this.tagToQueryMap.get(tag);
    }

    private Tag tagForQuery(QuerySpec query) {
        return (Tag) this.queryToTagMap.get(query);
    }

    public Node calcCompleteEventCache(Path path, List<Long> writeIdsToExclude) {
        ImmutableTree<SyncPoint> tree = this.syncPointTree;
        SyncPoint currentSyncPoint = (SyncPoint) tree.getValue();
        Node serverCache = null;
        Path pathToFollow = path;
        Path pathSoFar = Path.getEmptyPath();
        do {
            ChildKey front = pathToFollow.getFront();
            pathToFollow = pathToFollow.popFront();
            pathSoFar = pathSoFar.child(front);
            Path relativePath = Path.getRelative(pathSoFar, path);
            tree = front != null ? tree.getChild(front) : ImmutableTree.emptyInstance();
            currentSyncPoint = (SyncPoint) tree.getValue();
            if (currentSyncPoint != null) {
                serverCache = currentSyncPoint.getCompleteServerCache(relativePath);
            }
            if (pathToFollow.isEmpty()) {
                break;
            }
        } while (serverCache == null);
        return this.pendingWriteTree.calcCompleteEventCache(path, serverCache, writeIdsToExclude, true);
    }

    private Tag getNextQueryTag() {
        long j = this.nextQueryTag;
        this.nextQueryTag = 1 + j;
        return new Tag(j);
    }

    private List<Event> applyOperationToSyncPoints(Operation operation) {
        return applyOperationHelper(operation, this.syncPointTree, null, this.pendingWriteTree.childWrites(Path.getEmptyPath()));
    }

    private List<Event> applyOperationHelper(Operation operation, ImmutableTree<SyncPoint> syncPointTree, Node serverCache, WriteTreeRef writesCache) {
        if (operation.getPath().isEmpty()) {
            return applyOperationDescendantsHelper(operation, syncPointTree, serverCache, writesCache);
        }
        SyncPoint syncPoint = (SyncPoint) syncPointTree.getValue();
        if (serverCache == null && syncPoint != null) {
            serverCache = syncPoint.getCompleteServerCache(Path.getEmptyPath());
        }
        List<Event> events = new ArrayList();
        ChildKey childKey = operation.getPath().getFront();
        Operation childOperation = operation.operationForChild(childKey);
        ImmutableTree<SyncPoint> childTree = (ImmutableTree) syncPointTree.getChildren().get(childKey);
        if (!(childTree == null || childOperation == null)) {
            events.addAll(applyOperationHelper(childOperation, childTree, serverCache != null ? serverCache.getImmediateChild(childKey) : null, writesCache.child(childKey)));
        }
        if (syncPoint == null) {
            return events;
        }
        events.addAll(syncPoint.applyOperation(operation, writesCache, serverCache));
        return events;
    }

    private List<Event> applyOperationDescendantsHelper(Operation operation, ImmutableTree<SyncPoint> syncPointTree, Node serverCache, WriteTreeRef writesCache) {
        Node resolvedServerCache;
        SyncPoint syncPoint = (SyncPoint) syncPointTree.getValue();
        if (serverCache != null || syncPoint == null) {
            resolvedServerCache = serverCache;
        } else {
            resolvedServerCache = syncPoint.getCompleteServerCache(Path.getEmptyPath());
        }
        List<Event> events = new ArrayList();
        syncPointTree.getChildren().inOrderTraversal(new AnonymousClass14(resolvedServerCache, writesCache, operation, events));
        if (syncPoint != null) {
            events.addAll(syncPoint.applyOperation(operation, writesCache, resolvedServerCache));
        }
        return events;
    }

    ImmutableTree<SyncPoint> getSyncPointTree() {
        return this.syncPointTree;
    }
}
