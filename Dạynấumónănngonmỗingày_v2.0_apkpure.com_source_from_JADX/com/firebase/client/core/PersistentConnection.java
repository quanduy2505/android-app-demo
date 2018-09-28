package com.firebase.client.core;

import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;
import com.firebase.client.Firebase;
import com.firebase.client.Firebase.AuthListener;
import com.firebase.client.Firebase.CompletionListener;
import com.firebase.client.FirebaseError;
import com.firebase.client.core.SyncTree.SyncTreeHash;
import com.firebase.client.core.view.QuerySpec;
import com.firebase.client.realtime.Connection;
import com.firebase.client.realtime.Connection.DisconnectReason;
import com.firebase.client.snapshot.ChildKey;
import com.firebase.client.snapshot.NodeUtilities;
import com.firebase.client.utilities.LogWrapper;
import com.firebase.client.utilities.Utilities;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ScheduledFuture;

public class PersistentConnection implements com.firebase.client.realtime.Connection.Delegate {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static final long RECONNECT_MAX_DELAY = 30000;
    private static final long RECONNECT_MIN_DELAY = 1000;
    private static final double RECONNECT_MULTIPLIER = 1.3d;
    private static final long RECONNECT_RESET_TIMEOUT = 30000;
    private static final String REQUEST_ACTION = "a";
    private static final String REQUEST_ACTION_AUTH = "auth";
    private static final String REQUEST_ACTION_LISTEN = "l";
    private static final String REQUEST_ACTION_MERGE = "m";
    private static final String REQUEST_ACTION_ONDISCONNECT_CANCEL = "oc";
    private static final String REQUEST_ACTION_ONDISCONNECT_MERGE = "om";
    private static final String REQUEST_ACTION_ONDISCONNECT_PUT = "o";
    private static final String REQUEST_ACTION_PUT = "p";
    private static final String REQUEST_ACTION_QUERY = "q";
    private static final String REQUEST_ACTION_QUERY_UNLISTEN = "n";
    private static final String REQUEST_ACTION_STATS = "s";
    private static final String REQUEST_ACTION_UNAUTH = "unauth";
    private static final String REQUEST_ACTION_UNLISTEN = "u";
    private static final String REQUEST_COMPOUND_HASH = "ch";
    private static final String REQUEST_COMPOUND_HASH_HASHES = "hs";
    private static final String REQUEST_COMPOUND_HASH_PATHS = "ps";
    private static final String REQUEST_COUNTERS = "c";
    private static final String REQUEST_CREDENTIAL = "cred";
    private static final String REQUEST_DATA_HASH = "h";
    private static final String REQUEST_DATA_PAYLOAD = "d";
    private static final String REQUEST_ERROR = "error";
    private static final String REQUEST_NUMBER = "r";
    private static final String REQUEST_PATH = "p";
    private static final String REQUEST_PAYLOAD = "b";
    private static final String REQUEST_QUERIES = "q";
    private static final String REQUEST_STATUS = "s";
    private static final String REQUEST_TAG = "t";
    private static final String RESPONSE_FOR_REQUEST = "b";
    private static final String SERVER_ASYNC_ACTION = "a";
    private static final String SERVER_ASYNC_AUTH_REVOKED = "ac";
    private static final String SERVER_ASYNC_DATA_MERGE = "m";
    private static final String SERVER_ASYNC_DATA_RANGE_MERGE = "rm";
    private static final String SERVER_ASYNC_DATA_UPDATE = "d";
    private static final String SERVER_ASYNC_LISTEN_CANCELLED = "c";
    private static final String SERVER_ASYNC_PAYLOAD = "b";
    private static final String SERVER_ASYNC_SECURITY_DEBUG = "sd";
    private static final String SERVER_DATA_END_PATH = "e";
    private static final String SERVER_DATA_RANGE_MERGE = "m";
    private static final String SERVER_DATA_START_PATH = "s";
    private static final String SERVER_DATA_TAG = "t";
    private static final String SERVER_DATA_UPDATE_BODY = "d";
    private static final String SERVER_DATA_UPDATE_PATH = "p";
    private static final String SERVER_DATA_WARNINGS = "w";
    private static final String SERVER_RESPONSE_DATA = "d";
    private static long connectionIds;
    private AuthCredential authCredential;
    private ConnectionState connectionState;
    private Context ctx;
    private Delegate delegate;
    private boolean firstConnection;
    private long lastConnectionAttemptTime;
    private long lastConnectionEstablishedTime;
    private String lastSessionId;
    private Map<QuerySpec, OutstandingListen> listens;
    private LogWrapper logger;
    private List<OutstandingDisconnect> onDisconnectRequestQueue;
    private Map<Long, OutstandingPut> outstandingPuts;
    private Random random;
    private Connection realtime;
    private long reconnectDelay;
    private ScheduledFuture reconnectFuture;
    private RepoInfo repoInfo;
    private Map<Long, ResponseListener> requestCBHash;
    private long requestCounter;
    private boolean shouldReconnect;
    private long writeCounter;
    private boolean writesPaused;

