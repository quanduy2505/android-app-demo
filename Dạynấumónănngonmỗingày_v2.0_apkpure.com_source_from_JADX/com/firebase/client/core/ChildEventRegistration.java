package com.firebase.client.core;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.annotations.NotNull;
import com.firebase.client.core.view.Change;
import com.firebase.client.core.view.DataEvent;
import com.firebase.client.core.view.Event.EventType;
import com.firebase.client.core.view.QuerySpec;
import com.google.android.gms.common.ConnectionResult;
import rx.internal.operators.OnSubscribeConcatMap;

public class ChildEventRegistration extends EventRegistration {
    private final ChildEventListener eventListener;
    private final Repo repo;
    private final QuerySpec spec;

    /* renamed from: com.firebase.client.core.ChildEventRegistration.1 */
    static /* synthetic */ class C05461 {
        static final /* synthetic */ int[] $SwitchMap$com$firebase$client$core$view$Event$EventType;

        static {
            $SwitchMap$com$firebase$client$core$view$Event$EventType = new int[EventType.values().length];
            try {
                $SwitchMap$com$firebase$client$core$view$Event$EventType[EventType.CHILD_ADDED.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$firebase$client$core$view$Event$EventType[EventType.CHILD_CHANGED.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$firebase$client$core$view$Event$EventType[EventType.CHILD_MOVED.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$firebase$client$core$view$Event$EventType[EventType.CHILD_REMOVED.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    public ChildEventRegistration(@NotNull Repo repo, @NotNull ChildEventListener eventListener, @NotNull QuerySpec spec) {
        this.repo = repo;
        this.eventListener = eventListener;
        this.spec = spec;
    }

    public boolean respondsTo(EventType eventType) {
        return eventType != EventType.VALUE;
    }

    public boolean equals(Object other) {
        return (other instanceof ChildEventRegistration) && ((ChildEventRegistration) other).eventListener.equals(this.eventListener) && ((ChildEventRegistration) other).repo.equals(this.repo) && ((ChildEventRegistration) other).spec.equals(this.spec);
    }

    public int hashCode() {
        return (((this.eventListener.hashCode() * 31) + this.repo.hashCode()) * 31) + this.spec.hashCode();
    }

    public DataEvent createEvent(Change change, QuerySpec query) {
        return new DataEvent(change.getEventType(), this, new DataSnapshot(new Firebase(this.repo, query.getPath().child(change.getChildKey())), change.getIndexedNode()), change.getPrevName() != null ? change.getPrevName().asString() : null);
    }

    public void fireEvent(DataEvent eventData) {
        if (!isZombied()) {
            switch (C05461.$SwitchMap$com$firebase$client$core$view$Event$EventType[eventData.getEventType().ordinal()]) {
                case OnSubscribeConcatMap.BOUNDARY /*1*/:
                    this.eventListener.onChildAdded(eventData.getSnapshot(), eventData.getPreviousName());
                case OnSubscribeConcatMap.END /*2*/:
                    this.eventListener.onChildChanged(eventData.getSnapshot(), eventData.getPreviousName());
                case ConnectionResult.SERVICE_DISABLED /*3*/:
                    this.eventListener.onChildMoved(eventData.getSnapshot(), eventData.getPreviousName());
                case ConnectionResult.SIGN_IN_REQUIRED /*4*/:
                    this.eventListener.onChildRemoved(eventData.getSnapshot());
                default:
            }
        }
    }

    public void fireCancelEvent(FirebaseError error) {
        this.eventListener.onCancelled(error);
    }

    public EventRegistration clone(QuerySpec newQuery) {
        return new ChildEventRegistration(this.repo, this.eventListener, newQuery);
    }

    public boolean isSameListener(EventRegistration other) {
        return (other instanceof ChildEventRegistration) && ((ChildEventRegistration) other).eventListener.equals(this.eventListener);
    }

    @NotNull
    public QuerySpec getQuerySpec() {
        return this.spec;
    }

    public String toString() {
        return "ChildEventRegistration";
    }

    Repo getRepo() {
        return this.repo;
    }
}
