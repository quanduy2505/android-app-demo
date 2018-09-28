package com.facebook.appevents;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PointerIconCompat;
import com.facebook.GraphRequest;
import com.facebook.internal.AppEventsLoggerUtility;
import com.facebook.internal.AppEventsLoggerUtility.GraphAPIActivityType;
import com.facebook.internal.AttributionIdentifiers;
import com.facebook.internal.Utility;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class SessionEventsState {
    private final int MAX_ACCUMULATED_LOG_EVENTS;
    private List<AppEvent> accumulatedEvents;
    private String anonymousAppDeviceGUID;
    private AttributionIdentifiers attributionIdentifiers;
    private List<AppEvent> inFlightEvents;
    private int numSkippedEventsDueToFullBuffer;

    public SessionEventsState(AttributionIdentifiers identifiers, String anonymousGUID) {
        this.accumulatedEvents = new ArrayList();
        this.inFlightEvents = new ArrayList();
        this.MAX_ACCUMULATED_LOG_EVENTS = PointerIconCompat.TYPE_DEFAULT;
        this.attributionIdentifiers = identifiers;
        this.anonymousAppDeviceGUID = anonymousGUID;
    }

    public synchronized void addEvent(AppEvent event) {
        if (this.accumulatedEvents.size() + this.inFlightEvents.size() >= PointerIconCompat.TYPE_DEFAULT) {
            this.numSkippedEventsDueToFullBuffer++;
        } else {
            this.accumulatedEvents.add(event);
        }
    }

    public synchronized int getAccumulatedEventCount() {
        return this.accumulatedEvents.size();
    }

    public synchronized void clearInFlightAndStats(boolean moveToAccumulated) {
        if (moveToAccumulated) {
            this.accumulatedEvents.addAll(this.inFlightEvents);
        }
        this.inFlightEvents.clear();
        this.numSkippedEventsDueToFullBuffer = 0;
    }

    public int populateRequest(GraphRequest request, Context applicationContext, boolean includeImplicitEvents, boolean limitEventUsage) {
        synchronized (this) {
            int numSkipped = this.numSkippedEventsDueToFullBuffer;
            this.inFlightEvents.addAll(this.accumulatedEvents);
            this.accumulatedEvents.clear();
            JSONArray jsonArray = new JSONArray();
            for (AppEvent event : this.inFlightEvents) {
                if (!event.isChecksumValid()) {
                    Utility.logd("Event with invalid checksum: %s", event.toString());
                } else if (includeImplicitEvents || !event.getIsImplicit()) {
                    jsonArray.put(event.getJSONObject());
                }
            }
            if (jsonArray.length() == 0) {
                return 0;
            }
            populateRequest(request, applicationContext, numSkipped, jsonArray, limitEventUsage);
            return jsonArray.length();
        }
    }

    public synchronized List<AppEvent> getEventsToPersist() {
        List<AppEvent> result;
        result = this.accumulatedEvents;
        this.accumulatedEvents = new ArrayList();
        return result;
    }

    public synchronized void accumulatePersistedEvents(List<AppEvent> events) {
        this.accumulatedEvents.addAll(events);
    }

    private void populateRequest(GraphRequest request, Context applicationContext, int numSkipped, JSONArray events, boolean limitEventUsage) {
        JSONObject publishParams;
        try {
            publishParams = AppEventsLoggerUtility.getJSONObjectForGraphAPICall(GraphAPIActivityType.CUSTOM_APP_EVENTS, this.attributionIdentifiers, this.anonymousAppDeviceGUID, limitEventUsage, applicationContext);
            if (this.numSkippedEventsDueToFullBuffer > 0) {
                publishParams.put("num_skipped_events", numSkipped);
            }
        } catch (JSONException e) {
            publishParams = new JSONObject();
        }
        request.setGraphObject(publishParams);
        Bundle requestParameters = request.getParameters();
        if (requestParameters == null) {
            requestParameters = new Bundle();
        }
        String jsonString = events.toString();
        if (jsonString != null) {
            requestParameters.putByteArray("custom_events_file", getStringAsByteArray(jsonString));
            request.setTag(jsonString);
        }
        request.setParameters(requestParameters);
    }

    private byte[] getStringAsByteArray(String jsonString) {
        byte[] jsonUtf8 = null;
        try {
            jsonUtf8 = jsonString.getBytes(HTTP.UTF_8);
        } catch (Exception e) {
            Utility.logd("Encoding exception: ", e);
        }
        return jsonUtf8;
    }
}