    /* renamed from: com.firebase.client.core.PersistentConnection.1 */
    class C05501 implements Runnable {
        C05501() {
        }

        public void run() {
            PersistentConnection.this.establishConnection();
        }
    }

    private static class AuthCredential {
        static final /* synthetic */ boolean $assertionsDisabled;
        private Object authData;
        private String credential;
        private List<AuthListener> listeners;
        private boolean onSuccessCalled;

        static {
            $assertionsDisabled = !PersistentConnection.class.desiredAssertionStatus() ? true : PersistentConnection.$assertionsDisabled;
        }

        AuthCredential(AuthListener listener, String credential) {
            this.onSuccessCalled = PersistentConnection.$assertionsDisabled;
            this.listeners = new ArrayList();
            this.listeners.add(listener);
            this.credential = credential;
        }

        public boolean matches(String credential) {
            return this.credential.equals(credential);
        }

        public void preempt() {
            FirebaseError error = FirebaseError.fromStatus("preempted");
            for (AuthListener listener : this.listeners) {
                listener.onAuthError(error);
            }
        }

        public void addListener(AuthListener listener) {
            this.listeners.add(listener);
        }

        public void replay(AuthListener listener) {
            if ($assertionsDisabled || this.authData != null) {
                listener.onAuthSuccess(this.authData);
                return;
            }
            throw new AssertionError();
        }

        public boolean isComplete() {
            return this.onSuccessCalled;
        }

        public String getCredential() {
            return this.credential;
        }

        public void onCancel(FirebaseError error) {
            if (this.onSuccessCalled) {
                onRevoked(error);
                return;
            }
            for (AuthListener listener : this.listeners) {
                listener.onAuthError(error);
            }
        }

        public void onRevoked(FirebaseError error) {
            for (AuthListener listener : this.listeners) {
                listener.onAuthRevoked(error);
            }
        }

        public void onSuccess(Object authData) {
            if (!this.onSuccessCalled) {
                this.onSuccessCalled = true;
                this.authData = authData;
                for (AuthListener listener : this.listeners) {
                    listener.onAuthSuccess(authData);
                }
            }
        }
    }

    private enum ConnectionState {
        Disconnected,
        Authenticating,
        Connected
    }

    public interface Delegate {
        void onAuthStatus(boolean z);

        void onConnect();

        void onDataUpdate(String str, Object obj, boolean z, Tag tag);

        void onDisconnect();

        void onRangeMergeUpdate(Path path, List<RangeMerge> list, Tag tag);

        void onServerInfoUpdate(Map<ChildKey, Object> map);
    }

    private static class OutstandingDisconnect {
        private final String action;
        private final Object data;
        private final CompletionListener onComplete;
        private final Path path;

        private OutstandingDisconnect(String action, Path path, Object data, CompletionListener onComplete) {
            this.action = action;
            this.path = path;
            this.data = data;
            this.onComplete = onComplete;
        }

        public String getAction() {
            return this.action;
        }

        public Path getPath() {
            return this.path;
        }

        public Object getData() {
            return this.data;
        }

        public CompletionListener getOnComplete() {
            return this.onComplete;
        }
    }

    static class OutstandingListen {
        private final SyncTreeHash hashFunction;
        private final QuerySpec query;
        private final RequestResultListener resultListener;
        private final Tag tag;

        private OutstandingListen(RequestResultListener listener, QuerySpec query, Tag tag, SyncTreeHash hashFunction) {
            this.resultListener = listener;
            this.query = query;
            this.hashFunction = hashFunction;
            this.tag = tag;
        }

        public QuerySpec getQuery() {
            return this.query;
        }

        public Tag getTag() {
            return this.tag;
        }

        public SyncTreeHash getHashFunction() {
            return this.hashFunction;
        }

        public String toString() {
            return this.query.toString() + " (Tag: " + this.tag + ")";
        }
    }

    private static class OutstandingPut {
        private String action;
        private CompletionListener onComplete;
        private Map<String, Object> request;

        private OutstandingPut(String action, Map<String, Object> request, CompletionListener onComplete) {
            this.action = action;
            this.request = request;
            this.onComplete = onComplete;
        }

        public String getAction() {
            return this.action;
        }

        public Map<String, Object> getRequest() {
            return this.request;
        }

        public CompletionListener getOnComplete() {
            return this.onComplete;
        }
    }

    interface RequestResultListener {
        void onRequestResult(FirebaseError firebaseError);
    }

    private interface ResponseListener {
        void onResponse(Map<String, Object> map);
    }

    /* renamed from: com.firebase.client.core.PersistentConnection.2 */
    class C10922 implements ResponseListener {
        final /* synthetic */ CompletionListener val$listener;

        C10922(CompletionListener completionListener) {
            this.val$listener = completionListener;
        }

        public void onResponse(Map<String, Object> response) {
            String status = (String) response.get(PersistentConnection.SERVER_DATA_START_PATH);
            FirebaseError error = null;
            if (!status.equals("ok")) {
                error = FirebaseError.fromStatus(status, (String) response.get(PersistentConnection.SERVER_RESPONSE_DATA));
            }
            this.val$listener.onComplete(error, null);
        }
    }

