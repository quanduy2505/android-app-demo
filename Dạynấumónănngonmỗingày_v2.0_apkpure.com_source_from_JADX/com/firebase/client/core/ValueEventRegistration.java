package com.firebase.client.core;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.client.annotations.NotNull;
import com.firebase.client.core.view.Change;
import com.firebase.client.core.view.DataEvent;
import com.firebase.client.core.view.Event.EventType;
import com.firebase.client.core.view.QuerySpec;

public class ValueEventRegistration extends EventRegistration {
    private final ValueEventListener eventListener;
    private final Repo repo;
    private final QuerySpec spec;

    public ValueEventRegistration(Repo repo, ValueEventListener eventListener, @NotNull QuerySpec spec) {
        this.repo = repo;
        this.eventListener = eventListener;
        this.spec = spec;
    }

    public boolean respondsTo(EventType eventType) {
        return eventType == EventType.VALUE;
    }

    public boolean equals(Object other) {
        return (other instanceof ValueEventRegistration) && ((ValueEventRegistration) other).eventListener.equals(this.eventListener) && ((ValueEventRegistration) other).repo.equals(this.repo) && ((ValueEventRegistration) other).spec.equals(this.spec);
    }

    public int hashCode() {
        return (((this.eventListener.hashCode() * 31) + this.repo.hashCode()) * 31) + this.spec.hashCode();
    }

    public DataEvent createEvent(Change change, QuerySpec query) {
        return new DataEvent(EventType.VALUE, this, new DataSnapshot(new Firebase(this.repo, query.getPath()), change.getIndexedNode()), null);
    }

    public void fireEvent(DataEvent eventData) {
        if (!isZombied()) {
            this.eventListener.onDataChange(eventData.getSnapshot());
        }
    }

    public void fireCancelEvent(FirebaseError error) {
        this.eventListener.onCancelled(error);
    }

    public EventRegistration clone(QuerySpec newQuery) {
        return new ValueEventRegistration(this.repo, this.eventListener, newQuery);
    }

    public boolean isSameListener(EventRegistration other) {
        return (other instanceof ValueEventRegistration) && ((ValueEventRegistration) other).eventListener.equals(this.eventListener);
    }

    @NotNull
    public QuerySpec getQuerySpec() {
        return this.spec;
    }

    public String toString() {
        return "ValueEventRegistration";
    }

    Repo getRepo() {
        return this.repo;
    }
}
