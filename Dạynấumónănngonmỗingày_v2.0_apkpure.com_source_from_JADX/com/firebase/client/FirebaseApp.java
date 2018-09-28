package com.firebase.client;

import com.firebase.client.core.Repo;
import com.firebase.client.core.RepoManager;

public class FirebaseApp {
    private final Repo repo;

    /* renamed from: com.firebase.client.FirebaseApp.1 */
    class C05161 implements Runnable {
        C05161() {
        }

        public void run() {
            FirebaseApp.this.repo.purgeOutstandingWrites();
        }
    }

    protected FirebaseApp(Repo repo) {
        this.repo = repo;
    }

    public void purgeOutstandingWrites() {
        this.repo.scheduleNow(new C05161());
    }

    public void goOnline() {
        RepoManager.resume(this.repo);
    }

    public void goOffline() {
        RepoManager.interrupt(this.repo);
    }
}
