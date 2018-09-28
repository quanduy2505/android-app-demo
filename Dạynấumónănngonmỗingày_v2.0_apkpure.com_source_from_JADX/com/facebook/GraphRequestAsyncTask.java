package com.facebook;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import java.net.HttpURLConnection;
import java.util.Collection;
import java.util.List;

public class GraphRequestAsyncTask extends AsyncTask<Void, Void, List<GraphResponse>> {
    private static final String TAG;
    private final HttpURLConnection connection;
    private Exception exception;
    private final GraphRequestBatch requests;

    static {
        TAG = GraphRequestAsyncTask.class.getCanonicalName();
    }

    public GraphRequestAsyncTask(GraphRequest... requests) {
        this(null, new GraphRequestBatch(requests));
    }

    public GraphRequestAsyncTask(Collection<GraphRequest> requests) {
        this(null, new GraphRequestBatch((Collection) requests));
    }

    public GraphRequestAsyncTask(GraphRequestBatch requests) {
        this(null, requests);
    }

    public GraphRequestAsyncTask(HttpURLConnection connection, GraphRequest... requests) {
        this(connection, new GraphRequestBatch(requests));
    }

    public GraphRequestAsyncTask(HttpURLConnection connection, Collection<GraphRequest> requests) {
        this(connection, new GraphRequestBatch((Collection) requests));
    }

    public GraphRequestAsyncTask(HttpURLConnection connection, GraphRequestBatch requests) {
        this.requests = requests;
        this.connection = connection;
    }

    protected final Exception getException() {
        return this.exception;
    }

    protected final GraphRequestBatch getRequests() {
        return this.requests;
    }

    public String toString() {
        return "{RequestAsyncTask: " + " connection: " + this.connection + ", requests: " + this.requests + "}";
    }

    protected void onPreExecute() {
        super.onPreExecute();
        if (FacebookSdk.isDebugEnabled()) {
            Log.d(TAG, String.format("execute async task: %s", new Object[]{this}));
        }
        if (this.requests.getCallbackHandler() == null) {
            Handler handler;
            if (Thread.currentThread() instanceof HandlerThread) {
                handler = new Handler();
            } else {
                handler = new Handler(Looper.getMainLooper());
            }
            this.requests.setCallbackHandler(handler);
        }
    }

    protected void onPostExecute(List<GraphResponse> result) {
        super.onPostExecute(result);
        if (this.exception != null) {
            Log.d(TAG, String.format("onPostExecute: exception encountered during request: %s", new Object[]{this.exception.getMessage()}));
        }
    }

    protected List<GraphResponse> doInBackground(Void... params) {
        try {
            if (this.connection == null) {
                return this.requests.executeAndWait();
            }
            return GraphRequest.executeConnectionAndWait(this.connection, this.requests);
        } catch (Exception e) {
            this.exception = e;
            return null;
        }
    }
}