    /* renamed from: com.firebase.client.core.PersistentConnection.3 */
    class C10933 implements ResponseListener {
        final /* synthetic */ CompletionListener val$onComplete;

        C10933(CompletionListener completionListener) {
            this.val$onComplete = completionListener;
        }

        public void onResponse(Map<String, Object> response) {
            String status = (String) response.get(PersistentConnection.SERVER_DATA_START_PATH);
            FirebaseError error = null;
            if (!status.equals("ok")) {
                error = FirebaseError.fromStatus(status, (String) response.get(PersistentConnection.SERVER_RESPONSE_DATA));
            }
            if (this.val$onComplete != null) {
                this.val$onComplete.onComplete(error, null);
            }
        }
    }

    /* renamed from: com.firebase.client.core.PersistentConnection.4 */
    class C10944 implements ResponseListener {
        final /* synthetic */ AuthCredential val$credential;
        final /* synthetic */ boolean val$restoreWritesAfterComplete;

        C10944(AuthCredential authCredential, boolean z) {
            this.val$credential = authCredential;
            this.val$restoreWritesAfterComplete = z;
        }

        public void onResponse(Map<String, Object> response) {
            PersistentConnection.this.connectionState = ConnectionState.Connected;
            if (this.val$credential == PersistentConnection.this.authCredential) {
                String status = (String) response.get(PersistentConnection.SERVER_DATA_START_PATH);
                if (status.equals("ok")) {
                    PersistentConnection.this.delegate.onAuthStatus(true);
                    this.val$credential.onSuccess(response.get(PersistentConnection.SERVER_RESPONSE_DATA));
                } else {
                    PersistentConnection.this.authCredential = null;
                    PersistentConnection.this.delegate.onAuthStatus(PersistentConnection.$assertionsDisabled);
                    this.val$credential.onCancel(FirebaseError.fromStatus(status, (String) response.get(PersistentConnection.SERVER_RESPONSE_DATA)));
                }
            }
            if (this.val$restoreWritesAfterComplete) {
                PersistentConnection.this.restoreWrites();
            }
        }
    }

    /* renamed from: com.firebase.client.core.PersistentConnection.5 */
    class C10955 implements ResponseListener {
        final /* synthetic */ String val$action;
        final /* synthetic */ CompletionListener val$onComplete;
        final /* synthetic */ OutstandingPut val$put;
        final /* synthetic */ long val$putId;

        C10955(String str, long j, OutstandingPut outstandingPut, CompletionListener completionListener) {
            this.val$action = str;
            this.val$putId = j;
            this.val$put = outstandingPut;
            this.val$onComplete = completionListener;
        }

        public void onResponse(Map<String, Object> response) {
            if (PersistentConnection.this.logger.logsDebug()) {
                PersistentConnection.this.logger.debug(this.val$action + " response: " + response);
            }
            if (((OutstandingPut) PersistentConnection.this.outstandingPuts.get(Long.valueOf(this.val$putId))) == this.val$put) {
                PersistentConnection.this.outstandingPuts.remove(Long.valueOf(this.val$putId));
                if (this.val$onComplete != null) {
                    String status = (String) response.get(PersistentConnection.SERVER_DATA_START_PATH);
                    if (status.equals("ok")) {
                        this.val$onComplete.onComplete(null, null);
                    } else {
                        this.val$onComplete.onComplete(FirebaseError.fromStatus(status, (String) response.get(PersistentConnection.SERVER_RESPONSE_DATA)), null);
                    }
                }
            } else if (PersistentConnection.this.logger.logsDebug()) {
                PersistentConnection.this.logger.debug("Ignoring on complete for put " + this.val$putId + " because it was removed already.");
            }
        }
    }

    /* renamed from: com.firebase.client.core.PersistentConnection.6 */
    class C10966 implements ResponseListener {
        final /* synthetic */ OutstandingListen val$listen;

        C10966(OutstandingListen outstandingListen) {
            this.val$listen = outstandingListen;
        }

        public void onResponse(Map<String, Object> response) {
            String status = (String) response.get(PersistentConnection.SERVER_DATA_START_PATH);
            if (status.equals("ok")) {
                Map<String, Object> serverBody = (Map) response.get(PersistentConnection.SERVER_RESPONSE_DATA);
                if (serverBody.containsKey(PersistentConnection.SERVER_DATA_WARNINGS)) {
                    PersistentConnection.this.warnOnListenerWarnings((List) serverBody.get(PersistentConnection.SERVER_DATA_WARNINGS), this.val$listen.getQuery());
                }
            }
            if (((OutstandingListen) PersistentConnection.this.listens.get(this.val$listen.getQuery())) != this.val$listen) {
                return;
            }
            if (status.equals("ok")) {
                this.val$listen.resultListener.onRequestResult(null);
                return;
            }
            PersistentConnection.this.removeListen(this.val$listen.getQuery());
            this.val$listen.resultListener.onRequestResult(FirebaseError.fromStatus(status, (String) response.get(PersistentConnection.SERVER_RESPONSE_DATA)));
        }
    }

