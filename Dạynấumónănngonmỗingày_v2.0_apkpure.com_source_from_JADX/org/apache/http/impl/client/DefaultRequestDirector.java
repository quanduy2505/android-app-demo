package org.apache.http.impl.client;

import android.support.v7.widget.GridLayoutManager;
import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;
import com.google.android.gms.common.ConnectionResult;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolException;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.client.AuthenticationHandler;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.RedirectException;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.RequestDirector;
import org.apache.http.client.UserTokenHandler;
import org.apache.http.client.methods.AbortableHttpRequest;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.conn.BasicManagedEntity;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ClientConnectionRequest;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.ManagedClientConnection;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.routing.BasicRouteDirector;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.HttpRouteDirector;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.conn.ConnectionShutdownException;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestExecutor;
import org.apache.http.util.EntityUtils;
import rx.internal.operators.OnSubscribeConcatMap;
import rx.internal.schedulers.NewThreadWorker;

@NotThreadSafe
public class DefaultRequestDirector implements RequestDirector {
    protected final ClientConnectionManager connManager;
    private int execCount;
    protected final HttpProcessor httpProcessor;
    protected final ConnectionKeepAliveStrategy keepAliveStrategy;
    private final Log log;
    protected ManagedClientConnection managedConn;
    private int maxRedirects;
    protected final HttpParams params;
    protected final AuthenticationHandler proxyAuthHandler;
    protected final AuthState proxyAuthState;
    private int redirectCount;
    @Deprecated
    protected final RedirectHandler redirectHandler;
    protected final RedirectStrategy redirectStrategy;
    protected final HttpRequestExecutor requestExec;
    protected final HttpRequestRetryHandler retryHandler;
    protected final ConnectionReuseStrategy reuseStrategy;
    protected final HttpRoutePlanner routePlanner;
    protected final AuthenticationHandler targetAuthHandler;
    protected final AuthState targetAuthState;
    protected final UserTokenHandler userTokenHandler;
    private HttpHost virtualHost;

    @Deprecated
    public DefaultRequestDirector(HttpRequestExecutor requestExec, ClientConnectionManager conman, ConnectionReuseStrategy reustrat, ConnectionKeepAliveStrategy kastrat, HttpRoutePlanner rouplan, HttpProcessor httpProcessor, HttpRequestRetryHandler retryHandler, RedirectHandler redirectHandler, AuthenticationHandler targetAuthHandler, AuthenticationHandler proxyAuthHandler, UserTokenHandler userTokenHandler, HttpParams params) {
        this(LogFactory.getLog(DefaultRequestDirector.class), requestExec, conman, reustrat, kastrat, rouplan, httpProcessor, retryHandler, new DefaultRedirectStrategyAdaptor(redirectHandler), targetAuthHandler, proxyAuthHandler, userTokenHandler, params);
    }

