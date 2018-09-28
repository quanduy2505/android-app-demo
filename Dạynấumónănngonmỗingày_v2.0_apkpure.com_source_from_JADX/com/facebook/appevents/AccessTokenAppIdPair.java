package com.facebook.appevents;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.internal.Utility;
import java.io.Serializable;

class AccessTokenAppIdPair implements Serializable {
    private static final long serialVersionUID = 1;
    private final String accessTokenString;
    private final String applicationId;

    static class SerializationProxyV1 implements Serializable {
        private static final long serialVersionUID = -2488473066578201069L;
        private final String accessTokenString;
        private final String appId;

        private SerializationProxyV1(String accessTokenString, String appId) {
            this.accessTokenString = accessTokenString;
            this.appId = appId;
        }

        private Object readResolve() {
            return new AccessTokenAppIdPair(this.accessTokenString, this.appId);
        }
    }

    public AccessTokenAppIdPair(AccessToken accessToken) {
        this(accessToken.getToken(), FacebookSdk.getApplicationId());
    }

    public AccessTokenAppIdPair(String accessTokenString, String applicationId) {
        if (Utility.isNullOrEmpty(accessTokenString)) {
            accessTokenString = null;
        }
        this.accessTokenString = accessTokenString;
        this.applicationId = applicationId;
    }

    public String getAccessTokenString() {
        return this.accessTokenString;
    }

    public String getApplicationId() {
        return this.applicationId;
    }

    public int hashCode() {
        int i = 0;
        int hashCode = this.accessTokenString == null ? 0 : this.accessTokenString.hashCode();
        if (this.applicationId != null) {
            i = this.applicationId.hashCode();
        }
        return hashCode ^ i;
    }

    public boolean equals(Object o) {
        if (!(o instanceof AccessTokenAppIdPair)) {
            return false;
        }
        AccessTokenAppIdPair p = (AccessTokenAppIdPair) o;
        if (Utility.areObjectsEqual(p.accessTokenString, this.accessTokenString) && Utility.areObjectsEqual(p.applicationId, this.applicationId)) {
            return true;
        }
        return false;
    }

    private Object writeReplace() {
        return new SerializationProxyV1(this.applicationId, null);
    }
}
