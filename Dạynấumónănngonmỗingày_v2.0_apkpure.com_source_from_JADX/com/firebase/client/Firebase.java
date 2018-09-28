package com.firebase.client;

import com.firebase.client.Transaction.Handler;
import com.firebase.client.authentication.AuthenticationManager;
import com.firebase.client.core.CompoundWrite;
import com.firebase.client.core.Context;
import com.firebase.client.core.Path;
import com.firebase.client.core.Repo;
import com.firebase.client.core.RepoManager;
import com.firebase.client.core.ValidationPath;
import com.firebase.client.core.view.QueryParams;
import com.firebase.client.snapshot.ChildKey;
import com.firebase.client.snapshot.Node;
import com.firebase.client.snapshot.NodeUtilities;
import com.firebase.client.snapshot.PriorityUtilities;
import com.firebase.client.utilities.ParsedUrl;
import com.firebase.client.utilities.PushIdGenerator;
import com.firebase.client.utilities.Utilities;
import com.firebase.client.utilities.Validation;
import com.firebase.client.utilities.encoding.JsonHelpers;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import org.apache.http.protocol.HTTP;
import rx.android.BuildConfig;

public class Firebase extends Query {
    private static Config defaultConfig;

    /* renamed from: com.firebase.client.Firebase.1 */
    class C05111 implements Runnable {
        final /* synthetic */ CompletionListener val$listener;
        final /* synthetic */ Node val$node;

        C05111(Node node, CompletionListener completionListener) {
            this.val$node = node;
            this.val$listener = completionListener;
        }

        public void run() {
            Firebase.this.repo.setValue(Firebase.this.getPath(), this.val$node, this.val$listener);
        }
    }

    /* renamed from: com.firebase.client.Firebase.2 */
    class C05122 implements Runnable {
        final /* synthetic */ CompletionListener val$listener;
        final /* synthetic */ Node val$priority;

        C05122(Node node, CompletionListener completionListener) {
            this.val$priority = node;
            this.val$listener = completionListener;
        }

        public void run() {
            Firebase.this.repo.setValue(Firebase.this.getPath().child(ChildKey.getPriorityKey()), this.val$priority, this.val$listener);
        }
    }

    /* renamed from: com.firebase.client.Firebase.3 */
    class C05133 implements Runnable {
        final /* synthetic */ CompletionListener val$listener;
        final /* synthetic */ CompoundWrite val$merge;
        final /* synthetic */ Map val$update;

        C05133(CompoundWrite compoundWrite, CompletionListener completionListener, Map map) {
            this.val$merge = compoundWrite;
            this.val$listener = completionListener;
            this.val$update = map;
        }

        public void run() {
            Firebase.this.repo.updateChildren(Firebase.this.getPath(), this.val$merge, this.val$listener, this.val$update);
        }
    }

    /* renamed from: com.firebase.client.Firebase.4 */
    class C05144 implements Runnable {
        final /* synthetic */ boolean val$fireLocalEvents;
        final /* synthetic */ Handler val$handler;

        C05144(Handler handler, boolean z) {
            this.val$handler = handler;
            this.val$fireLocalEvents = z;
        }

        public void run() {
            Firebase.this.repo.startTransaction(Firebase.this.getPath(), this.val$handler, this.val$fireLocalEvents);
        }
    }

    /* renamed from: com.firebase.client.Firebase.5 */
    class C05155 implements Runnable {
        final /* synthetic */ boolean val$hijackHash;

        C05155(boolean z) {
            this.val$hijackHash = z;
        }

        public void run() {
            Firebase.this.repo.setHijackHash(this.val$hijackHash);
        }
    }

    @Deprecated
    public interface AuthListener {
        void onAuthError(FirebaseError firebaseError);

        void onAuthRevoked(FirebaseError firebaseError);

        void onAuthSuccess(Object obj);
    }

    public interface AuthResultHandler {
        void onAuthenticated(AuthData authData);

        void onAuthenticationError(FirebaseError firebaseError);
    }

    public interface AuthStateListener {
        void onAuthStateChanged(AuthData authData);
    }