    /* renamed from: com.firebase.client.core.PersistentConnection.7 */
    class C10977 implements ResponseListener {
        C10977() {
        }

        public void onResponse(Map<String, Object> response) {
            String status = (String) response.get(PersistentConnection.SERVER_DATA_START_PATH);
            if (!status.equals("ok")) {
                FirebaseError error = FirebaseError.fromStatus(status, (String) response.get(PersistentConnection.SERVER_RESPONSE_DATA));
                if (PersistentConnection.this.logger.logsDebug()) {
                    PersistentConnection.this.logger.debug("Failed to send stats: " + error);
                }
            }
        }
    }

    static {
        boolean z;
        if (PersistentConnection.class.desiredAssertionStatus()) {
            z = $assertionsDisabled;
        } else {
            z = true;
        }
        $assertionsDisabled = z;
        connectionIds = 0;
    }

    public PersistentConnection(Context ctx, RepoInfo info, Delegate delegate) {
        this.shouldReconnect = true;
        this.firstConnection = true;
        this.connectionState = ConnectionState.Disconnected;
        this.writeCounter = 0;
        this.requestCounter = 0;
        this.reconnectDelay = RECONNECT_MIN_DELAY;
        this.delegate = delegate;
        this.ctx = ctx;
        this.repoInfo = info;
        this.listens = new HashMap();
        this.requestCBHash = new HashMap();
        this.writesPaused = $assertionsDisabled;
        this.outstandingPuts = new HashMap();
        this.onDisconnectRequestQueue = new ArrayList();
        this.random = new Random();
        long connId = connectionIds;
        connectionIds = 1 + connId;
        this.logger = this.ctx.getLogger("PersistentConnection", "pc_" + connId);
        this.lastSessionId = null;
    }

    public void establishConnection() {
        if (this.shouldReconnect) {
            this.lastConnectionAttemptTime = System.currentTimeMillis();
            this.lastConnectionEstablishedTime = 0;
            this.realtime = new Connection(this.ctx, this.repoInfo, this, this.lastSessionId);
            this.realtime.open();
        }
    }

    public void onReady(long timestamp, String sessionId) {
        if (this.logger.logsDebug()) {
            this.logger.debug("onReady");
        }
        this.lastConnectionEstablishedTime = System.currentTimeMillis();
        handleTimestamp(timestamp);
        if (this.firstConnection) {
            sendConnectStats();
        }
        restoreState();
        this.firstConnection = $assertionsDisabled;
        this.lastSessionId = sessionId;
        this.delegate.onConnect();
    }

    public void listen(QuerySpec query, SyncTreeHash currentHashFn, Tag tag, RequestResultListener listener) {
        boolean z;
        boolean z2 = true;
        if (this.logger.logsDebug()) {
            this.logger.debug("Listening on " + query);
        }
        if (query.isDefault() || !query.loadsAllData()) {
            z = true;
        } else {
            z = $assertionsDisabled;
        }
        Utilities.hardAssert(z, "listen() called for non-default but complete query");
        if (this.listens.containsKey(query)) {
            z2 = $assertionsDisabled;
        }
        Utilities.hardAssert(z2, "listen() called twice for same QuerySpec.");
        if (this.logger.logsDebug()) {
            this.logger.debug("Adding listen query: " + query);
        }
        OutstandingListen outstandingListen = new OutstandingListen(query, tag, currentHashFn, null);
        this.listens.put(query, outstandingListen);
        if (connected()) {
            sendListen(outstandingListen);
        }
    }

    public Map<QuerySpec, OutstandingListen> getListens() {
        return this.listens;
    }

    public void put(String pathString, Object data, CompletionListener onComplete) {
        put(pathString, data, null, onComplete);
    }

    public void put(String pathString, Object data, String hash, CompletionListener onComplete) {
        putInternal(SERVER_DATA_UPDATE_PATH, pathString, data, hash, onComplete);
    }

    public void merge(String pathString, Object data, CompletionListener onComplete) {
        putInternal(SERVER_DATA_RANGE_MERGE, pathString, data, null, onComplete);
    }

    public void purgeOutstandingWrites() {
        FirebaseError error = FirebaseError.fromCode(-25);
        for (OutstandingPut put : this.outstandingPuts.values()) {
            if (put.onComplete != null) {
                put.onComplete.onComplete(error, null);
            }
        }
        for (OutstandingDisconnect onDisconnect : this.onDisconnectRequestQueue) {
            if (onDisconnect.onComplete != null) {
                onDisconnect.onComplete.onComplete(error, null);
            }
        }
        this.outstandingPuts.clear();
        this.onDisconnectRequestQueue.clear();
    }

