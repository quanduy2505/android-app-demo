package com.firebase.client.core;

import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.Firebase.CompletionListener;
import com.firebase.client.FirebaseApp;
import com.firebase.client.FirebaseError;
import com.firebase.client.FirebaseException;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;
import com.firebase.client.Transaction.Handler;
import com.firebase.client.Transaction.Result;
import com.firebase.client.ValueEventListener;
import com.firebase.client.annotations.NotNull;
import com.firebase.client.authentication.AuthenticationManager;
import com.firebase.client.core.PersistentConnection.Delegate;
import com.firebase.client.core.SparseSnapshotTree.SparseSnapshotTreeVisitor;
import com.firebase.client.core.SyncTree.ListenProvider;
import com.firebase.client.core.SyncTree.SyncTreeHash;
import com.firebase.client.core.persistence.NoopPersistenceManager;
import com.firebase.client.core.persistence.PersistenceManager;
import com.firebase.client.core.utilities.Tree;
import com.firebase.client.core.utilities.Tree.TreeFilter;
import com.firebase.client.core.utilities.Tree.TreeVisitor;
import com.firebase.client.core.view.Event;
import com.firebase.client.core.view.EventRaiser;
import com.firebase.client.core.view.QuerySpec;
import com.firebase.client.snapshot.ChildKey;
import com.firebase.client.snapshot.EmptyNode;
import com.firebase.client.snapshot.IndexedNode;
import com.firebase.client.snapshot.Node;
import com.firebase.client.snapshot.NodeUtilities;
import com.firebase.client.utilities.DefaultClock;
import com.firebase.client.utilities.LogWrapper;
import com.firebase.client.utilities.OffsetClock;
import com.firebase.client.utilities.Utilities;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Repo implements Delegate {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static final int TRANSACTION_MAX_RETRIES = 25;
    private static final String TRANSACTION_OVERRIDE_BY_SET = "overriddenBySet";
    private static final String TRANSACTION_TOO_MANY_RETRIES = "maxretries";
    private FirebaseApp app;
    private final AuthenticationManager authenticationManager;
    private final PersistentConnection connection;
    private final Context ctx;
    private final LogWrapper dataLogger;
    public long dataUpdateCount;
    private final EventRaiser eventRaiser;
    private boolean hijackHash;
    private SnapshotHolder infoData;
    private SyncTree infoSyncTree;
    private boolean loggedTransactionPersistenceWarning;
    private long nextWriteId;
    private SparseSnapshotTree onDisconnect;
    private final LogWrapper operationLogger;
    private final RepoInfo repoInfo;
    private final OffsetClock serverClock;
    private SyncTree serverSyncTree;
    private final LogWrapper transactionLogger;
    private long transactionOrder;
    private Tree<List<TransactionData>> transactionQueueTree;

    /* renamed from: com.firebase.client.core.Repo.13 */
    class AnonymousClass13 implements Runnable {
        final /* synthetic */ Handler val$handler;
        final /* synthetic */ FirebaseError val$innerClassError;
        final /* synthetic */ DataSnapshot val$snap;

        AnonymousClass13(Handler handler, FirebaseError firebaseError, DataSnapshot dataSnapshot) {
            this.val$handler = handler;
            this.val$innerClassError = firebaseError;
            this.val$snap = dataSnapshot;
        }

        public void run() {
            this.val$handler.onComplete(this.val$innerClassError, Repo.$assertionsDisabled, this.val$snap);
        }
    }

    /* renamed from: com.firebase.client.core.Repo.17 */
    class AnonymousClass17 implements Runnable {
        final /* synthetic */ TransactionData val$transaction;

        AnonymousClass17(TransactionData transactionData) {
            this.val$transaction = transactionData;
        }

        public void run() {
            Repo.this.removeEventCallback(new ValueEventRegistration(Repo.this, this.val$transaction.outstandingListener, QuerySpec.defaultQueryAtPath(this.val$transaction.path)));
        }
    }

    /* renamed from: com.firebase.client.core.Repo.18 */
    class AnonymousClass18 implements Runnable {
        final /* synthetic */ FirebaseError val$callbackError;
        final /* synthetic */ DataSnapshot val$snapshot;
        final /* synthetic */ TransactionData val$transaction;

        AnonymousClass18(TransactionData transactionData, FirebaseError firebaseError, DataSnapshot dataSnapshot) {
            this.val$transaction = transactionData;
            this.val$callbackError = firebaseError;
            this.val$snapshot = dataSnapshot;
        }

        public void run() {
            this.val$transaction.handler.onComplete(this.val$callbackError, Repo.$assertionsDisabled, this.val$snapshot);
        }
    }

    /* renamed from: com.firebase.client.core.Repo.1 */
    class C05521 implements Runnable {
        C05521() {
        }

        public void run() {
            Repo.this.deferredInitialization();
        }
    }

    /* renamed from: com.firebase.client.core.Repo.22 */
    class AnonymousClass22 implements Runnable {
        final /* synthetic */ FirebaseError val$abortError;
        final /* synthetic */ TransactionData val$transaction;

        AnonymousClass22(TransactionData transactionData, FirebaseError firebaseError) {
            this.val$transaction = transactionData;
            this.val$abortError = firebaseError;
        }

        public void run() {
            this.val$transaction.handler.onComplete(this.val$abortError, Repo.$assertionsDisabled, null);
        }
    }

    /* renamed from: com.firebase.client.core.Repo.5 */
    class C05545 implements Runnable {
        final /* synthetic */ FirebaseError val$error;
        final /* synthetic */ CompletionListener val$onComplete;
        final /* synthetic */ Firebase val$ref;

        C05545(CompletionListener completionListener, FirebaseError firebaseError, Firebase firebase) {
            this.val$onComplete = completionListener;
            this.val$error = firebaseError;
            this.val$ref = firebase;
        }

        public void run() {
            this.val$onComplete.onComplete(this.val$error, this.val$ref);
        }
    }

    private static class TransactionData implements Comparable<TransactionData> {
        private FirebaseError abortReason;
        private boolean applyLocally;
        private Node currentInputSnapshot;
        private Node currentOutputSnapshotRaw;
        private Node currentOutputSnapshotResolved;
        private long currentWriteId;
        private Handler handler;
        private long order;
        private ValueEventListener outstandingListener;
        private Path path;
        private int retryCount;
        private TransactionStatus status;

        private TransactionData(Path path, Handler handler, ValueEventListener outstandingListener, TransactionStatus status, boolean applyLocally, long order) {
            this.path = path;
            this.handler = handler;
            this.outstandingListener = outstandingListener;
            this.status = status;
            this.retryCount = 0;
            this.applyLocally = applyLocally;
            this.order = order;
            this.abortReason = null;
            this.currentInputSnapshot = null;
            this.currentOutputSnapshotRaw = null;
            this.currentOutputSnapshotResolved = null;
        }

        public int compareTo(TransactionData o) {
            if (this.order < o.order) {
                return -1;
            }
            if (this.order == o.order) {
                return 0;
            }
            return 1;
        }
    }

    private enum TransactionStatus {
        INITIALIZING,
        RUN,
        SENT,
        COMPLETED,
        SENT_NEEDS_ABORT,
        NEEDS_ABORT
    }

    /* renamed from: com.firebase.client.core.Repo.10 */
    class AnonymousClass10 implements CompletionListener {
        final /* synthetic */ CompletionListener val$onComplete;
        final /* synthetic */ Path val$path;

        AnonymousClass10(Path path, CompletionListener completionListener) {
            this.val$path = path;
            this.val$onComplete = completionListener;
        }

        public void onComplete(FirebaseError error, Firebase ref) {
            if (error == null) {
                Repo.this.onDisconnect.forget(this.val$path);
            }
            Repo.this.callOnComplete(this.val$onComplete, error, this.val$path);
        }
    }

    /* renamed from: com.firebase.client.core.Repo.11 */
    class AnonymousClass11 implements SparseSnapshotTreeVisitor {
        final /* synthetic */ List val$events;

        AnonymousClass11(List list) {
            this.val$events = list;
        }

        public void visitTree(Path prefixPath, Node node) {
            this.val$events.addAll(Repo.this.serverSyncTree.applyServerOverwrite(prefixPath, node));
            Repo.this.rerunTransactions(Repo.this.abortTransactions(prefixPath, -9));
        }
    }

    /* renamed from: com.firebase.client.core.Repo.15 */
    class AnonymousClass15 implements CompletionListener {
        final /* synthetic */ Path val$path;
        final /* synthetic */ List val$queue;
        final /* synthetic */ Repo val$repo;

        /* renamed from: com.firebase.client.core.Repo.15.1 */
        class C05511 implements Runnable {
            final /* synthetic */ DataSnapshot val$snap;
            final /* synthetic */ TransactionData val$txn;

            C05511(TransactionData transactionData, DataSnapshot dataSnapshot) {
                this.val$txn = transactionData;
                this.val$snap = dataSnapshot;
            }

            public void run() {
                this.val$txn.handler.onComplete(null, true, this.val$snap);
            }
        }

        AnonymousClass15(Path path, List list, Repo repo) {
            this.val$path = path;
            this.val$queue = list;
            this.val$repo = repo;
        }

        public void onComplete(FirebaseError error, Firebase ref) {
            Repo.this.warnIfWriteFailed("Transaction", this.val$path, error);
            List<Event> events = new ArrayList();
            if (error == null) {
                List<Runnable> callbacks = new ArrayList();
                for (TransactionData txn : this.val$queue) {
                    txn.status = TransactionStatus.COMPLETED;
                    events.addAll(Repo.this.serverSyncTree.ackUserWrite(txn.currentWriteId, Repo.$assertionsDisabled, Repo.$assertionsDisabled, Repo.this.serverClock));
                    callbacks.add(new C05511(txn, new DataSnapshot(new Firebase(this.val$repo, txn.path), IndexedNode.from(txn.currentOutputSnapshotResolved))));
                    Repo.this.removeEventCallback(new ValueEventRegistration(Repo.this, txn.outstandingListener, QuerySpec.defaultQueryAtPath(txn.path)));
                }
                Repo.this.pruneCompletedTransactions(Repo.this.transactionQueueTree.subTree(this.val$path));
                Repo.this.sendAllReadyTransactions();
                this.val$repo.postEvents(events);
                for (int i = 0; i < callbacks.size(); i++) {
                    Repo.this.postEvent((Runnable) callbacks.get(i));
                }
                return;
            }
            if (error.getCode() == -1) {
                for (TransactionData transaction : this.val$queue) {
                    if (transaction.status == TransactionStatus.SENT_NEEDS_ABORT) {
                        transaction.status = TransactionStatus.NEEDS_ABORT;
                    } else {
                        transaction.status = TransactionStatus.RUN;
                    }
                }
            } else {
                for (TransactionData transaction2 : this.val$queue) {
                    transaction2.status = TransactionStatus.NEEDS_ABORT;
                    transaction2.abortReason = error;
                }
            }
            Repo.this.rerunTransactions(this.val$path);
        }
    }

    /* renamed from: com.firebase.client.core.Repo.19 */
    class AnonymousClass19 implements TreeVisitor<List<TransactionData>> {
        final /* synthetic */ List val$queue;

        AnonymousClass19(List list) {
            this.val$queue = list;
        }

        public void visitTree(Tree<List<TransactionData>> tree) {
            Repo.this.aggregateTransactionQueues(this.val$queue, tree);
        }
    }

    /* renamed from: com.firebase.client.core.Repo.20 */
    class AnonymousClass20 implements TreeFilter<List<TransactionData>> {
        final /* synthetic */ int val$reason;

        AnonymousClass20(int i) {
            this.val$reason = i;
        }

        public boolean filterTreeNode(Tree<List<TransactionData>> tree) {
            Repo.this.abortTransactionsAtNode(tree, this.val$reason);
            return Repo.$assertionsDisabled;
        }
    }

    /* renamed from: com.firebase.client.core.Repo.21 */
    class AnonymousClass21 implements TreeVisitor<List<TransactionData>> {
        final /* synthetic */ int val$reason;

        AnonymousClass21(int i) {
            this.val$reason = i;
        }

        public void visitTree(Tree<List<TransactionData>> tree) {
            Repo.this.abortTransactionsAtNode(tree, this.val$reason);
        }
    }

    /* renamed from: com.firebase.client.core.Repo.2 */
    class C10982 implements ListenProvider {

        /* renamed from: com.firebase.client.core.Repo.2.1 */
        class C05531 implements Runnable {
            final /* synthetic */ SyncTree.CompletionListener val$onComplete;
            final /* synthetic */ QuerySpec val$query;

            C05531(QuerySpec querySpec, SyncTree.CompletionListener completionListener) {
                this.val$query = querySpec;
                this.val$onComplete = completionListener;
            }

            public void run() {
                Node node = Repo.this.infoData.getNode(this.val$query.getPath());
                if (!node.isEmpty()) {
                    Repo.this.postEvents(Repo.this.infoSyncTree.applyServerOverwrite(this.val$query.getPath(), node));
                    this.val$onComplete.onListenComplete(null);
                }
            }
        }

        C10982() {
        }

        public void startListening(QuerySpec query, Tag tag, SyncTreeHash hash, SyncTree.CompletionListener onComplete) {
            Repo.this.scheduleNow(new C05531(query, onComplete));
        }

        public void stopListening(QuerySpec query, Tag tag) {
        }
    }

    /* renamed from: com.firebase.client.core.Repo.3 */
    class C11003 implements ListenProvider {

        /* renamed from: com.firebase.client.core.Repo.3.1 */
        class C10991 implements RequestResultListener {
            final /* synthetic */ SyncTree.CompletionListener val$onListenComplete;

            C10991(SyncTree.CompletionListener completionListener) {
                this.val$onListenComplete = completionListener;
            }

            public void onRequestResult(FirebaseError error) {
                Repo.this.postEvents(this.val$onListenComplete.onListenComplete(error));
            }
        }

        C11003() {
        }

        public void startListening(QuerySpec query, Tag tag, SyncTreeHash hash, SyncTree.CompletionListener onListenComplete) {
            Repo.this.connection.listen(query, hash, tag, new C10991(onListenComplete));
        }

        public void stopListening(QuerySpec query, Tag tag) {
            Repo.this.connection.unlisten(query);
        }
    }

    /* renamed from: com.firebase.client.core.Repo.4 */
    class C11014 implements CompletionListener {
        final /* synthetic */ UserWriteRecord val$write;

        C11014(UserWriteRecord userWriteRecord) {
            this.val$write = userWriteRecord;
        }

        public void onComplete(FirebaseError error, Firebase ref) {
            Repo.this.warnIfWriteFailed("Persisted write", this.val$write.getPath(), error);
            Repo.this.ackWriteAndRerunTransactions(this.val$write.getWriteId(), this.val$write.getPath(), error);
        }
    }

    /* renamed from: com.firebase.client.core.Repo.6 */
    class C11026 implements CompletionListener {
        final /* synthetic */ CompletionListener val$onComplete;
        final /* synthetic */ Path val$path;
        final /* synthetic */ long val$writeId;

        C11026(Path path, long j, CompletionListener completionListener) {
            this.val$path = path;
            this.val$writeId = j;
            this.val$onComplete = completionListener;
        }

        public void onComplete(FirebaseError error, Firebase ref) {
            Repo.this.warnIfWriteFailed("setValue", this.val$path, error);
            Repo.this.ackWriteAndRerunTransactions(this.val$writeId, this.val$path, error);
            Repo.this.callOnComplete(this.val$onComplete, error, this.val$path);
        }
    }

    /* renamed from: com.firebase.client.core.Repo.7 */
    class C11037 implements CompletionListener {
        final /* synthetic */ CompletionListener val$onComplete;
        final /* synthetic */ Path val$path;
        final /* synthetic */ long val$writeId;

        C11037(Path path, long j, CompletionListener completionListener) {
            this.val$path = path;
            this.val$writeId = j;
            this.val$onComplete = completionListener;
        }

        public void onComplete(FirebaseError error, Firebase ref) {
            Repo.this.warnIfWriteFailed("updateChildren", this.val$path, error);
            Repo.this.ackWriteAndRerunTransactions(this.val$writeId, this.val$path, error);
            Repo.this.callOnComplete(this.val$onComplete, error, this.val$path);
        }
    }

    /* renamed from: com.firebase.client.core.Repo.8 */
    class C11048 implements CompletionListener {
        final /* synthetic */ Node val$newValue;
        final /* synthetic */ CompletionListener val$onComplete;
        final /* synthetic */ Path val$path;

        C11048(Path path, Node node, CompletionListener completionListener) {
            this.val$path = path;
            this.val$newValue = node;
            this.val$onComplete = completionListener;
        }

        public void onComplete(FirebaseError error, Firebase ref) {
            Repo.this.warnIfWriteFailed("onDisconnect().setValue", this.val$path, error);
            if (error == null) {
                Repo.this.onDisconnect.remember(this.val$path, this.val$newValue);
            }
            Repo.this.callOnComplete(this.val$onComplete, error, this.val$path);
        }
    }

    /* renamed from: com.firebase.client.core.Repo.9 */
    class C11059 implements CompletionListener {
        final /* synthetic */ CompletionListener val$listener;
        final /* synthetic */ Map val$newChildren;
        final /* synthetic */ Path val$path;

        C11059(Path path, Map map, CompletionListener completionListener) {
            this.val$path = path;
            this.val$newChildren = map;
            this.val$listener = completionListener;
        }

        public void onComplete(FirebaseError error, Firebase ref) {
            Repo.this.warnIfWriteFailed("onDisconnect().updateChildren", this.val$path, error);
            if (error == null) {
                for (Entry<Path, Node> entry : this.val$newChildren.entrySet()) {
                    Repo.this.onDisconnect.remember(this.val$path.child((Path) entry.getKey()), (Node) entry.getValue());
                }
            }
            Repo.this.callOnComplete(this.val$listener, error, this.val$path);
        }
    }

    private static class FirebaseAppImpl extends FirebaseApp {
        protected FirebaseAppImpl(Repo repo) {
            super(repo);
        }
    }

    static {
        $assertionsDisabled = !Repo.class.desiredAssertionStatus() ? true : $assertionsDisabled;
    }

    Repo(RepoInfo repoInfo, Context ctx) {
        this.serverClock = new OffsetClock(new DefaultClock(), 0);
        this.hijackHash = $assertionsDisabled;
        this.dataUpdateCount = 0;
        this.nextWriteId = 1;
        this.loggedTransactionPersistenceWarning = $assertionsDisabled;
        this.transactionOrder = 0;
        this.repoInfo = repoInfo;
        this.ctx = ctx;
        this.app = new FirebaseAppImpl(this);
        this.operationLogger = this.ctx.getLogger("RepoOperation");
        this.transactionLogger = this.ctx.getLogger("Transaction");
        this.dataLogger = this.ctx.getLogger("DataOperation");
        this.eventRaiser = new EventRaiser(this.ctx);
        this.connection = new PersistentConnection(ctx, repoInfo, this);
        this.authenticationManager = new AuthenticationManager(ctx, this, repoInfo, this.connection);
        this.authenticationManager.resumeSession();
        scheduleNow(new C05521());
    }

    private void deferredInitialization() {
        boolean authenticated;
        this.connection.establishConnection();
        PersistenceManager persistenceManager = this.ctx.getPersistenceManager(this.repoInfo.host);
        this.infoData = new SnapshotHolder();
        this.onDisconnect = new SparseSnapshotTree();
        this.transactionQueueTree = new Tree();
        this.infoSyncTree = new SyncTree(this.ctx, new NoopPersistenceManager(), new C10982());
        this.serverSyncTree = new SyncTree(this.ctx, persistenceManager, new C11003());
        restoreWrites(persistenceManager);
        if (this.authenticationManager.getAuth() != null) {
            authenticated = true;
        } else {
            authenticated = $assertionsDisabled;
        }
        updateInfo(Constants.DOT_INFO_AUTHENTICATED, Boolean.valueOf(authenticated));
        updateInfo(Constants.DOT_INFO_CONNECTED, Boolean.valueOf($assertionsDisabled));
    }

    private void restoreWrites(PersistenceManager persistenceManager) {
        List<UserWriteRecord> writes = persistenceManager.loadUserWrites();
        Map<String, Object> serverValues = ServerValues.generateServerValues(this.serverClock);
        long lastWriteId = Long.MIN_VALUE;
        for (UserWriteRecord write : writes) {
            CompletionListener onComplete = new C11014(write);
            if (lastWriteId >= write.getWriteId()) {
                throw new IllegalStateException("Write ids were not in order.");
            }
            lastWriteId = write.getWriteId();
            this.nextWriteId = write.getWriteId() + 1;
            if (write.isOverwrite()) {
                if (this.operationLogger.logsDebug()) {
                    this.operationLogger.debug("Restoring overwrite with id " + write.getWriteId());
                }
                this.connection.put(write.getPath().toString(), write.getOverwrite().getValue(true), null, onComplete);
                this.serverSyncTree.applyUserOverwrite(write.getPath(), write.getOverwrite(), ServerValues.resolveDeferredValueSnapshot(write.getOverwrite(), serverValues), write.getWriteId(), true, $assertionsDisabled);
            } else {
                if (this.operationLogger.logsDebug()) {
                    this.operationLogger.debug("Restoring merge with id " + write.getWriteId());
                }
                this.connection.merge(write.getPath().toString(), write.getMerge().getValue(true), onComplete);
                this.serverSyncTree.applyUserMerge(write.getPath(), write.getMerge(), ServerValues.resolveDeferredValueMerge(write.getMerge(), serverValues), write.getWriteId(), $assertionsDisabled);
            }
        }
    }

    public AuthenticationManager getAuthenticationManager() {
        return this.authenticationManager;
    }

    public FirebaseApp getFirebaseApp() {
        return this.app;
    }

    public String toString() {
        return this.repoInfo.toString();
    }

    public void scheduleNow(Runnable r) {
        this.ctx.requireStarted();
        this.ctx.getRunLoop().scheduleNow(r);
    }

    public void postEvent(Runnable r) {
        this.ctx.requireStarted();
        this.ctx.getEventTarget().postEvent(r);
    }

    private void postEvents(List<? extends Event> events) {
        if (!events.isEmpty()) {
            this.eventRaiser.raiseEvents(events);
        }
    }

    public long getServerTime() {
        return this.serverClock.millis();
    }

    boolean hasListeners() {
        return (this.infoSyncTree.isEmpty() && this.serverSyncTree.isEmpty()) ? $assertionsDisabled : true;
    }

    public void onDataUpdate(String pathString, Object message, boolean isMerge, Tag tag) {
        List<? extends Event> events;
        if (this.operationLogger.logsDebug()) {
            this.operationLogger.debug("onDataUpdate: " + pathString);
        }
        if (this.dataLogger.logsDebug()) {
            this.operationLogger.debug("onDataUpdate: " + pathString + MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR + message);
        }
        this.dataUpdateCount++;
        Path path = new Path(pathString);
        if (tag != null) {
            if (isMerge) {
                try {
                    Map<Path, Node> taggedChildren = new HashMap();
                    for (Entry<String, Object> entry : ((Map) message).entrySet()) {
                        taggedChildren.put(new Path((String) entry.getKey()), NodeUtilities.NodeFromJSON(entry.getValue()));
                    }
                    events = this.serverSyncTree.applyTaggedQueryMerge(path, taggedChildren, tag);
                } catch (FirebaseException e) {
                    this.operationLogger.error("FIREBASE INTERNAL ERROR", e);
                    return;
                }
            }
            events = this.serverSyncTree.applyTaggedQueryOverwrite(path, NodeUtilities.NodeFromJSON(message), tag);
        } else if (isMerge) {
            Map<Path, Node> changedChildren = new HashMap();
            for (Entry<String, Object> entry2 : ((Map) message).entrySet()) {
                changedChildren.put(new Path((String) entry2.getKey()), NodeUtilities.NodeFromJSON(entry2.getValue()));
            }
            events = this.serverSyncTree.applyServerMerge(path, changedChildren);
        } else {
            events = this.serverSyncTree.applyServerOverwrite(path, NodeUtilities.NodeFromJSON(message));
        }
        if (events.size() > 0) {
            rerunTransactions(path);
        }
        postEvents(events);
    }

    public void onRangeMergeUpdate(Path path, List<RangeMerge> merges, Tag tag) {
        List<? extends Event> events;
        if (this.operationLogger.logsDebug()) {
            this.operationLogger.debug("onRangeMergeUpdate: " + path);
        }
        if (this.dataLogger.logsDebug()) {
            this.operationLogger.debug("onRangeMergeUpdate: " + path + MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR + merges);
        }
        this.dataUpdateCount++;
        if (tag != null) {
            events = this.serverSyncTree.applyTaggedRangeMerges(path, merges, tag);
        } else {
            events = this.serverSyncTree.applyServerRangeMerges(path, merges);
        }
        if (events.size() > 0) {
            rerunTransactions(path);
        }
        postEvents(events);
    }

    void callOnComplete(CompletionListener onComplete, FirebaseError error, Path path) {
        if (onComplete != null) {
            Firebase ref;
            ChildKey last = path.getBack();
            if (last == null || !last.isPriorityChildName()) {
                ref = new Firebase(this, path);
            } else {
                ref = new Firebase(this, path.getParent());
            }
            postEvent(new C05545(onComplete, error, ref));
        }
    }

    private void ackWriteAndRerunTransactions(long writeId, Path path, FirebaseError error) {
        if (error == null || error.getCode() != -25) {
            boolean success;
            boolean z;
            if (error == null) {
                success = true;
            } else {
                success = $assertionsDisabled;
            }
            SyncTree syncTree = this.serverSyncTree;
            if (success) {
                z = $assertionsDisabled;
            } else {
                z = true;
            }
            List<? extends Event> clearEvents = syncTree.ackUserWrite(writeId, z, true, this.serverClock);
            if (clearEvents.size() > 0) {
                rerunTransactions(path);
            }
            postEvents(clearEvents);
        }
    }

    public void setValue(Path path, Node newValueUnresolved, CompletionListener onComplete) {
        if (this.operationLogger.logsDebug()) {
            this.operationLogger.debug("set: " + path);
        }
        if (this.dataLogger.logsDebug()) {
            this.dataLogger.debug("set: " + path + MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR + newValueUnresolved);
        }
        Node newValue = ServerValues.resolveDeferredValueSnapshot(newValueUnresolved, ServerValues.generateServerValues(this.serverClock));
        long writeId = getNextWriteId();
        postEvents(this.serverSyncTree.applyUserOverwrite(path, newValueUnresolved, newValue, writeId, true, true));
        this.connection.put(path.toString(), newValueUnresolved.getValue(true), new C11026(path, writeId, onComplete));
        rerunTransactions(abortTransactions(path, -9));
    }

    public void updateChildren(Path path, CompoundWrite updates, CompletionListener onComplete, Map<String, Object> unParsedUpdates) {
        if (this.operationLogger.logsDebug()) {
            this.operationLogger.debug("update: " + path);
        }
        if (this.dataLogger.logsDebug()) {
            this.dataLogger.debug("update: " + path + MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR + unParsedUpdates);
        }
        if (updates.isEmpty()) {
            if (this.operationLogger.logsDebug()) {
                this.operationLogger.debug("update called with no changes. No-op");
            }
            callOnComplete(onComplete, null, path);
            return;
        }
        CompoundWrite resolved = ServerValues.resolveDeferredValueMerge(updates, ServerValues.generateServerValues(this.serverClock));
        long writeId = getNextWriteId();
        postEvents(this.serverSyncTree.applyUserMerge(path, updates, resolved, writeId, true));
        this.connection.merge(path.toString(), unParsedUpdates, new C11037(path, writeId, onComplete));
        rerunTransactions(abortTransactions(path, -9));
    }

    public void purgeOutstandingWrites() {
        if (this.operationLogger.logsDebug()) {
            this.operationLogger.debug("Purging writes");
        }
        postEvents(this.serverSyncTree.removeAllWrites());
        abortTransactions(Path.getEmptyPath(), -25);
        this.connection.purgeOutstandingWrites();
    }

    public void removeEventCallback(@NotNull EventRegistration eventRegistration) {
        List<Event> events;
        if (Constants.DOT_INFO.equals(eventRegistration.getQuerySpec().getPath().getFront())) {
            events = this.infoSyncTree.removeEventRegistration(eventRegistration);
        } else {
            events = this.serverSyncTree.removeEventRegistration(eventRegistration);
        }
        postEvents(events);
    }

    public void onDisconnectSetValue(Path path, Node newValue, CompletionListener onComplete) {
        this.connection.onDisconnectPut(path, newValue.getValue(true), new C11048(path, newValue, onComplete));
    }

    public void onDisconnectUpdate(Path path, Map<Path, Node> newChildren, CompletionListener listener, Map<String, Object> unParsedUpdates) {
        this.connection.onDisconnectMerge(path, unParsedUpdates, new C11059(path, newChildren, listener));
    }

    public void onDisconnectCancel(Path path, CompletionListener onComplete) {
        this.connection.onDisconnectCancel(path, new AnonymousClass10(path, onComplete));
    }

    public void onConnect() {
        onServerInfoUpdate(Constants.DOT_INFO_CONNECTED, Boolean.valueOf(true));
    }

    public void onDisconnect() {
        onServerInfoUpdate(Constants.DOT_INFO_CONNECTED, Boolean.valueOf($assertionsDisabled));
        runOnDisconnectEvents();
    }

    public void onAuthStatus(boolean authOk) {
        onServerInfoUpdate(Constants.DOT_INFO_AUTHENTICATED, Boolean.valueOf(authOk));
    }

    public void onServerInfoUpdate(ChildKey key, Object value) {
        updateInfo(key, value);
    }

    public void onServerInfoUpdate(Map<ChildKey, Object> updates) {
        for (Entry<ChildKey, Object> entry : updates.entrySet()) {
            updateInfo((ChildKey) entry.getKey(), entry.getValue());
        }
    }

    void interrupt() {
        this.connection.interrupt();
    }

    void resume() {
        this.connection.resume();
    }

    public void addEventCallback(@NotNull EventRegistration eventRegistration) {
        List<? extends Event> events;
        ChildKey front = eventRegistration.getQuerySpec().getPath().getFront();
        if (front == null || !front.equals(Constants.DOT_INFO)) {
            events = this.serverSyncTree.addEventRegistration(eventRegistration);
        } else {
            events = this.infoSyncTree.addEventRegistration(eventRegistration);
        }
        postEvents(events);
    }

    public void keepSynced(QuerySpec query, boolean keep) {
        if ($assertionsDisabled || query.getPath().isEmpty() || !query.getPath().getFront().equals(Constants.DOT_INFO)) {
            this.serverSyncTree.keepSynced(query, keep);
            return;
        }
        throw new AssertionError();
    }

    PersistentConnection getConnection() {
        return this.connection;
    }

    private void updateInfo(ChildKey childKey, Object value) {
        if (childKey.equals(Constants.DOT_INFO_SERVERTIME_OFFSET)) {
            this.serverClock.setOffset(((Long) value).longValue());
        }
        Path path = new Path(Constants.DOT_INFO, childKey);
        try {
            Node node = NodeUtilities.NodeFromJSON(value);
            this.infoData.update(path, node);
            postEvents(this.infoSyncTree.applyServerOverwrite(path, node));
        } catch (FirebaseException e) {
            this.operationLogger.error("Failed to parse info update", e);
        }
    }

    private long getNextWriteId() {
        long j = this.nextWriteId;
        this.nextWriteId = 1 + j;
        return j;
    }

    private void runOnDisconnectEvents() {
        SparseSnapshotTree resolvedTree = ServerValues.resolveDeferredValueTree(this.onDisconnect, ServerValues.generateServerValues(this.serverClock));
        List<Event> events = new ArrayList();
        resolvedTree.forEachTree(Path.getEmptyPath(), new AnonymousClass11(events));
        this.onDisconnect = new SparseSnapshotTree();
        postEvents(events);
    }

    private void warnIfWriteFailed(String writeType, Path path, FirebaseError error) {
        if (error != null && error.getCode() != -1 && error.getCode() != -25) {
            this.operationLogger.warn(writeType + " at " + path.toString() + " failed: " + error.toString());
        }
    }

    public void startTransaction(Path path, Handler handler, boolean applyLocally) {
        Result result;
        if (this.operationLogger.logsDebug()) {
            this.operationLogger.debug("transaction: " + path);
        }
        if (this.dataLogger.logsDebug()) {
            this.operationLogger.debug("transaction: " + path);
        }
        if (this.ctx.isPersistenceEnabled() && !this.loggedTransactionPersistenceWarning) {
            this.loggedTransactionPersistenceWarning = true;
            this.transactionLogger.info("runTransaction() usage detected while persistence is enabled. Please be aware that transactions *will not* be persisted across app restarts.  See https://www.firebase.com/docs/android/guide/offline-capabilities.html#section-handling-transactions-offline for more details.");
        }
        Firebase watchRef = new Firebase(this, path);
        ValueEventListener listener = new ValueEventListener() {
            public void onDataChange(DataSnapshot snapshot) {
            }

            public void onCancelled(FirebaseError error) {
            }
        };
        addEventCallback(new ValueEventRegistration(this, listener, watchRef.getSpec()));
        TransactionData transaction = new TransactionData(handler, listener, TransactionStatus.INITIALIZING, applyLocally, nextTransactionOrder(), null);
        Node currentState = getLatestState(path);
        transaction.currentInputSnapshot = currentState;
        FirebaseError error = null;
        try {
            result = handler.doTransaction(new MutableData(currentState));
            if (result == null) {
                throw new NullPointerException("Transaction returned null as result");
            }
        } catch (Throwable e) {
            error = FirebaseError.fromException(e);
            result = Transaction.abort();
        }
        if (result.isSuccess()) {
            transaction.status = TransactionStatus.RUN;
            Tree<List<TransactionData>> queueNode = this.transactionQueueTree.subTree(path);
            List<TransactionData> nodeQueue = (List) queueNode.getValue();
            if (nodeQueue == null) {
                nodeQueue = new ArrayList();
            }
            nodeQueue.add(transaction);
            queueNode.setValue(nodeQueue);
            Map<String, Object> serverValues = ServerValues.generateServerValues(this.serverClock);
            Node newNodeUnresolved = result.getNode();
            Node newNode = ServerValues.resolveDeferredValueSnapshot(newNodeUnresolved, serverValues);
            transaction.currentOutputSnapshotRaw = newNodeUnresolved;
            transaction.currentOutputSnapshotResolved = newNode;
            transaction.currentWriteId = getNextWriteId();
            postEvents(this.serverSyncTree.applyUserOverwrite(path, newNodeUnresolved, newNode, transaction.currentWriteId, applyLocally, $assertionsDisabled));
            sendAllReadyTransactions();
            return;
        }
        transaction.currentOutputSnapshotRaw = null;
        transaction.currentOutputSnapshotResolved = null;
        postEvent(new AnonymousClass13(handler, error, new DataSnapshot(watchRef, IndexedNode.from(transaction.currentInputSnapshot))));
    }

    private Node getLatestState(Path path) {
        return getLatestState(path, new ArrayList());
    }

    private Node getLatestState(Path path, List<Long> excudeSets) {
        Node state = this.serverSyncTree.calcCompleteEventCache(path, excudeSets);
        if (state == null) {
            return EmptyNode.Empty();
        }
        return state;
    }

    public void setHijackHash(boolean hijackHash) {
        this.hijackHash = hijackHash;
    }

    private void sendAllReadyTransactions() {
        Tree<List<TransactionData>> node = this.transactionQueueTree;
        pruneCompletedTransactions(node);
        sendReadyTransactions(node);
    }

    private void sendReadyTransactions(Tree<List<TransactionData>> node) {
        if (((List) node.getValue()) != null) {
            List<TransactionData> queue = buildTransactionQueue(node);
            if ($assertionsDisabled || queue.size() > 0) {
                Boolean allRun = Boolean.valueOf(true);
                for (TransactionData transaction : queue) {
                    if (transaction.status != TransactionStatus.RUN) {
                        allRun = Boolean.valueOf($assertionsDisabled);
                        break;
                    }
                }
                if (allRun.booleanValue()) {
                    sendTransactionQueue(queue, node.getPath());
                    return;
                }
                return;
            }
            throw new AssertionError();
        } else if (node.hasChildren()) {
            node.forEachChild(new TreeVisitor<List<TransactionData>>() {
                public void visitTree(Tree<List<TransactionData>> tree) {
                    Repo.this.sendReadyTransactions(tree);
                }
            });
        }
    }

    private void sendTransactionQueue(List<TransactionData> queue, Path path) {
        List<Long> setsToIgnore = new ArrayList();
        for (TransactionData txn : queue) {
            setsToIgnore.add(Long.valueOf(txn.currentWriteId));
        }
        Node latestState = getLatestState(path, setsToIgnore);
        Node snapToSend = latestState;
        String latestHash = "badhash";
        if (!this.hijackHash) {
            latestHash = latestState.getHash();
        }
        for (TransactionData txn2 : queue) {
            if ($assertionsDisabled || txn2.status == TransactionStatus.RUN) {
                txn2.status = TransactionStatus.SENT;
                txn2.retryCount = txn2.retryCount + 1;
                snapToSend = snapToSend.updateChild(Path.getRelative(path, txn2.path), txn2.currentOutputSnapshotRaw);
            } else {
                throw new AssertionError();
            }
        }
        Object dataToSend = snapToSend.getValue(true);
        long writeId = getNextWriteId();
        this.connection.put(path.toString(), dataToSend, latestHash, new AnonymousClass15(path, queue, this));
    }

    private void pruneCompletedTransactions(Tree<List<TransactionData>> node) {
        List<TransactionData> queue = (List) node.getValue();
        if (queue != null) {
            int i = 0;
            while (i < queue.size()) {
                if (((TransactionData) queue.get(i)).status == TransactionStatus.COMPLETED) {
                    queue.remove(i);
                } else {
                    i++;
                }
            }
            if (queue.size() > 0) {
                node.setValue(queue);
            } else {
                node.setValue(null);
            }
        }
        node.forEachChild(new TreeVisitor<List<TransactionData>>() {
            public void visitTree(Tree<List<TransactionData>> tree) {
                Repo.this.pruneCompletedTransactions(tree);
            }
        });
    }

    private long nextTransactionOrder() {
        long j = this.transactionOrder;
        this.transactionOrder = 1 + j;
        return j;
    }

    private Path rerunTransactions(Path changedPath) {
        Tree<List<TransactionData>> rootMostTransactionNode = getAncestorTransactionNode(changedPath);
        Path path = rootMostTransactionNode.getPath();
        rerunTransactionQueue(buildTransactionQueue(rootMostTransactionNode), path);
        return path;
    }

    private void rerunTransactionQueue(List<TransactionData> queue, Path path) {
        if (!queue.isEmpty()) {
            List<Runnable> callbacks = new ArrayList();
            List<Long> setsToIgnore = new ArrayList();
            for (TransactionData transaction : queue) {
                setsToIgnore.add(Long.valueOf(transaction.currentWriteId));
            }
            for (TransactionData transaction2 : queue) {
                Path relativePath = Path.getRelative(path, transaction2.path);
                boolean abortTransaction = $assertionsDisabled;
                FirebaseError abortReason = null;
                List<Event> events = new ArrayList();
                if ($assertionsDisabled || relativePath != null) {
                    if (transaction2.status == TransactionStatus.NEEDS_ABORT) {
                        abortTransaction = true;
                        abortReason = transaction2.abortReason;
                        if (abortReason.getCode() != -25) {
                            events.addAll(this.serverSyncTree.ackUserWrite(transaction2.currentWriteId, true, $assertionsDisabled, this.serverClock));
                        }
                    } else if (transaction2.status == TransactionStatus.RUN) {
                        if (transaction2.retryCount >= TRANSACTION_MAX_RETRIES) {
                            abortTransaction = true;
                            abortReason = FirebaseError.fromStatus(TRANSACTION_TOO_MANY_RETRIES);
                            events.addAll(this.serverSyncTree.ackUserWrite(transaction2.currentWriteId, true, $assertionsDisabled, this.serverClock));
                        } else {
                            Result result;
                            Node currentNode = getLatestState(transaction2.path, setsToIgnore);
                            transaction2.currentInputSnapshot = currentNode;
                            FirebaseError error = null;
                            try {
                                result = transaction2.handler.doTransaction(new MutableData(currentNode));
                            } catch (Throwable e) {
                                error = FirebaseError.fromException(e);
                                result = Transaction.abort();
                            }
                            if (result.isSuccess()) {
                                Long oldWriteId = Long.valueOf(transaction2.currentWriteId);
                                Map<String, Object> serverValues = ServerValues.generateServerValues(this.serverClock);
                                Node newDataNode = result.getNode();
                                Node newNodeResolved = ServerValues.resolveDeferredValueSnapshot(newDataNode, serverValues);
                                transaction2.currentOutputSnapshotRaw = newDataNode;
                                transaction2.currentOutputSnapshotResolved = newNodeResolved;
                                transaction2.currentWriteId = getNextWriteId();
                                setsToIgnore.remove(oldWriteId);
                                events.addAll(this.serverSyncTree.applyUserOverwrite(transaction2.path, newDataNode, newNodeResolved, transaction2.currentWriteId, transaction2.applyLocally, $assertionsDisabled));
                                events.addAll(this.serverSyncTree.ackUserWrite(oldWriteId.longValue(), true, $assertionsDisabled, this.serverClock));
                            } else {
                                abortTransaction = true;
                                abortReason = error;
                                events.addAll(this.serverSyncTree.ackUserWrite(transaction2.currentWriteId, true, $assertionsDisabled, this.serverClock));
                            }
                        }
                    }
                    postEvents(events);
                    if (abortTransaction) {
                        transaction2.status = TransactionStatus.COMPLETED;
                        Firebase firebase = new Firebase(this, transaction2.path);
                        DataSnapshot dataSnapshot = new DataSnapshot(r0, IndexedNode.from(transaction2.currentInputSnapshot));
                        scheduleNow(new AnonymousClass17(transaction2));
                        callbacks.add(new AnonymousClass18(transaction2, abortReason, dataSnapshot));
                    }
                } else {
                    throw new AssertionError();
                }
            }
            pruneCompletedTransactions(this.transactionQueueTree);
            for (int i = 0; i < callbacks.size(); i++) {
                postEvent((Runnable) callbacks.get(i));
            }
            sendAllReadyTransactions();
        }
    }

    private Tree<List<TransactionData>> getAncestorTransactionNode(Path path) {
        Tree<List<TransactionData>> transactionNode = this.transactionQueueTree;
        while (!path.isEmpty() && transactionNode.getValue() == null) {
            transactionNode = transactionNode.subTree(new Path(path.getFront()));
            path = path.popFront();
        }
        return transactionNode;
    }

    private List<TransactionData> buildTransactionQueue(Tree<List<TransactionData>> transactionNode) {
        List<TransactionData> queue = new ArrayList();
        aggregateTransactionQueues(queue, transactionNode);
        Collections.sort(queue);
        return queue;
    }

    private void aggregateTransactionQueues(List<TransactionData> queue, Tree<List<TransactionData>> node) {
        List<TransactionData> childQueue = (List) node.getValue();
        if (childQueue != null) {
            queue.addAll(childQueue);
        }
        node.forEachChild(new AnonymousClass19(queue));
    }

    private Path abortTransactions(Path path, int reason) {
        Path affectedPath = getAncestorTransactionNode(path).getPath();
        if (this.transactionLogger.logsDebug()) {
            this.operationLogger.debug("Aborting transactions for path: " + path + ". Affected: " + affectedPath);
        }
        Tree<List<TransactionData>> transactionNode = this.transactionQueueTree.subTree(path);
        transactionNode.forEachAncestor(new AnonymousClass20(reason));
        abortTransactionsAtNode(transactionNode, reason);
        transactionNode.forEachDescendant(new AnonymousClass21(reason));
        return affectedPath;
    }

    private void abortTransactionsAtNode(Tree<List<TransactionData>> node, int reason) {
        List<TransactionData> queue = (List) node.getValue();
        List<Event> events = new ArrayList();
        if (queue != null) {
            FirebaseError abortError;
            List<Runnable> callbacks = new ArrayList();
            if (reason == -9) {
                abortError = FirebaseError.fromStatus(TRANSACTION_OVERRIDE_BY_SET);
            } else {
                Utilities.hardAssert(reason == -25 ? true : $assertionsDisabled, "Unknown transaction abort reason: " + reason);
                abortError = FirebaseError.fromCode(-25);
            }
            int lastSent = -1;
            int i = 0;
            while (i < queue.size()) {
                TransactionData transaction = (TransactionData) queue.get(i);
                if (transaction.status != TransactionStatus.SENT_NEEDS_ABORT) {
                    if (transaction.status == TransactionStatus.SENT) {
                        if ($assertionsDisabled || lastSent == i - 1) {
                            lastSent = i;
                            transaction.status = TransactionStatus.SENT_NEEDS_ABORT;
                            transaction.abortReason = abortError;
                        } else {
                            throw new AssertionError();
                        }
                    } else if ($assertionsDisabled || transaction.status == TransactionStatus.RUN) {
                        removeEventCallback(new ValueEventRegistration(this, transaction.outstandingListener, QuerySpec.defaultQueryAtPath(transaction.path)));
                        if (reason == -9) {
                            events.addAll(this.serverSyncTree.ackUserWrite(transaction.currentWriteId, true, $assertionsDisabled, this.serverClock));
                        } else {
                            Utilities.hardAssert(reason == -25 ? true : $assertionsDisabled, "Unknown transaction abort reason: " + reason);
                        }
                        callbacks.add(new AnonymousClass22(transaction, abortError));
                    } else {
                        throw new AssertionError();
                    }
                }
                i++;
            }
            if (lastSent == -1) {
                node.setValue(null);
            } else {
                node.setValue(queue.subList(0, lastSent + 1));
            }
            postEvents(events);
            for (Runnable r : callbacks) {
                postEvent(r);
            }
        }
    }

    SyncTree getServerSyncTree() {
        return this.serverSyncTree;
    }

    SyncTree getInfoSyncTree() {
        return this.infoSyncTree;
    }
}