    public interface CompletionListener {
        void onComplete(FirebaseError firebaseError, Firebase firebase);
    }

    public interface ResultHandler {
        void onError(FirebaseError firebaseError);

        void onSuccess();
    }

    public interface ValueResultHandler<T> {
        void onError(FirebaseError firebaseError);

        void onSuccess(T t);
    }

    private AuthenticationManager getAuthenticationManager() {
        return getRepo().getAuthenticationManager();
    }

    public Firebase(String url) {
        this(Utilities.parseUrl(url));
    }

    public Firebase(Repo repo, Path path) {
        super(repo, path);
    }

    Firebase(String url, Config config) {
        this(Utilities.parseUrl(url), config);
    }

    private Firebase(ParsedUrl parsedUrl, Config config) {
        super(RepoManager.getRepo(config, parsedUrl.repoInfo), parsedUrl.path, QueryParams.DEFAULT_PARAMS, false);
    }

    private Firebase(ParsedUrl parsedUrl) {
        this(parsedUrl, getDefaultConfig());
    }

    public Firebase child(String pathString) {
        if (pathString == null) {
            throw new NullPointerException("Can't pass null for argument 'pathString' in child()");
        }
        if (getPath().isEmpty()) {
            Validation.validateRootPathString(pathString);
        } else {
            Validation.validatePathString(pathString);
        }
        return new Firebase(this.repo, getPath().child(new Path(pathString)));
    }

    public Firebase push() {
        return new Firebase(this.repo, getPath().child(ChildKey.fromString(PushIdGenerator.generatePushChildName(this.repo.getServerTime()))));
    }

    public void setValue(Object value) {
        setValueInternal(value, PriorityUtilities.parsePriority(null), null);
    }

    public void setValue(Object value, Object priority) {
        setValueInternal(value, PriorityUtilities.parsePriority(priority), null);
    }

    public void setValue(Object value, CompletionListener listener) {
        setValueInternal(value, PriorityUtilities.parsePriority(null), listener);
    }

    public void setValue(Object value, Object priority, CompletionListener listener) {
        setValueInternal(value, PriorityUtilities.parsePriority(priority), listener);
    }

    private void setValueInternal(Object value, Node priority, CompletionListener listener) {
        Validation.validateWritablePath(getPath());
        ValidationPath.validateWithObject(getPath(), value);
        try {
            Object bouncedValue = JsonHelpers.getMapper().convertValue(value, Object.class);
            Validation.validateWritableObject(bouncedValue);
            this.repo.scheduleNow(new C05111(NodeUtilities.NodeFromJSON(bouncedValue, priority), listener));
        } catch (IllegalArgumentException e) {
            throw new FirebaseException("Failed to parse to snapshot", e);
        }
    }

    public void setPriority(Object priority) {
        setPriorityInternal(PriorityUtilities.parsePriority(priority), null);
    }

    public void setPriority(Object priority, CompletionListener listener) {
        setPriorityInternal(PriorityUtilities.parsePriority(priority), listener);
    }

    private void setPriorityInternal(Node priority, CompletionListener listener) {
        Validation.validateWritablePath(getPath());
        this.repo.scheduleNow(new C05122(priority, listener));
    }

    public void updateChildren(Map<String, Object> update) {
        updateChildren(update, null);
    }

    public void updateChildren(Map<String, Object> update, CompletionListener listener) {
        if (update == null) {
            throw new NullPointerException("Can't pass null for argument 'update' in updateChildren()");
        }
        this.repo.scheduleNow(new C05133(CompoundWrite.fromPathMerge(Validation.parseAndValidateUpdate(getPath(), update)), listener, update));
    }

    public void removeValue() {
        setValue(null);
    }

    public void removeValue(CompletionListener listener) {
        setValue(null, listener);
    }

    public OnDisconnect onDisconnect() {
        Validation.validateWritablePath(getPath());
        return new OnDisconnect(this.repo, getPath());
    }

