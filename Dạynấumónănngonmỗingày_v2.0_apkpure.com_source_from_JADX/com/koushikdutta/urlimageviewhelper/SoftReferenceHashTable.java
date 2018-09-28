package com.koushikdutta.urlimageviewhelper;

import java.lang.ref.SoftReference;
import java.util.Hashtable;

public class SoftReferenceHashTable<K, V> {
    Hashtable<K, SoftReference<V>> mTable;

    public SoftReferenceHashTable() {
        this.mTable = new Hashtable();
    }

    public V put(K key, V value) {
        SoftReference<V> old = (SoftReference) this.mTable.put(key, new SoftReference(value));
        if (old == null) {
            return null;
        }
        return old.get();
    }

    public V get(K key) {
        SoftReference<V> val = (SoftReference) this.mTable.get(key);
        if (val == null) {
            return null;
        }
        V ret = val.get();
        if (ret != null) {
            return ret;
        }
        this.mTable.remove(key);
        return ret;
    }

    public V remove(K k) {
        SoftReference<V> v = (SoftReference) this.mTable.remove(k);
        if (v == null) {
            return null;
        }
        return v.get();
    }
}
