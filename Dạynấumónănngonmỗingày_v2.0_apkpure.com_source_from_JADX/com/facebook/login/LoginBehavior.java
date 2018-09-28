package com.facebook.login;

public enum LoginBehavior {
    NATIVE_WITH_FALLBACK(true, true, true, false, true, true),
    NATIVE_ONLY(true, true, false, false, false, true),
    KATANA_ONLY(false, true, false, false, false, false),
    WEB_ONLY(false, false, true, false, true, false),
    WEB_VIEW_ONLY(false, false, true, false, false, false),
    DEVICE_AUTH(false, false, false, true, false, false);
    
    private final boolean allowsCustomTabAuth;
    private final boolean allowsDeviceAuth;
    private final boolean allowsFacebookLiteAuth;
    private final boolean allowsGetTokenAuth;
    private final boolean allowsKatanaAuth;
    private final boolean allowsWebViewAuth;

    private LoginBehavior(boolean allowsGetTokenAuth, boolean allowsKatanaAuth, boolean allowsWebViewAuth, boolean allowsDeviceAuth, boolean allowsCustomTabAuth, boolean allowsFacebookLiteAuth) {
        this.allowsGetTokenAuth = allowsGetTokenAuth;
        this.allowsKatanaAuth = allowsKatanaAuth;
        this.allowsWebViewAuth = allowsWebViewAuth;
        this.allowsDeviceAuth = allowsDeviceAuth;
        this.allowsCustomTabAuth = allowsCustomTabAuth;
        this.allowsFacebookLiteAuth = allowsFacebookLiteAuth;
    }

    boolean allowsGetTokenAuth() {
        return this.allowsGetTokenAuth;
    }

    boolean allowsKatanaAuth() {
        return this.allowsKatanaAuth;
    }

    boolean allowsWebViewAuth() {
        return this.allowsWebViewAuth;
    }

    boolean allowsDeviceAuth() {
        return this.allowsDeviceAuth;
    }

    boolean allowsCustomTabAuth() {
        return this.allowsCustomTabAuth;
    }

    boolean allowsFacebookLiteAuth() {
        return this.allowsFacebookLiteAuth;
    }
}
