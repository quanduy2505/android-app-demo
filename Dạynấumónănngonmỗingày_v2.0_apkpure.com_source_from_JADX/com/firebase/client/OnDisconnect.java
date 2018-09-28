package com.firebase.client;

import com.firebase.client.Firebase.CompletionListener;
import com.firebase.client.core.Path;
import com.firebase.client.core.Repo;
import com.firebase.client.core.ValidationPath;
import com.firebase.client.snapshot.Node;
import com.firebase.client.snapshot.NodeUtilities;
import com.firebase.client.snapshot.PriorityUtilities;
import com.firebase.client.utilities.Validation;
import com.firebase.client.utilities.encoding.JsonHelpers;
import java.util.Map;

public class OnDisconnect {
    private Path path;
    private Repo repo;

    /* renamed from: com.firebase.client.OnDisconnect.1 */
    class C05211 implements Runnable {
        final /* synthetic */ Node val$node;
        final /* synthetic */ CompletionListener val$onComplete;

        C05211(Node node, CompletionListener completionListener) {
            this.val$node = node;
            this.val$onComplete = completionListener;
        }

        public void run() {
            OnDisconnect.this.repo.onDisconnectSetValue(OnDisconnect.this.path, this.val$node, this.val$onComplete);
        }
    }

    /* renamed from: com.firebase.client.OnDisconnect.2 */
    class C05222 implements Runnable {
        final /* synthetic */ CompletionListener val$listener;
        final /* synthetic */ Map val$parsedUpdate;
        final /* synthetic */ Map val$update;

        C05222(Map map, CompletionListener completionListener, Map map2) {
            this.val$parsedUpdate = map;
            this.val$listener = completionListener;
            this.val$update = map2;
        }

        public void run() {
            OnDisconnect.this.repo.onDisconnectUpdate(OnDisconnect.this.path, this.val$parsedUpdate, this.val$listener, this.val$update);
        }
    }

    /* renamed from: com.firebase.client.OnDisconnect.3 */
    class C05233 implements Runnable {
        final /* synthetic */ CompletionListener val$listener;

        C05233(CompletionListener completionListener) {
            this.val$listener = completionListener;
        }

        public void run() {
            OnDisconnect.this.repo.onDisconnectCancel(OnDisconnect.this.path, this.val$listener);
        }
    }

    OnDisconnect(Repo repo, Path path) {
        this.repo = repo;
        this.path = path;
    }

    public void setValue(Object value) {
        onDisconnectSetInternal(value, PriorityUtilities.NullPriority(), null);
    }

    public void setValue(Object value, String priority) {
        onDisconnectSetInternal(value, PriorityUtilities.parsePriority(priority), null);
    }

    public void setValue(Object value, double priority) {
        onDisconnectSetInternal(value, PriorityUtilities.parsePriority(Double.valueOf(priority)), null);
    }

    public void setValue(Object value, CompletionListener listener) {
        onDisconnectSetInternal(value, PriorityUtilities.NullPriority(), listener);
    }

    public void setValue(Object value, String priority, CompletionListener listener) {
        onDisconnectSetInternal(value, PriorityUtilities.parsePriority(priority), listener);
    }

    public void setValue(Object value, double priority, CompletionListener listener) {
        onDisconnectSetInternal(value, PriorityUtilities.parsePriority(Double.valueOf(priority)), listener);
    }

    public void setValue(Object value, Map priority, CompletionListener listener) {
        onDisconnectSetInternal(value, PriorityUtilities.parsePriority(priority), listener);
    }

    private void onDisconnectSetInternal(Object value, Node priority, CompletionListener onComplete) {
        Validation.validateWritablePath(this.path);
        ValidationPath.validateWithObject(this.path, value);
        try {
            Object bouncedValue = JsonHelpers.getMapper().convertValue(value, Object.class);
            Validation.validateWritableObject(bouncedValue);
            this.repo.scheduleNow(new C05211(NodeUtilities.NodeFromJSON(bouncedValue, priority), onComplete));
        } catch (IllegalArgumentException e) {
            throw new FirebaseException("Failed to parse to snapshot", e);
        }
    }

    public void updateChildren(Map<String, Object> update) {
        updateChildren(update, null);
    }

    public void updateChildren(Map<String, Object> update, CompletionListener listener) {
        this.repo.scheduleNow(new C05222(Validation.parseAndValidateUpdate(this.path, update), listener, update));
    }

    public void removeValue() {
        setValue(null);
    }

    public void removeValue(CompletionListener listener) {
        setValue(null, listener);
    }

    public void cancel() {
        cancel(null);
    }

    public void cancel(CompletionListener listener) {
        this.repo.scheduleNow(new C05233(listener));
    }
}