    @Deprecated
    public void auth(String credential, AuthListener listener) {
        if (listener == null) {
            throw new NullPointerException("Can't pass null for argument 'listener' in auth()");
        } else if (credential == null) {
            throw new NullPointerException("Can't pass null for argument 'credential' in auth()");
        } else {
            getAuthenticationManager().authWithFirebaseToken(credential, listener);
        }
    }

    public void unauth() {
        getAuthenticationManager().unauth();
    }

    @Deprecated
    public void unauth(CompletionListener listener) {
        if (listener == null) {
            throw new NullPointerException("Can't pass null for argument 'listener' in unauth()");
        }
        getAuthenticationManager().unauth(listener);
    }

    public AuthStateListener addAuthStateListener(AuthStateListener listener) {
        if (listener == null) {
            throw new NullPointerException("Can't pass null for argument 'listener' in addAuthStateListener()");
        }
        getAuthenticationManager().addAuthStateListener(listener);
        return listener;
    }

    public void removeAuthStateListener(AuthStateListener listener) {
        if (listener == null) {
            throw new NullPointerException("Can't pass null for argument 'listener' in removeAuthStateListener()");
        }
        getAuthenticationManager().removeAuthStateListener(listener);
    }

    public AuthData getAuth() {
        return getAuthenticationManager().getAuth();
    }

    public void authAnonymously(AuthResultHandler handler) {
        getAuthenticationManager().authAnonymously(handler);
    }

    public void authWithPassword(String email, String password, AuthResultHandler handler) {
        if (email == null) {
            throw new NullPointerException("Can't pass null for argument 'email' in authWithPassword()");
        } else if (password == null) {
            throw new NullPointerException("Can't pass null for argument 'password' in authWithPassword()");
        } else {
            getAuthenticationManager().authWithPassword(email, password, handler);
        }
    }

    public void authWithCustomToken(String token, AuthResultHandler handler) {
        if (token == null) {
            throw new NullPointerException("Can't pass null for argument 'token' in authWithCustomToken()");
        }
        getAuthenticationManager().authWithCustomToken(token, handler);
    }

    public void authWithOAuthToken(String provider, String token, AuthResultHandler handler) {
        if (provider == null) {
            throw new NullPointerException("Can't pass null for argument 'provider' in authWithOAuthToken()");
        } else if (token == null) {
            throw new NullPointerException("Can't pass null for argument 'token' in authWithOAuthToken()");
        } else {
            getAuthenticationManager().authWithOAuthToken(provider, token, handler);
        }
    }

    public void authWithOAuthToken(String provider, Map<String, String> options, AuthResultHandler handler) {
        if (provider == null) {
            throw new NullPointerException("Can't pass null for argument 'provider' in authWithOAuthToken()");
        } else if (options == null) {
            throw new NullPointerException("Can't pass null for argument 'options' in authWithOAuthToken()");
        } else {
            getAuthenticationManager().authWithOAuthToken(provider, (Map) options, handler);
        }
    }

    public void createUser(String email, String password, ResultHandler handler) {
        if (email == null) {
            throw new NullPointerException("Can't pass null for argument 'email' in createUser()");
        } else if (password == null) {
            throw new NullPointerException("Can't pass null for argument 'password' in createUser()");
        } else {
            getAuthenticationManager().createUser(email, password, handler);
        }
    }

    public void createUser(String email, String password, ValueResultHandler<Map<String, Object>> handler) {
        if (email == null) {
            throw new NullPointerException("Can't pass null for argument 'email' in createUser()");
        } else if (password == null) {
            throw new NullPointerException("Can't pass null for argument 'password' in createUser()");
        } else {
            getAuthenticationManager().createUser(email, password, (ValueResultHandler) handler);
        }
    }

    public void removeUser(String email, String password, ResultHandler handler) {
        if (email == null) {
            throw new NullPointerException("Can't pass null for argument 'email' in removeUser()");
        } else if (password == null) {
            throw new NullPointerException("Can't pass null for argument 'password' in removeUser()");
        } else {
            getAuthenticationManager().removeUser(email, password, handler);
        }
    }

