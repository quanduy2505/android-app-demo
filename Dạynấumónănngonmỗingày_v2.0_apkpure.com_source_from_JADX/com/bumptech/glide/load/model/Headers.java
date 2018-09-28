package com.bumptech.glide.load.model;

import com.bumptech.glide.load.model.LazyHeaders.Builder;
import java.util.Collections;
import java.util.Map;

public interface Headers {
    public static final Headers DEFAULT;
    @Deprecated
    public static final Headers NONE;

    /* renamed from: com.bumptech.glide.load.model.Headers.1 */
    static class C09761 implements Headers {
        C09761() {
        }

        public Map<String, String> getHeaders() {
            return Collections.emptyMap();
        }
    }

    Map<String, String> getHeaders();

    static {
        NONE = new C09761();
        DEFAULT = new Builder().build();
    }
}
