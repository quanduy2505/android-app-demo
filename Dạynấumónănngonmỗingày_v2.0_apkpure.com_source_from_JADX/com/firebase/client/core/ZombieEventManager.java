package com.firebase.client.core;

import com.firebase.client.annotations.NotNull;
import com.firebase.client.core.view.QuerySpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class ZombieEventManager implements EventRegistrationZombieListener {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static ZombieEventManager defaultInstance;
    final HashMap<EventRegistration, List<EventRegistration>> globalEventRegistrations;

    static {
        $assertionsDisabled = !ZombieEventManager.class.desiredAssertionStatus();
        defaultInstance = new ZombieEventManager();
    }

    private ZombieEventManager() {
        this.globalEventRegistrations = new HashMap();
    }

    @NotNull
    public static ZombieEventManager getInstance() {
        return defaultInstance;
    }

    public void recordEventRegistration(EventRegistration registration) {
        synchronized (this.globalEventRegistrations) {
            List<EventRegistration> registrationList = (List) this.globalEventRegistrations.get(registration);
            if (registrationList == null) {
                registrationList = new ArrayList();
                this.globalEventRegistrations.put(registration, registrationList);
            }
            registrationList.add(registration);
            if (!registration.getQuerySpec().isDefault()) {
                EventRegistration defaultRegistration = registration.clone(QuerySpec.defaultQueryAtPath(registration.getQuerySpec().getPath()));
                registrationList = (List) this.globalEventRegistrations.get(defaultRegistration);
                if (registrationList == null) {
                    registrationList = new ArrayList();
                    this.globalEventRegistrations.put(defaultRegistration, registrationList);
                }
                registrationList.add(registration);
            }
            registration.setIsUserInitiated(true);
            registration.setOnZombied(this);
        }
    }

    private void unRecordEventRegistration(EventRegistration zombiedRegistration) {
        synchronized (this.globalEventRegistrations) {
            int i;
            boolean found = false;
            List<EventRegistration> registrationList = (List) this.globalEventRegistrations.get(zombiedRegistration);
            if (registrationList != null) {
                for (i = 0; i < registrationList.size(); i++) {
                    if (registrationList.get(i) == zombiedRegistration) {
                        found = true;
                        registrationList.remove(i);
                        break;
                    }
                }
                if (registrationList.isEmpty()) {
                    this.globalEventRegistrations.remove(zombiedRegistration);
                }
            }
            if ($assertionsDisabled || found || !zombiedRegistration.isUserInitiated()) {
                if (!zombiedRegistration.getQuerySpec().isDefault()) {
                    EventRegistration defaultRegistration = zombiedRegistration.clone(QuerySpec.defaultQueryAtPath(zombiedRegistration.getQuerySpec().getPath()));
                    registrationList = (List) this.globalEventRegistrations.get(defaultRegistration);
                    if (registrationList != null) {
                        for (i = 0; i < registrationList.size(); i++) {
                            if (registrationList.get(i) == zombiedRegistration) {
                                registrationList.remove(i);
                                break;
                            }
                        }
                        if (registrationList.isEmpty()) {
                            this.globalEventRegistrations.remove(defaultRegistration);
                        }
                    }
                }
            } else {
                throw new AssertionError();
            }
        }
    }

    public void zombifyForRemove(EventRegistration registration) {
        synchronized (this.globalEventRegistrations) {
            List<EventRegistration> registrationList = (List) this.globalEventRegistrations.get(registration);
            if (!(registrationList == null || registrationList.isEmpty())) {
                if (registration.getQuerySpec().isDefault()) {
                    HashSet<QuerySpec> zombiedQueries = new HashSet();
                    for (int i = registrationList.size() - 1; i >= 0; i--) {
                        EventRegistration currentRegistration = (EventRegistration) registrationList.get(i);
                        if (!zombiedQueries.contains(currentRegistration.getQuerySpec())) {
                            zombiedQueries.add(currentRegistration.getQuerySpec());
                            currentRegistration.zombify();
                        }
                    }
                } else {
                    ((EventRegistration) registrationList.get(0)).zombify();
                }
            }
        }
    }

    public void onZombied(EventRegistration zombiedInstance) {
        unRecordEventRegistration(zombiedInstance);
    }
}
