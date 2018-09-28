package com.facebook.appevents;

import android.content.Context;
import android.util.Log;
import com.facebook.FacebookSdk;
import com.facebook.appevents.internal.AppEventUtility;
import com.facebook.internal.Utility;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;

class AppEventStore {
    private static final String PERSISTED_EVENTS_FILENAME = "AppEventsLogger.persistedevents";
    private static final String TAG;

    private static class MovedClassObjectInputStream extends ObjectInputStream {
        private static final String ACCESS_TOKEN_APP_ID_PAIR_SERIALIZATION_PROXY_V1_CLASS_NAME = "com.facebook.appevents.AppEventsLogger$AccessTokenAppIdPair$SerializationProxyV1";
        private static final String APP_EVENT_SERIALIZATION_PROXY_V1_CLASS_NAME = "com.facebook.appevents.AppEventsLogger$AppEvent$SerializationProxyV1";

        public MovedClassObjectInputStream(InputStream in) throws IOException {
            super(in);
        }

        protected ObjectStreamClass readClassDescriptor() throws IOException, ClassNotFoundException {
            ObjectStreamClass resultClassDescriptor = super.readClassDescriptor();
            if (resultClassDescriptor.getName().equals(ACCESS_TOKEN_APP_ID_PAIR_SERIALIZATION_PROXY_V1_CLASS_NAME)) {
                return ObjectStreamClass.lookup(SerializationProxyV1.class);
            }
            if (resultClassDescriptor.getName().equals(APP_EVENT_SERIALIZATION_PROXY_V1_CLASS_NAME)) {
                return ObjectStreamClass.lookup(SerializationProxyV1.class);
            }
            return resultClassDescriptor;
        }
    }

    AppEventStore() {
    }

    static {
        TAG = AppEventStore.class.getName();
    }

    public static synchronized void persistEvents(AccessTokenAppIdPair accessTokenAppIdPair, SessionEventsState appEvents) {
        synchronized (AppEventStore.class) {
            AppEventUtility.assertIsNotMainThread();
            PersistedEvents persistedEvents = readAndClearStore();
            if (persistedEvents.containsKey(accessTokenAppIdPair)) {
                persistedEvents.get(accessTokenAppIdPair).addAll(appEvents.getEventsToPersist());
            } else {
                persistedEvents.addEvents(accessTokenAppIdPair, appEvents.getEventsToPersist());
            }
            saveEventsToDisk(persistedEvents);
        }
    }

    public static synchronized void persistEvents(AppEventCollection eventsToPersist) {
        synchronized (AppEventStore.class) {
            AppEventUtility.assertIsNotMainThread();
            PersistedEvents persistedEvents = readAndClearStore();
            for (AccessTokenAppIdPair accessTokenAppIdPair : eventsToPersist.keySet()) {
                persistedEvents.addEvents(accessTokenAppIdPair, eventsToPersist.get(accessTokenAppIdPair).getEventsToPersist());
            }
            saveEventsToDisk(persistedEvents);
        }
    }

    public static synchronized PersistedEvents readAndClearStore() {
        PersistedEvents persistedEvents;
        Exception e;
        Throwable th;
        synchronized (AppEventStore.class) {
            AppEventUtility.assertIsNotMainThread();
            MovedClassObjectInputStream ois = null;
            persistedEvents = null;
            Context context = FacebookSdk.getApplicationContext();
            try {
                MovedClassObjectInputStream ois2 = new MovedClassObjectInputStream(new BufferedInputStream(context.openFileInput(PERSISTED_EVENTS_FILENAME)));
                try {
                    persistedEvents = (PersistedEvents) ois2.readObject();
                    Utility.closeQuietly(ois2);
                    try {
                        context.getFileStreamPath(PERSISTED_EVENTS_FILENAME).delete();
                        ois = ois2;
                    } catch (Exception ex) {
                        Log.w(TAG, "Got unexpected exception when removing events file: ", ex);
                        ois = ois2;
                    }
                } catch (FileNotFoundException e2) {
                    ois = ois2;
                    Utility.closeQuietly(ois);
                    try {
                        context.getFileStreamPath(PERSISTED_EVENTS_FILENAME).delete();
                    } catch (Exception ex2) {
                        Log.w(TAG, "Got unexpected exception when removing events file: ", ex2);
                    }
                    if (persistedEvents == null) {
                        persistedEvents = new PersistedEvents();
                    }
                    return persistedEvents;
                } catch (Exception e3) {
                    e = e3;
                    ois = ois2;
                    try {
                        Log.w(TAG, "Got unexpected exception while reading events: ", e);
                        Utility.closeQuietly(ois);
                        try {
                            context.getFileStreamPath(PERSISTED_EVENTS_FILENAME).delete();
                        } catch (Exception ex22) {
                            Log.w(TAG, "Got unexpected exception when removing events file: ", ex22);
                        }
                        if (persistedEvents == null) {
                            persistedEvents = new PersistedEvents();
                        }
                        return persistedEvents;
                    } catch (Throwable th2) {
                        th = th2;
                        Utility.closeQuietly(ois);
                        try {
                            context.getFileStreamPath(PERSISTED_EVENTS_FILENAME).delete();
                        } catch (Exception ex222) {
                            Log.w(TAG, "Got unexpected exception when removing events file: ", ex222);
                        }
                        throw th;
                    }
                } catch (Throwable th3) {
                    th = th3;
                    ois = ois2;
                    Utility.closeQuietly(ois);
                    context.getFileStreamPath(PERSISTED_EVENTS_FILENAME).delete();
                    throw th;
                }
            } catch (FileNotFoundException e4) {
                Utility.closeQuietly(ois);
                context.getFileStreamPath(PERSISTED_EVENTS_FILENAME).delete();
                if (persistedEvents == null) {
                    persistedEvents = new PersistedEvents();
                }
                return persistedEvents;
            } catch (Exception e5) {
                e = e5;
                Log.w(TAG, "Got unexpected exception while reading events: ", e);
                Utility.closeQuietly(ois);
                context.getFileStreamPath(PERSISTED_EVENTS_FILENAME).delete();
                if (persistedEvents == null) {
                    persistedEvents = new PersistedEvents();
                }
                return persistedEvents;
            }
            if (persistedEvents == null) {
                persistedEvents = new PersistedEvents();
            }
        }
        return persistedEvents;
    }

    private static void saveEventsToDisk(PersistedEvents eventsToPersist) {
        Exception e;
        Throwable th;
        ObjectOutputStream oos = null;
        Context context = FacebookSdk.getApplicationContext();
        try {
            ObjectOutputStream oos2 = new ObjectOutputStream(new BufferedOutputStream(context.openFileOutput(PERSISTED_EVENTS_FILENAME, 0)));
            try {
                oos2.writeObject(eventsToPersist);
                Utility.closeQuietly(oos2);
                oos = oos2;
            } catch (Exception e2) {
                e = e2;
                oos = oos2;
                try {
                    Log.w(TAG, "Got unexpected exception while persisting events: ", e);
                    try {
                        context.getFileStreamPath(PERSISTED_EVENTS_FILENAME).delete();
                    } catch (Exception e3) {
                    }
                    Utility.closeQuietly(oos);
                } catch (Throwable th2) {
                    th = th2;
                    Utility.closeQuietly(oos);
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                oos = oos2;
                Utility.closeQuietly(oos);
                throw th;
            }
        } catch (Exception e4) {
            e = e4;
            Log.w(TAG, "Got unexpected exception while persisting events: ", e);
            context.getFileStreamPath(PERSISTED_EVENTS_FILENAME).delete();
            Utility.closeQuietly(oos);
        }
    }
}
