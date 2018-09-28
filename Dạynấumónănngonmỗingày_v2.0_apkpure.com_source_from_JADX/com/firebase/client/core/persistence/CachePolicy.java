package com.firebase.client.core.persistence;

public interface CachePolicy {
    public static final CachePolicy NONE;

    /* renamed from: com.firebase.client.core.persistence.CachePolicy.1 */
    static class C11111 implements CachePolicy {
        C11111() {
        }

        public boolean shouldPrune(long currentSizeBytes, long countOfPrunableQueries) {
            return false;
        }

        public boolean shouldCheckCacheSize(long serverUpdatesSinceLastCheck) {
            return false;
        }

        public float getPercentOfQueriesToPruneAtOnce() {
            return 0.0f;
        }

        public long getMaxNumberOfQueriesToKeep() {
            return Long.MAX_VALUE;
        }
    }

    long getMaxNumberOfQueriesToKeep();

    float getPercentOfQueriesToPruneAtOnce();

    boolean shouldCheckCacheSize(long j);

    boolean shouldPrune(long j, long j2);

    static {
        NONE = new C11111();
    }
}