    public DefaultRequestDirector(Log log, HttpRequestExecutor requestExec, ClientConnectionManager conman, ConnectionReuseStrategy reustrat, ConnectionKeepAliveStrategy kastrat, HttpRoutePlanner rouplan, HttpProcessor httpProcessor, HttpRequestRetryHandler retryHandler, RedirectStrategy redirectStrategy, AuthenticationHandler targetAuthHandler, AuthenticationHandler proxyAuthHandler, UserTokenHandler userTokenHandler, HttpParams params) {
        this.redirectHandler = null;
        if (log == null) {
            throw new IllegalArgumentException("Log may not be null.");
        } else if (requestExec == null) {
            throw new IllegalArgumentException("Request executor may not be null.");
        } else if (conman == null) {
            throw new IllegalArgumentException("Client connection manager may not be null.");
        } else if (reustrat == null) {
            throw new IllegalArgumentException("Connection reuse strategy may not be null.");
        } else if (kastrat == null) {
            throw new IllegalArgumentException("Connection keep alive strategy may not be null.");
        } else if (rouplan == null) {
            throw new IllegalArgumentException("Route planner may not be null.");
        } else if (httpProcessor == null) {
            throw new IllegalArgumentException("HTTP protocol processor may not be null.");
        } else if (retryHandler == null) {
            throw new IllegalArgumentException("HTTP request retry handler may not be null.");
        } else if (redirectStrategy == null) {
            throw new IllegalArgumentException("Redirect strategy may not be null.");
        } else if (targetAuthHandler == null) {
            throw new IllegalArgumentException("Target authentication handler may not be null.");
        } else if (proxyAuthHandler == null) {
            throw new IllegalArgumentException("Proxy authentication handler may not be null.");
        } else if (userTokenHandler == null) {
            throw new IllegalArgumentException("User token handler may not be null.");
        } else if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        } else {
            this.log = log;
            this.requestExec = requestExec;
            this.connManager = conman;
            this.reuseStrategy = reustrat;
            this.keepAliveStrategy = kastrat;
            this.routePlanner = rouplan;
            this.httpProcessor = httpProcessor;
            this.retryHandler = retryHandler;
            this.redirectStrategy = redirectStrategy;
            this.targetAuthHandler = targetAuthHandler;
            this.proxyAuthHandler = proxyAuthHandler;
            this.userTokenHandler = userTokenHandler;
            this.params = params;
            this.managedConn = null;
            this.execCount = 0;
            this.redirectCount = 0;
            this.maxRedirects = this.params.getIntParameter(ClientPNames.MAX_REDIRECTS, 100);
            this.targetAuthState = new AuthState();
            this.proxyAuthState = new AuthState();
        }
    }

    private RequestWrapper wrapRequest(HttpRequest request) throws ProtocolException {
        if (request instanceof HttpEntityEnclosingRequest) {
            return new EntityEnclosingRequestWrapper((HttpEntityEnclosingRequest) request);
        }
        return new RequestWrapper(request);
    }

    protected void rewriteRequestURI(RequestWrapper request, HttpRoute route) throws ProtocolException {
        try {
            URI uri = request.getURI();
            if (route.getProxyHost() == null || route.isTunnelled()) {
                if (uri.isAbsolute()) {
                    request.setURI(URIUtils.rewriteURI(uri, null));
                }
            } else if (!uri.isAbsolute()) {
                request.setURI(URIUtils.rewriteURI(uri, route.getTargetHost()));
            }
        } catch (URISyntaxException ex) {
            throw new ProtocolException("Invalid URI: " + request.getRequestLine().getUri(), ex);
        }
    }

    public HttpResponse execute(HttpHost target, HttpRequest request, HttpContext context) throws HttpException, IOException {
        HttpRequest orig = request;
        RequestWrapper origWrapper = wrapRequest(orig);
        origWrapper.setParams(this.params);
        HttpRoute origRoute = determineRoute(target, origWrapper, context);
        this.virtualHost = (HttpHost) orig.getParams().getParameter(ClientPNames.VIRTUAL_HOST);
        if (this.virtualHost != null) {
            if (this.virtualHost.getPort() == -1) {
                int port = target.getPort();
                if (port != -1) {
                    this.virtualHost = new HttpHost(this.virtualHost.getHostName(), port, this.virtualHost.getSchemeName());
                }
            }
        }
        RoutedRequest routedRequest = new RoutedRequest(origWrapper, origRoute);
        boolean reuse = false;
        boolean done = false;
        HttpResponse response = null;
        while (!done) {
            try {
                RoutedRequest roureq;
                RequestWrapper wrapper = roureq.getRequest();
                HttpRoute route = roureq.getRoute();
                Object userToken = context.getAttribute(ClientContext.USER_TOKEN);
                if (this.managedConn == null) {
                    ClientConnectionRequest connRequest = this.connManager.requestConnection(route, userToken);
                    if (orig instanceof AbortableHttpRequest) {
                        ((AbortableHttpRequest) orig).setConnectionRequest(connRequest);
                    }
                    long timeout = ConnManagerParams.getTimeout(this.params);
                    this.managedConn = connRequest.getConnection(timeout, TimeUnit.MILLISECONDS);
                    if (HttpConnectionParams.isStaleCheckingEnabled(this.params)) {
                        if (this.managedConn.isOpen()) {
                            this.log.debug("Stale connection check");
                            if (this.managedConn.isStale()) {
                                this.log.debug("Stale connection detected");
                                this.managedConn.close();
                            }
                        }
                    }
                }
                if (orig instanceof AbortableHttpRequest) {
                    ((AbortableHttpRequest) orig).setReleaseTrigger(this.managedConn);
                }
                try {
                    tryConnect(roureq, context);
                    wrapper.resetHeaders();
                    rewriteRequestURI(wrapper, route);
                    target = this.virtualHost;
                    if (target == null) {
                        target = route.getTargetHost();
                    }
                    HttpHost proxy = route.getProxyHost();
                    context.setAttribute(ExecutionContext.HTTP_TARGET_HOST, target);
                    context.setAttribute(ExecutionContext.HTTP_PROXY_HOST, proxy);
                    context.setAttribute(ExecutionContext.HTTP_CONNECTION, this.managedConn);
                    context.setAttribute(ClientContext.TARGET_AUTH_STATE, this.targetAuthState);
                    context.setAttribute(ClientContext.PROXY_AUTH_STATE, this.proxyAuthState);
                    this.requestExec.preProcess(wrapper, this.httpProcessor, context);
                    response = tryExecute(roureq, context);
                    if (response != null) {
                        response.setParams(this.params);
                        this.requestExec.postProcess(response, this.httpProcessor, context);
                        reuse = this.reuseStrategy.keepAlive(response, context);
                        if (reuse) {
                            long duration = this.keepAliveStrategy.getKeepAliveDuration(response, context);
                            if (this.log.isDebugEnabled()) {
                                String s;
                                if (duration > 0) {
                                    s = "for " + duration + MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR + TimeUnit.MILLISECONDS;
                                } else {
                                    s = "indefinitely";
                                }
                                this.log.debug("Connection can be kept alive " + s);
                            }
                            this.managedConn.setIdleDuration(duration, TimeUnit.MILLISECONDS);
                        }
                        RoutedRequest followup = handleResponse(roureq, response, context);
                        if (followup == null) {
                            done = true;
                        } else {
                            if (reuse) {
                                EntityUtils.consume(response.getEntity());
                                this.managedConn.markReusable();
                            } else {
                                this.managedConn.close();
                                invalidateAuthIfSuccessful(this.proxyAuthState);
                                invalidateAuthIfSuccessful(this.targetAuthState);
                            }
                            if (!followup.getRoute().equals(roureq.getRoute())) {
                                releaseConnection();
                            }
                            roureq = followup;
                        }
                        if (this.managedConn != null && userToken == null) {
                            userToken = this.userTokenHandler.getUserToken(context);
                            context.setAttribute(ClientContext.USER_TOKEN, userToken);
                            if (userToken != null) {
                                this.managedConn.setState(userToken);
                            }
                        }
                    }
                } catch (TunnelRefusedException ex) {
                    if (this.log.isDebugEnabled()) {
                        this.log.debug(ex.getMessage());
                    }
                    response = ex.getResponse();
                }
            } catch (InterruptedException interrupted) {
                InterruptedIOException iox = new InterruptedIOException();
                iox.initCause(interrupted);
                throw iox;
            } catch (ConnectionShutdownException ex2) {
                InterruptedIOException ioex = new InterruptedIOException("Connection has been shut down");
                ioex.initCause(ex2);
                throw ioex;
            } catch (HttpException ex3) {
                abortConnection();
                throw ex3;
            } catch (IOException ex4) {
                abortConnection();
                throw ex4;
            } catch (RuntimeException ex5) {
                abortConnection();
                throw ex5;
            }
        }
        if (response == null || response.getEntity() == null || !response.getEntity().isStreaming()) {
            if (reuse) {
                this.managedConn.markReusable();
            }
            releaseConnection();
        } else {
            response.setEntity(new BasicManagedEntity(response.getEntity(), this.managedConn, reuse));
        }
        return response;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void tryConnect(org.apache.http.impl.client.RoutedRequest r7, org.apache.http.protocol.HttpContext r8) throws org.apache.http.HttpException, java.io.IOException {
        /*
        r6 = this;
        r2 = r7.getRoute();
        r0 = 0;
    L_0x0005:
        r0 = r0 + 1;
        r3 = r6.managedConn;	 Catch:{ IOException -> 0x0026 }
        r3 = r3.isOpen();	 Catch:{ IOException -> 0x0026 }
        if (r3 != 0) goto L_0x001a;
    L_0x000f:
        r3 = r6.managedConn;	 Catch:{ IOException -> 0x0026 }
        r4 = r6.params;	 Catch:{ IOException -> 0x0026 }
        r3.open(r2, r8, r4);	 Catch:{ IOException -> 0x0026 }
    L_0x0016:
        r6.establishRoute(r2, r8);	 Catch:{ IOException -> 0x0026 }
        return;
    L_0x001a:
        r3 = r6.managedConn;	 Catch:{ IOException -> 0x0026 }
        r4 = r6.params;	 Catch:{ IOException -> 0x0026 }
        r4 = org.apache.http.params.HttpConnectionParams.getSoTimeout(r4);	 Catch:{ IOException -> 0x0026 }
        r3.setSocketTimeout(r4);	 Catch:{ IOException -> 0x0026 }
        goto L_0x0016;
    L_0x0026:
        r1 = move-exception;
        r3 = r6.managedConn;	 Catch:{ IOException -> 0x0084 }
        r3.close();	 Catch:{ IOException -> 0x0084 }
    L_0x002c:
        r3 = r6.retryHandler;
        r3 = r3.retryRequest(r1, r0, r8);
        if (r3 == 0) goto L_0x0083;
    L_0x0034:
        r3 = r6.log;
        r3 = r3.isInfoEnabled();
        if (r3 == 0) goto L_0x006a;
    L_0x003c:
        r3 = r6.log;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "I/O exception (";
        r4 = r4.append(r5);
        r5 = r1.getClass();
        r5 = r5.getName();
        r4 = r4.append(r5);
        r5 = ") caught when connecting to the target host: ";
        r4 = r4.append(r5);
        r5 = r1.getMessage();
        r4 = r4.append(r5);
        r4 = r4.toString();
        r3.info(r4);
    L_0x006a:
        r3 = r6.log;
        r3 = r3.isDebugEnabled();
        if (r3 == 0) goto L_0x007b;
    L_0x0072:
        r3 = r6.log;
        r4 = r1.getMessage();
        r3.debug(r4, r1);
    L_0x007b:
        r3 = r6.log;
        r4 = "Retrying connect";
        r3.info(r4);
        goto L_0x0005;
    L_0x0083:
        throw r1;
    L_0x0084:
        r3 = move-exception;
        goto L_0x002c;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.http.impl.client.DefaultRequestDirector.tryConnect(org.apache.http.impl.client.RoutedRequest, org.apache.http.protocol.HttpContext):void");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private org.apache.http.HttpResponse tryExecute(org.apache.http.impl.client.RoutedRequest r9, org.apache.http.protocol.HttpContext r10) throws org.apache.http.HttpException, java.io.IOException {
        /*
        r8 = this;
        r4 = r9.getRequest();
        r3 = r9.getRoute();
        r1 = 0;
        r2 = 0;
    L_0x000a:
        r5 = r8.execCount;
        r5 = r5 + 1;
        r8.execCount = r5;
        r4.incrementExecCount();
        r5 = r4.isRepeatable();
        if (r5 != 0) goto L_0x0032;
    L_0x0019:
        r5 = r8.log;
        r6 = "Cannot retry non-repeatable request";
        r5.debug(r6);
        if (r2 == 0) goto L_0x002a;
    L_0x0022:
        r5 = new org.apache.http.client.NonRepeatableRequestException;
        r6 = "Cannot retry request with a non-repeatable request entity.  The cause lists the reason the original request failed.";
        r5.<init>(r6, r2);
        throw r5;
    L_0x002a:
        r5 = new org.apache.http.client.NonRepeatableRequestException;
        r6 = "Cannot retry request with a non-repeatable request entity.";
        r5.<init>(r6);
        throw r5;
    L_0x0032:
        r5 = r8.managedConn;	 Catch:{ IOException -> 0x0087 }
        r5 = r5.isOpen();	 Catch:{ IOException -> 0x0087 }
        if (r5 != 0) goto L_0x004e;
    L_0x003a:
        r5 = r3.isTunnelled();	 Catch:{ IOException -> 0x0087 }
        if (r5 != 0) goto L_0x007f;
    L_0x0040:
        r5 = r8.log;	 Catch:{ IOException -> 0x0087 }
        r6 = "Reopening the direct connection.";
        r5.debug(r6);	 Catch:{ IOException -> 0x0087 }
        r5 = r8.managedConn;	 Catch:{ IOException -> 0x0087 }
        r6 = r8.params;	 Catch:{ IOException -> 0x0087 }
        r5.open(r3, r10, r6);	 Catch:{ IOException -> 0x0087 }
    L_0x004e:
        r5 = r8.log;	 Catch:{ IOException -> 0x0087 }
        r5 = r5.isDebugEnabled();	 Catch:{ IOException -> 0x0087 }
        if (r5 == 0) goto L_0x0076;
    L_0x0056:
        r5 = r8.log;	 Catch:{ IOException -> 0x0087 }
        r6 = new java.lang.StringBuilder;	 Catch:{ IOException -> 0x0087 }
        r6.<init>();	 Catch:{ IOException -> 0x0087 }
        r7 = "Attempt ";
        r6 = r6.append(r7);	 Catch:{ IOException -> 0x0087 }
        r7 = r8.execCount;	 Catch:{ IOException -> 0x0087 }
        r6 = r6.append(r7);	 Catch:{ IOException -> 0x0087 }
        r7 = " to execute request";
        r6 = r6.append(r7);	 Catch:{ IOException -> 0x0087 }
        r6 = r6.toString();	 Catch:{ IOException -> 0x0087 }
        r5.debug(r6);	 Catch:{ IOException -> 0x0087 }
    L_0x0076:
        r5 = r8.requestExec;	 Catch:{ IOException -> 0x0087 }
        r6 = r8.managedConn;	 Catch:{ IOException -> 0x0087 }
        r1 = r5.execute(r4, r6, r10);	 Catch:{ IOException -> 0x0087 }
    L_0x007e:
        return r1;
    L_0x007f:
        r5 = r8.log;	 Catch:{ IOException -> 0x0087 }
        r6 = "Proxied connection. Need to start over.";
        r5.debug(r6);	 Catch:{ IOException -> 0x0087 }
        goto L_0x007e;
    L_0x0087:
        r0 = move-exception;
        r5 = r8.log;
        r6 = "Closing the connection.";
        r5.debug(r6);
        r5 = r8.managedConn;	 Catch:{ IOException -> 0x00f2 }
        r5.close();	 Catch:{ IOException -> 0x00f2 }
    L_0x0094:
        r5 = r8.retryHandler;
        r6 = r4.getExecCount();
        r5 = r5.retryRequest(r0, r6, r10);
        if (r5 == 0) goto L_0x00f1;
    L_0x00a0:
        r5 = r8.log;
        r5 = r5.isInfoEnabled();
        if (r5 == 0) goto L_0x00d6;
    L_0x00a8:
        r5 = r8.log;
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r7 = "I/O exception (";
        r6 = r6.append(r7);
        r7 = r0.getClass();
        r7 = r7.getName();
        r6 = r6.append(r7);
        r7 = ") caught when processing request: ";
        r6 = r6.append(r7);
        r7 = r0.getMessage();
        r6 = r6.append(r7);
        r6 = r6.toString();
        r5.info(r6);
    L_0x00d6:
        r5 = r8.log;
        r5 = r5.isDebugEnabled();
        if (r5 == 0) goto L_0x00e7;
    L_0x00de:
        r5 = r8.log;
        r6 = r0.getMessage();
        r5.debug(r6, r0);
    L_0x00e7:
        r5 = r8.log;
        r6 = "Retrying request";
        r5.info(r6);
        r2 = r0;
        goto L_0x000a;
    L_0x00f1:
        throw r0;
    L_0x00f2:
        r5 = move-exception;
        goto L_0x0094;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.http.impl.client.DefaultRequestDirector.tryExecute(org.apache.http.impl.client.RoutedRequest, org.apache.http.protocol.HttpContext):org.apache.http.HttpResponse");
    }

    protected void releaseConnection() {
        try {
            this.managedConn.releaseConnection();
        } catch (IOException ignored) {
            this.log.debug("IOException releasing connection", ignored);
        }
        this.managedConn = null;
    }

    protected HttpRoute determineRoute(HttpHost target, HttpRequest request, HttpContext context) throws HttpException {
        if (target == null) {
            target = (HttpHost) request.getParams().getParameter(ClientPNames.DEFAULT_HOST);
        }
        if (target != null) {
            return this.routePlanner.determineRoute(target, request, context);
        }
        throw new IllegalStateException("Target host must not be null, or set in parameters.");
    }

    protected void establishRoute(HttpRoute route, HttpContext context) throws HttpException, IOException {
        HttpRouteDirector rowdy = new BasicRouteDirector();
        int step;
        do {
            HttpRoute fact = this.managedConn.getRoute();
            step = rowdy.nextStep(route, fact);
            boolean secure;
            switch (step) {
                case GridLayoutManager.DEFAULT_SPAN_COUNT /*-1*/:
                    throw new HttpException("Unable to establish route: planned = " + route + "; current = " + fact);
                case NewThreadWorker.PURGE_FREQUENCY:
                    break;
                case OnSubscribeConcatMap.BOUNDARY /*1*/:
                case OnSubscribeConcatMap.END /*2*/:
                    this.managedConn.open(route, context, this.params);
                    continue;
                case ConnectionResult.SERVICE_DISABLED /*3*/:
                    secure = createTunnelToTarget(route, context);
                    this.log.debug("Tunnel to target created.");
                    this.managedConn.tunnelTarget(secure, this.params);
                    continue;
                case ConnectionResult.SIGN_IN_REQUIRED /*4*/:
                    int hop = fact.getHopCount() - 1;
                    secure = createTunnelToProxy(route, hop, context);
                    this.log.debug("Tunnel to proxy created.");
                    this.managedConn.tunnelProxy(route.getHopTarget(hop), secure, this.params);
                    continue;
                case ConnectionResult.INVALID_ACCOUNT /*5*/:
                    this.managedConn.layerProtocol(context, this.params);
                    continue;
                default:
                    throw new IllegalStateException("Unknown step indicator " + step + " from RouteDirector.");
            }
        } while (step > 0);
    }

    protected boolean createTunnelToTarget(HttpRoute route, HttpContext context) throws HttpException, IOException {
        HttpHost proxy = route.getProxyHost();
        HttpHost target = route.getTargetHost();
        HttpResponse response = null;
        boolean done = false;
        while (!done) {
            done = true;
            if (!this.managedConn.isOpen()) {
                this.managedConn.open(route, context, this.params);
            }
            HttpRequest connect = createConnectRequest(route, context);
            connect.setParams(this.params);
            context.setAttribute(ExecutionContext.HTTP_TARGET_HOST, target);
            context.setAttribute(ExecutionContext.HTTP_PROXY_HOST, proxy);
            context.setAttribute(ExecutionContext.HTTP_CONNECTION, this.managedConn);
            context.setAttribute(ClientContext.TARGET_AUTH_STATE, this.targetAuthState);
            context.setAttribute(ClientContext.PROXY_AUTH_STATE, this.proxyAuthState);
            context.setAttribute(ExecutionContext.HTTP_REQUEST, connect);
            this.requestExec.preProcess(connect, this.httpProcessor, context);
            response = this.requestExec.execute(connect, this.managedConn, context);
            response.setParams(this.params);
            this.requestExec.postProcess(response, this.httpProcessor, context);
            if (response.getStatusLine().getStatusCode() < HttpStatus.SC_OK) {
                throw new HttpException("Unexpected response to CONNECT request: " + response.getStatusLine());
            }
            CredentialsProvider credsProvider = (CredentialsProvider) context.getAttribute(ClientContext.CREDS_PROVIDER);
            if (credsProvider != null && HttpClientParams.isAuthenticating(this.params)) {
                if (this.proxyAuthHandler.isAuthenticationRequested(response, context)) {
                    this.log.debug("Proxy requested authentication");
                    try {
                        processChallenges(this.proxyAuthHandler.getChallenges(response, context), this.proxyAuthState, this.proxyAuthHandler, response, context);
                    } catch (AuthenticationException ex) {
                        if (this.log.isWarnEnabled()) {
                            this.log.warn("Authentication error: " + ex.getMessage());
                            break;
                        }
                    }
                    updateAuthState(this.proxyAuthState, proxy, credsProvider);
                    if (this.proxyAuthState.getCredentials() != null) {
                        done = false;
                        if (this.reuseStrategy.keepAlive(response, context)) {
                            this.log.debug("Connection kept alive");
                            EntityUtils.consume(response.getEntity());
                        } else {
                            this.managedConn.close();
                        }
                    }
                } else {
                    this.proxyAuthState.setAuthScope(null);
                }
            }
        }
        if (response.getStatusLine().getStatusCode() > 299) {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                response.setEntity(new BufferedHttpEntity(entity));
            }
            this.managedConn.close();
            throw new TunnelRefusedException("CONNECT refused by proxy: " + response.getStatusLine(), response);
        }
        this.managedConn.markReusable();
        return false;
    }

    protected boolean createTunnelToProxy(HttpRoute route, int hop, HttpContext context) throws HttpException, IOException {
        throw new HttpException("Proxy chains are not supported.");
    }

    protected HttpRequest createConnectRequest(HttpRoute route, HttpContext context) {
        HttpHost target = route.getTargetHost();
        String host = target.getHostName();
        int port = target.getPort();
        if (port < 0) {
            port = this.connManager.getSchemeRegistry().getScheme(target.getSchemeName()).getDefaultPort();
        }
        StringBuilder buffer = new StringBuilder(host.length() + 6);
        buffer.append(host);
        buffer.append(':');
        buffer.append(Integer.toString(port));
        return new BasicHttpRequest("CONNECT", buffer.toString(), HttpProtocolParams.getVersion(this.params));
    }

    protected RoutedRequest handleResponse(RoutedRequest roureq, HttpResponse response, HttpContext context) throws HttpException, IOException {
        HttpRoute route = roureq.getRoute();
        RequestWrapper request = roureq.getRequest();
        HttpParams params = request.getParams();
        if (!HttpClientParams.isRedirecting(params) || !this.redirectStrategy.isRedirected(request, response, context)) {
            CredentialsProvider credsProvider = (CredentialsProvider) context.getAttribute(ClientContext.CREDS_PROVIDER);
            if (credsProvider != null && HttpClientParams.isAuthenticating(params)) {
                if (this.targetAuthHandler.isAuthenticationRequested(response, context)) {
                    HttpHost target = (HttpHost) context.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
                    if (target == null) {
                        target = route.getTargetHost();
                    }
                    this.log.debug("Target requested authentication");
                    try {
                        processChallenges(this.targetAuthHandler.getChallenges(response, context), this.targetAuthState, this.targetAuthHandler, response, context);
                    } catch (AuthenticationException ex) {
                        if (this.log.isWarnEnabled()) {
                            this.log.warn("Authentication error: " + ex.getMessage());
                            return null;
                        }
                    }
                    updateAuthState(this.targetAuthState, target, credsProvider);
                    if (this.targetAuthState.getCredentials() == null) {
                        return null;
                    }
                    return roureq;
                }
                this.targetAuthState.setAuthScope(null);
                if (this.proxyAuthHandler.isAuthenticationRequested(response, context)) {
                    HttpHost proxy = route.getProxyHost();
                    this.log.debug("Proxy requested authentication");
                    try {
                        processChallenges(this.proxyAuthHandler.getChallenges(response, context), this.proxyAuthState, this.proxyAuthHandler, response, context);
                    } catch (AuthenticationException ex2) {
                        if (this.log.isWarnEnabled()) {
                            this.log.warn("Authentication error: " + ex2.getMessage());
                            return null;
                        }
                    }
                    updateAuthState(this.proxyAuthState, proxy, credsProvider);
                    if (this.proxyAuthState.getCredentials() == null) {
                        return null;
                    }
                    return roureq;
                }
                this.proxyAuthState.setAuthScope(null);
            }
            return null;
        } else if (this.redirectCount >= this.maxRedirects) {
            throw new RedirectException("Maximum redirects (" + this.maxRedirects + ") exceeded");
        } else {
            this.redirectCount++;
            this.virtualHost = null;
            HttpUriRequest redirect = this.redirectStrategy.getRedirect(request, response, context);
            redirect.setHeaders(request.getOriginal().getAllHeaders());
            URI uri = redirect.getURI();
            if (uri.getHost() == null) {
                throw new ProtocolException("Redirect URI does not specify a valid host name: " + uri);
            }
            HttpHost newTarget = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
            this.targetAuthState.setAuthScope(null);
            this.proxyAuthState.setAuthScope(null);
            if (!route.getTargetHost().equals(newTarget)) {
                this.targetAuthState.invalidate();
                AuthScheme authScheme = this.proxyAuthState.getAuthScheme();
                if (authScheme != null && authScheme.isConnectionBased()) {
                    this.proxyAuthState.invalidate();
                }
            }
            RequestWrapper wrapper = wrapRequest(redirect);
            wrapper.setParams(params);
            HttpRoute newRoute = determineRoute(newTarget, wrapper, context);
            RoutedRequest newRequest = new RoutedRequest(wrapper, newRoute);
            if (this.log.isDebugEnabled()) {
                this.log.debug("Redirecting to '" + uri + "' via " + newRoute);
            }
            return newRequest;
        }
    }

    private void abortConnection() {
        ManagedClientConnection mcc = this.managedConn;
        if (mcc != null) {
            this.managedConn = null;
            try {
                mcc.abortConnection();
            } catch (IOException ex) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug(ex.getMessage(), ex);
                }
            }
            try {
                mcc.releaseConnection();
            } catch (IOException ignored) {
                this.log.debug("Error releasing connection", ignored);
            }
        }
    }

    private void processChallenges(Map<String, Header> challenges, AuthState authState, AuthenticationHandler authHandler, HttpResponse response, HttpContext context) throws MalformedChallengeException, AuthenticationException {
        AuthScheme authScheme = authState.getAuthScheme();
        if (authScheme == null) {
            authScheme = authHandler.selectScheme(challenges, response, context);
            authState.setAuthScheme(authScheme);
        }
        String id = authScheme.getSchemeName();
        Header challenge = (Header) challenges.get(id.toLowerCase(Locale.ENGLISH));
        if (challenge == null) {
            throw new AuthenticationException(id + " authorization challenge expected, but not found");
        }
        authScheme.processChallenge(challenge);
        this.log.debug("Authorization challenge processed");
    }

    private void updateAuthState(AuthState authState, HttpHost host, CredentialsProvider credsProvider) {
        if (authState.isValid()) {
            String hostname = host.getHostName();
            int port = host.getPort();
            if (port < 0) {
                port = this.connManager.getSchemeRegistry().getScheme(host).getDefaultPort();
            }
            AuthScheme authScheme = authState.getAuthScheme();
            AuthScope authScope = new AuthScope(hostname, port, authScheme.getRealm(), authScheme.getSchemeName());
            if (this.log.isDebugEnabled()) {
                this.log.debug("Authentication scope: " + authScope);
            }
            Credentials creds = authState.getCredentials();
            if (creds == null) {
                creds = credsProvider.getCredentials(authScope);
                if (this.log.isDebugEnabled()) {
                    if (creds != null) {
                        this.log.debug("Found credentials");
                    } else {
                        this.log.debug("Credentials not found");
                    }
                }
            } else if (authScheme.isComplete()) {
                this.log.debug("Authentication failed");
                creds = null;
            }
            authState.setAuthScope(authScope);
            authState.setCredentials(creds);
        }
    }

    private void invalidateAuthIfSuccessful(AuthState authState) {
        AuthScheme authscheme = authState.getAuthScheme();
        if (authscheme != null && authscheme.isConnectionBased() && authscheme.isComplete() && authState.getCredentials() != null) {
            authState.invalidate();
        }
    }
}
