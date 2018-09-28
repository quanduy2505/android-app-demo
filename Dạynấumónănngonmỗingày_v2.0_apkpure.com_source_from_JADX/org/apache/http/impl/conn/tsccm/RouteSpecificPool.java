package org.apache.http.impl.conn.tsccm;

import java.io.IOException;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Queue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.conn.params.ConnPerRoute;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.util.LangUtils;

@NotThreadSafe
public class RouteSpecificPool {
    protected final ConnPerRoute connPerRoute;
    protected final LinkedList<BasicPoolEntry> freeEntries;
    private final Log log;
    @Deprecated
    protected final int maxEntries;
    protected int numEntries;
    protected final HttpRoute route;
    protected final Queue<WaitingThread> waitingThreads;

    /* renamed from: org.apache.http.impl.conn.tsccm.RouteSpecificPool.1 */
    class C12371 implements ConnPerRoute {
        C12371() {
        }

        public int getMaxForRoute(HttpRoute route) {
            return RouteSpecificPool.this.maxEntries;
        }
    }

    @Deprecated
    public RouteSpecificPool(HttpRoute route, int maxEntries) {
        this.log = LogFactory.getLog(getClass());
        this.route = route;
        this.maxEntries = maxEntries;
        this.connPerRoute = new C12371();
        this.freeEntries = new LinkedList();
        this.waitingThreads = new LinkedList();
        this.numEntries = 0;
    }

    public RouteSpecificPool(HttpRoute route, ConnPerRoute connPerRoute) {
        this.log = LogFactory.getLog(getClass());
        this.route = route;
        this.connPerRoute = connPerRoute;
        this.maxEntries = connPerRoute.getMaxForRoute(route);
        this.freeEntries = new LinkedList();
        this.waitingThreads = new LinkedList();
        this.numEntries = 0;
    }

    public final HttpRoute getRoute() {
        return this.route;
    }

    public final int getMaxEntries() {
        return this.maxEntries;
    }

    public boolean isUnused() {
        return this.numEntries < 1 && this.waitingThreads.isEmpty();
    }

    public int getCapacity() {
        return this.connPerRoute.getMaxForRoute(this.route) - this.numEntries;
    }

    public final int getEntryCount() {
        return this.numEntries;
    }

    public BasicPoolEntry allocEntry(Object state) {
        BasicPoolEntry entry;
        if (!this.freeEntries.isEmpty()) {
            ListIterator<BasicPoolEntry> it = this.freeEntries.listIterator(this.freeEntries.size());
            while (it.hasPrevious()) {
                entry = (BasicPoolEntry) it.previous();
                if (entry.getState() != null) {
                    if (LangUtils.equals(state, entry.getState())) {
                    }
                }
                it.remove();
                return entry;
            }
        }
        if (getCapacity() != 0 || this.freeEntries.isEmpty()) {
            return null;
        }
        entry = (BasicPoolEntry) this.freeEntries.remove();
        entry.shutdownEntry();
        try {
            entry.getConnection().close();
            return entry;
        } catch (IOException ex) {
            this.log.debug("I/O error closing connection", ex);
            return entry;
        }
    }

    public void freeEntry(BasicPoolEntry entry) {
        if (this.numEntries < 1) {
            throw new IllegalStateException("No entry created for this pool. " + this.route);
        } else if (this.numEntries <= this.freeEntries.size()) {
            throw new IllegalStateException("No entry allocated from this pool. " + this.route);
        } else {
            this.freeEntries.add(entry);
        }
    }

    public void createdEntry(BasicPoolEntry entry) {
        if (this.route.equals(entry.getPlannedRoute())) {
            this.numEntries++;
            return;
        }
        throw new IllegalArgumentException("Entry not planned for this pool.\npool: " + this.route + "\nplan: " + entry.getPlannedRoute());
    }

    public boolean deleteEntry(BasicPoolEntry entry) {
        boolean found = this.freeEntries.remove(entry);
        if (found) {
            this.numEntries--;
        }
        return found;
    }

    public void dropEntry() {
        if (this.numEntries < 1) {
            throw new IllegalStateException("There is no entry that could be dropped.");
        }
        this.numEntries--;
    }

    public void queueThread(WaitingThread wt) {
        if (wt == null) {
            throw new IllegalArgumentException("Waiting thread must not be null.");
        }
        this.waitingThreads.add(wt);
    }

    public boolean hasThread() {
        return !this.waitingThreads.isEmpty();
    }

    public WaitingThread nextThread() {
        return (WaitingThread) this.waitingThreads.peek();
    }

    public void removeThread(WaitingThread wt) {
        if (wt != null) {
            this.waitingThreads.remove(wt);
        }
    }
}