    public void onDataMessage(Map<String, Object> message) {
        if (message.containsKey(REQUEST_NUMBER)) {
            ResponseListener responseListener = (ResponseListener) this.requestCBHash.remove(Long.valueOf((long) ((Integer) message.get(REQUEST_NUMBER)).intValue()));
            if (responseListener != null) {
                responseListener.onResponse((Map) message.get(SERVER_ASYNC_PAYLOAD));
            }
        } else if (!message.containsKey(REQUEST_ERROR)) {
            if (message.containsKey(SERVER_ASYNC_ACTION)) {
                onDataPush((String) message.get(SERVER_ASYNC_ACTION), (Map) message.get(SERVER_ASYNC_PAYLOAD));
            } else if (this.logger.logsDebug()) {
                this.logger.debug("Ignoring unknown message: " + message);
            }
        }
    }

    public void onDisconnect(DisconnectReason reason) {
        if (this.logger.logsDebug()) {
            this.logger.debug("Got on disconnect due to " + reason.name());
        }
        this.connectionState = ConnectionState.Disconnected;
        if (this.shouldReconnect) {
            long recDelay;
            if (reason == DisconnectReason.SERVER_RESET) {
                recDelay = 0;
            } else {
                if (this.lastConnectionEstablishedTime > 0) {
                    if (System.currentTimeMillis() - this.lastConnectionEstablishedTime > RECONNECT_RESET_TIMEOUT) {
                        this.reconnectDelay = RECONNECT_MIN_DELAY;
                    }
                    this.lastConnectionEstablishedTime = 0;
                }
                recDelay = (long) this.random.nextInt((int) Math.max(1, this.reconnectDelay - (System.currentTimeMillis() - this.lastConnectionAttemptTime)));
            }
            if (this.logger.logsDebug()) {
                this.logger.debug("Reconnecting in " + recDelay + "ms");
            }
            this.reconnectFuture = this.ctx.getRunLoop().schedule(new C05501(), recDelay);
            this.reconnectDelay = Math.min(RECONNECT_RESET_TIMEOUT, (long) (((double) this.reconnectDelay) * RECONNECT_MULTIPLIER));
        } else {
            cancelTransactions();
            this.requestCBHash.clear();
        }
        this.delegate.onDisconnect();
    }

    public void onKill(String reason) {
        if (this.logger.logsDebug()) {
            this.logger.debug("Firebase connection was forcefully killed by the server. Will not attempt reconnect. Reason: " + reason);
        }
        this.shouldReconnect = $assertionsDisabled;
    }

    void unlisten(QuerySpec query) {
        if (this.logger.logsDebug()) {
            this.logger.debug("unlistening on " + query);
        }
        boolean z = (query.isDefault() || !query.loadsAllData()) ? true : $assertionsDisabled;
        Utilities.hardAssert(z, "unlisten() called for non-default but complete query");
        OutstandingListen listen = removeListen(query);
        if (listen != null && connected()) {
            sendUnlisten(listen);
        }
    }

    private boolean connected() {
        return this.connectionState != ConnectionState.Disconnected ? true : $assertionsDisabled;
    }

    void onDisconnectPut(Path path, Object data, CompletionListener onComplete) {
        if (canSendWrites()) {
            sendOnDisconnect(REQUEST_ACTION_ONDISCONNECT_PUT, path, data, onComplete);
        } else {
            this.onDisconnectRequestQueue.add(new OutstandingDisconnect(path, data, onComplete, null));
        }
    }

    private boolean canSendWrites() {
        return (this.connectionState != ConnectionState.Connected || this.writesPaused) ? $assertionsDisabled : true;
    }

    void onDisconnectMerge(Path path, Map<String, Object> updates, CompletionListener onComplete) {
        if (canSendWrites()) {
            sendOnDisconnect(REQUEST_ACTION_ONDISCONNECT_MERGE, path, updates, onComplete);
        } else {
            this.onDisconnectRequestQueue.add(new OutstandingDisconnect(path, updates, onComplete, null));
        }
    }

    void onDisconnectCancel(Path path, CompletionListener onComplete) {
        if (canSendWrites()) {
            sendOnDisconnect(REQUEST_ACTION_ONDISCONNECT_CANCEL, path, null, onComplete);
        } else {
            this.onDisconnectRequestQueue.add(new OutstandingDisconnect(path, null, onComplete, null));
        }
    }

    void interrupt() {
        this.shouldReconnect = $assertionsDisabled;
        if (this.realtime != null) {
            this.realtime.close();
            this.realtime = null;
            return;
        }
        if (this.reconnectFuture != null) {
            this.reconnectFuture.cancel($assertionsDisabled);
            this.reconnectFuture = null;
        }
        onDisconnect(DisconnectReason.OTHER);
    }

    public void resume() {
        this.shouldReconnect = true;
        if (this.realtime == null) {
            establishConnection();
        }
    }

    public void auth(String credential, AuthListener listener) {
        if (this.authCredential == null) {
            this.authCredential = new AuthCredential(listener, credential);
        } else if (this.authCredential.matches(credential)) {
            this.authCredential.addListener(listener);
            if (this.authCredential.isComplete()) {
                this.authCredential.replay(listener);
            }
        } else {
            this.authCredential.preempt();
            this.authCredential = new AuthCredential(listener, credential);
        }
        if (connected()) {
            if (this.logger.logsDebug()) {
                this.logger.debug("Authenticating with credential: " + credential);
            }
            sendAuth();
        }
    }

