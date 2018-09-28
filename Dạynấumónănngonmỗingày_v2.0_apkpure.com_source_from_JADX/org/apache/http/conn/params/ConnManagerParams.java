package org.apache.http.conn.params;

import org.apache.http.annotation.Immutable;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;

@Immutable
@Deprecated
public final class ConnManagerParams implements ConnManagerPNames {
    private static final ConnPerRoute DEFAULT_CONN_PER_ROUTE;
    public static final int DEFAULT_MAX_TOTAL_CONNECTIONS = 20;

    /* renamed from: org.apache.http.conn.params.ConnManagerParams.1 */
    static class C12341 implements ConnPerRoute {
        C12341() {
        }

        public int getMaxForRoute(HttpRoute route) {
            return 2;
        }
    }

    @Deprecated
    public static long getTimeout(HttpParams params) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        }
        Long param = (Long) params.getParameter(ConnManagerPNames.TIMEOUT);
        if (param != null) {
            return param.longValue();
        }
        return (long) params.getIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 0);
    }

    @Deprecated
    public static void setTimeout(HttpParams params, long timeout) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        }
        params.setLongParameter(ConnManagerPNames.TIMEOUT, timeout);
    }

    static {
        DEFAULT_CONN_PER_ROUTE = new C12341();
    }

    @Deprecated
    public static void setMaxConnectionsPerRoute(HttpParams params, ConnPerRoute connPerRoute) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters must not be null.");
        }
        params.setParameter(ConnManagerPNames.MAX_CONNECTIONS_PER_ROUTE, connPerRoute);
    }

    @Deprecated
    public static ConnPerRoute getMaxConnectionsPerRoute(HttpParams params) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters must not be null.");
        }
        ConnPerRoute connPerRoute = (ConnPerRoute) params.getParameter(ConnManagerPNames.MAX_CONNECTIONS_PER_ROUTE);
        if (connPerRoute == null) {
            return DEFAULT_CONN_PER_ROUTE;
        }
        return connPerRoute;
    }

    @Deprecated
    public static void setMaxTotalConnections(HttpParams params, int maxTotalConnections) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters must not be null.");
        }
        params.setIntParameter(ConnManagerPNames.MAX_TOTAL_CONNECTIONS, maxTotalConnections);
    }

    @Deprecated
    public static int getMaxTotalConnections(HttpParams params) {
        if (params != null) {
            return params.getIntParameter(ConnManagerPNames.MAX_TOTAL_CONNECTIONS, DEFAULT_MAX_TOTAL_CONNECTIONS);
        }
        throw new IllegalArgumentException("HTTP parameters must not be null.");
    }
}
