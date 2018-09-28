package com.fasterxml.jackson.core.util;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

public final class InternCache extends LinkedHashMap<String, String> {
    private static final int MAX_ENTRIES = 100;
    public static final InternCache instance;

    static {
        instance = new InternCache();
    }

    private InternCache() {
        super(MAX_ENTRIES, 0.8f, true);
    }

    protected boolean removeEldestEntry(Entry<String, String> entry) {
        return size() > MAX_ENTRIES;
    }

    public synchronized String intern(String str) {
        String str2;
        str2 = (String) get(str);
        if (str2 == null) {
            str2 = str.intern();
            put(str2, str2);
        }
        return str2;
    }
}