    public void changePassword(String email, String oldPassword, String newPassword, ResultHandler handler) {
        if (email == null) {
            throw new NullPointerException("Can't pass null for argument 'email' in changePassword()");
        } else if (oldPassword == null) {
            throw new NullPointerException("Can't pass null for argument 'oldPassword' in changePassword()");
        } else if (newPassword == null) {
            throw new NullPointerException("Can't pass null for argument 'newPassword' in changePassword()");
        } else {
            getAuthenticationManager().changePassword(email, oldPassword, newPassword, handler);
        }
    }

    public void changeEmail(String oldEmail, String password, String newEmail, ResultHandler handler) {
        if (oldEmail == null) {
            throw new NullPointerException("Can't pass null for argument 'oldEmail' in changeEmail()");
        } else if (password == null) {
            throw new NullPointerException("Can't pass null for argument 'password' in changeEmail()");
        } else if (newEmail == null) {
            throw new NullPointerException("Can't pass null for argument 'newEmail' in changeEmail()");
        } else {
            getAuthenticationManager().changeEmail(oldEmail, password, newEmail, handler);
        }
    }

    public void resetPassword(String email, ResultHandler handler) {
        if (email == null) {
            throw new NullPointerException("Can't pass null for argument 'email' in resetPassword()");
        }
        getAuthenticationManager().resetPassword(email, handler);
    }

    public void runTransaction(Handler handler) {
        runTransaction(handler, true);
    }

    public void runTransaction(Handler handler, boolean fireLocalEvents) {
        if (handler == null) {
            throw new NullPointerException("Can't pass null for argument 'handler' in runTransaction()");
        }
        Validation.validateWritablePath(getPath());
        this.repo.scheduleNow(new C05144(handler, fireLocalEvents));
    }

    public static void goOffline() {
        goOffline(getDefaultConfig());
    }

    static void goOffline(Config config) {
        RepoManager.interrupt((Context) config);
    }

    public static void goOnline() {
        goOnline(getDefaultConfig());
    }

    static void goOnline(Config config) {
        RepoManager.resume((Context) config);
    }

    public FirebaseApp getApp() {
        return this.repo.getFirebaseApp();
    }

    public String toString() {
        Firebase parent = getParent();
        if (parent == null) {
            return this.repo.toString();
        }
        try {
            return parent.toString() + "/" + URLEncoder.encode(getKey(), HTTP.UTF_8).replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new FirebaseException("Failed to URLEncode key: " + getKey(), e);
        }
    }

    public Firebase getParent() {
        Path parentPath = getPath().getParent();
        if (parentPath != null) {
            return new Firebase(this.repo, parentPath);
        }
        return null;
    }

    public Firebase getRoot() {
        return new Firebase(this.repo, new Path(BuildConfig.VERSION_NAME));
    }

    public String getKey() {
        if (getPath().isEmpty()) {
            return null;
        }
        return getPath().getBack().asString();
    }

    public boolean equals(Object other) {
        return (other instanceof Firebase) && toString().equals(other.toString());
    }

    public int hashCode() {
        return toString().hashCode();
    }

    public static String getSdkVersion() {
        return Version.SDK_VERSION;
    }

    void setHijackHash(boolean hijackHash) {
        this.repo.scheduleNow(new C05155(hijackHash));
    }

    public static synchronized Config getDefaultConfig() {
        Config config;
        synchronized (Firebase.class) {
            if (defaultConfig == null) {
                defaultConfig = new Config();
            }
            config = defaultConfig;
        }
        return config;
    }

    public static synchronized void setDefaultConfig(Config config) {
        synchronized (Firebase.class) {
            if (defaultConfig == null || !defaultConfig.isFrozen()) {
                defaultConfig = config;
            } else {
                throw new FirebaseException("Modifications to Config objects must occur before they are in use");
            }
        }
    }

    public static void setAndroidContext(android.content.Context context) {
        if (context == null) {
            throw new NullPointerException("Can't pass null for argument 'context' in setAndroidContext()");
        }
        Context.setAndroidContext(context);
    }
}
