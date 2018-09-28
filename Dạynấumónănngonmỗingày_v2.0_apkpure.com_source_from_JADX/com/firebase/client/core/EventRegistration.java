package com.firebase.client.core;

import com.firebase.client.FirebaseError;
import com.firebase.client.annotations.NotNull;
import com.firebase.client.core.view.Change;
import com.firebase.client.core.view.DataEvent;
import com.firebase.client.core.view.Event.EventType;
import com.firebase.client.core.view.QuerySpec;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class EventRegistration {
    static final /* synthetic */ boolean $assertionsDisabled;
    private boolean isUserInitiated;
    private EventRegistrationZombieListener listener;
    private AtomicBoolean zombied;

    public abstract EventRegistration clone(QuerySpec querySpec);

    public abstract DataEvent createEvent(Change change, QuerySpec querySpec);

    public abstract void fireCancelEvent(FirebaseError firebaseError);

    public abstract void fireEvent(DataEvent dataEvent);

    @NotNull
    public abstract QuerySpec getQuerySpec();

    public abstract boolean isSameListener(EventRegistration eventRegistration);

    public abstract boolean respondsTo(EventType eventType);

    static {
        $assertionsDisabled = !EventRegistration.class.desiredAssertionStatus();
    }

    public EventRegistration() {
        this.zombied = new AtomicBoolean(false);
        this.isUserInitiated = false;
    }

    public void zombify() {
        if (this.zombied.compareAndSet(false, true) && this.listener != null) {
            this.listener.onZombied(this);
            this.listener = null;
        }
    }

    public boolean isZombied() {
        return this.zombied.get();
    }

    public void setOnZombied(EventRegistrationZombieListener listener) {
        if (!$assertionsDisabled && isZombied()) {
            throw new AssertionError();
        } else if ($assertionsDisabled || this.listener == null) {
            this.listener = listener;
        } else {
            throw new AssertionError();
        }
    }

    public boolean isUserInitiated() {
        return this.isUserInitiated;
    }

    public void setIsUserInitiated(boolean isUserInitiated) {
        this.isUserInitiated = isUserInitiated;
    }

    Repo getRepo() {
        return null;
    }
}
