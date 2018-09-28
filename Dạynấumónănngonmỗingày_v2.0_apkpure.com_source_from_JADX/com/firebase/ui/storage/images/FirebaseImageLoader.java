package com.firebase.ui.storage.images;

import android.util.Log;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.stream.StreamModelLoader;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StreamDownloadTask;
import com.google.firebase.storage.StreamDownloadTask.TaskSnapshot;
import java.io.IOException;
import java.io.InputStream;

public class FirebaseImageLoader implements StreamModelLoader<StorageReference> {
    private static final String TAG = "FirebaseImageLoader";

    private class FirebaseStorageFetcher implements DataFetcher<InputStream> {
        private InputStream mInputStream;
        private StorageReference mRef;
        private StreamDownloadTask mStreamTask;

        FirebaseStorageFetcher(StorageReference ref) {
            this.mRef = ref;
        }

        public InputStream loadData(Priority priority) throws Exception {
            this.mStreamTask = this.mRef.getStream();
            this.mInputStream = ((TaskSnapshot) Tasks.await(this.mStreamTask)).getStream();
            return this.mInputStream;
        }

        public void cleanup() {
            if (this.mInputStream != null) {
                try {
                    this.mInputStream.close();
                    this.mInputStream = null;
                } catch (IOException e) {
                    Log.w(FirebaseImageLoader.TAG, "Could not close stream", e);
                }
            }
        }

        public String getId() {
            return this.mRef.getPath();
        }

        public void cancel() {
            if (this.mStreamTask != null && this.mStreamTask.isInProgress()) {
                this.mStreamTask.cancel();
            }
        }
    }

    public DataFetcher<InputStream> getResourceFetcher(StorageReference model, int width, int height) {
        return new FirebaseStorageFetcher(model);
    }
}