    public void unauth(CompletionListener listener) {
        this.authCredential = null;
        this.delegate.onAuthStatus($assertionsDisabled);
        if (connected()) {
            sendAction(REQUEST_ACTION_UNAUTH, new HashMap(), new C10922(listener));
        }
    }

    public void pauseWrites() {
        if (this.logger.logsDebug()) {
            this.logger.debug("Writes paused.");
        }
        this.writesPaused = true;
    }

    public void unpauseWrites() {
        if (this.logger.logsDebug()) {
            this.logger.debug("Writes unpaused.");
        }
        this.writesPaused = $assertionsDisabled;
        if (canSendWrites()) {
            restoreWrites();
        }
    }

    public boolean writesPaused() {
        return this.writesPaused;
    }

    private void sendOnDisconnect(String action, Path path, Object data, CompletionListener onComplete) {
        Map<String, Object> request = new HashMap();
        request.put(SERVER_DATA_UPDATE_PATH, path.toString());
        request.put(SERVER_RESPONSE_DATA, data);
        if (this.logger.logsDebug()) {
            this.logger.debug("onDisconnect " + action + MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR + request);
        }
        sendAction(action, request, new C10933(onComplete));
    }

    private void cancelTransactions() {
        Iterator<Entry<Long, OutstandingPut>> iter = this.outstandingPuts.entrySet().iterator();
        while (iter.hasNext()) {
            OutstandingPut put = (OutstandingPut) ((Entry) iter.next()).getValue();
            if (put.getRequest().containsKey(REQUEST_DATA_HASH)) {
                put.getOnComplete().onComplete(FirebaseError.fromStatus("disconnected"), null);
                iter.remove();
            }
        }
    }

    private void sendUnlisten(OutstandingListen listen) {
        Map<String, Object> request = new HashMap();
        request.put(SERVER_DATA_UPDATE_PATH, listen.query.getPath().toString());
        Tag tag = listen.getTag();
        if (tag != null) {
            request.put(REQUEST_QUERIES, listen.getQuery().getParams().getWireProtocolParams());
            request.put(SERVER_DATA_TAG, Long.valueOf(tag.getTagNumber()));
        }
        sendAction(REQUEST_ACTION_QUERY_UNLISTEN, request, null);
    }

    private OutstandingListen removeListen(QuerySpec query) {
        if (this.logger.logsDebug()) {
            this.logger.debug("removing query " + query);
        }
        if (this.listens.containsKey(query)) {
            OutstandingListen oldListen = (OutstandingListen) this.listens.get(query);
            this.listens.remove(query);
            return oldListen;
        }
        if (this.logger.logsDebug()) {
            this.logger.debug("Trying to remove listener for QuerySpec " + query + " but no listener exists.");
        }
        return null;
    }

    public Collection<OutstandingListen> removeListens(Path path) {
        if (this.logger.logsDebug()) {
            this.logger.debug("removing all listens at path " + path);
        }
        List<OutstandingListen> removedListens = new ArrayList();
        for (Entry<QuerySpec, OutstandingListen> entry : this.listens.entrySet()) {
            OutstandingListen listen = (OutstandingListen) entry.getValue();
            if (((QuerySpec) entry.getKey()).getPath().equals(path)) {
                removedListens.add(listen);
            }
        }
        for (OutstandingListen toRemove : removedListens) {
            this.listens.remove(toRemove.getQuery());
        }
        return removedListens;
    }

    private void onDataPush(String action, Map<String, Object> body) {
        String pathString;
        Long tagNumber;
        Tag tag;
        if (this.logger.logsDebug()) {
            this.logger.debug("handleServerMessage: " + action + MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR + body);
        }
        if (!action.equals(SERVER_RESPONSE_DATA)) {
            if (!action.equals(SERVER_DATA_RANGE_MERGE)) {
                if (action.equals(SERVER_ASYNC_DATA_RANGE_MERGE)) {
                    pathString = (String) body.get(SERVER_DATA_UPDATE_PATH);
                    List<Map<String, Object>> payloadData = body.get(SERVER_RESPONSE_DATA);
                    tagNumber = Utilities.longFromObject(body.get(SERVER_DATA_TAG));
                    tag = tagNumber != null ? new Tag(tagNumber.longValue()) : null;
                    List<Map<String, Object>> ranges = payloadData;
                    List<RangeMerge> rangeMerges = new ArrayList();
                    for (Map<String, Object> range : ranges) {
                        String startString = (String) range.get(SERVER_DATA_START_PATH);
                        String endString = (String) range.get(SERVER_DATA_END_PATH);
                        rangeMerges.add(new RangeMerge(startString != null ? new Path(startString) : null, endString != null ? new Path(endString) : null, NodeUtilities.NodeFromJSON(range.get(SERVER_DATA_RANGE_MERGE))));
                    }
                    if (rangeMerges.isEmpty()) {
                        if (this.logger.logsDebug()) {
                            this.logger.debug("Ignoring empty range merge for path " + pathString);
                            return;
                        }
                        return;
                    }
                    this.delegate.onRangeMergeUpdate(new Path(pathString), rangeMerges, tag);
                    return;
                }
                if (action.equals(SERVER_ASYNC_LISTEN_CANCELLED)) {
                    onListenRevoked(new Path((String) body.get(SERVER_DATA_UPDATE_PATH)));
                    return;
                }
                if (action.equals(SERVER_ASYNC_AUTH_REVOKED)) {
                    onAuthRevoked((String) body.get(SERVER_DATA_START_PATH), (String) body.get(SERVER_RESPONSE_DATA));
                    return;
                }
                if (action.equals(SERVER_ASYNC_SECURITY_DEBUG)) {
                    onSecurityDebugPacket(body);
                    return;
                }
                if (this.logger.logsDebug()) {
                    this.logger.debug("Unrecognized action from server: " + action);
                    return;
                }
                return;
            }
        }
        boolean isMerge = action.equals(SERVER_DATA_RANGE_MERGE);
        pathString = (String) body.get(SERVER_DATA_UPDATE_PATH);
        Object payloadData2 = body.get(SERVER_RESPONSE_DATA);
        tagNumber = Utilities.longFromObject(body.get(SERVER_DATA_TAG));
        tag = tagNumber != null ? new Tag(tagNumber.longValue()) : null;
        if (isMerge && (payloadData2 instanceof Map) && ((Map) payloadData2).size() == 0) {
            if (this.logger.logsDebug()) {
                this.logger.debug("ignoring empty merge for path " + pathString);
                return;
            }
            return;
        }
        this.delegate.onDataUpdate(pathString, payloadData2, isMerge, tag);
    }

