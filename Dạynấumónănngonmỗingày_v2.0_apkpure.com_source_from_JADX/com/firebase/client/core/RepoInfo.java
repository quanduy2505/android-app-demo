package com.firebase.client.core;

import java.net.URI;
import org.apache.http.HttpHost;
import rx.android.BuildConfig;

public class RepoInfo {
    private static final String LAST_SESSION_ID_PARAM = "ls";
    private static final String VERSION_PARAM = "v";
    public String host;
    public String internalHost;
    public String namespace;
    public boolean secure;

    public String toString() {
        return HttpHost.DEFAULT_SCHEME_NAME + (this.secure ? "s" : BuildConfig.VERSION_NAME) + "://" + this.host;
    }

    public String toDebugString() {
        return "(host=" + this.host + ", secure=" + this.secure + ", ns=" + this.namespace + " internal=" + this.internalHost + ")";
    }

    public URI getConnectionURL(String optLastSessionId) {
        String url = (this.secure ? "wss" : "ws") + "://" + this.internalHost + "/.ws?ns=" + this.namespace + "&" + VERSION_PARAM + "=" + Constants.WIRE_PROTOCOL_VERSION;
        if (optLastSessionId != null) {
            url = url + "&ls=" + optLastSessionId;
        }
        return URI.create(url);
    }

    public boolean isCacheableHost() {
        return this.internalHost.startsWith("s-");
    }

    public boolean isSecure() {
        return this.secure;
    }

    public boolean isDemoHost() {
        return this.host.contains(".firebaseio-demo.com");
    }

    public boolean isCustomHost() {
        return (this.host.contains(".firebaseio.com") || this.host.contains(".firebaseio-demo.com")) ? false : true;
    }
}
