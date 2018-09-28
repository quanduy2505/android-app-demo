package com.facebook.internal;

import android.content.Intent;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import java.util.HashMap;
import java.util.Map;

public final class CallbackManagerImpl implements CallbackManager {
    private static Map<Integer, Callback> staticCallbacks;
    private Map<Integer, Callback> callbacks;

    public interface Callback {
        boolean onActivityResult(int i, Intent intent);
    }

    public enum RequestCodeOffset {
        Login(0),
        Share(1),
        Message(2),
        Like(3),
        GameRequest(4),
        AppGroupCreate(5),
        AppGroupJoin(6),
        AppInvite(7),
        DeviceShare(8);
        
        private final int offset;

        private RequestCodeOffset(int offset) {
            this.offset = offset;
        }

        public int toRequestCode() {
            return FacebookSdk.getCallbackRequestCodeOffset() + this.offset;
        }
    }

    public CallbackManagerImpl() {
        this.callbacks = new HashMap();
    }

    static {
        staticCallbacks = new HashMap();
    }

    public static synchronized void registerStaticCallback(int requestCode, Callback callback) {
        synchronized (CallbackManagerImpl.class) {
            Validate.notNull(callback, "callback");
            if (!staticCallbacks.containsKey(Integer.valueOf(requestCode))) {
                staticCallbacks.put(Integer.valueOf(requestCode), callback);
            }
        }
    }

    private static synchronized Callback getStaticCallback(Integer requestCode) {
        Callback callback;
        synchronized (CallbackManagerImpl.class) {
            callback = (Callback) staticCallbacks.get(requestCode);
        }
        return callback;
    }

    private static boolean runStaticCallback(int requestCode, int resultCode, Intent data) {
        Callback callback = getStaticCallback(Integer.valueOf(requestCode));
        if (callback != null) {
            return callback.onActivityResult(resultCode, data);
        }
        return false;
    }

    public void registerCallback(int requestCode, Callback callback) {
        Validate.notNull(callback, "callback");
        this.callbacks.put(Integer.valueOf(requestCode), callback);
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        Callback callback = (Callback) this.callbacks.get(Integer.valueOf(requestCode));
        if (callback != null) {
            return callback.onActivityResult(resultCode, data);
        }
        return runStaticCallback(requestCode, resultCode, data);
    }
}
