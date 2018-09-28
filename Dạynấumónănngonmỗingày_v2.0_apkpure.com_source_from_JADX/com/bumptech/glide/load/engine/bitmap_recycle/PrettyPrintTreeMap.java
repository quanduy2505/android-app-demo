package com.bumptech.glide.load.engine.bitmap_recycle;

import java.util.Map.Entry;
import java.util.TreeMap;
import rx.android.BuildConfig;

class PrettyPrintTreeMap<K, V> extends TreeMap<K, V> {
    PrettyPrintTreeMap() {
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("( ");
        for (Entry<K, V> entry : entrySet()) {
            sb.append('{').append(entry.getKey()).append(':').append(entry.getValue()).append("}, ");
        }
        if (!isEmpty()) {
            sb.replace(sb.length() - 2, sb.length(), BuildConfig.VERSION_NAME);
        }
        return sb.append(" )").toString();
    }
}
