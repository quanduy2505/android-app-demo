package com.firebase.client.core;

import com.firebase.client.FirebaseException;
import com.firebase.client.RunLoop;
import java.util.HashMap;
import java.util.Map;

public class RepoManager {
    private static final RepoManager instance;
    private final Map<Context, Map<String, Repo>> repos;

    /* renamed from: com.firebase.client.core.RepoManager.1 */
    static class C05551 implements Runnable {
        final /* synthetic */ Repo val$repo;

        C05551(Repo repo) {
            this.val$repo = repo;
        }

        public void run() {
            this.val$repo.interrupt();
        }
    }

    /* renamed from: com.firebase.client.core.RepoManager.2 */
    static class C05562 implements Runnable {
        final /* synthetic */ Repo val$repo;

        C05562(Repo repo) {
            this.val$repo = repo;
        }

        public void run() {
            this.val$repo.resume();
        }
    }

    /* renamed from: com.firebase.client.core.RepoManager.3 */
    class C05573 implements Runnable {
        final /* synthetic */ Context val$ctx;

        C05573(Context context) {
            this.val$ctx = context;
        }

        public void run() {
            synchronized (RepoManager.this.repos) {
                boolean allEmpty = true;
                if (RepoManager.this.repos.containsKey(this.val$ctx)) {
                    for (Repo repo : ((Map) RepoManager.this.repos.get(this.val$ctx)).values()) {
                        repo.interrupt();
                        allEmpty = allEmpty && !repo.hasListeners();
                    }
                    if (allEmpty) {
                        this.val$ctx.stop();
                    }
                }
            }
        }
    }

    /* renamed from: com.firebase.client.core.RepoManager.4 */
    class C05584 implements Runnable {
        final /* synthetic */ Context val$ctx;

        C05584(Context context) {
            this.val$ctx = context;
        }

        public void run() {
            synchronized (RepoManager.this.repos) {
                if (RepoManager.this.repos.containsKey(this.val$ctx)) {
                    for (Repo repo : ((Map) RepoManager.this.repos.get(this.val$ctx)).values()) {
                        repo.resume();
                    }
                }
            }
        }
    }

    static {
        instance = new RepoManager();
    }

    public static Repo getRepo(Context ctx, RepoInfo info) throws FirebaseException {
        return instance.getLocalRepo(ctx, info);
    }

    public static void interrupt(Context ctx) {
        instance.interruptInternal(ctx);
    }

    public static void interrupt(Repo repo) {
        repo.scheduleNow(new C05551(repo));
    }

    public static void resume(Repo repo) {
        repo.scheduleNow(new C05562(repo));
    }

    public static void resume(Context ctx) {
        instance.resumeInternal(ctx);
    }

    public RepoManager() {
        this.repos = new HashMap();
    }

    private Repo getLocalRepo(Context ctx, RepoInfo info) throws FirebaseException {
        ctx.freeze();
        String repoHash = "https://" + info.host + "/" + info.namespace;
        synchronized (this.repos) {
            if (!this.repos.containsKey(ctx)) {
                this.repos.put(ctx, new HashMap());
            }
            Map<String, Repo> innerMap = (Map) this.repos.get(ctx);
            if (innerMap.containsKey(repoHash)) {
                Repo repo = (Repo) innerMap.get(repoHash);
                return repo;
            }
            Repo repo2 = new Repo(info, ctx);
            innerMap.put(repoHash, repo2);
            return repo2;
        }
    }

    private void interruptInternal(Context ctx) {
        RunLoop runLoop = ctx.getRunLoop();
        if (runLoop != null) {
            runLoop.scheduleNow(new C05573(ctx));
        }
    }

    private void resumeInternal(Context ctx) {
        RunLoop runLoop = ctx.getRunLoop();
        if (runLoop != null) {
            runLoop.scheduleNow(new C05584(ctx));
        }
    }
}