    private void onListenRevoked(Path path) {
        Collection<OutstandingListen> listens = removeListens(path);
        if (listens != null) {
            FirebaseError error = FirebaseError.fromStatus("permission_denied");
            for (OutstandingListen listen : listens) {
                listen.resultListener.onRequestResult(error);
            }
        }
    }

    private void onAuthRevoked(String status, String reason) {
        if (this.authCredential != null) {
            this.authCredential.onRevoked(FirebaseError.fromStatus(status, reason));
            this.authCredential = null;
        }
    }

    private void onSecurityDebugPacket(Map<String, Object> message) {
        this.logger.info((String) message.get(NotificationCompatApi24.CATEGORY_MESSAGE));
    }

    private void sendAuth() {
        sendAuthHelper($assertionsDisabled);
    }

    private void sendAuthAndRestoreWrites() {
        sendAuthHelper(true);
    }

    private void sendAuthHelper(boolean restoreWritesAfterComplete) {
        if (!$assertionsDisabled && !connected()) {
            throw new AssertionError("Must be connected to send auth.");
        } else if ($assertionsDisabled || this.authCredential != null) {
            Map<String, Object> request = new HashMap();
            request.put(REQUEST_CREDENTIAL, this.authCredential.getCredential());
            sendAction(REQUEST_ACTION_AUTH, request, new C10944(this.authCredential, restoreWritesAfterComplete));
        } else {
            throw new AssertionError("Can't send auth if it's null.");
        }
    }

    private void restoreState() {
        if (this.logger.logsDebug()) {
            this.logger.debug("calling restore state");
        }
        if (this.authCredential != null) {
            if (this.logger.logsDebug()) {
                this.logger.debug("Restoring auth.");
            }
            this.connectionState = ConnectionState.Authenticating;
            sendAuthAndRestoreWrites();
        } else {
            this.connectionState = ConnectionState.Connected;
        }
        if (this.logger.logsDebug()) {
            this.logger.debug("Restoring outstanding listens");
        }
        for (OutstandingListen listen : this.listens.values()) {
            if (this.logger.logsDebug()) {
                this.logger.debug("Restoring listen " + listen.getQuery());
            }
            sendListen(listen);
        }
        if (this.connectionState == ConnectionState.Connected) {
            restoreWrites();
        }
    }

    private void restoreWrites() {
        if (!$assertionsDisabled && this.connectionState != ConnectionState.Connected) {
            throw new AssertionError("Should be connected if we're restoring writes.");
        } else if (!this.writesPaused) {
            if (this.logger.logsDebug()) {
                this.logger.debug("Restoring writes.");
            }
            ArrayList<Long> outstanding = new ArrayList(this.outstandingPuts.keySet());
            Collections.sort(outstanding);
            Iterator i$ = outstanding.iterator();
            while (i$.hasNext()) {
                sendPut(((Long) i$.next()).longValue());
            }
            for (OutstandingDisconnect disconnect : this.onDisconnectRequestQueue) {
                sendOnDisconnect(disconnect.getAction(), disconnect.getPath(), disconnect.getData(), disconnect.getOnComplete());
            }
            this.onDisconnectRequestQueue.clear();
        } else if (this.logger.logsDebug()) {
            this.logger.debug("Writes are paused; skip restoring writes.");
        }
    }

