package com.facebook.appevents;

class FlushStatistics {
    public int numEvents;
    public FlushResult result;

    FlushStatistics() {
        this.numEvents = 0;
        this.result = FlushResult.SUCCESS;
    }
}