    private void handleTimestamp(long timestamp) {
        if (this.logger.logsDebug()) {
            this.logger.debug("handling timestamp");
        }
        long timestampDelta = timestamp - System.currentTimeMillis();
        Map<ChildKey, Object> updates = new HashMap();
        updates.put(Constants.DOT_INFO_SERVERTIME_OFFSET, Long.valueOf(timestampDelta));
        this.delegate.onServerInfoUpdate(updates);
    }

    private Map<String, Object> getPutObject(String pathString, Object data, String hash) {
        Map<String, Object> request = new HashMap();
        request.put(SERVER_DATA_UPDATE_PATH, pathString);
        request.put(SERVER_RESPONSE_DATA, data);
        if (hash != null) {
            request.put(REQUEST_DATA_HASH, hash);
        }
        return request;
    }

    private void putInternal(String action, String pathString, Object data, String hash, CompletionListener onComplete) {
        Map<String, Object> request = getPutObject(pathString, data, hash);
        long writeId = this.writeCounter;
        this.writeCounter = 1 + writeId;
        this.outstandingPuts.put(Long.valueOf(writeId), new OutstandingPut(request, onComplete, null));
        if (canSendWrites()) {
            sendPut(writeId);
        }
    }

    private void sendPut(long putId) {
        if ($assertionsDisabled || canSendWrites()) {
            OutstandingPut put = (OutstandingPut) this.outstandingPuts.get(Long.valueOf(putId));
            CompletionListener onComplete = put.getOnComplete();
            String action = put.getAction();
            sendAction(action, put.getRequest(), new C10955(action, putId, put, onComplete));
            return;
        }
        throw new AssertionError("sendPut called when we can't send writes (we're disconnected or writes are paused).");
    }

    private void sendListen(OutstandingListen listen) {
        Map<String, Object> request = new HashMap();
        request.put(SERVER_DATA_UPDATE_PATH, listen.getQuery().getPath().toString());
        Tag tag = listen.getTag();
        if (tag != null) {
            request.put(REQUEST_QUERIES, listen.getQuery().getParams().getWireProtocolParams());
            request.put(SERVER_DATA_TAG, Long.valueOf(tag.getTagNumber()));
        }
        SyncTreeHash hashFunction = listen.getHashFunction();
        request.put(REQUEST_DATA_HASH, hashFunction.getSimpleHash());
        if (hashFunction.shouldIncludeCompoundHash()) {
            CompoundHash compoundHash = hashFunction.getCompoundHash();
            List<String> posts = new ArrayList();
            for (Path path : compoundHash.getPosts()) {
                posts.add(path.wireFormat());
            }
            Map<String, Object> hash = new HashMap();
            hash.put(REQUEST_COMPOUND_HASH_HASHES, compoundHash.getHashes());
            hash.put(REQUEST_COMPOUND_HASH_PATHS, posts);
            request.put(REQUEST_COMPOUND_HASH, hash);
        }
        sendAction(REQUEST_QUERIES, request, new C10966(listen));
    }

    private void sendStats(Map<String, Integer> stats) {
        if (!stats.isEmpty()) {
            Map<String, Object> request = new HashMap();
            request.put(SERVER_ASYNC_LISTEN_CANCELLED, stats);
            sendAction(SERVER_DATA_START_PATH, request, new C10977());
        } else if (this.logger.logsDebug()) {
            this.logger.debug("Not sending stats because stats are empty");
        }
    }

    private void warnOnListenerWarnings(List<String> warnings, QuerySpec query) {
        if (warnings.contains("no_index")) {
            this.logger.warn("Using an unspecified index. Consider adding '" + ("\".indexOn\": \"" + query.getIndex().getQueryDefinition() + '\"') + "' at " + query.getPath() + " to your security and Firebase rules for better performance");
        }
    }

    private void sendConnectStats() {
        Map<String, Integer> stats = new HashMap();
        if (AndroidSupport.isAndroid()) {
            if (this.ctx.isPersistenceEnabled()) {
                stats.put("persistence.android.enabled", Integer.valueOf(1));
            }
            stats.put("sdk.android." + Firebase.getSdkVersion().replace('.', '-'), Integer.valueOf(1));
        } else if ($assertionsDisabled || !this.ctx.isPersistenceEnabled()) {
            stats.put("sdk.java." + Firebase.getSdkVersion().replace('.', '-'), Integer.valueOf(1));
        } else {
            throw new AssertionError("Stats for persistence on JVM missing (persistence not yet supported)");
        }
        if (this.logger.logsDebug()) {
            this.logger.debug("Sending first connection stats");
        }
        sendStats(stats);
    }

    private void sendAction(String action, Map<String, Object> message, ResponseListener onResponse) {
        long rn = nextRequestNumber();
        Map<String, Object> request = new HashMap();
        request.put(REQUEST_NUMBER, Long.valueOf(rn));
        request.put(SERVER_ASYNC_ACTION, action);
        request.put(SERVER_ASYNC_PAYLOAD, message);
        this.realtime.sendRequest(request);
        this.requestCBHash.put(Long.valueOf(rn), onResponse);
    }

    private long nextRequestNumber() {
        long j = this.requestCounter;
        this.requestCounter = 1 + j;
        return j;
    }
}
